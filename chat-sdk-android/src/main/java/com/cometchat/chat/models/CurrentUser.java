package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cometchat.chat.utils.ContentEqualsHelper;

import androidx.annotation.Nullable;

/**
 * Created by adityagokula on 25/09/18.
 */

public class CurrentUser extends User {

    public static final String TABLE_CURRENT_USER = "CurrentUser";
    public static final String COLUMN_IDENTITY = "identity";
    public static final String COLUMN_SECRET = "secret";
    public static final String COLUMN_AUTH_TOKEN = "authToken";
    public static final String COLUMN_JWT = "jwt";
    public static final String COLUMN_FAT = "fat";

    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_METADATA = "metadata";
    public static final String COLUMN_CREDITS = "credits";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_STATUS_MESSAGE = "statusMessage";
    public static final String COLUMN_LAST_ACTIVE_AT = "lastActiveAt";
    public static final String COLUMN_TAGS = "tags";

    private String authToken;
    private String identity;
    private String secret;
    private String jwt;
    private String fat;

    public CurrentUser() {}

    protected CurrentUser(Parcel in) {
        super(in);
        authToken = in.readString();
        identity = in.readString();
        secret = in.readString();
        jwt = in.readString();
        fat = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(authToken);
        dest.writeString(identity);
        dest.writeString(secret);
        dest.writeString(jwt);
        dest.writeString(fat);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CurrentUser> CREATOR = new Creator<CurrentUser>() {
        @Override
        public CurrentUser createFromParcel(Parcel in) {
            return new CurrentUser(in);
        }

        @Override
        public CurrentUser[] newArray(int size) {
            return new CurrentUser[size];
        }
    };

    @Override
    public CurrentUser clone() {
        return (CurrentUser) super.clone();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
    }

    /**
     * Compares this object with another for content equality.
     * Unlike equals() which compares by identity (ID), this method
     * compares all fields for value equality, including inherited User fields.
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    @Override
    public boolean contentEquals(Object other) {
        // 1. Same reference check
        if (this == other) return true;

        // 2. Null check
        if (other == null) return false;

        // 3. Type check
        if (!(other instanceof CurrentUser)) return false;

        // 4. Call parent's contentEquals (compares all User fields)
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        CurrentUser that = (CurrentUser) other;

        // 6. Compare all CurrentUser-specific fields
        return ContentEqualsHelper.stringsEqual(this.authToken, that.authToken) &&
                ContentEqualsHelper.stringsEqual(this.identity, that.identity) &&
                ContentEqualsHelper.stringsEqual(this.secret, that.secret) &&
                ContentEqualsHelper.stringsEqual(this.jwt, that.jwt) &&
                ContentEqualsHelper.stringsEqual(this.fat, that.fat);
    }


    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public static CurrentUser fromJson(String json) {
        CurrentUser currentUser = new CurrentUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(COLUMN_UID))
                currentUser.setUid(jsonObject.getString(COLUMN_UID));
            if (jsonObject.has(COLUMN_NAME))
                currentUser.setName(jsonObject.getString(COLUMN_NAME));
            if (jsonObject.has(COLUMN_AVATAR))
                currentUser.setAvatar(jsonObject.getString(COLUMN_AVATAR));
            if (jsonObject.has(COLUMN_LINK))
                currentUser.setLink(jsonObject.getString(COLUMN_LINK));
            if (jsonObject.has(COLUMN_ROLE))
                currentUser.setRole(jsonObject.getString(COLUMN_ROLE));
            if (jsonObject.has(COLUMN_STATUS))
                currentUser.setStatus(jsonObject.getString(COLUMN_STATUS));
            if (jsonObject.has(COLUMN_STATUS_MESSAGE))
                currentUser.setStatusMessage(jsonObject.getString(COLUMN_STATUS_MESSAGE));
            if (jsonObject.has(COLUMN_LAST_ACTIVE_AT))
                currentUser.setLastActiveAt(jsonObject.getLong(COLUMN_LAST_ACTIVE_AT));
            if (jsonObject.has(COLUMN_METADATA))
                currentUser.setMetadata(jsonObject.getJSONObject(COLUMN_METADATA));
            if (jsonObject.has(COLUMN_AUTH_TOKEN))
                currentUser.setAuthToken(jsonObject.getString(COLUMN_AUTH_TOKEN));
            if(jsonObject.has(COLUMN_JWT))
                currentUser.setJwt(jsonObject.getString(COLUMN_JWT));
            if(jsonObject.has(COLUMN_FAT))
                currentUser.setFat(jsonObject.getString(COLUMN_FAT));
            JSONObject wsChannelObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_WS_CHANNEL);
            if (wsChannelObject.has(COLUMN_IDENTITY))
                currentUser.setIdentity(wsChannelObject.getString(COLUMN_IDENTITY));
            if (wsChannelObject.has(COLUMN_SECRET))
                currentUser.setSecret(wsChannelObject.getString(COLUMN_SECRET));
            if(jsonObject.has(CometChatConstants.UserKeys.USER_KEY_TAGS)){
                JSONArray tagsArray = jsonObject.getJSONArray(CometChatConstants.UserKeys.USER_KEY_TAGS);
                List<String> tags = new ArrayList<>();
                for(int i=0;i<tagsArray.length();i++){
                    tags.add(tagsArray.getString(i));
                }
                currentUser.setTags(tags);
            }
        } catch (JSONException je) {
            Logger.debug("Exception", je.getMessage());
        }
        return currentUser;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        if (this.getUid() != null)
            map.put(COLUMN_UID, this.getUid());
        if (this.getName() != null)
            map.put(COLUMN_NAME, this.getName());
        if (this.getAvatar() != null)
            map.put(COLUMN_AVATAR, this.getAvatar());
        if (this.getLink() != null)
            map.put(COLUMN_LINK, this.getLink());
        if (this.getRole() != null)
            map.put(COLUMN_ROLE, this.getRole());
        if (this.getMetadata() != null)
            map.put(COLUMN_METADATA, this.getMetadata().toString());
        else
            map.put(COLUMN_CREDITS, "0");
        if (this.getStatus() != null)
            map.put(COLUMN_STATUS, this.getStatus());
        if (this.getStatusMessage() != null)
            map.put(COLUMN_STATUS_MESSAGE, this.getStatusMessage());
        if (this.getLastActiveAt() > 0L)
            map.put(COLUMN_LAST_ACTIVE_AT, String.valueOf(this.getLastActiveAt()));
        else
            map.put(COLUMN_LAST_ACTIVE_AT, "0");
        if (this.getAuthToken() != null)
            map.put(COLUMN_AUTH_TOKEN, this.getAuthToken());
        if (this.getIdentity() != null)
            map.put(COLUMN_IDENTITY, this.getIdentity());
        if (this.getSecret() != null)
            map.put(COLUMN_SECRET, this.getSecret());
        if (this.getJwt() != null)
            map.put(COLUMN_JWT, this.getJwt());
        if(this.tags!=null) {
            JSONArray tagsArray = new JSONArray();
            for (String tag : tags) {
                tagsArray.put(tag);
            }
            map.put(CometChatConstants.UserKeys.USER_KEY_TAGS, tagsArray.toString());
        }
        if (this.getFat() != null)
            map.put(COLUMN_FAT, this.getFat());
        return map;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "authToken='" + authToken + '\'' +
                ", identity='" + identity + '\'' +
                ", secret='" + secret + '\'' +
                ", jwt='" + jwt + '\'' +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", link='" + link + '\'' +
                ", role='" + role + '\'' +
                ", metadata=" + metadata +
                ", status='" + status + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                ", lastActiveAt=" + lastActiveAt +
                ", hasBlockedMe=" + hasBlockedMe +
                ", blockedByMe=" + blockedByMe +
                ", tags=" + tags +
                ", fat=" + fat +
                '}';
    }
}
