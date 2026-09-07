package com.cometchat.chat.utils;

import com.cometchat.chat.utils.ThreadParser.Cursor;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Property-based tests for {@link ThreadParser#parseCursor(String)} — the {@code updatedAt} +
 * {@code id} boundary derivation added for tie-safe thread pagination.
 *
 * <p>The invariant under test, for ANY page: the cursor is the oldest {@code updatedAt} in the
 * page, and among rows tied on that second, the smallest {@code id} — so the next page always
 * resumes strictly below the whole tie-block. Example-based coverage lives in
 * {@code ThreadParserTest}; these properties sweep the input space for orderings and tie shapes
 * hand-written examples miss.
 */
class ThreadParserCursorPropertyTest {

    private static String pageOf(List<Tuple2<Long, Long>> rows) throws Exception {
        JSONArray data = new JSONArray();
        for (Tuple2<Long, Long> row : rows) {
            JSONObject json = new JSONObject();
            json.put("updatedAt", (long) row.get1());
            json.put("id", (long) row.get2());
            data.put(json);
        }
        return new JSONObject().put("data", data).toString();
    }

    @Provide
    Arbitrary<List<Tuple2<Long, Long>>> pages() {
        Arbitrary<Tuple2<Long, Long>> row = Combinators.combine(
                Arbitraries.longs().between(1, 1_000_000_000L),
                Arbitraries.longs().between(1, 1_000_000L)
        ).as(Tuple::of);
        return row.list().ofMinSize(1).ofMaxSize(25);
    }

    /** Ties are deliberately likely here: few distinct updatedAt values over many rows. */
    @Provide
    Arbitrary<List<Tuple2<Long, Long>>> tieHeavyPages() {
        Arbitrary<Tuple2<Long, Long>> row = Combinators.combine(
                Arbitraries.longs().between(1, 3),
                Arbitraries.longs().between(1, 1_000_000L)
        ).as(Tuple::of);
        return row.list().ofMinSize(1).ofMaxSize(25);
    }

    private void assertCursorIsOldestRowSmallestId(List<Tuple2<Long, Long>> rows) throws Exception {
        long expectedUpdatedAt = Long.MAX_VALUE;
        for (Tuple2<Long, Long> row : rows) {
            expectedUpdatedAt = Math.min(expectedUpdatedAt, row.get1());
        }
        long expectedId = Long.MAX_VALUE;
        for (Tuple2<Long, Long> row : rows) {
            if (row.get1() == expectedUpdatedAt) {
                expectedId = Math.min(expectedId, row.get2());
            }
        }

        Cursor cursor = ThreadParser.parseCursor(pageOf(rows));

        assertTrue(cursor.isValid(), "cursor from a non-empty valid page must be valid");
        assertEquals(expectedUpdatedAt, cursor.updatedAt, "cursor must carry the oldest updatedAt");
        assertEquals(expectedId, cursor.id, "cursor id must be the smallest id among the oldest tie-block");
    }

    @Property
    void cursorIsOldestRowWithSmallestTiedId(@ForAll("pages") List<Tuple2<Long, Long>> rows) throws Exception {
        assertCursorIsOldestRowSmallestId(rows);
    }

    @Property
    void tieBlocksResolveToSmallestId(@ForAll("tieHeavyPages") List<Tuple2<Long, Long>> rows) throws Exception {
        assertCursorIsOldestRowSmallestId(rows);
    }

    /** Row order within the page must never change the derived cursor. */
    @Property
    void cursorIsOrderInsensitive(@ForAll("pages") List<Tuple2<Long, Long>> rows,
                                  @ForAll long seed) throws Exception {
        Cursor original = ThreadParser.parseCursor(pageOf(rows));

        java.util.ArrayList<Tuple2<Long, Long>> shuffled = new java.util.ArrayList<>(rows);
        java.util.Collections.shuffle(shuffled, new java.util.Random(seed));
        Cursor reordered = ThreadParser.parseCursor(pageOf(shuffled));

        assertTrue(original.sameAs(reordered), "shuffling the page changed the cursor: " + original + " vs " + reordered);
    }

    /** Rows without a usable updatedAt are skipped; an all-invalid page yields NONE. */
    @Property
    void rowsWithoutUpdatedAtAreIgnored(@ForAll("pages") List<Tuple2<Long, Long>> validRows,
                                        @ForAll("invalidUpdatedAts") List<Long> invalidUpdatedAts) throws Exception {
        Cursor expected = ThreadParser.parseCursor(pageOf(validRows));

        JSONArray data = new JSONArray();
        for (Long bad : invalidUpdatedAts) {
            data.put(new JSONObject().put("updatedAt", (long) bad).put("id", 999L));
        }
        for (Tuple2<Long, Long> row : validRows) {
            data.put(new JSONObject().put("updatedAt", (long) row.get1()).put("id", (long) row.get2()));
        }
        Cursor actual = ThreadParser.parseCursor(new JSONObject().put("data", data).toString());

        assertTrue(expected.sameAs(actual), "invalid rows changed the cursor: " + expected + " vs " + actual);
    }

    @Provide
    Arbitrary<List<Long>> invalidUpdatedAts() {
        return Arbitraries.longs().between(-1_000_000L, 0).list().ofMinSize(1).ofMaxSize(10);
    }

    /** parseCursor and parseSubscriptionAck are documented as total — no input may throw. */
    @Property
    void parserEntryPointsNeverThrow(@ForAll String garbage) {
        assertNotNull(ThreadParser.parseCursor(garbage));
        assertNotNull(ThreadParser.parseSubscriptionAck(garbage));
        assertNotNull(ThreadParser.parseThreadList(garbage));
    }
}
