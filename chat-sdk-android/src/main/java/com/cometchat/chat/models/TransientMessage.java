package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class TransientMessage implements Parcelable, Cloneable {

    private String receiverId;
    @CometChatConstants.ReceiverTypes
    private String receiverType;
    private User sender;
    private JSONObject data;

    public TransientMessage(String receiverId, @CometChatConstants.ReceiverTypes String receiverType, JSONObject data){
        this.receiverId = receiverId;
        this.receiverType = receiverType;
        this.data = data;
    }

    protected TransientMessage(Parcel in) {
        receiverId = in.readString();
        receiverType = in.readString();
        sender = in.readParcelable(User.class.getClassLoader());
        String dataStr = in.readString();
        if (dataStr != null) {
            try {
                data = new JSONObject(dataStr);
            } catch (JSONException e) {
                data = null;
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(receiverId);
        dest.writeString(receiverType);
        dest.writeParcelable(sender, flags);
        dest.writeString(data != null ? data.toString() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransientMessage> CREATOR = new Creator<TransientMessage>() {
        @Override
        public TransientMessage createFromParcel(Parcel in) {
            return new TransientMessage(in);
        }

        @Override
        public TransientMessage[] newArray(int size) {
            return new TransientMessage[size];
        }
    };

    @Override
    public TransientMessage clone() {
        try {
            TransientMessage cloned = (TransientMessage) super.clone();
            if (this.sender != null) {
                cloned.sender = this.sender.clone();
            }
            if (this.data != null) {
                cloned.data = new JSONObject(this.data.toString());
            }
            return cloned;
        } catch (CloneNotSupportedException | JSONException e) {
            return null;
        }
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId( @CometChatConstants.ReceiverTypes  String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TransientMessage{" +
                "receiverId='" + receiverId + '\'' +
                ", receiverType='" + receiverType + '\'' +
                ", sender=" + sender +
                ", data=" + data +
                '}';
    }

    /**
     * Compares this TransientMessage with another object for content equality.
     * <p>
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality. Two TransientMessage objects are considered content-equal
     * if all their fields have equivalent values.
     * </p>
     * <p>
     * The following fields are compared:
     * <ul>
     *   <li>receiverId - compared using String equality</li>
     *   <li>receiverType - compared using String equality</li>
     *   <li>sender - compared using User.contentEquals()</li>
     *   <li>data - compared using JSONObject string representation</li>
     * </ul>
     * </p>
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        // Same reference check
        if (this == other) return true;

        // Null check
        if (other == null) return false;

        // Type check
        if (!(other instanceof TransientMessage)) return false;

        // Cast
        TransientMessage that = (TransientMessage) other;

        // Compare all fields
        return ContentEqualsHelper.stringsEqual(this.receiverId, that.receiverId)
                && ContentEqualsHelper.stringsEqual(this.receiverType, that.receiverType)
                && ContentEqualsHelper.objectsContentEqual(this.sender, that.sender)
                && ContentEqualsHelper.jsonObjectsEqual(this.data, that.data);
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }
}
