package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Feature Area: Presence / Real-time presence updates.
 *
 * <p>Covers:
 * <ul>
 *   <li>PRES-01 — the affected user is extracted from the event body (not the whole envelope),
 *       with status and last-active time updated.</li>
 *   <li>PRES-02 — a legacy "available" action is normalized to "online", and last-active comes
 *       from the event's own timestamp.</li>
 *   <li>PRES-03 — an unrecognized status value is passed through unchanged.</li>
 * </ul>
 *
 * <p>This test lives in {@code com.cometchat.chat.core} because {@code CometChatPresenceEvent}
 * and its {@code fromJSON} factory are package-private.
 */
public class PresenceEventTest {

    private JSONObject presenceEnvelope(String action, long timestamp, String userUid) throws Exception {
        JSONObject body = new JSONObject()
                .put(CometChatConstants.WSKeys.KEY_ACTION, action)
                .put(CometChatConstants.WSKeys.KEY_TIMESTAMP, timestamp)
                .put(CometChatConstants.WSKeys.KEY_USER, new JSONObject()
                        .put(CometChatConstants.UserKeys.USER_KEY_UID, userUid)
                        .put(CometChatConstants.UserKeys.USER_KEY_NAME, "Presence " + userUid));

        return new JSONObject()
                .put(CometChatConstants.WSKeys.KEY_APP_ID, "app-123")
                .put(CometChatConstants.WSKeys.KEY_DEVICE_ID, "device-abc")
                .put(CometChatConstants.WSKeys.KEY_SENDER, userUid)
                .put(CometChatConstants.WSKeys.KEY_BODY, body);
    }

    // ==================== PRES-01: affected user extracted from body ====================

    @Test
    public void pres01_extractsTheAffectedUserFromTheBody() throws Exception {
        CometChatPresenceEvent event =
                CometChatPresenceEvent.fromJSON(presenceEnvelope("offline", 1700000000L, "contact-1"));

        assertNotNull("the affected user must be extracted from the event body", event.getUser());
        assertEquals("contact-1", event.getUser().getUid());
        assertEquals("offline", event.getUser().getStatus());
    }

    @Test
    public void pres01_updatesLastActiveTimeFromTheEvent() throws Exception {
        CometChatPresenceEvent event =
                CometChatPresenceEvent.fromJSON(presenceEnvelope("offline", 1700000500L, "contact-2"));

        assertEquals("last-active must come from the event timestamp",
                1700000500L, event.getUser().getLastActiveAt());
        assertEquals(1700000500L, event.getTimestamp());
    }

    // ==================== PRES-02: legacy "available" normalized ====================

    @Test
    public void pres02_availableActionIsNormalizedToOnline() throws Exception {
        CometChatPresenceEvent event =
                CometChatPresenceEvent.fromJSON(presenceEnvelope("available", 1700000000L, "contact-3"));

        assertEquals("the legacy 'available' action must map to 'online'",
                "online", event.getUser().getStatus());
    }

    @Test
    public void pres02_lastActiveComesFromEventNotStaleUserValue() throws Exception {
        CometChatPresenceEvent event =
                CometChatPresenceEvent.fromJSON(presenceEnvelope("available", 1699999999L, "contact-4"));

        assertEquals(1699999999L, event.getUser().getLastActiveAt());
    }

    // ==================== PRES-03: unrecognized status passed through ====================

    @Test
    public void pres03_unrecognizedStatusIsPassedThroughUnchanged() throws Exception {
        CometChatPresenceEvent event =
                CometChatPresenceEvent.fromJSON(presenceEnvelope("in-a-meeting", 1700000000L, "contact-5"));

        assertEquals("an unrecognized presence value must not be discarded or remapped",
                "in-a-meeting", event.getUser().getStatus());
    }
}
