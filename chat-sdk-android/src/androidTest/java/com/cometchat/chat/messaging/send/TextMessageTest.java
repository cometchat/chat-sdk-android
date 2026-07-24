package com.cometchat.chat.messaging.send;


import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Conversation;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestPreferenceHelper;
import com.cometchat.chat.utils.TestUtils;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TextMessageTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_sendTextMessageWithEmptyReceiverIDToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.EMPTY_DATA,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_BLANK_UID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_sendTextMessageWithEmptyReceiverIDToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.EMPTY_DATA,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_BLANK_UID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_sendTextMessageWithEmptyTextToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_sendTextMessageWithEmptyTextToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a5_sendTextMessageWithEmptyReceiverTypeToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatTestConstants.EMPTY_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_sendTextMessageWithEmptyReceiverTypeToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatTestConstants.EMPTY_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a7_sendTextMessageWithInvalidReceiverTypeToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a8_sendTextMessageWithInvalidReceiverTypeToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a9_sendTextMessageWithInvalidReceiverIDToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.INVALID_UID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_UID_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b1_sendTextMessageWithInvalidReceiverIDToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.INVALID_DATA,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_GUID_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b2_sendTextMessageWithInvalidReceiverTypeAndEmptyMessageTextToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b3_sendTextMessageWithInvalidReceiverTypeAndEmptyMessageTextToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b4_sendTextMessageWithInvalidReceiverTypeAndInvalidReceiverIdAndEmptyMessageTextToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.INVALID_UID,
                CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY) ||
                        e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_UID_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b5_sendTextMessageWithInvalidReceiverTypeAndInvalidReceiverIdAndEmptyMessageTextToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.INVALID_DATA,
                CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY) ||
                        e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_GUID_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b6_sendTextMessageWithValidUIDAndInvalidReceiverTypeToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b7_sendTextMessageWithValidGUIDAndInvalidReceiverTypeToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b8_sendTextMessageWithInvalidReceiverIdAndEmptyMessageTextToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.INVALID_UID,
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b9_sendTextMessageWithInvalidReceiverIdAndEmptyMessageTextToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.INVALID_DATA,
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c1_sendTextMessageWithInvalidReceiverIdAndInvalidReceiverTypeToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.INVALID_DATA,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    @Test
    public void c2_sendTextMessageWithInvalidReceiverIdAndInvalidReceiverTypeToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.INVALID_DATA,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatTestConstants.INVALID_DATA);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c3_sendTextMessageToUser() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToUser(receivedTextMessage.getId());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c4_sendTextMessageToGroup() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToGroup(receivedTextMessage.getId());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c5_sendTextMessageWithMuidToUser() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        textMessage.setMuid(String.valueOf(System.currentTimeMillis()));
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                AssertHelper.assertTrue(methodName + "Message MUID check : ", textMessage.getMuid().equalsIgnoreCase(receivedTextMessage.getMuid()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToUser(receivedTextMessage.getId());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c6_sendTextMessageWithMuidToGroup() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        textMessage.setMuid(String.valueOf(System.currentTimeMillis()));
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                AssertHelper.assertTrue(methodName + "Message MUID check : ", textMessage.getMuid().equalsIgnoreCase(receivedTextMessage.getMuid()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToGroup(receivedTextMessage.getId());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c7_sendTextMessageWithMetadataToUser() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        textMessage.setMetadata(TestUtils.getTestMetadata());
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                AssertHelper.assertTrue(methodName + "Message Metadata check : ", textMessage.getMetadata().toString().equalsIgnoreCase(receivedTextMessage.getMetadata().toString()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToUser(receivedTextMessage.getId());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c8_sendTextMessageWithMetadataToGroup() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        textMessage.setMetadata(TestUtils.getTestMetadata());
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                AssertHelper.assertTrue(methodName + "Message Metadata check : ", textMessage.getMetadata().toString().equalsIgnoreCase(receivedTextMessage.getMetadata().toString()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToGroup(receivedTextMessage.getId());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c9_sendTextMessageWithReceiverTypeGroupToUserMustThrowError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_GUID_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void d1_sendTextMessageWithReceiverTypeUserToGroupMustThrowError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_UID_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void d2_tagConversationWithSuperhero2ShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags = new ArrayList<>();
        tags.add("conversation_tag1");
        tags.add("conversation_tag2");
        CometChat.tagConversation(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_USER,
                tags,
                new CometChat.CallbackListener<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        Assert.assertNotNull(methodName + " Conversation List null check : ", conversation);
                        String validateConversation = TestUtils.validateConversation(conversation);
                        AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversation, validateConversation.equalsIgnoreCase(""));
                        boolean validateTags = false;
                        if (conversation.getTags() != null && (conversation.getTags().contains("conversation_tag1")
                                || conversation.getTags().contains("conversation_tag2"))) {
                            validateTags = true;
                        } else {
                            validateTags = false;
                        }
                        AssertHelper.assertTrue(methodName + "Validate Tags :", validateTags);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        AssertHelper.fail(methodName + e.getMessage());
                        countDownLatch.countDown();
                    }
                });
    }

    @Test
    public void d3_tagConversationWithSupergroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags = new ArrayList<>();
        tags.add("conversation_tag1");
        tags.add("conversation_tag2");
        CometChat.tagConversation(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                tags,
                new CometChat.CallbackListener<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        Assert.assertNotNull(methodName + " Conversation List null check : ", conversation);
                        String validateConversation = TestUtils.validateConversation(conversation);
                        AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversation, validateConversation.equalsIgnoreCase(""));
                        boolean validateTags = false;
                        if (conversation.getTags() != null && (conversation.getTags().contains("conversation_tag1")
                                || conversation.getTags().contains("conversation_tag2"))) {
                            validateTags = true;
                        } else {
                            validateTags = false;
                        }
                        AssertHelper.assertTrue(methodName + "Validate Tags :", validateTags);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        AssertHelper.fail(methodName + e.getMessage());
                        countDownLatch.countDown();
                    }
                });
    }

}
