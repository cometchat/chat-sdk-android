package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.CustomMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Messaging / Custom messages.
 *
 * <p>Covers MSG-07 — App sends or receives a custom message carrying app-defined
 * structured data (e.g. location, poll, meeting invite). The custom payload, its
 * sub-type identifier, and notification/conversation-update flags all round-trip
 * intact regardless of nesting depth.
 *
 * <p>MSG-08 and MSG-09 (send-time validation that rejects a custom message with
 * neither customData nor metadata) have no counterpart in this SDK: the Android
 * send path does not perform that local pre-flight check.
 */
public class CustomMessageParsingTest {

    private static final String RECEIVER_UID = "receiver-1";

    private JSONObject customPayload(String subType, JSONObject customData) throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_SUB_TYPE, subType)
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_CUSTOM_DATA, customData);

        return MessagePayloads.envelope(201L, CometChatConstants.CATEGORY_CUSTOM, subType,
                        RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);
    }

    // ==================== MSG-07: payload and sub-type round-trip ====================

    @Test
    public void msg07_customMessage_exposesSubTypeAndCustomData() throws Exception {
        JSONObject location = new JSONObject().put("latitude", 19.076).put("longitude", 72.877);

        CustomMessage message = CustomMessage.fromJson(customPayload("location", location));

        assertEquals("location", message.getSubType());
        assertNotNull(message.getCustomData());
        assertEquals(19.076, message.getCustomData().getDouble("latitude"), 0.0001);
        assertEquals(72.877, message.getCustomData().getDouble("longitude"), 0.0001);
        assertEquals(CometChatConstants.CATEGORY_CUSTOM, message.getCategory());
    }

    @Test
    public void msg07_deeplyNestedCustomData_survivesParsingIntact() throws Exception {
        JSONObject poll = new JSONObject()
                .put("question", "Lunch?")
                .put("options", new JSONArray()
                        .put(new JSONObject().put("id", 1).put("label", "Pizza")
                                .put("votes", new JSONArray().put("u1").put("u2")))
                        .put(new JSONObject().put("id", 2).put("label", "Salad")
                                .put("votes", new JSONArray())))
                .put("settings", new JSONObject()
                        .put("anonymous", true)
                        .put("limits", new JSONObject().put("maxVotes", 1)));

        CustomMessage message = CustomMessage.fromJson(customPayload("poll", poll));
        JSONObject parsed = message.getCustomData();

        assertEquals("Lunch?", parsed.getString("question"));
        assertEquals(2, parsed.getJSONArray("options").length());
        assertEquals("Pizza", parsed.getJSONArray("options").getJSONObject(0).getString("label"));
        assertEquals("nesting several levels deep must not be flattened or lost",
                2, parsed.getJSONArray("options").getJSONObject(0).getJSONArray("votes").length());
        assertTrue(parsed.getJSONObject("settings").getBoolean("anonymous"));
        assertEquals(1, parsed.getJSONObject("settings").getJSONObject("limits").getInt("maxVotes"));
    }

    @Test
    public void msg07_customDataWithUnicodeAndEmoji_isPreservedExactly() throws Exception {
        JSONObject data = new JSONObject().put("title", "会議 🎉 مرحبا");

        CustomMessage message = CustomMessage.fromJson(customPayload("meeting", data));

        assertEquals("会議 🎉 مرحبا", message.getCustomData().getString("title"));
    }

    // ==================== MSG-07: notification / conversation-update flags ====================

    @Test
    public void msg07_conversationAndNotificationFlags_roundTripWhenSetTrue() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_SUB_TYPE, "meeting")
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_CUSTOM_DATA, new JSONObject().put("room", "A1"))
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_UPDATE_CONVERSATION, true)
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_SEND_NOTIFICATION, true)
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_TEXT, "Meeting invite");
        JSONObject payload = MessagePayloads.envelope(202L, CometChatConstants.CATEGORY_CUSTOM, "meeting",
                        RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        CustomMessage message = CustomMessage.fromJson(payload);

        assertTrue("updateConversation must round-trip", message.willUpdateConversation());
        assertTrue("sendNotification must round-trip", message.willSendNotification());
        assertEquals("Meeting invite", message.getConversationText());
    }

    @Test
    public void msg07_conversationAndNotificationFlags_roundTripWhenSetFalse() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_SUB_TYPE, "silent")
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_CUSTOM_DATA, new JSONObject())
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_UPDATE_CONVERSATION, false)
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_SEND_NOTIFICATION, false);
        JSONObject payload = MessagePayloads.envelope(203L, CometChatConstants.CATEGORY_CUSTOM, "silent",
                        RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        CustomMessage message = CustomMessage.fromJson(payload);

        assertFalse(message.willUpdateConversation());
        assertFalse(message.willSendNotification());
    }

    @Test
    public void msg07_absentFlags_defaultToFalse() throws Exception {
        CustomMessage message = CustomMessage.fromJson(
                customPayload("location", new JSONObject().put("latitude", 1.0)));

        assertFalse("an absent updateConversation flag defaults to false", message.willUpdateConversation());
        assertFalse("an absent sendNotification flag defaults to false", message.willSendNotification());
        assertNull("conversation text stays unset when the server omits it", message.getConversationText());
    }

    @Test
    public void msg07_customMessageToAGroup_preservesRoutingFields() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_SUB_TYPE, "poll")
                .put(CometChatConstants.MessageKeys.KEY_CUSTOM_CUSTOM_DATA, new JSONObject().put("q", "Where?"))
                .put(CometChatConstants.ResponseKeys.KEY_ENTITIES,
                        MessagePayloads.entitiesWithGroupReceiver("sender-1", "group-1"));
        JSONObject payload = MessagePayloads.envelope(204L, CometChatConstants.CATEGORY_CUSTOM, "poll",
                        "group-1", CometChatConstants.RECEIVER_TYPE_GROUP)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        CustomMessage message = CustomMessage.fromJson(payload);

        assertEquals("poll", message.getSubType());
        assertEquals(CometChatConstants.RECEIVER_TYPE_GROUP, message.getReceiverType());
        assertEquals("group-1", message.getReceiverUid());
        assertEquals("Where?", message.getCustomData().getString("q"));
    }

    @Test
    public void msg07_customMessageWithoutCustomData_stillParses() throws Exception {
        JSONObject payload = MessagePayloads.envelope(205L, CometChatConstants.CATEGORY_CUSTOM, "ping",
                        RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA,
                        new JSONObject().put(CometChatConstants.MessageKeys.KEY_CUSTOM_SUB_TYPE, "ping"));

        CustomMessage message = CustomMessage.fromJson(payload);

        assertEquals("ping", message.getSubType());
        assertNull("absent customData stays null rather than becoming an empty object",
                message.getCustomData());
    }
}
