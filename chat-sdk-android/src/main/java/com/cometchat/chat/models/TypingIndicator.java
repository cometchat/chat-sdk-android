package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by adityagokula on 05/02/19.
 */

public class TypingIndicator implements Parcelable, Cloneable {

    public static final String TYPING_START = "started";
    public static final String TYPING_END = "ended";

    public static TypingIndicator fromJSON(JSONObject mainObject) {
        if(mainObject!=null){

        }
        return null;
    }

    @StringDef({TYPING_START, TYPING_END})
    @Retention(RetentionPolicy.SOURCE)

    public @interface TypingStatus {

    }

    private User sender;
    private String receiverId;
    @CometChatConstants.ReceiverTypes
    private String receiverType;
    private JSONObject metadata;
    private long lastTimestamp;
    @TypingStatus
    private String typingStatus;


    public TypingIndicator(@NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType) {
        this.receiverId = receiverId;
        this.receiverType = receiverType;
    }

    public TypingIndicator(@NonNull String receiverId, @CometChatConstants.ReceiverTypes String receiverType, @NonNull JSONObject metadata) {
        this.receiverId = receiverId;
        this.receiverType = receiverType;
        this.metadata = metadata;
    }

    protected TypingIndicator(Parcel in) {
        sender = in.readParcelable(User.class.getClassLoader());
        receiverId = in.readString();
        receiverType = in.readString();
        String metadataStr = in.readString();
        if (metadataStr != null) {
            try {
                metadata = new JSONObject(metadataStr);
            } catch (JSONException e) {
                metadata = null;
            }
        }
        lastTimestamp = in.readLong();
        typingStatus = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(sender, flags);
        dest.writeString(receiverId);
        dest.writeString(receiverType);
        dest.writeString(metadata != null ? metadata.toString() : null);
        dest.writeLong(lastTimestamp);
        dest.writeString(typingStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TypingIndicator> CREATOR = new Creator<TypingIndicator>() {
        @Override
        public TypingIndicator createFromParcel(Parcel in) {
            return new TypingIndicator(in);
        }

        @Override
        public TypingIndicator[] newArray(int size) {
            return new TypingIndicator[size];
        }
    };

    @Override
    public TypingIndicator clone() {
        try {
            TypingIndicator cloned = (TypingIndicator) super.clone();
            if (this.sender != null) {
                cloned.sender = this.sender.clone();
            }
            if (this.metadata != null) {
                cloned.metadata = new JSONObject(this.metadata.toString());
            }
            return cloned;
        } catch (CloneNotSupportedException | JSONException e) {
            return null;
        }
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
        if (!(other instanceof TypingIndicator)) return false;

        // 4. Cast
        TypingIndicator that = (TypingIndicator) other;

        // 5. Compare all fields
        return lastTimestamp == that.lastTimestamp &&
                ContentEqualsHelper.objectsContentEqual(sender, that.sender) &&
                ContentEqualsHelper.stringsEqual(receiverId, that.receiverId) &&
                ContentEqualsHelper.stringsEqual(receiverType, that.receiverType) &&
                ContentEqualsHelper.jsonObjectsEqual(metadata, that.metadata) &&
                ContentEqualsHelper.stringsEqual(typingStatus, that.typingStatus);
    }

    /**
     * Get sender object
     *
     * @return An object of <code>User</code> class
     * @version <b>v2</b>
     * @see User
     * @since <b>v1</b>
     *
     */
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    /**
     *  Get id of the receiver
     *
     * @return id of the receiver(user/group)
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     *  Get type of receiver
     *
     * @return type of the receiver
     * @version <b>v2</b>
     * @since <b>v1</b>
     * @see  CometChatConstants.ReceiverTypes
     *
     */
    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    /**
     *  Get <code>JSONObject</code> of data set by developer
     *
     * @return <code>JSONObject</code> of custom data set by developer
     * @version <b>v2</b>
     * @since <b>v1</b>
     *
     */
    public JSONObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    public String getTypingStatus() {
        return typingStatus;
    }

    public void setTypingStatus(String typingStatus) {
        this.typingStatus = typingStatus;
    }


    @Override
    public String toString() {
        return "TypingIndicator{" +
                "sender=" + sender +
                ", receiverId='" + receiverId + '\'' +
                ", receiverType='" + receiverType + '\'' +
                ", metadata=" + metadata +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }
}
