package com.cometchat.chat.core;

import java.util.TimerTask;

class DisconnectionTimerTask extends TimerTask {

    DisconnectionListener disconnectionListener;

    DisconnectionTimerTask(DisconnectionListener disconnectionListener){
        this.disconnectionListener = disconnectionListener;
    }

    @Override
    public void run() {
        if(disconnectionListener!=null)
            disconnectionListener.onDisconnectionTimeReached();
    }

    interface DisconnectionListener {
        void onDisconnectionTimeReached();
    }
}
