package com.cometchat.chat.notifications;

import android.os.Parcel;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.cometchat.chat.enums.MemberActionsOptions;
import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.QuotedRepliesOptions;
import com.cometchat.chat.enums.ReactionsOptions;
import com.cometchat.chat.enums.RepliesOptions;
import com.cometchat.chat.models.GroupPreferences;
import com.cometchat.chat.models.OneOnOnePreferences;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Feature Area: Notifications &amp; Preferences / object transfer (Parcelable).
 *
 * <p>Covers QR-13 and QR-19 of the quoted-replies specification on the one path the JVM unit tests
 * structurally cannot reach. {@code android.os.Parcel} is a stub under this module's unit tests
 * ("Method obtain in android.os.Parcel not mocked") and there is no Robolectric on the unit-test
 * classpath, so {@code writeToParcel} has never been executed by any test. This runs it on a device.
 *
 * <p>Two failure modes are the reason it exists, and neither is visible to the unit suite:
 * <ul>
 *   <li>a value written into the wrong slot, or a field added to the parcel <i>read</i> and missed
 *       in the <i>write</i> — every assertion here carries populated sibling fields so the offset
 *       shift such a bug produces is caught rather than silently absorbed;</li>
 *   <li>the one-to-one bucket parcels enums by {@code name()} and reads them back through
 *       {@code Enum.valueOf}, which throws on a name it does not know. A parcel written by a newer
 *       build must degrade to unset (QR-13), not crash the reader.</li>
 * </ul>
 *
 * <p>These tests are pure in-process object transfer: no backend, no login, no ordering
 * relationship with the live end-to-end suite. They are deliberately not members of
 * {@code CometChatTestSuite}, which is a sequenced live-backend run.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificationPreferencesParcelTest {

    // ==================== one-to-one bucket ====================

    /** QR-19: every option survives the round trip, with the sibling fields populated alongside. */
    @Test
    public void a1_oneOnOne_everyQuotedRepliesOption_survivesTheRoundTrip() {
        for (QuotedRepliesOptions option : QuotedRepliesOptions.values()) {
            OneOnOnePreferences original = new OneOnOnePreferences();
            original.setMessagesPreference(MessagesOptions.SUBSCRIBE_TO_MENTIONS);
            original.setRepliesPreference(RepliesOptions.SUBSCRIBE_TO_ALL);
            original.setQuotedRepliesPreference(option);
            original.setReactionsPreference(ReactionsOptions.DONT_SUBSCRIBE);

            OneOnOnePreferences restored = roundTrip(original, OneOnOnePreferences.CREATOR);

            assertEquals("quoted replies " + option + " did not survive the parcel",
                    option, restored.getQuotedRepliesPreference());
            assertEquals("a sibling field shifted slot while parcelling " + option,
                    MessagesOptions.SUBSCRIBE_TO_MENTIONS, restored.getMessagesPreference());
            assertEquals(RepliesOptions.SUBSCRIBE_TO_ALL, restored.getRepliesPreference());
            assertEquals(ReactionsOptions.DONT_SUBSCRIBE, restored.getReactionsPreference());
            assertEquals("the whole bucket must compare equal after a round trip", original, restored);
        }
    }

    /** An unset preference must stay unset — not become a default — across the round trip. */
    @Test
    public void a2_oneOnOne_anUnsetQuotedRepliesPreference_staysUnset() {
        OneOnOnePreferences original = new OneOnOnePreferences();
        original.setMessagesPreference(MessagesOptions.SUBSCRIBE_TO_ALL);
        original.setReactionsPreference(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES);

        OneOnOnePreferences restored = roundTrip(original, OneOnOnePreferences.CREATOR);

        assertNull("unset must not decode as a default", restored.getQuotedRepliesPreference());
        assertNull(restored.getRepliesPreference());
        assertEquals("the set siblings must be unaffected by the unset one",
                MessagesOptions.SUBSCRIBE_TO_ALL, restored.getMessagesPreference());
        assertEquals(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES, restored.getReactionsPreference());
    }

    /** An entirely unset bucket must round trip without decoding anything. */
    @Test
    public void a3_oneOnOne_anEmptyBucket_roundTripsUnset() {
        OneOnOnePreferences restored = roundTrip(new OneOnOnePreferences(), OneOnOnePreferences.CREATOR);

        assertNull(restored.getMessagesPreference());
        assertNull(restored.getRepliesPreference());
        assertNull(restored.getQuotedRepliesPreference());
        assertNull(restored.getReactionsPreference());
    }

    /**
     * QR-13. The one-to-one bucket parcels enums by name, so a parcel written by a build that knows
     * a quoted-replies value this one does not carries a name {@code Enum.valueOf} would throw on.
     * The bucket must read it as unset instead. The parcel is hand-written in exactly the layout
     * {@code writeToParcel} produces so the reader is exercised for real.
     */
    @Test
    public void a4_oneOnOne_anUnknownQuotedRepliesName_degradesToUnsetWithoutThrowing() {
        Parcel parcel = Parcel.obtain();
        try {
            parcel.writeString(MessagesOptions.SUBSCRIBE_TO_ALL.name());
            parcel.writeString(RepliesOptions.DONT_SUBSCRIBE.name());
            parcel.writeString("SUBSCRIBE_TO_SOMETHING_FROM_A_LATER_RELEASE");
            parcel.writeString(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES.name());
            parcel.setDataPosition(0);

            OneOnOnePreferences restored = OneOnOnePreferences.CREATOR.createFromParcel(parcel);

            assertNull("an unknown constant name must degrade to unset, not throw",
                    restored.getQuotedRepliesPreference());
            assertEquals("the reader must not lose its place after the unknown name",
                    MessagesOptions.SUBSCRIBE_TO_ALL, restored.getMessagesPreference());
            assertEquals(RepliesOptions.DONT_SUBSCRIBE, restored.getRepliesPreference());
            assertEquals(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES, restored.getReactionsPreference());
        } finally {
            parcel.recycle();
        }
    }

    // ==================== group bucket ====================

    /**
     * QR-19 for the group bucket, which parcels enums as ints with -1 for unset. All seven member
     * fields are populated with distinct-position values so a wrong-slot write shows up.
     */
    @Test
    public void b1_group_everyQuotedRepliesOption_survivesTheRoundTrip() {
        for (QuotedRepliesOptions option : QuotedRepliesOptions.values()) {
            GroupPreferences original = populatedGroupPreferences();
            original.setQuotedRepliesPreference(option);

            GroupPreferences restored = roundTrip(original, GroupPreferences.CREATOR);

            assertEquals("quoted replies " + option + " did not survive the parcel",
                    option, restored.getQuotedRepliesPreference());
            assertEquals("the whole bucket must compare equal after a round trip", original, restored);
            assertSiblingsSurvived(restored);
        }
    }

    /** An unset group preference must stay unset while its populated siblings survive. */
    @Test
    public void b2_group_anUnsetQuotedRepliesPreference_staysUnset() {
        GroupPreferences original = populatedGroupPreferences();

        GroupPreferences restored = roundTrip(original, GroupPreferences.CREATOR);

        assertNull("unset must not decode as a default", restored.getQuotedRepliesPreference());
        assertSiblingsSurvived(restored);
    }

    /** An entirely unset bucket must round trip with every field still unset. */
    @Test
    public void b3_group_anEmptyBucket_roundTripsUnset() {
        GroupPreferences restored = roundTrip(new GroupPreferences(), GroupPreferences.CREATOR);

        assertNull(restored.getMessagesPreference());
        assertNull(restored.getRepliesPreference());
        assertNull(restored.getQuotedRepliesPreference());
        assertNull(restored.getReactionsPreference());
        assertNull(restored.getMemberLeftPreference());
        assertNull(restored.getMemberAddedPreference());
        assertNull(restored.getMemberJoinedPreference());
        assertNull(restored.getMemberKickedPreference());
        assertNull(restored.getMemberBannedPreference());
        assertNull(restored.getMemberUnbannedPreference());
        assertNull(restored.getMemberScopeChangedPreference());
    }

    // ==================== helpers ====================

    /**
     * Writes through {@code writeToParcel} and reads back through the class's own {@code CREATOR},
     * which is the pairing a wrong-slot write breaks.
     */
    private static <T extends android.os.Parcelable> T roundTrip(T original, android.os.Parcelable.Creator<T> creator) {
        Parcel parcel = Parcel.obtain();
        try {
            original.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            return creator.createFromParcel(parcel);
        } finally {
            parcel.recycle();
        }
    }

    /**
     * A group bucket with every field except quoted replies set, alternating between the two
     * member-action values so a field read from a neighbouring slot yields the wrong constant
     * rather than coincidentally the right one.
     */
    private static GroupPreferences populatedGroupPreferences() {
        GroupPreferences preferences = new GroupPreferences();
        preferences.setMessagesPreference(MessagesOptions.SUBSCRIBE_TO_MENTIONS);
        preferences.setRepliesPreference(RepliesOptions.DONT_SUBSCRIBE);
        preferences.setReactionsPreference(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES);
        preferences.setMemberLeftPreference(MemberActionsOptions.SUBSCRIBE);
        preferences.setMemberAddedPreference(MemberActionsOptions.DONT_SUBSCRIBE);
        preferences.setMemberJoinedPreference(MemberActionsOptions.SUBSCRIBE);
        preferences.setMemberKickedPreference(MemberActionsOptions.DONT_SUBSCRIBE);
        preferences.setMemberBannedPreference(MemberActionsOptions.SUBSCRIBE);
        preferences.setMemberUnbannedPreference(MemberActionsOptions.DONT_SUBSCRIBE);
        preferences.setMemberScopeChangedPreference(MemberActionsOptions.SUBSCRIBE);
        return preferences;
    }

    /** Asserts the layout {@link #populatedGroupPreferences()} wrote came back slot for slot. */
    private static void assertSiblingsSurvived(GroupPreferences restored) {
        assertEquals(MessagesOptions.SUBSCRIBE_TO_MENTIONS, restored.getMessagesPreference());
        assertEquals(RepliesOptions.DONT_SUBSCRIBE, restored.getRepliesPreference());
        assertEquals(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES, restored.getReactionsPreference());
        assertEquals(MemberActionsOptions.SUBSCRIBE, restored.getMemberLeftPreference());
        assertEquals(MemberActionsOptions.DONT_SUBSCRIBE, restored.getMemberAddedPreference());
        assertEquals(MemberActionsOptions.SUBSCRIBE, restored.getMemberJoinedPreference());
        assertEquals(MemberActionsOptions.DONT_SUBSCRIBE, restored.getMemberKickedPreference());
        assertEquals(MemberActionsOptions.SUBSCRIBE, restored.getMemberBannedPreference());
        assertEquals(MemberActionsOptions.DONT_SUBSCRIBE, restored.getMemberUnbannedPreference());
        assertEquals(MemberActionsOptions.SUBSCRIBE, restored.getMemberScopeChangedPreference());
    }
}
