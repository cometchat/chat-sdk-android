package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class CometChatPingEvent extends CometChatEvent {

    CometChatPingEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_TYPE_PING);
    }

    @Override
    protected String getAsString() throws JSONException {
        return getAsJSONObject().toString();
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        JSONObject pingObject = new JSONObject();
        pingObject.put(CometChatConstants.WSKeys.KEY_ACTION, CometChatConstants.WSKeys.KEY_ACTION_PING);
        pingObject.put(CometChatConstants.WSKeys.KEY_ACK, true);
        return pingObject;
    }
}
