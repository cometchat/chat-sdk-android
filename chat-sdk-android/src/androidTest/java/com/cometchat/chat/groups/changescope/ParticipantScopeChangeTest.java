package com.cometchat.chat.groups.changescope;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.utils.AssertHelper;
import com.cometchat.chat.utils.CometChatTestConstants;
import com.cometchat.chat.utils.TestPreferenceHelper;
import com.cometchat.chat.utils.TestUtils;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParticipantScopeChangeTest {

    private final int TEST_CASE_TIMEOUT = 5;

    // Tests with Participant RECEIVER_UID_4

    @Test
    public void a1_changeScopeWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.EMPTY_DATA, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
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
    public void a2_changeScopeWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.EMPTY_DATA,
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
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
    public void a3_changeScopeWithInvalidUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.INVALID_DATA, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
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
    public void a4_changeScopeWithInvalidGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.INVALID_DATA,
                CometChatConstants.SCOPE_MODERATOR, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
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
    public void a5_changeScopeWithEmptyScopeShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_EMPTY_SCOPE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a6_changeScopeWithInvalidScopeShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_INVALID_SCOPE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a7_changeScopeFromParticipantToAdminShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(TestPreferenceHelper.getLastCreatedUserUId(), TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_ADMIN, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_SCOPE_CLEARANCE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a8_changeScopeFromModeratorToAdminShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_ADMIN, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_SCOPE_CLEARANCE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void a9_changeScopeFromModeratorToParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_2, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_PARTICIPANT, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_SCOPE_CLEARANCE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b1_changeScopeFromAdminToParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.updateGroupMemberScope(CometChatTestConstants.RECEIVER_UID_1, TestPreferenceHelper.getLastPublicGroupGUID(),
                CometChatConstants.SCOPE_PARTICIPANT, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        AssertHelper.fail(methodName + " must not trigger the success block");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_SCOPE_CLEARANCE);
                        AssertHelper.assertTrue(methodName, condition);
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    // Kick Members as participant

    @Test
    public void b2_kickGroupMemberByParticipantWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.EMPTY_DATA, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void b3_kickGroupMemberByParticipantWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void b4_kickGroupMemberByParticipantWithInvalidUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.INVALID_UID, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void b5_kickGroupMemberByParticipantWithInvalidGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void b6_kickAdminByParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.RECEIVER_UID_1, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b7_kickModeratorByParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(CometChatTestConstants.RECEIVER_UID_2, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void b8_kickParticipantByParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.kickGroupMember(TestPreferenceHelper.getLastCreatedUserUId(), TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    // ban members by participant


    @Test
    public void b9_banGroupMemberByParticipantWithEmptyUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.EMPTY_DATA, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void c1_banGroupMemberByParticipantWithEmptyGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.EMPTY_DATA, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void c2_banGroupMemberByParticipantWithInvalidUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.INVALID_UID, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void c3_banGroupMemberByParticipantWithInvalidGUIDShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.RECEIVER_UID_2, CometChatTestConstants.INVALID_DATA, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
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
    public void c4_banAdminByParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.RECEIVER_UID_1, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c5_banModeratorByParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(CometChatTestConstants.RECEIVER_UID_2, TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }

    @Test
    public void c6_banParticipantByParticipantShouldReturnError() throws InterruptedException {
        final String methodName = TestUtils.getMethodName();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CometChat.banGroupMember(TestPreferenceHelper.getLastCreatedUserUId(), TestPreferenceHelper.getLastPublicGroupGUID(), new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                AssertHelper.fail(methodName + " must not trigger the success block");
                countDownLatch.countDown();
            }

            @Override
            public void onError(CometChatException e) {
                boolean condition = e.getCode().equalsIgnoreCase(CometChatTestConstants.APIErrors.API_ERR_GROUP_NO_CLEARANCE);
                AssertHelper.assertTrue(methodName, condition);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(TEST_CASE_TIMEOUT, TimeUnit.SECONDS);
        if (countDownLatch.getCount() == 1)
            AssertHelper.fail(methodName + ": Timeout");
    }
}
