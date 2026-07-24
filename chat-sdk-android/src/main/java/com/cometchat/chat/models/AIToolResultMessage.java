package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.CometChatUtils;
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
 * Represents an AI Tool Result message in the CometChat SDK.
 * This class extends BaseMessage and provides functionality for messages containing
 * the results of AI tool executions. It includes properties specific to tool result messages
 * such as run ID, thread ID, tool call ID, result text, and tags for categorization.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIToolResultMessage extends BaseMessage {

    private long runId = -1;
    private String threadId;
    private String text;
    private String toolCallId;
    private List<String> tags;

    /**
     * Private default constructor that sets up the message category and type.
     */
    private AIToolResultMessage() {
        super();
        setCategory(CometChatConstants.CATEGORY_AGENTIC);
        setType(CometChatConstants.MESSAGE_TYPE_TOOL_RESULT);
    }

    /**
     * Constructs an AI Tool Result message with the specified receiver details and result text.
     *
     * @param receiverUid The unique identifier of the message receiver
     * @param receiverType The type of receiver (user or group)
     * @param text The result text content from the tool execution
     */
    public AIToolResultMessage(@NonNull String receiverUid,
                               @NonNull String receiverType,
                               @NonNull String text) {
        super(receiverUid, CometChatConstants.MESSAGE_TYPE_TOOL_RESULT, receiverType);
        setCategory(CometChatConstants.CATEGORY_AGENTIC);
        this.text = text;
    }

    /**
     * Gets the run ID associated with this AI tool result message.
     * The run ID represents a specific execution instance of the AI assistant.
     *
     * @return The run ID, or -1 if not set
     */
    public long getRunId() {
        return runId;
    }

    /**
     * Gets the thread ID where this AI tool result message belongs.
     *
     * @return The thread ID
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Gets the result text content from the tool execution.
     *
     * @return The result text content
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the unique identifier of the tool call that produced this result.
     *
     * @return The tool call ID
     */
    public String getToolCallId() {
        return toolCallId;
    }

    /**
     * Gets the list of tags associated with this AI tool result message.
     * Tags can be used for categorization and filtering of messages.
     *
     * @return The list of tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Sets the run ID for this AI tool result message.
     *
     * @param runId The run ID to set
     */
    public void setRunId(long runId) {
        this.runId = runId;
    }

    /**
     * Sets the thread ID for this AI tool result message.
     *
     * @param threadId The thread ID to set
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    /**
     * Sets the result text content from the tool execution.
     *
     * @param text The result text content to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the unique identifier of the tool call that produced this result.
     *
     * @param toolCallId The tool call ID to set
     */
    public void setToolCallId(String toolCallId) {
        this.toolCallId = toolCallId;
    }

    /**
     * Sets the tags for this AI tool result message.
     *
     * @param tags The list of tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Creates an AIToolResultMessage instance from a JSON object.
     * This method parses the JSON response from the server and populates
     * all the message properties including AI-specific fields and tool result data.
     *
     * @param jsonObject The JSON object containing the message data
     * @return A fully populated AIToolResultMessage instance
     */
    public static AIToolResultMessage fromJson(JSONObject jsonObject) {
        AIToolResultMessage aiToolResultMessage = new AIToolResultMessage();
        try {
            aiToolResultMessage.setRawMessage(jsonObject);
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                aiToolResultMessage.setId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
                aiToolResultMessage.setConversationId(jsonObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
                aiToolResultMessage.setParentMessageId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
                aiToolResultMessage.setReplyCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT))
                aiToolResultMessage.setUnreadRepliesCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                aiToolResultMessage.setReceiverUid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
                aiToolResultMessage.setMuid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE))
                aiToolResultMessage.setReceiverType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
                aiToolResultMessage.setCategory(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE))
                aiToolResultMessage.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT))
                aiToolResultMessage.setSentAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT))
                aiToolResultMessage.setUpdatedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                aiToolResultMessage.setDeliveredAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                aiToolResultMessage.setReadAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT))
                aiToolResultMessage.setEditedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY))
                aiToolResultMessage.setEditedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT))
                aiToolResultMessage.setDeletedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY))
                aiToolResultMessage.setDeletedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));

            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);

                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT)) {
                    aiToolResultMessage.setText(dataObject.getString(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID)) {
                    aiToolResultMessage.setRunId(dataObject.getLong(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID)) {
                    aiToolResultMessage.setThreadId(dataObject.getString(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_TOOL_CALL_ID)) {
                    aiToolResultMessage.setToolCallId(dataObject.getString(CometChatConstants.MessageKeys.KEY_AGENTIC_TOOL_CALL_ID));
                }

                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                    JSONObject entitiesObject = new JSONObject(dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES).toString());
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_SENDER)) {
                        JSONObject senderObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SENDER);
                        User user = User.fromJson(senderObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                        aiToolResultMessage.setSender(user);
                    }
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID)) {
                        JSONObject receiverObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID);
                        String entityType = receiverObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE);
                        if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            User user = User.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            aiToolResultMessage.setReceiver(user);
                        } else if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                            Group group = Group.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            aiToolResultMessage.setReceiver(group);
                        }
                    }
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA)) {
                    aiToolResultMessage.setMetadata(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA));
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
                    aiToolResultMessage.setMentionedUsers(mentionedUsersList);
                    aiToolResultMessage.setHasMentionedMe(isMentionedMe);
                }
                //Reactions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_REACTIONS)){
                    JSONArray reactionCountObj = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_REACTIONS);
                    List<ReactionCount> reactionCountList = ReactionCount.listFromJSONArray(reactionCountObj);
                    aiToolResultMessage.setReactions(reactionCountList);
                }
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
                JSONObject receiptsObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                    aiToolResultMessage.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                    aiToolResultMessage.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TAGS)) {
                aiToolResultMessage.setTags(CometChatUtils.getListFromJSONArray(jsonObject.getJSONArray(CometChatConstants.MessageKeys.KEY_TAGS)));
            }
        } catch (JSONException je) {
            Logger.error("Messages from json exception : " + je.getMessage());
        }
        return aiToolResultMessage;
    }

    /**
     * Converts the AI Tool Result message to a map representation for API calls.
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
            if (this.getToolCallId() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_AGENTIC_TOOL_CALL_ID, this.getToolCallId());
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
     * Returns a string representation of the AI Tool Result message.
     * Includes all the key properties of the message for debugging purposes.
     *
     * @return A string representation of the message
     */
    @NonNull
    @Override
    public String toString() {
        return "AIToolResultMessage{" +
            "runId='" + runId + '\'' +
            ", threadId='" + threadId + '\'' +
            ", text='" + text + '\'' +
            ", toolCallId='" + toolCallId + '\'' +
            ", tags=" + tags +
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
     * Compares this AI Tool Result message with another object for equality.
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
     * Compares this AI Tool Result message with another object for content equality.
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
        if (!(other instanceof AIToolResultMessage)) return false;

        // Cast
        AIToolResultMessage that = (AIToolResultMessage) other;

        // Call parent's contentEquals first (BaseMessage)
        if (!super.contentEquals(other)) return false;

        // Compare AIToolResultMessage specific fields
        return runId == that.runId
                && ContentEqualsHelper.stringsEqual(threadId, that.threadId)
                && ContentEqualsHelper.stringsEqual(text, that.text)
                && ContentEqualsHelper.stringsEqual(toolCallId, that.toolCallId)
                && ContentEqualsHelper.listsEqual(tags, that.tags);
    }

    // Parcelable implementation
    protected AIToolResultMessage(Parcel in) {
        super(in);
        runId = in.readLong();
        threadId = in.readString();
        text = in.readString();
        toolCallId = in.readString();
        tags = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(runId);
        dest.writeString(threadId);
        dest.writeString(text);
        dest.writeString(toolCallId);
        dest.writeStringList(tags);
    }

    public static final Creator<AIToolResultMessage> CREATOR = new Creator<AIToolResultMessage>() {
        @Override
        public AIToolResultMessage createFromParcel(Parcel in) {
            return new AIToolResultMessage(in);
        }

        @Override
        public AIToolResultMessage[] newArray(int size) {
            return new AIToolResultMessage[size];
        }
    };

    @Override
    public AIToolResultMessage clone() {
        AIToolResultMessage clone = (AIToolResultMessage) super.clone();
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        clone.text = this.text;
        clone.toolCallId = this.toolCallId;
        clone.tags = this.tags != null ? new ArrayList<>(this.tags) : null;
        return clone;
    }
}