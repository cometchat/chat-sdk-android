package com.cometchat.chat.models.contentequals;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.enums.ModerationStatus;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.ReactionCount;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for TextMessage contentEquals() method.
 * 
 * Tests verify:
 * - Clone equivalence: textMessage.contentEquals(textMessage.clone()) == true
 * - Field modification detection for TextMessage-specific fields (text, tags, moderationStatus)
 * - Inheritance field coverage - modifying BaseMessage fields should affect TextMessage contentEquals
 * - Symmetry tests
 * 
 * Feature: content-equals-models
 * Validates: Requirements 2.4, 4.4, 6.3, 7.1
 */
public class TextMessageContentEqualsTest {

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
        message.setDeletedBy(null);
        message.setEditedBy(null);
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
     * Creates a test User with basic fields populated.
     */
    private User createTestUser(String uid, String name) {
        User user = new User(uid, name);
        user.setAvatar("https://example.com/avatar.png");
        user.setStatus(CometChatConstants.USER_STATUS_ONLINE);
        return user;
    }

    /**
     * Creates a test Group for receiver testing.
     */
    private Group createTestGroup(String guid, String name) {
        return new Group(guid, name, CometChatConstants.GROUP_TYPE_PUBLIC, null);
    }

    // ==================== Clone Equivalence Tests ====================

    @Test
    public void testCloneEquivalence_FullyPopulatedTextMessage_ShouldBeEqual() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage cloned = original.clone();
        
        assertTrue("TextMessage should be content-equal to its clone", original.contentEquals(cloned));
        assertTrue("Clone should be content-equal to original", cloned.contentEquals(original));
    }

    @Test
    public void testCloneEquivalence_MinimalTextMessage_ShouldBeEqual() {
        TextMessage original = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        TextMessage cloned = original.clone();
        
        assertTrue("Minimal TextMessage should be content-equal to its clone", original.contentEquals(cloned));
    }

    @Test
    public void testCloneEquivalence_TextMessageWithNullFields_ShouldBeEqual() {
        TextMessage original = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        original.setTags(null);
        original.setModerationStatus(null);
        original.setSender(null);
        original.setReceiver(null);
        
        TextMessage cloned = original.clone();
        assertTrue("TextMessage with null fields should be content-equal to its clone", original.contentEquals(cloned));
    }

    @Test
    public void testCloneEquivalence_TextMessageWithEmptyText_ShouldBeEqual() {
        TextMessage original = new TextMessage("receiver123", "", CometChatConstants.RECEIVER_TYPE_USER);
        TextMessage cloned = original.clone();
        
        assertTrue("TextMessage with empty text should be content-equal to its clone", original.contentEquals(cloned));
    }

    @Test
    public void testCloneEquivalence_TextMessageWithGroupReceiver_ShouldBeEqual() {
        TextMessage original = new TextMessage("group123", "Hello Group", CometChatConstants.RECEIVER_TYPE_GROUP);
        original.setReceiver(createTestGroup("group123", "Test Group"));
        original.setTags(new ArrayList<String>(Arrays.asList("group-tag")));
        original.setModerationStatus(ModerationStatus.PENDING);
        
        TextMessage cloned = original.clone();
        assertTrue("TextMessage with group receiver should be content-equal to its clone", original.contentEquals(cloned));
    }

    // ==================== TextMessage-Specific Field Modification Tests ====================

    @Test
    public void testFieldModification_Text_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setText("Different text");
        
        assertFalse("Modifying text should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_TextToNull_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setText(null);
        
        assertFalse("Setting text to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_TextFromNull_ShouldReturnFalse() {
        TextMessage original = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        original.setText(null);
        TextMessage modified = original.clone();
        modified.setText("Some text");
        
        assertFalse("Setting text from null to value should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Tags_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setTags(new ArrayList<String>(Arrays.asList("different_tag1", "different_tag2")));
        
        assertFalse("Modifying tags should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_TagsToNull_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setTags(null);
        
        assertFalse("Setting tags to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_TagsAddElement_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        List<String> modifiedTags = new ArrayList<String>(modified.getTags());
        modifiedTags.add("new_tag");
        modified.setTags(modifiedTags);
        
        assertFalse("Adding a tag should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_TagsRemoveElement_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        List<String> modifiedTags = new ArrayList<String>(modified.getTags());
        modifiedTags.remove(0);
        modified.setTags(modifiedTags);
        
        assertFalse("Removing a tag should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ModerationStatus_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setModerationStatus(ModerationStatus.DISAPPROVED);
        
        assertFalse("Modifying moderationStatus should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ModerationStatusToNull_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setModerationStatus(null);
        
        assertFalse("Setting moderationStatus to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ModerationStatusFromNull_ShouldReturnFalse() {
        TextMessage original = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        original.setModerationStatus(null);
        TextMessage modified = original.clone();
        modified.setModerationStatus(ModerationStatus.PENDING);
        
        assertFalse("Setting moderationStatus from null to value should make contentEquals return false", original.contentEquals(modified));
    }

    // ==================== Inheritance Field Coverage Tests (BaseMessage fields) ====================

    @Test
    public void testInheritance_ModifyingId_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setId(99999L);
        
        assertFalse("Modifying BaseMessage id should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testInheritance_ModifyingMuid_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setMuid("different-muid");
        
        assertFalse("Modifying BaseMessage muid should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testInheritance_ModifyingSender_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setSender(createTestUser("different_sender", "Different Sender"));
        
        assertFalse("Modifying BaseMessage sender should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testInheritance_ModifyingReceiver_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setReceiver(createTestUser("different_receiver", "Different Receiver"));
        
        assertFalse("Modifying BaseMessage receiver should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testInheritance_ModifyingSentAt_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setSentAt(9999999L);
        
        assertFalse("Modifying BaseMessage sentAt should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testInheritance_ModifyingConversationId_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setConversationId("different-conv-id");
        
        assertFalse("Modifying BaseMessage conversationId should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testInheritance_ModifyingReplyCount_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setReplyCount(999);
        
        assertFalse("Modifying BaseMessage replyCount should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testInheritance_ModifyingCategory_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setCategory(CometChatConstants.CATEGORY_ACTION);
        
        assertFalse("Modifying BaseMessage category should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testInheritance_ModifyingReceiverType_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setReceiverType(CometChatConstants.RECEIVER_TYPE_GROUP);
        
        assertFalse("Modifying BaseMessage receiverType should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testInheritance_ModifyingHasMentionedMe_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedTextMessage();
        TextMessage modified = original.clone();
        modified.setHasMentionedMe(true);
        
        assertFalse("Modifying BaseMessage hasMentionedMe should make TextMessage contentEquals return false", original.contentEquals(modified));
    }

    // ==================== Symmetry Tests ====================

    @Test
    public void testSymmetry_IdenticalTextMessages_ShouldBeSymmetric() {
        TextMessage message1 = createFullyPopulatedTextMessage();
        TextMessage message2 = createFullyPopulatedTextMessage();
        
        boolean result1to2 = message1.contentEquals(message2);
        boolean result2to1 = message2.contentEquals(message1);
        
        assertTrue("message1.contentEquals(message2) should be true", result1to2);
        assertTrue("message2.contentEquals(message1) should be true", result2to1);
    }

    @Test
    public void testSymmetry_DifferentTextMessages_ShouldBeSymmetric() {
        TextMessage message1 = createFullyPopulatedTextMessage();
        TextMessage message2 = createFullyPopulatedTextMessage();
        message2.setText("Different text");
        
        boolean result1to2 = message1.contentEquals(message2);
        boolean result2to1 = message2.contentEquals(message1);
        
        assertFalse("message1.contentEquals(message2) should be false", result1to2);
        assertFalse("message2.contentEquals(message1) should be false", result2to1);
    }

    @Test
    public void testSymmetry_MinimalTextMessages_ShouldBeSymmetric() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        
        boolean result1to2 = message1.contentEquals(message2);
        boolean result2to1 = message2.contentEquals(message1);
        
        assertTrue("message1.contentEquals(message2) should be true", result1to2);
        assertTrue("message2.contentEquals(message1) should be true", result2to1);
    }

    // ==================== Null Field Handling Tests ====================

    @Test
    public void testContentEquals_BothTextMessagesWithNullOptionalFields_ShouldBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setTags(null);
        message1.setModerationStatus(null);
        message1.setSender(null);
        message1.setReceiver(null);
        message1.setMuid(null);
        
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setTags(null);
        message2.setModerationStatus(null);
        message2.setSender(null);
        message2.setReceiver(null);
        message2.setMuid(null);
        
        assertTrue("Two TextMessages with all null optional fields should be content-equal", message1.contentEquals(message2));
    }

    @Test
    public void testContentEquals_EmptyTagsVsNullTags_ShouldNotBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setTags(new ArrayList<String>());
        
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setTags(null);
        
        assertFalse("Empty tags list should not be content-equal to null tags", message1.contentEquals(message2));
    }

    @Test
    public void testContentEquals_BothEmptyTags_ShouldBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setTags(new ArrayList<String>());
        
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setTags(new ArrayList<String>());
        
        assertTrue("Two TextMessages with empty tags lists should be content-equal", message1.contentEquals(message2));
    }

    @Test
    public void testContentEquals_BothNullText_ShouldBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setText(null);
        
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setText(null);
        
        assertTrue("Two TextMessages with null text should be content-equal", message1.contentEquals(message2));
    }

    @Test
    public void testContentEquals_BothNullModerationStatus_ShouldBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setModerationStatus(null);
        
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setModerationStatus(null);
        
        assertTrue("Two TextMessages with null moderationStatus should be content-equal", message1.contentEquals(message2));
    }

    // ==================== ModerationStatus Enum Tests ====================

    @Test
    public void testModerationStatus_AllValues_ShouldBeDistinct() {
        TextMessage original = createFullyPopulatedTextMessage();
        original.setModerationStatus(ModerationStatus.UNMODERATED);
        
        TextMessage pending = original.clone();
        pending.setModerationStatus(ModerationStatus.PENDING);
        
        TextMessage approved = original.clone();
        approved.setModerationStatus(ModerationStatus.APPROVED);
        
        TextMessage disapproved = original.clone();
        disapproved.setModerationStatus(ModerationStatus.DISAPPROVED);
        
        assertFalse("UNMODERATED should not equal PENDING", original.contentEquals(pending));
        assertFalse("UNMODERATED should not equal APPROVED", original.contentEquals(approved));
        assertFalse("UNMODERATED should not equal DISAPPROVED", original.contentEquals(disapproved));
        assertFalse("PENDING should not equal APPROVED", pending.contentEquals(approved));
        assertFalse("PENDING should not equal DISAPPROVED", pending.contentEquals(disapproved));
        assertFalse("APPROVED should not equal DISAPPROVED", approved.contentEquals(disapproved));
    }

    @Test
    public void testModerationStatus_SameValue_ShouldBeEqual() {
        TextMessage message1 = createFullyPopulatedTextMessage();
        message1.setModerationStatus(ModerationStatus.PENDING);
        
        TextMessage message2 = createFullyPopulatedTextMessage();
        message2.setModerationStatus(ModerationStatus.PENDING);
        
        assertTrue("Two TextMessages with same moderationStatus should be content-equal", message1.contentEquals(message2));
    }

    // ==================== Type Safety Tests ====================

    @Test
    public void testTypeSafety_TextMessageVsNull_ShouldReturnFalse() {
        TextMessage message = createFullyPopulatedTextMessage();
        
        assertFalse("TextMessage should not be content-equal to null", message.contentEquals(null));
    }

    @Test
    public void testTypeSafety_TextMessageVsUser_ShouldReturnFalse() {
        TextMessage message = createFullyPopulatedTextMessage();
        User user = createTestUser("user123", "Test User");
        
        assertFalse("TextMessage should not be content-equal to a User", message.contentEquals(user));
    }

    @Test
    public void testTypeSafety_TextMessageVsGroup_ShouldReturnFalse() {
        TextMessage message = createFullyPopulatedTextMessage();
        Group group = createTestGroup("group123", "Test Group");
        
        assertFalse("TextMessage should not be content-equal to a Group", message.contentEquals(group));
    }

    @Test
    public void testTypeSafety_SameReference_ShouldReturnTrue() {
        TextMessage message = createFullyPopulatedTextMessage();
        
        assertTrue("TextMessage should be content-equal to itself", message.contentEquals(message));
    }

    // ==================== Special Character Tests ====================

    @Test
    public void testSpecialCharacters_TextWithUnicode_ShouldBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello 你好 مرحبا 🎉", CometChatConstants.RECEIVER_TYPE_USER);
        TextMessage message2 = new TextMessage("receiver123", "Hello 你好 مرحبا 🎉", CometChatConstants.RECEIVER_TYPE_USER);
        
        assertTrue("TextMessages with same unicode text should be content-equal", message1.contentEquals(message2));
    }

    @Test
    public void testSpecialCharacters_TextWithUnicode_DifferentText_ShouldNotBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello 你好", CometChatConstants.RECEIVER_TYPE_USER);
        TextMessage message2 = new TextMessage("receiver123", "Hello مرحبا", CometChatConstants.RECEIVER_TYPE_USER);
        
        assertFalse("TextMessages with different unicode text should not be content-equal", message1.contentEquals(message2));
    }

    @Test
    public void testSpecialCharacters_TagsWithSpecialChars_ShouldBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setTags(new ArrayList<String>(Arrays.asList("tag-with-dash", "tag_with_underscore", "tag.with.dot")));
        
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setTags(new ArrayList<String>(Arrays.asList("tag-with-dash", "tag_with_underscore", "tag.with.dot")));
        
        assertTrue("TextMessages with same special character tags should be content-equal", message1.contentEquals(message2));
    }
}
