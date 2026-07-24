package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.TransientMessage;
import com.cometchat.chat.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class CometChatTransientMessageEvent extends CometChatEvent{

    private TransientMessage transientMessage;

    protected CometChatTransientMessageEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_TYPE_TRANSIENT_MESSAGE);
    }

    static CometChatTransientMessageEvent fromJSON(JSONObject mainObject) throws JSONException {
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
        CometChatTransientMessageEvent cometChatTransientMessageEvent = new CometChatTransientMessageEvent(appId, receiver, receiverType, deviceId, sender);
        JSONObject bodyObject = mainObject.getJSONObject(CometChatConstants.WSKeys.KEY_BODY);
        JSONObject dataObject = null;
        if(bodyObject.has(CometChatConstants.WSKeys.KEY_DATA)) {
            dataObject = bodyObject.getJSONObject(CometChatConstants.WSKeys.KEY_DATA);
        }
        TransientMessage transientMessage = new TransientMessage(receiver, receiverType, dataObject);
        transientMessage.setSender(User.fromJson(bodyObject.getJSONObject(CometChatConstants.WSKeys.KEY_USER).toString()));
        cometChatTransientMessageEvent.setTransientMessage(transientMessage);
        return cometChatTransientMessageEvent;
    }

    public TransientMessage getTransientMessage() {
        return transientMessage;
    }

    public void setTransientMessage(TransientMessage transientMessage) {
        this.transientMessage = transientMessage;
    }

    @Override
    protected String getAsString() throws JSONException {
        return getAsJSONObject().toString();
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        JSONObject mainObject = getCometChatEventJSON();
        JSONObject bodyObject = new JSONObject();
        JSONObject userObject = CometChat.getLoggedInUser().toJson();
        bodyObject.put(CometChatConstants.WSKeys.KEY_DATA, transientMessage.getData());
        bodyObject.put(CometChatConstants.WSKeys.KEY_USER, userObject);
        mainObject.put(CometChatConstants.WSKeys.KEY_BODY, bodyObject);
        return mainObject;
    }
}
