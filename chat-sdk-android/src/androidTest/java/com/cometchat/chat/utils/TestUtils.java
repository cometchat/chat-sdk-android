package com.cometchat.chat.utils;

import android.text.TextUtils;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.Call;
import com.cometchat.chat.core.CometChatUtils;
import com.cometchat.chat.core.Settings;
import com.cometchat.chat.models.Action;
import com.cometchat.chat.models.AppEntity;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.Conversation;
import com.cometchat.chat.models.CustomMessage;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.List;

public class TestUtils {

    public static String getMethodName() {

        String nameofCurrMethod = new Throwable()
                .getStackTrace()[1]
                .getMethodName();

        return nameofCurrMethod;
    }

    public static String validateUser(User user) {
        String isValid = "";
        if (user == null)
            isValid = "User is NULL";
        else if (user.getUid() == null || TextUtils.isEmpty(user.getUid()))
            isValid = "User UID is NULL";
        else if (user.getName() == null || TextUtils.isEmpty(user.getName()))
            isValid = "User name is NULL";
        else if (user.getRole() == null || TextUtils.isEmpty(user.getRole()))
            isValid = "User role is NULL";
        else if (user.getStatus() == null || TextUtils.isEmpty(user.getStatus()))
            isValid = "User status is NULL";
        return isValid;

    }

    public static String validateGroup(Group group, boolean isCreateGroup) {
        String isValid = "";
        if (group == null)
            isValid = "Group is NULL";
        else if (group.getGuid() == null || TextUtils.isEmpty(group.getGuid()))
            isValid = "Group GUID is NULL";
        else if (group.getName() == null || TextUtils.isEmpty(group.getName()))
            isValid = "Group name is NULL";
        else if (group.getGroupType() == null || TextUtils.isEmpty(group.getGroupType()) ||
                (!group.getGroupType().equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PUBLIC) && !group.getGroupType().equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PASSWORD) && !group.getGroupType().equalsIgnoreCase(CometChatConstants.GROUP_TYPE_PRIVATE)))
            isValid = "Group type null or invalid";
        else if (group.getOwner() == null || TextUtils.isEmpty(group.getOwner()))
            isValid = "Group owner is null or empty";
        else if (group.getCreatedAt() == 0)
            isValid = "Group createdAt = 0";
        else if ((group.getScope() == null || TextUtils.isEmpty(group.getScope())) && !isCreateGroup)
            isValid = "Group scope is null or empty";
        else if(group.getMembersCount() <=0)
            isValid = "Group member count <= 0";
        return isValid;

    }

    public static String validateConversationList (List<Conversation> conversations){
        String isValid = "";
        for(Conversation conversation : conversations){
            String validateConversation = validateConversation(conversation);
            if (!validateConversation.equalsIgnoreCase(""))
                isValid = "Conversation with CID : " + conversation.getConversationId() + " : " + validateConversation;
        }
        return isValid;
    }
    public static String validateConversation(Conversation conversation) {
        String isValid = "";
        if (conversation == null)
            isValid = "Conversation is null";
        else if (conversation.getConversationId() == null || TextUtils.isEmpty(conversation.getConversationId()))
            isValid = "Conversation Id is null";
        else if (conversation.getConversationType() == null || TextUtils.isEmpty(conversation.getConversationId()) ||
                (!conversation.getConversationType().equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER) &&
                        !conversation.getConversationType().equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)))
            isValid = "Conversation type is null or invalid";
        else if(!validateAppEntity(conversation.getConversationWith()).equalsIgnoreCase(""))
            isValid = validateAppEntity(conversation.getConversationWith());
        else if (conversation.getLastMessage()!=null && validateBaseMessage(conversation.getLastMessage()).equalsIgnoreCase("")){
            isValid = validateBaseMessage(conversation.getLastMessage());
        }else if(conversation.getUpdatedAt() <=0)
            isValid = "Conversation updated at invalid";
        return isValid;
    }

    public static String validateSettings(Settings settings) {
        String isValid = "";
        if (settings == null) {
            isValid = "Settings is NULL";
        } else if (settings.getWEBRTCHost() == null || TextUtils.isEmpty(settings.getWEBRTCHost())) {
            isValid = "Settings WEBRTC_HOST is null";
        } else if (settings.getAdminApiHost() == null || TextUtils.isEmpty(settings.getAdminApiHost())) {
            isValid = "Settings ADMIN_API_HOST is null";
        } else if (settings.getClientApiHost() == null || TextUtils.isEmpty(settings.getClientApiHost())) {
            isValid = "Settings CLIENT_API_HOST is null";
        } else if (settings.getGroupService() == null || TextUtils.isEmpty(settings.getGroupService())) {
            isValid = "Settings GROUP_SERVICE is null";
        } else if (settings.getCallService() == null || TextUtils.isEmpty(settings.getCallService())) {
            isValid = "Settings CALL_SERVICE is null";
        } else if (settings.getChatHost() == null || TextUtils.isEmpty(settings.getChatHost())) {
            isValid = "Settings CHAT_HOST is null";
        } else if (settings.getChatWSPort() == null || TextUtils.isEmpty(settings.getChatWSPort())) {
            isValid = "Settings CHAT_WS_PORT is null";
        } else if (settings.getChatWSSPort() == null || TextUtils.isEmpty(settings.getChatWSSPort())) {
            isValid = "Settings CHAT_WSS_PORT is null";
        } else if (settings.getChatHTTPPort() == null || TextUtils.isEmpty(settings.getChatHTTPPort())) {
            isValid = "Settings CHAT_HTTP_BIND_PORT is null";
        } else if (settings.getChatHTTPSPort() == null || TextUtils.isEmpty(settings.getChatHTTPSPort())) {
            isValid = "Settings CHAT_HTTPS_BIND_PORT is null";
        } else if (settings.getWEBRTCWSPort() == null || TextUtils.isEmpty(settings.getWEBRTCWSPort())) {
            isValid = "Settings WEBRTC_WS_PORT is null";
        } else if (settings.getWEBRTCWSSPort() == null || TextUtils.isEmpty(settings.getWEBRTCWSSPort())) {
            isValid = "Settings WEBRTC_WSS_PORT is null";
        } else if (settings.getWEBRTCHTTPPort() == null || TextUtils.isEmpty(settings.getWEBRTCHTTPPort())) {
            isValid = "Settings WEBRTC_HTTP_BIND_PORT is null";
        } else if (settings.getWEBRTCHTTPSPort() == null || TextUtils.isEmpty(settings.getWEBRTCHTTPSPort())) {
            isValid = "Settings WEBRTC_HTTPS_BIND_PORT is null";
        }
        return isValid;
    }

    public static String validateTextMessage(TextMessage receivedTextMessage, boolean isDeleted) {
        String isValid = "";
        String validateBaseMessage = validateBaseMessage(receivedTextMessage);
        if ((receivedTextMessage.getText() == null || TextUtils.isEmpty(receivedTextMessage.getText())) && !isDeleted)
            isValid = "Received Message with Id " + receivedTextMessage.getId() + ": text is null or empty";
        else if (!validateBaseMessage.equalsIgnoreCase(""))
            isValid = validateBaseMessage;
        return isValid;
    }

    public static JSONObject getTestMetadata() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lattitue", "19.0760");
        jsonObject.put("longitude", "72.8777");
        return jsonObject;
    }

    public static JSONObject getTestCustomData() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customData", "Custom Data Test");
        jsonObject.put("sent time", System.currentTimeMillis());
        return jsonObject;
    }

    public static JSONObject getTestEditedCustomData() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customData", "Custom Data Test");
        jsonObject.put("sent time", System.currentTimeMillis());
        jsonObject.put("edited", "YES");
        return jsonObject;
    }

    public static JSONObject getEmptyJSONObject() {
        return new JSONObject();
    }

    public static String validateCustomMessage(CustomMessage receivedCustomMessage, boolean isDeleted) {
        String isValid = "";
        String validateBaseMessage = validateBaseMessage(receivedCustomMessage);
        if ((receivedCustomMessage.getCustomData() == null || CometChatUtils.isJSONObjectEmpty(receivedCustomMessage.getCustomData())) && !isDeleted)
            isValid = "Received Message with Id " + receivedCustomMessage.getId() + ": custom data is null or empty";
        else if (!validateBaseMessage.equalsIgnoreCase(""))
            isValid = validateBaseMessage;
        return isValid;
    }

    private static String validateBaseMessage(BaseMessage baseMessage) {
        String isValid = "";
        String validateSender = validateUser(baseMessage.getSender());
        String validateReceiver = validateAppEntity(baseMessage.getReceiver());
        String validateMessageType = vaidateMessageType(baseMessage);
        String validateCategory = validateCategory(baseMessage);
        if (baseMessage.getId() == 0)
            isValid = "Received Message Id = 0";
        else if (baseMessage.getConversationId() == null)
            isValid = "Received Message with Id " + baseMessage.getId() + ": Conversation ID is null";
        else if (baseMessage.getSender() == null)
            isValid = "Received Message with Id " + baseMessage.getId() + ": sender is null";
        else if (!validateSender.equalsIgnoreCase(""))
            isValid = "Received Message with Id " + baseMessage.getId() + " Sender : " + validateSender;
        else if (baseMessage.getReceiverUid() == null)
            isValid = "Received Message with Id " + baseMessage.getId() + ": Receiver ID is null";
        else if (baseMessage.getReceiver() == null)
            isValid = "Received Message with Id " + baseMessage.getId() + ": Receiver is null";
        else if (!validateReceiver.equalsIgnoreCase(""))
            isValid = "Received Message with Id " + baseMessage.getId() + " Receiver : " + validateReceiver;
        else if (!validateMessageType.equalsIgnoreCase(""))
            isValid = "Received Message with Id " + baseMessage.getId() + " Message Type : " + validateMessageType;
        else if (baseMessage.getReceiverType() == null || TextUtils.isEmpty(baseMessage.getReceiverType()) ||
                (!baseMessage.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER) && !baseMessage.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP)))
            isValid = "Received Message with Id " + baseMessage.getId() + " ReceiverType : " + baseMessage.getReceiverType();
        else if (!validateCategory.equalsIgnoreCase(""))
            isValid = "Received Message with Id " + baseMessage.getId() + " Message Category : " + validateCategory;
        else if (baseMessage.getSentAt() == 0)
            isValid = "Received Message with Id " + baseMessage.getId() + " Message sentAt = 0";
        return isValid;
    }

    private static String vaidateMessageType(BaseMessage baseMessage) {
        String isValid = "";
        if (baseMessage.getType() == null)
            isValid = "Received Message with Id " + baseMessage.getId() + " MessageType : " + baseMessage.getType();
        else if (baseMessage instanceof TextMessage) {
            if (!baseMessage.getType().equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_TEXT))
                isValid = "Received Message with Id " + baseMessage.getId() + " MessageType : " + baseMessage.getType();
        } else if (baseMessage instanceof CustomMessage) {
            if (baseMessage.getType() == null) {
                isValid = "Received Message with Id " + baseMessage.getId() + " MessageType : " + baseMessage.getType();
            }
        } else if (baseMessage instanceof MediaMessage) {
            if (!baseMessage.getType().equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_IMAGE) &&
                    !baseMessage.getType().equalsIgnoreCase(CometChatConstants.CALL_TYPE_AUDIO) &&
                    !baseMessage.getType().equalsIgnoreCase(CometChatConstants.CALL_TYPE_VIDEO) &&
                    !baseMessage.getType().equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_FILE))
                isValid = "Received Message with Id " + baseMessage.getId() + " MessageType : " + baseMessage.getType();
        }
        return isValid;
    }

    private static String validateCategory(BaseMessage baseMessage) {
        String isValid = "";
        if (baseMessage.getCategory() == null)
            isValid = "Received Message with Id " + baseMessage.getId() + " Category : " + baseMessage.getCategory();
        else if (baseMessage instanceof TextMessage || baseMessage instanceof MediaMessage) {
            if (!baseMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_MESSAGE))
                isValid = "Received Message with Id " + baseMessage.getId() + " Category : " + baseMessage.getCategory();
        } else if (baseMessage instanceof CustomMessage) {
            if (!baseMessage.getCategory().equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM)) {
                isValid = "Received Message with Id " + baseMessage.getId() + " Category : " + baseMessage.getCategory();
            }
        }
        return isValid;
    }

    public static String validateMessageList(List<BaseMessage> receivedMessages) {
        String isValid = "";
        for (BaseMessage baseMessage : receivedMessages) {
            boolean isDeleted = baseMessage.getDeletedAt() != 0;
            if (baseMessage instanceof TextMessage) {
                isValid = validateTextMessage((TextMessage) baseMessage, isDeleted);
            } else if (baseMessage instanceof CustomMessage) {
                isValid = validateCustomMessage((CustomMessage) baseMessage, isDeleted);
            } else if (baseMessage instanceof MediaMessage) {
                isValid = validateMediaMessage((MediaMessage) baseMessage, isDeleted);
            } else if (baseMessage instanceof Action) {
                isValid = validateActionMessage((Action) baseMessage);
            } else if (baseMessage instanceof Call) {
                isValid = validateCallMessage((Call) baseMessage);
            }
            if (!isValid.equalsIgnoreCase(""))
                return isValid;
        }
        return isValid;
    }

    public static String validateUserList(List<User> users) {
        String isValid = "";
        for (User user : users) {
            String validateUser = validateUser(user);
            if (!validateUser.equalsIgnoreCase(""))
                isValid = "User with UID : " + user.getUid() + " : " + validateUser;
        }
        return isValid;
    }

    public static void runTestForClass(Class testClass) {
        JUnitCore jUnitCore = new JUnitCore();
        Result result = jUnitCore.runClasses(testClass);
        List<Failure> failures = result.getFailures();
        for (Failure failure : failures) {
            AssertHelper.fail(failure.getDescription().toString());
        }
    }

    public static String validateMediaMessage(MediaMessage mediaMessage, boolean isDeleted) {
        String isValid = "";
        return isValid;
    }

    public static String validateActionMessage(Action action) {
        String isValid = "";
        String validateBaseMessage = validateBaseMessage(action);
        if (!validateBaseMessage.equalsIgnoreCase(""))
            isValid = validateBaseMessage;
        return isValid;
    }

    public static String validateCallMessage(Call call) {
        String isValid = "";
        String validateBaseMessage = validateBaseMessage(call);
        if (!validateBaseMessage.equalsIgnoreCase(""))
            isValid = validateBaseMessage;
        return isValid;
    }

    public static String validateGroupList(List<Group> receivedGroups) {
        String isValid = "";
        for (Group group : receivedGroups) {
            String validateGroup = validateGroup(group, true);
            if (!validateGroup.equalsIgnoreCase(""))
                isValid = "Group with GUID : " + group.getGuid() + " : " + validateGroup;
        }
        return isValid;
    }

    public static String validateAppEntity(AppEntity appEntity){
        String isValid = "";
        if(appEntity instanceof User)
            isValid = validateUser((User)appEntity);
        else if(appEntity instanceof Group)
            isValid = validateGroup((Group)appEntity, true);
        else if(appEntity instanceof BaseMessage){
            BaseMessage baseMessage = (BaseMessage)appEntity;
            boolean isDeleted = baseMessage.getDeletedAt() != 0;
            if (baseMessage instanceof TextMessage) {
                isValid = validateTextMessage((TextMessage) baseMessage, isDeleted);
            } else if (baseMessage instanceof CustomMessage) {
                isValid = validateCustomMessage((CustomMessage) baseMessage, isDeleted);
            } else if (baseMessage instanceof MediaMessage) {
                isValid = validateMediaMessage((MediaMessage) baseMessage, isDeleted);
            } else if (baseMessage instanceof Action) {
                isValid = validateActionMessage((Action) baseMessage);
            } else if (baseMessage instanceof Call) {
                isValid = validateCallMessage((Call) baseMessage);
            }
        }
        return isValid;
    }

    public static String validateGroupMembersList(List<GroupMember> groupMembers) {
        String isValid = "";
        for (GroupMember groupMember : groupMembers){
            String validateGroupMember = validateGroupMember(groupMember);
            if (!validateGroupMember.equalsIgnoreCase(""))
                isValid = "GroupMember with UID : " + groupMember.getUid() + " : " + validateGroupMember;
        }

        return isValid;
    }

    private static String validateGroupMember(GroupMember groupMember) {
        String isValid = "";
        if(groupMember == null)
            isValid = "GroupMember is null";
        else if(groupMember.getJoinedAt() == 0)
            isValid = "Group member joinedAt == 0";
        else if(groupMember.getScope() == null || TextUtils.isEmpty(groupMember.getScope()))
            isValid = "GroupMember scope is invalid";
        else if(!validateUser(groupMember).equalsIgnoreCase(""))
            isValid = validateUser(groupMember);
        return isValid;
    }
}
