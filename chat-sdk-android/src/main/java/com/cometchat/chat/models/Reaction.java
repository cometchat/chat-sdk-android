package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Rohit Giri on 08/03/24.
 */
public class Reaction implements Parcelable, Cloneable {
    private String id;
    private long messageId;
    private String reaction;
    private String uid;
    private long reactedAt;
    private User reactedBy;

    public Reaction() {}

    protected Reaction(Parcel in) {
        id = in.readString();
        messageId = in.readLong();
        reaction = in.readString();
        uid = in.readString();
        reactedAt = in.readLong();
        reactedBy = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(messageId);
        dest.writeString(reaction);
        dest.writeString(uid);
        dest.writeLong(reactedAt);
        dest.writeParcelable(reactedBy, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Reaction> CREATOR = new Creator<Reaction>() {
        @Override
        public Reaction createFromParcel(Parcel in) {
            return new Reaction(in);
        }

        @Override
        public Reaction[] newArray(int size) {
            return new Reaction[size];
        }
    };

    @Override
    public Reaction clone() {
        try {
            Reaction cloned = (Reaction) super.clone();
            if (this.reactedBy != null) {
                cloned.reactedBy = this.reactedBy.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Get reaction id
     *
     * @return Reaction id
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public String getReactionId() {
        return id;
    }

    public void setReactionId(String id) {
        this.id = id;
    }

    /**
     * Get message id
     *
     * @return Message id
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    /**
     * Get reaction
     *
     * @return Reaction
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    /**
     * Get user UID
     *
     * @return User UID
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Get reacted timestamp
     *
     * @return Reacted timestamp
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public long getReactedAt() {
        return reactedAt;
    }

    public void setReactedAt(long reactedAt) {
        this.reactedAt = reactedAt;
    }

    /**
     * Get reacted by user object
     *
     * @return User object
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public User getReactedBy() {
        return reactedBy;
    }

    public void setReactedBy(User reactedBy) {
        this.reactedBy = reactedBy;
    }

    public static List<Reaction> listFromJSONArray(JSONObject mainObject) {
        List<Reaction> members = new ArrayList<>();
        try {
            if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
                for (int i = 0; i < dataArray.length(); i++) {
                    Reaction Reaction = new Reaction();
                    JSONObject jsonObject = dataArray.getJSONObject(i);
                    if (jsonObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_ID)) {
                        Reaction.setReactionId(jsonObject.getString(CometChatConstants.ReactionsKeys.KEY_REACTIONS_ID));
                    }
                    if (jsonObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_MESSAGE_ID)) {
                        Reaction.setMessageId(jsonObject.getLong(CometChatConstants.ReactionsKeys.KEY_REACTIONS_MESSAGE_ID));
                    }
                    if (jsonObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION)) {
                        Reaction.setReaction(jsonObject.getString(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION));
                    }
                    if (jsonObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_UID)) {
                        Reaction.setUid(jsonObject.getString(CometChatConstants.ReactionsKeys.KEY_REACTIONS_UID));
                    }
                    if (jsonObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_AT)) {
                        Reaction.setReactedAt(jsonObject.getLong(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_AT));
                    }
                    if (jsonObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY)) {
                        User user = User.fromJson(jsonObject.getJSONObject(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY).toString());
                        Reaction.setReactedBy(user);
                    }
                    members.add(Reaction);
                }
            }
        } catch (Exception e) {
            Logger.error("Error: " + e);
        }
        return members;
    }

    public static Reaction fromJson(JSONObject mainObject) {
        Reaction Reaction = new Reaction();
        try {
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_ID)) {
                Reaction.setReactionId(mainObject.getString(CometChatConstants.ReactionsKeys.KEY_REACTIONS_ID));
            }
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_MESSAGE_ID)) {
                Reaction.setMessageId(mainObject.getLong(CometChatConstants.ReactionsKeys.KEY_REACTIONS_MESSAGE_ID));
            }
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION)) {
                Reaction.setReaction(mainObject.getString(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION));
            }
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_UID)) {
                Reaction.setUid(mainObject.getString(CometChatConstants.ReactionsKeys.KEY_REACTIONS_UID));
            }
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_AT)) {
                Reaction.setReactedAt(mainObject.getLong(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_AT));
            }
            if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY)) {
                User user = User.fromJson(mainObject.getJSONObject(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY).toString());
                Reaction.setReactedBy(user);
            }
        } catch (Exception e) {
            Logger.error("Error: " + e);
        }
        return Reaction;
    }

    @Override
    public String toString() {
        return "Reaction{" +
                "id='" + id + '\'' +
                ", messageId=" + messageId +
                ", reaction='" + reaction + '\'' +
                ", uid='" + uid + '\'' +
                ", reactedAt=" + reactedAt +
                ", reactedBy=" + reactedBy +
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
        if (!(other instanceof Reaction)) return false;

        // 4. Cast
        Reaction that = (Reaction) other;

        // 5. Compare all fields
        return ContentEqualsHelper.stringsEqual(this.id, that.id) &&
                this.messageId == that.messageId &&
                ContentEqualsHelper.stringsEqual(this.reaction, that.reaction) &&
                ContentEqualsHelper.stringsEqual(this.uid, that.uid) &&
                this.reactedAt == that.reactedAt &&
                ContentEqualsHelper.objectsContentEqual(this.reactedBy, that.reactedBy);
    }
}
