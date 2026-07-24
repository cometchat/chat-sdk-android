package com.cometchat.chat.notifications;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.DNDOptions;
import com.cometchat.chat.enums.DayOfWeek;
import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.ReactionsOptions;
import com.cometchat.chat.enums.RepliesOptions;
import com.cometchat.chat.models.DaySchedule;
import com.cometchat.chat.models.MutePreferences;
import com.cometchat.chat.models.NotificationPreferences;
import com.cometchat.chat.models.OneOnOnePreferences;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Feature Area: Notifications & Preferences / preference payloads.
 *
 * <p>Covers:
 * <ul>
 *   <li>NOTIF-41 — a preferences payload with missing sections defaults those to null.</li>
 *   <li>PREF-02 — a day schedule carries from/to/dnd, defaulting to 0/0/false; numeric strings
 *       coerce to ints.</li>
 *   <li>PREF-03 — one-on-one subscription levels; unset categories are not force-defaulted;
 *       numeric-as-string levels parse.</li>
 *   <li>PREF-04 — a mute preference carries a DND toggle plus a day-of-week schedule map.</li>
 * </ul>
 */
public class NotificationPreferencesTest {

    // ==================== PREF-02: day schedule ====================

    @Test
    public void pref02_daySchedule_carriesFromToAndDnd() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM, 900)
                .put(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO, 1700)
                .put(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND, true);

        DaySchedule schedule = DaySchedule.fromJson(json);

        assertEquals(900, schedule.getFrom());
        assertEquals(1700, schedule.getTo());
        assertEquals(true, schedule.getDnd());
    }

    @Test
    public void pref02_dayScheduleDefaults_areZeroZeroFalse() throws Exception {
        DaySchedule schedule = DaySchedule.fromJson(new JSONObject());

        assertEquals("an omitted schedule is fully closed", 0, schedule.getFrom());
        assertEquals(0, schedule.getTo());
        assertFalse(schedule.getDnd());
    }

    @Test
    public void pref02_fromAndToAsNumericStrings_areCoercedToInts() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM, "800")
                .put(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO, "1600");

        DaySchedule schedule = DaySchedule.fromJson(json);

        assertEquals(800, schedule.getFrom());
        assertEquals(1600, schedule.getTo());
    }

    // ==================== PREF-03: one-on-one preferences ====================

    @Test
    public void pref03_oneOnOneLevels_areParsedPerCategory() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES,
                        MessagesOptions.SUBSCRIBE_TO_ALL.getValue())
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES,
                        RepliesOptions.SUBSCRIBE_TO_MENTIONS.getValue())
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS,
                        ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES.getValue());

        OneOnOnePreferences prefs = OneOnOnePreferences.fromJson(json);

        assertEquals(MessagesOptions.SUBSCRIBE_TO_ALL, prefs.getMessagesPreference());
        assertEquals(RepliesOptions.SUBSCRIBE_TO_MENTIONS, prefs.getRepliesPreference());
        assertEquals(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES, prefs.getReactionsPreference());
    }

    @Test
    public void pref03_unsetCategories_areNotForceDefaulted() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES,
                        MessagesOptions.SUBSCRIBE_TO_ALL.getValue());

        OneOnOnePreferences prefs = OneOnOnePreferences.fromJson(json);

        assertEquals(MessagesOptions.SUBSCRIBE_TO_ALL, prefs.getMessagesPreference());
        assertNull("an unconfigured replies category stays 'not yet configured'", prefs.getRepliesPreference());
        assertNull(prefs.getReactionsPreference());
    }

    @Test
    public void pref03_levelSentAsNumericString_parses() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES, "2");

        OneOnOnePreferences prefs = OneOnOnePreferences.fromJson(json);

        assertEquals(MessagesOptions.SUBSCRIBE_TO_ALL, prefs.getMessagesPreference());
    }

    // ==================== PREF-04: mute preferences ====================

    @Test
    public void pref04_mutePreference_carriesDndToggleAndDaySchedule() throws Exception {
        JSONObject monday = new JSONObject()
                .put(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM, 2200)
                .put(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO, 700)
                .put(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND, true);
        JSONObject schedule = new JSONObject().put(DayOfWeek.MONDAY.getDayName(), monday);
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND, DNDOptions.ENABLED.getValue())
                .put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE, schedule);

        MutePreferences prefs = MutePreferences.fromJson(json);

        assertEquals(DNDOptions.ENABLED, prefs.getDNDPreference());
        assertNotNull(prefs.getSchedulePreference());
        assertEquals("the schedule is keyed by day of week",
                2200, prefs.getSchedulePreference().get(DayOfWeek.MONDAY).getFrom());
    }

    @Test
    public void pref04_mutePreferenceWithOnlyDnd_hasNoSchedule() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND, DNDOptions.DISABLED.getValue());

        MutePreferences prefs = MutePreferences.fromJson(json);

        assertEquals(DNDOptions.DISABLED, prefs.getDNDPreference());
        assertNull("an absent schedule is distinct from an empty one", prefs.getSchedulePreference());
    }

    // ==================== NOTIF-41: missing preference sections ====================

    @Test
    public void notif41_missingSections_defaultToNullNotAWholeFailure() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE, true);

        NotificationPreferences prefs = NotificationPreferences.fromJson(json);

        assertNotNull("the preferences object still loads", prefs);
        assertEquals(true, prefs.getUsePrivacyTemplate());
        assertNull("an unconfigured one-on-one section stays null", prefs.getOneOnOnePreferences());
        assertNull("an unconfigured group section stays null", prefs.getGroupPreferences());
        assertNull(prefs.getMutePreferences());
    }

    @Test
    public void notif41_presentSectionsAreParsed() throws Exception {
        JSONObject oneOnOne = new JSONObject()
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES,
                        MessagesOptions.SUBSCRIBE_TO_ALL.getValue());
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE, false)
                .put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES, oneOnOne);

        NotificationPreferences prefs = NotificationPreferences.fromJson(json);

        assertNotNull(prefs.getOneOnOnePreferences());
        assertEquals(MessagesOptions.SUBSCRIBE_TO_ALL,
                prefs.getOneOnOnePreferences().getMessagesPreference());
    }
}
