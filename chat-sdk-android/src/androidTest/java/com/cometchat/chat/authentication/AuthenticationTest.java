package com.cometchat.chat.authentication;

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
import com.cometchat.chat.utils.TestUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthenticationTest {

    private static final int TEST_CASE_TIMEOUT = 20;
    private static String VALID_AUTH_TOKEN;

    @Test
    public void a0_generateAuthToken() {
        String url = null;
        if (CometChatTestConstants.isStaging)
            url = CometChatTestConstants.DEFAULT_STAGING_URL + "users/%s/auth_tokens";
        else
            url = CometChatTestConstants.DEFAULT_PROD_URL + "users/%s/auth_tokens";
        Headers headers = new Headers.Builder().add("appId", CometChatTestConstants.APP_ID).add("apiKey", CometChatTestConstants.VALID_API_KEY).build();
        Request createAuthTokenRequest = new Request.Builder().post(new FormBody.Builder().build()).headers(headers).url(String.format(url, CometChatTestConstants.REGION, CometChatTestConstants.LOGIN_UID)).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        try {
            response = okHttpClient.newCall(createAuthTokenRequest).execute();

            if (response != null) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                if (jsonObject.has("data"))
                    VALID_AUTH_TOKEN = jsonObject.getJSONObject("data").getString("authToken");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void a1_loginWithNullUIDAndNullApiKeyMustReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login((String) CometChatTestConstants.NULL_DATA, (String) CometChatTestConstants.NULL_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_UID) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_API_KEY_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_loginWithEmptyUIDAndEmptyApiKeyMustReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.EMPTY_DATA, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_UID) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_API_KEY_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_loginWithEmptyUIDAndValidApiKeyShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.EMPTY_DATA, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
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
    public void a4_loginWithInvalidUIDAndValidApiKeyShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.INVALID_UID, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
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
    public void a5_loginWithValidUIDAndInvalidApiKeyShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.LOGIN_UID, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + "  must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_API_KEY_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_loginWithInvalidUIDAndInvalidAPIKeyShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.INVALID_UID, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + "  must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_API_KEY_NOT_FOUND) ||
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
    public void a7_loginWithEmptyUIDAndInvalidApiKeyShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.EMPTY_DATA, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + "  must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_UID) ||
                        e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_API_KEY_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a8_loginWithInvalidUIDAndEmptyApiKeyShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.INVALID_UID, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + "  must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_API_KEY_NOT_FOUND) ||
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
    public void a9_loginWithValidUIDAndValidApiKeyShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.LOGIN_UID, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.assertTrue(methodName, user.getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID));
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
                List<String> uids = new ArrayList<>();
                uids.add(CometChatTestConstants.RECEIVER_UID_1);
                uids.add(CometChatTestConstants.RECEIVER_UID_2);
                CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
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
    public void b0_updateLoggedInUserShouldUpdateLocalDetails() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(CometChatTestConstants.LOGIN_UID);
        user.setName(CometChat.getLoggedInUser().getName() + "updated");
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate local user data : ", user.getName().equalsIgnoreCase(CometChat.getLoggedInUser().getName()));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b01_updateLoggedInUserShouldUpdateLocalDetails() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User user = new User();
        user.setUid(CometChatTestConstants.LOGIN_UID);
        user.setName("Iron Man");
        CometChat.updateUser(user, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Validate local user data : ", user.getName().equalsIgnoreCase(CometChat.getLoggedInUser().getName()));
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    @Test
    public void b1_loginWithNullAuthTokenMustReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login((String) CometChatTestConstants.NULL_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block ");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_BLANK_AUTHTOKEN);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b2_loginWithEmptyAuthTokenMustReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block ");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_BLANK_AUTHTOKEN);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b3_loginWithInvalidAuthTokenMustReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block ");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_AUTH_TOKEN_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b4_loginWithValidAuthTokenMustReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(VALID_AUTH_TOKEN, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.assertTrue(methodName, user.getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID));
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
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b5_loginWithSameUIDWhenAlreadyLoggedInShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.LOGIN_UID, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.assertTrue(methodName, user.getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID));
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
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b6_loginWithDifferentUIDWhenAlreadyLoggedInShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.RECEIVER_UID_1, CometChatTestConstants.VALID_API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.assertTrue(methodName, user.getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1));
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
                List<String> uids = new ArrayList<>();
                uids.add(CometChatTestConstants.LOGIN_UID);
                CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
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
    public void b7_loginWithSameAuthTokenWhenAlreadyLoggedInShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CurrentUserRepo.getCurrentUser().getAuthToken(), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.assertTrue(methodName, user.getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1));
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
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b8_loginWithDifferentAuthTokenWhenAlreadyLoggedInShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(VALID_AUTH_TOKEN, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.assertTrue(methodName, user.getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID));
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
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }
}
