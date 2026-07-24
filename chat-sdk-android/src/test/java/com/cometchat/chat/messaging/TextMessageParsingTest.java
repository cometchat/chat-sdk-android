package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.enums.ModerationStatus;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.ReactionCount;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Messaging / Text messages.
 *
 * <p>Covers:
 * <ul>
 *   <li>MSG-01 — App receives a text message from the server with sender, receiver,
 *       timestamps and reactions.</li>
 *   <li>MSG-02 — Server sends a minimal text message with only required fields.</li>
 *   <li>MSG-03 — A text message arrives without moderation data.</li>
 * </ul>
 *
 * <p>MSG-01 additionally names <em>mentions</em>. Mention parsing calls
 * {@code CometChat.getLoggedInUser()}, which reads the SQLite-backed current-user
 * store and so cannot run in a JVM unit test; mentions are covered by the
 * instrumented suite instead.
 */
public class TextMessageParsingTest {

    private static final String SENDER_UID = "sender-1";
    private static final String RECEIVER_UID = "receiver-1";

    /** A text message as the backend delivers it, with every optional block populated. */
    private JSONObject fullyPopulatedTextPayload() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, "Hello World")
                .put(CometChatConstants.ResponseKeys.KEY_ENTITIES,
                        MessagePayloads.entitiesWithUserReceiver(SENDER_UID, RECEIVER_UID))
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA,
                        new JSONObject().put("customKey", "customValue"))
                .put(CometChatConstants.MessageKeys.KEY_REACTIONS,
                        MessagePayloads.reactions("👍", 2, true))
                .put(CometChatConstants.MessageKeys.KEY_MODERATION,
                        MessagePayloads.moderation(ModerationStatus.APPROVED.getValue()));

        return MessagePayloads.envelope(101L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID, "sender-1_user_receiver-1")
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID, "muid-101")
                .put(CometChatConstants.MessageKeys.KEY_SENT_AT, 1700000000L)
                .put(CometChatConstants.MessageKeys.KEY_UPDATED_AT, 1700000300L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT, 1700000100L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT, 1700000200L)
                .put(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT,
                        MessagePayloads.myReceipt(1700000110L, 1700000210L))
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);
    }

    // ==================== MSG-01: fully populated text message ====================

    @Test
    public void msg01_fullyPopulatedTextMessage_exposesSenderAndReceiverIdentity() throws Exception {
        TextMessage message = TextMessage.fromJson(fullyPopulatedTextPayload());

        assertNotNull("sender should be resolved from data.entities", message.getSender());
        assertEquals(SENDER_UID, message.getSender().getUid());
        assertEquals("Sender " + SENDER_UID, message.getSender().getName());

        assertNotNull("receiver should be resolved from data.entities", message.getReceiver());
        assertTrue("a user receiver should resolve to a User", message.getReceiver() instanceof User);
        assertEquals(RECEIVER_UID, ((User) message.getReceiver()).getUid());
        assertEquals(RECEIVER_UID, message.getReceiverUid());
    }

    @Test
    public void msg01_fullyPopulatedTextMessage_exposesSentDeliveredAndReadTimestamps() throws Exception {
        TextMessage message = TextMessage.fromJson(fullyPopulatedTextPayload());

        assertEquals(1700000000L, message.getSentAt());
        assertEquals(1700000300L, message.getUpdatedAt());
        assertEquals("deliveredAt reflects delivery to the recipient", 1700000100L, message.getDeliveredAt());
        assertEquals("readAt reflects the recipient reading the message", 1700000200L, message.getReadAt());
        assertEquals("myReceipt carries the logged-in user's own delivery time",
                1700000110L, message.getDeliveredToMeAt());
        assertEquals("myReceipt carries the logged-in user's own read time",
                1700000210L, message.getReadByMeAt());
    }

    @Test
    public void msg01_fullyPopulatedTextMessage_exposesReactions() throws Exception {
        TextMessage message = TextMessage.fromJson(fullyPopulatedTextPayload());

        assertNotNull(message.getReactions());
        assertEquals(1, message.getReactions().size());

        ReactionCount reaction = message.getReactions().get(0);
        assertEquals("👍", reaction.getReaction());
        assertEquals(2, reaction.getCount());
        assertTrue("reactedByMe should survive parsing", reaction.getReactedByMe());
    }

    @Test
    public void msg01_fullyPopulatedTextMessage_exposesTextAndRoutingFields() throws Exception {
        TextMessage message = TextMessage.fromJson(fullyPopulatedTextPayload());

        assertEquals("Hello World", message.getText());
        assertEquals(101L, message.getId());
        assertEquals("muid-101", message.getMuid());
        assertEquals(CometChatConstants.CATEGORY_MESSAGE, message.getCategory());
        assertEquals(CometChatConstants.MESSAGE_TYPE_TEXT, message.getType());
        assertEquals(CometChatConstants.RECEIVER_TYPE_USER, message.getReceiverType());
        assertEquals("sender-1_user_receiver-1", message.getConversationId());
        assertNotNull(message.getMetadata());
        assertEquals("customValue", message.getMetadata().getString("customKey"));
    }

    @Test
    public void msg01_textMessageToAGroup_resolvesReceiverAsGroup() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, "Hello Group")
                .put(CometChatConstants.ResponseKeys.KEY_ENTITIES,
                        MessagePayloads.entitiesWithGroupReceiver(SENDER_UID, "group-1"));
        JSONObject payload = MessagePayloads.envelope(102L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, "group-1", CometChatConstants.RECEIVER_TYPE_GROUP)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        TextMessage message = TextMessage.fromJson(payload);

        assertTrue("a group receiver should resolve to a Group", message.getReceiver() instanceof Group);
        assertEquals("group-1", ((Group) message.getReceiver()).getGuid());
        assertEquals(CometChatConstants.RECEIVER_TYPE_GROUP, message.getReceiverType());
    }

    // ==================== MSG-02: minimal text message ====================

    @Test
    public void msg02_minimalTextMessage_leavesOptionalFieldsUnsetInsteadOfFailing() throws Exception {
        JSONObject payload = MessagePayloads.envelope(103L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA,
                        new JSONObject().put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, "Bare"));

        TextMessage message = TextMessage.fromJson(payload);

        assertNotNull("a bare-minimum message must still parse", message);
        assertEquals("Bare", message.getText());
        assertEquals(103L, message.getId());

        assertNull("metadata is optional and should stay null", message.getMetadata());
        assertNull("sender is optional and should stay null", message.getSender());
        assertNull("receiver is optional and should stay null", message.getReceiver());
        assertNull("tags are optional and should stay null", message.getTags());

        // Android differs from the Flutter contract here: mentions and reactions are
        // collection fields that start empty rather than null, so callers iterate safely.
        assertTrue("mentioned users should be empty when absent", message.getMentionedUsers().isEmpty());
        assertTrue("reactions should be empty when absent", message.getReactions().isEmpty());
    }

    @Test
    public void msg02_minimalTextMessage_leavesUnsentTimestampsAtZero() throws Exception {
        JSONObject payload = MessagePayloads.envelope(104L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA,
                        new JSONObject().put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, "Bare"));

        TextMessage message = TextMessage.fromJson(payload);

        assertEquals(0L, message.getDeliveredAt());
        assertEquals(0L, message.getReadAt());
        assertEquals(0L, message.getDeletedAt());
        assertEquals(0L, message.getEditedAt());
        assertNull(message.getDeletedBy());
        assertNull(message.getEditedBy());
    }

    // ==================== MSG-03: missing moderation data ====================

    @Test
    public void msg03_textMessageWithoutModerationBlock_defaultsToUnmoderated() throws Exception {
        JSONObject payload = MessagePayloads.envelope(105L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA,
                        new JSONObject().put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, "No moderation"));

        TextMessage message = TextMessage.fromJson(payload);

        assertEquals("apps must be able to branch on moderation state without a null check",
                ModerationStatus.UNMODERATED, message.getModerationStatus());
    }

    @Test
    public void msg03_moderationBlockWithoutStatus_defaultsToUnmoderated() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, "Empty moderation")
                .put(CometChatConstants.MessageKeys.KEY_MODERATION, new JSONObject());
        JSONObject payload = MessagePayloads.envelope(106L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        TextMessage message = TextMessage.fromJson(payload);

        assertEquals(ModerationStatus.UNMODERATED, message.getModerationStatus());
    }

    @Test
    public void msg03_unrecognizedModerationStatus_fallsBackToUnmoderated() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, "Unknown moderation")
                .put(CometChatConstants.MessageKeys.KEY_MODERATION,
                        MessagePayloads.moderation("some-future-status"));
        JSONObject payload = MessagePayloads.envelope(107L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        TextMessage message = TextMessage.fromJson(payload);

        assertEquals("an unrecognized status must not leave moderation undefined",
                ModerationStatus.UNMODERATED, message.getModerationStatus());
    }

    @Test
    public void msg03_explicitModerationStatus_isPreserved() throws Exception {
        TextMessage message = TextMessage.fromJson(fullyPopulatedTextPayload());

        assertEquals(ModerationStatus.APPROVED, message.getModerationStatus());
    }
}
