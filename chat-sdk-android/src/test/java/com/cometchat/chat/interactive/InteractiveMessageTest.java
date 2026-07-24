package com.cometchat.chat.interactive;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Interaction;
import com.cometchat.chat.models.InteractionGoal;
import com.cometchat.chat.models.InteractiveMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Interactive Messages (forms/buttons) and interaction tracking.
 *
 * <p>Covers:
 * <ul>
 *   <li>MSG-20 — an interactive message exposes the form/element definition, the completion goal,
 *       per-element interaction timestamps, and whether the sender may interact.</li>
 *   <li>INT-01 — an element interaction records which element and precisely when.</li>
 *   <li>INT-02 — a completion goal exposes its rule type and required elements.</li>
 * </ul>
 *
 * <p><b>Android divergence (INT-01):</b> {@code Interaction.fromJson} returns {@code null} unless
 * <em>both</em> {@code elementId} and {@code interactedAt} are present, rather than defaulting a
 * missing element id to empty or a missing timestamp to "now". The test documents this.
 */
public class InteractiveMessageTest {

    // ==================== MSG-20: interactive message definition ====================

    @Test
    public void msg20_interactiveMessage_exposesDataGoalAndSenderInteractionFlag() throws Exception {
        JSONObject interactiveData = new JSONObject()
                .put("type", "form")
                .put("fields", new JSONArray().put(new JSONObject().put("elementId", "name").put("type", "text")));
        JSONObject goal = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_TYPE, "anyAction")
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_ELEMENT_IDS, new JSONArray().put("name"));
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIVE_DATA, interactiveData)
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_GOAL, goal)
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ALLOW_SENDER_INTERACTION, true);
        JSONObject payload = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID, 810L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, CometChatConstants.CATEGORY_INTERACTIVE)
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, "form")
                .put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, "u-1")
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        InteractiveMessage message = InteractiveMessage.fromJson(payload);

        assertNotNull("the interactive form definition must be exposed", message.getInteractiveData());
        assertEquals("form", message.getInteractiveData().getString("type"));
        assertNotNull("the completion goal must be exposed", message.getInteractionGoal());
        assertEquals("anyAction", message.getInteractionGoal().getType());
        assertTrue("the sender-interaction flag must round-trip", message.isAllowSenderInteraction());
    }

    @Test
    public void msg20_interactiveMessage_exposesPerElementInteractionTimestamps() throws Exception {
        JSONObject interaction = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ELEMENT_ID, "submit")
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTED_AT, 1700000000L);
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIVE_DATA, new JSONObject().put("type", "form"))
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIONS, new JSONArray().put(interaction));
        JSONObject payload = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID, 811L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, CometChatConstants.CATEGORY_INTERACTIVE)
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, "form")
                .put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, "u-1")
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        InteractiveMessage message = InteractiveMessage.fromJson(payload);

        assertNotNull(message.getInteractions());
        assertEquals(1, message.getInteractions().size());
        assertEquals("submit", message.getInteractions().get(0).getElementId());
        assertEquals(1700000000L, message.getInteractions().get(0).getInteractedAt());
    }

    @Test
    public void msg20_allowSenderInteractionDefaultsToFalseWhenAbsent() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIVE_DATA, new JSONObject().put("type", "form"));
        JSONObject payload = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID, 812L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, CometChatConstants.CATEGORY_INTERACTIVE)
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, "form")
                .put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, "u-1")
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        InteractiveMessage message = InteractiveMessage.fromJson(payload);

        assertFalse(message.isAllowSenderInteraction());
    }

    // ==================== INT-01: element interaction ====================

    @Test
    public void int01_interaction_recordsElementAndTimestamp() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ELEMENT_ID, "poll-option-2")
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTED_AT, 1700000123L);

        Interaction interaction = Interaction.fromJson(json);

        assertNotNull(interaction);
        assertEquals("poll-option-2", interaction.getElementId());
        assertEquals(1700000123L, interaction.getInteractedAt());
    }

    @Test
    public void int01_interactionMissingEitherField_returnsNullOnAndroid() throws Exception {
        // Android divergence: both fields are required; a partial interaction yields null.
        Interaction missingTimestamp = Interaction.fromJson(
                new JSONObject().put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ELEMENT_ID, "e1"));
        Interaction missingElement = Interaction.fromJson(
                new JSONObject().put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTED_AT, 1L));

        assertNull("a missing timestamp yields null, not a defaulted 'now'", missingTimestamp);
        assertNull("a missing element id yields null, not an empty string", missingElement);
    }

    // ==================== INT-02: completion goal ====================

    @Test
    public void int02_goal_exposesRuleTypeAndRequiredElements() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_TYPE, "allOf")
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_ELEMENT_IDS,
                        new JSONArray().put("name").put("email"));

        InteractionGoal goal = InteractionGoal.fromJsom(json);

        assertEquals("allOf", goal.getType());
        assertEquals(2, goal.getElementIds().size());
        assertTrue(goal.getElementIds().contains("name"));
        assertTrue(goal.getElementIds().contains("email"));
    }

    @Test
    public void int02_goalWithoutElementIds_leavesThemUnset() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_TYPE, "anyAction");

        InteractionGoal goal = InteractionGoal.fromJsom(json);

        assertEquals("anyAction", goal.getType());
        assertNull("no required-elements list means the field stays unset", goal.getElementIds());
    }
}
