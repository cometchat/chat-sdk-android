package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class CometChatPongEvent extends CometChatEvent{

    private String action;

    CometChatPongEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_TYPE_PONG);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    protected String getAsString() throws JSONException {
        return null;
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        return null;
    }

    static CometChatPongEvent fromJSON(JSONObject mainObject) throws JSONException {
        CometChatPongEvent cometChatPongEvent = new CometChatPongEvent("","","","","");
        if(mainObject.has(CometChatConstants.WSKeys.KEY_ACTION)){
            cometChatPongEvent.setAction(CometChatConstants.WSKeys.KEY_ACTION_PONG);
        }
        return cometChatPongEvent;
    }
}
