package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONObject;

/**
 * Represents an AI Assistant event that is triggered when a message streaming has ended.
 * This event extends AIAssistantBaseEvent and contains information about the completed
 * message, including the message ID, run details, and streaming information.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIAssistantMessageEndedEvent extends AIAssistantBaseEvent {
    private String type;
    private String messageId;
    private long runId;
    private String threadId;
    private String streamMessageId;

    /**
     * Default constructor that initializes the parent AIAssistantBaseEvent.
     */
    public AIAssistantMessageEndedEvent() {
        super();
    }

    /**
     * Populates the event properties from a JSON object.
     * This method calls the parent fromJson method and then extracts specific properties
     * for message ended events from the data object.
     *
     * @param bodyJson The JSON object containing the event data
     */
    @Override
    public void fromJson(JSONObject bodyJson) {
        super.fromJson(bodyJson);
        if (bodyJson != null && bodyJson.has(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA)) {
            JSONObject dataObject = bodyJson.optJSONObject(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA);
            if (dataObject != null) {
                this.type = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_TYPE, "");
                this.messageId = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_MESSAGE_ID, "");
                this.runId = dataObject.optLong(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_RUN_ID, 0);
                this.threadId = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_THREAD_ID, "");
                this.streamMessageId = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_STREAM_MESSAGE_ID, "");
            }
        }
    }

    /**
     * Converts the event to a JSON object.
     * This method calls the parent toJson method and adds specific properties
     * for message ended events to the data object.
     *
     * @return A JSONObject containing the complete event data
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_TYPE, type);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_MESSAGE_ID, messageId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_RUN_ID, runId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_THREAD_ID, threadId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_STREAM_MESSAGE_ID, streamMessageId);
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA, dataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Gets the type of the message ended event.
     *
     * @return The event type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the message ended event.
     *
     * @param type The event type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the unique identifier of the message that has ended.
     *
     * @return The message ID
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the unique identifier of the message that has ended.
     *
     * @param messageId The message ID to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the run ID associated with this message ended event.
     *
     * @return The run ID
     */
    public long getRunId() {
        return runId;
    }

    /**
     * Sets the run ID associated with this message ended event.
     *
     * @param runId The run ID to set
     */
    public void setRunId(long runId) {
        this.runId = runId;
    }

    /**
     * Gets the thread ID where this message ended.
     *
     * @return The thread ID
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Sets the thread ID where this message ended.
     *
     * @param threadId The thread ID to set
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    /**
     * Gets the unique identifier of the streaming message that has ended.
     *
     * @return The stream message ID
     */
    public String getStreamMessageId() {
        return streamMessageId;
    }

    /**
     * Sets the unique identifier of the streaming message that has ended.
     *
     * @param streamMessageId The stream message ID to set
     */
    public void setStreamMessageId(String streamMessageId) {
        this.streamMessageId = streamMessageId;
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    /**
     * Compares this AIAssistantMessageEndedEvent with another object for content equality.
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality, including inherited fields from AIAssistantBaseEvent.
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    @Override
    public boolean contentEquals(Object other) {
        // Call parent's contentEquals first
        if (!super.contentEquals(other)) return false;

        // Type check for this specific class
        if (!(other instanceof AIAssistantMessageEndedEvent)) return false;

        // Cast
        AIAssistantMessageEndedEvent that = (AIAssistantMessageEndedEvent) other;

        // Compare all fields specific to this class
        return runId == that.runId &&
                ContentEqualsHelper.stringsEqual(type, that.type) &&
                ContentEqualsHelper.stringsEqual(messageId, that.messageId) &&
                ContentEqualsHelper.stringsEqual(threadId, that.threadId) &&
                ContentEqualsHelper.stringsEqual(streamMessageId, that.streamMessageId);
    }

    @Override
    public String toString() {
        return "AIAssistantMessageEndedEvent{" +
            "id=" + getId() +
            ", baseType='" + super.getType() + '\'' +
            ", conversationId='" + getConversationId() + '\'' +
            ", parentId='" + getParentId() + '\'' +
            ", additionalProperties=" + getAdditionalProperties() +
            ", type='" + type + '\'' +
            ", messageId='" + messageId + '\'' +
            ", runId=" + runId +
            ", threadId='" + threadId + '\'' +
            ", streamMessageId='" + streamMessageId + '\'' +
            '}';
    }

    // Parcelable implementation
    protected AIAssistantMessageEndedEvent(Parcel in) {
        super(in);
        type = in.readString();
        messageId = in.readString();
        runId = in.readLong();
        threadId = in.readString();
        streamMessageId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(type);
        dest.writeString(messageId);
        dest.writeLong(runId);
        dest.writeString(threadId);
        dest.writeString(streamMessageId);
    }

    public static final Creator<AIAssistantMessageEndedEvent> CREATOR = new Creator<AIAssistantMessageEndedEvent>() {
        @Override
        public AIAssistantMessageEndedEvent createFromParcel(Parcel in) {
            return new AIAssistantMessageEndedEvent(in);
        }

        @Override
        public AIAssistantMessageEndedEvent[] newArray(int size) {
            return new AIAssistantMessageEndedEvent[size];
        }
    };

    @Override
    public AIAssistantMessageEndedEvent clone() {
        AIAssistantMessageEndedEvent clone = (AIAssistantMessageEndedEvent) super.clone();
        clone.type = this.type;
        clone.messageId = this.messageId;
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        clone.streamMessageId = this.streamMessageId;
        return clone;
    }

}