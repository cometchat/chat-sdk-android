package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONObject;

/**
 * Represents an AI Assistant event that is triggered when content is received during streaming.
 * This event contains information about the streaming message, run details, and the delta content
 * that was received in the current chunk.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIAssistantContentReceivedEvent extends AIAssistantBaseEvent {
    private String streamMessageId;
    private long runId;
    private String threadId;
    private String delta;

    /**
     * Default constructor that initializes the parent AIAssistantBaseEvent.
     */
    public AIAssistantContentReceivedEvent() {
        super();
    }

    /**
     * Populates the event properties from a JSON object.
     * This method calls the parent fromJson method and then extracts specific properties
     * for content received events from the data object.
     *
     * @param bodyJson The JSON object containing the event data
     */
    @Override
    public void fromJson(JSONObject bodyJson) {
        super.fromJson(bodyJson);
        if (bodyJson != null && bodyJson.has(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA)) {
            JSONObject dataObject = bodyJson.optJSONObject(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA);
            if (dataObject != null) {
                this.streamMessageId = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_STREAM_MESSAGE_ID, "");
                this.runId = dataObject.optLong(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_RUN_ID, 0);
                this.threadId = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_THREAD_ID, "");
                this.delta = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_DELTA, "");
            }
        }
    }

    /**
     * Converts the event to a JSON object.
     * This method calls the parent toJson method and adds specific properties
     * for content received events to the data object.
     *
     * @return A JSONObject containing the complete event data
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_STREAM_MESSAGE_ID, streamMessageId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_RUN_ID, runId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_THREAD_ID, threadId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_DELTA, delta);
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA, dataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Gets the unique identifier of the streaming message.
     *
     * @return The stream message ID
     */
    public String getStreamMessageId() {
        return streamMessageId;
    }

    /**
     * Sets the unique identifier of the streaming message.
     *
     * @param streamMessageId The stream message ID to set
     */
    public void setStreamMessageId(String streamMessageId) {
        this.streamMessageId = streamMessageId;
    }

    /**
     * Gets the run ID associated with this content received event.
     *
     * @return The run ID
     */
    public long getRunId() {
        return runId;
    }

    /**
     * Sets the run ID associated with this content received event.
     *
     * @param runId The run ID to set
     */
    public void setRunId(long runId) {
        this.runId = runId;
    }

    /**
     * Gets the thread ID where this content was received.
     *
     * @return The thread ID
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Sets the thread ID where this content was received.
     *
     * @param threadId The thread ID to set
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    /**
     * Gets the delta content that was received in this chunk.
     * The delta represents the incremental content received during streaming.
     *
     * @return The delta content
     */
    public String getDelta() {
        return delta;
    }

    /**
     * Sets the delta content that was received in this chunk.
     *
     * @param delta The delta content to set
     */
    public void setDelta(String delta) {
        this.delta = delta;
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    /**
     * Compares this AIAssistantContentReceivedEvent with another object for content equality.
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
        if (!(other instanceof AIAssistantContentReceivedEvent)) return false;

        // Cast
        AIAssistantContentReceivedEvent that = (AIAssistantContentReceivedEvent) other;

        // Compare all fields specific to this class
        return runId == that.runId &&
                ContentEqualsHelper.stringsEqual(streamMessageId, that.streamMessageId) &&
                ContentEqualsHelper.stringsEqual(threadId, that.threadId) &&
                ContentEqualsHelper.stringsEqual(delta, that.delta);
    }

    @Override
    public String toString() {
        return "AIAssistantContentReceivedEvent{" +
            "id=" + getId() +
            ", type='" + getType() + '\'' +
            ", conversationId='" + getConversationId() + '\'' +
            ", parentId='" + getParentId() + '\'' +
            ", additionalProperties=" + getAdditionalProperties() +
            ", streamMessageId='" + streamMessageId + '\'' +
            ", runId=" + runId +
            ", threadId='" + threadId + '\'' +
            ", delta='" + delta + '\'' +
            '}';
    }

    // Parcelable implementation
    protected AIAssistantContentReceivedEvent(Parcel in) {
        super(in);
        streamMessageId = in.readString();
        runId = in.readLong();
        threadId = in.readString();
        delta = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(streamMessageId);
        dest.writeLong(runId);
        dest.writeString(threadId);
        dest.writeString(delta);
    }

    public static final Creator<AIAssistantContentReceivedEvent> CREATOR = new Creator<AIAssistantContentReceivedEvent>() {
        @Override
        public AIAssistantContentReceivedEvent createFromParcel(Parcel in) {
            return new AIAssistantContentReceivedEvent(in);
        }

        @Override
        public AIAssistantContentReceivedEvent[] newArray(int size) {
            return new AIAssistantContentReceivedEvent[size];
        }
    };

    @Override
    public AIAssistantContentReceivedEvent clone() {
        AIAssistantContentReceivedEvent clone = (AIAssistantContentReceivedEvent) super.clone();
        clone.streamMessageId = this.streamMessageId;
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        clone.delta = this.delta;
        return clone;
    }

}