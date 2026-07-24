package com.cometchat.chat.messaging.mentions;


import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.MessagesRequest;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestUtils;

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
public class MentionsTest {

    private static final int TEST_CASE_TIMEOUT = 8;

    //User with listMentionedUser()
    @Test
    public void a1_sendTextMessageWithSingleMentionUIDShouldReturnSingleMentionedUserList() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(
                CometChatTestConstants.RECEIVER_UID_1,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, "<@uid:superhero2>"),
                CometChatConstants.RECEIVER_TYPE_USER
        );
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                if (textMessage.getMentionedUsers().isEmpty()){
                    AssertHelper.fail(methodName + "list must not be empty");
                }else{
                    AssertHelper.assertTrue(methodName, true);
                }
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a2_sendTextMessageWithMultipleMentionUIDShouldReturnMultipleUserList() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(
                CometChatTestConstants.CREATE_USER_UID_WITH_TAG,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, "<@uid:superhero1> and <@uid:superhero_tag>"),
                CometChatConstants.RECEIVER_TYPE_USER
        );
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                if (textMessage.getMentionedUsers().isEmpty()){
                    AssertHelper.fail(methodName + "list must not be empty");
                }else{
                    AssertHelper.assertTrue(methodName, true);
                }
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    //Group with listMentionedUser()
    @Test
    public void b1_sendTextMessageInGroupWithMultipleMentionUIDShouldReturnMultipleUserList() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(
                CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, "<@uid:superhero_tag> <@uid:superhero2> <@uid:superhero3>"),
                CometChatConstants.RECEIVER_TYPE_GROUP
        );
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                if (textMessage.getMentionedUsers().isEmpty()){
                    AssertHelper.fail(methodName + "list must not be empty");
                }else if (textMessage.getMentionedUsers().size() == 3) {
                    AssertHelper.assertTrue(methodName, true);
                }else {
                    AssertHelper.fail(methodName + "mentioned users list count must be 3");
                }
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b2_sendTextMessageInGroupWithTwoMentionUIDShouldReturnTwoUserList() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TextMessage textMessage = new TextMessage(
                CometChatTestConstants.RECEIVER_GUID,
                String.format(CometChatTestConstants.DEFAULT_MESSAGE_TEXT, "<@uid:superhero1> and <@uid:superhero_tag>"),
                CometChatConstants.RECEIVER_TYPE_GROUP
        );
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                if (textMessage.getMentionedUsers().isEmpty()){
                    AssertHelper.fail(methodName + "list must not be empty");
                }else if (textMessage.getMentionedUsers().size() == 2) {
                    AssertHelper.assertTrue(methodName, true);
                }else {
                    AssertHelper.fail(methodName + "mentioned users list count must be 2");
                }
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + e);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    //Mentioned with user tag
    @Test
    public void c1_fetchMentionMessagesWithUserTagsShouldReturnUsersListWithTag() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.CREATE_USER_UID_WITH_TAG)
                .setLimit(30)
                .mentionsWithTagInfo(true)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
                boolean userHasTag = false;
                for (BaseMessage messageObj: messages){
                    for (User user: messageObj.getMentionedUsers()){
                        if (user.getTags() != null){
                            userHasTag = true;
                            break;
                        }
                    }
                }
                if (!userHasTag){
                    AssertHelper.fail(methodName + " must not be false");
                }else{
                    AssertHelper.assertTrue(methodName, true);
                }
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
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

    //Mentioned with blocked
    @Test
    public void c2_fetchMentionMessagesWithBlockedRelationShouldReturnUsersWithBlockedKey() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String > uids = new ArrayList<>();
        uids.add(CometChatTestConstants.CREATE_USER_UID_WITH_TAG);
        CometChat.blockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> resultMap) {
                MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                        .setGUID(CometChatTestConstants.RECEIVER_GUID)
                        .setLimit(30)
                        .mentionsWithBlockedInfo(true)
                        .build();
                messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
                    @Override
                    public void onSuccess(List<BaseMessage> messages) {
                        boolean blocKeyFound = false;
                        for (BaseMessage messageObj: messages){
                            for (User user: messageObj.getMentionedUsers()){
                                if (user.isBlockedByMe() || user.isHasBlockedMe()){
                                    blocKeyFound = true;
                                    break;
                                }
                            }
                        }
                        if (!blocKeyFound){
                            AssertHelper.fail(methodName + " must blocked key must be found");
                        }else{
                            AssertHelper.assertTrue(methodName, true);
                        }
                        countDownLatch.countDown();
                    }
                    @Override
                    public void onError(CometChatException e) {
                        AssertHelper.fail(methodName + e);
                        countDownLatch.countDown();
                    }
                });
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " Unable to block UID: " + CometChatTestConstants.CREATE_USER_UID_WITH_TAG + " because " + e.getMessage());
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1) AssertHelper.fail(methodName + ": Timeout");
    }

}
