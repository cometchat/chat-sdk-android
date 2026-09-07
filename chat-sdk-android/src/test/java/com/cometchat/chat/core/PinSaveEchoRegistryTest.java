package com.cometchat.chat.core;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin &amp; Save Message / self-echo suppression (parity with the JS SDK's
 * PinSaveEventDispatcher registry).
 *
 * <p>The two bugs this design guards against, and which these vectors pin down:
 * <ol>
 *   <li><b>The registration race</b> — the echo is registered BEFORE the REST write goes out,
 *       because a WebSocket push routinely beats an HTTP round-trip. Registering on the response
 *       would let the early frame through and the acting session would double-fire.</li>
 *   <li><b>The suppression window</b> — consumption is single-use, not a time-window peek. A
 *       window swallows every matching frame inside it, including a genuinely different user's
 *       action on the same message moments after ours.</li>
 * </ol>
 *
 * <p>This test lives in {@code com.cometchat.chat.core} because the registry is package-private.
 * The {@code CometChat} facade itself is not JVM-testable (static Looper init), which is exactly
 * why the registry is its own dependency-free class.
 */
public class PinSaveEchoRegistryTest {

    private static final String PINNED = "pinned";
    private static final String UNPINNED = "unpinned";

    @After
    public void tearDown() {
        // The registry is static state shared across tests.
        PinSaveEchoRegistry.clearAll();
    }

    /** V1 — the race: an entry registered pre-write suppresses a frame arriving before the response. */
    @Test
    public void v1_registeredEcho_suppressesTheMatchingFrame_once() {
        PinSaveEchoRegistry.register(1L, PINNED);

        assertTrue(PinSaveEchoRegistry.consume(1L, PINNED));
        // Single-use: the same registration must not suppress a second, genuine frame.
        assertFalse(PinSaveEchoRegistry.consume(1L, PINNED));
    }

    /** V2 — no registration ⇒ nothing suppressed (a frame from another session always lands). */
    @Test
    public void v2_unregisteredFrame_isNeverSuppressed() {
        assertFalse(PinSaveEchoRegistry.consume(1L, PINNED));
    }

    /** V3 — (messageId, action) is the identity: neither a different message nor a different action matches. */
    @Test
    public void v3_keyIsMessageIdPlusAction() {
        PinSaveEchoRegistry.register(1L, PINNED);

        assertFalse(PinSaveEchoRegistry.consume(2L, PINNED));
        assertFalse(PinSaveEchoRegistry.consume(1L, UNPINNED));
        assertTrue(PinSaveEchoRegistry.consume(1L, PINNED));
    }

    /** V4 — a failed write clears its registration, so the next genuine frame is delivered. */
    @Test
    public void v4_clearedEcho_doesNotSuppress() {
        PinSaveEchoRegistry.register(1L, PINNED);
        PinSaveEchoRegistry.clear(1L, PINNED);

        assertFalse(PinSaveEchoRegistry.consume(1L, PINNED));
    }

    /** V5 — an entry older than the TTL no longer suppresses (a frame lost in transit can't mute forever). */
    @Test
    public void v5_expiredEcho_doesNotSuppress() {
        long t0 = 1_000_000L;
        PinSaveEchoRegistry.register(1L, PINNED, t0);

        assertFalse(PinSaveEchoRegistry.consume(1L, PINNED, t0 + PinSaveEchoRegistry.TTL_MS + 1));
    }

    /** V6 — inside the TTL the entry still counts. */
    @Test
    public void v6_echoWithinTtl_suppresses() {
        long t0 = 1_000_000L;
        PinSaveEchoRegistry.register(1L, PINNED, t0);

        assertTrue(PinSaveEchoRegistry.consume(1L, PINNED, t0 + PinSaveEchoRegistry.TTL_MS));
    }

    /** V7 — the difference from the old window design: consume, then a SECOND frame within what
     * would have been the window still lands. This is user B re-pinning right after user A. */
    @Test
    public void v7_secondFrameInsideTheOldWindow_isDelivered() {
        long t0 = 1_000_000L;
        PinSaveEchoRegistry.register(1L, PINNED, t0);

        assertTrue(PinSaveEchoRegistry.consume(1L, PINNED, t0 + 100));
        // 2 seconds later — inside the old 5s window that would have swallowed this.
        assertFalse(PinSaveEchoRegistry.consume(1L, PINNED, t0 + 2_000));
    }

    /** V8 — registering again refreshes the timestamp rather than stacking entries. */
    @Test
    public void v8_reRegister_refreshesNotStacks() {
        long t0 = 1_000_000L;
        PinSaveEchoRegistry.register(1L, PINNED, t0);
        PinSaveEchoRegistry.register(1L, PINNED, t0 + 1_000);

        assertTrue(PinSaveEchoRegistry.consume(1L, PINNED, t0 + 1_000 + PinSaveEchoRegistry.TTL_MS));
        assertFalse(PinSaveEchoRegistry.consume(1L, PINNED, t0 + 1_000 + PinSaveEchoRegistry.TTL_MS));
    }

    /** V9 — expired entries are pruned on register; live ones survive the prune. */
    @Test
    public void v9_registerPrunesExpired_keepsLive() {
        long t0 = 1_000_000L;
        PinSaveEchoRegistry.register(1L, PINNED, t0);
        PinSaveEchoRegistry.register(2L, PINNED, t0 + PinSaveEchoRegistry.TTL_MS + 5_000);

        assertFalse(PinSaveEchoRegistry.consume(1L, PINNED, t0 + PinSaveEchoRegistry.TTL_MS + 5_000));
        assertTrue(PinSaveEchoRegistry.consume(2L, PINNED, t0 + PinSaveEchoRegistry.TTL_MS + 5_000));
    }

    /** V10 — clearAll (login/logout) empties everything. */
    @Test
    public void v10_clearAll_dropsEverything() {
        PinSaveEchoRegistry.register(1L, PINNED);
        PinSaveEchoRegistry.register(2L, UNPINNED);
        PinSaveEchoRegistry.clearAll();

        assertFalse(PinSaveEchoRegistry.consume(1L, PINNED));
        assertFalse(PinSaveEchoRegistry.consume(2L, UNPINNED));
    }

    /**
     * V11 — conversation echoes are keyed on (type, with) — what the write side knows BEFORE the
     * response delivers the conversationId — and behave exactly like message echoes: single-use,
     * identity includes the action, and a message-id entry can never satisfy a conversation key.
     */
    @Test
    public void v11_conversationKey_isolatedAndSingleUse() {
        String userKey = PinSaveEchoRegistry.conversationKey("user", "cometchat-uid-3");
        PinSaveEchoRegistry.register(userKey, "conversationPinned");

        // Different peer, different type, different action: no match.
        assertFalse(PinSaveEchoRegistry.consume(
            PinSaveEchoRegistry.conversationKey("user", "cometchat-uid-4"), "conversationPinned"));
        assertFalse(PinSaveEchoRegistry.consume(
            PinSaveEchoRegistry.conversationKey("group", "cometchat-uid-3"), "conversationPinned"));
        assertFalse(PinSaveEchoRegistry.consume(userKey, "conversationUnpinned"));

        assertTrue(PinSaveEchoRegistry.consume(userKey, "conversationPinned"));
        assertFalse(PinSaveEchoRegistry.consume(userKey, "conversationPinned"));
    }
}
