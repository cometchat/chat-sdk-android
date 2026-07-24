package com.cometchat.chat.users.management;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestPreferenceHelper;
import com.cometchat.chat.utils.TestUtils;

import org.json.JSONException;
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
public class UpdateUserTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_updateUserWithEmptyApiKeyShouldReturnError() throws InterruptedException {

        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User user = new User();
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        CometChat.updateUser(user, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_API_KEY_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_updateUserWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User user = new User();
        user.setUid(CometChatTestConstants.EMPTY_DATA);
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, "Edited"));
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
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
    public void a3_updateUserWithEmptyAvatarShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, "Edited"));
        user.setAvatar(CometChatTestConstants.EMPTY_DATA);
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate User Name : ", user.getName().equalsIgnoreCase(updatedUser.getName()));
                AssertHelper.assertTrue(methodName + " Validate Avatar : ", updatedUser.getAvatar() == null);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_updateUserWithValidAvatarShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, "Edited"));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate User Name : ", user.getName().equalsIgnoreCase(updatedUser.getName()));
                AssertHelper.assertTrue(methodName + " Validate Avatar : ", updatedUser.getAvatar().equalsIgnoreCase(user.getAvatar()));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a5_updateUserWithEmptyMetadataShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, "Edited"));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getEmptyJSONObject());
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate User Name : ", user.getName().equalsIgnoreCase(updatedUser.getName()));
                AssertHelper.assertTrue(methodName + " Validate Avatar : ", updatedUser.getAvatar().equalsIgnoreCase(user.getAvatar()));
                AssertHelper.assertTrue(methodName + " Validate Metadata : ", updatedUser.getMetadata() == null);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_updateUserWithValidMetadataShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, "Edited"));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestEditedCustomData());
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate User Name : ", user.getName().equalsIgnoreCase(updatedUser.getName()));
                AssertHelper.assertTrue(methodName + " Validate Avatar : ", updatedUser.getAvatar().equalsIgnoreCase(user.getAvatar()));
                AssertHelper.jsonAssert(methodName + "Validate Metadata : " , user.getMetadata(), updatedUser.getMetadata());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a7_updateUserWithEmptyLinkShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, "Edited"));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestEditedCustomData());
        user.setLink(CometChatTestConstants.EMPTY_DATA);
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate User Name : ", user.getName().equalsIgnoreCase(updatedUser.getName()));
                AssertHelper.assertTrue(methodName + " Validate Avatar : ", updatedUser.getAvatar().equalsIgnoreCase(user.getAvatar()));
                AssertHelper.jsonAssert(methodName + " Validate Metadata : ", updatedUser.getMetadata(), user.getMetadata());
                AssertHelper.assertTrue(methodName + " Validate Link : ", updatedUser.getLink() == null);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a8_updateUserWithValaidLinkShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, "Edited"));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestEditedCustomData());
        user.setLink("https://www.cometchat.com");
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate User Name : ", user.getName().equalsIgnoreCase(updatedUser.getName()));
                AssertHelper.assertTrue(methodName + " Validate Avatar : ", updatedUser.getAvatar().equalsIgnoreCase(user.getAvatar()));
                AssertHelper.jsonAssert(methodName + " Validate Metadata : ", updatedUser.getMetadata(), user.getMetadata());
                AssertHelper.assertTrue(methodName + " Validate Link : ", updatedUser.getLink().equalsIgnoreCase(user.getLink()));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    @Test
    public void a9_updateUserWithEmptyStatusMessageShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, "Edited"));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestEditedCustomData());
        user.setLink("https://www.cometchat.com");
        user.setStatusMessage(CometChatTestConstants.EMPTY_DATA);
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate User Name : ", user.getName().equalsIgnoreCase(updatedUser.getName()));
                AssertHelper.assertTrue(methodName + " Validate Avatar : ", updatedUser.getAvatar().equalsIgnoreCase(user.getAvatar()));
                AssertHelper.jsonAssert(methodName + " Validate Metadata : ", updatedUser.getMetadata(), user.getMetadata());
                AssertHelper.assertTrue(methodName + " Validate Link : ", updatedUser.getLink().equalsIgnoreCase(user.getLink()));
                AssertHelper.assertTrue(methodName + " Validate Status Message : ", updatedUser.getStatusMessage() == null);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    @Test
    public void b1_updateUserWithValidStatusMessageShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, "Edited"));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestEditedCustomData());
        user.setLink("https://www.cometchat.com");
        user.setStatusMessage("Happy after edited");
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate User Name : ", user.getName().equalsIgnoreCase(updatedUser.getName()));
                AssertHelper.assertTrue(methodName + " Validate Avatar : ", updatedUser.getAvatar().equalsIgnoreCase(user.getAvatar()));
                AssertHelper.jsonAssert(methodName + " Validate Metadata : ", updatedUser.getMetadata(), user.getMetadata());
                AssertHelper.assertTrue(methodName + " Validate Link : ", updatedUser.getLink().equalsIgnoreCase(user.getLink()));
                AssertHelper.assertTrue(methodName + " Validate Status Message : ", updatedUser.getStatusMessage().equalsIgnoreCase(user.getStatusMessage()));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b2_updateUserWithValidTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        List<String> tags = new ArrayList<>();
        tags.add(CometChatTestConstants.USER_TAG_EDITED);
        user.setTags(tags);
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                boolean validateTags = false;
                if(createdUser.getTags()!= null && createdUser.getTags().contains(CometChatTestConstants.USER_TAG_EDITED)){
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Tags check : " , validateTags);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b4_updateUserWithBlankTagsShouldRemoveTagsAndReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(TestPreferenceHelper.getLastCreatedUserUId());
        List<String> tags = new ArrayList<>();
        user.setTags(tags);
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                boolean validateTags = false;
                if(createdUser.getTags() == null){
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Tags check : " , validateTags);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b5_updateUserWithValidTagForConversationsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(CometChatTestConstants.RECEIVER_UID_1);
        List<String> tags = new ArrayList<>();
        tags.add("conversation_tag");
        user.setTags(tags);
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                boolean validateTags = false;
                if(createdUser.getTags()!= null && createdUser.getTags().contains("conversation_tag")){
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Tags check : " , validateTags);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }
}
