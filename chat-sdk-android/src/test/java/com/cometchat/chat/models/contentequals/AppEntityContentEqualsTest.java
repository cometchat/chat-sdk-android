package com.cometchat.chat.models.contentequals;

import com.cometchat.chat.models.AppEntity;
import com.cometchat.chat.models.User;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.constants.CometChatConstants;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for AppEntity contentEquals() method.
 * 
 * Since AppEntity is abstract, we use concrete subclasses (User, Group) for testing.
 * These tests verify the base contentEquals() behavior defined in AppEntity.
 * 
 * Feature: content-equals-models
 */
public class AppEntityContentEqualsTest {

    // ========================================================================
    // Test Reflexivity: entity.contentEquals(entity) == true
    // Validates: Requirements 1.3
    // ========================================================================

    /**
     * Test reflexivity with User instance.
     * An entity should be content-equal to itself.
     * 
     * Validates: Requirements 1.3
     */
    @Test
    public void testReflexivity_UserInstance_ShouldReturnTrue() {
        // Arrange
        User user = new User("user123", "Test User");
        
        // Act & Assert
        assertTrue("User should be content-equal to itself", user.contentEquals(user));
    }

    /**
     * Test reflexivity with Group instance.
     * An entity should be content-equal to itself.
     * 
     * Validates: Requirements 1.3
     */
    @Test
    public void testReflexivity_GroupInstance_ShouldReturnTrue() {
        // Arrange
        Group group = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        
        // Act & Assert
        assertTrue("Group should be content-equal to itself", group.contentEquals(group));
    }

    // ========================================================================
    // Test Null Safety: entity.contentEquals(null) == false
    // Validates: Requirements 1.2
    // ========================================================================

    /**
     * Test null safety with User instance.
     * An entity should not be content-equal to null.
     * 
     * Validates: Requirements 1.2
     */
    @Test
    public void testNullSafety_UserWithNull_ShouldReturnFalse() {
        // Arrange
        User user = new User("user123", "Test User");
        
        // Act & Assert
        assertFalse("User should not be content-equal to null", user.contentEquals(null));
    }

    /**
     * Test null safety with Group instance.
     * An entity should not be content-equal to null.
     * 
     * Validates: Requirements 1.2
     */
    @Test
    public void testNullSafety_GroupWithNull_ShouldReturnFalse() {
        // Arrange
        Group group = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        
        // Act & Assert
        assertFalse("Group should not be content-equal to null", group.contentEquals(null));
    }

    // ========================================================================
    // Test Type Safety: entity.contentEquals(differentType) == false
    // Validates: Requirements 1.4
    // ========================================================================

    /**
     * Test type safety with User and String.
     * An entity should not be content-equal to an object of a different type.
     * 
     * Validates: Requirements 1.4
     */
    @Test
    public void testTypeSafety_UserWithString_ShouldReturnFalse() {
        // Arrange
        User user = new User("user123", "Test User");
        String differentType = "not a user";
        
        // Act & Assert
        assertFalse("User should not be content-equal to a String", user.contentEquals(differentType));
    }

    /**
     * Test type safety with Group and String.
     * An entity should not be content-equal to an object of a different type.
     * 
     * Validates: Requirements 1.4
     */
    @Test
    public void testTypeSafety_GroupWithString_ShouldReturnFalse() {
        // Arrange
        Group group = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        String differentType = "not a group";
        
        // Act & Assert
        assertFalse("Group should not be content-equal to a String", group.contentEquals(differentType));
    }

    /**
     * Test type safety with User and Integer.
     * An entity should not be content-equal to an object of a different type.
     * 
     * Validates: Requirements 1.4
     */
    @Test
    public void testTypeSafety_UserWithInteger_ShouldReturnFalse() {
        // Arrange
        User user = new User("user123", "Test User");
        Integer differentType = 42;
        
        // Act & Assert
        assertFalse("User should not be content-equal to an Integer", user.contentEquals(differentType));
    }

    /**
     * Test type safety with Group and Object.
     * An entity should not be content-equal to a plain Object.
     * 
     * Validates: Requirements 1.4
     */
    @Test
    public void testTypeSafety_GroupWithPlainObject_ShouldReturnFalse() {
        // Arrange
        Group group = new Group("group123", "Test Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        Object differentType = new Object();
        
        // Act & Assert
        assertFalse("Group should not be content-equal to a plain Object", group.contentEquals(differentType));
    }
}
