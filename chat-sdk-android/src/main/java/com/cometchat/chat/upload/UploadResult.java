package com.cometchat.chat.upload;

import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Attachment;

import java.util.ArrayList;
import java.util.List;

/**
 * Settled state of an upload batch, delivered to
 * {@link UploadFileListener#onComplete(UploadResult)} every time the batch
 * drains (no files left in-flight).
 *
 * <p>The result always reflects the whole batch's current state, not just the
 * files added since the last firing. Consumers must be idempotent and recompute
 * from {@code result} on every firing (e.g. <i>set</i> the Send-enabled flag,
 * don't toggle it).</p>
 *
 * @since <b>v5</b>
 */
public class UploadResult {

    /**
     * A file that finished uploading successfully, paired with its ready
     * {@link Attachment} (already message-shaped — include it in the message via
     * {@code MediaMessage.setAttachments(...)}).
     */
    public static class Success {
        private final String fileId;
        private final Attachment attachment;

        public Success(String fileId, Attachment attachment) {
            this.fileId = fileId;
            this.attachment = attachment;
        }

        public String getFileId() {
            return fileId;
        }

        public Attachment getAttachment() {
            return attachment;
        }
    }

    /**
     * A file that did not upload successfully, paired with the error that
     * describes why.
     */
    public static class Failure {
        private final String fileId;
        private final CometChatException error;

        public Failure(String fileId, CometChatException error) {
            this.fileId = fileId;
            this.error = error;
        }

        public String getFileId() {
            return fileId;
        }

        public CometChatException getError() {
            return error;
        }
    }

    private final String batchId;
    private final List<Success> successful;
    private final List<Failure> rejected;
    private final List<Failure> failed;

    public UploadResult(String batchId,
                        List<Success> successful,
                        List<Failure> rejected,
                        List<Failure> failed) {
        this.batchId = batchId;
        this.successful = successful != null ? successful : new ArrayList<Success>();
        this.rejected = rejected != null ? rejected : new ArrayList<Failure>();
        this.failed = failed != null ? failed : new ArrayList<Failure>();
    }

    /** The upload batch id this result belongs to. */
    public String getBatchId() {
        return batchId;
    }

    /** Files that uploaded successfully (reached {@code onFileUploaded}). */
    public List<Success> getSuccessful() {
        return successful;
    }

    /**
     * Files that were rejected and hit {@link UploadFileListener#onFileError}
     * — <b>not retryable</b> (fix the input / permission, then remove/replace).
     */
    public List<Failure> getRejected() {
        return rejected;
    }

    /**
     * Files that failed at transfer and hit {@link UploadFileListener#onFileFailure}
     * — <b>retryable</b> by re-uploading the same {@code fileId} through the request.
     */
    public List<Failure> getFailed() {
        return failed;
    }

    @Override
    public String toString() {
        return "UploadResult{batchId='" + batchId + "', successful=" + successful.size()
                + ", rejected=" + rejected.size() + ", failed=" + failed.size() + "}";
    }
}
