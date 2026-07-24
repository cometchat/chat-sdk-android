package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChatUtils;
import com.cometchat.chat.helpers.Logger;

import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents an InteractiveMessage within a chat conversation.
 * InteractiveMessage, a specialized subclass of BaseMessage, contains interactive
 * data, tags, an interaction goal, and a list of interactions performed by users.
 * It also includes a flag to specify whether the sender of the message can interact
 * with the message. It provides methods to manage and operate on this data.
 */
public class InteractiveMessage extends BaseMessage {

    private JSONObject interactiveData;
    private List<String> tags;
    private InteractionGoal interactionGoal;
    private List<Interaction> interactions;
    private boolean allowSenderInteraction;

    /**
     * Creates a new InteractiveMessage instance.
     *
     * @param receiverUid The unique identifier of the message receiver.
     * @param receiverType The type of the receiver (User or Group).
     * @param interactiveType The type of interactive message.
     * @param interactiveData The interactive content contained in the message.
     */
    public InteractiveMessage(String receiverUid, @CometChatConstants.ReceiverTypes String receiverType, String interactiveType, @NonNull JSONObject interactiveData) {
        super(receiverUid, interactiveType, receiverType);
        setCategory(CometChatConstants.CATEGORY_INTERACTIVE);
        setInteractiveData(interactiveData);
        setInteractionGoal(new InteractionGoal(CometChatConstants.INTERACTION_TYPE_NONE, new ArrayList<String>()));
    }

    private InteractiveMessage() {
    }

    protected InteractiveMessage(Parcel in) {
        super(in);
        String interactiveDataStr = in.readString();
        if (interactiveDataStr != null) {
            try {
                interactiveData = new JSONObject(interactiveDataStr);
            } catch (JSONException e) {
                interactiveData = null;
            }
        }
        tags = in.createStringArrayList();
        interactionGoal = in.readParcelable(InteractionGoal.class.getClassLoader());
        interactions = in.createTypedArrayList(Interaction.CREATOR);
        allowSenderInteraction = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(interactiveData != null ? interactiveData.toString() : null);
        dest.writeStringList(tags);
        dest.writeParcelable(interactionGoal, flags);
        dest.writeTypedList(interactions);
        dest.writeByte((byte) (allowSenderInteraction ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InteractiveMessage> CREATOR = new Creator<InteractiveMessage>() {
        @Override
        public InteractiveMessage createFromParcel(Parcel in) {
            return new InteractiveMessage(in);
        }

        @Override
        public InteractiveMessage[] newArray(int size) {
            return new InteractiveMessage[size];
        }
    };

    @Override
    public InteractiveMessage clone() {
        try {
            InteractiveMessage cloned = (InteractiveMessage) super.clone();
            if (this.interactiveData != null) {
                cloned.interactiveData = new JSONObject(this.interactiveData.toString());
            }
            if (this.tags != null) {
                cloned.tags = new ArrayList<>(this.tags);
            }
            if (this.interactionGoal != null) {
                cloned.interactionGoal = this.interactionGoal.clone();
            }
            if (this.interactions != null) {
                cloned.interactions = new ArrayList<>();
                for (Interaction interaction : this.interactions) {
                    cloned.interactions.add(interaction.clone());
                }
            }
            return cloned;
        } catch (JSONException e) {
            return null;
        }
    }

    public void setInteractiveData(JSONObject interactiveData) {
        this.interactiveData = interactiveData;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setInteractionGoal(InteractionGoal interactionGoal) {
        this.interactionGoal = interactionGoal;
    }

    public void setAllowSenderInteraction(boolean allowSenderInteraction) {
        this.allowSenderInteraction = allowSenderInteraction;
    }

    public void setInteractions(List<Interaction> interactions) {
        this.interactions = interactions;
    }

    public boolean isAllowSenderInteraction() {
        return allowSenderInteraction;
    }

    public JSONObject getInteractiveData() {
        return interactiveData;
    }

    public List<String> getTags() {
        return tags;
    }

    public InteractionGoal getInteractionGoal() {
        return interactionGoal;
    }

    public List<Interaction> getInteractions() {
        return interactions;
    }

    public static InteractiveMessage fromJson(JSONObject jsonObject) {
        InteractiveMessage interactiveMessage = new InteractiveMessage();
        try {
            interactiveMessage.setRawMessage(jsonObject);
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                interactiveMessage.setId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
                interactiveMessage.setConversationId(jsonObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
                interactiveMessage.setParentMessageId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
                interactiveMessage.setReplyCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT))
                interactiveMessage.setUnreadRepliesCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
                interactiveMessage.setMuid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                interactiveMessage.setReceiverUid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE)) {
                interactiveMessage.setReceiverType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
                interactiveMessage.setCategory(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE))
                interactiveMessage.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT)) {
                interactiveMessage.setSentAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT)) {
                interactiveMessage.setUpdatedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT)) {
                interactiveMessage.setDeliveredAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT)) {
                interactiveMessage.setReadAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT)) {
                interactiveMessage.setEditedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY)) {
                interactiveMessage.setEditedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT)) {
                interactiveMessage.setDeletedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY)) {
                interactiveMessage.setDeletedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                    JSONObject entitiesObject = new JSONObject(dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES).toString());
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_SENDER)) {
                        JSONObject senderObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SENDER);
                        User user = User.fromJson(senderObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                        interactiveMessage.setSender(user);
                    }
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID)) {
                        JSONObject receiverObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID);
                        String entityType = receiverObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE);
                        if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            User user = User.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            interactiveMessage.setReceiver(user);
                        } else if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                            Group group = Group.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            interactiveMessage.setReceiver(group);
                        }
                    }
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_GOAL)) {
                    interactiveMessage.setInteractionGoal(InteractionGoal.fromJsom(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_GOAL)));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ALLOW_SENDER_INTERACTION)) {
                    interactiveMessage.setAllowSenderInteraction(dataObject.getBoolean(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ALLOW_SENDER_INTERACTION));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIONS)) {
                    JSONArray interactionsArray = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIONS);
                    List<Interaction> interactions = new ArrayList<>();
                    for (int i = 0; i < interactionsArray.length(); i++) {
                        JSONObject interactionObject = interactionsArray.getJSONObject(i);
                        Interaction interaction = Interaction.fromJson(interactionObject);
                        interactions.add(interaction);
                    }
                    interactiveMessage.setInteractions(interactions);
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIVE_DATA)) {
                    interactiveMessage.setInteractiveData(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIVE_DATA));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA)) {
                    interactiveMessage.setMetadata(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA));
                }
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
                JSONObject receiptsObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                    interactiveMessage.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                    interactiveMessage.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TAGS)) {
                interactiveMessage.setTags(CometChatUtils.getListFromJSONArray(jsonObject.getJSONArray(CometChatConstants.MessageKeys.KEY_TAGS)));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE)) {
                BaseMessage quotedMessage = BaseMessage.processMessage(jsonObject.getJSONObject(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE));
                interactiveMessage.setQuotedMessage(quotedMessage);
                interactiveMessage.setQuotedMessageId(quotedMessage != null ? quotedMessage.getId() : 0);
            }
        } catch (JSONException je) {
            Logger.error(je.toString());
        }
        return interactiveMessage;
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
        if (this.getParentMessageId() > 0)
            map.put(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID, String.valueOf(this.getParentMessageId()));
        JSONObject dataObject = new JSONObject();
        try {
            if (this.getInteractiveData() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIVE_DATA, this.getInteractiveData());
            }
            if (this.getInteractions() != null) {
                JSONArray interactionsArray = new JSONArray();
                for (int i = 0; i < this.getInteractions().size(); i++) {
                    JSONObject interactionObject = new JSONObject(this.getInteractions().get(i).toMap());
                    interactionsArray.put(interactionObject);
                }
                dataObject.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIONS, interactionsArray);
            }
            if (this.isAllowSenderInteraction()) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ALLOW_SENDER_INTERACTION, this.isAllowSenderInteraction());
            }
            if (this.getInteractionGoal() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTION_GOAL, this.getInteractionGoal().toJson());
            }
            if (this.getMetadata() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA, this.getMetadata());
            }
            if (this.tags != null) {
                JSONArray tagsArray = CometChatUtils.getJSONArrayFromList(this.tags);
                map.put(CometChatConstants.MessageKeys.KEY_TAGS, tagsArray.toString());
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

    @Override
    public String toString() {
        return "InteractiveMessage{" +
                "id=" + id +
                ",tags=" + tags +
                ", actionOn='" + interactionGoal + '\'' +
                ", interactedElements='" + interactions + '\'' +
                ", interactiveData=" + interactiveData +
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
        if (!(other instanceof InteractiveMessage)) return false;

        // 4. Call parent's contentEquals (BaseMessage)
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        InteractiveMessage that = (InteractiveMessage) other;

        // 6. Compare all InteractiveMessage-specific fields

        // Primitive field (boolean) - use == operator
        if (allowSenderInteraction != that.allowSenderInteraction) return false;

        // JSONObject field - use ContentEqualsHelper.jsonObjectsEqual()
        if (!ContentEqualsHelper.jsonObjectsEqual(interactiveData, that.interactiveData)) return false;

        // List<String> field - use ContentEqualsHelper.listsEqual()
        if (!ContentEqualsHelper.listsEqual(tags, that.tags)) return false;

        // Nested object field - use ContentEqualsHelper.objectsContentEqual()
        if (!ContentEqualsHelper.objectsContentEqual(interactionGoal, that.interactionGoal)) return false;

        // List<Interaction> field - use ContentEqualsHelper.listsEqual()
        if (!ContentEqualsHelper.listsEqual(interactions, that.interactions)) return false;

        return true;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
    }
}
