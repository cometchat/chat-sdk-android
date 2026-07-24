package com.cometchat.chat.users.fetch;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
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
public class GetUserTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_getUserWithInvalidUIDShouldReturnError() throws InterruptedException{

        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getUser(CometChatTestConstants.INVALID_UID, new CometChat.CallbackListener<User>() {
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
    public void a2_getUserWithEmptyUIDShouldReturnError() throws InterruptedException{

        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getUser(CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<User>() {
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
    public void a3_getUserWithValidDetailsShouldReturnSuccess() throws InterruptedException{

        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getUser(CometChatTestConstants.RECEIVER_UID_1, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User receivedUser) {
                Assert.assertNotNull(methodName + " User null check : ", receivedUser);
                String validateUser = TestUtils.validateUser(receivedUser);
                AssertHelper.assertTrue(methodName + " User validation : " + validateUser, validateUser.equalsIgnoreCase(""));
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
    public void a4_getLoggedInUserShouldReturnSuccess() throws InterruptedException{

        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User loggedInUser = CometChat.getLoggedInUser();
        Assert.assertNotNull(methodName + " User null check : ", loggedInUser);
        AssertHelper.assertTrue(methodName + " Logged In user UID check : " ,loggedInUser.getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID));
        String validateUser = TestUtils.validateUser(loggedInUser);
        AssertHelper.assertTrue(methodName + " Logged In user validation : " + validateUser, validateUser.equalsIgnoreCase(""));
        countDownLatch.countDown();
    }
}
