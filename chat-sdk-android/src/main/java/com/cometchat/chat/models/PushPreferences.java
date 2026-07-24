package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.helpers.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Rohit Giri on 12/02/24.
 * @deprecated This class is deprecated as of version 4.0. Use {@link NotificationPreferences} instead.
 */
@Deprecated
public class PushPreferences implements Parcelable, Cloneable {
    private boolean usePrivacyTemplate = false;
    private OneOnOnePreferences oneOnOnePreferences;
    private MutePreferences mutePreferences;
    private GroupPreferences groupPreferences;

    public PushPreferences(){}

    public boolean getUsePrivacyTemplate() {
        return usePrivacyTemplate;
    }

    public void setUsePrivacyTemplate(boolean usePrivacyTemplate) {
        this.usePrivacyTemplate = usePrivacyTemplate;
    }

    public OneOnOnePreferences getOneOnOnePreferences() {
        return oneOnOnePreferences;
    }

    public void setOneOnOnePreferences(OneOnOnePreferences oneOnOnePreferences) {
        this.oneOnOnePreferences = oneOnOnePreferences;
    }

    public MutePreferences getMutePreferences() {
        return mutePreferences;
    }

    public void setMutePreferences(MutePreferences mutePreferences) {
        this.mutePreferences = mutePreferences;
    }

    public GroupPreferences getGroupPreferences() {
        return groupPreferences;
    }

    public void setGroupPreferences(GroupPreferences groupPreferences) {
        this.groupPreferences = groupPreferences;
    }

    public static PushPreferences fromJson(JSONObject jsonObject) {
        PushPreferences pushPreferences = new PushPreferences();
        try {
            if (jsonObject.has(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE)){
                pushPreferences.setUsePrivacyTemplate(jsonObject.getBoolean(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE));
            }
            if (jsonObject.has(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES)){
                pushPreferences.setOneOnOnePreferences(OneOnOnePreferences.fromJson(jsonObject.getJSONObject(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES)){
                pushPreferences.setMutePreferences(MutePreferences.fromJson(jsonObject.getJSONObject(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES)){
                pushPreferences.setGroupPreferences(GroupPreferences.fromJson(jsonObject.getJSONObject(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES)));
            }
        } catch (Exception e){
            Logger.error(e.toString());
        }
        return pushPreferences;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if(usePrivacyTemplate){
                jsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE, true);
            }
            if(oneOnOnePreferences != null){
                jsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES, oneOnOnePreferences.toJson());
            }
            if(mutePreferences != null){
                jsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES, mutePreferences.toJson());
            }
            if(groupPreferences != null){
                jsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES, groupPreferences.toJson());
            }
        } catch (JSONException e) {
            Logger.error(e.toString());
        }
        return jsonObject;
    }

    public static PushPreferences fromMap(Map<String, Object> map) {
        PushPreferences pushPreferences = new PushPreferences();
        if (map.containsKey(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE)) {
            pushPreferences.setUsePrivacyTemplate((boolean) map.get(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE));
        }
        if (map.containsKey(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES)) {
            pushPreferences.setOneOnOnePreferences(OneOnOnePreferences.fromMap((Map<String, Integer>) map.get(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES)));
        }
        if (map.containsKey(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES)) {
            pushPreferences.setMutePreferences(MutePreferences.fromMap((Map<String, Object>) map.get(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES)));
        }
        if (map.containsKey(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES)) {
            pushPreferences.setGroupPreferences(GroupPreferences.fromMap((Map<String, Integer>) map.get(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES)));
        }
        return pushPreferences;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (usePrivacyTemplate){
            map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE, true);
        }
        if (oneOnOnePreferences != null){
            map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES, oneOnOnePreferences.toMap());
        }
        if (mutePreferences != null){
            map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES, mutePreferences.toMap());
        }
        if (groupPreferences != null){
            map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES, groupPreferences.toMap());
        }
        return map;
    }

    @Override
    public String toString() {
        return "PushPreferences{" +
                "usePrivacyTemplate=" + usePrivacyTemplate +
                ", oneOnOnePreferences=" + oneOnOnePreferences +
                ", mutePreferences=" + mutePreferences +
                ", groupPreferences=" + groupPreferences +
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
        if (!(other instanceof PushPreferences)) return false;

        // 4. Cast
        PushPreferences that = (PushPreferences) other;

        // 5. Compare all fields
        // Compare primitive field using ==
        if (usePrivacyTemplate != that.usePrivacyTemplate) return false;

        // Compare nested model objects using ContentEqualsHelper.objectsContentEqual
        if (!com.cometchat.chat.utils.ContentEqualsHelper.objectsContentEqual(oneOnOnePreferences, that.oneOnOnePreferences)) return false;
        if (!com.cometchat.chat.utils.ContentEqualsHelper.objectsContentEqual(mutePreferences, that.mutePreferences)) return false;
        if (!com.cometchat.chat.utils.ContentEqualsHelper.objectsContentEqual(groupPreferences, that.groupPreferences)) return false;

        return true;
    }

    // Parcelable implementation
    protected PushPreferences(Parcel in) {
        usePrivacyTemplate = in.readByte() != 0;
        oneOnOnePreferences = in.readParcelable(OneOnOnePreferences.class.getClassLoader());
        mutePreferences = in.readParcelable(MutePreferences.class.getClassLoader());
        groupPreferences = in.readParcelable(GroupPreferences.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (usePrivacyTemplate ? 1 : 0));
        dest.writeParcelable(oneOnOnePreferences, flags);
        dest.writeParcelable(mutePreferences, flags);
        dest.writeParcelable(groupPreferences, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PushPreferences> CREATOR = new Creator<PushPreferences>() {
        @Override
        public PushPreferences createFromParcel(Parcel in) {
            return new PushPreferences(in);
        }

        @Override
        public PushPreferences[] newArray(int size) {
            return new PushPreferences[size];
        }
    };

    @Override
    public PushPreferences clone() {
        try {
            PushPreferences clone = (PushPreferences) super.clone();
            clone.oneOnOnePreferences = this.oneOnOnePreferences != null ? this.oneOnOnePreferences.clone() : null;
            clone.mutePreferences = this.mutePreferences != null ? this.mutePreferences.clone() : null;
            clone.groupPreferences = this.groupPreferences != null ? this.groupPreferences.clone() : null;
            return clone;
        } catch (CloneNotSupportedException e) {
            PushPreferences clone = new PushPreferences();
            clone.usePrivacyTemplate = this.usePrivacyTemplate;
            clone.oneOnOnePreferences = this.oneOnOnePreferences != null ? this.oneOnOnePreferences.clone() : null;
            clone.mutePreferences = this.mutePreferences != null ? this.mutePreferences.clone() : null;
            clone.groupPreferences = this.groupPreferences != null ? this.groupPreferences.clone() : null;
            return clone;
        }
    }
}
