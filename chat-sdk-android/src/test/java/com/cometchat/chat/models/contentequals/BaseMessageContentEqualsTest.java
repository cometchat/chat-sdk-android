package com.cometchat.chat.models.contentequals;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.ReactionCount;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for BaseMessage contentEquals() method.
 * 
 * Since BaseMessage is abstract, we use TextMessage (a concrete subclass) for testing.
 * Tests verify:
 * - Clone equivalence: message.contentEquals(message.clone()) == true
 * - Field modification detection for key fields
 * - Inheritance field coverage (AppEntity parent class)
 * 
 * Feature: content-equals-models
 * Validates: Requirements 3.3, 4.4, 5.3, 7.1, 7.2, 7.3
 */
public class BaseMessageContentEqualsTest {

    /**
     * Creates a fully populated TextMessage for testing BaseMessage fields.
     * Note: We avoid using JSONObject directly since it's not mocked in unit tests.
     */
    private TextMessage createFullyPopulatedMessage() {
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
        
        // Set mentionedUsers list
        List<User> mentionedUsers = new ArrayList<User>();
        mentionedUsers.add(createTestUser("mentioned1", "Mentioned User 1"));
        message.setMentionedUsers(mentionedUsers);
        
        // Set reactions list
        List<ReactionCount> reactions = new ArrayList<ReactionCount>();
        ReactionCount reaction = new ReactionCount();
        reaction.setReaction("thumbsup");
        reaction.setCount(3);
        reaction.setReactedByMe(true);
        reactions.add(reaction);
        message.setReactions(reactions);
        
        // Note: We skip setting metadata and rawMessage since JSONObject is not mocked
        
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
    public void testCloneEquivalence_FullyPopulatedMessage_ShouldBeEqual() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage cloned = original.clone();
        
        assertTrue("Message should be content-equal to its clone", original.contentEquals(cloned));
        assertTrue("Clone should be content-equal to original", cloned.contentEquals(original));
    }

    @Test
    public void testCloneEquivalence_MinimalMessage_ShouldBeEqual() {
        TextMessage original = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        TextMessage cloned = original.clone();
        
        assertTrue("Minimal message should be content-equal to its clone", original.contentEquals(cloned));
    }

    @Test
    public void testCloneEquivalence_MessageWithNullFields_ShouldBeEqual() {
        TextMessage original = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        original.setSender(null);
        original.setReceiver(null);
        original.setMetadata(null);
        original.setMentionedUsers(null);
        original.setReactions(null);
        
        TextMessage cloned = original.clone();
        assertTrue("Message with null fields should be content-equal to its clone", original.contentEquals(cloned));
    }

    @Test
    public void testCloneEquivalence_MessageWithGroupReceiver_ShouldBeEqual() {
        TextMessage original = new TextMessage("group123", "Hello Group", CometChatConstants.RECEIVER_TYPE_GROUP);
        original.setReceiver(createTestGroup("group123", "Test Group"));
        
        TextMessage cloned = original.clone();
        assertTrue("Message with group receiver should be content-equal to its clone", original.contentEquals(cloned));
    }

    // ==================== Field Modification Detection Tests ====================

    @Test
    public void testFieldModification_Id_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setId(99999L);
        
        assertFalse("Modifying id should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Muid_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setMuid("different-muid");
        
        assertFalse("Modifying muid should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_MuidToNull_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setMuid(null);
        
        assertFalse("Setting muid to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Sender_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setSender(createTestUser("different_sender", "Different Sender"));
        
        assertFalse("Modifying sender should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_SenderToNull_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setSender(null);
        
        assertFalse("Setting sender to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Receiver_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setReceiver(createTestUser("different_receiver", "Different Receiver"));
        
        assertFalse("Modifying receiver should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ReceiverToNull_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setReceiver(null);
        
        assertFalse("Setting receiver to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Type_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setType(CometChatConstants.MESSAGE_TYPE_IMAGE);
        
        assertFalse("Modifying type should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ReceiverType_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setReceiverType(CometChatConstants.RECEIVER_TYPE_GROUP);
        
        assertFalse("Modifying receiverType should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Category_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setCategory(CometChatConstants.CATEGORY_ACTION);
        
        assertFalse("Modifying category should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_SentAt_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setSentAt(9999999L);
        
        assertFalse("Modifying sentAt should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_MentionedUsers_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        List<User> differentMentionedUsers = new ArrayList<User>();
        differentMentionedUsers.add(createTestUser("different_mentioned", "Different Mentioned User"));
        modified.setMentionedUsers(differentMentionedUsers);
        
        assertFalse("Modifying mentionedUsers should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_MentionedUsersToNull_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setMentionedUsers(null);
        
        assertFalse("Setting mentionedUsers to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Reactions_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        List<ReactionCount> differentReactions = new ArrayList<ReactionCount>();
        ReactionCount reaction = new ReactionCount();
        reaction.setReaction("heart");
        reaction.setCount(10);
        differentReactions.add(reaction);
        modified.setReactions(differentReactions);
        
        assertFalse("Modifying reactions should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ReactionsToNull_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setReactions(null);
        
        assertFalse("Setting reactions to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_DeliveredAt_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setDeliveredAt(9999999L);
        
        assertFalse("Modifying deliveredAt should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ReadAt_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setReadAt(9999999L);
        
        assertFalse("Modifying readAt should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ConversationId_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setConversationId("different-conv-id");
        
        assertFalse("Modifying conversationId should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ReplyCount_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setReplyCount(999);
        
        assertFalse("Modifying replyCount should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_HasMentionedMe_ShouldReturnFalse() {
        TextMessage original = createFullyPopulatedMessage();
        TextMessage modified = original.clone();
        modified.setHasMentionedMe(true);
        
        assertFalse("Modifying hasMentionedMe should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_MetadataBothNull_ShouldBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setMetadata(null);
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setMetadata(null);
        
        assertTrue("Two messages with null metadata should be content-equal", message1.contentEquals(message2));
    }

    // ==================== Inheritance Field Coverage Tests ====================

    @Test
    public void testInheritance_AppEntityContentEqualsIsCalled() {
        TextMessage message1 = createFullyPopulatedMessage();
        TextMessage message2 = createFullyPopulatedMessage();
        
        assertTrue("Two messages with same content should be content-equal", message1.contentEquals(message2));
    }

    @Test
    public void testInheritance_TypeCheckFromAppEntity() {
        TextMessage message = createFullyPopulatedMessage();
        User user = createTestUser("user123", "Test User");
        
        assertFalse("Message should not be content-equal to a User", message.contentEquals(user));
    }

    @Test
    public void testInheritance_NullCheckFromAppEntity() {
        TextMessage message = createFullyPopulatedMessage();
        
        assertFalse("Message should not be content-equal to null", message.contentEquals(null));
    }

    @Test
    public void testInheritance_SameReferenceFromAppEntity() {
        TextMessage message = createFullyPopulatedMessage();
        
        assertTrue("Message should be content-equal to itself", message.contentEquals(message));
    }

    // ==================== Additional Edge Case Tests ====================

    @Test
    public void testContentEquals_BothMessagesWithNullOptionalFields_ShouldBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setMuid(null);
        message1.setSender(null);
        message1.setReceiver(null);
        message1.setMetadata(null);
        message1.setMentionedUsers(null);
        message1.setReactions(null);
        
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setMuid(null);
        message2.setSender(null);
        message2.setReceiver(null);
        message2.setMetadata(null);
        message2.setMentionedUsers(null);
        message2.setReactions(null);
        
        assertTrue("Two messages with all null optional fields should be content-equal", message1.contentEquals(message2));
    }

    @Test
    public void testContentEquals_EmptyListsVsNullLists_ShouldNotBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setMentionedUsers(new ArrayList<User>());
        
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setMentionedUsers(null);
        
        assertFalse("Empty mentionedUsers list should not be content-equal to null", message1.contentEquals(message2));
    }

    @Test
    public void testContentEquals_BothEmptyLists_ShouldBeEqual() {
        TextMessage message1 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message1.setMentionedUsers(new ArrayList<User>());
        message1.setReactions(new ArrayList<ReactionCount>());
        
        TextMessage message2 = new TextMessage("receiver123", "Hello", CometChatConstants.RECEIVER_TYPE_USER);
        message2.setMentionedUsers(new ArrayList<User>());
        message2.setReactions(new ArrayList<ReactionCount>());
        
        assertTrue("Two messages with empty lists should be content-equal", message1.contentEquals(message2));
    }

    @Test
    public void testContentEquals_DifferentListSizes_ShouldNotBeEqual() {
        TextMessage message1 = createFullyPopulatedMessage();
        TextMessage message2 = createFullyPopulatedMessage();
        
        List<User> moreMentionedUsers = new ArrayList<User>(message2.getMentionedUsers());
        moreMentionedUsers.add(createTestUser("extra_user", "Extra User"));
        message2.setMentionedUsers(moreMentionedUsers);
        
        assertFalse("Messages with different list sizes should not be content-equal", message1.contentEquals(message2));
    }

    @Test
    public void testSymmetry_IdenticalMessages_ShouldBeSymmetric() {
        TextMessage message1 = createFullyPopulatedMessage();
        TextMessage message2 = createFullyPopulatedMessage();
        
        boolean result1to2 = message1.contentEquals(message2);
        boolean result2to1 = message2.contentEquals(message1);
        
        assertTrue("message1.contentEquals(message2) should be true", result1to2);
        assertTrue("message2.contentEquals(message1) should be true", result2to1);
    }

    @Test
    public void testSymmetry_DifferentMessages_ShouldBeSymmetric() {
        TextMessage message1 = createFullyPopulatedMessage();
        TextMessage message2 = createFullyPopulatedMessage();
        message2.setId(99999L);
        
        boolean result1to2 = message1.contentEquals(message2);
        boolean result2to1 = message2.contentEquals(message1);
        
        assertFalse("message1.contentEquals(message2) should be false", result1to2);
        assertFalse("message2.contentEquals(message1) should be false", result2to1);
    }
}
