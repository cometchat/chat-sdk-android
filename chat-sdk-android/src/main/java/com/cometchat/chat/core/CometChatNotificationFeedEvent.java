package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.NotificationFeedItem;

import org.json.JSONObject;

/**
 * Represents a WebSocket event for notification feed items.
 * Parses the WebSocket envelope and extracts the NotificationFeedItem from the body.
 * <p>
 * Only processes messages where:
 * - {@code type == "notification_feed_item"}
 * - {@code body.action == "sent"}
 * <p>
 * Malformed payloads are logged and skipped (no crash).
 *
 * @since v4
 */
class CometChatNotificationFeedEvent {

    private NotificationFeedItem feedItem;
    private boolean valid;

    /**
     * Parse a WebSocket message JSON into a notification feed event.
     *
     * @param messageJson The raw WebSocket message JSON
     * @return A CometChatNotificationFeedEvent (check isValid() before using)
     */
    public static CometChatNotificationFeedEvent fromJson(JSONObject messageJson) {
        CometChatNotificationFeedEvent event = new CometChatNotificationFeedEvent();
        event.valid = false;

        try {
            // Check type
            if (!messageJson.has(CometChatConstants.NotificationFeedKeys.KEY_TYPE)) {
                return event;
            }
            String type = messageJson.getString(CometChatConstants.NotificationFeedKeys.KEY_TYPE);
            if (!CometChatConstants.WS_TYPE_NOTIFICATION_FEED_ITEM.equals(type)) {
                return event;
            }

            // Check body and action
            if (!messageJson.has(CometChatConstants.NotificationFeedKeys.KEY_BODY)) {
                return event;
            }
            JSONObject body = messageJson.getJSONObject(CometChatConstants.NotificationFeedKeys.KEY_BODY);

            if (!body.has(CometChatConstants.NotificationFeedKeys.KEY_ACTION)) {
                return event;
            }
            String action = body.getString(CometChatConstants.NotificationFeedKeys.KEY_ACTION);
            if (!CometChatConstants.WS_ACTION_SENT.equals(action)) {
                return event;
            }

            // Extract feedItem
            if (!body.has(CometChatConstants.NotificationFeedKeys.KEY_FEED_ITEM)) {
                return event;
            }
            JSONObject feedItemJson = body.getJSONObject(CometChatConstants.NotificationFeedKeys.KEY_FEED_ITEM);
            event.feedItem = NotificationFeedItem.fromJson(feedItemJson);
            event.valid = (event.feedItem != null && event.feedItem.getId() != null);

        } catch (Exception e) {
            Logger.error("CometChatNotificationFeedEvent.fromJson error: " + e.getMessage());
            event.valid = false;
        }

        return event;
    }

    /**
     * Check if this event was successfully parsed and contains a valid feed item.
     *
     * @return true if the event is valid and can be dispatched
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Get the parsed NotificationFeedItem from the WebSocket event.
     *
     * @return The feed item, or null if parsing failed
     */
    public NotificationFeedItem getFeedItem() {
        return feedItem;
    }
}
