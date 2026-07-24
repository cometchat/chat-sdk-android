package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Attachment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Requests pre-signed upload forms from the chat-api ({@code POST /files/upload-url})
 * and parses the per-file response into either a ready-to-upload
 * {@link PresignedForm} or a per-file rejection.
 *
 * <p>Internal to the SDK; orchestrated by {@link UploadManager}.</p>
 *
 * @since <b>v5</b>
 */
class PresignClient {

    /** One file to presign. */
    static class FileSpec {
        final String fileId;
        final String name;
        final long size;
        final String mimeType;

        FileSpec(String fileId, String name, long size, String mimeType) {
            this.fileId = fileId;
            this.name = name;
            this.size = size;
            this.mimeType = mimeType;
        }
    }

    /** A successfully presigned upload form for one file. */
    static class PresignedForm {
        final String url;
        final Map<String, String> fields;
        final Attachment attachment;

        PresignedForm(String url, Map<String, String> fields, Attachment attachment) {
            this.url = url;
            this.fields = fields;
            this.attachment = attachment;
        }
    }

    interface PresignCallback {
        /**
         * @param forms      fileId &rarr; presigned form (uploadable)
         * @param rejections fileId &rarr; rejection (not retryable — billing/RBAC/MIME)
         */
        void onResult(Map<String, PresignedForm> forms, Map<String, CometChatException> rejections);

        /** Whole-request failure (network/server); every requested file is retryable. */
        void onError(CometChatException error);
    }

    void fetch(final List<FileSpec> files, final String receiverId, final String receiverType,
               final long parentMessageId, final PresignCallback callback) {
        JSONObject body = new JSONObject();
        try {
            JSONObject filesObject = new JSONObject();
            for (FileSpec f : files) {
                JSONObject entry = new JSONObject();
                entry.put(CometChatConstants.MessageKeys.KEY_UPLOAD_FILE_ID, f.fileId);
                entry.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_NAME, f.name);
                entry.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_SIZE, f.size);
                entry.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_MIMETYPE, f.mimeType);
                filesObject.put(f.fileId, entry);
            }
            body.put(CometChatConstants.MessageKeys.KEY_UPLOAD_FILES, filesObject);
            if (receiverId != null)
                body.put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, receiverId);
            if (receiverType != null)
                body.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, receiverType);
            if (parentMessageId > 0)
                body.put(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID, parentMessageId);
        } catch (JSONException e) {
            callback.onError(new CometChatException(CometChatConstants.Errors.ERR_PRESIGN_FAILED,
                    CometChatConstants.Errors.ERR_PRESIGN_FAILED_MESSAGE));
            return;
        }

        ApiConnection.getInstance().requestUploadUrls(body.toString(), new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, CometChatException ce) {
                if (ce != null) {
                    callback.onError(ce);
                    return;
                }
                try {
                    parseAndDeliver(response, callback);
                } catch (Exception e) {
                    callback.onError(new CometChatException(CometChatConstants.Errors.ERR_PRESIGN_FAILED,
                            e.getMessage() != null ? e.getMessage()
                                    : CometChatConstants.Errors.ERR_PRESIGN_FAILED_MESSAGE));
                }
            }
        });
    }

    private void parseAndDeliver(String response, PresignCallback callback) throws JSONException {
        Map<String, PresignedForm> forms = new HashMap<>();
        Map<String, CometChatException> rejections = new HashMap<>();

        JSONObject root = new JSONObject(response);
        JSONObject data = root.has(CometChatConstants.ResponseKeys.KEY_DATA)
                ? root.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                : root;

        Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String fileId = keys.next();
            JSONObject entry = data.getJSONObject(fileId);

            if (entry.has("request") && entry.has("attachment")) {
                JSONObject request = entry.getJSONObject("request");
                String url = request.getString(CometChatConstants.MessageKeys.KEY_ATTACHMENT_URL);
                Map<String, String> fields = new HashMap<>();
                if (request.has("body")) {
                    JSONObject bodyFields = request.getJSONObject("body");
                    Iterator<String> fieldKeys = bodyFields.keys();
                    while (fieldKeys.hasNext()) {
                        String fk = fieldKeys.next();
                        fields.put(fk, bodyFields.getString(fk));
                    }
                }
                Attachment attachment = Attachment.fromJson(entry.getJSONObject("attachment"));
                forms.put(fileId, new PresignedForm(url, fields, attachment));
            } else {
                // Per-file rejection (billing / RBAC / MIME). Not retryable.
                String code = entry.optString("code", CometChatConstants.Errors.ERR_PRESIGN_REJECTED);
                String message = entry.optString("message",
                        CometChatConstants.Errors.ERR_PRESIGN_REJECTED_MESSAGE);
                rejections.put(fileId, new CometChatException(code, message));
            }
        }
        callback.onResult(forms, rejections);
    }
}
