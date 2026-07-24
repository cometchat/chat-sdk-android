package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChatUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cometchat.chat.utils.ContentEqualsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by adityagokula on 04/09/18.
 */

public class User extends AppEntity {

    protected String uid;
    protected String name;
    protected String avatar;
    protected String link;
    protected String role;
    protected JSONObject metadata;
    @CometChatConstants.UserStatus
    protected String status;
    protected String statusMessage;
    protected long lastActiveAt;
    protected boolean hasBlockedMe;
    protected boolean blockedByMe;
    protected List<String> tags;
    protected long deactivatedAt;

    public User() {

    }

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    protected User(Parcel in) {
        super(in);
        uid = in.readString();
        name = in.readString();
        avatar = in.readString();
        link = in.readString();
        role = in.readString();
        String metadataStr = in.readString();
        if (metadataStr != null) {
            try {
                metadata = new JSONObject(metadataStr);
            } catch (JSONException e) {
                metadata = null;
            }
        }
        status = in.readString();
        statusMessage = in.readString();
        lastActiveAt = in.readLong();
        hasBlockedMe = in.readByte() != 0;
        blockedByMe = in.readByte() != 0;
        tags = in.createStringArrayList();
        deactivatedAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(link);
        dest.writeString(role);
        dest.writeString(metadata != null ? metadata.toString() : null);
        dest.writeString(status);
        dest.writeString(statusMessage);
        dest.writeLong(lastActiveAt);
        dest.writeByte((byte) (hasBlockedMe ? 1 : 0));
        dest.writeByte((byte) (blockedByMe ? 1 : 0));
        dest.writeStringList(tags);
        dest.writeLong(deactivatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public User clone() {
        try {
            User cloned = (User) super.clone();
            if (this.metadata != null) {
                cloned.metadata = new JSONObject(this.metadata.toString());
            }
            if (this.tags != null) {
                cloned.tags = new ArrayList<>(this.tags);
            }
            return cloned;
        } catch (CloneNotSupportedException | JSONException e) {
            return null;
        }
    }

    /**
     * Get unique identifier of the User
     *
     * @return UID of the group
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Get name of the user
     *
     * @return name of the user
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Get email of the user
     *
     * @return email of the user
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */


    /**
     * Get avatar of the user
     *
     * @return URL user avatar
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Get role of the user set by developer
     *
     * @return role of the user
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Get <code>JSONObject</code> of data set by developer
     *
     * @return <code>JSONObject</code> of custom data set by developer
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public JSONObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    /**
     * Get status of the user
     *
     * @return user status
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    @CometChatConstants.UserStatus
    public String getStatus() {
        return status;
    }

    public void setStatus(@CometChatConstants.UserStatus String status) {
        this.status = status;
    }

    /**
     * Get status message set by developer
     *
     * @return status message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Get last online timestamp of the user
     *
     * @return lastActive timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(long lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    /**
     * Get whether the user has blocked logged in user
     *
     * @return boolean value
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public boolean isHasBlockedMe() {
        return hasBlockedMe;
    }

    public void setHasBlockedMe(boolean hasBlockedMe) {
        this.hasBlockedMe = hasBlockedMe;
    }

    /**
     * Get whether the user has been blocked by logged in user
     *
     * @return boolean value
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public boolean isBlockedByMe() {
        return blockedByMe;
    }

    public void setBlockedByMe(boolean blockedByMe) {
        this.blockedByMe = blockedByMe;
    }

    /**
     * Get the list of tags the user has been tags with
     *
     * @return List<String></>
     * @version <b>v2.1.6</b>
     * @since <b>v2.1.6</b>
     */
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Returns the timestamp when the user was deactivated at if he was deactivated. Else returns 0.
     *
     * @return long</>
     * @version <b>v3.0.3</b>
     * @since <b>v3.0.3</b>
     */
    public long getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(long deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    public static User fromJson(String json) {
        User user = new User();
        try {
            JSONObject userObject = new JSONObject(json);
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_UID))
                user.setUid(userObject.getString(CometChatConstants.UserKeys.USER_KEY_UID));
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_NAME))
                user.setName(userObject.getString(CometChatConstants.UserKeys.USER_KEY_NAME));
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_AVATAR))
                user.setAvatar(userObject.getString(CometChatConstants.UserKeys.USER_KEY_AVATAR));
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_LINK))
                user.setLink(userObject.getString(CometChatConstants.UserKeys.USER_KEY_LINK));
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_ROLE))
                user.setRole(userObject.getString(CometChatConstants.UserKeys.USER_KEY_ROLE));
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_METADATA))
                user.setMetadata(userObject.getJSONObject(CometChatConstants.UserKeys.USER_KEY_METADATA));
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_STATUS)) {
                String userStatus = userObject.getString(CometChatConstants.UserKeys.USER_KEY_STATUS);
                if (userStatus.equalsIgnoreCase(CometChatConstants.ResponseKeys.KEY_STATUS_AVAILABLE))
                    user.setStatus(CometChatConstants.USER_STATUS_ONLINE);
                else
                    user.setStatus(userStatus);
            } else {
                user.setStatus(CometChatConstants.USER_STATUS_OFFLINE);
            }
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_STATUS_MESSAGE))
                user.setStatusMessage(userObject.getString(CometChatConstants.UserKeys.USER_KEY_STATUS_MESSAGE));
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_LAST_ACTIVE_AT))
                user.setLastActiveAt(userObject.getLong(CometChatConstants.UserKeys.USER_KEY_LAST_ACTIVE_AT));
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_HAS_BLOCKED_ME))
                user.setHasBlockedMe(userObject.getBoolean(CometChatConstants.UserKeys.USER_KEY_HAS_BLOCKED_ME));
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_BLOCKED_BY_ME))
                user.setBlockedByMe(userObject.getBoolean(CometChatConstants.UserKeys.USER_KEY_BLOCKED_BY_ME));
            if(userObject.has(CometChatConstants.UserKeys.USER_KEY_TAGS)){
                JSONArray tagsArray = userObject.getJSONArray(CometChatConstants.UserKeys.USER_KEY_TAGS);
                List<String> tags = new ArrayList<>();
                for(int i=0;i<tagsArray.length();i++){
                    tags.add(tagsArray.getString(i));
                }
                user.setTags(tags);
            }
            if (userObject.has(CometChatConstants.UserKeys.USER_KEY_DEACTIVATED_AT))
                user.setDeactivatedAt(userObject.getLong(CometChatConstants.UserKeys.USER_KEY_DEACTIVATED_AT));
        } catch (JSONException je) {
            je.printStackTrace();
            return user;
        }
        return user;
    }

    public static List<User> listFromJsonArray(String json) throws JSONException {
        List<User> users = new ArrayList<>();
        try {
            JSONObject mainObject = new JSONObject(json);
            if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
                for (int i = 0; i < dataArray.length(); i++) {
                    User user = fromJson(dataArray.getJSONObject(i).toString());
                    if (user.getStatus().equalsIgnoreCase(CometChatConstants.ResponseKeys.KEY_STATUS_AVAILABLE))
                        user.setStatus(CometChatConstants.USER_STATUS_ONLINE);
                    users.add(user);
                }
            }
        } catch (JSONException je) {
            throw new JSONException(je.getMessage());
        }
        return users;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        if (this.uid != null)
            map.put(CurrentUser.COLUMN_UID, this.uid);
        if (this.name != null)
            map.put(CurrentUser.COLUMN_NAME, this.name);
        if (this.avatar != null)
            map.put(CurrentUser.COLUMN_AVATAR, this.avatar);
        if (this.link != null)
            map.put(CurrentUser.COLUMN_LINK, this.link);
        if (this.role != null)
            map.put(CurrentUser.COLUMN_ROLE, this.role);
        if (this.metadata != null)
            map.put(CurrentUser.COLUMN_METADATA, this.metadata.toString());
        if (this.status != null)
            map.put(CurrentUser.COLUMN_STATUS, this.status);
        if (this.statusMessage != null)
            map.put(CurrentUser.COLUMN_STATUS_MESSAGE, this.statusMessage);
        if (this.lastActiveAt > 0L)
            map.put(CurrentUser.COLUMN_LAST_ACTIVE_AT, String.valueOf(this.lastActiveAt));
        if(this.tags!=null){
            JSONArray tagsArray = CometChatUtils.getJSONArrayFromList(this.tags);
            map.put(CometChatConstants.UserKeys.USER_KEY_TAGS, tagsArray.toString());
        }
        return map;
    }

    public JSONObject toJson() {
        JSONObject userObject = new JSONObject();
        try {
            if (this.uid != null)
                userObject.put(CometChatConstants.UserKeys.USER_KEY_UID, this.uid);
            if (this.name != null)
                userObject.put(CometChatConstants.UserKeys.USER_KEY_NAME, this.name);
            if (this.role != null)
                userObject.put(CometChatConstants.UserKeys.USER_KEY_ROLE, this.role);
            if (this.avatar != null)
                userObject.put(CometChatConstants.UserKeys.USER_KEY_AVATAR, this.avatar);
            if (this.link != null)
                userObject.put(CometChatConstants.UserKeys.USER_KEY_LINK, this.link);
            if (this.metadata != null)
                userObject.put(CometChatConstants.UserKeys.USER_KEY_METADATA, this.metadata);
            if (this.statusMessage != null)
                userObject.put(CometChatConstants.UserKeys.USER_KEY_STATUS_MESSAGE, this.statusMessage);
            userObject.put(CometChatConstants.UserKeys.USER_KEY_STATUS, CometChatConstants.USER_STATUS_ONLINE);
            if(this.tags!=null) {
                JSONArray tagsArray = new JSONArray();
                for(String tag:tags){
                    tagsArray.put(tag);
                }
                userObject.put(CometChatConstants.UserKeys.USER_KEY_TAGS, tagsArray);
            }
        }catch (JSONException e){
            return new JSONObject();
        }
        return userObject;

    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
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
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
    }

    /**
     * Compares this object with another for content equality.
     * Unlike equals() which compares by identity (ID), this method
     * compares all fields for value equality.
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
        if (!(other instanceof User)) return false;

        // 4. Call parent's contentEquals
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        User that = (User) other;

        // 6. Compare all fields using ContentEqualsHelper and == for primitives
        return ContentEqualsHelper.stringsEqual(this.uid, that.uid) &&
                ContentEqualsHelper.stringsEqual(this.name, that.name) &&
                ContentEqualsHelper.stringsEqual(this.avatar, that.avatar) &&
                ContentEqualsHelper.stringsEqual(this.link, that.link) &&
                ContentEqualsHelper.stringsEqual(this.role, that.role) &&
                ContentEqualsHelper.jsonObjectsEqual(this.metadata, that.metadata) &&
                ContentEqualsHelper.stringsEqual(this.status, that.status) &&
                ContentEqualsHelper.stringsEqual(this.statusMessage, that.statusMessage) &&
                this.lastActiveAt == that.lastActiveAt &&
                this.hasBlockedMe == that.hasBlockedMe &&
                this.blockedByMe == that.blockedByMe &&
                ContentEqualsHelper.listsEqual(this.tags, that.tags) &&
                this.deactivatedAt == that.deactivatedAt;
    }
}
