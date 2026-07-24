package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.ReactionsOptions;
import com.cometchat.chat.enums.RepliesOptions;
import com.cometchat.chat.helpers.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Rohit Giri on 09/02/24.
 */
public class OneOnOnePreferences implements Parcelable, Cloneable {
    private MessagesOptions oneOnOneMessages;
    private RepliesOptions oneOnOneReplies;
    private ReactionsOptions oneOnOneReactions;

    public OneOnOnePreferences() {}

    public MessagesOptions getMessagesPreference() {
        return oneOnOneMessages;
    }

    public void setMessagesPreference(MessagesOptions oneOnOneMessages) {
        this.oneOnOneMessages = oneOnOneMessages;
    }

    public RepliesOptions getRepliesPreference() {
        return oneOnOneReplies;
    }

    public void setRepliesPreference(RepliesOptions oneOnOneReplies) {
        this.oneOnOneReplies = oneOnOneReplies;
    }

    public ReactionsOptions getReactionsPreference() {
        return oneOnOneReactions;
    }

    public void setReactionsPreference(ReactionsOptions oneOnOneReactions) {
        this.oneOnOneReactions = oneOnOneReactions;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (oneOnOneMessages != null) {
                jsonObject.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES, oneOnOneMessages.getValue());
            }
            if (oneOnOneReplies != null) {
                jsonObject.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES, oneOnOneReplies.getValue());
            }
            if (oneOnOneReactions != null) {
                jsonObject.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS, oneOnOneReactions.getValue());
            }
        } catch (JSONException e) {
            Logger.error(e.toString());
        }
        return jsonObject;
    }

    public static OneOnOnePreferences fromJson(JSONObject jsonObject) {
        OneOnOnePreferences oneOnOnePreferences = new OneOnOnePreferences();
        try{
            if (jsonObject.has(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES)){
                oneOnOnePreferences.setMessagesPreference(MessagesOptions.get(jsonObject.optInt(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES)){
                oneOnOnePreferences.setRepliesPreference(RepliesOptions.get(jsonObject.optInt(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS)){
                oneOnOnePreferences.setReactionsPreference(ReactionsOptions.get(jsonObject.optInt(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS)));
            }
        }catch (Exception e){
            Logger.error(e.toString());
        }
        return oneOnOnePreferences;
    }

    public Map<String, Integer> toMap() {
        Map<String, Integer> map = new HashMap<>();
        if (oneOnOneMessages != null) {
            map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES, oneOnOneMessages.getValue());
        }
        if (oneOnOneReplies != null) {
            map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES, oneOnOneReplies.getValue());
        }
        if (oneOnOneReactions != null) {
            map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS, oneOnOneReactions.getValue());
        }
        return map;
    }

    public static OneOnOnePreferences fromMap(Map<String, Integer> map) {
        OneOnOnePreferences oneOnOnePreferences = new OneOnOnePreferences();
        if (map.containsKey(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES)) {
            oneOnOnePreferences.setMessagesPreference(MessagesOptions.get(map.get(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES)));
        }
        if (map.containsKey(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS)) {
            oneOnOnePreferences.setReactionsPreference(ReactionsOptions.get(map.get(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS)));
        }
        if (map.containsKey(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES)) {
            oneOnOnePreferences.setRepliesPreference(RepliesOptions.get(map.get(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES)));
        }
        return oneOnOnePreferences;
    }

    @Override
    public String toString() {
        return "OneOnOnePreferences{" +
                "oneOnOneMessages=" + oneOnOneMessages +
                ", oneOnOneReplies=" + oneOnOneReplies +
                ", oneOnOneReactions=" + oneOnOneReactions +
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
        if (!(other instanceof OneOnOnePreferences)) return false;

        // 4. Cast
        OneOnOnePreferences that = (OneOnOnePreferences) other;

        // 5. Compare all enum fields (use == for enums, handles null safely)
        return oneOnOneMessages == that.oneOnOneMessages
                && oneOnOneReplies == that.oneOnOneReplies
                && oneOnOneReactions == that.oneOnOneReactions;
    }

    // Parcelable implementation
    protected OneOnOnePreferences(Parcel in) {
        String messagesStr = in.readString();
        oneOnOneMessages = messagesStr != null ? MessagesOptions.valueOf(messagesStr) : null;
        String repliesStr = in.readString();
        oneOnOneReplies = repliesStr != null ? RepliesOptions.valueOf(repliesStr) : null;
        String reactionsStr = in.readString();
        oneOnOneReactions = reactionsStr != null ? ReactionsOptions.valueOf(reactionsStr) : null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(oneOnOneMessages != null ? oneOnOneMessages.name() : null);
        dest.writeString(oneOnOneReplies != null ? oneOnOneReplies.name() : null);
        dest.writeString(oneOnOneReactions != null ? oneOnOneReactions.name() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OneOnOnePreferences> CREATOR = new Creator<OneOnOnePreferences>() {
        @Override
        public OneOnOnePreferences createFromParcel(Parcel in) {
            return new OneOnOnePreferences(in);
        }

        @Override
        public OneOnOnePreferences[] newArray(int size) {
            return new OneOnOnePreferences[size];
        }
    };

    @Override
    public OneOnOnePreferences clone() {
        try {
            return (OneOnOnePreferences) super.clone();
        } catch (CloneNotSupportedException e) {
            OneOnOnePreferences clone = new OneOnOnePreferences();
            clone.oneOnOneMessages = this.oneOnOneMessages;
            clone.oneOnOneReplies = this.oneOnOneReplies;
            clone.oneOnOneReactions = this.oneOnOneReactions;
            return clone;
        }
    }
}
