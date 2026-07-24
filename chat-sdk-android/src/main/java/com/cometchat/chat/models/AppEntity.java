package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adityagokula on 01/11/18.
 */

public abstract class AppEntity implements Parcelable, Cloneable {

    public AppEntity() {
    }

    protected AppEntity(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public AppEntity clone() throws CloneNotSupportedException {
        return (AppEntity) super.clone();
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
        if (!(other instanceof AppEntity)) return false;

        // 4. AppEntity has no fields, so return true after basic checks
        return true;
    }
    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }


}
