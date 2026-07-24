package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <code>Action</code> class provides information about the action performed
 */
public class Action extends BaseMessage {
    private static final String TAG = Action.class.getSimpleName();

    private AppEntity actionBy;
    private AppEntity actionFor;
    private AppEntity actionOn;
    private String message;
    private String rawData;
    private String action;
    private String oldScope;
    private String newScope;

    public Action(){
        setCategory(CometChatConstants.CATEGORY_ACTION);
    }

    protected Action(Parcel in) {
        super(in);
        message = in.readString();
        rawData = in.readString();
        action = in.readString();
        oldScope = in.readString();
        newScope = in.readString();
        // Read actionBy, actionFor, actionOn based on their types stored
        String actionByType = in.readString();
        if ("user".equals(actionByType)) {
            actionBy = in.readParcelable(User.class.getClassLoader());
        } else if ("group".equals(actionByType)) {
            actionBy = in.readParcelable(Group.class.getClassLoader());
        }
        String actionForType = in.readString();
        if ("user".equals(actionForType)) {
            actionFor = in.readParcelable(User.class.getClassLoader());
        } else if ("group".equals(actionForType)) {
            actionFor = in.readParcelable(Group.class.getClassLoader());
        }
        String actionOnType = in.readString();
        if ("user".equals(actionOnType)) {
            actionOn = in.readParcelable(User.class.getClassLoader());
        } else if ("group".equals(actionOnType)) {
            actionOn = in.readParcelable(Group.class.getClassLoader());
        } else if ("message".equals(actionOnType)) {
            actionOn = in.readParcelable(BaseMessage.class.getClassLoader());
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(message);
        dest.writeString(rawData);
        dest.writeString(action);
        dest.writeString(oldScope);
        dest.writeString(newScope);
        // Write actionBy
        if (actionBy instanceof User) {
            dest.writeString("user");
            dest.writeParcelable((User) actionBy, flags);
        } else if (actionBy instanceof Group) {
            dest.writeString("group");
            dest.writeParcelable((Group) actionBy, flags);
        } else {
            dest.writeString(null);
        }
        // Write actionFor
        if (actionFor instanceof User) {
            dest.writeString("user");
            dest.writeParcelable((User) actionFor, flags);
        } else if (actionFor instanceof Group) {
            dest.writeString("group");
            dest.writeParcelable((Group) actionFor, flags);
        } else {
            dest.writeString(null);
        }
        // Write actionOn
        if (actionOn instanceof User) {
            dest.writeString("user");
            dest.writeParcelable((User) actionOn, flags);
        } else if (actionOn instanceof Group) {
            dest.writeString("group");
            dest.writeParcelable((Group) actionOn, flags);
        } else if (actionOn instanceof BaseMessage) {
            dest.writeString("message");
            dest.writeParcelable((BaseMessage) actionOn, flags);
        } else {
            dest.writeString(null);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Action> CREATOR = new Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel in) {
            return new Action(in);
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };

    @Override
    public Action clone() {
        try {
            Action cloned = (Action) super.clone();
            if (this.actionBy != null) {
                cloned.actionBy = this.actionBy.clone();
            }
            if (this.actionFor != null) {
                cloned.actionFor = this.actionFor.clone();
            }
            if (this.actionOn != null) {
                cloned.actionOn = this.actionOn.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Get Entity(User/Group) details which performed the action
     *
     * @return An object of the <code>AppEntity</code> class
     * @version <b>v2</b>
     * @see AppEntity
     * @since <b>v1</b>
     */
    @Deprecated
    public AppEntity getActioBy() {
        return actionBy;
    }

    @Deprecated
    public void setActioBy(AppEntity actionBy) {
        this.actionBy = actionBy;
    }

    /**
     * Get Entity(User/Group) details which performed the action
     *
     * @return An object of the <code>AppEntity</code> class
     * @version <b>v2</b>
     * @see AppEntity
     * @since <b>v1</b>
     */
    public AppEntity getActionBy() {
        return actionBy;
    }

    public void setActionBy(AppEntity actionBy) {
        this.actionBy = actionBy;
    }

    /**
     * Get details of the Entity(User/Group) that the action was performed for
     *
     * @return An object of the <code>AppEntity</code> class
     * @version <b>v2</b>
     * @see AppEntity
     * @since <b>v1</b>
     */
    public AppEntity getActionFor() {
        return actionFor;
    }

    public void setActionFor(AppEntity actionFor) {
        this.actionFor = actionFor;
    }

    /**
     * Get details of the Entity(User/Group) that the action was performed for
     *
     * @return An object of the <code>AppEntity</code> class
     * @version <b>v2</b>
     * @see AppEntity
     * @since <b>v1</b>
     */
    public AppEntity getActionOn() {
        return actionOn;
    }

    public void setActionOn(AppEntity actionOn) {
        this.actionOn = actionOn;
    }

    /**
     * Get Default action message provided by CometChat SDK
     *
     * @return default action message provided by CometChat SDK
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get raw JSON data in string form
     *
     * @return raw string format of JSONObject
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    /**
     * Get action which is performed
     *
     * @return action which is performed
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Get old scope of the user
     *
     * @return string value of scope
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getOldScope() {
        return oldScope;
    }

    public void setOldScope(String oldScope) {
        this.oldScope = oldScope;
    }

    /**
     * Get new scope of the user
     *
     * @return string value of scope
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getNewScope() {
        return newScope;
    }

    public void setNewScope(String newScope) {
        this.newScope = newScope;
    }

    public static Action fromJson(JSONObject jsonObject) {
        Action action = new Action();
        try {
            action.setRawMessage(jsonObject);
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                action.setId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
                action.setConversationId(jsonObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
                action.setParentMessageId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
                action.setReplyCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT))
                action.setUnreadRepliesCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                action.setReceiverUid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
                action.setMuid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE)) {
                action.setReceiverType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
                action.setCategory(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE))
                action.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE));
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ACTION)) {
                    action.setAction(dataObject.getString(CometChatConstants.ResponseKeys.KEY_ACTION));
                }
                action.setRawData(dataObject.toString());
                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                    JSONObject entitiesObject = dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES);
                    if (entitiesObject.has(CometChatConstants.ActionKeys.KEY_BY)) {
                        action.setActionBy(getAppEntityForAction(entitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_BY)));
                        action.setSender((User) getAppEntityForAction(entitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_BY)));
                    }
                    if (entitiesObject.has(CometChatConstants.ActionKeys.KEY_FOR)) {
                        action.setActionFor(getAppEntityForAction(entitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_FOR)));
                    }
                    if (entitiesObject.has(CometChatConstants.ActionKeys.KEY_ON)) {
                        action.setActionOn(getAppEntityForAction(entitiesObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ON)));
                    }
                }
                if (action.getType().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_TYPE_GROUP_MEMBER)) {
                    action.setReceiver(action.getActionFor());
                } else if (action.getType().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_TYPE_MESSAGE)) {
                    action.setReceiver(action.getActionFor());
                }
                if (dataObject.has(CometChatConstants.ActionKeys.KEY_EXTRAS)) {
                    JSONObject extrasObject = dataObject.getJSONObject(CometChatConstants.ActionKeys.KEY_EXTRAS);
                    if (extrasObject.has(CometChatConstants.ActionKeys.KEY_SCOPE)) {
                        JSONObject scopeObject = extrasObject.getJSONObject(CometChatConstants.ActionKeys.KEY_SCOPE);
                        if (scopeObject.has(CometChatConstants.ActionKeys.KEY_OLD))
                            action.setOldScope(scopeObject.getString(CometChatConstants.ActionKeys.KEY_OLD));
                        if (scopeObject.has(CometChatConstants.ActionKeys.KEY_NEW))
                            action.setNewScope(scopeObject.getString(CometChatConstants.ActionKeys.KEY_NEW));
                    }
                }
                // Mentions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_MENTIONS)){
                    List<User> mentionedUsersList = new ArrayList<>();
                    boolean isMentionedMe = false;
                    User loggedInUserObj = CometChat.getLoggedInUser();
                    JSONObject userObj = dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_MENTIONS);
                    Iterator<String> keys = userObj.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        String value = userObj.getString(key);
                        User user = User.fromJson(value);
                        mentionedUsersList.add(user);
                        if (key.toString().equals(loggedInUserObj.getUid())){
                            isMentionedMe = true;
                        }
                    }
                    action.setMentionedUsers(mentionedUsersList);
                    action.setHasMentionedMe(isMentionedMe);
                }
            }
            action.setMessage(getActionMessage(action));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT)) {
                action.setSentAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT)) {
                action.setUpdatedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
                JSONObject receiptsObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                    action.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                    action.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT)) {
                action.setDeliveredAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            }

            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT)) {
                action.setReadAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT)) {
                action.setEditedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY)) {
                action.setEditedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT)) {
                action.setDeletedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY)) {
                action.setDeletedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));
            }

        } catch (JSONException je) {
            Logger.error(TAG, "Error parsing action message : " + je.getMessage());
        }
        return action;
    }

    private static AppEntity getAppEntityForAction(JSONObject jsonObject) throws JSONException {
        AppEntity appEntity = null;
        if (jsonObject.has(CometChatConstants.ActionKeys.KEY_ENTITY_TYPE)) {
            String entityType = jsonObject.getString(CometChatConstants.ActionKeys.KEY_ENTITY_TYPE);
            if (jsonObject.has(CometChatConstants.ActionKeys.KEY_ENTITY)) {
                if (entityType.equalsIgnoreCase(CometChatConstants.ActionKeys.KEY_ENTITY_USER)) {
                    appEntity = User.fromJson(jsonObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY).toString());
                } else if (entityType.equalsIgnoreCase(CometChatConstants.ActionKeys.KEY_ENTITY_GROUP)) {
                    appEntity = Group.fromJson(jsonObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY).toString());
                } else if (entityType.equalsIgnoreCase(CometChatConstants.ActionKeys.KEY_ENTITY_MESSAGE)) {
                    JSONObject entityObject = jsonObject.getJSONObject(CometChatConstants.ActionKeys.KEY_ENTITY);
                    String category = entityObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY);
                    if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM)) {
                        appEntity = CustomMessage.fromJson(entityObject);
                    } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_INTERACTIVE)) {
                        appEntity = InteractiveMessage.fromJson(entityObject);
                    } else{
                        String type = entityObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE);
                        if (type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                            appEntity = TextMessage.fromJson(entityObject);
                        } else if (type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_CUSTOM)) {
                            appEntity = CustomMessage.fromJson(entityObject);
                        } else if (type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_IMAGE) || type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_VIDEO)
                                || type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_FILE) || type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_AUDIO)) {
                            appEntity = MediaMessage.fromJson(entityObject);
                        }
                    }
                }
            } else {
                throw new JSONException("Key \"entity\" not found in the JSON");
            }

        } else {
            throw new JSONException("Key \"entityType\" not found in the JSON");
        }
        return appEntity;
    }

    private static String getActionMessage(Action action) {
        String message = "";
        if (action.getType().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_TYPE_USER)) {
            switch (action.getAction()) {
                case CometChatConstants.ActionKeys.ACTION_CREATED:
                    break;
                case CometChatConstants.ActionKeys.ACTION_UPDATED:
                    break;
                case CometChatConstants.ActionKeys.ACTION_DELETED:
                    break;
            }
        } else if (action.getType().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_TYPE_GROUP)) {
            switch (action.getAction()) {
                case CometChatConstants.ActionKeys.ACTION_CREATED:
                    break;
                case CometChatConstants.ActionKeys.ACTION_UPDATED:
                    break;
                case CometChatConstants.ActionKeys.ACTION_DELETED:
                    break;
            }
        } else if (action.getType().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_TYPE_GROUP_MEMBER)) {
            switch (action.getAction()) {
                case CometChatConstants.ActionKeys.ACTION_JOINED: {
                    User actioBy = (User) action.getActionBy();
                    message = String.format(CometChatConstants.ActionMessages.ACTION_GROUP_JOINED_MESSAGE, actioBy.getName());
                    break;
                }
                case CometChatConstants.ActionKeys.ACTION_LEFT: {
                    User actioBy = (User) action.getActionBy();
                    message = String.format(CometChatConstants.ActionMessages.ACTION_GROUP_LEFT_MESSAGE, actioBy.getName());
                    break;
                }
                case CometChatConstants.ActionKeys.ACTION_KICKED: {
                    User actionBy = (User) action.getActionBy();
                    User actionOn = (User) action.getActionOn();
                    message = String.format(CometChatConstants.ActionMessages.ACTION_MEMBER_KICKED_MESSAGE, actionBy.getName(), actionOn.getName());
                    break;
                }
                case CometChatConstants.ActionKeys.ACTION_BANNED: {
                    User actionBy = (User) action.getActionBy();
                    User actionOn = (User) action.getActionOn();
                    message = String.format(CometChatConstants.ActionMessages.ACTION_MEMBER_BANNED_MESSAGE, actionBy.getName(), actionOn.getName());
                    break;
                }
                case CometChatConstants.ActionKeys.ACTION_UNBANNED: {
                    User actionBy = (User) action.getActionBy();
                    User actionOn = (User) action.getActionOn();
                    message = String.format(CometChatConstants.ActionMessages.ACTION_MEMBER_UNBANNED_MESSAGE, actionBy.getName(), actionOn.getName());
                    break;
                }
                case CometChatConstants.ActionKeys.ACTION_MEMBER_ADDED: {
                    User actionBy = (User) action.getActionBy();
                    User actionOn = (User) action.getActionOn();
                    message = String.format(CometChatConstants.ActionMessages.ACTION_MEMBER_ADDED_TO_GROUP, actionBy.getName(), actionOn.getName());
                    break;
                }
                case CometChatConstants.ActionKeys.ACTION_SCOPE_CHANGED: {
                    User actionBy = (User) action.getActionBy();
                    User actionOn = (User) action.getActionOn();
                    message = String.format(CometChatConstants.ActionMessages.ACTION_MEMBER_SCOPE_CHANGED, actionBy.getName(), actionOn.getName(), action.getNewScope());
                    break;
                }
            }
        } else if (action.getType().equalsIgnoreCase(CometChatConstants.ActionKeys.ACTION_TYPE_MESSAGE)) {
            switch (action.getAction()) {
                case CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED:
                    message = CometChatConstants.ActionMessages.ACTION_MESSAGE_EDITED_MESSAGE;
                    break;
                case CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED:
                    message = CometChatConstants.ActionMessages.ACTION_MESSAGE_DELETED_MESSAGE;
                    break;
            }
        }
        return message;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
    }

    /**
     * Compares this object with another for content equality.
     * Unlike equals() which compares by identity (ID), this method
     * compares all fields for value equality.
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    @Override
    public boolean contentEquals(Object other) {
        // 1. Same reference check
        if (this == other) return true;

        // 2. Null check
        if (other == null) return false;

        // 3. Type check
        if (!(other instanceof Action)) return false;

        // 4. Call parent's contentEquals (BaseMessage)
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        Action that = (Action) other;

        // 6. Compare all action-specific fields

        // String fields - use ContentEqualsHelper.stringsEqual()
        if (!ContentEqualsHelper.stringsEqual(message, that.message)) return false;
        if (!ContentEqualsHelper.stringsEqual(rawData, that.rawData)) return false;
        if (!ContentEqualsHelper.stringsEqual(action, that.action)) return false;
        if (!ContentEqualsHelper.stringsEqual(oldScope, that.oldScope)) return false;
        if (!ContentEqualsHelper.stringsEqual(newScope, that.newScope)) return false;

        // Object fields (AppEntity) - use ContentEqualsHelper.objectsContentEqual()
        if (!ContentEqualsHelper.objectsContentEqual(actionBy, that.actionBy)) return false;
        if (!ContentEqualsHelper.objectsContentEqual(actionFor, that.actionFor)) return false;
        if (!ContentEqualsHelper.objectsContentEqual(actionOn, that.actionOn)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Action{" +
                "actioBy=" + actionBy +
                ", actionFor=" + actionFor +
                ", actionOn=" + actionOn +
                ", message='" + message + '\'' +
                ", rawData='" + rawData + '\'' +
                ", action='" + action + '\'' +
                ", oldScope='" + oldScope + '\'' +
                ", newScope='" + newScope + '\'' +
                ", id=" + id +
                ", muid='" + muid + '\'' +
                ", sender=" + sender +
                ", receiverUid='" + receiverUid + '\'' +
                ", type='" + type + '\'' +
                ", receiverType='" + receiverType + '\'' +
                ", category='" + category + '\'' +
                ", sentAt=" + sentAt +
                ", deliveredAt=" + deliveredAt +
                ", readAt=" + readAt +
                ", metadata=" + metadata +
                ", readByMeAt=" + readByMeAt +
                ", deliveredToMeAt=" + deliveredToMeAt +
                ", deletedAt=" + deletedAt +
                ", editedAt=" + editedAt +
                ", deletedBy='" + deletedBy + '\'' +
                ", editedBy='" + editedBy + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
