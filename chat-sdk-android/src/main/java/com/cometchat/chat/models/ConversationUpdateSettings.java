package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConversationUpdateSettings implements Parcelable, Cloneable {
    private static final String TAG = "ConversationUpdateSettings";

    private boolean callActivities = true;
    private boolean groupActions = true;
    private boolean customMessages = true;
    private boolean messageReplies = true;

    public ConversationUpdateSettings() {}

    protected ConversationUpdateSettings(Parcel in) {
        callActivities = in.readByte() != 0;
        groupActions = in.readByte() != 0;
        customMessages = in.readByte() != 0;
        messageReplies = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (callActivities ? 1 : 0));
        dest.writeByte((byte) (groupActions ? 1 : 0));
        dest.writeByte((byte) (customMessages ? 1 : 0));
        dest.writeByte((byte) (messageReplies ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ConversationUpdateSettings> CREATOR = new Creator<ConversationUpdateSettings>() {
        @Override
        public ConversationUpdateSettings createFromParcel(Parcel in) {
            return new ConversationUpdateSettings(in);
        }

        @Override
        public ConversationUpdateSettings[] newArray(int size) {
            return new ConversationUpdateSettings[size];
        }
    };

    @Override
    public ConversationUpdateSettings clone() {
        try {
            return (ConversationUpdateSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public boolean shouldUpdateOnCallActivities() {
        return callActivities;
    }

    private void setCallActivities(boolean callActivities) {
        this.callActivities = callActivities;
    }

    public boolean shouldUpdateOnGroupActions() {
        return groupActions;
    }

    private void setGroupActions(boolean groupActions) {
        this.groupActions = groupActions;
    }

    public boolean shouldUpdateOnCustomMessages() {
        return customMessages;
    }

    private void setCustomMessages(boolean customMessages) {
        this.customMessages = customMessages;
    }

    public boolean shouldUpdateOnMessageReplies() {
        return messageReplies;
    }

    private void setMessageReplies(boolean messageReplies) {
        this.messageReplies = messageReplies;
    }

    public static ConversationUpdateSettings fromJson(String response) {
        ConversationUpdateSettings conversationUpdateSettings = new ConversationUpdateSettings();
        try {
            JSONObject mainObject = new JSONObject(response);
            if (mainObject.has(CometChatConstants.SettingsKeys.PARAMETERS)) {
                JSONObject data = mainObject.getJSONObject(CometChatConstants.SettingsKeys.PARAMETERS);
                if (data.has(CometChatConstants.SettingsKeys.CORE_CONVERSATIONS_UPDATE_ON_CALL_ACTIVITY)) {
                    conversationUpdateSettings.setCallActivities(data.getBoolean(CometChatConstants.SettingsKeys.CORE_CONVERSATIONS_UPDATE_ON_CALL_ACTIVITY));
                }
                if (data.has(CometChatConstants.SettingsKeys.CORE_CONVERSATIONS_UPDATE_ON_GROUP_ACTIONS)) {
                    conversationUpdateSettings.setGroupActions(data.getBoolean(CometChatConstants.SettingsKeys.CORE_CONVERSATIONS_UPDATE_ON_GROUP_ACTIONS));
                }
                if (data.has(CometChatConstants.SettingsKeys.CORE_CONVERSATIONS_UPDATE_ON_CUSTOM_MESSAGE)) {
                    conversationUpdateSettings.setCustomMessages(data.getBoolean(CometChatConstants.SettingsKeys.CORE_CONVERSATIONS_UPDATE_ON_CUSTOM_MESSAGE));
                }
                if (data.has(CometChatConstants.SettingsKeys.CORE_CONVERSATIONS_UPDATE_ON_REPLIES)) {
                    conversationUpdateSettings.setMessageReplies(data.getBoolean(CometChatConstants.SettingsKeys.CORE_CONVERSATIONS_UPDATE_ON_REPLIES));
                }
            }
        } catch (Exception e){
            Logger.error(TAG, e.toString());
        }
        return conversationUpdateSettings;
    }

    public String toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(CometChatConstants.ConversationUpdateSettingsKeys.CALL_ACTIVITIES, this.callActivities);
            jsonObj.put(CometChatConstants.ConversationUpdateSettingsKeys.GROUP_ACTIONS, this.groupActions);
            jsonObj.put(CometChatConstants.ConversationUpdateSettingsKeys.CUSTOM_MESSAGES, this.customMessages);
            jsonObj.put(CometChatConstants.ConversationUpdateSettingsKeys.MESSAGE_REPLIES, this.messageReplies);
        } catch (Exception e){
            Logger.error(TAG, e.toString());
        }
        return jsonObj.toString();
    }

    public Map<String, Boolean> toMap() {
        Map<String, Boolean> map = new HashMap<>();
        map.put(CometChatConstants.ConversationUpdateSettingsKeys.CALL_ACTIVITIES, this.callActivities);
        map.put(CometChatConstants.ConversationUpdateSettingsKeys.GROUP_ACTIONS, this.groupActions);
        map.put(CometChatConstants.ConversationUpdateSettingsKeys.CUSTOM_MESSAGES, this.customMessages);
        map.put(CometChatConstants.ConversationUpdateSettingsKeys.MESSAGE_REPLIES, this.messageReplies);
        return map;
    }

    public static ConversationUpdateSettings fromMap(Map<String, Boolean> map) {
        ConversationUpdateSettings conversationUpdateSettings = new ConversationUpdateSettings();
        conversationUpdateSettings.setCallActivities(map.get(CometChatConstants.ConversationUpdateSettingsKeys.CALL_ACTIVITIES));
        conversationUpdateSettings.setGroupActions(map.get(CometChatConstants.ConversationUpdateSettingsKeys.GROUP_ACTIONS));
        conversationUpdateSettings.setCustomMessages(map.get(CometChatConstants.ConversationUpdateSettingsKeys.CUSTOM_MESSAGES));
        conversationUpdateSettings.setMessageReplies(map.get(CometChatConstants.ConversationUpdateSettingsKeys.MESSAGE_REPLIES));
        return conversationUpdateSettings;
    }

    @Override
    public String toString() {
        return "ConversationUpdateSettings{" +
                "callActivities=" + callActivities +
                ", groupActions=" + groupActions +
                ", customMessages=" + customMessages +
                ", messageReplies=" + messageReplies +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

    /**
     * Compares this ConversationUpdateSettings object with another for content equality.
     * <p>
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality. Two ConversationUpdateSettings objects are considered
     * content-equal if all their fields have the same values.
     * </p>
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        // Same reference check
        if (this == other) return true;

        // Null check
        if (other == null) return false;

        // Type check
        if (!(other instanceof ConversationUpdateSettings)) return false;

        // Cast
        ConversationUpdateSettings that = (ConversationUpdateSettings) other;

        // Compare all primitive boolean fields using == operator
        return callActivities == that.callActivities &&
                groupActions == that.groupActions &&
                customMessages == that.customMessages &&
                messageReplies == that.messageReplies;
    }
}
