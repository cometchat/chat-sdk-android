package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.CurrentUserRepo;
import com.cometchat.chat.core.Settings;
import com.cometchat.chat.core.SettingsRepo;
import com.cometchat.chat.enums.ModerationStatus;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.core.CometChatUtils;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by adityagokula on 11/09/18.
 */

public class MediaMessage extends BaseMessage {
    private String TAG = "MediaMessage";
    private File file;
    private List<File> files;
    private String caption;
    private Attachment attachment;
    private List<Attachment> attachments;
    private List<String> tags;
    private ModerationStatus moderationStatus;

    public MediaMessage(String receiverUid, File file, @CometChatConstants.MessageTypes String messageType, @CometChatConstants.ReceiverTypes String receiverType) {
        super(receiverUid, messageType, receiverType);
        setCategory(CometChatConstants.CATEGORY_MESSAGE);
        if(file != null) {
            this.file = file;
            if (this.files == null) this.files = new ArrayList<>();
            this.files.add(this.file);
        }
    }

    public MediaMessage(String receiverUid, List<File> files, @CometChatConstants.MessageTypes String messageType, @CometChatConstants.ReceiverTypes String receiverType) {
        super(receiverUid, messageType, receiverType);
        setCategory(CometChatConstants.CATEGORY_MESSAGE);
        this.files = files;
    }

    public MediaMessage(String receiverUid, @CometChatConstants.MessageTypes String messageType, @CometChatConstants.ReceiverTypes String receiverType) {
        super(receiverUid, messageType, receiverType);
        setCategory(CometChatConstants.CATEGORY_MESSAGE);
    }

    public MediaMessage() {
        setCategory(CometChatConstants.CATEGORY_MESSAGE);
    }

    protected MediaMessage(Parcel in) {
        super(in);
        String filePath = in.readString();
        if (filePath != null) {
            file = new File(filePath);
        }
        int filesCount = in.readInt();
        if (filesCount > 0) {
            files = new ArrayList<>();
            for (int i = 0; i < filesCount; i++) {
                String path = in.readString();
                if (path != null) {
                    files.add(new File(path));
                }
            }
        }
        caption = in.readString();
        attachment = in.readParcelable(Attachment.class.getClassLoader());
        attachments = in.createTypedArrayList(Attachment.CREATOR);
        tags = in.createStringArrayList();
        String moderationStatusStr = in.readString();
        if (moderationStatusStr != null) {
            moderationStatus = ModerationStatus.get(moderationStatusStr);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(file != null ? file.getAbsolutePath() : null);
        if (files != null) {
            dest.writeInt(files.size());
            for (File f : files) {
                dest.writeString(f != null ? f.getAbsolutePath() : null);
            }
        } else {
            dest.writeInt(0);
        }
        dest.writeString(caption);
        dest.writeParcelable(attachment, flags);
        dest.writeTypedList(attachments);
        dest.writeStringList(tags);
        dest.writeString(moderationStatus != null ? moderationStatus.getValue() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaMessage> CREATOR = new Creator<MediaMessage>() {
        @Override
        public MediaMessage createFromParcel(Parcel in) {
            return new MediaMessage(in);
        }

        @Override
        public MediaMessage[] newArray(int size) {
            return new MediaMessage[size];
        }
    };

    @Override
    public MediaMessage clone() {
        MediaMessage cloned = (MediaMessage) super.clone();
        if (cloned != null) {
            if (this.attachment != null) {
                cloned.attachment = this.attachment.clone();
            }
            if (this.attachments != null) {
                cloned.attachments = new ArrayList<>();
                for (Attachment a : this.attachments) {
                    cloned.attachments.add(a.clone());
                }
            }
            if (this.tags != null) {
                cloned.tags = new ArrayList<>(this.tags);
            }
            if (this.files != null) {
                cloned.files = new ArrayList<>(this.files);
            }
        }
        return cloned;
    }


    /**
     * Get file object of media file shared
     *
     * @return An file object of the media file shared
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        if(file!=null) {
            this.file = file;
            if (this.files == null)
                this.files = new ArrayList<>();
            this.files.add(file);
        }
    }

    /**
     * Get caption set by developer
     *
     * @return caption set by developer for media file
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Get object of the <code>Attachment</code> class with file details
     *
     * @return An object of the <code>Attachment</code> class
     * @version <b>v2</b>
     * @see Attachment
     * @since <b>v1</b>
     */
    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        if(attachment!=null) {
            this.attachment = attachment;
            if (this.attachments == null)
                this.attachments = new ArrayList<>();
            this.attachments.add(attachment);
        }
    }

    /**
     * Returns the list of tags that the message has been tagged with.
     *
     * @return List<String> that holds the tags with which the message was tagged with.
     * @version <b>v3</b>
     * @since <b>v3</b>
     */
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
    /**
     * Returns the list of attachments that the message holds.
     *
     * @return List<Attachments> that holds the attachments in the message
     * @version <b>v3</b>
     * @since <b>v3</b>
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    /**
     * Gets the moderation status of the message.
     * @return <b>ModerationStatus</b> enum that holds the moderation status of the message.
     * @version <b>v3</b>
     * @since <b>v3</b>
     */
    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    /**
     * Sets the moderation status of the message.
     * @version <b>v3</b>
     * @since <b>v3</b>
     * @param status
     */
    public void setModerationStatus(ModerationStatus status){
        this.moderationStatus = status;
    }

    /**
     * Get the total size of all files in this media message
     *
     * @return The total file size in bytes, returns 0 if no files are present or if files don't exist
     * @since <b>v4</b>
     */
    public long getTotalFileSize() {
        long totalSize = 0;
        if (files != null) {
            for (File file : files) {
                if (file != null && file.exists()) {
                    totalSize += file.length();
                }
            }
        }
        return totalSize;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE, this.getReceiverType());
        if (this.getType() != null)
            map.put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, this.getType());
        if (this.getReceiverUid() != null)
            map.put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, this.getReceiverUid());
        map.put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, CometChatConstants.CATEGORY_MESSAGE);
        if (this.getType() != null)
            map.put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE, this.getType());
        if (this.getMuid() != null)
            map.put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID, this.getMuid());
        if (this.getParentMessageId() > 0)
            map.put(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID, String.valueOf(this.getParentMessageId()));
        JSONObject dataObject = new JSONObject();
        try {
            if (this.getMetadata() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA, this.getMetadata());
            }
            if (this.getCaption() != null) {
                dataObject.put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, this.getCaption());
            }
            if (this.attachments != null || this.attachment != null) {
                JSONArray attachmentsArray = new JSONArray();
                if (this.attachments != null && attachments.size() > 0) {
                    for (Attachment attachment : attachments) {
                        attachmentsArray.put(attachment.toJson());
                    }
                }
                if (this.attachment != null) {
                    attachmentsArray.put(this.getAttachment().toJson());
                }
                if (attachmentsArray.length() > 0) {
                    dataObject.put(CometChatConstants.MessageKeys.KEY_MESSAGE_ATTACHMENTS, attachmentsArray);
                }
            }

        } catch (JSONException je) {
            Logger.error("Error creating JSON : " + je.getMessage());
        }
        if (this.files != null || dataObject.length() > 0)
            map.put(CometChatConstants.ResponseKeys.KEY_DATA, dataObject.toString());
        if (this.tags != null) {
            JSONArray tagsArray = CometChatUtils.getJSONArrayFromList(this.tags);
            map.put(CometChatConstants.MessageKeys.KEY_TAGS, tagsArray.toString());
        }
        if (this.getQuotedMessageId() != 0) {
            map.put(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE_ID, String.valueOf(this.getQuotedMessageId()));
        }
        return map;

    }

    public static MediaMessage fromJson(JSONObject jsonObject) {
        MediaMessage mediaMessage = new MediaMessage();
        try {
            mediaMessage.setRawMessage(jsonObject);
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                mediaMessage.setId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
                mediaMessage.setConversationId(jsonObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
                mediaMessage.setParentMessageId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
                mediaMessage.setReplyCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT))
                mediaMessage.setUnreadRepliesCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
                mediaMessage.setMuid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                mediaMessage.setReceiverUid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE))
                mediaMessage.setReceiverType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
                mediaMessage.setCategory(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE))
                mediaMessage.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT))
                mediaMessage.setUpdatedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                mediaMessage.setDeliveredAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                mediaMessage.setReadAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT))
                mediaMessage.setEditedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY))
                mediaMessage.setEditedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT))
                mediaMessage.setDeletedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY))
                mediaMessage.setDeletedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT))
                mediaMessage.setSentAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT)) {
                    mediaMessage.setCaption(dataObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_ATTACHMENTS)) {
                    try{
                        CurrentUser currentUser = CurrentUserRepo.getCurrentUser();
                        Settings settings = SettingsRepo.getSettings();
                        String fat = currentUser.getFat();
                        String secureMediaHost = settings.getSecureMediaHost();
                        Attachment.setFat(fat);
                        Attachment.setSecureMediaHost(secureMediaHost);

                        //Modify metadata all urls
                        if(dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA)){
                            if (dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA).has(CometChatConstants.MessageKeys.KEY_INJECTED_METADATA)){
                                if (fat != null && secureMediaHost != null){
                                    JSONObject metadataObject = dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA);
                                    JSONObject modifiedJsonObject = modifyUrls(metadataObject, secureMediaHost, fat);
                                    mediaMessage.setMetadata(modifiedJsonObject);
                                }
                            }
                        }

                        //Modify all attachment url
                        JSONArray attachmentArray = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_MESSAGE_ATTACHMENTS);
                        Attachment attachment = Attachment.fromJson(attachmentArray.getJSONObject(0));
                        mediaMessage.setAttachment(attachment);
                        List<Attachment> attachments = new ArrayList<>();
                        for(int i=0; i< attachmentArray.length(); i++){
                            Attachment a = Attachment.fromJson(attachmentArray.getJSONObject(i));
                            attachments.add(a);
                        }
                        mediaMessage.setAttachments(attachments);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                    JSONObject entitiesObject = new JSONObject(dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES).toString());
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_SENDER)) {
                        JSONObject senderObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SENDER);
                        User user = User.fromJson(senderObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                        mediaMessage.setSender(user);
                    }
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID)) {
                        JSONObject receiverObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID);
                        String entityType = receiverObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE);
                        if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            User user = User.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            mediaMessage.setReceiver(user);
                        } else if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                            Group group = Group.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            mediaMessage.setReceiver(group);
                        }
                    }
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA)) {
                    mediaMessage.setMetadata(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA));
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
                    mediaMessage.setMentionedUsers(mentionedUsersList);
                    mediaMessage.setHasMentionedMe(isMentionedMe);
                }
                //Reactions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_REACTIONS)){
                    JSONArray reactionCountObj = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_REACTIONS);
                    List<ReactionCount> reactionCountList = ReactionCount.listFromJSONArray(reactionCountObj);
                    mediaMessage.setReactions(reactionCountList);
                }
                // Moderation Status
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_MODERATION)) {
                    JSONObject moderationObject = dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_MODERATION);
                    if (moderationObject.has(CometChatConstants.MessageKeys.KEY_MODERATION_STATUS)) {
                        String statusStr = moderationObject.getString(CometChatConstants.MessageKeys.KEY_MODERATION_STATUS);
                        ModerationStatus status = ModerationStatus.get(statusStr);
                        mediaMessage.moderationStatus = (status != null) ? status : ModerationStatus.UNMODERATED;
                    } else {
                        mediaMessage.moderationStatus = ModerationStatus.UNMODERATED;
                    }
                } else {
                    mediaMessage.moderationStatus = ModerationStatus.UNMODERATED;
                }
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
                JSONObject receiptsObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                    mediaMessage.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                    mediaMessage.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TAGS)) {
                mediaMessage.setTags(CometChatUtils.getListFromJSONArray(jsonObject.getJSONArray(CometChatConstants.MessageKeys.KEY_TAGS)));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE)) {
                BaseMessage quotedMessage = BaseMessage.processMessage(jsonObject.getJSONObject(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE));
                mediaMessage.setQuotedMessage(quotedMessage);
                mediaMessage.setQuotedMessageId(quotedMessage != null ? quotedMessage.getId() : 0);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return mediaMessage;
    }

    @Override
    public String toString() {
        return "MediaMessage{" +
                "id=" + id +
                ", muid='" + muid + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
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
                ", conversationId='" + conversationId + '\'' +
                ", parentMessageId=" + parentMessageId +
                ", replyCount=" + replyCount +
                ", rawMessage=" + rawMessage +
                ", file=" + file +
                ", caption='" + caption + '\'' +
                ", attachment=" + attachment +
                ", tags=" + tags +
                "} " + super.toString();
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
        if (!(other instanceof MediaMessage)) return false;

        // 4. Call parent's contentEquals (BaseMessage)
        if (!super.contentEquals(other)) return false;

        // 5. Cast
        MediaMessage that = (MediaMessage) other;

        // 6. Compare all MediaMessage-specific fields

        // File field - use Objects.equals() since File doesn't have contentEquals
        if (!Objects.equals(file, that.file)) return false;

        // List<File> field - use ContentEqualsHelper.listsEqual()
        if (!ContentEqualsHelper.listsEqual(files, that.files)) return false;

        // String field - use ContentEqualsHelper.stringsEqual()
        if (!ContentEqualsHelper.stringsEqual(caption, that.caption)) return false;

        // Attachment field - use ContentEqualsHelper.objectsContentEqual() which calls Attachment.contentEquals()
        if (!ContentEqualsHelper.objectsContentEqual(attachment, that.attachment)) return false;

        // List<Attachment> field - use ContentEqualsHelper.listsEqual() which calls Attachment.contentEquals()
        if (!ContentEqualsHelper.listsEqual(attachments, that.attachments)) return false;

        // List<String> field - use ContentEqualsHelper.listsEqual()
        if (!ContentEqualsHelper.listsEqual(tags, that.tags)) return false;

        // Enum field - use == operator
        if (moderationStatus != that.moderationStatus) return false;

        return true;
    }

    static JSONObject modifyUrls(JSONObject jsonObject, String secureMediaHost, String fat) {
        try{
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject) {
                    modifyUrls((JSONObject) value, secureMediaHost, fat);
                } else if (value instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) value;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        modifyUrls(jsonArray.getJSONObject(i), secureMediaHost, fat);
                    }
                } else if (value instanceof String && key.contains("url")) {
                    String url = (String) value;
                    if (url.contains(secureMediaHost)){
                        jsonObject.put(key, url + "?fat=" + fat);
                    } else if ((key.equals(CometChatConstants.ResponseKeys.URL_SMALL) || key.equals(CometChatConstants.ResponseKeys.URL_MEDIUM) || key.equals(CometChatConstants.ResponseKeys.URL_LARGE))
                               && fat != null && !fat.isEmpty()) {
                        jsonObject.put(key, url + "?fat=" + fat);
                    }
                }
            }
        }catch (Exception e){
            Logger.error("MediaMessage", "Error modifyUrls: " + e);
        }
        return jsonObject;
    }

}
