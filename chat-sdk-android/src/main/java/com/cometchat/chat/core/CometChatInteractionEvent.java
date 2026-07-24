package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Interaction;
import com.cometchat.chat.models.InteractionReceipt;
import com.cometchat.chat.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class CometChatInteractionEvent extends CometChatEvent {
    private InteractionReceipt interactionReceipt;

    CometChatInteractionEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_INTERACTION_COMPLETED);
    }

    public static CometChatInteractionEvent fromJSON(JSONObject mainObject) throws JSONException {
        String appId = null;
        if (mainObject.has(CometChatConstants.WSKeys.KEY_APP_ID))
            appId = mainObject.getString(CometChatConstants.WSKeys.KEY_APP_ID);
        String receiver = null;
        if (mainObject.has(CometChatConstants.WSKeys.KEY_RECEIVER))
            receiver = mainObject.getString(CometChatConstants.WSKeys.KEY_RECEIVER);
        String receiverType = null;
        if (mainObject.has(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE))
            receiverType = mainObject.getString(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE);
        String deviceId = null;
        if (mainObject.has(CometChatConstants.WSKeys.KEY_DEVICE_ID))
            deviceId = mainObject.getString(CometChatConstants.WSKeys.KEY_DEVICE_ID);
        String sender = null;
        if (mainObject.has(CometChatConstants.WSKeys.KEY_SENDER))
            sender = mainObject.getString(CometChatConstants.WSKeys.KEY_SENDER);
        String messageSender = null;
        if (mainObject.has(CometChatConstants.WSKeys.KEY_MESSAGE_SENDER))
            messageSender = mainObject.getString(CometChatConstants.WSKeys.KEY_MESSAGE_SENDER);

        CometChatInteractionEvent cometChatInteractionEvent = new CometChatInteractionEvent(appId, receiver, receiverType, deviceId, sender);
        JSONObject bodyObject = mainObject.getJSONObject(CometChatConstants.WSKeys.KEY_BODY);
        InteractionReceipt receivedInteractionReceipt = new InteractionReceipt();
        receivedInteractionReceipt.setReceiverId(receiver);
        receivedInteractionReceipt.setReceiverType(receiverType);
        receivedInteractionReceipt.setMessageSenderUid(messageSender);
        receivedInteractionReceipt.setSender(User.fromJson(bodyObject.getJSONObject(CometChatConstants.WSKeys.KEY_USER).toString()));
        receivedInteractionReceipt.setMessageId(bodyObject.getLong(CometChatConstants.WSKeys.KEY_MESSAGE_ID));
        JSONArray interactionsArray = bodyObject.getJSONArray(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIONS);
        List<Interaction> interactions = new ArrayList<>();
        for (int i = 0; i < interactionsArray.length(); i++) {
            JSONObject interactionObject = interactionsArray.getJSONObject(i);
            Interaction interaction = Interaction.fromJson(interactionObject);
            interactions.add(interaction);
        }
        receivedInteractionReceipt.setInteractions(interactions);
        cometChatInteractionEvent.setInteractionReceipt(receivedInteractionReceipt);

        return cometChatInteractionEvent;
    }

    public InteractionReceipt getInteractionReceipt() {
        return interactionReceipt;
    }

    public void setInteractionReceipt(InteractionReceipt interactionReceipt) {
        this.interactionReceipt = interactionReceipt;
    }

    @Override
    protected String getAsString() throws JSONException {
        return getAsJSONObject().toString();
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        JSONObject mainObject = getCometChatEventJSON();
        JSONObject bodyObject = new JSONObject();
        bodyObject.put(CometChatConstants.WSKeys.KEY_MESSAGE_ID, interactionReceipt.getMessageId());
        JSONArray interactionsArray = new JSONArray();
        if (interactionReceipt.getInteractions() != null) {
            for (int i = 0; i < interactionReceipt.getInteractions().size(); i++) {
                JSONObject interactionObject = new JSONObject(interactionReceipt.getInteractions().get(i).toMap());
                interactionsArray.put(interactionObject);
            }
        }
        bodyObject.put(CometChatConstants.WSKeys.KEY_USER, interactionReceipt.getSender().toJson());
        bodyObject.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTIONS, interactionsArray);
        mainObject.put(CometChatConstants.WSKeys.KEY_BODY, bodyObject);
        return mainObject;
    }
}
