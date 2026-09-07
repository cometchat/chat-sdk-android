package com.cometchat.chat.core;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.jqwik.api.Assume;

/**
 * Property-based tests for {@link PinSaveEchoRegistry} — the echo-suppression window that keeps a
 * session's own pin/save emits from double-firing its listeners.
 *
 * <p>The invariants under test, for ANY id/action/timing: an echo registered at {@code t} is
 * consumable exactly once within {@code TTL_MS}, never after it, and only under the exact
 * (id, action) it was registered with. Example-based coverage lives in
 * {@code PinSaveEchoRegistryTest}; these properties sweep timings and key combinations.
 *
 * <p>The registry is static — every property clears it first so tries stay independent.
 */
class PinSaveEchoRegistryPropertyTest {

    // A fixed action vocabulary mirrors the real emit sites and keeps keys collision-free.
    private static final String[] ACTIONS = {"pinned", "unpinned", "saved", "unsaved"};

    @Property
    void registeredEchoIsConsumableExactlyOnceWithinTtl(
            @ForAll @LongRange(min = 1, max = 1_000_000_000L) long messageId,
            @ForAll @LongRange(min = 0, max = PinSaveEchoRegistry.TTL_MS) long dt,
            @ForAll @IntRange(min = 0, max = 3) int actionIndex) {
        PinSaveEchoRegistry.clearAll();
        String action = ACTIONS[actionIndex];
        long t0 = 1_000_000L;

        PinSaveEchoRegistry.register(messageId, action, t0);

        assertTrue(PinSaveEchoRegistry.consume(messageId, action, t0 + dt),
                "echo within TTL must be consumed");
        assertFalse(PinSaveEchoRegistry.consume(messageId, action, t0 + dt),
                "echo is single-use — second consume must miss");
    }

    @Property
    void echoIsNeverConsumableAfterTtl(
            @ForAll @LongRange(min = 1, max = 1_000_000_000L) long messageId,
            @ForAll @LongRange(min = PinSaveEchoRegistry.TTL_MS + 1, max = 1_000_000_000L) long dt,
            @ForAll @IntRange(min = 0, max = 3) int actionIndex) {
        PinSaveEchoRegistry.clearAll();
        String action = ACTIONS[actionIndex];
        long t0 = 1_000_000L;

        PinSaveEchoRegistry.register(messageId, action, t0);

        assertFalse(PinSaveEchoRegistry.consume(messageId, action, t0 + dt),
                "echo past TTL must not be consumed");
    }

    @Property
    void differentActionOrIdNeverConsumesTheEcho(
            @ForAll @LongRange(min = 1, max = 1_000_000_000L) long messageId,
            @ForAll @LongRange(min = 1, max = 1_000_000_000L) long otherMessageId,
            @ForAll @IntRange(min = 0, max = 3) int actionIndex,
            @ForAll @IntRange(min = 0, max = 3) int otherActionIndex) {
        PinSaveEchoRegistry.clearAll();
        String action = ACTIONS[actionIndex];
        String otherAction = ACTIONS[otherActionIndex];
        Assume.that(messageId != otherMessageId || !action.equals(otherAction));
        long t0 = 1_000_000L;

        PinSaveEchoRegistry.register(messageId, action, t0);

        assertFalse(PinSaveEchoRegistry.consume(otherMessageId, otherAction, t0),
                "a different (id, action) must never match the echo");
        assertTrue(PinSaveEchoRegistry.consume(messageId, action, t0),
                "the miss above must not have consumed the real echo");
    }

    @Property
    void clearedEchoIsNeverConsumable(
            @ForAll @LongRange(min = 1, max = 1_000_000_000L) long messageId,
            @ForAll @LongRange(min = 0, max = PinSaveEchoRegistry.TTL_MS) long dt,
            @ForAll @IntRange(min = 0, max = 3) int actionIndex) {
        PinSaveEchoRegistry.clearAll();
        String action = ACTIONS[actionIndex];
        long t0 = 1_000_000L;

        PinSaveEchoRegistry.register(messageId, action, t0);
        PinSaveEchoRegistry.clear(messageId, action);

        assertFalse(PinSaveEchoRegistry.consume(messageId, action, t0 + dt),
                "a cleared echo (failed write) must never suppress a frame");
    }

    /** Re-registering refreshes the window: consumability follows the LATEST registration time. */
    @Property
    void reRegisteringRefreshesTheTtlWindow(
            @ForAll @LongRange(min = 1, max = 1_000_000_000L) long messageId,
            @ForAll @LongRange(min = 1, max = PinSaveEchoRegistry.TTL_MS) long gap,
            @ForAll @IntRange(min = 0, max = 3) int actionIndex) {
        PinSaveEchoRegistry.clearAll();
        String action = ACTIONS[actionIndex];
        long t0 = 1_000_000L;

        PinSaveEchoRegistry.register(messageId, action, t0);
        PinSaveEchoRegistry.register(messageId, action, t0 + gap);

        // TTL_MS past the SECOND registration is still consumable, even when it is past the first.
        assertTrue(PinSaveEchoRegistry.consume(messageId, action, t0 + gap + PinSaveEchoRegistry.TTL_MS),
                "TTL must be measured from the latest registration");
    }
}
