package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.FlagReason;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adityagokula on 30/11/18.
 */

public class Settings {

    private static final String TAG = Settings.class.getSimpleName();

    private String contactList;
    private String adminApiHost;
    private String clientApiHost;
    private String chatHost;
    private boolean useSSL;
    private String groupService;
    private String callService;
    private String chatWSPort;
    private String chatWSSPort;
    private String chatHTTPPort;
    private String chatHTTPSPort;
    private String WEBRTCHost;
    private boolean WEBRTCUseSSL;
    private String WEBRTCWSPort;
    private String WEBRTCWSSPort;
    private String WEBRTCHTTPPort;
    private String WEBRTCHTTPSPort;
    private String rawData;
    private String chatHostOverride;
    private String chatHostAppSpecific;
    private String jidHostOverride;
    private String mode = CometChatConstants.MODE_DEFAULT;
    private List<String> enabledExtensions = new ArrayList<>();
    private String rtcRegion;
    private boolean analyticsPingDisabled;
    private boolean analyticsUseSSL;
    private String analyticsHost;
    private String analyticsVersion;
    private String settingsHash;
    private long settingsHashReceivedAt;
    private int appVersion;
    private String mainDomain;
    private String chatAPIVersion;
    private String wsAPIVersion;
    private String region;
    private String extensionDomain;
    private String webrtcAPISubDomain;
    private String secureMediaHost;
    private long fileSize = 104857600;
    private int fileCount = 10;
    // Pin & Save UX feature flags (from the `parameters` block). Default enabled: a backend that
    // does not serve the flag must not dead-disable the feature (parity with the other SDKs).
    private boolean pinnedMessagesEnabled = true;
    private boolean savedMessagesEnabled = true;
    private boolean conversationPinnedEnabled = true;
    // Pin & Save limits (from the `parameters` block). LIMIT_UNSPECIFIED when the backend does not
    // serve the value: the client must not invent a cap — the server enforces and errors anyway.
    private int pinnedMessagesLimit = LIMIT_UNSPECIFIED;
    private int pinnedMessagesSystemLimit = LIMIT_UNSPECIFIED;
    private int savedMessagesLimit = LIMIT_UNSPECIFIED;
    private int conversationPinnedLimit = LIMIT_UNSPECIFIED;
    private int conversationPinnedSystemLimit = LIMIT_UNSPECIFIED;
    private List<FlagReason> flagReasons;

    /** Sentinel returned by the Pin &amp; Save limit getters when the backend did not serve the value. */
    public static final int LIMIT_UNSPECIFIED = -1;

    private Settings(){

    }

    public List<FlagReason> getFlagReasons() {
        return flagReasons;
    }

    private void setFlagReasons(List<FlagReason> tempFlagReasonsList) {
        this.flagReasons = tempFlagReasonsList;
    }

    public List<String> getEnabledExtensions() {
        return enabledExtensions;
    }

    public void setEnabledExtensions(List<String> enabledExtensions) {
        this.enabledExtensions = enabledExtensions;
    }

    /**
     * Whether the Pin Message feature is enabled for this app
     * ({@code features.ux.messages.pinned.enabled}). Defaults to {@code true} when the flag is not
     * served.
     *
     * @since <b>v5</b>
     */
    public boolean isPinnedMessagesEnabled() {
        return pinnedMessagesEnabled;
    }

    public void setPinnedMessagesEnabled(boolean pinnedMessagesEnabled) {
        this.pinnedMessagesEnabled = pinnedMessagesEnabled;
    }

    /**
     * Whether the Save Message feature is enabled for this app
     * ({@code features.ux.messages.saved.enabled}). Defaults to {@code true} when the flag is not
     * served.
     *
     * @since <b>v5</b>
     */
    public boolean isSavedMessagesEnabled() {
        return savedMessagesEnabled;
    }

    public void setSavedMessagesEnabled(boolean savedMessagesEnabled) {
        this.savedMessagesEnabled = savedMessagesEnabled;
    }

    /**
     * Whether the Pin Conversation feature is enabled for this app
     * ({@code features.ux.conversations.pinned.enabled}). Defaults to {@code true} when the flag is
     * not served.
     *
     * @since <b>v5</b>
     */
    public boolean isConversationPinnedEnabled() {
        return conversationPinnedEnabled;
    }

    public void setConversationPinnedEnabled(boolean conversationPinnedEnabled) {
        this.conversationPinnedEnabled = conversationPinnedEnabled;
    }

    /**
     * Maximum number of messages the current user can pin in a conversation
     * ({@code features.ux.messages.pinned.limit}). Returns {@link #LIMIT_UNSPECIFIED} when the
     * backend does not serve the value.
     *
     * @since <b>v5</b>
     */
    public int getPinnedMessagesLimit() {
        return pinnedMessagesLimit;
    }

    public void setPinnedMessagesLimit(int pinnedMessagesLimit) {
        this.pinnedMessagesLimit = pinnedMessagesLimit;
    }

    /**
     * Maximum number of messages an admin / system can pin in a conversation
     * ({@code features.ux.messages.pinned.system.limit}). Returns {@link #LIMIT_UNSPECIFIED} when
     * the backend does not serve the value.
     *
     * @since <b>v5</b>
     */
    public int getPinnedMessagesSystemLimit() {
        return pinnedMessagesSystemLimit;
    }

    public void setPinnedMessagesSystemLimit(int pinnedMessagesSystemLimit) {
        this.pinnedMessagesSystemLimit = pinnedMessagesSystemLimit;
    }

    /**
     * Maximum number of messages the current user can save
     * ({@code features.ux.messages.saved.limit}). Returns {@link #LIMIT_UNSPECIFIED} when the
     * backend does not serve the value.
     *
     * @since <b>v5</b>
     */
    public int getSavedMessagesLimit() {
        return savedMessagesLimit;
    }

    public void setSavedMessagesLimit(int savedMessagesLimit) {
        this.savedMessagesLimit = savedMessagesLimit;
    }

    /**
     * Maximum number of conversations the current user can pin
     * ({@code features.ux.conversations.pinned.limit}). Returns {@link #LIMIT_UNSPECIFIED} when the
     * backend does not serve the value.
     *
     * @since <b>v5</b>
     */
    public int getConversationPinnedLimit() {
        return conversationPinnedLimit;
    }

    public void setConversationPinnedLimit(int conversationPinnedLimit) {
        this.conversationPinnedLimit = conversationPinnedLimit;
    }

    /**
     * Maximum number of conversations an admin / system can pin
     * ({@code features.ux.conversations.pinned.system.limit}). Returns {@link #LIMIT_UNSPECIFIED}
     * when the backend does not serve the value.
     *
     * @since <b>v5</b>
     */
    public int getConversationPinnedSystemLimit() {
        return conversationPinnedSystemLimit;
    }

    public void setConversationPinnedSystemLimit(int conversationPinnedSystemLimit) {
        this.conversationPinnedSystemLimit = conversationPinnedSystemLimit;
    }

    public String getContactList() {
        return contactList;
    }

    public void setContactList(String contactList) {
        this.contactList = contactList;
    }

    public String getAdminApiHost() {
        return adminApiHost;
    }

    public void setAdminApiHost(String adminApiHost) {
        this.adminApiHost = adminApiHost;
    }

    public String getClientApiHost() {
        return clientApiHost;
    }

    public void setClientApiHost(String clientApiHost) {
        this.clientApiHost = clientApiHost;
    }

    public String getChatHost() {
        return chatHost;
    }

    public void setChatHost(String chatHost) {
        this.chatHost = chatHost;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getGroupService() {
        return groupService;
    }

    public void setGroupService(String groupService) {
        this.groupService = groupService;
    }

    public String getCallService() {
        return callService;
    }

    public void setCallService(String callService) {
        this.callService = callService;
    }

    public String getChatWSPort() {
        return chatWSPort;
    }

    public void setChatWSPort(String chatWSPort) {
        this.chatWSPort = chatWSPort;
    }

    public String getChatWSSPort() {
        return chatWSSPort;
    }

    public void setChatWSSPort(String chatWSSPort) {
        this.chatWSSPort = chatWSSPort;
    }

    public String getChatHTTPPort() {
        return chatHTTPPort;
    }

    public void setChatHTTPPort(String chatHTTPPort) {
        this.chatHTTPPort = chatHTTPPort;
    }

    public String getChatHTTPSPort() {
        return chatHTTPSPort;
    }

    public void setChatHTTPSPort(String chatHTTPSPort) {
        this.chatHTTPSPort = chatHTTPSPort;
    }

    public String getWEBRTCHost() {
        return WEBRTCHost;
    }

    public void setWEBRTCHost(String WEBRTCHost) {
        this.WEBRTCHost = WEBRTCHost;
    }

    public boolean isWEBRTCUseSSL() {
        return WEBRTCUseSSL;
    }

    public void setWEBRTCUseSSL(boolean WEBRTCUseSSL) {
        this.WEBRTCUseSSL = WEBRTCUseSSL;
    }

    public String getWEBRTCWSPort() {
        return WEBRTCWSPort;
    }

    public void setWEBRTCWSPort(String WEBRTCWSPort) {
        this.WEBRTCWSPort = WEBRTCWSPort;
    }

    public String getWEBRTCWSSPort() {
        return WEBRTCWSSPort;
    }

    public void setWEBRTCWSSPort(String WEBRTCWSSPort) {
        this.WEBRTCWSSPort = WEBRTCWSSPort;
    }

    public String getWEBRTCHTTPPort() {
        return WEBRTCHTTPPort;
    }

    public void setWEBRTCHTTPPort(String WEBRTCHTTPPort) {
        this.WEBRTCHTTPPort = WEBRTCHTTPPort;
    }

    public String getWEBRTCHTTPSPort() {
        return WEBRTCHTTPSPort;
    }

    public void setWEBRTCHTTPSPort(String WEBRTCHTTPSPort) {
        this.WEBRTCHTTPSPort = WEBRTCHTTPSPort;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getChatHostOverride() {
        return chatHostOverride;
    }

    public void setChatHostOverride(String chatHostOverride) {
        this.chatHostOverride = chatHostOverride;
    }

    public String getChatHostAppSpecific() {
        return chatHostAppSpecific;
    }

    public void setChatHostAppSpecific(String chatHostAppSpecific) {
        this.chatHostAppSpecific = chatHostAppSpecific;
    }

    public String getJidHostOverride() {
        return jidHostOverride;
    }

    public void setJidHostOverride(String jidHostOverride) {
        this.jidHostOverride = jidHostOverride;
    }

    public String getRtcRegion() {
        return rtcRegion;
    }

    public void setRtcRegion(String rtcRegion) {
        this.rtcRegion = rtcRegion;
    }

    public boolean isAnalyticsPingDisabled() {
        return analyticsPingDisabled;
    }

    public void setAnalyticsPingDisabled(boolean analyticsPingDisabled) {
        this.analyticsPingDisabled = analyticsPingDisabled;
    }

    public boolean isAnalyticsUseSSL() {
        return analyticsUseSSL;
    }

    public void setAnalyticsUseSSL(boolean analyticsUseSSL) {
        this.analyticsUseSSL = analyticsUseSSL;
    }

    public String getAnalyticsHost() {
        return analyticsHost;
    }

    public void setAnalyticsHost(String analyticsHost) {
        this.analyticsHost = analyticsHost;
    }

    public String getSecureMediaHost() {
        return secureMediaHost;
    }

    public void setSecureMediaHost(String secureMediaHost) {
        this.secureMediaHost = secureMediaHost;
    }

    public String getAnalyticsVersion() {
        return analyticsVersion;
    }

    public void setAnalyticsVersion(String analyticsVersion) {
        this.analyticsVersion = analyticsVersion;
    }

    public String getSettingsHash() {
        return settingsHash;
    }

    public void setSettingsHash(String settingsHash) {
        this.settingsHash = settingsHash;
    }

    public long getSettingsHashReceivedAt() {
        return settingsHashReceivedAt;
    }

    public void setSettingsHashReceivedAt(long settingsHashReceivedAt) {
        this.settingsHashReceivedAt = settingsHashReceivedAt;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public String getMainDomain() {
        return mainDomain;
    }

    public void setMainDomain(String mainDomain) {
        this.mainDomain = mainDomain;
    }

    public String getChatAPIVersion() {
        return chatAPIVersion;
    }

    public void setChatAPIVersion(String chatAPIVersion) {
        this.chatAPIVersion = chatAPIVersion;
    }

    public String getWsAPIVersion() {
        return wsAPIVersion;
    }

    public void setWsAPIVersion(String wsAPIVersion) {
        this.wsAPIVersion = wsAPIVersion;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getExtensionDomain() {
        return extensionDomain;
    }

    public void setExtensionDomain(String extensionDomain) {
        this.extensionDomain = extensionDomain;
    }

    public String getWebrtcAPISubDomain() {
        return webrtcAPISubDomain;
    }

    public void setWebrtcAPISubDomain(String webrtcAPISubDomain) {
        this.webrtcAPISubDomain = webrtcAPISubDomain;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }


    public static Settings fromJson(String response) throws JSONException {
        Settings settings = new Settings();
        JSONObject mainObject = new JSONObject(response);
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_ADMIN_API_HOST))
            settings.setAdminApiHost(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_ADMIN_API_HOST));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CLIENT_API_HOST))
            settings.setClientApiHost(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CLIENT_API_HOST));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CONTACT_LIST))
            settings.setContactList(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CONTACT_LIST));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HOST))
            settings.setChatHost(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HOST));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CHAT_USE_SSL))
            settings.setUseSSL(mainObject.getBoolean(CometChatConstants.SettingsKeys.SETTINGS_CHAT_USE_SSL));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_GROUP_SERVICE))
            settings.setGroupService(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_GROUP_SERVICE));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CALL_SERVICE))
            settings.setCallService(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CALL_SERVICE));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CHAT_WS_PORT))
            settings.setChatWSPort(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CHAT_WS_PORT));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CHAT_WSS_PORT))
            settings.setChatWSSPort(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CHAT_WSS_PORT));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HTTP_BIND_PORT))
            settings.setChatHTTPPort(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HTTP_BIND_PORT));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HTTPS_BIND_PORT))
            settings.setChatHTTPSPort(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HTTPS_BIND_PORT));
        if (mainObject.has(CometChatConstants.SettingsKeys.WEBRTC_HOST))
            settings.setWEBRTCHost(mainObject.getString(CometChatConstants.SettingsKeys.WEBRTC_HOST));
        if (mainObject.has(CometChatConstants.SettingsKeys.WEBRTC_USE_SSL))
            settings.setWEBRTCUseSSL(mainObject.getBoolean(CometChatConstants.SettingsKeys.WEBRTC_USE_SSL));
        if (mainObject.has(CometChatConstants.SettingsKeys.WEBRTC_WS_PORT))
            settings.setWEBRTCWSPort(mainObject.getString(CometChatConstants.SettingsKeys.WEBRTC_WS_PORT));
        if (mainObject.has(CometChatConstants.SettingsKeys.WEBRTC_WSS_PORT))
            settings.setWEBRTCWSSPort(mainObject.getString(CometChatConstants.SettingsKeys.WEBRTC_WSS_PORT));
        if (mainObject.has(CometChatConstants.SettingsKeys.WEBRTC_HTTP_BIND_PORT))
            settings.setWEBRTCHTTPPort(mainObject.getString(CometChatConstants.SettingsKeys.WEBRTC_HTTP_BIND_PORT));
        if (mainObject.has(CometChatConstants.SettingsKeys.WEBRTC_HTTPS_BIND_PORT))
            settings.setWEBRTCHTTPSPort(mainObject.getString(CometChatConstants.SettingsKeys.WEBRTC_HTTPS_BIND_PORT));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_MODE))
            settings.setMode(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_MODE));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HOST_OVERRIDE))
            settings.setChatHostOverride(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HOST_OVERRIDE));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HOST_APP_SPECIFIC))
            settings.setChatHostAppSpecific(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_CHAT_HOST_APP_SPECIFIC));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_JID_HOST_OVERRIDE))
            settings.setJidHostOverride(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_JID_HOST_OVERRIDE));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_RTC_REGION_KEY))
            settings.setRtcRegion(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_RTC_REGION_KEY));
        if(mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_ANALYTICS_PING_DISABLED))
            settings.setAnalyticsPingDisabled(mainObject.getBoolean(CometChatConstants.SettingsKeys.SETTINGS_ANALYTICS_PING_DISABLED));
        if(mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_ANALYTICS_USE_SSL))
            settings.setAnalyticsUseSSL(mainObject.getBoolean(CometChatConstants.SettingsKeys.SETTINGS_ANALYTICS_USE_SSL));
        if(mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_ANALYTICS_HOST))
            settings.setAnalyticsHost(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_ANALYTICS_HOST));
        if(mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_ANALYTICS_VERSION))
            settings.setAnalyticsVersion(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_ANALYTICS_VERSION));
        if(mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_SETTINGS_HASH))
            settings.setSettingsHash(mainObject.getString(CometChatConstants.SettingsKeys.SETTINGS_SETTINGS_HASH));
        if(mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_SETTINGS_HASH_RECEIVED_AT))
            settings.setSettingsHashReceivedAt(mainObject.getLong(CometChatConstants.SettingsKeys.SETTINGS_SETTINGS_HASH_RECEIVED_AT));
        if(mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_APP_VERSION))
            settings.setAppVersion(mainObject.getInt(CometChatConstants.SettingsKeys.SETTINGS_APP_VERSION));
        if(mainObject.has(CometChatConstants.SettingsKeys.SECURED_MEDIA_HOST))
            settings.setSecureMediaHost(mainObject.getString(CometChatConstants.SettingsKeys.SECURED_MEDIA_HOST));
        if (mainObject.has(CometChatConstants.SettingsKeys.SETTINGS_EXTENSIONS)) {
            JSONArray extenisonArray = mainObject.getJSONArray(CometChatConstants.SettingsKeys.SETTINGS_EXTENSIONS);
            List<String> tempExtensionList = new ArrayList<>();
            for (int i = 0; i < extenisonArray.length(); i++) {
                JSONObject extensionObject = extenisonArray.getJSONObject(i);
                tempExtensionList.add(extensionObject.getString("id"));
            }
            settings.setEnabledExtensions(tempExtensionList);
        }
        if(mainObject.has(CometChatConstants.SettingsKeys.MAIN_DOMAIN))
            settings.setMainDomain(mainObject.getString(CometChatConstants.SettingsKeys.MAIN_DOMAIN));
        if(mainObject.has(CometChatConstants.SettingsKeys.CHAT_API_VERSION))
            settings.setChatAPIVersion(mainObject.getString(CometChatConstants.SettingsKeys.CHAT_API_VERSION));
        if(mainObject.has(CometChatConstants.SettingsKeys.WS_API_VERSION))
            settings.setWsAPIVersion(mainObject.getString(CometChatConstants.SettingsKeys.WS_API_VERSION));
        if(mainObject.has(CometChatConstants.SettingsKeys.REGION))
            settings.setRegion(mainObject.getString(CometChatConstants.SettingsKeys.REGION));
        if(mainObject.has(CometChatConstants.SettingsKeys.EXTENSION_DOMAIN))
            settings.setExtensionDomain(mainObject.getString(CometChatConstants.SettingsKeys.EXTENSION_DOMAIN));
        if(mainObject.has(CometChatConstants.SettingsKeys.WEBRTC_API_SUBDOMAIN))
            settings.setWebrtcAPISubDomain(mainObject.getString(CometChatConstants.SettingsKeys.WEBRTC_API_SUBDOMAIN));

        if(mainObject.has(CometChatConstants.SettingsKeys.PARAMETERS)){
            JSONObject parametersObject = mainObject.getJSONObject(CometChatConstants.SettingsKeys.PARAMETERS);
            if (parametersObject.has(CometChatConstants.SettingsKeys.FILE_SIZE))
                settings.setFileSize(parametersObject.getLong(CometChatConstants.SettingsKeys.FILE_SIZE));
            if (parametersObject.has(CometChatConstants.SettingsKeys.FILE_COUNT))
                settings.setFileCount(parametersObject.getInt(CometChatConstants.SettingsKeys.FILE_COUNT));
            // Pin & Save flags — absence ⇒ enabled (see field defaults).
            settings.setPinnedMessagesEnabled(parametersObject.optBoolean(CometChatConstants.FeatureKeys.FEATURE_PIN_MESSAGE, true));
            settings.setSavedMessagesEnabled(parametersObject.optBoolean(CometChatConstants.FeatureKeys.FEATURE_SAVE_MESSAGE, true));
            settings.setConversationPinnedEnabled(parametersObject.optBoolean(CometChatConstants.FeatureKeys.FEATURE_PIN_CONVERSATION, true));
            // Pin & Save limits — absence ⇒ LIMIT_UNSPECIFIED (optInt also coerces string-typed values).
            settings.setPinnedMessagesLimit(parametersObject.optInt(CometChatConstants.FeatureKeys.FEATURE_PIN_MESSAGE_LIMIT, LIMIT_UNSPECIFIED));
            settings.setPinnedMessagesSystemLimit(parametersObject.optInt(CometChatConstants.FeatureKeys.FEATURE_PIN_MESSAGE_SYSTEM_LIMIT, LIMIT_UNSPECIFIED));
            settings.setSavedMessagesLimit(parametersObject.optInt(CometChatConstants.FeatureKeys.FEATURE_SAVE_MESSAGE_LIMIT, LIMIT_UNSPECIFIED));
            settings.setConversationPinnedLimit(parametersObject.optInt(CometChatConstants.FeatureKeys.FEATURE_PIN_CONVERSATION_LIMIT, LIMIT_UNSPECIFIED));
            settings.setConversationPinnedSystemLimit(parametersObject.optInt(CometChatConstants.FeatureKeys.FEATURE_PIN_CONVERSATION_SYSTEM_LIMIT, LIMIT_UNSPECIFIED));
        }

        if (mainObject.has(CometChatConstants.SettingsKeys.FLAG_REASONS)) {
            JSONArray flagReasonsArray = mainObject.getJSONArray(CometChatConstants.SettingsKeys.FLAG_REASONS);
            List<FlagReason> tempFlagReasonsList = new ArrayList<>();
            for (int i = 0; i < flagReasonsArray.length(); i++) {
                JSONObject flagReasonObject = flagReasonsArray.getJSONObject(i);
                FlagReason flagReason = FlagReason.fromJson(flagReasonObject);
                tempFlagReasonsList.add(flagReason);
            }
            settings.setFlagReasons(tempFlagReasonsList);
        }
        settings.setRawData(response);
        return settings;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "contactList='" + contactList + '\'' +
                ", adminApiHost='" + adminApiHost + '\'' +
                ", clientApiHost='" + clientApiHost + '\'' +
                ", chatHost='" + chatHost + '\'' +
                ", useSSL=" + useSSL +
                ", groupService='" + groupService + '\'' +
                ", callService='" + callService + '\'' +
                ", chatWSPort='" + chatWSPort + '\'' +
                ", chatWSSPort='" + chatWSSPort + '\'' +
                ", chatHTTPPort='" + chatHTTPPort + '\'' +
                ", chatHTTPSPort='" + chatHTTPSPort + '\'' +
                ", WEBRTCHost='" + WEBRTCHost + '\'' +
                ", WEBRTCUseSSL=" + WEBRTCUseSSL +
                ", WEBRTCWSPort='" + WEBRTCWSPort + '\'' +
                ", WEBRTCWSSPort='" + WEBRTCWSSPort + '\'' +
                ", WEBRTCHTTPPort='" + WEBRTCHTTPPort + '\'' +
                ", WEBRTCHTTPSPort='" + WEBRTCHTTPSPort + '\'' +
                ", rawData='" + rawData + '\'' +
                ", chatHostOverride='" + chatHostOverride + '\'' +
                ", chatHostAppSpecific='" + chatHostAppSpecific + '\'' +
                ", jidHostOverride='" + jidHostOverride + '\'' +
                ", mode='" + mode + '\'' +
                ", enabledExtensions=" + enabledExtensions +
                ", rtcRegion='" + rtcRegion + '\'' +
                ", analyticsPingDisabled=" + analyticsPingDisabled +
                ", analyticsUseSSL=" + analyticsUseSSL +
                ", analyticsHost='" + analyticsHost + '\'' +
                ", analyticsVersion='" + analyticsVersion + '\'' +
                ", settingsHash='" + settingsHash + '\'' +
                ", settingsHashReceivedAt=" + settingsHashReceivedAt +
                ", appVersion=" + appVersion +
                ", mainDomain='" + mainDomain + '\'' +
                ", chatAPIVersion='" + chatAPIVersion + '\'' +
                ", wsAPIVersion='" + wsAPIVersion + '\'' +
                ", region='" + region + '\'' +
                ", extensionDomain='" + extensionDomain + '\'' +
                ", webrtcAPISubDomain='" + webrtcAPISubDomain + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", fileCount='" + fileCount + '\'' +
                ", pinnedMessagesEnabled=" + pinnedMessagesEnabled +
                ", savedMessagesEnabled=" + savedMessagesEnabled +
                ", conversationPinnedEnabled=" + conversationPinnedEnabled +
                ", pinnedMessagesLimit=" + pinnedMessagesLimit +
                ", pinnedMessagesSystemLimit=" + pinnedMessagesSystemLimit +
                ", savedMessagesLimit=" + savedMessagesLimit +
                ", conversationPinnedLimit=" + conversationPinnedLimit +
                ", conversationPinnedSystemLimit=" + conversationPinnedSystemLimit +
                ", flagReasons='" + flagReasons + '\'' +
                '}';
    }
}
