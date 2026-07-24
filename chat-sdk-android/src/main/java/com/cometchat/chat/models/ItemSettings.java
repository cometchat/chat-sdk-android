package com.cometchat.chat.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.utils.ContentEqualsHelper;
import java.util.Objects;

/**
 * Created by Rohit Giri on 28/10/22.
 */

public class ItemSettings implements Parcelable, Cloneable {
    private String position;
    private Boolean visibility;
    private String color;

    public ItemSettings(String position,Boolean visibility,String color) {
        this.position = position;
        this.visibility = visibility;
        this.color = color;
    }

    protected ItemSettings(Parcel in) {
        position = in.readString();
        byte visibilityByte = in.readByte();
        visibility = visibilityByte == -1 ? null : visibilityByte == 1;
        color = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(position);
        dest.writeByte(visibility == null ? (byte) -1 : (visibility ? (byte) 1 : (byte) 0));
        dest.writeString(color);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemSettings> CREATOR = new Creator<ItemSettings>() {
        @Override
        public ItemSettings createFromParcel(Parcel in) {
            return new ItemSettings(in);
        }

        @Override
        public ItemSettings[] newArray(int size) {
            return new ItemSettings[size];
        }
    };

    @Override
    public ItemSettings clone() {
        try {
            return (ItemSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getPosition() {
        return this.position;
    }

    public Boolean getVisibility(){
        return this.visibility;
    }

    public String getColor() {
        return this.color;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        if (position != null) {
            bundle.putString("position", position);
        }
        if (visibility != null) {
            bundle.putBoolean("visibility", visibility);
        }
        if (color != null) {
            bundle.putString("color", color);
        }
        return  bundle;
    }

    /**
     * Compares this ItemSettings object with another for content equality.
     * <p>
     * Unlike equals() which compares by identity, this method compares all fields
     * for value equality. Two ItemSettings objects are considered content-equal
     * if all their fields have equal values.
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
        if (!(other instanceof ItemSettings)) return false;

        // Cast
        ItemSettings that = (ItemSettings) other;

        // Compare all fields
        return ContentEqualsHelper.stringsEqual(position, that.position) &&
                Objects.equals(visibility, that.visibility) &&
                ContentEqualsHelper.stringsEqual(color, that.color);
    }

    @Override
    public String toString() {
        return "ItemSettings{" +
                "position='" + position + '\'' +
                ", visibility=" + visibility +
                ", color='" + color + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }
}
