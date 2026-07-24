package com.cometchat.chat.converations;


import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.ConversationsRequest;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Conversation;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
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
public class ConversationsTest {

    private static final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_fetchConversationsWithLimitGreaterThan100ShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(120).build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
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
    public void a2_fetchConversationsWithLimit0ShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(0).build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
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
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(-10).build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
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
    public void a4_fetchConversationsWithNoLimitShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversations);
                AssertHelper.assertTrue(methodName + " Conversation list size check :", conversations.size() <= 30);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversationList, validateConversationList.equalsIgnoreCase(""));
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
    public void a5_fetchConversationsWithNoLimitShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversations);
                AssertHelper.assertTrue(methodName + " Conversation list size check :", conversations.size() <= 30);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversationList, validateConversationList.equalsIgnoreCase(""));
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
    public void a6_fetchConversationsWithTypeUserReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setConversationType(CometChatConstants.CONVERSATION_TYPE_USER).build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversations);
                AssertHelper.assertTrue(methodName + " Conversation list size check :", conversations.size() <= 30);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversationList, validateConversationList.equalsIgnoreCase(""));
                for (Conversation conversation : conversations) {
                    AssertHelper.assertTrue(methodName + " Conversation Type user check : ", conversation.getConversationType().equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER));
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
    public void a6_fetchConversationsWithTypeGroupReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setConversationType(CometChatConstants.CONVERSATION_TYPE_GROUP).build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversations);
                AssertHelper.assertTrue(methodName + " Conversation list size check :", conversations.size() <= 30);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversationList, validateConversationList.equalsIgnoreCase(""));
                for (Conversation conversation : conversations) {
                    AssertHelper.assertTrue(methodName + " Conversation Type user check : ", conversation.getConversationType().equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP));
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
    public void a7_fetchConversationWithEmptyConversationWithShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getConversation(CometChatTestConstants.EMPTY_DATA, CometChatConstants.CONVERSATION_TYPE_USER, new CometChat.CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a8_fetchConversationWithEmptyConversationTypeShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getConversation(CometChatTestConstants.RECEIVER_UID_1, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a9_fetchConversationWithInvalidConversationTypeShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getConversation(CometChatTestConstants.RECEIVER_UID_1, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b1_fetchConversationWithInvalidConversationWithShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getConversation(CometChatTestConstants.INVALID_UID, CometChatConstants.CONVERSATION_TYPE_USER, new CometChat.CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
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
    public void b2_fetchConversationWithInvalidConversationTypeShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getConversation(CometChatTestConstants.RECEIVER_UID_1, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b3_fetchConversationForUserWithValidDataShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getConversation(CometChatTestConstants.RECEIVER_UID_1, CometChatConstants.CONVERSATION_TYPE_USER, new CometChat.CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversation);
                String validateConversation = TestUtils.validateConversation(conversation);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversation, validateConversation.equalsIgnoreCase(""));
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
    public void b4_fetchConversationForGroupWithValidDataShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getConversation(CometChatTestConstants.RECEIVER_GUID, CometChatConstants.CONVERSATION_TYPE_GROUP, new CometChat.CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversation);
                String validateConversation = TestUtils.validateConversation(conversation);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversation, validateConversation.equalsIgnoreCase(""));
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
    public void b5_fetchConversationsWithWithTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().withTags(true).build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversations);
                AssertHelper.assertTrue(methodName + " Conversation list size check :", conversations.size() <= 30);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversationList, validateConversationList.equalsIgnoreCase(""));
                boolean validateTags = false;
                for (Conversation conversation : conversations) {
                    if (conversation.getConversationWith() instanceof User) {
                        if (((User) conversation.getConversationWith()).getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)) {
                            if (conversation.getTags() != null && conversation.getTags().contains("conversation_tag1")
                                    && conversation.getTags().contains("conversation_tag2")) {
                                validateTags = true;
                            } else {
                                validateTags = false;
                            }
                        }
                    } else {
                        if (((Group) conversation.getConversationWith()).getGuid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID)) {
                            {
                                if (conversation.getTags() != null && conversation.getTags().contains("conversation_tag1")
                                        && conversation.getTags().contains("conversation_tag2")) {
                                    validateTags = true;
                                } else {
                                    validateTags = false;
                                }
                            }
                        }
                    }
                }
                countDownLatch.countDown();
            }

            ;

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
    public void b6_fetchConversationsWithSetTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags = new ArrayList<>();
        tags.add("conversation_tag1");
        tags.add("conversation_tag2");
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setTags(tags).withTags(true).build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversations);
                AssertHelper.assertTrue(methodName + " Conversation list size check :", conversations.size() == 4);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversationList, validateConversationList.equalsIgnoreCase(""));
                String conversationWith = null;
                for (Conversation conversation : conversations) {
                    boolean validateSetTags = false;
                    boolean validateWithTags = false;
                    if (conversation.getConversationWith() instanceof User) {
                        conversationWith = ((User) conversation.getConversationWith()).getUid();
                    } else {
                        conversationWith = ((Group) conversation.getConversationWith()).getGuid();
                    }
                    if (conversationWith.equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)
                            || conversationWith.equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID)) {
                        validateSetTags = true;
                    } else {
                        validateSetTags = false;
                    }
                    if (conversation.getTags() != null && conversation.getTags().contains("conversation_tag1")
                            && conversation.getTags().contains("conversation_tag1")) {
                        validateWithTags = true;
                    } else {
                        validateWithTags = false;
                    }
                    AssertHelper.assertTrue(methodName + " Validate Set Tags : ", validateSetTags);
                    AssertHelper.assertTrue(methodName + " Validate  With Tags : ", validateSetTags);
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
    public void b7_fetchConversationsWithSetTagsAndNoWithTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags = new ArrayList<>();
        tags.add("conversation_tag1");
        tags.add("conversation_tag2");
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setTags(tags).build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversations);
                AssertHelper.assertTrue(methodName + " Conversation list size check :", conversations.size() == 4);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversationList, validateConversationList.equalsIgnoreCase(""));
                String conversationWith = null;
                for (Conversation conversation : conversations) {
                    boolean validateSetTags = false;
                    boolean validateWithTags = false;
                    if (conversation.getConversationWith() instanceof User) {
                        conversationWith = ((User) conversation.getConversationWith()).getUid();
                    } else {
                        conversationWith = ((Group) conversation.getConversationWith()).getGuid();
                    }
                    if (conversationWith.equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)
                            || conversationWith.equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID)) {
                        validateSetTags = true;
                    } else {
                        validateSetTags = false;
                    }
                    if (conversation.getTags() == null) {
                        validateWithTags = true;
                    } else {
                        validateWithTags = false;
                    }
                    AssertHelper.assertTrue(methodName + " Validate Set Tags : ", validateSetTags);
                    AssertHelper.assertTrue(methodName + " Validate  With Tags : ", validateSetTags);
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
    public void b8_tagConversationWithSuperhero2WithBlankTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags = new ArrayList<>();
        CometChat.tagConversation(CometChatTestConstants.RECEIVER_UID_1,
                CometChatConstants.RECEIVER_TYPE_USER,
                tags,
                new CometChat.CallbackListener<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        Assert.assertNotNull(methodName + " Conversation List null check : ", conversation);
                        String validateConversation = TestUtils.validateConversation(conversation);
                        AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversation, validateConversation.equalsIgnoreCase(""));
                        boolean validateTags = false;
                        if (conversation.getTags() == null) {
                            validateTags = true;
                        } else {
                            validateTags = false;
                        }
                        AssertHelper.assertTrue(methodName + "Validate Tags :", validateTags);
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
    public void b9_tagConversationWithSupergroupWithBlankTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> tags = new ArrayList<>();
        CometChat.tagConversation(CometChatTestConstants.RECEIVER_GUID,
                CometChatConstants.RECEIVER_TYPE_GROUP,
                tags,
                new CometChat.CallbackListener<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        Assert.assertNotNull(methodName + " Conversation List null check : ", conversation);
                        String validateConversation = TestUtils.validateConversation(conversation);
                        AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversation, validateConversation.equalsIgnoreCase(""));
                        boolean validateTags = false;
                        if (conversation.getTags() == null) {
                            validateTags = true;
                        } else {
                            validateTags = false;
                        }
                        AssertHelper.assertTrue(methodName + "Validate Tags :", validateTags);
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
    public void c1_fetchConversationsWithUserAndGroupTagsShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().withUserAndGroupTags(true).build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation List null check : ", conversations);
                AssertHelper.assertTrue(methodName + " Conversation list size check :", conversations.size() == 4);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " Conversation List validation : " + validateConversationList, validateConversationList.equalsIgnoreCase(""));
                boolean validateUserAndGroupTags = false;
                for (Conversation conversation : conversations) {
                    if (conversation.getConversationWith() instanceof User) {
                        User conversationWith = (User) conversation.getConversationWith();
                        if (conversationWith.getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)
                                && conversationWith.getTags().contains("conversation_tag")) {
                            validateUserAndGroupTags = true;
                        }
                    }
                }
                AssertHelper.assertTrue(methodName + "Validate User and Group Tags : ", validateUserAndGroupTags);
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
    public void c2_deleteConversationForUserWithNullConversationWithShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteConversation(null, CometChatConstants.CONVERSATION_TYPE_USER, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c3_deleteConversationForUserWithEmptyConversationWithShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteConversation("", CometChatConstants.CONVERSATION_TYPE_USER, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c4_deleteConversationForUserWithNullConversationTypeShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteConversation("superhero2", null, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c5_deleteConversationForUserWithEmptyConversationTypeShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteConversation("superhero2", "", new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c6_deleteConversationForUserShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteConversation(CometChatTestConstants.RECEIVER_UID_1, CometChatConstants.CONVERSATION_TYPE_USER, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.assertTrue(methodName + "conversation deletion success :", s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_CONVERSATION_DELETE_SUCCESS));
                CometChat.getConversation(CometChatTestConstants.RECEIVER_UID_1, CometChatConstants.CONVERSATION_TYPE_USER, new CometChat.CallbackListener<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        AssertHelper.fail(methodName + "should not trigger success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_CONVERSATION_NOT_ACCESSIBLE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });

            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c7_deleteConversationForGroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.deleteConversation(CometChatTestConstants.RECEIVER_GUID, CometChatConstants.CONVERSATION_TYPE_GROUP, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.assertTrue(methodName + "conversation deletion success :", s.equalsIgnoreCase(CometChatConstants.SuccessMessages.MESSAGE_CONVERSATION_DELETE_SUCCESS));
                CometChat.getConversation(CometChatTestConstants.RECEIVER_GUID, CometChatConstants.CONVERSATION_TYPE_GROUP, new CometChat.CallbackListener<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        AssertHelper.fail(methodName + "should not trigger success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_CONVERSATION_NOT_ACCESSIBLE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });

            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

}
