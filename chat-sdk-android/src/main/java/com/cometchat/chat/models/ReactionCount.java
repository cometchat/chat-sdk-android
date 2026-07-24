package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by Rohit Giri on 14/11/23.
 */
public class ReactionCount implements Parcelable, Cloneable {
    private String reaction;
    private boolean reactedByMe = false;
    private int count;

    public ReactionCount() {}

    protected ReactionCount(Parcel in) {
        reaction = in.readString();
        reactedByMe = in.readByte() != 0;
        count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reaction);
        dest.writeByte((byte) (reactedByMe ? 1 : 0));
        dest.writeInt(count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReactionCount> CREATOR = new Creator<ReactionCount>() {
        @Override
        public ReactionCount createFromParcel(Parcel in) {
            return new ReactionCount(in);
        }

        @Override
        public ReactionCount[] newArray(int size) {
            return new ReactionCount[size];
        }
    };

    @Override
    public ReactionCount clone() {
        try {
            return (ReactionCount) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Get reaction
     *
     * @return reaction
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
     * Get reacted users count
     *
     * @return Reacted users count
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Get boolean value if logged in users reacted on message
     *
     * @return Logged in users reacted on message
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public boolean getReactedByMe() {
        return reactedByMe;
    }

    public void setReactedByMe(boolean reactedByMe) {
        this.reactedByMe = reactedByMe;
    }

    public static List<ReactionCount> listFromJSONArray(JSONArray reactionsObj) {
        List<ReactionCount> reactionCountList = new ArrayList<>();
        try {
            for (int i = 0; i < reactionsObj.length(); i++) {
                JSONObject obj = reactionsObj.getJSONObject(i);
                String emoji = obj.getString(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTION);
                int count = obj.getInt(CometChatConstants.ReactionsKeys.KEY_REACTIONS_COUNT);
                boolean reactedByMe = false;
                if (obj.has(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY_ME)){
                    reactedByMe = obj.getBoolean(CometChatConstants.ReactionsKeys.KEY_REACTIONS_REACTED_BY_ME);
                }
                ReactionCount reactionCount = new ReactionCount();
                reactionCount.setReaction(emoji);
                reactionCount.setCount(count);
                reactionCount.setReactedByMe(reactedByMe);
                reactionCountList.add(reactionCount);
            }
        } catch (Exception e) {
            Logger.error("Error: " + e);
        }
        return reactionCountList;
    }

    @Override
    public String toString() {
        return "ReactionCount{" +
                "reaction='" + reaction + '\'' +
                ", reactedByMe=" + reactedByMe +
                ", count=" + count +
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
        if (!(other instanceof ReactionCount)) return false;

        // 4. Cast
        ReactionCount that = (ReactionCount) other;

        // 5. Compare all fields
        return ContentEqualsHelper.stringsEqual(this.reaction, that.reaction)
                && this.reactedByMe == that.reactedByMe
                && this.count == that.count;
    }
}
