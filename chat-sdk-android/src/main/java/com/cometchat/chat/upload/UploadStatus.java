package com.cometchat.chat.upload;

/**
 * Batch-level status of an {@link com.cometchat.chat.core.UploadFileRequest},
 * returned by {@code getStatus()}.
 *
 * <ul>
 *   <li>{@link #IN_PROGRESS} — any upload in the batch is still running
 *       (queued, requesting a presigned URL, or uploading bytes).</li>
 *   <li>{@link #IDLE} — the batch has finished: every file has reached a
 *       terminal state (uploaded, rejected, failed, or removed). An empty
 *       batch (fresh request, or after {@code clearAll()}) is {@code IDLE} —
 *       nothing is in flight.</li>
 * </ul>
 *
 * <p>{@link UploadFileListener#onComplete} fires exactly when the batch
 * transitions to {@code IDLE}.</p>
 *
 * @since <b>v5</b>
 */
public enum UploadStatus {
    IN_PROGRESS,
    IDLE
}
