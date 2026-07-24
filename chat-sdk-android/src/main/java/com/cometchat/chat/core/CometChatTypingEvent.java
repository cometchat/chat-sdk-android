package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.TypingIndicator;
import com.cometchat.chat.models.User;

import org.json.JSONException;
import org.json.JSONObject;

class CometChatTypingEvent extends CometChatEvent {

    private String action;
    private TypingIndicator typingIndicator;

    protected CometChatTypingEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_TYPE_TYPING_INDICATOR);
    }

    static CometChatTypingEvent fromJSON(JSONObject mainObject) throws JSONException {
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
        CometChatTypingEvent cometChatTypingEvent = new CometChatTypingEvent(appId, receiver, receiverType, deviceId, sender);
        JSONObject bodyObject = mainObject.getJSONObject(CometChatConstants.WSKeys.KEY_BODY);
        cometChatTypingEvent.setAction(bodyObject.getString(CometChatConstants.WSKeys.KEY_ACTION));
        TypingIndicator receivedTypingIndicator = new TypingIndicator(receiver, receiverType);
        receivedTypingIndicator.setSender(User.fromJson(bodyObject.getJSONObject(CometChatConstants.WSKeys.KEY_USER).toString()));
        if (bodyObject.has(CometChatConstants.WSKeys.KEY_METADATA))
            receivedTypingIndicator.setMetadata(bodyObject.getJSONObject(CometChatConstants.WSKeys.KEY_METADATA));
        receivedTypingIndicator.setTypingStatus(bodyObject.getString(CometChatConstants.WSKeys.KEY_ACTION));
        cometChatTypingEvent.setTypingIndicator(receivedTypingIndicator);
        return cometChatTypingEvent;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public TypingIndicator getTypingIndicator() {
        return typingIndicator;
    }

    public void setTypingIndicator(TypingIndicator typingIndicator) {
        this.typingIndicator = typingIndicator;
    }

    @Override
    protected String getAsString() throws JSONException {
        return getAsJSONObject().toString();
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        JSONObject mainObject = getCometChatEventJSON();
        JSONObject bodyObject = new JSONObject();
        bodyObject.put(CometChatConstants.WSKeys.KEY_ACTION, typingIndicator.getTypingStatus());
        JSONObject userObject = CometChat.getLoggedInUser().toJson();
        bodyObject.put(CometChatConstants.WSKeys.KEY_USER, userObject);
        if (typingIndicator.getMetadata() != null)
            bodyObject.put(CometChatConstants.WSKeys.KEY_METADATA, typingIndicator.getMetadata());
        mainObject.put(CometChatConstants.WSKeys.KEY_BODY, bodyObject);
        return mainObject;
    }


}
