package com.cometchat.chat.reactions;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.ReactionsRequest;
import com.cometchat.chat.models.Reaction;
import com.cometchat.chat.models.ReactionCount;
import com.cometchat.chat.models.ReactionEvent;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Reactions / Viewing reactions, aggregate counts, real-time delivery, history.
 *
 * <p>Covers:
 * <ul>
 *   <li>REACT-02 — a reaction resolves the reacting user's identity and exposes reaction detail.</li>
 *   <li>REACT-03 — aggregated per-emoji counts with the current-user flag, tolerating numeric
 *       strings.</li>
 *   <li>REACT-04 — a real-time reaction event identifies message/conversation/receiver, defaulting
 *       the parent-message reference safely when absent.</li>
 *   <li>REACT-05 — reaction-history request includes the emoji filter and cursor only when set.</li>
 * </ul>
 *
 * <p>REACT-01 (emoji URL-encoding on the add/remove endpoint) is a network-layer concern with no
 * pure-JVM surface and is not covered here.
 */
public class ReactionParsingTest {

    // ==================== REACT-02: reaction detail + reacting user ====================

    @Test
    public void react02_reaction_resolvesTheReactingUserAndDetail() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_ID, "r-1")
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_MESSAGE_ID, 500L)
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION, "👍")
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_UID, "reactor-1")
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_AT, 1700000000L)
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY, new JSONObject()
                        .put(CometChatConstants.UserKeys.USER_KEY_UID, "reactor-1")
                        .put(CometChatConstants.UserKeys.USER_KEY_NAME, "Reactor One"));

        Reaction reaction = Reaction.fromJson(json);

        assertEquals("r-1", reaction.getReactionId());
        assertEquals(500L, reaction.getMessageId());
        assertEquals("👍", reaction.getReaction());
        assertEquals("reactor-1", reaction.getUid());
        assertEquals(1700000000L, reaction.getReactedAt());
        assertNotNull("the reacting user's identity must be resolved", reaction.getReactedBy());
        assertEquals("Reactor One", reaction.getReactedBy().getName());
    }

    // ==================== REACT-03: aggregated counts ====================

    @Test
    public void react03_countsPerEmoji_totalAndFlagCurrentUser() throws Exception {
        JSONArray reactions = new JSONArray()
                .put(new JSONObject()
                        .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION, "👍")
                        .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_COUNT, 5)
                        .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY_ME, true))
                .put(new JSONObject()
                        .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION, "❤️")
                        .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_COUNT, 2)
                        .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY_ME, false));

        List<ReactionCount> counts = ReactionCount.listFromJSONArray(reactions);

        assertEquals(2, counts.size());
        assertEquals("👍", counts.get(0).getReaction());
        assertEquals(5, counts.get(0).getCount());
        assertTrue(counts.get(0).getReactedByMe());
        assertEquals("❤️", counts.get(1).getReaction());
        assertEquals(2, counts.get(1).getCount());
        assertFalse(counts.get(1).getReactedByMe());
    }

    @Test
    public void react03_countSentAsNumericString_isTolerated() throws Exception {
        JSONArray reactions = new JSONArray().put(new JSONObject()
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION, "🎉")
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_COUNT, "7"));

        List<ReactionCount> counts = ReactionCount.listFromJSONArray(reactions);

        assertEquals("a count sent as a string must still total correctly", 7, counts.get(0).getCount());
    }

    @Test
    public void react03_missingReactedByMe_defaultsToFalse() throws Exception {
        JSONArray reactions = new JSONArray().put(new JSONObject()
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION, "😀")
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_COUNT, 1));

        List<ReactionCount> counts = ReactionCount.listFromJSONArray(reactions);

        assertFalse(counts.get(0).getReactedByMe());
    }

    // ==================== REACT-04: real-time reaction event ====================

    @Test
    public void react04_reactionEvent_identifiesMessageConversationAndReceiver() throws Exception {
        JSONObject reaction = new JSONObject()
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_MESSAGE_ID, 900L)
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION, "👍")
                .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_UID, "u-1");
        JSONObject json = new JSONObject()
                .put(CometChatConstants.ReactionsKeys.KEY_RECEIVER_ID, "grp-1")
                .put(CometChatConstants.ReactionsKeys.KEY_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_GROUP)
                .put(CometChatConstants.ReactionsKeys.KEY_CONVERSATION_ID, "conv-xyz")
                .put(CometChatConstants.ReactionsKeys.KEY_PARENT_ID, 42L)
                .put(CometChatConstants.ReactionsKeys.KEY_REACTION, reaction);

        ReactionEvent event = ReactionEvent.fromJson(json);

        assertEquals("grp-1", event.getReceiverId());
        assertEquals(CometChatConstants.RECEIVER_TYPE_GROUP, event.getReceiverType());
        assertEquals("conv-xyz", event.getConversationId());
        assertEquals(42L, event.getParentMessageId());
        assertNotNull(event.getReaction());
        assertEquals("👍", event.getReaction().getReaction());
        assertEquals(900L, event.getReaction().getMessageId());
    }

    @Test
    public void react04_reactionEventWithoutParentId_defaultsToZero() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.ReactionsKeys.KEY_RECEIVER_ID, "u-2")
                .put(CometChatConstants.ReactionsKeys.KEY_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ReactionsKeys.KEY_REACTION, new JSONObject()
                        .put(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION, "❤️"));

        ReactionEvent event = ReactionEvent.fromJson(json);

        assertEquals("an absent parent reference must default to a safe zero",
                0L, event.getParentMessageId());
    }

    // ==================== REACT-05: reaction-history request ====================

    @Test
    public void react05_historyRequestIncludesEmojiAndMessageIdWhenSet() {
        ReactionsRequest request = new ReactionsRequest.ReactionsRequestBuilder()
                .setMessageId(500L)
                .setReaction("👍")
                .setLimit(25)
                .build();

        assertEquals(500L, request.getMessageId());
        assertEquals("👍", request.getReaction());
        assertEquals(25, request.getLimit());
    }

    @Test
    public void react05_historyRequestWithoutEmojiFilter_leavesItUnset() {
        ReactionsRequest request = new ReactionsRequest.ReactionsRequestBuilder()
                .setMessageId(500L)
                .build();

        assertEquals(500L, request.getMessageId());
        org.junit.Assert.assertNull("no emoji filter means the field stays unset", request.getReaction());
    }
}
