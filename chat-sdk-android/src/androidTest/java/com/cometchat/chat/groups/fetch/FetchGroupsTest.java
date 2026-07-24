package com.cometchat.chat.groups.fetch;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.GroupsRequest;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Group;
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
public class FetchGroupsTest {

    private static final int TEST_CASE_TIMEOUT = 5;
    private static final int LIMIT = 30;

    @Test
    public void a1_fetchGroupsWithZeroLimitShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(0).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
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
    public void a2_fetchGroupsWithNegativeLimitShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(-10).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
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
    public void a3_fetchGroupsWithValidDetailsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> receivedGroups) {
                Assert.assertNotNull(methodName + " group list null check :", receivedGroups);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() <= LIMIT);
                String validateGroupList = TestUtils.validateGroupList(receivedGroups);
                AssertHelper.assertTrue(methodName + " validate group list : " + validateGroupList, validateGroupList.equalsIgnoreCase(""));
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
    public void a4_fetchGroupsWithSpecifiedLimitShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(3).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> receivedGroups) {
                Assert.assertNotNull(methodName + " group list null check :", receivedGroups);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() <= LIMIT);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() == 3);
                String validateGroupList = TestUtils.validateGroupList(receivedGroups);
                AssertHelper.assertTrue(methodName + " validate group list : " + validateGroupList, validateGroupList.equalsIgnoreCase(""));
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
    public void a5_fetchGroupsWithSearchKeywordShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setSearchKeyWord("Public").build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> receivedGroups) {
                Assert.assertNotNull(methodName + " group list null check :", receivedGroups);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() <= LIMIT);
                String validateGroupList = TestUtils.validateGroupList(receivedGroups);
                AssertHelper.assertTrue(methodName + " validate group list : " + validateGroupList, validateGroupList.equalsIgnoreCase(""));
                for (Group group : receivedGroups)
                    AssertHelper.assertTrue(methodName + " Group search key check for : " + group.getGuid(), group.getName().contains("Public"));
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
    public void a5_fetchGroupsWithInvalidSearchKeywordShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setSearchKeyWord("abcdefghij").build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> receivedGroups) {
                Assert.assertNotNull(methodName + " group list null check :", receivedGroups);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() <= LIMIT);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() == 0);
                String validateGroupList = TestUtils.validateGroupList(receivedGroups);
                AssertHelper.assertTrue(methodName + " validate group list : " + validateGroupList, validateGroupList.equalsIgnoreCase(""));
                for (Group group : receivedGroups)
                    AssertHelper.assertTrue(methodName + " Group search key check for : " + group.getGuid(), !group.getName().contains("abcdefghij"));
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
    public void a6_fetchGroupsWithWithTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().withTags(true).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> receivedGroups) {
                Assert.assertNotNull(methodName + " group list null check :", receivedGroups);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() <= LIMIT);
                String validateGroupList = TestUtils.validateGroupList(receivedGroups);
                AssertHelper.assertTrue(methodName + " validate group list : " + validateGroupList, validateGroupList.equalsIgnoreCase(""));
                boolean validateTags = false;
                for (Group group : receivedGroups) {
                    if (group.getGuid().contains("withtags")) {
                        if (group.getTags() != null && (group.getTags().contains(CometChatConstants.GROUP_TYPE_PUBLIC))
                                || group.getTags().contains(CometChatConstants.GROUP_TYPE_PASSWORD)
                                || group.getTags().contains(CometChatConstants.GROUP_TYPE_PRIVATE)) {
                            validateTags = true;
                        } else {
                            validateTags = false;
                        }
                    }
                }
                AssertHelper.assertTrue(methodName + " Tags check : ", validateTags);
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
    public void a7_fetchGroupsWithSetTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags = new ArrayList<>();
        tags.add(CometChatConstants.GROUP_TYPE_PUBLIC);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setTags(tags).withTags(true).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> receivedGroups) {
                Assert.assertNotNull(methodName + " group list null check :", receivedGroups);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() <= LIMIT);
                String validateGroupList = TestUtils.validateGroupList(receivedGroups);
                AssertHelper.assertTrue(methodName + " validate group list : " + validateGroupList, validateGroupList.equalsIgnoreCase(""));
                boolean validateTags = false;
                for (Group group : receivedGroups) {
                    if (group.getTags() != null && group.getTags().contains(CometChatConstants.GROUP_TYPE_PUBLIC)) {
                        validateTags = true;
                    } else {
                        validateTags = false;
                    }
                }
                AssertHelper.assertTrue(methodName + " Tags check : ", validateTags);
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
    public void a8_fetchGroupsWithSetMultipleTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags = new ArrayList<>();
        tags.add(CometChatConstants.GROUP_TYPE_PUBLIC);
        tags.add(CometChatConstants.GROUP_TYPE_PASSWORD);
        tags.add(CometChatConstants.GROUP_TYPE_PRIVATE);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setTags(tags).withTags(true).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> receivedGroups) {
                Assert.assertNotNull(methodName + " group list null check :", receivedGroups);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() <= LIMIT);
                String validateGroupList = TestUtils.validateGroupList(receivedGroups);
                AssertHelper.assertTrue(methodName + " validate group list : " + validateGroupList, validateGroupList.equalsIgnoreCase(""));
                boolean validateTags = false;
                for (Group group : receivedGroups) {
                    if (group.getTags() != null && (group.getTags().contains(CometChatConstants.GROUP_TYPE_PUBLIC)
                            || group.getTags().contains(CometChatConstants.GROUP_TYPE_PASSWORD)
                            || group.getTags().contains(CometChatConstants.GROUP_TYPE_PRIVATE))) {
                        validateTags = true;
                    } else {
                        validateTags = false;
                    }
                }
                AssertHelper.assertTrue(methodName + " Tags check : ", validateTags);
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

    public void a9_fetchGroupsWithSetTagsAndNoWithTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags = new ArrayList<>();
        tags.add(CometChatConstants.GROUP_TYPE_PUBLIC);
        tags.add(CometChatConstants.GROUP_TYPE_PASSWORD);
        tags.add(CometChatConstants.GROUP_TYPE_PRIVATE);
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setTags(tags).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> receivedGroups) {
                Assert.assertNotNull(methodName + " group list null check :", receivedGroups);
                AssertHelper.assertTrue(methodName + " group lis size check :", receivedGroups.size() <= LIMIT);
                String validateGroupList = TestUtils.validateGroupList(receivedGroups);
                AssertHelper.assertTrue(methodName + " validate group list : " + validateGroupList, validateGroupList.equalsIgnoreCase(""));
                boolean validateTags = false;
                for (Group group : receivedGroups) {
                    if (group.getTags() == null) {
                        validateTags = true;
                    } else {
                        validateTags = false;
                    }
                }
                AssertHelper.assertTrue(methodName + " Tags check : ", validateTags);
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
