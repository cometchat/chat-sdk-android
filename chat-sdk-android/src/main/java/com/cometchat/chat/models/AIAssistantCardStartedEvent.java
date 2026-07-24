package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONObject;

/**
 * Represents an AI Assistant event that is triggered when a card is starting to be built.
 * This event is the first of three streaming events for agent card delivery:
 * card_start → card → card_end.
 * <p>
 * It signals the UI to show a loading state while the card is being constructed.
 *
 * @author CometChat Team
 * @version 5.0
 */
public class AIAssistantCardStartedEvent extends AIAssistantBaseEvent {
    private long runId;
    private String threadId;
    private String streamMessageId;
    private String cardId;
    private String executionText;

    /**
     * Default constructor that initializes the parent AIAssistantBaseEvent.
     */
    public AIAssistantCardStartedEvent() {
        super();
    }

    /**
     * Populates the event properties from a JSON object.
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
                this.cardId = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_CARD_ID, "");
                this.executionText = dataObject.optString(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_EXECUTION_TEXT, "");
            }
        }
    }

    /**
     * Converts the event to a JSON object.
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
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_CARD_ID, cardId);
            dataObject.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_EVENT_EXECUTION_TEXT, executionText);
            json.put(CometChatConstants.WSKeys.KEY_AI_ASSISTANT_BASE_EVENT_DATA, dataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getStreamMessageId() {
        return streamMessageId;
    }

    public void setStreamMessageId(String streamMessageId) {
        this.streamMessageId = streamMessageId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getExecutionText() {
        return executionText;
    }

    public void setExecutionText(String executionText) {
        this.executionText = executionText;
    }

    @Override
    public String toString() {
        return "AIAssistantCardStartedEvent{" +
                "id=" + getId() +
                ", type='" + getType() + '\'' +
                ", conversationId='" + getConversationId() + '\'' +
                ", parentId='" + getParentId() + '\'' +
                ", runId=" + runId +
                ", threadId='" + threadId + '\'' +
                ", streamMessageId='" + streamMessageId + '\'' +
                ", cardId='" + cardId + '\'' +
                ", executionText='" + executionText + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    @Override
    public boolean contentEquals(Object other) {
        if (!super.contentEquals(other)) return false;
        if (!(other instanceof AIAssistantCardStartedEvent)) return false;

        AIAssistantCardStartedEvent that = (AIAssistantCardStartedEvent) other;

        return runId == that.runId &&
                ContentEqualsHelper.stringsEqual(threadId, that.threadId) &&
                ContentEqualsHelper.stringsEqual(streamMessageId, that.streamMessageId) &&
                ContentEqualsHelper.stringsEqual(cardId, that.cardId) &&
                ContentEqualsHelper.stringsEqual(executionText, that.executionText);
    }

    // Parcelable implementation
    protected AIAssistantCardStartedEvent(Parcel in) {
        super(in);
        runId = in.readLong();
        threadId = in.readString();
        streamMessageId = in.readString();
        cardId = in.readString();
        executionText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(runId);
        dest.writeString(threadId);
        dest.writeString(streamMessageId);
        dest.writeString(cardId);
        dest.writeString(executionText);
    }

    public static final Creator<AIAssistantCardStartedEvent> CREATOR = new Creator<AIAssistantCardStartedEvent>() {
        @Override
        public AIAssistantCardStartedEvent createFromParcel(Parcel in) {
            return new AIAssistantCardStartedEvent(in);
        }

        @Override
        public AIAssistantCardStartedEvent[] newArray(int size) {
            return new AIAssistantCardStartedEvent[size];
        }
    };

    @Override
    public AIAssistantCardStartedEvent clone() {
        AIAssistantCardStartedEvent clone = (AIAssistantCardStartedEvent) super.clone();
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        clone.streamMessageId = this.streamMessageId;
        clone.cardId = this.cardId;
        clone.executionText = this.executionText;
        return clone;
    }
}
