package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class CometChatEvent {

    private String appId;
    private String receiver;
    private String receiverType;
    private String deviceId;
    private String type;
    private String sender;
    private String messageSender;

    CometChatEvent(String appId, String receiver, String receiverType, String deviceId, String sender){
        this.appId = appId;
        this.receiver = receiver;
        this.receiverType = receiverType;
        this.deviceId = deviceId;
        this.sender = sender;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    protected abstract String getAsString() throws JSONException;

    protected abstract JSONObject getAsJSONObject() throws JSONException;

    protected JSONObject getCometChatEventJSON() throws JSONException{
        JSONObject mainObject = new JSONObject();
        mainObject.put(CometChatConstants.WSKeys.KEY_APP_ID, getAppId());
        mainObject.put(CometChatConstants.WSKeys.KEY_RECEIVER, getReceiver());
        mainObject.put(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE, getReceiverType());
        mainObject.put(CometChatConstants.WSKeys.KEY_DEVICE_ID, getDeviceId());
        mainObject.put(CometChatConstants.WSKeys.KEY_TYPE, getType());
        mainObject.put(CometChatConstants.WSKeys.KEY_SENDER, getSender());
        if(getMessageSender()!=null){
            mainObject.put(CometChatConstants.WSKeys.KEY_MESSAGE_SENDER, getMessageSender());
        }
        return mainObject;
    }


}
