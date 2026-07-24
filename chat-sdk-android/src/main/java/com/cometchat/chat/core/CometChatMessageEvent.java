package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.CometChatHelper;
import com.cometchat.chat.models.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class CometChatMessageEvent extends CometChatEvent {

    private BaseMessage message;

    public BaseMessage getMessage() {
        return message;
    }

    public void setMessage(BaseMessage message) {
        this.message = message;
    }

    CometChatMessageEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_TYPE_MESSAGE);
    }

    public static CometChatMessageEvent fromJSON(JSONObject mainObject) throws JSONException {
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
        CometChatMessageEvent cometChatMessageEvent = new CometChatMessageEvent(appId, receiver, receiverType, deviceId, sender);
        JSONObject bodyObject = mainObject.getJSONObject(CometChatConstants.WSKeys.KEY_BODY);
        BaseMessage baseMessage = CometChatHelper.processMessage(bodyObject);
        cometChatMessageEvent.setMessage(baseMessage);
        return cometChatMessageEvent;
    }

    @Override
    protected String getAsString() throws JSONException {
        return null;
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        return null;
    }
}
