package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.CometChatHelper;
import com.cometchat.chat.models.AIAssistantBaseEvent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a stream message event in the CometChat SDK.
 * This class extends CometChatEvent and is specifically designed to handle streaming events
 * from AI assistants. It encapsulates AI assistant events that are delivered through
 * WebSocket connections during real-time streaming scenarios.
 *
 * <p>Stream message events are used when AI assistants are generating content in real-time,
 * such as during tool executions, content generation, or run lifecycle events. This class
 * provides a way to wrap AIAssistantBaseEvent instances and deliver them through the
 * CometChat event system.</p>
 *
 * @author CometChat Team
 * @version 4.0
 */
class CometChatStreamMessageEvent extends CometChatEvent {
    private AIAssistantBaseEvent event;

    /**
     * Constructs a new CometChatStreamMessageEvent with the specified connection parameters.
     * This constructor initializes the stream message event with all necessary identifiers
     * for the WebSocket connection and sets the event type to "streamed_message".
     *
     * @param appId The application ID for the CometChat app
     * @param receiver The unique identifier of the message receiver
     * @param receiverType The type of receiver (e.g., "user" or "group")
     * @param sender The unique identifier of the message sender
     * @param deviceId The unique identifier of the device sending the event
     */
    public CometChatStreamMessageEvent(String appId, String receiver, String receiverType, String sender, String deviceId) {
        super(appId, receiver, receiverType, sender, deviceId);
        setType(CometChatConstants.WSKeys.KEY_TYPE_STREAMED_MESSAGE);
    }

    /**
     * Converts the stream message event to its string representation.
     * This method is part of the CometChatEvent interface but returns null
     * as stream message events are not typically serialized to strings.
     *
     * @return null as string serialization is not implemented for stream events
     * @throws JSONException if there's an error during JSON processing (not applicable here)
     */
    @Override
    protected String getAsString() throws JSONException {
        return null;
    }

    /**
     * Converts the stream message event to its JSON object representation.
     * This method is part of the CometChatEvent interface but returns null
     * as stream message events use a different serialization approach.
     *
     * @return null as JSON object serialization is not implemented for stream events
     * @throws JSONException if there's an error during JSON processing (not applicable here)
     */
    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        return null;
    }

    /**
     * Gets the AI assistant event associated with this stream message event.
     * The event contains specific information about the AI assistant operation
     * that triggered this stream event.
     *
     * @return The AIAssistantBaseEvent instance, or null if no event is set
     */
    public AIAssistantBaseEvent getEvent() {
        return event;
    }

    /**
     * Sets the AI assistant event for this stream message event.
     * This method allows associating a specific AI assistant event with the stream,
     * such as content received events, tool call events, or run lifecycle events.
     *
     * @param event The AIAssistantBaseEvent to associate with this stream event (can be null)
     */
    public void setEvent(AIAssistantBaseEvent event) {
        this.event = event;
    }

    /**
     * Creates a CometChatStreamMessageEvent instance from a JSON object.
     * This factory method parses the JSON representation received from the WebSocket
     * and constructs a fully populated stream message event. It extracts connection
     * parameters and processes the body to create the appropriate AI assistant event.
     *
     * <p>The method handles the following JSON structure:</p>
     * <ul>
     *   <li>appId - Application identifier</li>
     *   <li>receiver - Receiver identifier</li>
     *   <li>receiverType - Type of receiver</li>
     *   <li>sender - Sender identifier</li>
     *   <li>deviceId - Device identifier</li>
     *   <li>body - Event body containing AI assistant event data</li>
     * </ul>
     *
     * @param json The JSON object containing the stream message event data
     * @return A fully populated CometChatStreamMessageEvent instance
     * @throws JSONException if there's an error parsing the JSON data
     */
    public static CometChatStreamMessageEvent fromJSON(JSONObject json) throws JSONException {
        String appId = null;
        if (json.has(CometChatConstants.WSKeys.KEY_APP_ID))
            appId = json.optString(CometChatConstants.WSKeys.KEY_APP_ID);

        String receiver = null;
        if (json.has(CometChatConstants.WSKeys.KEY_RECEIVER))
            receiver = json.optString(CometChatConstants.WSKeys.KEY_RECEIVER);

        String receiverType = null;
        if (json.has(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE))
            receiverType = json.optString(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE);

        String sender = null;
        if (json.has(CometChatConstants.WSKeys.KEY_SENDER))
            sender = json.optString(CometChatConstants.WSKeys.KEY_SENDER);

        String deviceId = null;
        if (json.has(CometChatConstants.WSKeys.KEY_DEVICE_ID))
            deviceId = json.optString(CometChatConstants.WSKeys.KEY_DEVICE_ID);

        JSONObject body = null;
        if (json.has(CometChatConstants.WSKeys.KEY_BODY))
            body = json.optJSONObject(CometChatConstants.WSKeys.KEY_BODY);

        CometChatStreamMessageEvent streamEvent = new CometChatStreamMessageEvent(appId, receiver, receiverType, sender, deviceId);
        AIAssistantBaseEvent eventObj = null;

        if (body != null) {
            eventObj = AIAssistantBaseEvent.processEvent(body);
        }

        streamEvent.setEvent(eventObj);
        return streamEvent;
    }
}