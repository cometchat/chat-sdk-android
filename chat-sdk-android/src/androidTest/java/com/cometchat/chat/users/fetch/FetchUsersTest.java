package com.cometchat.chat.users.fetch;


import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.cometchat.chat.users.blockunblock.BlockUserTest;
import com.cometchat.chat.users.blockunblock.FetchBlockedUsersTest;
import com.cometchat.chat.users.blockunblock.UnblockUserTest;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.UsersRequest;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FetchUsersTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_fetchUsersWithLimitGreaterThan100ShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(120).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
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
    public void a2_fetchUsersWithLimit0ShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(0).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_fetchUsersWithNegativeLimitShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(-10).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_fetchUsersWithNoLimitShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                AssertHelper.assertTrue(methodName + " User list size check :", receivedUsers.size() <= 30);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
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
    public void a5_fetchUsersWithValidSearchKeyword() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setSearchKeyword("TEST").build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                AssertHelper.assertTrue(methodName + " User list size check :", receivedUsers.size() <= 30);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                for (User user : receivedUsers) {
                    AssertHelper.assertTrue(methodName + " search keyword validation for user with UID : " + user.getUid(), user.getName().contains("Test"));
                }
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
    public void a6_fetchUsersWithInvalidSearchKeyword() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setSearchKeyword("ABC").build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                AssertHelper.assertTrue(methodName + " User list size check :", receivedUsers.size() <= 30);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
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
    public void a7_fetchUsersWithInvalidRole() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setRole("Invalid").build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                AssertHelper.assertTrue(methodName + " User list size check :", receivedUsers.size() == 0);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
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
    public void a8_fetchUsersWithValidRole() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setRole("default").build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                for (User user : receivedUsers)
                    AssertHelper.assertTrue(methodName + " Role check for user with UID : " + user.getUid(), user.getRole().equalsIgnoreCase("default"));
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
    public void a9_runBlockUserTest() {
        TestUtils.runTestForClass(BlockUserTest.class);
    }

    @Test
    public void b1_fetchUsersWithSearchKeySuperHero() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setSearchKeyword("superhero").build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                for (User user : receivedUsers) {
                    if (FetchBlockedUsersTest.blockedUsers.contains(user))
                        AssertHelper.assertTrue(methodName + " BlockedbyMe check for : " + user.getUid(), user.isBlockedByMe());
                    AssertHelper.assertTrue(methodName + " Search key check for user with UID : " + user.getUid(), user.getUid().contains("superhero"));
                }
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
    public void b2_fetchUsersWithSearchKeySuperHeroAndHideBlockedUsersAsTrue() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setSearchKeyword("superhero").hideBlockedUsers(true).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                for (User user : receivedUsers)
                    AssertHelper.assertTrue(methodName + " Block check for user with UID : " + user.getUid(), !FetchBlockedUsersTest.blockedUsers.contains(user));
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
    public void b3_runUnblockUsersTest() {
        TestUtils.runTestForClass(UnblockUserTest.class);
    }

    @Test
    public void b4_fetchUsersWithDeprecatedSingleRoleMethod() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setRole(TestPreferenceHelper.getRole1()).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " User List Count : " , receivedUsers.size() == 1);
                for (User user : receivedUsers)
                    AssertHelper.assertTrue(methodName + " Role check for user with UID : " + user.getUid(), user.getRole().equalsIgnoreCase(TestPreferenceHelper.getRole1()));
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
    public void b5_fetchUsersWithDeprecatedSingleRoleAndRolesList() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> roles = new ArrayList<>();
        roles.add(TestPreferenceHelper.getRole1());
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setRole(TestPreferenceHelper.getRole2()).setRoles(roles).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " User List Count : " , receivedUsers.size() == 2);
                for (User user : receivedUsers)
                    AssertHelper.assertTrue(methodName + " Role check for user with UID : " + user.getUid(), (user.getRole().equalsIgnoreCase(TestPreferenceHelper.getRole1())) || user.getRole().equalsIgnoreCase(TestPreferenceHelper.getRole2()));
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
    public void b6_fetchUsersWithMultiplValuesRolesList() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> roles = new ArrayList<>();
        roles.add(TestPreferenceHelper.getRole1());
        roles.add(TestPreferenceHelper.getRole2());
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setRoles(roles).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " User List Count : " , receivedUsers.size() == 2);
                for (User user : receivedUsers)
                    AssertHelper.assertTrue(methodName + " Role check for user with UID : " + user.getUid(), (user.getRole().equalsIgnoreCase(TestPreferenceHelper.getRole1())) || user.getRole().equalsIgnoreCase(TestPreferenceHelper.getRole2()));
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
    public void b7_fetchUsersWithWithTagsPropertyShouldReturnTags() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder()
                .withTags(true)
                .build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                boolean withTagsValidation = false;
                for(User user : receivedUsers){
                    if(user.getUid().contains("withtags") &&
                    user.getTags()!=null){
                        withTagsValidation = true;
                    }
                }
                AssertHelper.assertTrue(methodName + " With Tags validation", withTagsValidation) ;
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
    public void b8_fetchUsersWithSetTagsPropertyShouldReturnTags() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags= new ArrayList();
        tags.add(CometChatTestConstants.USER_TAG_FINAL);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder()
                .setTags(tags)
                .withTags(true)
                .build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                boolean setTagsValidation = false;
                for(User user : receivedUsers){
                    if(user.getTags()!= null && user.getTags().contains(CometChatTestConstants.USER_TAG_FINAL)){
                        setTagsValidation = true;
                    }else{
                        setTagsValidation = false;
                    }
                }
                AssertHelper.assertTrue(methodName + " With Tags validation", setTagsValidation) ;
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
    public void b9_fetchUsersWithSpecifiedUIDsShouldReturnSuccess() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final List<String> UIDS= new ArrayList();
        UIDS.add("superhero1");
        UIDS.add("superhero2");
        UIDS.add("superhero3");
        UIDS.add("superhero4");
        UIDS.add("superhero5");
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder()
                .setUIDs(UIDS)
                .build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                boolean withUIDSValidation = false;
                for (User user : receivedUsers){
                    if(UIDS.contains(user.getUid()))
                        withUIDSValidation = true;
                    else
                        withUIDSValidation = false;
                }
                AssertHelper.assertTrue(methodName + " With Tags validation", withUIDSValidation) ;
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
    public void c1_fetchUsersWithFriendsOnlyTrueShouldReturnOnlyFriends() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder()
                .friendsOnly(true)
                .build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                AssertHelper.assertTrue(methodName + " Friends count check", receivedUsers.size() == 4) ;
                boolean friendsOnlyValidation = false;
                List<String> friends = new ArrayList<>();
                friends.add("superhero2");
                friends.add("superhero3");
                friends.add("superhero4");
                friends.add("superhero5");
                for(User user : receivedUsers){
                    if(friends.contains(user.getUid())){
                        friendsOnlyValidation = true;
                    }else{
                        friendsOnlyValidation = false;
                    }
                }
                AssertHelper.assertTrue(methodName + " Friends Only List validation", friendsOnlyValidation) ;
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
    public void c2_fetchUsersWithSetTagsAndNoWithTagsPropertyShouldReturnTags() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags= new ArrayList();
        tags.add(CometChatTestConstants.USER_TAG_FINAL);
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder()
                .setTags(tags)
                .build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> receivedUsers) {
                Assert.assertNotNull(methodName + " User List null check : ", receivedUsers);
                String validateUserList = TestUtils.validateUserList(receivedUsers);
                AssertHelper.assertTrue(methodName + " User List validation : " + validateUserList, validateUserList.equalsIgnoreCase(""));
                boolean setTagsValidation = false;
                for(User user : receivedUsers){
                    if(user.getTags() == null){
                        setTagsValidation = true;
                    }else{
                        setTagsValidation = false;
                    }
                }
                AssertHelper.assertTrue(methodName + " With Tags validation", setTagsValidation) ;
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
