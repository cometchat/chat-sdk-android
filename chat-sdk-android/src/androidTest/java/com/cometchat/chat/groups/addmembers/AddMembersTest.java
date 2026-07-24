package com.cometchat.chat.groups.addmembers;


import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.ConversationsRequest;
import com.cometchat.chat.core.GroupMembersRequest;
import com.cometchat.chat.core.MessagesRequest;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.AppEntity;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.Conversation;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;
import com.cometchat.chat.core.CurrentUserRepo;
import com.cometchat.chat.core.SettingsRepo;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddMembersTest {

    private final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_addMembersToPublicGroupWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<GroupMember> members = new ArrayList<>();
        CometChat.addMembersToGroup(CometChatTestConstants.EMPTY_DATA, members, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_GUID);
                AssertHelper.assertTrue(methodName,condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_addMembersToPublicGroupWithInvalidGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<GroupMember> members = new ArrayList<>();
        CometChat.addMembersToGroup(CometChatTestConstants.INVALID_DATA, members, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_GUID_NOT_FOUND);
                AssertHelper.assertTrue(methodName,condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_addMembersToPublicGroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<GroupMember> members = new ArrayList<>();
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_1, CometChatConstants.SCOPE_ADMIN));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatConstants.SCOPE_MODERATOR));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_3, CometChatConstants.SCOPE_MODERATOR));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_4, CometChatConstants.SCOPE_PARTICIPANT));
        members.add(new GroupMember(TestPreferenceHelper.getLastCreatedUserUId(), CometChatConstants.SCOPE_PARTICIPANT));
        CometChat.addMembersToGroup(TestPreferenceHelper.getLastPublicGroupGUID(), members, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Assert.assertNotNull(methodName + " result hashmap not null check ", stringStringHashMap);
                AssertHelper.assertTrue(methodName + " result hashmap size check : " , stringStringHashMap.size() == 5);
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
    public void a4_addMembersToPrivateGroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<GroupMember> members = new ArrayList<>();
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_1, CometChatConstants.SCOPE_ADMIN));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatConstants.SCOPE_MODERATOR));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_3, CometChatConstants.SCOPE_MODERATOR));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_4, CometChatConstants.SCOPE_PARTICIPANT));
        members.add(new GroupMember(TestPreferenceHelper.getLastCreatedUserUId(), CometChatConstants.SCOPE_PARTICIPANT));
        CometChat.addMembersToGroup(TestPreferenceHelper.getLastPrivateGroupGUID(), members, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Assert.assertNotNull(methodName + " result hashmap not null check ", stringStringHashMap);
                AssertHelper.assertTrue(methodName + " result hashmap size check : " , stringStringHashMap.size() == 5);
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
    public void a5_addMembersToPasswordGroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<GroupMember> members = new ArrayList<>();
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_1, CometChatConstants.SCOPE_ADMIN));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatConstants.SCOPE_MODERATOR));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_3, CometChatConstants.SCOPE_MODERATOR));
        members.add(new GroupMember(CometChatTestConstants.RECEIVER_UID_4, CometChatConstants.SCOPE_PARTICIPANT));
        members.add(new GroupMember(TestPreferenceHelper.getLastCreatedUserUId(), CometChatConstants.SCOPE_PARTICIPANT));
        CometChat.addMembersToGroup(TestPreferenceHelper.getLastPasswordGroupGUID(), members, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Assert.assertNotNull(methodName + " result hashmap not null check ", stringStringHashMap);
                AssertHelper.assertTrue(methodName + " result hashmap size check : " , stringStringHashMap.size() == 5);
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
    public void a6_loginWithModerator() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.VALID_API_KEY
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

    // Moderator Tests with Public Group

    @Test
    public void a7_groupEntryInConversationsForAddedMemberGroupCheckForPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation null check : ", conversations);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " validate conversaation list : " + validateConversationList , validateConversationList.equalsIgnoreCase(""));
                boolean isConversationPresent = false;
                for(Conversation conversation : conversations){
                    AppEntity appEntity = conversation.getConversationWith();
                    if(appEntity instanceof Group){
                        Group group = (Group)appEntity;
                        if(group.getGuid().equalsIgnoreCase(TestPreferenceHelper.getLastPublicGroupGUID()))
                            isConversationPresent = true;
                    }
                }
                AssertHelper.assertTrue(methodName + " is conversation present after group creation : " , isConversationPresent);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName +  " " + e.getMessage());
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a8_sendMessageToPublicGroupByNewlyAddedMember() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToGroup(receivedTextMessage.getId());
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
    public void a9_getGroupForNewlyAddedMemberForPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getGroup(TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", TestPreferenceHelper.getLastPublicGroupGUID().equalsIgnoreCase(createdGroup.getGuid()));
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
    public void b1_fetchMessagesByNewlyAddedMemberForPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= 30);
                AssertHelper.assertTrue(methodName + " Received Messsage List Size", receivedMessages.size() == 7);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(TestPreferenceHelper.getLastPublicGroupGUID()));
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
    public void b2_fetchGroupMembersByNewlyAddedMemberForPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 6);
                String validateGroupMembers = TestUtils.validateGroupMembersList(groupMembers);
                AssertHelper.assertTrue(methodName + " Validate GroupMember List : " + validateGroupMembers, validateGroupMembers.equalsIgnoreCase(""));
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

    // moderator tests with Password Group

    @Test
    public void b3_groupEntryInConversationsForAddedMemberGroupCheckForPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation null check : ", conversations);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " validate conversaation list : " + validateConversationList , validateConversationList.equalsIgnoreCase(""));
                boolean isConversationPresent = false;
                for(Conversation conversation : conversations){
                    AppEntity appEntity = conversation.getConversationWith();
                    if(appEntity instanceof Group){
                        Group group = (Group)appEntity;
                        if(group.getGuid().equalsIgnoreCase(TestPreferenceHelper.getLastPasswordGroupGUID()))
                            isConversationPresent = true;
                    }
                }
                AssertHelper.assertTrue(methodName + " is conversation present after group creation : " , isConversationPresent);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName +  " " + e.getMessage());
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b4_sendMessageToPasswordGroupByNewlyAddedMember() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPasswordGroupGUID(),
                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToGroup(receivedTextMessage.getId());
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
    public void b5_getGroupForNewlyAddedMemberForPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getGroup(TestPreferenceHelper.getLastPasswordGroupGUID(), new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", TestPreferenceHelper.getLastPasswordGroupGUID().equalsIgnoreCase(createdGroup.getGuid()));
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
    public void b6_fetchMessagesByNewlyAddedMemberCreatedPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPasswordGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= 30);
                AssertHelper.assertTrue(methodName + " Received Messsage List Size", receivedMessages.size() == 7);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(TestPreferenceHelper.getLastPasswordGroupGUID()));
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
    public void b7_fetchGroupMembersByNewlyAddedMemberFOrPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 6);
                String validateGroupMembers = TestUtils.validateGroupMembersList(groupMembers);
                AssertHelper.assertTrue(methodName + " Validate GroupMember List : " + validateGroupMembers, validateGroupMembers.equalsIgnoreCase(""));
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

    // moderator tests for private groups

    @Test
    public void b8_groupEntryInConversationsForAddedMemberGroupCheck() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation null check : ", conversations);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " validate conversaation list : " + validateConversationList , validateConversationList.equalsIgnoreCase(""));
                boolean isConversationPresent = false;
                for(Conversation conversation : conversations){
                    AppEntity appEntity = conversation.getConversationWith();
                    if(appEntity instanceof Group){
                        Group group = (Group)appEntity;
                        if(group.getGuid().equalsIgnoreCase(TestPreferenceHelper.getLastPrivateGroupGUID()))
                            isConversationPresent = true;
                    }
                }
                AssertHelper.assertTrue(methodName + " is conversation present after group creation : " , isConversationPresent);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName +  " " + e.getMessage());
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b9_sendMessageToPrivateGroupByNewlyAddedMember() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPrivateGroupGUID(),
                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToGroup(receivedTextMessage.getId());
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
    public void c1_getGroupForNewlyAddedMemberForPrivateGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getGroup(TestPreferenceHelper.getLastPrivateGroupGUID(), new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", TestPreferenceHelper.getLastPrivateGroupGUID().equalsIgnoreCase(createdGroup.getGuid()));
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
    public void c2_fetchMessagesByNewlyAddedMemberCreatedPrivateGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPrivateGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= 30);
                AssertHelper.assertTrue(methodName + " Received Messsage List Size", receivedMessages.size() == 7);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(TestPreferenceHelper.getLastPrivateGroupGUID()));
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
    public void c3_fetchGroupMembersByNewlyAddedMemberForPrivateGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPrivateGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 6);
                String validateGroupMembers = TestUtils.validateGroupMembersList(groupMembers);
                AssertHelper.assertTrue(methodName + " Validate GroupMember List : " + validateGroupMembers, validateGroupMembers.equalsIgnoreCase(""));
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

    // Update Group By Moderator
    @Test
    public void c4_updatePublicGroupbyModerator() throws InterruptedException {
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
    public void c5_updatePasswordGroupbyModerator() throws InterruptedException {
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
    public void c6_updatePrivateGroupbyModerator() throws InterruptedException {
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
    public void c7_loginWithParticipant() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(CometChatTestConstants.RECEIVER_UID_4, CometChatTestConstants.VALID_API_KEY
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

    // Participant Tests with Public Group

    @Test
    public void c8_groupEntryInConversationsForAddedMemberGroupCheckForPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation null check : ", conversations);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " validate conversaation list : " + validateConversationList , validateConversationList.equalsIgnoreCase(""));
                boolean isConversationPresent = false;
                for(Conversation conversation : conversations){
                    AppEntity appEntity = conversation.getConversationWith();
                    if(appEntity instanceof Group){
                        Group group = (Group)appEntity;
                        if(group.getGuid().equalsIgnoreCase(TestPreferenceHelper.getLastPublicGroupGUID()))
                            isConversationPresent = true;
                    }
                }
                AssertHelper.assertTrue(methodName + " is conversation present after group creation : " , isConversationPresent);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName +  " " + e.getMessage());
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c9_sendMessageToPublicGroupByNewlyAddedMember() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToGroup(receivedTextMessage.getId());
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
    public void d1_getGroupForNewlyAddedMemberForPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getGroup(TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", TestPreferenceHelper.getLastPublicGroupGUID().equalsIgnoreCase(createdGroup.getGuid()));
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
    public void d2_fetchMessagesByNewlyAddedMemberForPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= 30);
                AssertHelper.assertTrue(methodName + " Received Messsage List Size", receivedMessages.size() == 8);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(TestPreferenceHelper.getLastPublicGroupGUID()));
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
    public void d3_fetchGroupMembersByNewlyAddedMemberForPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 6);
                String validateGroupMembers = TestUtils.validateGroupMembersList(groupMembers);
                AssertHelper.assertTrue(methodName + " Validate GroupMember List : " + validateGroupMembers, validateGroupMembers.equalsIgnoreCase(""));
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

    // participant tests with Password Group

    @Test
    public void d4_groupEntryInConversationsForAddedMemberGroupCheckForPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation null check : ", conversations);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " validate conversaation list : " + validateConversationList , validateConversationList.equalsIgnoreCase(""));
                boolean isConversationPresent = false;
                for(Conversation conversation : conversations){
                    AppEntity appEntity = conversation.getConversationWith();
                    if(appEntity instanceof Group){
                        Group group = (Group)appEntity;
                        if(group.getGuid().equalsIgnoreCase(TestPreferenceHelper.getLastPasswordGroupGUID()))
                            isConversationPresent = true;
                    }
                }
                AssertHelper.assertTrue(methodName + " is conversation present after group creation : " , isConversationPresent);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName +  " " + e.getMessage());
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void d5_sendMessageToPasswordGroupByNewlyAddedMember() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPasswordGroupGUID(),
                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToGroup(receivedTextMessage.getId());
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
    public void d6_getGroupForNewlyAddedMemberForPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getGroup(TestPreferenceHelper.getLastPasswordGroupGUID(), new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", TestPreferenceHelper.getLastPasswordGroupGUID().equalsIgnoreCase(createdGroup.getGuid()));
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
    public void d7_fetchMessagesByNewlyAddedMemberCreatedPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPasswordGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= 30);
                AssertHelper.assertTrue(methodName + " Received Messsage List Size", receivedMessages.size() == 8);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(TestPreferenceHelper.getLastPasswordGroupGUID()));
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
    public void d8_fetchGroupMembersByNewlyAddedMemberFOrPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 6);
                String validateGroupMembers = TestUtils.validateGroupMembersList(groupMembers);
                AssertHelper.assertTrue(methodName + " Validate GroupMember List : " + validateGroupMembers, validateGroupMembers.equalsIgnoreCase(""));
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

    // participant tests for private groups

    @Test
    public void d9_groupEntryInConversationsForAddedMemberGroupCheck() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Assert.assertNotNull(methodName + " Conversation null check : ", conversations);
                String validateConversationList = TestUtils.validateConversationList(conversations);
                AssertHelper.assertTrue(methodName + " validate conversaation list : " + validateConversationList , validateConversationList.equalsIgnoreCase(""));
                boolean isConversationPresent = false;
                for(Conversation conversation : conversations){
                    AppEntity appEntity = conversation.getConversationWith();
                    if(appEntity instanceof Group){
                        Group group = (Group)appEntity;
                        if(group.getGuid().equalsIgnoreCase(TestPreferenceHelper.getLastPrivateGroupGUID()))
                            isConversationPresent = true;
                    }
                }
                AssertHelper.assertTrue(methodName + " is conversation present after group creation : " , isConversationPresent);
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName +  " " + e.getMessage());
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void e1_sendMessageToPrivateGroupByNewlyAddedMember() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPrivateGroupGUID(),
                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                Assert.assertNotNull(methodName + " Received Messsage Null Check", receivedTextMessage);
                AssertHelper.assertTrue(methodName + "ReceiverID check : ", textMessage.getReceiverUid().equalsIgnoreCase(receivedTextMessage.getReceiverUid()));
                AssertHelper.assertTrue(methodName + "Receiver Type check : ", textMessage.getReceiverType().equalsIgnoreCase(receivedTextMessage.getReceiverType()));
                AssertHelper.assertTrue(methodName + "Message Text check : ", textMessage.getText().equalsIgnoreCase(receivedTextMessage.getText()));
                String validateTextMessage = TestUtils.validateTextMessage(receivedTextMessage, false);
                AssertHelper.assertTrue(methodName + " Validate Text Message : " + validateTextMessage, validateTextMessage.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastTextMessageIdToGroup(receivedTextMessage.getId());
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
    public void e2_getGroupForNewlyAddedMemberForPrivateGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.getGroup(TestPreferenceHelper.getLastPrivateGroupGUID(), new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", TestPreferenceHelper.getLastPrivateGroupGUID().equalsIgnoreCase(createdGroup.getGuid()));
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
    public void e3_fetchMessagesByNewlyAddedMemberCreatedPrivateGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPrivateGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= 30);
                AssertHelper.assertTrue(methodName + " Received Messsage List Size", receivedMessages.size() == 8);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(TestPreferenceHelper.getLastPrivateGroupGUID()));
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
    public void e4_fetchGroupMembersByNewlyAddedMemberForPrivateGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPrivateGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 6);
                String validateGroupMembers = TestUtils.validateGroupMembersList(groupMembers);
                AssertHelper.assertTrue(methodName + " Validate GroupMember List : " + validateGroupMembers, validateGroupMembers.equalsIgnoreCase(""));
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

    // Update Group By Participant
    @Test
    public void e5_updatePublicGroupbyParticipant() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPublicGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME + " Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName , condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void e6_updatePasswordGroupbyPaticipant() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPasswordGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME + " Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName , condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }
    @Test
    public void e7_updatePrivateGroupbyParticipant() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group();
        group.setGuid(TestPreferenceHelper.getLastPrivateGroupGUID());
        group.setName(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME + " Updated");
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_MODERATOR_SCOPE);
                AssertHelper.assertTrue(methodName , condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    // Fetch Group members test

    @Test
    public void e8_fetchGroupMembersWithEmptyScopesShouldReturnAllMembers() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> scopes = new ArrayList<>();
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPublicGroupGUID()).setScopes(scopes).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 6);
                String validateGroupMembers = TestUtils.validateGroupMembersList(groupMembers);
                AssertHelper.assertTrue(methodName + " Validate GroupMember List : " + validateGroupMembers, validateGroupMembers.equalsIgnoreCase(""));
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
    public void e9_fetchGroupMembersWithValidScopesShouldReturnMembersBelongingToThoseScopes() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> scopes = new ArrayList<>();
        scopes.add("admin");
        scopes.add("moderator");
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPublicGroupGUID()).setScopes(scopes).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                for(GroupMember groupMember : groupMembers){
                    AssertHelper.assertTrue(methodName + " GroupMember scope check for UID : " + groupMember.getUid(), (groupMember.getScope().equalsIgnoreCase("admin") || groupMember.getScope().equalsIgnoreCase("moderator")));
                }
                String validateGroupMembers = TestUtils.validateGroupMembersList(groupMembers);
                AssertHelper.assertTrue(methodName + " Validate GroupMember List : " + validateGroupMembers, validateGroupMembers.equalsIgnoreCase(""));
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
