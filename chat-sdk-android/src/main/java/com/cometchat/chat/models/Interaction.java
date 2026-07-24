package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.constants.CometChatConstants;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * This class represents an individual interaction within an InteractiveMessage.
 */
public class Interaction implements Parcelable, Cloneable {
    private String elementId;
    private long interactedAt;

    /**
     * Creates a new Interaction instance.
     *
     * @param elementId   The unique identifier of the interactive element.
     * @param interactedAt The timestamp of when the interaction occurred.
     */
    public Interaction(String elementId, long interactedAt) {
        this.elementId = elementId;
        this.interactedAt = interactedAt;
    }

    protected Interaction(Parcel in) {
        elementId = in.readString();
        interactedAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(elementId);
        dest.writeLong(interactedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Interaction> CREATOR = new Creator<Interaction>() {
        @Override
        public Interaction createFromParcel(Parcel in) {
            return new Interaction(in);
        }

        @Override
        public Interaction[] newArray(int size) {
            return new Interaction[size];
        }
    };

    @Override
    public Interaction clone() {
        try {
            return (Interaction) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Sets the identifier of the interactive element.
     *
     * @param elementId   The new unique identifier of the interactive element.
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * Sets the timestamp of the interaction.
     *
     * @param interactedAt The new timestamp of when the interaction occurred.
     */
    public void setInteractedAt(long interactedAt) {
        this.interactedAt = interactedAt;
    }

    public String getElementId() {
        return elementId;
    }

    public long getInteractedAt() {
        return interactedAt;
    }

    public static Interaction fromJson(JSONObject interaction) {
        Interaction interactionObj = null;
        if (interaction != null) {
            try {
                if (interaction.has(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ELEMENT_ID) && interaction.has(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTED_AT)) {
                    interactionObj = new Interaction(interaction.getString(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ELEMENT_ID), interaction.getLong(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTED_AT));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return interactionObj;
    }

    public HashMap<String,String> toMap(){
        HashMap<String,String> map=new HashMap<>();
        map.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_ELEMENT_ID,this.getElementId());
        map.put(CometChatConstants.MessageKeys.KEY_INTERACTIVE_INTERACTED_AT,String.valueOf(this.getInteractedAt()));
        return map;
    }

    @Override
    public String toString() {
        return "Interaction{ elementId = " + this.getElementId() + ", timestamp = " + this.getInteractedAt() + " }";
    }

    /**
     * Compares this Interaction with another object for content equality.
     * <p>
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality. Two Interaction objects are considered content-equal
     * if they have the same elementId and interactedAt values.
     * </p>
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof Interaction)) return false;

        Interaction that = (Interaction) other;

        return interactedAt == that.interactedAt &&
                com.cometchat.chat.utils.ContentEqualsHelper.stringsEqual(elementId, that.elementId);
    }
    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }


}
