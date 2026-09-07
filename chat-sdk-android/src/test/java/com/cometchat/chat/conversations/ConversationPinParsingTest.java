package com.cometchat.chat.conversations;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Conversation;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin Conversation / parse contract.
 *
 * <p>Golden vectors for {@link Conversation#applyPinAttributes} and its wiring into
 * {@link Conversation#fromJSON}: presence of {@code pinnedAt} IS the boolean; absent ⇒ not pinned
 * (never {@code 0}-as-set, never a throw); wrong-typed ⇒ missing; unpin clears the field;
 * {@code pinnedBy == "app_system"} ⇒ system / global pin.
 */
public class ConversationPinParsingTest {

    private static final long PINNED_AT = 1785332391L;

    private static JSONObject conversationJson() throws Exception {
        return new JSONObject()
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID, "c-1")
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_TYPE, CometChatConstants.CONVERSATION_TYPE_USER);
    }

    @Test
    public void absent_readsAsNotPinned() throws Exception {
        Conversation c = Conversation.fromJSON(conversationJson());
        assertFalse(c.isPinned());
        assertFalse(c.isSystemPinned());
        assertEquals(0L, c.getPinnedAt());
        assertNull(c.getPinnedBy());
    }

    @Test
    public void userPin_isPinnedNotSystem() throws Exception {
        Conversation c = Conversation.fromJSON(conversationJson()
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_AT, PINNED_AT)
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_BY, "uid1"));
        assertTrue(c.isPinned());
        assertFalse(c.isSystemPinned());
        assertEquals(PINNED_AT, c.getPinnedAt());
        assertEquals("uid1", c.getPinnedBy());
    }

    @Test
    public void systemPin_isSystemPinned() throws Exception {
        Conversation c = Conversation.fromJSON(conversationJson()
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_AT, PINNED_AT)
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_BY, CometChatConstants.MessageKeys.PINNED_BY_SYSTEM));
        assertTrue(c.isPinned());
        assertTrue(c.isSystemPinned());
    }

    @Test
    public void unpin_clearsStaleValue() throws Exception {
        Conversation c = new Conversation("c-1", CometChatConstants.CONVERSATION_TYPE_USER);
        Conversation.applyPinAttributes(c, new JSONObject()
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_AT, PINNED_AT)
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_BY, "uid1"));
        assertTrue(c.isPinned());

        // Unpin response omits the keys → cleared.
        Conversation.applyPinAttributes(c, new JSONObject());
        assertFalse(c.isPinned());
        assertEquals(0L, c.getPinnedAt());
        assertNull(c.getPinnedBy());
    }

    @Test
    public void wrongTyped_readsAsNotPinned_noThrow() throws Exception {
        Conversation c = Conversation.fromJSON(conversationJson()
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_AT, "not-a-number")
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_BY, JSONObject.NULL));
        assertFalse(c.isPinned());
        assertNull(c.getPinnedBy());
    }

    @Test
    public void contentEquals_accountsForPin() throws Exception {
        Conversation pinned = Conversation.fromJSON(conversationJson()
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_AT, PINNED_AT)
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_BY, "uid1"));
        Conversation notPinned = Conversation.fromJSON(conversationJson());
        assertFalse(pinned.contentEquals(notPinned));
    }
}
