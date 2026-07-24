package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by adityagokula on 30/10/18.
 */

public class GroupMember extends User {

    private String scope;
    private long joinedAt;

    public GroupMember(String UID, @CometChatConstants.MemberScope String scope) {
        this.uid = UID;
        this.scope = scope;
    }

    private GroupMember() {

    }

    protected GroupMember(Parcel in) {
        super(in);
        scope = in.readString();
        joinedAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(scope);
        dest.writeLong(joinedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupMember> CREATOR = new Creator<GroupMember>() {
        @Override
        public GroupMember createFromParcel(Parcel in) {
            return new GroupMember(in);
        }

        @Override
        public GroupMember[] newArray(int size) {
            return new GroupMember[size];
        }
    };

    @Override
    public GroupMember clone() {
        return (GroupMember) super.clone();
    }

    /**
     * Get join at timestamp of logged in user in a particular group
     *
     * @return timestamp of joined at of logged in user
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    /**
     * Get scope of logged in user in a particular group
     *
     * @return scope of logged in user
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getScope() {

        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    public static List<GroupMember> listFromJSONArray(String membersArray) throws JSONException {
        List<GroupMember> members = new ArrayList<>();
        try {
            JSONObject mainObject = new JSONObject(membersArray);
            if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
                for (int i = 0; i < dataArray.length(); i++) {
                    GroupMember groupMember = new GroupMember();
                    User user = fromJson(dataArray.getJSONObject(i).toString());
                    groupMember.setUid(user.getUid());
                    groupMember.setName(user.getName());
                    groupMember.setAvatar(user.getAvatar());
                    groupMember.setLink(user.getLink());
                    groupMember.setRole(user.getRole());
                    groupMember.setMetadata(user.getMetadata());
                    groupMember.setStatus(user.getStatus());
                    groupMember.setStatusMessage(user.getStatusMessage());
                    groupMember.setLastActiveAt(user.getLastActiveAt());
                    JSONObject jsonObject = dataArray.getJSONObject(i);
                    if (jsonObject.has(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE)) {
                        groupMember.setScope(jsonObject.getString(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE));
                    }
                    if (jsonObject.has(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_JOINED_AT)) {
                        groupMember.setJoinedAt(jsonObject.getLong(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_JOINED_AT));
                    }
                    members.add(groupMember);
                }
            }
        } catch (JSONException je) {
            throw new JSONException(je.getMessage());
        }
        return members;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "scope='" + scope + '\'' +
                ", joinedAt=" + joinedAt +
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
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
    }

    /**
     * Compares this object with another for content equality.
     * Unlike equals() which compares by identity (UID), this method
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

        // 3. Type check - must be GroupMember, not just User
        if (!(other instanceof GroupMember)) return false;

        // 4. Call parent's contentEquals to compare all inherited User fields
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        GroupMember that = (GroupMember) other;

        // 6. Compare GroupMember-specific fields
        return ContentEqualsHelper.stringsEqual(this.scope, that.scope) &&
                this.joinedAt == that.joinedAt;
    }
}
