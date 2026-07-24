package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.MemberActionsOptions;
import com.cometchat.chat.enums.MessagesOptions;
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
public class GroupPreferences implements Parcelable, Cloneable {
    private MessagesOptions groupMessages;
    private RepliesOptions groupReplies;
    private ReactionsOptions groupReactions;
    private MemberActionsOptions groupMemberLeft;
    private MemberActionsOptions groupMemberAdded;
    private MemberActionsOptions groupMemberJoined;
    private MemberActionsOptions groupMemberKicked;
    private MemberActionsOptions groupMemberBanned;
    private MemberActionsOptions groupMemberUnbanned;
    private MemberActionsOptions groupMemberScopeChanged;

    public GroupPreferences() {}

    protected GroupPreferences(Parcel in) {
        int msgVal = in.readInt();
        groupMessages = msgVal != -1 ? MessagesOptions.get(msgVal) : null;
        int repliesVal = in.readInt();
        groupReplies = repliesVal != -1 ? RepliesOptions.get(repliesVal) : null;
        int reactionsVal = in.readInt();
        groupReactions = reactionsVal != -1 ? ReactionsOptions.get(reactionsVal) : null;
        int leftVal = in.readInt();
        groupMemberLeft = leftVal != -1 ? MemberActionsOptions.get(leftVal) : null;
        int addedVal = in.readInt();
        groupMemberAdded = addedVal != -1 ? MemberActionsOptions.get(addedVal) : null;
        int joinedVal = in.readInt();
        groupMemberJoined = joinedVal != -1 ? MemberActionsOptions.get(joinedVal) : null;
        int kickedVal = in.readInt();
        groupMemberKicked = kickedVal != -1 ? MemberActionsOptions.get(kickedVal) : null;
        int bannedVal = in.readInt();
        groupMemberBanned = bannedVal != -1 ? MemberActionsOptions.get(bannedVal) : null;
        int unbannedVal = in.readInt();
        groupMemberUnbanned = unbannedVal != -1 ? MemberActionsOptions.get(unbannedVal) : null;
        int scopeVal = in.readInt();
        groupMemberScopeChanged = scopeVal != -1 ? MemberActionsOptions.get(scopeVal) : null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(groupMessages != null ? groupMessages.getValue() : -1);
        dest.writeInt(groupReplies != null ? groupReplies.getValue() : -1);
        dest.writeInt(groupReactions != null ? groupReactions.getValue() : -1);
        dest.writeInt(groupMemberLeft != null ? groupMemberLeft.getValue() : -1);
        dest.writeInt(groupMemberAdded != null ? groupMemberAdded.getValue() : -1);
        dest.writeInt(groupMemberJoined != null ? groupMemberJoined.getValue() : -1);
        dest.writeInt(groupMemberKicked != null ? groupMemberKicked.getValue() : -1);
        dest.writeInt(groupMemberBanned != null ? groupMemberBanned.getValue() : -1);
        dest.writeInt(groupMemberUnbanned != null ? groupMemberUnbanned.getValue() : -1);
        dest.writeInt(groupMemberScopeChanged != null ? groupMemberScopeChanged.getValue() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupPreferences> CREATOR = new Creator<GroupPreferences>() {
        @Override
        public GroupPreferences createFromParcel(Parcel in) {
            return new GroupPreferences(in);
        }

        @Override
        public GroupPreferences[] newArray(int size) {
            return new GroupPreferences[size];
        }
    };

    @Override
    public GroupPreferences clone() {
        try {
            return (GroupPreferences) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public MessagesOptions getMessagesPreference() {
        return groupMessages;
    }

    public RepliesOptions getRepliesPreference() {
        return groupReplies;
    }

    public ReactionsOptions getReactionsPreference() {
        return groupReactions;
    }

    public MemberActionsOptions getMemberLeftPreference() {
        return groupMemberLeft;
    }

    public MemberActionsOptions getMemberAddedPreference() {
        return groupMemberAdded;
    }

    public MemberActionsOptions getMemberJoinedPreference() {
        return groupMemberJoined;
    }

    public MemberActionsOptions getMemberKickedPreference() {
        return groupMemberKicked;
    }

    public MemberActionsOptions getMemberBannedPreference() {
        return groupMemberBanned;
    }

    public MemberActionsOptions getMemberUnbannedPreference() {
        return groupMemberUnbanned;
    }

    public MemberActionsOptions getMemberScopeChangedPreference() {
        return groupMemberScopeChanged;
    }

    public void setMessagesPreference(MessagesOptions groupMessages) {
        this.groupMessages = groupMessages;
    }

    public void setRepliesPreference(RepliesOptions groupReplies) {
        this.groupReplies = groupReplies;
    }

    public void setReactionsPreference(ReactionsOptions groupReactions) {
        this.groupReactions = groupReactions;
    }

    public void setMemberLeftPreference(MemberActionsOptions groupMemberLeft) {
        this.groupMemberLeft = groupMemberLeft;
    }

    public void setMemberAddedPreference(MemberActionsOptions groupMemberAdded) {
        this.groupMemberAdded = groupMemberAdded;
    }

    public void setMemberJoinedPreference(MemberActionsOptions groupMemberJoined) {
        this.groupMemberJoined = groupMemberJoined;
    }

    public void setMemberKickedPreference(MemberActionsOptions groupMemberKicked) {
        this.groupMemberKicked = groupMemberKicked;
    }

    public void setMemberBannedPreference(MemberActionsOptions groupMemberBanned) {
        this.groupMemberBanned = groupMemberBanned;
    }

    public void setMemberUnbannedPreference(MemberActionsOptions groupMemberUnbanned) {
        this.groupMemberUnbanned = groupMemberUnbanned;
    }

    public void setMemberScopeChangedPreference(MemberActionsOptions groupMemberScopeChanged) {
        this.groupMemberScopeChanged = groupMemberScopeChanged;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if(groupMessages != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES, groupMessages.getValue());
            }
            if(groupReplies != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REPLIES, groupReplies.getValue());
            }
            if(groupReactions != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REACTIONS, groupReactions.getValue());
            }
            if(groupMemberLeft != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_LEFT, groupMemberLeft.getValue());
            }
            if(groupMemberAdded != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_ADDED, groupMemberAdded.getValue());
            }
            if(groupMemberJoined != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_JOINED, groupMemberJoined.getValue());
            }
            if(groupMemberKicked != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_KICKED, groupMemberKicked.getValue());
            }
            if(groupMemberBanned != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_BANNED, groupMemberBanned.getValue());
            }
            if(groupMemberUnbanned != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_UNBANNED, groupMemberUnbanned.getValue());
            }
            if(groupMemberScopeChanged != null){
                jsonObject.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_SCOPE_CHANGED, groupMemberScopeChanged.getValue());
            }
        } catch (JSONException e) {
            Logger.error(e.toString());
        }
        return jsonObject;
    }

    public static GroupPreferences fromJson(JSONObject jsonObject) {
        GroupPreferences groupPreferences = new GroupPreferences();
        try {
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES)){
                groupPreferences.setMessagesPreference(MessagesOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REPLIES)){
                groupPreferences.setRepliesPreference(RepliesOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REPLIES)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REACTIONS)){
                groupPreferences.setReactionsPreference(ReactionsOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REACTIONS)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_LEFT)){
                groupPreferences.setMemberLeftPreference(MemberActionsOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_LEFT)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_ADDED)){
                groupPreferences.setMemberAddedPreference(MemberActionsOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_ADDED)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_JOINED)){
                groupPreferences.setMemberJoinedPreference(MemberActionsOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_JOINED)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_KICKED)){
                groupPreferences.setMemberKickedPreference(MemberActionsOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_KICKED)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_BANNED)){
                groupPreferences.setMemberBannedPreference(MemberActionsOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_BANNED)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_UNBANNED)){
                groupPreferences.setMemberUnbannedPreference(MemberActionsOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_UNBANNED)));
            }
            if (jsonObject.has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_SCOPE_CHANGED)){
                groupPreferences.setMemberScopeChangedPreference(MemberActionsOptions.get(jsonObject.optInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_SCOPE_CHANGED)));
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return groupPreferences;
    }

    public Map<String, Integer> toMap() {
        Map<String, Integer> map = new HashMap<>();
        if(groupMessages != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES, groupMessages.getValue());
        }
        if(groupReplies != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REPLIES, groupReplies.getValue());
        }
        if(groupReactions != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REACTIONS, groupReactions.getValue());
        }
        if(groupMemberLeft != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_LEFT, groupMemberLeft.getValue());
        }
        if(groupMemberAdded != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_ADDED, groupMemberAdded.getValue());
        }
        if(groupMemberJoined != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_JOINED, groupMemberJoined.getValue());
        }
        if(groupMemberKicked != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_KICKED, groupMemberKicked.getValue());
        }
        if(groupMemberBanned != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_BANNED, groupMemberBanned.getValue());
        }
        if(groupMemberUnbanned != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_UNBANNED, groupMemberUnbanned.getValue());
        }
        if(groupMemberScopeChanged != null){
            map.put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_SCOPE_CHANGED, groupMemberScopeChanged.getValue());
        }
        return map;
    }

    public static GroupPreferences fromMap(Map<String, Integer> map) {
        GroupPreferences groupPreferences = new GroupPreferences();
        if (map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES)){
            groupPreferences.setMessagesPreference(MessagesOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MESSAGES)));
        }
        if (map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REPLIES)){
            groupPreferences.setRepliesPreference(RepliesOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REPLIES)));
        }
        if (map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REACTIONS)){
            groupPreferences.setReactionsPreference(ReactionsOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_REACTIONS)));
        }
        if(map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_LEFT)){
            groupPreferences.setMemberLeftPreference(MemberActionsOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_LEFT)));
        }
        if (map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_ADDED)){
            groupPreferences.setMemberAddedPreference(MemberActionsOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_ADDED)));
        }
        if (map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_JOINED)){
            groupPreferences.setMemberJoinedPreference(MemberActionsOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_JOINED)));
        }
        if (map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_KICKED)){
            groupPreferences.setMemberKickedPreference(MemberActionsOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_KICKED)));
        }
        if (map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_BANNED)){
            groupPreferences.setMemberBannedPreference(MemberActionsOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_BANNED)));
        }
        if (map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_UNBANNED)){
            groupPreferences.setMemberUnbannedPreference(MemberActionsOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_UNBANNED)));
        }
        if (map.containsKey(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_SCOPE_CHANGED)){
            groupPreferences.setMemberScopeChangedPreference(MemberActionsOptions.get(map.get(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_MEMBER_SCOPE_CHANGED)));
        }
        return groupPreferences;
    }

    @Override
    public String toString() {
        return "GroupPreferences{" +
                "groupMessages=" + groupMessages +
                ", groupReplies=" + groupReplies +
                ", groupReactions=" + groupReactions +
                ", groupMemberLeft=" + groupMemberLeft +
                ", groupMemberAdded=" + groupMemberAdded +
                ", groupMemberJoined=" + groupMemberJoined +
                ", groupMemberKicked=" + groupMemberKicked +
                ", groupMemberBanned=" + groupMemberBanned +
                ", groupMemberUnbanned=" + groupMemberUnbanned +
                ", groupMemberScopeChanged=" + groupMemberScopeChanged +
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
        if (!(other instanceof GroupPreferences)) return false;

        // 4. Cast
        GroupPreferences that = (GroupPreferences) other;

        // 5. Compare all enum fields (use == for enums, handles null safely)
        return groupMessages == that.groupMessages
                && groupReplies == that.groupReplies
                && groupReactions == that.groupReactions
                && groupMemberLeft == that.groupMemberLeft
                && groupMemberAdded == that.groupMemberAdded
                && groupMemberJoined == that.groupMemberJoined
                && groupMemberKicked == that.groupMemberKicked
                && groupMemberBanned == that.groupMemberBanned
                && groupMemberUnbanned == that.groupMemberUnbanned
                && groupMemberScopeChanged == that.groupMemberScopeChanged;
    }
}