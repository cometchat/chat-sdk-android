package com.cometchat.chat.presence;

import com.cometchat.chat.models.UserPresence;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Presence / Presence snapshot model.
 *
 * <p>Covers:
 * <ul>
 *   <li>PRES-04 — a presence snapshot exposes identifier/status/last-active; cloning yields an
 *       equal-but-separate copy and two identical snapshots are equal.</li>
 *   <li>PRES-05 — missing fields default to unset rather than causing an error.</li>
 * </ul>
 */
public class UserPresenceTest {

    private UserPresence snapshot(String jid, String status, long lastActive) {
        UserPresence p = new UserPresence();
        p.setJid(jid);
        p.setStatus(status);
        p.setLastActiveAt(lastActive);
        return p;
    }

    // ==================== PRES-04: snapshot value semantics ====================

    @Test
    public void pres04_snapshotExposesIdentifierStatusAndLastActive() {
        UserPresence p = snapshot("user-1@host", "online", 1700000000L);

        assertEquals("user-1@host", p.getJid());
        assertEquals("online", p.getStatus());
        assertEquals(1700000000L, p.getLastActiveAt());
    }

    @Test
    public void pres04_twoIdenticalSnapshotsAreEqual() {
        UserPresence a = snapshot("user-1@host", "online", 1700000000L);
        UserPresence b = snapshot("user-1@host", "online", 1700000000L);

        assertTrue(a.contentEquals(b));
    }

    @Test
    public void pres04_snapshotsDifferingInOneFieldAreNotEqual() {
        UserPresence online = snapshot("user-1@host", "online", 1700000000L);
        UserPresence offline = snapshot("user-1@host", "offline", 1700000000L);

        assertFalse(online.contentEquals(offline));
    }

    @Test
    public void pres04_cloningAStatusChange_producesASeparateSnapshot() {
        UserPresence original = snapshot("user-1@host", "online", 1700000000L);
        UserPresence updated = original.clone();
        updated.setStatus("offline");

        assertNotNull(updated);
        assertEquals("the original snapshot must not be mutated", "online", original.getStatus());
        assertEquals("offline", updated.getStatus());
        assertFalse(original.contentEquals(updated));
    }

    // ==================== PRES-05: missing fields default to unset ====================

    @Test
    public void pres05_missingFieldsDefaultToUnsetWithoutError() {
        UserPresence p = new UserPresence();

        assertNull("an unset identifier stays null", p.getJid());
        assertNull("an unset status stays null", p.getStatus());
        assertEquals("an unset last-active is zero", 0L, p.getLastActiveAt());
    }
}
