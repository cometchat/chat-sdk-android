package com.cometchat.chat.groups.update;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CurrentUserRepo;
import com.cometchat.chat.core.SettingsRepo;
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
public class UpdateGroupTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_updateGroupWithEmptyGuidShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group();
        group.setGuid(CometChatTestConstants.EMPTY_DATA);
        group.setName(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME + " Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
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
    public void a2_updateGroupWithInvalidGuidShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group();
        group.setGuid(CometChatTestConstants.INVALID_DATA);
        group.setName(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME + " Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
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
    public void a3_updatePublicGroupWithValidGuidShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPublicGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME + " Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void a4_updatePasswordGroupWithValidGuidShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPasswordGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME + " Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void a5_updatePrivateGroupWithValidGuidShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPrivateGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME + " Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void a6_updateGroupIconAndDescription() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPasswordGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME + " Updated");
        group.setIcon(CometChatTestConstants.DEFAULT_AVATAR_URL);
        group.setDescription(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", group.getIcon().equalsIgnoreCase(createdGroup.getIcon()));
                AssertHelper.assertTrue(methodName + " Group Description Check :", group.getDescription().equalsIgnoreCase(createdGroup.getDescription()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void a7_updateGroupToClearIconAndDescription() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPasswordGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME + " Updated");
        group.setIcon(CometChatTestConstants.EMPTY_DATA);
        group.setDescription(CometChatTestConstants.EMPTY_DATA);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", createdGroup.getIcon() == null);
                AssertHelper.assertTrue(methodName + " Group Description Check :", createdGroup.getDescription() == null);
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void a8_updateGroupToUpdateMetadata() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPasswordGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME + " Updated");
        group.setIcon(CometChatTestConstants.EMPTY_DATA);
        group.setDescription(CometChatTestConstants.EMPTY_DATA);
        group.setMetadata(TestUtils.getTestEditedCustomData());
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", createdGroup.getIcon() == null);
                AssertHelper.assertTrue(methodName + " Group Description Check :", createdGroup.getDescription() == null);
                AssertHelper.jsonAssert(methodName + " Group Metadata Check :", group.getMetadata(), createdGroup.getMetadata());
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void a9_updateGroupToClearMetadata() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPasswordGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME + " Updated");
        group.setIcon(CometChatTestConstants.EMPTY_DATA);
        group.setDescription(CometChatTestConstants.EMPTY_DATA);
        group.setMetadata(TestUtils.getEmptyJSONObject());
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", createdGroup.getIcon() == null);
                AssertHelper.assertTrue(methodName + " Group Description Check :", createdGroup.getDescription() == null);
                AssertHelper.assertTrue(methodName + " Group Metadata Check :", createdGroup.getMetadata() == null);
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void b1_updateGroupTypeFromPublicToPrivate() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPublicGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME + " Updated");
        group.setIcon(CometChatTestConstants.EMPTY_DATA);
        group.setDescription(CometChatTestConstants.EMPTY_DATA);
        group.setMetadata(TestUtils.getEmptyJSONObject());
        group.setGroupType(CometChatConstants.GROUP_TYPE_PRIVATE);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", createdGroup.getIcon() == null);
                AssertHelper.assertTrue(methodName + " Group Description Check :", createdGroup.getDescription() == null);
                AssertHelper.assertTrue(methodName + " Group Metadata Check :", createdGroup.getMetadata() == null);
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void b2_updateGroupTypeFromPrivateToPublic() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPublicGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME + " Updated");
        group.setIcon(CometChatTestConstants.EMPTY_DATA);
        group.setDescription(CometChatTestConstants.EMPTY_DATA);
        group.setMetadata(TestUtils.getEmptyJSONObject());
        group.setGroupType(CometChatConstants.GROUP_TYPE_PUBLIC);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", createdGroup.getIcon() == null);
                AssertHelper.assertTrue(methodName + " Group Description Check :", createdGroup.getDescription() == null);
                AssertHelper.assertTrue(methodName + " Group Metadata Check :", createdGroup.getMetadata() == null);
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void b3_updateGroupTypeFromPublicToPassword() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPublicGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME + " Updated");
        group.setIcon(CometChatTestConstants.EMPTY_DATA);
        group.setDescription(CometChatTestConstants.EMPTY_DATA);
        group.setMetadata(TestUtils.getEmptyJSONObject());
        group.setGroupType(CometChatConstants.GROUP_TYPE_PASSWORD);
        group.setPassword(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_PASSWORD);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", createdGroup.getIcon() == null);
                AssertHelper.assertTrue(methodName + " Group Description Check :", createdGroup.getDescription() == null);
                AssertHelper.assertTrue(methodName + " Group Metadata Check :", createdGroup.getMetadata() == null);
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void b4_updateGroupTypeFromPasswordToPublic() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPublicGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME + " Updated");
        group.setIcon(CometChatTestConstants.EMPTY_DATA);
        group.setDescription(CometChatTestConstants.EMPTY_DATA);
        group.setMetadata(TestUtils.getEmptyJSONObject());
        group.setGroupType(CometChatConstants.GROUP_TYPE_PUBLIC);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", createdGroup.getIcon() == null);
                AssertHelper.assertTrue(methodName + " Group Description Check :", createdGroup.getDescription() == null);
                AssertHelper.assertTrue(methodName + " Group Metadata Check :", createdGroup.getMetadata() == null);
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void b5_updateGroupPassword() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPasswordGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME + " Updated");
        group.setIcon(CometChatTestConstants.EMPTY_DATA);
        group.setDescription(CometChatTestConstants.EMPTY_DATA);
        group.setMetadata(TestUtils.getEmptyJSONObject());
        group.setPassword(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_PASSWORD + "Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", createdGroup.getIcon() == null);
                AssertHelper.assertTrue(methodName + " Group Description Check :", createdGroup.getDescription() == null);
                AssertHelper.assertTrue(methodName + " Group Metadata Check :", createdGroup.getMetadata() == null);
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
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
    public void b6_loginWithSecondaryUser() throws InterruptedException {
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
    public void b7_updatePublicGroupByParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPublicGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME + " Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b8_loginWithMainUser() throws InterruptedException{
        Thread.sleep(5000);
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
    public void b9_updatePublicGroupWithTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_GUID, "" + "withtags"));
        List<String> tags = new ArrayList<>();
        tags.add("updated");
        group.setTags(tags);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group updatedGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", updatedGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(updatedGroup.getGuid()));
                String validateGroup = TestUtils.validateGroup(updatedGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
                boolean validateTags = false;
                if (updatedGroup.getTags() != null && updatedGroup.getTags().contains("updated")) {
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Validate Tags : ", validateTags);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c1_updatePasswordGroupWithTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_GUID, "" + "withtags"));
        List<String> tags = new ArrayList<>();
        tags.add("updated");
        group.setTags(tags);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group updatedGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", updatedGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(updatedGroup.getGuid()));
                String validateGroup = TestUtils.validateGroup(updatedGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
                boolean validateTags = false;
                if (updatedGroup.getTags() != null && updatedGroup.getTags().contains("updated")) {
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Validate Tags : ", validateTags);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c2_updatePrivateGroupWithTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_GUID, "" + "withtags"));
        List<String> tags = new ArrayList<>();
        tags.add("updated");
        group.setTags(tags);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group updatedGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", updatedGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(updatedGroup.getGuid()));
                String validateGroup = TestUtils.validateGroup(updatedGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
                boolean validateTags = false;
                if (updatedGroup.getTags() != null && updatedGroup.getTags().contains("updated")) {
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Validate Tags : ", validateTags);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c3_updatePublicGroupWithBlankTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_GUID, "" + "withtags"));
        List<String> tags = new ArrayList<>();
        group.setTags(tags);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group updatedGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", updatedGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(updatedGroup.getGuid()));
                String validateGroup = TestUtils.validateGroup(updatedGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
                boolean validateTags = false;
                if (updatedGroup.getTags() == null) {
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Validate Tags : ", validateTags);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c4_updatePasswordGroupWithBlankTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_GUID, "" + "withtags"));
        List<String> tags = new ArrayList<>();
        group.setTags(tags);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group updatedGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", updatedGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(updatedGroup.getGuid()));
                String validateGroup = TestUtils.validateGroup(updatedGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
                boolean validateTags = false;
                if (updatedGroup.getTags() == null) {
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Validate Tags : ", validateTags);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c5_updatePrivateGroupWithBlankTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_GUID, "" + "withtags"));
        List<String> tags = new ArrayList<>();
        group.setTags(tags);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group updatedGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", updatedGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(updatedGroup.getGuid()));
                String validateGroup = TestUtils.validateGroup(updatedGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
                boolean validateTags = false;
                if (updatedGroup.getTags() == null) {
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Validate Tags : ", validateTags);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

}
