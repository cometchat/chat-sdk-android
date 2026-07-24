package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.enums.FeedReadState;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.NotificationFeedItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationFeedRequest class helps developers fetch a paginated list of notification feed items
 * with optional filters for read state, category, channel, tags, and date range.
 * <p>
 * Uses cursor-based pagination. When the server returns no cursor, subsequent
 * {@code fetchNext()} calls return an empty list without making a network request.
 *
 * @since v4
 */
public class NotificationFeedRequest {

    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 20;

    private int limit;
    private String cursor;
    private boolean cursorExhausted;
    private FeedReadState readState;
    private String category;
    private String channelId;
    private List<String> tags;
    private String dateFrom;
    private String dateTo;
    private boolean inProgress;
    
    // Pagination via meta.next object
    private String nextAffix;
    private Long nextSentAt;
    private String nextId;

    private NotificationFeedRequest(NotificationFeedRequestBuilder builder) {
        this.limit = builder.limit;
        this.readState = builder.readState;
        this.category = builder.category;
        this.channelId = builder.channelId;
        this.tags = builder.tags;
        this.dateFrom = builder.dateFrom;
        this.dateTo = builder.dateTo;
        this.cursor = null;
        this.cursorExhausted = false;
        this.inProgress = false;
    }

    /**
     * Fetches the next page of notification feed items based on the configured parameters.
     * Manages cursor internally. When the server returns no cursor, subsequent calls
     * return an empty list without making a network request.
     *
     * @param listener Callback listener for success (List of NotificationFeedItem) or error
     * @since v4
     */
    public void fetchNext(final CometChat.CallbackListener<List<NotificationFeedItem>> listener) {
        if (limit <= 0) {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(
                            CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT,
                            CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT_MESSSAGE));
                }
            });
            return;
        }

        if (limit > MAX_LIMIT) {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(
                            CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED,
                            String.format(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE, MAX_LIMIT)));
                }
            });
            return;
        }

        // If cursor is exhausted, return empty list
        if (cursorExhausted) {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess(new ArrayList<NotificationFeedItem>());
                }
            });
            return;
        }

        // Prevent concurrent calls
        if (inProgress) {
            return;
        }

        inProgress = true;

        ApiConnection.getInstance().getNotificationFeed(
                limit, cursor, nextAffix, nextSentAt, nextId, readState, category, channelId, tags, dateFrom, dateTo,
                new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        if (ce != null) {
                            CometChat.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    inProgress = false;
                                    listener.onError(ce);
                                }
                            });
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                // Extract cursor from meta
                                if (jsonObject.has(CometChatConstants.PaginationKeys.KEY_META)) {
                                    JSONObject meta = jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META);
                                    if (meta.has("next") && !meta.isNull("next")) {
                                        JSONObject next = meta.getJSONObject("next");
                                        nextAffix = next.optString("affix", null);
                                        nextSentAt = next.has("sentAt") ? next.getLong("sentAt") : null;
                                        nextId = next.optString("id", null);
                                        cursor = null; // Not using cursor string
                                        cursorExhausted = (nextSentAt == null && nextId == null);
                                    } else if (meta.has(CometChatConstants.NotificationFeedKeys.KEY_CURSOR)
                                            && !meta.isNull(CometChatConstants.NotificationFeedKeys.KEY_CURSOR)) {
                                        cursor = meta.getString(CometChatConstants.NotificationFeedKeys.KEY_CURSOR);
                                        nextAffix = null;
                                        nextSentAt = null;
                                        nextId = null;
                                    } else {
                                        cursor = null;
                                        nextAffix = null;
                                        nextSentAt = null;
                                        nextId = null;
                                        cursorExhausted = true;
                                    }
                                } else {
                                    cursor = null;
                                    nextAffix = null;
                                    nextSentAt = null;
                                    nextId = null;
                                    cursorExhausted = true;
                                }

                                final List<NotificationFeedItem> items = NotificationFeedItem.listFromJson(jsonObject);

                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inProgress = false;
                                        listener.onSuccess(items);
                                    }
                                });

                            } catch (final JSONException je) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inProgress = false;
                                        listener.onError(new CometChatException(
                                                CometChatConstants.Errors.ERROR_JSON_EXCEPTION,
                                                je.getMessage()));
                                    }
                                });
                            }
                        }
                    }
                });
    }

    // region Getters

    public int getLimit() {
        return limit;
    }

    public FeedReadState getReadState() {
        return readState;
    }

    public String getCategory() {
        return category;
    }

    public String getChannelId() {
        return channelId;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    // endregion

    /**
     * Builder class for constructing {@link NotificationFeedRequest} instances.
     * Follows the existing CometChat SDK builder pattern.
     *
     * @since v4
     */
    public static class NotificationFeedRequestBuilder {

        private int limit = DEFAULT_LIMIT;
        private FeedReadState readState = FeedReadState.ALL;
        private String category = null;
        private String channelId = null;
        private List<String> tags = null;
        private String dateFrom = null;
        private String dateTo = null;

        /**
         * Set the maximum number of items to fetch per page.
         * Default: 20. Maximum: 100.
         *
         * @param limit Number of items per page
         * @return This builder for chaining
         */
        public NotificationFeedRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Filter by read state.
         *
         * @param readState READ, UNREAD, or ALL
         * @return This builder for chaining
         */
        public NotificationFeedRequestBuilder setReadState(FeedReadState readState) {
            this.readState = readState;
            return this;
        }

        /**
         * Filter by notification category.
         *
         * @param category Category identifier (from NotificationCategory.getId())
         * @return This builder for chaining
         */
        public NotificationFeedRequestBuilder setCategory(String category) {
            this.category = category;
            return this;
        }

        /**
         * Filter by channel ID.
         *
         * @param channelId Channel identifier
         * @return This builder for chaining
         */
        public NotificationFeedRequestBuilder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        /**
         * Filter by tags.
         *
         * @param tags List of tag strings
         * @return This builder for chaining
         */
        public NotificationFeedRequestBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Filter items sent on or after this date.
         *
         * @param dateFrom ISO 8601 date string
         * @return This builder for chaining
         */
        public NotificationFeedRequestBuilder setDateFrom(String dateFrom) {
            this.dateFrom = dateFrom;
            return this;
        }

        /**
         * Filter items sent on or before this date.
         *
         * @param dateTo ISO 8601 date string
         * @return This builder for chaining
         */
        public NotificationFeedRequestBuilder setDateTo(String dateTo) {
            this.dateTo = dateTo;
            return this;
        }

        /**
         * Build the NotificationFeedRequest instance.
         *
         * @return A configured NotificationFeedRequest
         */
        public NotificationFeedRequest build() {
            return new NotificationFeedRequest(this);
        }
    }
}
