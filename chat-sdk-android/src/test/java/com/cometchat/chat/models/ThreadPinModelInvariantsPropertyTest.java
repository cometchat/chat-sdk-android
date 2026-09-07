package com.cometchat.chat.models;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.constants.PinSaveContract;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.WithNull;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Property-based tests for the model-level invariants added by threads (ENG-37567) and pin/save:
 * the presence-is-the-boolean contract on {@link BaseMessage} / {@link Conversation}, and the
 * {@link MessageThread} bridge-map round-trip. Example-based coverage lives in
 * {@code MessageThreadTest} / {@code MessageThreadCloneAndMapTest}; these properties sweep the
 * value space.
 */
class ThreadPinModelInvariantsPropertyTest {

    // ---- presence-is-the-boolean: pinnedAt/savedAt ARE the flags -------------------------------

    @Property
    void isPinnedIsExactlyPinnedAtPositivity(@ForAll long pinnedAt) {
        BaseMessage message = new BaseMessage();
        message.setPinnedAt(pinnedAt);
        assertEquals(pinnedAt > 0, message.isPinned());
    }

    @Property
    void isSavedIsExactlySavedAtPositivity(@ForAll long savedAt) {
        BaseMessage message = new BaseMessage();
        message.setSavedAt(savedAt);
        assertEquals(savedAt > 0, message.isSaved());
    }

    /** System-pinned requires BOTH a live pin and the exact sentinel — no other string qualifies. */
    @Property
    void isSystemPinnedRequiresPinAndExactSentinel(@ForAll long pinnedAt,
                                                   @ForAll @WithNull String pinnedBy) {
        BaseMessage message = new BaseMessage();
        message.setPinnedAt(pinnedAt);
        message.setPinnedBy(pinnedBy);
        boolean expected = pinnedAt > 0 && PinSaveContract.SYSTEM_PINNER_SENTINEL.equals(pinnedBy);
        assertEquals(expected, message.isSystemPinned());
    }

    @Property
    void conversationPinInvariantsMatchBaseMessageSemantics(@ForAll long pinnedAt,
                                                            @ForAll @WithNull String pinnedBy) {
        Conversation conversation = new Conversation("conv-p", CometChatConstants.CONVERSATION_TYPE_USER);
        conversation.setPinnedAt(pinnedAt);
        conversation.setPinnedBy(pinnedBy);
        assertEquals(pinnedAt > 0, conversation.isPinned());
        assertEquals(pinnedAt > 0 && PinSaveContract.SYSTEM_PINNER_SENTINEL.equals(pinnedBy),
                conversation.isSystemPinned());
    }

    // ---- MessageThread bridge map round-trip ---------------------------------------------------

    @Property
    void toMapFromMapRoundTripPreservesEveryScalarField(
            @ForAll @LongRange(min = 0, max = 1_000_000_000L) long parentMessageId,
            @ForAll @IntRange(min = 0, max = 100_000) int replyCount,
            @ForAll @LongRange(min = 0, max = 1_000_000_000L) long updatedAt,
            @ForAll @WithNull String conversationId,
            @ForAll @WithNull String receiverType,
            @ForAll @WithNull String receiverUid,
            @ForAll boolean subscribed,
            @ForAll @WithNull @IntRange(min = 0, max = 100_000) Integer unreadReplyCount) {
        MessageThread original = new MessageThread();
        original.setParentMessageId(parentMessageId);
        original.setReplyCount(replyCount);
        original.setUpdatedAt(updatedAt);
        original.setConversationId(conversationId);
        original.setReceiverType(receiverType);
        original.setReceiverUid(receiverUid);
        original.setSubscribed(subscribed);
        original.setUnreadReplyCount(unreadReplyCount);

        MessageThread restored = MessageThread.fromMap(original.toMap());

        assertEquals(parentMessageId, restored.getParentMessageId());
        assertEquals(replyCount, restored.getReplyCount());
        assertEquals(updatedAt, restored.getUpdatedAt());
        assertEquals(conversationId, restored.getConversationId());
        assertEquals(receiverType, restored.getReceiverType());
        assertEquals(receiverUid, restored.getReceiverUid());
        assertEquals(subscribed, restored.isSubscribed());
        assertEquals(unreadReplyCount, restored.getUnreadReplyCount());
    }

    /** fromMap is total: a map holding ANY value under every key must never throw. */
    @Property
    void fromMapNeverThrowsOnArbitrarilyTypedValues(@ForAll("junkValues") Map<String, Object> map) {
        assertNotNull(MessageThread.fromMap(map));
    }

    @Provide
    Arbitrary<Map<String, Object>> junkValues() {
        Arbitrary<Object> junk = Arbitraries.oneOf(
                Arbitraries.strings().ofMaxLength(10).map(s -> (Object) s),
                Arbitraries.integers().map(i -> (Object) i),
                Arbitraries.longs().map(l -> (Object) l),
                Arbitraries.of(true, false).map(b -> (Object) b),
                Arbitraries.just(new Object()),
                Arbitraries.just((Object) null)
        );
        String[] keys = {
                MessageThread.MapKeys.PARENT_MESSAGE_ID,
                MessageThread.MapKeys.REPLY_COUNT,
                MessageThread.MapKeys.UPDATED_AT,
                MessageThread.MapKeys.CONVERSATION_ID,
                MessageThread.MapKeys.RECEIVER_TYPE,
                MessageThread.MapKeys.RECEIVER_UID,
                MessageThread.MapKeys.SUBSCRIBED,
                MessageThread.MapKeys.UNREAD_REPLY_COUNT,
                MessageThread.MapKeys.PARENT_MESSAGE,
                MessageThread.MapKeys.LAST_REPLY,
        };
        return junk.list().ofSize(keys.length).map(values -> {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], values.get(i));
            }
            return map;
        });
    }
}
