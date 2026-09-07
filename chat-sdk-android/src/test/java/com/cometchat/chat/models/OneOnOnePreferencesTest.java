package com.cometchat.chat.models;

import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.ReactionsOptions;
import com.cometchat.chat.enums.RepliesOptions;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Notifications / {@link OneOnOnePreferences} serialization + value semantics.
 *
 * <p>The existing hardening test covers only {@code safeValueOf} (the parcel-name restore path).
 * These cover the JSON and bridge-map round-trips, {@code contentEquals}, {@code clone}, and — the
 * behaviour this thread branch makes newly reachable — an <b>unknown enum code degrades to a null
 * preference</b> rather than throwing, because {@link RepliesOptions#get(int)} returns {@code null}
 * for a value an older build cannot resolve. A true {@code Parcel} round-trip needs the Android
 * runtime and stays in the instrumented suite.
 */
public class OneOnOnePreferencesTest {

    private static OneOnOnePreferences sample() {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setMessagesPreference(MessagesOptions.SUBSCRIBE_TO_ALL);
        prefs.setRepliesPreference(RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS);
        prefs.setReactionsPreference(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES);
        return prefs;
    }

    // ==================== JSON round-trip ====================

    @Test
    public void toJson_fromJson_roundTripsAllThreePreferences() {
        OneOnOnePreferences restored = OneOnOnePreferences.fromJson(sample().toJson());

        assertEquals(MessagesOptions.SUBSCRIBE_TO_ALL, restored.getMessagesPreference());
        assertEquals(RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS, restored.getRepliesPreference());
        assertEquals(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES, restored.getReactionsPreference());
    }

    @Test
    public void toJson_omitsNullPreferences() {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setRepliesPreference(RepliesOptions.DONT_SUBSCRIBE);

        JSONObject json = prefs.toJson();
        assertEquals("only the set preference is serialized", 1, json.length());
    }

    @Test
    public void fromJson_emptyObject_leavesAllPreferencesNull() {
        OneOnOnePreferences prefs = OneOnOnePreferences.fromJson(new JSONObject());
        assertNull(prefs.getMessagesPreference());
        assertNull(prefs.getRepliesPreference());
        assertNull(prefs.getReactionsPreference());
    }

    @Test
    public void fromJson_unknownRepliesCode_degradesToNull_noThrow() throws Exception {
        // A newer backend serves a replies value this build cannot resolve.
        JSONObject json = new JSONObject().put(soleKeyOf(onlyReplies().toJson()), 999);

        OneOnOnePreferences prefs = OneOnOnePreferences.fromJson(json);
        assertNull("an unknown code must leave the preference not-configured, not crash",
                prefs.getRepliesPreference());
    }

    // ==================== bridge-map round-trip ====================

    @Test
    public void toMap_fromMap_roundTripsAllThreePreferences() {
        OneOnOnePreferences restored = OneOnOnePreferences.fromMap(sample().toMap());

        assertEquals(MessagesOptions.SUBSCRIBE_TO_ALL, restored.getMessagesPreference());
        assertEquals(RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS, restored.getRepliesPreference());
        assertEquals(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES, restored.getReactionsPreference());
    }

    @Test
    public void toMap_omitsNullPreferences() {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setMessagesPreference(MessagesOptions.DONT_SUBSCRIBE);
        assertEquals(1, prefs.toMap().size());
    }

    @Test
    public void fromMap_unknownRepliesCode_degradesToNull() {
        Map<String, Integer> map = onlyReplies().toMap();
        map.put(soleKeyOf(map), 999); // clobber the sole entry's value with an unknown code

        assertNull(OneOnOnePreferences.fromMap(map).getRepliesPreference());
    }

    // ==================== contentEquals / equals ====================

    @Test
    public void contentEquals_trueForSameValues_falsePerField() {
        assertTrue(sample().contentEquals(sample()));
        assertEquals("equals() must delegate to contentEquals()", sample(), sample());

        OneOnOnePreferences diffMessages = sample();
        diffMessages.setMessagesPreference(MessagesOptions.DONT_SUBSCRIBE);
        assertFalse(sample().contentEquals(diffMessages));

        OneOnOnePreferences diffReplies = sample();
        diffReplies.setRepliesPreference(RepliesOptions.DONT_SUBSCRIBE);
        assertFalse(sample().contentEquals(diffReplies));

        OneOnOnePreferences diffReactions = sample();
        diffReactions.setReactionsPreference(ReactionsOptions.DONT_SUBSCRIBE);
        assertFalse(sample().contentEquals(diffReactions));
    }

    @Test
    public void contentEquals_nullPreferenceVsSet_isFalse() {
        assertFalse(sample().contentEquals(new OneOnOnePreferences()));
    }

    @Test
    public void contentEquals_nullAndWrongType_areFalseNotThrow() {
        assertFalse(sample().contentEquals(null));
        assertFalse(sample().contentEquals("not a preferences object"));
    }

    // ==================== clone ====================

    @Test
    public void clone_equalsOriginal_butIsIndependent() {
        OneOnOnePreferences original = sample();
        OneOnOnePreferences copy = original.clone();

        assertNotSame(original, copy);
        assertTrue(original.contentEquals(copy));

        copy.setRepliesPreference(RepliesOptions.DONT_SUBSCRIBE);
        assertEquals("mutating the clone must not touch the original",
                RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS, original.getRepliesPreference());
    }

    // ==================== helpers ====================

    private static OneOnOnePreferences onlyReplies() {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setRepliesPreference(RepliesOptions.SUBSCRIBE_TO_ALL);
        return prefs;
    }

    private static String soleKeyOf(JSONObject json) {
        return json.keys().next();
    }

    private static String soleKeyOf(Map<String, Integer> map) {
        return map.keySet().iterator().next();
    }
}
