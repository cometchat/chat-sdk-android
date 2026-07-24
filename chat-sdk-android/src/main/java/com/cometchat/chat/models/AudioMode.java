package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.utils.ContentEqualsHelper;

public class AudioMode implements Parcelable, Cloneable {

    private String mode;
    private boolean isSelected;

    public AudioMode(String type, boolean isSelected) {
        this.mode = type;
        this.isSelected = isSelected;
    }

    protected AudioMode(Parcel in) {
        mode = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mode);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AudioMode> CREATOR = new Creator<AudioMode>() {
        @Override
        public AudioMode createFromParcel(Parcel in) {
            return new AudioMode(in);
        }

        @Override
        public AudioMode[] newArray(int size) {
            return new AudioMode[size];
        }
    };

    @Override
    public AudioMode clone() {
        try {
            return (AudioMode) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String type) {
        this.mode = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    /**
     * Compares this AudioMode with another object for content equality.
     * <p>
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality. Two AudioMode objects are considered content-equal if
     * they have the same mode and isSelected values.
     * </p>
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof AudioMode)) return false;

        AudioMode that = (AudioMode) other;

        return isSelected == that.isSelected &&
                ContentEqualsHelper.stringsEqual(mode, that.mode);
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }
}
