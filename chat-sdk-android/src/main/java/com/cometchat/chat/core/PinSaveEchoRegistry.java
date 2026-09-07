package com.cometchat.chat.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The local-echo registry that makes pin/save self-echo suppression race-safe (parity with the JS
 * SDK's {@code PinSaveEventDispatcher} registry).
 *
 * <p>The acting session registers (messageId:action) <b>before</b> its REST write goes out — a
 * WebSocket push routinely beats an HTTP round-trip, so registering on the response would lose
 * that race and the acting session would see its own action twice. The matching realtime frame
 * <b>consumes</b> the entry (single-use): one registration suppresses exactly one frame, so a
 * later, genuine event for the same message still reaches listeners. A failed write <b>clears</b>
 * its registration so a dead entry cannot mute a real event for the full TTL.
 *
 * <p>All state is static because the emit sites ({@link CometChat}) are static. No Android
 * dependencies — this class is directly unit-testable on the JVM, which the {@code CometChat}
 * facade (static Looper init) is not; the time-taking overloads exist for those tests.
 */
final class PinSaveEchoRegistry {

    /** How long a registered echo stays eligible to suppress one frame. */
    static final long TTL_MS = 15000;

    private static final ConcurrentHashMap<String, Long> entries = new ConcurrentHashMap<>();

    private PinSaveEchoRegistry() {
    }

    private static String key(String entityKey, String action) {
        return entityKey + ":" + action;
    }

    /**
     * The entity key for a conversation pin/unpin: {@code conversationType:conversationWith}.
     * Keyed on those two rather than conversationId because the acting side knows only them when
     * the write is issued — the conversationId arrives on the response, which is precisely what
     * the echo must be registered BEFORE. The socket side derives the identical key from the
     * parsed Conversation.
     */
    static String conversationKey(String conversationType, String conversationWith) {
        return conversationType + ":" + conversationWith;
    }

    /** Notes an imminent local emit. Call BEFORE the write goes out. Prunes expired entries. */
    static void register(long messageId, String action) {
        register(String.valueOf(messageId), action, System.currentTimeMillis());
    }

    static void register(String entityKey, String action) {
        register(entityKey, action, System.currentTimeMillis());
    }

    static void register(long messageId, String action, long nowMs) {
        register(String.valueOf(messageId), action, nowMs);
    }

    static void register(String entityKey, String action, long nowMs) {
        for (Map.Entry<String, Long> entry : entries.entrySet()) {
            if (nowMs - entry.getValue() > TTL_MS) {
                entries.remove(entry.getKey(), entry.getValue());
            }
        }
        entries.put(key(entityKey, action), nowMs);
    }

    /** Drops a registration that will never be consumed (the write failed). */
    static void clear(long messageId, String action) {
        clear(String.valueOf(messageId), action);
    }

    static void clear(String entityKey, String action) {
        entries.remove(key(entityKey, action));
    }

    /**
     * Consumes a pending echo. {@code true} means the incoming frame duplicates this session's own
     * local emit and must be suppressed. Single-use: the entry is removed on match. Call at most
     * once per incoming frame, before any per-listener fan-out.
     */
    static boolean consume(long messageId, String action) {
        return consume(String.valueOf(messageId), action, System.currentTimeMillis());
    }

    static boolean consume(String entityKey, String action) {
        return consume(entityKey, action, System.currentTimeMillis());
    }

    static boolean consume(long messageId, String action, long nowMs) {
        return consume(String.valueOf(messageId), action, nowMs);
    }

    static boolean consume(String entityKey, String action, long nowMs) {
        Long ts = entries.remove(key(entityKey, action));
        return ts != null && nowMs - ts <= TTL_MS;
    }

    /** Empties the registry. Called on login/logout alongside the other caches; also for tests. */
    static void clearAll() {
        entries.clear();
    }
}
