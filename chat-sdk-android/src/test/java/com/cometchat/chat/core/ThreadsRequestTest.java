package com.cometchat.chat.core;

import com.cometchat.chat.models.MessageThread;
import com.cometchat.chat.utils.ThreadParser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Feature Area: Thread subscription / participated-threads pagination.
 *
 * <p>Exercises the builder contract and the pure {@link ThreadsRequest#reducePage} decision — the
 * boundary logic that guards against the highest-risk defect (silent row loss under an inclusive,
 * tie-prone cursor — which is why the cursor is {@code updatedAt} + {@code id}, not {@code updatedAt}
 * alone). The network-driven {@code fetchNext} loop is covered by instrumented / fixture tests.
 */
public class ThreadsRequestTest {

    private MessageThread row(long id, long updatedAt) {
        MessageThread t = new MessageThread();
        t.setParentMessageId(id);
        t.setUpdatedAt(updatedAt);
        t.setSubscribed(true);
        return t;
    }

    private List<MessageThread> page(long... ids) {
        List<MessageThread> p = new ArrayList<>();
        for (long id : ids) {
            p.add(row(id, 1000 - id)); // arbitrary decreasing updatedAt
        }
        return p;
    }

    /** A page whose rows all share one {@code updatedAt} — the tie-block the id cursor exists for. */
    private List<MessageThread> tiedPage(long updatedAt, long... ids) {
        List<MessageThread> p = new ArrayList<>();
        for (long id : ids) {
            p.add(row(id, updatedAt));
        }
        return p;
    }

    private ThreadParser.Cursor cursor(long updatedAt, long id) {
        return new ThreadParser.Cursor(updatedAt, id);
    }

    // ==================== builder ====================

    @Test
    public void builder_defaults() {
        ThreadsRequest req = new ThreadsRequest.ThreadsRequestBuilder().build();
        assertEquals(30, req.getLimit());
        assertTrue(req.isParticipatedByMe());
        assertTrue("a fresh request has more to fetch", req.hasMore());
    }

    @Test
    public void builder_carriesScopeAndLimit() {
        ThreadsRequest req = new ThreadsRequest.ThreadsRequestBuilder()
                .setLimit(50).setParticipatedByMe(false).setGuid("cricket").build();
        assertEquals(50, req.getLimit());
        assertFalse(req.isParticipatedByMe());
        assertEquals("cricket", req.getGuid());
    }

    @Test
    public void builder_guidAndUidAreMutuallyExclusive() {
        try {
            new ThreadsRequest.ThreadsRequestBuilder().setGuid("cricket").setUid("bob").build();
            fail("expected IllegalArgumentException for setting both guid and uid");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    // ==================== reducePage: de-dupe ====================

    @Test
    public void reducePage_dropsBoundaryRowAlreadySeen() {
        Set<Long> seen = new HashSet<>();
        // first page of a limit-3 request
        ThreadsRequest.PageOutcome first = ThreadsRequest.reducePage(
                page(10, 11, 12), 3, seen, ThreadParser.Cursor.NONE, cursor(900, 12));
        assertEquals(3, first.fresh.size());

        // next page repeats the boundary row 12 (inclusive cursor) plus two new rows
        ThreadsRequest.PageOutcome second = ThreadsRequest.reducePage(
                page(12, 13, 14), 3, seen, cursor(900, 12), cursor(850, 14));
        assertEquals("the repeated boundary row must be dropped", 2, second.fresh.size());
        assertFalse(idsOf(second.fresh).contains(12L));
    }

    // ==================== reducePage: cursor carries the boundary id ====================

    @Test
    public void reducePage_cursorCarriesBoundaryUpdatedAtAndId() {
        ThreadsRequest.PageOutcome out = ThreadsRequest.reducePage(
                page(1, 2, 3), 3, new HashSet<Long>(), ThreadParser.Cursor.NONE, cursor(500, 3));
        assertEquals(500, out.nextCursor.updatedAt);
        assertEquals("the id must ride along so a same-second block is not skipped", 3, out.nextCursor.id);
        assertFalse(out.exhausted);
    }

    @Test
    public void reducePage_pageWithNoBoundaryRow_keepsFetchCursor() {
        ThreadsRequest.PageOutcome out = ThreadsRequest.reducePage(
                page(1, 2, 3), 3, new HashSet<Long>(), cursor(700, 9), ThreadParser.Cursor.NONE);
        assertEquals(700, out.nextCursor.updatedAt);
        assertEquals(9, out.nextCursor.id);
    }

    // ==================== reducePage: exhaustion on short RAW page ====================

    @Test
    public void reducePage_shortRawPage_exhausts() {
        ThreadsRequest.PageOutcome out = ThreadsRequest.reducePage(
                page(1, 2), 3, new HashSet<Long>(), ThreadParser.Cursor.NONE, cursor(500, 2));
        assertTrue(out.exhausted);
    }

    @Test
    public void reducePage_fullPageWithFreshRows_notExhausted() {
        ThreadsRequest.PageOutcome out = ThreadsRequest.reducePage(
                page(1, 2, 3), 3, new HashSet<Long>(), ThreadParser.Cursor.NONE, cursor(500, 3));
        assertFalse(out.exhausted);
    }

    // ==================== reducePage: a same-second tie-block spills, it is never skipped ====================

    @Test
    public void reducePage_tieBlockWiderThanPage_advancesByIdWithoutLosingRows() {
        // Every row shares one second (updatedAt = 600); only the id can separate them.
        Set<Long> seen = new HashSet<>();
        ThreadsRequest.PageOutcome first = ThreadsRequest.reducePage(
                tiedPage(600, 30, 29, 28), 3, seen, ThreadParser.Cursor.NONE, cursor(600, 28));
        assertEquals(3, first.fresh.size());
        assertFalse("a full page is never the end of the list", first.exhausted);
        assertEquals("the cursor stays on the second but descends the ids", 600, first.nextCursor.updatedAt);
        assertEquals(28, first.nextCursor.id);

        // Resuming with (600, 28) the server returns the rest of the same-second block, boundary repeated.
        ThreadsRequest.PageOutcome second = ThreadsRequest.reducePage(
                tiedPage(600, 28, 27, 26), 3, seen, cursor(600, 28), cursor(600, 26));
        assertEquals("only the repeated boundary row is dropped", 2, second.fresh.size());
        assertTrue(idsOf(second.fresh).contains(27L));
        assertTrue(idsOf(second.fresh).contains(26L));
        assertFalse(second.exhausted);
    }

    @Test
    public void reducePage_fullPageAllSeenAndCursorCannotAdvance_exhausts() {
        // Only reachable with an inclusive cursor and limit 1: the page is the boundary row itself,
        // so there is no way to move. Ending the list beats looping on the same row forever.
        Set<Long> seen = new HashSet<>();
        seen.add(7L);
        ThreadsRequest.PageOutcome out = ThreadsRequest.reducePage(
                tiedPage(600, 7), 1, seen, cursor(600, 7), cursor(600, 7));
        assertTrue("a stalled cursor must end the list", out.exhausted);
        assertTrue(out.fresh.isEmpty());
    }

    private Set<Long> idsOf(List<MessageThread> threads) {
        Set<Long> ids = new HashSet<>();
        for (MessageThread t : threads) {
            ids.add(t.getParentMessageId());
        }
        return ids;
    }
}
