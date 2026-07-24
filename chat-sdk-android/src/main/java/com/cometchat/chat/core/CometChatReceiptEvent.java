package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.MessageReceipt;
import com.cometchat.chat.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class CometChatReceiptEvent extends CometChatEvent {

    private String action;
    private MessageReceipt messageReceipt;


    CometChatReceiptEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_TYPE_RECEIPTS);
    }

    public static CometChatReceiptEvent fromJSON(JSONObject mainObject) throws JSONException {
        String appId = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_APP_ID))
            appId = mainObject.getString(CometChatConstants.WSKeys.KEY_APP_ID);
        String receiver = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_RECEIVER))
            receiver = mainObject.getString(CometChatConstants.WSKeys.KEY_RECEIVER);
        String receiverType = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE))
            receiverType = mainObject.getString(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE);
        String deviceId = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_DEVICE_ID))
            deviceId = mainObject.getString(CometChatConstants.WSKeys.KEY_DEVICE_ID);
        String sender = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_SENDER))
            sender = mainObject.getString(CometChatConstants.WSKeys.KEY_SENDER);
        String messageSender = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_MESSAGE_SENDER))
            messageSender = mainObject.getString(CometChatConstants.WSKeys.KEY_MESSAGE_SENDER);
        CometChatReceiptEvent cometChatReceiptEvent = new CometChatReceiptEvent(appId, receiver, receiverType, deviceId, sender);
        JSONObject bodyObject = mainObject.getJSONObject(CometChatConstants.WSKeys.KEY_BODY);
        cometChatReceiptEvent.setAction(bodyObject.getString(CometChatConstants.WSKeys.KEY_ACTION));
        MessageReceipt receivedMessageReceipt = new MessageReceipt();
        receivedMessageReceipt.setReceiverId(receiver);
        receivedMessageReceipt.setReceiverType(receiverType);
        receivedMessageReceipt.setSender(User.fromJson(bodyObject.getJSONObject(CometChatConstants.WSKeys.KEY_USER).toString()));
        receivedMessageReceipt.setMessageId(bodyObject.getLong(CometChatConstants.WSKeys.KEY_MESSAGE_ID));
        receivedMessageReceipt.setReceiptType(bodyObject.getString(CometChatConstants.WSKeys.KEY_ACTION));
        receivedMessageReceipt.setMessageSender(messageSender);
        if(bodyObject.has(CometChatConstants.WSKeys.KEY_TIMESTAMP)){
            receivedMessageReceipt.setTimestamp(bodyObject.getLong(CometChatConstants.WSKeys.KEY_TIMESTAMP));
            if(receivedMessageReceipt.getReceiptType().equalsIgnoreCase(MessageReceipt.RECEIPT_TYPE_DELIVERED))
                receivedMessageReceipt.setDeliveredAt(bodyObject.getLong(CometChatConstants.WSKeys.KEY_TIMESTAMP));
            else if(receivedMessageReceipt.getReceiptType().equalsIgnoreCase(MessageReceipt.RECEIPT_TYPE_READ))
                receivedMessageReceipt.setReadAt(bodyObject.getLong(CometChatConstants.WSKeys.KEY_TIMESTAMP));
            else if(receivedMessageReceipt.getReceiptType().equalsIgnoreCase(MessageReceipt.RECEIPT_TYPE_DELIVERED_TO_ALL))
                receivedMessageReceipt.setDeliveredAt(bodyObject.getLong(CometChatConstants.WSKeys.KEY_TIMESTAMP));
            else if(receivedMessageReceipt.getReceiptType().equalsIgnoreCase(MessageReceipt.RECEIPT_TYPE_READ_BY_ALL))
                receivedMessageReceipt.setReadAt(bodyObject.getLong(CometChatConstants.WSKeys.KEY_TIMESTAMP));
        }
        cometChatReceiptEvent.setMessageReceipt(receivedMessageReceipt);
        return cometChatReceiptEvent;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public MessageReceipt getMessageReceipt() {
        return messageReceipt;
    }

    public void setMessageReceipt(MessageReceipt messageReceipt) {
        this.messageReceipt = messageReceipt;
    }

    @Override
    protected String getAsString() throws JSONException {
        return getAsJSONObject().toString();
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        JSONObject mainObject = getCometChatEventJSON();
        JSONObject bodyObject = new JSONObject();
        bodyObject.put(CometChatConstants.WSKeys.KEY_ACTION, messageReceipt.getReceiptType());
        bodyObject.put(CometChatConstants.WSKeys.KEY_MESSAGE_ID, messageReceipt.getMessageId());
        JSONObject userObject = CometChat.getLoggedInUser().toJson();
        bodyObject.put(CometChatConstants.WSKeys.KEY_USER, userObject);
        mainObject.put(CometChatConstants.WSKeys.KEY_BODY, bodyObject);
        return mainObject;
    }
}
