package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.ReactionEvent;

import org.json.JSONException;
import org.json.JSONObject;

public class CometChatMessageReactionEvent extends CometChatEvent {
    private String action;
    private ReactionEvent reaction;

    protected CometChatMessageReactionEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_REACTION);
    }

    static CometChatMessageReactionEvent fromJSON(JSONObject mainObject) throws JSONException {
        String appId = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_APP_ID))
            appId = mainObject.getString(CometChatConstants.WSKeys.KEY_APP_ID);
        String receiver = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_RECEIVER))
            receiver = mainObject.getString(CometChatConstants.WSKeys.KEY_RECEIVER);
        String receiverType = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE))
            receiverType = mainObject.getString(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE);
        String sender = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_SENDER))
            sender = mainObject.getString(CometChatConstants.WSKeys.KEY_SENDER);
        String deviceId = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_DEVICE_ID))
            deviceId = mainObject.getString(CometChatConstants.WSKeys.KEY_DEVICE_ID);

        CometChatMessageReactionEvent cometChatMessageReactionEvent = new CometChatMessageReactionEvent(appId, receiver, receiverType, deviceId, sender);
        JSONObject bodyObject = mainObject.getJSONObject(CometChatConstants.WSKeys.KEY_BODY);
        JSONObject responseJson = new JSONObject();
        if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_RECEIVER_ID)){
            responseJson.put(CometChatConstants.ReactionsKeys.KEY_RECEIVER_ID, mainObject.getString(CometChatConstants.ReactionsKeys.KEY_RECEIVER_ID));
        }
        if (mainObject.has(CometChatConstants.ReactionsKeys.KEY_RECEIVER_TYPE)){
            responseJson.put(CometChatConstants.ReactionsKeys.KEY_RECEIVER_TYPE, mainObject.getString(CometChatConstants.ReactionsKeys.KEY_RECEIVER_TYPE));
        }
        if (bodyObject.has(CometChatConstants.ReactionsKeys.KEY_CONVERSATION_ID)){
            responseJson.put(CometChatConstants.ReactionsKeys.KEY_CONVERSATION_ID, bodyObject.getString(CometChatConstants.ReactionsKeys.KEY_CONVERSATION_ID));
        }
        if (bodyObject.has(CometChatConstants.ReactionsKeys.KEY_PARENT_ID)){
            responseJson.put(CometChatConstants.ReactionsKeys.KEY_PARENT_ID, bodyObject.getLong(CometChatConstants.ReactionsKeys.KEY_PARENT_ID));
        }
        if (bodyObject.has(CometChatConstants.WSKeys.KEY_ACTION)){
            cometChatMessageReactionEvent.setAction(bodyObject.getString(CometChatConstants.WSKeys.KEY_ACTION));
        }
        responseJson.put(CometChatConstants.ReactionsKeys.KEY_REACTION, bodyObject);
        cometChatMessageReactionEvent.setReaction(ReactionEvent.fromJson(responseJson));
        return cometChatMessageReactionEvent;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ReactionEvent getReaction() {
        return reaction;
    }

    public void setReaction(ReactionEvent reaction) {
        this.reaction = reaction;
    }

    @Override
    protected String getAsString() throws JSONException {
        return getAsJSONObject().toString();
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        JSONObject bodyObject = new JSONObject();
        JSONObject mainObject = getCometChatEventJSON();
        bodyObject.put(CometChatConstants.WSKeys.KEY_ACTION, action);
        mainObject.put(CometChatConstants.WSKeys.KEY_REACTION, reaction.toString());
        return mainObject;
    }


}
