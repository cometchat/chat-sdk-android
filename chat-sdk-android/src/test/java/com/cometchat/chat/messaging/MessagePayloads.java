package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Builders for server-shaped message payloads, mirroring what the backend puts on
 * the wire and what {@code fromJson()} / {@code CometChatHelper.processMessage()} consume.
 *
 * <p>Keys are referenced through {@link CometChatConstants} rather than hard-coded
 * literals so that a rename on the wire contract fails these tests loudly.
 */
final class MessagePayloads {

    private MessagePayloads() {}

    /** An {@code entities} entry: {@code {"entityType": "...", "entity": {...}}}. */
    static JSONObject entity(String entityType, JSONObject entity) throws JSONException {
        return new JSONObject()
                .put(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE, entityType)
                .put(CometChatConstants.ResponseKeys.KEY_ENTITITY, entity);
    }

    static JSONObject user(String uid, String name) throws JSONException {
        return new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, uid)
                .put(CometChatConstants.UserKeys.USER_KEY_NAME, name)
                .put(CometChatConstants.UserKeys.USER_KEY_AVATAR, "https://example.com/" + uid + ".png")
                .put(CometChatConstants.UserKeys.USER_KEY_STATUS, CometChatConstants.USER_STATUS_ONLINE);
    }

    static JSONObject group(String guid, String name) throws JSONException {
        return new JSONObject()
                .put(CometChatConstants.GroupKeys.KEY_GROUP_GUID, guid)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_NAME, name)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_TYPE, CometChatConstants.GROUP_TYPE_PUBLIC);
    }

    /** A {@code data.entities} block naming the sender and a user receiver. */
    static JSONObject entitiesWithUserReceiver(String senderUid, String receiverUid) throws JSONException {
        return new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SENDER,
                        entity(CometChatConstants.CONVERSATION_TYPE_USER, user(senderUid, "Sender " + senderUid)))
                .put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID,
                        entity(CometChatConstants.CONVERSATION_TYPE_USER, user(receiverUid, "Receiver " + receiverUid)));
    }

    /** A {@code data.entities} block naming the sender and a group receiver. */
    static JSONObject entitiesWithGroupReceiver(String senderUid, String guid) throws JSONException {
        return new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SENDER,
                        entity(CometChatConstants.CONVERSATION_TYPE_USER, user(senderUid, "Sender " + senderUid)))
                .put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID,
                        entity(CometChatConstants.CONVERSATION_TYPE_GROUP, group(guid, "Group " + guid)));
    }

    static JSONArray reactions(String emoji, int count, boolean reactedByMe) throws JSONException {
        return new JSONArray().put(new JSONObject()
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION, emoji)
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_COUNT, count)
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY_ME, reactedByMe));
    }

    static JSONObject moderation(String status) throws JSONException {
        return new JSONObject().put(CometChatConstants.MessageKeys.KEY_MODERATION_STATUS, status);
    }

    /** The {@code myReceipt} block carrying the logged-in user's own delivery/read times. */
    static JSONObject myReceipt(long deliveredAt, long readAt) throws JSONException {
        return new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT, deliveredAt)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT, readAt);
    }

    static JSONObject attachment(String url, String name, String extension, String mimeType, int size)
            throws JSONException {
        return new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_URL, url)
                .put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_NAME, name)
                .put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_EXTENSION, extension)
                .put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_MIMETYPE, mimeType)
                .put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_SIZE, size);
    }

    /**
     * A message envelope with the identity/routing fields every category shares.
     * Callers attach a {@code data} block for the category-specific payload.
     */
    static JSONObject envelope(long id, String category, String type, String receiverUid, String receiverType)
            throws JSONException {
        return new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID, id)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, category)
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, type)
                .put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, receiverUid)
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, receiverType);
    }
}
