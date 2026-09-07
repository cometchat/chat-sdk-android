package com.cometchat.chat.utils;

import com.cometchat.chat.helpers.CometChatHelper;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.MessageThread;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The single chokepoint for the thread-subscription wire contract.
 *
 * <p>Every thread-related JSON key and every backend-shape assumption lives here and nowhere else,
 * so that when the backend fixes one of its acknowledged bugs (see {@link Contract}) the change is a
 * one-line flip in this file — and no public symbol has to change.
 *
 * <p>Parsing rules, applied uniformly (mirrors the other four SDKs):
 * <ol>
 *   <li>A missing key is never an error. No input — {@code null}, {@code []}, a bare string, or an
 *       object with none of our keys — may throw. {@code org.json opt*} is used throughout, never
 *       {@code get*}.</li>
 *   <li>A wrong-typed value is treated as missing / coerced.</li>
 *   <li>{@code unreadReplyCount} falls back to {@code null}, never to {@code 0} — "not served" and
 *       "zero unread" are different answers. {@code threadSubscribed} has no such third state: it
 *       normalises to {@code false}, and what {@code false} means is the consumer's problem (see
 *       {@link #applyThreadAttributes}).</li>
 *   <li>The parent-message id is read from any of {@code parentMessageId | parentId | id} (the socket
 *       frame and the REST payload disagree).</li>
 *   <li>Nested messages go through the existing message transformer
 *       ({@link CometChatHelper#processMessage(JSONObject)}), so attachments / reactions / mentions /
 *       tombstones behave exactly as on {@code /v3/messages}. If nested parsing fails, that field
 *       becomes {@code null} — a bad {@code lastReply} must not lose the whole row.</li>
 *   <li>The whole raw object is retained in {@link MessageThread#getRawData()} — the escape hatch.</li>
 * </ol>
 */
public final class ThreadParser {

    /**
     * The THREAD_CONTRACT flag block — the CONFIRMED backend contract (Chat backend, 2026-07-22).
     * Three values encode acknowledged, not-yet-fixed backend bugs (see notes); when a bug is fixed,
     * flipping its flag here is the only edit required.
     */
    public static final class Contract {
        /** A1 — unsubscribe hard-deletes the row, so presence in the list == subscribed. */
        public static final boolean LIST_IMPLIES_SUBSCRIBED = true;
        /** A8 (bug) — cursor is inclusive; the boundary row repeats, so dedupe is required. */
        public static final boolean CURSOR_IS_INCLUSIVE = true;
        /** A8 (bug) — {@code meta.next.affix} is hard-coded "append"; do not echo it. */
        public static final boolean ECHO_SERVER_AFFIX = false;
        public static final String FORCED_AFFIX = "prepend";
        /** A8 (bug) — {@code meta.next} is not a usable end-of-list signal; derive the cursor from the oldest row. */
        public static final boolean TRUST_META_NEXT_AS_HAS_MORE = false;
        /** A3 — the subscription flag rides the message payload, but only when we opt in per request. */
        public static final boolean REQUEST_THREAD_SUBSCRIBED_FLAG = true;

        private Contract() {}
    }

    /** Thread wire keys — confined to this file. */
    private static final String[] KEY_PARENT_MESSAGE_ID_ALIASES = {"parentMessageId", "parentId", "id"};
    private static final String KEY_LAST_REPLY = "lastReply";
    private static final String KEY_REPLY_COUNT = "replyCount";
    private static final String KEY_UPDATED_AT = "updatedAt";
    private static final String KEY_CONVERSATION_ID = "conversationId";
    private static final String KEY_RECEIVER = "receiver";
    private static final String KEY_RECEIVER_TYPE = "receiverType";
    private static final String KEY_UNREAD_REPLY_COUNT = "unreadReplyCount";
    private static final String KEY_THREAD_SUBSCRIBED = "threadSubscribed";

    /** Response envelope keys. */
    private static final String KEY_DATA = "data";
    private static final String KEY_THREADS = "threads";
    private static final String KEY_MESSAGE = "message";

    private ThreadParser() {}

    /**
     * Parses one thread row (a decorated root message) into a {@link MessageThread}. Never throws.
     *
     * <p>Because the row <i>is</i> the root message, most fields are read back off the reused
     * {@link BaseMessage} transformer rather than re-parsed from the wire, with raw-key fallbacks
     * for the rare case the message transformer returns {@code null}.
     *
     * @param json a single thread row; a {@code null} argument yields an empty {@link MessageThread}.
     */
    public static MessageThread parseThread(JSONObject json) {
        MessageThread thread = new MessageThread();
        if (json == null) {
            return thread;
        }

        thread.setRawData(json);

        BaseMessage parentMessage = safeProcessMessage(json);
        thread.setParentMessage(parentMessage);

        if (parentMessage != null) {
            thread.setParentMessageId(parentMessage.getId());
            thread.setReplyCount(parentMessage.getReplyCount());
            thread.setUpdatedAt(parentMessage.getUpdatedAt());
            thread.setConversationId(parentMessage.getConversationId());
            thread.setReceiverType(parentMessage.getReceiverType());
            thread.setReceiverUid(parentMessage.getReceiverUid());
        } else {
            // The message transformer could not build the row; fall back to raw keys so the row is
            // still identifiable rather than dropped.
            thread.setParentMessageId(optLongAlias(json, KEY_PARENT_MESSAGE_ID_ALIASES));
            thread.setReplyCount(optIntOrDefault(json, KEY_REPLY_COUNT, 0));
            thread.setUpdatedAt(json.optLong(KEY_UPDATED_AT, 0));
            thread.setConversationId(optStringOrNull(json, KEY_CONVERSATION_ID));
            thread.setReceiverType(optStringOrNull(json, KEY_RECEIVER_TYPE));
            thread.setReceiverUid(optStringOrNull(json, KEY_RECEIVER));
        }

        thread.setLastReply(safeProcessMessage(json.optJSONObject(KEY_LAST_REPLY)));
        thread.setUnreadReplyCount(optNullableInt(json, KEY_UNREAD_REPLY_COUNT));

        // A1: a row that appears in the list is, by definition, subscribed.
        thread.setSubscribed(Contract.LIST_IMPLIES_SUBSCRIBED || readSubscriptionState(json));

        return thread;
    }

    /**
     * Parses a full {@code /threads} response into a list of {@link MessageThread}. Never throws;
     * an unparseable envelope yields an empty list.
     */
    public static List<MessageThread> parseThreadList(String response) {
        List<MessageThread> threads = new ArrayList<>();
        JSONArray rows = extractRows(response);
        if (rows == null) {
            return threads;
        }
        for (int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.optJSONObject(i);
            if (row != null) {
                threads.add(parseThread(row));
            }
        }
        return threads;
    }

    /**
     * The pagination cursor for {@code /threads}: the boundary row of a page, as the
     * {@code updatedAt} + {@code id} pair the next request sends. {@code updatedAt} alone is
     * tie-prone (many threads can share a second); the id is the tie-breaker, mirroring the
     * {@code sentAt} + {@code id} cursor on {@code /messages}.
     */
    public static final class Cursor {

        /** No cursor — the first page, or a page with no usable boundary row. */
        public static final Cursor NONE = new Cursor(0, 0);

        public final long updatedAt;
        public final long id;

        public Cursor(long updatedAt, long id) {
            this.updatedAt = updatedAt;
            this.id = id;
        }

        /** @return {@code true} when this cursor can be sent on the wire. */
        public boolean isValid() {
            return updatedAt > 0;
        }

        public boolean sameAs(Cursor other) {
            return other != null && updatedAt == other.updatedAt && id == other.id;
        }

        @Override
        public String toString() {
            return "Cursor{updatedAt=" + updatedAt + ", id=" + id + '}';
        }
    }

    /**
     * Derives the pagination cursor from a {@code /threads} response: the OLDEST row in the page —
     * oldest {@code updatedAt}, and among rows tied on that second the smallest id. {@code meta.next}
     * is deliberately ignored (A8 bug — {@link Contract#TRUST_META_NEXT_AS_HAS_MORE}).
     *
     * @return the boundary row as an {@code updatedAt} + {@code id} pair, or {@link Cursor#NONE} when
     *         the page has no row carrying an {@code updatedAt}.
     */
    public static Cursor parseCursor(String response) {
        JSONArray rows = extractRows(response);
        if (rows == null || rows.length() == 0) {
            return Cursor.NONE;
        }
        long oldest = 0;
        long oldestId = 0;
        for (int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.optJSONObject(i);
            if (row == null) {
                continue;
            }
            long updatedAt = row.optLong(KEY_UPDATED_AT, 0);
            if (updatedAt <= 0) {
                continue;
            }
            long id = optLongAlias(row, KEY_PARENT_MESSAGE_ID_ALIASES);
            if (oldest == 0 || updatedAt < oldest) {
                oldest = updatedAt;
                oldestId = id;
            } else if (updatedAt == oldest && id > 0 && (oldestId == 0 || id < oldestId)) {
                // Same second: the smallest id is the true boundary, so the next page resumes
                // strictly below the whole tie-block rather than in the middle of it.
                oldestId = id;
            }
        }
        return oldest > 0 ? new Cursor(oldest, oldestId) : Cursor.NONE;
    }

    /**
     * Extracts the acknowledgement message from a subscribe / unsubscribe response
     * ({@code {"data":{"success":true,"message":"…"}}}). This path must never build a
     * {@link MessageThread}.
     *
     * @return the {@code message} string, or an empty string when absent.
     */
    public static String parseSubscriptionAck(String response) {
        if (response == null) {
            return "";
        }
        try {
            JSONObject root = new JSONObject(response);
            JSONObject data = root.optJSONObject(KEY_DATA);
            if (data != null && data.has(KEY_MESSAGE)) {
                return data.optString(KEY_MESSAGE, "");
            }
            return root.optString(KEY_MESSAGE, "");
        } catch (Exception e) {
            Logger.error(e.toString());
            return "";
        }
    }

    /**
     * Reads the {@code threadSubscribed} flag off a message payload.
     *
     * <p>Normalises rather than interprets: an absent, {@code null} or wrong-typed value reads
     * {@code false}, so a malformed payload can never make the accessor lie. There is no third state
     * — see {@link #applyThreadAttributes} for what {@code false} does and does not mean.
     *
     * @param messageJson a message wire object (may be {@code null}).
     * @return the flag as it arrived, or {@code false} when absent or not a boolean.
     */
    public static boolean readSubscriptionState(JSONObject messageJson) {
        if (messageJson == null || !messageJson.has(KEY_THREAD_SUBSCRIBED)) {
            return false;
        }
        return messageJson.optBoolean(KEY_THREAD_SUBSCRIBED, false);
    }

    /**
     * Single parse chokepoint for the thread attributes. This is the ONLY place the
     * {@code threadSubscribed} JSON key is written onto a message; every subtype {@code fromJson}
     * funnels through here, alongside {@code BaseMessage#applyPinSaveAttributes}.
     *
     * <p>The field is always assigned (cleared when the key is absent), so a payload that omits the
     * flag cannot leave a stale {@code true} behind.
     *
     * <p><b>The flag is only populated on responses to requests that asked for it</b>
     * ({@code withThreadSubscribed=true}). A socket-delivered message carries no flag, so
     * {@link BaseMessage#isThreadSubscribed()} reads {@code false} there — meaning "the server did
     * not tell me", not "the user is unsubscribed". Resolving that is the consumer's job; the SDK
     * deliberately remembers nothing.
     *
     * @param message    the message to populate; no-op if {@code null}
     * @param messageJson the message JSON; no-op if {@code null}
     */
    public static void applyThreadAttributes(BaseMessage message, JSONObject messageJson) {
        if (message == null || messageJson == null) {
            return;
        }
        message.setThreadSubscribed(readSubscriptionState(messageJson));
    }

    // ---- internals ----------------------------------------------------------

    /** Reuses the existing message transformer; returns {@code null} instead of throwing (rule 5). */
    private static BaseMessage safeProcessMessage(JSONObject messageJson) {
        if (messageJson == null) {
            return null;
        }
        try {
            return CometChatHelper.processMessage(messageJson);
        } catch (Exception e) {
            Logger.error(e.toString());
            return null;
        }
    }

    /** Locates the array of thread rows, tolerating {@code {"data":[…]}} and {@code {"data":{"threads":[…]}}}. */
    private static JSONArray extractRows(String response) {
        if (response == null) {
            return null;
        }
        try {
            JSONObject root = new JSONObject(response);
            Object data = root.opt(KEY_DATA);
            if (data instanceof JSONArray) {
                return (JSONArray) data;
            }
            if (data instanceof JSONObject) {
                JSONArray threads = ((JSONObject) data).optJSONArray(KEY_THREADS);
                if (threads != null) {
                    return threads;
                }
            }
            return root.optJSONArray(KEY_THREADS);
        } catch (Exception e) {
            Logger.error(e.toString());
            return null;
        }
    }

    private static long optLongAlias(JSONObject json, String[] keys) {
        for (String key : keys) {
            if (json.has(key)) {
                long value = json.optLong(key, 0);
                if (value != 0) {
                    return value;
                }
            }
        }
        return 0;
    }

    private static int optIntOrDefault(JSONObject json, String key, int fallback) {
        return json.optInt(key, fallback);
    }

    private static String optStringOrNull(JSONObject json, String key) {
        if (!json.has(key) || json.isNull(key)) {
            return null;
        }
        String value = json.optString(key, null);
        return (value == null || value.isEmpty()) ? null : value;
    }

    /** Present-and-coercible int, else {@code null} — unknown must not collapse to {@code 0} (rule 3). */
    private static Integer optNullableInt(JSONObject json, String key) {
        if (!json.has(key) || json.isNull(key)) {
            return null;
        }
        int sentinel = Integer.MIN_VALUE;
        int value = json.optInt(key, sentinel);
        return value == sentinel ? null : value;
    }
}
