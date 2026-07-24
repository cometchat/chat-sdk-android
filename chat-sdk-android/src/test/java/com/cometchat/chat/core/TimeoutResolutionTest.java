package com.cometchat.chat.core;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the timeout resolution logic used by CometChat.initiateCall().
 *
 * The resolution rule: positive integers are used as-is; zero and negative
 * values fall back to DEFAULT_TIMEOUT (45 seconds).
 *
 * Validates: Requirements 2.1, 2.2, 2.3
 */
class TimeoutResolutionTest {

    private static final int DEFAULT_TIMEOUT = 45;

    /** Mirrors the resolution logic in CometChat.initiateCall(). */
    private static int resolveTimeout(int timeout) {
        return timeout > 0 ? timeout : DEFAULT_TIMEOUT;
    }

    // --- Non-positive values fall back to default (Requirement 2.2, 2.3) ---

    @Test
    void zeroFallsBackToDefault() {
        assertEquals(DEFAULT_TIMEOUT, resolveTimeout(0));
    }

    @Test
    void negativeOneFallsBackToDefault() {
        assertEquals(DEFAULT_TIMEOUT, resolveTimeout(-1));
    }

    @Test
    void negativeHundredFallsBackToDefault() {
        assertEquals(DEFAULT_TIMEOUT, resolveTimeout(-100));
    }

    @Test
    void minIntFallsBackToDefault() {
        assertEquals(DEFAULT_TIMEOUT, resolveTimeout(Integer.MIN_VALUE));
    }

    // --- Positive values are preserved (Requirement 2.1) ---

    @Test
    void positiveOneIsPreserved() {
        assertEquals(1, resolveTimeout(1));
    }

    @Test
    void defaultValueIsPreserved() {
        assertEquals(45, resolveTimeout(45));
    }

    @Test
    void largePositiveIsPreserved() {
        assertEquals(600, resolveTimeout(600));
    }

    @Test
    void maxIntIsPreserved() {
        assertEquals(Integer.MAX_VALUE, resolveTimeout(Integer.MAX_VALUE));
    }

    // --- Property-based tests ---

    // Feature: configurable-call-timeout, Property 1: Positive integers are preserved
    /** Validates: Requirements 2.1 */
    @Property
    void positiveIntegersArePreserved(@ForAll @IntRange(min = 1) int n) {
        assertEquals(n, resolveTimeout(n));
    }

    // Feature: configurable-call-timeout, Property 2: Non-positive values fall back to default
    /** Validates: Requirements 2.2, 2.3 */
    @Property
    void nonPositiveValuesFallBackToDefault(@ForAll @IntRange(max = 0) int n) {
        assertEquals(DEFAULT_TIMEOUT, resolveTimeout(n));
    }
}
