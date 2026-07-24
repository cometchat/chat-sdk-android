package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.cometchat.chat.utils.ContentEqualsHelper;

/**
 * Created by adityagokula on 12/09/18.
 */

public class Group extends AppEntity {

    public static final String TABLE_GROUPS = "Groups";

    public static final String COLUMN_GUID = "guid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GROUP_TYPE = "type";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ICON = "icon";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_OWNER = "owner";
    public static final String COLUMN_METADATA = "metadata";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String COLUMN_UPDATED_AT = "updatedAt";
    public static final String COLUMN_HAS_JOINED = "hasJoined";
    public static final String COLUMN_IDENTITY = "groupIdentity";

    private String guid;
    private String name;
    @CometChatConstants.GroupTypes
    private String type;
    private String password;
    private String icon;
    private String description;
    private String owner;
    private JSONObject metadata;
    private long createdAt;
    private long updatedAt;
    private boolean hasJoined;
    private long joinedAt = -1;
    @CometChatConstants.MemberScope
    private String scope;
    private int membersCount = 0;
    private List<String> tags;
    private boolean isBannedFromGroup = false;

    public Group(String guid, String name, @CometChatConstants.GroupTypes String groupType, String password) {
        this.guid = guid;
        this.name = name;
        this.type = groupType;
        this.password = password;
    }

    public Group(String guid, String name, @CometChatConstants.GroupTypes String groupType, String password, String icon, String description) {
        this.guid = guid;
        this.name = name;
        this.type = groupType;
        this.password = password;
        this.icon = icon;
        this.description = description;
    }

    public Group(){

    }

    protected Group(Parcel in) {
        super(in);
        guid = in.readString();
        name = in.readString();
        type = in.readString();
        password = in.readString();
        icon = in.readString();
        description = in.readString();
        owner = in.readString();
        String metadataStr = in.readString();
        if (metadataStr != null) {
            try {
                metadata = new JSONObject(metadataStr);
            } catch (JSONException e) {
                metadata = null;
            }
        }
        createdAt = in.readLong();
        updatedAt = in.readLong();
        hasJoined = in.readByte() != 0;
        joinedAt = in.readLong();
        scope = in.readString();
        membersCount = in.readInt();
        tags = in.createStringArrayList();
        isBannedFromGroup = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(guid);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(password);
        dest.writeString(icon);
        dest.writeString(description);
        dest.writeString(owner);
        dest.writeString(metadata != null ? metadata.toString() : null);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeByte((byte) (hasJoined ? 1 : 0));
        dest.writeLong(joinedAt);
        dest.writeString(scope);
        dest.writeInt(membersCount);
        dest.writeStringList(tags);
        dest.writeByte((byte) (isBannedFromGroup ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public Group clone() {
        try {
            Group cloned = (Group) super.clone();
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
     * Get unique identifier of the Group
     *
     * @return GUID of the group
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * Get name of the group
     *
     * @return name of the Group
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
     * Get URL of the icon
     *
     * @return URL of the icon
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Get description of the Group
     *
     * @return description of the group
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get <code>UID</code> of the owner of the Group
     *
     * @return UID of the user
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get type of the group
     * types of th group :
     * <ul>
     * <li>GROUP_TYPE_PUBLIC<li/>
     * <li>GROUP_TYPE_PASSWORD<li/>
     * <li>GROUP_TYPE_PRIVATE<li/>
     * <ul/>
     *
     * @return type of the Group
     * @version <b>v2</b>
     * @see CometChatConstants.GroupTypes
     * @since <b>v1</b>
     */
    public String getGroupType() {
        return type;
    }

    public void setGroupType(String groupType) {
        this.type = groupType;
    }

    /**
     *  Get password of the password protected group
     *
     * @return password of the group
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    @CometChatConstants.GroupTypes
    public String getPassword() {
        return password;
    }

    public void setPassword(@CometChatConstants.GroupTypes String password) {
        this.password = password;
    }

    /**
     *  Get <code>JSONObject</code> of data set by developer
     *
     * @return <code>JSONObject</code> of custom data set by developer
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public JSONObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    /**
     *  Get timestamp of group creation
     *
     * @return creation timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *  Get timestamp of group update
     *
     * @return updated timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *  Get join information of logged in user in a particular group
     *
     * @return true if logged in user is part of the group else false
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public boolean isJoined() {
        return hasJoined;
    }

    public void setHasJoined(boolean hasJoined) {
        this.hasJoined = hasJoined;
    }

    /**
     *  Get join at timestamp of logged in user in a particular group
     *
     * @return timestamp of joined at of logged in user
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    /**
     *  Get scope of logged in user in a particular group
     *
     * @return scope of logged in user
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     *  Get number of members that are a part of the group
     *
     * @return number of participants for the group
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    /**
     *  Get the tags that are specified for the group
     *
     * @return number of participants for the group
     * @version <b>v2</b>
     * @since <b>v2.1.6</b>
     *
     */
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     *  Check if a user is banned
     *
     * @return true if the user is banned, false otherwise
     * @version <b>v4</b>
     * @since <b>v4</b>
     *
     */
    public boolean isBannedFromGroup() {
        return isBannedFromGroup;
    }

    /**
     *  Set the status of the user as banned or not
     *
     * @param bannedFromGroup The status to set if a user is banned
     * @version <b>v4</b>
     * @since <b>v4</b>
     *
     */
    private void setBannedFromGroup(boolean bannedFromGroup) {
        isBannedFromGroup = bannedFromGroup;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        if (this.guid != null)
            map.put(CometChatConstants.GroupKeys.KEY_GROUP_GUID, this.guid);
        if (this.name != null)
            map.put(CometChatConstants.GroupKeys.KEY_GROUP_NAME, this.name);
        if (this.icon != null)
            map.put(CometChatConstants.GroupKeys.KEY_GROUP_ICON, this.icon);
        if (this.description != null)
            map.put(CometChatConstants.GroupKeys.KEY_GROUP_DESCRIPTION, this.description);
        if (this.owner != null)
            map.put(CometChatConstants.GroupKeys.KEY_GROUP_OWNER, this.owner);
        if (this.type != null)
            map.put(CometChatConstants.GroupKeys.KEY_GROUP_TYPE, this.type);
        if ((this.type != null && this.type.equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PASSWORD)) && (this.password != null && !TextUtils.isEmpty(this.password)))
            map.put(CometChatConstants.GroupKeys.KEY_GROUP_PASSWORD, this.password);
        if (this.metadata != null)
            map.put(CometChatConstants.GroupKeys.KEY_METADATA, this.metadata.toString());
        if (this.createdAt != -1)
            map.put(CometChatConstants.GroupKeys.KEY_CREATED_AT, String.valueOf(this.createdAt));
        if (this.updatedAt != -1)
            map.put(CometChatConstants.GroupKeys.KEY_UPDATED_AT, String.valueOf(this.updatedAt));
        if (this.getJoinedAt() != -1)
            map.put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_JOINED_AT, String.valueOf(this.joinedAt));
        if (this.scope != null)
            map.put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE, this.scope);
        if(this.tags!=null){
            JSONArray tagsArray = new JSONArray();
            for(String tag: tags){
                tagsArray.put(tag);
            }
            map.put(CometChatConstants.UserKeys.USER_KEY_TAGS, tagsArray.toString());
        }
        map.put(CometChatConstants.GroupKeys.KEY_HAS_JOINED, String.valueOf(this.hasJoined ? 1 : 0));
        return map;
    }

    public static Group fromJson(String json) {
        Group group = new Group();
        try {
            JSONObject groupObject = new JSONObject(json);
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_GUID))
                group.setGuid(groupObject.getString(CometChatConstants.GroupKeys.KEY_GROUP_GUID));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_NAME))
                group.setName(groupObject.getString(CometChatConstants.GroupKeys.KEY_GROUP_NAME));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_ICON))
                group.setIcon(groupObject.getString(CometChatConstants.GroupKeys.KEY_GROUP_ICON));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_DESCRIPTION))
                group.setDescription(groupObject.getString(CometChatConstants.GroupKeys.KEY_GROUP_DESCRIPTION));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_OWNER))
                group.setOwner(groupObject.getString(CometChatConstants.GroupKeys.KEY_GROUP_OWNER));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_TYPE))
                group.setGroupType(groupObject.getString(CometChatConstants.GroupKeys.KEY_GROUP_TYPE));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_PASSWORD))
                group.setPassword(groupObject.getString(CometChatConstants.GroupKeys.KEY_GROUP_PASSWORD));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_METADATA))
                group.setMetadata(groupObject.getJSONObject(CometChatConstants.GroupKeys.KEY_METADATA));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_CREATED_AT))
                group.setCreatedAt(groupObject.getLong(CometChatConstants.GroupKeys.KEY_CREATED_AT));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_UPDATED_AT))
                group.setUpdatedAt(groupObject.getLong(CometChatConstants.GroupKeys.KEY_UPDATED_AT));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_HAS_JOINED))
                group.setHasJoined(groupObject.getBoolean(CometChatConstants.GroupKeys.KEY_HAS_JOINED));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE))
                group.setScope(groupObject.getString(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_JOINED_AT))
                group.setJoinedAt(groupObject.getLong(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_JOINED_AT));
            if(groupObject.has(CometChatConstants.GroupKeys.KEY_GROUP_MEMBERS_COUNT))
                group.setMembersCount(groupObject.getInt(CometChatConstants.GroupKeys.KEY_GROUP_MEMBERS_COUNT));
            if(groupObject.has(CometChatConstants.GroupKeys.GROUP_KEY_IS_BANNED))
                group.setBannedFromGroup(groupObject.getBoolean(CometChatConstants.GroupKeys.GROUP_KEY_IS_BANNED));
            if(groupObject.has(CometChatConstants.GroupKeys.GROUP_KEY_TAGS)){
                JSONArray tagsArray = groupObject.getJSONArray(CometChatConstants.UserKeys.USER_KEY_TAGS);
                List<String> tags = new ArrayList<>();
                for(int i=0;i<tagsArray.length();i++){
                    tags.add(tagsArray.getString(i));
                }
                group.setTags(tags);
            }

        }catch (JSONException je){
            je.printStackTrace();
            return group;
        }
        return group;
    }

    public static List<Group> fromJSONArray(String response) throws JSONException {
        List<Group> groups = new ArrayList<>();
        JSONObject mainObject = new JSONObject(response);
        if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
            JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
            for (int i = 0; i < dataArray.length(); i++) {
                Group group = fromJson(dataArray.getJSONObject(i).toString());
                groups.add(group);
            }
        }
        return groups;
    }

    @Override
    public String toString() {
        return "Group{" +
                "guid='" + guid + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", password='" + password + '\'' +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", owner='" + owner + '\'' +
                ", metadata=" + metadata +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", hasJoined=" + hasJoined +
                ", joinedAt=" + joinedAt +
                ", scope='" + scope + '\'' +
                ", membersCount=" + membersCount +
                ", tags=" + tags +
                ", isBannedFromGroup=" + isBannedFromGroup +
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
        if (!(other instanceof Group)) return false;

        // 4. Call parent's contentEquals
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        Group that = (Group) other;

        // 6. Compare all fields using ContentEqualsHelper and == for primitives
        return ContentEqualsHelper.stringsEqual(this.guid, that.guid) &&
                ContentEqualsHelper.stringsEqual(this.name, that.name) &&
                ContentEqualsHelper.stringsEqual(this.type, that.type) &&
                ContentEqualsHelper.stringsEqual(this.password, that.password) &&
                ContentEqualsHelper.stringsEqual(this.icon, that.icon) &&
                ContentEqualsHelper.stringsEqual(this.description, that.description) &&
                ContentEqualsHelper.stringsEqual(this.owner, that.owner) &&
                ContentEqualsHelper.jsonObjectsEqual(this.metadata, that.metadata) &&
                this.createdAt == that.createdAt &&
                this.updatedAt == that.updatedAt &&
                this.hasJoined == that.hasJoined &&
                this.joinedAt == that.joinedAt &&
                ContentEqualsHelper.stringsEqual(this.scope, that.scope) &&
                this.membersCount == that.membersCount &&
                ContentEqualsHelper.listsEqual(this.tags, that.tags) &&
                this.isBannedFromGroup == that.isBannedFromGroup;
    }
}
