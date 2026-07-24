package com.cometchat.chat.core;

import android.os.Handler;
import android.os.Looper;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;

import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.ref.WeakReference;

class ConnectionController {

    private static final String TAG = "ConnectionController";
    private static final int WS_STATE_DISCONNECTED = 0;
    private static final int WS_STATE_CONNECTING = 1;
    private static final int WS_STATE_CONNECTED = 2;
    private static final int WS_STATE_FEATURE_THROTTLED = 3;
    private static final int WS_STATE_ERROR = 4;
    private static final long DEFAULT_FORCE_COMPLETE_DELAY = 5000;
    private static final long DEFAULT_METHOD_QUEUING_DELAY = 0;
    private static volatile ConnectionController connectionControllerInstance;
    private final AtomicInteger wsConnectionState;
    private final Object lock;
    private volatile boolean isExecuting;
    private final Queue<CallbackRunnable> methodQueue;
    private final Handler mainHandler;
    private Runnable timeoutRunnable;

    static ConnectionController getInstance() {
        if (connectionControllerInstance == null) {
            synchronized (ConnectionController.class) {
                if (connectionControllerInstance == null) {
                    connectionControllerInstance = new ConnectionController();
                }
            }
        }
        return connectionControllerInstance;
    }

    private ConnectionController() {
        wsConnectionState = new AtomicInteger(WS_STATE_DISCONNECTED);
        lock = new Object();
        isExecuting = false;
        methodQueue = new LinkedList<>();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    void onWSConnected() {
        Logger.debug(TAG, "onWSConnected : ");
        int currentState = wsConnectionState.get();
        if (currentState != WS_STATE_CONNECTED &&
            wsConnectionState.compareAndSet(currentState, WS_STATE_CONNECTED)) {
            informConnectionListener(WS_STATE_CONNECTED, null);
        }
    }

    void onWSDisconnected(@Nullable CometChatException error) {
        int currentState = wsConnectionState.get();
        if (currentState != WS_STATE_DISCONNECTED &&
            currentState != WS_STATE_FEATURE_THROTTLED &&
            currentState != WS_STATE_CONNECTING &&
            wsConnectionState.compareAndSet(currentState, WS_STATE_DISCONNECTED)) {
            Logger.debug(TAG, "onWSDisconnected :");
            logout();
            informConnectionListener(WS_STATE_DISCONNECTED, null);
        }

        if (error != null) {
            informConnectionListener(WS_STATE_ERROR, error);
        }
    }

    void onWSConnecting() {
        int currentState = wsConnectionState.get();
        if (currentState != WS_STATE_CONNECTING &&
            currentState != WS_STATE_FEATURE_THROTTLED &&
            wsConnectionState.compareAndSet(currentState, WS_STATE_CONNECTING)) {
            Logger.debug(TAG, "onWSConnecting :");
            informConnectionListener(WS_STATE_CONNECTING, null);
        }
    }

    void onErrorConnectingWS(CometChatException ce) {
        Logger.debug(TAG, "onErrorConnectingWS : " + ce.getCode() + " " + ce.getMessage());
    }

    private void logout() {
        wsConnectionState.set(WS_STATE_DISCONNECTED);
    }

    synchronized String getConnectionStatus() {
        switch (wsConnectionState.get()) {
            case WS_STATE_CONNECTED:
                return CometChatConstants.WS_STATE_CONNECTED;
            case WS_STATE_CONNECTING:
                return CometChatConstants.WS_STATE_CONNECTING;
            case WS_STATE_FEATURE_THROTTLED:
                return CometChatConstants.WS_STATE_FEATURE_THROTTLED;
            case WS_STATE_DISCONNECTED:
            default:
                return CometChatConstants.WS_STATE_DISCONNECTED;
        }
    }

    private void informConnectionListener(int connectionState, @Nullable CometChatException error) {
        wsConnectionState.set(connectionState);
        if (error != null) {
            DispatchController.getInstance().informConnectionListener(CometChatConstants.WS_STATE_ERROR, error);
        } else {
            Logger.error(TAG, "informConnectionListener : " + getConnectionStatus() + " " + connectionState);
            DispatchController.getInstance().informConnectionListener(getConnectionStatus(), null);
        }
    }

    void connectDisconnectWithDelay(final CometChat.CallbackListener<String> callback, final long delay) {
        enqueueMethod(new Runnable() {
            @Override
            public void run() {
                Logger.error(TAG, "Executing connect/disconnect with delay...");
                sdkConnectDisconnect(callback, delay);
            }
        }, delay, callback);
    }

    void connect(final CometChat.CallbackListener<String> callback, final boolean isDeveloperCall) {
        enqueueMethod(new Runnable() {
            @Override
            public void run() {
                Logger.error(TAG, "Executing connect...");

                CometChat.connectInternal(new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Logger.error(TAG, "sdk Connect Success: " + s);
                        onMethodComplete(DEFAULT_METHOD_QUEUING_DELAY);
                        if (callback != null) {
                            callback.onSuccess(s);
                        }
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Logger.error(TAG, "sdk Connect Error: " + e);
                        onMethodComplete(DEFAULT_METHOD_QUEUING_DELAY);
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                }, isDeveloperCall);
            }
        }, DEFAULT_METHOD_QUEUING_DELAY, callback);
    }

    void disconnect(final CometChat.CallbackListener<String> callback, final boolean isDeveloperCall) {
        enqueueMethod(new Runnable() {
            @Override
            public void run() {
                Logger.error(TAG, "Executing disconnect...");

                CometChat.disconnectInternal(new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Logger.error(TAG, "sdk Disconnect Success: " + s);
                        onMethodComplete(DEFAULT_METHOD_QUEUING_DELAY);
                        if (callback != null) {
                            callback.onSuccess(s);
                        }
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Logger.error(TAG, "sdk Disconnect Error: " + e);
                        onMethodComplete(DEFAULT_METHOD_QUEUING_DELAY);
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                }, isDeveloperCall);
            }
        }, DEFAULT_METHOD_QUEUING_DELAY, callback);
    }

    void sdkConnectDisconnect(final CometChat.CallbackListener<String> callback, final long delay) {
        if (WSConnection.isAppInForeground.get()) {
            CometChat.connectInternal(new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                    Logger.error(TAG, "sdk Connect Success: " + s);
                    onMethodComplete(delay);
                    if (callback != null) {
                        callback.onSuccess(s);
                    }
                }

                @Override
                public void onError(CometChatException e) {
                    Logger.error(TAG, "sdk Connect Error: " + e);
                    onMethodComplete(delay);
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            },false);
        } else {
            CometChat.disconnectInternal(new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                    Logger.error(TAG, "sdk Disconnect Success: " + s);
                    onMethodComplete(delay);
                    if (callback != null) {
                        callback.onSuccess(s);
                    }
                }

                @Override
                public void onError(CometChatException e) {
                    Logger.error(TAG, "sdk Disconnect Error: " + e);
                    onMethodComplete(delay);
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            },false);
        }
    }

    void enqueueMethod(final Runnable method, final long delay) {
        enqueueMethod(method, delay, null);
    }

    void enqueueMethod(final Runnable method, final long delay, @Nullable final CometChat.CallbackListener<String> callback) {
        synchronized (lock) {
            final Runnable delayedTask = new Runnable() {
                @Override
                public void run() {
                    method.run();
                }
            };

            // Create a CallbackRunnable with the task and callback
            CallbackRunnable callbackRunnable = new CallbackRunnable(delayedTask, callback);
            methodQueue.add(callbackRunnable);

            if (!isExecuting) {
                isExecuting = true;
                executeNext(delay);
            }
        }
    }


    private void executeNext(final long forceCompleteDelay) {
        synchronized (lock) {
            final CallbackRunnable nextMethod = methodQueue.poll();
            if (nextMethod != null) {
                // Set a timeout to ensure the task doesn't block the queue indefinitely
                final long timeoutDelay = forceCompleteDelay == DEFAULT_METHOD_QUEUING_DELAY
                    ? DEFAULT_FORCE_COMPLETE_DELAY
                    : forceCompleteDelay;

                timeoutRunnable = new Runnable() {
                    @Override
                    public void run() {
                        CometChat.CallbackListener<String> callback = nextMethod.getCallback();
                        if (callback != null) {
                            Logger.error(TAG, "Execution timeout. Forcing onMethodComplete()");
                            callback.onError(new CometChatException(
                                CometChatConstants.Errors.ERR_METHOD_TIMEOUT,
                                "Queue task execution timed out"
                            ));
                        }

                        onMethodComplete(forceCompleteDelay);
                    }
                };

                mainHandler.postDelayed(timeoutRunnable, timeoutDelay);

                // If delay is needed, post with delay, otherwise post directly
                if (forceCompleteDelay > 0) {
                    mainHandler.postDelayed(nextMethod, forceCompleteDelay);
                } else {
                    mainHandler.post(nextMethod);
                }
            } else {
                isExecuting = false;
            }
        }
    }

    void clearMethodQueue() {
        synchronized (lock) {
            methodQueue.clear();
            isExecuting = false;
            if (timeoutRunnable != null) {
                mainHandler.removeCallbacks(timeoutRunnable);
                timeoutRunnable = null;
            }
            Logger.error(TAG, "Method queue cleared");
        }
    }

    private void onMethodComplete(long forceCompleteDelay) {
        synchronized (lock) {
            if (timeoutRunnable != null) {
                mainHandler.removeCallbacks(timeoutRunnable);
                timeoutRunnable = null;
            }
            executeNext(forceCompleteDelay);
        }
    }

    private static class CallbackRunnable implements Runnable {
        private final Runnable task;
        private final WeakReference<CometChat.CallbackListener<String>> callbackRef;

        CallbackRunnable(Runnable task, @Nullable CometChat.CallbackListener<String> callback) {
            this.task = task;
            this.callbackRef = callback != null ? new WeakReference<>(callback) : null;
        }

        @Override
        public void run() {
            task.run();
        }

        @Nullable
        public CometChat.CallbackListener<String> getCallback() {
            return callbackRef != null ? callbackRef.get() : null;
        }
    }
}