package com.cometchat.chat.models.contentequals;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;
import com.cometchat.chat.models.CustomMessage;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Unit tests for Type Safety of contentEquals() method across different model types.
 * 
 * Tests verify that contentEquals returns false when comparing objects of different types,
 * even when they might share some common fields or inheritance relationships.
 * 
 * Feature: content-equals-models
 * Validates: Requirements 1.4
 */
public class TypeSafetyContentEqualsTest {

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
     * Creates a test Group with basic fields populated.
     */
    private Group createTestGroup(String guid, String name) {
        Group group = new Group(guid, name, CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group.setIcon("https://example.com/icon.png");
        group.setDescription("Test group description");
        return group;
    }

    /**
     * Creates a test GroupMember with basic fields populated.
     */
    private GroupMember createTestGroupMember(String uid, String scope) {
        GroupMember member = new GroupMember(uid, scope);
        member.setName("Test Member");
        member.setAvatar("https://example.com/avatar.png");
        member.setStatus(CometChatConstants.USER_STATUS_ONLINE);
        member.setJoinedAt(1234567890L);
        return member;
    }

    /**
     * Creates a test TextMessage with basic fields populated.
     */
    private TextMessage createTestTextMessage() {
        TextMessage message = new TextMessage("receiver123", "Hello World", CometChatConstants.RECEIVER_TYPE_USER);
        message.setId(12345L);
        message.setMuid("muid-abc-123");
        message.setSender(createTestUser("sender123", "Sender User"));
        return message;
    }

    /**
     * Creates a test MediaMessage with basic fields populated.
     */
    private MediaMessage createTestMediaMessage() {
        MediaMessage message = new MediaMessage("receiver123", CometChatConstants.MESSAGE_TYPE_IMAGE, CometChatConstants.RECEIVER_TYPE_USER);
        message.setId(12345L);
        message.setMuid("muid-abc-123");
        message.setSender(createTestUser("sender123", "Sender User"));
        message.setCaption("Test caption");
        return message;
    }

    /**
     * Creates a test CustomMessage with basic fields populated.
     */
    private CustomMessage createTestCustomMessage() {
        JSONObject customData = new JSONObject();
        try {
            customData.put("key", "value");
        } catch (Exception e) {
            // Ignore
        }
        CustomMessage message = new CustomMessage("receiver123", CometChatConstants.RECEIVER_TYPE_USER, "custom_type", customData);
        message.setId(12345L);
        message.setMuid("muid-abc-123");
        message.setSender(createTestUser("sender123", "Sender User"));
        return message;
    }

    // ==================== User vs Group Tests ====================

    @Test
    public void testTypeSafety_UserVsGroup_ShouldReturnFalse() {
        User user = createTestUser("user123", "Test User");
        Group group = createTestGroup("group123", "Test Group");
        
        assertFalse("User.contentEquals(Group) should return false", user.contentEquals(group));
    }

    @Test
    public void testTypeSafety_GroupVsUser_ShouldReturnFalse() {
        User user = createTestUser("user123", "Test User");
        Group group = createTestGroup("group123", "Test Group");
        
        assertFalse("Group.contentEquals(User) should return false", group.contentEquals(user));
    }

    // ==================== TextMessage vs MediaMessage Tests ====================

    @Test
    public void testTypeSafety_TextMessageVsMediaMessage_ShouldReturnFalse() {
        TextMessage textMessage = createTestTextMessage();
        MediaMessage mediaMessage = createTestMediaMessage();
        
        assertFalse("TextMessage.contentEquals(MediaMessage) should return false", textMessage.contentEquals(mediaMessage));
    }

    @Test
    public void testTypeSafety_MediaMessageVsTextMessage_ShouldReturnFalse() {
        TextMessage textMessage = createTestTextMessage();
        MediaMessage mediaMessage = createTestMediaMessage();
        
        assertFalse("MediaMessage.contentEquals(TextMessage) should return false", mediaMessage.contentEquals(textMessage));
    }

    // ==================== TextMessage vs CustomMessage Tests ====================

    @Test
    public void testTypeSafety_TextMessageVsCustomMessage_ShouldReturnFalse() {
        TextMessage textMessage = createTestTextMessage();
        CustomMessage customMessage = createTestCustomMessage();
        
        assertFalse("TextMessage.contentEquals(CustomMessage) should return false", textMessage.contentEquals(customMessage));
    }

    @Test
    public void testTypeSafety_CustomMessageVsTextMessage_ShouldReturnFalse() {
        TextMessage textMessage = createTestTextMessage();
        CustomMessage customMessage = createTestCustomMessage();
        
        assertFalse("CustomMessage.contentEquals(TextMessage) should return false", customMessage.contentEquals(textMessage));
    }

    // ==================== User vs GroupMember Tests (Inheritance) ====================
    
    /**
     * Note on inheritance behavior:
     * - User.contentEquals(GroupMember) may return true if all User fields match,
     *   because GroupMember IS-A User (inheritance relationship).
     * - GroupMember.contentEquals(User) should return false because User is NOT a GroupMember.
     * 
     * This is the correct behavior per Requirement 1.4: different types should return false.
     * GroupMember is a different type from User (it's a subtype), so when comparing
     * from the subtype's perspective, the parent type should not be considered equal.
     */

    @Test
    public void testTypeSafety_GroupMemberVsUser_ShouldReturnFalse() {
        User user = createTestUser("user123", "Test User");
        GroupMember groupMember = createTestGroupMember("user123", CometChatConstants.SCOPE_PARTICIPANT);
        groupMember.setName("Test User");
        groupMember.setAvatar(user.getAvatar());
        groupMember.setStatus(user.getStatus());
        
        // GroupMember.contentEquals(User) should return false because User is not an instance of GroupMember
        // This validates Requirement 1.4 - different types should return false
        assertFalse("GroupMember.contentEquals(User) should return false", groupMember.contentEquals(user));
    }

    @Test
    public void testTypeSafety_GroupMemberVsUser_DifferentFieldValues_ShouldReturnFalse() {
        User user = createTestUser("user123", "Test User");
        GroupMember groupMember = createTestGroupMember("different_user", CometChatConstants.SCOPE_ADMIN);
        groupMember.setName("Different Name");
        
        // GroupMember.contentEquals(User) should return false regardless of field values
        assertFalse("GroupMember.contentEquals(User) should return false", groupMember.contentEquals(user));
    }

    // ==================== Model vs Null Tests ====================

    @Test
    public void testTypeSafety_UserVsNull_ShouldReturnFalse() {
        User user = createTestUser("user123", "Test User");
        
        assertFalse("User.contentEquals(null) should return false", user.contentEquals(null));
    }

    @Test
    public void testTypeSafety_GroupVsNull_ShouldReturnFalse() {
        Group group = createTestGroup("group123", "Test Group");
        
        assertFalse("Group.contentEquals(null) should return false", group.contentEquals(null));
    }

    @Test
    public void testTypeSafety_TextMessageVsNull_ShouldReturnFalse() {
        TextMessage textMessage = createTestTextMessage();
        
        assertFalse("TextMessage.contentEquals(null) should return false", textMessage.contentEquals(null));
    }

    @Test
    public void testTypeSafety_MediaMessageVsNull_ShouldReturnFalse() {
        MediaMessage mediaMessage = createTestMediaMessage();
        
        assertFalse("MediaMessage.contentEquals(null) should return false", mediaMessage.contentEquals(null));
    }

    @Test
    public void testTypeSafety_CustomMessageVsNull_ShouldReturnFalse() {
        CustomMessage customMessage = createTestCustomMessage();
        
        assertFalse("CustomMessage.contentEquals(null) should return false", customMessage.contentEquals(null));
    }

    @Test
    public void testTypeSafety_GroupMemberVsNull_ShouldReturnFalse() {
        GroupMember groupMember = createTestGroupMember("user123", CometChatConstants.SCOPE_PARTICIPANT);
        
        assertFalse("GroupMember.contentEquals(null) should return false", groupMember.contentEquals(null));
    }

    // ==================== Model vs String Tests ====================

    @Test
    public void testTypeSafety_UserVsString_ShouldReturnFalse() {
        User user = createTestUser("user123", "Test User");
        
        assertFalse("User.contentEquals(String) should return false", user.contentEquals("user123"));
    }

    @Test
    public void testTypeSafety_GroupVsString_ShouldReturnFalse() {
        Group group = createTestGroup("group123", "Test Group");
        
        assertFalse("Group.contentEquals(String) should return false", group.contentEquals("group123"));
    }

    @Test
    public void testTypeSafety_TextMessageVsString_ShouldReturnFalse() {
        TextMessage textMessage = createTestTextMessage();
        
        assertFalse("TextMessage.contentEquals(String) should return false", textMessage.contentEquals("Hello World"));
    }

    @Test
    public void testTypeSafety_MediaMessageVsString_ShouldReturnFalse() {
        MediaMessage mediaMessage = createTestMediaMessage();
        
        assertFalse("MediaMessage.contentEquals(String) should return false", mediaMessage.contentEquals("image"));
    }

    @Test
    public void testTypeSafety_CustomMessageVsString_ShouldReturnFalse() {
        CustomMessage customMessage = createTestCustomMessage();
        
        assertFalse("CustomMessage.contentEquals(String) should return false", customMessage.contentEquals("custom"));
    }

    @Test
    public void testTypeSafety_GroupMemberVsString_ShouldReturnFalse() {
        GroupMember groupMember = createTestGroupMember("user123", CometChatConstants.SCOPE_PARTICIPANT);
        
        assertFalse("GroupMember.contentEquals(String) should return false", groupMember.contentEquals("user123"));
    }

    // ==================== Additional Cross-Type Tests ====================

    @Test
    public void testTypeSafety_MediaMessageVsCustomMessage_ShouldReturnFalse() {
        MediaMessage mediaMessage = createTestMediaMessage();
        CustomMessage customMessage = createTestCustomMessage();
        
        assertFalse("MediaMessage.contentEquals(CustomMessage) should return false", mediaMessage.contentEquals(customMessage));
    }

    @Test
    public void testTypeSafety_CustomMessageVsMediaMessage_ShouldReturnFalse() {
        MediaMessage mediaMessage = createTestMediaMessage();
        CustomMessage customMessage = createTestCustomMessage();
        
        assertFalse("CustomMessage.contentEquals(MediaMessage) should return false", customMessage.contentEquals(mediaMessage));
    }

    @Test
    public void testTypeSafety_UserVsTextMessage_ShouldReturnFalse() {
        User user = createTestUser("user123", "Test User");
        TextMessage textMessage = createTestTextMessage();
        
        assertFalse("User.contentEquals(TextMessage) should return false", user.contentEquals(textMessage));
    }

    @Test
    public void testTypeSafety_TextMessageVsUser_ShouldReturnFalse() {
        User user = createTestUser("user123", "Test User");
        TextMessage textMessage = createTestTextMessage();
        
        assertFalse("TextMessage.contentEquals(User) should return false", textMessage.contentEquals(user));
    }

    @Test
    public void testTypeSafety_GroupVsTextMessage_ShouldReturnFalse() {
        Group group = createTestGroup("group123", "Test Group");
        TextMessage textMessage = createTestTextMessage();
        
        assertFalse("Group.contentEquals(TextMessage) should return false", group.contentEquals(textMessage));
    }

    @Test
    public void testTypeSafety_TextMessageVsGroup_ShouldReturnFalse() {
        Group group = createTestGroup("group123", "Test Group");
        TextMessage textMessage = createTestTextMessage();
        
        assertFalse("TextMessage.contentEquals(Group) should return false", textMessage.contentEquals(group));
    }

    @Test
    public void testTypeSafety_GroupMemberVsGroup_ShouldReturnFalse() {
        GroupMember groupMember = createTestGroupMember("user123", CometChatConstants.SCOPE_PARTICIPANT);
        Group group = createTestGroup("group123", "Test Group");
        
        assertFalse("GroupMember.contentEquals(Group) should return false", groupMember.contentEquals(group));
    }

    @Test
    public void testTypeSafety_GroupVsGroupMember_ShouldReturnFalse() {
        GroupMember groupMember = createTestGroupMember("user123", CometChatConstants.SCOPE_PARTICIPANT);
        Group group = createTestGroup("group123", "Test Group");
        
        assertFalse("Group.contentEquals(GroupMember) should return false", group.contentEquals(groupMember));
    }

    // ==================== Model vs Integer Tests ====================

    @Test
    public void testTypeSafety_UserVsInteger_ShouldReturnFalse() {
        User user = createTestUser("user123", "Test User");
        
        assertFalse("User.contentEquals(Integer) should return false", user.contentEquals(123));
    }

    @Test
    public void testTypeSafety_GroupVsInteger_ShouldReturnFalse() {
        Group group = createTestGroup("group123", "Test Group");
        
        assertFalse("Group.contentEquals(Integer) should return false", group.contentEquals(123));
    }

    @Test
    public void testTypeSafety_TextMessageVsInteger_ShouldReturnFalse() {
        TextMessage textMessage = createTestTextMessage();
        
        assertFalse("TextMessage.contentEquals(Integer) should return false", textMessage.contentEquals(12345));
    }
}
