package com.cometchat.chat.groups.delete;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CurrentUserRepo;
import com.cometchat.chat.core.SettingsRepo;
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
public class DeleteGroupTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_deletePublicGroupWithInvalidGUIDShouldReturnError() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteGroup(CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successString) {
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
    public void a2_deletePublicGroupWithEmptyGUIDShouldReturnError() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteGroup(CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successString) {
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
    public void a3_deletePublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteGroup(TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successString) {
                AssertHelper.assertTrue(methodName + " Delete group success check : " , successString.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_GROUP_DELETE_SUCCESS));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " +e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_deletePasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteGroup(TestPreferenceHelper.getLastPasswordGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successString) {
                AssertHelper.assertTrue(methodName + " Delete group success check : " , successString.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_GROUP_DELETE_SUCCESS));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " +e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_deletePrivateGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteGroup(TestPreferenceHelper.getLastPrivateGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successString) {
                AssertHelper.assertTrue(methodName + " Delete group success check : " , successString.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_GROUP_DELETE_SUCCESS));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " +e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_loginWithMainUser() throws InterruptedException{
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
