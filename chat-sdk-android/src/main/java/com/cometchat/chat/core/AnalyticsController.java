package com.cometchat.chat.core;

import com.cometchat.chat.helpers.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnalyticsController {

    private static final String TAG = "AnalyticsController";

    private static final int ANALYTICS_PING_INTERVAL = 60000; // 60 * 1000 milliseconds
    private static AnalyticsController analyticsControllerInstance;
    private final ScheduledExecutorService analyticsPingExecutorService;
    private final AtomicBoolean isAnalyticsPingInProgress = new AtomicBoolean();

    public static AnalyticsController getInstance() {
        if (analyticsControllerInstance == null)
            analyticsControllerInstance = new AnalyticsController();
        return analyticsControllerInstance;
    }

    private AnalyticsController() {
        isAnalyticsPingInProgress.set(false);
        analyticsPingExecutorService = Executors.newScheduledThreadPool(1);
    }


    public void enableAnalyticsPing(AnalyticsPingListener analyticsPingListener) {
        if(!isAnalyticsPingInProgress.get()) {
            isAnalyticsPingInProgress.set(true);
            AnalyticsRunnable analyticsRunnable = new AnalyticsRunnable(analyticsPingListener);
            analyticsPingExecutorService.scheduleAtFixedRate(analyticsRunnable, 0, ANALYTICS_PING_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    public void logout() {
        try {
            if(isAnalyticsPingInProgress.get()) {
                if (analyticsPingExecutorService != null) {
                    analyticsPingExecutorService.shutdownNow();
                }
            }else{
                Logger.error(TAG, "Analytics Ping not started");
            }
        }catch (Exception e){
            Logger.error(TAG, "Analytics Logout Error: " + e);
        }finally {
            isAnalyticsPingInProgress.set(false);
            analyticsControllerInstance = null;
        }
    }

    interface AnalyticsPingListener {

        void onAuthExpired();

        void onSettingsUpdated();
    }

}
