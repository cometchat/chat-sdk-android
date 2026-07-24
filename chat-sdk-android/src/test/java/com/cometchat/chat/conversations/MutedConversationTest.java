package com.cometchat.chat.conversations;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.MutedConversationType;
import com.cometchat.chat.models.ConversationUpdateSettings;
import com.cometchat.chat.models.MutedConversation;
import com.cometchat.chat.models.UnmutedConversation;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Conversations / Muting, unmuting, and update-notification settings.
 *
 * <p>Covers:
 * <ul>
 *   <li>CONV-12 — a mute record captures which conversation is muted, its type, and expiry;
 *       an unrecognized type is ignored rather than crashing.</li>
 *   <li>CONV-13 — a mute with no expiry defaults to zero.</li>
 *   <li>CONV-14 — an unmute record identifies which conversation and type was unmuted.</li>
 *   <li>CONV-15 — each conversation-update setting defaults to "on" when unspecified.</li>
 * </ul>
 */
public class MutedConversationTest {

    private JSONObject muted(String id, String type, Long until) throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.MutedConversationKeys.KEY_ID, id)
                .put(CometChatNotificationsConstants.MutedConversationKeys.KEY_TYPE, type);
        if (until != null) {
            json.put(CometChatNotificationsConstants.MutedConversationKeys.KEY_UNTIL, until);
        }
        return json;
    }

    // ==================== CONV-12: mute record ====================

    @Test
    public void conv12_muteRecord_capturesIdTypeAndExpiry() throws Exception {
        MutedConversation m = MutedConversation.fromJson(
                muted("u1_user_u2", MutedConversationType.ONE_ON_ONE.getType(), 1800000000L));

        assertEquals("u1_user_u2", m.getId());
        assertEquals(MutedConversationType.ONE_ON_ONE, m.getType());
        assertEquals(1800000000L, m.getUntil());
    }

    @Test
    public void conv12_groupMuteRecord_isTyped() throws Exception {
        MutedConversation m = MutedConversation.fromJson(
                muted("grp-1", MutedConversationType.GROUP.getType(), 1800000000L));

        assertEquals(MutedConversationType.GROUP, m.getType());
    }

    @Test
    public void conv12_unrecognizedType_isIgnoredRatherThanCrashing() throws Exception {
        MutedConversation m = MutedConversation.fromJson(muted("x", "someFutureType", 1L));

        assertEquals("x", m.getId());
        assertNull("an unknown type resolves to null instead of throwing", m.getType());
    }

    // ==================== CONV-13: mute with no expiry ====================

    @Test
    public void conv13_muteWithoutExpiry_defaultsToZero() throws Exception {
        MutedConversation m = MutedConversation.fromJson(
                muted("no-expiry", MutedConversationType.ONE_ON_ONE.getType(), null));

        assertEquals("an absent 'until' must default safely to zero", 0L, m.getUntil());
    }

    @Test
    public void conv13_muteDurationAsNumericString_isParsed() throws Exception {
        JSONObject json = muted("str-until", MutedConversationType.GROUP.getType(), null)
                .put(CometChatNotificationsConstants.MutedConversationKeys.KEY_UNTIL, "1800000000");

        MutedConversation m = MutedConversation.fromJson(json);

        assertEquals("a duration sent as a numeric string must still parse",
                1800000000L, m.getUntil());
    }

    // ==================== CONV-14: unmute record ====================

    @Test
    public void conv14_unmuteRecord_identifiesConversationAndType() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_ID, "grp-2")
                .put(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_TYPE,
                        MutedConversationType.GROUP.getType());

        UnmutedConversation u = UnmutedConversation.fromJson(json);

        assertEquals("grp-2", u.getId());
        assertEquals(MutedConversationType.GROUP, u.getType());
    }

    @Test
    public void conv14_unmuteRecordWithPartialData_stillParses() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_ID, "only-id");

        UnmutedConversation u = UnmutedConversation.fromJson(json);

        assertEquals("only-id", u.getId());
        assertNull(u.getType());
    }

    // ==================== CONV-15: conversation-update settings default on ====================

    @Test
    public void conv15_updateSettings_defaultToOnWhenUnspecified() throws Exception {
        ConversationUpdateSettings settings = ConversationUpdateSettings.fromJson(new JSONObject().toString());

        assertTrue(settings.shouldUpdateOnCallActivities());
        assertTrue(settings.shouldUpdateOnGroupActions());
        assertTrue(settings.shouldUpdateOnCustomMessages());
        assertTrue(settings.shouldUpdateOnMessageReplies());
    }

    @Test
    public void conv15_togglingOneSetting_leavesTheOthersAtTheirDefault() throws Exception {
        // The wire shape nests the toggles under a "parameters" object keyed by dotted paths.
        JSONObject parameters = new JSONObject()
                .put(CometChatConstants.SettingsKeys.CORE_CONVERSATIONS_UPDATE_ON_CALL_ACTIVITY, false);
        JSONObject json = new JSONObject()
                .put(CometChatConstants.SettingsKeys.PARAMETERS, parameters);

        ConversationUpdateSettings settings = ConversationUpdateSettings.fromJson(json.toString());

        assertFalse("the explicitly disabled setting is off", settings.shouldUpdateOnCallActivities());
        assertTrue("other settings stay at their default 'on'", settings.shouldUpdateOnGroupActions());
        assertTrue(settings.shouldUpdateOnCustomMessages());
        assertTrue(settings.shouldUpdateOnMessageReplies());
    }
}
