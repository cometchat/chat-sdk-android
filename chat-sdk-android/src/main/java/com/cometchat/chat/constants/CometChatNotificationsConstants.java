package com.cometchat.chat.constants;

/**
 * Created by Rohit Giri on 09/02/24.
 */
public class CometChatNotificationsConstants {
    public static final class ResponseKeys {
        public static final String KEY_DATA = "data";
        public static final String KEY_SUCCESS = "success";
        public static final String KEY_MESSAGE = "message";
        public static final String KEY_MUTED_CONVERSATIONS = "mutedConversations";
        public static final String KEY_TIMEZONE = "timezone";
    }

    public static final class RequestKeys {
        public static final String KEY_CONVERSATIONS = "conversations";
        public static final String KEY_TIMEZONE = "timezone";
    }

    public static final class GroupPreferencesKeys {
        public static final String KEY_GROUP_MESSAGES = "groupMessages";
        public static final String KEY_GROUP_REPLIES = "groupReplies";
        public static final String KEY_GROUP_REACTIONS = "groupReactions";
        public static final String KEY_GROUP_MEMBER_LEFT = "groupMemberLeft";
        public static final String KEY_GROUP_MEMBER_ADDED = "groupMemberAdded";
        public static final String KEY_GROUP_MEMBER_JOINED = "groupMemberJoined";
        public static final String KEY_GROUP_MEMBER_KICKED = "groupMemberKicked";
        public static final String KEY_GROUP_MEMBER_BANNED = "groupMemberBanned";
        public static final String KEY_GROUP_MEMBER_UNBANNED = "groupMemberUnbanned";
        public static final String KEY_GROUP_MEMBER_SCOPE_CHANGED = "groupMemberScopeChanged";
    }

    public static final class OneOnOnePreferencesKeys {
        public static final String ONE_ON_ONE_MESSAGES = "oneOnOneMessages";
        public static final String ONE_ON_ONE_REPLIES = "oneOnOneReplies";
        public static final String ONE_ON_ONE_REACTIONS = "oneOnOneReactions";
    }

    public static final class DayScheduleKeys {
        public static final String KEY_FROM = "from";
        public static final String KEY_TO = "to";
        public static final String KEY_DND = "dnd";
    }

    public static final class MutePreferencesKeys {
        public static final String KEY_DND = "dnd";
        public static final String KEY_SCHEDULE = "schedule";
    }

    public static final class MutedConversationKeys {
        public static final String KEY_ID = "id";
        public static final String KEY_TYPE = "type";
        public static final String KEY_UNTIL = "until";
    }

    public static final class UnmutedConversationKeys {
        public static final String KEY_ID = "id";
        public static final String KEY_TYPE = "type";
    }

    public static final class NotificationPreferencesKeys {
        public static final String KEY_USE_PRIVACY_TEMPLATE = "usePrivacyTemplate";
        public static final String KEY_ONE_ON_ONE_PREFERENCES = "oneOnOnePreferences";
        public static final String KEY_MUTE_PREFERENCES = "mutePreferences";
        public static final String KEY_GROUP_PREFERENCES = "groupPreferences";
    }

    public static final class HeadersKeys {
        public static final String KEY_APP_ID = "appid";
        public static final String KEY_AUTH_TOKEN = "authToken";
        public static final String KEY_CHAT_API_VERSION = "chatApiVersion";
    }

    public static final class TokenRegisterKeys {
        public static final String KEY_TIME_ZONE = "timezone";
        public static final String KEY_PLATFORM = "platform";
        public static final String KEY_FCM_TOKEN = "fcmToken";
        public static final String KEY_PROVIDER_ID = "providerId";
    }

    public static final class Errors {
        //Error Codes
        public static final String ERROR_INVALID_REQUEST = "ERROR_INVALID_REQUEST";
        public static final String ERROR_UNHANDLED_EXCEPTION = "ERROR_UNHANDLED_EXCEPTION";


        //Error Codes Messages
        public static final String ERROR_INVALID_UPDATE_REQUEST_MESSAGE = "Requested update request data is invalid";
        public static final String ERROR_INVALID_UNMUTE_REQUEST_MESSAGE = "Requested unmute request data is invalid";
        public static final String ERROR_INVALID_MUTE_REQUEST_MESSAGE = "Requested mute request data is invalid";
    }

    public static final class Success {
        //Success Messages
        public static final String TIMEZONE_UPDATE_SUCCESS = "Timezone updated successfully.";
        public static final String SUCCESS_TOKEN_REGISTER_MESSAGE = "Token registered successfully";
        public static final String SUCCESS_TOKEN_UNREGISTER_MESSAGE = "Token unregistered successfully";
        public static final String SUCCESS_CONVERSATION_UNMUTE_MESSAGE = "Conversations unmuted successfully";
        public static final String SUCCESS_CONVERSATION_MUTE_MESSAGE = "Conversations muted successfully";
    }

}
