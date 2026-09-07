package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin Conversation / realtime frame parsing (ENG-37690 §8).
 *
 * <p>{@link CometChatConversationPinEvent#tryFromJSON} is the resolver for conversation pin/unpin
 * socket frames. NO live capture of this frame exists on any platform — the alias sets mirror the
 * JS SDK's equally speculative handler ({@code conversation_pin} / {@code conversation_pinned} /
 * {@code conversation_unpinned}), so narrowing later is a one-line edit.
 *
 * <p>The load-bearing rule (V6/V7): the conversation family and the message pin family BOTH use
 * the bare word {@code "pinned"} as an action, so family membership is decided by {@code type}
 * membership FIRST — a mixed-up frame must be refused by the wrong parser, never mis-delivered
 * (a conversation handed to a MessageListener is a ClassCastException on Android).
 *
 * <p>This test lives in {@code com.cometchat.chat.core} because the event class is
 * package-private.
 */
public class ConversationPinEventParsingTest {

    private static final String TYPE = "conversation_pin";

    private static JSONObject frame(String type, String bodyAction, JSONObject conversation) throws Exception {
        JSONObject body = new JSONObject();
        if (bodyAction != null) {
            body.put(CometChatConstants.WSKeys.KEY_ACTION, bodyAction);
        }
        if (conversation != null) {
            body.put(CometChatConstants.WSKeys.KEY_CONVERSATION, conversation);
        }
        return new JSONObject()
            .put(CometChatConstants.WSKeys.KEY_APP_ID, "2547001d03d8066b")
            .put(CometChatConstants.WSKeys.KEY_TYPE, type)
            .put(CometChatConstants.WSKeys.KEY_SENDER, "cometchat-uid-5")
            .put(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE, "user")
            .put(CometChatConstants.WSKeys.KEY_RECEIVER, "cometchat-uid-5")
            .put(CometChatConstants.WSKeys.KEY_BODY, body);
    }

    /** A 1-1 conversation payload matching the REST pin response shape. */
    private static JSONObject userConversation(String peerUid, Long pinnedAt) throws Exception {
        JSONObject json = new JSONObject()
            .put("conversationId", peerUid + "_user_cometchat-uid-5")
            .put("conversationType", "user")
            .put("conversationWith", new JSONObject().put("uid", peerUid).put("name", "Peer " + peerUid));
        if (pinnedAt != null) {
            json.put("pinnedAt", pinnedAt.longValue()).put("pinnedBy", "cometchat-uid-5");
        }
        return json;
    }

    // ==================== resolution ====================

    /** V1 — the assumed unpin frame parses; action normalises; type canonicalises. */
    @Test
    public void v1_unpinFrame_parsesAndNormalises() throws Exception {
        JSONObject mainObject = frame(TYPE, "conversation_unpinned", userConversation("cometchat-uid-3", null));

        CometChatConversationPinEvent event = CometChatConversationPinEvent.tryFromJSON(mainObject, TYPE);

        assertNotNull(event);
        assertEquals(CometChatConstants.ActionKeys.ACTION_CONVERSATION_UNPINNED, event.getAction());
        assertEquals(CometChatConstants.WSKeys.KEY_TYPE_CONVERSATION_PIN, event.getType());
        assertNotNull(event.getConversation());
        assertEquals("cometchat-uid-3_user_cometchat-uid-5", event.getConversation().getConversationId());
        assertTrue(event.getConversation().getConversationWith() instanceof User);
    }

    /** V2 — pin direction, with the pin attributes riding the conversation payload. */
    @Test
    public void v2_pinFrame_carriesPinAttributes() throws Exception {
        JSONObject mainObject = frame(TYPE, "conversation_pinned", userConversation("cometchat-uid-3", 1786350100L));

        CometChatConversationPinEvent event = CometChatConversationPinEvent.tryFromJSON(mainObject, TYPE);

        assertNotNull(event);
        assertEquals(CometChatConstants.ActionKeys.ACTION_CONVERSATION_PINNED, event.getAction());
        assertTrue(event.getConversation().isPinned());
        assertEquals(1786350100L, event.getConversation().getPinnedAt());
    }

    /** V3 — bare-word actions are accepted INSIDE the conversation family (type decides family). */
    @Test
    public void v3_bareWordAction_resolvesWithinConversationFamily() throws Exception {
        JSONObject mainObject = frame(TYPE, "pinned", userConversation("cometchat-uid-3", 1786350100L));

        CometChatConversationPinEvent event = CometChatConversationPinEvent.tryFromJSON(mainObject, TYPE);

        assertNotNull(event);
        assertEquals(CometChatConstants.ActionKeys.ACTION_CONVERSATION_PINNED, event.getAction());
    }

    /** V4 — a *_unpinned style type doubles as the action when body.action is absent. */
    @Test
    public void v4_typeAliasCarriesTheAction() throws Exception {
        JSONObject mainObject = frame("conversation_unpinned", null, userConversation("cometchat-uid-3", null));

        CometChatConversationPinEvent event =
            CometChatConversationPinEvent.tryFromJSON(mainObject, "conversation_unpinned");

        assertNotNull(event);
        assertEquals(CometChatConstants.ActionKeys.ACTION_CONVERSATION_UNPINNED, event.getAction());
    }

    /** V5 — a group conversation payload yields a Group conversationWith. */
    @Test
    public void v5_groupConversation_parses() throws Exception {
        JSONObject conversation = new JSONObject()
            .put("conversationId", "group_cometchat-guid-1")
            .put("conversationType", "group")
            .put("conversationWith", new JSONObject().put("guid", "cometchat-guid-1").put("name", "Team"));
        JSONObject mainObject = frame(TYPE, "conversation_pinned", conversation);

        CometChatConversationPinEvent event = CometChatConversationPinEvent.tryFromJSON(mainObject, TYPE);

        assertNotNull(event);
        assertTrue(event.getConversation().getConversationWith() instanceof Group);
    }

    // ==================== family separation ====================

    /** V6 — message pin types are not this parser's business. */
    @Test
    public void v6_messagePinType_isNotAConversationPinFrame() throws Exception {
        assertFalse(CometChatConversationPinEvent.isConversationPinType("message_pin"));
        JSONObject mainObject = frame("message_pin", "pinned", userConversation("cometchat-uid-3", null));
        assertNull(CometChatConversationPinEvent.tryFromJSON(mainObject, "message_pin"));
    }

    /** V7 — and the message parser refuses conversation types (asserted from both directions). */
    @Test
    public void v7_conversationType_refusedByMessageParser() throws Exception {
        JSONObject mainObject = frame(TYPE, "pinned", userConversation("cometchat-uid-3", null));
        assertNull(CometChatPinSaveEvent.tryFromJSON(mainObject, TYPE));
    }

    // ==================== rejection rules ====================

    /** V8 — a bare "conversation_pin" type with no resolvable action is direction-ambiguous. */
    @Test
    public void v8_bareTypeWithoutAction_returnsNull() throws Exception {
        JSONObject mainObject = frame(TYPE, null, userConversation("cometchat-uid-3", null));
        assertNull(CometChatConversationPinEvent.tryFromJSON(mainObject, TYPE));
    }

    /** V9 — no conversation payload, or one with no identity at all, is refused (never a throw). */
    @Test
    public void v9_missingOrIdentitylessConversation_returnsNull() throws Exception {
        assertNull(CometChatConversationPinEvent.tryFromJSON(
            frame(TYPE, "conversation_unpinned", null), TYPE));

        // Conversation.fromJSON accepts an empty object (every key optional) — the parser must
        // still refuse it, else consumers receive a conversation with no identity.
        JSONObject junkBody = new JSONObject().put(CometChatConstants.WSKeys.KEY_ACTION, "conversation_unpinned");
        JSONObject mainObject = new JSONObject()
            .put(CometChatConstants.WSKeys.KEY_TYPE, TYPE)
            .put(CometChatConstants.WSKeys.KEY_BODY, junkBody);
        assertNull(CometChatConversationPinEvent.tryFromJSON(mainObject, TYPE));
    }

    /** V10 — conversation nested directly in body (no body.conversation wrapper) still parses. */
    @Test
    public void v10_conversationDirectlyInBody_fallbackParses() throws Exception {
        JSONObject body = userConversation("cometchat-uid-3", 1786350100L)
            .put(CometChatConstants.WSKeys.KEY_ACTION, "conversation_pinned");
        JSONObject mainObject = new JSONObject()
            .put(CometChatConstants.WSKeys.KEY_TYPE, TYPE)
            .put(CometChatConstants.WSKeys.KEY_BODY, body);

        CometChatConversationPinEvent event = CometChatConversationPinEvent.tryFromJSON(mainObject, TYPE);

        assertNotNull(event);
        assertEquals("cometchat-uid-3_user_cometchat-uid-5", event.getConversation().getConversationId());
    }

    /** V11 — conversationWith without conversationType NPEs inside fromJSON; the parser absorbs it. */
    @Test
    public void v11_malformedConversation_neverThrows() throws Exception {
        JSONObject malformed = new JSONObject()
            .put("conversationWith", new JSONObject().put("uid", "cometchat-uid-3"));
        JSONObject mainObject = frame(TYPE, "conversation_pinned", malformed);

        assertNull(CometChatConversationPinEvent.tryFromJSON(mainObject, TYPE));
    }
}
