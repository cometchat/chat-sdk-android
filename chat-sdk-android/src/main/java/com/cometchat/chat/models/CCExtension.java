package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.utils.ContentEqualsHelper;


public class CCExtension implements Parcelable, Cloneable {

    private String extensionId;
    private String extensionName;

    public CCExtension (String extensionId, String extensionName){
        this.extensionId = extensionId;
        this.extensionName = extensionName;
    }

    protected CCExtension(Parcel in) {
        extensionId = in.readString();
        extensionName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(extensionId);
        dest.writeString(extensionName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CCExtension> CREATOR = new Creator<CCExtension>() {
        @Override
        public CCExtension createFromParcel(Parcel in) {
            return new CCExtension(in);
        }

        @Override
        public CCExtension[] newArray(int size) {
            return new CCExtension[size];
        }
    };

    @Override
    public CCExtension clone() {
        try {
            return (CCExtension) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getExtensionId() {
        return extensionId;
    }


    public String getExtensionName() {
        return extensionName;
    }

    /**
     * Compares this CCExtension with another object for content equality.
     * <p>
     * Unlike equals() which compares by identity (ID), this method
     * compares all fields for value equality.
     * </p>
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    public boolean contentEquals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof CCExtension)) return false;

        CCExtension that = (CCExtension) other;

        return ContentEqualsHelper.stringsEqual(extensionId, that.extensionId) &&
                ContentEqualsHelper.stringsEqual(extensionName, that.extensionName);
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

}
