package com.cometchat.chat.core;

import android.app.Activity;

import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;
import com.cometchat.calls.CometChatRTCView;
import com.cometchat.calls.CometChatRTCViewListener;
import com.cometchat.calls.model.AnalyticsSettings;
import com.cometchat.calls.model.AudioMode;
import com.cometchat.calls.model.CallSwitchRequestInfo;
import com.cometchat.calls.model.MainVideoContainerSetting;
import com.cometchat.calls.model.RTCCallback;
import com.cometchat.calls.model.RTCMutedUser;
import com.cometchat.calls.model.RTCReceiver;
import com.cometchat.calls.model.RTCRecordingInfo;
import com.cometchat.calls.model.RTCUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;


/**
 * Created by adityagokula on 23/11/18.
 */

public class CallManager {

    private static final String TAG = CallManager.class.getSimpleName();
    static final int DEFAULT_TIMEOUT = 45;
    private int timeoutSeconds = DEFAULT_TIMEOUT;
    private static CallManager callManager;
    private static CallEventListener callEventListener = null;
    private static CallListener callListener = null;
    private Call call;
    private CallSettings callSettings;

    Call getInitialCall() {
        return initialCall;
    }

    CallSettings getCallSettings() {
        return callSettings;
    }

    private @Nullable ScheduledExecutorService threadExecutorService;

    private Call initialCall;
    private Activity cometChatVideoViewActivity = null;
    private RelativeLayout videoContainer;
    private boolean isTimerRunning = false;
    private boolean isConferenceJoined = false;
    private int numberOfAttempts = 0;
    private CometChatRTCView cometChatRTCView;
    private static final String CALL_MODULE_CLASS_COMETCHAT_RTC_VIEW = "com.cometchat.calls.CometChatRTCView";
    private static final String CALL_MODULE_CLASS_COMETCHAT_CALLS = "com.cometchat.calls.core.CometChatCalls";

    private CallManager() {
        isTimerRunning = false;
    }

    public static CallManager getInstance() {
        if (callManager == null) {
            callManager = new CallManager();
        }
        return callManager;
    }

    static void setCallEventListener(CallEventListener _callEventListener) {
        callEventListener = _callEventListener;
    }

    static void removeCallEventListener() {
        callEventListener = null;
    }

    private void startCallSession() {
    }

    private void endCallSession() {

    }

    void initiateCall(Call _call, int timeoutSeconds) {
        Logger.error(TAG, "initiateCall");
        call = _call;
        initialCall = _call;
        this.timeoutSeconds = timeoutSeconds;
        startCallTimer();
    }

    void joinCall(Call _call) {
        Logger.error(TAG, "joinCall");
        call = _call;
        initialCall = _call;
    }

    void cancelCall() {
        Logger.error(TAG, "cancelCall");
        stopCallTimer();
        destroy();
    }

    void rejectCall() {
        Logger.error(TAG, "rejectCall");
        destroy();
    }

    void unanswerCall() {
        Logger.error(TAG, "unanswerCall");
        destroy();
    }

    void sendBusyResponse() {
        Logger.error(TAG, "sendBusyResponse");
    }

    void endCall(boolean isInternal) {
        Logger.error(TAG, "endCall");
        if (call != null && call.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP)) {
            if (cometChatRTCView != null) {
                Logger.error(TAG, "Video view disposed external");
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        disposeView();
                    }
                });
            }
            destroy();
        } else {
            if (!isInternal) {
                if (cometChatRTCView != null) {
                    Logger.error(TAG, "Video view disposed external");
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            disposeView();
                        }
                    });
                }
            }
            destroy();
        }
    }

    void onCallInitiated() {
        Logger.error(TAG, "onCallInitiated");
    }

    void onCallJoined(Call call) {
        Logger.error(TAG, "onCallOngoing");
        this.call = call;
        stopCallTimer();
    }

    void onCallRejected() {
        if (call != null) {
            Logger.error(TAG, "onCallRejected");
            stopCallTimer();
            destroy();
        }
    }

    void onCallUnanswered() {
        if (call != null) {
            Logger.error(TAG, "onCallUnanswered");
            destroy();
        }
    }

    void onCallBusy() {
        if (call != null) {
            Logger.error(TAG, "onCallBusy");
            stopCallTimer();
            destroy();
        }
    }

    void onCallEnded() {
        if (call != null) {
            Logger.error(TAG, "onCallEnded");
            endCallSession();
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            disposeView();
                        }
                    });
                    Logger.error(TAG, "Video view disposed 1");
                    destroy();
                }
            });

        }
    }

    void onCallCancelled() {
        destroy();
    }

    private void startCallTimer() {
        Logger.error(TAG, "starting call timer");
        if(threadExecutorService == null){
            threadExecutorService = Executors.newScheduledThreadPool(1);
            threadExecutorService.schedule(callTimer, timeoutSeconds, TimeUnit.SECONDS);
        }
    }

    private void stopCallTimer() {
        Logger.error(TAG, "stop call timer");
        if(threadExecutorService != null){
            threadExecutorService.shutdownNow();
            threadExecutorService = null;
        }
    }

    final Runnable callTimer = new Runnable() {
        public void run() {
            Logger.error(TAG, "Call Not Answered");
            if (callEventListener != null && call != null)
                callEventListener.onCallUnanswered(call);
        }
    };

    void startCall(final CallSettings settings, AppSettings appSettings, final CometChat.OngoingCallListener ongoingCallListener, final CallListener listener) {
        try{
            Logger.error(TAG, "start Call");
            if (deprecatedCallModuleExists()) {
                if (cometChatRTCView == null) {
                    callSettings = settings;
                    startCallSession();
                    if (getActiveCall() != null) {
                        callSettings.setCallMode(CallSettings.CALL_MODE_DEFAULT);
                    } else {
                        callSettings.setCallMode(CallSettings.CALL_MODE_DIRECT);
                    }
                    videoContainer = settings.getVideoContainer();
                    callListener = listener;

                    String region;
                    String sessionID;
                    boolean isInitiator = false;
                    boolean isAudioOnly = false;
                    boolean isConference = false;
                    RTCReceiver rtcReceiver = null;
                    RTCUser rtcInitiator = null;
                    final Call call = getActiveCall();
                    final User user = CometChat.getLoggedInUser();
                    final Settings cometChatAppSettings = SettingsRepo.getSettings();

                    if (!TextUtils.isEmpty(cometChatAppSettings.getRtcRegion())) {
                        region = cometChatAppSettings.getRtcRegion();
                    } else if (TextUtils.isEmpty(settings.getRegion())) {
                        region = appSettings.getRegion().toLowerCase();
                    } else {
                        region = settings.getRegion();
                    }
                    if (call == null) {
                        isAudioOnly = settings.isAudioOnly();
                        sessionID = "v1." + region + "." + PreferenceHelper.getAppID() + "." + settings.getSessionId();
                    } else {
                        sessionID = settings.getSessionId();
                        User initiator = (User) call.getCallInitiator();
                        if (call.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                            Group receiverGrp = (Group) call.getCallReceiver();
                            rtcInitiator = new RTCUser(initiator.getUid(), initiator.getName(), initiator.getAvatar());
                            rtcReceiver = new RTCReceiver(receiverGrp.getGuid(), receiverGrp.getName(), receiverGrp.getIcon());
                        } else {
                            User receiver = (User) call.getCallReceiver();
                            rtcInitiator = new RTCUser(initiator.getUid(), initiator.getName(), initiator.getAvatar());
                            rtcReceiver = new RTCReceiver(receiver.getUid(), receiver.getName(), receiver.getAvatar());
                        }

                        if (((User) call.getCallInitiator()).getUid().equals(user.getUid())) {
                            isInitiator = true;
                        }

                        if (call.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                            isInitiator = true;
                            isConference = true;
                        }

                        if (call.getType().equals(CometChatConstants.CALL_TYPE_AUDIO)) {
                            isAudioOnly = true;
                        }
                    }

                    final boolean finalIsAudioOnly = isAudioOnly;
                    final boolean finalIsInitiator = isInitiator;
                    final boolean finalIsConference = isConference;
                    final String finalRegion = region;
                    final String finalSessionID = sessionID;
                    final RTCReceiver finalRtcReceiver = rtcReceiver;
                    final RTCUser finalRtcInitiator = rtcInitiator;

                    ApiConnection.getInstance().getCallingJWT(CometChat.getLoggedInUser().getUid(), sessionID, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            try {
                                if (ce != null) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ongoingCallListener.onError(ce);
                                        }
                                    });
                                } else {
                                    JSONObject mainObject = new JSONObject(response);
                                    JSONObject dataObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                                    if (dataObject.has(CometChatConstants.ResponseKeys.KEY_JWT_TOKEN)) {
                                        String jwt = dataObject.getString(CometChatConstants.ResponseKeys.KEY_JWT_TOKEN);
                                        RTCUser rtcUser;
                                        rtcUser = new RTCUser(user.getUid(), user.getName(), user.getAvatar());
                                        rtcUser.setJwt(jwt);
                                        rtcUser.setResource(CometChatUtils.getResource(false));
                                        AnalyticsSettings analyticsSettings = new AnalyticsSettings(cometChatAppSettings.getAnalyticsHost(), cometChatAppSettings.getAnalyticsVersion());
                                        analyticsSettings.setAnalyticsPingDisabled(cometChatAppSettings.isAnalyticsPingDisabled());
                                        analyticsSettings.setAnalyticsUseSSL(cometChatAppSettings.isAnalyticsUseSSL());
                                        MainVideoContainerSetting videoSettings = new MainVideoContainerSetting();
                                        videoSettings.setVideoStreamProp(settings.getVideoSettings().getMainVideoAspectRatio());
                                        videoSettings.setfullScreenButtonProps(
                                                settings.getVideoSettings().getFullScreenButtonParams().getPosition(),
                                                settings.getVideoSettings().getFullScreenButtonParams().getVisibility()
                                        );
                                        videoSettings.setNameLabelProps(
                                                settings.getVideoSettings().getNameLabelParams().getPosition(),
                                                settings.getVideoSettings().getNameLabelParams().getVisibility(),
                                                settings.getVideoSettings().getNameLabelParams().getColor()
                                        );
                                        videoSettings.setZoomButtonProps(
                                                settings.getVideoSettings().getZoomButtonParams().getPosition(),
                                                settings.getVideoSettings().getZoomButtonParams().getVisibility()
                                        );
                                        videoSettings.setUserListButtonProps(
                                                settings.getVideoSettings().getUserListButtonParams().getPosition(),
                                                settings.getVideoSettings().getUserListButtonParams().getVisibility()
                                        );
                                        String defaultAudioMode = CometChatConstants.AUDIO_MODE_SPEAKER;
                                        if (finalIsAudioOnly)
                                            defaultAudioMode = CometChatConstants.AUDIO_MODE_EARPIECE;
                                        if (callSettings.getDefaultAudioMode() != null) {
                                            defaultAudioMode = callSettings.getDefaultAudioMode();
                                        }
                                        Log.d("CometChat", "Start Call Triggered : " + finalSessionID + " " + System.currentTimeMillis());
                                        cometChatRTCView = new CometChatRTCView.CometChatRTCViewBuilder(settings.getActivity())
                                                .setDefaultLayoutEnable(settings.isDefaultLayout())
                                                .setSessionId(finalSessionID)
                                                .setRTCUser(rtcUser)
                                                .setRTCReceiver(finalRtcReceiver)
                                                .setIsAudioOnly(finalIsAudioOnly)
                                                .setIsInitiator(finalIsInitiator)
                                                .setRTCInitiator(finalRtcInitiator)
                                                .setEndCallButtonDisable(settings.isEndCallButtonDisable())
                                                .setSwitchCameraButtonDisable(settings.isSwitchCameraButtonDisable())
                                                .setMuteAudioButtonDisable(settings.isMuteAudioButtonDisable())
                                                .setPauseVideoButtonDisable(settings.isPauseVideoButtonDisable())
                                                .setAudioModeButtonDisable(settings.isAudioModeButtonDisable())
                                                .setRegion(finalRegion)
                                                .setDomain(cometChatAppSettings.getWEBRTCHost())
                                                .setMode(settings.getMode())
                                                .isConference(finalIsConference)
                                                .setAppId(PreferenceHelper.getAppID())
                                                .startWithAudioMuted(settings.startWithAudioMuted())
                                                .startWithVideoMuted(settings.startWithVideoMuted())
                                                .setAnalyticsSettings(analyticsSettings)
                                                .setDefaultAudioMode(defaultAudioMode)
                                                .showSwitchToVideoCallButton(callSettings.showSwitchToVideoCallButton())
                                                .showRecordingButton(settings.showCallRecordButton())
                                                .startRecordingOnCallStart(settings.startRecordingOnCallStart())
                                                .setAvatarMode(settings.getAvatarMode())
                                                .setMainVideoContainerSetting(videoSettings)
                                                .setEnableDraggableVideoTile(settings.getEnableVideoTileDrag())
                                                .setEnableVideoTileClick(settings.getEnableVideoTileClick())
                                                .setEventListner(new CometChatRTCViewListener() {
                                                    @Override
                                                    public void onCallEnded() {
                                                        Logger.error(TAG, "CometChatRTC onCallEnded Called.....");
                                                    }

                                                    @Override
                                                    public void onCallEndButtonPressed() {
                                                        Logger.error(TAG, "CometChatRTC onCallEndButtonPressed Called.....");
                                                        listener.onCallEnded(callSettings, call);
                                                    }

                                                    @Override
                                                    public void onUserJoined(RTCUser user) {
                                                        Logger.error(TAG, "onUserJoined() " + user.getName());
                                                        listener.onUserJoined(getUserFromRTCUser(user));
                                                    }

                                                    @Override
                                                    public void onUserLeft(RTCUser user) {
                                                        Logger.error(TAG, "onUserLeft() " + user.getName());
                                                        listener.onUserDisconnected(getUserFromRTCUser(user));
                                                    }

                                                    @Override
                                                    public void onUserListChanged(ArrayList<RTCUser> users) {
                                                        Logger.error(TAG, "onUserListChanged() " + users.toString());
                                                        listener.onUserListUpdated(getUserListFromRTCUserList(users));
                                                    }

                                                    @Override
                                                    public void onAudioModeChanged(ArrayList<AudioMode> devices) {
                                                        Logger.error(TAG, "onAudioModeChanged() " + devices.toString());
                                                        listener.onAudioModesUpdated(getCometChatAudioModes(devices));
                                                    }

                                                    @Override
                                                    public void onCallSwitchedToVideo(CallSwitchRequestInfo callSwitchRequestInfo) {
                                                        Logger.error(TAG, "onCallSwitchedToVideo() " + callSwitchRequestInfo.toString());
                                                        try {
                                                            listener.onCallSwitchedToVideo(callSwitchRequestInfo.getSessionId(), getUserFromRTCUser(callSwitchRequestInfo.getRequestInitiatedBy()), getUserFromRTCUser(callSwitchRequestInfo.getRequestAcceptedBy()));
                                                            if (callSwitchRequestInfo != null && CometChat.getLoggedInUser() != null &&
                                                                    callSwitchRequestInfo.getRequestInitiatedBy().getUid().equalsIgnoreCase(CometChat.getLoggedInUser().getUid())) {
                                                                if (getActiveCall() != null) {
                                                                    ApiConnection.getInstance().switchCallFromAudioToVideo(callSwitchRequestInfo.getSessionId(), new ApiConnection.APIConnectionListener() {
                                                                        @Override
                                                                        public void onResponse(String response, CometChatException ce) {
                                                                            try {
                                                                                if (ce != null) {
                                                                                    Logger.error(TAG, "onCallSwitchedToVideo : " + ce.getMessage());
                                                                                } else {
                                                                                    Logger.error(TAG, "onCallSwitchedToVideo : success");
                                                                                }
                                                                            } catch (Exception e) {
                                                                                Logger.error(TAG, "Call Switch from audio to video failed : " + e.getMessage());
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    Logger.error(TAG, "Call switched for direct call. No API call required");
                                                                }
                                                            } else {
                                                                Logger.error(TAG, "Insufficient data from the calling SDK");
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onUserMuted(RTCMutedUser muteObj) {
                                                        Logger.error(TAG, "onUserMuted() " + muteObj.getMuted() + " muted by " + muteObj.getMutedBy());
                                                        listener.onUserMuted(getUserFromRTCUser(muteObj.getMuted()), getUserFromRTCUser(muteObj.getMutedBy()));
                                                    }

                                                    @Override
                                                    public void onRecordingToggled(RTCRecordingInfo recordingInfo) {
                                                        Logger.error(TAG, "onRecordingToggled() " + recordingInfo.getUser().getName() + " started recording");
                                                        if (recordingInfo.getRecordingStarted()) {
                                                            listener.onRecordingStarted(getUserFromRTCUser(recordingInfo.getUser()));
                                                        } else {
                                                            listener.onRecordingStopped(getUserFromRTCUser(recordingInfo.getUser()));
                                                        }
                                                    }
                                                })
                                                .build();
                                        isConferenceJoined = true;
                                        CometChat.postOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                settings.getVideoContainer().addView(cometChatRTCView.getView());
                                            }
                                        });
                                    } else {
                                        throw new JSONException("JWT Token not found");
                                    }
                                }
                            } catch (final JSONException e) {
                                e.printStackTrace();
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ongoingCallListener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Logger.debug(TAG, "Call In Progress..");
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_IN_PROGRESS, CometChatConstants.Errors.ERROR_CALL_IN_PROGRESS_MESSAGE));
                        }
                    });
                }
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE));
                    }
                });
            }
        }catch (Exception e){
            Logger.error(TAG, "startCall: " + e);
        }
    }


    public void switchCamera() {
        if (deprecatedCallModuleExists()) {
            if (cometChatRTCView != null) {
                cometChatRTCView.switchCameraSource();
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE);
        }
    }

    public void muteAudio(boolean mute) {
        if (deprecatedCallModuleExists()) {
            if (cometChatRTCView != null) {
                if (mute) {
                    cometChatRTCView.muteAudio();
                } else {
                    cometChatRTCView.unMuteAudio();
                }
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE);
        }
    }

    public void pauseVideo(boolean pauseVideo) {
        if (deprecatedCallModuleExists()) {
            if (cometChatRTCView != null) {
                if (pauseVideo) {
                    cometChatRTCView.pauseVideo();
                } else {
                    cometChatRTCView.unPauseVideo();
                }
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE);
        }
    }

    public void setAudioMode(@CometChatConstants.AudioModes String audioMode) {
        if (deprecatedCallModuleExists()) {
            if (cometChatRTCView != null) {
                cometChatRTCView.setAudioMode(audioMode);
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE);
        }
    }

    public void getAudioOutputModes(final CometChat.CallbackListener<List<com.cometchat.chat.models.AudioMode>> listener) {
        if (deprecatedCallModuleExists()) {
            if (cometChatRTCView != null) {
                cometChatRTCView.getAudioModes(new RTCCallback<List<AudioMode>>() {
                    @Override
                    public void onSuccess(List<AudioMode> audioModes) {
                        listener.onSuccess(getCometChatAudioModes(audioModes));
                    }

                    @Override
                    public void onError(Exception e) {
                        listener.onError(new CometChatException("CALL_EXCEPTION", e.getMessage()));
                    }
                });
            }
        } else {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE));
                }
            });
        }
    }

    private void destroy() {
        Logger.error(TAG, "Call Manager Destroy called");
        callManager = null;
        call = null;
        cometChatVideoViewActivity = null;
        isConferenceJoined = false;
        disposeView();
        removeCallEventListener();
    }

    void disposeView() {
        try{
            if (deprecatedCallModuleExists()) {
                isConferenceJoined = false;
                if (cometChatRTCView != null) {
                    cometChatRTCView.endCallSession();
                    cometChatRTCView = null;
                }
            }else {
                Logger.exception(TAG, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE);
            }
        }catch (Exception e){
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE + e);
        }
    }

    private List<com.cometchat.chat.models.AudioMode> getCometChatAudioModes(List<AudioMode> audioModes) {
        List<com.cometchat.chat.models.AudioMode> cometChatAudioModes = new ArrayList<>();
        for (AudioMode audioMode : audioModes) {
            cometChatAudioModes.add(getCometChatAudioMode(audioMode));
        }
        return cometChatAudioModes;
    }


    private com.cometchat.chat.models.AudioMode getCometChatAudioMode(AudioMode audioMode) {
        return new com.cometchat.chat.models.AudioMode(audioMode.getMode(), audioMode.isSelected());
    }

    boolean callModuleExists() {
        try {
            Class.forName(CALL_MODULE_CLASS_COMETCHAT_CALLS);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    boolean deprecatedCallModuleExists() {
        try {
            Class.forName(CALL_MODULE_CLASS_COMETCHAT_RTC_VIEW);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private User getUserFromRTCUser(RTCUser rtcUser) {
        User user = new User();
        if (rtcUser != null) {
            user.setUid(rtcUser.getUid());
            user.setName(rtcUser.getName());
            user.setAvatar(rtcUser.getAvatar());
        }
        return user;
    }

    private List<User> getUserListFromRTCUserList(List<RTCUser> rtcUsers) {
        List<User> users = new ArrayList<>();
        if (rtcUsers != null) {
            for (RTCUser rtcUser : rtcUsers) {
                users.add(getUserFromRTCUser(rtcUser));
            }
        }
        return users;
    }

    public void enterPIPMode() {
        if (deprecatedCallModuleExists()) {
            if (cometChatRTCView != null) {
                cometChatRTCView.enterPIPMode();
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE);
        }
    }

    public void exitPIPMode() {
        if (deprecatedCallModuleExists()) {
            if (cometChatRTCView != null) {
                cometChatRTCView.exitPIPMode();
            }
        } else {
            Logger.exception(TAG, CometChatConstants.Errors.ERROR_CALL_MODULE_NOT_FOUND_MESSAGE);
        }
    }

    public void switchToVideoCall() {
        if (cometChatRTCView != null) {
            cometChatRTCView.switchToVideoCall();
        }
    }

    public void startRecording() {
        if (cometChatRTCView != null) {
            cometChatRTCView.startRecording();
        }
    }

    public void stopRecording() {
        if (cometChatRTCView != null) {
            cometChatRTCView.stopRecording();
        }
    }

    Call getActiveCall() {
        return call;
    }

    boolean isConferenceJoined() {
        return isConferenceJoined;
    }

    interface CallListener {

        void onYouJoined();

        void onYouLeft();

        void onUserJoined(User user);

        void onUserDisconnected(User user);

        void onCallEnded(CallSettings callSettings, Call call);

        void onUserListUpdated(List<User> users);

        void onAudioModesUpdated(List<com.cometchat.chat.models.AudioMode> audioModes);

        void onUserMuted(User userMuted, User mutedBy);

        void onRecordingStarted(User user);

        void onRecordingStopped(User user);

        void onCallSwitchedToVideo(String sessionId, User callSwitchInitiatedBy, User callSwitchAcceptedBy);

        void onError(CometChatException ce);
    }

    interface CallEventListener {

        void onCallUnanswered(Call call);

    }
}
