package com.cometchat.chat.core;

import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReconnectionController {

    private static final String TAG = ReconnectionController.class.getSimpleName();
    private static final int RECONNECT_INTERVAL = 5 * 1000; // number of seconds * 1000 milliseconds
    private static ReconnectionController reconnectionControllerInstance;
    private final AtomicBoolean isReconnectionInProgress = new AtomicBoolean(false);

    public synchronized static ReconnectionController getInstance() {
        if (reconnectionControllerInstance == null) {
            reconnectionControllerInstance = new ReconnectionController();
        }
        return reconnectionControllerInstance;
    }

    private ReconnectionController() {

    }

    synchronized void startReconnection() {
        // Check if reconnection is already in progress
        if (!isReconnectionInProgress.compareAndSet(false, true)) {
            Logger.error(TAG, "Reconnection already in progress, skipping duplicate request");
            return;
        }

        Logger.error(TAG, "Starting Reconnection");
        ConnectionController.getInstance().connectDisconnectWithDelay(new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Logger.error(TAG, "Reconnection attempt completed successfully");
                isReconnectionInProgress.set(false);
            }

            @Override
            public void onError(CometChatException e) {
                Logger.error(TAG, "Reconnection attempt failed: " + e.getMessage());
                isReconnectionInProgress.set(false);
            }
        }, RECONNECT_INTERVAL);
    }

    synchronized void stopReconnection() {
        Logger.error(TAG, "Stopping Reconnection clearing the Queue");
        isReconnectionInProgress.set(false);
        ConnectionController.getInstance().clearMethodQueue();
    }
}
