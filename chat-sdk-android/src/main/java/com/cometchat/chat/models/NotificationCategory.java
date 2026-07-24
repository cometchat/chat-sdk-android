package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.helpers.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a notification category used for filter chips in the notification feed UI.
 *
 * @since v4
 */
public class NotificationCategory implements Parcelable {

    private String id;
    private String appId;
    private String name;
    private String description;
    private long createdAt;
    private long updatedAt;

    public NotificationCategory() {
    }

    public NotificationCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected NotificationCategory(Parcel in) {
        id = in.readString();
        appId = in.readString();
        name = in.readString();
        description = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(appId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationCategory> CREATOR = new Creator<NotificationCategory>() {
        @Override
        public NotificationCategory createFromParcel(Parcel in) {
            return new NotificationCategory(in);
        }

        @Override
        public NotificationCategory[] newArray(int size) {
            return new NotificationCategory[size];
        }
    };

    /**
     * Get the category identifier.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the app ID this category belongs to.
     */
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * Get the category name. Used as display text for filter chips.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the category description.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the creation timestamp.
     */
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get the last updated timestamp.
     */
    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Deserialize a single NotificationCategory from a JSON object.
     *
     * @param jsonObject The JSON object from the API response
     * @return A NotificationCategory instance
     */
    public static NotificationCategory fromJson(JSONObject jsonObject) {
        NotificationCategory category = new NotificationCategory();
        try {
            if (jsonObject.has("id")) {
                category.setId(jsonObject.getString("id"));
            }
            if (jsonObject.has("name")) {
                category.setName(jsonObject.getString("name"));
            } else if (jsonObject.has("label")) {
                category.setName(jsonObject.getString("label"));
            }
            if (jsonObject.has("description")) {
                category.setDescription(jsonObject.getString("description"));
            }
            if (jsonObject.has("appId")) {
                category.setAppId(jsonObject.getString("appId"));
            }
            if (jsonObject.has("createdAt")) {
                category.setCreatedAt(jsonObject.getLong("createdAt"));
            }
            if (jsonObject.has("updatedAt")) {
                category.setUpdatedAt(jsonObject.getLong("updatedAt"));
            }
        } catch (Exception e) {
            Logger.error("NotificationCategory.fromJson error: " + e.getMessage());
        }
        return category;
    }

    /**
     * Deserialize a list of NotificationCategory from an API response JSON.
     * Expects a "data" array in the response.
     *
     * @param responseJson The full API response JSON
     * @return List of NotificationCategory instances
     */
    public static List<NotificationCategory> listFromJson(JSONObject responseJson) {
        List<NotificationCategory> categories = new ArrayList<>();
        try {
            if (responseJson.has("data")) {
                JSONArray dataArray = responseJson.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    categories.add(fromJson(dataArray.getJSONObject(i)));
                }
            }
        } catch (Exception e) {
            Logger.error("NotificationCategory.listFromJson error: " + e.getMessage());
        }
        return categories;
    }

    @Override
    public String toString() {
        return "NotificationCategory{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
