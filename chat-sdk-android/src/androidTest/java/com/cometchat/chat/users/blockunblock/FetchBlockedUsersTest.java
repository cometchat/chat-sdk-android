package com.cometchat.chat.users.blockunblock;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.BlockedUsersRequest;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.TestUtils;

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
public class FetchBlockedUsersTest {

    private static final int TEST_CASE_TIMEOUT = 5;
    public static List<User> blockedUsers = new ArrayList<>();

    @Test
    public void a1_fetchBlockedUsersWithLimitGreaterThan100ShouldReturnError() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BlockedUsersRequest blockedUsersRequest = new BlockedUsersRequest.BlockedUsersRequestBuilder().setLimit(120).build();
        blockedUsersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_fetchBlockedUsers() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BlockedUsersRequest blockedUsersRequest = new BlockedUsersRequest.BlockedUsersRequestBuilder().build();
        blockedUsersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                AssertHelper.assertTrue(methodName + " User list size check :", receivedUsers.size() == 2);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                blockedUsers = receivedUsers;
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
    public void a3_fetchBlockedUsersWithSearchKey() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BlockedUsersRequest blockedUsersRequest = new BlockedUsersRequest.BlockedUsersRequestBuilder()
                .setDirection(BlockedUsersRequest.DIRECTION_BOTH)
                .setSearchKeyword("superhero")
                .build();
        blockedUsersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                AssertHelper.assertTrue(methodName + " User list size check :", receivedUsers.size() == 2);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                for (User user : receivedUsers){
                   AssertHelper.assertTrue(methodName + " Block check for uid : " + user.getUid(), user.isBlockedByMe() || user.isHasBlockedMe());
                }
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
    public void a4_fetchBlockedUsersWithSearchKeyAndDirectionAsHasBlockedMe() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BlockedUsersRequest blockedUsersRequest = new BlockedUsersRequest.BlockedUsersRequestBuilder().setSearchKeyword("superhero")
                .setDirection(BlockedUsersRequest.DIRECTION_HAS_BLOCKED_ME)
                .build();
        blockedUsersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                AssertHelper.assertTrue(methodName + " User list size check :", receivedUsers.size() == 1);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                for (User user : receivedUsers){
                    AssertHelper.assertTrue(methodName + " Block check for uid : " + user.getUid(),  user.isHasBlockedMe());
                }
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
    public void a5_fetchBlockedUsersWithSearchKeyAndDirectionAsIsBlockedbyMe() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BlockedUsersRequest blockedUsersRequest = new BlockedUsersRequest.BlockedUsersRequestBuilder().setSearchKeyword("superhero")
                .setDirection(BlockedUsersRequest.DIRECTION_BLOCKED_BY_ME)
                .build();
        blockedUsersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                AssertHelper.assertTrue(methodName + " User list size check :", receivedUsers.size() == 2);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                for (User user : receivedUsers){
                    AssertHelper.assertTrue(methodName + " Block check for uid : " + user.getUid(),  user.isBlockedByMe());
                }
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
