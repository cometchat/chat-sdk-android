package com.cometchat.chat.groups.banunban;


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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BannedMemberTest {

    private final int TEST_CASE_TIMEOUT = 5;

    @Test
    public void a1_loggedInWithBannedUser() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.login(TestPreferenceHelper.getLastCreatedUserUId(), CometChatTestConstants.VALID_API_KEY
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

    @Test
    public void a2_groupEntryRemovedInConversationsForBannedMemberGroupCheck() throws InterruptedException{
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
                AssertHelper.assertTrue(methodName + " is conversation present after member kicked : " , !isConversationPresent);
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
    public void a3_sendMessageToPublicGroupByBannedMember() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TextMessage textMessage = new TextMessage(TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatTestConstants.DEFAULT_MESSAGE_TEXT,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage receivedTextMessage) {
                AssertHelper.fail(methodName + " must not return success" );
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERROR_NOT_A_MEMBER);
                AssertHelper.assertTrue(methodName , condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a4_fetchMessagesByBannedMemberForGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                AssertHelper.fail(methodName + " must not return success" );
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERROR_NOT_A_MEMBER);
                AssertHelper.assertTrue(methodName , condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a5_fetchGroupMembersByBannedMemberForGroup() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(TestPreferenceHelper.getLastPublicGroupGUID()).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                AssertHelper.fail(methodName + " must not return success" );
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERROR_NOT_A_MEMBER);
                AssertHelper.assertTrue(methodName , condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b6_joinPublicGroup() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.joinGroup(TestPreferenceHelper.getLastPublicGroupGUID(), CometChatConstants.GROUP_TYPE_PUBLIC, "", new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group joinedGroup) {
                AssertHelper.fail(methodName + " must not trigger success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_BANNED_GROUPMEMBER);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }
}
