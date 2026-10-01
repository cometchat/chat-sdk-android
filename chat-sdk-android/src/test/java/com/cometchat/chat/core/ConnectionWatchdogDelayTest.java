package com.cometchat.chat.core;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.LongRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ENG-39679 — the watchdog for a queued connection method must fire after the method starts.
 *
 * The deadline used to reuse the start delay for any non-zero value, so a method queued with a
 * 5000 ms delay had its watchdog armed at the same instant it began. The watchdog always won:
 * harmless while a late result was still delivered, but once each method completes exactly once
 * it would have made the real result unreachable — every reconnection attempt reporting a
 * timeout whatever actually happened.
 *
 * Drives {@link ConnectionController#watchdogDelay(long)} itself rather than restating the rule.
 */
class ConnectionWatchdogDelayTest {

    private static final long GRACE = 5000;

    @Test
    void immediateMethodKeepsTheFiveSecondWindow() {
        assertEquals(GRACE, ConnectionController.watchdogDelay(0));
    }

    @Test
    void reconnectIntervalGetsAWindowAfterItStarts() {
        // ReconnectionController queues with RECONNECT_INTERVAL = 5000.
        assertEquals(10000, ConnectionController.watchdogDelay(5000));
    }

    @Property
    void watchdogAlwaysFiresAfterTheMethodStarts(@ForAll @LongRange(min = 0, max = 3_600_000) long startDelay) {
        assertTrue(ConnectionController.watchdogDelay(startDelay) > startDelay);
    }

    @Property
    void everyMethodGetsTheSameWindowOnceStarted(@ForAll @LongRange(min = 0, max = 3_600_000) long startDelay) {
        assertEquals(GRACE, ConnectionController.watchdogDelay(startDelay) - startDelay);
    }
}
