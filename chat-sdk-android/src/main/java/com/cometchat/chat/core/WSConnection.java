package com.cometchat.chat.core;

import static com.cometchat.chat.constants.CometChatConstants.WS_STATE_ERROR;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.MessageReceipt;
import com.cometchat.chat.models.TransientMessage;
import com.cometchat.chat.models.TypingIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

class WSConnection extends AbstractRTTConnection {

    private static final String TAG = "WSConnection";
    private static final int PONG_RECEIVED_TIMEOUT = 3 * 1000; // 3 seconds
    private AppSettings appSettings;
    private CurrentUser currentUser;
    private Settings settings;
    private OkHttpClient okHttpClient;
    private WebSocket webSocket;
    public static AtomicBoolean isConnectCalled = new AtomicBoolean(false);
    public static AtomicBoolean isDisconnectCalled = new AtomicBoolean(false);

    public static boolean receivedPongFlag = false;
    public static AtomicBoolean isAppInForeground = new AtomicBoolean(true);
    public static boolean isWSConnectionAutoModeEnabled = true;
    private CometChat.CallbackListener wsPingListener = null;
    private final Handler pingTimeoutHandler;
    private Runnable pingTimeoutRunnable;
    private final AtomicBoolean isFailureReconnectionStarted;

    WSConnection(AppSettings appSettings, CurrentUser currentUser, Settings settings) {
        this.appSettings = appSettings;
        this.currentUser = currentUser;
        this.settings = settings;
        this.isFailureReconnectionStarted = new AtomicBoolean(false);
        this.pingTimeoutHandler = new Handler(Looper.getMainLooper());
    }

    private WebSocketListener wsListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            Logger.error(TAG, "onOpen : " + response);
            sendAuthEvent();
        }

        @Override
        public void onMessage(WebSocket webSocket, String response) {
            super.onMessage(webSocket, response);
            Logger.error(TAG + "/RECV", "onMessage : " + response);
            try {
                CometChatEvent cometChatEvent = getCometChatEventFromMessage(response);
                if (cometChatEvent != null) {
                    if (cometChatEvent instanceof CometChatPongEvent) {
                        Logger.error(TAG, "cometChatEvent: " + ((CometChatPongEvent) cometChatEvent).getAction());
                        // Clear the ping timeout as we received a pong
                        removePingTimeoutRunnable();

                        if (wsPingListener != null) {
                            wsPingListener.onSuccess(CometChat.getConnectionStatus());
                            wsPingListener = null;
                        }
                        receivedPongFlag = true;
                    } else if ((cometChatEvent.getDeviceId() == null || !cometChatEvent.getDeviceId().equalsIgnoreCase(CometChatUtils.getResource(false))) || (cometChatEvent instanceof CometChatAuthEvent) || (cometChatEvent instanceof CometChatModerationStatusChangeEvent)) {
                        processMessage(cometChatEvent);
                    } else {
                        Logger.error(TAG, "Message from the same resource. Message dropped");
                    }
                    informWSListener(cometChatEvent);
                } else {
                    Logger.error(TAG, "CometChatEvent cannot be null");
                }
            } catch (Exception e) {
                Logger.error(TAG, "onMessage error: " + e);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            Logger.error(TAG, "onMessage : " + bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            Logger.error(TAG, "onClosing : " + code + " " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Logger.error(TAG, "onClosed : " + code + " " + reason);
            ConnectionController.getInstance().onWSDisconnected(null);
            if (code != CometChatConstants.WSKeys.KEY_CODE_LOGOUT) {
                startReconnection();
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            Logger.error(TAG, "onFailure : " + t.getMessage() + " " + response);

            if (wsPingListener != null) {
                wsPingListener.onError(new CometChatException(WS_STATE_ERROR, t.toString()));
                wsPingListener = null;
            }

            isFailureReconnectionStarted.set(true);
            removePingTimeoutRunnable();

            ConnectionController.getInstance().onWSDisconnected(new CometChatException(WS_STATE_ERROR, t.toString()));

            if (isWSConnectionAutoModeEnabled) {
                if (!isDisconnectCalled.get()) {
                    startReconnection();
                }
            } else {
                if (isConnectCalled.get() && isAppInForeground.get()) {
                    startReconnection();
                }
            }
        }
    };

    @Override
    protected void connect() {
        try {
            ConnectionController.getInstance().onWSConnecting();
            okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
            String host = CometChatUtils.getFinalChatHost(settings);
            String port = settings.getChatWSSPort();
            Request request = new Request.Builder().url("wss://" + host + ":" + port).build();
            webSocket = okHttpClient.newWebSocket(request, wsListener);
            okHttpClient.dispatcher().executorService().shutdown();
            Logger.error(TAG, "WSConnect requested");
        } catch (Exception e) {
            Logger.error(TAG, "WSConnect Error: " + e);
        }
    }

    @Override
    protected void send(CometChatEvent cometChatEvent) {
        try {
            Logger.error(TAG + "/SENT", cometChatEvent.getAsString());
            webSocket.send(cometChatEvent.getAsString());
        } catch (Exception e) {
            Logger.error(TAG + "Errror", e.getMessage());
        }
    }

    @Override
    protected void disconnect() {
        if (webSocket != null) {
            // Clear any pending ping timeout
            removePingTimeoutRunnable();

            webSocket.close(CometChatConstants.WSKeys.KEY_CODE_LOGOUT, CometChatConstants.WSKeys.KEY_CODE_LOGOUT_REASON);
            PingController.getInstance(this).stopPing();
            Logger.error(TAG, "WebSocket connection disconnect with thread id: " + Thread.currentThread().getId());
        }
    }

    @Override
    protected void joinGroup(String GUID) {

    }

    @Override
    protected void leaveGroup(String GUID) {

    }

    @Override
    protected void startTyping(TypingIndicator typingIndicator) {
        try {
            CometChatTypingEvent cometChatEvent = new CometChatTypingEvent(PreferenceHelper.getAppID(),
                                                                           typingIndicator.getReceiverId(),
                                                                           typingIndicator.getReceiverType(),
                                                                           CometChatUtils.getResource(false), currentUser.getUid());
            cometChatEvent.setTypingIndicator(typingIndicator);
            send(cometChatEvent);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void endTyping(TypingIndicator typingIndicator) {
        try {
            CometChatTypingEvent cometChatEvent = new CometChatTypingEvent(PreferenceHelper.getAppID(),
                                                                           typingIndicator.getReceiverId(),
                                                                           typingIndicator.getReceiverType(),
                                                                           CometChatUtils.getResource(false), currentUser.getUid());
            cometChatEvent.setTypingIndicator(typingIndicator);
            send(cometChatEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void markAsDelivered(MessageReceipt messageReceipt, CometChat.CallbackListener<Void> listener) {
        if (ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_DISCONNECTED) ||
            ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_CONNECTING)) {
            CometChat.postError(listener,
                                new CometChatException(CometChatConstants.Errors.ERROR_NO_WEBSOCKET_CONNECTION,
                                                       CometChatConstants.Errors.ERROR_NO_WEBSOCKET_CONNECTION_MESSAGE));
        } else if (ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
            CometChat.postError(listener,
                                new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                       CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
        } else {
            CometChatReceiptEvent cometChatEvent = new CometChatReceiptEvent(PreferenceHelper.getAppID(),
                                                                             messageReceipt.getReceiverId(),
                                                                             messageReceipt.getReceiverType(),
                                                                             CometChatUtils.getResource(false), currentUser.getUid());
            cometChatEvent.setMessageSender(messageReceipt.getMessageSender());
            cometChatEvent.setMessageReceipt(messageReceipt);
            send(cometChatEvent);
            if (listener != null)
                listener.onSuccess(null);
        }

    }

    @Override
    protected void markAsRead(MessageReceipt messageReceipt, CometChat.CallbackListener<Void> listener) {

        if (ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_DISCONNECTED) ||
            ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_CONNECTING)) {
            CometChat.postError(listener,
                                new CometChatException(CometChatConstants.Errors.ERROR_NO_WEBSOCKET_CONNECTION,
                                                       CometChatConstants.Errors.ERROR_NO_WEBSOCKET_CONNECTION_MESSAGE));
        } else if (ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
            CometChat.postError(listener,
                                new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                       CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
        } else {
            CometChatReceiptEvent cometChatEvent = new CometChatReceiptEvent(PreferenceHelper.getAppID(),
                                                                             messageReceipt.getReceiverId(),
                                                                             messageReceipt.getReceiverType(),
                                                                             CometChatUtils.getResource(false), currentUser.getUid());
            cometChatEvent.setMessageSender(messageReceipt.getMessageSender());
            cometChatEvent.setMessageReceipt(messageReceipt);
            send(cometChatEvent);
            if (listener != null)
                listener.onSuccess(null);
        }
    }

    @Override
    protected void sendTransientMessage(TransientMessage transientMessage) {
        CometChatTransientMessageEvent cometChatTransientMessageEvent = new CometChatTransientMessageEvent(PreferenceHelper.getAppID(),
                                                                                                           transientMessage.getReceiverId(),
                                                                                                           transientMessage.getReceiverType(),
                                                                                                           CometChatUtils.getResource(false),
                                                                                                           currentUser.getUid());
        cometChatTransientMessageEvent.setTransientMessage(transientMessage);
        send(cometChatTransientMessageEvent);
    }

    private CometChatEvent getCometChatEventFromMessage(String message) throws JSONException {
        JSONObject mainObject = new JSONObject(message);
        if (mainObject.has(CometChatConstants.WSKeys.KEY_ACTION)) {
            if (mainObject.getString(CometChatConstants.WSKeys.KEY_ACTION).equalsIgnoreCase(CometChatConstants.WSKeys.KEY_ACTION_PONG)) {
                return CometChatPongEvent.fromJSON(mainObject);
            }
        }
        String type = mainObject.getString(CometChatConstants.WSKeys.KEY_TYPE);

        // Notification feed items don't extend CometChatEvent — dispatch directly
        if (CometChatConstants.WS_TYPE_NOTIFICATION_FEED_ITEM.equals(type)) {
            DispatchController.getInstance().informNotificationFeedListener(mainObject);
            return null;
        }

        switch (type) {
            case CometChatConstants.WSKeys.KEY_TYPE_TYPING_INDICATOR:
                return CometChatTypingEvent.fromJSON(mainObject);
            case CometChatConstants.WSKeys.KEY_TYPE_RECEIPTS:
                return CometChatReceiptEvent.fromJSON(mainObject);
            case CometChatConstants.WSKeys.KEY_TYPE_AUTH:
                return CometChatAuthEvent.fromJSON(mainObject);
            case CometChatConstants.WSKeys.KEY_TYPE_PRESENCE:
                return CometChatPresenceEvent.fromJSON(mainObject);
            case CometChatConstants.WSKeys.KEY_TYPE_MESSAGE:
                return CometChatMessageEvent.fromJSON(mainObject);
            case CometChatConstants.WSKeys.KEY_TYPE_TRANSIENT_MESSAGE:
                return CometChatTransientMessageEvent.fromJSON(mainObject);
            case CometChatConstants.WSKeys.KEY_INTERACTION_COMPLETED:
                return CometChatInteractionEvent.fromJSON(mainObject);
            case CometChatConstants.WSKeys.KEY_REACTION:
                return CometChatMessageReactionEvent.fromJSON(mainObject);
            case CometChatConstants.WSKeys.KEY_MODERATION_CHANGED:
                return CometChatModerationStatusChangeEvent.fromJSON(mainObject);
            case CometChatConstants.WSKeys.KEY_STREAMED_MESSAGE:
                return CometChatStreamMessageEvent.fromJSON(mainObject);
        }
        return null;
    }

    private void removePingTimeoutRunnable() {
        if (pingTimeoutRunnable != null) {
            pingTimeoutHandler.removeCallbacks(pingTimeoutRunnable);
            pingTimeoutRunnable = null;
        }
    }

    private void processMessage(CometChatEvent cometChatEvent) {
        switch (cometChatEvent.getType()) {
            case CometChatConstants.WSKeys.KEY_TYPE_TYPING_INDICATOR:
                informTypingListener(((CometChatTypingEvent) cometChatEvent).getTypingIndicator());
                break;
            case CometChatConstants.WSKeys.KEY_TYPE_RECEIPTS:
                informReceiptsListener(((CometChatReceiptEvent) cometChatEvent).getMessageReceipt());
                break;
            case CometChatConstants.WSKeys.KEY_TYPE_AUTH:
                CometChatAuthEvent cometChatAuthEvent = (CometChatAuthEvent) cometChatEvent;
                if (cometChatAuthEvent.getAuthResponse().getCode() == 200 && cometChatAuthEvent
                    .getAuthResponse()
                    .getStatus()
                    .equalsIgnoreCase(CometChatConstants.WSKeys.KEY_STATUS_OK)) {
                    authenticationSuccessful();
                } else {
                    Logger.error(TAG, "Error while connecting to WS");
                }
                break;
            case CometChatConstants.WSKeys.KEY_TYPE_PRESENCE:
                informPresenceListener(((CometChatPresenceEvent) cometChatEvent).getUser(), null);
                break;
            case CometChatConstants.WSKeys.KEY_TYPE_PING:
                break;
            case CometChatConstants.WSKeys.KEY_TYPE_MESSAGE:
                CometChatMessageEvent cometChatMessageEvent = (CometChatMessageEvent) cometChatEvent;
                BaseMessage baseMessage = cometChatMessageEvent.getMessage();
                PreferenceHelper.saveLastDeliveredMessageId(baseMessage.getId());
                informMessageReceivedListener(cometChatMessageEvent.getMessage());
                break;
            case CometChatConstants.WSKeys.KEY_INTERACTION_COMPLETED:
                CometChatInteractionEvent cometChatInteractionEvent = (CometChatInteractionEvent) cometChatEvent;
                informInteractionsListener(cometChatInteractionEvent.getInteractionReceipt());
                break;
            case CometChatConstants.WSKeys.KEY_TYPE_TRANSIENT_MESSAGE:
                CometChatTransientMessageEvent cometChatTransientMessageEvent = (CometChatTransientMessageEvent) cometChatEvent;
                informTransientMessageReceivedListener(cometChatTransientMessageEvent.getTransientMessage());
                break;
            case CometChatConstants.WSKeys.KEY_REACTION:
                informMessageReactionListener(((CometChatMessageReactionEvent) cometChatEvent));
                break;
            case CometChatConstants.WSKeys.KEY_MODERATION_CHANGED:
                CometChatModerationStatusChangeEvent cometChatModerationStatusChangeEvent = (CometChatModerationStatusChangeEvent) cometChatEvent;
                BaseMessage message = cometChatModerationStatusChangeEvent.getMessage();
                informModerationStatusChangedListener(message);
                break;
            case CometChatConstants.WSKeys.KEY_STREAMED_MESSAGE:
                CometChatStreamMessageEvent cometChatStreamMessageEvent = (CometChatStreamMessageEvent) cometChatEvent;
                informAIAssistantListener(cometChatStreamMessageEvent.getEvent());
                break;
        }
    }


    private synchronized void authenticationSuccessful() {
        Logger.error(TAG, "Authentication successful WsConnected");
        isFailureReconnectionStarted.set(false);
        ConnectionController.getInstance().onWSConnected();
        startWebSocketPing();
    }

    @Override
    protected void startWebSocketPing() {
        if (!PingController.getInstance(this).isPingInProgress()) {
            PingController.getInstance(this).startPing();
        }
    }

    @Override
    protected void ping(boolean isInternalPing, @Nullable CometChat.CallbackListener listener) {
        wsPingListener = listener;
        CometChatPingEvent cometChatPingEvent = new CometChatPingEvent("", "", "", "", currentUser.getUid());

        // Schedule the timeout
        if (!isFailureReconnectionStarted.get()) {

            // Cancel any existing ping timeout
            removePingTimeoutRunnable();

            // Set up ping timeout - will trigger reconnection if no pong received
            pingTimeoutRunnable = new Runnable() {
                @Override
                public void run() {
                    receivedPongFlag = false;
                    Logger.error(TAG, "ping timeout - no pong received within 3 seconds");
                    Logger.error(TAG, "ping timeout - trying to communicate with the server");
                    ConnectionController.getInstance().onWSConnecting();
                    // Start reconnection
                    startReconnection();
                }
            };
            pingTimeoutHandler.postDelayed(pingTimeoutRunnable, PONG_RECEIVED_TIMEOUT); // 3 seconds timeout
        }
        // Send the ping
        if (isWSConnectionAutoModeEnabled) {
            send(cometChatPingEvent);
        } else {
            if (isInternalPing && isAppInForeground.get()) {
                send(cometChatPingEvent);
            } else if (!isInternalPing && !isAppInForeground.get()) {
                send(cometChatPingEvent);
            }
        }
    }

    @Override
    protected void stopPing() {
        PingController.getInstance(this).stopPing();
    }

    private synchronized void sendAuthEvent() {
        CometChatAuthEvent cometChatAuthEvent = new CometChatAuthEvent(
            PreferenceHelper.getAppID(),
            "",
            "",
            CometChatUtils.getResource(false),
            currentUser.getUid()
        );
        cometChatAuthEvent.setJwt(currentUser.getJwt());
        cometChatAuthEvent.setPresenceSubscription(appSettings.getSubscriptionType());
        if (appSettings.getSubscriptionType().equalsIgnoreCase(AppSettings.SUBSCRIPTION_TYPE_ROLES)) {
            cometChatAuthEvent.setRoles(appSettings.getRoles());
        }
        send(cometChatAuthEvent);
    }

    @Override
    synchronized void startReconnection() {
        ReconnectionController.getInstance().startReconnection();
    }

    @Override
    synchronized void stopReconnection() {
        ReconnectionController.getInstance().stopReconnection();
    }

}
