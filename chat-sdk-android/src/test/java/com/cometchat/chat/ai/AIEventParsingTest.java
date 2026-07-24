package com.cometchat.chat.ai;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.AIAssistantBaseEvent;
import com.cometchat.chat.models.AIAssistantCardEndedEvent;
import com.cometchat.chat.models.AIAssistantCardReceivedEvent;
import com.cometchat.chat.models.AIAssistantCardStartedEvent;
import com.cometchat.chat.models.AIAssistantContentReceivedEvent;
import com.cometchat.chat.models.AIAssistantMessageEndedEvent;
import com.cometchat.chat.models.AIAssistantRunFinishedEvent;
import com.cometchat.chat.models.AIAssistantRunStartedEvent;
import com.cometchat.chat.models.AIAssistantToolArgumentEvent;
import com.cometchat.chat.models.AIAssistantToolEndedEvent;
import com.cometchat.chat.models.AIAssistantToolResultEvent;
import com.cometchat.chat.models.AIAssistantToolStartedEvent;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: AI Assistant Messaging / streaming lifecycle events (also MSG-17).
 *
 * <p>Covers the AI streaming event family, each dispatched by
 * {@code AIAssistantBaseEvent.processEvent} to its own typed object:
 * <ul>
 *   <li>AI-01 run started, AI-02 run finished, AI-03 content delta, AI-04 message ended.</li>
 *   <li>AI-05 tool started, AI-06 tool argument, AI-07 tool result, AI-08 tool ended.</li>
 *   <li>MSG-17 card started / card / card ended streaming events.</li>
 *   <li>AI-11 malformed/empty payload, AI-12 numeric id encodings, AI-13 unknown type,
 *       AI-14 round-trip.</li>
 * </ul>
 *
 * <p><b>Android divergence (AI-13):</b> {@code processEvent} returns {@code null} for an
 * unrecognized event type rather than preserving it as a base event; the test documents this.
 */
public class AIEventParsingTest {

    /** {type, id, conversationId, parentId, data:{...}} — the shape processEvent consumes. */
    private JSONObject event(String type, long id, JSONObject data) throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_TYPE, type)
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_ID, id)
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_CONVERSATION_ID, "conv-1")
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_PARENT_ID, "parent-1");
        if (data != null) {
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA, data);
        }
        return json;
    }

    private JSONObject runData() throws Exception {
        return new JSONObject()
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_RUN_ID, 555L)
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_THREAD_ID, "thread-9")
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_STREAM_MESSAGE_ID, "stream-1");
    }

    // ==================== AI-01 / AI-02: run lifecycle ====================

    @Test
    public void ai01_runStartedEvent_isDispatchedWithRunAndThreadIds() throws Exception {
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_RUN_STARTED, 1L, runData()));

        assertTrue(event instanceof AIAssistantRunStartedEvent);
        AIAssistantRunStartedEvent started = (AIAssistantRunStartedEvent) event;
        assertEquals(555L, started.getRunId());
        assertEquals("thread-9", started.getThreadId());
        assertEquals("stream-1", started.getStreamMessageId());
    }

    @Test
    public void ai01_textMessageStart_alsoDispatchesToRunStartedEvent() throws Exception {
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TEXT_MESSAGE_START, 1L, runData()));

        assertTrue(event instanceof AIAssistantRunStartedEvent);
    }

    @Test
    public void ai02_runFinishedEvent_isDispatchedWithRunAndThreadIds() throws Exception {
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_RUN_FINISHED, 2L, runData()));

        assertTrue(event instanceof AIAssistantRunFinishedEvent);
        assertEquals(555L, ((AIAssistantRunFinishedEvent) event).getRunId());
        assertEquals("thread-9", ((AIAssistantRunFinishedEvent) event).getThreadId());
    }

    // ==================== AI-03 / AI-04: content delta + message ended ====================

    @Test
    public void ai03_contentReceivedEvent_carriesTheIncrementalDelta() throws Exception {
        JSONObject data = runData().put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_DELTA, "Hello 🎉\n```code```");
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TEXT_MESSAGE_CONTENT, 3L, data));

        assertTrue(event instanceof AIAssistantContentReceivedEvent);
        assertEquals("emoji, newlines and code fences in a delta must survive",
                "Hello 🎉\n```code```", ((AIAssistantContentReceivedEvent) event).getDelta());
    }

    @Test
    public void ai04_messageEndedEvent_isDispatched() throws Exception {
        JSONObject data = runData().put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_MESSAGE_ID, "4242");
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TEXT_MESSAGE_END, 4L, data));

        assertTrue(event instanceof AIAssistantMessageEndedEvent);
        // The message-ended event exposes its final message id as a String (the segment id).
        assertEquals("4242", ((AIAssistantMessageEndedEvent) event).getMessageId());
    }

    // ==================== AI-05..08: tool lifecycle ====================

    private JSONObject toolData() throws Exception {
        return runData()
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_TOOL_CALL_ID, "tc-1")
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_TOOL_CALL_NAME, "getWeather")
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_DISPLAY_NAME, "Weather")
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_EXECUTION_TEXT, "Running Weather");
    }

    @Test
    public void ai05_toolStartedEvent_carriesToolIdentity() throws Exception {
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TOOL_CALL_STARTED, 5L, toolData()));

        assertTrue(event instanceof AIAssistantToolStartedEvent);
        AIAssistantToolStartedEvent started = (AIAssistantToolStartedEvent) event;
        assertEquals("tc-1", started.getToolCallId());
        assertEquals("getWeather", started.getToolCallName());
        assertEquals("Weather", started.getDisplayName());
    }

    @Test
    public void ai06_toolArgumentEvent_carriesTheArgumentDelta() throws Exception {
        JSONObject data = toolData().put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_DELTA, "{\"city\":\"Mum");
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TOOL_CALL_ARGUMENT, 6L, data));

        assertTrue(event instanceof AIAssistantToolArgumentEvent);
        assertEquals("{\"city\":\"Mum", ((AIAssistantToolArgumentEvent) event).getDelta());
    }

    @Test
    public void ai07_toolResultEvent_carriesResultContentAndRole() throws Exception {
        JSONObject data = toolData()
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_CONTENT, "22°C and sunny")
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_ROLE, "tool");
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TOOL_CALL_RESULT, 7L, data));

        assertTrue(event instanceof AIAssistantToolResultEvent);
        assertEquals("22°C and sunny", ((AIAssistantToolResultEvent) event).getContent());
        assertEquals("tool", ((AIAssistantToolResultEvent) event).getRole());
    }

    @Test
    public void ai08_toolEndedEvent_carriesToolIdentityAndArguments() throws Exception {
        JSONObject data = toolData().put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_ARGUMENTS, "{\"city\":\"Mumbai\"}");
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TOOL_CALL_ENDED, 8L, data));

        assertTrue(event instanceof AIAssistantToolEndedEvent);
        assertEquals("tc-1", ((AIAssistantToolEndedEvent) event).getToolCallId());
        assertEquals("{\"city\":\"Mumbai\"}", ((AIAssistantToolEndedEvent) event).getArguments());
    }

    // ==================== MSG-17: card streaming events ====================

    @Test
    public void msg17_cardStartedEvent_isDispatched() throws Exception {
        JSONObject data = runData().put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_CARD_ID, "card-1");
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD_STARTED, 9L, data));

        assertTrue(event instanceof AIAssistantCardStartedEvent);
        assertEquals("card-1", ((AIAssistantCardStartedEvent) event).getCardId());
    }

    @Test
    public void msg17_cardReceivedEvent_carriesTheCardPayload() throws Exception {
        JSONObject card = new JSONObject().put("version", 1).put("body", "hi");
        JSONObject data = runData()
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_CARD_ID, "card-2")
                .put(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD, card);
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD, 10L, data));

        assertTrue(event instanceof AIAssistantCardReceivedEvent);
        assertNotNull("a card streaming event must not be silently dropped",
                ((AIAssistantCardReceivedEvent) event).getCard());
        assertEquals(1, ((AIAssistantCardReceivedEvent) event).getCard().getInt("version"));
    }

    @Test
    public void msg17_cardEndedEvent_isDispatched() throws Exception {
        JSONObject data = runData().put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_CARD_ID, "card-3");
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD_ENDED, 11L, data));

        assertTrue(event instanceof AIAssistantCardEndedEvent);
        assertEquals("card-3", ((AIAssistantCardEndedEvent) event).getCardId());
    }

    // ==================== AI-11: malformed / empty payload ====================

    @Test
    public void ai11_nullPayload_returnsNullWithoutCrashing() {
        assertNull(AIAssistantBaseEvent.processEvent(null));
    }

    @Test
    public void ai11_emptyPayload_returnsNullWithoutCrashing() {
        assertNull("an empty body has no type and yields no event", AIAssistantBaseEvent.processEvent(new JSONObject()));
    }

    @Test
    public void ai11_eventWithoutDataBlock_stillParsesBaseFields() throws Exception {
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_RUN_STARTED, 12L, null));

        assertNotNull(event);
        assertEquals("absent data leaves run id at its default", 0L,
                ((AIAssistantRunStartedEvent) event).getRunId());
    }

    // ==================== AI-12: numeric id encodings ====================

    @Test
    public void ai12_numericIdAsNumber_isParsed() throws Exception {
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_RUN_STARTED, 987654L, runData()));

        assertEquals(987654L, event.getId());
    }

    @Test
    public void ai12_numericIdAsNumericString_isNormalized() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_TYPE,
                        CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_RUN_STARTED)
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_ID, "987654")
                .put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA, runData());

        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(json);

        assertEquals("a numeric id sent as a string must be normalized to a long",
                987654L, event.getId());
    }

    // ==================== AI-13: unknown event type ====================

    @Test
    public void ai13_unknownEventType_returnsNullOnAndroid() throws Exception {
        // Android divergence: an unrecognized type is dropped (null) rather than preserved.
        AIAssistantBaseEvent event = AIAssistantBaseEvent.processEvent(
                event("some_future_event_type", 13L, runData()));

        assertNull(event);
    }

    // ==================== AI-14: round-trip ====================

    @Test
    public void ai14_runStartedEvent_roundTripsThroughToJson() throws Exception {
        AIAssistantRunStartedEvent event = (AIAssistantRunStartedEvent) AIAssistantBaseEvent.processEvent(
                event(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_RUN_STARTED, 14L, runData()));

        JSONObject serialized = event.toJson();
        AIAssistantRunStartedEvent reparsed = (AIAssistantRunStartedEvent) AIAssistantBaseEvent.processEvent(serialized);

        assertEquals(event.getId(), reparsed.getId());
        assertEquals(event.getRunId(), reparsed.getRunId());
        assertEquals(event.getThreadId(), reparsed.getThreadId());
        assertEquals(event.getType(), reparsed.getType());
    }
}
