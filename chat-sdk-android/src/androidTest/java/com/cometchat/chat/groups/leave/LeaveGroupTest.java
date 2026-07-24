package com.cometchat.chat.groups.leave;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CurrentUserRepo;
import com.cometchat.chat.core.MessagesRequest;
import com.cometchat.chat.core.SettingsRepo;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestPreferenceHelper;
import com.cometchat.chat.utils.TestUtils;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class LeaveGroupTest {

    private static final int TEST_CASE_TIMEOUT = 5;


    @Test
    public void a1_loginWithSecondaryUser() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.RECEIVER_UID_1, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
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
    public void a2_leaveGroupWithEmptyGUIDShouldReturnError() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.leaveGroup(CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_GUID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    @Test
    public void a3_leaveGroupWithInvalidGUIDShouldReturnError() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.leaveGroup(CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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

    // Leave Public Group

    @Test
    public void a4_leavePublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.leaveGroup(TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.assertTrue(methodName + " leave group check : " , s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_GROUP_LEAVE_SUCCESS));
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
    public void a5_sendMessageInNonJoinedPublicGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPublicGroupGUID(), CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERROR_NOT_A_MEMBER);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }
    @Test
    public void a6_fetchMessagesForNotJoinedPublicGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPublicGroupGUID())
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERROR_GROUP_NOT_JOINED);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    // Leave Password Group

    @Test
    public void a7_leavePasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.leaveGroup(TestPreferenceHelper.getLastPasswordGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.assertTrue(methodName + " leave group check : " , s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_GROUP_LEAVE_SUCCESS));
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
    public void a8_sendMessageInNonJoinedPasswordGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPasswordGroupGUID(), CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERROR_NOT_A_MEMBER);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }
    @Test
    public void a9_fetchMessagesForNotJoinedPasswordGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPasswordGroupGUID())
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERROR_GROUP_NOT_JOINED);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }



    @Test
    public void b1_sendMessageInNonJoinedPrivateGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPrivateGroupGUID(), CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERROR_NOT_A_MEMBER);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }
    @Test
    public void b2_fetchMessagesForNotJoinedPublicGroupShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPrivateGroupGUID())
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERROR_GROUP_NOT_JOINED);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b3_loginWithMainUser() throws InterruptedException{
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

}
