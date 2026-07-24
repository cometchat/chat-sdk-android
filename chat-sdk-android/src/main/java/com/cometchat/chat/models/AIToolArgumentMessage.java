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
 * Represents an AI Tool Argument message in the CometChat SDK.
 * This class extends BaseMessage and provides functionality for messages containing
 * AI tool call arguments. It includes properties specific to tool argument messages
 * such as run ID, thread ID, tool calls, and tags for categorization.
 *
 * @author CometChat Team
 * @version 4.0
 */
public class AIToolArgumentMessage extends BaseMessage {

    private long runId = -1;
    private String threadId;
    private List<AIToolCall> toolCalls;
    private List<String> tags;

    /**
     * Private default constructor that sets up the message category and type.
     */
    private AIToolArgumentMessage() {
        super();
        setCategory(CometChatConstants.CATEGORY_AGENTIC);
        setType(CometChatConstants.MESSAGE_TYPE_TOOL_ARGUMENTS);
    }

    /**
     * Constructs an AI Tool Argument message with the specified receiver details.
     *
     * @param receiverUid   The unique identifier of the message receiver
     * @param receiverType  The type of receiver (user or group)
     */
    public AIToolArgumentMessage(@NonNull String receiverUid,
                                 @NonNull String receiverType) {
        super(receiverUid, CometChatConstants.MESSAGE_TYPE_TOOL_ARGUMENTS, receiverType);
        setCategory(CometChatConstants.CATEGORY_AGENTIC);
    }

    /**
     * Gets the run ID associated with this AI tool argument message.
     * The run ID represents a specific execution instance of the AI assistant.
     *
     * @return The run ID, or -1 if not set
     */
    public long getRunId() {
        return runId;
    }

    /**
     * Gets the thread ID where this AI tool argument message belongs.
     *
     * @return The thread ID
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Gets the list of tool calls associated with this message.
     * Each tool call contains information about a specific tool being invoked.
     *
     * @return The list of tool calls
     */
    public List<AIToolCall> getToolCalls() {
        return toolCalls;
    }

    /**
     * Gets the list of tags associated with this AI tool argument message.
     * Tags can be used for categorization and filtering of messages.
     *
     * @return The list of tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Sets the run ID for this AI tool argument message.
     *
     * @param runId The run ID to set
     */
    public void setRunId(long runId) {
        this.runId = runId;
    }

    /**
     * Sets the thread ID for this AI tool argument message.
     *
     * @param threadId The thread ID to set
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    /**
     * Sets the list of tool calls for this message.
     *
     * @param toolCalls The list of tool calls to set
     */
    public void setToolCalls(List<AIToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }

    /**
     * Sets the tags for this AI tool argument message.
     *
     * @param tags The list of tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Creates an AIToolArgumentMessage instance from a JSON object.
     * This method parses the JSON response from the server and populates
     * all the message properties including AI-specific fields and tool calls.
     *
     * @param jsonObject The JSON object containing the message data
     * @return A fully populated AIToolArgumentMessage instance
     */
    public static AIToolArgumentMessage fromJson(JSONObject jsonObject) {
        AIToolArgumentMessage aiToolArgumentMessage = new AIToolArgumentMessage();
        try {
            aiToolArgumentMessage.setRawMessage(jsonObject);
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                aiToolArgumentMessage.setId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
                aiToolArgumentMessage.setConversationId(jsonObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
                aiToolArgumentMessage.setParentMessageId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
                aiToolArgumentMessage.setReplyCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT))
                aiToolArgumentMessage.setUnreadRepliesCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                aiToolArgumentMessage.setReceiverUid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
                aiToolArgumentMessage.setMuid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE))
                aiToolArgumentMessage.setReceiverType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
                aiToolArgumentMessage.setCategory(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE))
                aiToolArgumentMessage.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT))
                aiToolArgumentMessage.setSentAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT))
                aiToolArgumentMessage.setUpdatedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                aiToolArgumentMessage.setDeliveredAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                aiToolArgumentMessage.setReadAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT))
                aiToolArgumentMessage.setEditedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY))
                aiToolArgumentMessage.setEditedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT))
                aiToolArgumentMessage.setDeletedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY))
                aiToolArgumentMessage.setDeletedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));

            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);

                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID)) {
                    aiToolArgumentMessage.setRunId(dataObject.getLong(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID)) {
                    aiToolArgumentMessage.setThreadId(dataObject.getString(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_TOOL_CALLS)) {
                    JSONArray toolCallsArray = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_AGENTIC_TOOL_CALLS);
                    List<AIToolCall> toolCallsList = new ArrayList<>();
                    for (int i = 0; i < toolCallsArray.length(); i++) {
                        JSONObject toolCallObject = toolCallsArray.getJSONObject(i);
                        AIToolCall toolCall = AIToolCall.fromJson(toolCallObject);
                        toolCallsList.add(toolCall);
                    }
                    aiToolArgumentMessage.setToolCalls(toolCallsList);
                }

                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                    JSONObject entitiesObject = new JSONObject(dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES).toString());
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_SENDER)) {
                        JSONObject senderObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SENDER);
                        User user = User.fromJson(senderObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                        aiToolArgumentMessage.setSender(user);
                    }
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID)) {
                        JSONObject receiverObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID);
                        String entityType = receiverObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE);
                        if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            User user = User.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            aiToolArgumentMessage.setReceiver(user);
                        } else if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                            Group group = Group.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            aiToolArgumentMessage.setReceiver(group);
                        }
                    }
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA)) {
                    aiToolArgumentMessage.setMetadata(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA));
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
                    aiToolArgumentMessage.setMentionedUsers(mentionedUsersList);
                    aiToolArgumentMessage.setHasMentionedMe(isMentionedMe);
                }
                //Reactions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_REACTIONS)) {
                    JSONArray reactionCountObj = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_REACTIONS);
                    List<ReactionCount> reactionCountList = ReactionCount.listFromJSONArray(reactionCountObj);
                    aiToolArgumentMessage.setReactions(reactionCountList);
                }
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
                JSONObject receiptsObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                    aiToolArgumentMessage.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                    aiToolArgumentMessage.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TAGS)) {
                aiToolArgumentMessage.setTags(CometChatUtils.getListFromJSONArray(jsonObject.getJSONArray(CometChatConstants.MessageKeys.KEY_TAGS)));
            }
        } catch (JSONException je) {
            Logger.error("Messages from json exception : " + je.getMessage());
        }
        return aiToolArgumentMessage;
    }

    /**
     * Converts the AI Tool Argument message to a map representation for API calls.
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
            if (this.getRunId() > -1) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_AGENTIC_RUN_ID, this.getRunId());
            }
            if (this.getThreadId() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_AGENTIC_THREAD_ID, this.getThreadId());
            }
            if (this.getToolCalls() != null && !this.getToolCalls().isEmpty()) {
                JSONArray toolCallsArray = new JSONArray();
                for (AIToolCall toolCall : this.getToolCalls()) {
                    toolCallsArray.put(toolCall.toJson());
                }
                dataObject.put(CometChatConstants.MessageKeys.KEY_AGENTIC_TOOL_CALLS, toolCallsArray);
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
     * Returns a string representation of the AI Tool Argument message.
     * Includes all the key properties of the message for debugging purposes.
     *
     * @return A string representation of the message
     */
    @NonNull
    @Override
    public String toString() {
        return "AIToolArgumentMessage{" +
            "runId='" + runId + '\'' +
            ", threadId='" + threadId + '\'' +
            ", toolCalls=" + toolCalls +
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
     * Compares this AI Tool Argument message with another object for equality.
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
        if (!(other instanceof AIToolArgumentMessage)) return false;

        // 4. Call parent's contentEquals (BaseMessage)
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        AIToolArgumentMessage that = (AIToolArgumentMessage) other;

        // 6. Compare all fields

        // Primitive fields (long) - use ==
        if (runId != that.runId) return false;

        // String fields - use ContentEqualsHelper.stringsEqual()
        if (!ContentEqualsHelper.stringsEqual(threadId, that.threadId)) return false;

        // List fields - use ContentEqualsHelper.listsEqual()
        if (!ContentEqualsHelper.listsEqual(toolCalls, that.toolCalls)) return false;
        if (!ContentEqualsHelper.listsEqual(tags, that.tags)) return false;

        return true;
    }

    // Parcelable implementation
    protected AIToolArgumentMessage(Parcel in) {
        super(in);
        runId = in.readLong();
        threadId = in.readString();
        toolCalls = in.createTypedArrayList(AIToolCall.CREATOR);
        tags = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(runId);
        dest.writeString(threadId);
        dest.writeTypedList(toolCalls);
        dest.writeStringList(tags);
    }

    public static final Creator<AIToolArgumentMessage> CREATOR = new Creator<AIToolArgumentMessage>() {
        @Override
        public AIToolArgumentMessage createFromParcel(Parcel in) {
            return new AIToolArgumentMessage(in);
        }

        @Override
        public AIToolArgumentMessage[] newArray(int size) {
            return new AIToolArgumentMessage[size];
        }
    };

    @Override
    public AIToolArgumentMessage clone() {
        AIToolArgumentMessage clone = (AIToolArgumentMessage) super.clone();
        clone.runId = this.runId;
        clone.threadId = this.threadId;
        if (this.toolCalls != null) {
            clone.toolCalls = new ArrayList<>();
            for (AIToolCall toolCall : this.toolCalls) {
                clone.toolCalls.add(toolCall.clone());
            }
        }
        clone.tags = this.tags != null ? new ArrayList<>(this.tags) : null;
        return clone;
    }
}