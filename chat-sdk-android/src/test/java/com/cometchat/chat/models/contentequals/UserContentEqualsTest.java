package com.cometchat.chat.models.contentequals;

import com.cometchat.chat.models.User;
import com.cometchat.chat.constants.CometChatConstants;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for User contentEquals() method.
 * 
 * Tests verify:
 * - Symmetry: user1.contentEquals(user2) == user2.contentEquals(user1)
 * - Clone equivalence: user.contentEquals(user.clone()) == true
 * - Field modification detection for each field
 * 
 * Feature: content-equals-models
 * Validates: Requirements 2.1, 2.4, 5.3, 7.1
 */
public class UserContentEqualsTest {

    private User createFullyPopulatedUser(String uid, String name) {
        User user = new User(uid, name);
        user.setAvatar("https://example.com/avatar.png");
        user.setLink("https://example.com/profile");
        user.setRole("admin");
        user.setStatus(CometChatConstants.USER_STATUS_ONLINE);
        user.setStatusMessage("Available");
        user.setLastActiveAt(1234567890L);
        user.setHasBlockedMe(false);
        user.setBlockedByMe(false);
        user.setTags(new ArrayList<String>(Arrays.asList("tag1", "tag2")));
        user.setDeactivatedAt(0L);
        return user;
    }

    @Test
    public void testSymmetry_IdenticalUsers_ShouldBeSymmetric() {
        User user1 = createFullyPopulatedUser("user123", "Test User");
        User user2 = createFullyPopulatedUser("user123", "Test User");
        boolean result1to2 = user1.contentEquals(user2);
        boolean result2to1 = user2.contentEquals(user1);
        assertTrue("user1.contentEquals(user2) should be true", result1to2);
        assertTrue("user2.contentEquals(user1) should be true", result2to1);
    }


    @Test
    public void testSymmetry_DifferentUsers_ShouldBeSymmetric() {
        User user1 = createFullyPopulatedUser("user123", "Test User");
        User user2 = createFullyPopulatedUser("user456", "Different User");
        boolean result1to2 = user1.contentEquals(user2);
        boolean result2to1 = user2.contentEquals(user1);
        assertFalse("user1.contentEquals(user2) should be false", result1to2);
        assertFalse("user2.contentEquals(user1) should be false", result2to1);
    }

    @Test
    public void testSymmetry_MinimalUsers_ShouldBeSymmetric() {
        User user1 = new User("user123", "Test User");
        User user2 = new User("user123", "Test User");
        boolean result1to2 = user1.contentEquals(user2);
        boolean result2to1 = user2.contentEquals(user1);
        assertTrue("user1.contentEquals(user2) should be true", result1to2);
        assertTrue("user2.contentEquals(user1) should be true", result2to1);
    }

    @Test
    public void testCloneEquivalence_FullyPopulatedUser_ShouldBeEqual() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User cloned = original.clone();
        assertTrue("User should be content-equal to its clone", original.contentEquals(cloned));
        assertTrue("Clone should be content-equal to original", cloned.contentEquals(original));
    }

    @Test
    public void testCloneEquivalence_MinimalUser_ShouldBeEqual() {
        User original = new User("user123", "Test User");
        User cloned = original.clone();
        assertTrue("Minimal user should be content-equal to its clone", original.contentEquals(cloned));
    }

    @Test
    public void testCloneEquivalence_UserWithNullFields_ShouldBeEqual() {
        User original = new User("user123", "Test User");
        original.setAvatar(null);
        original.setMetadata(null);
        original.setTags(null);
        User cloned = original.clone();
        assertTrue("User with null fields should be content-equal to its clone", original.contentEquals(cloned));
    }


    @Test
    public void testFieldModification_Uid_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setUid("different_uid");
        assertFalse("Modifying uid should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Name_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setName("Different Name");
        assertFalse("Modifying name should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Avatar_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setAvatar("https://example.com/different_avatar.png");
        assertFalse("Modifying avatar should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_AvatarToNull_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setAvatar(null);
        assertFalse("Setting avatar to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Link_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setLink("https://example.com/different_profile");
        assertFalse("Modifying link should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Role_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setRole("user");
        assertFalse("Modifying role should make contentEquals return false", original.contentEquals(modified));
    }


    @Test
    public void testMetadata_BothNull_ShouldBeEqual() {
        User user1 = new User("user123", "Test User");
        user1.setMetadata(null);
        User user2 = new User("user123", "Test User");
        user2.setMetadata(null);
        assertTrue("Two users with null metadata should be content-equal", user1.contentEquals(user2));
    }

    @Test
    public void testFieldModification_Status_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setStatus(CometChatConstants.USER_STATUS_OFFLINE);
        assertFalse("Modifying status should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_StatusMessage_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setStatusMessage("Busy");
        assertFalse("Modifying statusMessage should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_LastActiveAt_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setLastActiveAt(9999999999L);
        assertFalse("Modifying lastActiveAt should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_HasBlockedMe_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setHasBlockedMe(true);
        assertFalse("Modifying hasBlockedMe should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_BlockedByMe_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setBlockedByMe(true);
        assertFalse("Modifying blockedByMe should make contentEquals return false", original.contentEquals(modified));
    }


    @Test
    public void testFieldModification_Tags_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setTags(new ArrayList<String>(Arrays.asList("different_tag1", "different_tag2")));
        assertFalse("Modifying tags should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_TagsToNull_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setTags(null);
        assertFalse("Setting tags to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_TagsAddElement_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        List<String> modifiedTags = new ArrayList<String>(modified.getTags());
        modifiedTags.add("new_tag");
        modified.setTags(modifiedTags);
        assertFalse("Adding a tag should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_DeactivatedAt_ShouldReturnFalse() {
        User original = createFullyPopulatedUser("user123", "Test User");
        User modified = original.clone();
        modified.setDeactivatedAt(1234567890L);
        assertFalse("Modifying deactivatedAt should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testContentEquals_BothUsersWithNullOptionalFields_ShouldBeEqual() {
        User user1 = new User("user123", "Test User");
        user1.setAvatar(null);
        user1.setLink(null);
        user1.setRole(null);
        user1.setMetadata(null);
        user1.setStatus(null);
        user1.setStatusMessage(null);
        user1.setTags(null);
        User user2 = new User("user123", "Test User");
        user2.setAvatar(null);
        user2.setLink(null);
        user2.setRole(null);
        user2.setMetadata(null);
        user2.setStatus(null);
        user2.setStatusMessage(null);
        user2.setTags(null);
        assertTrue("Two users with all null optional fields should be content-equal", user1.contentEquals(user2));
    }

    @Test
    public void testContentEquals_EmptyTagsVsNullTags_ShouldNotBeEqual() {
        User user1 = new User("user123", "Test User");
        user1.setTags(new ArrayList<String>());
        User user2 = new User("user123", "Test User");
        user2.setTags(null);
        assertFalse("Empty tags list should not be content-equal to null tags", user1.contentEquals(user2));
    }

    @Test
    public void testContentEquals_BothEmptyTags_ShouldBeEqual() {
        User user1 = new User("user123", "Test User");
        user1.setTags(new ArrayList<String>());
        User user2 = new User("user123", "Test User");
        user2.setTags(new ArrayList<String>());
        assertTrue("Two users with empty tags lists should be content-equal", user1.contentEquals(user2));
    }
}
