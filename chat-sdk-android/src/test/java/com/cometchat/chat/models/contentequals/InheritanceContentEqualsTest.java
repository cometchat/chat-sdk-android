package com.cometchat.chat.models.contentequals;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.enums.ModerationStatus;
import com.cometchat.chat.models.AIAssistantContentReceivedEvent;
import com.cometchat.chat.models.AIAssistantToolStartedEvent;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for inheritance behavior in contentEquals() method.
 * 
 * Tests verify that modifying parent class fields affects subclass contentEquals:
 * 1. TextMessage: modifying BaseMessage fields should make contentEquals return false
 * 2. MediaMessage: modifying BaseMessage fields should make contentEquals return false
 * 3. GroupMember: modifying User fields should make contentEquals return false
 * 4. CurrentUser: modifying User fields should make contentEquals return false
 * 5. AI event subclasses: modifying AIAssistantBaseEvent fields should make contentEquals return false
 * 
 * Note: CustomMessage tests are excluded because CustomMessage uses JSONObject which
 * is not available in unit tests without Android framework mocking.
 * 
 * Feature: content-equals-models
 * Validates: Requirements 7.1, 7.2, 7.3
 */
public class InheritanceContentEqualsTest {

    // ==================== Helper Methods ====================

    /**
     * Creates a test User with basic fields populated.
     */
    private User createTestUser(String uid, String name) {
        User user = new User(uid, name);
        user.setAvatar("https://example.com/avatar.png");
        user.setStatus(CometChatConstants.USER_STATUS_ONLINE);
        return user;
    }

    /**
     * Creates a fully populated TextMessage for testing.
     */
    private TextMessage createFullyPopulatedTextMessage() {
        TextMessage message = new TextMessage("receiver123", "Hello World", CometChatConstants.RECEIVER_TYPE_USER);
        
        // Set BaseMessage fields
        message.setId(12345L);
        message.setMuid("muid-abc-123");
        message.setSender(createTestUser("sender123", "Sender User"));
        message.setReceiver(createTestUser("receiver123", "Receiver User"));
        message.setReceiverUid("receiver123");
        message.setType(CometChatConstants.MESSAGE_TYPE_TEXT);
        message.setReceiverType(CometChatConstants.RECEIVER_TYPE_USER);
        message.setCategory(CometChatConstants.CATEGORY_MESSAGE);
        message.setSentAt(1000000L);
        message.setDeliveredAt(1000001L);
        message.setReadAt(1000002L);
        message.setReadByMeAt(1000003L);
        message.setDeliveredToMeAt(1000004L);
        message.setDeletedAt(0L);
        message.setEditedAt(0L);
        message.setUpdatedAt(1000005L);
        message.setConversationId("conv-123");
        message.setParentMessageId(0L);
        message.setReplyCount(5);
        message.setHasMentionedMe(false);
        message.setUnreadRepliesCount(2);
        
        // Set TextMessage-specific fields
        message.setTags(new ArrayList<String>(Arrays.asList("tag1", "tag2")));
        message.setModerationStatus(ModerationStatus.APPROVED);
        
        return message;
    }

    /**
     * Creates a fully populated MediaMessage for testing.
     */
    private MediaMessage createFullyPopulatedMediaMessage() {
        MediaMessage message = new MediaMessage("receiver123", CometChatConstants.MESSAGE_TYPE_IMAGE, CometChatConstants.RECEIVER_TYPE_USER);
        
        // Set BaseMessage fields
        message.setId(12345L);
        message.setMuid("muid-media-123");
        message.setSender(createTestUser("sender123", "Sender User"));
        message.setReceiver(createTestUser("receiver123", "Receiver User"));
        message.setReceiverUid("receiver123");
        message.setType(CometChatConstants.MESSAGE_TYPE_IMAGE);
        message.setReceiverType(CometChatConstants.RECEIVER_TYPE_USER);
        message.setCategory(CometChatConstants.CATEGORY_MESSAGE);
        message.setSentAt(1000000L);
        message.setDeliveredAt(1000001L);
        message.setReadAt(1000002L);
        message.setReadByMeAt(1000003L);
        message.setDeliveredToMeAt(1000004L);
        message.setDeletedAt(0L);
        message.setEditedAt(0L);
        message.setUpdatedAt(1000005L);
        message.setConversationId("conv-123");
        message.setParentMessageId(0L);
        message.setReplyCount(3);
        message.setHasMentionedMe(false);
        message.setUnreadRepliesCount(1);
        
        // Set MediaMessage-specific fields
        message.setCaption("Test caption");
        message.setTags(new ArrayList<String>(Arrays.asList("media-tag1", "media-tag2")));
        message.setModerationStatus(ModerationStatus.APPROVED);
        
        return message;
    }

    /**
     * Creates a fully populated GroupMember for testing.
     */
    private GroupMember createFullyPopulatedGroupMember() {
        GroupMember member = new GroupMember("member123", CometChatConstants.SCOPE_PARTICIPANT);
        
        // Set User fields (inherited)
        member.setName("Test Member");
        member.setAvatar("https://example.com/member_avatar.png");
        member.setLink("https://example.com/member_profile");
        member.setRole("member_role");
        member.setStatus(CometChatConstants.USER_STATUS_ONLINE);
        member.setStatusMessage("Available");
        member.setLastActiveAt(1234567890L);
        member.setHasBlockedMe(false);
        member.setBlockedByMe(false);
        member.setTags(new ArrayList<String>(Arrays.asList("member-tag1", "member-tag2")));
        member.setDeactivatedAt(0L);
        
        // Set GroupMember-specific fields
        member.setScope(CometChatConstants.SCOPE_PARTICIPANT);
        member.setJoinedAt(1234567800L);
        
        return member;
    }

    /**
     * Creates a fully populated CurrentUser for testing.
     */
    private CurrentUser createFullyPopulatedCurrentUser() {
        CurrentUser user = new CurrentUser();
        
        // Set User fields (inherited)
        user.setUid("current123");
        user.setName("Current User");
        user.setAvatar("https://example.com/current_avatar.png");
        user.setLink("https://example.com/current_profile");
        user.setRole("admin");
        user.setStatus(CometChatConstants.USER_STATUS_ONLINE);
        user.setStatusMessage("Active");
        user.setLastActiveAt(1234567890L);
        user.setHasBlockedMe(false);
        user.setBlockedByMe(false);
        user.setTags(new ArrayList<String>(Arrays.asList("current-tag1", "current-tag2")));
        user.setDeactivatedAt(0L);
        
        // Set CurrentUser-specific fields
        user.setAuthToken("auth_token_123");
        user.setIdentity("identity_123");
        user.setSecret("secret_123");
        user.setJwt("jwt_token_123");
        user.setFat("fat_token_123");
        
        return user;
    }

    /**
     * Creates a fully populated AIAssistantContentReceivedEvent for testing.
     * Note: We don't set additionalProperties to avoid JSONObject issues in unit tests.
     */
    private AIAssistantContentReceivedEvent createContentReceivedEvent(long id, String type, String conversationId, String parentId,
            String streamMessageId, long runId, String threadId, String delta) {
        AIAssistantContentReceivedEvent event = new AIAssistantContentReceivedEvent();
        event.setId(id);
        event.setType(type);
        event.setConversationId(conversationId);
        event.setParentId(parentId);
        event.setStreamMessageId(streamMessageId);
        event.setRunId(runId);
        event.setThreadId(threadId);
        event.setDelta(delta);
        return event;
    }

    /**
     * Creates a fully populated AIAssistantToolStartedEvent for testing.
     * Note: We don't set additionalProperties to avoid JSONObject issues in unit tests.
     */
    private AIAssistantToolStartedEvent createToolStartedEvent(long id, String type, String conversationId, String parentId,
            String streamParentMessageId, long runId, String threadId, String toolCallId, String toolCallName,
            String displayName, String executionText, String arguments) {
        AIAssistantToolStartedEvent event = new AIAssistantToolStartedEvent();
        event.setId(id);
        event.setType(type);
        event.setConversationId(conversationId);
        event.setParentId(parentId);
        event.setStreamParentMessageId(streamParentMessageId);
        event.setRunId(runId);
        event.setThreadId(threadId);
        event.setToolCallId(toolCallId);
        event.setToolCallName(toolCallName);
        event.setDisplayName(displayName);
        event.setExecutionText(executionText);
        event.setArguments(arguments);
        return event;
    }

    // ==================== TextMessage Inheritance Tests (BaseMessage fields) ====================

    @Test
    public void testTextMessage_ModifyingBaseMessageId_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setId(99999L);
        
        assertFalse("Modifying BaseMessage id should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testTextMessage_ModifyingBaseMessageMuid_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setMuid("different-muid");
        
        assertFalse("Modifying BaseMessage muid should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testTextMessage_ModifyingBaseMessageSender_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setSender(createTestUser("different_sender", "Different Sender"));
        
        assertFalse("Modifying BaseMessage sender should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testTextMessage_ModifyingBaseMessageReceiver_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setReceiver(createTestUser("different_receiver", "Different Receiver"));
        
        assertFalse("Modifying BaseMessage receiver should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testTextMessage_ModifyingBaseMessageSentAt_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setSentAt(9999999L);
        
        assertFalse("Modifying BaseMessage sentAt should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testTextMessage_ModifyingBaseMessageConversationId_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setConversationId("different-conv-id");
        
        assertFalse("Modifying BaseMessage conversationId should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testTextMessage_ModifyingBaseMessageReplyCount_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setReplyCount(999);
        
        assertFalse("Modifying BaseMessage replyCount should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testTextMessage_ModifyingBaseMessageCategory_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setCategory(CometChatConstants.CATEGORY_ACTION);
        
        assertFalse("Modifying BaseMessage category should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testTextMessage_ModifyingBaseMessageReceiverType_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setReceiverType(CometChatConstants.RECEIVER_TYPE_GROUP);
        
        assertFalse("Modifying BaseMessage receiverType should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testTextMessage_ModifyingBaseMessageHasMentionedMe_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setHasMentionedMe(true);
        
        assertFalse("Modifying BaseMessage hasMentionedMe should make TextMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    // ==================== MediaMessage Inheritance Tests (BaseMessage fields) ====================

    @Test
    public void testMediaMessage_ModifyingBaseMessageId_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setId(99999L);
        
        assertFalse("Modifying BaseMessage id should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testMediaMessage_ModifyingBaseMessageMuid_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setMuid("different-muid");
        
        assertFalse("Modifying BaseMessage muid should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testMediaMessage_ModifyingBaseMessageSender_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setSender(createTestUser("different_sender", "Different Sender"));
        
        assertFalse("Modifying BaseMessage sender should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testMediaMessage_ModifyingBaseMessageReceiver_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setReceiver(createTestUser("different_receiver", "Different Receiver"));
        
        assertFalse("Modifying BaseMessage receiver should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testMediaMessage_ModifyingBaseMessageSentAt_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setSentAt(9999999L);
        
        assertFalse("Modifying BaseMessage sentAt should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testMediaMessage_ModifyingBaseMessageConversationId_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setConversationId("different-conv-id");
        
        assertFalse("Modifying BaseMessage conversationId should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testMediaMessage_ModifyingBaseMessageReplyCount_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setReplyCount(999);
        
        assertFalse("Modifying BaseMessage replyCount should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testMediaMessage_ModifyingBaseMessageCategory_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setCategory(CometChatConstants.CATEGORY_ACTION);
        
        assertFalse("Modifying BaseMessage category should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testMediaMessage_ModifyingBaseMessageReceiverType_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setReceiverType(CometChatConstants.RECEIVER_TYPE_GROUP);
        
        assertFalse("Modifying BaseMessage receiverType should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testMediaMessage_ModifyingBaseMessageHasMentionedMe_ShouldReturnFalse() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage modified = original.clone();
        modified.setHasMentionedMe(true);
        
        assertFalse("Modifying BaseMessage hasMentionedMe should make MediaMessage contentEquals return false", 
                original.contentEquals(modified));
    }

    // ==================== GroupMember Inheritance Tests (User fields) ====================

    @Test
    public void testGroupMember_ModifyingUserUid_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setUid("different_uid");
        
        assertFalse("Modifying User uid should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserName_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setName("Different Name");
        
        assertFalse("Modifying User name should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserAvatar_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setAvatar("https://example.com/different_avatar.png");
        
        assertFalse("Modifying User avatar should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserLink_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setLink("https://example.com/different_profile");
        
        assertFalse("Modifying User link should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserRole_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setRole("different_role");
        
        assertFalse("Modifying User role should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserStatus_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setStatus(CometChatConstants.USER_STATUS_OFFLINE);
        
        assertFalse("Modifying User status should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserStatusMessage_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setStatusMessage("Busy");
        
        assertFalse("Modifying User statusMessage should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserLastActiveAt_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setLastActiveAt(9999999999L);
        
        assertFalse("Modifying User lastActiveAt should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserHasBlockedMe_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setHasBlockedMe(true);
        
        assertFalse("Modifying User hasBlockedMe should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserBlockedByMe_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setBlockedByMe(true);
        
        assertFalse("Modifying User blockedByMe should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserTags_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setTags(new ArrayList<String>(Arrays.asList("different-tag")));
        
        assertFalse("Modifying User tags should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testGroupMember_ModifyingUserDeactivatedAt_ShouldReturnFalse() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember modified = original.clone();
        modified.setDeactivatedAt(1234567890L);
        
        assertFalse("Modifying User deactivatedAt should make GroupMember contentEquals return false", 
                original.contentEquals(modified));
    }

    // ==================== CurrentUser Inheritance Tests (User fields) ====================

    @Test
    public void testCurrentUser_ModifyingUserUid_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setUid("different_uid");
        
        assertFalse("Modifying User uid should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserName_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setName("Different Name");
        
        assertFalse("Modifying User name should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserAvatar_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setAvatar("https://example.com/different_avatar.png");
        
        assertFalse("Modifying User avatar should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserLink_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setLink("https://example.com/different_profile");
        
        assertFalse("Modifying User link should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserRole_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setRole("different_role");
        
        assertFalse("Modifying User role should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserStatus_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setStatus(CometChatConstants.USER_STATUS_OFFLINE);
        
        assertFalse("Modifying User status should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserStatusMessage_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setStatusMessage("Busy");
        
        assertFalse("Modifying User statusMessage should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserLastActiveAt_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setLastActiveAt(9999999999L);
        
        assertFalse("Modifying User lastActiveAt should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserHasBlockedMe_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setHasBlockedMe(true);
        
        assertFalse("Modifying User hasBlockedMe should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserBlockedByMe_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setBlockedByMe(true);
        
        assertFalse("Modifying User blockedByMe should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserTags_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setTags(new ArrayList<String>(Arrays.asList("different-tag")));
        
        assertFalse("Modifying User tags should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testCurrentUser_ModifyingUserDeactivatedAt_ShouldReturnFalse() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser modified = original.clone();
        modified.setDeactivatedAt(1234567890L);
        
        assertFalse("Modifying User deactivatedAt should make CurrentUser contentEquals return false", 
                original.contentEquals(modified));
    }

    // ==================== AIAssistantContentReceivedEvent Inheritance Tests (AIAssistantBaseEvent fields) ====================
    // Note: These tests create two separate instances instead of using clone() to avoid JSONObject issues

    @Test
    public void testAIContentReceivedEvent_ModifyingBaseEventId_ShouldReturnFalse() {
        AIAssistantContentReceivedEvent original = createContentReceivedEvent(
                12345L, "text_message_content", "conv-ai-123", "parent-123",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        AIAssistantContentReceivedEvent modified = createContentReceivedEvent(
                99999L, "text_message_content", "conv-ai-123", "parent-123",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        
        assertFalse("Modifying AIAssistantBaseEvent id should make AIAssistantContentReceivedEvent contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testAIContentReceivedEvent_ModifyingBaseEventType_ShouldReturnFalse() {
        AIAssistantContentReceivedEvent original = createContentReceivedEvent(
                12345L, "text_message_content", "conv-ai-123", "parent-123",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        AIAssistantContentReceivedEvent modified = createContentReceivedEvent(
                12345L, "different_type", "conv-ai-123", "parent-123",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        
        assertFalse("Modifying AIAssistantBaseEvent type should make AIAssistantContentReceivedEvent contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testAIContentReceivedEvent_ModifyingBaseEventConversationId_ShouldReturnFalse() {
        AIAssistantContentReceivedEvent original = createContentReceivedEvent(
                12345L, "text_message_content", "conv-ai-123", "parent-123",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        AIAssistantContentReceivedEvent modified = createContentReceivedEvent(
                12345L, "text_message_content", "different-conv-id", "parent-123",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        
        assertFalse("Modifying AIAssistantBaseEvent conversationId should make AIAssistantContentReceivedEvent contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testAIContentReceivedEvent_ModifyingBaseEventParentId_ShouldReturnFalse() {
        AIAssistantContentReceivedEvent original = createContentReceivedEvent(
                12345L, "text_message_content", "conv-ai-123", "parent-123",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        AIAssistantContentReceivedEvent modified = createContentReceivedEvent(
                12345L, "text_message_content", "conv-ai-123", "different-parent-id",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        
        assertFalse("Modifying AIAssistantBaseEvent parentId should make AIAssistantContentReceivedEvent contentEquals return false", 
                original.contentEquals(modified));
    }

    // ==================== AIAssistantToolStartedEvent Inheritance Tests (AIAssistantBaseEvent fields) ====================
    // Note: These tests create two separate instances instead of using clone() to avoid JSONObject issues

    @Test
    public void testAIToolStartedEvent_ModifyingBaseEventId_ShouldReturnFalse() {
        AIAssistantToolStartedEvent original = createToolStartedEvent(
                12345L, "tool_call_started", "conv-ai-123", "parent-123",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        AIAssistantToolStartedEvent modified = createToolStartedEvent(
                99999L, "tool_call_started", "conv-ai-123", "parent-123",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        
        assertFalse("Modifying AIAssistantBaseEvent id should make AIAssistantToolStartedEvent contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testAIToolStartedEvent_ModifyingBaseEventType_ShouldReturnFalse() {
        AIAssistantToolStartedEvent original = createToolStartedEvent(
                12345L, "tool_call_started", "conv-ai-123", "parent-123",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        AIAssistantToolStartedEvent modified = createToolStartedEvent(
                12345L, "different_type", "conv-ai-123", "parent-123",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        
        assertFalse("Modifying AIAssistantBaseEvent type should make AIAssistantToolStartedEvent contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testAIToolStartedEvent_ModifyingBaseEventConversationId_ShouldReturnFalse() {
        AIAssistantToolStartedEvent original = createToolStartedEvent(
                12345L, "tool_call_started", "conv-ai-123", "parent-123",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        AIAssistantToolStartedEvent modified = createToolStartedEvent(
                12345L, "tool_call_started", "different-conv-id", "parent-123",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        
        assertFalse("Modifying AIAssistantBaseEvent conversationId should make AIAssistantToolStartedEvent contentEquals return false", 
                original.contentEquals(modified));
    }

    @Test
    public void testAIToolStartedEvent_ModifyingBaseEventParentId_ShouldReturnFalse() {
        AIAssistantToolStartedEvent original = createToolStartedEvent(
                12345L, "tool_call_started", "conv-ai-123", "parent-123",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        AIAssistantToolStartedEvent modified = createToolStartedEvent(
                12345L, "tool_call_started", "conv-ai-123", "different-parent-id",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        
        assertFalse("Modifying AIAssistantBaseEvent parentId should make AIAssistantToolStartedEvent contentEquals return false", 
                original.contentEquals(modified));
    }

    // ==================== Clone Equivalence Tests (Verify inheritance works correctly) ====================

    @Test
    public void testTextMessage_CloneEquivalence_ShouldBeEqual() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage cloned = original.clone();
        
        assertTrue("TextMessage should be content-equal to its clone (inheritance working)", 
                original.contentEquals(cloned));
        assertTrue("Clone should be content-equal to original (symmetry)", 
                cloned.contentEquals(original));
    }

    @Test
    public void testMediaMessage_CloneEquivalence_ShouldBeEqual() {
        MediaMessage original = createFullyPopulatedMediaMessage();
        MediaMessage cloned = original.clone();
        
        assertTrue("MediaMessage should be content-equal to its clone (inheritance working)", 
                original.contentEquals(cloned));
        assertTrue("Clone should be content-equal to original (symmetry)", 
                cloned.contentEquals(original));
    }

    @Test
    public void testGroupMember_CloneEquivalence_ShouldBeEqual() {
        GroupMember original = createFullyPopulatedGroupMember();
        GroupMember cloned = original.clone();
        
        assertTrue("GroupMember should be content-equal to its clone (inheritance working)", 
                original.contentEquals(cloned));
        assertTrue("Clone should be content-equal to original (symmetry)", 
                cloned.contentEquals(original));
    }

    @Test
    public void testCurrentUser_CloneEquivalence_ShouldBeEqual() {
        CurrentUser original = createFullyPopulatedCurrentUser();
        CurrentUser cloned = original.clone();
        
        assertTrue("CurrentUser should be content-equal to its clone (inheritance working)", 
                original.contentEquals(cloned));
        assertTrue("Clone should be content-equal to original (symmetry)", 
                cloned.contentEquals(original));
    }

    // Note: AI event identical instance tests are excluded because AIAssistantBaseEvent
    // uses JSONObject for additionalProperties which is not available in unit tests
    // without Android framework mocking. The inheritance tests above verify that
    // modifying parent fields correctly affects contentEquals, which is the key
    // requirement being tested.

    // ==================== Symmetry Tests for Inheritance ====================

    @Test
    public void testTextMessage_Symmetry_WhenBaseMessageFieldModified() {
        TextMessage message1 = createFullyPopulatedTextMessage();
        TextMessage message2 = createFullyPopulatedTextMessage();
        message2.setId(99999L);
        
        boolean result1to2 = message1.contentEquals(message2);
        boolean result2to1 = message2.contentEquals(message1);
        
        assertFalse("message1.contentEquals(message2) should be false", result1to2);
        assertFalse("message2.contentEquals(message1) should be false", result2to1);
    }

    @Test
    public void testMediaMessage_Symmetry_WhenBaseMessageFieldModified() {
        MediaMessage message1 = createFullyPopulatedMediaMessage();
        MediaMessage message2 = createFullyPopulatedMediaMessage();
        message2.setMuid("different-muid");
        
        boolean result1to2 = message1.contentEquals(message2);
        boolean result2to1 = message2.contentEquals(message1);
        
        assertFalse("message1.contentEquals(message2) should be false", result1to2);
        assertFalse("message2.contentEquals(message1) should be false", result2to1);
    }

    @Test
    public void testGroupMember_Symmetry_WhenUserFieldModified() {
        GroupMember member1 = createFullyPopulatedGroupMember();
        GroupMember member2 = createFullyPopulatedGroupMember();
        member2.setName("Different Name");
        
        boolean result1to2 = member1.contentEquals(member2);
        boolean result2to1 = member2.contentEquals(member1);
        
        assertFalse("member1.contentEquals(member2) should be false", result1to2);
        assertFalse("member2.contentEquals(member1) should be false", result2to1);
    }

    @Test
    public void testCurrentUser_Symmetry_WhenUserFieldModified() {
        CurrentUser user1 = createFullyPopulatedCurrentUser();
        CurrentUser user2 = createFullyPopulatedCurrentUser();
        user2.setAvatar("https://example.com/different_avatar.png");
        
        boolean result1to2 = user1.contentEquals(user2);
        boolean result2to1 = user2.contentEquals(user1);
        
        assertFalse("user1.contentEquals(user2) should be false", result1to2);
        assertFalse("user2.contentEquals(user1) should be false", result2to1);
    }

    @Test
    public void testAIContentReceivedEvent_Symmetry_WhenBaseEventFieldModified() {
        AIAssistantContentReceivedEvent event1 = createContentReceivedEvent(
                12345L, "text_message_content", "conv-ai-123", "parent-123",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        AIAssistantContentReceivedEvent event2 = createContentReceivedEvent(
                12345L, "text_message_content", "different-conv-id", "parent-123",
                "stream-msg-123", 67890L, "thread-123", "Hello, this is delta content");
        
        boolean result1to2 = event1.contentEquals(event2);
        boolean result2to1 = event2.contentEquals(event1);
        
        assertFalse("event1.contentEquals(event2) should be false", result1to2);
        assertFalse("event2.contentEquals(event1) should be false", result2to1);
    }

    @Test
    public void testAIToolStartedEvent_Symmetry_WhenBaseEventFieldModified() {
        AIAssistantToolStartedEvent event1 = createToolStartedEvent(
                12345L, "tool_call_started", "conv-ai-123", "parent-123",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        AIAssistantToolStartedEvent event2 = createToolStartedEvent(
                12345L, "different_type", "conv-ai-123", "parent-123",
                "stream-parent-123", 67890L, "thread-123", "tool-call-123",
                "search_tool", "Search Tool", "Searching for information...", "{\"query\": \"test\"}");
        
        boolean result1to2 = event1.contentEquals(event2);
        boolean result2to1 = event2.contentEquals(event1);
        
        assertFalse("event1.contentEquals(event2) should be false", result1to2);
        assertFalse("event2.contentEquals(event1) should be false", result2to1);
    }
}
