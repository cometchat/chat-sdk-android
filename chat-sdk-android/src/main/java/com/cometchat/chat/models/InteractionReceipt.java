package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;

import java.util.ArrayList;
import java.util.List;
/**
 * This class represents a receipt for an interaction. Each InteractionReceipt
 * instance includes the details of the interactions performed on a specific message
 * by a user. It includes getters and setters to access and update the sender of
 * the interaction, receiverId, messageId, interactions, receiverType, and messageSenderUid.
 */
public class InteractionReceipt implements Parcelable, Cloneable {
    private String receiverId;
    private long messageId;
    private List<Interaction> interactions;
    private @CometChatConstants.ReceiverTypes String receiverType;
    private String messageSenderUid;
    private User sender;

    public InteractionReceipt() {}

    protected InteractionReceipt(Parcel in) {
        receiverId = in.readString();
        messageId = in.readLong();
        interactions = in.createTypedArrayList(Interaction.CREATOR);
        receiverType = in.readString();
        messageSenderUid = in.readString();
        sender = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(receiverId);
        dest.writeLong(messageId);
        dest.writeTypedList(interactions);
        dest.writeString(receiverType);
        dest.writeString(messageSenderUid);
        dest.writeParcelable(sender, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InteractionReceipt> CREATOR = new Creator<InteractionReceipt>() {
        @Override
        public InteractionReceipt createFromParcel(Parcel in) {
            return new InteractionReceipt(in);
        }

        @Override
        public InteractionReceipt[] newArray(int size) {
            return new InteractionReceipt[size];
        }
    };

    @Override
    public InteractionReceipt clone() {
        try {
            InteractionReceipt cloned = (InteractionReceipt) super.clone();
            if (this.sender != null) {
                cloned.sender = this.sender.clone();
            }
            if (this.interactions != null) {
                cloned.interactions = new ArrayList<>();
                for (Interaction interaction : this.interactions) {
                    cloned.interactions.add(interaction.clone());
                }
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }


    public void setSender(User sender) {
        this.sender = sender;
    }


    public void setMessageSenderUid(String messageSenderUid) {
        this.messageSenderUid = messageSenderUid;
    }

    public void setReceiverId(String receiverUid) {
        this.receiverId = receiverUid;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public void setReceiverType(@CometChatConstants.ReceiverTypes String receiverType) {
        this.receiverType = receiverType;
    }

    public void setInteractions(List<Interaction> interactions) {
        this.interactions = interactions;
    }

    public User getSender() {
        return sender;
    }

    public String getMessageSenderUid() {
        return messageSenderUid;
    }

    public String getReceiverId() {
        return receiverId;
    }


    public long getMessageId() {
        return messageId;
    }

    public List<Interaction> getInteractions() {
        return interactions;
    }

    public String getReceiverType() {
        return receiverType;
    }

    @Override
    public String toString() {
        return "MessageReceipt{" +
                "messageId=" + messageId +
                ", sender=" + sender +
                ", receiverType='" + receiverType + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", messageSender='" + messageSenderUid + '\'' +
                ", interactions=" + interactions +
                '}';
    }

    /**
     * Compares this InteractionReceipt with another object for content equality.
     * <p>
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality. Two InteractionReceipt objects are considered content-equal
     * if all their fields have equivalent values.
     * </p>
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof InteractionReceipt)) return false;

        InteractionReceipt that = (InteractionReceipt) other;

        // Compare primitive field
        if (messageId != that.messageId) return false;

        // Compare String fields using ContentEqualsHelper
        if (!com.cometchat.chat.utils.ContentEqualsHelper.stringsEqual(receiverId, that.receiverId)) return false;
        if (!com.cometchat.chat.utils.ContentEqualsHelper.stringsEqual(receiverType, that.receiverType)) return false;
        if (!com.cometchat.chat.utils.ContentEqualsHelper.stringsEqual(messageSenderUid, that.messageSenderUid)) return false;

        // Compare nested User object using contentEquals
        if (!com.cometchat.chat.utils.ContentEqualsHelper.objectsContentEqual(sender, that.sender)) return false;

        // Compare List<Interaction> using ContentEqualsHelper (will use Interaction.contentEquals)
        if (!com.cometchat.chat.utils.ContentEqualsHelper.listsEqual(interactions, that.interactions)) return false;

        return true;
    }
    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }


}
