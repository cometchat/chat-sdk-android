package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.QuotedRepliesOptions;
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
    private QuotedRepliesOptions oneOnOneQuotedReplies;
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

    /**
     * Returns the quoted-replies notification preference, or {@code null} when it has never been
     * configured. Unset is distinct from {@link QuotedRepliesOptions#DONT_SUBSCRIBE}: unset means the
     * server default applies.
     */
    public QuotedRepliesOptions getQuotedRepliesPreference() {
        return oneOnOneQuotedReplies;
    }

    /**
     * Sets the quoted-replies notification preference. Passing {@code null} leaves the preference
     * unset, and an unset preference is omitted from an update request rather than sent as a default.
     */
    public void setQuotedRepliesPreference(QuotedRepliesOptions oneOnOneQuotedReplies) {
        this.oneOnOneQuotedReplies = oneOnOneQuotedReplies;
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
            if (oneOnOneQuotedReplies != null) {
                jsonObject.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES, oneOnOneQuotedReplies.getValue());
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
            if (jsonObject.has(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES)){
                oneOnOnePreferences.setQuotedRepliesPreference(QuotedRepliesOptions.get(jsonObject.optInt(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES)));
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
        if (oneOnOneQuotedReplies != null) {
            map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES, oneOnOneQuotedReplies.getValue());
        }
        if (oneOnOneReactions != null) {
            map.put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS, oneOnOneReactions.getValue());
        }
        return map;
    }

    /**
     * Builds a bucket from a wire map. A key that is absent, or present with a {@code null} value,
     * leaves that preference unset. A null map yields an all-unset bucket.
     */
    public static OneOnOnePreferences fromMap(Map<String, Integer> map) {
        OneOnOnePreferences oneOnOnePreferences = new OneOnOnePreferences();
        Integer messages = PreferenceMaps.opt(map, CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES);
        if (messages != null) {
            oneOnOnePreferences.setMessagesPreference(MessagesOptions.get(messages));
        }
        Integer reactions = PreferenceMaps.opt(map, CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REACTIONS);
        if (reactions != null) {
            oneOnOnePreferences.setReactionsPreference(ReactionsOptions.get(reactions));
        }
        Integer replies = PreferenceMaps.opt(map, CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_REPLIES);
        if (replies != null) {
            oneOnOnePreferences.setRepliesPreference(RepliesOptions.get(replies));
        }
        Integer quotedReplies = PreferenceMaps.opt(map, CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES);
        if (quotedReplies != null) {
            oneOnOnePreferences.setQuotedRepliesPreference(QuotedRepliesOptions.get(quotedReplies));
        }
        return oneOnOnePreferences;
    }

    @Override
    public String toString() {
        return "OneOnOnePreferences{" +
                "oneOnOneMessages=" + oneOnOneMessages +
                ", oneOnOneReplies=" + oneOnOneReplies +
                ", oneOnOneQuotedReplies=" + oneOnOneQuotedReplies +
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
                && oneOnOneQuotedReplies == that.oneOnOneQuotedReplies
                && oneOnOneReactions == that.oneOnOneReactions;
    }

    // Parcelable implementation
    protected OneOnOnePreferences(Parcel in) {
        oneOnOneMessages = safeValueOf(MessagesOptions.class, in.readString());
        oneOnOneReplies = safeValueOf(RepliesOptions.class, in.readString());
        oneOnOneQuotedReplies = safeValueOf(QuotedRepliesOptions.class, in.readString());
        oneOnOneReactions = safeValueOf(ReactionsOptions.class, in.readString());
    }

    /**
     * Resolves an enum constant by name without throwing.
     *
     * <p>The parcel stores each preference as its enum {@link Enum#name()} and restores it here.
     * A plain {@link Enum#valueOf(Class, String)} throws {@link IllegalArgumentException} when the
     * stored name is not a member of the enum — which happens the moment the backend introduces a
     * new preference value (e.g. {@code RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS}) that an
     * older build cannot resolve. An absent/unknown value must resolve to {@code null}
     * ("not yet configured"), never crash. Kept package-private so it can be unit tested directly.
     *
     * @return the matching constant, or {@code null} when {@code name} is null or unrecognized
     */
    static <T extends Enum<T>> T safeValueOf(Class<T> type, String name) {
        if (name == null) {
            return null;
        }
        try {
            return Enum.valueOf(type, name);
        } catch (IllegalArgumentException e) {
            Logger.error(e.toString());
            return null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(oneOnOneMessages != null ? oneOnOneMessages.name() : null);
        dest.writeString(oneOnOneReplies != null ? oneOnOneReplies.name() : null);
        dest.writeString(oneOnOneQuotedReplies != null ? oneOnOneQuotedReplies.name() : null);
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
            clone.oneOnOneQuotedReplies = this.oneOnOneQuotedReplies;
            clone.oneOnOneReactions = this.oneOnOneReactions;
            return clone;
        }
    }
}
