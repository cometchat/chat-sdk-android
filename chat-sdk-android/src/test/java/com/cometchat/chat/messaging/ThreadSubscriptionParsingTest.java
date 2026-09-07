package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.CustomMessage;
import com.cometchat.chat.models.TextMessage;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Thread subscription / message-parse wiring.
 *
 * <p>Proves that {@code ThreadParser.applyThreadAttributes} is actually reached from the subtype
 * {@code fromJson} methods, so {@link BaseMessage#isThreadSubscribed()} reflects the server's flag
 * on a real frame rather than only on a direct chokepoint call.
 *
 * <p>The load-bearing rule these vectors pin down: <b>the flag is only present when the request
 * asked for it</b>. A socket-delivered frame carries no {@code threadSubscribed} key and so reads
 * {@code false} — which means "the server did not tell me", not "the user is unsubscribed".
 * Resolving that distinction is the consumer's job; the SDK holds no state and never infers.
 */
public class ThreadSubscriptionParsingTest {

    private static final String KEY_THREAD_SUBSCRIBED = "threadSubscribed";

    private static JSONObject textFrame(long id) throws Exception {
        return MessagePayloads.envelope(id, CometChatConstants.CATEGORY_MESSAGE,
                CometChatConstants.MESSAGE_TYPE_TEXT, "receiver-1", CometChatConstants.RECEIVER_TYPE_USER);
    }

    // ==================== wiring: the chokepoint is reached from fromJson ====================

    @Test
    public void textMessageFrame_withFlagTrue_isThreadSubscribed() throws Exception {
        BaseMessage message = BaseMessage.processMessage(textFrame(101L).put(KEY_THREAD_SUBSCRIBED, true));

        assertTrue(message instanceof TextMessage);
        assertTrue(message.isThreadSubscribed());
    }

    @Test
    public void textMessageFrame_withFlagFalse_isNotThreadSubscribed() throws Exception {
        BaseMessage message = BaseMessage.processMessage(textFrame(102L).put(KEY_THREAD_SUBSCRIBED, false));

        assertFalse(message.isThreadSubscribed());
    }

    @Test
    public void customMessageFrame_alsoRoutesThroughTheChokepoint() throws Exception {
        JSONObject json = MessagePayloads.envelope(202L, CometChatConstants.CATEGORY_CUSTOM,
                        "myCustomType", "receiver-1", CometChatConstants.RECEIVER_TYPE_USER)
                .put(KEY_THREAD_SUBSCRIBED, true);

        BaseMessage message = BaseMessage.processMessage(json);

        assertTrue(message instanceof CustomMessage);
        assertTrue(message.isThreadSubscribed());
    }

    // ==================== the rule consumers must internalise ====================

    /**
     * A frame with no flag — the shape every socket-delivered message has, because the socket cannot
     * be asked for {@code withThreadSubscribed=true}. It must read {@code false} and must not throw.
     */
    @Test
    public void frameWithoutTheFlag_readsFalse_neverThrows() throws Exception {
        BaseMessage message = BaseMessage.processMessage(textFrame(303L));

        assertFalse("absence is normalised to false; the consumer decides what that means",
                message.isThreadSubscribed());
    }

    /** A thread reply (parentMessageId set) is an ordinary message and parses the same way. */
    @Test
    public void threadReplyFrame_parsesTheFlagLikeAnyOtherMessage() throws Exception {
        JSONObject json = textFrame(404L)
                .put(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID, 400L)
                .put(KEY_THREAD_SUBSCRIBED, true);

        BaseMessage message = BaseMessage.processMessage(json);

        assertTrue(message.isThreadSubscribed());
        assertTrue(message.getParentMessageId() == 400L);
    }

    /** A wrong-typed flag must not make the accessor lie, and must not blow up the whole parse. */
    @Test
    public void wrongTypedFlag_readsFalse_ratherThanFailingTheParse() throws Exception {
        BaseMessage message = BaseMessage.processMessage(
                textFrame(505L).put(KEY_THREAD_SUBSCRIBED, new JSONObject()));

        assertFalse(message.isThreadSubscribed());
    }
}
