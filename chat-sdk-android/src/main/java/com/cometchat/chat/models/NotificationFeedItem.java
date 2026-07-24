package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cometchat.chat.helpers.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a notification feed item from the CometChat Campaigns service.
 * Maps from the backend "Announcement" entity.
 * <p>
 * The backend {@code subCategory} field is exposed as {@code category} to consumers.
 * The {@code content} field contains fully rendered Card_Schema JSON ready for rendering.
 *
 * @since v4
 */
public class NotificationFeedItem implements Parcelable {

    private String id;
    private String category;
    private JSONObject content;
    private Long readAt;
    private Long deliveredAt;
    private long sentAt;
    private HashMap<String, Object> metadata;
    private List<String> tags;
    private String sender;
    private String receiver;
    private String receiverType;

    public NotificationFeedItem() {
        this.metadata = new HashMap<>();
        this.tags = new ArrayList<>();
    }

    protected NotificationFeedItem(Parcel in) {
        id = in.readString();
        category = in.readString();
        try {
            String contentStr = in.readString();
            content = contentStr != null ? new JSONObject(contentStr) : null;
        } catch (Exception e) {
            content = null;
        }
        if (in.readByte() == 1) {
            readAt = in.readLong();
        } else {
            readAt = null;
        }
        if (in.readByte() == 1) {
            deliveredAt = in.readLong();
        } else {
            deliveredAt = null;
        }
        sentAt = in.readLong();
        metadata = (HashMap<String, Object>) in.readSerializable();
        tags = in.createStringArrayList();
        sender = in.readString();
        receiver = in.readString();
        receiverType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(category);
        dest.writeString(content != null ? content.toString() : null);
        if (readAt != null) {
            dest.writeByte((byte) 1);
            dest.writeLong(readAt);
        } else {
            dest.writeByte((byte) 0);
        }
        if (deliveredAt != null) {
            dest.writeByte((byte) 1);
            dest.writeLong(deliveredAt);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeLong(sentAt);
        dest.writeSerializable(metadata);
        dest.writeStringList(tags);
        dest.writeString(sender);
        dest.writeString(receiver);
        dest.writeString(receiverType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationFeedItem> CREATOR = new Creator<NotificationFeedItem>() {
        @Override
        public NotificationFeedItem createFromParcel(Parcel in) {
            return new NotificationFeedItem(in);
        }

        @Override
        public NotificationFeedItem[] newArray(int size) {
            return new NotificationFeedItem[size];
        }
    };

    // region Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the category of this feed item.
     * Maps from the backend "subCategory" field.
     *
     * @return Category string (e.g., "promotions", "updates", "orders")
     */
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Get the fully rendered Card_Schema JSON content.
     * Ready for use with CometChatCardsRenderer.
     *
     * @return JSONObject containing Card_Schema JSON
     */
    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    /**
     * Get the Unix timestamp when this item was read, or null if unread.
     *
     * @return Read timestamp or null
     */
    public Long getReadAt() {
        return readAt;
    }

    public void setReadAt(Long readAt) {
        this.readAt = readAt;
    }

    /**
     * Get the Unix timestamp when this item was delivered, or null if not yet delivered.
     *
     * @return Delivered timestamp or null
     */
    public Long getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    /**
     * Get the Unix timestamp when this item was sent.
     *
     * @return Sent timestamp
     */
    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    /**
     * Get custom key-value metadata.
     *
     * @return Metadata map
     */
    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Get optional tags for filtering.
     *
     * @return List of tags
     */
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    /**
     * Check if this feed item has been read.
     *
     * @return true if readAt is not null
     */
    public boolean isRead() {
        return readAt != null;
    }

    // endregion

    // region JSON Deserialization

    /**
     * Deserialize a single NotificationFeedItem from a JSON object.
     * Maps backend "subCategory" to "category".
     *
     * @param jsonObject The JSON object from the API response
     * @return A NotificationFeedItem instance
     */
    public static NotificationFeedItem fromJson(JSONObject jsonObject) {
        NotificationFeedItem item = new NotificationFeedItem();
        try {
            if (jsonObject.has("id")) {
                item.setId(jsonObject.getString("id"));
            }
            if (jsonObject.has("subCategory") && !jsonObject.isNull("subCategory")) {
                item.setCategory(jsonObject.getString("subCategory"));
            } else if (jsonObject.has("templateCategory") && !jsonObject.isNull("templateCategory")) {
                item.setCategory(jsonObject.getString("templateCategory"));
            } else if (jsonObject.has("category") && !jsonObject.isNull("category")) {
                item.setCategory(jsonObject.getString("category"));
            }
            if (jsonObject.has("data")) {
                item.setContent(jsonObject.getJSONObject("data"));
            } else if (jsonObject.has("content")) {
                item.setContent(jsonObject.getJSONObject("content"));
            }
            if (jsonObject.has("readAt") && !jsonObject.isNull("readAt")) {
                item.setReadAt(jsonObject.getLong("readAt"));
            }
            if (jsonObject.has("deliveredAt") && !jsonObject.isNull("deliveredAt")) {
                item.setDeliveredAt(jsonObject.getLong("deliveredAt"));
            }
            if (jsonObject.has("sentAt")) {
                item.setSentAt(jsonObject.getLong("sentAt"));
            }
            if (jsonObject.has("metadata") && !jsonObject.isNull("metadata")) {
                JSONObject metaJson = jsonObject.getJSONObject("metadata");
                HashMap<String, Object> metaMap = new HashMap<>();
                Iterator<String> keys = metaJson.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    metaMap.put(key, metaJson.get(key));
                }
                item.setMetadata(metaMap);
            }
            if (jsonObject.has("tags") && !jsonObject.isNull("tags")) {
                JSONArray tagsArray = jsonObject.getJSONArray("tags");
                List<String> tagsList = new ArrayList<>();
                for (int i = 0; i < tagsArray.length(); i++) {
                    tagsList.add(tagsArray.getString(i));
                }
                item.setTags(tagsList);
            }
            if (jsonObject.has("sender")) {
                item.setSender(jsonObject.getString("sender"));
            }
            if (jsonObject.has("receiver")) {
                item.setReceiver(jsonObject.getString("receiver"));
            }
            if (jsonObject.has("receiverType")) {
                item.setReceiverType(jsonObject.getString("receiverType"));
            }
        } catch (Exception e) {
            Logger.error("NotificationFeedItem.fromJson error: " + e.getMessage());
        }
        return item;
    }

    /**
     * Deserialize a list of NotificationFeedItems from an API response JSON.
     * Expects a "data" array in the response.
     *
     * @param responseJson The full API response JSON
     * @return List of NotificationFeedItem instances
     */
    public static List<NotificationFeedItem> listFromJson(JSONObject responseJson) {
        List<NotificationFeedItem> items = new ArrayList<>();
        try {
            if (responseJson.has("data")) {
                JSONArray dataArray = responseJson.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    items.add(fromJson(dataArray.getJSONObject(i)));
                }
            }
        } catch (Exception e) {
            Logger.error("NotificationFeedItem.listFromJson error: " + e.getMessage());
        }
        return items;
    }

    // endregion

    @Override
    public String toString() {
        return "NotificationFeedItem{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", readAt=" + readAt +
                ", deliveredAt=" + deliveredAt +
                ", sentAt=" + sentAt +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", receiverType='" + receiverType + '\'' +
                ", isRead=" + isRead() +
                ", tags=" + tags +
                '}';
    }
}
