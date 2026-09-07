package com.cometchat.chat.messaging.threads;

import android.content.Context;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.AppSettings;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.MessagesRequest;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestPreferenceHelper;
import com.cometchat.chat.utils.TestUtils;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * End-to-end instrumented coverage for thread subscriptions (ENG-37567), mirroring the
 * {@code PinSaveMessageTest} convention: send a real message, then act on it against the live app.
 *
 * <p>The class is self-contained — {@link #setUp()} inits and logs in so it runs standalone as
 * well as inside {@code CometChatTestSuite}. The flow is ordered by method name:
 * subscribe → subscribe again (idempotency) → verify via fetch → unsubscribe → verify via fetch
 * → unsubscribe again (idempotency), plus invalid-id validation on both calls.
 *
 * <p>Verification uses the documented confirmation path: a message fetch (thread subscription
 * state is included by default, see {@code MessagesRequestBuilder#withThreadSubscribed}) and
 * {@link BaseMessage#isThreadSubscribed()} on the parent message. Realtime events are NOT
 * asserted — subscribe/unsubscribe acks carry no follow-up event by design.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ThreadSubscriptionTest {

    private static final int TEST_CASE_TIMEOUT = 10;
    private static final int SETUP_TIMEOUT = 20;

    private static long parentMessageId = 0;

    @BeforeClass
    public static void setUp() throws InterruptedException {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        TestPreferenceHelper.init(context);

        // Init (idempotent — safe when the suite already initialised).
        final CountDownLatch initLatch = new CountDownLatch(1);
        AppSettings.AppSettingsBuilder appSettingsBuilder = new AppSettings.AppSettingsBuilder();
        appSettingsBuilder.setRegion(CometChatTestConstants.REGION);
        appSettingsBuilder.subscribePresenceForAllUsers();
        if (CometChatTestConstants.isStaging) {
            appSettingsBuilder.overrideAdminHost(CometChatTestConstants.APP_ID + ".api-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
            appSettingsBuilder.overrideClientHost(CometChatTestConstants.APP_ID + ".apiclient-" + CometChatTestConstants.REGION + ".cometchat-staging.com/v3.0");
        }
        CometChat.init(context, CometChatTestConstants.APP_ID, appSettingsBuilder.build(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                initLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                initLatch.countDown();
            }
        });
        initLatch.await(SETUP_TIMEOUT, TimeUnit.SECONDS);

        // Login only if a session is not already active (suite may have logged in already).
        if (CometChat.getLoggedInUser() == null) {
            final CountDownLatch loginLatch = new CountDownLatch(1);
            CometChat.login(CometChatTestConstants.LOGIN_UID, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<com.cometchat.chat.models.User>() {
                @Override
                public void onSuccess(com.cometchat.chat.models.User user) {
                    loginLatch.countDown();
                }

                @Override
                public void onError(CometChatException e) {
                    loginLatch.countDown();
                }
            });
            loginLatch.await(SETUP_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    /** Fetches the parent message from the group and asserts its thread subscription state. */
    private void assertParentSubscriptionState(final String methodName, final boolean expectedSubscribed) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .withThreadSubscribed(true)
                .setLimit(100)
                .build();
        request.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
                boolean found = false;
                for (BaseMessage m : messages) {
                    if (m.getId() == parentMessageId) {
                        found = true;
                        AssertHelper.assertTrue(methodName + " isThreadSubscribed == " + expectedSubscribed,
                                m.isThreadSubscribed() == expectedSubscribed);
                    }
                }
                AssertHelper.assertTrue(methodName + " fetch contains the parent message", found);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    // ==================== send the message whose thread we will follow ====================

    @Test
    public void a0_sendMessageShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID, "Thread subscription target", CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage sent) {
                parentMessageId = sent.getId();
                AssertHelper.assertTrue(methodName, parentMessageId > 0);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    // ==================== subscribe ====================

    @Test
    public void a1_subscribeWithInvalidMessageIdShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.subscribeToThread(0, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String ack) {
                AssertHelper.fail(methodName + " subscribe with invalid message id should return error");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.assertTrue(methodName, e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_subscribeToThreadShouldReturnAck() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.subscribeToThread(parentMessageId, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String ack) {
                AssertHelper.assertTrue(methodName + " ack is non-null", ack != null);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    /** Subscribing is documented as idempotent — a repeat call must also succeed. */
    @Test
    public void a3_subscribeToThreadAgainShouldStillSucceed() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.subscribeToThread(parentMessageId, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String ack) {
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_fetchedParentShouldReportThreadSubscribed() throws InterruptedException {
        assertParentSubscriptionState(TestUtils.getMethodName(), true);
    }

    // ==================== unsubscribe ====================

    @Test
    public void b1_unsubscribeWithInvalidMessageIdShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.unsubscribeFromThread(0, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String ack) {
                AssertHelper.fail(methodName + " unsubscribe with invalid message id should return error");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.assertTrue(methodName, e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b2_unsubscribeFromThreadShouldReturnAck() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.unsubscribeFromThread(parentMessageId, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String ack) {
                AssertHelper.assertTrue(methodName + " ack is non-null", ack != null);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b3_fetchedParentShouldReportNotThreadSubscribed() throws InterruptedException {
        assertParentSubscriptionState(TestUtils.getMethodName(), false);
    }

    /** Unsubscribing is documented as idempotent — a repeat call must also succeed. */
    @Test
    public void b4_unsubscribeFromThreadAgainShouldStillSucceed() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.unsubscribeFromThread(parentMessageId, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String ack) {
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }
}
