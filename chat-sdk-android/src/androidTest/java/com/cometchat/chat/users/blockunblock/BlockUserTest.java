package com.cometchat.chat.users.blockunblock;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.CurrentUserRepo;
import com.cometchat.chat.core.SettingsRepo;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestPreferenceHelper;
import com.cometchat.chat.utils.TestUtils;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BlockUserTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @BeforeClass
    public static void markPreviousMessagesAsRead(){
        CometChat.markAsRead(TestPreferenceHelper.getLastTextMessageIdSentToUser(),CometChatTestConstants.RECEIVER_UID_1, CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.markAsRead(TestPreferenceHelper.getLastTextMessageIdSentToGroup(),CometChatTestConstants.RECEIVER_GUID, CometChatConstants.RECEIVER_TYPE_GROUP);
    }

    @Test
    public void a1_blockUsersWithEmptyUsersArrayShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.blockUsers(new ArrayList<String>(), new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
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
    public void a2_blockUsersWithValidUsersArrayShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> uids = new ArrayList<>();
        uids.add(CometChatTestConstants.RECEIVER_UID_1);
        uids.add(CometChatTestConstants.RECEIVER_UID_2);
        CometChat.blockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Assert.assertNotNull(methodName + " Map null check : ", stringStringHashMap);
                AssertHelper.assertTrue(methodName + " map count check : ", stringStringHashMap.size() == 2);
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
    public void a3_sendMessageToBlockedUserShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_UID_1,
                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_BLOCKED_RECEIVER);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_sendMessageFromBlockedUserShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.RECEIVER_UID_1, CometChatTestConstants.VALID_API_KEY
                , new CometChat.CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        String validateUser = TestUtils.validateUser(user);
                        AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                        String validateLoggedInUser = TestUtils.validateUser(CometChat.getLoggedInUser());
                        AssertHelper.assertTrue(methodName + " Validate Logged In User : " + validateLoggedInUser, validateLoggedInUser.equalsIgnoreCase(""));
                        CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
                        Assert.assertNotNull(methodName + " CurrentUser Null Check : ", currentUser);
                        Assert.assertNotNull(methodName + " CurrentUser AuthToken Check : ", currentUser.getAuthToken());
                        Assert.assertNotNull(methodName + " CurrentUser Identity Check : ", currentUser.getIdentity());
                        Assert.assertNotNull(methodName + " CurrentUser Secret Check : ", currentUser.getSecret());
                        String validateSettings = TestUtils.validateSettings(SettingsRepo.getSettings());
                        AssertHelper.assertTrue(methodName + "Validate Settings : " + validateSettings, validateSettings.equalsIgnoreCase(""));
                        final TextMessage textMessage = new TextMessage(CometChatTestConstants.LOGIN_UID,
                                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
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
                    }

                    @Override
                    public void onError(CometChatException e) {
                        AssertHelper.fail(methodName + " " + e.getMessage());
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(5 * TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a5_sendMessageFromBlockedUserToGroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(CometChatTestConstants.RECEIVER_GUID,
                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
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
                TestPreferenceHelper.saveLastMessageIdFromBlockedUserInGroup(receivedTextMessage.getId());
                List<String> uids = new ArrayList<>();
                uids.add(CometChatTestConstants.LOGIN_UID);
                CometChat.blockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
                    @Override
                    public void onSuccess(HashMap<String, String> stringStringHashMap) {
                        Assert.assertNotNull(methodName + " Map null check : ", stringStringHashMap);
                        AssertHelper.assertTrue(methodName + " map count check : ", stringStringHashMap.size() == 1);
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        AssertHelper.fail(methodName + " " + e.getMessage());
                        countDownLatch.countDown();
                    }
                });
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_BLOCKED_RECEIVER);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_loginWithMainUser() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.LOGIN_UID, CometChatTestConstants.VALID_API_KEY
                , new CometChat.CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        String validateUser = TestUtils.validateUser(user);
                        AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                        String validateLoggedInUser = TestUtils.validateUser(CometChat.getLoggedInUser());
                        AssertHelper.assertTrue(methodName + " Validate Logged In User : " + validateLoggedInUser, validateLoggedInUser.equalsIgnoreCase(""));
                        CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
                        Assert.assertNotNull(methodName + " CurrentUser Null Check : ", currentUser);
                        Assert.assertNotNull(methodName + " CurrentUser AuthToken Check : ", currentUser.getAuthToken());
                        Assert.assertNotNull(methodName + " CurrentUser Identity Check : ", currentUser.getIdentity());
                        Assert.assertNotNull(methodName + " CurrentUser Secret Check : ", currentUser.getSecret());
                        String validateSettings = TestUtils.validateSettings(SettingsRepo.getSettings());
                        AssertHelper.assertTrue(methodName + "Validate Settings : " + validateSettings, validateSettings.equalsIgnoreCase(""));
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        AssertHelper.fail(methodName + " " + e.getMessage());
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(3 * TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a7_runFetchBlockedUsersTest() {
        TestUtils.runTestForClass(FetchBlockedUsersTest.class);
    }



}
