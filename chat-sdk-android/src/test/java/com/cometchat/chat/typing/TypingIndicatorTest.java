package com.cometchat.chat.typing;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.TypingIndicator;
import com.cometchat.chat.models.User;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Feature Area: Typing Indicators.
 *
 * <p>Covers:
 * <ul>
 *   <li>TYP-01 — a "started" typing signal carries sender identity and target, for one-to-one
 *       and group conversations.</li>
 *   <li>TYP-02 — an "ended" signal is distinct from "started"; extra metadata is only present
 *       when actually provided.</li>
 *   <li>TYP-03 — "is currently typing" is read strictly from the typing-status field, not from
 *       loosely-typed metadata.</li>
 * </ul>
 *
 * <p>The Android {@code TypingIndicator.fromJSON} is a non-functional stub (returns null), so
 * these tests exercise the model that the transport layer populates via setters.
 */
public class TypingIndicatorTest {

    private User sender(String uid) {
        return new User(uid, "Sender " + uid);
    }

    // ==================== TYP-01: started signal ====================

    @Test
    public void typ01_startedSignalForAUser_carriesSenderAndTarget() {
        TypingIndicator indicator = new TypingIndicator("receiver-1", CometChatConstants.RECEIVER_TYPE_USER);
        indicator.setSender(sender("typist-1"));
        indicator.setTypingStatus(TypingIndicator.TYPING_START);

        assertEquals("receiver-1", indicator.getReceiverId());
        assertEquals(CometChatConstants.RECEIVER_TYPE_USER, indicator.getReceiverType());
        assertEquals("typist-1", indicator.getSender().getUid());
        assertEquals(TypingIndicator.TYPING_START, indicator.getTypingStatus());
    }

    @Test
    public void typ01_startedSignalForAGroup_isSupported() {
        TypingIndicator indicator = new TypingIndicator("group-1", CometChatConstants.RECEIVER_TYPE_GROUP);
        indicator.setSender(sender("typist-2"));
        indicator.setTypingStatus(TypingIndicator.TYPING_START);

        assertEquals(CometChatConstants.RECEIVER_TYPE_GROUP, indicator.getReceiverType());
        assertEquals(TypingIndicator.TYPING_START, indicator.getTypingStatus());
    }

    // ==================== TYP-02: ended signal + optional metadata ====================

    @Test
    public void typ02_endedSignalIsDistinctFromStarted() {
        TypingIndicator started = new TypingIndicator("r", CometChatConstants.RECEIVER_TYPE_USER);
        started.setTypingStatus(TypingIndicator.TYPING_START);
        TypingIndicator ended = new TypingIndicator("r", CometChatConstants.RECEIVER_TYPE_USER);
        ended.setTypingStatus(TypingIndicator.TYPING_END);

        assertNotEquals(started.getTypingStatus(), ended.getTypingStatus());
        assertEquals("ended", ended.getTypingStatus());
    }

    @Test
    public void typ02_metadataIsAbsentWhenNotProvided() {
        TypingIndicator indicator = new TypingIndicator("r", CometChatConstants.RECEIVER_TYPE_USER);

        assertNull("without the metadata constructor, metadata stays unset", indicator.getMetadata());
    }

    @Test
    public void typ02_metadataIsPresentOnlyWhenProvided() throws Exception {
        JSONObject metadata = new JSONObject().put("intent", "compose");
        TypingIndicator indicator =
                new TypingIndicator("r", CometChatConstants.RECEIVER_TYPE_USER, metadata);

        assertNotNull(indicator.getMetadata());
        assertEquals("compose", indicator.getMetadata().getString("intent"));
    }

    // ==================== TYP-03: status read from the dedicated field ====================

    @Test
    public void typ03_isTypingIsDeterminedByTheTypingStatusFieldNotMetadata() throws Exception {
        // Metadata deliberately carries a conflicting hint; the dedicated field is authoritative.
        JSONObject misleadingMetadata = new JSONObject().put("typing", true);
        TypingIndicator indicator =
                new TypingIndicator("r", CometChatConstants.RECEIVER_TYPE_USER, misleadingMetadata);
        indicator.setTypingStatus(TypingIndicator.TYPING_END);

        assertEquals("the ended status must come from the typing-status field, not metadata",
                TypingIndicator.TYPING_END, indicator.getTypingStatus());
    }
}
