package com.cometchat.chat.messaging.edit;


import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.CustomMessage;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EditMessageTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_editTextMessageWithNoMessageIdForUser() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_EDITED_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_editTextMessageWithNoMessageIdForGroup() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_EDITED_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_editTextMessageWithZeroMessageIdForUser() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_EDITED_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        textMessage.setId(0);
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_editTextMessageWithZeroMessageIdForGroup() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_EDITED_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        textMessage.setId(0);
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a5_editTextMessageWithInvalidMessageIdForUser() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_EDITED_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        textMessage.setId(CometChatTestConstants.INVALID_MESSAGE_ID);
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_MESSAGE_ID_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_editTextMessageWithInvalidMessageIdForGroup() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_EDITED_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        textMessage.setId(CometChatTestConstants.INVALID_MESSAGE_ID);
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_MESSAGE_ID_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a7_editTextMessageForUser() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_EDITED_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_USER);
        textMessage.setId(TestPreferenceHelper.getLastTextMessageIdSentToUser());
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof TextMessage);
                TextMessage receivedTextMessage = ((TextMessage) receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedTextMessage.getEditedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedTextMessage.getEditedBy() != null);
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
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
    public void a8_editTextMessageForGroup() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_EDITED_MESSAGE_TEXT, System.currentTimeMillis()),
                CometChatConstants.RECEIVER_TYPE_GROUP);
        textMessage.setId(TestPreferenceHelper.getLastTextMessageIdSentToGroup());
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof TextMessage);
                TextMessage receivedTextMessage = ((TextMessage) receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedTextMessage.getEditedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedTextMessage.getEditedBy() != null);
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage ,false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
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
    public void a9_editCustomMessageForUser() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getTestEditedCustomData());
        customMessage.setId(TestPreferenceHelper.getLastCustomMessageIdSentToUser());
        CometChat.editMessage(customMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof CustomMessage);
                CustomMessage receivedCustomMessage = ((CustomMessage) receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Custom Data check : ", customMessage.getCustomData().toString().equalsIgnoreCase(receivedCustomMessage.getCustomData().toString()));
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedCustomMessage.getEditedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedCustomMessage.getEditedBy() != null);
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
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
    public void b1_editCustomMessageForGroup() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                null,
                TestUtils.getTestEditedCustomData());
        customMessage.setId(TestPreferenceHelper.getLastCustomMessageIdSentToGroup());
        CometChat.editMessage(customMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof CustomMessage);
                CustomMessage receivedCustomMessage = ((CustomMessage) receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Custom Data check : ", customMessage.getCustomData().toString().equalsIgnoreCase(receivedCustomMessage.getCustomData().toString()));
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedCustomMessage.getEditedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedCustomMessage.getEditedBy() != null);
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
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

}
