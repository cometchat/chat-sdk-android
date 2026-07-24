package com.cometchat.chat.core;

import com.cometchat.chat.helpers.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class PingController {

    private static final String TAG = PingController.class.getSimpleName();
    private static final int PING_INTERVAL = 15 * 1000; // number of seconds * 1000 milliseconds
    private static PingController pingControllerInstance;
    private ScheduledExecutorService pingExecutorService;
    private AbstractRTTConnection connection;
    private AtomicBoolean isPingInProgress = new AtomicBoolean();
    private AbstractRTTConnection.WSListener wsListener = new AbstractRTTConnection.WSListener() {
        @Override
        public void onMessage(CometChatEvent cometChatMessageEvent) {

        }
    };

    boolean isPingInProgress() {
        return isPingInProgress.get();
    }

    static PingController getInstance(AbstractRTTConnection connection) {
        if (pingControllerInstance == null)
            pingControllerInstance = new PingController(connection);
        return pingControllerInstance;

    }

    private PingController(AbstractRTTConnection connection) {
        this.connection = connection;
        pingExecutorService = Executors.newScheduledThreadPool(1);
        isPingInProgress.set(false);
        this.connection.addWSListener(TAG, wsListener);
    }


    synchronized void startPing() {
        if(!isPingInProgress.get()) {
            isPingInProgress.set(true);
            Logger.error(TAG, "Starting Ping");
            pingExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (connection != null) {
                            connection.ping(true, null);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, 0 ,PING_INTERVAL, TimeUnit.MILLISECONDS);
        }else{
            Logger.error(TAG, "Pinging in progress..!!!");
        }
    }

    synchronized void stopPing() {
        try {
            if(isPingInProgress()) {
                if (pingExecutorService != null) {
                    pingExecutorService.shutdownNow();
                    connection.removeWSListener(TAG);
                }
            }else{
                Logger.error(TAG, "WS Ping not started");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            isPingInProgress.set(false);
            pingControllerInstance = null;
        }
    }

}
