package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Rohit Giri on 14/11/23.
 */
public class ReactionEvent implements Parcelable, Cloneable {
    private Reaction reaction;
    private String receiverId;
    private String receiverType;
    private String conversationId;
    private long parentMessageId = 0;

    public ReactionEvent() {}

    protected ReactionEvent(Parcel in) {
        reaction = in.readParcelable(Reaction.class.getClassLoader());
        receiverId = in.readString();
        receiverType = in.readString();
        conversationId = in.readString();
        parentMessageId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(reaction, flags);
        dest.writeString(receiverId);
        dest.writeString(receiverType);
        dest.writeString(conversationId);
        dest.writeLong(parentMessageId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReactionEvent> CREATOR = new Creator<ReactionEvent>() {
        @Override
        public ReactionEvent createFromParcel(Parcel in) {
            return new ReactionEvent(in);
        }

        @Override
        public ReactionEvent[] newArray(int size) {
            return new ReactionEvent[size];
        }
    };

    @Override
    public ReactionEvent clone() {
        try {
            ReactionEvent cloned = (ReactionEvent) super.clone();
            if (this.reaction != null) {
                cloned.reaction = this.reaction.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Reaction getReaction() {
        return reaction;
    }

    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public long getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(long parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public static List<ReactionEvent> listFromJSONArray(JSONObject mainObject) {
        List<ReactionEvent> members = new ArrayList<>();
        try {
            if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject jsonObject = dataArray.getJSONObject(i);
                    members.add(fromJson(jsonObject));
                }
            }
        } catch (Exception e) {
            Logger.error("Error: " + e);
        }
        return members;
    }

    public static ReactionEvent fromJson(JSONObject mainObject) {
        ReactionEvent reactionEvent = new ReactionEvent();
        try {
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_RECEIVER_ID)) {
                reactionEvent.setReceiverId(mainObject.getString(CometChatConstants.ReactionsKeys.KEY_RECEIVER_ID));
            }
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_RECEIVER_TYPE)) {
                reactionEvent.setReceiverType(mainObject.getString(CometChatConstants.ReactionsKeys.KEY_RECEIVER_TYPE));
            }
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_CONVERSATION_ID)) {
                reactionEvent.setConversationId(mainObject.getString(CometChatConstants.ReactionsKeys.KEY_CONVERSATION_ID));
            }
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_PARENT_ID)) {
                reactionEvent.setParentMessageId(mainObject.getLong(CometChatConstants.ReactionsKeys.KEY_PARENT_ID));
            }
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_REACTION)) {
                reactionEvent.setReaction(Reaction.fromJson(mainObject.getJSONObject(CometChatConstants.ReactionsKeys.KEY_REACTION)));
            }
        } catch (Exception e) {
            Logger.error("Error: " + e);
        }
        return reactionEvent;
    }

    @Override
    public String toString() {
        return "ReactionEvent{" +
                "reaction=" + reaction +
                ", receiverId='" + receiverId + '\'' +
                ", receiverType='" + receiverType + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", parentMessageId='" + parentMessageId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    /**
     * Compares this object with another for content equality.
     * Unlike equals() which compares by identity (ID), this method
     * compares all fields for value equality.
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        // 1. Same reference check
        if (this == other) return true;

        // 2. Null check
        if (other == null) return false;

        // 3. Type check
        if (!(other instanceof ReactionEvent)) return false;

        // 4. Cast
        ReactionEvent that = (ReactionEvent) other;

        // 5. Compare all fields
        return ContentEqualsHelper.objectsContentEqual(this.reaction, that.reaction) &&
                ContentEqualsHelper.stringsEqual(this.receiverId, that.receiverId) &&
                ContentEqualsHelper.stringsEqual(this.receiverType, that.receiverType) &&
                ContentEqualsHelper.stringsEqual(this.conversationId, that.conversationId) &&
                this.parentMessageId == that.parentMessageId;
    }
}
