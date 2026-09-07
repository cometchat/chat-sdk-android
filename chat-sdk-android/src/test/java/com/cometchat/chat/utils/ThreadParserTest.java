package com.cometchat.chat.utils;

import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.MessageThread;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Thread subscription / the wire-contract chokepoint.
 *
 * <p>These cover the parser behaviour that is independent of the message transformer (which needs
 * the Android runtime and is therefore exercised in instrumented / fixture tests): the parser must
 * never throw, must retain the raw object, must treat unknown as neither absent nor zero, and must
 * derive the cursor from the oldest row rather than {@code meta.next}.
 */
public class ThreadParserTest {

    // ==================== never throws (rule 1) ====================

    @Test
    public void nullAndEmptyInputs_neverThrow() {
        assertNotNull(ThreadParser.parseThread(null));
        assertTrue(ThreadParser.parseThreadList(null).isEmpty());
        assertTrue(ThreadParser.parseThreadList("not json").isEmpty());
        assertTrue(ThreadParser.parseThreadList("[]").isEmpty());
        assertFalse(ThreadParser.parseCursor(null).isValid());
        assertEquals("", ThreadParser.parseSubscriptionAck(null));
        assertEquals("", ThreadParser.parseSubscriptionAck("not json"));
    }

    @Test
    public void rowWithNoneOfOurKeys_yieldsRowNotError() {
        MessageThread thread = ThreadParser.parseThread(new JSONObject());
        assertNotNull(thread);
        // A1: presence in a list is subscription, regardless of what the transformer could recover.
        assertTrue(thread.isSubscribed());
    }

    // ==================== rule 3: unknown != absent != 0 ====================

    @Test
    public void unreadReplyCount_absent_staysNull() throws Exception {
        JSONObject row = new JSONObject().put("id", 42);
        assertNull("an unserved unread count is unknown, not zero",
                ThreadParser.parseThread(row).getUnreadReplyCount());
    }

    @Test
    public void unreadReplyCount_present_isCarried() throws Exception {
        JSONObject row = new JSONObject().put("id", 42).put("unreadReplyCount", 3);
        assertEquals(Integer.valueOf(3), ThreadParser.parseThread(row).getUnreadReplyCount());
    }

    @Test
    public void unreadReplyCount_presentButZero_isZeroNotNull() throws Exception {
        JSONObject row = new JSONObject().put("id", 42).put("unreadReplyCount", 0);
        assertEquals(Integer.valueOf(0), ThreadParser.parseThread(row).getUnreadReplyCount());
    }

    // ==================== rule 6: raw object retained ====================

    @Test
    public void rawObject_isRetainedAsEscapeHatch() throws Exception {
        JSONObject row = new JSONObject().put("id", 7).put("somethingBackendAddedLater", "x");
        JSONObject raw = ThreadParser.parseThread(row).getRawData();
        assertNotNull(raw);
        assertEquals("x", raw.optString("somethingBackendAddedLater"));
    }

    // ==================== cursor: oldest row (updatedAt + id), not meta.next ====================

    @Test
    public void parseCursor_returnsOldestRowAsUpdatedAtAndId_ignoringMetaNext() throws Exception {
        JSONArray rows = new JSONArray()
                .put(new JSONObject().put("id", 1).put("updatedAt", 3000))
                .put(new JSONObject().put("id", 2).put("updatedAt", 1000))
                .put(new JSONObject().put("id", 3).put("updatedAt", 2000));
        JSONObject response = new JSONObject()
                .put("data", rows)
                .put("meta", new JSONObject().put("next", "should-be-ignored"));

        ThreadParser.Cursor cursor = ThreadParser.parseCursor(response.toString());
        assertEquals(1000, cursor.updatedAt);
        assertEquals("the boundary row's id rides the cursor as the tie-breaker", 2, cursor.id);
        assertTrue(cursor.isValid());
    }

    @Test
    public void parseCursor_tiedOnOldestSecond_picksSmallestId() throws Exception {
        // Three threads updated in the same second: the cursor must name the LOWEST id of that
        // block, so resuming from it cannot skip the rest of the block.
        JSONArray rows = new JSONArray()
                .put(new JSONObject().put("id", 30).put("updatedAt", 600))
                .put(new JSONObject().put("id", 28).put("updatedAt", 600))
                .put(new JSONObject().put("id", 29).put("updatedAt", 600));
        JSONObject response = new JSONObject().put("data", rows);

        ThreadParser.Cursor cursor = ThreadParser.parseCursor(response.toString());
        assertEquals(600, cursor.updatedAt);
        assertEquals(28, cursor.id);
    }

    @Test
    public void parseCursor_readsIdFromParentMessageIdAlias() throws Exception {
        JSONArray rows = new JSONArray()
                .put(new JSONObject().put("parentMessageId", 55).put("updatedAt", 900));
        JSONObject response = new JSONObject().put("data", rows);

        assertEquals(55, ThreadParser.parseCursor(response.toString()).id);
    }

    @Test
    public void parseCursor_emptyPage_isNone() throws Exception {
        JSONObject response = new JSONObject().put("data", new JSONArray());
        ThreadParser.Cursor cursor = ThreadParser.parseCursor(response.toString());
        assertFalse(cursor.isValid());
        assertEquals(0, cursor.updatedAt);
        assertEquals(0, cursor.id);
    }

    @Test
    public void parseCursor_rowsWithoutUpdatedAt_areNotABoundary() throws Exception {
        JSONArray rows = new JSONArray()
                .put(new JSONObject().put("id", 4))
                .put(new JSONObject().put("id", 5));
        JSONObject response = new JSONObject().put("data", rows);
        assertFalse(ThreadParser.parseCursor(response.toString()).isValid());
    }

    // ==================== list extraction tolerates envelope shapes ====================

    @Test
    public void parseThreadList_acceptsDataArrayAndNestedThreadsArray() throws Exception {
        JSONArray rows = new JSONArray()
                .put(new JSONObject().put("id", 1))
                .put(new JSONObject().put("id", 2));

        List<MessageThread> fromDataArray =
                ThreadParser.parseThreadList(new JSONObject().put("data", rows).toString());
        assertEquals(2, fromDataArray.size());

        JSONObject nested = new JSONObject().put("data", new JSONObject().put("threads", rows));
        assertEquals(2, ThreadParser.parseThreadList(nested.toString()).size());
    }

    // ==================== subscription ack ====================

    @Test
    public void parseSubscriptionAck_readsMessageFromDataEnvelope() throws Exception {
        JSONObject response = new JSONObject().put("data", new JSONObject()
                .put("success", true).put("message", "Subscribed to thread"));
        assertEquals("Subscribed to thread", ThreadParser.parseSubscriptionAck(response.toString()));
    }

    @Test
    public void parseSubscriptionAck_missingMessage_isEmptyString() throws Exception {
        JSONObject response = new JSONObject().put("data", new JSONObject().put("success", true));
        assertEquals("", ThreadParser.parseSubscriptionAck(response.toString()));
    }

    // ==================== threadSubscribed flag on a message (A3) ====================

    @Test
    public void subscribedFlag_true_reads_true() throws Exception {
        assertTrue(ThreadParser.readSubscriptionState(new JSONObject().put("threadSubscribed", true)));
    }

    @Test
    public void subscribedFlag_false_reads_false() throws Exception {
        assertFalse(ThreadParser.readSubscriptionState(new JSONObject().put("threadSubscribed", false)));
    }

    @Test
    public void subscribedFlag_absentOrNull_reads_false() {
        assertFalse("absent means the server did not tell us, which normalises to false",
                ThreadParser.readSubscriptionState(new JSONObject()));
        assertFalse(ThreadParser.readSubscriptionState(null));
    }

    @Test
    public void subscribedFlag_wrongTyped_reads_false_neverThrows() throws Exception {
        assertFalse(ThreadParser.readSubscriptionState(new JSONObject().put("threadSubscribed", new JSONObject())));
        assertFalse(ThreadParser.readSubscriptionState(new JSONObject().put("threadSubscribed", 17)));
        assertFalse(ThreadParser.readSubscriptionState(new JSONObject().put("threadSubscribed", "nope")));
    }

    @Test
    public void subscribedFlag_stringTrue_isCoerced() throws Exception {
        assertTrue("org.json coerces the canonical string forms",
                ThreadParser.readSubscriptionState(new JSONObject().put("threadSubscribed", "true")));
    }

    // ==================== applyThreadAttributes — the single write site ====================

    @Test
    public void applyThreadAttributes_writesTheFlagOntoTheMessage() throws Exception {
        BaseMessage message = new BaseMessage();
        ThreadParser.applyThreadAttributes(message, new JSONObject().put("threadSubscribed", true));
        assertTrue(message.isThreadSubscribed());
    }

    @Test
    public void applyThreadAttributes_absentKeyClearsAStaleTrue() throws Exception {
        BaseMessage message = new BaseMessage();
        message.setThreadSubscribed(true);
        ThreadParser.applyThreadAttributes(message, new JSONObject().put("id", 9));
        assertFalse("a payload that omits the flag must not leave a stale true behind",
                message.isThreadSubscribed());
    }

    @Test
    public void applyThreadAttributes_nullArguments_areNoOps() throws Exception {
        BaseMessage message = new BaseMessage();
        message.setThreadSubscribed(true);
        ThreadParser.applyThreadAttributes(message, null);
        assertTrue("a null payload is not a payload that omits the flag", message.isThreadSubscribed());
        ThreadParser.applyThreadAttributes(null, new JSONObject().put("threadSubscribed", true));
    }

    // ==================== contract flags (documents the confirmed backend contract) ====================

    @Test
    public void contract_encodesTheConfirmedBackendShape() {
        assertTrue(ThreadParser.Contract.LIST_IMPLIES_SUBSCRIBED);
        assertTrue(ThreadParser.Contract.CURSOR_IS_INCLUSIVE);
        assertFalse(ThreadParser.Contract.ECHO_SERVER_AFFIX);
        assertEquals("prepend", ThreadParser.Contract.FORCED_AFFIX);
        assertFalse(ThreadParser.Contract.TRUST_META_NEXT_AS_HAS_MORE);
        assertTrue(ThreadParser.Contract.REQUEST_THREAD_SUBSCRIBED_FLAG);
    }
}
