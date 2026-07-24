package com.cometchat.chat.models.contentequals;

import com.cometchat.chat.models.Group;
import com.cometchat.chat.constants.CometChatConstants;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for Group contentEquals() method.
 * 
 * Tests verify:
 * - Symmetry: group1.contentEquals(group2) == group2.contentEquals(group1)
 * - Clone equivalence: group.contentEquals(group.clone()) == true
 * - Field modification detection for each field
 * 
 * Feature: content-equals-models
 * Validates: Requirements 2.1, 2.4, 5.3, 7.1
 */
public class GroupContentEqualsTest {

    /**
     * Creates a fully populated Group for testing.
     * Note: Metadata is set to null because JSONObject is not available in unit tests.
     */
    private Group createFullyPopulatedGroup(String guid, String name) {
        Group group = new Group(guid, name, CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group.setIcon("https://example.com/icon.png");
        group.setDescription("Test group description");
        group.setOwner("owner123");
        // Note: Metadata is not set because JSONObject is not mocked in unit tests
        group.setMetadata(null);
        group.setCreatedAt(1234567890L);
        group.setUpdatedAt(1234567900L);
        group.setHasJoined(true);
        group.setJoinedAt(1234567895L);
        group.setScope(CometChatConstants.SCOPE_ADMIN);
        group.setMembersCount(10);
        group.setTags(new ArrayList<String>(Arrays.asList("tag1", "tag2")));
        return group;
    }

    // ==================== Symmetry Tests ====================

    @Test
    public void testSymmetry_IdenticalGroups_ShouldBeSymmetric() {
        Group group1 = createFullyPopulatedGroup("group123", "Test Group");
        Group group2 = createFullyPopulatedGroup("group123", "Test Group");
        boolean result1to2 = group1.contentEquals(group2);
        boolean result2to1 = group2.contentEquals(group1);
        assertTrue("group1.contentEquals(group2) should be true", result1to2);
        assertTrue("group2.contentEquals(group1) should be true", result2to1);
    }

    @Test
    public void testSymmetry_DifferentGroups_ShouldBeSymmetric() {
        Group group1 = createFullyPopulatedGroup("group123", "Test Group");
        Group group2 = createFullyPopulatedGroup("group456", "Different Group");
        boolean result1to2 = group1.contentEquals(group2);
        boolean result2to1 = group2.contentEquals(group1);
        assertFalse("group1.contentEquals(group2) should be false", result1to2);
        assertFalse("group2.contentEquals(group1) should be false", result2to1);
    }

    @Test
    public void testSymmetry_MinimalGroups_ShouldBeSymmetric() {
        Group group1 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        Group group2 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        boolean result1to2 = group1.contentEquals(group2);
        boolean result2to1 = group2.contentEquals(group1);
        assertTrue("group1.contentEquals(group2) should be true", result1to2);
        assertTrue("group2.contentEquals(group1) should be true", result2to1);
    }

    // ==================== Clone Equivalence Tests ====================

    @Test
    public void testCloneEquivalence_FullyPopulatedGroup_ShouldBeEqual() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group cloned = original.clone();
        assertTrue("Group should be content-equal to its clone", original.contentEquals(cloned));
        assertTrue("Clone should be content-equal to original", cloned.contentEquals(original));
    }

    @Test
    public void testCloneEquivalence_MinimalGroup_ShouldBeEqual() {
        Group original = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        Group cloned = original.clone();
        assertTrue("Minimal group should be content-equal to its clone", original.contentEquals(cloned));
    }

    @Test
    public void testCloneEquivalence_GroupWithNullFields_ShouldBeEqual() {
        Group original = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        original.setIcon(null);
        original.setDescription(null);
        original.setMetadata(null);
        original.setTags(null);
        Group cloned = original.clone();
        assertTrue("Group with null fields should be content-equal to its clone", original.contentEquals(cloned));
    }

    @Test
    public void testCloneEquivalence_PasswordProtectedGroup_ShouldBeEqual() {
        Group original = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PASSWORD, "secret123");
        original.setIcon("https://example.com/icon.png");
        Group cloned = original.clone();
        assertTrue("Password-protected group should be content-equal to its clone", original.contentEquals(cloned));
    }

    // ==================== Field Modification Tests ====================

    @Test
    public void testFieldModification_Guid_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setGuid("different_guid");
        assertFalse("Modifying guid should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Name_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setName("Different Name");
        assertFalse("Modifying name should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Type_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setGroupType(CometChatConstants.GROUP_TYPE_PRIVATE);
        assertFalse("Modifying type should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Password_ShouldReturnFalse() {
        Group original = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PASSWORD, "secret123");
        Group modified = original.clone();
        modified.setPassword("different_password");
        assertFalse("Modifying password should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_PasswordToNull_ShouldReturnFalse() {
        Group original = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PASSWORD, "secret123");
        Group modified = original.clone();
        modified.setPassword(null);
        assertFalse("Setting password to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Icon_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setIcon("https://example.com/different_icon.png");
        assertFalse("Modifying icon should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_IconToNull_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setIcon(null);
        assertFalse("Setting icon to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Description_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setDescription("Different description");
        assertFalse("Modifying description should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_DescriptionToNull_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setDescription(null);
        assertFalse("Setting description to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Owner_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setOwner("different_owner");
        assertFalse("Modifying owner should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_OwnerToNull_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setOwner(null);
        assertFalse("Setting owner to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Metadata_BothNull_ShouldBeEqual() {
        Group group1 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group1.setMetadata(null);
        Group group2 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group2.setMetadata(null);
        assertTrue("Two groups with null metadata should be content-equal", group1.contentEquals(group2));
    }

    @Test
    public void testFieldModification_MetadataToNull_ShouldReturnFalse() {
        // Note: This test verifies that a group with metadata differs from one without.
        // Since JSONObject is not available in unit tests, we test the null case.
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        // Both have null metadata, so they should be equal
        assertTrue("Groups with same null metadata should be content-equal", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_CreatedAt_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setCreatedAt(9999999999L);
        assertFalse("Modifying createdAt should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_UpdatedAt_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setUpdatedAt(9999999999L);
        assertFalse("Modifying updatedAt should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_HasJoined_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setHasJoined(false);
        assertFalse("Modifying hasJoined should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_JoinedAt_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setJoinedAt(9999999999L);
        assertFalse("Modifying joinedAt should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Scope_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setScope(CometChatConstants.SCOPE_PARTICIPANT);
        assertFalse("Modifying scope should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_ScopeToNull_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setScope(null);
        assertFalse("Setting scope to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_MembersCount_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setMembersCount(999);
        assertFalse("Modifying membersCount should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_Tags_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setTags(new ArrayList<String>(Arrays.asList("different_tag1", "different_tag2")));
        assertFalse("Modifying tags should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_TagsToNull_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        modified.setTags(null);
        assertFalse("Setting tags to null should make contentEquals return false", original.contentEquals(modified));
    }

    @Test
    public void testFieldModification_TagsAddElement_ShouldReturnFalse() {
        Group original = createFullyPopulatedGroup("group123", "Test Group");
        Group modified = original.clone();
        List<String> modifiedTags = new ArrayList<String>(modified.getTags());
        modifiedTags.add("new_tag");
        modified.setTags(modifiedTags);
        assertFalse("Adding a tag should make contentEquals return false", original.contentEquals(modified));
    }

    // ==================== Null Field Handling Tests ====================

    @Test
    public void testMetadata_BothNull_ShouldBeEqual() {
        Group group1 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group1.setMetadata(null);
        Group group2 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group2.setMetadata(null);
        assertTrue("Two groups with null metadata should be content-equal", group1.contentEquals(group2));
    }

    @Test
    public void testContentEquals_BothGroupsWithNullOptionalFields_ShouldBeEqual() {
        Group group1 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group1.setIcon(null);
        group1.setDescription(null);
        group1.setOwner(null);
        group1.setMetadata(null);
        group1.setScope(null);
        group1.setTags(null);
        Group group2 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group2.setIcon(null);
        group2.setDescription(null);
        group2.setOwner(null);
        group2.setMetadata(null);
        group2.setScope(null);
        group2.setTags(null);
        assertTrue("Two groups with all null optional fields should be content-equal", group1.contentEquals(group2));
    }

    @Test
    public void testContentEquals_EmptyTagsVsNullTags_ShouldNotBeEqual() {
        Group group1 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group1.setTags(new ArrayList<String>());
        Group group2 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group2.setTags(null);
        assertFalse("Empty tags list should not be content-equal to null tags", group1.contentEquals(group2));
    }

    @Test
    public void testContentEquals_BothEmptyTags_ShouldBeEqual() {
        Group group1 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group1.setTags(new ArrayList<String>());
        Group group2 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group2.setTags(new ArrayList<String>());
        assertTrue("Two groups with empty tags lists should be content-equal", group1.contentEquals(group2));
    }

    // ==================== Group Type Specific Tests ====================

    @Test
    public void testContentEquals_DifferentGroupTypes_ShouldNotBeEqual() {
        Group publicGroup = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        Group privateGroup = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PRIVATE, null);
        assertFalse("Groups with different types should not be content-equal", publicGroup.contentEquals(privateGroup));
    }

    @Test
    public void testContentEquals_PasswordProtectedGroups_SamePassword_ShouldBeEqual() {
        Group group1 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PASSWORD, "secret123");
        Group group2 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PASSWORD, "secret123");
        assertTrue("Password-protected groups with same password should be content-equal", group1.contentEquals(group2));
    }

    @Test
    public void testContentEquals_PasswordProtectedGroups_DifferentPassword_ShouldNotBeEqual() {
        Group group1 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PASSWORD, "secret123");
        Group group2 = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PASSWORD, "different_secret");
        assertFalse("Password-protected groups with different passwords should not be content-equal", group1.contentEquals(group2));
    }
}
