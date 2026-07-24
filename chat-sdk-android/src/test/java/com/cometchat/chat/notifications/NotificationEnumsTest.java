package com.cometchat.chat.notifications;

import com.cometchat.chat.enums.DNDOptions;
import com.cometchat.chat.enums.DayOfWeek;
import com.cometchat.chat.enums.FeedReadState;
import com.cometchat.chat.enums.MemberActionsOptions;
import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.ReactionsOptions;
import com.cometchat.chat.enums.RepliesOptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Feature Area: Notifications / Read-state filter and preference enums.
 *
 * <p>Covers:
 * <ul>
 *   <li>NOTIF-16 — the read-state filter transmits its values exactly and treats an unrecognized
 *       value as unset.</li>
 *   <li>NOTIF-42 — per-category subscription levels accept only documented values.</li>
 *   <li>NOTIF-43 — do-not-disturb states and day-of-week names accept only valid values.</li>
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
}
