package com.cometchat.chat.groups.changescope;


import android.text.TextUtils;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.GroupMember;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminScopeChangeTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a01_loginWithMainUser() throws InterruptedException{
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
    public void a02_addMembersToPublicGroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<GroupMember> members = new ArrayList<>();
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_1, CometChatConstants.SCOPE_ADMIN));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatConstants.SCOPE_MODERATOR));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_3, CometChatConstants.SCOPE_MODERATOR));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_4, CometChatConstants.SCOPE_PARTICIPANT));
        members.add(new GroupMember(TestPreferenceHelper.getLastCreatedUserUId(), CometChatConstants.SCOPE_PARTICIPANT));
        CometChat.addMembersToGroup(TestPreferenceHelper.getLastPublicGroupGUID(), members, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Assert.assertNotNull(methodName + " result hashmap not null check ", stringStringHashMap);
                AssertHelper.assertTrue(methodName + " result hashmap size check : " , stringStringHashMap.size() == 5);
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
    public void a03_loginWithAdminUser() throws InterruptedException{
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
    public void a1_changeScopeWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.EMPTY_DATA, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
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
    public void a2_changeScopeWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
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
    public void a3_changeScopeWithInvalidUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.INVALID_DATA, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
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
    public void a4_changeScopeWithInvalidGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.INVALID_DATA,
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
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
    public void a5_changeScopeWithEmptyScopeShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_SCOPE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_changeScopeWithInvalidScopeShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_SCOPE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

//    @Test
//    public void a7_changeScopeOfModertorToParticipantShouldReturnSuccess() throws InterruptedException {
//        final String methodName = TestUtils.getMethodName();
//        final CountDownLatch countDownLatch = new CountDownLatch(1);
//        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_4, TestPreferenceHelper.getLastPublicGroupGUID(),
//                CometChatConstants.SCOPE_PARTICIPANT, new CometChat.CallbackListener<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        Assert.assertNotNull(methodName + " Success string null check : " , s);
//                        AssertHelper.assertTrue(methodName + " Success string empty check : " , !TextUtils.isEmpty(s));
//                        AssertHelper.assertTrue(methodName + " Success string check : " , s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_MEMBER_SCOPE_CHANGED_SUCCESS));
//                        countDownLatch.countDown();
//                    }
//
//                    @Override
//                    public void onError(CometChatException e) {
//                        AssertHelper.fail(methodName + " " + e.getMessage());
//                        countDownLatch.countDown();
//                    }
//                });
//        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
//        if (countDownLatch.getCount() == 1)
//            AssertHelper.fail(methodName + ": Timeout");
//    }

    @Test
    public void a8_changeScopeOfParticipantToModeratorShouldReturnSuccess() throws InterruptedException {
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

    @Test
    public void a9_changeScopeOfParticipantToAdminShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_4, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_ADMIN, new CometChat.CallbackListener<String>() {
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

    @Test
    public void b1_changeScopeOfAdminToModeratorShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_4, TestPreferenceHelper.getLastPublicGroupGUID(),
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
    public void b2_changeScopeOfAdminToPartShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_4, TestPreferenceHelper.getLastPublicGroupGUID(),
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
    public void b3_changeScopeOfParticipantToAdminShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_3, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_ADMIN, new CometChat.CallbackListener<String>() {
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

    @Test
    public void b4_changeScopeOfAdminToParticipantShouldReturnSuccess() throws InterruptedException {
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
    public void b5_changeScopeOfParticipantToModeratorShouldReturnSuccess() throws InterruptedException {
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

    @Test
    public void b6_changeScopeOfModeratorToParticipantShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_PARTICIPANT, new CometChat.CallbackListener<String>() {
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

    // Kick User by admin test

    @Test
    public void b7_kickGroupMemberByAdminWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.EMPTY_DATA, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
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
    public void b8_kickGroupMemberByAdminWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<String>() {
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
    public void b9_kickGroupMemberByAdminWithInvalidUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.INVALID_UID, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
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
    public void c1_kickGroupMemberByAdminWithInvalidGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<String>() {
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
    public void c2_kickAdminByAdminShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.LOGIN_UID, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
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

}
