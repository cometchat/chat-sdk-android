package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.core.Call;
import com.cometchat.chat.core.PreferenceHelper;

import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseMessage class provides information about message
 */

public class BaseMessage extends AppEntity {

    public static final String TABLE_CONVERSATIONS = "Conversations";

    protected long id;
    protected String muid;
    protected User sender;
    protected AppEntity receiver;
    protected String receiverUid;
    protected long quotedMessageId;
    protected BaseMessage quotedMessage;

    @CometChatConstants.MessageTypes
    protected String type;

    @CometChatConstants.ReceiverTypes
    protected String receiverType;

    protected String category;
    protected long sentAt;
    protected long deliveredAt;
    protected long readAt;
    protected JSONObject metadata;
    protected long readByMeAt;
    protected long deliveredToMeAt;
    protected long deletedAt;
    protected long editedAt;
    protected String deletedBy;
    protected String editedBy;
    protected long updatedAt;
    protected String conversationId;
    protected long parentMessageId;
    protected int replyCount;
    protected List<User> mentionedUser = new ArrayList<>();
    protected boolean hasMentionedMe;
    protected JSONObject rawMessage;
    protected List<ReactionCount> reactions = new ArrayList<>();

    protected int unreadRepliesCount = 0;

    public BaseMessage(String receiverUid, String type, @CometChatConstants.ReceiverTypes String receiverType) {
        this.receiverUid = receiverUid;
        this.type = type;
        this.receiverType = receiverType;
    }

    public BaseMessage() {

    }

    protected BaseMessage(Parcel in) {
        super(in);
        id = in.readLong();
        muid = in.readString();
        sender = in.readParcelable(User.class.getClassLoader());
        receiverUid = in.readString();
        quotedMessageId = in.readLong();
        type = in.readString();
        receiverType = in.readString();
        category = in.readString();
        sentAt = in.readLong();
        deliveredAt = in.readLong();
        readAt = in.readLong();
        String metadataStr = in.readString();
        if (metadataStr != null) {
            try {
                metadata = new JSONObject(metadataStr);
            } catch (JSONException e) {
                metadata = null;
            }
        }
        readByMeAt = in.readLong();
        deliveredToMeAt = in.readLong();
        deletedAt = in.readLong();
        editedAt = in.readLong();
        deletedBy = in.readString();
        editedBy = in.readString();
        updatedAt = in.readLong();
        conversationId = in.readString();
        parentMessageId = in.readLong();
        replyCount = in.readInt();
        mentionedUser = in.createTypedArrayList(User.CREATOR);
        hasMentionedMe = in.readByte() != 0;
        String rawMessageStr = in.readString();
        if (rawMessageStr != null) {
            try {
                rawMessage = new JSONObject(rawMessageStr);
            } catch (JSONException e) {
                rawMessage = null;
            }
        }
        reactions = in.createTypedArrayList(ReactionCount.CREATOR);
        unreadRepliesCount = in.readInt();
        // Read receiver based on receiverType
        if (CometChatConstants.RECEIVER_TYPE_USER.equals(receiverType)) {
            receiver = in.readParcelable(User.class.getClassLoader());
        } else if (CometChatConstants.RECEIVER_TYPE_GROUP.equals(receiverType)) {
            receiver = in.readParcelable(Group.class.getClassLoader());
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(id);
        dest.writeString(muid);
        dest.writeParcelable(sender, flags);
        dest.writeString(receiverUid);
        dest.writeLong(quotedMessageId);
        dest.writeString(type);
        dest.writeString(receiverType);
        dest.writeString(category);
        dest.writeLong(sentAt);
        dest.writeLong(deliveredAt);
        dest.writeLong(readAt);
        dest.writeString(metadata != null ? metadata.toString() : null);
        dest.writeLong(readByMeAt);
        dest.writeLong(deliveredToMeAt);
        dest.writeLong(deletedAt);
        dest.writeLong(editedAt);
        dest.writeString(deletedBy);
        dest.writeString(editedBy);
        dest.writeLong(updatedAt);
        dest.writeString(conversationId);
        dest.writeLong(parentMessageId);
        dest.writeInt(replyCount);
        dest.writeTypedList(mentionedUser);
        dest.writeByte((byte) (hasMentionedMe ? 1 : 0));
        dest.writeString(rawMessage != null ? rawMessage.toString() : null);
        dest.writeTypedList(reactions);
        dest.writeInt(unreadRepliesCount);
        // Write receiver based on type
        if (receiver instanceof User) {
            dest.writeParcelable((User) receiver, flags);
        } else if (receiver instanceof Group) {
            dest.writeParcelable((Group) receiver, flags);
        } else {
            dest.writeParcelable(null, flags);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseMessage> CREATOR = new Creator<BaseMessage>() {
        @Override
        public BaseMessage createFromParcel(Parcel in) {
            return new BaseMessage(in);
        }

        @Override
        public BaseMessage[] newArray(int size) {
            return new BaseMessage[size];
        }
    };

    @Override
    public BaseMessage clone() {
        try {
            BaseMessage cloned = (BaseMessage) super.clone();
            if (this.sender != null) {
                cloned.sender = this.sender.clone();
            }
            if (this.receiver != null) {
                cloned.receiver = this.receiver.clone();
            }
            if (this.metadata != null) {
                cloned.metadata = new JSONObject(this.metadata.toString());
            }
            if (this.rawMessage != null) {
                cloned.rawMessage = new JSONObject(this.rawMessage.toString());
            }
            if (this.mentionedUser != null) {
                cloned.mentionedUser = new ArrayList<>();
                for (User user : this.mentionedUser) {
                    cloned.mentionedUser.add(user.clone());
                }
            }
            if (this.reactions != null) {
                cloned.reactions = new ArrayList<>();
                for (ReactionCount rc : this.reactions) {
                    cloned.reactions.add(rc.clone());
                }
            }
            if (this.quotedMessage != null) {
                cloned.quotedMessage = this.quotedMessage.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException | JSONException e) {
            return null;
        }
    }

    /**
     * Get id of the message
     *
     * @return unique id of the message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get muid of Message
     * Additional id field for the developers in case they want to use.
     *
     * @return muid set by developer
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    /**
     * Get sender object
     *
     * @return An object of <code>User</code> class
     * @version <b>v2</b>
     * @see User
     * @since <b>v1</b>
     */
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Get id of the receiver
     *
     * @return id of the receiver(user/group)
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    /**
     * Get quoted message object
     *
     * @return An object of <code>BaseMessage</code> class
     * @see BaseMessage
     * @since <b>v4.1.7</b>
     */
    public long getQuotedMessageId() {
        return quotedMessageId;
    }

    public void setQuotedMessageId(long quotedMessageId) {
        this.quotedMessageId = quotedMessageId;
    }

    /**
     * Get quoted message object
     *
     * @return An object of <code>BaseMessage</code> class
     * @see BaseMessage
     * @since <b>v4.1.7</b>
     */
    public BaseMessage getQuotedMessage() {
        return quotedMessage;
    }

    public void setQuotedMessage(BaseMessage quotedMessage) {
        this.quotedMessage = quotedMessage;
    }

    /**
     * Get type of the message
     *
     * @return type of the message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getType() {
        return type;
    }

    public void setType(@CometChatConstants.MessageTypes String type) {
        this.type = type;
    }

    /**
     * Get type of receiver
     *
     * @return type of the receiver
     * @version <b>v2</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v1</b>
     */
    @CometChatConstants.ReceiverTypes
    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(@CometChatConstants.ReceiverTypes String receiverType) {
        this.receiverType = receiverType;
    }

    /**
     * Get message's sent at timestamp
     *
     * @return message sent timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    /**
     * Get of the category of message
     *
     * @return category of the message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Get delivery timestamp of the message
     *
     * @return delivery timestamp of message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    /**
     * Get Timestamp of the when message was read at
     *
     * @return Timestamp of the time the message was read at
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     * <b>Note</b>
     * In case of group this field is set only when at message is read by all  the member of the group
     */
    public long getReadAt() {
        return readAt;
    }

    public void setReadAt(long readAt) {
        this.readAt = readAt;
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
     * Get timestamp when the message is read by logged in user
     *
     * @return read at of the message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getReadByMeAt() {
        return readByMeAt;
    }

    public void setReadByMeAt(long readByMeAt) {
        this.readByMeAt = readByMeAt;
    }

    /**
     * Get timestamp of the message at which it was delivered to logged in user
     *
     * @return delivery timestamp of the message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getDeliveredToMeAt() {
        return deliveredToMeAt;
    }

    public void setDeliveredToMeAt(long deliveredToMeAt) {
        this.deliveredToMeAt = deliveredToMeAt;
    }

    /**
     * Get timestamp of the message when it was deleted
     *
     * @return deleted at timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(long deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     * Get timestamp of the message when it was updated/edited
     *
     * @return edited/updated at timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(long editedAt) {
        this.editedAt = editedAt;
    }

    /**
     * Get UID of the user who deleted the message
     *
     * @return UID of the user
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    /**
     * Get UID of the user who edited/updated the message
     *
     * @return UID of the user
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }

    /**
     * Get unread reply count
     *
     * @return Unread count of replies
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public int getUnreadRepliesCount() {
        return unreadRepliesCount;
    }

    public void setUnreadRepliesCount(int unreadRepliesCount) {
        this.unreadRepliesCount = unreadRepliesCount;
    }

    /**
     * Get timestamp of the message when it was updated/edited
     *
     * @return updated/edited at timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the id of the Conversation.
     *
     * @return conversationId
     * @version <b>v2</b>
     * @since <b>v2</b>
     */
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * Returns User/Group object with the details of the receiver of the message.
     *
     * @return User/Group object with the details of the receiever of the message.
     * @version <b>v2</b>
     * @since <b>v2</b>
     */
    public AppEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(AppEntity receiver) {
        this.receiver = receiver;
    }

    /**
     * Returns the parent message id for the message showing that the message belongs to a thread.
     *
     * @return an int which is the parent message id for the message showing that the message belongs to a thread.
     * @version <b>v2</b>
     * @since <b>v2</b>
     */
    public long getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(long parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    /**
     * Returns the count of the messages that belong to the thread for which the message is the parent.
     *
     * @return an int which is the count of the messages that belong to the thread for which the message is the parent.
     * @version <b>v2</b>
     * @since <b>v2</b>
     */
    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    /**
     * Returns the raw JSON message for the message object.
     *
     * @return A JSONObject which holds the raw data of the message..
     * @version <b>v2</b>
     * @since <b>v2</b>
     */
    public JSONObject getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(JSONObject rawMessage) {
        this.rawMessage = rawMessage;
    }

    /**
     * Returns the reactions list.
     *
     * @return List of reactions..
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public List<ReactionCount> getReactions() {
        return reactions;
    }

    public void setReactions(List<ReactionCount> reactions) {
        this.reactions = reactions;
    }

    /**
     * Returns the user list of mentioned users int the message.
     *
     * @return A user list who mentioned in the message.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public List<User> getMentionedUsers() {
        return mentionedUser;
    }

    public void setMentionedUsers(List<User> mentionedUser) {
        this.mentionedUser = mentionedUser;
    }

    /**
     * Returns the boolean flag if current logged in user is present in mentioned user list.
     *
     * @return Returns the boolean flag when user is mentioned in message.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public boolean hasMentionedMe() {
        return hasMentionedMe;
    }

    public void setHasMentionedMe(boolean hasMentionedMe) {
        this.hasMentionedMe = hasMentionedMe;
    }

    public static List<BaseMessage> getMessagesFromJSON(String json) throws JSONException {
        List<BaseMessage> messages = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
            JSONArray messagesArray = jsonObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
            for (int i = 0; i < messagesArray.length(); i++) {
                JSONObject messageObject = messagesArray.getJSONObject(i);
                BaseMessage baseMessage = processMessage(messageObject);
                if (baseMessage != null) {
                    if (baseMessage.getId() > PreferenceHelper.getLastDeliveredMessageId()) {
                        PreferenceHelper.saveLastDeliveredMessageId(baseMessage.getId());
                    }
                    messages.add(baseMessage);
                } else {
                    Logger.error("Category missing in JSON");
                }
            }
        }
        return messages;
    }

    public static BaseMessage processMessage(JSONObject messageObject) throws JSONException {
        if (messageObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY)) {
            String category = messageObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY);
            if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_MESSAGE)) {
                if (messageObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE)) {
                    String type = messageObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE);
                    if (type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                        return TextMessage.fromJson(messageObject);
                    } else if (type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_CUSTOM)) {
                        return CustomMessage.fromJson(messageObject);
                    } else {
                        return MediaMessage.fromJson(messageObject);
                    }
                }
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_ACTION)) {
                return Action.fromJson(messageObject);
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CALL)) {
                return Call.fromJson(messageObject.toString());
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM)) {
                return CustomMessage.fromJson(messageObject);
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_INTERACTIVE)) {
                return InteractiveMessage.fromJson(messageObject);
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CARD)) {
                return CardMessage.fromJson(messageObject);
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_AGENTIC)) {
                if (messageObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE)) {
                    String type = messageObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE);
                    if (CometChatConstants.MESSAGE_TYPE_ASSISTANT.equalsIgnoreCase(type)) {
                        return AIAssistantMessage.fromJson(messageObject);
                    } else if (CometChatConstants.MESSAGE_TYPE_TOOL_ARGUMENTS.equalsIgnoreCase(type)) {
                        return AIToolArgumentMessage.fromJson(messageObject);
                    } else if (CometChatConstants.MESSAGE_TYPE_TOOL_RESULT.equalsIgnoreCase(type)) {
                        return AIToolResultMessage.fromJson(messageObject);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
            "id=" + id +
            ", muid='" + muid + '\'' +
            ", sender=" + sender +
            ", receiver=" + receiver +
            ", receiverUid='" + receiverUid + '\'' +
            ", type='" + type + '\'' +
            ", receiverType='" + receiverType + '\'' +
            ", category='" + category + '\'' +
            ", sentAt=" + sentAt +
            ", deliveredAt=" + deliveredAt +
            ", readAt=" + readAt +
            ", metadata=" + metadata +
            ", readByMeAt=" + readByMeAt +
            ", deliveredToMeAt=" + deliveredToMeAt +
            ", deletedAt=" + deletedAt +
            ", editedAt=" + editedAt +
            ", deletedBy='" + deletedBy + '\'' +
            ", editedBy='" + editedBy + '\'' +
            ", updatedAt=" + updatedAt +
            ", conversationId='" + conversationId + '\'' +
            ", parentMessageId=" + parentMessageId +
            ", replyCount=" + replyCount +
            ", mentionedUser=" + mentionedUser +
            ", hasMentionedMe=" + hasMentionedMe +
            ", rawMessage=" + rawMessage +
            ", reactions=" + reactions +
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
        if (!(other instanceof BaseMessage)) return false;

        // 4. Call parent's contentEquals (AppEntity)
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        BaseMessage that = (BaseMessage) other;

        // 6. Compare all fields

        // Primitive fields (long, int, boolean) - use ==
        if (id != that.id) return false;
        if (quotedMessageId != that.quotedMessageId) return false;
        if (sentAt != that.sentAt) return false;
        if (deliveredAt != that.deliveredAt) return false;
        if (readAt != that.readAt) return false;
        if (readByMeAt != that.readByMeAt) return false;
        if (deliveredToMeAt != that.deliveredToMeAt) return false;
        if (deletedAt != that.deletedAt) return false;
        if (editedAt != that.editedAt) return false;
        if (updatedAt != that.updatedAt) return false;
        if (parentMessageId != that.parentMessageId) return false;
        if (replyCount != that.replyCount) return false;
        if (hasMentionedMe != that.hasMentionedMe) return false;
        if (unreadRepliesCount != that.unreadRepliesCount) return false;

        // String fields - use ContentEqualsHelper.stringsEqual()
        if (!ContentEqualsHelper.stringsEqual(muid, that.muid)) return false;
        if (!ContentEqualsHelper.stringsEqual(receiverUid, that.receiverUid)) return false;
        if (!ContentEqualsHelper.stringsEqual(type, that.type)) return false;
        if (!ContentEqualsHelper.stringsEqual(receiverType, that.receiverType)) return false;
        if (!ContentEqualsHelper.stringsEqual(category, that.category)) return false;
        if (!ContentEqualsHelper.stringsEqual(deletedBy, that.deletedBy)) return false;
        if (!ContentEqualsHelper.stringsEqual(editedBy, that.editedBy)) return false;
        if (!ContentEqualsHelper.stringsEqual(conversationId, that.conversationId)) return false;

        // Object fields - use ContentEqualsHelper.objectsContentEqual()
        if (!ContentEqualsHelper.objectsContentEqual(sender, that.sender)) return false;
        if (!ContentEqualsHelper.objectsContentEqual(receiver, that.receiver)) return false;
        if (!ContentEqualsHelper.objectsContentEqual(quotedMessage, that.quotedMessage)) return false;

        // JSONObject fields - use ContentEqualsHelper.jsonObjectsEqual()
        if (!ContentEqualsHelper.jsonObjectsEqual(metadata, that.metadata)) return false;
        if (!ContentEqualsHelper.jsonObjectsEqual(rawMessage, that.rawMessage)) return false;

        // List fields - use ContentEqualsHelper.listsEqual()
        if (!ContentEqualsHelper.listsEqual(mentionedUser, that.mentionedUser)) return false;
        if (!ContentEqualsHelper.listsEqual(reactions, that.reactions)) return false;

        return true;
    }
}
