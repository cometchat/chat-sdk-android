package com.cometchat.chat.core;


import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.AIAssistantBaseEvent;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.InteractionReceipt;
import com.cometchat.chat.models.MessageReceipt;
import com.cometchat.chat.models.TransientMessage;
import com.cometchat.chat.models.TypingIndicator;
import com.cometchat.chat.models.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public abstract class AbstractRTTConnection {

    private static final String TAG = "AbstractRTTConnection";

    private static ConcurrentHashMap<String , WSListener> wsListeners = new ConcurrentHashMap<>();

    protected abstract void connect();

    protected abstract void disconnect();

    protected abstract void joinGroup(String GUID);

    protected abstract void leaveGroup(String GUID);

    protected abstract void startTyping(TypingIndicator typingIndicator);

    protected abstract void endTyping(TypingIndicator typingIndicator);

    protected abstract void markAsDelivered(MessageReceipt messageReceipt, CometChat.CallbackListener<Void> listener);

    protected abstract void markAsRead(MessageReceipt messageReceipt , CometChat.CallbackListener<Void> listener);

    protected abstract void sendTransientMessage(TransientMessage transientMessage);

    protected abstract void send(CometChatEvent cometChatEvent);

    protected abstract void ping(boolean isInternalPing, @Nullable CometChat.CallbackListener listener);

    protected abstract void stopPing();

    protected abstract void startWebSocketPing();

    abstract void startReconnection();

    abstract void stopReconnection();


    protected void informPresenceListener(User user, CometChatException ce) {
        DispatchController.getInstance().informPresenceListener(user, ce);
    }

    protected void informReceiptsListener(MessageReceipt messageReceipt) {
        DispatchController.getInstance().informReceiptsListener(messageReceipt);
    }

    protected void informInteractionsListener(InteractionReceipt interactionReceipt) {
        DispatchController.getInstance().informInteractionsListener(interactionReceipt);
    }

    protected void informMessageReceivedListener(BaseMessage baseMessage) {
        DispatchController.getInstance().informMessageReceivedListener(baseMessage);
    }

    protected void informTransientMessageReceivedListener(TransientMessage transientMessage){
        DispatchController.getInstance().informTransientMessageReceivedListener(transientMessage);
    }

    protected void informTypingListener(TypingIndicator typingIndicator) {
        DispatchController.getInstance().informTypingListener(typingIndicator);
    }

    protected void informMessageReactionListener(CometChatMessageReactionEvent cometChatMessageReactionEvent) {
        DispatchController.getInstance().informMessageReactionListener(cometChatMessageReactionEvent);
    }

    protected void informModerationStatusChangedListener(BaseMessage baseMessage) {
        DispatchController.getInstance().informModerationStatusChangedListener(baseMessage);
    }

    protected void informAIAssistantListener(AIAssistantBaseEvent event) {
        DispatchController.getInstance().informAIAssistantListener(event);
    }
    protected void informConnectionListener(String connectionState) {
        DispatchController.getInstance().informConnectionListener(connectionState, null);
    }

    void addWSListener(String listenerId, WSListener listener){
        wsListeners.put(listenerId, listener);
    }

    void removeWSListener(String listenerId){
        wsListeners.remove(listenerId);
    }

    protected void  informWSListener(CometChatEvent cometChatEvent){
        for (Map.Entry<String, WSListener> entry : wsListeners.entrySet()) {
            entry.getValue().onMessage(cometChatEvent);
        }
    }

    interface WSListener {

        void onMessage(CometChatEvent cometChatEvent);

    }
}
