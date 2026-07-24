package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Represents a flag reason that can be used to report inappropriate content.
 * This class contains information about predefined reasons for flagging messages,
 * including unique identifiers, names, descriptions, and timestamps.
 */
public class FlagReason implements Parcelable, Cloneable {
    /**
     * The timestamp when this flag reason was created (in milliseconds since epoch).
     */
    private long createdAt;

    /**
     * A detailed description of the flag reason explaining when it should be used.
     */
    private String description;

    /**
     * The unique identifier for this flag reason.
     */
    private String id;

    /**
     * The display name of the flag reason shown to users.
     */
    private String name;

    /**
     * The timestamp when this flag reason was last updated (in milliseconds since epoch).
     */
    private long updatedAt;

    /**
     * Constructs a new FlagReason with the specified ID and name.
     *
     * @param id the unique identifier for the flag reason
     * @param name the display name for the flag reason
     */
    public FlagReason(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Private default constructor for internal use during deserialization.
     */
    private FlagReason() {}

    protected FlagReason(Parcel in) {
        createdAt = in.readLong();
        description = in.readString();
        id = in.readString();
        name = in.readString();
        updatedAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createdAt);
        dest.writeString(description);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeLong(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FlagReason> CREATOR = new Creator<FlagReason>() {
        @Override
        public FlagReason createFromParcel(Parcel in) {
            return new FlagReason(in);
        }

        @Override
        public FlagReason[] newArray(int size) {
            return new FlagReason[size];
        }
    };

    @Override
    public FlagReason clone() {
        try {
            return (FlagReason) super.clone();
        } catch (CloneNotSupportedException e) {
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
        if (!(other instanceof FlagReason)) return false;

        // 4. Cast
        FlagReason that = (FlagReason) other;

        // 5. Compare all fields
        return createdAt == that.createdAt &&
                updatedAt == that.updatedAt &&
                ContentEqualsHelper.stringsEqual(description, that.description) &&
                ContentEqualsHelper.stringsEqual(id, that.id) &&
                ContentEqualsHelper.stringsEqual(name, that.name);
    }

    /**
     * Gets the creation timestamp of this flag reason.
     *
     * @return the timestamp when this flag reason was created (in milliseconds since epoch)
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of this flag reason.
     *
     * @param createdAt the timestamp when this flag reason was created (in milliseconds since epoch)
     */
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the description of this flag reason.
     *
     * @return a detailed description explaining when this flag reason should be used
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this flag reason.
     *
     * @param description a detailed description explaining when this flag reason should be used
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the unique identifier of this flag reason.
     *
     * @return the unique identifier for this flag reason
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this flag reason.
     *
     * @param id the unique identifier for this flag reason
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the display name of this flag reason.
     *
     * @return the display name shown to users for this flag reason
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of this flag reason.
     *
     * @param name the display name shown to users for this flag reason
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the last updated timestamp of this flag reason.
     *
     * @return the timestamp when this flag reason was last updated (in milliseconds since epoch)
     */
    public long getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last updated timestamp of this flag reason.
     *
     * @param updatedAt the timestamp when this flag reason was last updated (in milliseconds since epoch)
     */
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a FlagReason object from a JSON representation.
     * This method parses JSON data and populates a new FlagReason instance
     * with the available fields from the JSON object.
     *
     * @param json the JSONObject containing flag reason data
     * @return a new FlagReason object populated with data from the JSON, or empty FlagReason if json is null
     * @throws JSONException if there's an error parsing the JSON data
     */
    public static FlagReason fromJson(JSONObject json) throws JSONException {
        FlagReason flagReason = new FlagReason();
        if (json != null) {
            if (json.has(CometChatConstants.FlagReasonsKeys.CREATED_AT)) {
                flagReason.setCreatedAt(json.getLong(CometChatConstants.FlagReasonsKeys.CREATED_AT));
            }
            if (json.has(CometChatConstants.FlagReasonsKeys.DESCRIPTION)) {
                flagReason.setDescription(json.optString(CometChatConstants.FlagReasonsKeys.DESCRIPTION));
            }
            if (json.has(CometChatConstants.FlagReasonsKeys.ID)) {
                flagReason.setId(json.optString(CometChatConstants.FlagReasonsKeys.ID));
            }
            if (json.has(CometChatConstants.FlagReasonsKeys.NAME)) {
                flagReason.setName(json.optString(CometChatConstants.FlagReasonsKeys.NAME));
            }
            if (json.has(CometChatConstants.FlagReasonsKeys.UPDATED_AT)) {
                flagReason.setUpdatedAt(json.optLong(CometChatConstants.FlagReasonsKeys.UPDATED_AT));
            }
        }
        return flagReason;
    }

    /**
     * Returns a string representation of this FlagReason object.
     * The string includes all the field values in a readable format,
     * useful for debugging and logging purposes.
     *
     * @return a string representation of this FlagReason containing all field values
     */
    @NonNull
    @Override
    public String toString() {
        return "FlagReason{" +
                "createdAt=" + createdAt +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /**
     * Converts this FlagReason object to a HashMap representation.
     * This method is useful for serialization, API calls, or when the flag reason
     * data needs to be passed as key-value pairs. All field values are converted
     * to String format for compatibility.
     *
     * @return a HashMap containing all flag reason data with keys from CometChatConstants.FlagReasonsKeys
     */
    public HashMap<String, String> toMap() {
        HashMap<String, String> flagReason = new HashMap<>();
        flagReason.put(CometChatConstants.FlagReasonsKeys.CREATED_AT, String.valueOf(createdAt));
        flagReason.put(CometChatConstants.FlagReasonsKeys.DESCRIPTION, description);
        flagReason.put(CometChatConstants.FlagReasonsKeys.ID, id);
        flagReason.put(CometChatConstants.FlagReasonsKeys.NAME, name);
        flagReason.put(CometChatConstants.FlagReasonsKeys.UPDATED_AT, String.valueOf(updatedAt));
        return flagReason;
    }

    @Override
    public boolean equals(Object o) {
        return contentEquals(o);
    }

}
