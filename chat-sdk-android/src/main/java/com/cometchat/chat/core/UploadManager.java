package com.cometchat.chat.core;

import android.os.Handler;
import android.os.Looper;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Attachment;
import com.cometchat.chat.upload.UploadFileListener;
import com.cometchat.chat.upload.ProgressRequestBody;
import com.cometchat.chat.upload.UploadResult;
import com.cometchat.chat.upload.UploadStatus;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Owns the lifecycle of multi-attachment uploads. The SDK uploads files
 * directly to storage via a pre-signed form, reports per-file progress and a
 * batch-completion event through {@link UploadFileListener}, and supports
 * per-file remove/retry. Sending is the app's separate step (existing
 * {@code sendMediaMessage}).
 *
 * <p>State is in-memory and transient, grouped by {@code batchId}; nothing is
 * persisted (a network loss fails the file &rarr; manual retry). The public
 * surface is {@link UploadFileRequest}; this manager is the shared engine
 * behind every request.</p>
 *
 * <p>Two listener scopes coexist per batch: the <b>per-call</b> listener passed
 * to each {@code uploadAttachment(s)} call fires for that call's files only;
 * the batch's <b>global</b> listener ({@code addUploadListener}) additionally
 * fires for every file in the batch. For a given event the per-call listener
 * fires first, then the global one.</p>
 *
 * @since <b>v5</b>
 */
class UploadManager {

    private static final int STALL_TIMEOUT_MS = 30_000;
    private static final long DEFAULT_FILE_SIZE_MAX = 104857600L; // 100 MB
    private static final int DEFAULT_FILE_COUNT_MAX = 10;

    private static UploadManager instance;

    static synchronized UploadManager getInstance() {
        if (instance == null) instance = new UploadManager();
        return instance;
    }

    private final Map<String, UploadGroup> groups = new ConcurrentHashMap<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final PresignClient presignClient = new PresignClient();

    enum State {QUEUED, REQUESTING_URL, UPLOADING, UPLOADED, REJECTED, FAILED}

    private static class FileEntry {
        String fileId;
        UploadFile input;
        String name;
        long size;
        String mimeType;
        State state = State.QUEUED;
        Attachment attachment;
        CometChatException error;
        int lastPercent = -1;
        Call call;
        Runnable stallRunnable;
        UploadFileListener listener;   // per-call listener of the upload call that added this file
    }

    private static class UploadGroup {
        String batchId;
        String receiverId;
        String receiverType;
        long parentMessageId;           // 0 = top-level (omitted from the presign call)
        UploadFileListener globalListener;
        int concurrency = 1;
        final LinkedHashMap<String, FileEntry> files = new LinkedHashMap<>();
        final Deque<String> queue = new ArrayDeque<>();
        int activeCount = 0;
        boolean completeFired = false;
        final Object lock = new Object();
    }

    // ----------------------------------------------------------------- public

    void uploadAttachments(List<UploadFile> inputs, String batchId,
                           String receiverId, String receiverType, long parentMessageId,
                           int concurrency, UploadFileListener listener) {
        Settings settings = SettingsRepo.getSettings();
        long maxSize = settings != null ? settings.getFileSize() : DEFAULT_FILE_SIZE_MAX;
        int maxCount = settings != null ? settings.getFileCount() : DEFAULT_FILE_COUNT_MAX;

        UploadGroup group = resolveGroup(batchId);
        List<FileEntry> rejectedNow = new ArrayList<>();
        List<FileEntry> displaced = new ArrayList<>();

        synchronized (group.lock) {
            group.receiverId = receiverId;
            group.receiverType = receiverType;
            group.parentMessageId = parentMessageId;
            group.concurrency = concurrency > 0 ? concurrency : 1;
            group.completeFired = false;

            // Upload-time count backstop (file.count.max): accept files up to the
            // remaining capacity in input order, reject the rest. Rejected entries
            // don't consume capacity — replacing them frees nothing extra.
            int occupied = 0;
            for (FileEntry existing : group.files.values()) {
                if (existing.state != State.REJECTED) occupied++;
            }
            int capacity = maxCount - occupied;

            for (UploadFile input : inputs) {
                String fileId = input.getFileId();
                FileEntry e = new FileEntry();
                e.fileId = fileId;
                e.input = input;
                e.listener = listener;

                // App-supplied id must be present (id ownership is the app's, §5.3).
                if (fileId == null || fileId.isEmpty()) {
                    e.state = State.REJECTED;
                    e.error = new CometChatException(CometChatConstants.Errors.ERR_INVALID_FILE_OBJECT,
                            "A non-empty fileId must be supplied for every file.");
                    rejectedNow.add(e);
                    continue;
                }

                // Re-using a fileId REPLACES that entry with a fresh QUEUED one — this
                // is the retry path (re-upload the same fileId after onFileFailure; a
                // fresh entry has no presign, so it re-presigns automatically). The old
                // entry is displaced with a terminal marker so its late transport
                // callbacks are ignored, and its in-flight call (if any) is aborted.
                FileEntry old = group.files.remove(fileId);
                if (old != null) {
                    group.queue.remove(fileId);
                    if (old.state == State.REQUESTING_URL || old.state == State.UPLOADING) {
                        if (old.call != null) old.call.cancel();
                        if (group.activeCount > 0) group.activeCount--;
                    }
                    if (old.state != State.REJECTED) capacity++; // replacing frees the old entry's slot
                    old.state = State.REJECTED;
                    old.call = null;
                    displaced.add(old);
                }
                group.files.put(fileId, e);

                if (!input.isValid()) {
                    e.state = State.REJECTED;
                    e.error = new CometChatException(CometChatConstants.Errors.ERR_INVALID_FILE_OBJECT,
                            CometChatConstants.Errors.ERR_INVALID_FILE_OBJECT_MESSAGE);
                    rejectedNow.add(e);
                    continue;
                }

                e.name = input.getName();
                e.size = input.getSize();
                e.mimeType = input.getMimeType();

                if (capacity <= 0) {
                    e.state = State.REJECTED;
                    e.error = new CometChatException(CometChatConstants.Errors.ERR_FILE_COUNT_EXCEEDED,
                            String.format(CometChatConstants.Errors.ERR_FILE_COUNT_EXCEEDED_MESSAGE,
                                    String.valueOf(maxCount)));
                    rejectedNow.add(e);
                    continue;
                }

                // Per-file size check: reject only the oversized file (others still upload).
                if (e.size > maxSize) {
                    e.state = State.REJECTED;
                    double fileMB = e.size / (1024.0 * 1024.0);
                    double maxMB = maxSize / (1024.0 * 1024.0);
                    e.error = new CometChatException(CometChatConstants.Errors.ERR_FILE_SIZE_EXCEEDED,
                            String.format(CometChatConstants.Errors.ERR_FILE_SIZE_EXCEEDED_MESSAGE,
                                    String.format("%.2f MB", fileMB), String.format("%.2f MB", maxMB)));
                    rejectedNow.add(e);
                    continue;
                }

                capacity--;
                e.state = State.QUEUED;
                group.queue.add(fileId);
            }
        }

        for (FileEntry d : displaced) {
            cancelStallWatchdog(d);
        }
        for (FileEntry e : rejectedNow) {
            postError(group, e, e.error);
        }
        pump(group);
        maybeComplete(group);
    }

    /**
     * Removes one file from the batch. Aborts the in-flight request if it is
     * still uploading; an already-uploaded file is dropped from the batch (the
     * unreferenced storage object is cleaned up server-side). The entry no
     * longer appears in the getters, no longer counts toward the batch, and is
     * excluded from future {@link UploadResult}s.
     */
    void removeAttachment(String batchId, String fileId) {
        UploadGroup group = groups.get(batchId);
        if (group == null) return;
        FileEntry e;
        boolean wasActive = false;
        synchronized (group.lock) {
            e = group.files.remove(fileId);
            if (e == null) return;
            group.queue.remove(fileId);
            if (e.state == State.REQUESTING_URL || e.state == State.UPLOADING) {
                wasActive = true;
                if (e.call != null) e.call.cancel();
            }
            // Terminal marker so late transport callbacks for this entry are ignored.
            e.state = State.REJECTED;
            e.error = new CometChatException(CometChatConstants.Errors.ERR_UPLOAD_CANCELLED,
                    CometChatConstants.Errors.ERR_UPLOAD_CANCELLED_MESSAGE);
            e.call = null;
            if (wasActive && group.activeCount > 0) group.activeCount--;
        }
        cancelStallWatchdog(e);
        pump(group);
        maybeComplete(group);
    }

    /**
     * Applies a new concurrency to the batch mid-flight: queued files pick it up
     * on the next drain (pumping immediately, so raising it starts more queued
     * uploads right away); uploads already in flight are never interrupted.
     * A no-op if the batch doesn't exist yet — the value then arrives with the
     * first {@link #uploadAttachments} call.
     */
    void updateConcurrency(String batchId, int concurrency) {
        if (concurrency <= 0) return;
        UploadGroup group = groups.get(batchId);
        if (group == null) return;
        synchronized (group.lock) {
            group.concurrency = concurrency;
        }
        pump(group);
    }

    void clearGroup(String batchId) {
        UploadGroup group = groups.remove(batchId);
        if (group == null) return;
        synchronized (group.lock) {
            for (FileEntry e : group.files.values()) {
                if (e.call != null) e.call.cancel();
                cancelStallWatchdog(e);
            }
            group.globalListener = null;
        }
    }

    // ------------------------------------------------------------ batch reads

    void setGlobalListener(String batchId, UploadFileListener listener) {
        UploadGroup group = resolveGroup(batchId);
        synchronized (group.lock) {
            group.globalListener = listener;
        }
    }

    /** The uploaded attachment for {@code fileId}, or {@code null} if unknown / not yet uploaded. */
    Attachment getAttachment(String batchId, String fileId) {
        UploadGroup group = groups.get(batchId);
        if (group == null) return null;
        synchronized (group.lock) {
            FileEntry e = group.files.get(fileId);
            return (e != null && e.state == State.UPLOADED) ? e.attachment : null;
        }
    }

    /** All uploaded attachments in the batch, in the order the files were added. */
    List<Attachment> getAttachments(String batchId) {
        List<Attachment> out = new ArrayList<>();
        UploadGroup group = groups.get(batchId);
        if (group == null) return out;
        synchronized (group.lock) {
            for (FileEntry e : group.files.values()) {
                if (e.state == State.UPLOADED && e.attachment != null) out.add(e.attachment);
            }
        }
        return out;
    }

    /**
     * Uploaded attachments of one kind ({@code image} / {@code video} /
     * {@code audio} / {@code file}), derived from each attachment's mime type.
     * Always a list — possibly with one element, possibly empty.
     */
    List<Attachment> getAttachmentsByType(String batchId, String type) {
        List<Attachment> out = new ArrayList<>();
        if (type == null) return out;
        for (Attachment attachment : getAttachments(batchId)) {
            if (type.equalsIgnoreCase(kindOf(attachment.getFileMimeType()))) out.add(attachment);
        }
        return out;
    }

    /** Total files currently in the batch, in any state (in-progress, failed, rejected, uploaded). */
    int getAttachmentCount(String batchId) {
        UploadGroup group = groups.get(batchId);
        if (group == null) return 0;
        synchronized (group.lock) {
            return group.files.size();
        }
    }

    /**
     * {@link UploadStatus#IN_PROGRESS} while any file is queued/presigning/uploading,
     * {@link UploadStatus#IDLE} once every file is terminal. An empty or unknown
     * batch is {@code IDLE} (nothing in flight).
     */
    UploadStatus getStatus(String batchId) {
        UploadGroup group = groups.get(batchId);
        if (group == null) return UploadStatus.IDLE;
        synchronized (group.lock) {
            for (FileEntry e : group.files.values()) {
                if (e.state == State.QUEUED || e.state == State.REQUESTING_URL || e.state == State.UPLOADING) {
                    return UploadStatus.IN_PROGRESS;
                }
            }
        }
        return UploadStatus.IDLE;
    }

    private static String kindOf(String mimeType) {
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) return CometChatConstants.MESSAGE_TYPE_IMAGE;
            if (mimeType.startsWith("video/")) return CometChatConstants.MESSAGE_TYPE_VIDEO;
            if (mimeType.startsWith("audio/")) return CometChatConstants.MESSAGE_TYPE_AUDIO;
        }
        return CometChatConstants.MESSAGE_TYPE_FILE;
    }

    // ---------------------------------------------------------------- internal

    private UploadGroup resolveGroup(String requestedBatchId) {
        if (requestedBatchId != null) {
            UploadGroup existing = groups.get(requestedBatchId);
            if (existing != null) return existing;
        }
        UploadGroup group = new UploadGroup();
        group.batchId = requestedBatchId != null ? requestedBatchId : UUID.randomUUID().toString();
        groups.put(group.batchId, group);
        return group;
    }

    private void pump(UploadGroup group) {
        List<FileEntry> toStart = new ArrayList<>();
        synchronized (group.lock) {
            while (group.activeCount < group.concurrency && !group.queue.isEmpty()) {
                String fileId = group.queue.pollFirst();
                FileEntry e = group.files.get(fileId);
                if (e == null || e.state != State.QUEUED) continue;
                e.state = State.REQUESTING_URL;
                group.activeCount++;
                toStart.add(e);
            }
        }
        for (FileEntry e : toStart) startUpload(group, e);
    }

    private void startUpload(final UploadGroup group, final FileEntry e) {
        PresignClient.FileSpec spec = new PresignClient.FileSpec(e.fileId, e.name, e.size, e.mimeType);
        presignClient.fetch(Collections.singletonList(spec), group.receiverId, group.receiverType,
                group.parentMessageId, new PresignClient.PresignCallback() {
            @Override
            public void onResult(Map<String, PresignClient.PresignedForm> forms,
                                 Map<String, CometChatException> rejections) {
                synchronized (group.lock) {
                    if (e.state != State.REQUESTING_URL) return; // removed meanwhile
                }
                if (rejections.containsKey(e.fileId)) {
                    failReject(group, e, rejections.get(e.fileId));
                    return;
                }
                PresignClient.PresignedForm form = forms.get(e.fileId);
                if (form == null) {
                    failTransfer(group, e, new CometChatException(CometChatConstants.Errors.ERR_PRESIGN_FAILED,
                            CometChatConstants.Errors.ERR_PRESIGN_FAILED_MESSAGE));
                    return;
                }
                uploadBytes(group, e, form);
            }

            @Override
            public void onError(CometChatException error) {
                synchronized (group.lock) {
                    if (e.state != State.REQUESTING_URL) return;
                }
                if (CometChatConstants.Errors.ERR_PRESIGNED_URL_MODE_NOT_ENABLED.equalsIgnoreCase(error.getCode())) {
                    failReject(group, e, error);
                    return;
                }
                failTransfer(group, e, error);
            }
        });
    }

    private void uploadBytes(final UploadGroup group, final FileEntry e, PresignClient.PresignedForm form) {
        synchronized (group.lock) {
            if (e.state != State.REQUESTING_URL) return; // removed meanwhile
            e.state = State.UPLOADING;
            e.attachment = form.attachment;
        }
        RequestBody fileBody = e.input.toRequestBody();
        ProgressRequestBody progressBody = new ProgressRequestBody(fileBody,
                new ProgressRequestBody.ProgressCallback() {
                    @Override
                    public void onProgress(long bytesWritten, long contentLength) {
                        reportProgress(group, e, bytesWritten, contentLength);
                    }
                });
        startStallWatchdog(group, e);
        Call call = ApiConnection.getInstance().uploadToStorage(form.url, form.fields, e.name, progressBody,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ex) {
                        if (call.isCanceled()) return; // remove/stall handled elsewhere
                        failTransfer(group, e, new CometChatException(
                                CometChatConstants.Errors.ERR_S3_UPLOAD_FAILED, ex.getMessage()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int code = response.code();
                        String bodyStr = "";
                        try {
                            if (response.body() != null) bodyStr = response.body().string();
                        } catch (Exception ignored) {
                        } finally {
                            response.close();
                        }
                        if (code >= 200 && code < 300) {
                            succeed(group, e);
                        } else if (code == 403 && bodyStr != null && bodyStr.contains("Expired")) {
                            failTransfer(group, e, new CometChatException(
                                    CometChatConstants.Errors.ERR_PRESIGNED_URL_EXPIRED,
                                    CometChatConstants.Errors.ERR_PRESIGNED_URL_EXPIRED_MESSAGE));
                        } else {
                            failTransfer(group, e, new CometChatException(
                                    CometChatConstants.Errors.ERR_S3_UPLOAD_FAILED,
                                    CometChatConstants.Errors.ERR_S3_UPLOAD_FAILED_MESSAGE));
                        }
                    }
                });
        synchronized (group.lock) {
            e.call = call;
        }
    }

    private void reportProgress(UploadGroup group, FileEntry e, long written, long total) {
        long t = total > 0 ? total : e.size;
        int percent = t > 0 ? (int) Math.min(100, (written * 100) / t) : 0;
        resetStallWatchdog(group, e);
        boolean report;
        synchronized (group.lock) {
            if (e.state != State.UPLOADING) return;
            report = percent != e.lastPercent;
            if (report) e.lastPercent = percent;
        }
        if (report) postProgress(group, e, written, t, percent);
    }

    private void succeed(UploadGroup group, FileEntry e) {
        Attachment attachment;
        synchronized (group.lock) {
            if (e.state != State.UPLOADING) return;
            e.state = State.UPLOADED;
            attachment = e.attachment;
        }
        postUploaded(group, e, attachment);
        finishActive(group, e);
    }

    private void failTransfer(UploadGroup group, FileEntry e, CometChatException error) {
        synchronized (group.lock) {
            if (e.state != State.REQUESTING_URL && e.state != State.UPLOADING) return;
            e.state = State.FAILED;
            e.error = error;
        }
        postFailure(group, e, error);
        finishActive(group, e);
    }

    private void failReject(UploadGroup group, FileEntry e, CometChatException error) {
        synchronized (group.lock) {
            if (e.state != State.REQUESTING_URL && e.state != State.UPLOADING) return;
            e.state = State.REJECTED;
            e.error = error;
        }
        postError(group, e, error);
        finishActive(group, e);
    }

    private void finishActive(UploadGroup group, FileEntry e) {
        cancelStallWatchdog(e);
        synchronized (group.lock) {
            e.call = null;
            if (group.activeCount > 0) group.activeCount--;
        }
        pump(group);
        maybeComplete(group);
    }

    private void maybeComplete(UploadGroup group) {
        UploadResult result;
        List<UploadFileListener> targets;
        synchronized (group.lock) {
            for (FileEntry e : group.files.values()) {
                if (e.state == State.QUEUED || e.state == State.REQUESTING_URL || e.state == State.UPLOADING) {
                    return; // still in-flight
                }
            }
            if (group.completeFired) return;
            group.completeFired = true;
            result = buildResult(group);
            targets = completeTargets(group);
        }
        postComplete(targets, result);
    }

    private UploadResult buildResult(UploadGroup group) {
        List<UploadResult.Success> successful = new ArrayList<>();
        List<UploadResult.Failure> rejected = new ArrayList<>();
        List<UploadResult.Failure> failed = new ArrayList<>();
        for (FileEntry e : group.files.values()) {
            switch (e.state) {
                case UPLOADED:
                    successful.add(new UploadResult.Success(e.fileId, e.attachment));
                    break;
                case REJECTED:
                    rejected.add(new UploadResult.Failure(e.fileId, e.error));
                    break;
                case FAILED:
                    failed.add(new UploadResult.Failure(e.fileId, e.error));
                    break;
                default:
                    break;
            }
        }
        return new UploadResult(group.batchId, successful, rejected, failed);
    }

    /** Every distinct per-call listener in the batch, then the global listener last. */
    private List<UploadFileListener> completeTargets(UploadGroup group) {
        Set<UploadFileListener> distinct = new LinkedHashSet<>();
        for (FileEntry e : group.files.values()) {
            if (e.listener != null) distinct.add(e.listener);
        }
        if (group.globalListener != null) distinct.add(group.globalListener);
        return new ArrayList<>(distinct);
    }

    // ----------------------------------------------------------- stall watchdog

    private void startStallWatchdog(final UploadGroup group, final FileEntry e) {
        cancelStallWatchdog(e);
        e.stallRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (group.lock) {
                    if (e.state != State.UPLOADING) return;
                    if (e.call != null) e.call.cancel();
                }
                failTransfer(group, e, new CometChatException(
                        CometChatConstants.Errors.ERR_UPLOAD_STALLED,
                        CometChatConstants.Errors.ERR_UPLOAD_STALLED_MESSAGE));
            }
        };
        mainHandler.postDelayed(e.stallRunnable, STALL_TIMEOUT_MS);
    }

    private void resetStallWatchdog(UploadGroup group, FileEntry e) {
        startStallWatchdog(group, e);
    }

    private void cancelStallWatchdog(FileEntry e) {
        if (e.stallRunnable != null) {
            mainHandler.removeCallbacks(e.stallRunnable);
            e.stallRunnable = null;
        }
    }

    // ------------------------------------------------------- listener dispatch

    /** Per-call listener first, then the batch's global listener. */
    private List<UploadFileListener> targetsFor(UploadGroup group, FileEntry e) {
        List<UploadFileListener> targets = new ArrayList<>(2);
        UploadFileListener global;
        synchronized (group.lock) {
            global = group.globalListener;
        }
        if (e.listener != null) targets.add(e.listener);
        if (global != null && global != e.listener) targets.add(global);
        return targets;
    }

    private void postProgress(UploadGroup group, FileEntry e,
                              final long loaded, final long total, final int percent) {
        final List<UploadFileListener> targets = targetsFor(group, e);
        if (targets.isEmpty()) return;
        final String fileId = e.fileId;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (UploadFileListener l : targets) l.onFileProgress(fileId, loaded, total, percent);
            }
        });
    }

    private void postUploaded(UploadGroup group, FileEntry e, final Attachment attachment) {
        final List<UploadFileListener> targets = targetsFor(group, e);
        if (targets.isEmpty()) return;
        final String fileId = e.fileId;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (UploadFileListener l : targets) l.onFileUploaded(fileId, attachment);
            }
        });
    }

    private void postError(UploadGroup group, FileEntry e, final CometChatException error) {
        final List<UploadFileListener> targets = targetsFor(group, e);
        if (targets.isEmpty()) return;
        final String fileId = e.fileId;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (UploadFileListener l : targets) l.onFileError(fileId, error);
            }
        });
    }

    private void postFailure(UploadGroup group, FileEntry e, final CometChatException error) {
        final List<UploadFileListener> targets = targetsFor(group, e);
        if (targets.isEmpty()) return;
        final String fileId = e.fileId;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (UploadFileListener l : targets) l.onFileFailure(fileId, error);
            }
        });
    }

    private void postComplete(final List<UploadFileListener> targets, final UploadResult result) {
        if (targets.isEmpty()) return;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (UploadFileListener l : targets) l.onComplete(result);
            }
        });
    }
}
