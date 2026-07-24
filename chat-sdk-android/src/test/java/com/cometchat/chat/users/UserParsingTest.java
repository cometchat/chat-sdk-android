package com.cometchat.chat.users;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.User;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Users / User profile parsing, block flags, status, deactivation.
 *
 * <p>Covers:
 * <ul>
 *   <li>USR-01 — full profile (avatar, role, tags, custom metadata) exposed.</li>
 *   <li>USR-02 — minimal profile: only the identifier populated, rest unset.</li>
 *   <li>USR-03 — tolerant of a numeric field sent as a string.</li>
 *   <li>USR-04 / USR-05 — block flags.</li>
 *   <li>USR-06 — deactivation timestamp of zero means "not deactivated".</li>
 *   <li>USR-07 — legacy "available" normalized to "online".</li>
 *   <li>USR-08 — missing status defaults to "offline".</li>
 * </ul>
 *
 * <p><b>Android divergence (USR-04/USR-05):</b> {@code User.hasBlockedMe}/{@code blockedByMe}
 * are primitive {@code boolean} fields. Unlike the Flutter SDK's nullable flags, Android
 * cannot represent "unknown" — an absent flag reads as {@code false}. These tests assert the
 * <em>actual</em> Android behavior rather than the Flutter contract.
 */
public class UserParsingTest {

    private JSONObject fullUser() throws Exception {
        return new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "cool-cat")
                .put(CometChatConstants.UserKeys.USER_KEY_NAME, "Kevin")
                .put(CometChatConstants.UserKeys.USER_KEY_AVATAR, "https://example.com/kevin.png")
                .put(CometChatConstants.UserKeys.USER_KEY_ROLE, "admin")
                .put(CometChatConstants.UserKeys.USER_KEY_STATUS, CometChatConstants.USER_STATUS_ONLINE)
                .put(CometChatConstants.UserKeys.USER_KEY_STATUS_MESSAGE, "Available for chat")
                .put(CometChatConstants.UserKeys.USER_KEY_LAST_ACTIVE_AT, 1700000000L)
                .put(CometChatConstants.UserKeys.USER_KEY_TAGS, new JSONArray().put("vip").put("beta"))
                .put(CometChatConstants.UserKeys.USER_KEY_METADATA,
                        new JSONObject().put("department", "engineering").put("level", 7));
    }

    // ==================== USR-01: full profile ====================

    @Test
    public void usr01_fullProfile_exposesEveryProvidedField() throws Exception {
        User user = User.fromJson(fullUser().toString());

        assertEquals("cool-cat", user.getUid());
        assertEquals("Kevin", user.getName());
        assertEquals("https://example.com/kevin.png", user.getAvatar());
        assertEquals("admin", user.getRole());
        assertEquals(CometChatConstants.USER_STATUS_ONLINE, user.getStatus());
        assertEquals("Available for chat", user.getStatusMessage());
        assertEquals(1700000000L, user.getLastActiveAt());
    }

    @Test
    public void usr01_fullProfile_exposesTagsAndCustomMetadata() throws Exception {
        User user = User.fromJson(fullUser().toString());

        assertEquals(2, user.getTags().size());
        assertTrue(user.getTags().contains("vip"));
        assertTrue(user.getTags().contains("beta"));
        assertEquals("arbitrary custom metadata must be preserved",
                "engineering", user.getMetadata().getString("department"));
        assertEquals(7, user.getMetadata().getInt("level"));
    }

    // ==================== USR-02: minimal profile ====================

    @Test
    public void usr02_minimalProfile_populatesOnlyTheIdentifier() throws Exception {
        JSONObject json = new JSONObject().put(CometChatConstants.UserKeys.USER_KEY_UID, "lonely-uid");

        User user = User.fromJson(json.toString());

        assertEquals("lonely-uid", user.getUid());
        assertNull("name is optional and must not be invented", user.getName());
        assertNull("avatar is optional and must not be invented", user.getAvatar());
        assertNull("role is optional and must not be invented", user.getRole());
        assertNull("metadata is optional and must not be invented", user.getMetadata());
        assertNull("tags are optional and must not be invented", user.getTags());
    }

    // ==================== USR-03: numeric field as string ====================

    @Test
    public void usr03_lastActiveAtSentAsNumericString_isTolerated() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "u1")
                // backend inconsistency: a numeric timestamp encoded as a string
                .put(CometChatConstants.UserKeys.USER_KEY_LAST_ACTIVE_AT, "1700000000");

        User user = User.fromJson(json.toString());

        assertEquals("a numeric string must be normalized to a long, not dropped",
                1700000000L, user.getLastActiveAt());
    }

    @Test
    public void usr03_malformedNumericField_doesNotCrashAndStillReturnsAUser() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "u2")
                .put(CometChatConstants.UserKeys.USER_KEY_LAST_ACTIVE_AT, "not-a-number");

        User user = User.fromJson(json.toString());

        assertEquals("parsing must not throw; the uid parsed before the bad field is retained",
                "u2", user.getUid());
    }

    // ==================== USR-04 / USR-05: block flags ====================

    @Test
    public void usr05_explicitBlockFlags_arePreserved() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "u3")
                .put(CometChatConstants.UserKeys.USER_KEY_HAS_BLOCKED_ME, true)
                .put(CometChatConstants.UserKeys.USER_KEY_BLOCKED_BY_ME, true);

        User user = User.fromJson(json.toString());

        assertTrue(user.isHasBlockedMe());
        assertTrue(user.isBlockedByMe());
    }

    @Test
    public void usr05_explicitFalseBlockFlags_arePreserved() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "u4")
                .put(CometChatConstants.UserKeys.USER_KEY_HAS_BLOCKED_ME, false)
                .put(CometChatConstants.UserKeys.USER_KEY_BLOCKED_BY_ME, false);

        User user = User.fromJson(json.toString());

        assertFalse(user.isHasBlockedMe());
        assertFalse(user.isBlockedByMe());
    }

    @Test
    public void usr04_absentBlockFlags_readAsFalseOnAndroid() throws Exception {
        // Android divergence: the flags are primitive booleans, so an absent flag is
        // indistinguishable from an explicit false (the Flutter SDK keeps these nullable).
        JSONObject json = new JSONObject().put(CometChatConstants.UserKeys.USER_KEY_UID, "u5");

        User user = User.fromJson(json.toString());

        assertFalse("Android reports false (not 'unknown') for an omitted block flag",
                user.isHasBlockedMe());
        assertFalse(user.isBlockedByMe());
    }

    // ==================== USR-06: deactivation ====================

    @Test
    public void usr06_zeroDeactivationTimestamp_meansNotDeactivated() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "u6")
                .put(CometChatConstants.UserKeys.USER_KEY_DEACTIVATED_AT, 0);

        User user = User.fromJson(json.toString());

        assertEquals("a zero deactivation timestamp must not flag an active account",
                0L, user.getDeactivatedAt());
    }

    @Test
    public void usr06_realDeactivationTimestamp_isPreserved() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "u7")
                .put(CometChatConstants.UserKeys.USER_KEY_DEACTIVATED_AT, 1699999999L);

        User user = User.fromJson(json.toString());

        assertEquals(1699999999L, user.getDeactivatedAt());
    }

    // ==================== USR-07 / USR-08: status normalization ====================

    @Test
    public void usr07_legacyAvailableStatus_isNormalizedToOnline() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "u8")
                .put(CometChatConstants.UserKeys.USER_KEY_STATUS, "available");

        User user = User.fromJson(json.toString());

        assertEquals("the legacy 'available' presence must map to the standard 'online'",
                CometChatConstants.USER_STATUS_ONLINE, user.getStatus());
    }

    @Test
    public void usr07_availableStatusIsNormalizedCaseInsensitively() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "u9")
                .put(CometChatConstants.UserKeys.USER_KEY_STATUS, "AVAILABLE");

        User user = User.fromJson(json.toString());

        assertEquals(CometChatConstants.USER_STATUS_ONLINE, user.getStatus());
    }

    @Test
    public void usr08_missingStatus_defaultsToOffline() throws Exception {
        JSONObject json = new JSONObject().put(CometChatConstants.UserKeys.USER_KEY_UID, "u10");

        User user = User.fromJson(json.toString());

        assertEquals("an absent status must resolve to a definite 'offline'",
                CometChatConstants.USER_STATUS_OFFLINE, user.getStatus());
    }

    @Test
    public void usr08_explicitOfflineStatus_isPreserved() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "u11")
                .put(CometChatConstants.UserKeys.USER_KEY_STATUS, CometChatConstants.USER_STATUS_OFFLINE);

        User user = User.fromJson(json.toString());

        assertEquals(CometChatConstants.USER_STATUS_OFFLINE, user.getStatus());
    }
}
