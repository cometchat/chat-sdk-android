package com.cometchat.chat.messaging.fetch;

import android.util.Log;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.MessagesRequest;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.users.blockunblock.BlockUserTest;
import com.cometchat.chat.users.blockunblock.UnblockUserTest;
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
public class FetchMessageTest {

    private static final int TEST_CASE_TIMEOUT = 5;
    private static final int VALID_LIMIT = 30;
    private static final int VALID_FILTER_LIMIT = 25;

    @Test
    public void a1_fetchMessagesForUserWithInvalidLimitShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setLimit(120)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
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
    public void a2_fetchMessagesForGroupWithInvalidLimitShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(120)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
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
    public void a3_fetchMessagesForUserWithZeroLimitShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setLimit(0)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
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
    public void a4_fetchMessagesForGroupWithZeroLimitShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(0)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
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
    public void a5_fetchMessagesForUserWithNegativeLimitShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setLimit(-30)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
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
    public void a6_fetchMessagesForGroupWithNegativeLimitShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(-30)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
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
    public void a7_fetchMessagesForUserWithInvalidUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.INVALID_UID)
                .setLimit(VALID_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
                Log.e(methodName, messages.size() + " ");
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
    public void a8_fetchMessagesForGroupWithInvalidGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.INVALID_DATA)
                .setLimit(VALID_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_GUID_NOT_FOUND);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a9_fetchMessagesForUserWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.EMPTY_DATA)
                .setLimit(VALID_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
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
    public void b1_fetchMessagesForGroupWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.EMPTY_DATA)
                .setLimit(VALID_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messages) {
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
    public void b2_fetchMessagesForUser() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setLimit(VALID_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", (baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Message Sender check : ", (baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                }
                final long lastMessageId = receivedMessages.get(0).getId();
                final long lastTimeStamp = receivedMessages.get(0).getSentAt();
                TestPreferenceHelper.saveDefaultMessageIdForUser(lastMessageId);
                TestPreferenceHelper.saveDefaultTimestampForUser(lastTimeStamp);
                messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
                    @Override
                    public void onSuccess(List<BaseMessage> receivedMessages1) {
                        Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages1);
                        AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages1.size() <= VALID_LIMIT);
                        String validateMessageList = TestUtils.validateMessageList(receivedMessages1);
                        AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                        for (BaseMessage baseMessage : receivedMessages1) {
                            AssertHelper.assertTrue(methodName + " Message Receiver check : ", (baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                                    || baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                            AssertHelper.assertTrue(methodName + " Message Sender check : ", (baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                                    || baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                            AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getId() < lastMessageId);
                            AssertHelper.assertTrue(methodName + " Timestamp check : " , baseMessage.getSentAt() <= lastTimeStamp);
                        }
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        AssertHelper.fail(methodName + " " + e.getMessage());
                        countDownLatch.countDown();
                    }
                });
            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(2 * TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b3_fetchMessagesForGroup() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(VALID_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                }
                final long lastMessageId = receivedMessages.get(0).getId();
                final long lastTimeStamp = receivedMessages.get(0).getSentAt();
                TestPreferenceHelper.saveDefaultMessageIdForGroup(lastMessageId);
                TestPreferenceHelper.saveDefaultTimestampForGroup(lastTimeStamp);
                messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
                    @Override
                    public void onSuccess(List<BaseMessage> receivedMessages1) {
                        Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages1);
                        AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages1.size() <= VALID_LIMIT);
                        String validateMessageList = TestUtils.validateMessageList(receivedMessages1);
                        AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                        for (BaseMessage baseMessage : receivedMessages1) {
                            AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                            AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getId() < lastMessageId);
                            AssertHelper.assertTrue(methodName + " Timestamp check : " , baseMessage.getSentAt() <= lastTimeStamp);
                        }
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        AssertHelper.fail(methodName + " " + e.getMessage());
                        countDownLatch.countDown();
                    }
                });

            }

            @Override
            public void onError(CometChatException e) {
                AssertHelper.fail(methodName + " " + e.getMessage());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(2 * TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b4_fetchMessagesForUserBeforeMessageId() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setMessageId(TestPreferenceHelper.getDefaultMessageIdForUser())
                .setLimit(VALID_FILTER_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", (baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Message Sender check : ", (baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getId() < TestPreferenceHelper.getDefaultMessageIdForUser());
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
    public void b5_fetchMessagesForUserAfterMessageId() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setMessageId(TestPreferenceHelper.getDefaultMessageIdForUser())
                .setLimit(VALID_FILTER_LIMIT)
                .build();
        messagesRequest.fetchNext(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", (baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Message Sender check : ", (baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getId() > TestPreferenceHelper.getDefaultMessageIdForUser());
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
    public void b5_fetchMessagesForGroupBeforeMessageId() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setMessageId(TestPreferenceHelper.getDefaultMessageIdForUser())
                .setLimit(VALID_FILTER_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ",baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                    AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getId() < TestPreferenceHelper.getDefaultMessageIdForUser());
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
    public void b6_fetchMessagesForGroupAfterMessageId() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setMessageId(TestPreferenceHelper.getDefaultMessageIdForGroup())
                .setLimit(VALID_FILTER_LIMIT)
                .build();
        messagesRequest.fetchNext(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                    AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getId() > TestPreferenceHelper.getDefaultMessageIdForGroup());
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
    public void b7_fetchMessagesForUserBeforeTimestamp() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setTimestamp(TestPreferenceHelper.getDefaultTimestampForUser())
                .setLimit(VALID_FILTER_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", (baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Message Sender check : ", (baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getSentAt() <= TestPreferenceHelper.getDefaultTimestampForUser());
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
    public void b8_fetchMessagesForUserAfterTimestamp() throws InterruptedException{
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setTimestamp(TestPreferenceHelper.getDefaultTimestampForUser())
                .setLimit(VALID_FILTER_LIMIT)
                .build();
        messagesRequest.fetchNext(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", (baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Message Sender check : ", (baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getSender().getUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getSentAt() >= TestPreferenceHelper.getDefaultTimestampForUser());
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
    public void b9_fetchMessagesForGroupBeforeTimestamp() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setTimestamp(TestPreferenceHelper.getDefaultTimestampForGroup())
                .setLimit(VALID_FILTER_LIMIT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                    AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getSentAt() <= TestPreferenceHelper.getDefaultTimestampForGroup());
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
    public void c1_fetchMessagesForGroupAfterTimestamp() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setTimestamp(TestPreferenceHelper.getDefaultTimestampForGroup())
                .setLimit(VALID_FILTER_LIMIT)
                .build();
        messagesRequest.fetchNext(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                    AssertHelper.assertTrue(methodName + " Message ID check : " , baseMessage.getSentAt() >= TestPreferenceHelper.getDefaultTimestampForGroup());
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
    public void c2_runBlockedUsersTest(){
        TestUtils.runTestForClass(BlockUserTest.class);
    }

    @Test
    public void c3_fetchMessagesForGroupWithHideMessagesFromBlockedUsersTrue() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(VALID_FILTER_LIMIT)
                .hideMessagesFromBlockedUsers(true)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                    AssertHelper.assertTrue(methodName + " Hide Messages from Blocked users check : " , baseMessage.getId() != TestPreferenceHelper.getLastMessageIdFromBlockedUserInGroup());
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
    public void   c4_runUnblockUsersTest(){
        TestUtils.runTestForClass(UnblockUserTest.class);
    }

    @Test
    public void c5_fetchMessagesForGroupWithUnreadTrue() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(VALID_FILTER_LIMIT)
                .setUnread(true)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                System.out.println("group received Messages size : " + receivedMessages.size());
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
//                AssertHelper.assertTrue(methodName + " UnreadMessage count check : " , receivedMessages.size() == 1);
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
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
    public void c6_fetchMessagesForUserWithUnreadTrue() throws InterruptedException {
        //TODO : Check this
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setLimit(VALID_FILTER_LIMIT)
                .setUnread(true)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                System.out.println("user received Messages size : " + receivedMessages.size());
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
//                AssertHelper.assertTrue(methodName + " UnreadMessage count check : " , receivedMessages.size() == 2);
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Unread Message ReadAt check :" , baseMessage.getReadAt() == 0);
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
    public void c7_fetchMessagesForUserWithValidSearchKeyWordTrue() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setLimit(VALID_FILTER_LIMIT)
                .setSearchKeyword(CometChatTestConstants.DEFAULT_MESSAGE_TEXT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                System.out.println("user received Messages size : " + receivedMessages.size());
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Search Keyword check in messageID : " + baseMessage.getId() , ((TextMessage)baseMessage).getText().contains(CometChatTestConstants.DEFAULT_MESSAGE_TEXT));
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
    public void c8_fetchMessagesForGroupWithValidSearchKeyWordTrue() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(VALID_FILTER_LIMIT)
                .setSearchKeyword(CometChatTestConstants.DEFAULT_MESSAGE_TEXT)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                System.out.println("user received Messages size : " + receivedMessages.size());
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Search Keyword check in messageID : " + baseMessage.getId() , ((TextMessage)baseMessage).getText().contains(CometChatTestConstants.DEFAULT_MESSAGE_TEXT));
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
    public void c9_fetchMessagesForUserWithInvalidCategoryShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setLimit(VALID_FILTER_LIMIT)
                .setCategory(CometChatTestConstants.INVALID_DATA)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CATEGORY);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void d1_fetchMessagesForGroupWithInvalidCategoryShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(VALID_FILTER_LIMIT)
                .setCategory(CometChatTestConstants.INVALID_DATA)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_CATEGORY);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void d2_fetchMessagesForUserWithValidCategory() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setLimit(VALID_FILTER_LIMIT)
                .setCategory(CometChatConstants.CATEGORY_CUSTOM)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_FILTER_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", (baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Category Check for message id : " + baseMessage.getId(), baseMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM));
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
    public void d3_fetchMessagesForGroupWithValidCategory() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(VALID_FILTER_LIMIT)
                .setCategory(CometChatConstants.CATEGORY_CUSTOM)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_FILTER_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                    AssertHelper.assertTrue(methodName + " Category Check for message id : " + baseMessage.getId(), baseMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM));
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
    public void d4_fetchMessagesForUserWithValidType() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setUID(CometChatTestConstants.RECEIVER_UID_1)
                .setLimit(VALID_FILTER_LIMIT)
                .setCategory(CometChatConstants.CATEGORY_CUSTOM)
                .setType("custom")
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_FILTER_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", (baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.LOGIN_UID)
                            || baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_UID_1)));
                    AssertHelper.assertTrue(methodName + " Type Check for message id : " + baseMessage.getId(), baseMessage.getType().equalsIgnoreCase("custom"));
                    AssertHelper.assertTrue(methodName + " Category Check for message id : " + baseMessage.getId(), baseMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM));
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
    public void d5_fetchMessagesForGroupWithValidType() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(VALID_FILTER_LIMIT)
                .setCategory(CometChatConstants.CATEGORY_CUSTOM)
                .setType("custom")
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_FILTER_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                    AssertHelper.assertTrue(methodName + " Category Check for message id : " + baseMessage.getId(), baseMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM));
                    AssertHelper.assertTrue(methodName + " Type Check for message id : " + baseMessage.getId(), baseMessage.getType().equalsIgnoreCase("custom"));
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
    public void d6_fetchMessagesForUserWithValidMultipleCategories() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> categories = new ArrayList<>();
        categories.add("message");
        categories.add("custom");
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(VALID_FILTER_LIMIT)
                .setCategories(categories)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_FILTER_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                    AssertHelper.assertTrue(methodName + " Category Check for message id : " + baseMessage.getId(), baseMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_MESSAGE) || baseMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM));
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
    public void d6_fetchMessagesForUserWithValidMultipleTypes() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> types = new ArrayList<>();
        types.add("TEST_CUSTOM");
        types.add("text");
        final MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(CometChatTestConstants.RECEIVER_GUID)
                .setLimit(VALID_FILTER_LIMIT)
                .setTypes(types)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> receivedMessages) {
                Assert.assertNotNull(methodName + " Received Messsage List Null Check", receivedMessages);
                AssertHelper.assertTrue(methodName + " Received Messsage List Limit Check", receivedMessages.size() <= VALID_FILTER_LIMIT);
                String validateMessageList = TestUtils.validateMessageList(receivedMessages);
                AssertHelper.assertTrue(methodName + " Validate Message List : " + validateMessageList, validateMessageList.equalsIgnoreCase(""));
                for (BaseMessage baseMessage : receivedMessages) {
                    AssertHelper.assertTrue(methodName + " Message Receiver check : ", baseMessage.getReceiverUid().equalsIgnoreCase(CometChatTestConstants.RECEIVER_GUID));
                    AssertHelper.assertTrue(methodName + " Type Check for message id : " + baseMessage.getId(), baseMessage.getType().equalsIgnoreCase("TEST_CUSTOM") || baseMessage.getType().equalsIgnoreCase("text"));
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
