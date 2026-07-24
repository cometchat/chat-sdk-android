package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an AI Assistant event that is triggered when a card payload is received during streaming.
 * This event is the second of three streaming events for agent card delivery:
 * card_start → card → card_end.
 * <p>
 * It carries the actual card payload that the UI can render immediately.
 *
 * @author CometChat Team
 * @version 5.0
 */
public class AIAssistantCardReceivedEvent extends AIAssistantBaseEvent {
    private long runId;
    private String threadId;
    private String streamMessageId;
    private String cardId;
    private JSONObject card;

    /**
     * Default constructor that initializes the parent AIAssistantBaseEvent.
     */
    public AIAssistantCardReceivedEvent() {
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
                if (dataObject.has(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD)) {
                    this.card = dataObject.optJSONObject(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD);
                }
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
            if (card != null) {
                dataObject.put(CometChatConstants.WSKeys.AI_ASSISTANT_EVENT_CARD, card);
            }
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

    /**
     * Gets the raw card payload as a JSONObject.
     * This is the complete card JSON that an external renderer library uses to draw the card UI.
     *
     * @return The raw card JSONObject, or null if not present
     */
    public JSONObject getCard() {
        return card;
    }

    public void setCard(JSONObject card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return "AIAssistantCardReceivedEvent{" +
                "id=" + getId() +
                ", type='" + getType() + '\'' +
                ", conversationId='" + getConversationId() + '\'' +
                ", parentId='" + getParentId() + '\'' +
                ", runId=" + runId +
                ", threadId='" + threadId + '\'' +
                ", streamMessageId='" + streamMessageId + '\'' +
                ", cardId='" + cardId + '\'' +
                ", card=" + card +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    @Override
    public boolean contentEquals(Object other) {
        if (!super.contentEquals(other)) return false;
        if (!(other instanceof AIAssistantCardReceivedEvent)) return false;

        AIAssistantCardReceivedEvent that = (AIAssistantCardReceivedEvent) other;

        return runId == that.runId &&
                ContentEqualsHelper.stringsEqual(threadId, that.threadId) &&
                ContentEqualsHelper.stringsEqual(streamMessageId, that.streamMessageId) &&
                ContentEqualsHelper.stringsEqual(cardId, that.cardId) &&
                ContentEqualsHelper.jsonObjectsEqual(card, that.card);
    }

    // Parcelable implementation
    protected AIAssistantCardReceivedEvent(Parcel in) {
        super(in);
        runId = in.readLong();
        threadId = in.readString();
        streamMessageId = in.readString();
        cardId = in.readString();
        String cardStr = in.readString();
        if (cardStr != null) {
            try {
                card = new JSONObject(cardStr);
            } catch (JSONException e) {
                card = null;
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(runId);
        dest.writeString(threadId);
        dest.writeString(streamMessageId);
        dest.writeString(cardId);
        dest.writeString(card != null ? card.toString() : null);
    }

    public static final Creator<AIAssistantCardReceivedEvent> CREATOR = new Creator<AIAssistantCardReceivedEvent>() {
        @Override
        public AIAssistantCardReceivedEvent createFromParcel(Parcel in) {
            return new AIAssistantCardReceivedEvent(in);
        }

        @Override
        public AIAssistantCardReceivedEvent[] newArray(int size) {
            return new AIAssistantCardReceivedEvent[size];
        }
    };

    @Override
    public AIAssistantCardReceivedEvent clone() {
        AIAssistantCardReceivedEvent clone = (AIAssistantCardReceivedEvent) super.clone();
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        clone.streamMessageId = this.streamMessageId;
        clone.cardId = this.cardId;
        if (this.card != null) {
            try {
                clone.card = new JSONObject(this.card.toString());
            } catch (JSONException e) {
                clone.card = null;
            }
        }
        return clone;
    }
}
