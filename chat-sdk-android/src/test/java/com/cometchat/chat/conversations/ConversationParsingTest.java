package com.cometchat.chat.conversations;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Conversation;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Conversations / Fetching the conversation list.
 *
 * <p>Covers:
 * <ul>
 *   <li>CONV-01 — a conversation identifies its counterpart (user or group) and exposes
 *       that counterpart's profile.</li>
 *   <li>CONV-02 — unread counts and message-id fields parse and default safely.</li>
 *   <li>CONV-03 — the "last updated" time (see Android divergence note).</li>
 *   <li>CONV-04 — a conversation with missing optional data is still usable.</li>
 * </ul>
 *
 * <p><b>Android divergence (CONV-03):</b> {@code Conversation.updatedAt} is a primitive
 * {@code long}. A backend timestamp of zero is stored as {@code 0}; there is no "never updated"
 * null sentinel as in the Flutter SDK. The test asserts the actual Android behavior.
 *
 * <p>{@code fromJSON} runs the {@code lastMessage} block through {@code CometChatHelper}, so these
 * tests attach the counterpart via {@code conversationWith} (parsed by {@code User}/{@code Group})
 * and keep {@code lastMessage} out, which is the JVM-safe surface.
 */
public class ConversationParsingTest {

    private JSONObject baseConversation(String id, String type) throws Exception {
        return new JSONObject()
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID, id)
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_TYPE, type);
    }

    // ==================== CONV-01: counterpart identity ====================

    @Test
    public void conv01_userConversation_exposesTheUserCounterpart() throws Exception {
        JSONObject counterpart = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "friend-1")
                .put(CometChatConstants.UserKeys.USER_KEY_NAME, "Bob")
                .put(CometChatConstants.UserKeys.USER_KEY_AVATAR, "https://example.com/bob.png");
        JSONObject json = baseConversation("c-user", CometChatConstants.CONVERSATION_TYPE_USER)
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_WITH, counterpart);

        Conversation conv = Conversation.fromJSON(json);

        assertEquals(CometChatConstants.CONVERSATION_TYPE_USER, conv.getConversationType());
        assertTrue("a user conversation's counterpart must be a User",
                conv.getConversationWith() instanceof User);
        User with = (User) conv.getConversationWith();
        assertEquals("friend-1", with.getUid());
        assertEquals("Bob", with.getName());
        assertEquals("https://example.com/bob.png", with.getAvatar());
    }

    @Test
    public void conv01_groupConversation_exposesTheGroupCounterpart() throws Exception {
        JSONObject counterpart = new JSONObject()
                .put(CometChatConstants.GroupKeys.KEY_GROUP_GUID, "grp-1")
                .put(CometChatConstants.GroupKeys.KEY_GROUP_NAME, "Engineering")
                .put(CometChatConstants.GroupKeys.KEY_GROUP_TYPE, CometChatConstants.GROUP_TYPE_PUBLIC)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBERS_COUNT, 12);
        JSONObject json = baseConversation("c-group", CometChatConstants.CONVERSATION_TYPE_GROUP)
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_WITH, counterpart);

        Conversation conv = Conversation.fromJSON(json);

        assertEquals(CometChatConstants.CONVERSATION_TYPE_GROUP, conv.getConversationType());
        assertTrue("a group conversation's counterpart must be a Group",
                conv.getConversationWith() instanceof Group);
        Group with = (Group) conv.getConversationWith();
        assertEquals("grp-1", with.getGuid());
        assertEquals(12, with.getMembersCount());
    }

    // ==================== CONV-02: unread counts and message ids ====================

    @Test
    public void conv02_unreadCountsAndMessageIds_areParsed() throws Exception {
        JSONObject json = baseConversation("c-2", CometChatConstants.CONVERSATION_TYPE_USER)
                .put(CometChatConstants.ConversationKeys.KEY_UNREAD_MESSAGE_COUNT, 5)
                .put(CometChatConstants.ConversationKeys.KEY_UNREAD_MENTIONS_COUNT, 2)
                .put(CometChatConstants.ConversationKeys.KEY_LAST_READ_MESSAGE_ID, 900L)
                .put(CometChatConstants.ConversationKeys.KEY_LATEST_MESSAGE_ID, 950L);

        Conversation conv = Conversation.fromJSON(json);

        assertEquals(5, conv.getUnreadMessageCount());
        assertEquals(2, conv.getUnreadMentionsCount());
        assertEquals(900L, conv.getLastReadMessageId());
        assertEquals(950L, conv.getLatestMessageId());
    }

    @Test
    public void conv02_missingCounts_defaultToZeroInsteadOfCrashing() throws Exception {
        Conversation conv = Conversation.fromJSON(baseConversation("c-3", CometChatConstants.CONVERSATION_TYPE_USER));

        assertEquals("apps must never crash on a missing counter", 0, conv.getUnreadMessageCount());
        assertEquals(0, conv.getUnreadMentionsCount());
    }

    // ==================== CONV-03: last-updated time ====================

    @Test
    public void conv03_lastUpdatedTimestamp_isParsed() throws Exception {
        JSONObject json = baseConversation("c-4", CometChatConstants.CONVERSATION_TYPE_USER)
                .put(CometChatConstants.ConversationKeys.KEY_UPDATED_AT, 1700000000L);

        Conversation conv = Conversation.fromJSON(json);

        assertEquals(1700000000L, conv.getUpdatedAt());
    }

    @Test
    public void conv03_zeroUpdatedAt_isStoredAsZeroOnAndroid() throws Exception {
        // Android divergence: updatedAt is a primitive long, so a zero timestamp is stored as 0
        // rather than the Flutter SDK's "never updated" null.
        JSONObject json = baseConversation("c-5", CometChatConstants.CONVERSATION_TYPE_USER)
                .put(CometChatConstants.ConversationKeys.KEY_UPDATED_AT, 0);

        Conversation conv = Conversation.fromJSON(json);

        assertEquals(0L, conv.getUpdatedAt());
    }

    // ==================== CONV-04: missing optional data ====================

    @Test
    public void conv04_conversationWithNoOptionalData_isStillUsable() throws Exception {
        Conversation conv = Conversation.fromJSON(baseConversation("c-6", CometChatConstants.CONVERSATION_TYPE_USER));

        assertEquals("c-6", conv.getConversationId());
        assertNull("no tags means the field stays absent, not a placeholder", conv.getTags());
        assertNull("no last message means the field stays absent", conv.getLastMessage());
        assertNull("no counterpart means the field stays absent", conv.getConversationWith());
        assertEquals("no update time means zero, not a fabricated date", 0L, conv.getUpdatedAt());
    }

    @Test
    public void conv04_tagsWhenPresent_areExposed() throws Exception {
        JSONObject json = baseConversation("c-7", CometChatConstants.CONVERSATION_TYPE_USER)
                .put(CometChatConstants.ConversationKeys.KEY_TAGS, new JSONArray().put("pinned").put("work"));

        Conversation conv = Conversation.fromJSON(json);

        assertEquals(2, conv.getTags().size());
        assertTrue(conv.getTags().contains("pinned"));
        assertTrue(conv.getTags().contains("work"));
    }
}
