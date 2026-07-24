package com.cometchat.chat.upload;

import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Attachment;

/**
 * Callback bundle for multi-attachment uploads, driven through
 * {@code UploadFileRequest} ({@code CometChat.createUploadFileRequest}). It is
 * used in two scopes: as the <b>per-call</b> listener passed to
 * {@code uploadAttachments} / {@code uploadAttachment} (events for that call's
 * files only), and as the batch-level <b>global</b> listener registered via
 * {@code addUploadListener} (events for every file in the batch, plus batch
 * completion). Both may be active at once — the per-call listener fires first.
 *
 * <p>Unlike {@code MessageListener} / {@code CallListener}, this is <b>not</b>
 * registered globally with a string id. Override only the callbacks you need.</p>
 *
 * <p><b>onFileError vs onFileFailure — two distinct outcomes.</b> Exactly one
 * fires per non-successful file:</p>
 * <ul>
 *   <li>{@link #onFileError} — <b>rejected; retry won't help.</b> Local
 *       fail-fast validation (combined size, file count, or an invalid/unreadable
 *       file) or presign-time authorization from the server (billing/plan caps,
 *       RBAC MIME denial, presigned-URL mode not enabled for the app).
 *       Prompt remove/replace ({@code removeAttachment(fileId)}).
 *       re-uploading one would just re-fail the same check.</li>
 *   <li>{@link #onFileFailure} — <b>transfer failed; retryable.</b> Network
 *       error, storage 5xx/policy reject, upload stall, or expired presign.
 *       Show Retry &rarr; re-upload that file through the request under the
 *       <b>same {@code fileId}</b> ({@code uploadAttachment(fileId, file, listener)})
 *       — the fresh entry replaces the failed one and re-presigns automatically.</li>
 * </ul>
 *
 * @since <b>v5</b>
 */
public abstract class UploadFileListener {

    /**
     * Per-file upload progress. Throttled internally, so it is safe to update
     * the UI on every event.
     *
     * @param fileId  the file's stable id (as returned from {@code uploadAttachments})
     * @param loaded  bytes uploaded so far
     * @param total   total bytes for this file
     * @param percent {@code 0..100}
     */
    public void onFileProgress(String fileId, long loaded, long total, int percent) {
    }

    /**
     * A file finished uploading. {@code attachment} is ready to include in a
     * {@code MediaMessage} via {@code setAttachments(...)}.
     */
    public void onFileUploaded(String fileId, Attachment attachment) {
    }

    /**
     * A file was <b>rejected</b> — not retryable. Remove/replace it.
     */
    public void onFileError(String fileId, CometChatException error) {
    }

    /**
     * A file <b>failed at transfer</b> — retryable by re-uploading it through
     * the request under the same {@code fileId}.
     */
    public void onFileFailure(String fileId, CometChatException error) {
    }

    /**
     * Fires once when the group reaches no-files-in-flight (every file
     * UPLOADED, FAILED, or CANCELLED), and again every time the group next
     * drains after more files are added. <b>Must be treated idempotently</b> —
     * recompute from {@code result} on each firing.
     */
    public void onComplete(UploadResult result) {
    }
}
