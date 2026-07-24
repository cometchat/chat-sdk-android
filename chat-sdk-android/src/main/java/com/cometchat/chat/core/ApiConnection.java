package com.cometchat.chat.core;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cometchat.chat.BuildConfig;
import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.AttachmentType;
import com.cometchat.chat.enums.PushPlatforms;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.CustomMessage;
import com.cometchat.chat.models.FlagDetail;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.InteractiveMessage;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.MessageReceipt;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by adityagokula on 06/09/18.
 */

class ApiConnection {

    private static final String TAG = ApiConnection.class.getSimpleName();

    private static final String BASE_ADMIN_URL = "%s.api-%s.cometchat.io";
    private static final String BASE_ADMIN_DEV_URL = "%s.api-%s.cometchat-dev.com";
    private static final String BASE_URL = "%s.apiclient-%s.cometchat.io";
    private static final String BASE_DEV_URL = "%s.apiclient-%s.cometchat-dev.com";
    private static final String BASE_ANALYTICS_URL = "metrics-%s.cometchat.io";
    private static final String ANALYTICS_VERSION = "/v1";
    private static final String WS_VERSION = "/v1";
    static final String API_VERSION = "v3.0";
    private static final String URL_LOGIN = "/users/%s/auth_tokens";
    private static final String URL_LOGIN_TOKEN = "/me";
    private static final String URL_SEND_MESSAGE = "/messages";
    private static final String URL_FILES_UPLOAD_URL = "/files/upload-url";
    private static final String URL_CREATE_GROUP = "/groups";
    private static final String URL_FETCH_SETTINGS = "/settings";
    private static final String URL_FLAG_MESSAGE = "/messages/%s/flagged";
    private static final String URL_JOIN_GROUP = "/groups/%s/members";
    private static final String URL_USERS_LIST = "/users";
    private static final String URL_USER_DETAILS = "/users/%s";
    private static final String URL_GROUPS_LIST = "/groups";
    private static final String URL_USER_GET_CONVERSATIONS = "/users/%s/messages";
    private static final String URL_GROUP_GET_CONVERSATIONS = "/groups/%s/messages";
    private static final String URL_GET_USERS_MESSAGES = "/messages";
    private static final String URL_GET_GROUP_MEMBERS = "/groups/%s/members";
    private static final String URL_LEAVE_GROUP = "/groups/%s/members";
    private static final String URL_UPDATE_GROUP = "/groups/%s";
    private static final String URL_DELETE_GROUP = "/groups/%s";
    private static final String URL_KICK_USER = "/groups/%s/members/%s";
    private static final String URL_BAN_USER = "/groups/%s/bannedusers/%s";
    private static final String URL_UNBAN_USER = "/groups/%s/bannedusers/%s";
    private static final String URL_CHANGE_MEMBER_SCOPE = "/groups/%s/members/%s";
    private static final String URL_GET_BANNED_MEMBERS = "/groups/%s/bannedusers";
    private static final String URL_GET_GROUP = "/groups/%s";
    private static final String URL_INITIATE_CALL = "/calls";
    private static final String URL_UPDATE_CALL_STATUS = "/calls/%s";
    private static final String URL_MARK_AS_DELIVERED = "/%s/%s/conversation/delivered";
    private static final String URL_MARK_AS_READ = "/%s/%s/conversation/read";
    private static final String URL_MARK_AS_INTERACTED = "/messages/%s/interacted";
    private static final String URL_LOGOUT = "/me";
    private static final String URL_BLOCK_USER = "/blockedusers";
    private static final String URL_UNBLOCK_USER = "/blockedusers";
    private static final String URL_BLOCKED_USER_LIST = "/blockedusers";
    private static final String URL_MESSAGE_DETAILS = "/messages/%s";
    private static final String URL_EDIT_MESSAGE = "/messages/%s";
    private static final String URL_DELETE_MESSAGE = "/messages/%s";
    private static final String URL_ADD_MEMBERS = "/groups/%s/members";
    private static final String URL_GET_CONVERSATIONS = "/conversations";
    private static final String URL_REGISTER_PUSH_NOTIFICATION = "/me";
    private static final String URL_CREATE_USER = "/users";
    private static final String URL_UPDATE_USER = "/users/%s";
    private static final String URL_SEND_THREADED_MESSAGE = "/messages/%s/thread";
    private static final String URL_GET_THREADED_MESSAGES = "/messages/%s/thread";
    private static final String URL_POLL_MESSAGES = "/messages";
    private static final String URL_ANALYTICS_PING = "/ping";
    private static final String URL_TRANSFER_OWNERSHIP = "/groups/%s/owner";
    private static final String WEBRTC_API_SUBDOMAIN = "xmpp";
    private static final String URL_CALL_PARTICIPANT_COUNT = "https://%s.%s/room-size"; //need check
    private static final String URL_CALL_JWT = "/me/jwt";
    private static final String URL_UPDATE_MY_DETAILS = "/me";
    private static final String URL_DELETE_USER_CONVERSATION = "/users/%s/conversation";
    private static final String URL_DELETE_GROUP_CONVERSATION = "/groups/%s/conversation";
    private static final String URL_GET_USER_CONVERSATION = "/users/%s/conversation";
    private static final String URL_GET_GROUP_CONVERSATION = "/groups/%s/conversation";
    private static final String URL_GET_ONLINE_USERS = "/api/%s/online-members";
    private static final String URL_SWITCH_AUDIO_CALL_TO_VIDEO = "/calls/%s/type";
    private static final String URL_AI_GET_USER_SMART_REPLIES = "/ai/smart-replies/users/%s";
    private static final String URL_AI_GET_GROUP_SMART_REPLIES = "/ai/smart-replies/groups/%s";
    private static final String URL_AI_GET_USER_CONVERSATION_STARTER = "/ai/conversation-starter/users/%s";
    private static final String URL_AI_GET_GROUP_CONVERSATION_STARTER = "/ai/conversation-starter/groups/%s";
    private static final String URL_AI_GET_USER_CONVERSATION_SUMMARY = "/ai/conversation-summary/users/%s";
    private static final String URL_AI_GET_GROUP_CONVERSATION_SUMMARY = "/ai/conversation-summary/groups/%s";
    private static final String URL_AI_GET_USER_ASK_BOT = "/ai/bots/%s/assistance/users/%s";
    private static final String URL_AI_GET_GROUP_ASK_BOT = "/ai/bots/%s/assistance/group/%s";
    private static final String URL_USER_SESSIONS = "/user_sessions";
    private static final String URL_REACTIONS = "/messages/%s/reactions/%s";
    private static final String URL_REACTIONS_WITHOUT_REACTION = "/messages/%s/reactions";
    private static final String MID_URL_NOTIFICATION_PUSH = "/notifications/push/v1";
    private static final String MID_URL_NOTIFICATION = "/notifications/v1";
    private static final String URL_NOTIFICATION_GET_PUSH_PREFERENCES_PUSH = MID_URL_NOTIFICATION_PUSH + "/preferences";
    private static final String URL_NOTIFICATION_GET_PUSH_PREFERENCES = MID_URL_NOTIFICATION + "/preferences";
    private static final String URL_NOTIFICATION_GET_TOKEN_MANAGEMENT = MID_URL_NOTIFICATION_PUSH + "/tokens";
    private static final String URL_NOTIFICATION_MUTE_UNMUTE = MID_URL_NOTIFICATION_PUSH + "/preferences/mute";
    private static final String URL_NOTIFICATION_UPDATE_TIMEZONE = URL_NOTIFICATION_GET_PUSH_PREFERENCES + "/timezone";

    // Notification Feed URLs
    private static final String URL_NOTIFICATION_FEED = "/campaigns/notification-feed";
    private static final String URL_NOTIFICATION_FEED_UNREAD_COUNT = "/campaigns/notification-feed/unread-count";
    private static final String URL_NOTIFICATION_FEED_DELIVERED = "/campaigns/notification-feed/%s/delivered";
    private static final String URL_NOTIFICATION_FEED_READ = "/campaigns/notification-feed/%s/read";
    private static final String URL_NOTIFICATION_FEED_ENGAGEMENT = "/campaigns/notification-feed/%s/engagement";
    private static final String URL_ANNOUNCEMENTS_SINGLE = "/campaigns/notification-feed/%s";
    private static final String URL_TEMPLATE_CATEGORIES = "/campaigns/templates/categories";
    private static final String URL_PUSH_NOTIFICATION_DELIVERED = "/campaigns/push-notifications/%s/delivered";
    private static final String URL_PUSH_NOTIFICATION_CLICKED = "/campaigns/push-notifications/%s/clicked";

    private static ApiConnection mConnection;
    private static OkHttpClient okHttpClient;
    private static OkHttpClient okHttpFileClient;
    private static Context context;
    private static AppSettings appSettings;
    private static int getSettingsRetryCounter = 0;

    // Urls to the APIs

    private ApiConnection(Context appContext) {
        context = appContext;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        okHttpFileClient = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    static synchronized ApiConnection getInstance() {
        if (mConnection == null) {
            Log.d(TAG, "CometChat.init() not called");
            throw new RuntimeException(CometChatConstants.Errors.ERROR_INIT_NOT_CALLED_MESSAGE);
        } else {
            return mConnection;
        }
    }

    static synchronized void init(Context context, AppSettings globalSettings) {
        if (mConnection == null) {
            mConnection = new ApiConnection(context);
        }
        appSettings = globalSettings;
    }

    private Request createGET(String url, Headers headers, HashMap<String, String> queryParams) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        return new Request.Builder()
                .url(URLDecoder.decode(httpBuilder.build().toString()))
                .headers(headers)
                .build();
    }

    private Request createPUT(String url, Headers headers, HashMap<String, String> body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = getRequestBodyFromMap(body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .put(requestBody)
                .url(URLDecoder.decode(url))
                .headers(headers)
                .build();
    }

    private Request createPUT(String url, Headers headers, String body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .put(requestBody)
                .url(URLDecoder.decode(url))
                .headers(headers)
                .build();
    }

    private Request createPUTForArray(String url, Headers headers, HashMap<String, JSONArray> body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = getRequestBodyFromJSONArray(body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .put(requestBody)
                .headers(headers)
                .build();
    }

    private Request createPOST(String url, Headers headers, HashMap<String, String> body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = getRequestBodyFromMap(body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .post(requestBody)
                .headers(headers)
                .build();
    }

    private Request createPOSTForArray(String url, Headers headers, HashMap<String, JSONArray> body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = getRequestBodyFromJSONArray(body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .post(requestBody)
                .headers(headers)
                .build();
    }

    private Request createPOSTWithJson(String url, Headers headers, String body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .post(requestBody)
                .headers(headers)
                .build();
    }

    private Request createFile(String url, Headers headers, HashMap<String, String> body, List<File> files) {
        RequestBody requestBody = getRequestBodyFromMap(body, files);
        Request request = new Request.Builder()
                .url(URLDecoder.decode(url))
                .post(requestBody)
                .headers(headers)
                .build();

        return request;
    }

    private Request createDELETE(String url, Headers headers, HashMap<String, String> body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = getRequestBodyFromMap(body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .delete(requestBody)
                .headers(headers)
                .build();
    }

    private Request createDELETEWithJsonRequest(String url, Headers headers, String body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .delete(requestBody)
                .headers(headers)
                .build();
    }

    private Request createDELETEForArray(String url, Headers headers, HashMap<String, JSONArray> body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = getRequestBodyFromJSONArray(body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .delete(requestBody)
                .headers(headers)
                .build();
    }

    private Request createPATCH(String url, Headers headers, HashMap<String, String> body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = getRequestBodyFromMap(body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .patch(requestBody)
                .headers(headers)
                .build();
    }

    private Request createPATCH(String url, Headers headers, String body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .patch(requestBody)
                .headers(headers)
                .build();
    }

    private Request createPATCHForArray(String url, Headers headers, HashMap<String, JSONArray> body) {
        RequestBody requestBody;
        if (body != null)
            requestBody = getRequestBodyFromJSONArray(body);
        else
            requestBody = new FormBody.Builder().build();
        return new Request.Builder()
                .url(URLDecoder.decode(url))
                .patch(requestBody)
                .headers(headers)
                .build();
    }

    void login(String UID, String apiKey, final APIConnectionListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(CometChatConstants.Params.UID, UID);
        params.put(CometChatConstants.AppInfoKeys.KEY_PLATFORM, CometChatUtils.getPlatform());
        params.put(CometChatConstants.AppInfoKeys.KEY_USER_AGENT, CometChatUtils.getAgent());
        params.put(CometChatConstants.AppInfoKeys.KEY_DEVICE_ID, CometChatUtils.getDeviceUniqueId(context));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_OS_VERSION, CometChatUtils.getAndroidVersion());
            jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_VERSION, CometChatUtils.getSDKVersion());
            jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_API_VERSION, API_VERSION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("appInfo", jsonObject.toString());
        Headers headers = new Headers.Builder()
                .add(CometChatConstants.Params.APIKEY, apiKey)
                .add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID())
                .add(CometChatConstants.Params.KEY_HEADER_SDK_VERSION, CometChatUtils.getPlatform().toLowerCase() + "@" + CometChatUtils.getSDKVersion())
                .build();
        Request loginRequest = createPOST(getAdminUrl(String.format(URL_LOGIN, UID)), headers, params);
        makeApiCall(loginRequest, listener, false);

    }

    void login(final String authToken, final APIConnectionListener listener) {

        Headers headers = new Headers.Builder()
                .add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID())
                .add(CometChatConstants.Params.AUTHTOKEN, authToken)
                .add(CometChatConstants.Params.KEY_HEADER_SDK_VERSION, CometChatUtils.getPlatform().toLowerCase() + "@" + CometChatUtils.getSDKVersion())
                .add(CometChatConstants.Params.KEY_HEADER_RESOURCE, CometChatUtils.getResource(false))
                .build();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(CometChatConstants.AppInfoKeys.KEY_PLATFORM, CometChatUtils.getPlatform());
        params.put(CometChatConstants.AppInfoKeys.KEY_USER_AGENT, CometChatUtils.getAgent());
        params.put(CometChatConstants.AppInfoKeys.KEY_DEVICE_ID, CometChatUtils.getDeviceUniqueId(context));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_OS_VERSION, CometChatUtils.getAndroidVersion());
            jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_VERSION, CometChatUtils.getSDKVersion());
            jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_API_VERSION, API_VERSION);
            if (PreferenceHelper.getResource() != null)
                jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_RESOURCE, PreferenceHelper.getResource());
            if (PreferenceHelper.getPlatform() != null)
                jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_PLATFORM, PreferenceHelper.getPlatform());
            if (PreferenceHelper.getLanguage() != null)
                jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_LANGUAGE, PreferenceHelper.getLanguage());
            if (CometChat.getActiveCall() != null)
                jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_SID, CometChat.getActiveCall().getSessionId());
            jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_ORIGIN, context.getPackageName());
            jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_UTS, System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("appInfo", jsonObject.toString());
        Request loginRequest = createPUT(getApiUrl(URL_LOGIN_TOKEN), headers, params);
        makeApiCall(loginRequest, listener, false);

    }

    void getUser(String uid, final APIConnectionListener listener) {
        Request getUserRequest = createGET(getApiUrl(String.format(URL_USER_DETAILS, uid)), getDefaultHeaders(), null);
        makeApiCall(getUserRequest, listener, false);
    }

    void sendMessage(BaseMessage message, final APIConnectionListener listener) {
        if (message.getParentMessageId() <= 0) {
            if (message instanceof TextMessage) {
                HashMap<String, String> bodyMap = ((TextMessage) message).toMap();
                Request sendMessageRequest = createPOST(getApiUrl(URL_SEND_MESSAGE), getDefaultHeaders(), bodyMap);
                makeApiCall(sendMessageRequest, listener, false);
            } else if (message instanceof MediaMessage) {
                MediaMessage mediaMessage = (MediaMessage) message;
                Request sendMessageRequest = null;
                if (mediaMessage.getFiles() != null)
                    sendMessageRequest = createFile(getApiUrl(URL_SEND_MESSAGE), getDefaultHeaders(), mediaMessage.toMap(), ((MediaMessage) message).getFiles());
                else
                    sendMessageRequest = createPOST(getApiUrl(URL_SEND_MESSAGE), getDefaultHeaders(), mediaMessage.toMap());
                makeApiCall(sendMessageRequest, listener, true);
            } else if (message instanceof CustomMessage) {
                Request sendCustomMessageRequest = createPOST(getApiUrl(URL_SEND_MESSAGE), getDefaultHeaders(), ((CustomMessage) message).toMap());
                makeApiCall(sendCustomMessageRequest, listener, false);
            } else if (message instanceof InteractiveMessage) {
                Request sendInteractiveMessageRequest = createPOST(getApiUrl(URL_SEND_MESSAGE), getDefaultHeaders(), ((InteractiveMessage) message).toMap());
                makeApiCall(sendInteractiveMessageRequest, listener, false);
            }
        } else {
            if (message instanceof TextMessage) {
                HashMap<String, String> bodyMap = ((TextMessage) message).toMap();
                Request sendMessageRequest = createPOST(getApiUrl(String.format(URL_SEND_THREADED_MESSAGE, String.valueOf(message.getParentMessageId()))), getDefaultHeaders(), bodyMap);
                makeApiCall(sendMessageRequest, listener, false);
            } else if (message instanceof MediaMessage) {
                MediaMessage mediaMessage = (MediaMessage) message;
                Request sendMessageRequest = null;
                if (mediaMessage.getFiles() != null)
                    sendMessageRequest = createFile(getApiUrl(String.format(URL_SEND_THREADED_MESSAGE, String.valueOf(message.getParentMessageId()))), getDefaultHeaders(), mediaMessage.toMap(), ((MediaMessage) message).getFiles());
                else
                    sendMessageRequest = createPOST(getApiUrl(String.format(URL_SEND_THREADED_MESSAGE, String.valueOf(message.getParentMessageId()))), getDefaultHeaders(), mediaMessage.toMap());
                makeApiCall(sendMessageRequest, listener, true);
            } else if (message instanceof CustomMessage) {
                Request sendCustomMessageRequest = createPOST(getApiUrl(String.format(URL_SEND_THREADED_MESSAGE, String.valueOf(message.getParentMessageId()))), getDefaultHeaders(), ((CustomMessage) message).toMap());
                makeApiCall(sendCustomMessageRequest, listener, false);
            } else if (message instanceof InteractiveMessage) {
                Request sendInteractiveMessageRequest = createPOST(getApiUrl(String.format(URL_SEND_THREADED_MESSAGE, String.valueOf(message.getParentMessageId()))), getDefaultHeaders(), ((InteractiveMessage) message).toMap());
                makeApiCall(sendInteractiveMessageRequest, listener, false);
            }
        }

    }

    /**
     * Requests pre-signed upload forms for one or more files from the chat-api.
     * The {@code filesBody} is a JSON object of the shape
     * {@code { "files": { "<fileId>": { "name", "size", "mimeType" } } }}.
     * The response is delivered raw to the listener for per-file parsing.
     */
    void requestUploadUrls(String filesBody, final APIConnectionListener listener) {
        Request request = createPOSTWithJson(getApiUrl(URL_FILES_UPLOAD_URL), getDefaultHeaders(), filesBody);
        makeApiCall(request, listener, false);
    }

    /**
     * Uploads a single file's bytes directly to storage using a pre-signed POST
     * form. Builds a multipart body with every policy field first and the file
     * part last (storage policy requirement), on a clean request that carries
     * <b>no</b> SDK auth headers (storage authorizes via the presigned policy
     * fields only).
     *
     * @param url       the storage upload URL ({@code request.url} from presign)
     * @param formFields the policy fields ({@code request.body} from presign)
     * @param fileName  the file name for the file part
     * @param fileBody  the file's request body (typically wrapped in
     *                  {@code ProgressRequestBody} for progress)
     * @param callback  OkHttp callback for success/failure
     * @return the OkHttp {@link Call} (already enqueued) so the caller can cancel it
     */
    Call uploadToStorage(String url,
                         Map<String, String> formFields,
                         String fileName,
                         RequestBody fileBody,
                         Callback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (formFields != null) {
            for (Map.Entry<String, String> field : formFields.entrySet()) {
                builder.addFormDataPart(field.getKey(), field.getValue());
            }
        }
        // The file part MUST be added last (storage policy requirement).
        builder.addFormDataPart("file", fileName, fileBody);
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = okHttpFileClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

    void createGroup(Group group, final APIConnectionListener listener) {
        Request createGroupRequest = createPOST(getApiUrl(URL_CREATE_GROUP), getDefaultHeaders(), group.toMap());
        makeApiCall(createGroupRequest, listener, false);
    }

    void joinGroup(String groupGUID, String groupType, String password, final APIConnectionListener listener) {
        HashMap<String, String> body = new HashMap<>();
        if (groupType.equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PASSWORD))
            body.put(CometChatConstants.Params.PASSWORD, password);
        Request joinGroupRequest = createPOST(getApiUrl(String.format(URL_JOIN_GROUP, groupGUID)), getDefaultHeaders(), body);
        makeApiCall(joinGroupRequest, listener, false);
    }

    void leaveGroup(String groupGUID, final APIConnectionListener listener) {
        Request leaveGroupRequest = createDELETE(getApiUrl(String.format(URL_LEAVE_GROUP, groupGUID)), getDefaultHeaders(), null);
        makeApiCall(leaveGroupRequest, listener, false);
    }

    void getGroup(String guid, final APIConnectionListener listener) {
        Request getGroup = createGET(getApiUrl(String.format(URL_GET_GROUP, guid)), getDefaultHeaders(), null);
        makeApiCall(getGroup, listener, false);
    }

    void updateGroup(Group group, final APIConnectionListener listener) {
        Request updateGroup = createPUT(getApiUrl(String.format(URL_UPDATE_GROUP, group.getGuid())), getDefaultHeaders(), group.toMap());
        makeApiCall(updateGroup, listener, false);
    }

    void deleteGroup(String guid, final APIConnectionListener listener) {
        Request deleteGroup = createDELETE(getApiUrl(String.format(URL_DELETE_GROUP, guid)), getDefaultHeaders(), null);
        makeApiCall(deleteGroup, listener, false);
    }

    void getSettingsRetry(@NonNull final CometChat.CallbackListener<Settings> listener) {
        try {
            if (CurrentUserRepo.getCurrentUser() != null && CurrentUserRepo.getCurrentUser().getAuthToken() != null) {
                getSettings(CurrentUserRepo.getCurrentUser().getAuthToken(), new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        try {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(ce);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject mainObject = new JSONObject(response);
                                    final Settings settings = Settings.fromJson(mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA).toString());
                                    SettingsRepo.insertSettings(settings);
                                    listener.onSuccess(settings);
                                } catch (final JSONException e) {
                                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
                                }
                            }
                        } catch (Exception e) {
                            final CometChatException uncaughtException = new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.getMessage());
                            listener.onError(uncaughtException);
                        }
                    }
                });
            } else {
                listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
            }
        } catch (Exception e) {
            Logger.error(TAG, "Error: getSettingsRetry >>:" + e);
        }
    }

    void getSettings(String authToken, final APIConnectionListener listener) {
        Headers headers = new Headers.Builder()
                .add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID())
                .add(CometChatConstants.Params.AUTHTOKEN, authToken)
                .add(CometChatConstants.Params.KEY_HEADER_SDK_VERSION, CometChatUtils.getPlatform().toLowerCase() + "@" + CometChatUtils.getSDKVersion())
                .build();
        Request settingsRequest = createGET(getApiUrl(URL_FETCH_SETTINGS), headers, null);
        makeApiCall(settingsRequest, listener, false);
    }

    void getUsers(int limit, long token, int page, String searchKeyword, String status, boolean includeBlockedUsers, List<String> roles, boolean friendsOnly, List<String> tags, boolean withTags, List<String> uids, List<String> searchInFields, String sortBy, String sortOrder, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.LIMIT, String.valueOf(limit));
        if (token != 0L)
            queryParams.put(CometChatConstants.Params.TOKEN, String.valueOf(token));
        if (page != 0)
            queryParams.put(CometChatConstants.Params.PAGE, String.valueOf(page));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (status != null)
            queryParams.put(CometChatConstants.Params.KEY_STATUS, status);
        if (includeBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_BLOCKED_USERS, String.valueOf(1));
        if (roles != null && roles.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_ROLES, CometChatUtils.getCSStringFromList(roles));
        if (friendsOnly)
            queryParams.put(CometChatConstants.Params.KEY_FRIENDS_ONLY, String.valueOf(1));
        if (tags != null && tags.size() > 0)
            queryParams.put(CometChatConstants.UserKeys.USER_KEY_TAGS, CometChatUtils.getCSStringFromList(tags));
        if (withTags)
            queryParams.put(CometChatConstants.UserKeys.USER_KEY_WITH_TAGS, String.valueOf(1));
        if (uids != null && uids.size() > 0)
            queryParams.put(CometChatConstants.UserKeys.USER_KEY_UIDS, CometChatUtils.getCSStringFromList(uids));
        if (searchInFields != null && searchInFields.size() > 0) {
            queryParams.put(CometChatConstants.UserKeys.USER_KEY_SEARCH_IN, CometChatUtils.getCSStringFromList(searchInFields));
        }
        if (sortBy != null) {
            queryParams.put(CometChatConstants.UserKeys.USER_KEY_SORT_BY, sortBy);
        }
        if (sortOrder != null) {
            queryParams.put(CometChatConstants.UserKeys.USER_KEY_SORT_ORDER, sortOrder);
        }
        Request userListRequest = createGET(getApiUrl(URL_USERS_LIST), getDefaultHeaders(), queryParams);
        makeApiCall(userListRequest, listener, false);
    }

    void getBlockedUsers(int limit, long token, int page, String searchKeyword, String direction, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.LIMIT, String.valueOf(limit));
        if (token != 0L)
            queryParams.put(CometChatConstants.Params.TOKEN, String.valueOf(token));
        if (page != 0)
            queryParams.put(CometChatConstants.Params.PAGE, String.valueOf(page));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (direction != null)
            queryParams.put(CometChatConstants.Params.KEY_DIRECTION, direction);
        Request blockedUsersListRequest = createGET(getApiUrl(URL_BLOCKED_USER_LIST), getDefaultHeaders(), queryParams);
        makeApiCall(blockedUsersListRequest, listener, false);
    }

    void getGroups(int limit, long token, int page, String searchKeyword, boolean joinedOnly, List<String> tags, boolean withTags, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.LIMIT, String.valueOf(limit));
        if (token != 0L)
            queryParams.put(CometChatConstants.Params.TOKEN, String.valueOf(token));
        if (page != 0)
            queryParams.put(CometChatConstants.Params.PAGE, String.valueOf(page));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (joinedOnly)
            queryParams.put(CometChatConstants.Params.KEY_HAS_JOINED, String.valueOf(1));
        if (tags != null && tags.size() > 0)
            queryParams.put(CometChatConstants.GroupKeys.GROUP_KEY_TAGS, CometChatUtils.getCSStringFromList(tags));
        if (withTags)
            queryParams.put(CometChatConstants.GroupKeys.GROUP_KEY_WITH_TAGS, String.valueOf(1));
        Request groupListRequest = createGET(getApiUrl(URL_GROUPS_LIST), getDefaultHeaders(), queryParams);
        makeApiCall(groupListRequest, listener, false);
    }

    void getAllMessages(int limit, long timestamp, int id, @CometChatConstants.Affix String affix, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        if (timestamp != -1 && timestamp != 0) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_FIELD, CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP);
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_VALUE, String.valueOf(timestamp));
        } else if (id != -1 && id != 0) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_FIELD, CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID);
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_VALUE, String.valueOf(id));
        }
        if (affix.equalsIgnoreCase(CometChatConstants.AFFIX_PREPEND)) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_PREPEND);
        } else {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_APPEND);
        }
        Request getAllMessagesRequest = createGET(getApiUrl(URL_GET_USERS_MESSAGES), getDefaultHeaders(), queryParams);
        makeApiCall(getAllMessagesRequest, listener, false);
    }

    void getUserConversationsByTimestamp(String fromUID, int limit, long timestamp, String affix, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        if (timestamp != -1 && timestamp != 0) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_FIELD, CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP);
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_VALUE, String.valueOf(timestamp));
        }
        if (affix.equalsIgnoreCase(CometChatConstants.AFFIX_PREPEND)) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_PREPEND);
        } else {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_APPEND);
        }
        Request getConversationsRequest = createGET(getApiUrl(String.format(URL_USER_GET_CONVERSATIONS, fromUID)), getDefaultHeaders(), queryParams);
        makeApiCall(getConversationsRequest, listener, false);
    }

    void getUserConversationsByMessageId(String fromUID, int limit, int id, String affix, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        if (id > 0) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_FIELD, CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID);
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_VALUE, String.valueOf(id));
        }
        if (affix.equalsIgnoreCase(CometChatConstants.AFFIX_PREPEND)) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_PREPEND);
        } else {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_APPEND);
        }
        Request getConversationsRequest = createGET(getApiUrl(String.format(URL_USER_GET_CONVERSATIONS, fromUID)), getDefaultHeaders(), queryParams);
        makeApiCall(getConversationsRequest, listener, false);
    }

    void getGroupConversationsByTimestamp(String guid, int limit, long timestamp, String affix, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        if (timestamp != -1 && timestamp != 0) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_FIELD, CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP);
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_VALUE, String.valueOf(timestamp));
        }
        if (affix.equalsIgnoreCase(CometChatConstants.AFFIX_PREPEND)) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_PREPEND);
        } else {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_APPEND);
        }
        Request getConversationsRequest = createGET(getApiUrl(String.format(URL_GROUP_GET_CONVERSATIONS, guid)), getDefaultHeaders(), queryParams);
        makeApiCall(getConversationsRequest, listener, false);
    }

    void getGroupConversationsByMessageId(String guid, int limit, int id, String affix, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        if (id > 0) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_FIELD, CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID);
            queryParams.put(CometChatConstants.PaginationKeys.KEY_CURSOR_VALUE, String.valueOf(id));
        }
        if (affix.equalsIgnoreCase(CometChatConstants.AFFIX_PREPEND)) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_PREPEND);
        } else {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, CometChatConstants.AFFIX_APPEND);
        }
        Request getConversationsRequest = createGET(getApiUrl(String.format(URL_GROUP_GET_CONVERSATIONS, guid)), getDefaultHeaders(), queryParams);
        makeApiCall(getConversationsRequest, listener, false);
    }

    void getGroupMembers(String guid, int limit, long cursor, int page, String searchKeyword, List<String> scopes, @UsersRequest.UserStatus String status, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.LIMIT, String.valueOf(limit));
        if (cursor != 0L)
            queryParams.put(CometChatConstants.Params.TOKEN, String.valueOf(cursor));
        if (page != 0)
            queryParams.put(CometChatConstants.Params.PAGE, String.valueOf(page));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (scopes != null && scopes.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_SCOPES, CometChatUtils.getCSStringFromList(scopes));
        if (status != null && !status.isEmpty())
            queryParams.put(CometChatConstants.Params.KEY_STATUS, status);
        Request groupListRequest = createGET(getApiUrl(String.format(URL_GET_GROUP_MEMBERS, guid)), getDefaultHeaders(), queryParams);
        makeApiCall(groupListRequest, listener, false);
    }

    void getBannedMembers(String guid, int limit, long cursor, int page, String searchKeyword, List<String> scopes, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.LIMIT, String.valueOf(limit));
        if (cursor != 0L)
            queryParams.put(CometChatConstants.Params.TOKEN, String.valueOf(cursor));
        if (page != 0)
            queryParams.put(CometChatConstants.Params.PAGE, String.valueOf(page));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (scopes != null && !scopes.isEmpty())
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_SCOPES, CometChatUtils.getCSStringFromList(scopes));

        Request groupListRequest = createGET(getApiUrl(String.format(URL_GET_BANNED_MEMBERS, guid)), getDefaultHeaders(), queryParams);
        makeApiCall(groupListRequest, listener, false);
    }

    void kickUser(String guid, String uid, final APIConnectionListener listener) {
        Request kickUserRequest = createDELETE(getApiUrl(String.format(URL_KICK_USER, guid, uid)), getDefaultHeaders(), null);
        makeApiCall(kickUserRequest, listener, false);
    }

    void banUser(String guid, String uid, final APIConnectionListener listener) {
        Request banUserRequest = createPOST(getApiUrl(String.format(URL_BAN_USER, guid, uid)), getDefaultHeaders(), null);
        makeApiCall(banUserRequest, listener, false);
    }

    void unbanUser(String guid, String uid, final APIConnectionListener listener) {
        Request unbanUserRequest = createDELETE(getApiUrl(String.format(URL_UNBAN_USER, guid, uid)), getDefaultHeaders(), null);
        makeApiCall(unbanUserRequest, listener, false);
    }

    void changeMemberScope(String guid, String uid, @NonNull @CometChatConstants.MemberScope String scope, final APIConnectionListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE, scope);
        Request changeScopeRequest = createPUT(getApiUrl(String.format(URL_CHANGE_MEMBER_SCOPE, guid, uid)), getDefaultHeaders(), params);
        makeApiCall(changeScopeRequest, listener, false);
    }

    void initiateConference(com.cometchat.chat.core.Call call, final APIConnectionListener listener) {
        Request initiateConferenceRequest = createPOST(getApiUrl(URL_INITIATE_CALL), getDefaultHeaders(), call.toMap());
        makeApiCall(initiateConferenceRequest, listener, false);
    }

    void updateCallStatus(String sessionId, @CometChatConstants.CallStatus String callStatus, long joinedAt, final APIConnectionListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put(CometChatConstants.CallKeys.CALL_STATUS, callStatus);
        if (callStatus.equalsIgnoreCase(CometChatConstants.CALL_STATUS_ENDED) && joinedAt != -1)
            params.put(CometChatConstants.CallKeys.CALL_JOINED_AT, String.valueOf(joinedAt));
        Request updateCallStatusRequest = createPUT(getApiUrl(String.format(URL_UPDATE_CALL_STATUS, sessionId)), getDefaultHeaders(), params);
        makeApiCall(updateCallStatusRequest, listener, false);
    }

    void logout(final APIConnectionListener listener) {
        Request logoutRequest = createDELETE(getApiUrl(URL_LOGOUT), getDefaultHeaders(), null);
        makeApiCall(logoutRequest, listener, false);
    }

    // MessagesRequest class api methods
    void getThreadedMessages(long parentMessageId, int limit, @CometChatConstants.Affix String affix, long timestamp, long messageId, boolean unread, boolean hideMessagesFromBlockedUsers, String searchKeyword, long updatedAfter, boolean updatesOnly, List<String> categories, List<String> types, boolean hideDeleted, List<String> tags, boolean withTags, boolean interactionGoalCompleted, boolean mentionsWithTagInfo, boolean mentionsWithBlockedInfo, boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes, boolean withParent, boolean hideQuotedMessages, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, affix);
        if (timestamp != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP, String.valueOf(timestamp));
        if (messageId != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID, String.valueOf(messageId));
        if (unread)
            queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (updatedAfter != -1)
            queryParams.put(CometChatConstants.Params.KEY_UPDATED_AT, String.valueOf(updatedAfter));
        if (updatesOnly)
            queryParams.put(CometChatConstants.Params.KEY_ONLY_UPDATES, String.valueOf(1));
        if (categories != null && categories.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_CATEGORIES, CometChatUtils.getCSStringFromList(categories));
        if (types != null && types.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_TYPES, CometChatUtils.getCSStringFromList(types));
        if (hideDeleted)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_DELETED, String.valueOf(1));
        if (tags != null && tags.size() > 0)
            queryParams.put(CometChatConstants.MessageKeys.KEY_TAGS, CometChatUtils.getCSStringFromList(tags));
        if (withTags)
            queryParams.put(CometChatConstants.MessageKeys.KEY_WITH_TAGS, String.valueOf(1));
        if (interactionGoalCompleted)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ONLY_INTERACTION_GOAL_COMPLETED, String.valueOf(1));
        if (mentionsWithTagInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_TAG_INFO, String.valueOf(1));
        if (mentionsWithBlockedInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_BLOCKED_INFO, String.valueOf(1));
        if(hasAttachments)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_ATTACHMENTS, String.valueOf(1));
        if(hasLinks)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_LINKS, String.valueOf(1));
        if(hasMentions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_MENTIONS, String.valueOf(1));
        if(hasReactions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_REACTIONS, String.valueOf(1));
        if (mentionedUids != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONED_UIDS, CometChatUtils.getCSStringFromList(mentionedUids));
        if (attachmentTypes != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_TYPE, CometChatUtils.getCSStringFromList(CometChatUtils.getListOfStringsFromType(attachmentTypes)));
        if (withParent) {
            queryParams.put(CometChatConstants.MessageKeys.KEY_WITH_PARENT, String.valueOf(1));
        }
        if (hideQuotedMessages) {
            queryParams.put(CometChatConstants.MessageKeys.KEY_HIDE_QUOTED_MESSAGES, String.valueOf(1));
        }
        Request getConversationsRequest = createGET(getApiUrl(String.format(URL_GET_THREADED_MESSAGES, String.valueOf(parentMessageId))), getDefaultHeaders(), queryParams);
        makeApiCall(getConversationsRequest, listener, false);
    }

    void getUserConversations(String UID, int limit, @CometChatConstants.Affix String affix, long timestamp, long messageId, boolean unread, boolean hideMessagesFromBlockedUsers, String searchKeyword, long updatedAfter, boolean updatesOnly, List<String> categories, List<String> types, boolean hideReplies, boolean hideDeleted, List<String> tags, boolean withTags, boolean interactionGoalCompleted, boolean mentionsWithTagInfo, boolean mentionsWithBlockedInfo, boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes, boolean hideQuotedMessages, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, affix);
        if (timestamp != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP, String.valueOf(timestamp));
        if (messageId != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID, String.valueOf(messageId));
        if (unread)
            queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (updatedAfter != -1)
            queryParams.put(CometChatConstants.Params.KEY_UPDATED_AT, String.valueOf(updatedAfter));
        if (updatesOnly)
            queryParams.put(CometChatConstants.Params.KEY_ONLY_UPDATES, String.valueOf(1));
        if (categories != null && categories.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_CATEGORIES, CometChatUtils.getCSStringFromList(categories));
        if (types != null && types.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_TYPES, CometChatUtils.getCSStringFromList(types));
        if (hideReplies)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_REPLIES, String.valueOf(1));
        if (hideDeleted)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_DELETED, String.valueOf(1));
        if (tags != null && tags.size() > 0)
            queryParams.put(CometChatConstants.MessageKeys.KEY_TAGS, CometChatUtils.getCSStringFromList(tags));
        if (withTags)
            queryParams.put(CometChatConstants.MessageKeys.KEY_WITH_TAGS, String.valueOf(1));
        if (interactionGoalCompleted)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ONLY_INTERACTION_GOAL_COMPLETED, String.valueOf(1));
        if (mentionsWithTagInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_TAG_INFO, String.valueOf(1));
        if (mentionsWithBlockedInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_BLOCKED_INFO, String.valueOf(1));
        if(hasAttachments)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_ATTACHMENTS, String.valueOf(1));
        if(hasLinks)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_LINKS, String.valueOf(1));
        if(hasMentions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_MENTIONS, String.valueOf(1));
        if(hasReactions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_REACTIONS, String.valueOf(1));
        if (mentionedUids != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONED_UIDS, CometChatUtils.getCSStringFromList(mentionedUids));
        if (attachmentTypes != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_TYPE, CometChatUtils.getCSStringFromList(CometChatUtils.getListOfStringsFromType(attachmentTypes)));
        if (hideQuotedMessages) {
            queryParams.put(CometChatConstants.MessageKeys.KEY_HIDE_QUOTED_MESSAGES, String.valueOf(1));
        }

        Request getConversationsRequest = createGET(getApiUrl(String.format(URL_USER_GET_CONVERSATIONS, UID)), getDefaultHeaders(), queryParams);
        makeApiCall(getConversationsRequest, listener, false);
    }

    void getGroupConversations(String GUID, int limit, @CometChatConstants.Affix String affix, long timestamp, long messageId, boolean unread, boolean hideMessagesFromBlockedUsers, String searchKeyword, long updatedAfter, boolean updatesOnly, List<String> categories, List<String> types, boolean hideReplies, boolean hideDeleted, List<String> tags, boolean withTags, boolean interactionGoalCompleted, boolean mentionsWithTagInfo, boolean mentionsWithBlockedInfo, boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes, boolean hideQuotedMessages, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, affix);
        if (timestamp != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP, String.valueOf(timestamp));
        if (messageId != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID, String.valueOf(messageId));
        if (unread)
            queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (updatedAfter != -1)
            queryParams.put(CometChatConstants.Params.KEY_UPDATED_AT, String.valueOf(updatedAfter));
        if (updatesOnly)
            queryParams.put(CometChatConstants.Params.KEY_ONLY_UPDATES, String.valueOf(1));
        if (categories != null && categories.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_CATEGORIES, CometChatUtils.getCSStringFromList(categories));
        if (types != null && types.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_TYPES, CometChatUtils.getCSStringFromList(types));
        if (hideReplies)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_REPLIES, String.valueOf(1));
        if (hideDeleted)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_DELETED, String.valueOf(1));
        if (tags != null && tags.size() > 0)
            queryParams.put(CometChatConstants.MessageKeys.KEY_TAGS, CometChatUtils.getCSStringFromList(tags));
        if (withTags)
            queryParams.put(CometChatConstants.MessageKeys.KEY_WITH_TAGS, String.valueOf(1));
        if (interactionGoalCompleted)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ONLY_INTERACTION_GOAL_COMPLETED, String.valueOf(1));
        if (mentionsWithTagInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_TAG_INFO, String.valueOf(1));
        if (mentionsWithBlockedInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_BLOCKED_INFO, String.valueOf(1));
        if(hasAttachments)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_ATTACHMENTS, String.valueOf(1));
        if(hasLinks)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_LINKS, String.valueOf(1));
        if(hasMentions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_MENTIONS, String.valueOf(1));
        if(hasReactions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_REACTIONS, String.valueOf(1));
        if (mentionedUids != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONED_UIDS, CometChatUtils.getCSStringFromList(mentionedUids));
        if (attachmentTypes != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_TYPE, CometChatUtils.getCSStringFromList(CometChatUtils.getListOfStringsFromType(attachmentTypes)));
        if (hideQuotedMessages) {
            queryParams.put(CometChatConstants.MessageKeys.KEY_HIDE_QUOTED_MESSAGES, String.valueOf(1));
        }

        Request getConversationsRequest = createGET(getApiUrl(String.format(URL_GROUP_GET_CONVERSATIONS, GUID)), getDefaultHeaders(), queryParams);
        makeApiCall(getConversationsRequest, listener, false);
    }

    void getUserConversationsInGroup(String UID, String GUID, int limit, @CometChatConstants.Affix String affix, long timestamp, long messageId, boolean unread, boolean hideMessagesFromBlockedUsers, String searchKeyword, long updatedAfter, boolean updatesOnly, List<String> categories, List<String> types, boolean hideReplies, boolean hideDeleted, List<String> tags, boolean withTags, boolean interactionGoalCompleted, boolean mentionsWithTagInfo, boolean mentionsWithBlockedInfo, boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes, boolean hideQuotedMessages, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, affix);
        queryParams.put(CometChatConstants.PaginationKeys.KEY_UID, UID);
        if (timestamp != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP, String.valueOf(timestamp));
        if (messageId != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID, String.valueOf(messageId));
        if (unread)
            queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (updatedAfter != -1)
            queryParams.put(CometChatConstants.Params.KEY_UPDATED_AT, String.valueOf(updatedAfter));
        if (updatesOnly)
            queryParams.put(CometChatConstants.Params.KEY_ONLY_UPDATES, String.valueOf(1));
        if (categories != null && categories.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_CATEGORIES, CometChatUtils.getCSStringFromList(categories));
        if (types != null && types.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_TYPES, CometChatUtils.getCSStringFromList(types));
        if (hideReplies)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_REPLIES, String.valueOf(1));
        if (hideDeleted)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_DELETED, String.valueOf(1));
        if (tags != null && tags.size() > 0)
            queryParams.put(CometChatConstants.MessageKeys.KEY_TAGS, CometChatUtils.getCSStringFromList(tags));
        if (withTags)
            queryParams.put(CometChatConstants.MessageKeys.KEY_WITH_TAGS, String.valueOf(1));
        if (interactionGoalCompleted)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ONLY_INTERACTION_GOAL_COMPLETED, String.valueOf(1));
        if (mentionsWithTagInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_TAG_INFO, String.valueOf(1));
        if (mentionsWithBlockedInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_BLOCKED_INFO, String.valueOf(1));
        if(hasAttachments)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_ATTACHMENTS, String.valueOf(1));
        if(hasLinks)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_LINKS, String.valueOf(1));
        if(hasMentions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_MENTIONS, String.valueOf(1));
        if(hasReactions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_REACTIONS, String.valueOf(1));
        if (mentionedUids != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONED_UIDS, CometChatUtils.getCSStringFromList(mentionedUids));
        if (attachmentTypes != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_TYPE, CometChatUtils.getCSStringFromList(CometChatUtils.getListOfStringsFromType(attachmentTypes)));
        if (hideQuotedMessages) {
            queryParams.put(CometChatConstants.MessageKeys.KEY_HIDE_QUOTED_MESSAGES, String.valueOf(1));
        }

        Request getConversationsRequest = createGET(getApiUrl(String.format(URL_GROUP_GET_CONVERSATIONS, GUID)), getDefaultHeaders(), queryParams);
        makeApiCall(getConversationsRequest, listener, false);
    }

    void getAllMessages(int limit, @CometChatConstants.Affix String affix, long timestamp, long messageId, boolean unread, boolean hideMessagesFromBlockedUsers, String searchKeyword, long updatedAfter, boolean updatesOnly, List<String> categories, List<String> types, boolean hideReplies, boolean hideDeleted, List<String> tags, boolean withTags, boolean interactionGoalCompleted, boolean mentionsWithTagInfo, boolean mentionsWithBlockedInfo, boolean hasAttachments, boolean hasLinks, boolean hasMentions, boolean hasReactions, List<String> mentionedUids, List<AttachmentType> attachmentTypes, boolean hideQuotedMessages, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, affix);
        if (timestamp != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP, String.valueOf(timestamp));
        if (messageId != -1)
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID, String.valueOf(messageId));
        if (unread)
            queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (updatedAfter != -1)
            queryParams.put(CometChatConstants.Params.KEY_UPDATED_AT, String.valueOf(updatedAfter));
        if (updatesOnly)
            queryParams.put(CometChatConstants.Params.KEY_ONLY_UPDATES, String.valueOf(1));
        if (categories != null && categories.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_CATEGORIES, CometChatUtils.getCSStringFromList(categories));
        if (types != null && types.size() > 0)
            queryParams.put(CometChatConstants.Params.KEY_MESSAGE_TYPES, CometChatUtils.getCSStringFromList(types));
        if (hideReplies)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_REPLIES, String.valueOf(1));
        if (hideDeleted)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_DELETED, String.valueOf(1));
        if (tags != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_TAGS, CometChatUtils.getCSStringFromList(tags));
        if (withTags)
            queryParams.put(CometChatConstants.MessageKeys.KEY_WITH_TAGS, String.valueOf(1));
        if (interactionGoalCompleted)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ONLY_INTERACTION_GOAL_COMPLETED, String.valueOf(1));
        if (mentionsWithTagInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_TAG_INFO, String.valueOf(1));
        if (mentionsWithBlockedInfo)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONS_WITH_BLOCKED_INFO, String.valueOf(1));
        if(hasAttachments)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_ATTACHMENTS, String.valueOf(1));
        if(hasLinks)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_LINKS, String.valueOf(1));
        if(hasMentions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_MENTIONS, String.valueOf(1));
        if(hasReactions)
            queryParams.put(CometChatConstants.MessageKeys.KEY_HAS_REACTIONS, String.valueOf(1));
        if (mentionedUids != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_MENTIONED_UIDS, CometChatUtils.getCSStringFromList(mentionedUids));
        if (attachmentTypes != null)
            queryParams.put(CometChatConstants.MessageKeys.KEY_ATTACHMENT_TYPE, CometChatUtils.getCSStringFromList(CometChatUtils.getListOfStringsFromType(attachmentTypes)));
        if (hideQuotedMessages) {
            queryParams.put(CometChatConstants.MessageKeys.KEY_HIDE_QUOTED_MESSAGES, String.valueOf(1));
        }

        Request getAllMessagesRequest = createGET(getApiUrl(URL_GET_USERS_MESSAGES), getDefaultHeaders(), queryParams);
        makeApiCall(getAllMessagesRequest, listener, false);
    }

    void blockUsers(@NonNull List<String> uids, APIConnectionListener listener) {
        JSONArray jsonArray = new JSONArray();
        for (String uid : uids) {
            jsonArray.put(uid);
            Logger.error(TAG, jsonArray.toString());
        }
        HashMap<String, JSONArray> queryParams = new HashMap<String, JSONArray>();
        queryParams.put(CometChatConstants.ResponseKeys.KEY_BLOCKED_UIDS, jsonArray);
        Request blockUserRequest = createPOSTForArray(getApiUrl(URL_BLOCK_USER), getDefaultHeaders(), queryParams);
        makeApiCall(blockUserRequest, listener, false);
    }

    void unblockUsers(@NonNull List<String> uids, APIConnectionListener listener) {
        JSONArray jsonArray = new JSONArray();
        for (String uid : uids) {
            jsonArray.put(uid);
            Logger.error(TAG, jsonArray.toString());
        }
        HashMap<String, JSONArray> queryParams = new HashMap<String, JSONArray>();
        queryParams.put(CometChatConstants.ResponseKeys.KEY_BLOCKED_UIDS, jsonArray);
        Request unblockUserRequest = createDELETEForArray(getApiUrl(URL_UNBLOCK_USER), getDefaultHeaders(), queryParams);
        makeApiCall(unblockUserRequest, listener, false);
    }

    void getMessageReceipts(long messageId, APIConnectionListener listener) {
        Request messageDetailsRequest = createGET(getApiUrl(String.format(URL_MESSAGE_DETAILS, messageId)), getDefaultHeaders(), null);
        makeApiCall(messageDetailsRequest, listener, false);
    }

    void getMessageDetails(long messageId, APIConnectionListener listener) {
        Request messageDetailsRequest = createGET(getApiUrl(String.format(URL_MESSAGE_DETAILS, messageId)), getDefaultHeaders(), null);
        makeApiCall(messageDetailsRequest, listener, false);
    }

    void flagMessage(long messageId, FlagDetail flagDetail, APIConnectionListener listener) {
        Request flagMessageRequest = createPOST(getApiUrl(String.format(URL_FLAG_MESSAGE, messageId)), getDefaultHeaders(), flagDetail.toMap());
        makeApiCall(flagMessageRequest, listener, false);
    }

    void getUnreadMessageCountForUser(String UID, boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request userUnreadMessageCountRequest = createGET(getApiUrl(String.format(URL_USER_GET_CONVERSATIONS, UID)), getDefaultHeaders(), queryParams);
        makeApiCall(userUnreadMessageCountRequest, listener, false);
    }

    void getUnreadMessageCountForGroup(String GUID, boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request groupUnreadMessageCountRequest = createGET(getApiUrl(String.format(URL_GROUP_GET_CONVERSATIONS, GUID)), getDefaultHeaders(), queryParams);
        makeApiCall(groupUnreadMessageCountRequest, listener, false);
    }

    void getUnreadMessageCount(boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request unreadMessageCountRequest = createGET(getApiUrl(URL_GET_USERS_MESSAGES), getDefaultHeaders(), queryParams);
        makeApiCall(unreadMessageCountRequest, listener, false);
    }

    void getUndeliveredMessageCountForUser(String UID, boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.KEY_UNDELIVERED, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request userUnreadMessageCountRequest = createGET(getApiUrl(String.format(URL_USER_GET_CONVERSATIONS, UID)), getDefaultHeaders(), queryParams);
        makeApiCall(userUnreadMessageCountRequest, listener, false);
    }

    void getUndeliveredMessageCountForGroup(String GUID, boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.KEY_UNDELIVERED, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request groupUnreadMessageCountRequest = createGET(getApiUrl(String.format(URL_GROUP_GET_CONVERSATIONS, GUID)), getDefaultHeaders(), queryParams);
        makeApiCall(groupUnreadMessageCountRequest, listener, false);
    }

    void getUndeliveredMessageCount(boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.KEY_UNDELIVERED, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request unreadMessageCountRequest = createGET(getApiUrl(URL_GET_USERS_MESSAGES), getDefaultHeaders(), queryParams);
        makeApiCall(unreadMessageCountRequest, listener, false);
    }

    void getUnreadMessageCountForAllUsers(boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_USER);
        queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request unreadMessageCountRequest = createGET(getApiUrl(URL_GET_USERS_MESSAGES), getDefaultHeaders(), queryParams);
        makeApiCall(unreadMessageCountRequest, listener, false);
    }

    void getUnreadMessageCountForAllGroups(boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
        queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request unreadMessageCountRequest = createGET(getApiUrl(URL_GET_USERS_MESSAGES), getDefaultHeaders(), queryParams);
        makeApiCall(unreadMessageCountRequest, listener, false);
    }

    void getUndeliveredMessageCountForAllUsers(boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_USER);
        queryParams.put(CometChatConstants.Params.KEY_UNDELIVERED, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request unreadMessageCountRequest = createGET(getApiUrl(URL_GET_USERS_MESSAGES), getDefaultHeaders(), queryParams);
        makeApiCall(unreadMessageCountRequest, listener, false);
    }

    void getUndeliveredMessageCountForAllGroups(boolean hideMessagesFromBlockedUsers, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
        queryParams.put(CometChatConstants.Params.KEY_UNDELIVERED, String.valueOf(1));
        queryParams.put(CometChatConstants.Params.KEY_COUNT, String.valueOf(1));
        if (hideMessagesFromBlockedUsers)
            queryParams.put(CometChatConstants.Params.KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS, String.valueOf(1));
        Request unreadMessageCountRequest = createGET(getApiUrl(URL_GET_USERS_MESSAGES), getDefaultHeaders(), queryParams);
        makeApiCall(unreadMessageCountRequest, listener, false);
    }

    // edit & delete messages

    void editMessage(BaseMessage message, APIConnectionListener listener) {
        HashMap<String, String> paramsMap = null;
        if (message instanceof TextMessage)
            paramsMap = ((TextMessage) message).toMap();
        if (message instanceof MediaMessage)
            paramsMap = ((MediaMessage) message).toMap();
        if (message instanceof CustomMessage)
            paramsMap = ((CustomMessage) message).toMap();
        Request editMessageRequest = createPUT(getApiUrl(String.format(URL_EDIT_MESSAGE, message.getId())), getDefaultHeaders(), paramsMap);
        makeApiCall(editMessageRequest, listener, false);
    }

    void deleteMessage(long messageId, APIConnectionListener listener) {
        Request deleteMessageRequest = createDELETE(getApiUrl(String.format(URL_DELETE_MESSAGE, messageId)), getDefaultHeaders(), null);
        makeApiCall(deleteMessageRequest, listener, false);
    }

    // Add Members to group

    void addMembersToGroup(String GUID, List<GroupMember> members, List<String> bannedUserIds, APIConnectionListener listener) {
        HashMap<String, JSONArray> params = getmembersMap(members, bannedUserIds);
        Request addMembersRequest = createPUTForArray(getApiUrl(String.format(URL_ADD_MEMBERS, GUID)), getDefaultHeaders(), params);
        makeApiCall(addMembersRequest, listener, false);
    }

    void registerTokenForPushNotification(final String fcmToken, final JSONObject pushNotificationParams, final APIConnectionListener listener) {
        try {
            Settings settings = SettingsRepo.getSettings();
            if (settings != null && settings.getChatAPIVersion() != null) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(CometChatConstants.AppInfoKeys.KEY_PLATFORM, CometChatUtils.getPlatform());
                params.put(CometChatConstants.AppInfoKeys.KEY_USER_AGENT, CometChatUtils.getAgent());
                params.put(CometChatConstants.AppInfoKeys.KEY_DEVICE_ID, CometChatUtils.getDeviceUniqueId(context));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_OS_VERSION, CometChatUtils.getAndroidVersion());
                    jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_VERSION, CometChatUtils.getSDKVersion());
                    jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_API_VERSION, String.valueOf(settings.getChatAPIVersion()));
                    JSONObject pushNotificationObject = new JSONObject();
                    pushNotificationObject.put("fcmDeviceToken", fcmToken);
                    if (pushNotificationParams != null) {
                        pushNotificationObject.put("settings", pushNotificationParams);

                    }
                    jsonObject.put("pushNotification", pushNotificationObject);
                    Logger.error(TAG, jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("appInfo", jsonObject.toString());
                Request registerPNRequest = createPUT(getApiUrl(URL_REGISTER_PUSH_NOTIFICATION), getDefaultHeaders(), params);
                makeApiCall(registerPNRequest, listener, false);
            } else {
                getSettingsRetry(new CometChat.CallbackListener<Settings>() {
                    @Override
                    public void onSuccess(Settings settings) {
                        getSettingsRetryCounter = 0;
                        registerTokenForPushNotification(fcmToken, pushNotificationParams, listener);
                    }

                    @Override
                    public void onError(final CometChatException e) {
                        if (Objects.equals(e.getMessage(), CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE)) {
                            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                        } else {
                            getSettingsRetryCounter++;
                            if (getSettingsRetryCounter < 4) {
                                registerTokenForPushNotification(fcmToken, pushNotificationParams, listener);
                            } else {
                                getSettingsRetryCounter = 0;
                                listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_APP_SETTINGS_NULL, CometChatConstants.Errors.ERROR_APP_SETTING_NULL_MESSAGE));
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Logger.error(TAG, "Error: " + e);
        }
    }

    private HashMap<String, JSONArray> getmembersMap(List<GroupMember> members, List<String> bannedUserIds) {
        HashMap<String, JSONArray> params = new HashMap<>();
        JSONArray adminArray = new JSONArray();
        JSONArray moderatorArray = new JSONArray();
        JSONArray participantArray = new JSONArray();
        JSONArray bannedArray = new JSONArray();
        for (GroupMember groupMember : members) {
            switch (groupMember.getScope()) {
                case CometChatConstants.SCOPE_ADMIN:
                    adminArray.put(groupMember.getUid());
                    break;
                case CometChatConstants.SCOPE_MODERATOR:
                    moderatorArray.put(groupMember.getUid());
                    break;
                case CometChatConstants.SCOPE_PARTICIPANT:
                    participantArray.put(groupMember.getUid());
                    break;
            }
        }
        if (bannedUserIds != null && bannedUserIds.size() > 0) {
            for (String bannedUserId : bannedUserIds) {
                bannedArray.put(bannedUserId);
            }
        }
        if (adminArray.length() > 0)
            params.put(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_ADMINS, adminArray);
        if (moderatorArray.length() > 0)
            params.put(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_MODERATORS, moderatorArray);
        if (participantArray.length() > 0)
            params.put(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_PARTICIPANTS, participantArray);
        if (bannedArray.length() > 0)
            params.put(CometChatConstants.ExtraKeys.KEY_ADD_MEMBER_BANNED, bannedArray);
        Logger.error(TAG, params.toString());
        return params;
    }

    // Conversations
    void getConversations(int limit, @CometChatConstants.ConversationTypes String conversationType, boolean withUserAndGroupTags, List<String> tags, boolean withTags, int nextPage, List<String> userTags, List<String> groupTags, boolean includeBlockedUsers, boolean withBlockedInfo,String searchKeyword, boolean unread, boolean hideAgentic, boolean onlyAgentic, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.Params.LIMIT, String.valueOf(limit));
        if (conversationType != null)
            queryParams.put(CometChatConstants.ConversationKeys.KEY_CONVERSATION_TYPE, conversationType);
        if (nextPage != 0)
            queryParams.put(CometChatConstants.Params.PAGE, String.valueOf(nextPage));
        if (withUserAndGroupTags)
            queryParams.put(CometChatConstants.ConversationKeys.KEY_WITH_USER_AND_GROUP_TAGS, String.valueOf(1));
        if (tags != null && tags.size() > 0)
            queryParams.put(CometChatConstants.ConversationKeys.KEY_TAGS, CometChatUtils.getCSStringFromList(tags));
        if (withTags)
            queryParams.put(CometChatConstants.ConversationKeys.KEY_WITH_TAGS, String.valueOf(1));
        if (userTags != null && userTags.size() > 0)
            queryParams.put(CometChatConstants.ConversationKeys.KEY_USER_TAGS, CometChatUtils.getCSStringFromList(userTags));
        if (groupTags != null && groupTags.size() > 0)
            queryParams.put(CometChatConstants.ConversationKeys.KEY_GROUP_TAGS, CometChatUtils.getCSStringFromList(groupTags));
        if (includeBlockedUsers)
            queryParams.put(CometChatConstants.ConversationKeys.KEY_INCLUDE_BLOCKED_USERS, String.valueOf(1));
        if (withBlockedInfo)
            queryParams.put(CometChatConstants.ConversationKeys.KEY_WITH_BLOCKED_INFO, String.valueOf(1));
        if (searchKeyword != null)
            queryParams.put(CometChatConstants.Params.KEY_SEARCH_KEYWORD, searchKeyword);
        if (unread)
            queryParams.put(CometChatConstants.Params.KEY_UNREAD, String.valueOf(1));
        if (hideAgentic)
            queryParams.put(CometChatConstants.Params.HIDE_AGENTIC, String.valueOf(1));
        if (onlyAgentic)
            queryParams.put(CometChatConstants.Params.ONLY_AGENTIC, String.valueOf(1));

        Request conversationRequest = createGET(getApiUrl(URL_GET_CONVERSATIONS), getDefaultHeaders(), queryParams);
        makeApiCall(conversationRequest, listener, false);
    }

    void getConversation(final String conversationWith, final String conversationType, final APIConnectionListener listener) {
        String url = null;
        if (conversationType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
            url = String.format(URL_GET_USER_CONVERSATION, conversationWith);
        } else {
            url = String.format(URL_GET_GROUP_CONVERSATION, conversationWith);
        }
        Request getConversationRequest = createGET(getApiUrl(url), getDefaultHeaders(), null);
        makeApiCall(getConversationRequest, listener, false);
    }

    void tagConversation(String conversationWith, String conversationType, List<String> tags, APIConnectionListener listener) {
        String url = null;
        if (conversationType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            url = String.format(URL_GET_USER_CONVERSATION, conversationWith);
        else
            url = String.format(URL_GET_GROUP_CONVERSATION, conversationWith);
        HashMap<String, JSONArray> params = new HashMap<>();
        params.put(CometChatConstants.ConversationKeys.KEY_TAGS, getJSONArrayFromList(tags));
        Request getConversationRequest = createPUTForArray(getApiUrl(url), getDefaultHeaders(), params);
        makeApiCall(getConversationRequest, listener, false);
    }

    private JSONArray getJSONArrayFromList(List<String> tags) {
        JSONArray jsonArray = new JSONArray();
        for (String tag : tags) {
            jsonArray.put(tag);
        }
        return jsonArray;
    }

    void callExtension(final String url, final String requestType, final JSONObject body, final APIConnectionListener listener) {
        try {
            CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
            Settings settings = SettingsRepo.getSettings();
            if (settings != null && settings.getChatAPIVersion() != null) {
                Request.Builder extensionRequestBuilder = new Request.Builder();
                extensionRequestBuilder.url(URLDecoder.decode(url));
                RequestBody requestBody = null;
                if (body != null) {
                    requestBody = RequestBody.create(MediaType.parse("application/json"), body.toString());
                }
                if (requestType.equalsIgnoreCase("POST")) {
                    if (requestBody == null)
                        requestBody = new FormBody.Builder().build();
                    extensionRequestBuilder.post(requestBody);
                } else if (requestType.equalsIgnoreCase("PUT")) {
                    if (requestBody == null)
                        requestBody = new FormBody.Builder().build();
                    extensionRequestBuilder.put(requestBody);
                } else if (requestType.equalsIgnoreCase("PATCH")) {
                    if (requestBody == null)
                        requestBody = new FormBody.Builder().build();
                    extensionRequestBuilder.patch(requestBody);
                } else if (requestType.equalsIgnoreCase("DELETE")) {
                    if (requestBody == null) {
                        extensionRequestBuilder.delete();
                    } else {
                        extensionRequestBuilder.delete(requestBody);
                    }
                } else if (requestType.equalsIgnoreCase("GET")) {
                    extensionRequestBuilder.get();
                }
                Headers.Builder headersBuilder = new Headers.Builder()
                        .add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID())
                        .add(CometChatConstants.Params.AUTHTOKEN, CurrentUserRepo.getCurrentUser().getAuthToken())
                        .add(CometChatConstants.Params.CONTENT_TYPE, CometChatConstants.Params.CONTENT_TYPE_JSON_VALUE)
                        .add(CometChatConstants.Params.KEY_HEADER_RESOURCE, CometChatUtils.getResource(false))
                        .add(CometChatConstants.Params.KEY_HEADER_SDK_VERSION, CometChatUtils.getPlatform().toLowerCase() + "@" + CometChatUtils.getSDKVersion())
                        .add(CometChatConstants.Params.KEY_CHAT_API_VERSION, settings.getChatAPIVersion());
                if (currentUser.getJwt() != null)
                    headersBuilder.add(CometChatConstants.Params.KEY_AUTHORIZATION, "Bearer " + currentUser.getJwt());
                Headers headers = headersBuilder.build();
                extensionRequestBuilder.headers(headers);
                makeApiCall(extensionRequestBuilder.build(), listener, false);
            } else {
                getSettingsRetry(new CometChat.CallbackListener<Settings>() {
                    @Override
                    public void onSuccess(Settings settings) {
                        getSettingsRetryCounter = 0;
                        callExtension(url, requestType, body, listener);
                    }

                    @Override
                    public void onError(final CometChatException e) {
                        if (Objects.equals(e.getMessage(), CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE)) {
                            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                        } else {
                            getSettingsRetryCounter++;
                            if (getSettingsRetryCounter < 4) {
                                callExtension(url, requestType, body, listener);
                            } else {
                                getSettingsRetryCounter = 0;
                                listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_APP_SETTINGS_NULL, CometChatConstants.Errors.ERROR_APP_SETTING_NULL_MESSAGE));
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Logger.error(TAG, "Error: " + e);
        }
    }

    //Mark as delivered and read
    void markAsDelivered(MessageReceipt messageReceipt, APIConnectionListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("messageId", String.valueOf(messageReceipt.getMessageId()));
        String receiverType = "";
        if (messageReceipt.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            receiverType = "users";
        } else {
            receiverType = "groups";
        }
        Request unreadMessageCountRequest = createPOST(getApiUrl(String.format(URL_MARK_AS_DELIVERED, receiverType, messageReceipt.getReceiverId())), getDefaultHeaders(), params);
        makeApiCall(unreadMessageCountRequest, listener, false);
    }

    void markAsRead(MessageReceipt messageReceipt, APIConnectionListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("messageId", String.valueOf(messageReceipt.getMessageId()));
        String receiverType = "";
        if (messageReceipt.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            receiverType = "users";
        } else {
            receiverType = "groups";
        }
        Request unreadMessageCountRequest = createPOST(getApiUrl(String.format(URL_MARK_AS_READ, receiverType, messageReceipt.getReceiverId())), getDefaultHeaders(), params);
        makeApiCall(unreadMessageCountRequest, listener, false);
    }

    void markConversationAsDelivered(String conversationWithId, String receiverType, APIConnectionListener listener) {
        try {
            String finalReceiverType;
            if (receiverType.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                finalReceiverType = "users";
            } else {
                finalReceiverType = "groups";
            }
            Request markConversationAsDeliveredRequest = createPOST(getApiUrl(String.format(URL_MARK_AS_DELIVERED, finalReceiverType, conversationWithId)), getDefaultHeaders(), null);
            makeApiCall(markConversationAsDeliveredRequest, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
        }
    }

    void markConversationAsRead(String conversationWithId, String receiverType, APIConnectionListener listener) {
        try {
            String finalReceiverType;
            if (receiverType.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                finalReceiverType = "users";
            } else {
                finalReceiverType = "groups";
            }
            Request markConversationAsDeliveredRequest = createPOST(getApiUrl(String.format(URL_MARK_AS_READ, finalReceiverType, conversationWithId)), getDefaultHeaders(), null);
            makeApiCall(markConversationAsDeliveredRequest, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
        }
    }

    void markMessageAsUnread(long messageId, String uid, String receiverType, APIConnectionListener listener) {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("messageId", String.valueOf(messageId));
            String finalReceiverType;
            if (receiverType.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                finalReceiverType = "users";
            } else {
                finalReceiverType = "groups";
            }
            Request markConversationAsDeliveredRequest = createDELETE(getApiUrl(String.format(URL_MARK_AS_READ, finalReceiverType, uid)), getDefaultHeaders(), params);
            makeApiCall(markConversationAsDeliveredRequest, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
        }
    }

    void markAsUnread(MessageReceipt messageReceipt, APIConnectionListener listener) {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("messageId", String.valueOf(messageReceipt.getMessageId()));
            String receiverType = messageReceipt.getReceiverType();
            String id = messageReceipt.getReceiverId();
            Request unreadMessageCountRequest = createDELETE(getApiUrl(String.format(URL_MARK_AS_READ, receiverType, id)), getDefaultHeaders(), params);
            makeApiCall(unreadMessageCountRequest, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
        }
    }

    void markAsInteracted(long messageId, JSONArray interactedElementIdArray, APIConnectionListener listener) {
        try {
            HashMap<String, JSONArray> params = new HashMap<>();
            params.put(CometChatConstants.MessageKeys.KEY_INTERACTIONS, interactedElementIdArray);
            Request markAsInteractedRequest = createPATCHForArray(getApiUrl(String.format(URL_MARK_AS_INTERACTED, messageId)), getDefaultHeaders(), params);
            makeApiCall(markAsInteractedRequest, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
        }
    }

    //Reactions
    void addReaction(long messageId, String emoji, APIConnectionListener listener) {
        try {
            HashMap<String, String> params = new HashMap<>();
            String encodedReactionString = URLEncoder.encode(emoji, "UTF-8");
            Request reactionRequest = createPOST(getApiUrl(String.format(URL_REACTIONS, messageId, encodedReactionString)), getDefaultHeaders(), params);
            makeApiCall(reactionRequest, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
        }
    }

    void removeReaction(long messageId, String emoji, APIConnectionListener listener) {
        try {
            HashMap<String, String> params = new HashMap<>();
            String encodedReactionString = URLEncoder.encode(emoji, "UTF-8");
            Request reactionRequest = createDELETE(getApiUrl(String.format(URL_REACTIONS, messageId, encodedReactionString)), getDefaultHeaders(), params);
            makeApiCall(reactionRequest, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
        }
    }

    void getReactedUsersList(int limit, String affix, long messageId, String reactionId, String emoji, APIConnectionListener listener) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("limit", String.valueOf(limit));
            params.put("affix", affix);
            if (reactionId != null) {
                params.put("id", reactionId);
            }
            String url = "";
            if (emoji != null) {
                String encodedReactionString = URLEncoder.encode(emoji, "UTF-8");
                url = String.format(URL_REACTIONS, messageId, encodedReactionString);
            } else {
                url = String.format(URL_REACTIONS_WITHOUT_REACTION, messageId);
            }
            Request reactionRequest = createGET(getApiUrl(url), getDefaultHeaders(), params);
            makeApiCall(reactionRequest, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
        }
    }

    // User Management methods
    void createUser(User user, String apiKey, APIConnectionListener listener) {
        Headers headers = new Headers.Builder()
                .add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID())
                .add(CometChatConstants.Params.APIKEY, apiKey)
                .add(CometChatConstants.Params.KEY_HEADER_SDK_VERSION, CometChatUtils.getPlatform().toLowerCase() + "@" + CometChatUtils.getSDKVersion())
                .build();
        Request createUserRequest = createPOST(getAdminUrl(URL_CREATE_USER), headers, user.toMap());
        makeApiCall(createUserRequest, listener, false);
    }

    void updateUser(User user, String apiKey, APIConnectionListener listener) {
        Headers headers = new Headers.Builder()
                .add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID())
                .add(CometChatConstants.Params.APIKEY, apiKey)
                .add(CometChatConstants.Params.KEY_HEADER_SDK_VERSION, CometChatUtils.getPlatform().toLowerCase() + "@" + CometChatUtils.getSDKVersion())
                .build();
        Request updateUserRequest = createPUT(getAdminUrl(String.format(URL_UPDATE_USER, user.getUid())), headers, user.toMap());
        makeApiCall(updateUserRequest, listener, false);
    }

    void pollMessages(long timestamp, long messageId, int limit, String affix, APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.PaginationKeys.KEY_PER_PAGE, String.valueOf(limit));
        queryParams.put(CometChatConstants.PaginationKeys.KEY_AFFIX, affix);
        if (timestamp != -1 && timestamp != 0) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP, String.valueOf(timestamp));
        }
        if (messageId != -1 && messageId != 0) {
            queryParams.put(CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID, String.valueOf(messageId));
        }
        queryParams.put(CometChatConstants.Params.KEY_POLLING, String.valueOf(1));
        Request getAllMessagesRequest = createGET(getApiUrl(URL_POLL_MESSAGES), getDefaultHeaders(), queryParams);
        makeApiCall(getAllMessagesRequest, listener, false);
    }

    void pingAnalytics(final APIConnectionListener listener) {
        Settings settings = SettingsRepo.getSettings();
        CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
        if (settings != null && currentUser != null) {
            Headers.Builder headersBuilder = new Headers.Builder();
            headersBuilder.add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID());
            if (settings.getSettingsHash() != null)
                headersBuilder.add(CometChatConstants.Params.KEY_SETTINGS_HASH, settings.getSettingsHash());
            if (settings.getSettingsHashReceivedAt() != 0)
                headersBuilder.add(CometChatConstants.Params.KEY_SETTINGS_HASH_RECEIVED_AT, String.valueOf(settings.getSettingsHashReceivedAt()));
            if (currentUser.getJwt() != null)
                headersBuilder.add(CometChatConstants.Params.KEY_AUTHORIZATION, "Bearer " + currentUser.getJwt());
            if (currentUser.getAuthToken() != null)
                headersBuilder.add(CometChatConstants.Params.AUTHTOKEN, currentUser.getAuthToken());
            headersBuilder.add(CometChatConstants.Params.KEY_HEADER_SDK_VERSION, CometChatUtils.getPlatform().toLowerCase() + "@" + CometChatUtils.getSDKVersion());
            headersBuilder.add(CometChatConstants.Params.CONTENT_TYPE, CometChatConstants.Params.CONTENT_TYPE_JSON_VALUE);
            Headers headers = headersBuilder.build();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(CometChatConstants.Params.UID, PreferenceHelper.getLoggedInUID());
            params.put(CometChatConstants.AppInfoKeys.KEY_USER_AGENT, CometChatUtils.getAgent());
            params.put(CometChatConstants.AppInfoKeys.KEY_WS_ID, CometChatUtils.getResource(false));
            params.put(CometChatConstants.AppInfoKeys.KEY_DEVICE_ID, CometChatUtils.getDeviceUniqueId(context));
            params.put(CometChatConstants.AppInfoKeys.KEY_PLATFORM, CometChatUtils.getPlatform());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_OS_VERSION, CometChatUtils.getAndroidVersion());
                jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_VERSION, CometChatUtils.getSDKVersion());
                jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO_API_VERSION, settings.getChatAPIVersion());
                if (PreferenceHelper.getResource() != null)
                    jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_RESOURCE, PreferenceHelper.getResource());
                if (PreferenceHelper.getPlatform() != null)
                    jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_PLATFORM, PreferenceHelper.getPlatform());
                if (PreferenceHelper.getLanguage() != null)
                    jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_LANGUAGE, PreferenceHelper.getLanguage());
                jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_ORIGIN, context.getPackageName());
                jsonObject.put(CometChatConstants.AppInfoKeys.KEY_APPINFO_UTS, System.currentTimeMillis());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            params.put(CometChatConstants.AppInfoKeys.KEY_APP_INFO, jsonObject.toString());
            String url = getAnalyticsUrl(URL_ANALYTICS_PING);
            Request pingAnalyticsRequest = createPOST(url, headers, params);
            makeApiCall(pingAnalyticsRequest, listener, false);
        } else {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
        }
    }

    void getCallParticipantCount(@NonNull final String sessionId, @NonNull final APIConnectionListener listener) {
        try {
            Settings settings = SettingsRepo.getSettings();
            if (settings != null && settings.getWEBRTCHost() != null && settings.getWebrtcAPISubDomain() != null) {
                HashMap<String, String> queryParams = new HashMap<>();
                queryParams.put(CometChatConstants.Params.KEY_DOMAIN, settings.getWEBRTCHost());
                queryParams.put(CometChatConstants.Params.KEY_ROOM, sessionId);
                String URL;
                URL = String.format(URL_CALL_PARTICIPANT_COUNT, settings.getWebrtcAPISubDomain(), settings.getWEBRTCHost());
                HttpUrl.Builder httpBuilder = HttpUrl.parse(URL).newBuilder();
                if (queryParams != null) {
                    for (Map.Entry<String, String> param : queryParams.entrySet()) {
                        httpBuilder.addQueryParameter(param.getKey(), param.getValue());
                    }
                }
                Request request = new Request.Builder()
                        .url(URLDecoder.decode(httpBuilder.build().toString()))
                        .build();
                Logger.error(TAG, "API_URL : " + request.url());
                Logger.error(TAG, "API_REQUEST_TYPE : " + request.method());
                if (!CometChatUtils.isConnectedToNetwork(context)) {
                    listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_INTERNET_UNAVAILABLE, CometChatConstants.Errors.ERROR_INTERNET_UNAVAILABLE_MESSAGE));
                } else {
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_IO_EXCEPTION, e.getMessage()));
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.code() == CometChatConstants.ResponseKeys.CODE_REQUEST_OK) {
                                listener.onResponse(response.body().string(), null);
                            } else {
                                listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_NO_PARTICIPANTS, CometChatConstants.Errors.ERROR_NO_PARTICIPANTS_MESSAGE));
                            }
                        }
                    });
                }
            } else {
                getSettingsRetry(new CometChat.CallbackListener<Settings>() {
                    @Override
                    public void onSuccess(Settings settings) {
                        getSettingsRetryCounter = 0;
                        getCallParticipantCount(sessionId, listener);
                    }

                    @Override
                    public void onError(final CometChatException e) {
                        if (Objects.equals(e.getMessage(), CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE)) {
                            Logger.error(TAG, "Error: " + e.getMessage());
                            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                        } else {
                            getSettingsRetryCounter++;
                            if (getSettingsRetryCounter < 4) {
                                getCallParticipantCount(sessionId, listener);
                            } else {
                                Logger.error(TAG, "Error: " + e.getMessage());
                                getSettingsRetryCounter = 0;
                                listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_APP_SETTINGS_NULL, CometChatConstants.Errors.ERROR_APP_SETTING_NULL_MESSAGE));
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Logger.error(TAG, "Error: Unable to get the call participant count; " + e);
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_NO_PARTICIPANTS, CometChatConstants.Errors.ERROR_NO_PARTICIPANTS_MESSAGE + ". Reason: " + e));
        }
    }

    void transferGroupOwnership(String GUID, String UID, APIConnectionListener connectionListener) {
        HashMap<String, String> body = new HashMap<>();
        body.put(CometChatConstants.Params.OWNER, UID);
        Request transferOwnershipRequest = createPATCH(getApiUrl(String.format(URL_TRANSFER_OWNERSHIP, GUID)), getDefaultHeaders(), body);
        makeApiCall(transferOwnershipRequest, connectionListener, false);
    }

    void getCallingJWT(@NonNull String UID, @NonNull String sessionID, APIConnectionListener listener) {
        JSONObject passthroughObject = new JSONObject();
        try {
            passthroughObject.put(CometChatConstants.UserKeys.USER_KEY_UID, UID);
            passthroughObject.put(CometChatConstants.CallKeys.CALL_SESSION_ID_CAMEL_CASE, sessionID);
        } catch (JSONException e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
            return;
        }
        HashMap<String, String> postBody = new HashMap<>();
        postBody.put(CometChatConstants.ExtraKeys.KEY_CALLING_JWT_PASSTHROUGH, passthroughObject.toString());
        Request getCallingJWTRequest = createPOST(getApiUrl(URL_CALL_JWT), getDefaultHeaders(), postBody);
        makeApiCall(getCallingJWTRequest, listener, false);
    }

    void updateCurrentUserDetails(@NonNull User user, APIConnectionListener listener) {
        Request updateMyDetailsRequest = createPUT(getApiUrl(URL_UPDATE_MY_DETAILS), getDefaultHeaders(), user.toMap());
        makeApiCall(updateMyDetailsRequest, listener, false);
    }

    void deleteConversation(@NonNull final String conversationWith, @NonNull final String conversationType, final APIConnectionListener listener) {
        try {
            String url = null;
            if (conversationType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
                url = String.format(URL_DELETE_USER_CONVERSATION, conversationWith);
            } else {
                url = String.format(URL_DELETE_GROUP_CONVERSATION, conversationWith);
            }
            Request deleteConversationRequest = createDELETE(getApiUrl(url), getDefaultHeaders(), null);
            makeApiCall(deleteConversationRequest, listener, false);
        } catch (Exception e) {
            Logger.error(TAG, "Error: Unable to delete conversation; " + e);
        }
    }

    void getOnlineUserCount(final APIConnectionListener connectionListener) {
        try {
            Settings settings = SettingsRepo.getSettings();
            if (settings != null) {
                String url = CometChatConstants.ExtraKeys.KEY_HTTPS + CometChatUtils.getFinalChatHost(settings) + String.format(URL_GET_ONLINE_USERS, settings.getWsAPIVersion());
                Request onlineUserCountRequest = createPOST(url, getWSDefaultHeaders(), null);
                makeApiCall(onlineUserCountRequest, connectionListener, false);
            } else {
                getSettingsRetry(new CometChat.CallbackListener<Settings>() {
                    @Override
                    public void onSuccess(Settings settings) {
                        getSettingsRetryCounter = 0;
                        getOnlineUserCount(connectionListener);
                    }

                    @Override
                    public void onError(final CometChatException e) {
                        if (Objects.equals(e.getMessage(), CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE)) {
                            Logger.error(TAG, "Error: " + e.getMessage());
                            connectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                        } else {
                            getSettingsRetryCounter++;
                            if (getSettingsRetryCounter < 3) {
                                getOnlineUserCount(connectionListener);
                            } else {
                                Logger.error(TAG, "Error: " + e.getMessage());
                                getSettingsRetryCounter = 0;
                                connectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_APP_SETTINGS_NULL, CometChatConstants.Errors.ERROR_APP_SETTING_NULL_MESSAGE));
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Logger.error(TAG, "Error: Unable to get online user count; " + e);
        }
    }

    void getOnlineGroupMemberCount(final List<String> guids, final APIConnectionListener connectionListener) {
        try {
            Settings settings = SettingsRepo.getSettings();
            if (settings != null) {
                JSONArray guidsArray = getGUIDsArray(guids);
                HashMap<String, JSONArray> queryParams = new HashMap<String, JSONArray>();
                queryParams.put(CometChatConstants.Params.KEY_GROUPS, guidsArray);
                String url = CometChatConstants.ExtraKeys.KEY_HTTPS + CometChatUtils.getFinalChatHost(settings) + String.format(URL_GET_ONLINE_USERS, settings.getWsAPIVersion());
                Request onlineUserCountRequest = createPOSTForArray(url, getWSDefaultHeaders(), queryParams);
                makeApiCall(onlineUserCountRequest, connectionListener, false);
            } else {
                getSettingsRetry(new CometChat.CallbackListener<Settings>() {
                    @Override
                    public void onSuccess(Settings settings) {
                        getSettingsRetryCounter = 0;
                        getOnlineGroupMemberCount(guids, connectionListener);
                    }

                    @Override
                    public void onError(final CometChatException e) {
                        if (Objects.equals(e.getMessage(), CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE)) {
                            Logger.error(TAG, "Error: " + e.getMessage());
                            connectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                        } else {
                            getSettingsRetryCounter++;
                            if (getSettingsRetryCounter < 3) {
                                getOnlineGroupMemberCount(guids, connectionListener);
                            } else {
                                Logger.error(TAG, "Error: " + e.getMessage());
                                getSettingsRetryCounter = 0;
                                connectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_APP_SETTINGS_NULL, CometChatConstants.Errors.ERROR_APP_SETTING_NULL_MESSAGE));
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Logger.error(TAG, "Error: Unable to get group online member count; " + e);
        }
    }

    void switchCallFromAudioToVideo(String sessionId, APIConnectionListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put(CometChatConstants.CallKeys.CALL_TYPE, CometChatConstants.CALL_TYPE_VIDEO);
        Request switchAudioToVideoRequest = createPATCH(getApiUrl(String.format(URL_SWITCH_AUDIO_CALL_TO_VIDEO, sessionId)), getDefaultHeaders(), params);
        makeApiCall(switchAudioToVideoRequest, listener, false);
    }

    private JSONArray getGUIDsArray(List<String> guids) {
        JSONArray jsonArray = new JSONArray();
        for (String guid : guids) {
            jsonArray.put(guid);
        }
        return jsonArray;
    }

    void createGroupWithMembers(Group group, List<GroupMember> members, List<String> bannedUserIds, APIConnectionListener listener) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject membersObject = new JSONObject();
            for (Map.Entry<String, String> stringStringEntry : group.toMap().entrySet()) {
                Map.Entry pair = (Map.Entry) stringStringEntry;
                jsonObject.put((String) pair.getKey(), (String) pair.getValue());
            }
            for (Map.Entry<String, JSONArray> stringStringEntry : getmembersMap(members, bannedUserIds).entrySet()) {
                Map.Entry pair = (Map.Entry) stringStringEntry;
                membersObject.put((String) pair.getKey(), (JSONArray) pair.getValue());
            }
            jsonObject.put("members", membersObject);
            Logger.error(TAG, jsonObject.toString());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Request request = new Request.Builder()
                    .url(URLDecoder.decode(getApiUrl(URL_CREATE_GROUP)))
                    .post(requestBody)
                    .headers(getDefaultHeaders())
                    .build();
            makeApiCall(request, listener, false);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, e.getMessage()));
        }
    }

    void getSmartReplies(String receiverId, String receiverType, JSONObject configuration, APIConnectionListener listener) {
        String url = receiverType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER) ? URL_AI_GET_USER_SMART_REPLIES : URL_AI_GET_GROUP_SMART_REPLIES;
        url = getApiUrl(String.format(url, receiverId));
        Request smartRepliesRequest = createGETWithJSONObject(url, getAIDefaultHeaders(), configuration, null);
        makeApiCall(smartRepliesRequest, listener, false);

    }

    void getConversationStarter(String receiverId, String receiverType, JSONObject configuration, APIConnectionListener listener) {
        String url = receiverType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER) ? URL_AI_GET_USER_CONVERSATION_STARTER : URL_AI_GET_GROUP_CONVERSATION_STARTER;
        url = getApiUrl(String.format(url, receiverId));
        Request conversationStarterRequest = createGETWithJSONObject(url, getAIDefaultHeaders(), configuration, null);
        makeApiCall(conversationStarterRequest, listener, false);
    }

    void getConversationSummary(String receiverId, String receiverType, JSONObject configuration, APIConnectionListener listener) {
        String url = receiverType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER) ? URL_AI_GET_USER_CONVERSATION_SUMMARY : URL_AI_GET_GROUP_CONVERSATION_SUMMARY;
        url = getApiUrl(String.format(url, receiverId));
        Request conversationsSummaryRequest = createGETWithJSONObject(url, getAIDefaultHeaders(), configuration, null);
        makeApiCall(conversationsSummaryRequest, listener, false);
    }

    void askBot(String receiverId, String receiverType, String botId, String question, JSONObject configuration, APIConnectionListener listener) {
        String url = receiverType.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER) ? URL_AI_GET_USER_ASK_BOT : URL_AI_GET_GROUP_ASK_BOT;
        url = getApiUrl(String.format(url, botId, receiverId));
        HashMap<String, String> extraParams = new HashMap<>();
        extraParams.put("question", question);
        Request askBotRequest = createGETWithJSONObject(url, getAIDefaultHeaders(), configuration, extraParams);
        makeApiCall(askBotRequest, listener, false);

    }

    private Request createGETWithJSONObject(String url, Headers headers, JSONObject configuration, HashMap<String, String> extraParams) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (configuration != null) {
            try {
                Iterator<?> keys = configuration.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (configuration.get(key) instanceof JSONObject || configuration.get(key) instanceof JSONArray) {
                        httpBuilder.addQueryParameter(key, configuration.get(key).toString());
                    } else {
                        httpBuilder.addQueryParameter(key, String.valueOf(configuration.get(key)));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (extraParams != null) {
            Iterator it = extraParams.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                httpBuilder.addQueryParameter((String) pair.getKey(), (String) pair.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(URLDecoder.decode(httpBuilder.build().toString()))
                .get()
                .headers(headers)
                .build();
        return request;
    }

    void sdkIdentification(final APIConnectionListener listener) {
        try {
            //Create a HashMap to store the data
            HashMap<String, Object> rootNode = new HashMap<>();
            HashMap<String, Object> data = new HashMap<>();
            // Add the cometchat data
            HashMap<String, Object> cometchatData = new HashMap<>();
            if (CometChatUtils.getMetaInfo() != null) {
                HashMap<String, Object> map = jsonToHashMap(CometChatUtils.getMetaInfo());
                data.put(CometChatConstants.SdkIdentificationKeys.COMETCHAT, map);
            } else {
                // Chat SDK
                HashMap<String, Object> chatsdkData = new HashMap<>();
                chatsdkData.put(CometChatConstants.SdkIdentificationKeys.PLATFORM, CometChatUtils.getPlatform());
                chatsdkData.put(CometChatConstants.SdkIdentificationKeys.VERSION, BuildConfig.VERSION_NAME);
                cometchatData.put(CometChatConstants.SdkIdentificationKeys.CHAT_SDK, chatsdkData);
                // Calls SDK
                String callsSdkVersion = callStaticMethodUsingReflection("com.cometchat.calls.core.CometChatCalls", "getSDKVersion");
                if (callsSdkVersion != null) {
                    HashMap<String, Object> callssdkData = new HashMap<>();
                    callssdkData.put(CometChatConstants.SdkIdentificationKeys.PLATFORM, CometChatUtils.getPlatform());
                    callssdkData.put(CometChatConstants.SdkIdentificationKeys.VERSION, callsSdkVersion);
                    cometchatData.put(CometChatConstants.SdkIdentificationKeys.CALLS_SDK, callssdkData);
                }
                // UI KIT SDK
                String uiKitSdkVersion = callStaticMethodUsingReflection("com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit", "getSDKVersion");
                if (uiKitSdkVersion != null) {
                    HashMap<String, Object> uikitData = new HashMap<>();
                    uikitData.put(CometChatConstants.SdkIdentificationKeys.PLATFORM, CometChatUtils.getPlatform());
                    uikitData.put(CometChatConstants.SdkIdentificationKeys.VERSION, uiKitSdkVersion);
                    cometchatData.put(CometChatConstants.SdkIdentificationKeys.UI_KIT, uikitData);
                }
                // Builder Module
                // The detection is different because in builder module BuilderSettingsHelper is a kotlin object
                try {
                    Class<?> clazz = Class.forName("com.cometchat.builder.BuilderSettingsHelper");
                    Object instance = clazz.getField("INSTANCE").get(null);
                    Method method = clazz.getMethod("getSDKVersion");
                    String builderVersion = (String) method.invoke(instance);
                    if (builderVersion != null) {
                        HashMap<String, Object> builderData = new HashMap<>();
                        builderData.put(CometChatConstants.SdkIdentificationKeys.PLATFORM, CometChatUtils.getPlatform());
                        builderData.put(CometChatConstants.SdkIdentificationKeys.VERSION, builderVersion);
                        cometchatData.put(CometChatConstants.SdkIdentificationKeys.BUILDER, builderData);
                    }
                } catch (Exception error) {
                    Logger.error(TAG, "Error: builder identification: " + error);
                }
                Logger.error(TAG, "cometchat sdk identification data: " + cometchatData);
                data.put(CometChatConstants.SdkIdentificationKeys.COMETCHAT, cometchatData);
            }
            // Demo Data
            if (CometChatUtils.getDemoMetaInfo() != null) {
                HashMap<String, Object> map = jsonToHashMap(CometChatUtils.getDemoMetaInfo());
                data.put(CometChatConstants.SdkIdentificationKeys.DEMO, map);
            }
            // Add the app data
            HashMap<String, Object> appData = new HashMap<>();
            appData.put(CometChatConstants.SdkIdentificationKeys.NAME, getAppName());
            appData.put(CometChatConstants.SdkIdentificationKeys.VERSION, getAppVersion(context));
            appData.put(CometChatConstants.SdkIdentificationKeys.PLATFORM, CometChatUtils.getPlatform());
            appData.put(CometChatConstants.SdkIdentificationKeys.BUNDLE_ID, context.getPackageName());
            appData.put(CometChatConstants.SdkIdentificationKeys.DEBUG, isAppDebuggable());
            // App data put and close
            data.put(CometChatConstants.SdkIdentificationKeys.APP, appData);
            // Add the device data
            HashMap<String, Object> deviceData = new HashMap<>();
            deviceData.put(CometChatConstants.SdkIdentificationKeys.ID, CometChatUtils.getDeviceUniqueId(context));
            deviceData.put(CometChatConstants.SdkIdentificationKeys.PLATFORM, CometChatConstants.AppInfoKeys.KEY_PLATFORM_ANDROID);
            deviceData.put(CometChatConstants.SdkIdentificationKeys.LANGUAGE, getLocalLanguage());
            deviceData.put(CometChatConstants.SdkIdentificationKeys.MODEL, getMobileDeviceModel());
            deviceData.put(CometChatConstants.SdkIdentificationKeys.RESOLUTION, getScreenResolution(context));
            deviceData.put(CometChatConstants.SdkIdentificationKeys.USER_AGENT, CometChatUtils.getAgent());
            deviceData.put(CometChatConstants.SdkIdentificationKeys.TIME_ZONE, getTimeZone());
            deviceData.put(CometChatConstants.SdkIdentificationKeys.CAMERA, isCameraAvailable(context));
            deviceData.put(CometChatConstants.SdkIdentificationKeys.MIC, isMicrophoneAvailable(context));
            // Add OS data
            HashMap<String, Object> osData = new HashMap<>();
            osData.put(CometChatConstants.SdkIdentificationKeys.VERSION, Build.VERSION.RELEASE);
            osData.put(CometChatConstants.SdkIdentificationKeys.NAME, CometChatConstants.AppInfoKeys.KEY_PLATFORM_ANDROID);
            // Put OS data into device object
            deviceData.put(CometChatConstants.SdkIdentificationKeys.OS, osData);
            // Put device object into data object
            data.put(CometChatConstants.SdkIdentificationKeys.DEVICE, deviceData);
            // Add the meta data
            /*HashMap<String, Object> metaData = new HashMap<>();
            data.put(CometChatConstants.SdkIdentificationKeys.META, metaData);*/
            data.put(CometChatConstants.SdkIdentificationKeys.INTEGRATION_SOURCE, PreferenceHelper.getIntegrationSource());
            rootNode.put(CometChatConstants.SdkIdentificationKeys.CC_DATA, data);
            rootNode.put(CometChatConstants.SdkIdentificationKeys.GENERATED_AT, System.currentTimeMillis() / 1000);
            JSONObject hashJson = getSessionIdHashJson();
            if (hashJson != null) {
                rootNode.put(CometChatConstants.SdkIdentificationKeys.SESSION_ID, CometChatUtils.generateHash(hashJson.toString()));
                JSONObject rootNodeAsJsonObject = new JSONObject(rootNode);
                HashMap<String, String> rootNodeAsStringHashMap = convertJsonStringToHashMapString(rootNodeAsJsonObject.toString());
                Request createGroupRequest = createPOST(getApiUrl(URL_USER_SESSIONS), getDefaultHeaders(), rootNodeAsStringHashMap);
                makeApiCall(createGroupRequest, listener, false);
            } else {
                Logger.error(TAG, "Session hash json is null");
            }
        } catch (Exception e) {
            Logger.error(TAG, "Error: SdkIdentification: " + e);
        }
    }

    void getNotificationPreferences(final APIConnectionListener listener) {
        try {
            Request request = createGET(getApiUrl(URL_NOTIFICATION_GET_PUSH_PREFERENCES_PUSH), getNotificationAPIDefaultHeaders(), null);
            makeApiCall(request, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    void getNotificationTimezone(final APIConnectionListener listener) {
        try {
            Request request = createGET(getApiUrl(URL_NOTIFICATION_UPDATE_TIMEZONE), getNotificationAPIDefaultHeaders(), null);
            makeApiCall(request, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    void updatedPreferences(final JSONObject requestObj, final APIConnectionListener listener) {
        try {
            Request request = createPATCH(getApiUrl(URL_NOTIFICATION_GET_PUSH_PREFERENCES_PUSH), getNotificationAPIDefaultHeaders(), requestObj.toString());
            makeApiCall(request, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    void resetPushPreferences(final APIConnectionListener listener) {
        try {
            Request request = createDELETE(getApiUrl(URL_NOTIFICATION_GET_PUSH_PREFERENCES_PUSH), getNotificationAPIDefaultHeaders(), null);
            makeApiCall(request, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    void getMutedConversations(final APIConnectionListener listener) {
        try {
            Request request = createGET(getApiUrl(URL_NOTIFICATION_MUTE_UNMUTE), getNotificationAPIDefaultHeaders(), null);
            makeApiCall(request, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    void muteConversations(JSONObject requestJsonObj, final APIConnectionListener listener) {
        try {
            Request request = createPUT(getApiUrl(URL_NOTIFICATION_MUTE_UNMUTE), getNotificationAPIDefaultHeaders(), requestJsonObj.toString());
            makeApiCall(request, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    void unmuteConversations(JSONObject requestJsonObj, final APIConnectionListener listener) {
        try {
            Request request = createDELETEWithJsonRequest(getApiUrl(URL_NOTIFICATION_MUTE_UNMUTE), getNotificationAPIDefaultHeaders(), requestJsonObj.toString());
            makeApiCall(request, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    void registerPushToken(@NonNull String pushToken, @NonNull PushPlatforms pushPlatforms, @Nullable String providerId, final APIConnectionListener listener) {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(CometChatNotificationsConstants.TokenRegisterKeys.KEY_TIME_ZONE, TimeZone.getDefault().getID());
            params.put(CometChatNotificationsConstants.TokenRegisterKeys.KEY_FCM_TOKEN, pushToken);
            params.put(CometChatNotificationsConstants.TokenRegisterKeys.KEY_PLATFORM, pushPlatforms.getValue());
            if (providerId != null) {
                params.put(CometChatNotificationsConstants.TokenRegisterKeys.KEY_PROVIDER_ID, providerId);
            }
            Request request = createPOST(getApiUrl(URL_NOTIFICATION_GET_TOKEN_MANAGEMENT), getNotificationAPIDefaultHeaders(), params);
            makeApiCall(request, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    void unregisterPushToken(final APIConnectionListener listener) {
        try {
            Request request = createDELETE(getApiUrl(URL_NOTIFICATION_GET_TOKEN_MANAGEMENT), getNotificationAPIDefaultHeaders(), null);
            makeApiCall(request, listener, false);
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    public void updateTimezone(JSONObject requestJsonObj, APIConnectionListener apiConnectionListener) {
        try {
            Request request = createPATCH(getApiUrl(URL_NOTIFICATION_UPDATE_TIMEZONE), getNotificationAPIDefaultHeaders(), requestJsonObj.toString());
            makeApiCall(request, apiConnectionListener, false);
        } catch (Exception e) {
            apiConnectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    private RequestBody getRequestBodyFromMap(HashMap<String, String> params) {
        try {
            JSONObject jsonObject = new JSONObject();
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (((String) pair.getKey()).equalsIgnoreCase(CometChatConstants.ResponseKeys.KEY_DATA))
                    jsonObject.put((String) pair.getKey(), new JSONObject((String) pair.getValue()));
                else if (((String) pair.getKey()).equalsIgnoreCase(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA))
                    jsonObject.put((String) pair.getKey(), new JSONObject((String) pair.getValue()));
                else if (((String) pair.getKey()).equalsIgnoreCase("appInfo"))
                    jsonObject.put((String) pair.getKey(), new JSONObject((String) pair.getValue()));
                else if (((String) pair.getKey()).equalsIgnoreCase(CometChatConstants.UserKeys.USER_KEY_TAGS))
                    jsonObject.put((String) pair.getKey(), new JSONArray((String) pair.getValue()));
                else if (((String) pair.getKey()).equalsIgnoreCase(CometChatConstants.ExtraKeys.KEY_CALLING_JWT_PASSTHROUGH))
                    jsonObject.put((String) pair.getKey(), new JSONObject((String) pair.getValue()));
                else if (((String) pair.getKey()).equalsIgnoreCase(CometChatConstants.SdkIdentificationKeys.CC_DATA)) {
                    JSONObject obj = new JSONObject(String.valueOf(pair.getValue()));
                    if (obj.getJSONObject(CometChatConstants.SdkIdentificationKeys.APP).getString(CometChatConstants.SdkIdentificationKeys.DEBUG).equalsIgnoreCase("true")) {
                        obj.getJSONObject(CometChatConstants.SdkIdentificationKeys.APP).put(CometChatConstants.SdkIdentificationKeys.DEBUG, true);
                    } else {
                        obj.getJSONObject(CometChatConstants.SdkIdentificationKeys.APP).put(CometChatConstants.SdkIdentificationKeys.DEBUG, false);
                    }
                    if (obj.getJSONObject(CometChatConstants.SdkIdentificationKeys.DEVICE).getString(CometChatConstants.SdkIdentificationKeys.MIC).equalsIgnoreCase("true")) {
                        obj.getJSONObject(CometChatConstants.SdkIdentificationKeys.DEVICE).put(CometChatConstants.SdkIdentificationKeys.MIC, true);
                    } else {
                        obj.getJSONObject(CometChatConstants.SdkIdentificationKeys.DEVICE).put(CometChatConstants.SdkIdentificationKeys.MIC, false);
                    }
                    if (obj.getJSONObject(CometChatConstants.SdkIdentificationKeys.DEVICE).getString(CometChatConstants.SdkIdentificationKeys.CAMERA).equalsIgnoreCase("true")) {
                        obj.getJSONObject(CometChatConstants.SdkIdentificationKeys.DEVICE).put(CometChatConstants.SdkIdentificationKeys.CAMERA, true);
                    } else {
                        obj.getJSONObject(CometChatConstants.SdkIdentificationKeys.DEVICE).put(CometChatConstants.SdkIdentificationKeys.CAMERA, false);
                    }
                    jsonObject.put(CometChatConstants.SdkIdentificationKeys.DATA, obj);
                } else if (((String) pair.getKey()).equalsIgnoreCase(CometChatConstants.SdkIdentificationKeys.META))
                    jsonObject.put((String) pair.getKey(), new JSONObject((String) pair.getValue()));
                else
                    jsonObject.put((String) pair.getKey(), pair.getValue());
            }
            Logger.error(jsonObject.toString());
            return RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        } catch (JSONException je) {
            Logger.error("Error creating request body : " + je.getMessage());
        }
        return null;
    }

    private RequestBody getRequestBodyFromJSONArray(HashMap<String, JSONArray> params) {
        try {
            JSONObject jsonObject = new JSONObject();
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                jsonObject.put((String) pair.getKey(), (JSONArray) pair.getValue());
            }
            Logger.error(jsonObject.toString());
            return RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        } catch (JSONException je) {
            Logger.error("Error creating request body : " + je.getMessage());
        }
        return null;
    }

    private RequestBody getRequestBodyFromMap(HashMap<String, String> params, List<File> files) {
        try {
            Logger.error(TAG, "getRequestBodyFromMap " + files.size());
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (((String) pair.getKey()).equalsIgnoreCase(CometChatConstants.ResponseKeys.KEY_DATA))
                    builder.addFormDataPart((String) pair.getKey(), (String) pair.getValue());
                else if (((String) pair.getKey()).equalsIgnoreCase(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA))
                    builder.addFormDataPart((String) pair.getKey(), (String) pair.getValue());
                else if (((String) pair.getKey()).equalsIgnoreCase("appInfo"))
                    builder.addFormDataPart((String) pair.getKey(), (String) pair.getValue());
                else if (((String) pair.getKey()).equalsIgnoreCase(CometChatConstants.UserKeys.USER_KEY_TAGS)) {
                    JSONArray jsonArray = new JSONArray((String) pair.getValue());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        builder.addFormDataPart("tags[]", jsonArray.getString(i));
                    }
                } else {
                    builder.addFormDataPart((String) pair.getKey(), (String) pair.getValue());
                }
            }
            for (File file : files) {
                if (file.exists())
                    Logger.error("File Found : " + file.getAbsolutePath() + "file size : " + file.getName());
                builder.addFormDataPart(CometChatConstants.MessageKeys.KEY_MESSAGE_FILES, file.getName(), RequestBody.create(MediaType.parse("*/*"), file));
            }
            MultipartBody multipartBody = builder.build();
            for (MultipartBody.Part part : multipartBody.parts()) {
                Logger.error(TAG, part.toString());
            }
            return multipartBody;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Headers getDefaultHeaders() {
        Headers headers = null;
        if (CurrentUserRepo.getCurrentUser() != null && CurrentUserRepo.getCurrentUser().getAuthToken() != null) {
            Headers.Builder headersBuilder = new Headers.Builder()
                    .add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID())
                    .add(CometChatConstants.Params.AUTHTOKEN, CurrentUserRepo.getCurrentUser().getAuthToken())
                    .add(CometChatConstants.Params.CONTENT_TYPE, CometChatConstants.Params.CONTENT_TYPE_JSON_VALUE)
                    .add(CometChatConstants.Params.KEY_HEADER_RESOURCE, CometChatUtils.getResource(false))
                    .add(CometChatConstants.Params.KEY_HEADER_SDK_VERSION, CometChatUtils.getPlatform().toLowerCase() + "@" + CometChatUtils.getSDKVersion());
            headers = headersBuilder.build();
        } else {
            headers = new Headers.Builder()
                    .build();
        }
        return headers;
    }

    private Headers getNotificationAPIDefaultHeaders() {
        Headers headers;
        Settings settings = SettingsRepo.getSettings();
        CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
        if (currentUser != null && currentUser.getAuthToken() != null && settings != null && settings.getChatAPIVersion() != null) {
            Headers.Builder headersBuilder = new Headers.Builder()
                    .add(CometChatNotificationsConstants.HeadersKeys.KEY_APP_ID, PreferenceHelper.getAppID())
                    .add(CometChatNotificationsConstants.HeadersKeys.KEY_AUTH_TOKEN, CurrentUserRepo.getCurrentUser().getAuthToken())
                    .add(CometChatNotificationsConstants.HeadersKeys.KEY_CHAT_API_VERSION, settings.getChatAPIVersion());
            headers = headersBuilder.build();
        } else {
            headers = new Headers.Builder()
                    .build();
        }
        return headers;
    }

    private Headers getWSDefaultHeaders() {
        Headers headers = null;
        CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
        if (currentUser != null && currentUser.getJwt() != null) {
            Headers.Builder headersBuilder = new Headers.Builder()
                    .add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID())
                    .add(CometChatConstants.Params.KEY_AUTHORIZATION, currentUser.getJwt())
                    .add(CometChatConstants.Params.CONTENT_TYPE, CometChatConstants.Params.CONTENT_TYPE_JSON_VALUE);
            headers = headersBuilder.build();
        } else {
            headers = new Headers.Builder()
                    .build();
        }
        return headers;
    }

    private Headers getAIDefaultHeaders() {
        Headers headers = null;
        CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
        if (currentUser != null && currentUser.getAuthToken() != null) {
            JSONObject onBehalfOfUser = new JSONObject();
            try {
                onBehalfOfUser.put(CometChatConstants.UserKeys.USER_KEY_UID, currentUser.getUid());
                onBehalfOfUser.put(CometChatConstants.UserKeys.USER_KEY_NAME, currentUser.getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Headers.Builder headersBuilder = new Headers.Builder()
                    .add(CometChatConstants.Params.APPID, PreferenceHelper.getAppID())
                    .add(CometChatConstants.Params.AUTHTOKEN, CurrentUserRepo.getCurrentUser().getAuthToken())
                    .add(CometChatConstants.AIKeys.KEY_ON_BEHALF_OF_USER, new String(Base64.encode(onBehalfOfUser.toString().getBytes(), Base64.NO_WRAP)))
                    .add(CometChatConstants.Params.CONTENT_TYPE, CometChatConstants.Params.CONTENT_TYPE_JSON_VALUE)
                    .add(CometChatConstants.Params.KEY_HEADER_RESOURCE, CometChatUtils.getResource(false))
                    .add(CometChatConstants.Params.KEY_HEADER_SDK_VERSION, CometChatUtils.getPlatform().toLowerCase() + "@" + CometChatUtils.getSDKVersion());
            headers = headersBuilder.build();
        } else {
            headers = new Headers.Builder()
                    .build();
        }
        return headers;
    }

    private String getApiUrl(String url) {
        Settings settings = SettingsRepo.getSettings();
        String urlPrefix;
        if (settings != null && settings.getClientApiHost() != null) {
            if (settings.getChatAPIVersion() != null) {
                urlPrefix = CometChatConstants.ExtraKeys.KEY_HTTPS + settings.getClientApiHost() + "/" + settings.getChatAPIVersion() + url;
            } else {
                urlPrefix = CometChatConstants.ExtraKeys.KEY_HTTPS + settings.getClientApiHost() + "/" + API_VERSION + url;
            }
        } else {
            if (appSettings.getClientHost() != null) {
                urlPrefix = CometChatConstants.ExtraKeys.KEY_HTTPS + appSettings.getClientHost() + url;
            } else {
                urlPrefix = CometChatConstants.ExtraKeys.KEY_HTTPS + String.format(BASE_URL, PreferenceHelper.getAppID(), appSettings.getRegion()) + "/" + API_VERSION + url;
            }
        }
        return urlPrefix;
    }

    private String getAdminUrl(String url) {
        Settings settings = SettingsRepo.getSettings();
        String urlPrefix;
        if (settings != null && settings.getAdminApiHost() != null) {
            if (settings.getChatAPIVersion() != null) {
                urlPrefix = CometChatConstants.ExtraKeys.KEY_HTTPS + settings.getAdminApiHost() + "/" + settings.getChatAPIVersion() + url;
                ;
            } else {
                urlPrefix = CometChatConstants.ExtraKeys.KEY_HTTPS + settings.getAdminApiHost() + "/" + API_VERSION + url;
            }
        } else {
            if (appSettings.getAdminHost() != null) {
                urlPrefix = CometChatConstants.ExtraKeys.KEY_HTTPS + appSettings.getAdminHost() + url;
                ;
            } else {
                urlPrefix = CometChatConstants.ExtraKeys.KEY_HTTPS + String.format(BASE_ADMIN_URL, PreferenceHelper.getAppID(), appSettings.getRegion()) + "/" + API_VERSION + url;
            }
        }
        return urlPrefix;
    }

    private String getAnalyticsUrl(final String url) {
        Settings settings = SettingsRepo.getSettings();
        String urlPrefix = null;
        if (settings != null && settings.getAnalyticsHost() != null && settings.getAnalyticsVersion() != null) {
            urlPrefix = CometChatConstants.ExtraKeys.KEY_HTTPS + settings.getAnalyticsHost() + CometChatConstants.ExtraKeys.DELIMETER_SLASH + settings.getAnalyticsVersion() + url;
        } else {
            getSettingsRetry(new CometChat.CallbackListener<Settings>() {
                @Override
                public void onSuccess(Settings settings) {
                    getSettingsRetryCounter = 0;
                    getAnalyticsUrl(url);
                }

                @Override
                public void onError(final CometChatException e) {
                    if (Objects.equals(e.getMessage(), CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE)) {
                        Logger.error(TAG, "Error: " + e.getMessage());
                    } else {
                        getSettingsRetryCounter++;
                        if (getSettingsRetryCounter < 3) {
                            getAnalyticsUrl(url);
                        } else {
                            Logger.error(TAG, "Error: " + e.getMessage());
                        }
                    }
                }
            });
        }
        return urlPrefix;
    }

    private void makeApiCall(Request okHttpRequest, final APIConnectionListener apiConnectionListener, boolean isMediaRequest) {
        try {
            Logger.error(TAG, "API_URL : " + okHttpRequest.url());
            Logger.error(TAG, "API_REQUEST_TYPE : " + okHttpRequest.method());
            Headers headers = okHttpRequest.headers();
            Logger.error(TAG, "Headers : " + headers.toString());
            if (!CometChatUtils.isConnectedToNetwork(context)) {
                apiConnectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_INTERNET_UNAVAILABLE, CometChatConstants.Errors.ERROR_INTERNET_UNAVAILABLE_MESSAGE));
            } else {
                if (headers.get(CometChatConstants.Params.APPID) != null) {
                    if (isMediaRequest) {
                        okHttpFileClient.newCall(okHttpRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                apiConnectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_IO_EXCEPTION, e.getMessage()));
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseBody = response.body().string();
                                handleApiResponse(response.code(), responseBody, apiConnectionListener);
                            }
                        });
                    } else {
                        okHttpClient.newCall(okHttpRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                apiConnectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_IO_EXCEPTION, e.getMessage()));
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseBody = response.body().string();
                                handleApiResponse(response.code(), responseBody, apiConnectionListener);
                            }
                        });
                    }
                } else {
                    apiConnectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN, CometChatConstants.Errors.ERROR_USER_NOT_LOGGED_IN_MESSAGE));
                }
            }
        } catch (Exception e) {
            Logger.error(TAG, "makeApiCall: " + e);
            apiConnectionListener.onResponse(null, new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    private void handleApiResponse(int responseCode, String responseBody, APIConnectionListener listener) throws IOException {
        try {
            Logger.error("Response Code: " + responseCode + " Response : " + responseBody);
            if (responseCode == CometChatConstants.ResponseKeys.CODE_REQUEST_OK) {
                listener.onResponse(responseBody, null);
            } else if (responseCode == CometChatConstants.ResponseKeys.CODE_BAD_REQUEST) {
                JSONObject mainObject = new JSONObject(responseBody);
                JSONObject errorObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ERROR);
                String details = null;
                if (errorObject.has(CometChatConstants.ResponseKeys.KEY_ERROR_DETAILS)) {
                    details = errorObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ERROR_DETAILS).toString();
                }
                listener.onResponse(null, new CometChatException(errorObject.getString(CometChatConstants.ResponseKeys.KEY_ERROR_CODE), errorObject.getString(CometChatConstants.ResponseKeys.KEY_ERROR_MESSAGE), details));
            } else if (responseCode == CometChatConstants.ResponseKeys.CODE_BAD_GATEWAY || responseCode == CometChatConstants.ResponseKeys.CODE_SERVICE_UNAVAILABLE || responseCode == CometChatConstants.ResponseKeys.CODE_GATEWAY_TIME_OUT) {
                listener.onResponse(null, new CometChatException(CometChatConstants.Errors.FAILED_TO_FETCH, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
            } else {
                JSONObject mainObject = new JSONObject(responseBody);
                JSONObject errorObject = mainObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ERROR);
                CometChatException cometChatException = new CometChatException(errorObject.getString(CometChatConstants.ResponseKeys.KEY_ERROR_CODE), errorObject.getString(CometChatConstants.ResponseKeys.KEY_ERROR_MESSAGE));
                if (cometChatException.getCode().equalsIgnoreCase(CometChatConstants.Errors.ERROR_API_AUTH_ERR_AUTH_TOKEN_NOT_FOUND)) {
                    CometChat.internalLogout(true);
                }
                listener.onResponse(null, cometChatException);
            }
        } catch (Exception e) {
            listener.onResponse(null, new CometChatException(CometChatConstants.Errors.FAILED_TO_FETCH, CometChatConstants.Errors.ERROR_DEFAULT_MESSAGE));
        }
    }

    interface APIConnectionListener {
        public void onResponse(String response, CometChatException ce);
    }

    private String getAppName() {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        String appName = "";
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            appName = (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return appName;
    }

    private String isAppDebuggable() {
        boolean isDebuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        return String.valueOf(isDebuggable);
    }

    private HashMap<String, Object> jsonToHashMap(JSONObject jsonObject) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        Iterator<String> keysItr = jsonObject.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONArray) {
                value = jsonArrayToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToHashMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private List<Object> jsonArrayToList(JSONArray array) throws Exception {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = jsonArrayToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToHashMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    private HashMap<String, String> convertJsonStringToHashMapString(String jsonString) {
        HashMap<String, String> stringHashMap = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                Object value = jsonObject.get(key);
                if (value instanceof String) {
                    stringHashMap.put(key, (String) value);
                } else {
                    try {
                        stringHashMap.put(key, value.toString());
                    } catch (Exception e) {
                        // Ignore the exception and skip the key-value pair
                        Logger.error(e.toString());
                    }
                }
            }
        } catch (JSONException e) {
            // Ignore the exception and return an empty HashMap
            Logger.error(e.toString());
        }
        return stringHashMap;
    }

    JSONObject getSessionIdHashJson() {
        try {
            JSONObject sessionIdHashJsonObj = new JSONObject();
            sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.APP_VERSION, getAppVersion(context));
            if (CometChatUtils.getMetaInfo() != null) {
                if (CometChatUtils.getMetaInfo().has(CometChatConstants.SdkIdentificationKeys.CHAT_SDK)) {
                    sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.CHAT_SDK_VERSION, CometChatUtils.getMetaInfo().getJSONObject(CometChatConstants.SdkIdentificationKeys.CHAT_SDK).getString(CometChatConstants.SdkIdentificationKeys.VERSION));
                }
                if (CometChatUtils.getMetaInfo().has(CometChatConstants.SdkIdentificationKeys.CALLS_SDK)) {
                    sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.CALLS_SDK_VERSION, CometChatUtils.getMetaInfo().getJSONObject(CometChatConstants.SdkIdentificationKeys.CALLS_SDK).getString(CometChatConstants.SdkIdentificationKeys.VERSION));
                }
                if (CometChatUtils.getMetaInfo().has(CometChatConstants.SdkIdentificationKeys.CALLS_UIKIT)) {
                    sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.CALLS_UIKIT_VERSION, CometChatUtils.getMetaInfo().getJSONObject(CometChatConstants.SdkIdentificationKeys.CALLS_UIKIT).getString(CometChatConstants.SdkIdentificationKeys.VERSION));
                }
                if (CometChatUtils.getMetaInfo().has(CometChatConstants.SdkIdentificationKeys.UI_KIT)) {
                    sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.UI_KIT_VERSION, CometChatUtils.getMetaInfo().getJSONObject(CometChatConstants.SdkIdentificationKeys.UI_KIT).getString(CometChatConstants.SdkIdentificationKeys.VERSION));
                }
                sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.DEBUG, isAppDebuggable());
            } else {
                sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.CHAT_SDK_VERSION, BuildConfig.VERSION_NAME);
//                if (getCallSDKVersion() != null) {
//                    sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.CALLS_SDK_VERSION, getCallSDKVersion());
//                }
//                if (getUIKitSDKVersion() != null) {
//                    sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.UI_KIT_VERSION, getUIKitSDKVersion());
//                }
                sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.DEBUG, isAppDebuggable());
            }
            if (CometChatUtils.getDemoMetaInfo() != null) {
                if (CometChatUtils.getDemoMetaInfo().has(CometChatConstants.SdkIdentificationKeys.VERSION)) {
                    sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.DEMO_VERSION, CometChatUtils.getDemoMetaInfo().getString(CometChatConstants.SdkIdentificationKeys.VERSION));
                }
            }
            // Include integrationSource in hash so switching init methods triggers new telemetry
            sessionIdHashJsonObj.put(CometChatConstants.SdkIdentificationKeys.INTEGRATION_SOURCE, PreferenceHelper.getIntegrationSource());
            return sessionIdHashJsonObj;
        } catch (Exception e) {
            Logger.error(TAG, "Error: getSessionIdHashJson >>" + e);
            return null;
        }
    }

    private String getCallSDKVersion() {
        try {
            Class.forName("com.cometchat.calls.core.CometChatCalls");
            return callStaticMethodUsingReflection("com.cometchat.calls.core.CometChatCalls", "getSDKVersion");
        } catch (ClassNotFoundException e) {
            Logger.error(TAG, "Error: " + e);
            return null;
        }
    }

    private String getUIKitSDKVersion() {
        try {
            Class.forName("com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit");
            return callStaticMethodUsingReflection("com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit", "getSDKVersion");
        } catch (ClassNotFoundException e) {
            Logger.error(TAG, "Error: " + e);
            return null;
        }
    }

    private String getTimeZone() {
        TimeZone timeZone = TimeZone.getDefault();
        int offset = timeZone.getRawOffset();
        int hours = offset / (60 * 60 * 1000);
        int minutes = (offset % (60 * 60 * 1000)) / (60 * 1000);
        String sign = hours >= 0 ? "+" : "-";
        String offsetString = String.format("%s%02d:%02d", sign, hours, minutes);
        return offsetString;
    }

    private String getScreenResolution(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return width + "x" + height;
    }

    private String getAppVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getMobileDeviceModel() {
        return Build.MODEL;
    }

    private String getLocalLanguage() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    private boolean isCameraAvailable(Context context) {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            return cameraManager.getCameraIdList().length > 0;
        } catch (Exception e) {
            return false;
        }
    }

    boolean isMicrophoneAvailable(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return !audioManager.isMicrophoneMute();
    }

    private String getNetworkProvider(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperatorName = telephonyManager.getNetworkOperatorName();
        return networkOperatorName;
    }

    private int getBatteryLevel(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return batteryLevel;
    }

    private String callStaticMethodUsingReflection(String className, String methodName) {
        String data = null;
        try {
            Class<?> myClass = Class.forName(className);
            Method myMethod = myClass.getMethod(methodName);
            Object result = myMethod.invoke(null);
            if (result != null) {
                data = (String) result;
            } else {
                data = null;
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return data;
    }

    private String callMethodUsingReflection(String className, String methodName) {
        String data = null;
        try {
            // Get the class object using the fully qualified class name
            Class<?> myClass = Class.forName(className);
            // Get the method using the method name and parameter types
            Method myMethod = myClass.getMethod(methodName);
            // Create an instance of the class (if the method is non-static)
            Object myObject = myClass.getConstructor().newInstance();
            // Invoke the method on the object (or null for static methods)
            Object result = myMethod.invoke(myObject);
            // Print the result returned by the method
            if (result != null) {
                data = (String) result;
            } else {
                data = null;
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return data;
    }

    // region Notification Feed API Methods

    void getNotificationFeed(int limit, String cursor, String nextAffix, Long nextSentAt, String nextId, com.cometchat.chat.enums.FeedReadState readState, String category, String channelId, List<String> tags, String dateFrom, String dateTo, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_LIMIT, String.valueOf(limit));
        if (cursor != null)
            queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_CURSOR, cursor);
        // Pagination via meta.next fields
        if (nextAffix != null)
            queryParams.put("affix", nextAffix);
        if (nextSentAt != null)
            queryParams.put("sentAt", String.valueOf(nextSentAt));
        if (nextId != null)
            queryParams.put("id", nextId);
        if (readState != null && readState != com.cometchat.chat.enums.FeedReadState.ALL)
            queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_READ_STATE, readState.getValue());
        if (category != null)
            queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_CATEGORY, category);
        if (channelId != null)
            queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_CHANNEL_ID, channelId);
        if (tags != null && tags.size() > 0)
            queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_TAGS, CometChatUtils.getCSStringFromList(tags));
        if (dateFrom != null)
            queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_DATE_FROM, dateFrom);
        if (dateTo != null)
            queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_DATE_TO, dateTo);
        Request request = createGET(getApiUrl(URL_NOTIFICATION_FEED), getDefaultHeaders(), queryParams);
        makeApiCall(request, listener, false);
    }

    void getNotificationCategories(int limit, String cursor, final APIConnectionListener listener) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_LIMIT, String.valueOf(limit));
        if (cursor != null)
            queryParams.put(CometChatConstants.NotificationFeedKeys.KEY_CURSOR, cursor);
        Request request = createGET(getApiUrl(URL_TEMPLATE_CATEGORIES), getDefaultHeaders(), queryParams);
        makeApiCall(request, listener, false);
    }

    void getNotificationFeedUnreadCount(final APIConnectionListener listener) {
        Request request = createGET(getApiUrl(URL_NOTIFICATION_FEED_UNREAD_COUNT), getDefaultHeaders(), null);
        makeApiCall(request, listener, false);
    }

    void getNotificationFeedItem(String id, final APIConnectionListener listener) {
        Request request = createGET(getApiUrl(String.format(URL_ANNOUNCEMENTS_SINGLE, id)), getDefaultHeaders(), null);
        makeApiCall(request, listener, false);
    }

    void markFeedItemAsDelivered(String feedItemId, final APIConnectionListener listener) {
        Request request = createPOST(getApiUrl(String.format(URL_NOTIFICATION_FEED_DELIVERED, feedItemId)), getDefaultHeaders(), null);
        makeApiCall(request, listener, false);
    }

    void markFeedItemAsRead(String feedItemId, final APIConnectionListener listener) {
        Request request = createPOST(getApiUrl(String.format(URL_NOTIFICATION_FEED_READ, feedItemId)), getDefaultHeaders(), null);
        makeApiCall(request, listener, false);
    }

    void reportFeedEngagement(String feedItemId, String interactionString, final APIConnectionListener listener) {
        HashMap<String, String> body = new HashMap<>();
        body.put("topic", interactionString);
        Request request = createPOST(getApiUrl(String.format(URL_NOTIFICATION_FEED_ENGAGEMENT, feedItemId)), getDefaultHeaders(), body);
        makeApiCall(request, listener, false);
    }

    void markPushNotificationDelivered(String pushNotificationId, final APIConnectionListener listener) {
        Request request = createPUT(getApiUrl(String.format(URL_PUSH_NOTIFICATION_DELIVERED, pushNotificationId)), getDefaultHeaders(), (HashMap<String, String>) null);
        makeApiCall(request, listener, false);
    }

    void markPushNotificationClicked(String pushNotificationId, final APIConnectionListener listener) {
        Request request = createPUT(getApiUrl(String.format(URL_PUSH_NOTIFICATION_CLICKED, pushNotificationId)), getDefaultHeaders(), (HashMap<String, String>) null);
        makeApiCall(request, listener, false);
    }

    // endregion
}