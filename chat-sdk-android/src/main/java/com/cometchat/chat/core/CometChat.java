package com.cometchat.chat.core;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.enums.AttachmentType;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.CometChatHelper;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.AIAssistantBaseEvent;
import com.cometchat.chat.models.AIAssistantMessage;
import com.cometchat.chat.models.AIAssistantToolStartedEvent;
import com.cometchat.chat.models.AIToolArgumentMessage;
import com.cometchat.chat.models.AIToolResultMessage;
import com.cometchat.chat.models.Action;
import com.cometchat.chat.models.Attachment;
import com.cometchat.chat.models.AudioMode;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.CCExtension;
import com.cometchat.chat.models.CardMessage;
import com.cometchat.chat.models.Conversation;
import com.cometchat.chat.models.ConversationUpdateSettings;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.CustomMessage;
import com.cometchat.chat.models.FlagDetail;
import com.cometchat.chat.models.FlagReason;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.InteractionReceipt;
import com.cometchat.chat.models.InteractiveMessage;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.ReactionEvent;
import com.cometchat.chat.models.MessageReceipt;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.TransientMessage;
import com.cometchat.chat.models.TypingIndicator;
import com.cometchat.chat.models.User;

import com.cometchat.chat.models.NotificationFeedItem;
import com.cometchat.chat.models.PushNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * The class that the CometChat Pro Android SDK exposes to the developers to perform most of the operations.
 */

public final class CometChat {

    private static final String TAG = CometChat.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT = 45;
    private static Context context;
    private static CometChat cometChatInstance;
    private static Handler mainThreadHandler;
    private static ConcurrentHashMap<String, MessageListener> messageListeners = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String,AIAssistantListener> aiAssistantListeners = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, UserListener> userListeners = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, GroupListener> groupListeners = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, CallListener> callListeners = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, TypingIndicator> startTypingMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, TypingIndicator> endTypingMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ConnectionListener> connectionListeners = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, LoginListener> loginListeners = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, NotificationFeedListener> notificationFeedListeners = new ConcurrentHashMap<>();
    private static OngoingCallListener ongoingCallListener;
    private static boolean loginWithUIDInProgress = false;
    private static boolean loginWithAuthTokenInProgress = false;
    private static boolean isInitialized = false;
    private static AppSettings appSettings;
    private static User loggedInUser;
    private static AbstractRTTConnection rttConnection;
    private static int getSettingsRetryCounter;
    private static ScheduledExecutorService reconnectExecutorServiceManualMode;
    private static ScheduledExecutorService callingExecutorService;
    private static CallbackListener wsConnectListener;
    private static CallbackListener wsDisconnectListener;

    private static void setupWSListeners() {
        DispatchController.getInstance().setMessageReceivedListener(messageReceivedListener);
        DispatchController.getInstance().setPresenceListener(presenceListener);
        DispatchController.getInstance().setTypingListener(typingListener);
        DispatchController.getInstance().setReceiptsListener(receiptsListener);
        DispatchController.getInstance().setInteractionsListener(interactionsListener);
        DispatchController.getInstance().setConnectionStatusListener(connectionStatusListener);
        DispatchController.getInstance().setTransientMessageListener(transientMessageReceivedListener);
        DispatchController.getInstance().setMessageReactionListener(messageReactionListener);
        DispatchController.getInstance().setModerationStatusListener(moderationStatusListener);
        DispatchController.getInstance().setAIAssistantListener(aiAssistantListener);
    }

    private static void setupPollingListeners() {
        DispatchController.getInstance().setMessageReceivedListener(messageReceivedListener);
        DispatchController.getInstance().setConnectionStatusListener(connectionStatusListener);
    }

    static {
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    static String getPackageName() {
        return context.getPackageName();
    }

    static String getDeviceUniqueId() {
        return CometChatUtils.getDeviceUniqueId(context);
    }

    private static DispatchController.ReceiptsListener receiptsListener = new DispatchController.ReceiptsListener() {
        @Override
        public void onMessageReceiptReceived(final MessageReceipt messageReceipt) {
            Iterator it = messageListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (messageReceipt.getReceiptType().equalsIgnoreCase(MessageReceipt.RECEIPT_TYPE_DELIVERED))
                                ((MessageListener) pair.getValue()).onMessagesDelivered(messageReceipt);
                            else if (messageReceipt.getReceiptType().equalsIgnoreCase(MessageReceipt.RECEIPT_TYPE_READ))
                                ((MessageListener) pair.getValue()).onMessagesRead(messageReceipt);
                            else if (messageReceipt.getReceiptType().equalsIgnoreCase(MessageReceipt.RECEIPT_TYPE_DELIVERED_TO_ALL))
                                ((MessageListener) pair.getValue()).onMessagesDeliveredToAll(messageReceipt);
                            else if (messageReceipt.getReceiptType().equalsIgnoreCase(MessageReceipt.RECEIPT_TYPE_READ_BY_ALL))
                                ((MessageListener) pair.getValue()).onMessagesReadByAll(messageReceipt);
                        }
                    });
                }
            }
        }
    };

    private static DispatchController.InteractionsListener interactionsListener = new DispatchController.InteractionsListener() {
        @Override
        public void onInteractionGoalCompleted(final InteractionReceipt interactionReceipt) {
            Iterator it = messageListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MessageListener) pair.getValue()).onInteractionGoalCompleted(interactionReceipt);
                        }
                    });
                }
            }
        }
    };

    private static DispatchController.ConnectionStatusListener connectionStatusListener = new DispatchController.ConnectionStatusListener() {
        @Override
        public void onDisconnected() {
            if (wsDisconnectListener != null) {
                wsDisconnectListener.onSuccess(getConnectionStatus());
                wsDisconnectListener = null;
            }
            Iterator it = connectionListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            Logger.error(TAG, "onDisconnected: ");
                            ((ConnectionListener) pair.getValue()).onDisconnected();
                        }
                    });
                }
            }
        }

        @Override
        public void onConnecting() {
            Iterator it = connectionListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ConnectionListener) pair.getValue()).onConnecting();
                        }
                    });
                }
            }
        }

        @Override
        public void onConnected() {
            if (wsConnectListener != null) {
                wsConnectListener.onSuccess(getConnectionStatus());
                wsConnectListener = null;
            }
            Iterator it = connectionListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ConnectionListener) pair.getValue()).onConnected();
                        }
                    });
                }
            }
        }

        @Override
        public void onFeatureThrottled() {
            Iterator it = connectionListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ConnectionListener) pair.getValue()).onFeatureThrottled();
                        }
                    });
                }
            }
        }

        @Override
        public void onConnectionError(final CometChatException error) {
            if (wsConnectListener != null) {
                wsConnectListener.onError(error);
                wsConnectListener = null;
            }
            if (wsDisconnectListener != null) {
                wsDisconnectListener.onError(error);
                wsDisconnectListener = null;
            }
            Iterator it = connectionListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ConnectionListener) pair.getValue()).onConnectionError(error);
                        }
                    });
                }
            }
        }
    };


    public static String getConnectionStatus() {
        return ConnectionController.getInstance().getConnectionStatus();
    }

    private static AnalyticsController.AnalyticsPingListener analyticsPingListener = new AnalyticsController.AnalyticsPingListener() {
        @Override
        public void onAuthExpired() {
            Logger.error(TAG, "onAuthExpired : ");
            CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
            final Settings settings = SettingsRepo.getSettings();
            if (currentUser != null) {
                Logger.error(TAG, "onAuthExpired : Current User found");
                ApiConnection.getInstance().login(currentUser.getAuthToken(), new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, CometChatException ce) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            final CurrentUser updatedUser = CurrentUser.fromJson(jsonObject
                                                                                     .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                     .toString());
                            CurrentUserRepo.insertCurrentUser(updatedUser.toMap());
                            JSONObject settingsObject = jsonObject
                                .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                .getJSONObject(CometChatConstants.ResponseKeys.KEY_SETTINGS);
                            final Settings updatedSettings = Settings.fromJson(settingsObject.toString());
                            if (settings.getSettingsHash() != null) {
                                if (!settings.getSettingsHash().equalsIgnoreCase(updatedSettings.getSettingsHash())) {
                                    Logger.error(TAG, "onAuthExpired : settings hash mismatch");
                                    restart(false);
                                }
                            } else {
                                Logger.error(TAG, "onAuthExpired : Settings hash not present");
                                restart(false);
                            }
                        } catch (Exception e) {
                            Logger.error(TAG, "onAuthExpired() : " + e.getMessage());
                        }
                    }
                });
            } else {
                Logger.error(TAG, "User not logged in");
            }
        }

        @Override
        public void onSettingsUpdated() {
            Logger.error(TAG, "onSettingsUpdated : ");
            restart(false);
        }
    };

    private static DispatchController.MessageReceivedListener messageReceivedListener = new DispatchController.MessageReceivedListener() {
        @Override
        public void onMessageReceived(final BaseMessage baseMessage) {
            final BaseMessage extMessage = ExtensionManager.callOnMessageReceived(baseMessage);
            final String category = extMessage.getCategory();
            if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CALL)) {
                final Call call = (Call) extMessage;
                switch (call.getCallStatus()) {
                    case CometChatConstants.CALL_STATUS_INITIATED:
                        if (getActiveCall() != null && getActiveCall().getSessionId().equalsIgnoreCase(call.getSessionId()))
                            CallManager.getInstance().onCallInitiated();
                        break;
                    case CometChatConstants.CALL_STATUS_CANCELLED:
                        if (getActiveCall() != null && getActiveCall().getSessionId().equalsIgnoreCase(call.getSessionId()))
                            CallManager.getInstance().onCallCancelled();
                        break;
                    case CometChatConstants.CALL_STATUS_ONGOING:
                        if (getActiveCall() != null && getActiveCall().getSessionId().equalsIgnoreCase(call.getSessionId())) {
                            CallManager.getInstance().onCallJoined(call);
                        }
                        break;
                    case CometChatConstants.CALL_STATUS_BUSY:
                        if (getActiveCall() != null && getActiveCall().getSessionId().equalsIgnoreCase(call.getSessionId()))
                            CallManager.getInstance().onCallBusy();
                        break;
                    case CometChatConstants.CALL_STATUS_UNANSWERED:
                        if (getActiveCall() != null && getActiveCall().getSessionId().equalsIgnoreCase(call.getSessionId()))
                            CallManager.getInstance().onCallUnanswered();
                        break;
                    case CometChatConstants.CALL_STATUS_ENDED:
                        if (getActiveCall() != null && getActiveCall().getSessionId().equalsIgnoreCase(call.getSessionId()))
                            CallManager.getInstance().onCallEnded();
                        break;
                    case CometChatConstants.CALL_STATUS_REJECTED:
                        if (getActiveCall() != null && getActiveCall().getSessionId().equalsIgnoreCase(call.getSessionId()))
                            CallManager.getInstance().onCallRejected();
                        break;
                }
                Iterator it = callListeners.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry pair = (Map.Entry) it.next();
                    if (pair.getValue() != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (call.getCallStatus().equalsIgnoreCase(CometChatConstants.CALL_STATUS_INITIATED)) {
                                    ((CallListener) pair.getValue()).onIncomingCallReceived(call);
                                } else if (call.getCallStatus().equalsIgnoreCase(CometChatConstants.CALL_STATUS_ONGOING)) {
                                    if (call.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                                        if (((User) call.getCallInitiator()).getUid().equalsIgnoreCase(CurrentUserRepo.getCurrentUser().getUid())) {
                                            ((CallListener) pair.getValue()).onOutgoingCallAccepted(call);
                                        }
                                    } else {
                                        ((CallListener) pair.getValue()).onOutgoingCallAccepted(call);
                                    }
                                } else if (call.getCallStatus().equalsIgnoreCase(CometChatConstants.CALL_STATUS_CANCELLED)) {
                                    ((CallListener) pair.getValue()).onIncomingCallCancelled(call);
                                } else if (call.getCallStatus().equalsIgnoreCase(CometChatConstants.CALL_STATUS_ENDED)) {
                                    if (ongoingCallListener != null) {
                                        ongoingCallListener.onCallEnded(call);
                                    }
                                    ((CallListener) pair.getValue()).onCallEndedMessageReceived(call);
                                } else if (call.getCallStatus().equalsIgnoreCase(CometChatConstants.CALL_STATUS_UNANSWERED)) {
                                    if (((User) call.getCallInitiator()).getUid().equalsIgnoreCase(CurrentUserRepo.getCurrentUser().getUid())) {
                                        ((CallListener) pair.getValue()).onOutgoingCallRejected(call);
                                    } else {
                                        ((CallListener) pair.getValue()).onIncomingCallCancelled(call);
                                    }
                                } else {
                                    if (call.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                                        if (((User) call.getCallInitiator()).getUid().equalsIgnoreCase(CurrentUserRepo.getCurrentUser().getUid())) {
                                            ((CallListener) pair.getValue()).onOutgoingCallRejected(call);
                                        }
                                    } else {
                                        ((CallListener) pair.getValue()).onOutgoingCallRejected(call);
                                    }
                                }
                            }
                        });

                    }
                }
            } else if (extMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_ACTION)) {
                if (extMessage.getType().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_TYPE_GROUP_MEMBER)) {
                    Iterator it = groupListeners.entrySet().iterator();
                    while (it.hasNext()) {
                        final Map.Entry pair = (Map.Entry) it.next();
                        if (pair.getValue() != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    Action receivedAction = (Action) extMessage;
                                    if ((receivedAction.getAction().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_JOINED))) {
                                        ((GroupListener) pair.getValue()).onGroupMemberJoined(receivedAction,
                                                                                              (User) receivedAction.getActionBy(),
                                                                                              (Group) receivedAction.getActionFor());
                                    } else if (receivedAction.getAction().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_LEFT)) {
                                        ((GroupListener) pair.getValue()).onGroupMemberLeft(receivedAction,
                                                                                            (User) receivedAction.getActionBy(),
                                                                                            (Group) receivedAction.getActionFor());
                                    } else if (receivedAction.getAction().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_KICKED)) {
                                        ((GroupListener) pair.getValue()).onGroupMemberKicked(receivedAction,
                                                                                              (User) receivedAction.getActionOn(),
                                                                                              (User) receivedAction.getActionBy(),
                                                                                              (Group) receivedAction.getActionFor());
                                    } else if (receivedAction.getAction().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_BANNED)) {
                                        ((GroupListener) pair.getValue()).onGroupMemberBanned(receivedAction,
                                                                                              (User) receivedAction.getActionOn(),
                                                                                              (User) receivedAction.getActionBy(),
                                                                                              (Group) receivedAction.getActionFor());
                                    } else if (receivedAction.getAction().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_UNBANNED)) {
                                        ((GroupListener) pair.getValue()).onGroupMemberUnbanned(receivedAction,
                                                                                                (User) receivedAction.getActionOn(),
                                                                                                (User) receivedAction.getActionBy(),
                                                                                                (Group) receivedAction.getActionFor());
                                    } else if (receivedAction.getAction().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_SCOPE_CHANGED)) {
                                        ((GroupListener) pair.getValue()).onGroupMemberScopeChanged(receivedAction,
                                                                                                    (User) receivedAction.getActionBy(),
                                                                                                    (User) receivedAction.getActionOn(),
                                                                                                    receivedAction.getNewScope(),
                                                                                                    receivedAction.getOldScope(),
                                                                                                    (Group) receivedAction.getActionFor());
                                    } else if (receivedAction.getAction().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_MEMBER_ADDED)) {
                                        ((GroupListener) pair.getValue()).onMemberAddedToGroup(receivedAction,
                                                                                               (User) receivedAction.getActionBy(),
                                                                                               (User) receivedAction.getActionOn(),
                                                                                               (Group) receivedAction.getActionFor());
                                    }
                                }
                            });
                        }
                    }
                } else if (extMessage.getType().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_TYPE_MESSAGE)) {
                    Iterator it = messageListeners.entrySet().iterator();
                    while (it.hasNext()) {
                        final Map.Entry pair = (Map.Entry) it.next();
                        if (pair.getValue() != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    Action receivedAction = (Action) extMessage;
                                    if (receivedAction.getAction().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                        ((MessageListener) pair.getValue()).onMessageEdited(((BaseMessage) ((Action) extMessage).getActionOn()));
                                    } else if (receivedAction.getAction().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED)) {
                                        ((MessageListener) pair.getValue()).onMessageDeleted(((BaseMessage) ((Action) extMessage).getActionOn()));
                                    }
                                }
                            });
                        }
                    }
                }
            } else if (extMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM)) {
                Iterator it = messageListeners.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry pair = (Map.Entry) it.next();
                    if (pair.getValue() != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MessageListener) pair.getValue()).onCustomMessageReceived((CustomMessage) extMessage);
                            }
                        });
                    }
                }
            } else if (extMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_INTERACTIVE)) {
                Iterator it = messageListeners.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry pair = (Map.Entry) it.next();
                    if (pair.getValue() != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MessageListener) pair.getValue()).onInteractiveMessageReceived((InteractiveMessage) extMessage);
                            }
                        });
                    }
                }
            } else if(extMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_AGENTIC)){
                Iterator it = messageListeners.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry pair = (Map.Entry) it.next();
                    if (pair.getValue() != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                    if (extMessage.getType().equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_ASSISTANT)) {
                                        ((MessageListener) pair.getValue()).onAIAssistantMessageReceived((AIAssistantMessage) extMessage);
                                    }
                                    else if (extMessage.getType().equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_TOOL_ARGUMENTS))
                                        ((MessageListener) pair.getValue()).onAIToolArgumentsReceived((AIToolArgumentMessage) extMessage);
                                    else if(extMessage.getType().equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_TOOL_RESULT))
                                        ((MessageListener) pair.getValue()).onAIToolResultReceived((AIToolResultMessage) extMessage);
                            }
                        });
                    }
                }

            } else if(extMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_CARD)){
                Iterator it = messageListeners.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry pair = (Map.Entry) it.next();
                    if (pair.getValue() != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MessageListener) pair.getValue()).onCardMessageReceived((CardMessage) extMessage);
                            }
                        });
                    }
                }

            } else {
                Iterator it = messageListeners.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry pair = (Map.Entry) it.next();
                    if (pair.getValue() != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_MESSAGE)) {
                                    if (extMessage.getType().equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_TEXT))
                                        ((MessageListener) pair.getValue()).onTextMessageReceived((TextMessage) extMessage);
                                    else if (extMessage.getType().equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_CUSTOM))
                                        ((MessageListener) pair.getValue()).onCustomMessageReceived((CustomMessage) extMessage);
                                    else
                                        ((MessageListener) pair.getValue()).onMediaMessageReceived((MediaMessage) extMessage);
                                }
                            }
                        });
                    }
                }
            }
        }
    };

    private static DispatchController.TransientMessageReceivedListener transientMessageReceivedListener = new DispatchController.TransientMessageReceivedListener() {
        @Override
        public void onTransientMessageReceived(final TransientMessage transientMessage) {
            Iterator it = messageListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MessageListener) pair.getValue()).onTransientMessageReceived(transientMessage);
                        }
                    });
                }
            }
        }
    };

    private static DispatchController.TypingListener typingListener = new DispatchController.TypingListener() {
        @Override
        public void onUserTypingStart(final TypingIndicator typingIndicator) {
            Iterator it = messageListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MessageListener) pair.getValue()).onTypingStarted(typingIndicator);
                        }
                    });
                }
            }
        }

        @Override
        public void onUserTypingEnd(final TypingIndicator typingIndicator) {
            Iterator it = messageListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MessageListener) pair.getValue()).onTypingEnded(typingIndicator);
                        }
                    });
                }
            }
        }
    };

    private static DispatchController.MessageReactionListener messageReactionListener = new DispatchController.MessageReactionListener() {
        @Override
        public void onMessageReactionAdded(final ReactionEvent reactionEvent) {
            Iterator it = messageListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MessageListener) pair.getValue()).onMessageReactionAdded(reactionEvent);
                        }
                    });
                }
            }
        }

        @Override
        public void onMessageReactionRemoved(final ReactionEvent reactionEvent) {
            Iterator it = messageListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MessageListener) pair.getValue()).onMessageReactionRemoved(reactionEvent);
                        }
                    });
                }
            }
        }
    };

    private static DispatchController.PresenceListener presenceListener = new DispatchController.PresenceListener() {
        @Override
        public void onUserPresenceChanged(final User user, final CometChatException ce) {
            Iterator it = userListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ce != null) {
                                Logger.error(TAG, "Exception when presence updated");
                            } else {
                                if (user.getStatus().equalsIgnoreCase(CometChatConstants.USER_STATUS_OFFLINE))
                                    ((UserListener) pair.getValue()).onUserOffline(user);
                                else if (user.getStatus().equalsIgnoreCase(CometChatConstants.USER_STATUS_ONLINE))
                                    ((UserListener) pair.getValue()).onUserOnline(user);
                            }
                        }
                    });
                }
            }
        }
    };

    private static DispatchController.ModerationStatusListener moderationStatusListener = new DispatchController.ModerationStatusListener() {
        @Override
        public void onModerationStatusChanged(final BaseMessage baseMessage) {
            Iterator it = messageListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MessageListener) pair.getValue()).onMessageModerated(baseMessage);
                        }
                    });
                }
            }
        }
    };

    private static DispatchController.AIAssistantListener aiAssistantListener = new DispatchController.AIAssistantListener() {
        @Override
        public void onAIAssistantEventReceived(final AIAssistantBaseEvent event) {
            Iterator it = aiAssistantListeners.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue() != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ((AIAssistantListener) pair.getValue()).onAIAssistantEventReceived(event);
                        }
                    });
                }
            }
        }
    };

    private static CallManager.CallEventListener callEventListener = new CallManager.CallEventListener() {
        @Override
        public void onCallUnanswered(Call call) {
            Logger.error(TAG, "onCallUnanswered: callObject ==>> " + call);
            if (call != null) {
                try {
                    Logger.error(TAG, "onCallUnanswered: callObject ==>> in side if block: " + call);
                    sendUnansweredResponse(call.getSessionId(), new CallbackListener<Call>() {
                                               @Override
                                               public void onSuccess(Call call) {
                                                   messageReceivedListener.onMessageReceived(call);
                                               }

                                               @Override
                                               public void onError(CometChatException e) {
                                                   Logger.error(TAG, "onCallUnanswered: onError ==>> " + e.getMessage());
                                               }
                                           }
                    );
                } catch (Exception e) {
                    Logger.error(TAG, "onCallUnanswered: Exception ==>>:" + e);
                }
            } else {
                Logger.error(TAG, "onCallUnanswered: callObject ==>> is null" + call);
            }
        }
    };

    /**
     * {@inheritDoc}
     * This method is used to initialize the SDK with the various parameters required for the SDK to function as expected. No other methods can be called unless the <code>init()</code> method is called.
     *
     * @param appContext        - An object of the  @see android.content.Context Context class that provides the SDK with the context of the application in which the SDK is being integrated.
     * @param appID             - App ID of the CometChat App created. This can be found at the CometChat Dashboard {@link "https://app.cometchat.io"}
     * @param globalAppSettings - An object of the @see com.cometchat.chat.core.AppSettings AppSettings class, that holds basic settings related to the SDK.
     * @param listener          - An object of the  <code>CallbackListener&lt;String&gt;</code> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @docs {@link "https://www.cometchat.com/docs/android-chat-sdk/overview#initialize-cometchat"}
     *
     * <b>Note</b>
     * This should be the first method call before using any other method of CometChat SDK.
     * <code>init()</code> to be callled only once in the app lifecycle
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void init(@NonNull Context appContext,
                            @NonNull String appID,
                            @NonNull AppSettings globalAppSettings,
                            @NonNull CallbackListener<String> listener) {
        try {
            if (null == appID || TextUtils.isEmpty(appID)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_APPID,
                                                        CometChatConstants.Errors.ERROR_EMPTY_APPID_MESSAGE));
            } else if (globalAppSettings == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_APP_SETTINGS_NULL,
                                                        CometChatConstants.Errors.ERROR_APP_SETTING_NULL_MESSAGE));
            } else {
                if (null == globalAppSettings.getRegion() || TextUtils.isEmpty(globalAppSettings.getRegion())) {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_REGION_MISSING,
                                                            CometChatConstants.Errors.ERROR_REGION_MISSING_MESSAGE));
                } else {
                    if (cometChatInstance == null) {
                        appSettings = globalAppSettings;
                        cometChatInstance = new CometChat(appID, appContext);
                        initLifecycleAware();
                    }
                    if (PreferenceHelper.getAppID() != null && !PreferenceHelper.getAppID().equalsIgnoreCase(appID)) {
                        internalLogout(false);
                        appSettings = globalAppSettings;
                        cometChatInstance = new CometChat(appID, appContext);
                    }
                    WSConnection.isWSConnectionAutoModeEnabled = appSettings.isAutoSocketConnectionEnabled();
                    if (getLoggedInUser() != null && CurrentUserRepo.getCurrentUser() != null && CurrentUserRepo
                        .getCurrentUser()
                        .getAuthToken() != null) {
                        final CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
                        Settings settings = SettingsRepo.getSettings();
                        if (settings != null) {
                            proceedWithSettingsData(settings, currentUser);
                        } else {
                            Logger.error(TAG, "User logged in but setting object data is not present in DB or corrupted");
                            getSettings(CurrentUserRepo.getCurrentUser().getAuthToken(), new CallbackListener<Settings>() {
                                @Override
                                public void onSuccess(Settings settings) {
                                    Logger.error(TAG, "Settings received from server and updated in DB");
                                    proceedWithSettingsData(settings, currentUser);
                                }

                                @Override
                                public void onError(final CometChatException e) {
                                    Logger.error(TAG,
                                                 "Error: Unable to get settings from server and settings object is missing in DB or corrupted" + e);
                                }
                            });
                        }
                    } else if (PreferenceHelper.getRestartAUthToken() != null) {
                        loginWithAuthTokenInternal(PreferenceHelper.getRestartAUthToken(), false, false, new CallbackListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                PreferenceHelper.clearRestartAuthToken();
                                loginSuccess(user);
                            }

                            @Override
                            public void onError(CometChatException e) {
                                loginFail(e);
                            }
                        });
                    }
                    isInitialized = true;
                    // Persist integrationSource as "manual" for traditional init path
                    PreferenceHelper.saveIntegrationSource("manual");
                    listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_INIT_SUCCESS);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: Unable to initialize CometChat SDK. Caused by: " + e);
        }
    }

    /**
     * Initializes CometChat SDK by reading configuration from cometchat-settings.json in assets.
     * This method reads appId, region, and chatSDK settings from the JSON file,
     * builds AppSettings, and calls the existing init method.
     * Sets integrationSource = "ai-agent" for telemetry attribution.
     *
     * @param appContext The application context
     * @param listener  Callback listener for success/error
     * @hide
     */
    public static void initFromSettings(@NonNull Context appContext,
                                        @NonNull CallbackListener<String> listener) {
        try {
            // 1. Read cometchat-settings.json from assets
            String jsonString;
            try {
                java.io.InputStream inputStream = appContext.getAssets().open("cometchat-settings.json");
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                jsonString = new String(buffer, java.nio.charset.StandardCharsets.UTF_8);
            } catch (java.io.FileNotFoundException e) {
                listener.onError(new CometChatException(
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_NOT_FOUND,
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_NOT_FOUND_MESSAGE));
                return;
            } catch (java.io.IOException e) {
                listener.onError(new CometChatException(
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_NOT_FOUND,
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_NOT_FOUND_MESSAGE));
                return;
            }

            // 2. Parse JSON
            JSONObject settingsJson;
            try {
                settingsJson = new JSONObject(jsonString);
            } catch (JSONException e) {
                listener.onError(new CometChatException(
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_INVALID_JSON,
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_INVALID_JSON_MESSAGE));
                return;
            }

            // 3. Validate required fields
            String appId = settingsJson.optString("appId", null);
            if (appId == null || appId.isEmpty()) {
                listener.onError(new CometChatException(
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_MISSING_APPID,
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_MISSING_APPID_MESSAGE));
                return;
            }

            String region = settingsJson.optString("region", null);
            if (region == null || region.isEmpty()) {
                listener.onError(new CometChatException(
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_MISSING_REGION,
                        CometChatConstants.Errors.ERROR_SETTINGS_FILE_MISSING_REGION_MESSAGE));
                return;
            }

            // 4. Build AppSettings from chatSDK section
            AppSettings.AppSettingsBuilder builder = new AppSettings.AppSettingsBuilder();
            builder.setRegion(region);

            JSONObject chatSdkJson = settingsJson.optJSONObject("chatSDK");
            if (chatSdkJson != null) {
                // Parse presenceSubscription
                JSONObject presenceJson = chatSdkJson.optJSONObject("presenceSubscription");
                if (presenceJson != null) {
                    String type = presenceJson.optString("type", "NONE");
                    switch (type) {
                        case "ALL_USERS":
                            builder.subscribePresenceForAllUsers();
                            break;
                        case "ROLES":
                            JSONArray rolesArray = presenceJson.optJSONArray("roles");
                            if (rolesArray != null) {
                                List<String> roles = new ArrayList<>();
                                for (int i = 0; i < rolesArray.length(); i++) {
                                    roles.add(rolesArray.getString(i));
                                }
                                builder.subscribePresenceForRoles(roles);
                            }
                            break;
                        case "FRIENDS":
                            builder.subscribePresenceForFriends();
                            break;
                        case "NONE":
                            // Default, no action needed
                            break;
                        default:
                            listener.onError(new CometChatException(
                                    CometChatConstants.Errors.ERROR_SETTINGS_FILE_INVALID_PRESENCE_TYPE,
                                    CometChatConstants.Errors.ERROR_SETTINGS_FILE_INVALID_PRESENCE_TYPE_MESSAGE));
                            return;
                    }
                }

                // Parse autoEstablishSocketConnection
                boolean autoConnect = chatSdkJson.optBoolean("autoEstablishSocketConnection", true);
                builder.autoEstablishSocketConnection(autoConnect);

                // Parse host overrides
                String adminHost = chatSdkJson.optString("adminHost", null);
                if (adminHost != null && !adminHost.equals("null") && !adminHost.isEmpty()) {
                    builder.overrideAdminHost(adminHost);
                }

                String clientHost = chatSdkJson.optString("clientHost", null);
                if (clientHost != null && !clientHost.equals("null") && !clientHost.isEmpty()) {
                    builder.overrideClientHost(clientHost);
                }
            }

            AppSettings builtSettings = builder.build();

            // 5. Save authKey for UIKit use
            JSONObject credentialsJson = settingsJson.optJSONObject("credentials");
            if (credentialsJson != null) {
                String authKey = credentialsJson.optString("authKey", null);
                if (authKey != null && !authKey.isEmpty()) {
                    final String authKeyToSave = authKey;
                    // Call existing init, then persist integrationSource and authKey
                    init(appContext, appId, builtSettings, new CallbackListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            // Override the "manual" that was set by init() with "ai-agent"
                            PreferenceHelper.saveIntegrationSource("ai-agent");
                            PreferenceHelper.saveSettingsAuthKey(authKeyToSave);
                            listener.onSuccess(s);
                        }

                        @Override
                        public void onError(CometChatException e) {
                            listener.onError(e);
                        }
                    });
                    return;
                }
            }

            // 6. No authKey case — still call init and set integrationSource
            init(appContext, appId, builtSettings, new CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                    // Override the "manual" that was set by init() with "ai-agent"
                    PreferenceHelper.saveIntegrationSource("ai-agent");
                    listener.onSuccess(s);
                }

                @Override
                public void onError(CometChatException e) {
                    listener.onError(e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error: Unable to initialize CometChat SDK from settings. Caused by: " + e);
            listener.onError(new CometChatException(
                    CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                    e.getMessage()));
        }
    }

    /**
     * Returns the authKey parsed from cometchat-settings.json, if available.
     * This is intended for UIKit to retrieve the authKey after file-based init.
     *
     * @return The authKey string, or null if not available
     * @hide
     */
    public static String getSettingsAuthKey() {
        return PreferenceHelper.getSettingsAuthKey();
    }

    private static void proceedWithSettingsData(Settings settings, CurrentUser currentUser) {
        try {
            boolean isRestartRequired = checkRestartRequired(settings.getAppVersion());
            Logger.error(TAG, "isRestartRequired : " + isRestartRequired);
            if (isRestartRequired) {
                restart(true);
            } else {
                //callSdkIdentificationApi();
                if (appSettings.isAutoSocketConnectionEnabled()) {
                    postLoginSetup(currentUser, settings, null);
                } else {
                    initRTTConnection(settings, currentUser);
                    Logger.error(TAG, "Not connecting to WS as auto connect is disabled");
                }
            }
        } catch (Exception e) {
            Logger.error(TAG, e.toString());
        }
    }

    private static boolean checkRestartRequired(int appVersion) {
        if (appVersion == 0) {
            return true;
        } else if (appVersion == 3) {
            return false;
        } else {
            String SDKVersion = CometChatUtils.getSDKVersion();
            int SDKMajorVersion = Integer.parseInt(SDKVersion.substring(0, 1));
            return appVersion < SDKMajorVersion;
        }
    }

    /**
     * {@inheritDoc}
     * This method is used to check if the SDK has been initialized.
     */
    public static boolean isInitialized() {
        return isInitialized;
    }

    private static void connectToWS(Settings settings, CurrentUser currentUser, @Nullable CallbackListener<String> listener) {
        initRTTConnection(settings, currentUser);
        if (rttConnection != null) {
            rttConnection.connect();
        } else {
            Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
            if (listener != null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_RTT_CONNECTION,
                                                        CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE));
            }
        }
    }

    private static void initRTTConnection(Settings settings, CurrentUser currentUser) {
        if (rttConnection == null) {
            rttConnection = new WSConnection(appSettings, currentUser, settings);
        }
    }

    private static void disconnectFromWS() {
        if (rttConnection != null) {
            rttConnection.disconnect();
        } else {
            if (wsDisconnectListener != null) {
                wsDisconnectListener.onError(new CometChatException(CometChatConstants.Errors.ERROR_RTT_CONNECTION,
                                                                    CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE));
            }
            Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
        }
    }

    private CometChat(String appID, Context appContext) {
        context = appContext.getApplicationContext();
        ApiConnection.init(context, appSettings);
        PreferenceHelper.init(context);
        String currentAppId = PreferenceHelper.getAppID();
        PreferenceHelper.saveAppID(appID);
        SQLiteManager.init(new SQLiteHelper(context, CometChatConstants.DATABASE_NAME, CometChatConstants.DATABASE_VERSION));
        checkAndRunMigrationsIfAny();
        if (PreferenceHelper.getAppID() != null && !PreferenceHelper.getAppID().equalsIgnoreCase(currentAppId)) {
            internalLogout(false);
        }

    }

    private void checkAndRunMigrationsIfAny() {
        if (!PreferenceHelper.isMigrationSuccessful()) {
            Logger.error(TAG, "Running migration for JWT");
            try {
                SQLiteManager.getInstance().runMigrationForJWT();
                PreferenceHelper.saveMigrationSuccessful(true);
            } catch (Exception e) {
                Logger.error(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        if (!PreferenceHelper.isTagsMigrationSuccessful()) {
            Logger.error(TAG, "Running migration for TAGS");
            try {
                SQLiteManager.getInstance().runMigrationForTags();
                PreferenceHelper.saveTagsMigrationSuccessful(true);
            } catch (Exception e) {
                Logger.error(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        if (!PreferenceHelper.isFatMigrationSuccessful()) {
            Logger.error(TAG, "Running migration for Fat");
            try {
                SQLiteManager.getInstance().runMigrationForFat();
                PreferenceHelper.saveFatMigrationSuccessful(true);
            } catch (Exception e) {
                Logger.error(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * {@inheritDoc}
     * This method is used to login in CometChat SDK. Developer can only call other CometChat methods after authentication with this method.
     *
     * @param uid      Unique identifier of the user
     * @param apiKey   API_KEY of the CometChat App created. This can be found at the CometChat Dashboard {@link "https://app.cometchat.io"}.
     * @param listener An object of the  <code>CallbackListener&lt;User&gt;</code> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/authentication#login-using-auth-key"}
     *
     * <b>Note</b>
     * This login method is insecure way to login as it may be leads to exposing your <b>apiKey</b>.
     * Secure way to login will be using AuthToken
     * @see CallbackListener
     * @see CometChat#login(java.lang.String, CometChat.CallbackListener)
     * @since <b>v1</b>
     */
    public static void login(@NonNull final String uid, @NonNull final String apiKey, @NonNull final CallbackListener<User> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (loginWithUIDInProgress) {
                final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_LOGIN_IN_PROGRESS,
                                                                                     CometChatConstants.Errors.ERROR_LOGIN_IN_PROGRESS_MESSAGE);
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(cometChatException);
                    }
                });
                loginFail(cometChatException);
            } else if (null == uid || TextUtils.isEmpty(uid)) {
                final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                                     CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE);
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(cometChatException);
                    }
                });
                loginFail(cometChatException);
            } else if (CometChatUtils.containsSpace(uid)) {
                final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_UID_WITH_SPACE,
                                                                                     CometChatConstants.Errors.ERROR_UID_WITH_SPACE_MESSAGE);
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(cometChatException);
                    }
                });
                loginFail(cometChatException);
            } else if (null == apiKey || TextUtils.isEmpty(apiKey)) {
                final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_API_KEY_NOT_FOUND,
                                                                                     CometChatConstants.Errors.ERROR_API_KEY_NOT_FOUND_MESSAGE);
                listener.onError(cometChatException);
                loginFail(cometChatException);
            } else {
                if (getLoggedInUser() != null) {
                    if (getLoggedInUser().getUid().equalsIgnoreCase(uid) && CurrentUserRepo.getCurrentUser() != null && CurrentUserRepo
                        .getCurrentUser()
                        .getAuthToken() != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(getLoggedInUser());
                            }
                        });
                    } else {
                        logout(new CallbackListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                loginWithUIDinternal(uid, apiKey, listener);
                            }

                            @Override
                            public void onError(CometChatException e) {
                                final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_LOGOUT_FAIL,
                                                                                                     CometChatConstants.Errors.ERROR_LOGOUT_FAIL_MESSAGE);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(cometChatException);
                                    }
                                });
                                loginFail(cometChatException);

                            }
                        });
                    }
                } else {
                    loginWithUIDinternal(uid, apiKey, listener);
                }
            }
        } catch (Exception e) {
            loginWithUIDInProgress = false;
            loginWithAuthTokenInProgress = false;
            final CometChatException ce = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("loginUID", uid);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(ce);
                }
            });
            loginFail(ce);
        }

    }

    private static void loginWithUIDinternal(final String UID, String apiKey,
                                             final CallbackListener<User> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        loginWithUIDInProgress = true;
        ApiConnection.getInstance().login(UID.trim(), apiKey, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(final String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                loginWithUIDInProgress = false;
                                listener.onError(ce);
                            }
                        });
                        loginFail(ce);
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                if (dataObject.has(CometChatConstants.Params.UID))
                                    PreferenceHelper.saveLoggedInUID(dataObject.getString(CometChatConstants.Params.UID));
                                if (dataObject.has(CometChatConstants.Params.AUTHTOKEN)) {
                                    String authToken = dataObject.getString(CometChatConstants.Params.AUTHTOKEN);
                                    login(authToken, listener);
                                }
                            } else {
                                loginWithUIDInProgress = false;
                                final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                     CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE);
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(cometChatException);
                                    }
                                });
                                loginFail(cometChatException);
                            }

                        } catch (final JSONException je) {
                            loginWithUIDInProgress = false;
                            final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                 je.getMessage());
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(cometChatException);
                                }
                            });
                            loginFail(cometChatException);
                        }

                    }
                } catch (Exception e) {
                    loginWithUIDInProgress = false;
                    loginWithAuthTokenInProgress = false;
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    detailsMap.put("loginUID", UID);
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });
                    loginFail(uncaughtException);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     * This method is used to login in CometChat SDK. Developer can only call other CometChat methods after authentication with this method.
     *
     * @param authToken AuthToken can be generated using API_KEY of the CometChat App created. Developer can also use authOnly API_KEY.
     *                  This can be found at the CometChat Dashboard {@link "https://app.cometchat.io"}.
     * @param listener  An object of the <code>CallbackListener&lt;User&gt;</code> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/authentication#login-using-auth-token"}
     *
     * <b>Note</b>
     * This authentication method is recommended by CometChat Team for using CometChat Pro SDK
     * @see CallbackListener
     * @see User
     * @since <b>v1</b>
     */
    public static void login(@NonNull final String authToken, @NonNull final CallbackListener<User> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (loginWithAuthTokenInProgress) {
                final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_LOGIN_IN_PROGRESS,
                                                                                     CometChatConstants.Errors.ERROR_LOGIN_IN_PROGRESS_MESSAGE);
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(cometChatException);
                    }
                });
                loginFail(cometChatException);

            } else if (null == authToken || TextUtils.isEmpty(authToken)) {
                final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_BLANK_AUTHTOKEN,
                                                                                     CometChatConstants.Errors.ERROR_BLANK_AUTHTOKEN_MESSAGE);
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(cometChatException);
                    }
                });
                loginFail(cometChatException);
            } else {
                if (CurrentUserRepo.getCurrentUser() != null && CurrentUserRepo.getCurrentUser().getAuthToken() != null) {
                    if (authToken.equalsIgnoreCase(CurrentUserRepo.getCurrentUser().getAuthToken())) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(getLoggedInUser());
                            }
                        });
                    } else {
                        logout(new CallbackListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                loginWithAuthTokenInternal(authToken, false, false, listener);
                            }

                            @Override
                            public void onError(CometChatException e) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_LOGOUT_FAIL,
                                                                                CometChatConstants.Errors.ERROR_LOGOUT_FAIL_MESSAGE));
                                    }
                                });
                                loginFail(e);
                            }
                        });
                    }
                } else {
                    loginWithAuthTokenInternal(authToken, false, false, listener);
                }

            }
        } catch (Exception e) {
            loginWithUIDInProgress = false;
            loginWithAuthTokenInProgress = false;
            final CometChatException ce = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("authToken", authToken);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(ce);
                }
            });
            loginFail(ce);
        }

    }

    private static void loginWithAuthTokenInternal(final String authToken,
                                                   final boolean fromRestart,
                                                   final boolean shouldAutoConnectOnRestart,
                                                   final CallbackListener<User> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        loginWithAuthTokenInProgress = true;
        ApiConnection.getInstance().login(authToken, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(final String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                loginWithAuthTokenInProgress = false;
                                loginWithUIDInProgress = false;
                                listener.onError(ce);
                            }
                        });
                        loginFail(ce);
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            final CurrentUser user = CurrentUser.fromJson(jsonObject
                                                                              .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                              .toString());
                            JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            if (dataObject.has(CometChatConstants.ResponseKeys.KEY_SETTINGS)) {
                                Logger.error(TAG, "Settings obtained from login response");
                                JSONObject settingsObject = dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_SETTINGS);
                                final Settings settings = Settings.fromJson(settingsObject.toString());
                                loginSuccessful(user, settings, fromRestart, shouldAutoConnectOnRestart, listener);
                            } else {
                                Logger.error(TAG, "Settings fetched from API");
                                getSettings(authToken, new CallbackListener<Settings>() {
                                    @Override
                                    public void onSuccess(Settings settings) {
                                        loginSuccessful(user, settings, fromRestart, shouldAutoConnectOnRestart, listener);
                                    }

                                    @Override
                                    public void onError(final CometChatException e) {
                                        loggedInUser = null;
                                        CurrentUserRepo.clearUser();
                                        SettingsRepo.clearSettings();
                                        if (rttConnection != null) {
                                            rttConnection.disconnect();
                                        } else {
                                            Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                        }
                                        loginWithAuthTokenInProgress = false;
                                        loginWithUIDInProgress = false;
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(e);
                                            }
                                        });
                                        loginFail(e);
                                    }
                                });
                            }

                        } catch (final JSONException je) {
                            loggedInUser = null;
                            CurrentUserRepo.clearUser();
                            SettingsRepo.clearSettings();
                            loginWithAuthTokenInProgress = false;
                            loginWithUIDInProgress = false;
                            final CometChatException cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                 je.getMessage());
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(cometChatException);
                                }
                            });
                            loginFail(cometChatException);
                        }
                    }
                } catch (Exception e) {
                    loginWithUIDInProgress = false;
                    loginWithAuthTokenInProgress = false;
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    detailsMap.put("authToken", authToken);
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });
                    loginFail(uncaughtException);
                }
            }
        });
    }

    private static void loginSuccessful(CurrentUser currentUser,
                                        Settings settings,
                                        boolean fromRestart,
                                        boolean shouldAutoConnectOnRestart,
                                        final CallbackListener<User> listener) {
        rttConnection = null;
        CurrentUserRepo.insertCurrentUser(currentUser.toMap());
        SettingsRepo.insertSettings(settings);
        PreferenceHelper.saveLoggedInUID(currentUser.getUid());
        ExtensionManager.callOnLogin(getLoggedInUser());
        callSdkIdentificationApi();
        loginWithAuthTokenInProgress = false;
        loginWithUIDInProgress = false;
        postOnMainThread(new Runnable() {
            @Override
            public void run() {
                listener.onSuccess(getLoggedInUser());
            }
        });
        loginSuccess(getLoggedInUser());
        if (fromRestart) {
            if (shouldAutoConnectOnRestart) {
                if (appSettings.isAutoSocketConnectionEnabled()) {
                    postLoginSetup(currentUser, settings, null);
                } else {
                    initRTTConnection(settings, currentUser);
                    Logger.error(TAG, "No connection to web-sockets as auto-connect is set to false");
                }
            } else {
                postLoginSetup(currentUser, settings, null);
            }
        } else {
            if (appSettings.isAutoSocketConnectionEnabled()) {
                postLoginSetup(currentUser, settings, null);
            } else {
                initRTTConnection(settings, currentUser);
                Logger.error(TAG, "No connection to web-sockets as auto-connect is set to false");
            }
        }
    }

    private static void postLoginSetup(CurrentUser currentUser, Settings settings, @Nullable CallbackListener<String> listener) {
        if (!getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_CONNECTED)) {
            setupWSListeners();
            connectToWS(settings, currentUser, listener);
        }
        if (!settings.isAnalyticsPingDisabled()) {
            AnalyticsController.getInstance().enableAnalyticsPing(analyticsPingListener);
        }
    }

    private static void loginFail(final CometChatException ce) {
        Iterator it = loginListeners.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue() != null) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ((LoginListener) pair.getValue()).loginFailure(ce);
                    }
                });
            }
        }
    }

    private static void loginSuccess(final User user) {
        Iterator it = loginListeners.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue() != null) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ((LoginListener) pair.getValue()).loginSuccess(user);
                    }
                });
            }
        }
    }

    private static void logoutSuccess() {
        Iterator it = loginListeners.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue() != null) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ((LoginListener) pair.getValue()).logoutSuccess();
                    }
                });
            }
        }
    }

    private static void logoutFail(final CometChatException ce) {
        Iterator it = loginListeners.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue() != null) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ((LoginListener) pair.getValue()).logoutFailure(ce);
                    }
                });
            }
        }
    }

    // Connect and Disconnect methods

    static void connectInternal(CallbackListener<String> listener, boolean isDeveloperCall) {
        if (getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_CONNECTED)) {
            if (isDeveloperCall) {
                markExplicitConnect();
            }
            listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_WS_CONNECTION_ALREADY_CONNECTED);
        } else {
            if (getLoggedInUser() != null) {
                if (listener != null) {
                    wsConnectListener = listener;
                }
                if (isDeveloperCall) {
                    markExplicitConnect();
                }
                CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
                Settings settings = SettingsRepo.getSettings();
                if (currentUser != null && settings != null) {
                    postLoginSetup(currentUser, settings, listener);
                } else {
                    Logger.exception(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
                    if (listener != null) {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                                CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                    }
                }
            } else {
                Logger.exception(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
                if (listener != null) {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                            CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                }
            }
        }
    }

    private static void markExplicitConnect() {
        WSConnection.isConnectCalled.set(true);
        WSConnection.isDisconnectCalled.set(false);
    }

    private static void markExplicitDisconnect(){
        WSConnection.isConnectCalled.set(false);
        WSConnection.isDisconnectCalled.set(true);
    }

    /**
     * {@inheritDoc}
     * Establishes a connection to the web-socket server for the logged in user
     *
     * @version <b>v3</b>
     * @since <b>v3</b>
     */
    public static void connect(CallbackListener<String> listener) {
        ConnectionController.getInstance().connect(listener, true);
    }

    @Deprecated
    public static void connect() {
        connect(null);
    }

    static void disconnectInternal(CallbackListener<String> listener, boolean isDeveloperCall) {
        if (getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_DISCONNECTED)) {
            if (isDeveloperCall) {
                markExplicitDisconnect();
            }
            internalDisconnect();
            listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_WS_CONNECTION_ALREADY_DISCONNECTED);
        } else {
            if (getLoggedInUser() != null) {
                if (listener != null) {
                    wsDisconnectListener = listener;
                }
                if (isDeveloperCall) {
                    markExplicitDisconnect();
                }
                internalDisconnect();
            } else {
                Logger.error(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
                if (listener != null) {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                            CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * Destroys the connection to the web-socket server for the logged in user
     *
     * @version <b>v3</b>
     * @since <b>v3</b>
     */
    public static void disconnect(CallbackListener<String> listener) {
        ConnectionController.getInstance().disconnect(listener, true);
    }

    @Deprecated
    public static void disconnect() {
        disconnect(null);
    }

    /**
     * {@inheritDoc}
     * Get the information for a user
     *
     * @param UID      Unique identifier of the user
     * @param listener An object of the  <code>CallbackListener&lt;User&gt;</code> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/users-retrieve-users#retrieve-particular-user-details"}
     * @see CallbackListener
     * @see User
     * @since <b>v1</b>
     */
    public static void getUser(@NonNull final String UID, @NonNull final CallbackListener<User> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (null != UID && !TextUtils.isEmpty(UID)) {
                if (CometChatUtils.containsSpace(UID)) {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {

                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UID_WITH_SPACE,
                                                                    CometChatConstants.Errors.ERROR_UID_WITH_SPACE_MESSAGE));
                        }
                    });
                } else {
                    ApiConnection.getInstance().getUser(UID.trim(), new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(ce);
                                        }
                                    });
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                            final User user = User.fromJson(jsonObject.get(CometChatConstants.ResponseKeys.KEY_DATA).toString());
                                            postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onSuccess(user);
                                                }
                                            });
                                        }
                                    } catch (final JSONException je) {
                                        CometChat.postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                        je.getMessage()));
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                    e.getMessage());
                                HashMap<String, String> detailsMap = new HashMap<>();
                                detailsMap.put("fetchedUID", UID);
                                handleException(methodName, e, detailsMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                }
            } else {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("fetchedUID", UID);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }


    /**
     * {@inheritDoc}
     * This method is used for sending text message to a particular user or group with mentioned parameters
     *
     * @param message  An object of the <code>TextMessage</code> class with the required details about the receiverUid, text be to sent and receiverType .
     *                 <code>TextMessage(@NonNull String receiverUid, @NonNull  String text, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     * @param listener An object of the  <code>CallbackListener&lt;TextMessage&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-send-message#text-message"}
     * @see TextMessage
     * @see CometChatConstants.ReceiverTypes
     * @see CometChatConstants
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void sendMessage(@NonNull final TextMessage message, @NonNull final CallbackListener<TextMessage> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (message == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE,
                                                        CometChatConstants.Errors.ERROR_INVALID_MESSAGE_MESSAGE));
            } else if (message.getType() == null || !message.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_TYPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_SENDING_MESSAGE_TYPE_MESSAGE));
            } else if (message.getText() == null || TextUtils.isEmpty(message.getText().trim())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY,
                                                        CometChatConstants.Errors.ERROR_MESSAGE_TEXT_EMPTY_MESSAGE));
            } else if (message.getReceiverUid() == null || TextUtils.isEmpty(message.getReceiverUid().trim())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_BLANK_UID,
                                                        CometChatConstants.Errors.ERROR_BLANK_UID_MESSAGE));
            } else if (message.getReceiverType() == null || TextUtils.isEmpty(message.getReceiverType()) || (!message
                .getReceiverType()
                .equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER) && !message
                .getReceiverType()
                .equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP))) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
            } else {
                final TextMessage extMessage = (TextMessage) ExtensionManager.callBeforeMessageSent(message);
                ApiConnection.getInstance().sendMessage(extMessage, new ApiConnection.APIConnectionListener() {
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    final TextMessage receivedMessage = TextMessage.fromJson(dataObject);
                                    PreferenceHelper.saveLastDeliveredMessageId(receivedMessage.getId());
                                    final TextMessage extReceivedMessage = (TextMessage) ExtensionManager.callAfterMessageSent(receivedMessage);
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(extReceivedMessage);
                                        }
                                    });
                                } catch (final JSONException je) {
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("textMessage", message.toString());
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("textMessage", message.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * This method is used to fetch the list of flag reasons available in the CometChat App
     *
     * @param callback An object of the <code>CallbackListener&lt;List&lt;FlagReason&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @see FlagReason
     * @see CallbackListener
     * @since <b>v4.1.8<b/>
     */
    public static void getFlagReasons(@NonNull final CometChat.CallbackListener<List<FlagReason>> callback) {
        final String methodName = new Throwable()
                .getStackTrace()[0]
                .getMethodName();

        try {
            Settings settings = SettingsRepo.getSettings();
            if (settings != null) {
                handleFlagReasonsCallback(settings, callback);
            } else {
                String userAuthToken = getUserAuthToken();
                if (userAuthToken != null && TextUtils.isEmpty(userAuthToken)) {
                    getSettings(userAuthToken, new CallbackListener<Settings>() {
                        @Override
                        public void onSuccess(Settings settings) {
                            handleFlagReasonsCallback(settings, callback);
                        }

                        @Override
                        public void onError(final CometChatException e) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(e);
                                }
                            });
                        }
                    });
                }
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onError(uncaughtException);
                }
            });
        }
    }

    private static void handleFlagReasonsCallback (@NonNull Settings settings, @NonNull final CometChat.CallbackListener<List<FlagReason>> callback) {
        final List<FlagReason> flagReasons = settings.getFlagReasons();
        if (flagReasons != null && !flagReasons.isEmpty()) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(flagReasons);
                }
            });
        } else {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(new ArrayList<FlagReason>());
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * This method is used to flag a particular message in CometChat
     *
     * @param messageId   Unique identifier of the message to be flagged
     * @param flagDetail  An object of the <code>FlagDetail<code/> class with the required details about the reason for flagging the message.
     *                    <code>FlagDetail(String reasonId)<code/> or <code>FlagDetail(String reasonId, String remark)<code/>
     * @param callbackListener An object of the <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @since <b>v4.1.8<b/>
     */
    public static void flagMessage(final long messageId, @NonNull FlagDetail flagDetail, @NonNull final CometChat.CallbackListener<String> callbackListener) {
        final String methodName = new Throwable()
                .getStackTrace()[0]
                .getMethodName();

        if (messageId < 0) {
            callbackListener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID, CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
            return;
        }

        ApiConnection.getInstance().flagMessage(messageId, flagDetail, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                callbackListener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            final String message = dataObject.getString(CometChatConstants.ResponseKeys.KEY_MESSAGE);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    callbackListener.onSuccess(message);
                                }
                            });
                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    callbackListener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    detailsMap.put("flagMessage", String.valueOf(messageId));
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callbackListener.onError(uncaughtException);
                        }
                    });
                }
            }
        });
    }


    /**
     * {@inheritDoc}
     * This method is used for sending media message to a particular user or group with mentioned parameters
     *
     * @param message  An object of the <code>MediaMessage<code/> class with the required details about the receiverUid, file to be sent,messageType and receiverType .
     *                 <code>MediaMessage(String receiverUid, File file, @CometChatConstants.MessageTypes String messageType, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     * @param listener An object of the  <code>CallbackListener&lt;MediaMessage&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-send-message#media-message"}
     * @see MediaMessage
     * @see CometChatConstants.MessageTypes
     * @see CometChatConstants.ReceiverTypes
     * @see CometChatConstants
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void sendMediaMessage(@NonNull final MediaMessage message, @NonNull final CallbackListener<MediaMessage> listener) {
        final Settings settings = SettingsRepo.getSettings();

        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (message == null) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE,
                                                    CometChatConstants.Errors.ERROR_INVALID_MESSAGE_MESSAGE));
            return;
        }
        try {
            boolean isMediaMessage = false;
            if ((message.getType() != null) && (message.getType().equals(CometChatConstants.MESSAGE_TYPE_AUDIO)
                || message.getType().equals(CometChatConstants.MESSAGE_TYPE_FILE)
                || message.getType().equals(CometChatConstants.MESSAGE_TYPE_VIDEO)
                || message.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE))) {

                isMediaMessage = true;
            }
            if (message == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE,
                                                        CometChatConstants.Errors.ERROR_INVALID_MESSAGE_MESSAGE));
                return;
            }
            if (!isMediaMessage) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_TYPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_SENDING_MESSAGE_TYPE_MESSAGE));
                return;
            }
            if (message.getReceiverUid() == null || TextUtils.isEmpty(message.getReceiverUid().trim())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_BLANK_UID,
                                                        CometChatConstants.Errors.ERROR_BLANK_UID_MESSAGE));
                return;
            }
            if (message.getFiles() == null && message.getAttachments() == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MEDIA_MESSAGE,
                                                        CometChatConstants.Errors.ERROR_INVALID_MEDIA_MESSAGE_MESSAGE));
                return;
            }
            boolean isValid = true;
            if (settings != null) {
                if (message.getFiles() != null && message.getFiles().size() > 0) {
                    if (!doFilesExist(message.getFiles())) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_FILE_OBJECT_INVALID,
                                        CometChatConstants.Errors.ERROR_FILE_OBJECT_INVALID_MESSAGE));
                            }
                        });
                        return;
                    }

                    // Check file count limit
                    if (message.getFiles().size() > settings.getFileCount()) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERR_BAD_REQUEST,
                                        String.format(CometChatConstants.Errors.ERROR_INVALID_FILE_COUNT, settings.getFileCount())));
                            }
                        });
                        return;
                    }

                    // Check file size limit — enforced per file only; there is
                    // intentionally no combined/total-size cap (a message may carry
                    // up to file.count.max files, each up to file.size.max).
                    for (File file : message.getFiles()) {
                        if (file.length() > settings.getFileSize()) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    double allowedSizeMB = settings.getFileSize() / (1024.0 * 1024.0);
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERR_BAD_REQUEST,
                                            String.format(CometChatConstants.Errors.ERROR_INVALID_FILE_SIZE, String.format("%.2f MB", allowedSizeMB))));
                                }
                            });
                            return;
                        }
                    }
                    isValid = true;
                }
            }
            if (message.getAttachments() != null && message.getAttachments().size() > 0) {
                final int maxAttachmentCount = settings != null ? settings.getFileCount() : 10;
                if (message.getAttachments().size() > maxAttachmentCount) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERR_FILE_COUNT_EXCEEDED,
                                    String.format(CometChatConstants.Errors.ERR_FILE_COUNT_EXCEEDED_MESSAGE, maxAttachmentCount)));
                        }
                    });
                    return;
                }
                final CometChatException ce = validateAttachments(message);
                if (ce != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(ce);
                        }
                    });
                    return;
                } else {
                    isValid = true;
                }
            }
            if (isValid) {
                final MediaMessage extMessage = (MediaMessage) ExtensionManager.callBeforeMessageSent(message);
                ApiConnection.getInstance().sendMessage(extMessage, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce1) {
                        try {
                            if (ce1 != null) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce1);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    final MediaMessage receivedMessage = MediaMessage.fromJson(dataObject);
                                    PreferenceHelper.saveLastDeliveredMessageId(receivedMessage.getId());
                                    final MediaMessage extReceivedMessage = (MediaMessage) ExtensionManager.callAfterMessageSent(receivedMessage);
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(extReceivedMessage);
                                        }
                                    });

                                } catch (final JSONException je) {
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                        }
                                    });
                                }


                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("mediaMessage", message.toString());
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("mediaMessage", message.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });

        }
    }

    private static boolean doFilesExist(List<File> files) {
        boolean doFilesExist = true;
        for (File file : files) {
            if (!file.exists()) {
                doFilesExist = false;
            }
        }
        return doFilesExist;
    }

    /**
     * Creates an {@link UploadFileRequest} — the entry point for multi-attachment
     * uploads. The request is scoped to one destination and one upload <b>batch</b>:
     * configure it ({@code setParentMessageId} / {@code setBatchId} /
     * {@code setConcurrency}), upload through it ({@code uploadAttachments} /
     * {@code uploadAttachment}), read the batch off it ({@code getAttachments},
     * {@code getStatus}, …), and release it with {@code clearAll()} after a
     * successful send.
     *
     * <p>Upload is <b>decoupled from send</b>: when uploads are ready, build a
     * {@link MediaMessage}, set the uploaded attachments via
     * {@code setAttachments(...)} (plus an optional caption and a {@code muid}),
     * and send it with the existing
     * {@link #sendMediaMessage(MediaMessage, CallbackListener)} — send is instant
     * because the bytes are already on storage.</p>
     *
     * <p>One request = one batch. Concurrent composers (e.g. main and thread)
     * create separate requests with separate batch ids; they never cross-talk.</p>
     *
     * @param receiverId   the destination uid (user) or guid (group)
     * @param receiverType the destination type ({@code user} or {@code group}); required
     *                     so the api can authorize the upload against the receiver (RBAC/SBAC)
     * @since <b>v5</b>
     */
    public static UploadFileRequest createUploadFileRequest(@NonNull String receiverId,
                                                            @CometChatConstants.ReceiverTypes String receiverType) {
        return new UploadFileRequest(receiverId, receiverType);
    }

    /**
     * Returns the maximum number of attachments allowed in a single message
     * ({@code file.count.max} from app settings, default {@code 10}). Consumers
     * (e.g. the UIKit) can read this to gate the number of attachments in the
     * composer before uploading; the SDK also enforces it in
     * {@link #sendMediaMessage(MediaMessage, CallbackListener)}.
     *
     * @since <b>v5</b>
     */
    public static int getMaxAttachmentCount() {
        Settings settings = SettingsRepo.getSettings();
        return settings != null ? settings.getFileCount() : 10;
    }

    /**
     * Returns the maximum allowed file size in bytes ({@code file.size.max} from
     * app settings, default {@code 104857600} = 100 MB). The SDK enforces this
     * per file in {@code UploadFileRequest.uploadAttachments}.
     *
     * @since <b>v5</b>
     */
    public static long getMaxFileSize() {
        Settings settings = SettingsRepo.getSettings();
        return settings != null ? settings.getFileSize() : 104857600L;
    }

    private static CometChatException validateAttachments(MediaMessage message) {
        CometChatException cometChatException = null;
        for (Attachment attachment : message.getAttachments()) {
            if (attachment == null) {
                cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_ATTACHMENT,
                                                            CometChatConstants.Errors.ERROR_INVALID_ATTACHMENT_MESSAGE);
            } else if (attachment.getFileName() == null || TextUtils.isEmpty(attachment.getFileName())) {
                cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_FILE_NAME,
                                                            CometChatConstants.Errors.ERROR_INVALID_FILE_NAME_MESSAGE);
            } else if (attachment.getFileExtension() == null || TextUtils.isEmpty(attachment.getFileExtension())) {
                cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_FILE_EXTENSION,
                                                            CometChatConstants.Errors.ERROR_INVALID_FILE_EXTENSION_MESSAGE);
            } else if (attachment.getFileMimeType() == null || TextUtils.isEmpty(attachment.getFileMimeType())) {
                cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_FILE_MIME_TYPE,
                                                            CometChatConstants.Errors.ERROR_INVALID_FILE_MIME_TYPE_MESSAGE);
            } else if (attachment.getFileUrl() == null || TextUtils.isEmpty(attachment.getFileUrl())) {
                cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_FILE_URL,
                                                            CometChatConstants.Errors.ERROR_INVALID_FILE_URL_MESSAGE);
            }
        }
        return cometChatException;
    }


    /**
     * {@inheritDoc}
     * This method is used for sending custom message to a particular user or group with mentioned parameters.
     * This method is made for developers to make their own custom messages as per requirement
     *
     * @param customMessage An object of the <code>CustomMessage<code/> class with the required details about the receiverUid, receiverType,customType of the message and JSONObject of the custom data .
     *                      <code>CustomMessage(String receiverUid, @CometChatConstants.ReceiverTypes String receiverType, String customType, @NonNull JSONObject customData )<code/>
     * @param listener      An object of the  <code>CallbackListener&lt;CustomMessage&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-send-message#custom-message"}
     * @see CustomMessage
     * @see CometChatConstants
     * @see CometChatConstants.ReceiverTypes
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void sendCustomMessage(@NonNull final CustomMessage customMessage, @NonNull final CallbackListener<CustomMessage> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (customMessage == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE,
                                                        CometChatConstants.Errors.ERROR_INVALID_MESSAGE_MESSAGE));
            } else if (customMessage.getReceiverUid() == null || TextUtils.isEmpty(customMessage.getReceiverUid().trim())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_BLANK_UID,
                                                        CometChatConstants.Errors.ERROR_BLANK_UID_MESSAGE));
            } else if (customMessage.getCustomData() == null || CometChatUtils.isJSONObjectEmpty(customMessage.getCustomData())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_CUSTOM_DATA,
                                                        CometChatConstants.Errors.ERROR_EMPTY_CUSTOM_DATA_MESSAGE));
            } else if (customMessage.getReceiverType() == null || TextUtils.isEmpty(customMessage.getReceiverType()) ||
                (!customMessage.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER) && !customMessage
                    .getReceiverType()
                    .equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP))) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
            } else {
                final CustomMessage extMessage = (CustomMessage) ExtensionManager.callBeforeMessageSent(customMessage);
                ApiConnection.getInstance().sendMessage(extMessage, new ApiConnection.APIConnectionListener() {
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    final CustomMessage receivedMessage = CustomMessage.fromJson(dataObject);
                                    PreferenceHelper.saveLastDeliveredMessageId(receivedMessage.getId());
                                    final CustomMessage extReceivedMessage = (CustomMessage) ExtensionManager.callAfterMessageSent(receivedMessage);
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(extReceivedMessage);
                                        }
                                    });


                                } catch (final JSONException je) {
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                        }
                                    });

                                }
                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("customMessage", customMessage.toString());
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("customMessage", customMessage.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * This method sends an InteractiveMessage. It accepts an interactive message object and
     * a callback listener. The callback listener gets triggered when the interactive message
     * is successfully sent or if an error occurs.
     *
     * @param message  An object of the <code>InteractiveMessage<code/> class with the required details about the receiverUid, interactive data to be sent, messageType and receiverType .
     *                 <code>InteractiveMessage(String receiverUid, @CometChatConstants.ReceiverTypes String receiverType, String interactiveType, JSONObject interactiveData)<code/>
     * @param listener An object of the  <code>CallbackListener&lt;InteractiveMessage&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v4</b>
     * @see InteractiveMessage
     * @see CometChatConstants
     * @see CometChatConstants.ReceiverTypes
     * @see CallbackListener
     */
    public static void sendInteractiveMessage(@NonNull final InteractiveMessage message,
                                              @NonNull final CallbackListener<InteractiveMessage> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (message == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE,
                                                        CometChatConstants.Errors.ERROR_INVALID_MESSAGE_MESSAGE));
            } else if (message.getReceiverUid() == null || TextUtils.isEmpty(message.getReceiverUid().trim())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_BLANK_UID,
                                                        CometChatConstants.Errors.ERROR_BLANK_UID_MESSAGE));
            } else if (message.getInteractiveData() == null || CometChatUtils.isJSONObjectEmpty(message.getInteractiveData())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_INTERACTIVE_DATA,
                                                        CometChatConstants.Errors.ERROR_EMPTY_INTERACTIVE_DATA));
            } else if (message.getReceiverType() == null || TextUtils.isEmpty(message.getReceiverType()) ||
                (!message.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER) && !message
                    .getReceiverType()
                    .equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP))) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
            } else {
                final InteractiveMessage extMessage = (InteractiveMessage) ExtensionManager.callBeforeMessageSent(message);
                ApiConnection.getInstance().sendMessage(extMessage, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    final InteractiveMessage receivedMessage = InteractiveMessage.fromJson(dataObject);
                                    PreferenceHelper.saveLastDeliveredMessageId(receivedMessage.getId());
                                    final InteractiveMessage extReceivedMessage = (InteractiveMessage) ExtensionManager.callAfterMessageSent(
                                        receivedMessage);
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(extReceivedMessage);
                                        }
                                    });
                                } catch (final JSONException je) {
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                        }
                                    });

                                }
                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("message", message.toString());
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("message", message.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * <code>createGroup()<code/> method is used for creating groups of different types such as public,password and private group
     *
     * @param group    An object of the <code>Group<code/> class with the required details about the guid, name,groupType,password,icon and description based on the constructor
     *                 <code>Group(String guid, String name, @CometChatConstants.GroupTypes String groupType, String password)<code/>
     *                 <code>Group(String guid, String name, @CometChatConstants.GroupTypes String groupType, String password, String icon, String description)<code/>
     * @param listener An object of the  <code>CallbackListener&lt;Group&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-create-group"}
     * @see Group
     * @see CometChatConstants.GroupTypes
     * @see CometChatConstants
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void createGroup(@NonNull final Group group, @NonNull final CallbackListener<Group> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (group == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GROUP,
                                                        CometChatConstants.Errors.ERROR_INVALID_GROUP_MESSAGE));
                return;
            }
            if (group.getGuid() == null || TextUtils.isEmpty(group.getGuid())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                        CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                return;
            }

            if (group.getName() == null || TextUtils.isEmpty(group.getName())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME,
                                                        CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME_MESSAGE));
                return;
            }

            if (group.getGroupType() == null || TextUtils.isEmpty(group.getGroupType())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_GROUP_TYPE,
                                                        CometChatConstants.Errors.ERROR_EMPTY_GROUP_TYPE_MESSAGE));
                return;
            }
            if (CometChatUtils.isEmpty(group.getIcon())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_ICON,
                                                        CometChatConstants.Errors.ERROR_EMPTY_ICON_MESSAGE));
                return;
            }

            if (CometChatUtils.isEmpty(group.getDescription())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_DESCRIPTION,
                                                        CometChatConstants.Errors.ERROR_EMPTY_DESCRIPTION_MESSAGE));
                return;
            }
            if (group.getGroupType().equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PASSWORD)
                && (group.getPassword() == null || TextUtils.isEmpty(group.getPassword()))) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_PASSWORD_MISSING,
                                                        CometChatConstants.Errors.ERROR_PASSWORD_MISSING_MESSAGE));
                return;
            }
            if (group.getMetadata() != null && CometChatUtils.isJSONObjectEmpty(group.getMetadata())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_METADATA,
                                                        CometChatConstants.Errors.ERROR_EMPTY_METADATA_MESSAGE));
                return;
            }
            ApiConnection.getInstance().createGroup(group, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(final String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });

                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                final Group createdGroup = Group.fromJson(dataObject.toString());
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(createdGroup);
                                    }
                                });
                                if (rttConnection != null) {
                                    rttConnection.joinGroup(createdGroup.getGuid());
                                } else {
                                    Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                }
                            } catch (final JSONException je) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("group", group.toString());
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("group", group.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * <code>joinGroup()<code/> method is used to join groups of different types such as public and password group
     *
     * @param guid      Unique identifier of a Group
     * @param groupType Type of the Group user wants to join
     * @param password  password of the group if the group type is <code>CometChatConstants.GROUP_TYPE_PASSWORD<code/> else the field can be left null or empty
     * @param listener  An object of the  <code>CallbackListener&lt;Group&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-join-group"}
     * @see CometChatConstants.GroupTypes
     * @see CometChatConstants
     * @see Group
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void joinGroup(@NonNull final String guid,
                                 @NonNull final String groupType,
                                 final String password,
                                 @NonNull final CallbackListener<Group> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (guid == null || TextUtils.isEmpty(guid)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                        CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                return;
            }

            if (groupType != null && !TextUtils.isEmpty(groupType)) {
                if (groupType.equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PASSWORD)
                    && (password == null || TextUtils.isEmpty(password))) {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_PASSWORD_MISSING,
                                                            CometChatConstants.Errors.ERROR_PASSWORD_MISSING_MESSAGE));
                    return;
                }
                ApiConnection.getInstance().joinGroup(guid.trim(), groupType, password, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (ce.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_GROUP_ALREADY_JOINED)) {
                                            if (rttConnection != null) {
                                                rttConnection.joinGroup(guid);
                                            } else {
                                                Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                            }
                                        }
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                        JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                        Action action = Action.fromJson(dataObject);
                                        if (action.getActionFor() instanceof Group) {
                                            final Group groupJoined = ((Group) action.getActionFor());
                                            postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onSuccess(groupJoined);
                                                }
                                            });
                                            if (rttConnection != null) {
                                                rttConnection.joinGroup(groupJoined.getGuid());
                                            } else {
                                                Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                            }
                                        }
                                    }
                                } catch (final JSONException je) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                        }
                                    });
                                }
                            }
                        } catch (
                            Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("GUID", guid);
                            detailsMap.put("groupType", groupType);
                            detailsMap.put("password", password);
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });

            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GROUP_TYPE,
                                                                CometChatConstants.Errors.ERROR_INVALID_GROUP_TYPE_MESSAGE));
                    }
                });
            }
        } catch (
            Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("GUID", guid);
            detailsMap.put("groupType", groupType);
            detailsMap.put("password", password);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * This method is used for leaving a particular group.The method requires guid and an object of <code>CallbackListener<code/> class
     *
     * @param guid     Unique identifier of a Group
     * @param listener An object of the  <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-leave-group"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void leaveGroup(@NonNull final String guid, @NonNull final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (guid == null || TextUtils.isEmpty(guid)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                        CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                return;
            }

            if (guid != null && !TextUtils.isEmpty(guid)) {
                ApiConnection.getInstance().leaveGroup(guid.trim(), new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                if (rttConnection != null) {
                                    rttConnection.leaveGroup(guid);
                                } else {
                                    Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                }
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_GROUP_LEAVE_SUCCESS);
                                    }
                                });

                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("GUID", guid);
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                                CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("GUID", guid);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }

    }


    /**
     * {@inheritDoc}
     * This method is used for updating the group information such as name,description,icon,password,type etc.
     *
     * @param group    An object of the <code>Group<code/> class with the required details about the guid, name,groupType,password,icon and description based on the constructor
     *                 <code>Group(String guid, String name, @CometChatConstants.GroupTypes String groupType, String password)<code/>
     *                 <code>Group(String guid, String name, @CometChatConstants.GroupTypes String groupType, String password, String icon, String description)<code/>
     * @param listener An object of the  <code>CallbackListener&lt;Group&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-update-group"}
     *
     * <b>Note</b>
     * GUID of any groups can not be updated
     * @see Group
     * @see CometChatConstants.GroupTypes
     * @see CometChatConstants
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void updateGroup(@NonNull final Group group, @NonNull final CallbackListener<Group> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (group == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GROUP,
                                                        CometChatConstants.Errors.ERROR_INVALID_GROUP_MESSAGE));
                return;
            }
            if (group.getGuid() == null || TextUtils.isEmpty(group.getGuid())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                        CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                return;
            }
            if (group.getName() != null && group.getName().equalsIgnoreCase("")) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GROUP_NAME,
                                                        CometChatConstants.Errors.ERROR_INVALID_GROUP_NAME_MESSAGE));
                return;
            }
            ApiConnection.getInstance().updateGroup(group, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                final Group updatedGroup = Group.fromJson(dataObject.toString());
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(updatedGroup);
                                    }
                                });
                            } catch (final JSONException je) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("group", group.toString());
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("group", group.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * <code>deleteGroup()</> is called for deleting a particular group.The method requires guid and an object of <code>CallbackListener<code/> class
     *
     * @param guid     Unique identifier of a Group
     * @param listener An object of the  <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-delete-group"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void deleteGroup(@NonNull final String guid, @NonNull final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (guid == null || TextUtils.isEmpty(guid)) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                                CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                    }
                });
            } else {
                ApiConnection.getInstance().deleteGroup(guid.trim(), new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_GROUP_DELETE_SUCCESS);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("GUID", guid);
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("GUID", guid);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * Get information about a group
     *
     * @param guid     Unique identifier of the Group
     * @param listener An object of the  <code>CallbackListener&lt;Group&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-retrieve-groups#retrieve-particular-group-details"}
     * @see CallbackListener
     * @see User
     * @see Group
     * @since <b>v1</b>
     */
    public static void getGroup(@NonNull final String guid, @NonNull final CallbackListener<Group> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();

        try {
            if (guid != null && !TextUtils.isEmpty(guid)) {
                ApiConnection.getInstance().getGroup(guid.trim(), new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject groupObject = new JSONObject(response);
                                    final Group group = Group.fromJson(groupObject
                                                                           .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                           .toString());
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(group);
                                        }
                                    });

                                } catch (final JSONException e) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                        }
                                    });
                                }

                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("GUID", guid);
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                                CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("GUID", guid);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * Method to kick a member from a Group
     *
     * @param uid      Unique identifier of a User
     * @param guid     Unique identifier of a Group
     * @param listener An object of the  <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-kick-ban-members#kick-a-group-member"}
     *
     * <b>Note</b>
     * Only member with Admin or Moderator scope can perform this action.
     * Moderator or Admin can not perform this action on members with same Scope
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void kickGroupMember(@NonNull final String uid, @NonNull final String guid, @NonNull final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (guid != null && !TextUtils.isEmpty(guid)) {
                if (uid != null && !TextUtils.isEmpty(uid)) {
                    ApiConnection.getInstance().kickUser(guid, uid, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(ce);
                                        }
                                    });
                                } else {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_MEMBER_KICKED_SUCCESS);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                    e.getMessage());
                                HashMap<String, String> detailsMap = new HashMap<>();
                                detailsMap.put("uid", uid);
                                detailsMap.put("guid", guid);
                                handleException(methodName, e, detailsMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                    CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
                        }
                    });
                }
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                                CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("uid", uid);
            detailsMap.put("guid", guid);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * Method to ban a member from a Group
     *
     * @param uid      Unique identifier of a User
     * @param guid     Unique identifier of a Group
     * @param listener An object of the  <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-kick-ban-members#ban-a-group-member"}
     *
     * <b>Note</b>
     * Only member with Admin or Moderator scope can perform this action.
     * Moderator or Admin can not perform this action on members with same Scope
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void banGroupMember(@NonNull final String uid, @NonNull final String guid, @NonNull final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();

        try {
            if (guid != null && !TextUtils.isEmpty(guid)) {
                if (uid != null && !TextUtils.isEmpty(uid)) {
                    ApiConnection.getInstance().banUser(guid, uid, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(ce);
                                        }
                                    });
                                } else {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_MEMBER_BANNED_SUCCESS);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                    e.getMessage());
                                HashMap<String, String> detailsMap = new HashMap<>();
                                detailsMap.put("uid", uid);
                                detailsMap.put("guid", guid);
                                handleException(methodName, e, detailsMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                    CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
                        }
                    });
                }
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                                CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("uid", uid);
            detailsMap.put("guid", guid);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * Method to unban a member from a Group
     *
     * @param uid      Unique identifier of a User
     * @param guid     Unique identifier of a Group
     * @param listener An object of the  <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-kick-ban-members#unban-a-banned-group-member-from-a-group"}
     *
     * <b>Note</b>
     * Only member with Admin or Moderator scope can perform this action.
     * Moderator or Admin can not perform this action on members with same Scope
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void unbanGroupMember(@NonNull final String uid, @NonNull final String guid, @NonNull final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (guid != null && !TextUtils.isEmpty(guid)) {
                if (uid != null && !TextUtils.isEmpty(uid)) {
                    ApiConnection.getInstance().unbanUser(guid, uid, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(ce);
                                        }
                                    });
                                } else {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_MEMBER_UNBANNED_SUCCESS);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                    e.getMessage());
                                HashMap<String, String> detailsMap = new HashMap<>();
                                detailsMap.put("uid", uid);
                                detailsMap.put("guid", guid);
                                handleException(methodName, e, detailsMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                    CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
                        }
                    });
                }
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                                CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("uid", uid);
            detailsMap.put("guid", guid);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * Method to update scope a member in a Group
     *
     * @param uid      Unique identifier of a User
     * @param guid     Unique identifier of a Group
     * @param scope    Scope to be assigned to the user
     * @param listener An object of the  <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-change-member-scope"}
     *
     * <b>Note</b>
     * Only member with Admin or Moderator scope can perform this action.
     * Moderator or Admin can not perform this action on members with same Scope
     * @see CallbackListener
     * @see CometChatConstants.MemberScope
     * @since <b>v1</b>
     */
    public static void updateGroupMemberScope(
        @NonNull final String uid,
        @NonNull final String guid,
        @NonNull @CometChatConstants.MemberScope final String scope,
        @NonNull final CallbackListener<String> listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (guid != null && !TextUtils.isEmpty(guid)) {
                if (uid != null && !TextUtils.isEmpty(uid)) {
                    if (validateScope(scope) == null) {
                        ApiConnection.getInstance().changeMemberScope(guid, uid, scope, new ApiConnection.APIConnectionListener() {
                            @Override
                            public void onResponse(String response, final CometChatException ce) {
                                try {
                                    if (ce != null) {
                                        CometChat.postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(ce);
                                            }
                                        });
                                    } else {
                                        CometChat.postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_MEMBER_SCOPE_CHANGED_SUCCESS);
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                        e.getMessage());
                                    HashMap<String, String> detailsMap = new HashMap<>();
                                    detailsMap.put("uid", uid);
                                    detailsMap.put("guid", guid);
                                    detailsMap.put("scope", scope);
                                    handleException(methodName, e, detailsMap);
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(uncaughtException);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(validateScope(scope));
                            }
                        });
                    }
                } else {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                    CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
                        }
                    });
                }
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                                CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("uid", uid);
            detailsMap.put("guid", guid);
            detailsMap.put("scope", scope);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * Method to create a new group and add members to it at the same time
     *
     * @param group
     */
    public static void createGroupWithMembers(
        @NonNull final Group group,
        final List<GroupMember> members,
        final List<String> bannedUserIds,
        final CreateGroupWithMembersListener listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (validateGroup(group, listener)) {
                if (members != null || members.size() > 0) {
                    ApiConnection.getInstance().createGroupWithMembers(group, members, bannedUserIds, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(ce);
                                        }
                                    });
                                } else {
                                    JSONObject mainObject = new JSONObject(response);
                                    JSONObject dataObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    final Group group = Group.fromJson(dataObject.toString());
                                    if (dataObject.has(CometChatConstants.GroupKeys.GROUP_KEY_MEMBERS)) {
                                        JSONObject membersObject = dataObject.getJSONObject(CometChatConstants.GroupKeys.GROUP_KEY_MEMBERS);
                                        if (membersObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                            JSONObject membersDataObject = membersObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                            final HashMap<String, String> membersMap = getMembersMap(membersDataObject);
                                            postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onSuccess(group, membersMap);
                                                }
                                            });
                                        } else {
                                            Logger.exception(TAG, "Members data not found");
                                        }
                                    } else {
                                        Logger.exception(TAG, "Members array not found");
                                    }

                                }
                            } catch (Exception e) {
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                    e.getMessage());
                                HashMap<String, String> detailsMap = new HashMap<>();
                                detailsMap.put("group", group.toString());
                                detailsMap.put("members", members.toString());
                                detailsMap.put("bannedUids", bannedUserIds.toString());
                                detailsMap.put("response", response);
                                handleException(methodName, e, detailsMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_LIST_EMPTY,
                                                                    CometChatConstants.Errors.ERROR_LIST_EMPTY_MESSAGE));
                        }
                    });
                }
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("Group", group.toString());
            detailsMap.put("Members", members.toString());
            detailsMap.put("BannedUIDs", bannedUserIds.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    private static boolean validateGroup(Group group, CreateGroupWithMembersListener listener) {
        boolean isSuccess = true;
        try {
            if (group == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GROUP,
                                                        CometChatConstants.Errors.ERROR_INVALID_GROUP_MESSAGE));
                isSuccess = false;
            }
            if (group.getGuid() == null || TextUtils.isEmpty(group.getGuid())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                        CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                isSuccess = false;
            }
            if (group.getName() == null || TextUtils.isEmpty(group.getName())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME,
                                                        CometChatConstants.Errors.ERROR_EMPTY_GROUP_NAME_MESSAGE));
                isSuccess = false;
            }
            if (group.getGroupType() == null || TextUtils.isEmpty(group.getGroupType())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_GROUP_TYPE,
                                                        CometChatConstants.Errors.ERROR_EMPTY_GROUP_TYPE_MESSAGE));
                isSuccess = false;
            }
            if (CometChatUtils.isEmpty(group.getIcon())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_ICON,
                                                        CometChatConstants.Errors.ERROR_EMPTY_ICON_MESSAGE));
                isSuccess = false;
            }
            if (CometChatUtils.isEmpty(group.getDescription())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_DESCRIPTION,
                                                        CometChatConstants.Errors.ERROR_EMPTY_DESCRIPTION_MESSAGE));
                isSuccess = false;
            }
            if (group.getGroupType().equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PASSWORD)
                && (group.getPassword() == null || TextUtils.isEmpty(group.getPassword()))) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_PASSWORD_MISSING,
                                                        CometChatConstants.Errors.ERROR_PASSWORD_MISSING_MESSAGE));
                isSuccess = false;
            }
            if (group.getMetadata() != null && CometChatUtils.isJSONObjectEmpty(group.getMetadata())) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_METADATA,
                                                        CometChatConstants.Errors.ERROR_EMPTY_METADATA_MESSAGE));
                isSuccess = false;
            }
        } catch (Exception e) {
            Logger.error(e.toString());
            isSuccess = false;
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_NULLPOINTER_EXCEPTION, e.toString()));
        }
        return isSuccess;
    }


    /**
     * {@inheritDoc}
     * Method to transfer the group ownership to a different group member
     *
     * @param GUID     Unique identifier of the group
     * @param UID      Unique identifier of the user
     * @param listener An object of the  <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-transfer-group-ownership"}
     *
     * <b>Note</b>
     * Only members with Admin scope can perform this action.
     * @see CallbackListener
     * @see CometChatConstants.MemberScope
     * @since <b>v1</b>
     */
    public static void transferGroupOwnership(@NonNull final String GUID,
                                              @NonNull final String UID,
                                              @NonNull final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (GUID != null && !TextUtils.isEmpty(GUID)) {
                if (UID != null && !TextUtils.isEmpty(UID)) {
                    ApiConnection.getInstance().transferGroupOwnership(GUID, UID, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(ce);
                                        }
                                    });
                                } else {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_TRANSFER_OWNERSHIP_SUCCESS);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                    e.getMessage());
                                HashMap<String, String> detailsMap = new HashMap<>();
                                detailsMap.put("uid", UID);
                                detailsMap.put("guid", GUID);
                                handleException(methodName, e, detailsMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                    CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
                        }
                    });
                }
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                                CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("uid", UID);
            detailsMap.put("guid", GUID);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }


    /**
     * {@inheritDoc}
     * Sends the typing start indicator to user or group
     *
     * @param typingIndicator An object of <code>TypingIndicator<code/> class which requires receiverId and receiverType.
     *                        <code>TypingIndicator(@NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     *                        <code>TypingIndicator(@NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType, @NonNull JSONObject metadata)<code/>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-typing-indicators#start-typing"}
     * @see CometChatConstants.ReceiverTypes
     * @see TypingIndicator
     */
    public static void startTyping(@NonNull TypingIndicator typingIndicator) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        Settings settings = SettingsRepo.getSettings();
        if (getLoggedInUser() != null) {
            if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                    !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                    try {
                        if (typingIndicator != null) {
                            typingIndicator.setTypingStatus(TypingIndicator.TYPING_START);
                            if (startTypingMap.containsKey(typingIndicator.getReceiverId())) {
                                if (System.currentTimeMillis() - ((long) startTypingMap
                                    .get(typingIndicator.getReceiverId())
                                    .getLastTimestamp()) >= (long) CometChatConstants.ExtraKeys.TYPING_LIMIT) {
                                    typingIndicator.setLastTimestamp(System.currentTimeMillis());
                                    startTypingMap.put(typingIndicator.getReceiverId(), typingIndicator);
                                    endTypingMap.remove(typingIndicator.getReceiverId());
                                    if (rttConnection != null) {
                                        rttConnection.startTyping(typingIndicator);
                                    } else {
                                        Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                    }
                                }
                            } else {
                                typingIndicator.setLastTimestamp(System.currentTimeMillis());
                                startTypingMap.put(typingIndicator.getReceiverId(), typingIndicator);
                                endTypingMap.remove(typingIndicator.getReceiverId());
                                if (rttConnection != null) {
                                    rttConnection.startTyping(typingIndicator);
                                } else {
                                    Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("typingIndicator", typingIndicator.toString());
                        handleException(methodName, e, detailsMap);
                    }
                } else {
                    Logger.exception(TAG, "startTyping not functional in " + settings + " mode");
                }
            } else {
                Logger.exception(TAG, "startTyping not functional in feature-throttled mode");
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
        }
    }

    /**
     * {@inheritDoc}
     * Sends the end typing indicator to user or group
     *
     * @param typingIndicator An object of <code>TypingIndicator<code/> class which requires receiverId and receiverType.
     *                        <code>TypingIndicator(@NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     *                        <code>TypingIndicator(@NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType, @NonNull JSONObject metadata)<code/>
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-typing-indicators#stop-typing"}
     * @see CometChatConstants.ReceiverTypes
     * @see TypingIndicator
     * @since <b>v1</b>
     */
    public static void endTyping(@NonNull TypingIndicator typingIndicator) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        Settings settings = SettingsRepo.getSettings();
        if (getLoggedInUser() != null) {
            if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                    !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                    try {
                        if (typingIndicator != null) {
                            typingIndicator.setTypingStatus(TypingIndicator.TYPING_END);
                            if (startTypingMap.containsKey(typingIndicator.getReceiverId())) {
                                if (endTypingMap.containsKey(typingIndicator.getReceiverId())) {
                                    if (System.currentTimeMillis() - ((long) endTypingMap
                                        .get(typingIndicator.getReceiverId())
                                        .getLastTimestamp()) >= (long) CometChatConstants.ExtraKeys.TYPING_LIMIT) {
                                        typingIndicator.setLastTimestamp(System.currentTimeMillis());
                                        endTypingMap.put(typingIndicator.getReceiverId(), typingIndicator);
                                        startTypingMap.remove(typingIndicator.getReceiverId());
                                        if (rttConnection != null) {
                                            rttConnection.endTyping(typingIndicator);
                                        } else {
                                            Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                        }
                                    }
                                } else {
                                    typingIndicator.setLastTimestamp(System.currentTimeMillis());
                                    endTypingMap.put(typingIndicator.getReceiverId(), typingIndicator);
                                    startTypingMap.remove(typingIndicator.getReceiverId());
                                    if (rttConnection != null) {
                                        rttConnection.endTyping(typingIndicator);
                                    } else {
                                        Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("typingIndicator", typingIndicator.toString());
                        handleException(methodName, e, detailsMap);
                    }
                } else {
                    Logger.exception(TAG, "endTyping not functional in " + settings + " mode");
                }
            } else {
                Logger.exception(TAG, "endTyping not functional in feature-throttled mode");
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
        }

    }


    /**
     * This method marks the message with the provided Id and all the messages prior to that message as read for that for that particular conversation
     *
     * @param messageId    Unique Id of the message to be marked
     * @param receiverId   Id of the receiver whose message to be marked
     * @param receiverType Type of the receiver whether user or group
     * @version <b>v2</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v1</b>
     * @deprecated
     */
    @Deprecated
    public static void markAsRead(long messageId, @NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType) {
        Log.e(TAG, "This method is deprecated. Please use the new available methods");
    }

    // Mark As Read with callbacks

    /**
     * This method marks the message with the provided Id and all the messages prior to that message as read for that for that particular conversation
     *
     * @param messageId     Unique Id of the message to be marked
     * @param receiverId    Id of the receiver whose message to be marked
     * @param receiverType  Type of the receiver whether user or group
     * @param messageSender The UID of the sender of the message.
     * @param listener      An object of the  <code>CallbackListener&lt;Void&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v3</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markAsRead(long messageId,
                                  @NonNull String receiverId,
                                  @CometChatConstants.ReceiverTypes String receiverType,
                                  @NonNull String messageSender,
                                  @NonNull final CallbackListener<Void> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (messageId <= 0) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID,
                                             CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
        } else if (receiverId == null || TextUtils.isEmpty(receiverId)) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_ID,
                                             CometChatConstants.Errors.ERROR_INVALID_RECEIVER_ID_MESSAGE));
        } else if (receiverType == null || TextUtils.isEmpty(receiverType)) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE,
                                             CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
        } else if (messageSender == null || TextUtils.isEmpty(messageSender)) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_SENDER,
                                             CometChatConstants.Errors.ERROR_INVALID_MESSAGE_SENDER_MESSAGE));
        } else {
            Settings settings = SettingsRepo.getSettings();
            if (getLoggedInUser() != null) {
                if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                    if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                        !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                        try {
                            MessageReceipt messageReceipt = new MessageReceipt();
                            messageReceipt.setMessageId(messageId);
                            messageReceipt.setReceiverType(receiverType);
                            messageReceipt.setReceiverId(receiverId);
                            messageReceipt.setReceiptType(MessageReceipt.RECEIPT_TYPE_READ);
                            messageReceipt.setMessageSender(messageSender);
                            markAsRead(messageReceipt, listener);
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("messageId", String.valueOf(messageId));
                            detailsMap.put("receiverId", receiverId);
                            detailsMap.put("receiverType", receiverType);
                            detailsMap.put("messageSender", messageSender);
                            handleException(methodName, uncaughtException, detailsMap);
                            postError(listener, uncaughtException);
                        }
                    } else {
                        postError(listener,
                                  new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                         CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
                    }
                } else {
                    postError(listener,
                              new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                     CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
                }
            } else {
                postError(listener,
                          new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                 CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
            }
        }
    }


    /**
     * This method marks the message with the provided Id and all the messages prior to that message as read for that for that particular conversation
     *
     * @param messageId     Unique Id of the message to be marked
     * @param receiverId    Id of the receiver whose message to be marked
     * @param receiverType  Type of the receiver whether user or group
     * @param messageSender The UID of the sender of the message.
     * @version <b>v3</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markAsRead(long messageId,
                                  @NonNull String receiverId,
                                  @CometChatConstants.ReceiverTypes String receiverType,
                                  @NonNull String messageSender) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        Settings settings = SettingsRepo.getSettings();
        if (getLoggedInUser() != null) {
            if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                    !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                    try {
                        MessageReceipt messageReceipt = new MessageReceipt();
                        messageReceipt.setMessageId(messageId);
                        messageReceipt.setReceiverType(receiverType);
                        messageReceipt.setReceiverId(receiverId);
                        messageReceipt.setReceiptType(MessageReceipt.RECEIPT_TYPE_READ);
                        messageReceipt.setMessageSender(messageSender);
                        markAsRead(messageReceipt, null);
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("messageId", String.valueOf(messageId));
                        detailsMap.put("receiverId", receiverId);
                        detailsMap.put("receiverType", receiverType);
                        detailsMap.put("messageSender", messageSender);
                        handleException(methodName, e, detailsMap);
                    }
                } else {
                    Logger.exception(TAG, "markAsRead not functional in " + settings + " mode");
                }
            } else {
                Logger.exception(TAG, "markAsRead not functional in feature-throttled mode");
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
        }
    }

    //Mark As Read (BaseMessage) with callbacks

    /**
     * This method marks the message provided and all the messages prior to that message as read for that particular conversation
     *
     * @param message  An object of the <code>BaseMessage<code/> class which requires receiverUid,type and receiverType
     *                 <code>BaseMessage(String receiverUid, String type, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     * @param listener An object of the  <code>CallbackListener&lt;Void&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v3</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markAsRead(@NonNull BaseMessage message, @NonNull final CallbackListener<Void> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (message == null) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE,
                                             CometChatConstants.Errors.ERROR_INVALID_MESSAGE_MESSAGE));
        } else {
            Settings settings = SettingsRepo.getSettings();
            if (getLoggedInUser() != null) {
                if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                    if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                        !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                        try {
                            MessageReceipt messageReceipt = new MessageReceipt();
                            messageReceipt.setMessageId(message.getId());
                            messageReceipt.setReceiverType(message.getReceiverType());
                            String receiverUID = null;
                            if (message.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                                receiverUID = message.getReceiverUid();
                            } else {
                                if (message.getSender().getUid().equalsIgnoreCase(getLoggedInUser().getUid())) {
                                    receiverUID = message.getReceiverUid();
                                } else {
                                    receiverUID = message.getSender().getUid();
                                }
                            }
                            messageReceipt.setReceiverId(receiverUID);
                            messageReceipt.setReceiptType(MessageReceipt.RECEIPT_TYPE_READ);
                            messageReceipt.setMessageSender(message.getSender().getUid());
                            markAsRead(messageReceipt, listener);
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("messageId", String.valueOf(message.getId()));
                            detailsMap.put("receiverId", message.getReceiverUid());
                            detailsMap.put("receiverType", message.getReceiverType());
                            detailsMap.put("messageSender", message.getSender().getUid());
                            handleException(methodName, e, detailsMap);
                            postError(listener, uncaughtException);
                        }
                    } else {
                        postError(listener,
                                  new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                         CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
                    }
                } else {
                    postError(listener,
                              new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                     CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
                }
            } else {
                postError(listener,
                          new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                 CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
            }
        }
    }

    /**
     * This method marks the message provided and all the messages prior to that message as read for that particular conversation
     *
     * @param message An object of the <code>BaseMessage<code/> class which requires receiverUid,type and receiverType
     *                <code>BaseMessage(String receiverUid, String type, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     * @version <b>v3</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markAsRead(@NonNull BaseMessage message) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        Settings settings = SettingsRepo.getSettings();
        if (getLoggedInUser() != null) {
            if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                    !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                    try {
                        MessageReceipt messageReceipt = new MessageReceipt();
                        messageReceipt.setMessageId(message.getId());
                        messageReceipt.setReceiverType(message.getReceiverType());
                        String receiverUID = null;
                        if (message.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                            receiverUID = message.getReceiverUid();
                        } else {
                            if (message.getSender().getUid().equalsIgnoreCase(getLoggedInUser().getUid())) {
                                receiverUID = message.getReceiverUid();
                            } else {
                                receiverUID = message.getSender().getUid();
                            }
                        }
                        messageReceipt.setReceiverId(receiverUID);
                        messageReceipt.setReceiptType(MessageReceipt.RECEIPT_TYPE_READ);
                        messageReceipt.setMessageSender(message.getSender().getUid());
                        markAsRead(messageReceipt, null);
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("messageId", String.valueOf(message.getId()));
                        detailsMap.put("receiverId", message.getReceiverUid());
                        detailsMap.put("receiverType", message.getReceiverType());
                        detailsMap.put("messageSender", message.getSender().getUid());
                        handleException(methodName, e, detailsMap);
                    }
                } else {
                    Logger.exception(TAG, "markAsRead not functional in " + settings + " mode");
                }
            } else {
                Logger.exception(TAG, "markAsRead not functional in feature-throttled mode");
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
        }
    }

    /**
     * {@inheritDoc}
     * Marks message as Delivered
     *
     * @param messageId    Unique Id of the message to be marked
     * @param receiverId   Id of the receiver whose message to be marked
     * @param receiverType Type of the receiver whether user or group
     * @version <b>v2</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v1</b>
     * @deprecated
     */
    @Deprecated
    public static void markAsDelivered(long messageId, @NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType) {
        Log.e(TAG, "This method is deprecated. Please use the new available methods");
    }

    // Mark as delivered with callbacks

    /**
     * This method marks the message with the provided Id and all the messages prior to that message as delivered for that particular conversation
     *
     * @param messageId     Unique Id of the message to be marked
     * @param receiverId    Id of the receiver whose message to be marked
     * @param receiverType  Type of the receiver whether user or group
     * @param messageSender The UID of the sender of the message.
     * @param listener      An object of the  <code>CallbackListener&lt;Void&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v3</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markAsDelivered(long messageId,
                                       @NonNull String receiverId,
                                       @CometChatConstants.ReceiverTypes String receiverType,
                                       @NonNull String messageSender,
                                       @NonNull final CallbackListener<Void> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (messageId <= 0) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID,
                                             CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
        } else if (receiverId == null || TextUtils.isEmpty(receiverId)) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_ID,
                                             CometChatConstants.Errors.ERROR_INVALID_RECEIVER_ID_MESSAGE));
        } else if (receiverType == null || TextUtils.isEmpty(receiverType)) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE,
                                             CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
        } else if (messageSender == null || TextUtils.isEmpty(messageSender)) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_SENDER,
                                             CometChatConstants.Errors.ERROR_INVALID_MESSAGE_SENDER_MESSAGE));
        } else {
            Settings settings = SettingsRepo.getSettings();
            if (getLoggedInUser() != null) {
                if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                    if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                        !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                        try {
                            MessageReceipt messageReceipt = new MessageReceipt();
                            messageReceipt.setMessageId(messageId);
                            messageReceipt.setReceiverType(receiverType);
                            messageReceipt.setReceiverId(receiverId);
                            messageReceipt.setReceiptType(MessageReceipt.RECEIPT_TYPE_DELIVERED);
                            messageReceipt.setMessageSender(messageSender);
                            markAsDelivered(messageReceipt, listener);
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("messageId", String.valueOf(messageId));
                            detailsMap.put("receiverId", receiverId);
                            detailsMap.put("receiverType", receiverType);
                            detailsMap.put("messageSender", messageSender);
                            handleException(methodName, uncaughtException, detailsMap);
                            postError(listener, uncaughtException);
                        }
                    } else {
                        postError(listener,
                                  new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                         CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
                    }
                } else {
                    postError(listener,
                              new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                     CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
                }
            } else {
                postError(listener,
                          new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                 CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
            }
        }
    }

    /**
     * This method marks the message with the provided Id and all the messages prior to that message as delivered for that particular conversation
     *
     * @param messageId     Unique Id of the message to be marked
     * @param receiverId    Id of the receiver whose message to be marked
     * @param receiverType  Type of the receiver whether user or group
     * @param messageSender The UID of the sender of the message.
     * @version <b>v3</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markAsDelivered(long messageId,
                                       @NonNull String receiverId,
                                       @CometChatConstants.ReceiverTypes String receiverType,
                                       @NonNull String messageSender) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        Settings settings = SettingsRepo.getSettings();
        if (getLoggedInUser() != null) {
            if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                    !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                    try {
                        MessageReceipt messageReceipt = new MessageReceipt();
                        messageReceipt.setMessageId(messageId);
                        messageReceipt.setReceiverType(receiverType);
                        messageReceipt.setReceiverId(receiverId);
                        messageReceipt.setReceiptType(MessageReceipt.RECEIPT_TYPE_DELIVERED);
                        messageReceipt.setMessageSender(messageSender);
                        markAsDelivered(messageReceipt, null);
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("messageId", String.valueOf(messageId));
                        detailsMap.put("receiverId", receiverId);
                        detailsMap.put("receiverType", receiverType);
                        detailsMap.put("messageSender", messageSender);
                        handleException(methodName, uncaughtException, detailsMap);
                    }
                } else {
                    Logger.exception(TAG, "markAsDelivered not functional in " + settings + " mode");
                }
            } else {
                Logger.exception(TAG, "markAsDelivered not functional in feature-throttled mode");
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
        }
    }

    // Mark as Delivered (BaseMessage) with callbacks

    /**
     * This method marks the message provided and all the messages prior to that message as delivered for that particular conversation
     *
     * @param message  An object of the <code>BaseMessage<code/> class which requires receiverUid,type and receiverType
     *                 <code>BaseMessage(String receiverUid, String type, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     * @param listener An object of the  <code>CallbackListener&lt;Void&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v3</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markAsDelivered(@NonNull BaseMessage message, @NonNull final CallbackListener<Void> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (message == null) {
            postError(listener,
                      new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE,
                                             CometChatConstants.Errors.ERROR_INVALID_MESSAGE_MESSAGE));
        } else {
            Settings settings = SettingsRepo.getSettings();
            if (getLoggedInUser() != null) {
                if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                    if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                        !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                        try {
                            MessageReceipt messageReceipt = new MessageReceipt();
                            messageReceipt.setMessageId(message.getId());
                            messageReceipt.setReceiverType(message.getReceiverType());
                            String receiverUID = null;
                            if (message.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                                receiverUID = message.getReceiverUid();
                            } else {
                                if (message.getSender().getUid().equalsIgnoreCase(getLoggedInUser().getUid())) {
                                    receiverUID = message.getReceiverUid();
                                } else {
                                    receiverUID = message.getSender().getUid();
                                }
                            }
                            messageReceipt.setReceiverId(receiverUID);
                            messageReceipt.setReceiptType(MessageReceipt.RECEIPT_TYPE_DELIVERED);
                            messageReceipt.setMessageSender(message.getSender().getUid());
                            markAsDelivered(messageReceipt, listener);
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("messageId", String.valueOf(message.getId()));
                            detailsMap.put("receiverId", message.getReceiverUid());
                            detailsMap.put("receiverType", message.getReceiverType());
                            detailsMap.put("messageSender", message.getSender().getUid());
                            handleException(methodName, uncaughtException, detailsMap);
                            postError(listener, uncaughtException);
                        }
                    } else {
                        postError(listener,
                                  new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                         CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
                    }
                } else {
                    postError(listener,
                              new CometChatException(CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED,
                                                     CometChatConstants.Errors.ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE));
                }
            } else {
                postError(listener,
                          new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                 CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
            }
        }
    }

    /**
     * This method marks the message provided and all the messages prior to that message as delivered for that particular conversation
     *
     * @param message An object of the <code>BaseMessage<code/> class which requires receiverUid,type and receiverType
     *                <code>BaseMessage(String receiverUid, String type, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     * @version <b>v3</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markAsDelivered(@NonNull BaseMessage message) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        Settings settings = SettingsRepo.getSettings();
        if (getLoggedInUser() != null) {
            if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_LIMITED_TRANSIENT) &&
                    !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                    try {
                        MessageReceipt messageReceipt = new MessageReceipt();
                        messageReceipt.setMessageId(message.getId());
                        messageReceipt.setReceiverType(message.getReceiverType());
                        String receiverUID = null;
                        if (message.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                            receiverUID = message.getReceiverUid();
                        } else {
                            if (message.getSender().getUid().equalsIgnoreCase(getLoggedInUser().getUid())) {
                                receiverUID = message.getReceiverUid();
                            } else {
                                receiverUID = message.getSender().getUid();
                            }
                        }
                        messageReceipt.setReceiverId(receiverUID);
                        messageReceipt.setReceiptType(MessageReceipt.RECEIPT_TYPE_DELIVERED);
                        messageReceipt.setMessageSender(message.getSender().getUid());
                        markAsDelivered(messageReceipt, null);
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("messageId", String.valueOf(message.getId()));
                        detailsMap.put("receiverId", message.getReceiverUid());
                        detailsMap.put("receiverType", message.getReceiverType());
                        detailsMap.put("messageSender", message.getSender().getUid());
                        handleException(methodName, e, detailsMap);
                    }
                } else {
                    Logger.exception(TAG, "markAsDelivered not functional in " + settings + " mode");
                }
            } else {
                Logger.exception(TAG, "markAsDelivered not functional in feature-throttled mode");
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
        }
    }

    private static void markAsDelivered(MessageReceipt messageReceipt, CallbackListener<Void> listener) {
        if (Objects.equals(getConnectionStatus(), CometChatConstants.WS_STATE_DISCONNECTED) || Objects.equals(getConnectionStatus(),
                                                                                                              CometChatConstants.WS_STATE_CONNECTING)) {
            markAsDeliveredInternal(messageReceipt, listener);
        } else {
            if (rttConnection != null) {
                rttConnection.markAsDelivered(messageReceipt, listener);
            } else {
                Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
            }
        }
    }

    private static void markAsRead(MessageReceipt messageReceipt, CallbackListener<Void> listener) {
        if (Objects.equals(getConnectionStatus(), CometChatConstants.WS_STATE_DISCONNECTED) || Objects.equals(getConnectionStatus(),
                                                                                                              CometChatConstants.WS_STATE_CONNECTING)) {
            markAsReadInternal(messageReceipt, listener);
        } else {
            if (rttConnection != null) {
                rttConnection.markAsRead(messageReceipt, listener);
            } else {
                Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
            }
        }
    }

    /**
     * This method marks a conversation as delivered by identifying it with the provided Id.
     *
     * @param conversationWithId    Unique Id of the message up to which the conversation
     *                     should be marked as delivered
     * @param conversationType   Id of the receiver (UID for users, GUID for groups)
     * @param listener     An object of the {@code CallbackListener<Void>} class
     *                     that helps inform the developer if the operation was
     *                     successful or any error occurred
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markConversationAsDelivered(@NonNull String conversationWithId, @CometChatConstants.ConversationTypes String conversationType, @NonNull final CallbackListener<String> listener) {
        if (conversationWithId.isEmpty()) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_ID, CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_ID_MESSAGE));
        } else if (conversationType == null || TextUtils.isEmpty(conversationType)) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE, CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
        } else {
            markConversationAsDeliveredInternal(conversationWithId, conversationType, listener);
        }
    }

    private static void markConversationAsDeliveredInternal(String uid, String receiverType, @Nullable final CallbackListener<String> listener) {
        try {
            ApiConnection.getInstance().markConversationAsDelivered(uid, receiverType, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null)
                                        listener.onError(ce);
                                }
                            });
                        } else {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            final String message = data.optString(CometChatConstants.ResponseKeys.KEY_MESSAGE);
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null)
                                        listener.onSuccess(message);
                                }
                            });
                        }
                    } catch (final Exception e) {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null)
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Logger.error(TAG, "onException: markConversationAsDeliveredInternal: " + e);
        }
    }

    /**
     * This method marks a conversation as read by identifying it with the provided Id.
     *
     * @param conversationWithId    Unique Id of the message up to which the conversation
     *                     should be marked as read
     * @param conversationType   Id of the receiver (UID for users, GUID for groups)
     * @param listener     An object of the {@code CallbackListener<Void>} class
     *                     that helps inform the developer if the operation was
     *                     successful or any error occurred
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v3</b>
     */
    public static void markConversationAsRead(@NonNull String conversationWithId, @CometChatConstants.ConversationTypes String conversationType, @NonNull final CallbackListener<String> listener) {
        if (conversationWithId.isEmpty()) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_ID, CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_ID_MESSAGE));
        } else if (conversationType == null || TextUtils.isEmpty(conversationType)) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE, CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
        } else {
            markConversationAsReadInternal(conversationWithId, conversationType, listener);
        }
    }

    private static void markConversationAsReadInternal(String uid, @CometChatConstants.ConversationTypes String conversationType, @Nullable final CallbackListener<String> listener) {
        try {
            ApiConnection.getInstance().markConversationAsRead(uid, conversationType, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null)
                                        listener.onError(ce);
                                }
                            });
                        } else {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            final String message = data.optString(CometChatConstants.ResponseKeys.KEY_MESSAGE);
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null)
                                        listener.onSuccess(message);
                                }
                            });
                        }
                    } catch (final Exception e) {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null)
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Logger.error(TAG, "onException: markConversationAsDeliveredInternal: " + e);
        }
    }

    /**
     * This method marks a message as unread by identifying it with the provided Id.
     *
     * @param message  An object of the <code>BaseMessage<code/> class which requires id, sender and receiverType
     *                 <code>BaseMessage(long id, User sender, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     * @param listener An object of the  <code>CallbackListener&lt;Conversation&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @see CallbackListener
     * @since v4
     */
    public static void markMessageAsUnread(@NonNull BaseMessage message, @NonNull final CallbackListener<Conversation> listener) {
        if (message.getId() < 0) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_ID, CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
        } else if (message.getSender() == null) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_SENDER, CometChatConstants.Errors.ERROR_INVALID_MESSAGE_SENDER_MESSAGE));
        } else if (message.getReceiverType() == null || TextUtils.isEmpty(message.getReceiverType())) {
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE, CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
        } else if (message.getSender().getUid().equals(getLoggedInUser().getUid())) {
            String uid = message.getSender().getUid();
            postError(listener, new CometChatException(CometChatConstants.Errors.ERROR_MESSAGE_NOT_A_RECEIVER, String.format(CometChatConstants.Errors.ERROR_MESSAGE_NOT_A_RECEIVER_MESSAGE, uid, message.getId())));
        } else {
            String uid;
            if (CometChatConstants.RECEIVER_TYPE_USER.equals(message.getReceiverType())) {
                uid = message.getSender().getUid();
            } else {
                uid = message.getReceiverUid();
            }
            markMessageAsUnreadInternal(message.getId(), uid, message.getReceiverType(), listener);
        }
    }

    private static void markMessageAsUnreadInternal(long messageId, String uid, @CometChatConstants.ReceiverTypes String conversationType, @Nullable final CallbackListener<Conversation> listener) {
        try {
            ApiConnection.getInstance().markMessageAsUnread(messageId, uid, conversationType, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null)
                                        listener.onError(ce);
                                }
                            });
                        } else {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            final Conversation conversation = Conversation.fromJSON(data.getJSONObject(CometChatConstants.ResponseKeys.KEY_CONVERSATION));
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null)
                                        listener.onSuccess(conversation);
                                }
                            });
                        }
                    } catch (final Exception e) {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null)
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Logger.error(TAG, "onException: markConversationAsDeliveredInternal: " + e);
        }
    }
    /**
     * This method marks a message as interacted by identifying it with the provided Id.
     * It also logs the interactive element associated with the interaction.
     *
     * @param messageId           The unique Id of the message to be marked as interacted.
     * @param interactedElementId The Id of the element that was interacted with.
     * @param listener            The CallbackListener that will handle the response, e.g., onSuccess or onError.
     * @see CallbackListener
     * @since v4
     */
    public static void markAsInteracted(long messageId, String interactedElementId, CallbackListener<Void> listener) {
        markAsInteracted(messageId, Arrays.asList(interactedElementId), listener);
    }

    /**
     * This method marks a message, identified by messageId, as interacted with by using a list of interacted elements' identifiers.
     * The API call is performed and the response is handled in the provided listener.
     *
     * @param messageId              The unique Id of the message to be marked as interacted.
     * @param interactedElementArray The list of Ids of the elements that were interacted with.
     * @param listener               The CallbackListener that will handle the response, e.g., onSuccess or onError.
     * @since v4
     */
    public static void markAsInteracted(long messageId, List<String> interactedElementArray, CallbackListener<Void> listener) {
        JSONArray interactedIdArray = CometChatUtils.getJSONArrayFromList(interactedElementArray);
        markAsInteractedInternal(messageId, interactedIdArray, listener);
    }

    /**
     * {@inheritDoc}
     * Method to logout from CometChat SDK
     *
     * @param listener An object of the  <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/authentication#logout"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void logout(@NonNull final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection.getInstance().logout(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, CometChatException ce) {
                try {
                    if (ce != null) {
                        internalLogout(true);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_LOGOUT_SUCCESS);
                            }
                        });
                    } else {
                        internalLogout(true);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_LOGOUT_SUCCESS);
                            }
                        });
                    }
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    handleException(methodName, e, detailsMap);
                    internalLogout(true);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_LOGOUT_SUCCESS);
                        }
                    });
                }
            }
        });

    }

    static void internalLogout(boolean informListeners) {
        internalDisconnect();
        ExtensionManager.callOnLogout();
        PreferenceHelper.clearPreferences();
        SQLiteManager.getInstance().deleteDatabase(context);
        loggedInUser = null;
        cometChatInstance = null;
        if (informListeners)
            logoutSuccess();
    }

    private static void internalDisconnect() {
        disconnectFromWS();
        AnalyticsController.getInstance().logout();
    }

    // Fetch Messages methods
    static void getAllMessages(
            final int limit,
            final String affix,
            final long timestamp,
            final long messageId,
            final boolean unread,
            final boolean hideMessagesFromBlockedUsers,
            final String searchKeyword,
            final long updatedAfter,
            final boolean updatesOnly,
            final List<String> categories,
            final List<String> types,
            boolean hideReplies,
            boolean hideDeleted,
            List<String> tags,
            boolean withTags,
            boolean interactionGoalCompleted,
            boolean mentionsWithTagInfo,
            boolean mentionsWithBlockedInfo,
            boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes,
            boolean hideQuotedMessages, final MessagesRequest.MessagesFetchedListener messagesFetchedListener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection
            .getInstance()
            .getAllMessages(limit,
                            affix,
                            timestamp,
                            messageId,
                            unread,
                            hideMessagesFromBlockedUsers,
                            searchKeyword,
                            updatedAfter,
                            updatesOnly,
                            categories,
                            types,
                            hideReplies,
                            hideDeleted,
                            tags,
                            withTags,
                            interactionGoalCompleted,
                            mentionsWithTagInfo,
                            mentionsWithBlockedInfo,
                            hasAttachments,
                            hasLinks,
                            hasMentions,
                            hasReactions,
                            mentionedUids,
                            attachmentTypes,
                            hideQuotedMessages,
                            new ApiConnection.APIConnectionListener() {
                                @Override
                                public void onResponse(String response, final CometChatException ce) {
                                    try {
                                        if (ce != null) {
                                            CometChat.postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    messagesFetchedListener.onMessagesFetched(null, null, ce);
                                                }
                                            });
                                        } else {
                                            try {
                                                final List<BaseMessage> baseMessages = BaseMessage.getMessagesFromJSON(response);
                                                messagesFetchedListener.onMessagesFetched(baseMessages, response, null);
                                            } catch (JSONException e) {
                                                messagesFetchedListener.onMessagesFetched(null,
                                                                                          null,
                                                                                          new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                                 e.getMessage()));
                                            }

                                        }
                                    } catch (Exception e) {
                                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                            e.getMessage());
                                        HashMap<String, String> detailsMap = new HashMap<>();
                                        detailsMap.put("limit", String.valueOf(limit));
                                        detailsMap.put("affix", affix);
                                        detailsMap.put("timestamp", String.valueOf(timestamp));
                                        detailsMap.put("messageId", String.valueOf(messageId));
                                        detailsMap.put("unread", String.valueOf(unread));
                                        detailsMap.put("hideMessagesFromBlockedUsers", String.valueOf(hideMessagesFromBlockedUsers));
                                        detailsMap.put("searchKeyword", String.valueOf(searchKeyword));
                                        detailsMap.put("updatedAfter", String.valueOf(updatedAfter));
                                        detailsMap.put("updatesOnly", String.valueOf(updatesOnly));
                                        detailsMap.put("categories", categories != null ? CometChatUtils.getCSStringFromList(categories) : "");
                                        detailsMap.put("types", types != null ? CometChatUtils.getCSStringFromList(types) : "");
                                        handleException(methodName, e, detailsMap);
                                    }
                                }
                            });
    }

    static void getUserConversationsInGroup(
            final String UID,
            final String GUID,
            final int limit,
            final String affix,
            final long timestamp,
            final long messageId,
            final boolean unread,
            final boolean hideMessagesFromBlockedUsers,
            final String searchKeyword,
            final long updatedAfter,
            final boolean updatesOnly,
            final List<String> categories,
            final List<String> types,
            boolean hideReplies,
            boolean hideDeleted,
            List<String> tags,
            boolean withTags,
            boolean interactionGoalCompleted,
            boolean mentionsWithTagInfo,
            boolean mentionsWithBlockedInfo,
            boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes,
            boolean hideQuotedMessages, final MessagesRequest.MessagesFetchedListener messagesFetchedListener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection
            .getInstance()
            .getUserConversationsInGroup(UID,
                                         GUID,
                                         limit,
                                         affix,
                                         timestamp,
                                         messageId,
                                         unread,
                                         hideMessagesFromBlockedUsers,
                                         searchKeyword,
                                         updatedAfter,
                                         updatesOnly,
                                         categories,
                                         types,
                                         hideReplies,
                                         hideDeleted,
                                         tags,
                                         withTags,
                                         interactionGoalCompleted,
                                         mentionsWithTagInfo,
                                         mentionsWithBlockedInfo,
                                         hasAttachments,
                                         hasLinks,
                                         hasMentions,
                                         hasReactions,
                                         mentionedUids,
                                         attachmentTypes,
                                         hideQuotedMessages,
                                         new ApiConnection.APIConnectionListener() {
                                             @Override
                                             public void onResponse(String response, final CometChatException ce) {
                                                 try {
                                                     if (ce != null) {
                                                         CometChat.postOnMainThread(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 messagesFetchedListener.onMessagesFetched(null, null, ce);
                                                             }
                                                         });
                                                     } else {
                                                         try {
                                                             final List<BaseMessage> baseMessages = BaseMessage.getMessagesFromJSON(response);
                                                             messagesFetchedListener.onMessagesFetched(baseMessages, response, null);
                                                         } catch (JSONException e) {
                                                             messagesFetchedListener.onMessagesFetched(null,
                                                                                                       null,
                                                                                                       new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                                              e.getMessage()));
                                                         }
                                                     }
                                                 } catch (Exception e) {
                                                     final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                                         e.getMessage());
                                                     HashMap<String, String> detailsMap = new HashMap<>();
                                                     detailsMap.put("UID", String.valueOf(UID));
                                                     detailsMap.put("GUID", String.valueOf(GUID));
                                                     detailsMap.put("limit", String.valueOf(limit));
                                                     detailsMap.put("affix", affix);
                                                     detailsMap.put("timestamp", String.valueOf(timestamp));
                                                     detailsMap.put("messageId", String.valueOf(messageId));
                                                     detailsMap.put("unread", String.valueOf(unread));
                                                     detailsMap.put("hideMessagesFromBlockedUsers", String.valueOf(hideMessagesFromBlockedUsers));
                                                     detailsMap.put("searchKeyword", String.valueOf(searchKeyword));
                                                     detailsMap.put("updatedAfter", String.valueOf(updatedAfter));
                                                     detailsMap.put("updatesOnly", String.valueOf(updatesOnly));
                                                     detailsMap.put("categories",
                                                                    categories != null ? CometChatUtils.getCSStringFromList(categories) : "");
                                                     detailsMap.put("types", types != null ? CometChatUtils.getCSStringFromList(types) : "");
                                                     handleException(methodName, e, detailsMap);
                                                 }
                                             }
                                         });
    }

    static void getGroupConversations(
            final String GUID,
            final int limit,
            final String affix,
            final long timestamp,
            final long messageId,
            final boolean unread,
            final boolean hideMessagesFromBlockedUsers,
            final String searchKeyword,
            final long updatedAfter,
            final boolean updatesOnly,
            final List<String> categories,
            final List<String> types,
            boolean hideReplies,
            boolean hideDeleted,
            List<String> tags,
            boolean withTags,
            boolean interactionGoalCompleted,
            boolean mentionsWithTagInfo,
            boolean mentionsWithBlockedInfo,
            boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes,
            boolean hideQuotedMessages, final MessagesRequest.MessagesFetchedListener messagesFetchedListener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection
            .getInstance()
            .getGroupConversations(GUID,
                                   limit,
                                   affix,
                                   timestamp,
                                   messageId,
                                   unread,
                                   hideMessagesFromBlockedUsers,
                                   searchKeyword,
                                   updatedAfter,
                                   updatesOnly,
                                   categories,
                                   types,
                                   hideReplies,
                                   hideDeleted,
                                   tags,
                                   withTags,
                                   interactionGoalCompleted,
                                   mentionsWithTagInfo,
                                   mentionsWithBlockedInfo,
                                   hasAttachments,
                                   hasLinks,
                                   hasMentions,
                                   hasReactions,
                                   mentionedUids,
                                   attachmentTypes,
                                   hideQuotedMessages,
                                   new ApiConnection.APIConnectionListener() {
                                       @Override
                                       public void onResponse(String response, final CometChatException ce) {
                                           try {
                                               if (ce != null) {
                                                   CometChat.postOnMainThread(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                           messagesFetchedListener.onMessagesFetched(null, null, ce);
                                                       }
                                                   });
                                               } else {
                                                   try {
                                                       final List<BaseMessage> baseMessages = BaseMessage.getMessagesFromJSON(response);
                                                       messagesFetchedListener.onMessagesFetched(baseMessages, response, null);
                                                   } catch (JSONException e) {
                                                       messagesFetchedListener.onMessagesFetched(null,
                                                                                                 null,
                                                                                                 new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                                        e.getMessage()));
                                                   }
                                               }
                                           } catch (Exception e) {
                                               final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                                   e.getMessage());
                                               HashMap<String, String> detailsMap = new HashMap<>();
                                               detailsMap.put("GUID", String.valueOf(GUID));
                                               detailsMap.put("limit", String.valueOf(limit));
                                               detailsMap.put("affix", affix);
                                               detailsMap.put("timestamp", String.valueOf(timestamp));
                                               detailsMap.put("messageId", String.valueOf(messageId));
                                               detailsMap.put("unread", String.valueOf(unread));
                                               detailsMap.put("hideMessagesFromBlockedUsers", String.valueOf(hideMessagesFromBlockedUsers));
                                               detailsMap.put("searchKeyword", String.valueOf(searchKeyword));
                                               detailsMap.put("updatedAfter", String.valueOf(updatedAfter));
                                               detailsMap.put("updatesOnly", String.valueOf(updatesOnly));
                                               detailsMap.put("categories", categories != null ? CometChatUtils.getCSStringFromList(categories) : "");
                                               detailsMap.put("types", types != null ? CometChatUtils.getCSStringFromList(types) : "");
                                               handleException(methodName, e, detailsMap);
                                           }
                                       }
                                   });
    }

    static void getThreadedMessages(
            final long parentMessageId,
            final int limit,
            final String affix,
            final long timestamp,
            final long messageId,
            final boolean unread,
            final boolean hideMessagesFromBlockedUsers,
            final String searchKeyword,
            final long updatedAfter,
            final boolean updatesOnly,
            final List<String> categories,
            final List<String> types,
            boolean hideDeleted,
            List<String> tags,
            boolean withTags,
            boolean interactionGoalCompleted,
            boolean mentionsWithTagInfo,
            boolean mentionsWithBlockedInfo,
            boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes, boolean withParent,
            boolean hideQuotedMessages, final MessagesRequest.MessagesFetchedListener messagesFetchedListener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection
            .getInstance()
            .getThreadedMessages(parentMessageId,
                                 limit,
                                 affix,
                                 timestamp,
                                 messageId,
                                 unread,
                                 hideMessagesFromBlockedUsers,
                                 searchKeyword,
                                 updatedAfter,
                                 updatesOnly,
                                 categories,
                                 types,
                                 hideDeleted,
                                 tags,
                                 withTags,
                                 interactionGoalCompleted,
                                 mentionsWithTagInfo,
                                 mentionsWithBlockedInfo,
                                 hasAttachments,
                                 hasLinks,
                                 hasMentions,
                                 hasReactions,
                                 mentionedUids,
                                 attachmentTypes,
                                 withParent,
                                 hideQuotedMessages,
                                 new ApiConnection.APIConnectionListener() {
                                     @Override
                                     public void onResponse(String response, final CometChatException ce) {

                                         try {
                                             if (ce != null) {
                                                 CometChat.postOnMainThread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         messagesFetchedListener.onMessagesFetched(null, null, ce);
                                                     }
                                                 });
                                             } else {
                                                 try {
                                                     final List<BaseMessage> baseMessages = BaseMessage.getMessagesFromJSON(response);
                                                     messagesFetchedListener.onMessagesFetched(baseMessages, response, null);
                                                 } catch (JSONException e) {
                                                     messagesFetchedListener.onMessagesFetched(null,
                                                                                               null,
                                                                                               new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                                      e.getMessage()));
                                                 }
                                             }
                                         } catch (Exception e) {
                                             final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                                 e.getMessage());
                                             HashMap<String, String> detailsMap = new HashMap<>();
                                             detailsMap.put("parentMessageId", String.valueOf(parentMessageId));
                                             detailsMap.put("limit", String.valueOf(limit));
                                             detailsMap.put("affix", affix);
                                             detailsMap.put("timestamp", String.valueOf(timestamp));
                                             detailsMap.put("messageId", String.valueOf(messageId));
                                             detailsMap.put("unread", String.valueOf(unread));
                                             detailsMap.put("hideMessagesFromBlockedUsers", String.valueOf(hideMessagesFromBlockedUsers));
                                             detailsMap.put("searchKeyword", String.valueOf(searchKeyword));
                                             detailsMap.put("updatedAfter", String.valueOf(updatedAfter));
                                             detailsMap.put("updatesOnly", String.valueOf(updatesOnly));
                                             detailsMap.put("categories", categories != null ? CometChatUtils.getCSStringFromList(categories) : "");
                                             detailsMap.put("types", types != null ? CometChatUtils.getCSStringFromList(types) : "");
                                             handleException(methodName, e, detailsMap);
                                         }
                                     }
                                 });

    }

    static void getUserConversations(
            final String UID,
            final int limit,
            final String affix,
            final long timestamp,
            final long messageId,
            final boolean unread,
            final boolean hideMessagesFromBlockedUsers,
            final String searchKeyword,
            final long updatedAfter,
            final boolean updatesOnly,
            final List<String> categories,
            final List<String> types,
            boolean hideReplies,
            boolean hideDeleted,
            List<String> tags,
            boolean withTags,
            boolean interactionGoalCompleted,
            boolean mentionsWithTagInfo,
            boolean mentionsWithBlockedInfo,
            boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes,
            boolean hideQuotedMessages, final MessagesRequest.MessagesFetchedListener messagesFetchedListener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection
            .getInstance()
            .getUserConversations(UID,
                                  limit,
                                  affix,
                                  timestamp,
                                  messageId,
                                  unread,
                                  hideMessagesFromBlockedUsers,
                                  searchKeyword,
                                  updatedAfter,
                                  updatesOnly,
                                  categories,
                                  types,
                                  hideReplies,
                                  hideDeleted,
                                  tags,
                                  withTags,
                                  interactionGoalCompleted,
                                  mentionsWithTagInfo,
                                  mentionsWithBlockedInfo,
                                  hasAttachments,
                                  hasLinks,
                                  hasMentions,
                                  hasReactions,
                                  mentionedUids,
                                  attachmentTypes,
                                  hideQuotedMessages,
                                  new ApiConnection.APIConnectionListener() {
                                      @Override
                                      public void onResponse(String response, final CometChatException ce) {

                                          try {
                                              if (ce != null) {
                                                  CometChat.postOnMainThread(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          messagesFetchedListener.onMessagesFetched(null, null, ce);
                                                      }
                                                  });
                                              } else {
                                                  try {
                                                      final List<BaseMessage> baseMessages = BaseMessage.getMessagesFromJSON(response);
                                                      messagesFetchedListener.onMessagesFetched(baseMessages, response, null);
                                                  } catch (JSONException e) {
                                                      messagesFetchedListener.onMessagesFetched(null,
                                                                                                null,
                                                                                                new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                                       e.getMessage()));
                                                  }
                                              }
                                          } catch (Exception e) {
                                              final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                                  e.getMessage());
                                              HashMap<String, String> detailsMap = new HashMap<>();
                                              detailsMap.put("UID", String.valueOf(UID));
                                              detailsMap.put("limit", String.valueOf(limit));
                                              detailsMap.put("affix", affix);
                                              detailsMap.put("timestamp", String.valueOf(timestamp));
                                              detailsMap.put("messageId", String.valueOf(messageId));
                                              detailsMap.put("unread", String.valueOf(unread));
                                              detailsMap.put("hideMessagesFromBlockedUsers", String.valueOf(hideMessagesFromBlockedUsers));
                                              detailsMap.put("searchKeyword", String.valueOf(searchKeyword));
                                              detailsMap.put("updatedAfter", String.valueOf(updatedAfter));
                                              detailsMap.put("updatesOnly", String.valueOf(updatesOnly));
                                              detailsMap.put("categories", categories != null ? CometChatUtils.getCSStringFromList(categories) : "");
                                              detailsMap.put("types", types != null ? CometChatUtils.getCSStringFromList(types) : "");
                                              handleException(methodName, e, detailsMap);
                                          }
                                      }
                                  });

    }

    /**
     * {@inheritDoc}
     * A method to initiate call between between two Users or a particular Group
     *
     * @param call     An object of the <code>Call<code/> class which requires receiverId,receiverType and callType to work as expected
     *                 <code><Call(@NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType, @CometChatConstants.CallType String callType)code/>
     * @param listener An object of the  <code>CallbackListener&lt;Call&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/calling-default-calling#initiate-call"}
     * @see Call
     * @see CometChatConstants.ReceiverTypes
     * @see CometChatConstants.CallType
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void initiateCall(@NonNull final Call call, @NonNull final CallbackListener<Call> listener) {
        initiateCall(call, 45, listener);
    }

    /**
     * Initiates a call with a configurable timeout.
     *
     * @param call     An object of the {@link Call} class
     * @param timeout  Timeout in seconds before the call is auto-cancelled. Values &lt;= 0 fall back to the default of 45 seconds.
     * @param listener An object of the {@link CallbackListener} class
     */
    public static void initiateCall(@NonNull final Call call, int timeout, @NonNull final CallbackListener<Call> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        final int resolvedTimeout = timeout > 0 ? timeout : DEFAULT_TIMEOUT;
        try {
            if (CallManager.getInstance().deprecatedCallModuleExists() || CallManager.getInstance().callModuleExists()) {
                Log.d(TAG, "Call Initiated : " + System.currentTimeMillis());
                if (call != null) {
                    if (call.getReceiverUid() == null || TextUtils.isEmpty(call.getReceiverUid().trim())) {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
                        return;
                    }
                    if (call.getType() == null || TextUtils.isEmpty(call.getType().trim())) {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CALL_TYPE,
                                                                CometChatConstants.Errors.ERROR_INVALID_CALL_TYPE_MESSAGE));
                        return;
                    }
                    if (call.getReceiverType() == null || TextUtils.isEmpty(call.getReceiverType().trim())) {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE,
                                                                CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
                        return;
                    }
                    if (getActiveCall() != null) {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_IN_PROGRESS,
                                                                CometChatConstants.Errors.ERROR_CALL_IN_PROGRESS_MESSAGE));
                        return;
                    }
                    call.setCallStatus(CometChatConstants.CALL_STATUS_INITIATED);
                    ApiConnection.getInstance().initiateConference(call, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(ce);
                                        }
                                    });
                                } else {
                                    try {
                                        JSONObject mainObject = new JSONObject(response);
                                        final Call receivedCall = Call.fromJson(mainObject
                                                                                    .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                    .toString());
                                        CallManager.setCallEventListener(callEventListener);
                                        CallManager.getInstance().initiateCall(receivedCall, resolvedTimeout);
                                        CometChat.postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onSuccess(receivedCall);
                                            }
                                        });
                                        Log.d(TAG, "Call Initiated Successfully : " + receivedCall.getSessionId() + System.currentTimeMillis());
                                    } catch (final JSONException e) {
                                        CometChat.postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                        e.getMessage()));
                                            }
                                        });
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                    e.getMessage());
                                HashMap<String, String> detailsMap = new HashMap<>();
                                detailsMap.put("call", call.toString());
                                handleException(methodName, e, detailsMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CALL,
                                                            CometChatConstants.Errors.ERROR_INVALID_CALL_MESSAGE));
                }
            } else {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND,
                                                                CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("call", call.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * A method to reject call between between two Users or a particular Group
     *
     * @param sessionId The Unique sessionId of the call be to rejected
     * @param status    Status to be sent while rejecting a call
     *                  <code>CometChatConstants.CALL_STATUS_REJECTED<code/>
     *                  <code>CometChatConstants.CALL_STATUS_CANCELLED<code/>
     *                  <code>CometChatConstants.CALL_STATUS_BUSY<code/>
     * @param listener  An object of the  <code>CallbackListener&lt;Call&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/calling-default-calling#reject-the-incoming-call"}
     * @see CometChatConstants.CallType
     * @see Call
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void rejectCall(@NonNull String sessionId,
                                  @CometChatConstants.CallStatus String status,
                                  @NonNull final CallbackListener<Call> listener) {
        if (CallManager.getInstance().deprecatedCallModuleExists() || CallManager.getInstance().callModuleExists()) {
            Log.d(TAG, "Call Rejected : " + status + " " + sessionId + " " + System.currentTimeMillis());
            switch (status) {
                case CometChatConstants.CALL_STATUS_REJECTED:
                    rejectCall(sessionId, listener);
                    break;
                case CometChatConstants.CALL_STATUS_CANCELLED:
                    cancelCall(sessionId, listener);
                    break;
                case CometChatConstants.CALL_STATUS_BUSY:
                    sendBusyResponse(sessionId, listener);
                    break;
                default:
                    //TODO : Send exception
                    break;
            }
        } else {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND,
                                                            CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE));
                }
            });
        }
    }

    /**
     * A method to accept call between between two Users or a particular Group
     *
     * @param sessionId The Unique sessionId of the call be to rejected
     * @param listener  An object of the  <code>CallbackListener&lt;Call&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/calling-default-calling#accept-the-incoming-call"}
     * @see Call
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void acceptCall(@NonNull final String sessionId, @NonNull final CallbackListener<Call> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            Log.d(TAG, "Call Accepted : " + sessionId + " " + System.currentTimeMillis());
            if (CallManager.getInstance().deprecatedCallModuleExists() || CallManager.getInstance().callModuleExists()) {
                if (sessionId == null || TextUtils.isEmpty(sessionId)) {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SESSION_ID,
                                                            CometChatConstants.Errors.ERROR_INVALID_SESSION_ID_MESSAGE));
                } else {
                    if (getActiveCall() != null) {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_IN_PROGRESS,
                                                                CometChatConstants.Errors.ERROR_CALL_IN_PROGRESS_MESSAGE));
                        return;
                    }
                    ApiConnection
                        .getInstance()
                        .updateCallStatus(sessionId, CometChatConstants.CALL_STATUS_ONGOING, -1, new ApiConnection.APIConnectionListener() {
                            @Override
                            public void onResponse(String response, final CometChatException ce) {
                                try {
                                    if (ce != null) {
                                        CometChat.postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(ce);
                                            }
                                        });
                                        Log.d(TAG, "Call Accept Failure : " + sessionId + " " + System.currentTimeMillis() + " " + ce.getMessage());
                                    } else {
                                        try {
                                            JSONObject mainObject = new JSONObject(response);
                                            final Call receivedCall = Call.fromJson(mainObject
                                                                                        .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                        .toString());
                                            CallManager.setCallEventListener(callEventListener);
                                            CallManager.getInstance().joinCall(receivedCall);
                                            CometChat.postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onSuccess(receivedCall);
                                                }
                                            });
                                            Log.d(TAG, "Call Accepted Successfully: " + " " + sessionId + " " + System.currentTimeMillis());
                                        } catch (final JSONException e) {
                                            CometChat.postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                            e.getMessage()));
                                                }
                                            });
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                        e.getMessage());
                                    HashMap<String, String> detailsMap = new HashMap<>();
                                    detailsMap.put("sessionId", sessionId);
                                    handleException(methodName, e, detailsMap);
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(uncaughtException);
                                        }
                                    });
                                }
                            }
                        });
                }
            } else {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND,
                                                                CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("sessionId", sessionId);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    private static void rejectCall(@NonNull final String sessionId,
                                   @NonNull final CallbackListener<Call> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (sessionId == null || TextUtils.isEmpty(sessionId)) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SESSION_ID,
                                                    CometChatConstants.Errors.ERROR_INVALID_SESSION_ID_MESSAGE));
        } else {
            ApiConnection
                .getInstance()
                .updateCallStatus(sessionId, CometChatConstants.CALL_STATUS_REJECTED, -1, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject mainObject = new JSONObject(response);
                                    final Call receivedCall = Call.fromJson(mainObject
                                                                                .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                .toString());
                                    if (getActiveCall() != null && sessionId.equalsIgnoreCase(getActiveCall().getSessionId()))
                                        CallManager.getInstance().rejectCall();
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(receivedCall);
                                        }
                                    });
                                } catch (final JSONException e) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                        }
                                    });
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("sessionId", sessionId);
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
        }
    }

    private static void cancelCall(@NonNull final String sessionId,
                                   @NonNull final CallbackListener<Call> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (sessionId == null || TextUtils.isEmpty(sessionId)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SESSION_ID,
                                                        CometChatConstants.Errors.ERROR_INVALID_SESSION_ID_MESSAGE));
            } else {
                if (getActiveCall() != null) {
                    if (!((User) getActiveCall().getCallInitiator()).getUid().equalsIgnoreCase(getLoggedInUser().getUid())) {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INCORRECT_INITIATOR,
                                                                CometChatConstants.Errors.ERROR_INCORRECT_INITIATOR_MESSAGE));
                        return;
                    }
                    ApiConnection
                        .getInstance()
                        .updateCallStatus(sessionId, CometChatConstants.CALL_STATUS_CANCELLED, -1, new ApiConnection.APIConnectionListener() {
                            @Override
                            public void onResponse(String response, final CometChatException ce) {
                                try {
                                    if (ce != null) {
                                        CometChat.postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(ce);
                                            }
                                        });
                                    } else {
                                        try {
                                            JSONObject mainObject = new JSONObject(response);
                                            final Call receivedCall = Call.fromJson(mainObject
                                                                                        .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                        .toString());
                                            CallManager.getInstance().cancelCall();
                                            CometChat.postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onSuccess(receivedCall);
                                                }
                                            });
                                        } catch (final JSONException e) {
                                            CometChat.postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                            e.getMessage()));
                                                }
                                            });
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                        e.getMessage());
                                    HashMap<String, String> detailsMap = new HashMap<>();
                                    detailsMap.put("sessionId", sessionId);
                                    handleException(methodName, e, detailsMap);
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(uncaughtException);
                                        }
                                    });
                                }
                            }
                        });
                } else {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_NOT_INITIATED,
                                                                    CometChatConstants.Errors.ERROR_CALL_NOT_INITIATED_MESSAGE));
                        }
                    });

                }
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("sessionId", sessionId);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    private static void sendBusyResponse(@NonNull final String sessionId,
                                         @NonNull final CallbackListener<Call> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (sessionId == null || TextUtils.isEmpty(sessionId)) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SESSION_ID,
                                                    CometChatConstants.Errors.ERROR_INVALID_SESSION_ID_MESSAGE));
        } else {
            ApiConnection
                .getInstance()
                .updateCallStatus(sessionId, CometChatConstants.CALL_STATUS_BUSY, -1, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject mainObject = new JSONObject(response);
                                    final Call receivedCall = Call.fromJson(mainObject
                                                                                .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                .toString());
                                    CallManager.getInstance().sendBusyResponse();
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(receivedCall);
                                        }
                                    });
                                } catch (final JSONException e) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                        }
                                    });
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("sessionId", sessionId);
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
        }
    }

    private static void endCallInternal(@NonNull final String sessionId,
                                        final boolean isInternal, @NonNull final CallbackListener<Call> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (sessionId == null || TextUtils.isEmpty(sessionId)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SESSION_ID,
                                                        CometChatConstants.Errors.ERROR_INVALID_SESSION_ID_MESSAGE));
            } else {
                if (getActiveCall() != null) {
                    if (getActiveCall().getSessionId().equalsIgnoreCase(sessionId)) {
                        long joinedAt = 0;
                        if (CallManager.getInstance().getInitialCall() != null)
                            joinedAt = CallManager.getInstance().getInitialCall().getJoinedAt();
                        ApiConnection
                            .getInstance()
                            .updateCallStatus(sessionId, CometChatConstants.CALL_STATUS_ENDED, joinedAt, new ApiConnection.APIConnectionListener() {
                                @Override
                                public void onResponse(String response, final CometChatException ce) {
                                    try {
                                        if (ce != null) {
                                            CallManager.getInstance().endCall(isInternal);
                                            CometChat.postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onError(ce);
                                                }
                                            });
                                        } else {
                                            try {
                                                JSONObject mainObject = new JSONObject(response);
                                                final Call receivedCall = Call.fromJson(mainObject
                                                                                            .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                            .toString());
                                                CallManager.getInstance().endCall(isInternal);
                                                CometChat.postOnMainThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.onSuccess(receivedCall);
                                                    }
                                                });
                                            } catch (final JSONException e) {
                                                CometChat.postOnMainThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                e.getMessage()));
                                                    }
                                                });
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {
                                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                            e.getMessage());
                                        HashMap<String, String> detailsMap = new HashMap<>();
                                        detailsMap.put("sessionId", sessionId);
                                        handleException(methodName, e, detailsMap);
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(uncaughtException);
                                            }
                                        });
                                    }
                                }
                            });
                    } else {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_SESSION_MISMATCH,
                                                                        CometChatConstants.Errors.ERROR_CALL_SESSION_MISMATCH_MESSAGE));
                            }
                        });

                    }
                } else {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_NOT_INITIATED,
                                                                    CometChatConstants.Errors.ERROR_CALL_NOT_INITIATED_MESSAGE));
                        }
                    });
                }
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("sessionId", sessionId);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }


    /**
     * {@inheritDoc}
     * A method to end call between between two Users or a particular Group
     *
     * @param sessionId The Unique sessionId of the call be to ended
     * @param listener  An object of the  <code>CallbackListener&lt;Call&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @see Call
     * @see CallbackListener
     * @see {@link= "https://www.cometchat.com/docs/android-chat-sdk/calling-default-calling#end-call"}
     * @since <b>v1</b>
     */
    public static void endCall(@NonNull String sessionId, @NonNull final CallbackListener<Call> listener) {
        if (CallManager.getInstance().deprecatedCallModuleExists() || CallManager.getInstance().callModuleExists()) {
            if (getActiveCall() != null) {
                if (CallManager.getInstance().getCallSettings() != null) {
                    if (CallManager.getInstance().getCallSettings().getCallMode() != null) {
                        if (CallManager.getInstance().getCallSettings().getCallMode().equalsIgnoreCase(CallSettings.CALL_MODE_DEFAULT)) {
                            endCallInternal(sessionId, false, listener);
                        } else if (CallManager.getInstance().getCallSettings().getCallMode().equalsIgnoreCase(CallSettings.CALL_MODE_DIRECT)) {
                            CallManager.getInstance().endCall(false);
                            listener.onSuccess(getActiveCall());
                        }
                    } else {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_MODE_NOT_FOUND,
                                                                        CometChatConstants.Errors.ERROR_CALL_MODE_NOT_FOUND_MESSAGE));
                            }
                        });
                    }
                } else {
                    endCallInternal(sessionId, false, listener);
                }
            } else {
                CallManager.getInstance().endCall(false);
                listener.onSuccess(getActiveCall());
            }
        } else {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND,
                                                            CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE));
                }
            });
        }

    }

    private static void sendUnansweredResponse(@NonNull final String sessionId,
                                               @NonNull final CallbackListener<Call> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (sessionId == null || TextUtils.isEmpty(sessionId)) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SESSION_ID,
                                                    CometChatConstants.Errors.ERROR_INVALID_SESSION_ID_MESSAGE));
        } else {
            ApiConnection
                .getInstance()
                .updateCallStatus(sessionId, CometChatConstants.CALL_STATUS_UNANSWERED, -1, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject mainObject = new JSONObject(response);
                                    final Call receivedCall = Call.fromJson(mainObject
                                                                                .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                .toString());
                                    CallManager.getInstance().unanswerCall();
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(receivedCall);
                                        }
                                    });
                                } catch (final JSONException e) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                        }
                                    });
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                e.getMessage());
                            HashMap<String, String> detailsMap = new HashMap<>();
                            detailsMap.put("sessionId", sessionId);
                            handleException(methodName, e, detailsMap);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(uncaughtException);
                                }
                            });
                        }
                    }
                });
        }
    }


    /**
     * {@inheritDoc}
     * A method to start call between between two Users or a particular Group
     *
     * @param activity       Activity reference where you want to show the call view
     * @param sessionId      The Unique sessionId of the call be to rejected
     * @param relativeLayout An object of the relativeLayout class in which CometChat can load the calling views
     * @param listener       An object of the  <code>OngoingCallListener<code/> class that helps inform the developer about the ongoing call events like <code>onYouJoined()<code/>,
     *                       <code>onYouLeft()<code/>,<code>onUserJoined()<code/>,<code>onUserDisconnected()<code/>,<code>onCallEnded()<code/> and <code>onError()<code/>
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/calling-default-calling#start-a-call"}
     * @see CallManager.CallListener
     * @since <b>v1</b>
     */
    public static void startCall(@NonNull Activity activity,
                                 @NonNull String sessionId,
                                 @NonNull RelativeLayout relativeLayout,
                                 @NonNull final OngoingCallListener listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (sessionId == null || TextUtils.isEmpty(sessionId)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SESSION_ID,
                                                        CometChatConstants.Errors.ERROR_INVALID_SESSION_ID_MESSAGE));
                return;
            }
            if (activity == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_ACTIVITY_NULL,
                                                        CometChatConstants.Errors.ERROR_ACTIVITY_NULL_MESSAGE));
                return;
            }
            if (relativeLayout == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_VIEW_NULL,
                                                        CometChatConstants.Errors.ERROR_VIEW_NULL_MESSAGE));
                return;
            }
            if (getActiveCall() != null && getActiveCall().getSessionId().equalsIgnoreCase(sessionId)) {
                if (CallManager.getInstance().isConferenceJoined()) {
                    CallManager.getInstance().disposeView();
                }
                ongoingCallListener = listener;
                final CallSettings callSettings = new CallSettings.CallSettingsBuilder(activity, relativeLayout)
                    .setSessionId(sessionId)
                    .enableDefaultLayout(true)
                    .build();

                CallManager.getInstance().startCall(callSettings, appSettings, listener, new CallManager.CallListener() {
                    @Override
                    public void onYouJoined() {

                    }

                    @Override
                    public void onYouLeft() {

                    }

                    @Override
                    public void onUserJoined(final User user) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onUserJoined(user);
                            }
                        });
                    }

                    @Override
                    public void onUserDisconnected(final User user) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onUserLeft(user);
                            }
                        });
                    }

                    @Override
                    public void onCallEnded(CallSettings callSettings, Call call) {
                        if (callSettings.getCallMode() != null) {
                            if (callSettings.getCallMode().equalsIgnoreCase(CallSettings.CALL_MODE_DEFAULT)) {
                                endCallInternal(callSettings.getSessionId(), true, new CallbackListener<Call>() {

                                    @Override
                                    public void onSuccess(final Call call) {
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onCallEnded(call);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(CometChatException e) {
                                        Logger.error(TAG, "End Call Exception : " + e.getMessage());
                                    }
                                });
                            } else if (callSettings.getCallMode().equalsIgnoreCase(CallSettings.CALL_MODE_DIRECT)) {
                                CallManager.getInstance().endCall(true);
                                listener.onCallEnded(call);
                            }
                        }
                    }

                    @Override
                    public void onUserListUpdated(final List<User> users) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onUserListUpdated(users);
                            }
                        });
                    }

                    @Override
                    public void onAudioModesUpdated(final List<AudioMode> audioModes) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onAudioModesUpdated(audioModes);
                            }
                        });
                    }

                    @Override
                    public void onError(final CometChatException ce) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    }

                    @Override
                    public void onUserMuted(final User userMuted, final User mutedBy) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onUserMuted(userMuted, mutedBy);
                            }
                        });
                    }

                    @Override
                    public void onRecordingStarted(final User user) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onRecordingStarted(user);
                            }
                        });
                    }

                    @Override
                    public void onRecordingStopped(final User user) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onRecordingStopped(user);
                            }
                        });
                    }

                    @Override
                    public void onCallSwitchedToVideo(final String sessionId, final User callSwitchInitiatedBy, final User callSwitchAcceptedBy) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCallSwitchedToVideo(sessionId, callSwitchInitiatedBy, callSwitchAcceptedBy);
                            }
                        });
                    }
                });
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_NOT_INITIATED,
                                                                CometChatConstants.Errors.ERROR_CALL_NOT_INITIATED_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("sessionId", sessionId);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * A method to start call between between two Users or a particular Group
     *
     * @param settings An object of the <code>CallSettings</code> class, that provides the settings for the call to be started
     * @param listener An object of the  <code>OngoingCallListener<code/> class that helps inform the developer about the ongoing call events like <code>onYouJoined()<code/>,
     *                 <code>onYouLeft()<code/>,<code>onUserJoined()<code/>,<code>onUserDisconnected()<code/>,<code>onCallEnded()<code/> and <code>onError()<code/>
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/calling-default-calling#start-a-call"}
     * @see CallManager.CallListener
     * @since <b>v1</b>
     */
    public static void startCall(@NonNull final CallSettings settings, @NonNull final OngoingCallListener listener) {
        if (getLoggedInUser() == null) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                            CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                    return;
                }
            });
        }
        if (TextUtils.isEmpty(settings.getSessionId())) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SESSION_ID,
                                                    CometChatConstants.Errors.ERROR_INVALID_SESSION_ID_MESSAGE));
            return;
        }
        if (settings.getActivity() == null) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_ACTIVITY_NULL,
                                                    CometChatConstants.Errors.ERROR_ACTIVITY_NULL_MESSAGE));
            return;
        }
        if (settings.getVideoContainer() == null) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_VIEW_NULL, CometChatConstants.Errors.ERROR_VIEW_NULL_MESSAGE));
            return;
        }
        if (CallManager.getInstance().isConferenceJoined()) {
            CallManager.getInstance().disposeView();
        }
        ongoingCallListener = listener;
        CallManager.getInstance().startCall(settings, appSettings, listener, new CallManager.CallListener() {
            @Override
            public void onYouJoined() {

            }

            @Override
            public void onYouLeft() {

            }

            @Override
            public void onUserJoined(final User user) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onUserJoined(user);
                    }
                });
            }

            @Override
            public void onUserDisconnected(final User user) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onUserLeft(user);
                    }
                });
            }

            @Override
            public void onCallEnded(CallSettings callSettings, Call call) {
                if (callSettings.getCallMode() != null) {
                    if (callSettings.getCallMode().equalsIgnoreCase(CallSettings.CALL_MODE_DEFAULT)) {
                        endCallInternal(callSettings.getSessionId(), true, new CallbackListener<Call>() {

                            @Override
                            public void onSuccess(final Call call) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onCallEnded(call);
                                    }
                                });
                            }

                            @Override
                            public void onError(CometChatException e) {
                                Logger.error(TAG, "End Call Exception : " + e.getMessage());
                            }
                        });
                    } else if (callSettings.getCallMode().equalsIgnoreCase(CallSettings.CALL_MODE_DIRECT)) {
                        CallManager.getInstance().endCall(true);
                        listener.onCallEnded(call);
                    }
                }
            }

            @Override
            public void onUserListUpdated(final List<User> users) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onUserListUpdated(users);
                    }
                });
            }

            @Override
            public void onAudioModesUpdated(final List<AudioMode> audioModes) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAudioModesUpdated(audioModes);
                    }
                });
            }

            @Override
            public void onError(final CometChatException ce) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(ce);
                    }
                });
            }

            @Override
            public void onUserMuted(final User userMuted, final User mutedBy) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onUserMuted(userMuted, mutedBy);
                    }
                });
            }

            @Override
            public void onRecordingStarted(final User user) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onRecordingStarted(user);
                    }
                });
            }

            @Override
            public void onRecordingStopped(final User user) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onRecordingStopped(user);
                    }
                });
            }

            @Override
            public void onCallSwitchedToVideo(final String sessionId, final User callSwitchInitiatedBy, final User callSwitchAcceptedBy) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCallSwitchedToVideo(sessionId, callSwitchInitiatedBy, callSwitchAcceptedBy);
                    }
                });
            }
        });
    }

    /**
     * {@inheritDoc}
     * Returns Call object of Active call
     *
     * @return Call Object of the currently Active Call
     * @version <b>v2</b>
     * @see Call
     * @since <b>v2</b>
     */
    public static Call getActiveCall() {
        return CallManager.getInstance().getActiveCall();
    }

    /**
     * {@inheritDoc}
     * Return last Delivered Message Id
     *
     * @return id of message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public static long getLastDeliveredMessageId() {
        return PreferenceHelper.getLastDeliveredMessageId();
    }

    public static void addExtension(CometChatExtension cometChatExtension) {
        ExtensionManager.addCometChatExtension(context, cometChatExtension);
    }

    public static boolean isExtensionEnabled(String extensionId) {
        Settings settings = SettingsRepo.getSettings();
        return CometChatUtils.isExtensionEnabled(settings, extensionId);
    }

    private static void getSettings(@NonNull String authToken, @NonNull final CallbackListener<Settings> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection.getInstance().getSettings(authToken, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            JSONObject mainObject = new JSONObject(response);
                            final Settings settings = Settings.fromJson(mainObject
                                                                            .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                            .toString());
                            rttConnection = null;
                            SettingsRepo.insertSettings(settings);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(settings);
                                }
                            });
                        } catch (final JSONException e) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     * Method to block users
     *
     * @param uids     List of UID to be blocked
     * @param listener An object of the  <code>CallbackListener&lt;HashMap&lt;String,String&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/users-block-users#block-users"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void blockUsers(@NonNull final List<String> uids, final CallbackListener<HashMap<String, String>> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (uids == null || uids.size() == 0) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_BLANK_UID, CometChatConstants.Errors.ERROR_BLANK_UID_MESSAGE));
        } else {
            ApiConnection.getInstance().blockUsers(uids, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                final HashMap<String, String> resultMap = new HashMap<>();
                                if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                    JSONObject dataObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    for (String uid : uids) {
                                        if (dataObject.has(uid)) {
                                            boolean success = dataObject.getJSONObject(uid).getBoolean(CometChatConstants.ResponseKeys.SUCCESS);
                                            resultMap.put(uid, success ? "success" : "fail");
                                        }
                                    }
                                }
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(resultMap);
                                    }
                                });
                            } catch (final JSONException e) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                    }
                                });
                            }

                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("uids", uids.toString());
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * Method to unblock users
     *
     * @param uids     List of UID to be blocked
     * @param listener An object of the  <code>CallbackListener&lt;HashMap&lt;String,String&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/users-block-users#unblock-users"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void unblockUsers(@NonNull final List<String> uids, final CallbackListener<HashMap<String, String>> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (uids == null || uids.size() == 0) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_BLANK_UID, CometChatConstants.Errors.ERROR_BLANK_UID_MESSAGE));
        } else {
            ApiConnection.getInstance().unblockUsers(uids, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                final HashMap<String, String> resultMap = new HashMap<>();
                                if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                    JSONObject dataObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    for (String uid : uids) {
                                        if (dataObject.has(uid)) {
                                            boolean success = dataObject.getJSONObject(uid).getBoolean(CometChatConstants.ResponseKeys.SUCCESS);
                                            resultMap.put(uid, success ? "success" : "fail");
                                        }
                                    }
                                }
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(resultMap);
                                    }
                                });
                            } catch (final JSONException e) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                    }
                                });

                            }

                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("uids", uids.toString());
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * This method get information about the delivery and read receipts of a particular message
     *
     * @param messageId Unique id of the message
     * @param listener  An object of the  <code>CallbackListener&lt;List&lt;MessageReceipt&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receipts#receipt-history-for-a-single-message"}
     * @see CallbackListener
     * @see MessageReceipt
     * @since <b>v1</b>
     */
    public static void getMessageReceipts(final long messageId, final CallbackListener<List<MessageReceipt>> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (messageId > 0) {
            ApiConnection.getInstance().getMessageReceipts(messageId, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                    JSONObject dataObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    String receiverType = null;
                                    String receiverId = null;
                                    long messageId = 0;
                                    if (dataObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                                        receiverId = dataObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID);
                                    if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE))
                                        receiverType = dataObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE);
                                    if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                                        messageId = dataObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID);
                                    if (dataObject.has(CometChatConstants.ResponseKeys.KEY_RECEIPTS)) {
                                        JSONObject receiptsObject = dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_RECEIPTS);
                                        final List<MessageReceipt> receiptsList = MessageReceipt.receiptsFromJSON(receiptsObject.getJSONArray(
                                            CometChatConstants.ResponseKeys.KEY_DATA), receiverId, receiverType, messageId);
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onSuccess(receiptsList);
                                            }
                                        });
                                    } else {
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onSuccess(new ArrayList<MessageReceipt>());
                                            }
                                        });
                                    }
                                }
                            } catch (final JSONException e) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("messageId", String.valueOf(messageId));
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        } else {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID,
                                                            CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * This method get information of a single message based on the ID provided
     *
     * @param messageId Unique id of the message
     * @param listener  An object of the  <code>CallbackListener&lt;List&lt;MessageReceipt&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v3</b>
     * @see CallbackListener
     * @since <b>v3</b>
     */
    public static void getMessageDetails(final long messageId, @NonNull final CallbackListener<BaseMessage> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (messageId > 0) {
            ApiConnection.getInstance().getMessageReceipts(messageId, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                    JSONObject dataObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    final BaseMessage receivedMessage = CometChatHelper.processMessage(dataObject);
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(receivedMessage);
                                        }
                                    });
                                }

                            } catch (final JSONException e) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("messageId", String.valueOf(messageId));
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        } else {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID,
                                                            CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
                }
            });
        }
    }


    /**
     * {@inheritDoc}
     * Gets unread count of messages for a particular User
     *
     * @param UID      Unique identifier of a User
     * @param listener An object of the  <code>CallbackListener&lt;HashMap&lt;String,Integer&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#unread-messages-count"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCountForUser(@NonNull String UID, final CallbackListener<HashMap<String, Integer>> listener) {
        if (UID == null || TextUtils.isEmpty(UID.trim())) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                    CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
        } else {
            getUnreadMessageCountForUser(UID.trim(), false, listener);
        }
    }

    /**
     * {@inheritDoc}
     * Gets unread count of messages for a particular User
     *
     * @param UID                          Unique identifier of a User
     * @param hideMessagesFromBlockedUsers boolean parameter to show or hide messages count of the blocked user
     * @param listener                     An object of the  <code>CallbackListener&lt;HashMap&lt;String,Integer&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#unread-messages-count"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCountForUser(
        @NonNull final String UID,
        final boolean hideMessagesFromBlockedUsers,
        final CallbackListener<HashMap<String, Integer>> listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (UID == null || TextUtils.isEmpty(UID)) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_BLANK_UID,
                                                            CometChatConstants.Errors.ERROR_BLANK_UID_MESSAGE));
                }
            });
        } else {
            ApiConnection.getInstance().getUnreadMessageCountForUser(UID, hideMessagesFromBlockedUsers, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                final HashMap<String, Integer> countMap = new HashMap<>();
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                    JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject jsonObject = dataArray.getJSONObject(i);
                                        if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_ENTITY_ID) && jsonObject.has(CometChatConstants.ResponseKeys.KEY_COUNT)) {
                                            countMap.put(jsonObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_ID),
                                                         jsonObject.getInt(CometChatConstants.ResponseKeys.KEY_COUNT));
                                        }
                                    }
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(countMap);
                                        }
                                    });
                                }
                            } catch (final JSONException je) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("UID", UID);
                        detailsMap.put("hideMessagesFromBlockedUsers", String.valueOf(hideMessagesFromBlockedUsers));
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * Gets unread count of messages for a particular Group
     *
     * @param GUID     Unique identifier of a Group
     * @param listener An object of the  <code>CallbackListener&lt;HashMap&lt;String,Integer&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCountForGroup(@NonNull String GUID, final CallbackListener<HashMap<String, Integer>> listener) {
        getUnreadMessageCountForGroup(GUID, false, listener);
    }

    /**
     * {@inheritDoc}
     * Gets unread count of messages for a particular Group
     *
     * @param GUID                         Unique identifier of a Group
     * @param hideMessagesFromBlockedUsers boolean parameter to show or hide messages count of the blocked user
     * @param listener                     An object of the  <code>CallbackListener&lt;HashMap&lt;String,Integer&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#unread-messages-count"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCountForGroup(
        @NonNull final String GUID,
        final boolean hideMessagesFromBlockedUsers,
        final CallbackListener<HashMap<String, Integer>> listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (GUID == null || TextUtils.isEmpty(GUID)) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                            CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                }
            });
        } else {
            ApiConnection.getInstance().getUnreadMessageCountForGroup(GUID, hideMessagesFromBlockedUsers, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                final HashMap<String, Integer> countMap = new HashMap<>();
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                    JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject jsonObject = dataArray.getJSONObject(i);
                                        if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_ENTITY_ID) && jsonObject.has(CometChatConstants.ResponseKeys.KEY_COUNT)) {
                                            countMap.put(jsonObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_ID),
                                                         jsonObject.getInt(CometChatConstants.ResponseKeys.KEY_COUNT));
                                        }
                                    }
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(countMap);
                                        }
                                    });
                                }
                            } catch (final JSONException je) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("GUID", GUID);
                        detailsMap.put("hideMessagesFromBlockedUsers", String.valueOf(hideMessagesFromBlockedUsers));
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });

                    }
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * Gets total unread count of messages for all users and all groups
     *
     * @param listener An object of the  <code>CallbackListener&lt;HashMap&lt;String,HashMap&lt;String,Integer&gt;&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#unread-messages-count"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCount(final CallbackListener<HashMap<String, HashMap<String, Integer>>> listener) {
        getUnreadMessageCount(false, listener);
    }

    /**
     * {@inheritDoc}
     * Gets unread count of messages for all users and all groups
     *
     * @param hideMessagesFromBlockedUsers boolean parameter to show or hide messages count of the blocked user
     * @param listener                     An object of the  <code>CallbackListener&lt;HashMap&lt;String,HashMap&lt;String,Integer&gt;&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#unread-messages-count"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCount(boolean hideMessagesFromBlockedUsers,
                                             final CallbackListener<HashMap<String, HashMap<String, Integer>>> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection.getInstance().getUnreadMessageCount(hideMessagesFromBlockedUsers, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            final HashMap<String, Integer> userCountMap = new HashMap<>();
                            final HashMap<String, Integer> groupCountMap = new HashMap<>();
                            JSONObject mainObject = new JSONObject(response);
                            if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject jsonObject = dataArray.getJSONObject(i);
                                    if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_ENTITY_ID) && jsonObject.has(CometChatConstants.ResponseKeys.KEY_COUNT) && jsonObject.has(
                                        CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE)) {
                                        String entityType = jsonObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE);
                                        if (entityType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
                                            userCountMap.put(jsonObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_ID),
                                                             jsonObject.getInt(CometChatConstants.ResponseKeys.KEY_COUNT));
                                        else if (entityType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP))
                                            groupCountMap.put(jsonObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_ID),
                                                              jsonObject.getInt(CometChatConstants.ResponseKeys.KEY_COUNT));
                                    }
                                }
                                final HashMap<String, HashMap<String, Integer>> finalMap = new HashMap<>();
                                finalMap.put(CometChatConstants.RECEIVER_TYPE_USER, userCountMap);
                                finalMap.put(CometChatConstants.RECEIVER_TYPE_GROUP, groupCountMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(finalMap);
                                    }
                                });
                            }
                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });

                }
            }
        });
    }


    /**
     * {@inheritDoc}
     * Gets total unread count of messages for all Users
     *
     * @param listener An object of the  <code>CallbackListener&lt;HashMap&lt;String,Integer&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#unread-messages-count"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCountForAllUsers(final CallbackListener<HashMap<String, Integer>> listener) {
        getUnreadMessageCountForAllUsers(false, listener);
    }

    /**
     * {@inheritDoc}
     * Gets total unread count of messages for all Users
     *
     * @param hideMessagesFromBlockedUsers boolean parameter to show or hide messages count of the blocked user
     * @param listener                     An object of the  <code>CallbackListener&lt;HashMap&lt;String,Integer&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#unread-messages-count"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCountForAllUsers(
        final boolean hideMessagesFromBlockedUsers,
        final CallbackListener<HashMap<String, Integer>> listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection.getInstance().getUnreadMessageCountForAllUsers(hideMessagesFromBlockedUsers, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            final HashMap<String, Integer> countMap = new HashMap<>();
                            JSONObject mainObject = new JSONObject(response);
                            if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject jsonObject = dataArray.getJSONObject(i);
                                    if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_ENTITY_ID) && jsonObject.has(CometChatConstants.ResponseKeys.KEY_COUNT)) {
                                        countMap.put(jsonObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_ID),
                                                     jsonObject.getInt(CometChatConstants.ResponseKeys.KEY_COUNT));
                                    }
                                }
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(countMap);
                                    }
                                });
                            }
                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    detailsMap.put("hideMessagesFromBlockedUsers", String.valueOf(hideMessagesFromBlockedUsers));
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });
                }
            }
        });
    }


    /**
     * {@inheritDoc}
     * Gets total unread count of messages for all Groups
     *
     * @param listener An object of the  <code>CallbackListener&lt;HashMap&lt;String,Integer&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#unread-messages-count"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCountForAllGroups(final CallbackListener<HashMap<String, Integer>> listener) {
        getUnreadMessageCountForAllGroups(false, listener);
    }

    /**
     * {@inheritDoc}
     * Gets total unread count of messages for all Groups
     *
     * @param hideMessagesFromBlockedUsers boolean parameter to show or hide messages count of the blocked user
     * @param listener                     An object of the  <code>CallbackListener&lt;HashMap&lt;String,Integer&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#unread-messages-count"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void getUnreadMessageCountForAllGroups(
        final boolean hideMessagesFromBlockedUsers,
        final CallbackListener<HashMap<String, Integer>> listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection.getInstance().getUnreadMessageCountForAllGroups(hideMessagesFromBlockedUsers, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            final HashMap<String, Integer> countMap = new HashMap<>();
                            JSONObject mainObject = new JSONObject(response);
                            if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                JSONArray dataArray = mainObject.getJSONArray(CometChatConstants.ResponseKeys.KEY_DATA);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject jsonObject = dataArray.getJSONObject(i);
                                    if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_ENTITY_ID) && jsonObject.has(CometChatConstants.ResponseKeys.KEY_COUNT)) {
                                        countMap.put(jsonObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_ID),
                                                     jsonObject.getInt(CometChatConstants.ResponseKeys.KEY_COUNT));
                                    }
                                }
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(countMap);
                                    }
                                });
                            }
                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    detailsMap.put("hideMessagesFromBlockedUsers", String.valueOf(hideMessagesFromBlockedUsers));
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });
                }
            }
        });
    }


    /**
     * {@inheritDoc}
     * A method to edit/update a message
     *
     * @param message  An object of the <code>BaseMessage<code/> class which requires receiverUid,type and receiverType
     *                 <code>BaseMessage(String receiverUid, String type, @CometChatConstants.ReceiverTypes String receiverType)<code/>
     * @param listener An object of the  <code>CallbackListener&lt;BaseMessage&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-edit-message#edit-a-message"}
     *
     * <b>Note</b>
     * Only Sender of the message can edit the message.This method only supports <code>TextMessage<code/> and <code>CustomMessage<code/>
     * @see CometChatConstants.ReceiverTypes
     * @see CallbackListener
     */
    public static void editMessage(@NonNull final BaseMessage message, final CallbackListener<BaseMessage> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (message != null && message.getId() > 0) {
            ApiConnection.getInstance().editMessage(message, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                    JSONObject actionObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    if (actionObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                        JSONObject dataObject = actionObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                        if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                                            JSONObject entitiesObject = dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES);
                                            if (entitiesObject.has(CometChatConstants.ActionKeys.KEY_ON)) {
                                                JSONObject onObject = entitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ON);
                                                if (onObject.has(CometChatConstants.ActionKeys.KEY_ENTITY)) {
                                                    JSONObject entityObject = onObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY);
                                                    final BaseMessage editedMessage = MessageHelper.processMessage(entityObject);
                                                    postOnMainThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            listener.onSuccess(editedMessage);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (final JSONException je) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("message", message.toString());
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        } else {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID,
                                                            CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * A method to delete a message
     *
     * @param messageId Id of the message to be deleted
     * @param listener  An object of the  <code>CallbackListener&lt;BaseMessage&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-delete-message#delete-a-message"}
     * @see CometChatConstants.ReceiverTypes
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void deleteMessage(final long messageId, final CallbackListener<BaseMessage> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (messageId > 0) {
            ApiConnection.getInstance().deleteMessage(messageId, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                    JSONObject actionObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    if (actionObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                                        JSONObject dataObject = actionObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                        if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                                            JSONObject entitiesObject = dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES);
                                            if (entitiesObject.has(CometChatConstants.ActionKeys.KEY_ON)) {
                                                JSONObject onObject = entitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ON);
                                                if (onObject.has(CometChatConstants.ActionKeys.KEY_ENTITY)) {
                                                    JSONObject entityObject = onObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY);
                                                    final BaseMessage deletedMessage = MessageHelper.processMessage(entityObject);
                                                    postOnMainThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            listener.onSuccess(deletedMessage);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (final JSONException je) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("messageId", String.valueOf(messageId));
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        } else {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGEID,
                                                            CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To add members to a Group developer can call this method
     *
     * @param GUID          Unique Identifier of the Group
     * @param members       List of Group members to added into the group
     * @param bannedUserIds List of members to be banned into the group
     * @param listener      An object of the  <code>CallbackListener&lt;HashMap&lt;String,String&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-add-members-to-group#add-members-to-group"}
     * @see CallbackListener
     * @since <b>v1</b>
     */
    public static void addMembersToGroup(
        @NonNull String GUID,
        @NonNull List<GroupMember> members,
        List<String> bannedUserIds,
        @NonNull final CallbackListener<HashMap<String, String>> listener
    ) {
        if (GUID == null || TextUtils.isEmpty(GUID)) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID,
                                                            CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                }
            });
        } else {
            if (members != null || members.size() > 0) {
                addMembersToGroupInternal(GUID, members, bannedUserIds, listener);
            } else {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_LIST_EMPTY,
                                                                CometChatConstants.Errors.ERROR_LIST_EMPTY_MESSAGE));
                    }
                });
            }
        }
    }

    private static void addMembersToGroupInternal(final String GUID,
                                                  final List<GroupMember> members, final List<String> bannedMemberIds,
                                                  final CallbackListener<HashMap<String, String>> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        ApiConnection.getInstance().addMembersToGroup(GUID, members, bannedMemberIds, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                            final JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            final HashMap<String, String> successMap = getMembersMap(dataObject);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(successMap);
                                }
                            });
                        } else {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                            CometChatConstants.Errors.ERROR_JSON_MESSAGE));
                                }
                            });
                        }
                    }
                } catch (final JSONException e) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                        }
                    });
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    detailsMap.put("GUID", GUID);
                    detailsMap.put("members", members.toString());
                    detailsMap.put("bannedMembersIds", bannedMemberIds.toString());
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });

                }
            }
        });
    }

    private static HashMap<String, String> getMembersMap(JSONObject dataObject) {
        HashMap<String, String> successMap = new HashMap<>();
        try {
            if (dataObject.has(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_ADMINS)) {
                JSONObject adminsObject = dataObject.getJSONObject(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_ADMINS);
                Iterator<String> keys = adminsObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject keyObject = adminsObject.getJSONObject(key);
                    if (keyObject.has(CometChatConstants.ResponseKeys.SUCCESS)) {
                        if (keyObject.getBoolean(CometChatConstants.ResponseKeys.SUCCESS)) {
                            successMap.put(key, CometChatConstants.ResponseKeys.SUCCESS);
                        } else {
                            if (keyObject.has(CometChatConstants.ResponseKeys.KEY_ERROR)) {
                                JSONObject errorObject = keyObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ERROR);
                                successMap.put(key, errorObject.getString(CometChatConstants.ResponseKeys.KEY_ERROR_MESSAGE));
                            }
                        }
                    }

                }
            }
            if (dataObject.has(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_MODERATORS)) {
                JSONObject moderatorsObject = dataObject.getJSONObject(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_MODERATORS);
                Iterator<String> keys = moderatorsObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject keyObject = moderatorsObject.getJSONObject(key);
                    if (keyObject.has(CometChatConstants.ResponseKeys.SUCCESS)) {
                        if (keyObject.getBoolean(CometChatConstants.ResponseKeys.SUCCESS)) {
                            successMap.put(key, CometChatConstants.ResponseKeys.SUCCESS);
                        } else {
                            if (keyObject.has(CometChatConstants.ResponseKeys.KEY_ERROR)) {
                                JSONObject errorObject = keyObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ERROR);
                                successMap.put(key, errorObject.getString(CometChatConstants.ResponseKeys.KEY_ERROR_MESSAGE));
                            }
                        }
                    }

                }
            }
            if (dataObject.has(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_PARTICIPANTS)) {
                JSONObject participantsObject = dataObject.getJSONObject(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_PARTICIPANTS);
                Iterator<String> keys = participantsObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject keyObject = participantsObject.getJSONObject(key);
                    if (keyObject.has(CometChatConstants.ResponseKeys.SUCCESS)) {
                        if (keyObject.getBoolean(CometChatConstants.ResponseKeys.SUCCESS)) {
                            successMap.put(key, CometChatConstants.ResponseKeys.SUCCESS);
                        } else {
                            if (keyObject.has(CometChatConstants.ResponseKeys.KEY_ERROR)) {
                                JSONObject errorObject = keyObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ERROR);
                                successMap.put(key, errorObject.getString(CometChatConstants.ResponseKeys.KEY_ERROR_MESSAGE));
                            }
                        }
                    }

                }
            }
            if (dataObject.has(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_BANNED)) {
                JSONObject bannedObjects = dataObject.getJSONObject(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_BANNED);
                Iterator<String> keys = bannedObjects.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject keyObject = bannedObjects.getJSONObject(key);
                    if (keyObject.has(CometChatConstants.ResponseKeys.SUCCESS)) {
                        if (keyObject.getBoolean(CometChatConstants.ResponseKeys.SUCCESS)) {
                            successMap.put(key, CometChatConstants.ResponseKeys.SUCCESS);
                        } else {
                            if (keyObject.has(CometChatConstants.ResponseKeys.KEY_ERROR)) {
                                JSONObject errorObject = keyObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ERROR);
                                successMap.put(key, errorObject.getString(CometChatConstants.ResponseKeys.KEY_ERROR_MESSAGE));
                            }
                        }
                    }

                }
            }
        } catch (Exception er) {
            er.printStackTrace();
        }
        return successMap;
    }

    /**
     * {@inheritDoc}
     * To register for the push notifications for the logged in user
     *
     * @param fcmToken fcm Token required for receiving push notifications
     * @param listener An object of the  <code>CallbackListener&lt;HashMap&lt;String,String&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @see CallbackListener
     * @since <b>2.0.2</b>
     */
    public static void registerTokenForPushNotification(
        @NonNull String fcmToken,
        final @NonNull CallbackListener<String> listener
    ) {
        registerTokenForPushNotification(fcmToken, null, listener);
    }

    /**
     * {@inheritDoc}
     * To register for the push notifications for the logged in user along with custom params.
     *
     * @param fcmToken fcm Token required for receiving push notifications
     * @param params   A JSONObject which can be used to pass extra parameters.
     * @param listener An object of the  <code>CallbackListener&lt;HashMap&lt;String,String&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @see CallbackListener
     * @since <b>2.0.2</b>
     */
    public static void registerTokenForPushNotification(
        @NonNull final String fcmToken,
        final JSONObject params,
        final @NonNull CallbackListener<String> listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (fcmToken != null && !TextUtils.isEmpty(fcmToken)) {
            ApiConnection.getInstance().registerTokenForPushNotification(fcmToken, params, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_REGISTRATION_SUCCESS);
                                }
                            });
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("fcmToken", fcmToken);
                        detailsMap.put("params", params.toString());
                        handleException(methodName, e, detailsMap);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        } else {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_FCM_TOKEN,
                                                            CometChatConstants.Errors.ERROR_INVALID_FCM_TOKEN_MESSAGE));
                }
            });
        }
    }

    // User Management methods

    /**
     * {@inheritDoc}
     * To create users on the fly.
     *
     * @param user     object of the {@link User} class containing the data for the User.
     * @param apiKey   Auth API Key.
     * @param listener An object of the  <code>CallbackListener&lt;{@link User}&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/users-user-management#creating-a-user"}
     * @see CallbackListener
     * @since <b>2.0.3</b>
     */
    public static void createUser(
        @NonNull final User user,
        @NonNull String apiKey,
        final CallbackListener<User> listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (apiKey != null && !TextUtils.isEmpty(apiKey)) {
                if (user != null) {
                    if (user.getUid() != null && !TextUtils.isEmpty(user.getUid())) {
                        if (user.getName() != null && !TextUtils.isEmpty(user.getName())) {
                            ApiConnection.getInstance().createUser(user, apiKey, new ApiConnection.APIConnectionListener() {
                                @Override
                                public void onResponse(String response, final CometChatException ce) {
                                    try {
                                        if (ce != null) {
                                            postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onError(ce);
                                                }
                                            });
                                        } else {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                final User createdUser = User.fromJson(jsonObject
                                                                                           .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                           .toString());
                                                postOnMainThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.onSuccess(createdUser);
                                                    }
                                                });
                                            } catch (final JSONException je) {
                                                postOnMainThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                je.getMessage()));
                                                    }
                                                });
                                            }
                                        }
                                    } catch (Exception e) {
                                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                            e.getMessage());
                                        HashMap<String, String> detailsMap = new HashMap<>();
                                        detailsMap.put("user", user.toString());
                                        handleException(methodName, e, detailsMap);
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(uncaughtException);
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            // invalid name error
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_USER_NAME,
                                                                            CometChatConstants.Errors.ERROR_INVALID_USER_NAME_MESSAGE));
                                }
                            });
                        }
                    } else {
                        // invalid uid error
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                        CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
                            }
                        });
                    }
                } else {
                    // Error for null user
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_USER,
                                                                    CometChatConstants.Errors.ERROR_INVALID_USER_MESSAGE));
                        }
                    });
                }
            } else {
                // Error invalid api key
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_API_KEY_NOT_FOUND,
                                                                CometChatConstants.Errors.ERROR_INVALID_USER_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("user", user.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To update users on the fly.
     *
     * @param user     object of the {@link User} class containing the data for the User.
     * @param apiKey   Auth API Key.
     * @param listener An object of the  <code>CallbackListener&lt;{@link User}&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/users-user-management#updating-a-user"}
     * @see CallbackListener
     * @since <b>2.0.3</b>
     */
    public static void updateUser(
        @NonNull final User user,
        @NonNull String apiKey,
        final CallbackListener<User> listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (apiKey != null && !TextUtils.isEmpty(apiKey)) {
                if (user != null) {
                    if (user.getUid() != null && !TextUtils.isEmpty(user.getUid())) {
                        if (user.getName() == null || !user.getName().equalsIgnoreCase("")) {
                            ApiConnection.getInstance().updateUser(user, apiKey, new ApiConnection.APIConnectionListener() {
                                @Override
                                public void onResponse(String response, final CometChatException ce) {
                                    try {
                                        if (ce != null) {
                                            postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onError(ce);
                                                }
                                            });
                                        } else {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                final User updatedUser = User.fromJson(jsonObject
                                                                                           .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                           .toString());
                                                User currentUser = getLoggedInUser();
                                                if (currentUser != null) {
                                                    if (currentUser.getUid().equalsIgnoreCase(updatedUser.getUid())) {
                                                        CurrentUserRepo.updateCurrentUser(updatedUser.toMap());
                                                        loggedInUser = null;
                                                    }
                                                }
                                                postOnMainThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.onSuccess(updatedUser);
                                                    }
                                                });
                                            } catch (final JSONException je) {
                                                postOnMainThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                                je.getMessage()));
                                                    }
                                                });
                                            }
                                        }
                                    } catch (Exception e) {
                                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                            e.getMessage());
                                        HashMap<String, String> detailsMap = new HashMap<>();
                                        detailsMap.put("user", user.toString());
                                        handleException(methodName, e, detailsMap);
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(uncaughtException);
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            // invalid name error
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_USER_NAME,
                                                                            CometChatConstants.Errors.ERROR_INVALID_USER_NAME_MESSAGE));
                                }
                            });
                        }
                    } else {
                        // invalid uid error
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID,
                                                                        CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE));
                            }
                        });
                    }
                } else {
                    // Error for null user
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_USER,
                                                                    CometChatConstants.Errors.ERROR_INVALID_USER_MESSAGE));
                        }
                    });
                }
            } else {
                // Error invalid api key
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_API_KEY_NOT_FOUND,
                                                                CometChatConstants.Errors.ERROR_INVALID_USER_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("user", user.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To get the list of all the joined groups for the Logged-in user.
     *
     * @param listener An object of the  <code>CallbackListener&lt;&lt;String&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @see CallbackListener
     * @since <b>2.0.3</b>
     */
    public static void getJoinedGroups(final CometChat.CallbackListener<List<String>> listener) {
        if (getLoggedInUser() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //rttConnection.getJoinedGroups(listener);
                }
            }).start();
        } else {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                            CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To get individual conversation for a particular user or group.
     *
     * @param conversationWith The uid/guid of the user/group for which the conversation is to be fetched.
     * @param conversationType The type of the conversation(user/group)
     * @param listener         An object of the  <code>CallbackListener&lt;{@link Conversation}&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-retrieve-conversations#retrieve-single-conversation"}
     * @see CallbackListener
     * @since <b>2.0.4</b>
     */
    public static void getConversation(
        @NonNull final String conversationWith,
        @NonNull @CometChatConstants.ConversationTypes final String conversationType,
        @NonNull final CallbackListener<Conversation> listener
    ) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (conversationWith == null || TextUtils.isEmpty(conversationWith)) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH,
                                                    CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH_MESSAGE));
            return;
        }
        if ((conversationType == null || TextUtils.isEmpty(conversationType) || (!conversationType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) && !conversationType.equalsIgnoreCase(
            CometChatConstants.CONVERSATION_TYPE_GROUP))) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE,
                                                    CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE_MESSAGE));
            return;
        }
        ApiConnection.getInstance().getConversation(conversationWith, conversationType, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            Logger.error(TAG, response);
                            JSONObject jsonObject = new JSONObject(response);
                            final Conversation conversation = Conversation.fromJSON(jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA));
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(conversation);
                                }
                            });

                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    detailsMap.put("conversationType", conversationType);
                    detailsMap.put("conversationWith", conversationWith);
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });
                }
            }
        });
    }

    public static void tagConversation(@NonNull final String conversationWith,
                                       @NonNull @CometChatConstants.ConversationTypes final String conversationType,
                                       final List<String> tags,
                                       final CallbackListener<Conversation> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        if (conversationWith == null || TextUtils.isEmpty(conversationWith)) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH,
                                                    CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH_MESSAGE));
            return;
        }
        if ((conversationType == null || TextUtils.isEmpty(conversationType) || (!conversationType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) && !conversationType.equalsIgnoreCase(
            CometChatConstants.CONVERSATION_TYPE_GROUP))) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE,
                                                    CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE_MESSAGE));
            return;
        }
        if (tags == null) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_TAG_LIST,
                                                    CometChatConstants.Errors.ERROR_INVALID_TAG_LIST_MESSAGE));
            return;
        }
        ApiConnection.getInstance().tagConversation(conversationWith, conversationType, tags, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                try {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            Logger.error(TAG, response);
                            JSONObject jsonObject = new JSONObject(response);
                            final Conversation conversation = Conversation.fromJSON(jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA));
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(conversation);
                                }
                            });

                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    detailsMap.put("conversationType", conversationType);
                    detailsMap.put("conversationWith", conversationWith);
                    detailsMap.put("tags", tags.toString());
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });
                }
            }
        });
    }

    public static void setSource(String resource, String platform, String language) {
        PreferenceHelper.saveResource(resource);
        PreferenceHelper.savePlatform(platform);
        PreferenceHelper.saveLanguage(language);
    }

    public static void setPlatformParams(String platform, String sdkVersion) {
        if (platform != null && !platform.isEmpty() && sdkVersion != null && !sdkVersion.isEmpty()) {
            if (platform.equalsIgnoreCase("flutter") || platform.equalsIgnoreCase("android")) {
                CometChatUtils.overridePlatform = platform;
                CometChatUtils.overrideSdkVersion = sdkVersion;
            }
        }
    }

    public static void setMetaInfo(JSONObject jsonObject) {
        CometChatUtils.metaInfo = jsonObject;
    }

    public static void setDemoMetaInfo(JSONObject jsonObject) {
        CometChatUtils.demoMetaInfo = jsonObject;
    }

    /**
     * {@inheritDoc}
     * To trigger push notification related operations directly via the SDKs.
     *
     * @param slug        Slug of the extension
     * @param requestType Type of the request
     * @param endPoint    Endpoint of the URL For the respective extension
     * @param listener    An object of the  <code>CallbackListener&lt;JSONObject&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @see CallbackListener
     * @since <b>2</b>
     */
    public static void callExtension(@NonNull final String slug,
                                     @NonNull final String requestType,
                                     String endPoint,
                                     final JSONObject body,
                                     final CallbackListener<JSONObject> listener) {
        try {
            final String methodName = new Throwable()
                .getStackTrace()[0]
                .getMethodName();
            Settings settings = SettingsRepo.getSettings();
            if (settings != null) {
                try {
                    if (endPoint.endsWith("/")) {
                        endPoint = endPoint.substring(0, endPoint.length() - 1);
                    }
                    String baseUrl;
                    if (settings.getRegion() == null) {
                        baseUrl = "https://" + slug + "." + settings.getExtensionDomain();
                    } else {
                        baseUrl = "https://" + slug + "-" + settings.getRegion() + "." + settings.getExtensionDomain();
                    }
                    final String url = baseUrl + endPoint;
                    ApiConnection.getInstance().callExtension(url, requestType, body, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(ce);
                                        }
                                    });
                                } else {
                                    try {
                                        final JSONObject jsonObject = new JSONObject(response);
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onSuccess(jsonObject);
                                            }
                                        });
                                    } catch (final JSONException je) {
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                        je.getMessage()));
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                    e.getMessage());
                                HashMap<String, String> detailsMap = new HashMap<>();
                                detailsMap.put("slug", slug);
                                detailsMap.put("requestType", requestType);
                                if (body != null)
                                    detailsMap.put("body", body.toString());
                                detailsMap.put("url", url);
                                detailsMap.put("requestType", requestType);
                                handleException(methodName, e, detailsMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                        e.getMessage());
                    HashMap<String, String> detailsMap = new HashMap<>();
                    detailsMap.put("slug", slug);
                    detailsMap.put("requestType", requestType);
                    if (body != null)
                        detailsMap.put("body", body.toString());
                    detailsMap.put("endpoint", endPoint);
                    detailsMap.put("requestType", requestType);
                    handleException(methodName, e, detailsMap);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });
                }
            } else {
                if (CurrentUserRepo.getCurrentUser() != null && CurrentUserRepo.getCurrentUser().getAuthToken() != null) {
                    final String finalEndPoint = endPoint;
                    getSettings(CurrentUserRepo.getCurrentUser().getAuthToken(), new CallbackListener<Settings>() {
                        @Override
                        public void onSuccess(Settings settings) {
                            Logger.error(TAG, "Settings received from server and updated in DB");
                            getSettingsRetryCounter = 0;
                            callExtension(slug, requestType, finalEndPoint, body, listener);
                        }

                        @Override
                        public void onError(final CometChatException e) {
                            getSettingsRetryCounter++;
                            if (getSettingsRetryCounter < 4) {
                                callExtension(slug, requestType, finalEndPoint, body, listener);
                            } else {
                                Logger.error(TAG, "Error: " + CometChatConstants.Errors.ERROR_APP_SETTING_NULL_MESSAGE + e);
                                getSettingsRetryCounter = 0;
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_APP_SETTINGS_NULL,
                                                                                                    CometChatConstants.Errors.ERROR_APP_SETTING_NULL_MESSAGE);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                                                        CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(uncaughtException);
                        }
                    });
                }
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To detemine if a specific feature is enabled.
     *
     * @param featureName Name of the feature to be checked
     * @param listener    An object of the  <code>CallbackListener&lt;Boolean&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @see CallbackListener
     * @since <b>2.0</b>
     */
    public static void isFeatureEnabled(@NonNull String featureName,
                                        final CallbackListener<Boolean> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (featureName == null || TextUtils.isEmpty(featureName)) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_FEATURE,
                                                                CometChatConstants.Errors.ERROR_INVALID_FEATURE_MESSAGE));
                    }
                });
            } else {
                Settings settings = SettingsRepo.getSettings();
                if (settings != null) {
                    JSONObject jsonObject = new JSONObject(settings.getRawData());
                    if (jsonObject.has(CometChatConstants.SettingsKeys.SETTINGS_APP_PARAMETERS)) {
                        JSONObject parametersObject = jsonObject.getJSONObject(CometChatConstants.SettingsKeys.SETTINGS_APP_PARAMETERS);
                        if (parametersObject.has(featureName)) {
                            final boolean isEnabled = parametersObject.getBoolean(featureName);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(isEnabled);
                                }
                            });
                        } else {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_FEATURE_NOT_FOUND,
                                                                            CometChatConstants.Errors.ERROR_FEATURE_NOT_FOUND_MESSAGE));
                                }
                            });
                        }
                    } else {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_FEATURE_NOT_FOUND,
                                                                        CometChatConstants.Errors.ERROR_FEATURE_NOT_FOUND_MESSAGE));
                            }
                        });
                    }
                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_SETTINGS_NOT_FOUND,
                                                                    CometChatConstants.Errors.ERROR_SETTINGS_NOT_FOUND_MESSAGE));
                        }
                    });
                }
            }

        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("featureName", featureName);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To determine if a specific extension is enabled.
     *
     * @param extensionId Id of the extension for which the details are to be fetched
     * @param listener    An object of the  <code>CallbackListener&lt;Boolean&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @see CallbackListener
     * @since <b>2.0</b>
     */
    public static void isExtensionEnabled(@NonNull String extensionId, final CallbackListener<Boolean> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (extensionId == null || TextUtils.isEmpty(extensionId)) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_EXTENSION,
                                                                CometChatConstants.Errors.ERROR_INVALID_EXTENSION_MESSAGE));
                    }
                });
            } else {
                Settings settings = SettingsRepo.getSettings();
                if (settings != null) {
                    List<String> enabledExtensions = settings.getEnabledExtensions();
                    if (enabledExtensions != null) {
                        final boolean isEnabled = enabledExtensions.contains(extensionId);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(isEnabled);
                            }
                        });
                    } else {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EXTENSION_NOT_FOUND,
                                                                        CometChatConstants.Errors.ERROR_EXTENSION_NOT_FOUND_MESSAGE));
                            }
                        });
                    }


                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_SETTINGS_NOT_FOUND,
                                                                    CometChatConstants.Errors.ERROR_SETTINGS_NOT_FOUND_MESSAGE));
                        }
                    });
                }
            }

        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("extensionId", extensionId);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To get details of a particular extension
     *
     * @param extensionId Id of the extension for which the details are to be fetched
     * @param listener    An object of the  <code>CallbackListener&lt;@Link{CCExtension}&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @see CallbackListener
     * @since <b>2.0</b>
     */
    public static void getExtensionDetails(@NonNull String extensionId, final CallbackListener<CCExtension> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (extensionId == null || TextUtils.isEmpty(extensionId)) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_EXTENSION,
                                                                CometChatConstants.Errors.ERROR_INVALID_EXTENSION_MESSAGE));
                    }
                });
            } else {
                Settings settings = SettingsRepo.getSettings();
                if (settings != null) {
                    List<String> enabledExtensions = settings.getEnabledExtensions();
                    if (enabledExtensions != null) {
                        final boolean isEnabled = enabledExtensions.contains(extensionId);
                        if (isEnabled) {
                            JSONObject mainObject = new JSONObject(settings.getRawData());
                            JSONArray extensionsArray = mainObject.getJSONArray(CometChatConstants.SettingsKeys.SETTINGS_EXTENSIONS);
                            for (int i = 0; i < extensionsArray.length(); i++) {
                                JSONObject jsonObject = extensionsArray.getJSONObject(i);
                                if (!jsonObject.getString("id").equalsIgnoreCase(extensionId))
                                    continue;
                                final CCExtension ccExtension = new CCExtension(jsonObject.getString("id"), jsonObject.getString("name"));
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(ccExtension);
                                    }
                                });
                            }
                        } else {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EXTENSION_NOT_FOUND,
                                                                            CometChatConstants.Errors.ERROR_EXTENSION_NOT_FOUND_MESSAGE));
                                }
                            });
                        }

                    } else {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_EXTENSION_NOT_FOUND,
                                                                        CometChatConstants.Errors.ERROR_EXTENSION_NOT_FOUND_MESSAGE));
                            }
                        });
                    }


                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_SETTINGS_NOT_FOUND,
                                                                    CometChatConstants.Errors.ERROR_SETTINGS_NOT_FOUND_MESSAGE));
                        }
                    });
                }
            }

        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("extensionId", extensionId);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    public static void isAIFeatureEnabled(@NonNull String aiFeatureSlug, @NonNull final CallbackListener<Boolean> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (aiFeatureSlug == null || TextUtils.isEmpty(aiFeatureSlug)) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_FEATURE,
                                                                CometChatConstants.Errors.ERROR_INVALID_FEATURE_MESSAGE));
                    }
                });
            } else {
                Settings settings = SettingsRepo.getSettings();
                if (settings != null) {
                    JSONObject jsonObject = new JSONObject(settings.getRawData());
                    if (jsonObject.has(CometChatConstants.SettingsKeys.SETTINGS_APP_PARAMETERS)) {
                        JSONObject parametersObject = jsonObject.getJSONObject(CometChatConstants.SettingsKeys.SETTINGS_APP_PARAMETERS);
                        boolean isAIFeatureAccessible;
                        boolean isAIFeatureEnabled;
                        boolean isSlugFeatureAccessible;
                        boolean isSlugFeatureEnabled;
                        if (parametersObject.has(CometChatConstants.AIKeys.KEY_AI_FEATURE_ACCESSIBLE)
                            && parametersObject.has(CometChatConstants.AIKeys.KEY_AI_FEATURE_ENABLED)
                            && parametersObject.has(String.format(CometChatConstants.AIKeys.KEY_AI_SLUG_ACCESSIBLE, aiFeatureSlug))
                            && parametersObject.has(String.format(CometChatConstants.AIKeys.KEY_AI_SLUG_ENABLED, aiFeatureSlug))) {
                            isAIFeatureAccessible = parametersObject.getBoolean(CometChatConstants.AIKeys.KEY_AI_FEATURE_ACCESSIBLE);
                            isAIFeatureEnabled = parametersObject.getBoolean(CometChatConstants.AIKeys.KEY_AI_FEATURE_ENABLED);
                            isSlugFeatureAccessible = parametersObject.getBoolean(String.format(CometChatConstants.AIKeys.KEY_AI_SLUG_ACCESSIBLE,
                                                                                                aiFeatureSlug));
                            isSlugFeatureEnabled = parametersObject.getBoolean(String.format(CometChatConstants.AIKeys.KEY_AI_SLUG_ENABLED,
                                                                                             aiFeatureSlug));
                            if (isAIFeatureAccessible && isAIFeatureEnabled && isSlugFeatureAccessible && isSlugFeatureEnabled) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(true);
                                    }
                                });
                            } else {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(false);
                                    }
                                });
                            }
                        } else {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(false);
                                }
                            });
                        }

                    } else {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_FEATURE_NOT_FOUND,
                                                                        CometChatConstants.Errors.ERROR_FEATURE_NOT_FOUND_MESSAGE));
                            }
                        });
                    }
                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_SETTINGS_NOT_FOUND,
                                                                    CometChatConstants.Errors.ERROR_SETTINGS_NOT_FOUND_MESSAGE));
                        }
                    });
                }
            }

        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("aiFeatureSlug", aiFeatureSlug);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }


    /**
     * {@inheritDoc}
     * This method helps to get the count of the participants in any particular call..
     *
     * @param sessionId The session Id of the call for which the participant count is to be fetched
     * @param type      The type of the call(direct/default)
     * @param listener  An object of the  <code>CallbackListener&lt;Integer&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/calling-direct-calling#call-participant-count"}
     * @see CallbackListener
     * @since <b>2.0.4</b>
     */
    public static void getCallParticipantCount(@NonNull String sessionId, @NonNull String
        type, final @NonNull CallbackListener<Integer> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (sessionId == null || TextUtils.isEmpty(sessionId)) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SESSIONID,
                                                                CometChatConstants.Errors.ERROR_INVALID_SESSIONID_MESSAGE));
                    }
                });
            } else if (type == null || TextUtils.isEmpty(type)) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_TYPE,
                                                                CometChatConstants.Errors.ERROR_INVALID_TYPE_MESSAGE));
                    }
                });
            } else if (!type.equalsIgnoreCase(CallSettings.CALL_MODE_DIRECT) && !type.equalsIgnoreCase(CallSettings.CALL_MODE_DEFAULT)) {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_TYPE,
                                                                CometChatConstants.Errors.ERROR_INVALID_TYPE_MESSAGE));
                    }
                });
            } else {
                if (type.equalsIgnoreCase(CallSettings.CALL_MODE_DIRECT)) {
                    String region = appSettings.getRegion();
                    sessionId = "v1." + region + "." + PreferenceHelper.getAppID() + "." + sessionId;
                }
                ApiConnection.getInstance().getCallParticipantCount(sessionId, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        if (ce != null) {
                            if (ce.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_NO_PARTICIPANTS)) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(0);
                                    }
                                });
                            } else {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            }
                        } else {
                            try {
                                final JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has(CometChatConstants.CallKeys.KEY_PARTICIPANTS)) {
                                    final int participantCount = jsonObject.getInt(CometChatConstants.CallKeys.KEY_PARTICIPANTS);
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(participantCount);
                                        }
                                    });
                                } else {
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(0);
                                        }
                                    });
                                }
                            } catch (final JSONException je) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                    }
                                });
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("sessionId", sessionId);
            detailsMap.put("type", sessionId);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To update the details of the logged in user.
     *
     * @param user     An object of the @link{User} class containing the details to be updated
     * @param listener An object of the  <code>CallbackListener&lt;@link{User}&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/users-user-management#updating-logged-in-user"}
     * @see CallbackListener
     * @since <b>2.0</b>
     */
    public static void updateCurrentUserDetails(@NonNull final User user, final CallbackListener<User> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (user != null) {
                if (user.getName() == null || !user.getName().equalsIgnoreCase("")) {
                    ApiConnection.getInstance().updateCurrentUserDetails(user, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onError(ce);
                                        }
                                    });
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        final User updatedUser = User.fromJson(jsonObject
                                                                                   .getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                                                                   .toString());
                                        User currentUser = getLoggedInUser();
                                        if (currentUser != null) {
                                            if (currentUser.getUid().equalsIgnoreCase(updatedUser.getUid())) {
                                                CurrentUserRepo.updateCurrentUser(updatedUser.toMap());
                                                loggedInUser = null;
                                            }
                                        }
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onSuccess(updatedUser);
                                            }
                                        });
                                    } catch (final JSONException je) {
                                        postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                        je.getMessage()));
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                                    e.getMessage());
                                HashMap<String, String> detailsMap = new HashMap<>();
                                detailsMap.put("user", user.toString());
                                handleException(methodName, e, detailsMap);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(uncaughtException);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    // invalid name error
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_USER_NAME,
                                                                    CometChatConstants.Errors.ERROR_INVALID_USER_NAME_MESSAGE));
                        }
                    });
                }
            } else {
                // Error for null user
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_USER,
                                                                CometChatConstants.Errors.ERROR_INVALID_USER_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("user", user.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }


    /**
     * {@inheritDoc}
     * To delete a specific conversation for the logged in user.
     *
     * @param conversationWith The UID/GUID of the conversation to be deleted.
     * @param conversationType The type of the conversations (user/group)
     * @param listener         An object of the  <code>CallbackListener&lt;String&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-delete-conversation"}
     * @see CallbackListener
     * @since <b>2.0</b>
     */
    public static void deleteConversation(@NonNull String conversationWith,
                                          @NonNull String conversationType,
                                          final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (conversationWith != null && !conversationWith.equalsIgnoreCase("")) {
                if (conversationType != null && !conversationType.equalsIgnoreCase("")) {
                    ApiConnection.getInstance().deleteConversation(conversationWith, conversationType, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            if (ce != null) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(CometChatConstants.SuccessMessages.MESSAGE_CONVERSATION_DELETE_SUCCESS);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE,
                                                                    CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE_MESSAGE));
                        }
                    });
                }
            } else {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH,
                                                                CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("conversationWith", conversationWith);
            detailsMap.put("conversationType", conversationType);
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To send a transient message to a user or a group.
     *
     * @param transientMessage An object of the @link{TransientMessage}.
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-transient-messages#send-a-transient-message"}
     * @see CallbackListener
     * @since <b>2.0</b>
     */
    public static void sendTransientMessage(@NonNull TransientMessage transientMessage) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        Settings settings = SettingsRepo.getSettings();
        if (getLoggedInUser() != null) {
            if (!ConnectionController.getInstance().getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_FEATURE_THROTTLED)) {
                if (settings != null && !settings.getMode().equalsIgnoreCase(CometChatConstants.MODE_NO_TRANSIENT)) {
                    try {
                        if (transientMessage != null) {
                            if (rttConnection != null) {
                                rttConnection.sendTransientMessage(transientMessage);
                            } else {
                                Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                            }
                        } else {
                            Logger.exception(TAG, "Transient Message cannot be null");
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        HashMap<String, String> detailsMap = new HashMap<>();
                        detailsMap.put("transientMessage", transientMessage.toString());
                        handleException(methodName, e, detailsMap);
                    }
                } else {
                    Logger.exception(TAG, "Transient Messages not functional in " + settings + " mode");
                }
            } else {
                Logger.exception(TAG, "Transient Messages not function in the feature-throttled mode");
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE);
        }
    }

    static void restart(boolean fromVersionCheckRestart) {
        CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
        if (currentUser != null && currentUser.getAuthToken() != null) {
            PreferenceHelper.saveRestartAuthToken(currentUser.getAuthToken());
            internalLogout(false);
            loginWithAuthTokenInternal(PreferenceHelper.getRestartAUthToken(), true, fromVersionCheckRestart, new CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    PreferenceHelper.clearRestartAuthToken();
                    Logger.error(TAG, "Restart Successful");

                }

                @Override
                public void onError(CometChatException e) {
                    Logger.error(TAG, "Restart Failed");
                }
            });

        }
    }

    /**
     * {@inheritDoc}
     * To get the total online users count for the app id
     *
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/users-retrieve-users#get-online-user-count"}
     * @see CallbackListener
     * @since <b>2.0</b>
     */
    public static void getOnlineUserCount(final CallbackListener<Integer> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            ApiConnection.getInstance().getOnlineUserCount(new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ONLINE_USER_COUNT)) {

                                final int onlineUserCount = dataObject.getInt(CometChatConstants.ResponseKeys.KEY_ONLINE_USER_COUNT);
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(onlineUserCount);
                                    }
                                });
                            } else {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(0);
                                    }
                                });
                            }
                        } catch (final JSONException e) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                }
                            });
                        }
                    }
                }
            });
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * To get the count of online users in specific groups.
     *
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/groups-retrieve-groups#get-online-group-member-count"}
     * @see CallbackListener
     * @since <b>2.0</b>
     */
    public static void getOnlineGroupMemberCount(@NonNull List<String> guids, final CallbackListener<HashMap<String, Integer>> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (guids != null && guids.size() > 0) {
                ApiConnection.getInstance().getOnlineGroupMemberCount(guids, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        if (ce != null) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                final HashMap<String, Integer> groupOnlineMemberCount = new HashMap<>();
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                if (dataObject.has(CometChatConstants.Params.KEY_GROUPS)) {
                                    JSONObject groupObject = dataObject.getJSONObject(CometChatConstants.Params.KEY_GROUPS);
                                    Iterator<String> keys = groupObject.keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        groupOnlineMemberCount.put(key, groupObject.getInt(key));
                                    }
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(groupOnlineMemberCount);
                                        }
                                    });
                                } else {
                                    postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(groupOnlineMemberCount);
                                        }
                                    });
                                }

                            } catch (final JSONException e) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                    }
                                });
                            }
                        }
                    }
                });
            } else {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GROUPLIST,
                                                                CometChatConstants.Errors.ERROR_INVALID_GROUPLIST_MESSAGE));
                    }
                });
            }
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    // Methods for CometChat AI Features

    public static void getSmartReplies(@NonNull String receiverId,
                                       @NonNull String receiverType,
                                       @NonNull final CallbackListener<HashMap<String, String>> listener) {
        getSmartReplies(receiverId, receiverType, null, listener);
    }

    public static void getSmartReplies(@NonNull String receiverId,
                                       @NonNull String receiverType,
                                       JSONObject configuration,
                                       @NonNull final CallbackListener<HashMap<String, String>> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (receiverId == null || TextUtils.isEmpty(receiverId)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_ID,
                                                        CometChatConstants.Errors.ERROR_INVALID_RECEIVER_ID_MESSAGE));
                return;
            }
            if ((receiverType == null || TextUtils.isEmpty(receiverType) || (!receiverType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) && !receiverType.equalsIgnoreCase(
                CometChatConstants.RECEIVER_TYPE_GROUP))) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_RECEIVER_TYPE_MESSAGE));
                return;
            }
            ApiConnection.getInstance().getSmartReplies(receiverId, receiverType, configuration, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            JSONObject SmartReplyObject = dataObject.getJSONObject(CometChatConstants.AIKeys.KEY_SMART_REPLIES);
                            final HashMap<String, String> smartReplyMap = new HashMap<>();
                            Iterator<String> iterator = SmartReplyObject.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                String value = SmartReplyObject.getString(key);
                                smartReplyMap.put(key, value);
                            }
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(smartReplyMap);
                                }
                            });
                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                }
            });
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("receiverId", receiverId);
            detailsMap.put("receiverType", receiverType);
            detailsMap.put("configuration", configuration.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    public static void getConversationStarter(@NonNull String receiverId,
                                              @NonNull String receiverType,
                                              @NonNull final CallbackListener<List<String>> listener) {
        getConversationStarter(receiverId, receiverType, null, listener);
    }

    public static void getConversationStarter(@NonNull String receiverId,
                                              @NonNull String receiverType,
                                              JSONObject configuration,
                                              @NonNull final CallbackListener<List<String>> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (receiverId == null || TextUtils.isEmpty(receiverId)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH,
                                                        CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH_MESSAGE));
                return;
            }
            if ((receiverType == null || TextUtils.isEmpty(receiverType) || (!receiverType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) && !receiverType.equalsIgnoreCase(
                CometChatConstants.RECEIVER_TYPE_GROUP))) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE_MESSAGE));
                return;
            }
            ApiConnection.getInstance().getConversationStarter(receiverId, receiverType, configuration, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            JSONArray conversationStartersObject = dataObject.getJSONArray(CometChatConstants.AIKeys.KEY_CONVERSATION_STARTER);
                            final List conversationStarterList = new ArrayList();
                            for (int i = 0; i < conversationStartersObject.length(); i++) {
                                String a = conversationStartersObject.getString(i);
                                conversationStarterList.add(a);
                            }
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(conversationStarterList);
                                }
                            });
                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                }
            });
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("receiverId", receiverId);
            detailsMap.put("receiverType", receiverType);
            detailsMap.put("configuration", configuration.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    public static void getConversationSummary(@NonNull String receiverId, @NonNull String receiverType, @NonNull CallbackListener<String> listener) {
        getConversationSummary(receiverId, receiverType, null, listener);
    }

    public static void getConversationSummary(@NonNull String receiverId,
                                              @NonNull String receiverType,
                                              JSONObject configuration,
                                              @NonNull final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (receiverId == null || TextUtils.isEmpty(receiverId)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH,
                                                        CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH_MESSAGE));
                return;
            }
            if ((receiverType == null || TextUtils.isEmpty(receiverType) || (!receiverType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) && !receiverType.equalsIgnoreCase(
                CometChatConstants.RECEIVER_TYPE_GROUP))) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE_MESSAGE));
                return;
            }
            ApiConnection.getInstance().getConversationSummary(receiverId, receiverType, configuration, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            final JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            if (dataObject.has(CometChatConstants.AIKeys.KEY_CONVERSATION_SUMMARY)) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            listener.onSuccess(dataObject.getString(CometChatConstants.AIKeys.KEY_CONVERSATION_SUMMARY));
                                        } catch (final JSONException jee) {
                                            postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                            jee.getMessage()));
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION));
                                    }
                                });
                            }
                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                }
            });
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("receiverId", receiverId);
            detailsMap.put("receiverType", receiverType);
            detailsMap.put("configuration", configuration.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }

    public static void askBot(@NonNull String receiverId,
                              @NonNull String receiverType,
                              @NonNull String botId,
                              @NonNull String question,
                              @NonNull CallbackListener<String> listener) {
        askBot(receiverId, receiverType, botId, question, null, listener);
    }

    public static void askBot(@NonNull String receiverId,
                              @NonNull String receiverType,
                              @NonNull String botId,
                              @NonNull String question,
                              JSONObject configuration,
                              @NonNull final CallbackListener<String> listener) {
        final String methodName = new Throwable()
            .getStackTrace()[0]
            .getMethodName();
        try {
            if (receiverId == null || TextUtils.isEmpty(receiverId)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH,
                                                        CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_WITH_MESSAGE));
                return;
            }
            if ((receiverType == null || TextUtils.isEmpty(receiverType) || (!receiverType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) && !receiverType.equalsIgnoreCase(
                CometChatConstants.RECEIVER_TYPE_GROUP))) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_CONVERSATION_TYPE_MESSAGE));
                return;
            }
            if (botId == null || TextUtils.isEmpty(botId)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_BOT_ID,
                                                        CometChatConstants.Errors.ERROR_INVALID_BOT_ID_MESSAGE));
                return;
            }
            if (question == null || TextUtils.isEmpty(question)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_QUESTION,
                                                        CometChatConstants.Errors.ERROR_INVALID_QUESTION_MESSAGE));
                return;
            }
            ApiConnection.getInstance().askBot(receiverId, receiverType, botId, question, configuration, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    if (ce != null) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(ce);
                            }
                        });
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            final JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            if (dataObject.has(CometChatConstants.AIKeys.KEY_AI_BOT_REPLY)) {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            listener.onSuccess(dataObject.getString(CometChatConstants.AIKeys.KEY_AI_BOT_REPLY));
                                        } catch (final JSONException jee) {
                                            postOnMainThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                                                            jee.getMessage()));
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION));
                                    }
                                });
                            }
                        } catch (final JSONException je) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                }
                            });
                        }
                    }
                }
            });
        } catch (Exception e) {
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("receiverId", receiverId);
            detailsMap.put("receiverType", receiverType);
            detailsMap.put("botId", botId);
            detailsMap.put("question", question);
            detailsMap.put("configuration", configuration.toString());
            handleException(methodName, e, detailsMap);
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(uncaughtException);
                }
            });
        }
    }


    private static CometChatException validateScope(String scope) {
        CometChatException cometChatException = null;
        if (scope == null || TextUtils.isEmpty(scope))
            cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_EMPTY_SCOPE,
                                                        CometChatConstants.Errors.ERROR_EMPTY_SCOPE_MESSAGE);
        else if (!scope.equalsIgnoreCase(CometChatConstants.SCOPE_ADMIN) &&
            !scope.equalsIgnoreCase(CometChatConstants.SCOPE_MODERATOR) &&
            !scope.equalsIgnoreCase(CometChatConstants.SCOPE_PARTICIPANT))
            cometChatException = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_SCOPE,
                                                        CometChatConstants.Errors.ERROR_INVALID_SCOPE_MESSAGE);
        return cometChatException;

    }

    static void postOnMainThread(Runnable runnable) {
        if (mainThreadHandler != null) {
            mainThreadHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    static void postError(final CallbackListener listener, final CometChatException ce) {
        postOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null)
                    listener.onError(ce);
            }
        });
    }

    /**
     * {@inheritDoc}
     * To receive different types of messages Developer needs to make use of this method
     *
     * @param listenerID Unique Identifier for the Listener
     * @param listener   An object of the <code>MessageListener<code/> class
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#real-time-messages"}
     * @see MessageListener
     * @since <b>v1</b>
     */
    public static void addMessageListener(@NonNull String listenerID, @NonNull MessageListener listener) {
        if (!TextUtils.isEmpty(listenerID)) {
            messageListeners.put(listenerID, listener);
        }
    }

    public static void addAIAssistantListener(@NonNull String listenerID, @NonNull AIAssistantListener listener) {
        if (!TextUtils.isEmpty(listenerID)) {
            aiAssistantListeners.put(listenerID, listener);
        }
    }

    /**
     * {@inheritDoc}
     * To stop receiving messages developer can make use of this method
     *
     * @param listenerID Unique Identifier for the Listener which was used in <code>addMessageListener()<code/>
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/messaging-receive-messages#real-time-messages"}
     * @since <b>v1</b>
     */
    public static void removeMessageListener(@NonNull String listenerID) {
        if (listenerID != null && !TextUtils.isEmpty(listenerID)) {
            messageListeners.remove(listenerID);
        }
    }

    public static void removeAIAssistantListener(@NonNull String listenerID){
        if (listenerID != null && !TextUtils.isEmpty(listenerID)) {
            aiAssistantListeners.remove(listenerID);
        }
    }

    /**
     * {@inheritDoc}
     * To receive presence information of users Developer needs to make use of this method
     *
     * @param listenerID Unique Identifier for the Listener
     * @param listener   An object of the <code>UserListener<code/> class
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/user-presence#real-time-presence"}
     * @see UserListener
     * @since <b>v1</b>
     */
    public static void addUserListener(@NonNull String listenerID, @NonNull UserListener
        listener) {
        if (listenerID != null && !TextUtils.isEmpty(listenerID) && listener != null) {
            userListeners.put(listenerID, listener);
        }
    }

    /**
     * {@inheritDoc}
     * To stop receiving presence information of different users developer can make use of this method
     *
     * @param listenerID Unique Identifier for the Listener which was used in <code>addUserListener()<code/>
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/user-presence#real-time-presence"}
     * @since <b>v1</b>
     */
    public static void removeUserListener(@NonNull String listenerID) {
        if (!TextUtils.isEmpty(listenerID)) {
            userListeners.remove(listenerID);
        }
    }

    /**
     * {@inheritDoc}
     * To receive call events Developer needs to make use of this method
     *
     * @param listenerId   Unique Identifier for the Listener
     * @param callListener An object of the <code>CallListener<code/> class
     * @version <b>v2</b>
     * @see CallListener
     * @since <b>v1</b>
     */
    public static void addCallListener(@NonNull String listenerId, @NonNull CallListener callListener) {
        if (listenerId != null && callListener != null)
            callListeners.put(listenerId, callListener);
    }

    /**
     * {@inheritDoc}
     * To stop receiving Call event developer can make use of this method
     *
     * @param listenerId Unique Identifier for the Listener which was used in <code>addCallListener()<code/>
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public static void removeCallListener(@NonNull String listenerId) {
        if (listenerId != null)
            callListeners.remove(listenerId);
    }

    /**
     * {@inheritDoc}
     * To receive connection events Developer needs to make use of this method
     *
     * @param listenerId         Unique Identifier for the Listener
     * @param connectionListener An object of the <code>ConnectionListener<code/> class
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/advanced-connection-status"}
     * @see CallListener
     * @since <b>v2</b>
     */
    public static void addConnectionListener(@NonNull String listenerId, @NonNull ConnectionListener connectionListener) {
        if (listenerId != null && connectionListener != null)
            connectionListeners.put(listenerId, connectionListener);
    }

    /**
     * {@inheritDoc}
     * To stop receiving Connection events developer can make use of this method
     *
     * @param listenerId Unique Identifier for the Listener which was used in <code>addConnectionListener()<code/>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/advanced-connection-status"}
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public static void removeConnectionListener(@NonNull String listenerId) {
        if (listenerId != null)
            connectionListeners.remove(listenerId);
    }

    /**
     * {@inheritDoc}
     * To receiver Group Actions developer can make use of this method
     *
     * @param listenerId    Unique Identifier for the Listener
     * @param groupListener An object of the <code>GroupListener<code/> class
     * @version <b>v2</b>
     * @see GroupListener
     * @since <b>v1</b>
     */
    public static void addGroupListener(@NonNull String listenerId, @NonNull GroupListener groupListener) {
        groupListeners.put(listenerId, groupListener);
    }

    /**
     * To stop receiving Group event developer can make use of this method
     *
     * @param listenerId Unique Identifier for the Listener which was used in <code>addGroupListener()<code/>
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public static void removeGroupListener(@NonNull String listenerId) {
        groupListeners.remove(listenerId);
    }

    /**
     * {@inheritDoc}
     * Get information about logged in User
     *
     * @return User object which contains information about the logged in user
     * @version <b>v2</b>
     * @docs {@link= "https://www.cometchat.com/docs/android-chat-sdk/users-retrieve-users#retrieve-logged-in-user-details"}
     * @see User
     * @since <b>v1</b>
     */
    public static User getLoggedInUser() {
        if (loggedInUser == null)
            loggedInUser = CurrentUserRepo.getLoggedInUser();
        if (loggedInUser != null && loggedInUser.getStatus().equalsIgnoreCase(CometChatConstants.USER_STATUS_OFFLINE))
            loggedInUser.setStatus(CometChatConstants.USER_STATUS_ONLINE);
        return loggedInUser;
    }

    /**
     * {@inheritDoc}
     * Get logged in user auth token
     *
     * @return Return logged in user auth token
     * @version <b>v2</b>
     * @see String
     * @since <b>v1</b>
     */
    public static String getUserAuthToken() {
        try {
            if (CurrentUserRepo.getCurrentUser() != null) {
                CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
                return currentUser.getAuthToken();
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.error(TAG, "Error: " + e);
            return null;
        }
    }

    /**
     * MessageListener class to provides methods to receive different types of messages
     */
    public abstract static class MessageListener {

        /**
         * {@inheritDoc}
         * To receive text messages
         *
         * @param message An object of the <code>TextMessage<code/> class
         * @version <b>v2</b>
         * @see TextMessage
         * @since <b>v1</b>
         */
        public void onTextMessageReceived(TextMessage message) {

        }

        /**
         * {@inheritDoc}
         * To receive media messages
         *
         * @param message An object of the <code>MediaMessage<code/> class
         * @version <b>v2</b>
         * @see MediaMessage
         * @since <b>v1</b>
         */
        public void onMediaMessageReceived(MediaMessage message) {

        }

        /**
         * {@inheritDoc}
         * To receive custom messages
         *
         * @param message An object of the <code>CustomMessage<code/> class
         * @version <b>v2</b>
         * @see CustomMessage
         * @since <b>v1</b>
         */
        public void onCustomMessageReceived(CustomMessage message) {

        }

        /**
         * {@inheritDoc}
         * To receive interactive messages
         *
         * @param message An object of the <code>InteractiveMessage<code/> class
         * @version <b>v4</b>
         * @see InteractiveMessage
         */
        public void onInteractiveMessageReceived(InteractiveMessage message) {

        }

        /**
         * {@inheritDoc}
         * To receive interaction receipt when the goal is completed
         *
         * @param receipt An object of the <code>InteractionReceipt<code/> class
         * @version <b>v4</b>
         * @see InteractionReceipt
         */
        public void onInteractionGoalCompleted(InteractionReceipt receipt) {

        }

        /**
         * {@inheritDoc}
         * To receive typingIndicator when user starts typing
         *
         * @param typingIndicator An object of the <code>TypingIndicator<code/> class
         * @version <b>v2</b>
         * @see TypingIndicator
         * @since <b>v1</b>
         */
        public void onTypingStarted(TypingIndicator typingIndicator) {

        }

        /**
         * {@inheritDoc}
         * To receive typingIndicator when user ends typing
         *
         * @param typingIndicator An object of the <code>TypingIndicator<code/> class
         * @version <b>v2</b>
         * @see TypingIndicator
         * @since <b>v1</b>
         */
        public void onTypingEnded(TypingIndicator typingIndicator) {

        }

        /**
         * {@inheritDoc}
         * To receive message delivery receipts on message being delivered
         *
         * @param messageReceipt An object of the <code>MessageReceipt<code/> class
         * @version <b>v2</b>
         * @see MessageReceipt
         * @since <b>v1</b>
         */
        public void onMessagesDelivered(MessageReceipt messageReceipt) {

        }

        /**
         * {@inheritDoc}
         * To receive message read receipts on message being read
         *
         * @param messageReceipt An object of the <code>MessageReceipt<code/> class
         * @version <b>v2</b>
         * @see MessageReceipt
         * @since <b>v1</b>
         */
        public void onMessagesRead(MessageReceipt messageReceipt) {

        }

        /**
         * {@inheritDoc}
         * To receive message delivery receipts on message being delivered to all the members of a group.
         *
         * @param messageReceipt An object of the <code>MessageReceipt<code/> class
         * @version <b>v2</b>
         * @see MessageReceipt
         * @since <b>v1</b>
         */
        public void onMessagesDeliveredToAll(MessageReceipt messageReceipt) {

        }

        /**
         * {@inheritDoc}
         * To receive message read receipts on message being read by all the members of a group.
         *
         * @param messageReceipt An object of the <code>MessageReceipt<code/> class
         * @version <b>v2</b>
         * @see MessageReceipt
         * @since <b>v1</b>
         */
        public void onMessagesReadByAll(MessageReceipt messageReceipt) {

        }

        /**
         * {@inheritDoc}
         * To receive edited/updated message
         *
         * @param message An object of the <code>BaseMessage<code/> class
         * @version <b>v2</b>
         * @see BaseMessage
         * @since <b>v1</b>
         */
        public void onMessageEdited(BaseMessage message) {

        }

        /**
         * {@inheritDoc}
         * To receive deleted message
         *
         * @param message An object of the <code>BaseMessage<code/> class
         * @version <b>v2</b>
         * @see BaseMessage
         * @since <b>v1</b>
         */
        public void onMessageDeleted(BaseMessage message) {

        }

        public void onTransientMessageReceived(TransientMessage transientMessage) {

        }

        /**
         * {@inheritDoc}
         * To receive reaction event when user reacts on message
         *
         * @param reactionEvent An object of <code>ReactionEvent<code/> class
         * @version <b>v4</b>
         * @see ReactionEvent
         * @since <b>v4</b>
         */
        public void onMessageReactionAdded(ReactionEvent reactionEvent) {

        }

        /**
         * {@inheritDoc}
         * To receive reaction remove event when user remove reaction on message
         *
         * @param reactionEvent An object of <code>ReactionEvent<code/> class
         * @version <b>v4</b>
         * @see ReactionEvent
         * @since <b>v4</b>
         */
        public void onMessageReactionRemoved(ReactionEvent reactionEvent) {

        }

        /**
         * Called when a message has been moderated by the platform.
         * This callback is triggered when a message is reviewed and potentially
         * modified or flagged by CometChat's moderation system.
         *
         * @param baseMessage The message that has been moderated
         */
        public void onMessageModerated(BaseMessage baseMessage) {

        }

        /**
         * Called when an AI Assistant message is received.
         * This callback is triggered when the AI assistant sends a response
         * message containing generated content or answers.
         *
         * @param aiAssistantMessage The AI assistant message that was received
         */
        public void onAIAssistantMessageReceived(AIAssistantMessage aiAssistantMessage){

        }

        /**
         * Called when an AI tool execution result is received.
         * This callback is triggered when an AI tool has completed its execution
         * and returns the result of the tool call operation.
         *
         * @param aiToolResultMessage The message containing the tool execution result
         */
        public void onAIToolResultReceived(AIToolResultMessage aiToolResultMessage){

        }

        /**
         * Called when AI tool arguments are received.
         * This callback is triggered when the AI assistant is about to invoke
         * a tool and sends the arguments that will be passed to the tool.
         *
         * @param aiToolArgumentMessage The message containing the tool call arguments
         */
        public void onAIToolArgumentsReceived(AIToolArgumentMessage aiToolArgumentMessage){

        }

        /**
         * Called when a Card message is received.
         * This callback is triggered when a developer-sent card message arrives.
         * Card messages contain rich, interactive content described as a block of JSON.
         *
         * @param cardMessage The card message that was received
         */
        public void onCardMessageReceived(CardMessage cardMessage){

        }
    }

    /**
     * Abstract listener class for receiving AI Assistant events in the CometChat SDK.
     * This listener provides callbacks for various AI Assistant events such as tool calls,
     * streaming responses, and other AI-related activities. Implement this class to handle
     * AI Assistant events in your application.
     *
     * <p>The listener receives events through the {@link #onAIAssistantEventReceived(AIAssistantBaseEvent)}
     * method, which can include various types of events such as:
     * <ul>
     *   <li>{@link AIAssistantToolStartedEvent} - When an AI tool call starts</li>
     *   <li>{@link AIAssistantToolEndedEvent} - When an AI tool call ends</li>
     *   <li>{@link AIAssistantToolResultEvent} - When an AI tool call result is received</li>
     *   <li>Other AI Assistant related events extending {@link AIAssistantBaseEvent}</li>
     * </ul>
     *
     * <p>Usage example:
     * <pre>{@code
     * CometChat.addAIAssistantListener("LISTENER_ID", new CometChat.AIAssistantListener() {
     *     @Override
     *     public void onAIAssistantEventReceived(AIAssistantBaseEvent event) {
     *         if (event instanceof AIAssistantToolStartedEvent) {
     *             AIAssistantToolStartedEvent toolStarted = (AIAssistantToolStartedEvent) event;
     *             // Handle tool started event
     *         } else if (event instanceof AIAssistantToolEndedEvent) {
     *             AIAssistantToolEndedEvent toolEnded = (AIAssistantToolEndedEvent) event;
     *             // Handle tool ended event
     *         }
     *     }
     * });
     * }</pre>
     *
     * @author CometChat Team
     * @version 4.0
     * @see AIAssistantBaseEvent
     * @see AIAssistantToolStartedEvent
     * @see AIAssistantToolEndedEvent
     * @see AIAssistantToolResultEvent
     * @since 4.0
     */
    public abstract static class AIAssistantListener {
        /**
         * Called when an AI Assistant event is received from the CometChat server.
         * This method is invoked for all types of AI Assistant events including tool calls,
         * streaming responses, and other AI-related activities.
         *
         * <p>The event parameter will be an instance of a specific event type that extends
         * {@link AIAssistantBaseEvent}. Use instanceof checks to determine the specific
         * event type and handle accordingly.
         *
         * @param event The AI Assistant event that was received. This will be a concrete
         *              implementation of {@link AIAssistantBaseEvent} such as
         *              {@link AIAssistantToolStartedEvent}, {@link AIAssistantToolEndedEvent},
         *              or {@link AIAssistantToolResultEvent}
         * @see AIAssistantBaseEvent
         * @see AIAssistantToolStartedEvent
         * @see AIAssistantToolEndedEvent
         * @see AIAssistantToolResultEvent
         */
        public abstract void onAIAssistantEventReceived(AIAssistantBaseEvent event);
    }

    /**
     * Make use of <code>UserListener<code/> class to receive user presence
     */
    public abstract static class UserListener {

        /**
         * {@inheritDoc}
         * To receive presence when user gets online
         *
         * @param user An object of <code>User<code/> class
         * @version <b>v2</b>
         * @see User
         * @since <b>v1</b>
         */
        public void onUserOnline(User user) {

        }

        /**
         * {@inheritDoc}
         * To receive presence when user gets offline
         *
         * @param user An object of <code>User<code/> class
         * @version <b>v2</b>
         * @see User
         * @since <b>v1</b>
         */
        public void onUserOffline(User user) {
        }

    }

    /**
     * <code>GroupListener<code/> class provides different methods to receive group events/actions
     */
    public abstract static class GroupListener {

        /**
         * {@inheritDoc}
         * To receive member join event of groups
         *
         * @param action      An object of the <code>Action<code/> class which gives information about the action triggered or performed
         * @param joinedUser  An object of the <code>User<code/> class which gives information about the user joined
         * @param joinedGroup An object of the <code>Group<code/> class which gives information about the group which is joined
         * @version <b>v2</b>
         * @see Action
         * @see User
         * @see Group
         * @since <b>v1</b>
         */
        public void onGroupMemberJoined(Action action, User joinedUser, Group joinedGroup) {
        }

        /**
         * {@inheritDoc}
         * To receive member left event of groups
         *
         * @param action    An object of the <code>Action<code/> class which gives information about the action triggered or performed
         * @param leftUser  An object of the <code>User<code/> class which gives information about the user left
         * @param leftGroup An object of the <code>Group<code/> class which gives information about the group which is left
         * @version <b>v2</b>
         * @see Action
         * @see User
         * @see Group
         * @since <b>v1</b>
         */
        public void onGroupMemberLeft(Action action, User leftUser, Group leftGroup) {

        }

        /**
         * {@inheritDoc}
         * To receive member kicked event of groups
         *
         * @param action     An object of the <code>Action<code/> class which gives information about the action triggered or performed
         * @param kickedUser An object of the <code>User<code/> class which gives information about the kicked user
         * @param kickedBy   An object of the <code>User<code/> class which gives information about the user who kicked a user
         * @param kickedFrom An object of the <code>Group<code/> class which gives information about the group from which user is kicked
         * @version <b>v2</b>
         * @see Action
         * @see User
         * @see Group
         * @since <b>v1</b>
         */
        public void onGroupMemberKicked(Action action, User kickedUser, User kickedBy, Group kickedFrom) {

        }

        /**
         * {@inheritDoc}
         * To receive member banned event of groups
         *
         * @param action     An object of the <code>Action<code/> class which gives information about the action triggered or performed
         * @param bannedUser An object of the <code>User<code/> class which gives information about the banned user
         * @param bannedBy   An object of the <code>User<code/> class which gives information about the user who banned a user
         * @param bannedFrom An object of the <code>Group<code/> class which gives information about the group from which user is banned
         * @version <b>v2</b>
         * @see Action
         * @see User
         * @see Group
         * @since <b>v1</b>
         */
        public void onGroupMemberBanned(Action action, User bannedUser, User bannedBy, Group bannedFrom) {

        }

        /**
         * {@inheritDoc}
         * To receive member unbanned event of groups
         *
         * @param action       An object of the <code>Action<code/> class which gives information about the action triggered or performed
         * @param unbannedUser An object of the <code>User<code/> class which gives information about the unbanned user
         * @param unbannedBy   An object of the <code>User<code/> class which gives information about the user who unbanned a user
         * @param unbannedFrom An object of the <code>Group<code/> class which gives information about the group from which user is unbanned
         * @version <b>v2</b>
         * @see Action
         * @see User
         * @see Group
         * @since <b>v1</b>
         */
        public void onGroupMemberUnbanned(Action action, User unbannedUser, User unbannedBy, Group unbannedFrom) {

        }

        /**
         * {@inheritDoc}
         * To receive member's scope change event of groups
         *
         * @param action           An object of the <code>Action<code/> class which gives information about the action triggered or performed
         * @param updatedUser      An object of the <code>User<code/> class which gives information about the updated user
         * @param updatedBy        An object of the <code>User<code/> class which gives information about the user who updated a user's scope
         * @param scopeChangedTo   updated scope of the user
         * @param scopeChangedFrom old scope of the user
         * @param group            An object of the <code>Group<code/> class which gives information about the group from which user's scope is updated
         * @version <b>v2</b>
         * @see Action
         * @see User
         * @see Group
         * @see CometChatConstants.MemberScope
         * @since <b>v1</b>
         */
        public void onGroupMemberScopeChanged(Action action,
                                              User updatedBy,
                                              User updatedUser,
                                              String scopeChangedTo,
                                              String scopeChangedFrom,
                                              Group group) {

        }

        /**
         * {@inheritDoc}
         * To receive event on user added to group
         *
         * @param action    An object of the <code>Action<code/> class which gives information about the action triggered or performed
         * @param addedby   An object of the <code>User<code/> class which gives information about the user who added another user
         * @param userAdded An object of the <code>User<code/> class which gives information about the added user
         * @param addedTo   An object of the <code>Group<code/> class which gives information about the group in which user is added
         * @version <b>v2</b>
         * @see Action
         * @see User
         * @see Group
         * @since <b>v1</b>
         */
        public void onMemberAddedToGroup(Action action, User addedby, User userAdded, Group addedTo) {

        }
    }


    public abstract static class CallListener {

        /**
         * {@inheritDoc}
         * To receive incoming call events
         *
         * @param call An object of the <code>Call<code/> class gives information about incoming call
         * @version <b>v2</b>
         * @see Call
         * @since <b>v1</b>
         */
        public abstract void onIncomingCallReceived(Call call);

        /**
         * {@inheritDoc}
         * To receive outgoing call accepted events
         *
         * @param call An object of the <code>Call<code/> class gives information about outgoing call accepted
         * @version <b>v2</b>
         * @see Call
         * @since <b>v1</b>
         */
        public abstract void onOutgoingCallAccepted(Call call);

        /**
         * {@inheritDoc}
         * To receive outgoing call rejected events
         *
         * @param call An object of the <code>Call<code/> class gives information about outgoing call rejected
         * @version <b>v2</b>
         * @see Call
         * @since <b>v1</b>
         */
        public abstract void onOutgoingCallRejected(Call call);

        /**
         * {@inheritDoc}
         * To receive incoming call cancelled events
         *
         * @param call An object of the <code>Call<code/> class gives information about cancelled incoming call
         * @version <b>v2</b>
         * @see Call
         * @since <b>v1</b>
         */
        public abstract void onIncomingCallCancelled(Call call);

        /**
         * {@inheritDoc}
         * To receive end call events
         *
         * @param call An object of the <code>Call<code/> class gives information about ended call
         * @version <b>v2</b>
         * @see Call
         * @since <b>v1</b>
         */
        public void onCallEndedMessageReceived(Call call) {
        }
    }

    public abstract static class LoginListener {

        public abstract void loginSuccess(User user);

        public abstract void loginFailure(CometChatException ce);

        public abstract void logoutSuccess();

        public abstract void logoutFailure(CometChatException ce);
    }

    public static void addLoginListener(@NonNull String listenerId, @NonNull LoginListener
        loginListener) {
        if (listenerId != null && !TextUtils.isEmpty(listenerId) && loginListener != null)
            loginListeners.put(listenerId, loginListener);
    }

    public static void removeLoginListener(@NonNull String listenerId) {
        loginListeners.remove(listenerId);
    }

    public interface ConnectionListener {
        void onConnected();

        void onConnecting();

        void onDisconnected();

        void onFeatureThrottled();

        void onConnectionError(CometChatException error);
    }


    public interface OngoingCallListener {

        void onUserJoined(User user);

        void onUserLeft(User user);

        void onError(CometChatException ce);

        void onCallEnded(Call call);

        void onUserListUpdated(List<User> users);

        void onAudioModesUpdated(List<AudioMode> audioModes);

        void onRecordingStarted(User user);

        void onRecordingStopped(User user);

        void onUserMuted(User userMuted, User userMutedBy);

        void onCallSwitchedToVideo(String sessionId, User callSwitchInitiatedBy, User callSwitchAcceptedBy);
    }

    public abstract static class CallbackListener<T> {

        public abstract void onSuccess(T t);

        public abstract void onError(CometChatException e);

    }

    public abstract static class CreateGroupWithMembersListener {
        public abstract void onSuccess(Group group, HashMap<String, String> membersResult);

        public abstract void onError(CometChatException e);
    }

    public abstract static class ExtensionCallbackListener {

        public abstract void onSuccess(JSONObject successObject);

        public abstract void onError(JSONObject successObject);
    }


    static void handleException(String methodName, Exception
        e, HashMap<String, String> detailsMap) {
        try {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            detailsMap.put("method", methodName);
            detailsMap.put("stacktrace", exceptionAsString);
            if (PreferenceHelper.isInitialized) {
                if (PreferenceHelper.getAppID() != null)
                    detailsMap.put("appId", PreferenceHelper.getAppID());
                if (PreferenceHelper.getLoggedInUID() != null)
                    detailsMap.put("UID", PreferenceHelper.getLoggedInUID());
            }
            Logger.exception(TAG, detailsMap.toString());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private static void initLifecycleAware() {
        postOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
                        @Override
                        public void onCreate(@NonNull LifecycleOwner owner) {
                            Logger.error(TAG, "onCreate");
                        }

                        @Override
                        public void onStart(@NonNull LifecycleOwner owner) {
                            Logger.error(TAG, "onStart");
                            WSConnection.isAppInForeground.set(true);
                            if (appSettings.isAutoSocketConnectionEnabled()) {
                                ConnectionController.getInstance().connectDisconnectWithDelay(null, 0);
                            } else {
                                if (reconnectExecutorServiceManualMode != null)
                                    reconnectExecutorServiceManualMode.shutdownNow();
                                if (WSConnection.isConnectCalled.get()) {
                                    if (getConnectionStatus().equalsIgnoreCase(CometChatConstants.WS_STATE_CONNECTED)) {
                                        if (rttConnection != null) {
                                            rttConnection.startWebSocketPing();
                                        } else {
                                            Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                        }
                                        AnalyticsController.getInstance().enableAnalyticsPing(analyticsPingListener);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onResume(@NonNull LifecycleOwner owner) {
                            Logger.error(TAG, "onResume");
                        }

                        @Override
                        public void onPause(@NonNull LifecycleOwner owner) {
                            Logger.error(TAG, "onPause");
                        }

                        @Override
                        public void onStop(@NonNull LifecycleOwner owner) {
                            Logger.error(TAG, "onStop");
                            WSConnection.isAppInForeground.set(false);
                            if (appSettings.isAutoSocketConnectionEnabled()) {
                                if (getActiveCall() != null) {
                                    if (Objects.equals(getActiveCall().getCallStatus(), CometChatConstants.CALL_STATUS_INITIATED)) {
                                        callingService();
                                    } else {
                                        ConnectionController.getInstance().connectDisconnectWithDelay(null, 0);
                                        if (callingExecutorService != null) {
                                            callingExecutorService.shutdownNow();
                                            callingExecutorService = null;
                                        }
                                    }
                                } else {
                                    ConnectionController.getInstance().connectDisconnectWithDelay(null, 0);
                                }
                            } else {
                                if (WSConnection.isConnectCalled.get()) {
                                    if (rttConnection != null) {
                                        rttConnection.stopPing();
                                    } else {
                                        Logger.error(TAG, CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE);
                                    }
                                    AnalyticsController.getInstance().logout();
                                    manualModeConnectionManager();
                                }
                            }
                        }

                        @Override
                        public void onDestroy(@NonNull LifecycleOwner owner) {
                            Logger.error(TAG, "onDestroy");
                        }
                    });
                } catch (Exception e) {
                    Logger.error(TAG, "onException: initLifecycleAware: " + e);
                }
            }
        });

    }

    public static void ping(@NonNull CallbackListener<String> listener) {
        try {
            if (getLoggedInUser() == null) {
                Logger.error(TAG, "Error: ping() -> user not logged in");
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN,
                                                        CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
            } else if (rttConnection == null) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_RTT_CONNECTION,
                                                        CometChatConstants.Errors.ERROR_RTT_CONNECTION_MESSAGE));
            } else if (getConnectionStatus().equals(CometChatConstants.WS_STATE_DISCONNECTED)) {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_NO_WEBSOCKET_CONNECTION,
                                                        CometChatConstants.Errors.ERROR_NO_WEBSOCKET_CONNECTION_MESSAGE));
            } else {
                rttConnection.ping(false, listener);
            }
        } catch (Exception e) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
        }
    }

    private static void callingService() {
        try {
            if (callingExecutorService == null) {
                callingExecutorService = Executors.newScheduledThreadPool(1);
            }
            callingExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        disconnect();
                        callingExecutorService.shutdownNow();
                        callingExecutorService = null;
                    } catch (Exception e) {
                        Logger.error(TAG, "Error: " + e);
                    }
                }
            }, 30, 30, TimeUnit.SECONDS);
        } catch (Exception e) {
            Logger.error(TAG, "onException: callingService: " + e);
        }
    }

    private static void manualModeConnectionManager() {
        try {
            if (reconnectExecutorServiceManualMode == null) {
                reconnectExecutorServiceManualMode = Executors.newScheduledThreadPool(1);
            }
            reconnectExecutorServiceManualMode.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        Logger.error(TAG, "Manual Pong Check " + Thread.currentThread().getName());
                        if (WSConnection.receivedPongFlag) {
                            WSConnection.receivedPongFlag = false;
                        } else {
                            disconnect();
                            reconnectExecutorServiceManualMode.shutdownNow();
                            reconnectExecutorServiceManualMode = null;
                        }
                    } catch (Exception e) {
                        Logger.error(TAG, "Error: " + e);
                    }
                }
            }, 0, 30, TimeUnit.SECONDS);
        } catch (Exception e) {
            Logger.error(TAG, "onException: manualModeConnectionManager: " + e);
        }
    }

    public static void clearActiveCall() {
        if (getActiveCall() != null) {
            CallManager.getInstance().onCallEnded();
        }
    }

    /**
     * This method will call an API that will mark the message as delivered
     */
    private static void markAsDeliveredInternal(MessageReceipt messageReceipt, @Nullable final CallbackListener<Void> listener) {
        try {
            ApiConnection.getInstance().markAsDelivered(messageReceipt, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) listener.onError(ce);
                                }
                            });
                        } else {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) listener.onSuccess(null);
                                }
                            });
                        }
                    } catch (final Exception e) {
                        Logger.error(TAG, "Error: markAsDeliveredInternal: " + e.getMessage());
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null)
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Logger.error(TAG, "onException: markAsDeliveredInternal: " + e);
        }
    }

    /**
     * This method will call an API that will mark the message as read
     */
    private static void markAsReadInternal(MessageReceipt messageReceipt, @Nullable final CallbackListener<Void> listener) {
        try {
            ApiConnection.getInstance().markAsRead(messageReceipt, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) listener.onError(ce);
                                }
                            });
                        } else {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) listener.onSuccess(null);
                                }
                            });
                        }
                    } catch (final Exception e) {
                        Logger.error(TAG, "Error: markAsReadInternal: " + e.getMessage());
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null)
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Logger.error(TAG, "onException: markAsReadInternal: " + e);
        }
    }

    private static void markAsInteractedInternal(long messageId, JSONArray elementId, @Nullable final CallbackListener<Void> listener) {
        try {
            ApiConnection.getInstance().markAsInteracted(messageId, elementId, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) listener.onError(ce);
                                }
                            });
                        } else {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) listener.onSuccess(null);
                                }
                            });
                        }
                    } catch (final Exception e) {
                        Logger.error(TAG, "Error: markAsReadInternal: " + e.getMessage());
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null)
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Logger.error(TAG, "onException: markAsInteractedInternal: " + e);
        }
    }

    private static void callSdkIdentificationApi() {
        try {
            JSONObject hashJson = ApiConnection.getInstance().getSessionIdHashJson();
            String hashString = "";
            if (hashJson != null) {
                hashString = hashJson.toString();
            }
            if (!PreferenceHelper.getSDKSessionHash().equals(CometChatUtils.generateHash(hashString)) || hashString.isEmpty()) {
                ApiConnection.getInstance().sdkIdentification(new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, CometChatException ce) {
                        if (ce != null) {
                            Logger.error(TAG, "onError: callSdkIdentificationApi >> " + ce);
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("data")) {
                                    if (jsonObject.getJSONObject("data").has("sessionId")) {
                                        PreferenceHelper.saveSDKSessionHash(jsonObject.getJSONObject("data").getString("sessionId"));
                                    }
                                }
                            } catch (Exception e) {
                                Logger.error(TAG, e.toString());
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Logger.error(TAG, "Error: " + e);
        }
    }
    /**
     * @deprecated This method is deprecated.
     * Use {@link #markMessageAsUnread(BaseMessage, CallbackListener)} instead.
     */
    @Deprecated
    public static void markAsUnread(@NonNull BaseMessage message, @NonNull final CallbackListener<Void> listener) {
        try {
            MessageReceipt messageReceipt = new MessageReceipt();
            messageReceipt.setMessageId(message.getId());
            messageReceipt.setReceiverType(message.getReceiverType() + "s");
            String receiverUID;
            if (message.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                receiverUID = message.getReceiverUid();
            } else {
                if (message.getSender().getUid().equalsIgnoreCase(getLoggedInUser().getUid())) {
                    receiverUID = message.getReceiverUid();
                } else {
                    receiverUID = message.getSender().getUid();
                }
            }
            messageReceipt.setReceiverId(receiverUID);
            markAsUnreadInternal(messageReceipt, listener);
        } catch (Exception e) {
            Logger.error(TAG, "onError: markAsUnreadWithListener: " + e);
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            listener.onError(uncaughtException);
        }
    }

    /*** A private common method which will handle the mark as unread api call* */
    private static void markAsUnreadInternal(@NonNull MessageReceipt messageReceipt, @Nullable final CallbackListener<Void> listener) {
        try {
            ApiConnection.getInstance().markAsUnread(messageReceipt, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    if (ce != null) {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) listener.onError(ce);
                            }
                        });
                    } else {
                        CometChat.postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) listener.onSuccess(null);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Logger.error(TAG, "Error: markAsUnreadApiCall: " + e);
            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
            if (listener != null) listener.onError(uncaughtException);
        }
    }

    /**
     * Adds a reaction to a specific message.
     * <p>
     * This method takes a messageId, a reaction string, and a callback listener as input.
     *
     * @param messageId The ID of the message to add a reaction to.
     * @param reaction  The reaction that needs to be added to the message.
     * @param listener  A callback listener that gets called on success or error of the operation.
     * @throws CometChatException If an invalid messageId or reaction is passed, or if an exception occurs while processing the API call.
     * @version <b>v4</b>
     * @see BaseMessage
     * @since <b>v4</b>
     */
    public static void addReaction(long messageId, String reaction, @NonNull final CallbackListener<BaseMessage> listener) {
        if (messageId == 0) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_ID,
                                                    CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
        } else if (reaction == null) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_REACTION,
                                                    CometChatConstants.Errors.ERROR_INVALID_REACTION_MESSAGE));
        } else {
            ApiConnection.getInstance().addReaction(messageId, reaction, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            final BaseMessage baseMessage = BaseMessage.processMessage(dataObject);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(baseMessage);
                                }
                            });
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Remove a reaction to a specific message.
     * <p>
     * This method takes a messageId, a reaction string, and a callback listener as input.
     *
     * @param messageId The ID of the message to add a reaction to.
     * @param reaction  The reaction that needs to be added to the message.
     * @param listener  A callback listener that gets called on success or error of the operation.
     * @throws CometChatException If an invalid messageId or reaction is passed, or if an exception occurs while processing the API call.
     * @version <b>v4</b>
     * @see BaseMessage
     * @since <b>v4</b>
     */
    public static void removeReaction(long messageId, String reaction, @NonNull final CallbackListener<BaseMessage> listener) {
        if (messageId == 0) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_ID,
                                                    CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE));
        } else if (reaction == null) {
            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_REACTION,
                                                    CometChatConstants.Errors.ERROR_INVALID_REACTION_MESSAGE));
        } else {
            ApiConnection.getInstance().removeReaction(messageId, reaction, new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, final CometChatException ce) {
                    try {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            final BaseMessage baseMessage = BaseMessage.processMessage(dataObject);
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccess(baseMessage);
                                }
                            });
                        }
                    } catch (Exception e) {
                        final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION,
                                                                                            e.getMessage());
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(uncaughtException);
                            }
                        });
                    }
                }
            });
        }
    }


    /**
     * Retrieves the ConversationUpdateSettings.
     *
     * @return A ConversationUpdateSettings object that holds the settings for updating the conversation.
     */
    public static ConversationUpdateSettings getConversationUpdateSettings() {
        return ConversationUpdateSettingsRepo.getConversationUpdateSettings();
    }

    // region Notification Feed Methods

    /**
     * Register a listener for real-time notification feed events.
     * The listener will receive callbacks when new feed items arrive via WebSocket.
     * Independent from MessageListener, GroupListener, and CallListener.
     *
     * @param listenerId Unique identifier for this listener
     * @param listener The NotificationFeedListener implementation
     * @since v4
     */
    public static void addNotificationFeedListener(@NonNull String listenerId, @NonNull NotificationFeedListener listener) {
        if (!TextUtils.isEmpty(listenerId)) {
            notificationFeedListeners.put(listenerId, listener);
        }
    }

    /**
     * Remove a previously registered notification feed listener.
     *
     * @param listenerId The unique identifier used during registration
     * @since v4
     */
    public static void removeNotificationFeedListener(@NonNull String listenerId) {
        if (listenerId != null && !TextUtils.isEmpty(listenerId)) {
            notificationFeedListeners.remove(listenerId);
        }
    }

    /**
     * Internal method called by DispatchController to dispatch feed items to registered listeners.
     * Dispatches on main thread.
     *
     * @param feedItem The received NotificationFeedItem
     */
    static void dispatchNotificationFeedItem(final NotificationFeedItem feedItem) {
        Iterator<Map.Entry<String, NotificationFeedListener>> it = notificationFeedListeners.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<String, NotificationFeedListener> pair = it.next();
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    pair.getValue().onFeedItemReceived(feedItem);
                }
            });
        }
    }

    /**
     * Mark a single notification feed item as delivered.
     * Hits: POST /v3.0/campaigns/notification-feed/{id}/delivered
     * Idempotent — safe to call multiple times.
     *
     * @param feedItem The feed item to mark as delivered
     * @param listener Callback listener
     * @since v4
     */
    public static void markFeedItemAsDelivered(@NonNull NotificationFeedItem feedItem, @NonNull final CallbackListener<Void> listener) {
        final String methodName = new Throwable().getStackTrace()[0].getMethodName();
        if (feedItem.getId() == null || feedItem.getId().isEmpty()) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException("ERROR_INVALID_FEED_ITEM", "Feed item ID cannot be null or empty"));
                }
            });
            return;
        }
        ApiConnection.getInstance().markFeedItemAsDelivered(feedItem.getId(), new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                if (ce != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(ce);
                        }
                    });
                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(null);
                        }
                    });
                }
            }
        });
    }

    /**
     * Mark multiple notification feed items as delivered (batch).
     * Idempotent — safe to call multiple times.
     *
     * @param feedItems List of feed items to mark as delivered
     * @param listener Callback listener
     * @since v4
     */
    public static void markFeedItemsAsDelivered(@NonNull final List<NotificationFeedItem> feedItems, @NonNull final CallbackListener<Void> listener) {
        if (feedItems.isEmpty()) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess(null);
                }
            });
            return;
        }
        // Mark each item individually
        final int[] remaining = {feedItems.size()};
        final CometChatException[] firstError = {null};
        for (NotificationFeedItem item : feedItems) {
            if (item.getId() == null || item.getId().isEmpty()) {
                synchronized (remaining) {
                    remaining[0]--;
                    if (remaining[0] == 0) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstError[0] != null) listener.onError(firstError[0]);
                                else listener.onSuccess(null);
                            }
                        });
                    }
                }
                continue;
            }
            ApiConnection.getInstance().markFeedItemAsDelivered(item.getId(), new ApiConnection.APIConnectionListener() {
                @Override
                public void onResponse(String response, CometChatException ce) {
                    if (ce != null && firstError[0] == null) {
                        firstError[0] = ce;
                    }
                    synchronized (remaining) {
                        remaining[0]--;
                        if (remaining[0] == 0) {
                            postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (firstError[0] != null) listener.onError(firstError[0]);
                                    else listener.onSuccess(null);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    /**
     * Mark a single notification feed item as read.
     * Hits: POST /v3.0/campaigns/notification-feed/{id}/read
     * Idempotent — safe to call multiple times.
     *
     * @param feedItem The feed item to mark as read
     * @param listener Callback listener
     * @since v4
     */
    public static void markFeedItemAsRead(@NonNull NotificationFeedItem feedItem, @NonNull final CallbackListener<Void> listener) {
        if (feedItem.getId() == null || feedItem.getId().isEmpty()) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException("ERROR_INVALID_FEED_ITEM", "Feed item ID cannot be null or empty"));
                }
            });
            return;
        }
        ApiConnection.getInstance().markFeedItemAsRead(feedItem.getId(), new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                if (ce != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(ce);
                        }
                    });
                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(null);
                        }
                    });
                }
            }
        });
    }

    /**
     * Report engagement on a notification feed item.
     * Hits: POST /v3.0/campaigns/notification-feed/{id}/engagement
     * Idempotent — safe to call multiple times.
     *
     * @param feedItem The feed item to report engagement on
     * @param interactionString The interaction topic string (e.g., "viewed", "clicked", "interacted")
     * @param listener Callback listener
     * @since v4
     */
    public static void reportFeedEngagement(@NonNull NotificationFeedItem feedItem, @NonNull String interactionString, @NonNull final CallbackListener<Void> listener) {
        if (feedItem.getId() == null || feedItem.getId().isEmpty()) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException("ERROR_INVALID_FEED_ITEM", "Feed item ID cannot be null or empty"));
                }
            });
            return;
        }
        ApiConnection.getInstance().reportFeedEngagement(feedItem.getId(), interactionString, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                if (ce != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(ce);
                        }
                    });
                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(null);
                        }
                    });
                }
            }
        });
    }

    /**
     * Get the total unread count for notification feed items.
     * Hits: GET /v3.0/campaigns/notification-feed/unread-count
     *
     * @param listener Callback listener returning the unread count
     * @since v4
     */
    public static void getNotificationFeedUnreadCount(@NonNull final CallbackListener<Integer> listener) {
        ApiConnection.getInstance().getNotificationFeedUnreadCount(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                if (ce != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(ce);
                        }
                    });
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int count = 0;
                        if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                            JSONObject data = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                            if (data.has(CometChatConstants.NotificationFeedKeys.KEY_COUNT)) {
                                count = data.getInt(CometChatConstants.NotificationFeedKeys.KEY_COUNT);
                            }
                        }
                        final int finalCount = count;
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(finalCount);
                            }
                        });
                    } catch (final Exception e) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Fetch a single notification feed item by ID (for deep linking).
     * Hits: GET /v3/announcements/{id}
     *
     * @param id The feed item ID
     * @param listener Callback listener returning the NotificationFeedItem
     * @since v4
     */
    public static void getNotificationFeedItem(@NonNull final String id, @NonNull final CallbackListener<NotificationFeedItem> listener) {
        if (id.isEmpty()) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException("ERROR_INVALID_ID", "Feed item ID cannot be empty"));
                }
            });
            return;
        }
        ApiConnection.getInstance().getNotificationFeedItem(id, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                if (ce != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(ce);
                        }
                    });
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject data = jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)
                                ? jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA)
                                : jsonObject;
                        final NotificationFeedItem item = NotificationFeedItem.fromJson(data);
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(item);
                            }
                        });
                    } catch (final Exception e) {
                        postOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Mark a push notification as delivered.
     * Hits: PUT /v3.0/campaigns/push-notifications/{id}/delivered
     * Idempotent — safe to call multiple times.
     *
     * @param pushNotification The push notification object from the Push SDK
     * @param listener Callback listener
     * @since v4
     */
    public static void markPushNotificationDelivered(@NonNull PushNotification pushNotification, @NonNull final CallbackListener<Void> listener) {
        String id = pushNotification.getId();
        if (id == null || id.isEmpty()) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException("ERROR_INVALID_PUSH_NOTIFICATION", "Push notification ID cannot be null or empty"));
                }
            });
            return;
        }
        ApiConnection.getInstance().markPushNotificationDelivered(id, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                if (ce != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(ce);
                        }
                    });
                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(null);
                        }
                    });
                }
            }
        });
    }

    /**
     * Mark a push notification as clicked.
     * Hits: PUT /v3.0/campaigns/push-notifications/{id}/clicked
     * Idempotent — safe to call multiple times.
     *
     * @param pushNotification The push notification object from the Push SDK
     * @param listener Callback listener
     * @since v4
     */
    public static void markPushNotificationClicked(@NonNull PushNotification pushNotification, @NonNull final CallbackListener<Void> listener) {
        String id = pushNotification.getId();
        if (id == null || id.isEmpty()) {
            postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException("ERROR_INVALID_PUSH_NOTIFICATION", "Push notification ID cannot be null or empty"));
                }
            });
            return;
        }
        ApiConnection.getInstance().markPushNotificationClicked(id, new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, final CometChatException ce) {
                if (ce != null) {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(ce);
                        }
                    });
                } else {
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(null);
                        }
                    });
                }
            }
        });
    }

    // endregion
}
