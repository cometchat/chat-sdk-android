package com.cometchat.chat.users.management;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestUtils;

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
public class UpdateCurrentDetailsTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_updateCurrentDetailsWithNullUserShouldReturnError() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateCurrentUserDetails(null, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_USER);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_updateCurrentDetailsWithBlankNameShouldReturnError() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User user = new User();
        user.setName("");
        CometChat.updateCurrentUserDetails(user, new CometChat.CallbackListener<User>() {
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
    public void a3_updateCurrentDetailsWithValidDetailsShouldReturnSuccess() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User user = new User();
        List<String> tags = new ArrayList<>();
        tags.add(CometChatTestConstants.USER_TAG_FINAL);
        user.setTags(tags);
        CometChat.updateCurrentUserDetails(user, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                String validateUser = TestUtils.validateUser(updatedUser);
                AssertHelper.assertTrue(methodName + " Validate User : " + validateUser, validateUser.equalsIgnoreCase(""));
                boolean validateTags = false;
                if(updatedUser.getTags().contains(CometChatTestConstants.USER_TAG_FINAL)){
                    validateTags = true;
                }
                AssertHelper.assertTrue(methodName + " Tags check : " , validateTags);
                boolean localUserCheck = false;
                User loggedInUser = CometChat.getLoggedInUser();
                if(loggedInUser.getTags().contains(CometChatTestConstants.USER_TAG_FINAL)){
                    localUserCheck = true;
                }
                AssertHelper.assertTrue(methodName + " Local User Check : " , localUserCheck);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " + e.getMessage());
                countDownLatch.countDown();
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

}
