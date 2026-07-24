package com.cometchat.chat.messaging.delete;


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

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteMessageTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_deleteMessageWithMessageIdZeroShouldReturnError() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteMessage(0, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage baseMessage) {
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
    public void a2_deleteMessageWithInvalidMessageIdShouldReturnError() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteMessage(CometChatTestConstants.INVALID_MESSAGE_ID, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage baseMessage) {
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
    public void a3_deleteTextMessageForUser() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteMessage(TestPreferenceHelper.getLastTextMessageIdSentToUser(), new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Message Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof TextMessage);
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedBaseMessage.getDeletedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedBaseMessage.getDeletedBy() != null);
                TextMessage receivedTextMessage = (TextMessage) receivedBaseMessage;
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, true);
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
    public void a4_deleteTextMessageForGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteMessage(TestPreferenceHelper.getLastTextMessageIdSentToGroup(), new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof TextMessage);
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedBaseMessage.getDeletedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedBaseMessage.getDeletedBy() != null);
                TextMessage receivedTextMessage = (TextMessage) receivedBaseMessage;
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, true);
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
    public void a5_deleteCustomMessageForUser() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteMessage(TestPreferenceHelper.getLastCustomMessageIdSentToUser(), new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof CustomMessage);
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedBaseMessage.getDeletedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedBaseMessage.getDeletedBy() != null);
                CustomMessage receivedCustomMessage = (CustomMessage) receivedBaseMessage;
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, true);
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
    public void a6_deleteCustomMessageForGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteMessage(TestPreferenceHelper.getLastCustomMessageIdSentToGroup(), new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof CustomMessage);
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedBaseMessage.getDeletedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedBaseMessage.getDeletedBy() != null);
                CustomMessage receivedCustomMessage = (CustomMessage) receivedBaseMessage;
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, true);
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


    public void a7_deleteMediaMessageForUser() throws InterruptedException{
        // TODO
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteMessage(TestPreferenceHelper.getLastCustomMessageIdSentToGroup(), new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof TextMessage);
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedBaseMessage.getDeletedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedBaseMessage.getDeletedBy() != null);
                CustomMessage receivedCustomMessage = (CustomMessage) receivedBaseMessage;
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, true);
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

    public void a8_deleteMediaMessageForUser() throws InterruptedException{
        // TODO
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteMessage(TestPreferenceHelper.getLastCustomMessageIdSentToGroup(), new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage receivedBaseMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedBaseMessage);
                AssertHelper.assertTrue(methodName + "Message instance check : ", receivedBaseMessage instanceof TextMessage);
                AssertHelper.assertTrue(methodName + "Message Edited At check : ", receivedBaseMessage.getDeletedAt() != 0);
                AssertHelper.assertTrue(methodName + "Message Edited By check : ", receivedBaseMessage.getDeletedBy() != null);
                CustomMessage receivedCustomMessage = (CustomMessage) receivedBaseMessage;
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, true);
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
