package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.MutedConversationType;
import com.cometchat.chat.helpers.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Rohit Giri on 16/02/24.
 */
public class UnmutedConversation implements Parcelable, Cloneable {
    private String id;
    private MutedConversationType type;

    public UnmutedConversation() {}

    protected UnmutedConversation(Parcel in) {
        id = in.readString();
        String typeStr = in.readString();
        if (typeStr != null) {
            type = MutedConversationType.get(typeStr);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type != null ? type.getType() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UnmutedConversation> CREATOR = new Creator<UnmutedConversation>() {
        @Override
        public UnmutedConversation createFromParcel(Parcel in) {
            return new UnmutedConversation(in);
        }

        @Override
        public UnmutedConversation[] newArray(int size) {
            return new UnmutedConversation[size];
        }
    };

    @Override
    public UnmutedConversation clone() {
        try {
            return (UnmutedConversation) super.clone();
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

    public static JSONArray toJsonArray(List<UnmutedConversation> unmutedConversationList) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (UnmutedConversation unmutedConversation : unmutedConversationList) {
                jsonArray.put(unmutedConversation.toJson());
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return jsonArray;
    }

    public static UnmutedConversation fromJson(JSONObject jsonObject) {
        UnmutedConversation unmutedConversation = new UnmutedConversation();
        try {
            if (jsonObject.has(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_ID)) {
                unmutedConversation.setId(jsonObject.getString(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_ID));
            }
            if (jsonObject.has(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_TYPE)) {
                unmutedConversation.setType(MutedConversationType.get(jsonObject.getString(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_TYPE)));
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return unmutedConversation;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if(id != null){
                jsonObject.put(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_ID, id);
            }
            if(type != null){
                jsonObject.put(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_TYPE, type.getType());
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return jsonObject;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if(id != null){
            map.put(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_ID, id);
        }
        if(type != null){
            map.put(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_TYPE, type.getType());
        }
        return map;
    }

    public static UnmutedConversation fromMap(Map<String, Object> map) {
        UnmutedConversation unmutedConversation = new UnmutedConversation();
        if(map.containsKey(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_ID)){
            unmutedConversation.setId((String) map.get(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_ID));
        }
        if(map.containsKey(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_TYPE)){
            unmutedConversation.setType(MutedConversationType.get((String) map.get(CometChatNotificationsConstants.UnmutedConversationKeys.KEY_TYPE)));
        }
        return unmutedConversation;
    }

    @Override
    public String toString() {
        return "UnmutedConversation{" +
                "id='" + id + '\'' +
                ", type=" + type +
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
        if (!(other instanceof UnmutedConversation)) return false;

        // 4. Cast
        UnmutedConversation that = (UnmutedConversation) other;

        // 5. Compare all fields
        return com.cometchat.chat.utils.ContentEqualsHelper.stringsEqual(this.id, that.id)
                && this.type == that.type;
    }
}