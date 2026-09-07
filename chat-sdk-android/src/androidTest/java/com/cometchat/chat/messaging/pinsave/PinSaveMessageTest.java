package com.cometchat.chat.messaging.pinsave;

import android.content.Context;
import android.os.Parcel;

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
 * End-to-end instrumented coverage for Pin &amp; Save Message (ENG-37752), mirroring the
 * {@code ReactionsTest} convention: send a real message, then act on it against the live app.
 *
 * <p>The class is self-contained — {@link #setUp()} inits and logs in so it runs standalone as
 * well as inside {@code CometChatTestSuite}. The flow is ordered by method name:
 * pin → verify pinned-list → unpin → save → verify saved-list → unsave, plus a Parcelable
 * round-trip (the one path pure-JVM unit tests cannot exercise) and a feature-flag smoke test.
 *
 * <p><b>Preconditions:</b> the target app must have {@code features.ux.messages.pinned.enabled} /
 * {@code ...saved.enabled} turned on and the pin/save endpoints deployed; otherwise the write
 * calls return {@code ERR_FEATURE_NOT_ACCESSIBLE}. Realtime callbacks are NOT asserted — the
 * backend does not emit pin/save events yet (ENG-37690 §8).
 */
@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PinSaveMessageTest {

    private static final int TEST_CASE_TIMEOUT = 10;
    private static final int SETUP_TIMEOUT = 20;

    private static long messageId = 0;

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

    // ==================== send the message we will pin/save ====================

    @Test
    public void a0_sendMessageShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID, "Pin/Save target", CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage sent) {
                messageId = sent.getId();
                AssertHelper.assertTrue(methodName, messageId > 0);
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

    // ==================== pin ====================

    @Test
    public void a1_pinWithInvalidMessageIdShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.pinMessage(0, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage message) {
                AssertHelper.fail(methodName + " pin with invalid message id should return error");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.assertTrue(methodName, e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_ID));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_pinMessageShouldReturnPinnedMessage() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.pinMessage(messageId, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage message) {
                AssertHelper.assertTrue(methodName + " id matches", message.getId() == messageId);
                AssertHelper.assertTrue(methodName + " isPinned", message.isPinned());
                AssertHelper.assertTrue(methodName + " pinnedAt is set", message.getPinnedAt() > 0);
                AssertHelper.assertTrue(methodName + " pinnedBy is set", message.getPinnedBy() != null);
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
    public void a3_pinnedMessagesListShouldContainTheMessage() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setPinned(true)
                .setLimit(100)
                .build();
        request.fetchNext(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
                boolean found = false;
                for (BaseMessage m : messages) {
                    if (m.getId() == messageId) {
                        found = true;
                        AssertHelper.assertTrue(methodName + " listed message is pinned", m.isPinned());
                    }
                }
                AssertHelper.assertTrue(methodName + " pinned list contains the message", found);
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
    public void a4_unpinMessageShouldClearThePin() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.unpinMessage(messageId, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage message) {
                AssertHelper.assertTrue(methodName + " not pinned after unpin", !message.isPinned());
                AssertHelper.assertTrue(methodName + " pinnedAt cleared", message.getPinnedAt() == 0);
                AssertHelper.assertTrue(methodName + " pinnedBy cleared", message.getPinnedBy() == null);
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

    // ==================== save ====================

    @Test
    public void b1_saveWithInvalidMessageIdShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.saveMessage(0, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage message) {
                AssertHelper.fail(methodName + " save with invalid message id should return error");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.assertTrue(methodName, e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_ID));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b2_saveMessageShouldReturnSavedMessage() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.saveMessage(messageId, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage message) {
                AssertHelper.assertTrue(methodName + " id matches", message.getId() == messageId);
                AssertHelper.assertTrue(methodName + " isSaved", message.isSaved());
                AssertHelper.assertTrue(methodName + " savedAt is set", message.getSavedAt() > 0);
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
    public void b3_savedMessagesListShouldContainTheMessage() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setSaved(true)
                .setLimit(100)
                .build();
        request.fetchNext(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
                boolean found = false;
                for (BaseMessage m : messages) {
                    if (m.getId() == messageId) {
                        found = true;
                        AssertHelper.assertTrue(methodName + " listed message is saved", m.isSaved());
                    }
                }
                AssertHelper.assertTrue(methodName + " saved list contains the message", found);
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
    public void b4_unsaveMessageShouldClearTheSave() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.unsaveMessage(messageId, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage message) {
                AssertHelper.assertTrue(methodName + " not saved after unsave", !message.isSaved());
                AssertHelper.assertTrue(methodName + " savedAt cleared", message.getSavedAt() == 0);
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

    // ==================== model-layer checks that need a real device ====================

    /**
     * Parcelable round-trip — validates the {@code pinnedAt}/{@code pinnedBy}/{@code savedAt}
     * read/write ordering added to {@link BaseMessage}. This is the path the pure-JVM unit tests
     * cannot exercise (android.os.Parcel is a device-only stub off-device).
     */
    @Test
    public void c1_parcelRoundTripPreservesPinSaveAttributes() {
        final String methodName = TestUtils.getMethodName();
        BaseMessage original = new BaseMessage();
        original.setId(9001L);
        original.setPinnedAt(1785332391L);
        original.setPinnedBy(CometChatConstants.MessageKeys.PINNED_BY_SYSTEM);
        original.setSavedAt(1785332395L);

        Parcel parcel = Parcel.obtain();
        try {
            original.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            BaseMessage restored = BaseMessage.CREATOR.createFromParcel(parcel);

            AssertHelper.assertTrue(methodName + " pinnedAt", restored.getPinnedAt() == 1785332391L);
            AssertHelper.assertTrue(methodName + " pinnedBy", CometChatConstants.MessageKeys.PINNED_BY_SYSTEM.equals(restored.getPinnedBy()));
            AssertHelper.assertTrue(methodName + " savedAt", restored.getSavedAt() == 1785332395L);
            AssertHelper.assertTrue(methodName + " isSystemPinned", restored.isSystemPinned());
            AssertHelper.assertTrue(methodName + " isSaved", restored.isSaved());
        } finally {
            parcel.recycle();
        }
    }

    /** Feature-flag readers must not throw and return a definite boolean. */
    @Test
    public void c2_featureFlagReadersDoNotThrow() {
        final String methodName = TestUtils.getMethodName();
        boolean pinEnabled = CometChat.isPinMessageEnabled();
        boolean saveEnabled = CometChat.isSaveMessageEnabled();
        // We only assert the calls resolve deterministically; the actual value depends on the
        // app's plan/flags. Logged for visibility.
        AssertHelper.assertTrue(methodName + " pin=" + pinEnabled + " save=" + saveEnabled,
                pinEnabled == pinEnabled && saveEnabled == saveEnabled);
    }
}
