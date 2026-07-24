package com.cometchat.chat.enums;

import java.util.Objects;

/**
 * Type-safe enum for filtering notification feed items by read state.
 * Used by {@link com.cometchat.chat.core.NotificationFeedRequest.NotificationFeedRequestBuilder#setReadState}.
 *
 * @since v4
 */
public enum FeedReadState {
    READ("read"),
    UNREAD("unread"),
    ALL("all");

    private final String value;

    FeedReadState(String value) {
        this.value = value;
    }

    /**
     * Get the string value for API serialization.
     *
     * @return The read state string value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the enum constant from a string value.
     *
     * @param value The string value to look up
     * @return The matching FeedReadState, or null if not found
     */
    public static FeedReadState get(String value) {
        for (FeedReadState state : values()) {
            if (Objects.equals(state.getValue(), value)) {
                return state;
            }
        }
        return null;
    }
}
