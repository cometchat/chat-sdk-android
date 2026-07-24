package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.helpers.Logger;

import org.json.JSONObject;

/**
 * Represents a push notification from the CometChat Campaigns service.
 * Used by {@link com.cometchat.chat.core.CometChat#markPushNotificationDelivered}
 * and {@link com.cometchat.chat.core.CometChat#markPushNotificationClicked}.
 * <p>
 * This model is typically constructed from the push notification payload
 * received via FCM/APNs.
 *
 * @since v4
 */
public class PushNotification implements Parcelable {

    private String id;
    private String announcementId;
    private String campaignId;
    private String source;

    public PushNotification() {
    }

    public PushNotification(String id, String announcementId, String campaignId, String source) {
        this.id = id;
        this.announcementId = announcementId;
        this.campaignId = campaignId;
        this.source = source;
    }

    protected PushNotification(Parcel in) {
        id = in.readString();
        announcementId = in.readString();
        campaignId = in.readString();
        source = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(announcementId);
        dest.writeString(campaignId);
        dest.writeString(source);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PushNotification> CREATOR = new Creator<PushNotification>() {
        @Override
        public PushNotification createFromParcel(Parcel in) {
            return new PushNotification(in);
        }

        @Override
        public PushNotification[] newArray(int size) {
            return new PushNotification[size];
        }
    };

    /**
     * Get the announcement ID from the push payload.
     *
     * @return Announcement ID
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the announcement ID (same as id, for clarity).
     *
     * @return Announcement ID
     */
    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    /**
     * Get the campaign ID if this push is from a campaign.
     *
     * @return Campaign ID or null
     */
    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    /**
     * Get the source of this push notification.
     * Always "campaign" for notification feed pushes.
     *
     * @return Source string
     */
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Deserialize a PushNotification from a JSON object.
     *
     * @param jsonObject The JSON object from the push payload
     * @return A PushNotification instance
     */
    public static PushNotification fromJson(JSONObject jsonObject) {
        PushNotification pushNotification = new PushNotification();
        try {
            if (jsonObject.has("id")) {
                pushNotification.setId(jsonObject.getString("id"));
            }
            if (jsonObject.has("announcementId")) {
                pushNotification.setAnnouncementId(jsonObject.getString("announcementId"));
            }
            if (jsonObject.has("campaignId") && !jsonObject.isNull("campaignId")) {
                pushNotification.setCampaignId(jsonObject.getString("campaignId"));
            }
            if (jsonObject.has("source")) {
                pushNotification.setSource(jsonObject.getString("source"));
            }
        } catch (Exception e) {
            Logger.error("PushNotification.fromJson error: " + e.getMessage());
        }
        return pushNotification;
    }

    @Override
    public String toString() {
        return "PushNotification{" +
                "id='" + id + '\'' +
                ", announcementId='" + announcementId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
