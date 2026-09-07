package com.cometchat.chat.models;


import org.json.JSONObject;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Thread subscription / {@link MessageThread} deep-copy, bridge-map object entries,
 * and {@code contentEquals} beyond the scalar round-trip already covered by {@code MessageThreadTest}.
 *
 * <p>Focus: {@link MessageThread#clone()} must produce an independent copy (including a fresh
 * {@code rawData} and deep-cloned nested {@link BaseMessage}s), {@code fromMap} must type-guard the
 * nested {@code parentMessage}/{@code lastReply} entries and floor an out-of-range subscription code
 * to {@code UNKNOWN}, and {@code contentEquals} must react to the fields the scalar test does not
 * exercise (replyCount, updatedAt, conversationId, the nested messages, and the null-vs-nonnull
 * unread count).
 */
public class MessageThreadCloneAndMapTest {

    private static BaseMessage message(long id) {
        BaseMessage m = new BaseMessage();
        m.setId(id);
        return m;
    }

    private MessageThread sample() {
        MessageThread thread = new MessageThread();
        thread.setParentMessageId(4242L);
        thread.setReplyCount(5);
        thread.setUpdatedAt(1700000000L);
        thread.setConversationId("alice_bob");
        thread.setReceiverType("user");
        thread.setReceiverUid("bob");
        thread.setSubscribed(true);
        thread.setUnreadReplyCount(2);
        return thread;
    }

    // ==================== clone ====================

    @Test
    public void clone_isIndependentForScalarsAndRawData() throws Exception {
        MessageThread original = sample();
        original.setRawData(new JSONObject().put("k", "v"));

        MessageThread copy = original.clone();
        assertNotSame(original, copy);
        assertTrue(original.contentEquals(copy));

        copy.setReplyCount(99);
        copy.getRawData().put("k", "mutated");

        assertEquals("scalar mutation on the clone must not touch the original", 5, original.getReplyCount());
        assertEquals("rawData must be a separate JSONObject", "v", original.getRawData().optString("k"));
    }

    @Test
    public void clone_deepCopiesNestedParentMessage() {
        MessageThread original = sample();
        original.setParentMessage(message(11L));

        MessageThread copy = original.clone();

        assertNotSame("the nested parentMessage must be deep-cloned, not shared",
                original.getParentMessage(), copy.getParentMessage());
        assertTrue(original.getParentMessage().contentEquals(copy.getParentMessage()));
    }

    // ==================== bridge map: object entries ====================

    @Test
    public void toMap_fromMap_carriesNestedMessagesByReference() {
        MessageThread thread = sample();
        BaseMessage parent = message(11L);
        BaseMessage last = message(12L);
        thread.setParentMessage(parent);
        thread.setLastReply(last);

        MessageThread restored = MessageThread.fromMap(thread.toMap());

        assertSame("the bridge map carries the message instance, not a copy", parent, restored.getParentMessage());
        assertSame(last, restored.getLastReply());
    }

    @Test
    public void fromMap_wrongTypedNestedMessage_isIgnored() {
        Map<String, Object> map = sample().toMap();
        map.put(MessageThread.MapKeys.PARENT_MESSAGE, "not a BaseMessage");

        assertNull("a non-BaseMessage entry must be ignored, not cast", MessageThread.fromMap(map).getParentMessage());
    }

    @Test
    public void fromMap_wrongTypedSubscribedValue_isIgnoredNotCoerced() {
        Map<String, Object> map = sample().toMap();
        map.put(MessageThread.MapKeys.SUBSCRIBED, 99);

        assertFalse("a non-boolean must be ignored, not truthy-coerced",
                MessageThread.fromMap(map).isSubscribed());
    }

    @Test
    public void toMap_omitsNullStringFields() {
        Map<String, Object> map = new MessageThread().toMap();

        assertFalse(map.containsKey(MessageThread.MapKeys.CONVERSATION_ID));
        assertFalse(map.containsKey(MessageThread.MapKeys.RECEIVER_TYPE));
        assertFalse(map.containsKey(MessageThread.MapKeys.RECEIVER_UID));
        // scalars are always present
        assertTrue(map.containsKey(MessageThread.MapKeys.PARENT_MESSAGE_ID));
        assertTrue(map.containsKey(MessageThread.MapKeys.SUBSCRIBED));
    }

    // ==================== contentEquals — fields the scalar test omits ====================

    @Test
    public void contentEquals_differsOnReplyCountUpdatedAtAndConversationId() {
        MessageThread other = sample();
        other.setReplyCount(6);
        assertFalse(sample().contentEquals(other));

        other = sample();
        other.setUpdatedAt(1700000001L);
        assertFalse(sample().contentEquals(other));

        other = sample();
        other.setConversationId("carol_dave");
        assertFalse(sample().contentEquals(other));
    }

    @Test
    public void contentEquals_differsOnNestedParentMessage() {
        MessageThread a = sample();
        a.setParentMessage(message(11L));
        MessageThread b = sample();
        b.setParentMessage(message(22L));

        assertFalse(a.contentEquals(b));
    }

    @Test
    public void contentEquals_unreadCountNullVsZero_areNotEqual() {
        MessageThread nullCount = sample();
        nullCount.setUnreadReplyCount(null);
        MessageThread zeroCount = sample();
        zeroCount.setUnreadReplyCount(0);

        assertFalse("null (unknown) must not equal 0 (known-none)", nullCount.contentEquals(zeroCount));
    }
}
