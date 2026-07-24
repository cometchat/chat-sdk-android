package com.cometchat.chat.models.contentequals;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.ReactionsOptions;
import com.cometchat.chat.enums.RepliesOptions;
import com.cometchat.chat.models.Attachment;
import com.cometchat.chat.models.AIToolCall;
import com.cometchat.chat.models.AIToolCallFunction;
import com.cometchat.chat.models.Conversation;
import com.cometchat.chat.models.FlagDetail;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.OneOnOnePreferences;
import com.cometchat.chat.models.Reaction;
import com.cometchat.chat.models.ReactionCount;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.TypingIndicator;
import com.cometchat.chat.models.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Cross-model symmetry tests for contentEquals() method.
 * 
 * Tests verify the symmetry property across all model classes:
 * - For any two model instances m1 and m2 of the same type, 
 *   m1.contentEquals(m2) SHALL equal m2.contentEquals(m1)
 * 
 * Feature: content-equals-models, Property 4: Symmetry
 * **Validates: Requirements 1.5**
 */
public class SymmetryContentEqualsTest {

    // ==================== Helper Methods ====================

    /**
     * Creates a test User with basic fields populated.
     */
    private User createTestUser(String uid, String name) {
        User user = new User(uid, name);
        user.setAvatar("https://example.com/avatar.png");
        user.setStatus(CometChatConstants.USER_STATUS_ONLINE);
        user.setLastActiveAt(1234567890L);
        return user;
    }

    /**
     * Creates a test Group with basic fields populated.
     */
    private Group createTestGroup(String guid, String name) {
        Group group = new Group(guid, name, CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group.setIcon("https://example.com/icon.png");
        group.setDescription("Test group description");
        group.setMembersCount(10);
        return group;
    }

    /**
     * Creates a test GroupMember with basic fields populated.
     */
    private GroupMember createTestGroupMember(String uid, String scope) {
        GroupMember member = new GroupMember(uid, scope);
        member.setName("Test Member");
        member.setAvatar("https://example.com/avatar.png");
        member.setJoinedAt(1234567890L);
        return member;
    }

    /**
     * Creates a test TextMessage with basic fields populated.
     */
    private TextMessage createTestTextMessage(String text) {
        TextMessage message = new TextMessage("receiver123", text, CometChatConstants.RECEIVER_TYPE_USER);
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
     * Creates a test Attachment with basic fields populated.
     */
    private Attachment createTestAttachment() {
        Attachment attachment = new Attachment();
        attachment.setFileName("test.jpg");
        attachment.setFileExtension("jpg");
        attachment.setFileSize(1024);
        attachment.setFileMimeType("image/jpeg");
        attachment.setFileUrl("https://example.com/test.jpg");
        return attachment;
    }

    /**
     * Creates a test Reaction with basic fields populated.
     */
    private Reaction createTestReaction() {
        Reaction reaction = new Reaction();
        reaction.setReactionId("reaction123");
        reaction.setMessageId(12345L);
        reaction.setReaction("👍");
        reaction.setUid("user123");
        reaction.setReactedAt(1234567890L);
        reaction.setReactedBy(createTestUser("user123", "Test User"));
        return reaction;
    }

    /**
     * Creates a test ReactionCount with basic fields populated.
     */
    private ReactionCount createTestReactionCount() {
        ReactionCount reactionCount = new ReactionCount();
        reactionCount.setReaction("👍");
        reactionCount.setCount(5);
        reactionCount.setReactedByMe(true);
        return reactionCount;
    }

    /**
     * Creates a test FlagDetail with basic fields populated.
     */
    private FlagDetail createTestFlagDetail() {
        return new FlagDetail("reason123", "Test remark");
    }

    /**
     * Creates a test OneOnOnePreferences with basic fields populated.
     */
    private OneOnOnePreferences createTestOneOnOnePreferences() {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setMessagesPreference(MessagesOptions.DONT_SUBSCRIBE);
        prefs.setRepliesPreference(RepliesOptions.SUBSCRIBE_TO_MENTIONS);
        prefs.setReactionsPreference(ReactionsOptions.DONT_SUBSCRIBE);
        return prefs;
    }

    /**
     * Creates a test TypingIndicator with basic fields populated.
     */
    private TypingIndicator createTestTypingIndicator() {
        TypingIndicator indicator = new TypingIndicator("receiver123", CometChatConstants.RECEIVER_TYPE_USER);
        indicator.setSender(createTestUser("sender123", "Sender User"));
        indicator.setLastTimestamp(1234567890L);
        return indicator;
    }

    /**
     * Creates a test Conversation with basic fields populated.
     */
    private Conversation createTestConversation() {
        Conversation conversation = new Conversation("conv123", CometChatConstants.CONVERSATION_TYPE_USER);
        conversation.setUnreadMessageCount(5);
        conversation.setUpdatedAt(1234567890L);
        conversation.setConversationWith(createTestUser("user123", "Test User"));
        conversation.setTags(new ArrayList<>(Arrays.asList("tag1", "tag2")));
        return conversation;
    }

    /**
     * Creates a test AIToolCall with basic fields populated.
     */
    private AIToolCall createTestAIToolCall() {
        AIToolCallFunction function = new AIToolCallFunction();
        function.setName("test_function");
        function.setArguments("{\"key\": \"value\"}");
        return new AIToolCall("toolcall123", "function", function, "Test Tool", "Executing test");
    }

    /**
     * Helper method to verify symmetry property for equal objects.
     * Asserts that a.contentEquals(b) == b.contentEquals(a) == true
     */
    private void assertSymmetricEquality(Object a, Object b, String modelName) {
        boolean aEqualsB = invokeContentEquals(a, b);
        boolean bEqualsA = invokeContentEquals(b, a);
        assertTrue(modelName + ": a.contentEquals(b) should be true", aEqualsB);
        assertTrue(modelName + ": b.contentEquals(a) should be true", bEqualsA);
        assertEquals(modelName + ": Symmetry violated - a.contentEquals(b) != b.contentEquals(a)", aEqualsB, bEqualsA);
    }

    /**
     * Helper method to verify symmetry property for unequal objects.
     * Asserts that a.contentEquals(b) == b.contentEquals(a) == false
     */
    private void assertSymmetricInequality(Object a, Object b, String modelName) {
        boolean aEqualsB = invokeContentEquals(a, b);
        boolean bEqualsA = invokeContentEquals(b, a);
        assertFalse(modelName + ": a.contentEquals(b) should be false", aEqualsB);
        assertFalse(modelName + ": b.contentEquals(a) should be false", bEqualsA);
        assertEquals(modelName + ": Symmetry violated - a.contentEquals(b) != b.contentEquals(a)", aEqualsB, bEqualsA);
    }

    /**
     * Helper method to invoke contentEquals via reflection-free approach.
     * Each model class has its own contentEquals method.
     */
    private boolean invokeContentEquals(Object a, Object b) {
        if (a instanceof User) return ((User) a).contentEquals(b);
        if (a instanceof Group) return ((Group) a).contentEquals(b);
        if (a instanceof GroupMember) return ((GroupMember) a).contentEquals(b);
        if (a instanceof TextMessage) return ((TextMessage) a).contentEquals(b);
        if (a instanceof MediaMessage) return ((MediaMessage) a).contentEquals(b);
        if (a instanceof Attachment) return ((Attachment) a).contentEquals(b);
        if (a instanceof Reaction) return ((Reaction) a).contentEquals(b);
        if (a instanceof ReactionCount) return ((ReactionCount) a).contentEquals(b);
        if (a instanceof FlagDetail) return ((FlagDetail) a).contentEquals(b);
        if (a instanceof OneOnOnePreferences) return ((OneOnOnePreferences) a).contentEquals(b);
        if (a instanceof TypingIndicator) return ((TypingIndicator) a).contentEquals(b);
        if (a instanceof Conversation) return ((Conversation) a).contentEquals(b);
        if (a instanceof AIToolCall) return ((AIToolCall) a).contentEquals(b);
        throw new IllegalArgumentException("Unknown model type: " + a.getClass().getName());
    }

    // ==================== User Symmetry Tests ====================

    @Test
    public void testSymmetry_User_IdenticalObjects_ShouldBeSymmetric() {
        User user1 = createTestUser("user123", "Test User");
        User user2 = createTestUser("user123", "Test User");
        assertSymmetricEquality(user1, user2, "User");
    }

    @Test
    public void testSymmetry_User_ClonedObjects_ShouldBeSymmetric() {
        User original = createTestUser("user123", "Test User");
        User cloned = original.clone();
        assertSymmetricEquality(original, cloned, "User (cloned)");
    }

    @Test
    public void testSymmetry_User_ModifiedObjects_ShouldBeSymmetric() {
        User user1 = createTestUser("user123", "Test User");
        User user2 = createTestUser("user456", "Different User");
        assertSymmetricInequality(user1, user2, "User (modified)");
    }

    @Test
    public void testSymmetry_User_OneFieldDifferent_ShouldBeSymmetric() {
        User user1 = createTestUser("user123", "Test User");
        User user2 = user1.clone();
        user2.setName("Different Name");
        assertSymmetricInequality(user1, user2, "User (name different)");
    }

    // ==================== Group Symmetry Tests ====================

    @Test
    public void testSymmetry_Group_IdenticalObjects_ShouldBeSymmetric() {
        Group group1 = createTestGroup("group123", "Test Group");
        Group group2 = createTestGroup("group123", "Test Group");
        assertSymmetricEquality(group1, group2, "Group");
    }

    @Test
    public void testSymmetry_Group_ClonedObjects_ShouldBeSymmetric() {
        Group original = createTestGroup("group123", "Test Group");
        Group cloned = original.clone();
        assertSymmetricEquality(original, cloned, "Group (cloned)");
    }

    @Test
    public void testSymmetry_Group_ModifiedObjects_ShouldBeSymmetric() {
        Group group1 = createTestGroup("group123", "Test Group");
        Group group2 = createTestGroup("group456", "Different Group");
        assertSymmetricInequality(group1, group2, "Group (modified)");
    }

    @Test
    public void testSymmetry_Group_OneFieldDifferent_ShouldBeSymmetric() {
        Group group1 = createTestGroup("group123", "Test Group");
        Group group2 = group1.clone();
        group2.setMembersCount(999);
        assertSymmetricInequality(group1, group2, "Group (membersCount different)");
    }

    // ==================== GroupMember Symmetry Tests ====================

    @Test
    public void testSymmetry_GroupMember_IdenticalObjects_ShouldBeSymmetric() {
        GroupMember member1 = createTestGroupMember("user123", CometChatConstants.SCOPE_PARTICIPANT);
        GroupMember member2 = createTestGroupMember("user123", CometChatConstants.SCOPE_PARTICIPANT);
        assertSymmetricEquality(member1, member2, "GroupMember");
    }

    @Test
    public void testSymmetry_GroupMember_ClonedObjects_ShouldBeSymmetric() {
        GroupMember original = createTestGroupMember("user123", CometChatConstants.SCOPE_PARTICIPANT);
        GroupMember cloned = original.clone();
        assertSymmetricEquality(original, cloned, "GroupMember (cloned)");
    }

    @Test
    public void testSymmetry_GroupMember_ModifiedObjects_ShouldBeSymmetric() {
        GroupMember member1 = createTestGroupMember("user123", CometChatConstants.SCOPE_PARTICIPANT);
        GroupMember member2 = createTestGroupMember("user456", CometChatConstants.SCOPE_ADMIN);
        assertSymmetricInequality(member1, member2, "GroupMember (modified)");
    }

    // ==================== TextMessage Symmetry Tests ====================

    @Test
    public void testSymmetry_TextMessage_IdenticalObjects_ShouldBeSymmetric() {
        TextMessage msg1 = createTestTextMessage("Hello World");
        TextMessage msg2 = createTestTextMessage("Hello World");
        assertSymmetricEquality(msg1, msg2, "TextMessage");
    }

    @Test
    public void testSymmetry_TextMessage_ClonedObjects_ShouldBeSymmetric() {
        TextMessage original = createTestTextMessage("Hello World");
        TextMessage cloned = original.clone();
        assertSymmetricEquality(original, cloned, "TextMessage (cloned)");
    }

    @Test
    public void testSymmetry_TextMessage_ModifiedObjects_ShouldBeSymmetric() {
        TextMessage msg1 = createTestTextMessage("Hello World");
        TextMessage msg2 = createTestTextMessage("Different Text");
        assertSymmetricInequality(msg1, msg2, "TextMessage (modified)");
    }

    @Test
    public void testSymmetry_TextMessage_OneFieldDifferent_ShouldBeSymmetric() {
        TextMessage msg1 = createTestTextMessage("Hello World");
        TextMessage msg2 = msg1.clone();
        msg2.setId(99999L);
        assertSymmetricInequality(msg1, msg2, "TextMessage (id different)");
    }

    // ==================== MediaMessage Symmetry Tests ====================

    @Test
    public void testSymmetry_MediaMessage_IdenticalObjects_ShouldBeSymmetric() {
        MediaMessage msg1 = createTestMediaMessage();
        MediaMessage msg2 = createTestMediaMessage();
        assertSymmetricEquality(msg1, msg2, "MediaMessage");
    }

    @Test
    public void testSymmetry_MediaMessage_ClonedObjects_ShouldBeSymmetric() {
        MediaMessage original = createTestMediaMessage();
        MediaMessage cloned = original.clone();
        assertSymmetricEquality(original, cloned, "MediaMessage (cloned)");
    }

    @Test
    public void testSymmetry_MediaMessage_ModifiedObjects_ShouldBeSymmetric() {
        MediaMessage msg1 = createTestMediaMessage();
        MediaMessage msg2 = createTestMediaMessage();
        msg2.setCaption("Different caption");
        assertSymmetricInequality(msg1, msg2, "MediaMessage (modified)");
    }

    // ==================== Attachment Symmetry Tests ====================

    @Test
    public void testSymmetry_Attachment_IdenticalObjects_ShouldBeSymmetric() {
        Attachment att1 = createTestAttachment();
        Attachment att2 = createTestAttachment();
        assertSymmetricEquality(att1, att2, "Attachment");
    }

    @Test
    public void testSymmetry_Attachment_ClonedObjects_ShouldBeSymmetric() {
        Attachment original = createTestAttachment();
        Attachment cloned = original.clone();
        assertSymmetricEquality(original, cloned, "Attachment (cloned)");
    }

    @Test
    public void testSymmetry_Attachment_ModifiedObjects_ShouldBeSymmetric() {
        Attachment att1 = createTestAttachment();
        Attachment att2 = createTestAttachment();
        att2.setFileName("different.png");
        assertSymmetricInequality(att1, att2, "Attachment (modified)");
    }

    @Test
    public void testSymmetry_Attachment_FileSizeDifferent_ShouldBeSymmetric() {
        Attachment att1 = createTestAttachment();
        Attachment att2 = att1.clone();
        att2.setFileSize(9999);
        assertSymmetricInequality(att1, att2, "Attachment (fileSize different)");
    }

    // ==================== Reaction Symmetry Tests ====================

    @Test
    public void testSymmetry_Reaction_IdenticalObjects_ShouldBeSymmetric() {
        Reaction reaction1 = createTestReaction();
        Reaction reaction2 = createTestReaction();
        assertSymmetricEquality(reaction1, reaction2, "Reaction");
    }

    @Test
    public void testSymmetry_Reaction_ClonedObjects_ShouldBeSymmetric() {
        Reaction original = createTestReaction();
        Reaction cloned = original.clone();
        assertSymmetricEquality(original, cloned, "Reaction (cloned)");
    }

    @Test
    public void testSymmetry_Reaction_ModifiedObjects_ShouldBeSymmetric() {
        Reaction reaction1 = createTestReaction();
        Reaction reaction2 = createTestReaction();
        reaction2.setReaction("👎");
        assertSymmetricInequality(reaction1, reaction2, "Reaction (modified)");
    }

    @Test
    public void testSymmetry_Reaction_MessageIdDifferent_ShouldBeSymmetric() {
        Reaction reaction1 = createTestReaction();
        Reaction reaction2 = reaction1.clone();
        reaction2.setMessageId(99999L);
        assertSymmetricInequality(reaction1, reaction2, "Reaction (messageId different)");
    }

    // ==================== ReactionCount Symmetry Tests ====================

    @Test
    public void testSymmetry_ReactionCount_IdenticalObjects_ShouldBeSymmetric() {
        ReactionCount rc1 = createTestReactionCount();
        ReactionCount rc2 = createTestReactionCount();
        assertSymmetricEquality(rc1, rc2, "ReactionCount");
    }

    @Test
    public void testSymmetry_ReactionCount_ClonedObjects_ShouldBeSymmetric() {
        ReactionCount original = createTestReactionCount();
        ReactionCount cloned = original.clone();
        assertSymmetricEquality(original, cloned, "ReactionCount (cloned)");
    }

    @Test
    public void testSymmetry_ReactionCount_ModifiedObjects_ShouldBeSymmetric() {
        ReactionCount rc1 = createTestReactionCount();
        ReactionCount rc2 = createTestReactionCount();
        rc2.setCount(100);
        assertSymmetricInequality(rc1, rc2, "ReactionCount (modified)");
    }

    @Test
    public void testSymmetry_ReactionCount_ReactedByMeDifferent_ShouldBeSymmetric() {
        ReactionCount rc1 = createTestReactionCount();
        ReactionCount rc2 = rc1.clone();
        rc2.setReactedByMe(false);
        assertSymmetricInequality(rc1, rc2, "ReactionCount (reactedByMe different)");
    }

    // ==================== FlagDetail Symmetry Tests ====================

    @Test
    public void testSymmetry_FlagDetail_IdenticalObjects_ShouldBeSymmetric() {
        FlagDetail fd1 = createTestFlagDetail();
        FlagDetail fd2 = createTestFlagDetail();
        assertSymmetricEquality(fd1, fd2, "FlagDetail");
    }

    @Test
    public void testSymmetry_FlagDetail_ClonedObjects_ShouldBeSymmetric() {
        FlagDetail original = createTestFlagDetail();
        FlagDetail cloned = original.clone();
        assertSymmetricEquality(original, cloned, "FlagDetail (cloned)");
    }

    @Test
    public void testSymmetry_FlagDetail_ModifiedObjects_ShouldBeSymmetric() {
        FlagDetail fd1 = createTestFlagDetail();
        FlagDetail fd2 = new FlagDetail("different_reason", "Different remark");
        assertSymmetricInequality(fd1, fd2, "FlagDetail (modified)");
    }

    @Test
    public void testSymmetry_FlagDetail_RemarkDifferent_ShouldBeSymmetric() {
        FlagDetail fd1 = createTestFlagDetail();
        FlagDetail fd2 = fd1.clone();
        fd2.setRemark("Different remark");
        assertSymmetricInequality(fd1, fd2, "FlagDetail (remark different)");
    }

    // ==================== OneOnOnePreferences Symmetry Tests ====================

    @Test
    public void testSymmetry_OneOnOnePreferences_IdenticalObjects_ShouldBeSymmetric() {
        OneOnOnePreferences prefs1 = createTestOneOnOnePreferences();
        OneOnOnePreferences prefs2 = createTestOneOnOnePreferences();
        assertSymmetricEquality(prefs1, prefs2, "OneOnOnePreferences");
    }

    @Test
    public void testSymmetry_OneOnOnePreferences_ClonedObjects_ShouldBeSymmetric() {
        OneOnOnePreferences original = createTestOneOnOnePreferences();
        OneOnOnePreferences cloned = original.clone();
        assertSymmetricEquality(original, cloned, "OneOnOnePreferences (cloned)");
    }

    @Test
    public void testSymmetry_OneOnOnePreferences_ModifiedObjects_ShouldBeSymmetric() {
        OneOnOnePreferences prefs1 = createTestOneOnOnePreferences();
        OneOnOnePreferences prefs2 = new OneOnOnePreferences();
        prefs2.setMessagesPreference(MessagesOptions.SUBSCRIBE_TO_ALL);
        assertSymmetricInequality(prefs1, prefs2, "OneOnOnePreferences (modified)");
    }

    @Test
    public void testSymmetry_OneOnOnePreferences_OneEnumDifferent_ShouldBeSymmetric() {
        OneOnOnePreferences prefs1 = createTestOneOnOnePreferences();
        OneOnOnePreferences prefs2 = prefs1.clone();
        prefs2.setReactionsPreference(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES);
        assertSymmetricInequality(prefs1, prefs2, "OneOnOnePreferences (reactions different)");
    }

    // ==================== TypingIndicator Symmetry Tests ====================

    @Test
    public void testSymmetry_TypingIndicator_IdenticalObjects_ShouldBeSymmetric() {
        TypingIndicator ti1 = createTestTypingIndicator();
        TypingIndicator ti2 = createTestTypingIndicator();
        assertSymmetricEquality(ti1, ti2, "TypingIndicator");
    }

    @Test
    public void testSymmetry_TypingIndicator_ClonedObjects_ShouldBeSymmetric() {
        TypingIndicator original = createTestTypingIndicator();
        TypingIndicator cloned = original.clone();
        assertSymmetricEquality(original, cloned, "TypingIndicator (cloned)");
    }

    @Test
    public void testSymmetry_TypingIndicator_ModifiedObjects_ShouldBeSymmetric() {
        TypingIndicator ti1 = createTestTypingIndicator();
        TypingIndicator ti2 = new TypingIndicator("different_receiver", CometChatConstants.RECEIVER_TYPE_GROUP);
        assertSymmetricInequality(ti1, ti2, "TypingIndicator (modified)");
    }

    @Test
    public void testSymmetry_TypingIndicator_TimestampDifferent_ShouldBeSymmetric() {
        TypingIndicator ti1 = createTestTypingIndicator();
        TypingIndicator ti2 = ti1.clone();
        ti2.setLastTimestamp(9999999999L);
        assertSymmetricInequality(ti1, ti2, "TypingIndicator (timestamp different)");
    }

    // ==================== Conversation Symmetry Tests ====================

    @Test
    public void testSymmetry_Conversation_IdenticalObjects_ShouldBeSymmetric() {
        Conversation conv1 = createTestConversation();
        Conversation conv2 = createTestConversation();
        assertSymmetricEquality(conv1, conv2, "Conversation");
    }

    @Test
    public void testSymmetry_Conversation_ClonedObjects_ShouldBeSymmetric() {
        Conversation original = createTestConversation();
        Conversation cloned = original.clone();
        assertSymmetricEquality(original, cloned, "Conversation (cloned)");
    }

    @Test
    public void testSymmetry_Conversation_ModifiedObjects_ShouldBeSymmetric() {
        Conversation conv1 = createTestConversation();
        Conversation conv2 = new Conversation("different_conv", CometChatConstants.CONVERSATION_TYPE_GROUP);
        assertSymmetricInequality(conv1, conv2, "Conversation (modified)");
    }

    @Test
    public void testSymmetry_Conversation_UnreadCountDifferent_ShouldBeSymmetric() {
        Conversation conv1 = createTestConversation();
        Conversation conv2 = conv1.clone();
        conv2.setUnreadMessageCount(999);
        assertSymmetricInequality(conv1, conv2, "Conversation (unreadCount different)");
    }

    // ==================== AIToolCall Symmetry Tests ====================

    @Test
    public void testSymmetry_AIToolCall_IdenticalObjects_ShouldBeSymmetric() {
        AIToolCall tc1 = createTestAIToolCall();
        AIToolCall tc2 = createTestAIToolCall();
        assertSymmetricEquality(tc1, tc2, "AIToolCall");
    }

    @Test
    public void testSymmetry_AIToolCall_ClonedObjects_ShouldBeSymmetric() {
        AIToolCall original = createTestAIToolCall();
        AIToolCall cloned = original.clone();
        assertSymmetricEquality(original, cloned, "AIToolCall (cloned)");
    }

    @Test
    public void testSymmetry_AIToolCall_ModifiedObjects_ShouldBeSymmetric() {
        AIToolCall tc1 = createTestAIToolCall();
        AIToolCall tc2 = createTestAIToolCall();
        tc2.setId("different_id");
        assertSymmetricInequality(tc1, tc2, "AIToolCall (modified)");
    }

    @Test
    public void testSymmetry_AIToolCall_DisplayNameDifferent_ShouldBeSymmetric() {
        AIToolCall tc1 = createTestAIToolCall();
        AIToolCall tc2 = tc1.clone();
        tc2.setDisplayName("Different Display Name");
        assertSymmetricInequality(tc1, tc2, "AIToolCall (displayName different)");
    }

    // ==================== Null Field Symmetry Tests ====================

    @Test
    public void testSymmetry_User_BothNullFields_ShouldBeSymmetric() {
        User user1 = new User("user123", "Test User");
        user1.setAvatar(null);
        user1.setTags(null);
        User user2 = new User("user123", "Test User");
        user2.setAvatar(null);
        user2.setTags(null);
        assertSymmetricEquality(user1, user2, "User (null fields)");
    }

    @Test
    public void testSymmetry_User_OneNullOneNonNull_ShouldBeSymmetric() {
        User user1 = new User("user123", "Test User");
        user1.setAvatar("https://example.com/avatar.png");
        User user2 = new User("user123", "Test User");
        user2.setAvatar(null);
        assertSymmetricInequality(user1, user2, "User (one null avatar)");
    }

    @Test
    public void testSymmetry_Group_BothNullFields_ShouldBeSymmetric() {
        Group group1 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group1.setIcon(null);
        group1.setDescription(null);
        Group group2 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group2.setIcon(null);
        group2.setDescription(null);
        assertSymmetricEquality(group1, group2, "Group (null fields)");
    }

    @Test
    public void testSymmetry_Attachment_BothNullFields_ShouldBeSymmetric() {
        Attachment att1 = new Attachment();
        att1.setFileName(null);
        att1.setFileUrl(null);
        Attachment att2 = new Attachment();
        att2.setFileName(null);
        att2.setFileUrl(null);
        assertSymmetricEquality(att1, att2, "Attachment (null fields)");
    }

    // ==================== Collection Field Symmetry Tests ====================

    @Test
    public void testSymmetry_User_SameTagsList_ShouldBeSymmetric() {
        User user1 = createTestUser("user123", "Test User");
        user1.setTags(new ArrayList<>(Arrays.asList("tag1", "tag2", "tag3")));
        User user2 = createTestUser("user123", "Test User");
        user2.setTags(new ArrayList<>(Arrays.asList("tag1", "tag2", "tag3")));
        assertSymmetricEquality(user1, user2, "User (same tags)");
    }

    @Test
    public void testSymmetry_User_DifferentTagsList_ShouldBeSymmetric() {
        User user1 = createTestUser("user123", "Test User");
        user1.setTags(new ArrayList<>(Arrays.asList("tag1", "tag2")));
        User user2 = createTestUser("user123", "Test User");
        user2.setTags(new ArrayList<>(Arrays.asList("tag3", "tag4")));
        assertSymmetricInequality(user1, user2, "User (different tags)");
    }

    @Test
    public void testSymmetry_User_EmptyVsPopulatedTags_ShouldBeSymmetric() {
        User user1 = createTestUser("user123", "Test User");
        user1.setTags(new ArrayList<String>());
        User user2 = createTestUser("user123", "Test User");
        user2.setTags(new ArrayList<>(Arrays.asList("tag1")));
        assertSymmetricInequality(user1, user2, "User (empty vs populated tags)");
    }

    @Test
    public void testSymmetry_Conversation_SameTagsList_ShouldBeSymmetric() {
        Conversation conv1 = createTestConversation();
        conv1.setTags(new ArrayList<>(Arrays.asList("important", "work")));
        Conversation conv2 = createTestConversation();
        conv2.setTags(new ArrayList<>(Arrays.asList("important", "work")));
        assertSymmetricEquality(conv1, conv2, "Conversation (same tags)");
    }

    // ==================== Nested Object Symmetry Tests ====================

    @Test
    public void testSymmetry_Reaction_SameNestedUser_ShouldBeSymmetric() {
        Reaction reaction1 = createTestReaction();
        Reaction reaction2 = createTestReaction();
        assertSymmetricEquality(reaction1, reaction2, "Reaction (same nested user)");
    }

    @Test
    public void testSymmetry_Reaction_DifferentNestedUser_ShouldBeSymmetric() {
        Reaction reaction1 = createTestReaction();
        Reaction reaction2 = createTestReaction();
        reaction2.setReactedBy(createTestUser("different_user", "Different User"));
        assertSymmetricInequality(reaction1, reaction2, "Reaction (different nested user)");
    }

    @Test
    public void testSymmetry_Conversation_SameNestedEntity_ShouldBeSymmetric() {
        Conversation conv1 = createTestConversation();
        Conversation conv2 = createTestConversation();
        assertSymmetricEquality(conv1, conv2, "Conversation (same nested entity)");
    }

    @Test
    public void testSymmetry_Conversation_DifferentNestedEntity_ShouldBeSymmetric() {
        Conversation conv1 = createTestConversation();
        Conversation conv2 = createTestConversation();
        conv2.setConversationWith(createTestUser("different_user", "Different User"));
        assertSymmetricInequality(conv1, conv2, "Conversation (different nested entity)");
    }
}
