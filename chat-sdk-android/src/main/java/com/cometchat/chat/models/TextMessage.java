package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.enums.ModerationStatus;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.core.CometChatUtils;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by adityagokula on 08/09/18.
 */

public class TextMessage extends BaseMessage {

    private String text;
    protected List<String> tags;
    private ModerationStatus moderationStatus;

    public TextMessage(@NonNull String receiverUid, @NonNull String text, @CometChatConstants.ReceiverTypes String receiverType) {
        super(receiverUid, CometChatConstants.MESSAGE_TYPE_TEXT, receiverType);
        setCategory(CometChatConstants.CATEGORY_MESSAGE);
        this.text = text;
    }

    private TextMessage() {}

    protected TextMessage(Parcel in) {
        super(in);
        text = in.readString();
        tags = in.createStringArrayList();
        String moderationStatusStr = in.readString();
        if (moderationStatusStr != null) {
            moderationStatus = ModerationStatus.get(moderationStatusStr);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(text);
        dest.writeStringList(tags);
        dest.writeString(moderationStatus != null ? moderationStatus.getValue() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TextMessage> CREATOR = new Creator<TextMessage>() {
        @Override
        public TextMessage createFromParcel(Parcel in) {
            return new TextMessage(in);
        }

        @Override
        public TextMessage[] newArray(int size) {
            return new TextMessage[size];
        }
    };

    @Override
    public TextMessage clone() {
        TextMessage cloned = (TextMessage) super.clone();
        if (cloned != null && this.tags != null) {
            cloned.tags = new ArrayList<>(this.tags);
        }
        return cloned;
    }

    /**
     * Get text set by developer while sending a text message
     *
     * @return text message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     *  Returns the list of tags that the message has been tagged with.
     * @return List<String> that holds the tags with which the message was tagged with.
     * @version <b>v3</b>
     * @since <b>v3</b>
     */
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Gets the moderation status of the message.
     * @return <b>ModerationStatus</b> enum that holds the moderation status of the message.
     * @version <b>v3</b>
     * @since <b>v3</b>
     */
    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    /**
     * Sets the moderation status of the message.
     * @version <b>v3</b>
     * @since <b>v3</b>
     * @param status
     */
    public void setModerationStatus(ModerationStatus status){
        this.moderationStatus = status;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, this.getReceiverType());
        if (this.getType() != null)
            map.put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, this.getType());
        if (this.getReceiverUid() != null)
            map.put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, this.getReceiverUid());
        map.put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, CometChatConstants.CATEGORY_MESSAGE);
        if (this.getType() != null)
            map.put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, this.getType());
        if (this.getMuid() != null)
            map.put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID, this.getMuid());
        if(this.getParentMessageId() >0)
            map.put(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID, String.valueOf(this.getParentMessageId()));
        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, this.getText());
            if (this.getMetadata() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA, this.getMetadata());
            }
        } catch (JSONException je) {
            Logger.error("JSON exception : " + je.getMessage());
        }
        Logger.error("Data Object : " + dataObject.toString());
        if (this.text != null)
            map.put(CometChatConstants.ResponseKeys.KEY_DATA, dataObject.toString());
        if(this.tags!=null) {
            JSONArray tagsArray = CometChatUtils.getJSONArrayFromList(this.tags);
            map.put(CometChatConstants.MessageKeys.KEY_TAGS, tagsArray.toString());
        }
        if (this.getQuotedMessageId() != 0)
            map.put(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE_ID, String.valueOf(this.getQuotedMessageId()));
        return map;
    }

    public static TextMessage fromJson(JSONObject jsonObject) {
        TextMessage textMessage = new TextMessage();
        try {
            textMessage.setRawMessage(jsonObject);
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                textMessage.setId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
                textMessage.setConversationId(jsonObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
                textMessage.setParentMessageId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
                textMessage.setReplyCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT))
                textMessage.setUnreadRepliesCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                textMessage.setReceiverUid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
                textMessage.setMuid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE))
                textMessage.setReceiverType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
                textMessage.setCategory(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE))
                textMessage.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT))
                textMessage.setSentAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT))
                textMessage.setUpdatedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                textMessage.setDeliveredAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                textMessage.setReadAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT))
                textMessage.setEditedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY))
                textMessage.setEditedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT))
                textMessage.setDeletedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY))
                textMessage.setDeletedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT)) {
                    textMessage.setText(dataObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT));
                }
                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                    JSONObject entitiesObject = new JSONObject(dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES).toString());
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_SENDER)) {
                        JSONObject senderObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SENDER);
                        User user = User.fromJson(senderObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                        textMessage.setSender(user);
                    }
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID)) {
                        JSONObject receiverObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID);
                        String entityType = receiverObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE);
                        if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            User user = User.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            textMessage.setReceiver(user);
                        } else if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                            Group group = Group.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            textMessage.setReceiver(group);
                        }
                    }
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA)) {
                    textMessage.setMetadata(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA));
                }
                // Mentions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_MENTIONS)){
                    List<User> mentionedUsersList = new ArrayList<>();
                    boolean isMentionedMe = false;
                    User loggedInUserObj = CometChat.getLoggedInUser();
                    JSONObject userObj = dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_MENTIONS);
                    Iterator<String> keys = userObj.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        String value = userObj.getString(key);
                        User user = User.fromJson(value);
                        mentionedUsersList.add(user);
                        if (key.toString().equals(loggedInUserObj.getUid())){
                            isMentionedMe = true;
                        }
                    }
                    textMessage.setMentionedUsers(mentionedUsersList);
                    textMessage.setHasMentionedMe(isMentionedMe);
                }
                //Reactions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_REACTIONS)){
                    JSONArray reactionCountObj = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_REACTIONS);
                    List<ReactionCount> reactionCountList = ReactionCount.listFromJSONArray(reactionCountObj);
                    textMessage.setReactions(reactionCountList);
                }
                // Moderation Status
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_MODERATION)) {
                    JSONObject moderationObject = dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_MODERATION);
                    if (moderationObject.has(CometChatConstants.MessageKeys.KEY_MODERATION_STATUS)) {
                        String statusStr = moderationObject.getString(CometChatConstants.MessageKeys.KEY_MODERATION_STATUS);
                        ModerationStatus status = ModerationStatus.get(statusStr);
                        textMessage.moderationStatus = (status != null) ? status : ModerationStatus.UNMODERATED;
                    } else {
                        textMessage.moderationStatus = ModerationStatus.UNMODERATED;
                    }
                } else {
                    textMessage.moderationStatus = ModerationStatus.UNMODERATED;
                }
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
                JSONObject receiptsObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                    textMessage.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                    textMessage.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if(jsonObject.has(CometChatConstants.MessageKeys.KEY_TAGS)) {
                textMessage.setTags(CometChatUtils.getListFromJSONArray(jsonObject.getJSONArray(CometChatConstants.MessageKeys.KEY_TAGS)));
            }
            if(jsonObject.has(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE)) {
                BaseMessage quotedMessage = BaseMessage.processMessage(jsonObject.getJSONObject(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE));
                textMessage.setQuotedMessage(quotedMessage);
                textMessage.setQuotedMessageId(quotedMessage != null ? quotedMessage.getId() : 0);
            }
        } catch (JSONException je) {
            Logger.error("Messages from json exception : " + je.getMessage());
        }
        return textMessage;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "text='" + text + '\'' +
                ", id=" + id +
                ", muid='" + muid + '\'' +
                ", sender=" + sender +
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
        if (!(other instanceof TextMessage)) return false;

        // 4. Call parent's contentEquals (BaseMessage)
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        TextMessage that = (TextMessage) other;

        // 6. Compare all TextMessage-specific fields

        // String field - use ContentEqualsHelper.stringsEqual()
        if (!ContentEqualsHelper.stringsEqual(text, that.text)) return false;

        // List field - use ContentEqualsHelper.listsEqual()
        if (!ContentEqualsHelper.listsEqual(tags, that.tags)) return false;

        // Enum field - use == operator
        if (moderationStatus != that.moderationStatus) return false;

        return true;
    }
}
