package com.cometchat.chat.notifications;

import com.cometchat.chat.enums.DNDOptions;
import com.cometchat.chat.enums.DayOfWeek;
import com.cometchat.chat.enums.FeedReadState;
import com.cometchat.chat.enums.MemberActionsOptions;
import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.QuotedRepliesOptions;
import com.cometchat.chat.enums.ReactionsOptions;
import com.cometchat.chat.enums.RepliesOptions;
import com.cometchat.chat.models.GroupPreferences;
import com.cometchat.chat.models.OneOnOnePreferences;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Feature Area: Notifications / Read-state filter and preference enums.
 *
 * <p>Covers:
 * <ul>
 *   <li>NOTIF-16 — the read-state filter transmits its values exactly and treats an unrecognized
 *       value as unset.</li>
 *   <li>NOTIF-42 — per-category subscription levels accept only documented values.</li>
 *   <li>NOTIF-43 — do-not-disturb states and day-of-week names accept only valid values.</li>
 *   <li>QR-6 — the quoted-replies preference carries exactly four values, on the fixed wire
 *       numbers 1-4.</li>
 *   <li>QR-8 — quoted replies has its own dedicated option set, which cannot be interchanged with
 *       the threaded-replies option set.</li>
 *   <li>QR-13 — an unrecognised quoted-replies value degrades to unset rather than failing.</li>
 * </ul>
 */
public class NotificationEnumsTest {

    // ==================== NOTIF-16: feed read-state filter ====================

    @Test
    public void notif16_readStateValues_areTransmittedExactly() {
        assertEquals("read", FeedReadState.READ.getValue());
        assertEquals("unread", FeedReadState.UNREAD.getValue());
        assertEquals("all", FeedReadState.ALL.getValue());
    }

    @Test
    public void notif16_recognizedReadStateValues_resolve() {
        assertEquals(FeedReadState.READ, FeedReadState.get("read"));
        assertEquals(FeedReadState.UNREAD, FeedReadState.get("unread"));
        assertEquals(FeedReadState.ALL, FeedReadState.get("all"));
    }

    @Test
    public void notif16_unrecognizedReadState_resolvesToNull() {
        assertNull("an unrecognized read-state value is treated as unset", FeedReadState.get("archived"));
    }

    // ==================== NOTIF-42: subscription levels ====================

    @Test
    public void notif42_messageSubscriptionLevels_mapToTheirDocumentedValues() {
        assertEquals(MessagesOptions.DONT_SUBSCRIBE, MessagesOptions.get(1));
        assertEquals(MessagesOptions.SUBSCRIBE_TO_ALL, MessagesOptions.get(2));
        assertEquals(MessagesOptions.SUBSCRIBE_TO_MENTIONS, MessagesOptions.get(3));
    }

    @Test
    public void notif42_replyAndReactionLevels_mapToTheirDocumentedValues() {
        assertEquals(RepliesOptions.SUBSCRIBE_TO_ALL, RepliesOptions.get(2));
        assertEquals(ReactionsOptions.SUBSCRIBE_TO_REACTIONS_ON_ALL_MESSAGES, ReactionsOptions.get(3));
        assertEquals(MemberActionsOptions.SUBSCRIBE, MemberActionsOptions.get(2));
    }

    @Test
    public void notif42_subscribedThreadsReplyLevel_mapsToValueFour() {
        assertEquals(4, RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS.getValue());
        assertEquals(RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS, RepliesOptions.get(4));
    }

    @Test
    public void notif42_allReplyLevels_mapToTheirDocumentedValues() {
        assertEquals(RepliesOptions.DONT_SUBSCRIBE, RepliesOptions.get(1));
        assertEquals(RepliesOptions.SUBSCRIBE_TO_ALL, RepliesOptions.get(2));
        assertEquals(RepliesOptions.SUBSCRIBE_TO_MENTIONS, RepliesOptions.get(3));
        assertEquals(RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS, RepliesOptions.get(4));
        assertNull("a value past the documented range is not silently applied", RepliesOptions.get(5));
    }

    @Test
    public void notif42_unrecognizedSubscriptionLevel_isRejectedAsNull() {
        assertNull("an out-of-range level is not silently applied", MessagesOptions.get(99));
        assertNull(RepliesOptions.get(0));
        assertNull(ReactionsOptions.get(-1));
        assertNull(MemberActionsOptions.get(5));
    }

    // ==================== NOTIF-43: DND states and day-of-week ====================

    @Test
    public void notif43_dndStates_mapToTheirDocumentedValues() {
        assertEquals(DNDOptions.DISABLED, DNDOptions.get(1));
        assertEquals(DNDOptions.ENABLED, DNDOptions.get(2));
        assertNull("an unrecognized DND value is rejected", DNDOptions.get(7));
    }

    @Test
    public void notif43_validDayNames_resolve() {
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.get("monday"));
        assertEquals(DayOfWeek.SUNDAY, DayOfWeek.get("sunday"));
    }

    @Test
    public void notif43_invalidOrMiscasedDayNames_areRejected() {
        assertNull("a misspelled day is rejected", DayOfWeek.get("funday"));
        assertNull("a mis-cased day is rejected (lookup is case-sensitive)", DayOfWeek.get("Monday"));
    }

    // ==================== QR-6: the four quoted-replies values ====================

    @Test
    public void qr6_quotedReplyLevels_mapToTheirDocumentedValues() {
        assertEquals(QuotedRepliesOptions.DONT_SUBSCRIBE, QuotedRepliesOptions.get(1));
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_ALL, QuotedRepliesOptions.get(2));
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_MENTIONS, QuotedRepliesOptions.get(3));
        assertEquals("value 4 notifies the author when their own message is quoted",
                QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES, QuotedRepliesOptions.get(4));
    }

    @Test
    public void qr6_quotedReplyLevels_transmitTheirWireNumbersExactly() {
        assertEquals(1, QuotedRepliesOptions.DONT_SUBSCRIBE.getValue());
        assertEquals(2, QuotedRepliesOptions.SUBSCRIBE_TO_ALL.getValue());
        assertEquals(3, QuotedRepliesOptions.SUBSCRIBE_TO_MENTIONS.getValue());
        assertEquals(4, QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES.getValue());
    }

    @Test
    public void qr6_theOptionSetHasExactlyFourMembers() {
        assertEquals("a fifth option would be a wire-contract change",
                4, QuotedRepliesOptions.values().length);
    }

    // ==================== QR-13: unrecognised quoted-replies value ====================

    @Test
    public void qr13_unrecognisedQuotedReplyLevel_isTreatedAsUnset() {
        assertNull("an out-of-range level is not silently applied", QuotedRepliesOptions.get(0));
        assertNull(QuotedRepliesOptions.get(5));
        assertNull(QuotedRepliesOptions.get(-1));
        assertNull(QuotedRepliesOptions.get(99));
    }

    // ==================== QR-8: a dedicated, non-interchangeable option set ====================

    // QR-8 mandates that the two option sets are distinct, non-interchangeable types. It does not
    // freeze the membership of the threaded-replies set: that set is owned by the threaded-replies
    // preference and is expected to grow independently. Do not assert its arity, or the value range
    // of its get(int), from a quoted-replies test — that couples this feature to an unrelated one
    // and fails the moment the other set gains a member.

    @Test
    public void qr8_theTwoOptionSets_areDistinctTypes() {
        assertFalse("quoted replies must not be an alias of threaded replies",
                RepliesOptions.class.equals(QuotedRepliesOptions.class));
        assertFalse(RepliesOptions.class.isAssignableFrom(QuotedRepliesOptions.class));
        assertFalse(QuotedRepliesOptions.class.isAssignableFrom(RepliesOptions.class));
    }

    /**
     * QR-8 is enforced by the type system: the compiler rejects a quoted-replies value passed to the
     * threaded-replies setter, and vice versa. That cannot be written as a failing statement in a
     * test that must compile, so the accessor signatures are asserted instead — the absence of a
     * cross-typed overload is what makes the mix-up a compile error rather than a silent misconfig.
     */
    @Test
    public void qr8_neitherPreferenceAcceptsTheOtherOptionSet() {
        assertSetterAcceptsExactly(OneOnOnePreferences.class, "setQuotedRepliesPreference", QuotedRepliesOptions.class);
        assertSetterAcceptsExactly(OneOnOnePreferences.class, "setRepliesPreference", RepliesOptions.class);
        assertSetterAcceptsExactly(GroupPreferences.class, "setQuotedRepliesPreference", QuotedRepliesOptions.class);
        assertSetterAcceptsExactly(GroupPreferences.class, "setRepliesPreference", RepliesOptions.class);

        assertNoSuchSetter(OneOnOnePreferences.class, "setRepliesPreference", QuotedRepliesOptions.class);
        assertNoSuchSetter(OneOnOnePreferences.class, "setQuotedRepliesPreference", RepliesOptions.class);
        assertNoSuchSetter(GroupPreferences.class, "setRepliesPreference", QuotedRepliesOptions.class);
        assertNoSuchSetter(GroupPreferences.class, "setQuotedRepliesPreference", RepliesOptions.class);
    }

    @Test
    public void qr8_theQuotedReplyGetters_returnTheDedicatedOptionSet() throws Exception {
        assertEquals(QuotedRepliesOptions.class,
                OneOnOnePreferences.class.getMethod("getQuotedRepliesPreference").getReturnType());
        assertEquals(QuotedRepliesOptions.class,
                GroupPreferences.class.getMethod("getQuotedRepliesPreference").getReturnType());
    }

    private static void assertSetterAcceptsExactly(Class<?> owner, String setter, Class<?> optionSet) {
        try {
            owner.getMethod(setter, optionSet);
        } catch (NoSuchMethodException e) {
            fail(owner.getSimpleName() + "." + setter + " must accept " + optionSet.getSimpleName());
        }
    }

    private static void assertNoSuchSetter(Class<?> owner, String setter, Class<?> optionSet) {
        try {
            owner.getMethod(setter, optionSet);
            fail(owner.getSimpleName() + "." + setter + " must not accept " + optionSet.getSimpleName()
                    + " — a shared option set makes a wrong value settable with no error at any layer");
        } catch (NoSuchMethodException expected) {
            // expected: the mix-up is a compile error, not a silent misconfiguration
        }
    }
}
