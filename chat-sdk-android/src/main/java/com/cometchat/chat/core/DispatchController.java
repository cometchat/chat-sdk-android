package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.AIAssistantBaseEvent;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.InteractionReceipt;
import com.cometchat.chat.models.ReactionEvent;
import com.cometchat.chat.models.MessageReceipt;
import com.cometchat.chat.models.TransientMessage;
import com.cometchat.chat.models.TypingIndicator;
import com.cometchat.chat.models.User;

import androidx.annotation.Nullable;

public class DispatchController {

    private static DispatchController dispatchControllerInstance = null;

    protected static final String WS_STATE_DISCONNECTED = "disconnected";
    protected static final String WS_STATE_CONNECTING = "connecting";
    protected static final String WS_STATE_CONNECTED = "connected";
    protected static final String WS_STATE_FEATURE_THROTTLED = "featureThrottled";
    protected static final String WS_STATE_ERROR = "onError";

    private MessageReceivedListener messageReceivedListener;
    private PresenceListener presenceListener;
    private TypingListener typingListener;
    private MessageReactionListener messageReactionListener;
    private ReceiptsListener receiptsListener;
    private InteractionsListener interactionsListener;
    private ConnectionStatusListener connectionStatusListener;
    private TransientMessageReceivedListener transientMessageReceivedListener;
    private ModerationStatusListener moderationStatusListener;

    private AIAssistantListener aiAssistantListener;
    static DispatchController getInstance() {
        if (dispatchControllerInstance == null)
            dispatchControllerInstance = new DispatchController();
        return dispatchControllerInstance;
    }

    public void informPresenceListener(User user, CometChatException ce) {
        if (presenceListener != null)
            presenceListener.onUserPresenceChanged(user, ce);
    }

    public void informReceiptsListener(MessageReceipt messageReceipt) {
        if (receiptsListener != null)
            receiptsListener.onMessageReceiptReceived(messageReceipt);
    }

    public void informInteractionsListener(InteractionReceipt interactionReceipt) {
        if (interactionsListener != null)
            interactionsListener.onInteractionGoalCompleted(interactionReceipt);
    }

    public void informMessageReceivedListener(BaseMessage baseMessage) {
        if (messageReceivedListener != null)
            messageReceivedListener.onMessageReceived(baseMessage);
    }

    public void informTransientMessageReceivedListener(TransientMessage transientMessage) {
        if (transientMessageReceivedListener != null)
            transientMessageReceivedListener.onTransientMessageReceived(transientMessage);
    }

    public void informTypingListener(TypingIndicator typingIndicator) {
        if (typingListener != null) {
            if (typingIndicator.getTypingStatus().equalsIgnoreCase(TypingIndicator.TYPING_START))
                typingListener.onUserTypingStart(typingIndicator);
            else if (typingIndicator.getTypingStatus().equalsIgnoreCase(TypingIndicator.TYPING_END))
                typingListener.onUserTypingEnd(typingIndicator);
        }
    }

    public void informMessageReactionListener(CometChatMessageReactionEvent cometChatMessageReactionEvent) {
        if (messageReactionListener != null) {
            if (cometChatMessageReactionEvent.getAction().equalsIgnoreCase(CometChatConstants.WSKeys.KEY_ACTION_MESSAGE_REACTION_ADDED))
                messageReactionListener.onMessageReactionAdded(cometChatMessageReactionEvent.getReaction());
            else if (cometChatMessageReactionEvent.getAction().equalsIgnoreCase(CometChatConstants.WSKeys.KEY_ACTION_MESSAGE_REACTION_REMOVED))
                messageReactionListener.onMessageReactionRemoved(cometChatMessageReactionEvent.getReaction());
        }
    }

    public void informModerationStatusChangedListener(BaseMessage baseMessage) {
        if (moderationStatusListener != null) {
            moderationStatusListener.onModerationStatusChanged(baseMessage);
        }
    }

    public void informAIAssistantListener(AIAssistantBaseEvent event){
        if (aiAssistantListener != null) {
            aiAssistantListener.onAIAssistantEventReceived(event);
        }
    }

    public void informConnectionListener(String connectionState, @Nullable CometChatException error) {
        switch (connectionState) {
            case WS_STATE_DISCONNECTED:
                if (connectionStatusListener != null) {
                    Logger.error("DispatchController", "onDisconnected : " + error);
                    connectionStatusListener.onDisconnected();
                }
                break;
            case WS_STATE_CONNECTING:
                if (connectionStatusListener != null)
                    connectionStatusListener.onConnecting();
                break;
            case WS_STATE_CONNECTED:
                if (connectionStatusListener != null)
                    connectionStatusListener.onConnected();
                break;
            case WS_STATE_FEATURE_THROTTLED:
                if (connectionStatusListener != null)
                    connectionStatusListener.onFeatureThrottled();
                break;
            case WS_STATE_ERROR:
                if (connectionStatusListener != null)
                    connectionStatusListener.onConnectionError(error);
                break;
        }
    }

    public void setMessageReceivedListener(MessageReceivedListener messageReceivedListener) {
        this.messageReceivedListener = messageReceivedListener;
    }

    public void setPresenceListener(PresenceListener presenceListener) {
        this.presenceListener = presenceListener;
    }

    public void setTypingListener(TypingListener typingListener) {
        this.typingListener = typingListener;
    }

    public void setMessageReactionListener(MessageReactionListener messageReactionListener) {
        this.messageReactionListener = messageReactionListener;
    }

    public void setReceiptsListener(ReceiptsListener receiptsListener) {
        this.receiptsListener = receiptsListener;
    }

    public void setInteractionsListener(InteractionsListener interactionsListener) {
        this.interactionsListener = interactionsListener;
    }

    public void setConnectionStatusListener(ConnectionStatusListener connectionStatusListener) {
        this.connectionStatusListener = connectionStatusListener;
    }

    public void setTransientMessageListener(TransientMessageReceivedListener transientMessageReceivedListener) {
        this.transientMessageReceivedListener = transientMessageReceivedListener;
    }

    public void setModerationStatusListener(ModerationStatusListener moderationStatusListener) {
        this.moderationStatusListener = moderationStatusListener;
    }

    public void setAIAssistantListener(AIAssistantListener aiAssistantListener) {
        this.aiAssistantListener = aiAssistantListener;
    }

    interface MessageReceivedListener {
        void onMessageReceived(BaseMessage message);
    }

    interface TransientMessageReceivedListener {
        void onTransientMessageReceived(TransientMessage transientMessage);
    }

    interface PresenceListener {
        void onUserPresenceChanged(User user, CometChatException ce);
    }

    interface TypingListener {
        void onUserTypingStart(TypingIndicator typingIndicator);

        void onUserTypingEnd(TypingIndicator typingIndicator);
    }

    interface MessageReactionListener {
        void onMessageReactionAdded(ReactionEvent reactionEvent);

        void onMessageReactionRemoved(ReactionEvent reactionEvent);
    }

    interface ReceiptsListener {
        void onMessageReceiptReceived(final MessageReceipt messageReceipt);
    }

    interface InteractionsListener {
        void onInteractionGoalCompleted(final InteractionReceipt interactionReceipt);
    }

    interface ModerationStatusListener {
        void onModerationStatusChanged(BaseMessage baseMessage);
    }

    interface AIAssistantListener {
        void onAIAssistantEventReceived(AIAssistantBaseEvent event);
    }

    interface ConnectionStatusListener {
        void onDisconnected();

        void onConnecting();

        void onConnected();

        void onFeatureThrottled();

        void onConnectionError(CometChatException error);
    }

    // region Notification Feed

    private NotificationFeedReceivedListener notificationFeedReceivedListener;

    public void informNotificationFeedListener(org.json.JSONObject messageJson) {
        CometChatNotificationFeedEvent event = CometChatNotificationFeedEvent.fromJson(messageJson);
        if (event.isValid()) {
            CometChat.dispatchNotificationFeedItem(event.getFeedItem());
        }
    }

    public void setNotificationFeedReceivedListener(NotificationFeedReceivedListener listener) {
        this.notificationFeedReceivedListener = listener;
    }

    interface NotificationFeedReceivedListener {
        void onNotificationFeedItemReceived(com.cometchat.chat.models.NotificationFeedItem feedItem);
    }

    // endregion

}
