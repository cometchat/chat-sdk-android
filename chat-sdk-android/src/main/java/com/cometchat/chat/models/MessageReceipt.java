package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.StringDef;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.utils.ContentEqualsHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adityagokula on 25/02/19.
 */

public class MessageReceipt implements Parcelable, Cloneable {

    public static final String RECEIPT_TYPE_DELIVERED = "delivered";
    public static final String RECEIPT_TYPE_READ = "read";
    public static final String RECEIPT_TYPE_DELIVERED_TO_ALL = "deliveredToAll";
    public static final String RECEIPT_TYPE_READ_BY_ALL = "readByAll";

    @StringDef({RECEIPT_TYPE_DELIVERED, RECEIPT_TYPE_READ})
    @Retention(RetentionPolicy.SOURCE)

    public @interface ReceiptType {

    }

    private long messageId;
    private User sender;
    @CometChatConstants.ReceiverTypes
    private String receiverType;
    private String receiverId;
    private long timestamp;
    @ReceiptType
    private String receiptType;
    private long deliveredAt;
    private long readAt;
    private String messageSender;

    public MessageReceipt() {}

    protected MessageReceipt(Parcel in) {
        messageId = in.readLong();
        sender = in.readParcelable(User.class.getClassLoader());
        receiverType = in.readString();
        receiverId = in.readString();
        timestamp = in.readLong();
        receiptType = in.readString();
        deliveredAt = in.readLong();
        readAt = in.readLong();
        messageSender = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(messageId);
        dest.writeParcelable(sender, flags);
        dest.writeString(receiverType);
        dest.writeString(receiverId);
        dest.writeLong(timestamp);
        dest.writeString(receiptType);
        dest.writeLong(deliveredAt);
        dest.writeLong(readAt);
        dest.writeString(messageSender);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageReceipt> CREATOR = new Creator<MessageReceipt>() {
        @Override
        public MessageReceipt createFromParcel(Parcel in) {
            return new MessageReceipt(in);
        }

        @Override
        public MessageReceipt[] newArray(int size) {
            return new MessageReceipt[size];
        }
    };

    @Override
    public MessageReceipt clone() {
        try {
            MessageReceipt cloned = (MessageReceipt) super.clone();
            if (this.sender != null) {
                cloned.sender = this.sender.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Get id of the message
     *
     * @return unique id of the message
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    /**
     * Get sender object
     *
     * @return An object of <code>User</code> class
     * @version <b>v2</b>
     * @see User
     * @since <b>v1</b>
     */
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Get type of receiver
     * This method is deprecated and will be removed in future versions.
     * @return type of the receiver
     * @version <b>v2</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v1</b>
     */
    @Deprecated
    public String getReceivertype() {
        return receiverType;
    }

    /**
     * Set type of receiver
     * This method is deprecated and will be removed in future versions use .
     * @param receiverType type of the receiver
     * @version <b>v2</b>
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v1</b>
     */
    @Deprecated
    public void setReceivertype(String receiverType) {
        this.receiverType = receiverType;
    }

    /**
     * Get type of receiver
     *
     * @return type of the receiver
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v1</b>
     */
    public String getReceiverType(){
        return receiverType;
    }

    /**
     * Set type of receiver
     *
     * @param receiverType type of the receiver
     * @see CometChatConstants.ReceiverTypes
     * @since <b>v1</b>
     */
    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    /**
     * Get id of the receiver
     *
     * @return id of the receiver(user/group)
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * Get message's sent at timestamp
     *
     * @return message sent timestamp
     * @version <b>v2</b>
     * @since <b>v1</b>
     */
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get receipt type of the message
     *
     * @return receipt type of the message
     * @version <b>v2</b>
     * @see ReceiptType
     * @since <b>v1</b>
     *
     */
    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }
    /**
     *  Get delivery timestamp of the message
     *
     * @return delivery timestamp of message
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public long getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    /**
     *  Get Timestamp of the when message was read at
     *
     * @return Timestamp of the time the message was read at
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     *   <b>Note</b>
     *    In case of group this field is set only when at message is read by all  the member of the group
     */
    public long getReadAt() {
        return readAt;
    }

    public void setReadAt(long readAt) {
        this.readAt = readAt;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public static List<MessageReceipt> receiptsFromJSON(JSONArray receiptsArray, String receiverId, String receivertype, long messageId) throws JSONException {
        List<MessageReceipt> receiptsList = new ArrayList<>();
        for (int i = 0; i < receiptsArray.length(); i++) {
            MessageReceipt messageReceipt = new MessageReceipt();
            messageReceipt.setReceiverType(receivertype);
            messageReceipt.setReceiverId(receiverId);
            messageReceipt.setMessageId(messageId);
            JSONObject receiptObject = receiptsArray.getJSONObject(i);
            if (receiptObject.has(CometChatConstants.ResponseKeys.KEY_RECIPIENT))
                messageReceipt.setSender(User.fromJson(receiptObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_RECIPIENT).toString()));
            else
                messageReceipt.setSender(CometChat.getLoggedInUser());
            if (receiptObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT)) {
                messageReceipt.setReceiptType(RECEIPT_TYPE_DELIVERED);
                messageReceipt.setTimestamp(receiptObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                messageReceipt.setDeliveredAt(receiptObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            }
            if (receiptObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT)) {
                messageReceipt.setReceiptType(RECEIPT_TYPE_READ);
                messageReceipt.setTimestamp(receiptObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
                messageReceipt.setReadAt(receiptObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            receiptsList.add(messageReceipt);
        }
        return receiptsList;

    }

    @Override
    public String toString() {
        return "MessageReceipt{" +
                "messageId=" + messageId +
                ", sender=" + sender +
                ", receivertype='" + receiverType + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", timestamp=" + timestamp +
                ", receiptType='" + receiptType + '\'' +
                ", deliveredAt=" + deliveredAt +
                ", readAt=" + readAt +
                ", messageSender='" + messageSender + '\'' +
                '}';
    }

    /**
     * Compares this object with another for content equality.
     * Unlike equals() which compares by identity (ID), this method
     * compares all fields for value equality.
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        // 1. Same reference check
        if (this == other) return true;

        // 2. Null check
        if (other == null) return false;

        // 3. Type check
        if (!(other instanceof MessageReceipt)) return false;

        // 4. Cast
        MessageReceipt that = (MessageReceipt) other;

        // 5. Compare all fields
        // Compare primitive fields using ==
        if (messageId != that.messageId) return false;
        if (timestamp != that.timestamp) return false;
        if (deliveredAt != that.deliveredAt) return false;
        if (readAt != that.readAt) return false;

        // Compare String fields using ContentEqualsHelper
        if (!ContentEqualsHelper.stringsEqual(receiverType, that.receiverType)) return false;
        if (!ContentEqualsHelper.stringsEqual(receiverId, that.receiverId)) return false;
        if (!ContentEqualsHelper.stringsEqual(receiptType, that.receiptType)) return false;
        if (!ContentEqualsHelper.stringsEqual(messageSender, that.messageSender)) return false;

        // Compare nested model object using contentEquals
        if (!ContentEqualsHelper.objectsContentEqual(sender, that.sender)) return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

}
