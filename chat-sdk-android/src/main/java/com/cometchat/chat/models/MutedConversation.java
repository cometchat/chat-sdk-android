package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.MutedConversationType;
import com.cometchat.chat.helpers.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cometchat.chat.utils.ContentEqualsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Rohit Giri on 15/02/24.
 */
public class MutedConversation implements Parcelable, Cloneable {
    String id;
    MutedConversationType type;
    long until = 0;

    public MutedConversation() {
    }

    protected MutedConversation(Parcel in) {
        id = in.readString();
        String typeStr = in.readString();
        if (typeStr != null) {
            type = MutedConversationType.get(typeStr);
        }
        until = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type != null ? type.getType() : null);
        dest.writeLong(until);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MutedConversation> CREATOR = new Creator<MutedConversation>() {
        @Override
        public MutedConversation createFromParcel(Parcel in) {
            return new MutedConversation(in);
        }

        @Override
        public MutedConversation[] newArray(int size) {
            return new MutedConversation[size];
        }
    };

    @Override
    public MutedConversation clone() {
        try {
            return (MutedConversation) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MutedConversationType getType() {
        return type;
    }

    public void setType(MutedConversationType type) {
        this.type = type;
    }

    public long getUntil() {
        return until;
    }

    public void setUntil(long until) {
        this.until = until;
    }

    public static JSONArray toJsonArray(List<MutedConversation> mutedConversationList) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (MutedConversation mutedConversation : mutedConversationList) {
                jsonArray.put(mutedConversation.toJson());
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return jsonArray;
    }

    public static List<MutedConversation> fromJsonArray(JSONObject mainObject) {
        List<MutedConversation> mutedConversationList = new ArrayList<>();
        try {
            if (mainObject.has(CometChatNotificationsConstants.ResponseKeys.KEY_DATA)) {
                JSONObject jsonObject = mainObject.getJSONObject(CometChatNotificationsConstants.ResponseKeys.KEY_DATA);
                if (jsonObject.has(CometChatNotificationsConstants.ResponseKeys.KEY_MUTED_CONVERSATIONS)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(CometChatNotificationsConstants.ResponseKeys.KEY_MUTED_CONVERSATIONS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject mutedConversationJsonObject = jsonArray.getJSONObject(i);
                        mutedConversationList.add(fromJson(mutedConversationJsonObject));
                    }
                }
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return mutedConversationList;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (id != null) {
                jsonObject.put(CometChatNotificationsConstants.MutedConversationKeys.KEY_ID, id);
            }
            if (type != null) {
                jsonObject.put(CometChatNotificationsConstants.MutedConversationKeys.KEY_TYPE, type.getType());
            }
            if (until != 0) {
                jsonObject.put(CometChatNotificationsConstants.MutedConversationKeys.KEY_UNTIL, until);
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return jsonObject;
    }

    public static MutedConversation fromJson(JSONObject jsonObject) {
        MutedConversation mutedConversation = new MutedConversation();
        try {
            if (jsonObject.has(CometChatNotificationsConstants.MutedConversationKeys.KEY_ID)) {
                mutedConversation.setId(jsonObject.getString(CometChatNotificationsConstants.MutedConversationKeys.KEY_ID));
            }
            if (jsonObject.has(CometChatNotificationsConstants.MutedConversationKeys.KEY_TYPE)) {
                mutedConversation.setType(MutedConversationType.get(jsonObject.getString(CometChatNotificationsConstants.MutedConversationKeys.KEY_TYPE)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.MutedConversationKeys.KEY_UNTIL)) {
                mutedConversation.setUntil(jsonObject.getLong(CometChatNotificationsConstants.MutedConversationKeys.KEY_UNTIL));
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return mutedConversation;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (id != null) {
            map.put(CometChatNotificationsConstants.MutedConversationKeys.KEY_ID, id);
        }
        if (type != null) {
            String typeValue = type.getType();
            if (typeValue != null) {
                map.put(CometChatNotificationsConstants.MutedConversationKeys.KEY_TYPE, typeValue);
            }
        }
        if (until != 0) {
            map.put(CometChatNotificationsConstants.MutedConversationKeys.KEY_UNTIL, until);
        }
        return map;
    }

    public static MutedConversation fromMap(Map<String, Object> map) {
        MutedConversation mutedConversation = new MutedConversation();
        if (map.containsKey(CometChatNotificationsConstants.MutedConversationKeys.KEY_ID)) {
            mutedConversation.setId((String) map.get(CometChatNotificationsConstants.MutedConversationKeys.KEY_ID));
        }
        if (map.containsKey(CometChatNotificationsConstants.MutedConversationKeys.KEY_TYPE)) {
            mutedConversation.setType(MutedConversationType.get((String) map.get(CometChatNotificationsConstants.MutedConversationKeys.KEY_TYPE)));
        }
        if (map.containsKey(CometChatNotificationsConstants.MutedConversationKeys.KEY_UNTIL)) {
            mutedConversation.setUntil(((long) map.get(CometChatNotificationsConstants.MutedConversationKeys.KEY_UNTIL)));
        }
        return mutedConversation;
    }

    @Override
    public String toString() {
        return "MutedConversation{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", until='" + until + '\'' +
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
        if (!(other instanceof MutedConversation)) return false;

        // 4. Cast
        MutedConversation that = (MutedConversation) other;

        // 5. Compare all fields
        return ContentEqualsHelper.stringsEqual(id, that.id)
                && type == that.type
                && until == that.until;
    }
}
