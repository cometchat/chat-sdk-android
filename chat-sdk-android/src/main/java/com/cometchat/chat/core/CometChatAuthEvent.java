package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CometChatAuthEvent extends CometChatEvent {

    private String jwt;
    private String presenceSubscription;
    private List<String> roles;
    private AuthResponse authResponse;

    static CometChatEvent fromJSON(JSONObject mainObject) throws JSONException{
        String appId = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_APP_ID))
            appId = mainObject.getString(CometChatConstants.WSKeys.KEY_APP_ID);
        String deviceId = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_DEVICE_ID))
            deviceId = mainObject.getString(CometChatConstants.WSKeys.KEY_DEVICE_ID);
        String sender = null;
        if(mainObject.has(CometChatConstants.WSKeys.KEY_SENDER))
            sender = mainObject.getString(CometChatConstants.WSKeys.KEY_SENDER);
        CometChatAuthEvent cometChatAuthEvent = new CometChatAuthEvent(appId, null, null, deviceId, sender);
        JSONObject bodyObject = mainObject.getJSONObject(CometChatConstants.WSKeys.KEY_BODY);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setCode(bodyObject.getInt(CometChatConstants.WSKeys.KEY_AUTH_CODE));
        authResponse.setStatus(bodyObject.getString(CometChatConstants.WSKeys.KEY_AUTH_STATUS));
        cometChatAuthEvent.setAuthResponse(authResponse);
        return cometChatAuthEvent;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getPresenceSubscription() {
        return presenceSubscription;
    }

    public void setPresenceSubscription(String presenceSubscription) {
        this.presenceSubscription = presenceSubscription;
    }


    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public AuthResponse getAuthResponse() {
        return authResponse;
    }

    public void setAuthResponse(AuthResponse authResponse) {
        this.authResponse = authResponse;
    }

    CometChatAuthEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
        setType(CometChatConstants.WSKeys.KEY_TYPE_AUTH);
    }

    @Override
    protected String getAsString() throws JSONException {
        return getAsJSONObject().toString();
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        JSONObject mainObject = new JSONObject();
        mainObject.put(CometChatConstants.WSKeys.KEY_APP_ID, getAppId());
        mainObject.put(CometChatConstants.WSKeys.KEY_TYPE, getType());
        mainObject.put(CometChatConstants.WSKeys.KEY_DEVICE_ID, getDeviceId());
        JSONObject bodyObject = new JSONObject();
        bodyObject.put(CometChatConstants.WSKeys.KEY_TYPE_AUTH, getJwt());
        if(!getPresenceSubscription().equalsIgnoreCase(AppSettings.SUBSCRIPTION_TYPE_NONE))
            bodyObject.put(CometChatConstants.WSKeys.KEY_PRESENCE_SUBSCRIPTION, getPresenceSubscription());
        if(getPresenceSubscription().equalsIgnoreCase(AppSettings.SUBSCRIPTION_TYPE_ROLES)){
            bodyObject.put(CometChatConstants.WSKeys.KEY_TYPE_ROLES, getRolesArray(getRoles()));
        }
        bodyObject.put(CometChatConstants.WSKeys.KEY_DEVICE_ID, CometChatUtils.getResource(false));
        JSONObject paramsObject = getParamsObject();
        bodyObject.put("params", paramsObject);
        mainObject.put(CometChatConstants.WSKeys.KEY_BODY, bodyObject);
        return mainObject;
    }

    private JSONObject getParamsObject(){
        JSONObject paramsObject = new JSONObject();
        JSONObject appInfoObject = new JSONObject();
        try{
            paramsObject.put(CometChatConstants.AppInfoKeys.KEY_PLATFORM, CometChatUtils.getPlatform());
            paramsObject.put(CometChatConstants.AppInfoKeys.KEY_USER_AGENT, CometChatUtils.getAgent());
            paramsObject.put(CometChatConstants.AppInfoKeys.KEY_DEVICE_ID, CometChat.getDeviceUniqueId());

            appInfoObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_OS_VERSION, CometChatUtils.getAndroidVersion());
            appInfoObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_VERSION, CometChatUtils.getSDKVersion());
            appInfoObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_API_VERSION, ApiConnection.API_VERSION);
            if (PreferenceHelper.getResource() != null)
                appInfoObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_RESOURCE, PreferenceHelper.getResource());
            if (PreferenceHelper.getPlatform() != null)
                appInfoObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_PLATFORM, PreferenceHelper.getPlatform());
            if (PreferenceHelper.getLanguage() != null)
                appInfoObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_LANGUAGE, PreferenceHelper.getLanguage());
            if (CometChat.getActiveCall() != null)
                appInfoObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_SID, CometChat.getActiveCall().getSessionId());
            appInfoObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_ORIGIN, CometChat.getPackageName());
            appInfoObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_UTS, System.currentTimeMillis());
            paramsObject.put("appInfo", appInfoObject);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return paramsObject;
    }

    private JSONArray getRolesArray(List<String> roles){
        JSONArray rolesArray = new JSONArray();
        if(roles!=null && roles.size()>0) {
            for (String role : roles) {
                rolesArray.put(role);
            }
        }
        return rolesArray;
    }
}
