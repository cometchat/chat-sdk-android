package com.cometchat.chat.users.management;


import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CometChat;

import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestPreferenceHelper;
import com.cometchat.chat.utils.TestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateUserTest {

    private static final int TEST_CASE_TIMEOUT = 8;

    @Test
    public void a0_createRoles() throws JSONException {
        Logger.error("Creating Roles");
        String url = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if(CometChatTestConstants.isStaging){
            url = CometChatTestConstants.DEFAULT_STAGING_URL + "roles";
        }else {
            url = CometChatTestConstants.DEFAULT_PROD_URL + "roles";
        }
        for (int i = 0; i < 2; i++) {
            Headers headers = new Headers.Builder().add("appId", CometChatTestConstants.APP_ID).add("apiKey", CometChatTestConstants.VALID_API_KEY).build();
            JSONObject rolesObject = new JSONObject();
            Random rand = new Random();
            int random = rand.nextInt(100000);
            rolesObject.put("role", "Role_" + i + "_" + random);
            rolesObject.put("name", "Role_" + i + "_" + random);
            Logger.error(rolesObject.toString());
            Request createAuthTokenRequest = new Request.Builder().post(RequestBody.create(MediaType.parse("application/json"), rolesObject.toString())).headers(headers).url(String.format(url, CometChatTestConstants.REGION, CometChatTestConstants.LOGIN_UID)).build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = null;
            try {
                response = okHttpClient.newCall(createAuthTokenRequest).execute();
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.has("data")) {
                        String role = jsonObject.getJSONObject("data").getString("role");
                        if (i == 0) {
                            TestPreferenceHelper.saveRole1(role);
                        } else {
                            TestPreferenceHelper.saveRole2(role);
                            countDownLatch.countDown();
                        }
                    } else if (jsonObject.has("error")) {
                        Logger.error(jsonObject.getJSONObject("error").toString());
                        AssertHelper.fail(jsonObject.getJSONObject("error").toString());
                        countDownLatch.countDown();
                    }
                }
                countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void a01_addFriends() throws JSONException {
        Logger.error("Adding friends");
        String url = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if (CometChatTestConstants.isStaging)
            //url = "CometChatTestConstants.DEFAULT_STAGING_URLusers/%s/friends";
            url = "https://" + CometChatTestConstants.APP_ID + ".api-%s.cometchat-staging.com/v3.0/users/%s/friends";
        else
            url = "https://" + CometChatTestConstants.APP_ID + ".api-%s.cometchat.io/v3.0/users/%s/friends";
        JSONObject dataObject = new JSONObject();
        JSONArray friendsArray = new JSONArray();
        friendsArray.put("superhero2");
        friendsArray.put("superhero3");
        friendsArray.put("superhero4");
        friendsArray.put("superhero5");
        dataObject.put("accepted",friendsArray);
        Headers headers = new Headers.Builder().add("appId", CometChatTestConstants.APP_ID).add("apiKey", CometChatTestConstants.VALID_API_KEY).build();
        Request addFriendsRequest = new Request.Builder().post(RequestBody.create(MediaType.parse("application/json"), dataObject.toString())).headers(headers).url(String.format(url, CometChatTestConstants.REGION, CometChatTestConstants.LOGIN_UID)).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        try {
            response = okHttpClient.newCall(addFriendsRequest).execute();
            if (response != null) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                if (jsonObject.has("data")) {
                    countDownLatch.countDown();

                } else if (jsonObject.has("error")) {
                    Logger.error(jsonObject.getJSONObject("error").toString());
                    AssertHelper.fail(jsonObject.getJSONObject("error").toString());
                    countDownLatch.countDown();
                }
            }
            countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void a1_createUserWithEmptyApiKeyShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        CometChat.createUser(user, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<User>() {
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
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_createUserWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User user = new User();
        user.setUid(CometChatTestConstants.EMPTY_DATA);
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
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
    public void a3_createUserWithEmptyNameShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(CometChatTestConstants.EMPTY_DATA);
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_USER_NAME);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_createUser() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a5_createUserWithValidAvatarShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Avatar Check : ", user.getAvatar().equalsIgnoreCase(createdUser.getAvatar()));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_createUserWithValidMetadataShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestMetadata());
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Avatar Check : ", user.getAvatar().equalsIgnoreCase(createdUser.getAvatar()));
                AssertHelper.assertTrue(methodName + " Metadata Check : ", user.getMetadata().toString().equalsIgnoreCase(createdUser.getMetadata().toString()));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a7_createUserWithValidLinkShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestMetadata());
        user.setLink("https://www.cometchat.com");
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Avatar Check : ", user.getAvatar().equalsIgnoreCase(createdUser.getAvatar()));
                AssertHelper.assertTrue(methodName + " Metadata Check : ", user.getMetadata().toString().equalsIgnoreCase(createdUser.getMetadata().toString()));
                AssertHelper.assertTrue(methodName + " Link Check : ", user.getLink().equalsIgnoreCase(createdUser.getLink()));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a8_createUserWithValidStatusMessageShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestMetadata());
        user.setLink("https://www.cometchat.com");
        user.setStatusMessage("Happy :)");
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Avatar Check : ", user.getAvatar().equalsIgnoreCase(createdUser.getAvatar()));
                AssertHelper.assertTrue(methodName + " Metadata Check : ", user.getMetadata().toString().equalsIgnoreCase(createdUser.getMetadata().toString()));
                AssertHelper.assertTrue(methodName + " Link Check : ", user.getLink().equalsIgnoreCase(createdUser.getLink()));
                AssertHelper.assertTrue(methodName + " Status Message Check : ", user.getStatusMessage().equalsIgnoreCase(createdUser.getStatusMessage()));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a90_createUserWithValidRole1ShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestMetadata());
        user.setLink("https://www.cometchat.com");
        user.setStatusMessage("Happy :)");
        user.setRole(TestPreferenceHelper.getRole1());
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Avatar Check : ", user.getAvatar().equalsIgnoreCase(createdUser.getAvatar()));
                AssertHelper.assertTrue(methodName + " Metadata Check : ", user.getMetadata().toString().equalsIgnoreCase(createdUser.getMetadata().toString()));
                AssertHelper.assertTrue(methodName + " Link Check : ", user.getLink().equalsIgnoreCase(createdUser.getLink()));
                AssertHelper.assertTrue(methodName + " Status Message Check : ", user.getStatusMessage().equalsIgnoreCase(createdUser.getStatusMessage()));
                AssertHelper.assertTrue(methodName + " Role Check : ", user.getRole().equalsIgnoreCase(createdUser.getRole()));
                TestPreferenceHelper.saveLastCreatedUserUID(createdUser.getUid());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a91_createUserWithValidRole2ShouldReturnSuccess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestMetadata());
        user.setLink("https://www.cometchat.com");
        user.setStatusMessage("Happy :)");
        user.setRole(TestPreferenceHelper.getRole2());
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Avatar Check : ", user.getAvatar().equalsIgnoreCase(createdUser.getAvatar()));
                AssertHelper.assertTrue(methodName + " Metadata Check : ", user.getMetadata().toString().equalsIgnoreCase(createdUser.getMetadata().toString()));
                AssertHelper.assertTrue(methodName + " Link Check : ", user.getLink().equalsIgnoreCase(createdUser.getLink()));
                AssertHelper.assertTrue(methodName + " Status Message Check : ", user.getStatusMessage().equalsIgnoreCase(createdUser.getStatusMessage()));
                AssertHelper.assertTrue(methodName + " Role Check : ", user.getRole().equalsIgnoreCase(createdUser.getRole()));
                TestPreferenceHelper.saveLastCreatedUserUID(createdUser.getUid());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b1_createUserWithInvalidRoleShouldReturnError() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(String.format(CometChatTestConstants.CREATE_USER_UID_PREFIX, System.currentTimeMillis()));
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestMetadata());
        user.setLink("https://www.cometchat.com");
        user.setStatusMessage("Happy :)");
        user.setRole("abc");
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.ERROR_ERR_BAD_REQUEST);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b2_createUserWithAlreadyTakenUIDShouldReturnError() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid("superhero1");
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, System.currentTimeMillis()));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setMetadata(TestUtils.getTestMetadata());
        user.setLink("https://www.cometchat.com");
        user.setStatusMessage("Happy :)");
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase("ERR_UID_ALREADY_EXISTS");
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b3_createUserWithTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(CometChatTestConstants.CREATE_USER_UID_WITH_TAG);
        user.setName(String.format(CometChatTestConstants.CREATE_USER_NAME_PREFIX, CometChatTestConstants.CREATE_USER_UID_WITH_TAG));
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setLink("https://www.cometchat.com");
        user.setStatusMessage("Happy :)");
        List<String> tags = new ArrayList<>();
        tags.add(CometChatTestConstants.USER_TAG_1);
        tags.add(CometChatTestConstants.USER_TAG_2);
        user.setTags(tags);
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Avatar Check : ", user.getAvatar().equalsIgnoreCase(createdUser.getAvatar()));
                AssertHelper.assertTrue(methodName + " Link Check : ", user.getLink().equalsIgnoreCase(createdUser.getLink()));
                AssertHelper.assertTrue(methodName + " Status Message Check : ", user.getStatusMessage().equalsIgnoreCase(createdUser.getStatusMessage()));
                boolean validateTags = false;
                if(createdUser.getTags().contains(CometChatTestConstants.USER_TAG_1) &&
                                        createdUser.getTags().contains(CometChatTestConstants.USER_TAG_2)){
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Tags check : " , validateTags);
                TestPreferenceHelper.saveLastCreatedUserUID(createdUser.getUid());
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b4_createUserWithFinalTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid("abc_" + System.currentTimeMillis() +"_withtags");
        user.setName("abc "+ System.currentTimeMillis() +" withtags");
        user.setAvatar(CometChatTestConstants.DEFAULT_AVATAR_URL);
        user.setLink("https://www.cometchat.com");
        user.setStatusMessage("Happy :)");
        List<String> tags = new ArrayList<>();
        tags.add(CometChatTestConstants.USER_TAG_FINAL);
        user.setTags(tags);
        CometChat.createUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User createdUser) {
                String validateUser = TestUtils.validateUser(createdUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Avatar Check : ", user.getAvatar().equalsIgnoreCase(createdUser.getAvatar()));
                AssertHelper.assertTrue(methodName + " Link Check : ", user.getLink().equalsIgnoreCase(createdUser.getLink()));
                AssertHelper.assertTrue(methodName + " Status Message Check : ", user.getStatusMessage().equalsIgnoreCase(createdUser.getStatusMessage()));
                boolean validateTags = false;
                if(createdUser.getTags().contains(CometChatTestConstants.USER_TAG_FINAL)){
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
        countDownLatch.await();
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

}
