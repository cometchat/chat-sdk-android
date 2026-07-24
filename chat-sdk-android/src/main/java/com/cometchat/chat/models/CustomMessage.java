package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
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
 * Created by adityagokula on 12/03/19.
 * Modified by Rohit Giri on 11/01/24.
 *
 */

public class CustomMessage extends BaseMessage {

    private String subType;
    private JSONObject customData;
    private List<String> tags;
    private String text;
    private boolean updateConversation = false;
    private boolean sendNotification = false;

    public CustomMessage(String receiverUid, @CometChatConstants.ReceiverTypes String receiverType, String customType, @NonNull JSONObject customData) {
        super(receiverUid, customType, receiverType);
        setCategory(CometChatConstants.CATEGORY_CUSTOM);
        this.customData = customData;
    }

    private CustomMessage() {}

    protected CustomMessage(Parcel in) {
        super(in);
        subType = in.readString();
        String customDataStr = in.readString();
        if (customDataStr != null) {
            try {
                customData = new JSONObject(customDataStr);
            } catch (JSONException e) {
                customData = null;
            }
        }
        tags = in.createStringArrayList();
        text = in.readString();
        updateConversation = in.readByte() != 0;
        sendNotification = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(subType);
        dest.writeString(customData != null ? customData.toString() : null);
        dest.writeStringList(tags);
        dest.writeString(text);
        dest.writeByte((byte) (updateConversation ? 1 : 0));
        dest.writeByte((byte) (sendNotification ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomMessage> CREATOR = new Creator<CustomMessage>() {
        @Override
        public CustomMessage createFromParcel(Parcel in) {
            return new CustomMessage(in);
        }

        @Override
        public CustomMessage[] newArray(int size) {
            return new CustomMessage[size];
        }
    };

    @Override
    public CustomMessage clone() {
        try {
            CustomMessage cloned = (CustomMessage) super.clone();
            if (this.customData != null) {
                cloned.customData = new JSONObject(this.customData.toString());
            }
            if (this.tags != null) {
                cloned.tags = new ArrayList<>(this.tags);
            }
            return cloned;
        } catch (JSONException e) {
            return null;
        }
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    /**
     * Get<code>JSONObject</code> of the custom
     *
     * @return <code>JSONObject</code> of the custom data set by developer
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public JSONObject getCustomData() {
        return customData;
    }

    public void setCustomData(JSONObject customData) {
        this.customData = customData;
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
     * Retrieves the preview text for a custom message to be displayed in the Conversation List.
     * This method allows for a textual representation of the custom message that can be used
     * as a conversation snippet or preview, enhancing the user experience by providing context.
     *
     * @return A String containing the preview text for the custom message.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public String getConversationText() {
        return text;
    }

    /**
     *  Set the text for a conversation
     *
     * @param text The text to be set for the conversation
     * @version <b>v4</b>
     * @since <b>v4</b>
     *
     */
    public void setConversationText(String text) {
        this.text = text;
    }

    /**
     * Determines whether the custom message should be considered as the last message
     * in the conversation, potentially changing the order of conversations.
     * This method indicates if sending a custom message will update the conversation's last message,
     * which may affect the conversation's position in the list based on the sorting order.
     *
     * @return A boolean value indicating whether sending the custom message triggers an update
     * to the last message of the conversation, potentially altering the conversation order.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public boolean willUpdateConversation() {
        return updateConversation;
    }

    /**
     *  Set the flag to determine if the conversation should be updated
     *
     * @param updateConversation The flag to set if the conversation should be updated
     * @version <b>v4</b>
     * @since <b>v4</b>
     *
     */
    public void shouldUpdateConversation(boolean updateConversation) {
        this.updateConversation = updateConversation;
    }

    /**
     * Determines whether a push notification should be sent for the custom message.
     * This method checks if the current custom message configuration specifies that a push notification
     * is warranted when the message is sent.
     *
     * @return A boolean value indicating whether to send a push notification for the custom message.
     * @version <b>v4</b>
     * @since <b>v4</b>
     */
    public boolean willSendNotification() {
        return sendNotification;
    }

    /**
     *  Set the flag to determine if a notification should be sent
     *
     * @param sendNotification The flag to set if a notification should be sent
     * @version <b>v4</b>
     * @since <b>v4</b>
     *
     */
    public void shouldSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, this.getReceiverType());
        if (this.getType() != null)
            map.put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, this.getType());
        if (this.getReceiverUid() != null)
            map.put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, this.getReceiverUid());
        map.put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, getCategory());
        if (this.getMuid() != null)
            map.put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID, this.getMuid());
        if(this.getParentMessageId() >0)
            map.put(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID, String.valueOf(this.getParentMessageId()));
        JSONObject dataObject = new JSONObject();
        try {
            if (this.getSubType() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_CUSTOM_SUB_TYPE, this.getSubType());
            }
            if (this.getCustomData() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_CUSTOM_CUSTOM_DATA, this.getCustomData());
            }
            if (this.getMetadata() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA, this.getMetadata());
            }
            if(this.tags!=null){
                JSONArray tagsArray = CometChatUtils.getJSONArrayFromList(this.tags);
                map.put(CometChatConstants.MessageKeys.KEY_TAGS, tagsArray.toString());
            }
            if(this.getConversationText() != null && !this.getConversationText().isEmpty()) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_CUSTOM_TEXT, this.getConversationText());
            }
            if(this.willUpdateConversation()) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_CUSTOM_UPDATE_CONVERSATION, this.willUpdateConversation());
            }
            if(this.willSendNotification()) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_CUSTOM_SEND_NOTIFICATION, this.willSendNotification());
            }
            if (this.getQuotedMessageId() != 0) {
                map.put(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE_ID, String.valueOf(this.getQuotedMessageId()));
            }
        } catch (JSONException je) {
            Logger.error("Error creating JSON : " + je.getMessage());
        }
        map.put(CometChatConstants.ResponseKeys.KEY_DATA, dataObject.toString());
        return map;

    }

    public static CustomMessage fromJson(JSONObject jsonObject) {
        CustomMessage customMessage = new CustomMessage();
        try {
            customMessage.setRawMessage(jsonObject);
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                customMessage.setId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
                customMessage.setConversationId(jsonObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
                customMessage.setParentMessageId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
                customMessage.setReplyCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT))
                customMessage.setUnreadRepliesCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
                customMessage.setMuid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                customMessage.setReceiverUid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE)) {
                customMessage.setReceiverType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
                customMessage.setCategory(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE))
                customMessage.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT)) {
                customMessage.setSentAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT)) {
                customMessage.setUpdatedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT)) {
                customMessage.setDeliveredAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT)) {
                customMessage.setReadAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT)) {
                customMessage.setEditedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY)) {
                customMessage.setEditedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT)) {
                customMessage.setDeletedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY)) {
                customMessage.setDeletedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                    JSONObject entitiesObject = new JSONObject(dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES).toString());
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_SENDER)) {
                        JSONObject senderObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SENDER);
                        User user = User.fromJson(senderObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                        customMessage.setSender(user);
                    }
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID)) {
                        JSONObject receiverObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID);
                        String entityType = receiverObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE);
                        if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            User user = User.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            customMessage.setReceiver(user);
                        } else if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                            Group group = Group.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            customMessage.setReceiver(group);
                        }
                    }
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_CUSTOM_SUB_TYPE)) {
                    customMessage.setSubType(dataObject.getString(CometChatConstants.MessageKeys.KEY_CUSTOM_SUB_TYPE));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_CUSTOM_CUSTOM_DATA)) {
                    customMessage.setCustomData(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_CUSTOM_CUSTOM_DATA));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA)) {
                    customMessage.setMetadata(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA));
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
                    customMessage.setMentionedUsers(mentionedUsersList);
                    customMessage.setHasMentionedMe(isMentionedMe);
                }
                //Reactions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_REACTIONS)){
                    JSONArray reactionCountObj = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_REACTIONS);
                    List<ReactionCount> reactionCountList = ReactionCount.listFromJSONArray(reactionCountObj);
                    customMessage.setReactions(reactionCountList);
                }
                //Optional Properties
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_CUSTOM_TEXT)) {
                    customMessage.setConversationText(dataObject.getString(CometChatConstants.MessageKeys.KEY_CUSTOM_TEXT));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_CUSTOM_UPDATE_CONVERSATION)) {
                    customMessage.shouldUpdateConversation(dataObject.getBoolean(CometChatConstants.MessageKeys.KEY_CUSTOM_UPDATE_CONVERSATION));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_CUSTOM_SEND_NOTIFICATION)) {
                    customMessage.shouldSendNotification(dataObject.getBoolean(CometChatConstants.MessageKeys.KEY_CUSTOM_SEND_NOTIFICATION));
                }
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
                JSONObject receiptsObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                    customMessage.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                    customMessage.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if(jsonObject.has(CometChatConstants.MessageKeys.KEY_TAGS)){
                customMessage.setTags(CometChatUtils.getListFromJSONArray(jsonObject.getJSONArray(CometChatConstants.MessageKeys.KEY_TAGS)));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE)) {
                BaseMessage quotedMessage = BaseMessage.processMessage(jsonObject.getJSONObject(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE));
                customMessage.setQuotedMessage(quotedMessage);
                customMessage.setQuotedMessageId(quotedMessage != null ? quotedMessage.getId() : 0);
            }
        } catch (JSONException je) {
            Logger.error(je.toString());
        }
        return customMessage;
    }

    @Override
    public String toString() {
        return "CustomMessage{" +
                "subType='" + subType + '\'' +
                ", customData=" + customData +
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
                ", text=" + text +
                ", updateConversation=" + updateConversation +
                ", sendNotification=" + sendNotification +
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
        if (!(other instanceof CustomMessage)) return false;

        // 4. Call parent's contentEquals (BaseMessage)
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        CustomMessage that = (CustomMessage) other;

        // 6. Compare all CustomMessage-specific fields

        // String fields - use ContentEqualsHelper.stringsEqual()
        if (!ContentEqualsHelper.stringsEqual(subType, that.subType)) return false;
        if (!ContentEqualsHelper.stringsEqual(text, that.text)) return false;

        // JSONObject field - use ContentEqualsHelper.jsonObjectsEqual()
        if (!ContentEqualsHelper.jsonObjectsEqual(customData, that.customData)) return false;

        // List field - use ContentEqualsHelper.listsEqual()
        if (!ContentEqualsHelper.listsEqual(tags, that.tags)) return false;

        // Boolean fields - use == operator
        if (updateConversation != that.updateConversation) return false;
        if (sendNotification != that.sendNotification) return false;

        return true;
    }
}
