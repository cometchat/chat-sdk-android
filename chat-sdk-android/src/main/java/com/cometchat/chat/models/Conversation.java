package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.CometChatHelper;
import com.cometchat.chat.core.CometChatUtils;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Conversation implements Parcelable, Cloneable {

    private String conversationId;
    private String conversationType;
    private BaseMessage lastMessage;
    private AppEntity conversationWith;
    private int unreadMessageCount = 0;
    private long updatedAt = 0;
    private List<String> tags;
    private int unreadMentionsCount = 0;
    private long lastReadMessageId;
    private long latestMessageId;

    private Conversation() {}

    public Conversation(@NonNull String conversationId, @CometChatConstants.ConversationTypes String conversationType){
        this.conversationId = conversationId;
        this.conversationType = conversationType;
    }

    protected Conversation(Parcel in) {
        conversationId = in.readString();
        conversationType = in.readString();
        lastMessage = in.readParcelable(BaseMessage.class.getClassLoader());
        if (CometChatConstants.CONVERSATION_TYPE_USER.equals(conversationType)) {
            conversationWith = in.readParcelable(User.class.getClassLoader());
        } else if (CometChatConstants.CONVERSATION_TYPE_GROUP.equals(conversationType)) {
            conversationWith = in.readParcelable(Group.class.getClassLoader());
        }
        unreadMessageCount = in.readInt();
        updatedAt = in.readLong();
        tags = in.createStringArrayList();
        unreadMentionsCount = in.readInt();
        lastReadMessageId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversationId);
        dest.writeString(conversationType);
        dest.writeParcelable(lastMessage, flags);
        if (conversationWith instanceof User) {
            dest.writeParcelable((User) conversationWith, flags);
        } else if (conversationWith instanceof Group) {
            dest.writeParcelable((Group) conversationWith, flags);
        } else {
            dest.writeParcelable(null, flags);
        }
        dest.writeInt(unreadMessageCount);
        dest.writeLong(updatedAt);
        dest.writeStringList(tags);
        dest.writeInt(unreadMentionsCount);
        dest.writeLong(lastReadMessageId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    @Override
    public Conversation clone() {
        try {
            Conversation cloned = (Conversation) super.clone();
            if (this.lastMessage != null) {
                cloned.lastMessage = this.lastMessage.clone();
            }
            if (this.conversationWith != null) {
                cloned.conversationWith = this.conversationWith.clone();
            }
            if (this.tags != null) {
                cloned.tags = new ArrayList<>(this.tags);
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationType() {
        return conversationType;
    }

    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }

    public BaseMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(BaseMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public AppEntity getConversationWith() {
        return conversationWith;
    }

    public void setConversationWith(AppEntity conversationWith) {
        this.conversationWith = conversationWith;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getUnreadMentionsCount() {
        return unreadMentionsCount;
    }

    public void setUnreadMentionsCount(int unreadMentionsCount) {
        this.unreadMentionsCount = unreadMentionsCount;
    }

    public long getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }

    public long getLatestMessageId() {
        return latestMessageId;
    }

    public void setLatestMessageId(long latestMessageId) {
        this.latestMessageId = latestMessageId;
    }

    public static List<Conversation> listFromJsonArray(String response) throws JSONException {
        List<Conversation> conversations = new ArrayList<>();
        JSONObject mainObject = new JSONObject(response);
        if(mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)){
            JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
            for(int i=0; i<dataArray.length();i++){
                Conversation conversation = fromJSON(dataArray.getJSONObject(i));
                conversations.add(conversation);
            }
        }
        return conversations;
    }

    public static Conversation fromJSON(JSONObject conversationObject) throws JSONException {
        Conversation conversation = new Conversation();
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
            conversation.setConversationId(conversationObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_TYPE))
            conversation.setConversationType(conversationObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_TYPE));
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_UPDATED_AT))
            conversation.setUpdatedAt(conversationObject.getLong(CometChatConstants.ConversationKeys.KEY_UPDATED_AT));
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_UNREAD_MESSAGE_COUNT))
            conversation.setUnreadMessageCount(conversationObject.getInt(CometChatConstants.ConversationKeys.KEY_UNREAD_MESSAGE_COUNT));
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_LAST_MESSAGE)) {
            JSONObject messageObject = conversationObject.getJSONObject(CometChatConstants.ConversationKeys.KEY_LAST_MESSAGE);
            if (messageObject.length() != 0) {
                BaseMessage lastMessage = CometChatHelper.processMessage(messageObject);
                conversation.setLastMessage(lastMessage);
            }
        }
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_WITH)) {
            if (conversation.getConversationType().equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER))
                conversation.setConversationWith(User.fromJson(conversationObject.getJSONObject(CometChatConstants.ConversationKeys.KEY_CONVERSATION_WITH).toString()));
            else if (conversation.getConversationType().equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP))
                conversation.setConversationWith(Group.fromJson(conversationObject.getJSONObject(CometChatConstants.ConversationKeys.KEY_CONVERSATION_WITH).toString()));
        }
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_TAGS)) {
            conversation.setTags(CometChatUtils.getListFromJSONArray(conversationObject.getJSONArray(CometChatConstants.ConversationKeys.KEY_TAGS)));
        }
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_UNREAD_MENTIONS_COUNT))
            conversation.setUnreadMentionsCount(conversationObject.getInt(CometChatConstants.ConversationKeys.KEY_UNREAD_MENTIONS_COUNT));
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_LAST_READ_MESSAGE_ID)) {
            conversation.setLastReadMessageId(conversationObject.optLong(CometChatConstants.ConversationKeys.KEY_LAST_READ_MESSAGE_ID, -1));
        }
        if (conversationObject.has(CometChatConstants.ConversationKeys.KEY_LATEST_MESSAGE_ID)) {
            conversation.setLatestMessageId(conversationObject.optLong(CometChatConstants.ConversationKeys.KEY_LATEST_MESSAGE_ID, -1));
        }
        return conversation;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "conversationId='" + conversationId + '\'' +
                ", conversationType='" + conversationType + '\'' +
                ", lastMessage=" + lastMessage +
                ", conversationWith=" + conversationWith +
                ", unreadMessageCount=" + unreadMessageCount +
                ", updatedAt=" + updatedAt +
                ", tags=" + tags +
                ", unreadMentionsCount=" + unreadMentionsCount +
                ", lastReadMessageId='" + lastReadMessageId + '\'' +
                ", latestMessageId='" + latestMessageId + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
    }

    /**
     * Compares this Conversation with another object for content equality.
     * <p>
     * Unlike {@link #equals(Object)} which compares by conversation ID only,
     * this method compares all fields for value equality, including:
     * conversationId, conversationType, lastMessage, conversationWith,
     * unreadMessageCount, updatedAt, tags, unreadMentionsCount, and lastReadMessageId.
     * </p>
     * <p>
     * For nested objects (lastMessage, conversationWith), this method uses their
     * respective contentEquals() methods for deep comparison.
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
        if (!(other instanceof Conversation)) return false;

        // Cast
        Conversation that = (Conversation) other;

        // Compare primitive fields
        if (unreadMessageCount != that.unreadMessageCount) return false;
        if (updatedAt != that.updatedAt) return false;
        if (unreadMentionsCount != that.unreadMentionsCount) return false;

        // Compare String fields using ContentEqualsHelper
        if (!ContentEqualsHelper.stringsEqual(conversationId, that.conversationId)) return false;
        if (!ContentEqualsHelper.stringsEqual(conversationType, that.conversationType)) return false;
        if (lastReadMessageId != that.lastReadMessageId) return false;

        // Compare nested objects using contentEquals
        if (!ContentEqualsHelper.objectsContentEqual(lastMessage, that.lastMessage)) return false;
        if (!ContentEqualsHelper.objectsContentEqual(conversationWith, that.conversationWith)) return false;

        // Compare List field using ContentEqualsHelper
        if (!ContentEqualsHelper.listsEqual(tags, that.tags)) return false;

        return true;
    }
}
