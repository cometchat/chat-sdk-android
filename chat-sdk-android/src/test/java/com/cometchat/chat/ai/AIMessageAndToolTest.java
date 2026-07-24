package com.cometchat.chat.ai;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.AIAssistantMessage;
import com.cometchat.chat.models.AIToolArgumentMessage;
import com.cometchat.chat.models.AIToolCall;
import com.cometchat.chat.models.AIToolCallFunction;
import com.cometchat.chat.models.AIToolResultMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: AI Assistant Messaging / message and tool-call representation.
 *
 * <p>Covers:
 * <ul>
 *   <li>AI-09 / MSG-19 — a tool invocation (id, function name, arguments) and its structured
 *       representation; agentic messages expose run/thread/toolCall metadata.</li>
 *   <li>AI-10 — an assistant message carries standard chat fields plus AI-specific run/thread ids.</li>
 *   <li>MSG-16 — an assistant reply with an inline card is a single assistant message whose card
 *       lives in its content elements (not a separate card message type).</li>
 *   <li>MSG-18 — a tool-argument/result message resolves the full sender profile from the
 *       accompanying entity data.</li>
 * </ul>
 *
 * <p>Payloads omit {@code mentions} to avoid the {@code CometChat.getLoggedInUser()} branch.
 */
public class AIMessageAndToolTest {

    private JSONObject agenticEnvelope(String type) throws Exception {
        return new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID, 800L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, CometChatConstants.CATEGORY_AGENTIC)
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, type)
                .put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, "user-1")
                .put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_USER);
    }

    private JSONObject senderEntities(String uid, String name) throws Exception {
        JSONObject senderEntity = new JSONObject().put(CometChatConstants.ResponseKeys.KEY_ENTITITY,
                new JSONObject()
                        .put(CometChatConstants.UserKeys.USER_KEY_UID, uid)
                        .put(CometChatConstants.UserKeys.USER_KEY_NAME, name));
        return new JSONObject().put(CometChatConstants.MessageKeys.KEY_SENDER, senderEntity);
    }

    // ==================== AI-10 / MSG-19: assistant message metadata ====================

    @Test
    public void ai10_assistantMessage_carriesChatAndAiFields() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT, "Here is your answer.")
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID, 555L)
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID, "thread-9");
        JSONObject payload = agenticEnvelope(CometChatConstants.MESSAGE_TYPE_ASSISTANT)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        AIAssistantMessage message = AIAssistantMessage.fromJson(payload);

        assertEquals("standard chat id is present", 800L, message.getId());
        assertEquals(CometChatConstants.CATEGORY_AGENTIC, message.getCategory());
        assertEquals("Here is your answer.", message.getText());
        assertEquals("AI-specific run id is present", 555L, message.getRunId());
        assertEquals("thread-9", message.getThreadId());
    }

    // ==================== MSG-16: inline card lives in the assistant message ====================

    @Test
    public void msg16_assistantReplyWithInlineCard_isASingleMessageWithCardElement() throws Exception {
        JSONObject cardElement = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENT_TYPE, "card")
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENT_VALUE,
                        new JSONObject().put("title", "Product"));
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT, "Check this out")
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID, 556L)
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID, "thread-10")
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENTS, new JSONArray().put(cardElement));
        JSONObject payload = agenticEnvelope(CometChatConstants.MESSAGE_TYPE_ASSISTANT)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        AIAssistantMessage message = AIAssistantMessage.fromJson(payload);

        assertTrue("the reply stays a single AIAssistantMessage", message instanceof AIAssistantMessage);
        assertNotNull("the inline card is embedded in the content elements", message.getElements());
        assertFalse(message.getElements().isEmpty());
        assertEquals("card", message.getElements().get(0).getType());
        assertEquals("the message still carries its run identifiers", 556L, message.getRunId());
    }

    // ==================== AI-09 / MSG-19: tool call structured object ====================

    @Test
    public void ai09_toolCall_isAStructuredObjectWithFunctionNameAndArguments() throws Exception {
        JSONObject function = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_NAME, "getWeather")
                .put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_ARGUMENTS, "{\"city\":\"Mumbai\"}");
        JSONObject toolCallJson = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_ID, "tc-1")
                .put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_TYPE, "function")
                .put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION, function);

        AIToolCall toolCall = AIToolCall.fromJson(toolCallJson);

        assertEquals("tc-1", toolCall.getId());
        assertEquals("function", toolCall.getType());
        AIToolCallFunction fn = toolCall.getFunction();
        assertNotNull("the app must not hand-parse raw JSON for the function", fn);
        assertEquals("getWeather", fn.getName());
        assertEquals("{\"city\":\"Mumbai\"}", fn.getArguments());
    }

    @Test
    public void msg19_toolArgumentMessage_exposesRunThreadAndStructuredToolCalls() throws Exception {
        JSONObject function = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_NAME, "search")
                .put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION_ARGUMENTS, "{\"q\":\"cats\"}");
        JSONObject toolCall = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_ID, "tc-2")
                .put(CometChatConstants.MessageKeys.KEY_TOOL_CALL_FUNCTION, function);
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID, 557L)
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID, "thread-11")
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_TOOL_CALLS, new JSONArray().put(toolCall));
        JSONObject payload = agenticEnvelope(CometChatConstants.MESSAGE_TYPE_TOOL_ARGUMENTS)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        AIToolArgumentMessage message = AIToolArgumentMessage.fromJson(payload);

        assertEquals(557L, message.getRunId());
        assertEquals("thread-11", message.getThreadId());
        assertNotNull(message.getToolCalls());
        assertEquals(1, message.getToolCalls().size());
        assertEquals("search", message.getToolCalls().get(0).getFunction().getName());
    }

    // ==================== MSG-18: sender resolved from entity data ====================

    @Test
    public void msg18_toolArgumentMessage_resolvesSenderProfileFromEntities() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID, 558L)
                .put(CometChatConstants.ResponseKeys.KEY_ENTITIES, senderEntities("assistant-bot", "Support Bot"));
        JSONObject payload = agenticEnvelope(CometChatConstants.MESSAGE_TYPE_TOOL_ARGUMENTS)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        AIToolArgumentMessage message = AIToolArgumentMessage.fromJson(payload);

        assertNotNull("the full sender profile must be resolved from entity data", message.getSender());
        assertEquals("assistant-bot", message.getSender().getUid());
        assertEquals("Support Bot", message.getSender().getName());
    }

    @Test
    public void msg18_toolResultMessage_resolvesSenderProfileAndCarriesResult() throws Exception {
        JSONObject data = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT, "22°C and sunny")
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_TOOL_CALL_ID, "tc-3")
                .put(CometChatConstants.ResponseKeys.KEY_ENTITIES, senderEntities("assistant-bot", "Support Bot"));
        JSONObject payload = agenticEnvelope(CometChatConstants.MESSAGE_TYPE_TOOL_RESULT)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        AIToolResultMessage message = AIToolResultMessage.fromJson(payload);

        assertNotNull(message.getSender());
        assertEquals("assistant-bot", message.getSender().getUid());
        assertEquals("22°C and sunny", message.getText());
        assertEquals("tc-3", message.getToolCallId());
    }
}
