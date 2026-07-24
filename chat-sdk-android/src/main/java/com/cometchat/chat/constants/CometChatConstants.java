package com.cometchat.chat.constants;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by adityagokula on 06/09/18.
 */

public class CometChatConstants {

    public static final String DATABASE_NAME = "CometChatSDK.db";
    public static final int DATABASE_VERSION = 1;

    public static final class Params {

        public static final String UID = "uid";
        public static final String APPID = "appid";
        public static final String APIKEY = "apikey";
        public static final String AUTHTOKEN = "authToken";
        public static final String PASSWORD = "password";
        public static final String LIMIT = "per_page";
        public static final String TOKEN = "cursor";
        public static final String PAGE = "page";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String CONTENT_TYPE_JSON_VALUE = "application/json";
        public static final String CONTENT_TYPE_JSON_MULTIPART = "multipart/form-data";
        public static final String KEY_SEARCH_KEYWORD = "searchKey";
        public static final String KEY_STATUS = "status";
        public static final String KEY_HIDE_BLOCKED_USERS = "hideBlockedUsers";
        public static final String KEY_DIRECTION = "direction";
        public static final String KEY_UNDELIVERED = "undelivered";
        public static final String KEY_UNREAD = "unread";
        public static final String KEY_COUNT = "count";
        public static final String KEY_HIDE_MESSAGES_FROM_BLOCKED_USERS = "hideMessagesFromBlockedUsers";
        public static final String KEY_UPDATED_AT = "updatedAt";
        public static final String KEY_ONLY_UPDATES = "onlyUpdates";
        public static final String KEY_ROLES = "roles";
        public static final String KEY_HAS_JOINED = "hasJoined";
        public static final String KEY_FRIENDS_ONLY = "friendsOnly";
        public static final String KEY_CONVERSATION_WITH = "conversationWith";
        public static final String KEY_CONVERSATION_TYPE = "conversationType";
        public static final String KEY_HEADER_RESOURCE = "resource";
        public static final String KEY_HEADER_SDK_VERSION = "sdk";
        public static final String KEY_HEADER_AUTHORIZATION = "Authorization";
        public static final String KEY_HIDE_REPLIES = "hideReplies";
        public static final String KEY_MESSAGE_CATEGORIES = "categories";
        public static final String KEY_MESSAGE_TYPES = "types";
        public static final String KEY_MESSAGE_SCOPES = "scopes";
        public static final String KEY_SETTINGS_HASH = "settingsHash";
        public static final String KEY_SETTINGS_HASH_RECEIVED_AT = "settingsHashReceivedAt";
        public static final String KEY_AUTHORIZATION = "Authorization";
        public static final String KEY_POLLING = "polling";
        public static final String OWNER = "owner";
        public static final String KEY_DOMAIN = "domain";
        public static final String KEY_ROOM = "room";
        public static final String KEY_HIDE_DELETED = "hideDeleted";
        public static final String KEY_GROUPS = "groups";
        public static final String KEY_CHAT_API_VERSION = "chatApiVersion";
        public static final String HIDE_AGENTIC = "hideAgentic";
        public static final String ONLY_AGENTIC = "onlyAgentic";
    }

    public static final class UserKeys {
        public static final String USER_KEY_UID = "uid";
        public static final String USER_KEY_NAME = "name";
        public static final String USER_KEY_AVATAR = "avatar";
        public static final String USER_KEY_LINK = "link";
        public static final String USER_KEY_ROLE = "role";
        public static final String USER_KEY_METADATA = "metadata";
        public static final String USER_KEY_CREDITS = "credits";
        public static final String USER_KEY_STATUS = "status";
        public static final String USER_KEY_STATUS_MESSAGE = "statusMessage";
        public static final String USER_KEY_LAST_ACTIVE_AT = "lastActiveAt";
        public static final String USER_KEY_HAS_BLOCKED_ME = "hasBlockedMe";
        public static final String USER_KEY_BLOCKED_BY_ME = "blockedByMe";
        public static final String USER_KEY_TAGS = "tags";
        public static final String USER_KEY_WITH_TAGS = "withTags";
        public static final String USER_KEY_UIDS = "uids";
        public static final String USER_KEY_DEACTIVATED_AT = "deactivatedAt";
        public static final String USER_KEY_SEARCH_IN = "searchIn";
        public static final String USER_KEY_SORT_BY = "sortBy";
        public static final String USER_KEY_SORT_ORDER = "sortOrder";
    }

    public static final class ResponseKeys {
        public static final String KEY_DATA = "data";
        public static final String KEY_ACTION = "action";
        public static final String KEY_MESSAGE = "message";
        public static final String KEY_ERROR = "error";
        public static final String KEY_ERROR_DETAILS = "details";
        public static final String KEY_ERROR_CODE = "code";
        public static final String KEY_ERROR_MESSAGE = "message";
        public static final String KEY_AUTH_TOKEN = "authToken";
        public static final String KEY_WS_CHANNEL = "wsChannel";
        public static final String KEY_IDENTITY = "identity";
        public static final String KEY_SERVICE = "service";
        public static final String KEY_ENTITIES = "entities";
        public static final String KEY_ENTITITY = "entity";
        public static final int CODE_REQUEST_OK = 200;
        public static final int CODE_BAD_REQUEST = 400;
        public static final int CODE_BAD_GATEWAY = 502;
        public static final int CODE_SERVICE_UNAVAILABLE = 503;
        public static final int CODE_GATEWAY_TIME_OUT = 504;
        public static final String KEY_BLOCKED_UIDS = "blockedUids";
        public static final String SUCCESS = "success";
        public static final String KEY_RECEIPTS = "receipts";
        public static final String KEY_MESSAGE_ID = "messageId";
        public static final String KEY_RECIPIENT = "recipient";
        public static final String KEY_ENTITY_ID = "entityId";
        public static final String KEY_ENTITY_TYPE = "entityType";
        public static final String KEY_COUNT = "count";
        public static final String KEY_MY_RECEIPT = "myReceipt";
        public static final String KEY_STATUS_AVAILABLE = "available";
        public static final String KEY_SETTINGS = "settings";
        public static final String KEY_JWT_TOKEN = "token";
        public static final String KEY_ONLINE_USER_COUNT = "onlineUsersCount";
        public static final String KEY_CONVERSATION = "conversation";
        public static final String URL_SMALL = "url_small";
        public static final String URL_MEDIUM = "url_medium";
        public static final String URL_LARGE = "url_large";
    }

    public static final class Errors {
        public static final String ERROR_IO_EXCEPTION = "ERROR_IO_EXCEPTION";
        public static final String ERROR_JSON_EXCEPTION = "ERROR_JSON_EXCEPTION";
        public static final String ERROR_PASSWORD_MISSING = "ERROR_PASSWORD_MISSING";
        public static final String ERROR_LIMIT_EXCEEDED = "ERROR_LIMIT_EXCEEDED";
        public static final String ERROR_NON_POSITIVE_LIMIT = "ERROR_NON_POSITIVE_LIMIT";
        public static final String ERROR_USER_NOT_LOGGED_IN = "ERROR_USER_NOT_LOGGED_IN";
        public static final String ERROR_INVALID_GUID = "ERROR_INVALID_GUID";
        public static final String ERROR_INVALID_UID = "ERROR_INVALID_UID";
        public static final String ERROR_BLANK_UID = "ERROR_BLANK_UID";
        public static final String ERROR_UID_WITH_SPACE = "ERROR_UID_WITH_SPACE";
        public static final String ERROR_XMPP = "ERROR_XMPP";
        public static final String ERROR_CALL_NOT_INITIATED = "ERROR_CALL_NOT_INITIATED";
        public static final String ERROR_CALL = "ERROR_CALL";
        public static final String ERROR_CALL_SESSION_MISMATCH = "ERROR_CALL_SESSION_MISMATCH";
        public static final String ERROR_UID_GUID_NOT_SPECIFIED = "ERROR_UID_GUID_NOT_SPECIFIED";
        public static final String ERROR_INTERNET_UNAVAILABLE = "ERROR_INTERNET_UNAVAILABLE";
        public static final String ERROR_REQUEST_IN_PROGRESS = "ERROR_REQUEST_IN_PROGRESS";
        public static final String ERROR_FILTERS_MISSING = "ERROR_FILTERS_MISSING";
        public static final String ERROR_BLANK_AUTHTOKEN = "ERROR_BLANK_AUTHTOKEN";
        public static final String ERROR_EXTENSION_DISABLED = "ERROR_EXTENSION_DISABLED";
        public static final String ERROR_INVALID_MESSAGEID = "ERROR_INVALID_MESSAGEID";
        public static final String ERROR_INVALID_MESSAGE_TYPE = "ERROR_INVALID_MESSAGE_TYPE";
        public static final String ERROR_LIST_EMPTY = "ERROR_LIST_EMPTY";
        public static final String ERROR_UPDATESONLY_WITHOUT_UPDATEDAFTER = "ERROR_UPDATESONLY_WITHOUT_UPDATEDAFTER";
        public static final String ERROR_LOGOUT_FAIL = "ERROR_LOGOUT_FAIL";
        public static final String ERROR_EMPTY_APPID = "ERROR_EMPTY_APPID";
        public static final String ERROR_REGION_MISSING = "ERROR_REGION_MISSING";
        public static final String ERROR_APP_SETTINGS_NULL = "ERROR_APP_SETTINGS_NULL";
        public static final String ERROR_API_KEY_NOT_FOUND = "ERROR_API_KEY_NOT_FOUND";
        public static final String ERROR_MESSAGE_TEXT_EMPTY = "ERROR_MESSAGE_TEXT_EMPTY";
        public static final String ERROR_FILE_OBJECT_INVALID = "ERROR_FILE_OBJECT_INVALID";
        public static final String ERROR_FILE_URL_EMPTY = "ERROR_FILE_URL_EMPTY";
        public static final String ERROR_EMPTY_CUSTOM_DATA = "ERROR_EMPTY_CUSTOM_DATA";
        public static final String ERROR_EMPTY_INTERACTIVE_DATA = "ERROR_EMPTY_INTERACTIVE_DATA";
        public static final String ERROR_EMPTY_GROUP_NAME = "ERROR_EMPTY_GROUP_NAME";
        public static final String ERROR_EMPTY_GROUP_TYPE = "ERROR_EMPTY_GROUP_TYPE";
        public static final String ERROR_LOGIN_IN_PROGRESS = "ERROR_LOGIN_IN_PROGRESS";
        public static final String ERROR_INVALID_MESSAGE = "ERROR_INVALID_MESSAGE";
        public static final String ERROR_INVALID_MESSAGE_ID = "ERROR_INVALID_MESSAGE_ID";
        public static final String ERROR_INVALID_GROUP = "ERROR_INVALID_GROUP";
        public static final String ERROR_INVALID_CALL = "ERROR_INVALID_CALL";
        public static final String ERROR_INVALID_CALL_TYPE = "ERROR_INVALID_CALL_TYPE";
        public static final String ERROR_INVALID_RECEIVER_TYPE = "ERROR_INVALID_RECEIVER_TYPE";
        public static final String ERROR_INVALID_SESSION_ID = "ERROR_INVALID_SESSION_ID";
        public static final String ERROR_ACTIVITY_NULL = "ERROR_ACTIVITY_NULL";
        public static final String ERROR_VIEW_NULL = "ERROR_VIEW_NULL";
        public static final String ERROR_UNHANDLED_EXCEPTION = "ERROR_UNHANDLED_EXCEPTION";
        public static final String ERROR_INVALID_FCM_TOKEN = "ERROR_INVALID_FCM_TOKEN";
        public static final String ERROR_FETECH_JOINED_GROUPS = "ERROR_FETECH_JOINED_GROUPS";
        public static final String ERROR_INVALID_GROUP_TYPE = "ERROR_INVALID_GROUP_TYPE";
        public static final String ERROR_CALL_IN_PROGRESS = "ERROR_CALL_IN_PROGRESS";
        public static final String ERROR_INCORRECT_INITIATOR = "ERROR_INCORRECT_INITIATOR";
        public static final String ERROR_GROUP_JOIN = "ERROR_GROUP_JOIN";
        public static final String ERROR_INVALID_USER_NAME = "ERROR_INVALID_USER_NAME";
        public static final String ERROR_INVALID_USER = "ERROR_INVALID_USER";
        public static final String ERROR_INVALID_GROUP_NAME = "ERROR_INVALID_GROUP_NAME";
        public static final String ERROR_INVALID_TIMESTAMP = "ERROR_INVALID_TIMESTAMP";
        public static final String ERROR_INVALID_CATEGORY = "ERROR_INVALID_CATEGORY";
        public static final String ERROR_EMPTY_ICON = "ERROR_EMPTY_ICON";
        public static final String ERROR_EMPTY_DESCRIPTION = "ERROR_EMPTY_DESCRIPTION";
        public static final String ERROR_EMPTY_METADATA = "ERROR_EMPTY_METADATA";
        public static final String ERROR_EMPTY_SCOPE = "ERROR_EMPTY_SCOPE";
        public static final String ERROR_INVALID_SCOPE = "ERROR_INVALID_SCOPE";
        public static final String ERROR_INVALID_CONVERSATION_WITH = "ERROR_INVALID_CONVERSATION_WITH";
        public static final String ERROR_INVALID_CONVERSATION_TYPE = "ERROR_INVALID_CONVERSATION_TYPE";
        public static final String ERROR_INVALID_MEDIA_MESSAGE = "ERROR_INVALID_MEDIA_MESSAGE";
        public static final String ERROR_INVALID_ATTACHMENT = "ERROR_INVALID_ATTACHMENT";
        public static final String ERROR_INVALID_FILE_NAME = "ERROR_INVALID_FILE_NAME";
        public static final String ERROR_INVALID_FILE_EXTENSION = "ERROR_INVALID_FILE_EXTENSION";
        public static final String ERROR_INVALID_FILE_MIME_TYPE = "ERROR_INVALID_FILE_MIME_TYPE";
        public static final String ERROR_INVALID_FILE_URL = "ERROR_INVALID_FILE_URL";
        public static final String ERROR_CONVERSATION_NOT_FOUND = "ERROR_CONVERSATION_NOT_FOUND";
        public static final String ERROR_GROUP_ALREADY_JOINED = "ERR_ALREADY_JOINED";
        public static final String ERR_SETTINGS_HASH_OUTDATED = "ERR_SETTINGS_HASH_OUTDATED";
        public static final String ERR_NO_AUTH = "ERR_NO_AUTH";
        public static final String ERROR_INVALID_FEATURE = "ERROR_INVALID_FEATURE";
        public static final String ERROR_INVALID_EXTENSION = "ERROR_INVALID_EXTENSION";
        public static final String ERROR_FEATURE_NOT_FOUND = "ERROR_FEATURE_NOT_FOUND";
        public static final String ERROR_EXTENSION_NOT_FOUND = "ERROR_EXTENSION_NOT_FOUND";
        public static final String ERROR_SETTINGS_NOT_FOUND = "ERROR_FEATURE_NOT_FOUND";
        public static final String ERROR_INVALID_SESSIONID = "ERROR_INVALID_SESSIONID";
        public static final String ERROR_INVALID_TYPE = "ERROR_INVALID_TYPE";
        public static final String ERROR_NO_PARTICIPANTS = "ERROR_NO_PARTICIPANTS";
        public static final String ERROR_API_AUTH_ERR_AUTH_TOKEN_NOT_FOUND = "AUTH_ERR_AUTH_TOKEN_NOT_FOUND";
        public static final String ERROR_CALL_MODULE_NOT_FOUND = "ERROR_CALL_MODULE_NOT_FOUND";
        public static final String ERROR_INVALID_GROUPLIST = "ERROR_INVALID_GROUPLIST";
        public static final String ERROR_INVALID_RECEIVER_ID = "ERROR_INVALID_RECEIVER_ID";
        public static final String ERROR_INVALID_MESSAGE_SENDER = "ERROR_INVALID_MESSAGE_SENDER";
        public static final String ERROR_RECEIPTS_TEMPORARILY_BLOCKED = "ERROR_RECEIPTS_TEMPORARILY_BLOCKED";
        public static final String ERROR_NO_WEBSOCKET_CONNECTION = "ERROR_NO_WEBSOCKET_CONNECTION";
        public static final String ERROR_INVALID_TAG_LIST = "ERROR_INVALID_TAG_LIST";
        public static final String ERROR_NULLPOINTER_EXCEPTION = "ERROR_NULLPOINTER_EXCEPTION";
        public static final String ERROR_CALL_MODE_NOT_FOUND = "ERROR_CALL_MODE_NOT_FOUND";
        public static final String ERROR_RTT_CONNECTION = "ERROR_RTT_CONNECTION";
        public static final String ERROR_INVALID_BOT_ID = "ERROR_INVALID_BOT_ID";
        public static final String ERROR_INVALID_QUESTION = "ERROR_INVALID_QUESTION" ;
        public static final String FAILED_TO_FETCH = "FAILED_TO_FETCH" ;
        public static final String ERROR_INVALID_REACTION = "ERROR_INVALID_REACTION" ;
        public static final String ERROR_INVALID_CONVERSATION_ID = "ERROR_INVALID_CONVERSATION_ID" ;
        public static final String ERROR_MESSAGE_NOT_A_RECEIVER = "ERROR_MESSAGE_NOT_A_RECEIVER" ;

        public static final String ERROR_INIT_NOT_CALLED = "INIT_NOT_CALLED";
        public static final String ERROR_PASSWORD_MISSING_MESSAGE = "Password is mandatory for a protected group";
        public static final String ERROR_LIMIT_EXCEEDED_MESSAGE = "Limit exceeded max limit of %s";
        public static final String ERROR_USER_NOT_LOGGED_IN_MESSAGE = "Please log in to CometChat before calling this method";
        public static final String ERROR_INVALID_GUID_MESSAGE = "Please provide a valid GUID";
        public static final String ERROR_INVALID_UID_MESSAGE = "Please provide a valid UID";
        public static final String ERROR_BLANK_UID_MESSAGE = "UID cannot be blank. Please provide a valid UID";
        public static final String ERROR_UID_WITH_SPACE_MESSAGE = "UID cannot contain spaces. Please provide a valid UID";
        public static final String ERROR_DEFAULT_MESSAGE = "Something went wrong";
        public static final String ERROR_CALL_NOT_INITIATED_MESSAGE = "Call cannot be cancelled without initiating a call";
        public static final String ERROR_CALL_MESSAGE = "Something went wrong while connecting";
        public static final String ERROR_CALL_SESSION_MISMATCH_MESSAGE = "Call session mismatch. Please check the session ID";
        public static final String ERROR_INIT_NOT_CALLED_MESSAGE = " Please call the CometChat.init() method preferably in the onCreate() method of the application class before calling any other methods related to CometChat";
        public static final String ERROR_UID_GUID_NOT_SPECIFIED_MESSAGE = "Both UID and GUID not specified. Please specify the UID or the GUID for which the messages need to be fetched";
        public static final String ERROR_INTERNET_UNAVAILABLE_MESSAGE = "No internet connection. Please try again later";
        public static final String ERROR_REQUEST_IN_PROGRESS_MESSAGE = "Request already in progress";
        public static final String ERROR_FILTERS_MISSING_MESSAGE = "'Timestamp' or 'MessageId' or `updatedAfter` is required to use the 'fetchNext()' method";
        public static final String ERROR_BLANK_AUTHTOKEN_MESSAGE = "Auth token cannot be empty. Please provide a valid auth token";
        public static final String ERROR_EXTENSION_DISABLED_MESSAGE = "The extension is disabled. Please enable the extension from CometChat Dashboard";
        public static final String ERROR_INVALID_MESSAGEID_MESSAGE = "The message ID provided is invalid. Please provide a valid Message ID";
        public static final String ERROR_INVALID_MESSAGE_TYPE_MESSAGE = "Only TextMessage or CustomMessage can be edited. Please provide a valid message";
        public static final String ERROR_LIST_EMPTY_MESSAGE = "The list provided is empty. Please provide a valid list";
        public static final String ERROR_UPDATESONLY_WITHOUT_UPDATEDAFTER_MESSAGE = "The `updatesOnly()` method cannot be used without the `setUpdatedAfter()` method";
        public static final String ERROR_JSON_MESSAGE = "Error while parsing JSON";
        public static final String ERROR_LOGOUT_FAIL_MESSAGE = "Error while logging out";
        public static final String ERROR_EMPTY_APPID_MESSAGE = "AppID cannot be empty. Please specify a valid appID";
        public static final String ERROR_NON_POSITIVE_LIMIT_MESSSAGE = "The limit specified must be a positive number";
        public static final String ERROR_REGION_MISSING_MESSAGE = "Region not specified. Please specify the region in the AppSettingsBuilder class using the `setRegion()` method";
        public static final String ERROR_APP_SETTING_NULL_MESSAGE = "The AppSettings cannot be null";
        public static final String ERROR_API_KEY_NOT_FOUND_MESSAGE = "ApiKey cannot be null or empty. Please provide a valid Api Key";
        public static final String ERROR_INVALID_SENDING_MESSAGE_TYPE_MESSAGE = "The Message type you have entered is invalid for following action";
        public static final String ERROR_MESSAGE_TEXT_EMPTY_MESSAGE = "Message text cannot be empty";
        public static final String ERROR_FILE_OBJECT_INVALID_MESSAGE = "File object cannot be null or File path doesn't exist";
        public static final String ERROR_FILE_URL_EMPTY_MESSAGE = "Media Message URL is set as null or blank";
        public static final String ERROR_EMPTY_CUSTOM_DATA_MESSAGE = "Custom data field cannot be null or empty";
        public static final String ERROR_EMPTY_GROUP_NAME_MESSAGE = "Group Name cannot be null or empty";
        public static final String ERROR_EMPTY_GROUP_TYPE_MESSAGE = "Group Type cannot be null or empty";
        public static final String ERROR_LOGIN_IN_PROGRESS_MESSAGE = "Login in progress. Please wait for the login request to finish";
        public static final String ERROR_INVALID_MESSAGE_MESSAGE = "Message cannot be null. Please pass a valid `BaseMessage` object";
        public static final String ERROR_INVALID_GROUP_MESSAGE = "Group cannot be null. Please pass a valid `Group` object";
        public static final String ERROR_INVALID_CALL_MESSAGE = "Call cannot be null. Please pass a valid `Call` object";
        public static final String ERROR_INVALID_CALL_TYPE_MESSAGE = "Invalid Call Type. Please provide a valid Call type";
        public static final String ERROR_INVALID_RECEIVER_TYPE_MESSAGE = "Invalid Receiver Type. Please provide a valid Receiver type";
        public static final String ERROR_INVALID_SESSION_ID_MESSAGE = "Invalid sessionId Type. Please provide a valid session id";
        public static final String ERROR_ACTIVITY_NULL_MESSAGE = "Provided Activity cannot be null.";
        public static final String ERROR_VIEW_NULL_MESSAGE = "Provided RelativeLayout cannot be null";
        public static final String ERROR_INVALID_FCM_TOKEN_MESSAGE = "The FCM token provided cannot be null or empty. Please provide a valid FCM token";
        public static final String ERROR_INVALID_GROUP_TYPE_MESSAGE = "The group type provided cannot be null or empty";
        public static final String ERROR_CALL_IN_PROGRESS_MESSAGE = "Call is in progress. Please end the previous call to perform this operation";
        public static final String ERROR_INCORRECT_INITIATOR_MESSAGE = "Cannot cancel call initiated by someone else. Please use status `rejected` instead";
        public static final String ERROR_INVALID_USER_NAME_MESSAGE = "Invalid name provided for the user. Please provide a valid name";
        public static final String ERROR_INVALID_USER_MESSAGE = "User object cannot be null. Please provide a valid user object";
        public static final String ERROR_INVALID_GROUP_NAME_MESSAGE = "Group name cannot be empty. Please provide a valid group name";
        public static final String ERROR_INVALID_TIMESTAMP_MESSAGE = "Timestamp has to be positive. Please provide a valid timestamp";
        public static final String ERROR_INVALID_CATEGORY_MESSAGE = "Invalid Category. Please provide a valid category";
        public static final String ERROR_EMPTY_ICON_MESSAGE = "Empty Icon. Please provide a valid icon";
        public static final String ERROR_EMPTY_DESCRIPTION_MESSAGE = "Empty Description. Please provide a valid description";
        public static final String ERROR_EMPTY_METADATA_MESSAGE = "Empty Metatdata. Please provide a valid metadata";
        public static final String ERROR_EMPTY_SCOPE_MESSAGE = "Invalid Scope. Please provide a valid scope";
        public static final String ERROR_INVALID_SCOPE_MESSAGE = "Invalid Scope. Please provide a valid scope";
        public static final String ERROR_INVALID_CONVERSATION_WITH_MESSAGE = "Invalid conversationWith. Please provide a valid value for conversationWith";
        public static final String ERROR_INVALID_CONVERSATION_TYPE_MESSAGE = "Invalid conversationType. Please provide a valid value for conversationWith";
        public static final String ERROR_INVALID_MEDIA_MESSAGE_MESSAGE = "Invalid Media Message.Please provide a valid File object or Attachment details.";
        public static final String ERROR_INVALID_ATTACHMENT_MESSAGE = "Attachment cannot be null. Please provide valid attachment details";
        public static final String ERROR_INVALID_FILE_NAME_MESSAGE = "Invalid File Name. Please provide a valid file name";
        public static final String ERROR_INVALID_FILE_EXTENSION_MESSAGE = "Invalid File Extension. Please provide a valid file extension";
        public static final String ERROR_INVALID_FILE_MIME_TYPE_MESSAGE = "Invalid File Mime Type. Please provide a valid file mime type";
        public static final String ERROR_INVALID_FILE_URL_MESSAGE = "Invalid File URL. Please provide a valid file URL";
        public static final String ERROR_CONVERSATION_NOT_FOUND_MESSAGE = "Conversation not found for conversationWith %s and conversationType %s";
        public static final String ERROR_SETTINGS_NOT_FOUND_MESSAGE = "Settings not found.";
        public static final String ERROR_INVALID_FEATURE_MESSAGE = "Provided feature cannot be null or empty. Please provide a valid feature";
        public static final String ERROR_INVALID_EXTENSION_MESSAGE = "Provided extension cannot be null or empty. Please provide a valid extension";
        public static final String ERROR_FEATURE_NOT_FOUND_MESSAGE = "Provided feature not found.";
        public static final String ERROR_EXTENSION_NOT_FOUND_MESSAGE = "The provided extension could not be found.";
        public static final String ERROR_INVALID_SESSIONID_MESSAGE = "The provided sessionId cannot be null or empty. Please provide a valid sessionId.";
        public static final String ERROR_INVALID_TYPE_MESSAGE = "The provided type cannot be null or empty. Please provide a valid type.";
        public static final String ERROR_NO_PARTICIPANTS_MESSAGE = "Unable to get the call participant count";
        public static final String ERROR_CALL_MODULE_NOT_FOUND_MESSAGE = "CometChat Calling module not found. Please add the CometChat Calling dependency and try again.";
        public static final String ERROR_CALL_DISPOSE_VIEW_MESSAGE = "Unable to dispose the calling view due to ";
        public static final String ERROR_CALL_MODE_NOT_FOUND_MESSAGE = "CometChat Calling mode not found.";
        public static final String ERROR_INVALID_GROUPLIST_MESSAGE = "Grouplist cannot be null or empty.";
        public static final String ERROR_INVALID_RECEIVER_ID_MESSAGE = "Invalid Receiver ID. The receiver ID cannot be null or empty";
        public static final String ERROR_INVALID_MESSAGE_SENDER_MESSAGE = "Invalid Message Sender. The Message Sender cannot be null or empty";
        public static final String ERROR_RECEIPTS_TEMPORARILY_BLOCKED_MESSAGE = "Due to high load. Receipts have been blocked for your app.";
        public static final String ERROR_NO_WEBSOCKET_CONNECTION_MESSAGE = "Connection to our Websockets server is broken. Please retry after some time.";
        public static final String ERROR_INVALID_TAG_LIST_MESSAGE = "Tags list cannot be null. Please provide a valid tags list";
        public static final String ERROR_RTT_CONNECTION_MESSAGE = "RTT Connection has not been initialized";
        public static final String ERROR_INVALID_BOT_ID_MESSAGE = "Bot Id cannot be null or empty. Please provide a valid Bot Id";
        public static final String ERROR_INVALID_QUESTION_MESSAGE = "Question to be asked cannot be null or empty. Please provide a valid questions";
        public static final String ERROR_INVALID_REACTION_MESSAGE = "Invalid reaction" ;
        public static final String ERR_METHOD_TIMEOUT = "Method execution in Queue was forcibly completed due to timeout.";
        public static final String ERR_BAD_REQUEST = "ERR_BAD_REQUEST";
        public static final String ERROR_INVALID_FILE_SIZE = "The file must not be greater than %s";
        public static final String ERROR_INVALID_FILE_SIZE_MULTIPLE = "The total size of all uploaded files is %s, which exceeds the allowed limit of %s.";
        public static final String ERROR_INVALID_FILE_COUNT = "The files must not have more than %s items.";
        public static final String ERROR_INVALID_CONVERSATION_ID_MESSAGE = "The conversation ID provided is invalid. Please provide a valid conversation ID";
        public static final String ERROR_MESSAGE_NOT_A_RECEIVER_MESSAGE = "The user with uid %s is not a receiver of the message with id %s";

        // Multiple-attachment upload errors
        public static final String ERR_FILE_COUNT_EXCEEDED = "ERR_FILE_COUNT_EXCEEDED";
        public static final String ERR_FILE_COUNT_EXCEEDED_MESSAGE = "The files must not have more than %s items.";
        public static final String ERR_FILE_SIZE_EXCEEDED = "ERR_FILE_SIZE_EXCEEDED";
        public static final String ERR_FILE_SIZE_EXCEEDED_MESSAGE = "The file size is %s, which exceeds the allowed limit of %s.";
        public static final String ERR_INVALID_FILE_OBJECT = "ERR_INVALID_FILE_OBJECT";
        public static final String ERR_INVALID_FILE_OBJECT_MESSAGE = "The file is invalid.";
        public static final String ERR_PRESIGN_FAILED = "ERR_PRESIGN_FAILED";
        public static final String ERR_PRESIGN_FAILED_MESSAGE = "Failed to obtain an upload URL for the file.";
        public static final String ERR_PRESIGN_REJECTED = "ERR_PRESIGN_REJECTED";
        public static final String ERR_PRESIGN_REJECTED_MESSAGE = "The file was rejected by the server (billing, plan, or content-type policy).";
        public static final String ERR_PRESIGNED_URL_EXPIRED = "ERR_PRESIGNED_URL_EXPIRED";
        public static final String ERR_PRESIGNED_URL_EXPIRED_MESSAGE = "The upload URL expired before the file finished uploading.";
        public static final String ERR_PRESIGNED_URL_MODE_NOT_ENABLED = "Presigned URL mode is not enabled for this app";
        public static final String ERR_S3_UPLOAD_FAILED = "ERR_S3_UPLOAD_FAILED";
        public static final String ERR_S3_UPLOAD_FAILED_MESSAGE = "The file failed to upload to storage.";
        public static final String ERR_UPLOAD_STALLED = "ERR_UPLOAD_STALLED";
        public static final String ERR_UPLOAD_STALLED_MESSAGE = "The upload stalled with no progress.";
        public static final String ERR_UPLOAD_CANCELLED = "ERR_UPLOAD_CANCELLED";
        public static final String ERR_UPLOAD_CANCELLED_MESSAGE = "The upload was cancelled.";

        // Settings file errors
        public static final String ERROR_SETTINGS_FILE_NOT_FOUND = "ERROR_SETTINGS_FILE_NOT_FOUND";
        public static final String ERROR_SETTINGS_FILE_NOT_FOUND_MESSAGE = "cometchat-settings.json not found. Ensure the file exists at app/src/main/assets/.";
        public static final String ERROR_SETTINGS_FILE_INVALID_JSON = "ERROR_SETTINGS_FILE_INVALID_JSON";
        public static final String ERROR_SETTINGS_FILE_INVALID_JSON_MESSAGE = "cometchat-settings.json is not valid JSON.";
        public static final String ERROR_SETTINGS_FILE_MISSING_APPID = "ERROR_SETTINGS_FILE_MISSING_APPID";
        public static final String ERROR_SETTINGS_FILE_MISSING_APPID_MESSAGE = "appId is required in cometchat-settings.json.";
        public static final String ERROR_SETTINGS_FILE_MISSING_REGION = "ERROR_SETTINGS_FILE_MISSING_REGION";
        public static final String ERROR_SETTINGS_FILE_MISSING_REGION_MESSAGE = "region is required in cometchat-settings.json.";
        public static final String ERROR_SETTINGS_FILE_INVALID_PRESENCE_TYPE = "ERROR_SETTINGS_FILE_INVALID_PRESENCE_TYPE";
        public static final String ERROR_SETTINGS_FILE_INVALID_PRESENCE_TYPE_MESSAGE = "Invalid presenceSubscription.type. Must be one of: ALL_USERS, ROLES, FRIENDS, NONE.";
    }


    public static final class MessageKeys {
        public static final String KEY_SEND_MESSAGE_ID = "id";
        public static final String KEY_SEND_MESSAGE_MUID = "muid";
        public static final String KEY_SEND_TEXT_MESSAGE_TYPE = "type";
        public static final String KEY_SEND_TEXT_MESSAGE_TEXT = "text";
        public static final String KEY_SEND_TEXT_METADATA = "metadata";
        public static final String KEY_INJECTED_METADATA = "@injected";
        public static final String KEY_SEND_TEXT_RECEIVER_TYPE = "receiverType";
        public static final String KEY_SEND_MESSAGE_TYPE = "type";
        public static final String KEY_MESSAGE = "message";
        public static final String KEY_RECEIVER_UID = "receiver";
        public static final String KEY_RECEIVER_ID = "receiverId";
        public static final String KEY_SENDER = "sender";
        public static final String KEY_SENDER_UID = "senderUid";
        public static final String KEY_SENT_AT = "sentAt";
        public static final String KEY_MESSAGE_URL = "url";
        public static final String KEY_MESSAGE_FILE = "file";
        public static final String KEY_MESSAGE_FILES = "files[]";
        public static final String KEY_MESSAGE_STATUS = "status";
        public static final String KEY_MESSAGE_DELIVERED_AT = "deliveredAt";
        public static final String KEY_MESSAGE_READ_AT = "readAt";
        public static final String KEY_MESSAGE_EDITED_AT = "editedAt";
        public static final String KEY_MESSAGE_DELETED_AT = "deletedAt";
        public static final String KEY_MESSAGE_DELETED_BY = "deletedBy";
        public static final String KEY_MESSAGE_EDITED_BY = "editedBy";
        public static final String KEY_MESSAGE_CATEGORY = "category";
        public static final String KEY_MESSAGE_ATTACHMENTS = "attachments";
        public static final String KEY_ATTACHMENT_NAME = "name";
        public static final String KEY_ATTACHMENT_EXTENSION = "extension";
        public static final String KEY_ATTACHMENT_SIZE = "size";
        public static final String KEY_ATTACHMENT_MIMETYPE = "mimeType";
        public static final String KEY_ATTACHMENT_URL = "url";
        public static final String KEY_ATTACHMENT_METADATA = "metadata";
        public static final String KEY_UPLOAD_FILES = "files";
        public static final String KEY_UPLOAD_FILE_ID = "fileId";
        // Metadata-key conventions for multi-attachment batch grouping. The SDK
        // never reads or writes these — apps/UIKits stamp them into message
        // metadata at send and interpret them at render; published here only so
        // every consumer agrees on the literal key names.
        public static final String KEY_METADATA_BATCH_ID = "batchId";
        public static final String KEY_METADATA_BATCH_INDEX = "batchIndex";
        public static final String KEY_METADATA_BATCH_SIZE = "batchSize";
        public static final String KEY_METADATA_AUDIO_TYPE = "audioType";
        public static final String AUDIO_TYPE_VOICE_NOTE = "voice_note";
        public static final String KEY_CUSTOM_SUB_TYPE = "subType";
        public static final String KEY_CUSTOM_CUSTOM_DATA = "customData";
        public static final String KEY_INTERACTIVE_INTERACTIVE_DATA = "interactiveData";
        public static final String KEY_INTERACTIVE_INTERACTION_GOAL = "interactionGoal";
        public static final String KEY_INTERACTIVE_INTERACTION_TYPE = "type";
        public static final String KEY_INTERACTIVE_INTERACTION_ELEMENT_IDS = "elementIds";
        public static final String KEY_INTERACTIVE_INTERACTIONS = "interactions";
        public static final String KEY_INTERACTIVE_ELEMENT_ID = "elementId";
        public static final String KEY_INTERACTIVE_INTERACTED_AT = "interactedAt";
        public static final String KEY_UPDATED_AT = "updatedAt";
        public static final String KEY_PARENT_MESSAGE_ID = "parentId";
        public static final String KEY_REPLY_COUNT = "replyCount";
        public static final String KEY_TAGS = "tags";
        public static final String KEY_WITH_TAGS = "withTags";
        public static final String KEY_MENTIONS_WITH_TAG_INFO = "mentionsWithTagInfo";
        public static final String KEY_MENTIONS_WITH_BLOCKED_INFO = "mentionsWithBlockedInfo";
        public static final String KEY_MENTIONS = "mentions";
        public static final String KEY_REACTIONS = "reactions";
        public static final String KEY_ONLY_INTERACTION_GOAL_COMPLETED = "onlyInteractionGoalCompleted";
        public static final String KEY_INTERACTIONS = "interactions";
        public static final String KEY_INTERACTIVE_ALLOW_SENDER_INTERACTION = "allowSenderInteraction";
        public static final String KEY_UNREAD_REPLY_COUNT = "unreadRepliesCount";
        public static final String KEY_CUSTOM_TEXT = "text";
        public static final String KEY_CUSTOM_UPDATE_CONVERSATION = "updateConversation";
        public static final String KEY_CUSTOM_SEND_NOTIFICATION = "sendNotification";
        public static final String KEY_HAS_ATTACHMENTS = "hasAttachments";
        public static final String KEY_HAS_LINKS = "hasLinks";
        public static final String KEY_HAS_MENTIONS = "hasMentions";
        public static final String KEY_HAS_REACTIONS = "hasReactions";
        public static final String KEY_MENTIONED_UIDS = "mentionedUids";
        public static final String KEY_ATTACHMENT_TYPE = "attachmentTypes";
        public static final String KEY_MODERATION = "moderation";
        public static final String KEY_MODERATION_STATUS = "status";
        public static final String KEY_AGENTIC_RUN_ID = "runId";
        public static final String KEY_AGENTIC_THREAD_ID = "threadId";
        public static final String KEY_AGENTIC_TEXT = "text";
        public static final String KEY_AGENTIC_TOOL_CALLS = "toolCalls";

        // AI Tool Call related constants
        public static final String KEY_TOOL_CALL_ID = "id";
        public static final String KEY_TOOL_CALL_TYPE = "type";
        public static final String KEY_TOOL_CALL_DISPLAY_NAME = "displayName";
        public static final String KEY_TOOL_CALL_EXECUTION_TEXT = "executionText";
        public static final String KEY_TOOL_CALL_FUNCTION = "function";

        // AI Tool Call Function related constants
        public static final String KEY_TOOL_CALL_FUNCTION_NAME = "name";
        public static final String KEY_TOOL_CALL_FUNCTION_ARGUMENTS = "arguments";
        public static final String KEY_AGENTIC_TOOL_CALL_ID = "toolCallId";
        public static final String KEY_AGENTIC_CARD = "card";
        public static final String KEY_AGENTIC_CARD_ID = "cardId";
        public static final String KEY_AGENTIC_CARD_FALLBACK_TEXT = "fallbackText";
        public static final String KEY_AGENTIC_ELEMENTS = "elements";
        public static final String KEY_AGENTIC_ELEMENT_TYPE = "type";
        public static final String KEY_AGENTIC_ELEMENT_VALUE = "value";
        public static final String KEY_WITH_PARENT = "withParent";

        // Swipe to reply
        public static final String KEY_HIDE_QUOTED_MESSAGES = "hideQuotedMessage";
        public static final String KEY_QUOTED_MESSAGE = "quotedMessage";
        public static final String KEY_QUOTED_MESSAGE_ID = "quotedMessageId";
    }

    public static final class GroupKeys {
        public static final String KEY_GROUP_GUID = "guid";
        public static final String KEY_GROUP_NAME = "name";
        public static final String KEY_GROUP_ICON = "icon";
        public static final String KEY_GROUP_DESCRIPTION = "description";
        public static final String KEY_GROUP_OWNER = "owner";
        public static final String KEY_GROUP_TYPE = "type";
        public static final String KEY_GROUP_PASSWORD = "password";
        public static final String KEY_METADATA = "metadata";
        public static final String KEY_CREATED_AT = "createdAt";
        public static final String KEY_UPDATED_AT = "updatedAt";
        public static final String KEY_HAS_JOINED = "hasJoined";
        public static final String KEY_GROUP_IDENTITY = "groupIdentity";
        public static final String KEY_GROUP_MEMBER_SCOPE = "scope";
        public static final String KEY_GROUP_MEMBER_JOINED_AT = "joinedAt";
        public static final String KEY_GROUP_MEMBERS_COUNT = "membersCount";
        public static final String GROUP_KEY_TAGS = "tags";
        public static final String GROUP_KEY_WITH_TAGS = "withTags";
        public static final String GROUP_KEY_MEMBERS = "members";
        public static final String GROUP_KEY_IS_BANNED = "isBanned";
    }

    public static final class SettingsKeys {
        public static final String SETTINGS_CHAT_HOST = "CHAT_HOST";
        public static final String SETTINGS_ADMIN_API_HOST = "ADMIN_API_HOST";
        public static final String SETTINGS_CLIENT_API_HOST = "CLIENT_API_HOST";
        public static final String SETTINGS_CHAT_USE_SSL = "XMPP_USE_SSL";
        public static final String SETTINGS_GROUP_SERVICE = "GROUP_SERVICE";
        public static final String SETTINGS_CALL_SERVICE = "CALL_SERVICE";
        public static final String SETTINGS_CHAT_WS_PORT = "CHAT_WS_PORT";
        public static final String SETTINGS_CHAT_WSS_PORT = "CHAT_WSS_PORT";
        public static final String SETTINGS_CHAT_HTTP_BIND_PORT = "CHAT_HTTP_BIND_PORT";
        public static final String SETTINGS_CHAT_HTTPS_BIND_PORT = "CHAT_HTTPS_BIND_PORT";
        public static final String SETTINGS_CONTACT_LIST = "contactList";
        public static final String WEBRTC_HOST = "WEBRTC_HOST";
        public static final String WEBRTC_USE_SSL = "WEBRTC_USE_SSL";
        public static final String WEBRTC_WS_PORT = "WEBRTC_WS_PORT";
        public static final String WEBRTC_WSS_PORT = "WEBRTC_WSS_PORT";
        public static final String WEBRTC_HTTP_BIND_PORT = "WEBRTC_HTTP_BIND_PORT";
        public static final String WEBRTC_HTTPS_BIND_PORT = "WEBRTC_HTTPS_BIND_PORT";
        public static final String SETTINGS_MODE = "MODE";
        public static final String SETTINGS_CHAT_HOST_OVERRIDE = "CHAT_HOST_OVERRIDE";
        public static final String SETTINGS_CHAT_HOST_APP_SPECIFIC = "CHAT_HOST_APP_SPECIFIC";
        public static final String SETTINGS_JID_HOST_OVERRIDE = "JID_HOST_OVERRIDE";
        public static final String SETTINGS_EXTENSIONS = "extensions";
        public static final String SETTINGS_DB_KEY = "cc_settings";
        public static final String SETTINGS_RTC_REGION_KEY = "RTC_REGION";
        public static final String SETTINGS_POLLING_ENABLED = "POLLING_ENABLED";
        public static final String SETTINGS_POLLING_INTERVAL = "POLLING_INTERVAL";
        public static final String SETTINGS_ANALYTICS_PING_DISABLED = "ANALYTICS_PING_DISABLED";
        public static final String SETTINGS_ANALYTICS_USE_SSL = "ANALYTICS_USE_SSL";
        public static final String SETTINGS_ANALYTICS_HOST = "ANALYTICS_HOST";
        public static final String SETTINGS_ANALYTICS_VERSION = "ANALYTICS_VERSION";
        public static final String SETTINGS_SETTINGS_HASH = "settingsHash";
        public static final String SETTINGS_SETTINGS_HASH_RECEIVED_AT = "settingsHashReceivedAt";
        public static final String DENY_FALLBACK_TO_POLLING = "DENY_FALLBACK_TO_POLLING";
        public static final String SETTINGS_APP_PARAMETERS = "parameters";
        public static final String SETTINGS_APP_VERSION = "APP_VERSION";
        public static final String MAIN_DOMAIN = "MAIN_DOMAIN";
        public static final String CHAT_API_VERSION = "CHAT_API_VERSION";
        public static final String WS_API_VERSION = "WS_API_VERSION";
        public static final String REGION = "REGION";
        public static final String EXTENSION_DOMAIN = "EXTENSION_DOMAIN";
        public static final String WEBRTC_API_SUBDOMAIN = "WEBRTC_API_SUBDOMAIN";
        public static final String SECURED_MEDIA_HOST = "SECURED_MEDIA_HOST";
        public static final String PARAMETERS = "parameters";
        public static final String CORE_CONVERSATIONS_UPDATE_ON_CALL_ACTIVITY = "core.conversations.updateOnCallActivity";
        public static final String CORE_CONVERSATIONS_UPDATE_ON_GROUP_ACTIONS = "core.conversations.updateOnGroupActions";
        public static final String CORE_CONVERSATIONS_UPDATE_ON_CUSTOM_MESSAGE = "core.conversations.updateOnCustomMessage";
        public static final String CORE_CONVERSATIONS_UPDATE_ON_REPLIES = "core.conversations.updateOnReplies";
        public static final String FILE_SIZE = "file.size.max";
        public static final String FILE_COUNT = "file.count.max";
        public static final String FLAG_REASONS = "flagReasons";
    }

    public static final class FlagReasonsKeys {
        public static final String ID = "id";
        public static final String CREATED_AT = "createdAt";
        public static final String DESCRIPTION = "description";
        public static final String NAME = "name";
        public static final String UPDATED_AT = "updatedAt";
    }

    public static final class FlagDetail {
        public static final String ID = "id";
        public static final String REASON = "reason";
    }

    public static final class ConversationUpdateSettingsKeys {
        public static final String CALL_ACTIVITIES = "callActivities";
        public static final String GROUP_ACTIONS = "groupActions";
        public static final String CUSTOM_MESSAGES = "customMessages";
        public static final String MESSAGE_REPLIES = "messageReplies";
    }

    @StringDef({MODE_DEFAULT, MODE_LIMITED_TRANSIENT, MODE_NO_TRANSIENT})
    @Retention(RetentionPolicy.SOURCE)

    public @interface Modes {
    }

    public static final String MODE_DEFAULT = "DEFAULT";
    public static final String MODE_LIMITED_TRANSIENT = "LIMITED_TRANSIENT";
    public static final String MODE_NO_TRANSIENT = "NO_TRANSIENT";


    public static final String AUDIO_MODE_SPEAKER = "SPEAKER";
    public static final String AUDIO_MODE_EARPIECE = "EARPIECE";
    public static final String AUDIO_MODE_BLUETOOTH = "BLUETOOTH";
    public static final String AUDIO_MODE_HEADPHONES = "HEADPHONES";

    @StringDef({AUDIO_MODE_EARPIECE, AUDIO_MODE_SPEAKER, AUDIO_MODE_BLUETOOTH, AUDIO_MODE_HEADPHONES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioModes {

    }

    public static final String RECEIVER_TYPE_USER = "user";
    public static final String RECEIVER_TYPE_GROUP = "group";

    @StringDef({RECEIVER_TYPE_USER, RECEIVER_TYPE_GROUP})
    @Retention(RetentionPolicy.SOURCE)

    public @interface ReceiverTypes {
    }

    public static final String CONVERSATION_TYPE_USER = "user";
    public static final String CONVERSATION_TYPE_GROUP = "group";

    @StringDef({CONVERSATION_TYPE_USER, CONVERSATION_TYPE_GROUP})
    @Retention(RetentionPolicy.SOURCE)

    public @interface ConversationTypes {
    }


    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String MESSAGE_TYPE_IMAGE = "image";
    public static final String MESSAGE_TYPE_VIDEO = "video";
    public static final String MESSAGE_TYPE_AUDIO = "audio";
    public static final String MESSAGE_TYPE_FILE = "file";
    public static final String MESSAGE_TYPE_CUSTOM = "custom";
    public static final String MESSAGE_TYPE_ASSISTANT = "assistant";
    public static final String MESSAGE_TYPE_TOOL_RESULT = "tool-result";
    public static final String MESSAGE_TYPE_TOOL_ARGUMENTS = "tool-arguments";

    @StringDef({MESSAGE_TYPE_TEXT, MESSAGE_TYPE_IMAGE, MESSAGE_TYPE_VIDEO, MESSAGE_TYPE_AUDIO, MESSAGE_TYPE_FILE, MESSAGE_TYPE_CUSTOM, MESSAGE_TYPE_ASSISTANT, MESSAGE_TYPE_TOOL_RESULT, MESSAGE_TYPE_TOOL_ARGUMENTS})
    @Retention(RetentionPolicy.SOURCE)

    public @interface MessageTypes {
    }

    public static final String USER_STATUS_ONLINE = "online";
    public static final String USER_STATUS_OFFLINE = "offline";

    @StringDef({USER_STATUS_ONLINE, USER_STATUS_OFFLINE})
    @Retention(RetentionPolicy.SOURCE)

    public @interface UserStatus {
    }

    public static final String GROUP_TYPE_PUBLIC = "public";
    public static final String GROUP_TYPE_PRIVATE = "private";
    public static final String GROUP_TYPE_PASSWORD = "password";

    @StringDef({GROUP_TYPE_PUBLIC, GROUP_TYPE_PRIVATE, GROUP_TYPE_PASSWORD})
    @Retention(RetentionPolicy.SOURCE)

    public @interface GroupTypes {
    }

    public static final String AFFIX_PREPEND = "prepend";
    public static final String AFFIX_APPEND = "append";

    @StringDef({AFFIX_PREPEND, AFFIX_APPEND})
    @Retention(RetentionPolicy.SOURCE)

    public @interface Affix {

    }

    public static final String CATEGORY_MESSAGE = "message";
    public static final String CATEGORY_ACTION = "action";
    public static final String CATEGORY_CALL = "call";
    public static final String CATEGORY_CUSTOM = "custom";
    public static final String CATEGORY_INTERACTIVE = "interactive";
    public static final String CATEGORY_AGENTIC = "agentic";
    public static final String CATEGORY_CARD = "card";

    @StringDef({CATEGORY_MESSAGE, CATEGORY_ACTION, CATEGORY_CALL, CATEGORY_CUSTOM, CATEGORY_INTERACTIVE, CATEGORY_AGENTIC, CATEGORY_CARD})
    @Retention(RetentionPolicy.SOURCE)

    public @interface MessageCategory {

    }

    public static final String SCOPE_ADMIN = "admin";
    public static final String SCOPE_MODERATOR = "moderator";
    public static final String SCOPE_PARTICIPANT = "participant";

    @StringDef({SCOPE_ADMIN, SCOPE_MODERATOR, SCOPE_PARTICIPANT})
    @Retention(RetentionPolicy.SOURCE)

    public @interface MemberScope {

    }

    public static final String INTERACTION_TYPE_ANY = "anyAction";
    public static final String INTERACTION_TYPE_ANY_OF = "anyOf";
    public static final String INTERACTION_TYPE_All_OF = "allOf";
    public static final String INTERACTION_TYPE_NONE = "none";

    @StringDef({INTERACTION_TYPE_ANY, INTERACTION_TYPE_ANY_OF, INTERACTION_TYPE_All_OF, INTERACTION_TYPE_NONE})
    @Retention(RetentionPolicy.SOURCE)

    public @interface InteractionType {

    }

    public static final class PresenceResponse {
        public static final String PRESENCE_IQ = "<iq from='%s' id='CC^PRESENCE' type='get'><query xmlns='jabber:iq:presencereq' action='presence'>[%s]</query></iq>";
        public static final String PRESENCE_ELEMENT_NAME = "presence";
        public static final String PRESENCE_ATTRIBUTE_FROM = "from";
        public static final String PRESENCE_ATTRIBUTE_STATUS = "status";
        public static final String PRESENCE_ATTRIBUTE_LASTACTIVEAT = "lastActiveAt";
        public static final String PRESENCE_ATTRIBUTE_TYPE = "type";
        public static final String PRESENCE_STANZA_ID = "CC^PRESENCE";
        public static final String PRESENCE_CHILD_ELEMENT_NAME = "query";
        public static final String PRESENCE_CHILD_ELEMENT_NAMESPACE = "jabber:iq:presencereq";
        public static final String PRESENCE_PROVIDER_ELEMENT_NAME = "presences";
        public static final String PRESENCE_PROVIDER_ELEMENT_NAMESPACE = "jabber:client";
    }

    public static final class PaginationKeys {
        public static final String KEY_PAGINATION = "pagination";
        public static final String KEY_META = "meta";
        public static final String KEY_PAGE = "page";
        public static final String KEY_CURSOR = "cursor";
        public static final String KEY_AFFIX = "affix";
        public static final String KEY_UID = "uid";
        public static final String KEY_CURSOR_VALUE = "cursorValue";
        public static final String KEY_CURSOR_FIELD = "cursorField";
        public static final String KEY_PER_PAGE = "per_page";
        public static final String KEY_FIELD_TIMESTAMP = "sentAt";
        public static final String KEY_FIELD_MESSAGEID = "id";
        public static final String KEY_PAGINATION_CURRENT_PAGE = "current_page";
        public static final String KEY_PAGINATION_TOTAL_PAGES = "total_pages";
        public static final String KEY_PAGINATION_PREVIOUS = "previous";
        public static final String KEY_PAGINATION_NEXT = "next";
        public static final String KEY_PAGINATION_ID = "id";
    }

    public static final class ActionKeys {
        public static final String KEY_BY = "by";
        public static final String KEY_ON = "on";
        public static final String KEY_FOR = "for";
        public static final String KEY_ENTITY_TYPE = "entityType";
        public static final String KEY_ENTITY = "entity";
        public static final String KEY_ENTITY_USER = "user";
        public static final String KEY_ENTITY_GROUP = "group";
        public static final String KEY_ENTITY_MESSAGE = "message";
        public static final String KEY_EXTRAS = "extras";
        public static final String KEY_SCOPE = "scope";
        public static final String KEY_OLD = "old";
        public static final String KEY_NEW = "new";

        public static final String ACTION_CREATED = "created";
        public static final String ACTION_UPDATED = "updated";
        public static final String ACTION_DELETED = "deleted";

        public static final String ACTION_JOINED = "joined";
        public static final String ACTION_LEFT = "left";
        public static final String ACTION_KICKED = "kicked";
        public static final String ACTION_BANNED = "banned";
        public static final String ACTION_UNBANNED = "unbanned";
        public static final String ACTION_SCOPE_CHANGED = "scopeChanged";
        public static final String ACTION_MESSAGE_EDITED = "edited";
        public static final String ACTION_MESSAGE_DELETED = "deleted";
        public static final String ACTION_MEMBER_ADDED = "added";

        public static final String ACTION_TYPE_USER = "user";
        public static final String ACTION_TYPE_GROUP = "group";
        public static final String ACTION_TYPE_GROUP_MEMBER = "groupMember";
        public static final String ACTION_TYPE_MESSAGE = "message";
        public static final String ACTION_TYPE_CALL = "call";
    }

    public static final class ActionMessages {
        public static final String ACTION_GROUP_JOINED_MESSAGE = "%s joined";
        public static final String ACTION_GROUP_LEFT_MESSAGE = "%s left";
        public static final String ACTION_MEMBER_KICKED_MESSAGE = "%s kicked %s";
        public static final String ACTION_MEMBER_BANNED_MESSAGE = "%s banned %s";
        public static final String ACTION_MEMBER_UNBANNED_MESSAGE = "%s unbanned %s";
        public static final String ACTION_MESSAGE_EDITED_MESSAGE = "Message Edited";
        public static final String ACTION_MESSAGE_DELETED_MESSAGE = "Message Deleted";
        public static final String ACTION_MEMBER_ADDED_TO_GROUP = "%s added %s";
        public static final String ACTION_MEMBER_SCOPE_CHANGED = "%s made %s %s";
    }

    public static final class CallKeys {
        public static final String CALL_ID = "id";
        public static final String CALL_SESSION_ID = "sessionid";
        public static final String CALL_SESSION_ID_CAMEL_CASE = "sessionId";
        public static final String CALL_RECEIVER = "receiver";
        public static final String CALL_SENDER = "sender";
        public static final String CALL_RECEIVER_TYPE = "receiverType";
        public static final String CALL_STATUS = "status";
        public static final String CALL_TYPE = "type";
        public static final String CALL_INITIATED_AT = "initiatedAt";
        public static final String CALL_JOINED_AT = "joinedAt";
        public static final String CALL_METADATA = "metadata";
        public static final String CALL_ENTITIES = "entities";
        public static final String CALL_ENTITY_TYPE = "entityType";
        public static final String CALL_ENTITY = "entity";
        public static final String CALL_ENTITY_USER = "user";
        public static final String CALL_ENTITY_GROUP = "group";
        public static final String KEY_PARTICIPANTS = "participants";
    }

    public static final class ExtraKeys {
        public static final String DELIMETER_DOT = ".";
        public static final String DELIMETER_OPEN_SQUARE_BRACE = "[";
        public static final String DELIMETER_CLOSE_SQUARE_BRACE = "]";
        public static final String DELIMETER_AT = "@";
        public static final String DELIMETER_SLASH = "/";
        public static final String KEYWORD_UNAVAILABLE = "unavailable";
        public static final String KEY_GROUP_CHAT = "groupchat";
        public static final String KEY_HTTPS = "https://";
        public static final int TYPING_LIMIT = 5000;
        public static final String KEY_ANDROID = "android";
        public static final String KEY_SPACE = " ";
        public static final String KEY_ADD_MEMBER_BANNED = "usersToBan";
        public static final String KEY_ADD_MEMBER_ADMINS = "admins";
        public static final String KEY_ADD_MEMBER_PARTICIPANTS = "participants";
        public static final String KEY_ADD_MEMBER_MODERATORS = "moderators";
        public static final String KEY_CALLING_JWT_PASSTHROUGH = "passthrough";
    }

    public static final class SuccessMessages {
        public static final String MESSAGE_INIT_SUCCESS = "Init Successful";
        public static final String MESSAGE_GROUP_JOIN_SUCCESS = "Group joined successfully.";
        public static final String MESSAGE_GROUP_LEAVE_SUCCESS = "Group left successfully.";
        public static final String MESSAGE_GROUP_DELETE_SUCCESS = "Group deleted successfully.";
        public static final String MESSAGE_MEMBER_KICKED_SUCCESS = "Group member kicked successfully";
        public static final String MESSAGE_MEMBER_BANNED_SUCCESS = "Group member banned successfully";
        public static final String MESSAGE_MEMBER_UNBANNED_SUCCESS = "Group member unbanned successfully.";
        public static final String MESSAGE_MEMBER_SCOPE_CHANGED_SUCCESS = "Group member scope changed successfully.";
        public static final String MESSAGE_LOGOUT_SUCCESS = "User logged out successfully.";
        public static final String MESSAGE_REGISTRATION_SUCCESS = "Token Registration successful.";
        public static final String MESSAGE_TRANSFER_OWNERSHIP_SUCCESS = "Group Ownership Transferred successfully.";
        public static final String MESSAGE_CONVERSATION_DELETE_SUCCESS = "Conversation deleted successfully.";
        public static final String MESSAGE_WS_CONNECTION_DISCONNECT = "Websocket connection terminated successfully";
        public static final String MESSAGE_WS_CONNECTION_ALREADY_CONNECTED = "Websocket is already connected";
        public static final String MESSAGE_WS_CONNECTION_ALREADY_DISCONNECTED = "Websocket is already disconnected";
    }

    public static final class XMPPStanzas {
        public static final String STANZA_DELIVERY_TAG = "<delivered xmlns='urn:xmpp:receipts' id='%s' receiverId='%s' type='%s'/>";
        public static final String STANZA_READ_TAG = "<read xmlns='urn:xmpp:receipts' id='%s' receiverId='%s' type='%s'/>";
        public static final String STANZA_USER_TAG = "<user xmlns='com.cometchat.models:User'>%s</user>";
        public static final String STANZA_COMPOSING_TAG = "<composing xmlns='http://jabber.org/protocol/chatstates'/>";
        public static final String STANZA_PAUSED_TAG = "<paused xmlns='http://jabber.org/protocol/chatstates'/>";

    }

    public static final class XMPPKeys {
        public static final String XMPP_KEY_JABBER_CLIENT = "jabber:client";
        public static final String XMPP_KEY_DELIVERED_AT = "deliveredAt";
        public static final String XMPP_KEY_READ_AT = "readAt";
        public static final String XMPP_KEY_TIME = "time";
        public static final String XMPP_KEY_DELIVERED = "delivered";
        public static final String XMPP_KEY_READ = "read";
        public static final String XMPP_RECEIPTS_NAMESPACE = "urn:xmpp:receipts";
        public static final String KEY_FORWARD_NAMESPACE = "urn:xmpp:forward:0";
        public static final String KEY_FORWARDED = "forwarded";
        public static final String KEY_SENT = "sent";
        public static final String KEY_RECEIVED = "received";
        public static final String KEY_MUC_JOIN = "muc_join";
        public static final String KEY_MUC_JOIN_NAMESPACE = "cometchat.io.muc.join";
        public static final String KEY_SETTINGS = "settings";
        public static final String KEY_SETTINGS_NAMESPACE = "com.cometchat:settings";
        public static final String KEY_USER = "user";
        public static final String KEY_USER_NAMESPACE = "com.cometchat.models:User";
        public static final String KEY_COMPOSING = "composing";
        public static final String KEY_COMPOSING_NAMESPACE = "http://jabber.org/protocol/chatstates";
        public static final String KEY_PAUSED = "paused";
        public static final String KEY_PAUSED_NAMESPACE = "http://jabber.org/protocol/chatstates";
    }

    public static final class WSKeys {
        public static final String KEY_TYPE_TYPING_INDICATOR = "typing_indicator";
        public static final String KEY_APP_ID = "appId";
        public static final String KEY_RECEIVER = "receiver";
        public static final String KEY_RECEIVER_TYPE = "receiverType";
        public static final String KEY_DEVICE_ID = "deviceId";
        public static final String KEY_TYPE = "type";
        public static final String KEY_ACK = "ack";
        public static final String KEY_ACTION = "action";
        public static final String KEY_USER = "user";
        public static final String KEY_METADATA = "metadata";
        public static final String KEY_BODY = "body";
        public static final String KEY_TYPE_PRESENCE = "presence";
        public static final String KEY_TYPE_AUTH = "auth";
        public static final String KEY_TYPE_PING = "ping";
        public static final String KEY_TYPE_PONG = "pong";
        public static final String KEY_TYPE_RECEIPTS = "receipts";
        public static final String KEY_TYPE_STREAMED_MESSAGE = "streamed_message";
        public static final String KEY_INTERACTION_COMPLETED = "interaction_completed";
        public static final String KEY_TYPE_MESSAGE = "message";
        public static final String KEY_MESSAGE_ID = "messageId";
        public static final String KEY_ACTION_PING = "ping";
        public static final String KEY_ACTION_PONG = "pong";
        public static final String KEY_PRESENCE_SUBSCRIPTION = "presenceSubscription";
        public static final String KEY_TYPE_ROLES = "roles";
        public static final String KEY_AUTH_CODE = "code";
        public static final String KEY_AUTH_STATUS = "status";
        public static final String KEY_STATUS_OK = "OK";
        public static final String KEY_TIMESTAMP = "timestamp";
        public static final String KEY_SENDER = "sender";
        public static final String KEY_MESSAGE_SENDER = "messageSender";
        public static final int KEY_CODE_LOGOUT = 1002;
        public static final String KEY_CODE_LOGOUT_REASON = "USER_LOGGED_OUT";
        public static final String KEY_TYPE_TRANSIENT_MESSAGE = "transient_message";
        public static final String KEY_DATA = "data";
        public static final String KEY_ACTION_MESSAGE_REACTION_ADDED = "message_reaction_added";
        public static final String KEY_ACTION_MESSAGE_REACTION_REMOVED = "message_reaction_removed";
        public static final String KEY_REACTION = "reaction";
        public static final String KEY_MODERATION_CHANGED = "on_moderation_status_changed";

        public static final String KEY_AI_ASSISTANT_BASE_EVENT_TYPE = "type";
        public static final String KEY_AI_ASSISTANT_BASE_EVENT_CONVERSATION_ID = "conversationId";
        public static final String KEY_AI_ASSISTANT_BASE_EVENT_ID = "id";
        public static final String KEY_AI_ASSISTANT_BASE_EVENT_PARENT_ID = "parentId";
        public static final String KEY_AI_ASSISTANT_BASE_EVENT_DATA = "data";
        public static final String KEY_AI_ASSISTANT_EVENT_RUN_ID = "runId";
        public static final String KEY_AI_ASSISTANT_EVENT_THREAD_ID = "threadId";
        public static final String KEY_AI_ASSISTANT_EVENT_STREAM_MESSAGE_ID = "streamMessageId";
        public static final String KEY_AI_ASSISTANT_EVENT_ROLE = "role";
        public static final String KEY_AI_ASSISTANT_EVENT_DELTA = "delta";
        public static final String KEY_AI_ASSISTANT_EVENT_TYPE = "type";
        public static final String KEY_AI_ASSISTANT_EVENT_MESSAGE_ID = "messageId";
        public static final String KEY_AI_ASSISTANT_EVENT_STREAM_PARENT_MESSAGE_ID = "streamParentMessageId";
        public static final String KEY_AI_ASSISTANT_EVENT_TOOL_CALL_ID = "toolCallId";
        public static final String KEY_AI_ASSISTANT_EVENT_TOOL_CALL_NAME = "toolCallName";
        public static final String KEY_AI_ASSISTANT_EVENT_DISPLAY_NAME = "displayName";
        public static final String KEY_AI_ASSISTANT_EVENT_EXECUTION_TEXT = "executionText";

        public static final String KEY_AI_ASSISTANT_EVENT_ARGUMENTS = "arguments";
        public static final String AI_ASSISTANT_EVENT_RUN_STARTED = "run_started";
        public static final String AI_ASSISTANT_EVENT_RUN_FINISHED = "run_finished";
        public static final String AI_ASSISTANT_EVENT_TEXT_MESSAGE_START = "text_message_start";
        public static final String AI_ASSISTANT_EVENT_TEXT_MESSAGE_END = "text_message_end";
        public static final String AI_ASSISTANT_EVENT_TEXT_MESSAGE_CONTENT = "text_message_content";
        public static final String AI_ASSISTANT_EVENT_TOOL_CALL_STARTED = "tool_call_start";
        public static final String AI_ASSISTANT_EVENT_TOOL_CALL_ENDED = "tool_call_end";
        public static final String AI_ASSISTANT_EVENT_TOOL_CALL_RESULT = "tool_call_result";
        public static final String AI_ASSISTANT_EVENT_TOOL_CALL_ARGUMENT = "tool_call_args";
        public static final String KEY_STREAMED_MESSAGE = "streamed_message";

        public static final String KEY_AI_ASSISTANT_EVENT_CONTENT = "content";
        public static final String AI_ASSISTANT_EVENT_CARD_STARTED = "card_start";
        public static final String AI_ASSISTANT_EVENT_CARD = "card";
        public static final String AI_ASSISTANT_EVENT_CARD_ENDED = "card_end";
        public static final String KEY_AI_ASSISTANT_EVENT_CARD_ID = "cardId";
    }

    public static final class AppInfoKeys {
        public static final String KEY_PLATFORM = "platform";
        public static final String KEY_USER_AGENT = "userAgent";
        public static final String KEY_DEVICE_ID = "deviceId";
        public static final String KEY_WS_ID = "wsId";
        public static final String KEY_APP_INFO = "appInfo";
        public static final String KEY_APP_INFO_VERSION = "version";
        public static final String KEY_APP_INFO_API_VERSION = "apiVersion";
        public static final String KEY_APP_INFO_OS_VERSION = "osVersion";
        public static final String KEY_PLATFORM_ANDROID = "Android";
        public static final String KEY_APPINFO_RESOURCE = "resource";
        public static final String KEY_APPINFO_PLATFORM = "platform";
        public static final String KEY_APPINFO_LANGUAGE = "language";
        public static final String KEY_USER_AGENT_ANDROID = "cc_android_sdk";
        public static final String KEY_APPINFO_SID = "sid";
        public static final String KEY_APPINFO_ORIGIN = "origin";
        public static final String KEY_APPINFO_UTS = "uts";

    }

    public static final class ConversationKeys {
        public static final String KEY_CONVERSATION_ID = "conversationId";
        public static final String KEY_CONVERSATION_TYPE = "conversationType";
        public static final String KEY_UNREAD_MESSAGE_COUNT = "unreadMessageCount";
        public static final String KEY_UPDATED_AT = "updatedAt";
        public static final String KEY_LAST_MESSAGE = "lastMessage";
        public static final String KEY_CONVERSATION_WITH = "conversationWith";
        public static final String KEY_WITH_USER_AND_GROUP_TAGS = "withUserAndGroupTags";
        public static final String KEY_TAGS = "tags";
        public static final String KEY_WITH_TAGS = "withTags";
        public static final String KEY_USER_TAGS = "userTags";
        public static final String KEY_GROUP_TAGS = "groupTags";
        public static final String KEY_UNREAD_MENTIONS_COUNT = "unreadMentionsCount";
        public static final String KEY_LAST_READ_MESSAGE_ID = "lastReadMessageId";
        public static final String KEY_INCLUDE_BLOCKED_USERS = "includeBlockedUsers";
        public static final String KEY_WITH_BLOCKED_INFO = "withBlockedInfo";
        public static final String KEY_LATEST_MESSAGE_ID = "latestMessageId";
    }

    public static final class SdkIdentificationKeys {
        public static final String SESSION_ID = "sessionId";
        public static final String PLATFORM = "platform";
        public static final String BUNDLE_ID = "bundle";
        public static final String VERSION = "version";
        public static final String UI_KIT = "uikit";
        public static final String BUILDER = "builder";
        public static final String UI_KIT_VERSION = "uikitVersion";
        public static final String CHAT_SDK = "chatsdk";
        public static final String CHAT_SDK_VERSION = "chatsdkVersion";
        public static final String CALLS_SDK = "callssdk";
        public static final String CALLS_SDK_VERSION = "callssdkVersion";
        public static final String CALLS_UIKIT = "callsuikit";
        public static final String CALLS_UIKIT_VERSION = "callsuikitVersion";
        public static final String DEMO_VERSION = "demoVersion";
        public static final String APP_VERSION = "appVersion";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String DEMO = "demo";
        public static final String COMETCHAT = "cometchat";
        public static final String DEBUG = "debug";
        public static final String APP = "app";
        public static final String ID = "id";
        public static final String LANGUAGE = "language";
        public static final String MODEL = "model";
        public static final String RESOLUTION = "resolution";
        public static final String USER_AGENT = "userAgent";
        public static final String TIME_ZONE = "timezone";
        public static final String CAMERA = "camera";
        public static final String MIC = "mic";
        public static final String DEVICE = "device";
        public static final String OS = "os";
        public static final String META = "meta";
        public static final String GENERATED_AT = "generatedAt";
        public static final String DATA = "data";
        public static final String CC_DATA = "ccData";
        public static final String INTEGRATION_SOURCE = "integrationSource";
    }

    public static final class AIKeys{
        public static final String KEY_ON_BEHALF_OF_USER = "onBehalfOfUser";
        public static final String KEY_SMART_REPLIES = "smart-replies";
        public static final String KEY_CONVERSATION_STARTER = "conversation-starter";
        public static final String KEY_AI_FEATURE_ACCESSIBLE = "features.ai.accessible";
        public static final String KEY_AI_FEATURE_ENABLED = "features.ai.enabled";

        public static final String KEY_AI_SLUG_ACCESSIBLE = "features.ai.%s.accessible";

        public static final String KEY_AI_SLUG_ENABLED = "features.ai.%s.enabled";

        public static final String KEY_CONVERSATION_SUMMARY = "conversation-summary";
        public static final String KEY_AI_BOT_REPLY = "bot-reply";
    }

    public static final class PubSubKeys {
        public static final String PUBSUB_GROBAL_PRESENCE = "global_presence";
        public static final String PUBSUB_SERVICE = "pubsub.%s";
    }

    public static final class ReactionsKeys {
        //For ReactionEvent DTO
        public static final String KEY_RECEIVER_ID = "receiver";
        public static final String KEY_RECEIVER_TYPE = "receiverType";
        public static final String KEY_CONVERSATION_ID = "conversationId";
        public static final String KEY_PARENT_ID = "parentId";
        public static final String KEY_REACTION = "reaction";
        //For Reaction DTO
        public static final String KEY_REACTIONS_ID = "id";
        public static final String KEY_REACTIONS_MESSAGE_ID = "messageId";
        public static final String KEY_REACTIONS_REACTION = "reaction";
        public static final String KEY_REACTIONS_UID = "uid";
        public static final String KEY_REACTIONS_REACTED_AT = "reactedAt";
        public static final String KEY_REACTIONS_REACTED_BY = "reactedBy";
        public static final String KEY_REACTIONS_COUNT = "count";
        public static final String KEY_REACTIONS_REACTED_BY_ME = "reactedByMe";
    }

    public static final String CALL_TYPE_AUDIO = "audio";
    public static final String CALL_TYPE_VIDEO = "video";

    @StringDef({CALL_TYPE_AUDIO, CALL_TYPE_VIDEO})
    @Retention(RetentionPolicy.SOURCE)

    public @interface CallType {

    }

    public static final String CALL_STATUS_INITIATED = "initiated";
    public static final String CALL_STATUS_ONGOING = "ongoing";
    public static final String CALL_STATUS_UNANSWERED = "unanswered";
    public static final String CALL_STATUS_REJECTED = "rejected";
    public static final String CALL_STATUS_BUSY = "busy";
    public static final String CALL_STATUS_CANCELLED = "cancelled";
    public static final String CALL_STATUS_ENDED = "ended";

    @StringDef({CALL_STATUS_INITIATED, CALL_STATUS_ONGOING, CALL_STATUS_UNANSWERED, CALL_STATUS_REJECTED, CALL_STATUS_BUSY, CALL_STATUS_CANCELLED, CALL_STATUS_ENDED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallStatus {

    }

    public static final String WS_STATE_CONNECTED = "connected";
    public static final String WS_STATE_CONNECTING = "connecting";
    public static final String WS_STATE_DISCONNECTED = "disconnected";
    public static final String WS_STATE_FEATURE_THROTTLED = "featureThrottled";
    public static final String WS_STATE_ERROR = "onError";

    @StringDef({WS_STATE_CONNECTED, WS_STATE_CONNECTING, WS_STATE_DISCONNECTED, WS_STATE_FEATURE_THROTTLED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WSState {

    }

    public static final String SORT_BY_STATUS = "status";
    public static final String SORT_BY_NAME = "name";

    @StringDef({SORT_BY_STATUS, SORT_BY_NAME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortBy {

    }

    public static final String SORT_ORDER_ASCENDING = "asc";
    public static final String SORT_ORDER_DESCENDING = "desc";

    @StringDef({SORT_ORDER_ASCENDING, SORT_ORDER_DESCENDING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortOrder {

    }

    public static final String REACTION_ADDED = "added";
    public static final String REACTION_REMOVED = "removed";

    @StringDef({REACTION_ADDED, REACTION_REMOVED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReactionAction {

    }

    // region Notification Feed Constants

    /**
     * JSON keys for NotificationFeedItem deserialization.
     */
    public static final class NotificationFeedKeys {
        public static final String KEY_ID = "id";
        public static final String KEY_SUB_CATEGORY = "subCategory";
        public static final String KEY_CATEGORY = "templateCategory";
        public static final String KEY_DATA = "data";
        public static final String KEY_CONTENT = "content";
        public static final String KEY_READ_AT = "readAt";
        public static final String KEY_DELIVERED_AT = "deliveredAt";
        public static final String KEY_SENT_AT = "sentAt";
        public static final String KEY_METADATA = "metadata";
        public static final String KEY_TAGS = "tags";
        public static final String KEY_SENDER = "sender";
        public static final String KEY_RECEIVER = "receiver";
        public static final String KEY_RECEIVER_TYPE = "receiverType";
        public static final String KEY_CURSOR = "cursor";
        public static final String KEY_LIMIT = "limit";
        public static final String KEY_READ_STATE = "readState";
        public static final String KEY_CHANNEL_ID = "channelId";
        public static final String KEY_DATE_FROM = "dateFrom";
        public static final String KEY_DATE_TO = "dateTo";
        public static final String KEY_COUNT = "count";
        public static final String KEY_TYPE = "type";
        public static final String KEY_ACTION = "action";
        public static final String KEY_BODY = "body";
        public static final String KEY_FEED_ITEM = "feedItem";
    }

    /**
     * JSON keys for NotificationCategory deserialization.
     */
    public static final class NotificationCategoryKeys {
        public static final String KEY_ID = "id";
        public static final String KEY_LABEL = "label";
    }

    /**
     * JSON keys for PushNotification deserialization.
     */
    public static final class PushNotificationKeys {
        public static final String KEY_ID = "id";
        public static final String KEY_ANNOUNCEMENT_ID = "announcementId";
        public static final String KEY_CAMPAIGN_ID = "campaignId";
        public static final String KEY_SOURCE = "source";
    }

    /**
     * API paths for Notification Feed endpoints.
     */
    public static final class NotificationFeedPaths {
        public static final String NOTIFICATION_FEED = "campaigns/notification-feed";
        public static final String NOTIFICATION_FEED_UNREAD_COUNT = "campaigns/notification-feed/unread-count";
        public static final String NOTIFICATION_FEED_DELIVERED = "campaigns/notification-feed/%s/delivered";
        public static final String NOTIFICATION_FEED_READ = "campaigns/notification-feed/%s/read";
        public static final String NOTIFICATION_FEED_ENGAGEMENT = "campaigns/notification-feed/%s/engagement";
        public static final String ANNOUNCEMENTS_READ = "announcements/read";
        public static final String ANNOUNCEMENTS_SINGLE = "announcements/%s";
        public static final String TEMPLATE_CATEGORIES = "campaigns/templates/categories";
        public static final String PUSH_NOTIFICATION_DELIVERED = "campaigns/push-notifications/%s/delivered";
        public static final String PUSH_NOTIFICATION_CLICKED = "campaigns/push-notifications/%s/clicked";
    }

    /**
     * WebSocket type for notification feed items.
     */
    public static final String WS_TYPE_NOTIFICATION_FEED_ITEM = "notification_feed_item";

    /**
     * WebSocket action for new feed items.
     */
    public static final String WS_ACTION_SENT = "sent";

    // endregion

}
