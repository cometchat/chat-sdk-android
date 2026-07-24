package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.CometChatHelper;
import com.cometchat.chat.models.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class CometChatModerationStatusChangeEvent extends CometChatEvent {
    private BaseMessage message;

    public CometChatModerationStatusChangeEvent(String appId, String receiver, String receiverType, String sender, String deviceId) {
        super(appId, receiver, receiverType, sender, deviceId);
        setType(CometChatConstants.WSKeys.KEY_MODERATION_CHANGED);
    }

    @Override
    protected String getAsString() throws JSONException {
        return null;
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        return null;
    }

    public BaseMessage getMessage() {
        return message;
    }

    public void setMessage(BaseMessage message) {
        this.message = message;
    }

    public static CometChatModerationStatusChangeEvent fromJSON(JSONObject json) throws JSONException {
        String appId = null;
        if (json.has(CometChatConstants.WSKeys.KEY_APP_ID))
            appId = json.optString(CometChatConstants.WSKeys.KEY_APP_ID);

        String receiver = null;
        if (json.has(CometChatConstants.WSKeys.KEY_RECEIVER))
            receiver = json.optString(CometChatConstants.WSKeys.KEY_RECEIVER);

        String receiverType = null;
        if (json.has(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE))
            receiverType = json.optString(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE);

        String sender = null;
        if (json.has(CometChatConstants.WSKeys.KEY_SENDER))
            sender = json.optString(CometChatConstants.WSKeys.KEY_SENDER);

        String deviceId = null;
        if (json.has(CometChatConstants.WSKeys.KEY_DEVICE_ID))
            deviceId = json.optString(CometChatConstants.WSKeys.KEY_DEVICE_ID);

        JSONObject body = null;
        if (json.has(CometChatConstants.WSKeys.KEY_BODY))
            body = json.optJSONObject(CometChatConstants.WSKeys.KEY_BODY);

        CometChatModerationStatusChangeEvent event = new CometChatModerationStatusChangeEvent(appId, receiver, receiverType, sender, deviceId);
        BaseMessage messageObj = null;

        if (body != null) {
            messageObj = CometChatHelper.processMessage(body);
        }

        event.setMessage(messageObj);
        return event;
    }
}

