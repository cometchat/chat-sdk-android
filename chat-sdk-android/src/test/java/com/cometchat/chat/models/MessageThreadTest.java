package com.cometchat.chat.models;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Thread subscription / {@link MessageThread} model.
 *
 * <p>Covers the bridge map round-trip (consumed by the Flutter and React Native bridges), content
 * equality, and the one invariant that keeps "unknown" honest — a null unread count stays null
 * rather than collapsing to zero. Subscription itself is a plain boolean: the SDK holds no state to
 * be unsure about. Parcel round-trips need the Android runtime and are covered by instrumented
 * tests.
 */
public class MessageThreadTest {

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

    @Test
    public void toMap_fromMap_roundTripsScalarFields() {
        MessageThread restored = MessageThread.fromMap(sample().toMap());

        assertEquals(4242L, restored.getParentMessageId());
        assertEquals(5, restored.getReplyCount());
        assertEquals(1700000000L, restored.getUpdatedAt());
        assertEquals("alice_bob", restored.getConversationId());
        assertEquals("user", restored.getReceiverType());
        assertEquals("bob", restored.getReceiverUid());
        assertTrue(restored.isSubscribed());
        assertEquals(Integer.valueOf(2), restored.getUnreadReplyCount());
    }

    @Test
    public void fromMap_null_yieldsDefaultsNotCrash() {
        MessageThread thread = MessageThread.fromMap(null);
        assertEquals(0L, thread.getParentMessageId());
        assertFalse(thread.isSubscribed());
        assertNull(thread.getUnreadReplyCount());
    }

    @Test
    public void unknownUnreadCount_isOmittedFromMap_notForcedToZero() {
        MessageThread thread = sample();
        thread.setUnreadReplyCount(null);

        Map<String, Object> map = thread.toMap();
        assertFalse("an unknown unread count must not be serialized as a value",
                map.containsKey(MessageThread.MapKeys.UNREAD_REPLY_COUNT));
        assertNull(MessageThread.fromMap(map).getUnreadReplyCount());
    }

    @Test
    public void subscribed_roundTripsThroughTheMapAsABoolean() {
        MessageThread thread = sample();
        thread.setSubscribed(false);
        assertFalse(MessageThread.fromMap(thread.toMap()).isSubscribed());
    }

    @Test
    public void defaultSubscribed_isFalse() {
        assertFalse(new MessageThread().isSubscribed());
    }

    @Test
    public void contentEquals_matchesOnValue_differsOnAnyField() {
        assertTrue(sample().contentEquals(sample()));
        assertEquals(sample(), sample());

        MessageThread other = sample();
        other.setReceiverUid("carol");
        assertFalse(sample().contentEquals(other));

        MessageThread differentState = sample();
        differentState.setSubscribed(false);
        assertFalse(sample().contentEquals(differentState));
    }

    @Test
    public void contentEquals_nullAndWrongType_areFalseNotThrow() {
        assertFalse(sample().contentEquals(null));
        assertFalse(sample().contentEquals("not a thread"));
    }
}
