package com.cometchat.chat.core;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Attachment;
import com.cometchat.chat.upload.UploadFileItem;
import com.cometchat.chat.upload.UploadFileListener;
import com.cometchat.chat.upload.UploadResult;
import com.cometchat.chat.upload.UploadStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A request object scoped to one destination and one upload <b>batch</b>,
 * created via {@link CometChat#createUploadFileRequest(String, String)}. Every
 * upload operation runs through this request: it owns the batch — tracks
 * per-file state, aggregates progress, exposes the batch's attachments and
 * status, and drives both the per-call {@link UploadFileListener} and an
 * optional batch-level global listener ({@link #addUploadListener}).
 *
 * <p>Upload is <b>decoupled from send</b>: upload files in the composer, then
 * build a {@link com.cometchat.chat.models.MediaMessage}, set the uploaded
 * attachments via {@code setAttachments(...)} (read off this request), and send
 * with the existing {@code sendMediaMessage} — send is instant because the
 * bytes are already on storage. Carry {@link #getBatchId()} as the message
 * {@code muid} / {@code metadata.batchId} for reconciliation and grouping.</p>
 *
 * <p>Per-file identity is <b>app-supplied</b>: every upload call takes the
 * {@code fileId} the app minted for each file ({@link UploadFileItem}), and the
 * SDK echoes it back on every event — the app already holds its tile keys
 * before any byte moves. The SDK owns only the {@code batchId}, and only when
 * the app doesn't {@link #setBatchId set} one.</p>
 *
 * <p>Typical composer flow:</p>
 * <pre>{@code
 * UploadFileRequest request = CometChat.createUploadFileRequest(receiverId, receiverType)
 *         .setBatchId(UUID.randomUUID().toString())   // app-provided by default; SDK generates if omitted
 *         .setParentMessageId(threadParentId)         // optional — thread batches only
 *         .setConcurrency(2);                         // optional — default 1 (sequential)
 *
 * request.addUploadListener(new UploadFileListener() {   // optional global listener
 *     @Override public void onComplete(UploadResult result) { enableSend(result); }
 * });
 *
 * List<UploadFileItem> items = new ArrayList<>();
 * for (File f : pickedFiles) items.add(new UploadFileItem(app.newFileId(), f));
 * request.uploadAttachments(items, listener);   // tiles already keyed by the app's ids
 *
 * // when ready: message.setAttachments(request.getAttachments()); sendMediaMessage(...)
 * request.clearAll();   // after a successful send, release the batch
 * }</pre>
 *
 * @since <b>v5</b>
 */
public class UploadFileRequest {

    private final String receiverId;
    private final String receiverType;
    private long parentMessageId;
    private String batchId;
    private int concurrency = 1;

    UploadFileRequest(@NonNull String receiverId, @NonNull String receiverType) {
        this.receiverId = receiverId;
        this.receiverType = receiverType;
    }

    // ---------------------------------------------------------------- configure

    /**
     * Marks this batch as belonging to a thread. The parent message id is sent
     * along with the {@code receiverId} and {@code receiverType} in the
     * presign-URL call for every file uploaded through this request. Omit for a
     * top-level (non-thread) message.
     */
    public UploadFileRequest setParentMessageId(long parentMessageId) {
        this.parentMessageId = parentMessageId;
        return this;
    }

    /**
     * Sets the batch id all files uploaded through this request share. If never
     * called, the SDK auto-generates a UUID the first time the batch is needed.
     * Must be set before the first upload call; once files are in the batch the
     * id is fixed and later calls are ignored.
     */
    public UploadFileRequest setBatchId(@NonNull String batchId) {
        if (this.batchId == null) this.batchId = batchId;
        return this;
    }

    /** The batch id (the value set via {@link #setBatchId}, or an auto-generated UUID). */
    @NonNull
    public String getBatchId() {
        if (batchId == null) batchId = UUID.randomUUID().toString();
        return batchId;
    }

    /**
     * How many files upload concurrently for this batch. Default is {@code 1}
     * (sequential) — parallel uploads of large files over cellular data can
     * saturate the connection and stall every file, so raise this only on
     * known-fast networks (ideally network-aware: higher on Wi-Fi, 1 on cellular).
     *
     * <p>Changing it mid-batch applies to queued files from the next drain;
     * uploads already in flight are never interrupted.</p>
     */
    public UploadFileRequest setConcurrency(int concurrency) {
        if (concurrency > 0) {
            this.concurrency = concurrency;
            UploadManager.getInstance().updateConcurrency(getBatchId(), concurrency);
        }
        return this;
    }

    // ------------------------------------------------------------------- upload

    /**
     * Uploads multiple files into this batch, each under the <b>app-supplied
     * {@code fileId}</b> carried by its {@link UploadFileItem} — the SDK does
     * not assign, derive, or reorder ids, and echoes each id back on every
     * per-file event, so the app keys its UI tile by the id it already holds.
     * The app is responsible for id uniqueness within the batch. Call again to
     * add more files to the same batch (picker / paste / drag-drop) — tiles
     * accumulate and {@link UploadFileListener#onComplete} refires when the
     * batch next drains.
     *
     * <p>Uri-backed items need a {@code Context} to resolve name/size/mime —
     * use {@link #uploadAttachments(Context, List, UploadFileListener)} for
     * them; through this overload they are rejected as invalid.</p>
     *
     * @param items    the files to upload, each paired with the app's fileId
     * @param listener per-call listener — receives events for these files only
     *                 (a global listener added via {@link #addUploadListener}
     *                 additionally receives every event in the batch)
     */
    public void uploadAttachments(@NonNull List<UploadFileItem> items, @Nullable UploadFileListener listener) {
        start(toInputs(items, null), listener);
    }

    /**
     * Same as {@link #uploadAttachments(List, UploadFileListener)} but with a
     * {@link Context} so content-{@link Uri}-backed {@link UploadFileItem}s
     * (e.g. from the Android document/media pickers) can be read.
     */
    public void uploadAttachments(@NonNull Context context, @NonNull List<UploadFileItem> items,
                                  @Nullable UploadFileListener listener) {
        start(toInputs(items, context), listener);
    }

    /**
     * Uploads a single file into this batch under the app-supplied
     * {@code fileId} (convenience for a 1-element {@link #uploadAttachments}).
     */
    public void uploadAttachment(@NonNull String fileId, @NonNull File file,
                                 @Nullable UploadFileListener listener) {
        start(Collections.singletonList(new UploadFile(fileId, file)), listener);
    }

    /** Uploads a single content {@link Uri} into this batch under the app-supplied {@code fileId}. */
    public void uploadAttachment(@NonNull Context context, @NonNull String fileId, @NonNull Uri uri,
                                 @Nullable UploadFileListener listener) {
        start(Collections.singletonList(new UploadFile(fileId, uri, context)), listener);
    }

    private static List<UploadFile> toInputs(List<UploadFileItem> items, @Nullable Context context) {
        List<UploadFile> inputs = new ArrayList<>();
        for (UploadFileItem item : items) {
            if (item.getFile() != null) {
                inputs.add(new UploadFile(item.getFileId(), item.getFile()));
            } else {
                inputs.add(new UploadFile(item.getFileId(), item.getUri(), context));
            }
        }
        return inputs;
    }

    private void start(List<UploadFile> inputs, UploadFileListener listener) {
        UploadManager.getInstance().uploadAttachments(
                inputs, getBatchId(), receiverId, receiverType, parentMessageId, concurrency, listener);
    }

    // --------------------------------------------------------------- read batch

    /** The uploaded attachment for {@code fileId}, or {@code null} if unknown / not yet uploaded. */
    @Nullable
    public Attachment getAttachment(@NonNull String fileId) {
        return UploadManager.getInstance().getAttachment(getBatchId(), fileId);
    }

    /** All uploaded attachments in this batch, in the order the files were added. */
    @NonNull
    public List<Attachment> getAttachments() {
        return UploadManager.getInstance().getAttachments(getBatchId());
    }

    /**
     * Uploaded attachments of one kind — {@link CometChatConstants#MESSAGE_TYPE_IMAGE},
     * {@link CometChatConstants#MESSAGE_TYPE_VIDEO}, {@link CometChatConstants#MESSAGE_TYPE_AUDIO},
     * or {@link CometChatConstants#MESSAGE_TYPE_FILE} — derived from each
     * attachment's mime type. <b>Always a list</b>: several elements when several
     * are present, one when only one is, empty when none. Use this to partition
     * the batch for the one-message-per-kind send convention.
     */
    @NonNull
    public List<Attachment> getAttachmentsByType(@NonNull String type) {
        return UploadManager.getInstance().getAttachmentsByType(getBatchId(), type);
    }

    /** Total files in this batch, in any state (in-progress, failed, rejected, uploaded). */
    public int getAttachmentCount() {
        return UploadManager.getInstance().getAttachmentCount(getBatchId());
    }

    /**
     * {@link UploadStatus#IN_PROGRESS} while any upload in the batch is still
     * running, {@link UploadStatus#IDLE} once every file has reached a terminal
     * state (an empty batch is {@code IDLE}). {@link UploadFileListener#onComplete}
     * fires exactly at the transition to {@code IDLE}.
     */
    @NonNull
    public UploadStatus getStatus() {
        return UploadManager.getInstance().getStatus(getBatchId());
    }

    // ---------------------------------------------------------- global listener

    /**
     * Adds a global listener scoped to this batch. It fires for <b>every</b>
     * per-file event across all upload calls on this request — in addition to
     * (after) each call's own per-call listener — plus the batch-level
     * {@link UploadFileListener#onComplete} when the batch drains. The natural
     * place for batch-wide concerns: the aggregate progress bar, the
     * Send-enabled flag. Adding again replaces the previous global listener.
     */
    public void addUploadListener(@NonNull UploadFileListener listener) {
        UploadManager.getInstance().setGlobalListener(getBatchId(), listener);
    }

    /** Removes the global listener (lifecycle symmetry — detach on composer unmount/re-mount). */
    public void removeUploadListener() {
        UploadManager.getInstance().setGlobalListener(getBatchId(), null);
    }

    // ------------------------------------------------------------- mutate batch

    /**
     * Removes one file from the batch by its {@code fileId}. Aborts the
     * in-flight request if it is still uploading; an already-uploaded file is
     * dropped from the set to send (the unreferenced storage object is cleaned
     * up server-side). After removal it no longer appears in the getters and no
     * longer counts toward {@link #getAttachmentCount()}.
     */
    public void removeAttachment(@NonNull String fileId) {
        UploadManager.getInstance().removeAttachment(getBatchId(), fileId);
    }

    /**
     * Clears all files and the batch state — everything reset to empty. Any
     * in-flight uploads are aborted as part of clearing; held attachments,
     * request handles, and per-file state are released from SDK memory. This is
     * the "I'm done with this batch, release it" call — use it after a
     * successful send (a pure release), or to abandon a composer mid-upload
     * (abort + release). There is no auto-clear on send.
     */
    public void clearAll() {
        UploadManager.getInstance().clearGroup(getBatchId());
    }
}
