package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;


import java.util.HashMap;

/**
 * Represents the details of a flag report.
 * This class contains information about why content was flagged,
 * including a reason ID and optional remark.
 */
public class FlagDetail implements Parcelable, Cloneable {
    /**
     * The unique identifier for the flagging reason.
     */
    private String reasonId;

    /**
     * Optional remark or additional details about the flag.
     */
    private String remark;

    /**
     * Constructs a new FlagDetail with the specified reason ID and remark.
     *
     * @param reasonId the unique identifier for the flagging reason
     * @param remark additional details or comments about the flag
     */
    public FlagDetail(String reasonId, String remark) {
        this.reasonId = reasonId;
        this.remark = remark;
    }

    /**
     * Default constructor that creates an empty FlagDetail.
     */
    public FlagDetail() {}

    protected FlagDetail(Parcel in) {
        reasonId = in.readString();
        remark = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reasonId);
        dest.writeString(remark);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FlagDetail> CREATOR = new Creator<FlagDetail>() {
        @Override
        public FlagDetail createFromParcel(Parcel in) {
            return new FlagDetail(in);
        }

        @Override
        public FlagDetail[] newArray(int size) {
            return new FlagDetail[size];
        }
    };

    @Override
    public FlagDetail clone() {
        try {
            return (FlagDetail) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Gets the reason ID for this flag detail.
     *
     * @return the unique identifier for the flagging reason
     */
    public String getReasonId() {
        return reasonId;
    }

    /**
     * Sets the reason ID for this flag detail.
     *
     * @param reasonId the unique identifier for the flagging reason
     */
    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }

    /**
     * Gets the remark or additional details for this flag.
     *
     * @return the remark associated with this flag, may be null
     */
    public String getRemark() {
        return remark;
    }

    /**
     * Sets the remark or additional details for this flag.
     *
     * @param remark additional details or comments about the flag
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * Converts this FlagDetail object to a HashMap representation.
     * This method is useful for serialization or when preparing data for API calls.
     *
     * @return a HashMap containing the flag detail data with keys from CometChatConstants.FlagDetail
     */
    public HashMap<String, String> toMap() {
        HashMap<String, String> flagDetailMap = new HashMap<>();
        flagDetailMap.put(CometChatConstants.FlagDetail.ID, reasonId);
        if (remark != null) {
            flagDetailMap.put(CometChatConstants.FlagDetail.REASON, remark);
        }
        return flagDetailMap;
    }

    /**
     * Creates a new FlagDetail object from a HashMap representation.
     * This method is useful for deserialization or when parsing data from API responses.
     *
     * @param flagDetailJson a HashMap containing flag detail data with expected keys
     * @return a new FlagDetail object populated with data from the HashMap
     */
    public FlagDetail fromJson(HashMap<String, String> flagDetailJson) {
        FlagDetail flagDetail = new FlagDetail();
        if (flagDetailJson.containsKey(CometChatConstants.FlagDetail.ID)) {
            flagDetail.setReasonId(flagDetailJson.get(CometChatConstants.FlagDetail.ID));
        }
        if (flagDetailJson.containsKey(CometChatConstants.FlagDetail.REASON)) {
            flagDetail.setRemark(flagDetailJson.get(CometChatConstants.FlagDetail.REASON));
        }
        return flagDetail;
    }

    /**
     * Returns a string representation of this FlagDetail object.
     * The string includes the reason ID and remark in a readable format,
     * useful for debugging and logging purposes.
     *
     * @return a string representation of this FlagDetail containing reasonId and remark
     */
    @NonNull
    @Override
    public String toString() {
        return "FlagDetail{" +
                "reasonId='" + reasonId + '\'' +
                ", remark='" + remark + '\'' +
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
        if (!(other instanceof FlagDetail)) return false;

        // 4. Cast
        FlagDetail that = (FlagDetail) other;

        // 5. Compare all fields
        return ContentEqualsHelper.stringsEqual(reasonId, that.reasonId) &&
                ContentEqualsHelper.stringsEqual(remark, that.remark);
    }
    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

}
