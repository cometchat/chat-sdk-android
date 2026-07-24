package com.cometchat.chat.utils;

public class CometChatTestConstants {
    // Prod-App
    public static final String APP_ID = "2392275800af7835";
    public static final String VALID_API_KEY = "526c6f536d08f2d6cf5f435d8571bf8b9f6cd485";

    // Staging-Appp
    /*public static final String APP_ID = "1124dc2277fdd9";
    public static final String VALID_API_KEY = "f9388c32f33bac57f0448a2c8e163d3d0334e82c";*/

    public static final boolean isStaging = false;

    public static final String REGION = "eu";
    public static final String DEFAULT_PROD_URL = "https://api-%s.cometchat.io/v3.0/";
    public static final String DEFAULT_STAGING_URL = "https://" + APP_ID + ".api-%s.cometchat-staging.com/v3.0/";

    public static final String INVALID_DATA = "abc";
    public static final String EMPTY_DATA = "";
    public static final Object NULL_DATA = null;
    public static final String LOGIN_UID = "superhero1";
    public static final String RECEIVER_UID_1 = "superhero2";
    public static final String RECEIVER_UID_2 = "superhero3";
    public static final String RECEIVER_UID_3 = "superhero4";
    public static final String RECEIVER_UID_4 = "superhero5";
    public static final String RECEIVER_GUID = "supergroup";
    public static final String INVALID_UID = "superhero10";
    public static final String DEFAULT_MESSAGE_TEXT = "Hello World Test (%s)";
    public static final String DEFAULT_EDITED_MESSAGE_TEXT = DEFAULT_MESSAGE_TEXT + " (Edited) ";
    public static final int INVALID_MESSAGE_ID = 123456789;
    public static final String CREATE_USER_UID_PREFIX = "testuser_%s";
    public static final String CREATE_USER_NAME_PREFIX = "Test User %s";
    public static final String DEFAULT_AVATAR_URL = "https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50?f=y";
    public static final String DEFAULT_PUBLIC_GROUP_GUID = "public_%s";
    public static final String DEFAULT_PUBLIC_GROUP_NAME = "Public %s";
    public static final String DEFAULT_PRIVATE_GROUP_GUID = "private_%s";
    public static final String DEFAULT_PRIVATE_GROUP_NAME = "Private %s";
    public static final String DEFAULT_PASSWORD_GROUP_GUID = "password_%s";
    public static final String DEFAULT_PASSWORD_GROUP_NAME = "Password %s";
    public static final String DEFAULT_PASSWORD_GROUP_PASSWORD = "password";
    public static final String USER_TAG_1 = "user_tag_1";
    public static final String USER_TAG_2 = "user_tag_2";
    public static final String USER_TAG_EDITED = "user_tag_edited";
    public static final String USER_TAG_FINAL = "user_tag_final";
    public static final String CREATE_USER_UID_WITH_TAG = "superhero_tag";


    public static final class APIErrors {
        public static final String API_UID_NOT_FOUND = "ERR_UID_NOT_FOUND";
        public static final String API_GUID_NOT_FOUND = "ERR_GUID_NOT_FOUND";
        public static final String API_API_KEY_NOT_FOUND = "AUTH_ERR_APIKEY_NOT_FOUND";
        public static final String API_AUTH_TOKEN_NOT_FOUND = "AUTH_ERR_AUTH_TOKEN_NOT_FOUND";
        public static final String API_ERR_MESSAGE_ID_NOT_FOUND = "ERR_MESSAGE_ID_NOT_FOUND";
        public static final String ERROR_ERR_BAD_REQUEST = "ERR_BAD_REQUEST";
        public static final String API_ERR_BLOCKED_RECEIVER = "ERR_BLOCKED_RECEIVER";
        public static final String API_ERROR_BAD_REQUEST = "ERR_BAD_REQUEST";
        public static final String API_ERROR_NOT_A_MEMBER = "ERR_NOT_A_MEMBER";
        public static final String API_ERROR_GROUP_NOT_JOINED = "ERR_GROUP_NOT_JOINED";
        public static final String ERROR_PASSWORD_INCORRECT = "ERR_WRONG_GROUP_PASS";
        public static final String API_ERR_GROUP_JOIN_NOT_ALLOWED = "ERR_GROUP_JOIN_NOT_ALLOWED";
        public static final String API_ERR_GROUP_NO_MODERATOR_SCOPE = "ERR_GROUP_NO_MODERATOR_SCOPE";
        public static final String API_ERR_GROUP_NO_SCOPE_CLEARANCE = "ERR_GROUP_NO_SCOPE_CLEARANCE";
        public static final String API_ERR_GROUP_NO_CLEARANCE = "ERR_GROUP_NO_CLEARANCE";
        public static final String API_ERR_BANNED_GROUPMEMBER = "ERR_BANNED_GROUPMEMBER";
        public static final String API_ERR_CONVERSATION_NOT_ACCESSIBLE = "ERR_CONVERSATION_NOT_ACCESSIBLE";

    }
}
