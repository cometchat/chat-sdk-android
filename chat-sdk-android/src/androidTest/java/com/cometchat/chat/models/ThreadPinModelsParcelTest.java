package com.cometchat.chat.models;

import android.os.Parcel;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.constants.PinSaveContract;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.TestUtils;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Device-only Parcel round-trips for the models touched by threads (ENG-37567) and pin/save.
 * {@code MessageThreadTest}'s javadoc defers parcel coverage to "instrumented suites" — this class
 * is that suite. Pure Parcel, no network, no init/login: it validates the read/write ordering that
 * pure-JVM unit tests cannot exercise (android.os.Parcel is a throwing stub off-device).
 *
 * <p>{@code PinSaveMessageTest.c1} already round-trips {@code pinnedAt}/{@code pinnedBy}/
 * {@code savedAt} on {@link BaseMessage}; this class covers the remaining parceled additions:
 * {@link BaseMessage#isThreadSubscribed()}, the whole {@link MessageThread} model, and the
 * {@link Conversation} pin attributes.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class ThreadPinModelsParcelTest {

    private static MessageThread roundTrip(MessageThread original) {
        Parcel parcel = Parcel.obtain();
        try {
            original.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            return MessageThread.CREATOR.createFromParcel(parcel);
        } finally {
            parcel.recycle();
        }
    }

    @Test
    public void messageThread_parcelRoundTrip_preservesAllFields() throws Exception {
        final String methodName = TestUtils.getMethodName();

        BaseMessage parent = new BaseMessage();
        parent.setId(101L);
        parent.setThreadSubscribed(true);
        BaseMessage lastReply = new BaseMessage();
        lastReply.setId(202L);

        MessageThread original = new MessageThread();
        original.setParentMessageId(101L);
        original.setParentMessage(parent);
        original.setReplyCount(7);
        original.setLastReply(lastReply);
        original.setUpdatedAt(1785332391L);
        original.setConversationId("conv-1");
        original.setReceiverType(CometChatConstants.RECEIVER_TYPE_GROUP);
        original.setReceiverUid("group-1");
        original.setSubscribed(true);
        original.setUnreadReplyCount(3);
        original.setRawData(new JSONObject("{\"extra\":\"kept\"}"));

        MessageThread restored = roundTrip(original);

        AssertHelper.assertTrue(methodName + " parentMessageId", restored.getParentMessageId() == 101L);
        AssertHelper.assertTrue(methodName + " nested parent id", restored.getParentMessage() != null && restored.getParentMessage().getId() == 101L);
        AssertHelper.assertTrue(methodName + " nested parent threadSubscribed", restored.getParentMessage().isThreadSubscribed());
        AssertHelper.assertTrue(methodName + " replyCount", restored.getReplyCount() == 7);
        AssertHelper.assertTrue(methodName + " nested lastReply id", restored.getLastReply() != null && restored.getLastReply().getId() == 202L);
        AssertHelper.assertTrue(methodName + " updatedAt", restored.getUpdatedAt() == 1785332391L);
        AssertHelper.assertTrue(methodName + " conversationId", "conv-1".equals(restored.getConversationId()));
        AssertHelper.assertTrue(methodName + " receiverType", CometChatConstants.RECEIVER_TYPE_GROUP.equals(restored.getReceiverType()));
        AssertHelper.assertTrue(methodName + " receiverUid", "group-1".equals(restored.getReceiverUid()));
        AssertHelper.assertTrue(methodName + " subscribed", restored.isSubscribed());
        AssertHelper.assertTrue(methodName + " unreadReplyCount", Integer.valueOf(3).equals(restored.getUnreadReplyCount()));
        AssertHelper.assertTrue(methodName + " rawData", restored.getRawData() != null && "kept".equals(restored.getRawData().optString("extra")));
    }

    /** The {@code null != 0} contract on unreadReplyCount must survive the -1 parcel sentinel. */
    @Test
    public void messageThread_parcelRoundTrip_nullUnreadCountStaysNull() {
        final String methodName = TestUtils.getMethodName();

        MessageThread original = new MessageThread();
        original.setParentMessageId(5L);
        original.setUnreadReplyCount(null);

        MessageThread restored = roundTrip(original);

        AssertHelper.assertTrue(methodName + " unreadReplyCount stays null", restored.getUnreadReplyCount() == null);
        AssertHelper.assertTrue(methodName + " nested parent stays null", restored.getParentMessage() == null);
        AssertHelper.assertTrue(methodName + " lastReply stays null", restored.getLastReply() == null);
        AssertHelper.assertTrue(methodName + " rawData stays null", restored.getRawData() == null);
        AssertHelper.assertTrue(methodName + " subscribed default false", !restored.isSubscribed());
    }

    /** {@code zero} unreadReplyCount is a real value and must NOT collapse into the null sentinel. */
    @Test
    public void messageThread_parcelRoundTrip_zeroUnreadCountStaysZero() {
        final String methodName = TestUtils.getMethodName();

        MessageThread original = new MessageThread();
        original.setUnreadReplyCount(0);

        MessageThread restored = roundTrip(original);

        AssertHelper.assertTrue(methodName + " unreadReplyCount stays 0", Integer.valueOf(0).equals(restored.getUnreadReplyCount()));
    }

    /** Covers the {@code threadSubscribed} byte added to BaseMessage's parcel contract. */
    @Test
    public void baseMessage_parcelRoundTrip_preservesThreadSubscribed() {
        final String methodName = TestUtils.getMethodName();

        BaseMessage original = new BaseMessage();
        original.setId(9002L);
        original.setThreadSubscribed(true);

        Parcel parcel = Parcel.obtain();
        try {
            original.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            BaseMessage restored = BaseMessage.CREATOR.createFromParcel(parcel);

            AssertHelper.assertTrue(methodName + " id", restored.getId() == 9002L);
            AssertHelper.assertTrue(methodName + " threadSubscribed", restored.isThreadSubscribed());
        } finally {
            parcel.recycle();
        }
    }

    /** Covers the {@code pinnedAt}/{@code pinnedBy} tail added to Conversation's parcel contract. */
    @Test
    public void conversation_parcelRoundTrip_preservesPinAttributes() {
        final String methodName = TestUtils.getMethodName();

        Conversation original = new Conversation("conv-42", CometChatConstants.CONVERSATION_TYPE_USER);
        original.setPinnedAt(1785332391L);
        original.setPinnedBy(PinSaveContract.SYSTEM_PINNER_SENTINEL);

        Parcel parcel = Parcel.obtain();
        try {
            original.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            Conversation restored = Conversation.CREATOR.createFromParcel(parcel);

            AssertHelper.assertTrue(methodName + " conversationId", "conv-42".equals(restored.getConversationId()));
            AssertHelper.assertTrue(methodName + " pinnedAt", restored.getPinnedAt() == 1785332391L);
            AssertHelper.assertTrue(methodName + " pinnedBy", PinSaveContract.SYSTEM_PINNER_SENTINEL.equals(restored.getPinnedBy()));
            AssertHelper.assertTrue(methodName + " isPinned", restored.isPinned());
            AssertHelper.assertTrue(methodName + " isSystemPinned", restored.isSystemPinned());
        } finally {
            parcel.recycle();
        }
    }

    /** Not-pinned conversations must read back as not pinned (absence semantics, never 0-as-set). */
    @Test
    public void conversation_parcelRoundTrip_notPinnedStaysNotPinned() {
        final String methodName = TestUtils.getMethodName();

        Conversation original = new Conversation("conv-43", CometChatConstants.CONVERSATION_TYPE_USER);

        Parcel parcel = Parcel.obtain();
        try {
            original.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            Conversation restored = Conversation.CREATOR.createFromParcel(parcel);

            AssertHelper.assertTrue(methodName + " not pinned", !restored.isPinned());
            AssertHelper.assertTrue(methodName + " pinnedBy null", restored.getPinnedBy() == null);
            AssertHelper.assertTrue(methodName + " not system pinned", !restored.isSystemPinned());
        } finally {
            parcel.recycle();
        }
    }
}
