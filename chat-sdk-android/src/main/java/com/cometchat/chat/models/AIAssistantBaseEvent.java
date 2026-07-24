package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base class for all AI Assistant events.
 * This class provides common functionality and properties for AI Assistant events
 * and serves as a factory for creating specific event types based on the event type.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIAssistantBaseEvent implements Parcelable, Cloneable {
    private long id;
    private String type;
    private String conversationId;
    private String parentId;
    private JSONObject additionalProperties;

    /**
     * Default constructor that initializes the additional properties as an empty JSONObject.
     */
    public AIAssistantBaseEvent() {
        this.additionalProperties = new JSONObject();
    }

    /**
     * Populates the event properties from a JSON object.
     * This method extracts common properties like type, conversationId, id, and parentId
     * from the provided JSON object and sets them on this event instance.
     *
     * @param bodyJson The JSON object containing the event data
     */
    public void fromJson(JSONObject bodyJson) {
        if (bodyJson != null) {
            this.type = bodyJson.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_TYPE, "");
            this.conversationId = bodyJson.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_CONVERSATION_ID, "");
            this.id = bodyJson.optLong(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_ID, 0);
            this.parentId = bodyJson.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_PARENT_ID, "");
        }
    }

    /**
     * Converts the event to a JSON object.
     * This method creates a JSON representation of the event with all the common properties.
     *
     * @return A JSONObject containing the event data
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_TYPE, type);
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_CONVERSATION_ID, conversationId);
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_ID, id);
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_PARENT_ID, parentId);
        } catch (Exception e) {
            Logger.error(AIAssistantBaseEvent.class.getName(), e.getMessage());
        }
        return json;
    }

    /**
     * Factory method to create specific AI Assistant event instances based on the event type.
     * This method examines the event type in the provided JSON and creates the appropriate
     * event subclass instance, then populates it with the JSON data.
     *
     * @param bodyJson The JSON object containing the event data with type information
     * @return An instance of the appropriate AIAssistantBaseEvent subclass, or null if the event type is unknown or bodyJson is null
     */
    public static AIAssistantBaseEvent processEvent(JSONObject bodyJson) {

        if (bodyJson == null) {
            return null;
        }

        String eventType = bodyJson.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_TYPE, "");
        AIAssistantBaseEvent event;

        switch (eventType) {
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_RUN_STARTED:
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TEXT_MESSAGE_START:
                event = new AIAssistantRunStartedEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_RUN_FINISHED:
                event = new AIAssistantRunFinishedEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TEXT_MESSAGE_END:
                event = new AIAssistantMessageEndedEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TEXT_MESSAGE_CONTENT:
                event = new AIAssistantContentReceivedEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TOOL_CALL_STARTED:
                event = new AIAssistantToolStartedEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TOOL_CALL_ENDED:
                event = new AIAssistantToolEndedEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TOOL_CALL_RESULT:
                event = new AIAssistantToolResultEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_TOOL_CALL_ARGUMENT:
                event = new AIAssistantToolArgumentEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD_STARTED:
                event = new AIAssistantCardStartedEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD:
                event = new AIAssistantCardReceivedEvent();
                break;
            case CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD_ENDED:
                event = new AIAssistantCardEndedEvent();
                break;
            default:
                Logger.error(AIAssistantBaseEvent.class.getName(), "Unknown AI Assistant event type: " + eventType);
                return null;
        }

        event.fromJson(bodyJson);

        return event;
    }

    // Getters and setters

    /**
     * Gets the unique identifier of the event.
     *
     * @return The event ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the event.
     *
     * @param id The event ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the type of the AI Assistant event.
     *
     * @return The event type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the AI Assistant event.
     *
     * @param type The event type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the conversation ID associated with this event.
     *
     * @return The conversation ID
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * Sets the conversation ID associated with this event.
     *
     * @param conversationId The conversation ID to set
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * Gets the parent ID of this event, if it's a child event.
     *
     * @return The parent event ID
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Sets the parent ID of this event.
     *
     * @param parentId The parent event ID to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the additional properties associated with this event.
     *
     * @return A JSONObject containing additional properties
     */
    public JSONObject getAdditionalProperties() {
        return additionalProperties;
    }

    /**
     * Sets the additional properties for this event.
     *
     * @param additionalProperties A JSONObject containing additional properties to set
     */
    public void setAdditionalProperties(JSONObject additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String toString() {
        return "AIAssistantBaseEvent{" +
            "id=" + id +
            ", type='" + type + '\'' +
            ", conversationId='" + conversationId + '\'' +
            ", parentId='" + parentId + '\'' +
            ", additionalProperties=" + additionalProperties +
            '}';
    }

    /**
     * Compares this AIAssistantBaseEvent with another object for content equality.
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality.
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        // Same reference check
        if (this == other) return true;

        // Null check
        if (other == null) return false;

        // Type check
        if (!(other instanceof AIAssistantBaseEvent)) return false;

        // Cast
        AIAssistantBaseEvent that = (AIAssistantBaseEvent) other;

        // Compare all fields
        return id == that.id &&
                ContentEqualsHelper.stringsEqual(type, that.type) &&
                ContentEqualsHelper.stringsEqual(conversationId, that.conversationId) &&
                ContentEqualsHelper.stringsEqual(parentId, that.parentId) &&
                ContentEqualsHelper.jsonObjectsEqual(additionalProperties, that.additionalProperties);
    }

    // Parcelable implementation
    protected AIAssistantBaseEvent(Parcel in) {
        id = in.readLong();
        type = in.readString();
        conversationId = in.readString();
        parentId = in.readString();
        String additionalPropsStr = in.readString();
        try {
            additionalProperties = additionalPropsStr != null ? new JSONObject(additionalPropsStr) : new JSONObject();
        } catch (JSONException e) {
            additionalProperties = new JSONObject();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(type);
        dest.writeString(conversationId);
        dest.writeString(parentId);
        dest.writeString(additionalProperties != null ? additionalProperties.toString() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AIAssistantBaseEvent> CREATOR = new Creator<AIAssistantBaseEvent>() {
        @Override
        public AIAssistantBaseEvent createFromParcel(Parcel in) {
            return new AIAssistantBaseEvent(in);
        }

        @Override
        public AIAssistantBaseEvent[] newArray(int size) {
            return new AIAssistantBaseEvent[size];
        }
    };

    @Override
    public AIAssistantBaseEvent clone() {
        try {
            AIAssistantBaseEvent clone = (AIAssistantBaseEvent) super.clone();
            if (this.additionalProperties != null) {
                clone.additionalProperties = new JSONObject(this.additionalProperties.toString());
            }
            return clone;
        } catch (CloneNotSupportedException | JSONException e) {
            AIAssistantBaseEvent clone = new AIAssistantBaseEvent();
            clone.id = this.id;
            clone.type = this.type;
            clone.conversationId = this.conversationId;
            clone.parentId = this.parentId;
            try {
                clone.additionalProperties = this.additionalProperties != null ? new JSONObject(this.additionalProperties.toString()) : new JSONObject();
            } catch (JSONException ex) {
                clone.additionalProperties = new JSONObject();
            }
            return clone;
        }
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

}