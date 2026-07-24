package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class CometChatPresenceEvent extends CometChatEvent{

    private String action;
    private User user;
    private long timestamp;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    CometChatPresenceEvent(String appId, String receiver, String receiverType, String deviceId, String sender){
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_TYPE_PRESENCE);
    }

    @Override
    protected String getAsString() throws JSONException {
        return getAsJSONObject().toString();
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        return null;
    }

    static CometChatPresenceEvent fromJSON(JSONObject mainObject) throws JSONException {
        String appId = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_APP_ID))
            appId = mainObject.getString(CometChatConstants.WSKeys.KEY_APP_ID);
        String deviceId = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_DEVICE_ID))
            deviceId = mainObject.getString(CometChatConstants.WSKeys.KEY_DEVICE_ID);
        String sender = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_SENDER))
            sender = mainObject.getString(CometChatConstants.WSKeys.KEY_SENDER);
        CometChatPresenceEvent cometChatPresenceEvent = new CometChatPresenceEvent(appId, null, null, deviceId, sender);
        JSONObject bodyObject = mainObject.getJSONObject(CometChatConstants.WSKeys.KEY_BODY);
        cometChatPresenceEvent.setAction(bodyObject.getString(CometChatConstants.WSKeys.KEY_ACTION));
        cometChatPresenceEvent.setTimestamp(bodyObject.getLong(CometChatConstants.WSKeys.KEY_TIMESTAMP));
        User user = User.fromJson(bodyObject.getJSONObject(CometChatConstants.WSKeys.KEY_USER).toString());
        user.setLastActiveAt(cometChatPresenceEvent.getTimestamp());
        String status = cometChatPresenceEvent.getAction();
        if(status.equalsIgnoreCase("available"))
            status = "online";
        user.setStatus(status);
        cometChatPresenceEvent.setUser(user);
        return cometChatPresenceEvent;
    }
}
