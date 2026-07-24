package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;

class AnalyticsRunnable implements Runnable {

    private static final String TAG = "AnalyticsPingTimerTask";
    private AnalyticsController.AnalyticsPingListener analyticsPingListener;

    AnalyticsRunnable(AnalyticsController.AnalyticsPingListener analyticsPingListener) {
        this.analyticsPingListener = analyticsPingListener;
    }

    @Override
    public void run() {
        ApiConnection.getInstance().pingAnalytics(new ApiConnection.APIConnectionListener() {
            @Override
            public void onResponse(String response, CometChatException ce) {
                if (ce != null) {
                    if (ce.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERR_SETTINGS_HASH_OUTDATED)) {
                        analyticsPingListener.onSettingsUpdated();
                    } else if (ce.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERR_NO_AUTH)) {
                        analyticsPingListener.onAuthExpired();
                    }
                } else {
                    Logger.error(TAG, "Analytics Ping Successful : " + response);
                }
            }
        });

    }
}
