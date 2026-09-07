package com.cometchat.chat.conversations;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.constants.PinSaveContract;
import com.cometchat.chat.models.Conversation;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin Conversation / boundary and equality edges beyond the golden parse vectors.
 *
 * <p>{@code ConversationPinParsingTest} covers the happy parse paths. These pin down the corners a
 * regression is most likely to slip through:
 * <ul>
 *   <li>the system sentinel alone does <b>not</b> make a conversation system-pinned — {@code isSystemPinned()}
 *       is gated on {@code isPinned()}, so a stray {@code pinnedBy} with no {@code pinnedAt} is still "not pinned",</li>
 *   <li>{@code pinnedAt} is truthy only when {@code > 0} (zero and negatives are "not pinned"),</li>
 *   <li>{@code applyPinAttributes} is null-safe on either argument,</li>
 *   <li>{@code contentEquals} reacts to a change in {@code pinnedBy} <i>or</i> {@code pinnedAt} alone,
 *       not merely to the pinned↔not-pinned flip.</li>
 * </ul>
 */
public class ConversationPinEdgeCasesTest {

    private static final long PINNED_AT = 1785332391L;

    private static Conversation conversation() {
        return new Conversation("c-1", CometChatConstants.CONVERSATION_TYPE_USER);
    }

    // ==================== isSystemPinned is gated on isPinned ====================

    @Test
    public void systemSentinelWithoutPinnedAt_isNeitherPinnedNorSystemPinned() {
        Conversation c = conversation();
        c.setPinnedBy(PinSaveContract.SYSTEM_PINNER_SENTINEL); // pinnedAt stays 0

        assertFalse("no pinnedAt ⇒ not pinned, sentinel notwithstanding", c.isPinned());
        assertFalse("isSystemPinned must be gated on isPinned", c.isSystemPinned());
    }

    @Test
    public void pinnedWithoutPinner_isPinnedButNotSystem() throws Exception {
        Conversation c = Conversation.fromJSON(new JSONObject()
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID, "c-1")
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_TYPE, CometChatConstants.CONVERSATION_TYPE_USER)
                .put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_PINNED_AT, PINNED_AT));

        assertTrue(c.isPinned());
        assertFalse(c.isSystemPinned());
        assertNull("a pin with no pinner leaves pinnedBy null", c.getPinnedBy());
    }

    // ==================== pinnedAt truthiness boundary ====================

    @Test
    public void pinnedAtBoundary_onlyStrictlyPositiveIsPinned() {
        Conversation c = conversation();

        c.setPinnedAt(0);
        assertFalse("0 is not-pinned, never 0-as-set", c.isPinned());

        c.setPinnedAt(-5);
        assertFalse("a negative timestamp is not pinned", c.isPinned());

        c.setPinnedAt(1);
        assertTrue(c.isPinned());
    }

    // ==================== applyPinAttributes null-guards ====================

    @Test
    public void applyPinAttributes_nullArguments_areNoOps() throws Exception {
        Conversation c = conversation();
        c.setPinnedAt(PINNED_AT);
        c.setPinnedBy("uid1");

        Conversation.applyPinAttributes(null, new JSONObject()); // must not throw
        Conversation.applyPinAttributes(c, null);                // null json ⇒ leave state untouched

        assertTrue("a null json must not clear an existing pin", c.isPinned());
        assertEquals("uid1", c.getPinnedBy());
    }

    // ==================== contentEquals sensitivity ====================

    @Test
    public void contentEquals_differsOnPinnedByAlone() {
        Conversation a = conversation();
        a.setPinnedAt(PINNED_AT);
        a.setPinnedBy("uid1");

        Conversation b = conversation();
        b.setPinnedAt(PINNED_AT);           // same timestamp
        b.setPinnedBy(PinSaveContract.SYSTEM_PINNER_SENTINEL); // different pinner

        assertFalse("a same-timestamp / different-pinner change must not compare equal", a.contentEquals(b));
    }

    @Test
    public void contentEquals_differsOnPinnedAtAlone() {
        Conversation a = conversation();
        a.setPinnedAt(PINNED_AT);
        a.setPinnedBy("uid1");

        Conversation b = conversation();
        b.setPinnedAt(PINNED_AT + 1);       // different timestamp
        b.setPinnedBy("uid1");              // same pinner

        assertFalse(a.contentEquals(b));
    }

    // ==================== direct setters ====================

    @Test
    public void setters_driveIsPinnedAndIsSystemPinned() {
        Conversation c = conversation();
        c.setPinnedAt(PINNED_AT);
        c.setPinnedBy(PinSaveContract.SYSTEM_PINNER_SENTINEL);

        assertTrue(c.isPinned());
        assertTrue(c.isSystemPinned());
        assertEquals(PINNED_AT, c.getPinnedAt());
    }
}
