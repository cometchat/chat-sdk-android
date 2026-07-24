package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONObject;

/**
 * Represents an AI Assistant event that is triggered when a tool call has ended.
 * This event extends AIAssistantBaseEvent and contains information about the completed
 * tool call, including its ID, name, display name, execution text, and arguments.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIAssistantToolEndedEvent extends AIAssistantBaseEvent {
    private long runId;
    private String threadId;
    private String toolCallId;
    private String toolCallName;
    private String displayName;
    private String executionText;
    private String arguments;

    /**
     * Default constructor that initializes the parent AIAssistantBaseEvent.
     */
    public AIAssistantToolEndedEvent() {
        super();
    }

    /**
     * Populates the event properties from a JSON object.
     * This method calls the parent fromJson method and then extracts specific properties
     * for tool ended events from the data object.
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
                this.toolCallId = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_TOOL_CALL_ID, "");
                this.toolCallName = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_TOOL_CALL_NAME, "");
                this.displayName = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_DISPLAY_NAME, "");
                this.executionText = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_EXECUTION_TEXT, "");
                this.arguments = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_ARGUMENTS, "");
            }
        }
    }

    /**
     * Converts the event to a JSON object.
     * This method calls the parent toJson method and adds specific properties
     * for tool ended events to the data object.
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
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_TOOL_CALL_ID, toolCallId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_TOOL_CALL_NAME, toolCallName);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_DISPLAY_NAME, displayName);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_EXECUTION_TEXT, executionText);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_ARGUMENTS, arguments);
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA, dataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Gets the run ID associated with this tool ended event.
     *
     * @return The run ID
     */
    public long getRunId() {
        return runId;
    }

    /**
     * Sets the run ID associated with this tool ended event.
     *
     * @param runId The run ID to set
     */
    public void setRunId(long runId) {
        this.runId = runId;
    }

    /**
     * Gets the thread ID where this tool call ended.
     *
     * @return The thread ID
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Sets the thread ID where this tool call ended.
     *
     * @param threadId The thread ID to set
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    /**
     * Gets the unique identifier of the tool call that has ended.
     *
     * @return The tool call ID
     */
    public String getToolCallId() {
        return toolCallId;
    }

    /**
     * Sets the unique identifier of the tool call that has ended.
     *
     * @param toolCallId The tool call ID to set
     */
    public void setToolCallId(String toolCallId) {
        this.toolCallId = toolCallId;
    }

    /**
     * Gets the name of the tool that has ended execution.
     *
     * @return The tool call name
     */
    public String getToolCallName() {
        return toolCallName;
    }

    /**
     * Sets the name of the tool that has ended execution.
     *
     * @param toolCallName The tool call name to set
     */
    public void setToolCallName(String toolCallName) {
        this.toolCallName = toolCallName;
    }

    /**
     * Gets the display name for the tool call, used for UI presentation.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name for the tool call, used for UI presentation.
     *
     * @param displayName The display name to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the execution text describing what the tool was doing.
     *
     * @return The execution text
     */
    public String getExecutionText() {
        return executionText;
    }

    /**
     * Sets the execution text describing what the tool was doing.
     *
     * @param executionText The execution text to set
     */
    public void setExecutionText(String executionText) {
        this.executionText = executionText;
    }

    /**
     * Gets the arguments that were passed to the tool call.
     *
     * @return The arguments as a string
     */
    public String getArguments() {
        return arguments;
    }

    /**
     * Sets the arguments that were passed to the tool call.
     *
     * @param arguments The arguments to set
     */
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "AIAssistantToolEndedEvent{" +
            "id=" + getId() +
            ", type='" + getType() + '\'' +
            ", conversationId='" + getConversationId() + '\'' +
            ", parentId='" + getParentId() + '\'' +
            ", additionalProperties=" + getAdditionalProperties() +
            ", runId=" + runId +
            ", threadId='" + threadId + '\'' +
            ", toolCallId='" + toolCallId + '\'' +
            ", toolCallName='" + toolCallName + '\'' +
            ", displayName='" + displayName + '\'' +
            ", executionText='" + executionText + '\'' +
            ", arguments='" + arguments + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    /**
     * Compares this AIAssistantToolEndedEvent with another object for content equality.
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
        if (!(other instanceof AIAssistantToolEndedEvent)) return false;

        // Cast
        AIAssistantToolEndedEvent that = (AIAssistantToolEndedEvent) other;

        // Compare all fields specific to this class
        return runId == that.runId &&
                ContentEqualsHelper.stringsEqual(threadId, that.threadId) &&
                ContentEqualsHelper.stringsEqual(toolCallId, that.toolCallId) &&
                ContentEqualsHelper.stringsEqual(toolCallName, that.toolCallName) &&
                ContentEqualsHelper.stringsEqual(displayName, that.displayName) &&
                ContentEqualsHelper.stringsEqual(executionText, that.executionText) &&
                ContentEqualsHelper.stringsEqual(arguments, that.arguments);
    }

    // Parcelable implementation
    protected AIAssistantToolEndedEvent(Parcel in) {
        super(in);
        runId = in.readLong();
        threadId = in.readString();
        toolCallId = in.readString();
        toolCallName = in.readString();
        displayName = in.readString();
        executionText = in.readString();
        arguments = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(runId);
        dest.writeString(threadId);
        dest.writeString(toolCallId);
        dest.writeString(toolCallName);
        dest.writeString(displayName);
        dest.writeString(executionText);
        dest.writeString(arguments);
    }

    public static final Creator<AIAssistantToolEndedEvent> CREATOR = new Creator<AIAssistantToolEndedEvent>() {
        @Override
        public AIAssistantToolEndedEvent createFromParcel(Parcel in) {
            return new AIAssistantToolEndedEvent(in);
        }

        @Override
        public AIAssistantToolEndedEvent[] newArray(int size) {
            return new AIAssistantToolEndedEvent[size];
        }
    };

    @Override
    public AIAssistantToolEndedEvent clone() {
        AIAssistantToolEndedEvent clone = (AIAssistantToolEndedEvent) super.clone();
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        clone.toolCallId = this.toolCallId;
        clone.toolCallName = this.toolCallName;
        clone.displayName = this.displayName;
        clone.executionText = this.executionText;
        clone.arguments = this.arguments;
        return clone;
    }

}