package com.cometchat.chat.core;

import android.os.Handler;
import android.os.Looper;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;

import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        final MethodCompletion completion = new MethodCompletion("connect/disconnect", callback, delay);
        enqueueMethod(new Runnable() {
            @Override
            public void run() {
                Logger.error(TAG, "Executing connect/disconnect with delay...");
                sdkConnectDisconnect(completion);
            }
        }, delay, completion);
    }

    void connect(final CometChat.CallbackListener<String> callback, final boolean isDeveloperCall) {
        final MethodCompletion completion =
            new MethodCompletion("connect", callback, DEFAULT_METHOD_QUEUING_DELAY);
        enqueueMethod(new Runnable() {
            @Override
            public void run() {
                Logger.error(TAG, "Executing connect...");
                CometChat.connectInternal(completion, isDeveloperCall);
            }
        }, DEFAULT_METHOD_QUEUING_DELAY, completion);
    }

    void disconnect(final CometChat.CallbackListener<String> callback, final boolean isDeveloperCall) {
        final MethodCompletion completion =
            new MethodCompletion("disconnect", callback, DEFAULT_METHOD_QUEUING_DELAY);
        enqueueMethod(new Runnable() {
            @Override
            public void run() {
                Logger.error(TAG, "Executing disconnect...");
                CometChat.disconnectInternal(completion, isDeveloperCall);
            }
        }, DEFAULT_METHOD_QUEUING_DELAY, completion);
    }

    /**
     * Runs the connect or disconnect that matches the app's current foreground state.
     *
     * Queue advancement and one-shot delivery belong to the {@link MethodCompletion} the caller
     * passes in, so this only chooses which call to make.
     *
     * @param completion the single completion for the queued method
     */
    void sdkConnectDisconnect(final MethodCompletion completion) {
        if (WSConnection.isAppInForeground.get()) {
            Logger.error(TAG, "sdk connect/disconnect: connecting, app is in foreground");
            CometChat.connectInternal(completion, false);
        } else {
            Logger.error(TAG, "sdk connect/disconnect: disconnecting, app is in background");
            CometChat.disconnectInternal(completion, false);
        }
    }

    void enqueueMethod(final Runnable method, final long delay) {
        enqueueMethod(method, delay, null);
    }

    void enqueueMethod(final Runnable method, final long delay, @Nullable final MethodCompletion completion) {
        synchronized (lock) {
            final Runnable delayedTask = new Runnable() {
                @Override
                public void run() {
                    method.run();
                }
            };

            // Create a CallbackRunnable with the task and its completion
            CallbackRunnable callbackRunnable = new CallbackRunnable(delayedTask, completion);
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
                final long timeoutDelay = watchdogDelay(forceCompleteDelay);

                timeoutRunnable = new Runnable() {
                    @Override
                    public void run() {
                        MethodCompletion completion = nextMethod.getCompletion();
                        if (completion != null) {
                            Logger.error(TAG, "Execution timeout. Forcing onMethodComplete()");
                            // The completion advances the queue as part of the one result it
                            // delivers, so advancing it again here would pop a second method.
                            completion.onError(new CometChatException(
                                CometChatConstants.Errors.ERR_METHOD_TIMEOUT,
                                "Queue task execution timed out"
                            ));
                        } else {
                            onMethodComplete(forceCompleteDelay);
                        }
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

    /**
     * How long after being dequeued a method's watchdog fires.
     *
     * The method itself is posted {@code startDelay} ms out, so the watchdog has to be armed
     * relative to that start rather than to the moment of queuing. Reusing {@code startDelay} as
     * the deadline — as this once did for any non-zero delay — put the deadline at the instant
     * the method began, so the watchdog always won and the real result could never be delivered.
     *
     * @param startDelay delay before the queued method starts, in milliseconds
     * @return delay before the watchdog fires, always {@link #DEFAULT_FORCE_COMPLETE_DELAY} ms
     * after the method starts
     */
    static long watchdogDelay(long startDelay) {
        return startDelay + DEFAULT_FORCE_COMPLETE_DELAY;
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
        // Held strongly: if the completion could be collected, the watchdog would find none and
        // advance the queue itself, reopening the double advance the completion exists to prevent.
        @Nullable
        private final MethodCompletion completion;

        CallbackRunnable(Runnable task, @Nullable MethodCompletion completion) {
            this.task = task;
            this.completion = completion;
        }

        @Override
        public void run() {
            task.run();
        }

        @Nullable
        public MethodCompletion getCompletion() {
            return completion;
        }
    }

    /**
     * Delivers exactly one terminal result for a queued method.
     *
     * A queued method has two independent completers: the real SDK result, and the watchdog armed
     * in {@link #executeNext(long)} when the method overruns {@link #DEFAULT_FORCE_COMPLETE_DELAY}.
     * Neither knows about the other, so without a shared latch both could fire — handing the caller
     * two terminal callbacks for one call, which breaks any one-shot adapter built on top of the
     * listener, and advancing the method queue twice, which cancels the next method's watchdog and
     * starts the one after it early.
     *
     * The first completer wins: it advances the queue and forwards to the caller. Later ones are
     * dropped. Advancing the queue is part of that single completion, so a late result can no
     * longer pop a method that is still running.
     */
    class MethodCompletion extends CometChat.CallbackListener<String> {
        private final String methodName;
        private final CometChat.CallbackListener<String> target;
        private final long completionDelay;
        private final AtomicBoolean completed = new AtomicBoolean(false);

        MethodCompletion(String methodName,
                         @Nullable CometChat.CallbackListener<String> target,
                         long completionDelay) {
            this.methodName = methodName;
            this.target = target;
            this.completionDelay = completionDelay;
        }

        @Override
        public void onSuccess(String s) {
            if (!completed.compareAndSet(false, true)) {
                Logger.error(TAG, "Dropping late success for an already completed " + methodName + ": " + s);
                return;
            }
            Logger.error(TAG, "sdk " + methodName + " Success: " + s);
            onMethodComplete(completionDelay);
            if (target != null) {
                target.onSuccess(s);
            }
        }

        @Override
        public void onError(CometChatException e) {
            if (!completed.compareAndSet(false, true)) {
                Logger.error(TAG, "Dropping late error for an already completed " + methodName + ": " + e);
                return;
            }
            Logger.error(TAG, "sdk " + methodName + " Error: " + e);
            onMethodComplete(completionDelay);
            if (target != null) {
                target.onError(e);
            }
        }
    }
}
