package com.cometchat.chat.messaging.send;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.CustomMessage;
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
public class CustomMessageTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_sendCustomMessageWithEmptyReceiverIDToUserShouldReturnError() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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
    public void a2_sendCustomMessageWithEmptyReceiverIDToGroupShouldReturnError() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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
    public void a3_sendCustomMessageWithEmptyCustomDataToUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getEmptyJSONObject());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_CUSTOM_DATA);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_sendCustomMessageWithEmptyCustomDataToGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                null,
                TestUtils.getEmptyJSONObject());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_CUSTOM_DATA);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a5_sendCustomMessageWithEmptyReceiverTypeToUserShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatTestConstants.EMPTY_DATA,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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
    public void a6_sendCustomMessageWithEmptyReceiverTypeToGroupShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatTestConstants.EMPTY_DATA,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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
    public void a7_sendCustomMessageWithInvalidReceiverTypeToUserShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatTestConstants.INVALID_DATA,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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
    public void a8_sendCustomMessageWithInvalidReceiverTypeToGroupShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatTestConstants.INVALID_DATA,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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
    public void a9_sendCustomMessageWithInvalidReceiverTypeAndEmptyCustmDataToUserShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatTestConstants.INVALID_DATA,
                null,
                TestUtils.getEmptyJSONObject());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_CUSTOM_DATA);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b1_sendCustomMessageWithInvalidReceiverTypeAndEmptyCustmDataToGroupShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatTestConstants.INVALID_DATA,
                null,
                TestUtils.getEmptyJSONObject());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_CUSTOM_DATA);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b2_sendCustomMessageWithInvalidReceiverIdToUserShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.INVALID_UID,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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
    public void b3_sendCustomMessageWithInvalidReceiverIdToGroupShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.INVALID_DATA,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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
    public void b4_sendCustomMessageWithInvalidReceiverIdAndEmptyCustomeDataToUserShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.INVALID_UID,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getEmptyJSONObject());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_CUSTOM_DATA) ||
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
    public void b5_sendCustomMessageWithInvalidReceiverIdAndEmptyCustomeDataToGroupShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.INVALID_DATA,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getEmptyJSONObject());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_CUSTOM_DATA) ||
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
    public void b6_sendCustomMessageToUser() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData(),receivedCustomMessage.getCustomData());
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage , false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToUser(receivedCustomMessage.getId());
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
    public void b7_sendCustomMessageToGroup() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData() , receivedCustomMessage.getCustomData());
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage , false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToGroup(receivedCustomMessage.getId());
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
    public void b8_sendCustomMessageToUserWithMuid() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getTestCustomData());
        customMessage.setMuid(String.valueOf(System.currentTimeMillis()));
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData(), receivedCustomMessage.getCustomData());
                AssertHelper.assertTrue(methodName + "Message MUID check : ", customMessage.getMuid().equalsIgnoreCase(receivedCustomMessage.getMuid()));
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToUser(receivedCustomMessage.getId());
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
    public void b9_sendCustomMessageToGroupWithMuid() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                null,
                TestUtils.getTestCustomData());
        customMessage.setMuid(String.valueOf(System.currentTimeMillis()));
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData(),receivedCustomMessage.getCustomData());
                AssertHelper.assertTrue(methodName + "Message MUID check : ", customMessage.getMuid().equalsIgnoreCase(receivedCustomMessage.getMuid()));
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToGroup(receivedCustomMessage.getId());
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
    public void c1_sendCustomMessageToUserWithMetadata() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getTestCustomData());
        customMessage.setMetadata(TestUtils.getTestMetadata());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData(),receivedCustomMessage.getCustomData());
                AssertHelper.assertTrue(methodName + "Metadata Check : ", customMessage.getMetadata().toString().equalsIgnoreCase(receivedCustomMessage.getMetadata().toString()));
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToUser(receivedCustomMessage.getId());
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
    public void c2_sendCustomMessageToGroupWithMetadata() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                null,
                TestUtils.getTestCustomData());
        customMessage.setMetadata(TestUtils.getTestMetadata());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData(),receivedCustomMessage.getCustomData());
                AssertHelper.assertTrue(methodName + "Metadata Check : ", customMessage.getMetadata().toString().equalsIgnoreCase(receivedCustomMessage.getMetadata().toString()));
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToGroup(receivedCustomMessage.getId());
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
    public void c3_sendCustomMessageToUserWithType() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_USER,
                "TEST_CUSTOM",
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData(),receivedCustomMessage.getCustomData());
                AssertHelper.assertTrue(methodName + "Type Check : ", customMessage.getType().equalsIgnoreCase(receivedCustomMessage.getType()));
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage,false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToUser(receivedCustomMessage.getId());
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
    public void c4_sendCustomMessageToGroupWithType() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                "TEST_CUSTOM",
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData(),receivedCustomMessage.getCustomData());
                AssertHelper.assertTrue(methodName + "Type Check : ", customMessage.getType().equalsIgnoreCase(receivedCustomMessage.getType()));
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage,false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToGroup(receivedCustomMessage.getId());
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
    public void c5_sendCustomMessageToUserWithSubType() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_USER,
                "TEST_CUSTOM",
                TestUtils.getTestCustomData());
        customMessage.setSubType("SUBTYPE_CUSTOM");
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData(),receivedCustomMessage.getCustomData());
                AssertHelper.assertTrue(methodName + "Type Check : ", customMessage.getType().equalsIgnoreCase(receivedCustomMessage.getType()));
                AssertHelper.assertTrue(methodName + "SubType Check : ", customMessage.getSubType().equalsIgnoreCase(receivedCustomMessage.getSubType()));
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToUser(receivedCustomMessage.getId());
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
    public void c6_sendCustomMessageToGroupWithSubType() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                "TEST_CUSTOM",
                TestUtils.getTestCustomData());
        customMessage.setSubType("SUBTYPE_CUSTOM");
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedCustomMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", customMessage.getReceiverUid().equalsIgnoreCase(receivedCustomMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", customMessage.getReceiverType().equalsIgnoreCase(receivedCustomMessage.getReceiverType()));
                AssertHelper.jsonAssert(methodName + "CustomData Check : ", customMessage.getCustomData(),receivedCustomMessage.getCustomData());
                AssertHelper.assertTrue(methodName + "Type Check : ", customMessage.getType().equalsIgnoreCase(receivedCustomMessage.getType()));
                AssertHelper.assertTrue(methodName + "SubType Check : ", customMessage.getSubType().equalsIgnoreCase(receivedCustomMessage.getSubType()));
                String validateCustomMessage = TestUtils.validateCustomMessage(receivedCustomMessage,false);
                AssertHelper.assertTrue(methodName + " Validate Custom Message : " + validateCustomMessage, validateCustomMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCustomMessageIdToGroup(receivedCustomMessage.getId());
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
    public void c7_sendCustomMessageWithReceiverTypeGroupToUserShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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
    public void c8_sendCustomMessageWithReceiverTypeUserToGroupShouldReturnError() throws InterruptedException,JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomMessage customMessage = new CustomMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_USER,
                null,
                TestUtils.getTestCustomData());
        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage receivedCustomMessage) {
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


}
