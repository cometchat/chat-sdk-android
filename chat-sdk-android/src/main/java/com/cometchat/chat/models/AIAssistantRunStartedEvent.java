package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONObject;

/**
 * Represents an AI Assistant event that is triggered when an AI assistant run has started.
 * This event extends AIAssistantBaseEvent and contains information about the newly started
 * run, including the run ID, thread ID, stream message ID, and role information.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIAssistantRunStartedEvent extends AIAssistantBaseEvent {
    private long runId;
    private String threadId;
    private String streamMessageId;
    private String role;

    /**
     * Default constructor that initializes the parent AIAssistantBaseEvent.
     */
    public AIAssistantRunStartedEvent() {
        super();
    }

    /**
     * Populates the event properties from a JSON object.
     * This method calls the parent fromJson method and then extracts specific properties
     * for run started events from the data object.
     *
     * @param bodyJson The JSON object containing the event data
     */
    @Override
    public void fromJson(JSONObject bodyJson) {
        super.fromJson(bodyJson);
        if (bodyJson != null && bodyJson.has(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA)) {
            JSONObject dataObject = bodyJson.optJSONObject(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA);
            if (dataObject != null) {
                this.runId = dataObject.optLong(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_RUN_ID, 0);
                this.threadId = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_THREAD_ID, "");
                this.streamMessageId = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_STREAM_MESSAGE_ID, "");
                this.role = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_ROLE, "");
            }
        }
    }

    /**
     * Converts the event to a JSON object.
     * This method calls the parent toJson method and adds specific properties
     * for run started events to the data object.
     *
     * @return A JSONObject containing the complete event data
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_RUN_ID, runId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_THREAD_ID, threadId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_STREAM_MESSAGE_ID, streamMessageId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_ROLE, role);
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA, dataObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Gets the run ID of the started AI assistant run.
     *
     * @return The run ID
     */
    public long getRunId() {
        return runId;
    }

    /**
     * Sets the run ID of the started AI assistant run.
     *
     * @param runId The run ID to set
     */
    public void setRunId(long runId) {
        this.runId = runId;
    }

    /**
     * Gets the thread ID where the AI assistant run was started.
     *
     * @return The thread ID
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Sets the thread ID where the AI assistant run was started.
     *
     * @param threadId The thread ID to set
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    /**
     * Gets the unique identifier of the streaming message associated with this run.
     *
     * @return The stream message ID
     */
    public String getStreamMessageId() {
        return streamMessageId;
    }

    /**
     * Sets the unique identifier of the streaming message associated with this run.
     *
     * @param streamMessageId The stream message ID to set
     */
    public void setStreamMessageId(String streamMessageId) {
        this.streamMessageId = streamMessageId;
    }

    /**
     * Gets the role of the AI assistant in this run (e.g., "assistant", "user", etc.).
     *
     * @return The role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the AI assistant in this run.
     *
     * @param role The role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    /**
     * Compares this AIAssistantRunStartedEvent with another object for content equality.
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
        if (!(other instanceof AIAssistantRunStartedEvent)) return false;

        // Cast
        AIAssistantRunStartedEvent that = (AIAssistantRunStartedEvent) other;

        // Compare all fields specific to this class
        return runId == that.runId &&
                ContentEqualsHelper.stringsEqual(threadId, that.threadId) &&
                ContentEqualsHelper.stringsEqual(streamMessageId, that.streamMessageId) &&
                ContentEqualsHelper.stringsEqual(role, that.role);
    }

    @Override
    public String toString() {
        return "AIAssistantRunStartedEvent{" +
            "id=" + getId() +
            ", type='" + getType() + '\'' +
            ", conversationId='" + getConversationId() + '\'' +
            ", parentId='" + getParentId() + '\'' +
            ", additionalProperties=" + getAdditionalProperties() +
            ", runId=" + runId +
            ", threadId='" + threadId + '\'' +
            ", streamMessageId='" + streamMessageId + '\'' +
            ", role='" + role + '\'' +
            '}';
    }

    // Parcelable implementation
    protected AIAssistantRunStartedEvent(Parcel in) {
        super(in);
        runId = in.readLong();
        threadId = in.readString();
        streamMessageId = in.readString();
        role = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(runId);
        dest.writeString(threadId);
        dest.writeString(streamMessageId);
        dest.writeString(role);
    }

    public static final Creator<AIAssistantRunStartedEvent> CREATOR = new Creator<AIAssistantRunStartedEvent>() {
        @Override
        public AIAssistantRunStartedEvent createFromParcel(Parcel in) {
            return new AIAssistantRunStartedEvent(in);
        }

        @Override
        public AIAssistantRunStartedEvent[] newArray(int size) {
            return new AIAssistantRunStartedEvent[size];
        }
    };

    @Override
    public AIAssistantRunStartedEvent clone() {
        AIAssistantRunStartedEvent clone = (AIAssistantRunStartedEvent) super.clone();
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        clone.streamMessageId = this.streamMessageId;
        clone.role = this.role;
        return clone;
    }

}