package com.cometchat.chat.groups.changescope;

import android.text.TextUtils;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.groups.banunban.BannedMemberTest;
import com.cometchat.chat.groups.kickmember.KickedMemberTest;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CurrentUserRepo;
import com.cometchat.chat.core.SettingsRepo;
import com.cometchat.chat.users.blockunblock.UnblockUserTest;
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
public class ModeratorScopeChangeTest {

    private final int TEST_CASE_TIMEOUT = 5;

    // Tests with Moderator RECEIVER_UID_2

    @Test
    public void a0_loginWithModerator() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.VALID_API_KEY
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
    public void a1_changeScopeFromParticipantToAdminShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(TestPreferenceHelper.getLastCreatedUserUId(), TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_ADMIN, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_SCOPE_CLEARANCE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_changeScopeFromModeratorToAdminShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_3, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_ADMIN, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_SCOPE_CLEARANCE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_changeScopeFromAdminToModeratorShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_1, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_SCOPE_CLEARANCE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_changeScopeFromAdminToParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_1, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_PARTICIPANT, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_SCOPE_CLEARANCE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a5_changeScopeFromModeratorToParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_3, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_PARTICIPANT, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_SCOPE_CLEARANCE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_changeScopeFromParticipantToModeratorShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_4, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Assert.assertNotNull(methodName + " Success string null check : " , s);
                        AssertHelper.assertTrue(methodName + " Success string empty check : " , !TextUtils.isEmpty(s));
                        AssertHelper.assertTrue(methodName + " Success string check : " , s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_MEMBER_SCOPE_CHANGED_SUCCESS));
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


    // kick members by moderator

    @Test
    public void a8_kickAdminMyModeratorShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.RECEIVER_UID_1, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a9_kickModeratorByModeratorShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.RECEIVER_UID_1, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b1_kickParticipantByModerator() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(TestPreferenceHelper.getLastCreatedUserUId(), TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Assert.assertNotNull(methodName + " Success string null check : " , s);
                AssertHelper.assertTrue(methodName + " Success string empty check : " , !TextUtils.isEmpty(s));
                AssertHelper.assertTrue(methodName + " Success string check : " , s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_MEMBER_KICKED_SUCCESS));
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
    public void b2_runKickedMemberTests(){
        TestUtils.runTestForClass(KickedMemberTest.class);
    }

    @Test
    public void b3_loginWithModerator() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.VALID_API_KEY
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




    // Ban members by moderator tests

    @Test
    public void b4_banGroupMemberByModeratorWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.EMPTY_DATA, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_UID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b5_banGroupMemberByModeratorWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<String>() {
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
    public void b6_banGroupMemberByModeratorWithInvalidUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.INVALID_UID, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void b7_banGroupMemberByModeratorWithInvalidGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<String>() {
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

    @Test
    public void b8_banAdminByModeratorShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.RECEIVER_UID_1, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b9_banModeratorByModeratorShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.RECEIVER_UID_3, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c1_banParticipantByModeratorShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(TestPreferenceHelper.getLastCreatedUserUId(), TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Assert.assertNotNull(methodName + " Success string null check : " , s);
                AssertHelper.assertTrue(methodName + " Success string empty check : " , !TextUtils.isEmpty(s));
                AssertHelper.assertTrue(methodName + " Success string check : " , s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_MEMBER_BANNED_SUCCESS));
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

    // Run banned menmber test

    @Test
    public void c3_runBannedMemberTest(){
        TestUtils.runTestForClass(BannedMemberTest.class);
    }

    // Unban Member Test

    @Test
    public void c4_loginWithModerator() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.VALID_API_KEY
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
    public void c5_unbanGroupMemberByModeratorWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.unbanGroupMember(CometChatTestConstants.EMPTY_DATA, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_UID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c3_unbanGroupMemberByModeratorWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.unbanGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<String>() {
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
    public void c4_unbanGroupMemberByModeratorWithInvalidUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.INVALID_UID, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void c5_unbanGroupMemberByModeratorWithInvalidGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.unbanGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<String>() {
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

    @Test
    public void c6_unbanGroupMemberWithValidDetailsShouldReturnSuccess() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.unbanGroupMember(TestPreferenceHelper.getLastCreatedUserUId(), TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Assert.assertNotNull(methodName + " Success string null check : " , s);
                AssertHelper.assertTrue(methodName + " Success string empty check : " , !TextUtils.isEmpty(s));
                AssertHelper.assertTrue(methodName + " Success string check : " , s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_MEMBER_UNBANNED_SUCCESS));
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
    public void c7_runUnbannedGroupMemberTest (){
        TestUtils.runTestForClass(UnblockUserTest.class);
    }


}
