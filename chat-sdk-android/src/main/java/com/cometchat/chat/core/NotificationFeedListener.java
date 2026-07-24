package com.cometchat.chat.core;

import com.cometchat.chat.models.NotificationFeedItem;

/**
 * Listener for real-time notification feed events delivered via WebSocket.
 * <p>
 * This listener is independent from {@link MessageListener}, {@link GroupListener},
 * and {@link CallListener}. It only receives events where the WebSocket message
 * has {@code type == "notification_feed_item"} and {@code body.action == "sent"}.
 * <p>
 * Register with {@link CometChat#addNotificationFeedListener(String, NotificationFeedListener)}
 * and unregister with {@link CometChat#removeNotificationFeedListener(String)}.
 *
 * @since v4
 */
public abstract class NotificationFeedListener {

    /**
     * Called when a new notification feed item is received in real-time via WebSocket.
     *
     * @param feedItem The newly received NotificationFeedItem
     */
    public abstract void onFeedItemReceived(NotificationFeedItem feedItem);
}
