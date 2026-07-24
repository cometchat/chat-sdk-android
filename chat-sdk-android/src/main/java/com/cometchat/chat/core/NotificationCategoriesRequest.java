package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.NotificationCategory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationCategoriesRequest class helps developers fetch a paginated list of
 * notification categories for filter chips in the notification feed UI.
 * <p>
 * Uses cursor-based pagination. When the server returns no cursor, subsequent
 * {@code fetchNext()} calls return an empty list without making a network request.
 *
 * @since v4
 */
public class NotificationCategoriesRequest {

    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 50;

    private int limit;
    private String cursor;
    private boolean cursorExhausted;
    private boolean inProgress;

    private NotificationCategoriesRequest(NotificationCategoriesRequestBuilder builder) {
        this.limit = builder.limit;
        this.cursor = null;
        this.cursorExhausted = false;
        this.inProgress = false;
    }

    /**
     * Fetches the next page of notification categories.
     * Manages cursor internally. When the server returns no cursor, subsequent calls
     * return an empty list without making a network request.
     *
     * @param listener Callback listener for success (List of NotificationCategory) or error
     * @since v4
     */
    public void fetchNext(final CometChat.CallbackListener<List<NotificationCategory>> listener) {
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
                    listener.onSuccess(new ArrayList<NotificationCategory>());
                }
            });
            return;
        }

        // Prevent concurrent calls
        if (inProgress) {
            return;
        }

        inProgress = true;

        ApiConnection.getInstance().getNotificationCategories(
                limit, cursor,
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
                                    if (meta.has(CometChatConstants.NotificationFeedKeys.KEY_CURSOR)
                                            && !meta.isNull(CometChatConstants.NotificationFeedKeys.KEY_CURSOR)) {
                                        cursor = meta.getString(CometChatConstants.NotificationFeedKeys.KEY_CURSOR);
                                    } else {
                                        cursor = null;
                                        cursorExhausted = true;
                                    }
                                } else {
                                    cursor = null;
                                    cursorExhausted = true;
                                }

                                final List<NotificationCategory> categories = NotificationCategory.listFromJson(jsonObject);

                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inProgress = false;
                                        listener.onSuccess(categories);
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

    // endregion

    /**
     * Builder class for constructing {@link NotificationCategoriesRequest} instances.
     *
     * @since v4
     */
    public static class NotificationCategoriesRequestBuilder {

        private int limit = DEFAULT_LIMIT;

        /**
         * Set the maximum number of categories to fetch per page.
         * Default: 50. Maximum: 100.
         *
         * @param limit Number of categories per page
         * @return This builder for chaining
         */
        public NotificationCategoriesRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Build the NotificationCategoriesRequest instance.
         *
         * @return A configured NotificationCategoriesRequest
         */
        public NotificationCategoriesRequest build() {
            return new NotificationCategoriesRequest(this);
        }
    }
}
