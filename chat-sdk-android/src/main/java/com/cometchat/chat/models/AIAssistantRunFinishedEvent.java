package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONObject;

/**
 * Represents an AI Assistant event that is triggered when an AI assistant run has finished.
 * This event extends AIAssistantBaseEvent and contains information about the completed
 * run, including the run ID and thread ID where the run was executed.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIAssistantRunFinishedEvent extends AIAssistantBaseEvent {
    private long runId;
    private String threadId;

    /**
     * Default constructor that initializes the parent AIAssistantBaseEvent.
     */
    public AIAssistantRunFinishedEvent() {
        super();
    }

    /**
     * Populates the event properties from a JSON object.
     * This method calls the parent fromJson method and then extracts specific properties
     * for run finished events from the data object.
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
            }
        }
    }

    /**
     * Converts the event to a JSON object.
     * This method calls the parent toJson method and adds specific properties
     * for run finished events to the data object.
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
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA, dataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Gets the run ID of the finished AI assistant run.
     *
     * @return The run ID
     */
    public long getRunId() {
        return runId;
    }

    /**
     * Sets the run ID of the finished AI assistant run.
     *
     * @param runId The run ID to set
     */
    public void setRunId(long runId) {
        this.runId = runId;
    }

    /**
     * Gets the thread ID where the AI assistant run was executed.
     *
     * @return The thread ID
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Sets the thread ID where the AI assistant run was executed.
     *
     * @param threadId The thread ID to set
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    /**
     * Compares this AIAssistantRunFinishedEvent with another object for content equality.
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
        if (!(other instanceof AIAssistantRunFinishedEvent)) return false;

        // Cast
        AIAssistantRunFinishedEvent that = (AIAssistantRunFinishedEvent) other;

        // Compare all fields specific to this class
        return runId == that.runId &&
                ContentEqualsHelper.stringsEqual(threadId, that.threadId);
    }

    @Override
    public String toString() {
        return "AIAssistantRunFinishedEvent{" +
            "id=" + getId() +
            ", type='" + getType() + '\'' +
            ", conversationId='" + getConversationId() + '\'' +
            ", parentId='" + getParentId() + '\'' +
            ", additionalProperties=" + getAdditionalProperties() +
            ", runId=" + runId +
            ", threadId='" + threadId + '\'' +
            '}';
    }

    // Parcelable implementation
    protected AIAssistantRunFinishedEvent(Parcel in) {
        super(in);
        runId = in.readLong();
        threadId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(runId);
        dest.writeString(threadId);
    }

    public static final Creator<AIAssistantRunFinishedEvent> CREATOR = new Creator<AIAssistantRunFinishedEvent>() {
        @Override
        public AIAssistantRunFinishedEvent createFromParcel(Parcel in) {
            return new AIAssistantRunFinishedEvent(in);
        }

        @Override
        public AIAssistantRunFinishedEvent[] newArray(int size) {
            return new AIAssistantRunFinishedEvent[size];
        }
    };

    @Override
    public AIAssistantRunFinishedEvent clone() {
        AIAssistantRunFinishedEvent clone = (AIAssistantRunFinishedEvent) super.clone();
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        return clone;
    }

}