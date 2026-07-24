package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.CometChatUtils;
import com.cometchat.chat.enums.ModerationStatus;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an AI Assistant message in the CometChat SDK.
 * This class extends BaseMessage and provides functionality for AI-generated messages
 * that are part of agentic conversations. It includes properties specific to AI assistants
 * such as run ID, thread ID, and tags for categorization.
 *
 * @author CometChat Team
 * @version 5.0
 */
public class AIAssistantMessage extends BaseMessage {

    private long runId = -1;
    private String threadId;
    private String text;
    private List<String> tags;
    private List<AIAssistantElement> elements;

    /**
     * Public default constructor that sets up the message category and type.
     */
    public AIAssistantMessage() {
        super();
        setCategory(CometChatConstants.CATEGORY_AGENTIC);
        setType(CometChatConstants.MESSAGE_TYPE_ASSISTANT);
    }

    /**
     * Constructs an AI Assistant message with the specified receiver details and text content.
     *
     * @param receiverUid The unique identifier of the message receiver
     * @param receiverType The type of receiver (user or group)
     * @param text The text content of the AI assistant message
     */
    public AIAssistantMessage(@NonNull String receiverUid,
                              @NonNull String receiverType,
                              @NonNull String text) {
        super(receiverUid, CometChatConstants.MESSAGE_TYPE_ASSISTANT, receiverType);
        setCategory(CometChatConstants.CATEGORY_AGENTIC);
        this.text = text;
    }

    /**
     * Gets the run ID associated with this AI assistant message.
     * The run ID represents a specific execution instance of the AI assistant.
     *
     * @return The run ID, or -1 if not set
     */
    public long getRunId() {
        return runId;
    }

    /**
     * Gets the thread ID where this AI assistant message belongs.
     *
     * @return The thread ID
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Gets the text content of the AI assistant message.
     *
     * @return The message text content
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the list of tags associated with this AI assistant message.
     * Tags can be used for categorization and filtering of messages.
     *
     * @return The list of tags, or null if no tags are set
     */
    public List<String> getTags() {return tags;}

    /**
     * Sets the run ID for this AI assistant message.
     *
     * @param runId The run ID to set
     */
    public void setRunId(long runId) {
        this.runId = runId;
    }

    /**
     * Sets the thread ID for this AI assistant message.
     *
     * @param threadId The thread ID to set
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    /**
     * Sets the text content for this AI assistant message.
     *
     * @param text The text content to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the tags for this AI assistant message.
     *
     * @param tags The list of tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Gets the list of structured elements for this message.
     * Returns null when the message was produced by an older server
     * that doesn't include data.elements.
     *
     * @return The list of elements, or null if not present
     */
    public List<AIAssistantElement> getElements() {
        return elements;
    }

    /**
     * Sets the elements list for this AI assistant message.
     *
     * @param elements The list of elements to set
     */
    public void setElements(List<AIAssistantElement> elements) {
        this.elements = elements;
    }

    /**
     * Creates an AIAssistantMessage instance from a JSON object.
     * This method parses the JSON response from the server and populates
     * all the message properties including AI-specific fields.
     *
     * @param jsonObject The JSON object containing the message data
     * @return A fully populated AIAssistantMessage instance
     * @throws JSONException if there's an error parsing the JSON
     */
    public static AIAssistantMessage fromJson(JSONObject jsonObject) {
        AIAssistantMessage aiAssistantMessage = new AIAssistantMessage();
        try {
            aiAssistantMessage.setRawMessage(jsonObject);
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                aiAssistantMessage.setId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
                aiAssistantMessage.setConversationId(jsonObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
                aiAssistantMessage.setParentMessageId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
                aiAssistantMessage.setReplyCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT))
                aiAssistantMessage.setUnreadRepliesCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                aiAssistantMessage.setReceiverUid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
                aiAssistantMessage.setMuid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE))
                aiAssistantMessage.setReceiverType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
                aiAssistantMessage.setCategory(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE))
                aiAssistantMessage.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT))
                aiAssistantMessage.setSentAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT))
                aiAssistantMessage.setUpdatedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                aiAssistantMessage.setDeliveredAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                aiAssistantMessage.setReadAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT))
                aiAssistantMessage.setEditedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY))
                aiAssistantMessage.setEditedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT))
                aiAssistantMessage.setDeletedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY))
                aiAssistantMessage.setDeletedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT)) {
                    aiAssistantMessage.setText(dataObject.getString(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID)) {
                    aiAssistantMessage.setRunId(dataObject.getLong(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID)) {
                    aiAssistantMessage.setThreadId(dataObject.getString(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID));
                }

                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENTS)) {
                    JSONArray elementsArray = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENTS);
                    List<AIAssistantElement> elements = new ArrayList<>();
                    for (int i = 0; i < elementsArray.length(); i++) {
                        JSONObject elementJson = elementsArray.optJSONObject(i);
                        if (elementJson != null) {
                            AIAssistantElement element = AIAssistantElement.fromJson(elementJson);
                            if (element != null) {
                                elements.add(element);
                            }
                        }
                    }
                    aiAssistantMessage.setElements(elements);
                }

                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                    JSONObject entitiesObject = new JSONObject(dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES).toString());
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_SENDER)) {
                        JSONObject senderObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SENDER);
                        User user = User.fromJson(senderObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                        aiAssistantMessage.setSender(user);
                    }
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID)) {
                        JSONObject receiverObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID);
                        String entityType = receiverObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE);
                        if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            User user = User.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            aiAssistantMessage.setReceiver(user);
                        } else if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                            Group group = Group.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            aiAssistantMessage.setReceiver(group);
                        }
                    }
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA)) {
                    aiAssistantMessage.setMetadata(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA));
                }
                // Mentions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_MENTIONS)) {
                    List<User> mentionedUsersList = new ArrayList<>();
                    boolean isMentionedMe = false;
                    User loggedInUserObj = CometChat.getLoggedInUser();
                    JSONObject userObj = dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_MENTIONS);
                    Iterator<String> keys = userObj.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = userObj.getString(key);
                        User user = User.fromJson(value);
                        mentionedUsersList.add(user);
                        if (key.toString().equals(loggedInUserObj.getUid())) {
                            isMentionedMe = true;
                        }
                    }
                    aiAssistantMessage.setMentionedUsers(mentionedUsersList);
                    aiAssistantMessage.setHasMentionedMe(isMentionedMe);
                }
                //Reactions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_REACTIONS)) {
                    JSONArray reactionCountObj = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_REACTIONS);
                    List<ReactionCount> reactionCountList = ReactionCount.listFromJSONArray(reactionCountObj);
                    aiAssistantMessage.setReactions(reactionCountList);
                }
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE)) {
                BaseMessage quotedMessage = BaseMessage.processMessage(jsonObject.getJSONObject(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE));
                aiAssistantMessage.setQuotedMessage(quotedMessage);
                aiAssistantMessage.setQuotedMessageId(quotedMessage != null ? quotedMessage.getId() : 0);
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
                JSONObject receiptsObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                    aiAssistantMessage.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                    aiAssistantMessage.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TAGS)) {
                aiAssistantMessage.setTags(CometChatUtils.getListFromJSONArray(jsonObject.getJSONArray(CometChatConstants.MessageKeys.KEY_TAGS)));
            }
        } catch (JSONException je) {
            Logger.error("Messages from json exception : " + je.getMessage());
        }
        return aiAssistantMessage;
    }

    /**
     * Converts the AI Assistant message to a map representation for API calls.
     * This method creates a HashMap containing all the message properties
     * formatted for sending to the CometChat server.
     *
     * @return A HashMap containing the message data in API format
     */
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
            if (this.getText() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT, this.getText());
            }
            if (this.getRunId() > -1) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID, this.getRunId());
            }
            if (this.getThreadId() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID, this.getThreadId());
            }
            if (this.getElements() != null) {
                JSONArray elementsArray = new JSONArray();
                for (AIAssistantElement element : this.getElements()) {
                    JSONObject elementJson = new JSONObject();
                    elementJson.put(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENT_TYPE, element.getType());
                    if (element.getData() != null) {
                        elementJson.put(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENT_VALUE, element.getData());
                    }
                    elementsArray.put(elementJson);
                }
                dataObject.put(CometChatConstants.MessageKeys.KEY_AGENTIC_ELEMENTS, elementsArray);
            }

            if (this.getMetadata() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA, this.getMetadata());
            }
        } catch (JSONException je) {
            Logger.error("Error creating JSON : " + je.getMessage());
        }

        map.put(CometChatConstants.ResponseKeys.KEY_DATA, dataObject.toString());
        return map;
    }

    /**
     * Returns a string representation of the AI Assistant message.
     * Includes all the key properties of the message for debugging purposes.
     *
     * @return A string representation of the message
     */
    @NonNull
    @Override
    public String toString() {
        return "AIAssistantMessage{" +
            "runId='" + runId + '\'' +
            ", threadId='" + threadId + '\'' +
            ", text='" + text + '\'' +
            ", elements=" + elements +
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

    /**
     * Compares this AI Assistant message with another object for equality.
     * Two messages are considered equal if they have the same ID.
     *
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
    }

    /**
     * Compares this AI Assistant message with another object for content equality.
     * Unlike equals() which compares by identity (ID), this method compares all fields
     * for value equality, including inherited fields from BaseMessage.
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
        if (!(other instanceof AIAssistantMessage)) return false;

        // Cast
        AIAssistantMessage that = (AIAssistantMessage) other;

        // Call parent's contentEquals first (BaseMessage)
        if (!super.contentEquals(other)) return false;

        // Compare AIAssistantMessage specific fields
        return runId == that.runId
                && ContentEqualsHelper.stringsEqual(threadId, that.threadId)
                && ContentEqualsHelper.stringsEqual(text, that.text)
                && ContentEqualsHelper.listsEqual(tags, that.tags)
                && ContentEqualsHelper.listsEqual(elements, that.elements);
    }

    // Parcelable implementation
    protected AIAssistantMessage(Parcel in) {
        super(in);
        runId = in.readLong();
        threadId = in.readString();
        text = in.readString();
        tags = in.createStringArrayList();
        int elementsSize = in.readInt();
        if (elementsSize >= 0) {
            elements = new ArrayList<>(elementsSize);
            for (int i = 0; i < elementsSize; i++) {
                elements.add(AIAssistantElement.CREATOR.createFromParcel(in));
            }
        } else {
            elements = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(runId);
        dest.writeString(threadId);
        dest.writeString(text);
        dest.writeStringList(tags);
        if (elements != null) {
            dest.writeInt(elements.size());
            for (AIAssistantElement element : elements) {
                element.writeToParcel(dest, flags);
            }
        } else {
            dest.writeInt(-1);
        }
    }

    public static final Creator<AIAssistantMessage> CREATOR = new Creator<AIAssistantMessage>() {
        @Override
        public AIAssistantMessage createFromParcel(Parcel in) {
            return new AIAssistantMessage(in);
        }

        @Override
        public AIAssistantMessage[] newArray(int size) {
            return new AIAssistantMessage[size];
        }
    };

    @Override
    public AIAssistantMessage clone() {
        AIAssistantMessage clone = (AIAssistantMessage) super.clone();
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        clone.text = this.text;
        clone.tags = this.tags != null ? new ArrayList<>(this.tags) : null;
        if (this.elements != null) {
            clone.elements = new ArrayList<>(this.elements.size());
            for (AIAssistantElement element : this.elements) {
                clone.elements.add(element.clone());
            }
        } else {
            clone.elements = null;
        }
        return clone;
    }
}