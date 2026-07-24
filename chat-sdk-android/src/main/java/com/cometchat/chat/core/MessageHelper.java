package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.Action;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.CustomMessage;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.InteractiveMessage;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.TextMessage;
import com.cometchat.chat.models.User;
import com.cometchat.chat.models.UserPresence;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by adityagokula on 30/10/18.
 */

public class MessageHelper {

    private static final String TAG = MessageHelper.class.getSimpleName();

    static BaseMessage processMessage(JSONObject messageObject) throws JSONException {

        if (messageObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY)) {
            String category = messageObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY);
            if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_MESSAGE)) {
                if (messageObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE)) {
                    String type = messageObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE);
                    if (type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                        return TextMessage.fromJson(messageObject);
                    } else if (type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_CUSTOM)) {
                        return CustomMessage.fromJson(messageObject);
                    }
                    else {
                        return MediaMessage.fromJson(messageObject);
                    }
                }
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_ACTION)) {
                return Action.fromJson(messageObject);

            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CALL)) {
                return Call.fromJson(messageObject.toString());
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM)) {
                return CustomMessage.fromJson(messageObject);
            }
            else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_INTERACTIVE)) {
                return InteractiveMessage.fromJson(messageObject);
            }
        } else {
            Logger.error(TAG, "Malformed Json. Message Category missing");
        }
        return null;
    }

    static List<User> getUsersFromPresence(HashMap<String, User> usersMap, List<UserPresence> presences) {
        List<User> users = new ArrayList<>();
        for (UserPresence userPresence : presences) {
            String userIdentity = "\'" + userPresence.getJid().substring(0, userPresence.getJid().lastIndexOf("@")) + "\'";
            User user = usersMap.get(userIdentity);
            user.setStatus(userPresence.getStatus());
            user.setLastActiveAt(userPresence.getLastActiveAt());
            users.add(user);
            Logger.error("JID : " + userIdentity + " status : " + user.getStatus() + " last active : " + user.getLastActiveAt());
        }
        return users;
    }

    static List<GroupMember> getGroupMembersFromPresence(HashMap<String, GroupMember> membersMap, List<UserPresence> presences) {
        List<GroupMember> members = new ArrayList<>();
        for (UserPresence userPresence : presences) {
            String userIdentity = "\'" + userPresence.getJid().substring(0, userPresence.getJid().lastIndexOf("@")) + "\'";
            GroupMember groupMember = membersMap.get(userIdentity);
            groupMember.setStatus(userPresence.getStatus());
            groupMember.setLastActiveAt(userPresence.getLastActiveAt());
            members.add(groupMember);
            Logger.error("JID : " + userIdentity + " status : " + groupMember.getStatus() + " last active : " + groupMember.getLastActiveAt());
        }
        return members;
    }
}
