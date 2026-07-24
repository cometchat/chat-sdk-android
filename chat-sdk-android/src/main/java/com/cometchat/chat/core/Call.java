package com.cometchat.chat.core;

import androidx.annotation.NonNull;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.AppEntity;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Call class provides information about call Messages
 */

public class Call extends BaseMessage {

    private String sessionId;
    @CometChatConstants.CallStatus
    private String callStatus;
    private String action;
    private String rawData;
    private long initiatedAt;
    private long joinedAt;
    private AppEntity callInitiator;
    private AppEntity callReceiver;

    public Call(@NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType, @CometChatConstants.CallType String callType) {
        this.receiverUid = receiverId;
        this.receiverType = receiverType;
        this.type = callType;
        setCategory(CometChatConstants.CATEGORY_CALL);
    }

    private Call() {

    }

    /**
     * Get unique session id of the call message
     *
     * @return unique session id of the message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Get status of the call
     *
     * @return Call status
     * @version <b>v2</b>
     * @see CometChatConstants.CallStatus
     * @since <b>v1</b>
     */
    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    /**
     * Get action on call
     *
     * @return action on call
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getAction() {
        return action;
    }


    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Get raw JSON data of the call message
     *
     * @return raw JSON data
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    /**
     * Get Call initiated timestamp
     *
     * @return initiated timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(long initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    /**
     * Get call initiator entity(User/Group)
     *
     * @return call initiator entity(user/group)
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public AppEntity getCallInitiator() {
        return callInitiator;
    }

    public void setCallInitiator(AppEntity callInitiator) {
        this.callInitiator = callInitiator;
    }

    /**
     * Get call receiver entity(User/Group)
     *
     * @return call receiver entity(user/group)
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public AppEntity getCallReceiver() {
        return callReceiver;
    }

    public void setCallReceiver(AppEntity callReceiver) {
        this.callReceiver = callReceiver;
    }

    /**
     * Get Call join timestamp
     *
     * @return call joined timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(CometChatConstants.CallKeys.CALL_RECEIVER, this.receiverUid);
        map.put(CometChatConstants.CallKeys.CALL_RECEIVER_TYPE, this.receiverType);
        map.put(CometChatConstants.CallKeys.CALL_TYPE, this.type);
        map.put(CometChatConstants.CallKeys.CALL_STATUS, this.callStatus);
        if(this.getParentMessageId() >0)
            map.put(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID, String.valueOf(this.getParentMessageId()));
        if (this.metadata != null) {
            map.put(CometChatConstants.CallKeys.CALL_METADATA, this.metadata.toString());
        }
        if (this.getMuid() != null)
            map.put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID, this.getMuid());
        return map;
    }

    public static Call fromJson(String response) throws JSONException {
        Call call = new Call();
        JSONObject mainObject = new JSONObject(response);
        call.setRawMessage(mainObject);
        if (mainObject.has(CometChatConstants.CallKeys.CALL_ID))
            call.setId(mainObject.getLong(CometChatConstants.CallKeys.CALL_ID));
        if (mainObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
            call.setConversationId(mainObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
            call.setParentMessageId(mainObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
            call.setReplyCount(mainObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
        if (mainObject.has(CometChatConstants.CallKeys.CALL_RECEIVER_TYPE))
            call.setReceiverType(mainObject.getString(CometChatConstants.CallKeys.CALL_RECEIVER_TYPE));
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
            call.setReceiverUid(mainObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
            call.setMuid(mainObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
        if (mainObject.has(CometChatConstants.CallKeys.CALL_TYPE))
            call.setType(mainObject.getString(CometChatConstants.CallKeys.CALL_TYPE));
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
            call.setCategory(mainObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT))
            call.setSentAt(mainObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT)) {
            call.setUpdatedAt(mainObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
        }
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT)) {
            call.setDeliveredAt(mainObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
        }

        if (mainObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT)) {
            call.setReadAt(mainObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
        }
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT)) {
            call.setEditedAt(mainObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
        }
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY)) {
            call.setEditedBy(mainObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
        }
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT)) {
            call.setDeletedAt(mainObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
        }
        if (mainObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY)) {
            call.setDeletedBy(mainObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));
        }
        if (mainObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
            JSONObject receiptsObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
            if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                call.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                call.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
        }
        if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
            JSONObject dataObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
            if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ACTION)) {
                call.setAction(dataObject.getString(CometChatConstants.ResponseKeys.KEY_ACTION));
            }
            call.setRawData(dataObject.toString());
            if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                JSONObject dataEntitiesObject = dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES);
                if (dataEntitiesObject.has(CometChatConstants.ActionKeys.KEY_ON)) {
                    JSONObject onObject = dataEntitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ON);
                    if (onObject.has(CometChatConstants.ResponseKeys.KEY_ENTITITY)) {
                        JSONObject entityObject = onObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY);
                        if (entityObject.has(CometChatConstants.CallKeys.CALL_SESSION_ID))
                            call.setSessionId(entityObject.getString(CometChatConstants.CallKeys.CALL_SESSION_ID));
                        if (entityObject.has(CometChatConstants.CallKeys.CALL_STATUS))
                            call.setCallStatus(entityObject.getString(CometChatConstants.CallKeys.CALL_STATUS));
                        if (entityObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                            JSONObject entityDataObject = entityObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            if (entityDataObject.has(CometChatConstants.CallKeys.CALL_METADATA))
                                call.setMetadata(entityDataObject.getJSONObject(CometChatConstants.CallKeys.CALL_METADATA));
                            if (entityDataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                                JSONObject entitiesObject = entityDataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES);
                                if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_SENDER)) {
                                    call.setCallInitiator(User.fromJson(entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SENDER).getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY).toString()));
                                }
                                if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID)) {
                                    if (entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID).getString(CometChatConstants.ActionKeys.KEY_ENTITY_TYPE).equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
                                        call.setCallReceiver(User.fromJson(entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID).getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY).toString()));
                                    } else {
                                        call.setCallReceiver(Group.fromJson(entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID).getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY).toString()));
                                    }

                                }
                            }
                        }
                        if (entityObject.has(CometChatConstants.CallKeys.CALL_INITIATED_AT))
                            call.setInitiatedAt(entityObject.getLong(CometChatConstants.CallKeys.CALL_INITIATED_AT));
                        if (entityObject.has(CometChatConstants.CallKeys.CALL_JOINED_AT))
                            call.setJoinedAt(entityObject.getLong(CometChatConstants.CallKeys.CALL_JOINED_AT));
                    }
                }
                if (dataEntitiesObject.has(CometChatConstants.ActionKeys.KEY_BY)) {
                    if (dataEntitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_BY).has(CometChatConstants.ActionKeys.KEY_ENTITY)) {
                        call.setSender(User.fromJson(dataEntitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_BY).getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY).toString()));
                    }
                }
                if (dataEntitiesObject.has(CometChatConstants.ActionKeys.KEY_FOR)) {
                    JSONObject forObject = dataEntitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_FOR);
                    String entityType = forObject.getString(CometChatConstants.ActionKeys.KEY_ENTITY_TYPE);
                    if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                        call.setReceiver(Group.fromJson(forObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY).toString()));
                    } else {
                        call.setReceiver(User.fromJson(forObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY).toString()));
                    }
                }
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
                call.setMentionedUsers(mentionedUsersList);
                call.setHasMentionedMe(isMentionedMe);
            }
        }
        return call;
    }

    @Override
    public String toString() {
        return "Call{" +
                "sessionId='" + sessionId + '\'' +
                ", callStatus='" + callStatus + '\'' +
                ", action='" + action + '\'' +
                ", rawData='" + rawData + '\'' +
                ", initiatedAt=" + initiatedAt +
                ", joinedAt=" + joinedAt +
                ", callInitiator=" + callInitiator +
                ", callReceiver=" + callReceiver +
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
}
