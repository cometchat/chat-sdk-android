package com.cometchat.chat.groups.create;

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
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestPreferenceHelper;
import com.cometchat.chat.utils.TestUtils;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateGroupTest1 {

    private static final int TEST_CASE_TIMEOUT = 5;

    // Public Groups

    @Test
    public void a1_createPublicGroupWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group(CometChatTestConstants.EMPTY_DATA,
                String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_GUID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_createPublicGroupWithEmptyNameShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME, System.currentTimeMillis()),
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a3_createPublicGroupWithEmptyGUIDAndEmptyNameShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group(CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME)
                        || e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_GUID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_createPublicGroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCreatedPublicGroupGUID(createdGroup.getGuid());
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
    public void a5_createdGroupEntryInConversationsForNewlyCreatedPublicGroupCheck() throws InterruptedException{
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
    public void a6_sendMessageToNewlyCreatedPublicGroup() throws InterruptedException{
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
    public void a7_getGroupForNewlyCreatedPublicGroup() throws InterruptedException{
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
    public void a8_fetchMessagesForNewlyCreatedPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= 30);
                AssertHelper.assertTrue(methodName + " Received Messsage List Size", receivedMessages.size() == 1);
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
    public void a9_fetchGroupMembersForNewlyCreatedPublicGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 1);
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
    public void b1_createPublicGroupWithAlreadyTakenGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME,
                CometChatConstants.GROUP_TYPE_PUBLIC,
                CometChatTestConstants.EMPTY_DATA);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase("ERR_GUID_ALREADY_EXISTS");
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
    }

    // Private Groups

    @Test
    public void b2_createPrivateGroupWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(CometChatTestConstants.EMPTY_DATA,
                String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PRIVATE,
                CometChatTestConstants.EMPTY_DATA);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_GUID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
    }

    @Test
    public void b3_createPrivateGroupWithEmptyNameShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_GUID, System.currentTimeMillis()),
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.GROUP_TYPE_PRIVATE,
                CometChatTestConstants.EMPTY_DATA);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
    }

    @Test
    public void b4_createPrivateGroupWithEmptyGUIDAndEmptyNameShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group(CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.GROUP_TYPE_PRIVATE,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME)
                        || e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_GUID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b5_createPrivateGroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PRIVATE,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCreatedPrivateGroupGUID(createdGroup.getGuid());
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
    public void b6_createdGroupEntryInConversationsForNewlyCreatedPrivateGroupCheck() throws InterruptedException{
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
    public void b7_sendMessageToNewlyCreatedPrivateGroup() throws InterruptedException{
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
    public void b8_getGroupForNewlyCreatedPrivateGroup() throws InterruptedException{
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
    public void b9_fetchMessagesForNewlyCreatedPrivateGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPrivateGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= 30);
                AssertHelper.assertTrue(methodName + " Received Messsage List Size", receivedMessages.size() == 1);
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
    public void c1_fetchGroupMembersForNewlyCreatedPrivateGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPrivateGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 1);
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
    public void c2_createPrivateGroupWithAlreadyTakenGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(TestPreferenceHelper.getLastPrivateGroupGUID(),
                CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME,
                CometChatConstants.GROUP_TYPE_PRIVATE,
                CometChatTestConstants.EMPTY_DATA);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase("ERR_GUID_ALREADY_EXISTS");
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
    }

    // Password Groups

    @Test
    public void c3_createPasswordGroupWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group(CometChatTestConstants.EMPTY_DATA,
                String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PASSWORD,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_GUID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c4_createPasswordGroupWithEmptyNameShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME, System.currentTimeMillis()),
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.GROUP_TYPE_PASSWORD,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c5_createPasswordGroupWithEmptyGUIDAndEmptyNameShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group(CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.GROUP_TYPE_PASSWORD,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME)
                        || e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_GUID);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c6_createPasswordGroupWithEmptyPasswordShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PASSWORD,
                "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_PASSWORD_MISSING);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c7_createPasswordGroupShouldReturnSuccess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PASSWORD,
                CometChatTestConstants.DEFAULT_PASSWORD_GROUP_PASSWORD);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                String validateGroup = TestUtils.validateGroup(createdGroup, true);
                AssertHelper.assertTrue(methodName + " Validate Group : " + validateGroup, validateGroup.equalsIgnoreCase(""));
                TestPreferenceHelper.saveLastCreatedPasswordGroupGUID(createdGroup.getGuid());
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
    public void c8_createdGroupEntryInConversationsForNewlyCreatedPasswordGroupCheck() throws InterruptedException{
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
    public void c9_sendMessageToNewlyCreatedPasswordGroup() throws InterruptedException{
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
    public void d1_getGroupForNewlyCreatedPasswordGroup() throws InterruptedException{
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
    public void d2_fetchMessagesForNewlyCreatedPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPasswordGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= 30);
                AssertHelper.assertTrue(methodName + " Received Messsage List Size", receivedMessages.size() == 1);
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
    public void d3_fetchGroupMembersForNewlyCreatedPasswordGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPasswordGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Assert.assertNotNull(methodName + " GroupMember List not null check : " , groupMembers);
                AssertHelper.assertTrue(methodName + " GroupMember limit Check : " , groupMembers.size() <= 30);
                AssertHelper.assertTrue(methodName + " GroupMember size Check : " , groupMembers.size() == 1);
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
    public void d4_createPasswordGroupWithAlreadyTakenGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(TestPreferenceHelper.getLastPasswordGroupGUID(),
                CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME,
                CometChatConstants.GROUP_TYPE_PASSWORD,
                CometChatTestConstants.DEFAULT_PASSWORD_GROUP_PASSWORD);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase("ERR_GUID_ALREADY_EXISTS");
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
    }


    // Public groups with icon, description and metadata

    @Test
    public void d5_createPublicGroupWithIconAndDescriptionShouldReturnSUcess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "",
                CometChatTestConstants.DEFAULT_AVATAR_URL,
                CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", group.getIcon().equalsIgnoreCase(createdGroup.getIcon()));
                AssertHelper.assertTrue(methodName + " Group Description Check :", group.getDescription().equalsIgnoreCase(createdGroup.getDescription()));
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
    public void d6_createPublicGroupWithEmptyIconAndEmptyDescriptionShouldReturnSUcess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "",
                CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.EMPTY_DATA);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_ICON) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_DESCRIPTION);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    @Test
    public void d7_createPublicGroupWithIconAndDescriptionAndMetadataShouldReturnSucess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "",
                CometChatTestConstants.DEFAULT_AVATAR_URL,
                CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME);
        group.setMetadata(TestUtils.getTestMetadata());
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", group.getIcon().equalsIgnoreCase(createdGroup.getIcon()));
                AssertHelper.assertTrue(methodName + " Group Description Check :", group.getDescription().equalsIgnoreCase(createdGroup.getDescription()));
                AssertHelper.jsonAssert(methodName + " Group Metadata Check :", group.getMetadata(), createdGroup.getMetadata());
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
    public void d8_createPublicGroupWithIconAndDescriptionAndEmptyMetadataShouldReturnError() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PUBLIC,
                "",
                CometChatTestConstants.DEFAULT_AVATAR_URL,
                CometChatTestConstants.DEFAULT_PUBLIC_GROUP_NAME);
        group.setMetadata(TestUtils.getEmptyJSONObject());
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_METADATA);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    // Private groups with icon, description and metdata

    @Test
    public void d9_createPrivateGroupWithIconAndDescriptionShouldReturnSUcess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PRIVATE,
                "",
                CometChatTestConstants.DEFAULT_AVATAR_URL,
                CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", group.getIcon().equalsIgnoreCase(createdGroup.getIcon()));
                AssertHelper.assertTrue(methodName + " Group Description Check :", group.getDescription().equalsIgnoreCase(createdGroup.getDescription()));
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
    public void e1_createPrivateGroupWithEmptyIconAndEmptyDescriptionShouldReturnSUcess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PRIVATE,
                "",
                CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.EMPTY_DATA);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_ICON) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_DESCRIPTION);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    @Test
    public void e2_createPrivateGroupWithIconAndDescriptionAndMetadataShouldReturnSucess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PRIVATE,
                "",
                CometChatTestConstants.DEFAULT_AVATAR_URL,
                CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME);
        group.setMetadata(TestUtils.getTestMetadata());
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", group.getIcon().equalsIgnoreCase(createdGroup.getIcon()));
                AssertHelper.assertTrue(methodName + " Group Description Check :", group.getDescription().equalsIgnoreCase(createdGroup.getDescription()));
                AssertHelper.jsonAssert(methodName + " Group Metadata Check :", group.getMetadata(), createdGroup.getMetadata());
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
    public void e3_createPrivateGroupWithIconAndDescriptionAndEmptyMetadataShouldReturnError() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PRIVATE,
                "",
                CometChatTestConstants.DEFAULT_AVATAR_URL,
                CometChatTestConstants.DEFAULT_PRIVATE_GROUP_NAME);
        group.setMetadata(TestUtils.getEmptyJSONObject());
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_METADATA);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    //Pasword Group with icon, description, metadata


    @Test
    public void e4_createPasswordGroupWithIconAndDescriptionShouldReturnSUcess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PASSWORD,
                CometChatTestConstants.DEFAULT_PASSWORD_GROUP_PASSWORD,
                CometChatTestConstants.DEFAULT_AVATAR_URL,
                CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", group.getIcon().equalsIgnoreCase(createdGroup.getIcon()));
                AssertHelper.assertTrue(methodName + " Group Description Check :", group.getDescription().equalsIgnoreCase(createdGroup.getDescription()));
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
    public void e5_createPasswordGroupWithEmptyIconAndEmptyDescriptionShouldReturnSUcess() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PASSWORD,
                CometChatTestConstants.DEFAULT_PASSWORD_GROUP_PASSWORD,
                CometChatTestConstants.EMPTY_DATA,
                CometChatTestConstants.EMPTY_DATA);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_ICON) ||
                        e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_DESCRIPTION);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }


    @Test
    public void e6_createPasswordGroupWithIconAndDescriptionAndMetadataShouldReturnSucess() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PASSWORD,
                CometChatTestConstants.DEFAULT_PASSWORD_GROUP_PASSWORD,
                CometChatTestConstants.DEFAULT_AVATAR_URL,
                CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME);
        group.setMetadata(TestUtils.getTestMetadata());
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                Assert.assertNotNull(methodName + " Group Null Check :", createdGroup);
                AssertHelper.assertTrue(methodName + " Group Guid Check :", group.getGuid().equalsIgnoreCase(createdGroup.getGuid()));
                AssertHelper.assertTrue(methodName + " Group Name Check :", group.getName().equalsIgnoreCase(createdGroup.getName()));
                AssertHelper.assertTrue(methodName + " Group Type Check :", group.getGroupType().equalsIgnoreCase(createdGroup.getGroupType()));
                AssertHelper.assertTrue(methodName + " Group Icon Check :", group.getIcon().equalsIgnoreCase(createdGroup.getIcon()));
                AssertHelper.assertTrue(methodName + " Group Description Check :", group.getDescription().equalsIgnoreCase(createdGroup.getDescription()));
                AssertHelper.jsonAssert(methodName + " Group Metadata Check :", group.getMetadata(), createdGroup.getMetadata());
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
    public void e7_createPasswordGroupWithIconAndDescriptionAndEmptyMetadataShouldReturnError() throws InterruptedException, JSONException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Group group = new Group(String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_GUID, System.currentTimeMillis()),
                String.format(CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME, System.currentTimeMillis()),
                CometChatConstants.GROUP_TYPE_PASSWORD,
                CometChatTestConstants.DEFAULT_PASSWORD_GROUP_PASSWORD,
                CometChatTestConstants.DEFAULT_AVATAR_URL,
                CometChatTestConstants.DEFAULT_PASSWORD_GROUP_NAME);
        group.setMetadata(TestUtils.getEmptyJSONObject());
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group createdGroup) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_METADATA);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

}
