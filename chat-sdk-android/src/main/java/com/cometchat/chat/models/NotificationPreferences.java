package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Vivek Prajapati on 01/07/24.
 */
public class NotificationPreferences implements Parcelable, Cloneable {

    private boolean usePrivacyTemplate = false;
    private OneOnOnePreferences oneOnOnePreferences;
    private MutePreferences mutePreferences;
    private GroupPreferences groupPreferences;

    public NotificationPreferences() {
    }

    protected NotificationPreferences(Parcel in) {
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

    public static final Creator<NotificationPreferences> CREATOR = new Creator<NotificationPreferences>() {
        @Override
        public NotificationPreferences createFromParcel(Parcel in) {
            return new NotificationPreferences(in);
        }

        @Override
        public NotificationPreferences[] newArray(int size) {
            return new NotificationPreferences[size];
        }
    };

    @Override
    public NotificationPreferences clone() {
        try {
            NotificationPreferences cloned = (NotificationPreferences) super.clone();
            if (this.oneOnOnePreferences != null) cloned.oneOnOnePreferences = this.oneOnOnePreferences.clone();
            if (this.mutePreferences != null) cloned.mutePreferences = this.mutePreferences.clone();
            if (this.groupPreferences != null) cloned.groupPreferences = this.groupPreferences.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

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

    public static NotificationPreferences fromJson(JSONObject jsonObject) {
        NotificationPreferences notificationPreferences = new NotificationPreferences();
        try {
            if (jsonObject.has(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE)) {
                notificationPreferences.setUsePrivacyTemplate(jsonObject.getBoolean(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE));
            }
            if (jsonObject.has(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES)) {
                notificationPreferences.setOneOnOnePreferences(OneOnOnePreferences.fromJson(jsonObject.getJSONObject(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES)) {
                notificationPreferences.setMutePreferences(MutePreferences.fromJson(jsonObject.getJSONObject(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES)) {
                notificationPreferences.setGroupPreferences(GroupPreferences.fromJson(jsonObject.getJSONObject(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES)));
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return notificationPreferences;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (usePrivacyTemplate) {
                jsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE, true);
            }
            if (oneOnOnePreferences != null) {
                jsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES, oneOnOnePreferences.toJson());
            }
            if (mutePreferences != null) {
                jsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES, mutePreferences.toJson());
            }
            if (groupPreferences != null) {
                jsonObject.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES, groupPreferences.toJson());
            }
        } catch (JSONException e) {
            Logger.error(e.toString());
        }
        return jsonObject;
    }

    public static NotificationPreferences fromMap(Map<String, Object> map) {
        NotificationPreferences notificationPreferences = new NotificationPreferences();
        if (map.containsKey(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE)) {
            notificationPreferences.setUsePrivacyTemplate((boolean) map.get(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE));
        }
        if (map.containsKey(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES)) {
            notificationPreferences.setOneOnOnePreferences(OneOnOnePreferences.fromMap((Map<String, Integer>) map.get(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES)));
        }
        if (map.containsKey(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES)) {
            notificationPreferences.setMutePreferences(MutePreferences.fromMap((Map<String, Object>) map.get(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES)));
        }
        if (map.containsKey(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES)) {
            notificationPreferences.setGroupPreferences(GroupPreferences.fromMap((Map<String, Integer>) map.get(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES)));
        }
        return notificationPreferences;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (usePrivacyTemplate) {
            map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_USE_PRIVACY_TEMPLATE, true);
        }
        if (oneOnOnePreferences != null) {
            map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_ONE_ON_ONE_PREFERENCES, oneOnOnePreferences.toMap());
        }
        if (mutePreferences != null) {
            map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_MUTE_PREFERENCES, mutePreferences.toMap());
        }
        if (groupPreferences != null) {
            map.put(CometChatNotificationsConstants.NotificationPreferencesKeys.KEY_GROUP_PREFERENCES, groupPreferences.toMap());
        }
        return map;
    }

    @Override
    public String toString() {
        return "NotificationPreferences{" +
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
        if (!(other instanceof NotificationPreferences)) return false;

        // 4. Cast
        NotificationPreferences that = (NotificationPreferences) other;

        // 5. Compare all fields
        // Compare primitive boolean field
        if (usePrivacyTemplate != that.usePrivacyTemplate) return false;

        // Compare nested preference objects using ContentEqualsHelper
        return ContentEqualsHelper.objectsContentEqual(oneOnOnePreferences, that.oneOnOnePreferences)
                && ContentEqualsHelper.objectsContentEqual(mutePreferences, that.mutePreferences)
                && ContentEqualsHelper.objectsContentEqual(groupPreferences, that.groupPreferences);
    }

}
