package com.cometchat.chat.core;

import android.app.Activity;
import android.widget.RelativeLayout;

import androidx.annotation.StringDef;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.MainVideoContainerSetting;
import com.cometchat.calls.CometChatRTCView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CallSettings class helps developer to set the various settings for the call.
 *
 */

public class CallSettings {

    public static final String CALL_MODE_DEFAULT = "DEFAULT";
    public static final String CALL_MODE_DIRECT = "DIRECT";
    @StringDef({CALL_MODE_DEFAULT,CALL_MODE_DIRECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallModes { }

    public static final String AVATAR_MODE_CIRCLE = "circle";
    public static final String AVATAR_MODE_SQUARE = "square";
    public static final String AVATAR_MODE_FULLSCREEN = "fullscreen";
    @StringDef({AVATAR_MODE_CIRCLE,AVATAR_MODE_SQUARE,AVATAR_MODE_FULLSCREEN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AvatarModes { }

    public static final String MODE_SPOTLIGHT = "SPOTLIGHT";
    public static final String MODE_SINGLE = "SINGLE";
    public static final String MODE_DEFAULT = "DEFAULT";
    @StringDef({MODE_SPOTLIGHT, MODE_SINGLE , MODE_DEFAULT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode { }

    public static final String ASPECT_RATIO_CONTAIN = "contain";
    public static final String ASPECT_RATIO_COVER = "cover";
    public static final String ASPECT_RATIO_DEFAULT = "default";
    @StringDef({ASPECT_RATIO_CONTAIN, ASPECT_RATIO_COVER, ASPECT_RATIO_DEFAULT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoStreamsMode { }

    public static final String POSITION_TOP_RIGHT = "top-right";
    public static final String POSITION_TOP_LEFT = "top-left";
    public static final String POSITION_BOTTOM_RIGHT = "bottom-right";
    public static final String POSITION_BOTTOM_LEFT = "bottom-left";
    @StringDef({POSITION_TOP_RIGHT, POSITION_TOP_LEFT , POSITION_BOTTOM_RIGHT, POSITION_BOTTOM_LEFT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoStreamsPosition { }

    private Activity activity;
    private boolean defaultLayout,audioOnly;
    private String sessionId,region,mode;
    private RelativeLayout videoContainer;
    private boolean showEndCallButton,showSwitchCameraButton,showMuteAudioButton,showPauseVideoButton,showAudioModeButton;
    @CallModes
    private String callMode;
    private boolean startWithAudioMuted = false;
    private boolean startWithVideoMuted = false;
    private String audioMode;
    private boolean showSwitchToVideoCallButton = true;
    private boolean showCallRecordButton = false;
    private boolean startRecordingOnCallStart = false;
    private String avatarMode = AVATAR_MODE_CIRCLE;
    private MainVideoContainerSetting videoSettings;
    private boolean enableVideoTileDrag = true;
    private boolean enableVideoTileClick = true;

    private CallSettings(CallSettingsBuilder builder) {
        this.activity = builder.activity;
        this.defaultLayout = builder.defaultLayout;
        this.sessionId = builder.sessionId;
        this.videoContainer = builder.videoContainer;
        this.showEndCallButton = builder.showEndCallButton;
        this.showSwitchCameraButton = builder.showSwitchCameraButton;
        this.showMuteAudioButton = builder.showMuteAudioButton;
        this.showPauseVideoButton = builder.showPauseVideoButton;
        this.showAudioModeButton = builder.showAudioModeButton;
        this.audioOnly = builder.audioOnly;
        this.region = builder.region;
        this.mode = builder.mode;
        this.startWithAudioMuted = builder.startWithAudioMuted;
        this.startWithVideoMuted = builder.startWithVideoMuted;
        this.audioMode = builder.audioMode;
        this.showSwitchToVideoCallButton = builder.showSwitchToVideoCallButton;
        this.showCallRecordButton = builder.showCallRecordButton;
        this.startRecordingOnCallStart = builder.startRecordingOnCallStart;
        this.avatarMode = builder.avatarMode;
        this.videoSettings = builder.videoSettings;
        this.enableVideoTileDrag = builder.enableVideoTileDrag;
        this.enableVideoTileClick = builder.enableVideoTileClick;
    }

    public Activity getActivity() {
        return activity;
    }

    public boolean isDefaultLayout() {
        return defaultLayout;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRegion(){
        return region;
    }

    public RelativeLayout getVideoContainer() {
        return videoContainer;
    }

    public boolean isEndCallButtonDisable() {
        return !showEndCallButton;
    }

    public boolean isSwitchCameraButtonDisable() {
        return !showSwitchCameraButton;
    }

    public boolean isMuteAudioButtonDisable() {
        return !showMuteAudioButton;
    }

    public boolean isPauseVideoButtonDisable() {
        return !showPauseVideoButton;
    }

    public boolean isAudioModeButtonDisable() {
        return !showAudioModeButton;
    }

    public boolean isAudioOnly(){
        return audioOnly;
    }

    public String getMode(){
        return mode;
    }

    String getCallMode() {
        return callMode;
    }

    void setCallMode(String callMode) {
        this.callMode = callMode;
    }

    public boolean startWithAudioMuted() {
        return startWithAudioMuted;
    }

    public boolean startWithVideoMuted() {
        return startWithVideoMuted;
    }

    public String getDefaultAudioMode() {
        return audioMode;
    }

    public boolean showSwitchToVideoCallButton() {
        return showSwitchToVideoCallButton;
    }

    public boolean showCallRecordButton() {
        return showCallRecordButton;
    }

    public boolean startRecordingOnCallStart() {
        return startRecordingOnCallStart;
    }

    public String getAvatarMode() {
        return avatarMode;
    }

    public MainVideoContainerSetting getVideoSettings() {
        if(videoSettings == null){
            videoSettings = new MainVideoContainerSetting();
        }
        return videoSettings;
    }

    public boolean getEnableVideoTileDrag() {
        return enableVideoTileDrag;
    }

    public boolean getEnableVideoTileClick() {
        return enableVideoTileClick;
    }

    public static class CallSettingsBuilder {
        private Activity activity;
        private boolean defaultLayout = true,audioOnly = false;
        private String sessionId,region, mode = "DEFAULT";
        private RelativeLayout videoContainer;
        private boolean showEndCallButton = true,showSwitchCameraButton = true,showMuteAudioButton = true,showPauseVideoButton = true,showAudioModeButton= true;
        private boolean startWithAudioMuted = false;
        private boolean startWithVideoMuted = false;
        private String audioMode;
        private boolean showSwitchToVideoCallButton = true;
        private boolean showCallRecordButton = false;
        private boolean startRecordingOnCallStart = false;
        private String avatarMode = AVATAR_MODE_CIRCLE;
        private MainVideoContainerSetting videoSettings;
        private boolean enableVideoTileDrag = true;
        private boolean enableVideoTileClick = true;

        public CallSettingsBuilder(Activity activity,RelativeLayout videoContainer) {
            this.activity = activity;
            this.videoContainer = videoContainer;
        }

        public CallSettingsBuilder enableDefaultLayout(boolean defaultLayout) {
            this.defaultLayout = defaultLayout;
            return this;
        }

        /**
         *  A method to set the unique session id of the call.
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param sid A unique session id for the call.
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */
        public CallSettingsBuilder setSessionId(String sid) {
            this.sessionId = sid;
            return this;
        }

        /**
         *  A method to set if the end call button is to be displayed in the UI.
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param showEndCallButton A boolean that determines if the end call button is to be displayed on the UI
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */
        public CallSettingsBuilder showEndCallButton(boolean showEndCallButton){
            this.showEndCallButton = showEndCallButton;
            return this;
        }

        /**
         *  A method to set if the switch camera button is to be displayed in the UI.
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param showSwitchCameraButton A boolean that determines if the switch camera button is to be displayed on the UI
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */
        public CallSettingsBuilder showSwitchCameraButton(boolean showSwitchCameraButton){
            this.showSwitchCameraButton = showSwitchCameraButton;
            return this;
        }

        /**
         *  A method to set if the mute audio button is to be displayed in the UI.
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param showMuteAudioButton A boolean that determines if the mute audio button is to be displayed on the UI
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */

        public CallSettingsBuilder showMuteAudioButton(boolean showMuteAudioButton){
            this.showMuteAudioButton = showMuteAudioButton;
            return this;
        }

        /**
         *  A method to set if the pause video button is to be displayed in the UI.
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param showPauseVideoButton A boolean that determines if the pause video button is to be displayed on the UI
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */
        public CallSettingsBuilder showPauseVideoButton(boolean showPauseVideoButton){
            this.showPauseVideoButton = showPauseVideoButton;
            return this;
        }

        /**
         *  A method to set if the audio mode button is to be displayed in the UI.
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param showAudioModeButton A boolean that determines if the audio mode button is to be displayed on the UI
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */
        public CallSettingsBuilder showAudioModeButton(boolean showAudioModeButton){
            this.showAudioModeButton = showAudioModeButton;
            return this;
        }

        /**
         *  A method to determine if the call is going to be an audio only call.
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param audioOnly A boolean that determines if the call is going to be an audio only call.
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */
        public CallSettingsBuilder setAudioOnlyCall(boolean audioOnly){
            this.audioOnly = audioOnly;
            return this;
        }

        /**
         *  A method to set the region where your app is hosted
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param region The region where the app is hosted.
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */
        public CallSettingsBuilder setRegion(String region){
            this.region = region;
            return this;
        }

        /**
         *  A method to start the call with a specified mode
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param mode The mode that the call is supposed to be started in.  This could hold either of the below values
         *             1. spotlight
         *             2. single
         *             3. default
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */
        public CallSettingsBuilder setMode(@CometChatRTCView.CometChatRTCViewBuilder.Mode String mode){
            this.mode = mode;
            return this;
        }

        /**
         *  A method to start the call with audio muted
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param startWithAudioMuted A boolean that determines if the call should be started with the audio muted
         * @return CallSettingsBuilder object when <code>build()</code> is called
         *
         */
        public CallSettingsBuilder startWithAudioMuted(boolean startWithAudioMuted){
            this.startWithAudioMuted = startWithAudioMuted;
            return this;
        }

        /**
         *  A method to start the call with video muted
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param startWithVideoMuted A boolean that determines if the call should be started with the video muted
         * @return CallSettingsBuilder object when <code><build()code/> is called
         *
         */
        public CallSettingsBuilder startWithVideoMuted(boolean startWithVideoMuted){
            this.startWithVideoMuted = startWithVideoMuted;
            return this;
        }

        /**
         *  A method to set the default audio mode for the call
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param audioMode The audio mode that is to be set for the call
         * @return CallSettingsBuilder object when <code><build()code/> is called
         *
         */
        public CallSettingsBuilder setDefaultAudioMode(@CometChatConstants.AudioModes String audioMode){
            this.audioMode = audioMode;
            return this;
        }
        /**
         *  A method to set the default audio mode for the call
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param showSwitchToVideoCallButton A boolean that determines if the switch voice to video call option should be visible
         * @return CallSettingsBuilder object when <code><build()code/> is called
         *
         */
        public CallSettingsBuilder showSwitchToVideoCallButton(boolean showSwitchToVideoCallButton){
            this.showSwitchToVideoCallButton = showSwitchToVideoCallButton;
            return this;
        }

        /**
         *  A method to determine if the recording button should be visible.
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param showCallRecordButton A boolean that determines if the call recording button should be visible
         * @return CallSettingsBuilder object when <code><build()code/> is called
         *
         */
        public CallSettingsBuilder showCallRecordButton(boolean showCallRecordButton){
            this.showCallRecordButton = showCallRecordButton;
            return this;
        }

        /**
         *  A method to determine if the recording should be started on call start
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param startRecordingOnCallStart A boolean that determines if the call recording Should be started when the call is started.
         * @return CallSettingsBuilder object when <code><build()code/> is called
         *
         */
        public CallSettingsBuilder startRecordingOnCallStart(boolean startRecordingOnCallStart){
            this.startRecordingOnCallStart = startRecordingOnCallStart;
            return this;
        }

        /**
         *  A method to determine the avatar mode. This helps you determine the UI mode in which the user avatar should be displayed
         *
         * @version <b>v2</b>
         * @since   <b>v2</b>
         * @param avatarMode String value to determine how the avatar should be displayed.
         * Possible values:
         * 1. circle : The avatar will be displayed in a circle.
         * 2. square : The avatar will be displayed in a square.
         * 3. fullscreen : The avatar will be displayed in full screen mode
         * @return CallSettingsBuilder object when <code><build()code/> is called
         *
         */
        public CallSettingsBuilder setAvatarMode(@AvatarModes String avatarMode){
            this.avatarMode = avatarMode;
            return this;
        }

        /**
         *  A method to set the video containers items positions.
         *
         * @version <b>v3</b>
         * @since   <b>v3</b>
         * @param videoSettings A configuration for video call container items position.
         * @return CallSettingsBuilder object when <code><build()code/> is called
         *
         */
        public CallSettingsBuilder setMainVideoContainerSetting(MainVideoContainerSetting videoSettings) {
            this.videoSettings = videoSettings;
            return this;
        }

        /**
         *  A method to set the video containers items positions.
         *
         * @version <b>v3</b>
         * @since   <b>v3</b>
         * @param enableVideoTileDrag A configuration for video tile item position.
         * @return CallSettingsBuilder object when <code><build()code/> is called
         *
         */
        public CallSettingsBuilder enableVideoTileDrag(Boolean enableVideoTileDrag) {
            this.enableVideoTileDrag = enableVideoTileDrag;
            return this;
        }
        
        /**
         *  A method to set the video containers items positions.
         *
         * @version <b>v3</b>
         * @since   <b>v3</b>
         * @param enableVideoTileClick A configuration for video tile items click.
         * @return CallSettingsBuilder object when <code><build()code/> is called
         *
         */
        public CallSettingsBuilder enableVideoTileClick(boolean enableVideoTileClick) {
            this.enableVideoTileClick = enableVideoTileClick;
            return this;
        }

        public CallSettings build() {
            return new CallSettings(this);
        }
    }

    @Override
    public String toString() {
        return "CallSettings{" +
                "activity=" + activity +
                ", defaultLayout=" + defaultLayout +
                ", audioOnly=" + audioOnly +
                ", sessionId='" + sessionId + '\'' +
                ", region='" + region + '\'' +
                ", mode='" + mode + '\'' +
                ", videoContainer=" + videoContainer +
                ", showEndCallButton=" + showEndCallButton +
                ", showSwitchCameraButton=" + showSwitchCameraButton +
                ", showMuteAudioButton=" + showMuteAudioButton +
                ", showPauseVideoButton=" + showPauseVideoButton +
                ", showAudioModeButton=" + showAudioModeButton +
                ", callMode='" + callMode + '\'' +
                ", startWithAudioMuted=" + startWithAudioMuted +
                ", startWithVideoMuted=" + startWithVideoMuted +
                ", audioMode='" + audioMode + '\'' +
                ", showSwitchToVideoCallButton=" + showSwitchToVideoCallButton +
                ", showCallRecordButton=" + showCallRecordButton +
                ", startRecordingOnCallStart=" + startRecordingOnCallStart +
                ", avatarMode='" + avatarMode + '\'' +
                ", videoSettings=" + videoSettings +
                ", enableVideoTileDrag=" + enableVideoTileDrag +
                ", enableVideoTileClick=" + enableVideoTileClick +
                '}';
    }
}
