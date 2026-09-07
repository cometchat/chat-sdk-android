package com.cometchat.chat.notifications;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.DNDOptions;
import com.cometchat.chat.enums.DayOfWeek;
import com.cometchat.chat.enums.MemberActionsOptions;
import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.QuotedRepliesOptions;
import com.cometchat.chat.enums.ReactionsOptions;
import com.cometchat.chat.enums.RepliesOptions;
import com.cometchat.chat.models.DaySchedule;
import com.cometchat.chat.models.GroupPreferences;
import com.cometchat.chat.models.MutePreferences;
import com.cometchat.chat.models.NotificationPreferences;
import com.cometchat.chat.models.OneOnOnePreferences;
import com.cometchat.chat.models.PushPreferences;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
 *   <li>QR-1/QR-5 — quoted replies is read independently for the one-to-one and group groups.</li>
 *   <li>PREF-07 — a preference map carrying an explicit {@code null} value leaves that preference
 *       unset instead of throwing, on every bucket's {@code fromMap} path (ENG-38347).</li>
 *   <li>QR-13 — an unrecognised quoted-replies value degrades to unset on the one-to-one bucket's
 *       object-transfer (parcel) decode path rather than throwing; and the group bucket's unset
 *       sentinel cannot collide with a real option value.</li>
 * </ul>
 *
 * <p><b>Platform note (QR-13).</b> A true parcel round trip cannot run as a JVM unit test in this
 * module: {@code android.os.Parcel} is a stub here ("Method obtain in android.os.Parcel not
 * mocked") and the module has no Robolectric on the unit-test classpath. The parcel read path is
 * therefore covered at its decision point — the helper the parcel constructor delegates to — which
 * is where the {@code Enum.valueOf} hazard lives. The group bucket has no such helper, so for that
 * bucket only the precondition its int/-1 encoding rests on is asserted here; its parcel
 * constructor is not executed. A device-side round trip is left as follow-up for both buckets.
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

    @Test
    public void pref03_subscribedThreadsReplyLevel_roundTripsThroughJson() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES,
                        RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS.getValue());

        OneOnOnePreferences parsed = OneOnOnePreferences.fromJson(json);
        assertEquals("the new replies level decodes rather than resetting to null",
                RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS, parsed.getRepliesPreference());

        // ...and re-encoding preserves it, so an upgraded client never downgrades the choice.
        assertEquals(RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS.getValue(),
                parsed.toJson().getInt(
                        CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES));
    }

    @Test
    public void pref03_subscribedThreadsReplyLevel_roundTripsThroughMap() {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setRepliesPreference(RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS);

        OneOnOnePreferences restored = OneOnOnePreferences.fromMap(prefs.toMap());

        assertEquals(RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS, restored.getRepliesPreference());
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

    // ==================== QR-1/QR-5: quoted replies, read per conversation type ====================

    @Test
    public void qr1_oneOnOneQuotedReplies_isParsedIndependentlyOfThreadedReplies() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES,
                        RepliesOptions.DONT_SUBSCRIBE.getValue())
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES,
                        QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES.getValue());

        OneOnOnePreferences prefs = OneOnOnePreferences.fromJson(json);

        assertEquals("threaded replies is a separate preference and keeps its own value",
                RepliesOptions.DONT_SUBSCRIBE, prefs.getRepliesPreference());
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                prefs.getQuotedRepliesPreference());
    }

    @Test
    public void qr5_theTwoConversationTypes_holdQuotedRepliesIndependently() throws Exception {
        JSONObject oneOnOne = new JSONObject()
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES,
                        QuotedRepliesOptions.SUBSCRIBE_TO_ALL.getValue());
        JSONObject group = new JSONObject()
                .put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_QUOTED_REPLIES,
                        QuotedRepliesOptions.DONT_SUBSCRIBE.getValue());
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES, oneOnOne)
                .put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES, group);

        NotificationPreferences prefs = NotificationPreferences.fromJson(json);

        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_ALL,
                prefs.getOneOnOnePreferences().getQuotedRepliesPreference());
        assertEquals("a user may hold a different value per conversation type",
                QuotedRepliesOptions.DONT_SUBSCRIBE,
                prefs.getGroupPreferences().getQuotedRepliesPreference());
    }

    @Test
    public void qr1_unsetQuotedReplies_staysUnsetOnBothBuckets() throws Exception {
        JSONObject oneOnOneJson = new JSONObject()
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES,
                        MessagesOptions.SUBSCRIBE_TO_ALL.getValue());
        JSONObject groupJson = new JSONObject()
                .put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES,
                        MessagesOptions.SUBSCRIBE_TO_ALL.getValue());

        assertNull("an unconfigured quoted-replies category stays 'not yet configured'",
                OneOnOnePreferences.fromJson(oneOnOneJson).getQuotedRepliesPreference());
        assertNull(GroupPreferences.fromJson(groupJson).getQuotedRepliesPreference());
    }

    // ==================== QR-13: the object-transfer (parcel) path ====================

    /**
     * The one-to-one bucket parcels its enums by constant name and reads them back with
     * {@code Enum.valueOf}, which throws on a name it does not know. A parcel written by a build that
     * knows a value this one does not must degrade to unset instead of crashing.
     */
    @Test
    public void qr13_anUnknownEnumNameOnTheParcelPath_doesNotThrow() throws Exception {
        Method decoder = OneOnOnePreferences.class.getDeclaredMethod("safeValueOf", Class.class, String.class);
        decoder.setAccessible(true);

        assertNull("an unknown constant name degrades to unset",
                decoder.invoke(null, QuotedRepliesOptions.class, "SUBSCRIBE_TO_SOMETHING_FROM_A_LATER_RELEASE"));
        assertNull("a null (unset) name stays unset", decoder.invoke(null, QuotedRepliesOptions.class, (String) null));
        assertNull("a malformed name does not throw", decoder.invoke(null, QuotedRepliesOptions.class, ""));
        assertEquals("a known name still resolves",
                QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                decoder.invoke(null, QuotedRepliesOptions.class, "SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES"));
    }

    /**
     * Guards the reason the helper above has to exist: the raw call it wraps really does throw.
     */
    @Test
    public void qr13_theRawValueOfCall_wouldHaveThrown() {
        try {
            QuotedRepliesOptions.valueOf("SUBSCRIBE_TO_SOMETHING_FROM_A_LATER_RELEASE");
            fail("Enum.valueOf is expected to throw on an unknown name — the guard is what prevents it");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    /**
     * The group bucket parcels enums as ints and writes -1 for unset, then reads that back by
     * comparing against -1 before consulting {@code get(int)}. That encoding is only unambiguous
     * while no real option carries the value -1 — if one ever did, an explicitly-set preference
     * would come back off the wire as unset. This asserts that precondition on the option set.
     *
     * <p>It does <b>not</b> execute the group parcel constructor: no {@code android.os.Parcel}
     * instance can be obtained in this module's JVM unit tests (see the platform note on this
     * class), and the group bucket has no extractable decode helper of the kind the one-to-one
     * bucket exposes. Executing that constructor is part of the device-side follow-up.
     */
    @Test
    public void qr13_theGroupBucketsUnsetSentinel_cannotCollideWithARealOption() {
        for (QuotedRepliesOptions option : QuotedRepliesOptions.values()) {
            assertFalse("option " + option + " collides with the -1 the group bucket writes for unset,"
                            + " so a set preference would decode as unset",
                    option.getValue() == -1);
        }
        assertNull("the sentinel itself must never resolve to an option", QuotedRepliesOptions.get(-1));
    }

    // ==================== PREF-07 / QR-13: explicit nulls in a preference map ====================

    /**
     * Wrapper SDKs (React Native, Flutter) build these maps from JSON, where a JSON {@code null}
     * becomes a key present with a null value rather than an absent key. Such an entry clears
     * {@code containsKey}, and the value then used to be handed straight to an enum's
     * {@code get(int)} resolver, which force-unboxes. {@code fromMap} has no {@code try/catch}, so
     * the resulting {@link NullPointerException} escaped a public static factory.
     *
     * <p>Every field on every bucket shared the pattern, so every field is asserted here rather
     * than only the one the defect was found through.
     */
    @Test
    public void pref07_groupPreferencesFromMap_withEveryValueExplicitlyNull_leavesEveryPreferenceUnset() {
        Map<String, Integer> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REPLIES, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_QUOTED_REPLIES, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REACTIONS, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_LEFT, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_ADDED, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_JOINED, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_KICKED, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_BANNED, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_UNBANNED, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_SCOPE_CHANGED, null);

        GroupPreferences preferences = GroupPreferences.fromMap(map);

        assertNull(preferences.getMessagesPreference());
        assertNull(preferences.getRepliesPreference());
        assertNull("the field this defect was found through", preferences.getQuotedRepliesPreference());
        assertNull(preferences.getReactionsPreference());
        assertNull(preferences.getMemberLeftPreference());
        assertNull(preferences.getMemberAddedPreference());
        assertNull(preferences.getMemberJoinedPreference());
        assertNull(preferences.getMemberKickedPreference());
        assertNull(preferences.getMemberBannedPreference());
        assertNull(preferences.getMemberUnbannedPreference());
        assertNull(preferences.getMemberScopeChangedPreference());
    }

    /**
     * The null tolerance must not turn into blanket tolerance: a real value sitting next to a null
     * one still has to arrive.
     */
    @Test
    public void pref07_groupPreferencesFromMap_withOneNullBesideRealValues_keepsTheRealValues() {
        Map<String, Integer> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES,
                MessagesOptions.SUBSCRIBE_TO_ALL.getValue());
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_QUOTED_REPLIES, null);
        map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_SCOPE_CHANGED,
                MemberActionsOptions.SUBSCRIBE.getValue());

        GroupPreferences preferences = GroupPreferences.fromMap(map);

        assertEquals(MessagesOptions.SUBSCRIBE_TO_ALL, preferences.getMessagesPreference());
        assertNull(preferences.getQuotedRepliesPreference());
        assertEquals(MemberActionsOptions.SUBSCRIBE, preferences.getMemberScopeChangedPreference());
        assertNull("a key that is simply absent stays unset too", preferences.getRepliesPreference());
    }

    @Test
    public void pref07_oneOnOnePreferencesFromMap_withEveryValueExplicitlyNull_leavesEveryPreferenceUnset() {
        Map<String, Integer> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES, null);
        map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES, null);
        map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES, null);
        map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS, null);

        OneOnOnePreferences preferences = OneOnOnePreferences.fromMap(map);

        assertNull(preferences.getMessagesPreference());
        assertNull(preferences.getRepliesPreference());
        assertNull("the field this defect was found through", preferences.getQuotedRepliesPreference());
        assertNull(preferences.getReactionsPreference());
    }

    @Test
    public void pref07_oneOnOnePreferencesFromMap_withOneNullBesideRealValues_keepsTheRealValues() {
        Map<String, Integer> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES,
                QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES.getValue());
        map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES, null);

        OneOnOnePreferences preferences = OneOnOnePreferences.fromMap(map);

        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                preferences.getQuotedRepliesPreference());
        assertNull(preferences.getRepliesPreference());
    }

    @Test
    public void pref07_mutePreferencesFromMap_withExplicitNulls_leavesEveryPreferenceUnset() {
        Map<String, Object> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_DND, null);
        map.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE, null);

        MutePreferences preferences = MutePreferences.fromMap(map);

        assertNull(preferences.getDNDPreference());
        assertNull(preferences.getSchedulePreference());
    }

    /**
     * A schedule whose day entry is null must not take the whole decode down with it; the day
     * decodes to the same fully-closed default an omitted schedule produces.
     */
    @Test
    public void pref07_mutePreferencesFromMap_withANullDayEntry_decodesToTheClosedDefault() {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put(DayOfWeek.MONDAY.getDayName(), null);
        Map<String, Object> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE, schedule);

        MutePreferences preferences = MutePreferences.fromMap(map);

        assertNotNull(preferences.getSchedulePreference());
        DaySchedule monday = preferences.getSchedulePreference().get(DayOfWeek.MONDAY);
        assertNotNull(monday);
        assertEquals(0, monday.getFrom());
        assertEquals(0, monday.getTo());
        assertFalse(monday.getDnd());
    }

    /**
     * A day name the SDK does not recognise (the lookup is exact and case-sensitive) used to be
     * stored under a {@code null} key, which then threw out of {@code toMap()} and
     * {@code writeToParcel()} — far from the payload that caused it. It is dropped instead.
     */
    @Test
    public void pref07_mutePreferencesFromMap_withAnUnrecognisedDayName_dropsThatDayRatherThanKeyingItNull() {
        Map<String, Object> daySchedule = new HashMap<>();
        daySchedule.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM, 900);
        daySchedule.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO, 1700);
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("Monday", daySchedule);
        schedule.put(DayOfWeek.TUESDAY.getDayName(), daySchedule);
        Map<String, Object> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.MutePreferencesKeys.KEY_SCHEDULE, schedule);

        MutePreferences preferences = MutePreferences.fromMap(map);

        assertNotNull(preferences.getSchedulePreference());
        assertFalse("a null key would throw out of toMap()/writeToParcel() later",
                preferences.getSchedulePreference().containsKey(null));
        assertEquals("only the recognised day survives", 1, preferences.getSchedulePreference().size());
        assertNotNull(preferences.getSchedulePreference().get(DayOfWeek.TUESDAY));
        assertEquals(900, preferences.getSchedulePreference().get(DayOfWeek.TUESDAY).getFrom());
        assertNotNull("the surviving schedule must still be usable end to end", preferences.toMap());
    }

    @Test
    public void pref07_dayScheduleFromMap_withExplicitNulls_fallsBackToTheClosedDefault() {
        Map<String, Object> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_FROM, null);
        map.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_TO, null);
        map.put(CometChatNotificationsConstants.DayScheduleKeys.KEY_DND, null);

        DaySchedule schedule = DaySchedule.fromMap(map);

        assertEquals(0, schedule.getFrom());
        assertEquals(0, schedule.getTo());
        assertFalse(schedule.getDnd());
    }

    /**
     * A null section must leave the bucket unset, exactly as an omitted section does (NOTIF-41) —
     * not throw, and not substitute an empty bucket that would be indistinguishable from one the
     * server actually sent.
     */
    @Test
    public void pref07_notificationPreferencesFromMap_withNullSections_leavesThemUnset() {
        Map<String, Object> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE, null);
        map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES, null);
        map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES, null);
        map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES, null);

        NotificationPreferences preferences = NotificationPreferences.fromMap(map);

        assertFalse("the privacy template keeps its documented default", preferences.getUsePrivacyTemplate());
        assertNull(preferences.getOneOnOnePreferences());
        assertNull(preferences.getMutePreferences());
        assertNull(preferences.getGroupPreferences());
    }

    /** QR-20: the deprecated type reaches the same buckets, so it needs the same tolerance. */
    @Test
    public void pref07_pushPreferencesFromMap_withNullSections_leavesThemUnset() {
        Map<String, Object> map = new HashMap<>();
        map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE, null);
        map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES, null);
        map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES, null);
        map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES, null);

        PushPreferences preferences = PushPreferences.fromMap(map);

        assertFalse(preferences.getUsePrivacyTemplate());
        assertNull(preferences.getOneOnOnePreferences());
        assertNull(preferences.getMutePreferences());
        assertNull(preferences.getGroupPreferences());
    }

    /**
     * A nested null section must not resurface as a throw one level down either: the inner bucket
     * receives a null map and has to answer with an all-unset bucket.
     */
    @Test
    public void pref07_aBucketFactory_toleratesANullMap() {
        assertNotNull(GroupPreferences.fromMap(null));
        assertNull(GroupPreferences.fromMap(null).getQuotedRepliesPreference());
        assertNotNull(OneOnOnePreferences.fromMap(null));
        assertNull(OneOnOnePreferences.fromMap(null).getQuotedRepliesPreference());
        assertNotNull(MutePreferences.fromMap(null));
        assertNull(MutePreferences.fromMap(null).getDNDPreference());
        assertNotNull(DaySchedule.fromMap(null));
        assertEquals(0, DaySchedule.fromMap(null).getFrom());
    }

    /**
     * Guards the reason the null checks have to exist: the raw call they replaced really does throw.
     * Without this, a refactor could drop the guards and every assertion above would still pass for
     * the wrong reason.
     */
    @Test
    public void pref07_theRawUnboxingCall_wouldHaveThrown() {
        Integer absentValue = null;
        try {
            QuotedRepliesOptions.get(absentValue);
            fail("unboxing a null Integer into get(int) is expected to throw — the guard is what prevents it");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }
}
