package com.cometchat.chat.core;

import androidx.annotation.StringDef;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * BlockedUsersRequest class helps developer to fetch list of users blocked by the logged in user based on different parameters set by developer
 *
 * Created by adityagokula on 20/05/19.
 */

public class BlockedUsersRequest {

    public static final String DIRECTION_BLOCKED_BY_ME = "blockedByMe";
    public static final String DIRECTION_HAS_BLOCKED_ME = "hasBlockedMe";
    public static final String DIRECTION_BOTH = "both";

    @StringDef({DIRECTION_BLOCKED_BY_ME, DIRECTION_HAS_BLOCKED_ME, DIRECTION_BOTH})
    @Retention(RetentionPolicy.SOURCE)

    public @interface Direction {

    }


    private static final String TAG = BlockedUsersRequest.class.getSimpleName();
    private static final int FIRST_PAGE = 1;
    private static final int PAGE_OFFSET = 1;
    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 30;

    private int limit = DEFAULT_LIMIT;
    private long token = 0L;
    private String searchKeyword;
    private int nextPage = -1;
    private int totalPages = -1;
    private int currentPage = -1;
    private boolean inProgress = false;
    @Direction
    private String direction = DIRECTION_BOTH;

    private BlockedUsersRequest(BlockedUsersRequest.BlockedUsersRequestBuilder builder) {
        this.limit = builder.limit;
        this.searchKeyword = builder.searchKeyword;
        this.direction = builder.direction;
        this.currentPage = builder.page;
        this.nextPage = builder.page;
        this.totalPages = builder.page;
    }

    public void fetchNext(final CometChat.CallbackListener<List<User>> listener) {
        if (limit > MAX_LIMIT) {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED, String.format(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE, MAX_LIMIT)));
                        }
                    });
                }
            });
        } else {
            if (nextPage <= totalPages && !inProgress) {
                inProgress = true;
                ApiConnection.getInstance().getBlockedUsers(limit, token, nextPage, searchKeyword,direction, new ApiConnection.APIConnectionListener() {
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
                                if (jsonObject.has(CometChatConstants.PaginationKeys.KEY_META)) {
                                    if (jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).has(CometChatConstants.PaginationKeys.KEY_PAGINATION)) {
                                        if (jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getJSONObject(CometChatConstants.PaginationKeys.KEY_PAGINATION).has(CometChatConstants.PaginationKeys.KEY_PAGINATION_TOTAL_PAGES))
                                            totalPages = jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getJSONObject(CometChatConstants.PaginationKeys.KEY_PAGINATION).getInt(CometChatConstants.PaginationKeys.KEY_PAGINATION_TOTAL_PAGES);
                                        if (jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getJSONObject(CometChatConstants.PaginationKeys.KEY_PAGINATION).has(CometChatConstants.PaginationKeys.KEY_PAGINATION_CURRENT_PAGE)) {
                                            currentPage = jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getJSONObject(CometChatConstants.PaginationKeys.KEY_PAGINATION).getInt(CometChatConstants.PaginationKeys.KEY_PAGINATION_CURRENT_PAGE);
                                            nextPage = currentPage + PAGE_OFFSET;
                                        }
                                    } else if (jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).has(CometChatConstants.PaginationKeys.KEY_CURSOR)) {
                                        token = jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getLong(CometChatConstants.PaginationKeys.KEY_CURSOR);
                                    }
                                }
                                final List<User> finalUserList = User.listFromJsonArray(response);
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inProgress = false;
                                        listener.onSuccess(finalUserList);
                                    }
                                });

                            } catch (final JSONException je) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inProgress = false;
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()));
                                    }
                                });
                            }

                        }
                    }
                });
            } else {
                CometChat.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(new ArrayList<User>());
                    }
                });
            }
        }
    }


    /**
     * Gets the limit on the number of blocked users to be fetched in a single operation.
     * This limit is the maximum number of blocked users that the request will return.
     *
     * @return The limit as an {@code int}.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets the search keyword used to filter the blocked users by name or UID.
     * Only blocked users whose names or UIDs contain this keyword will be fetched.
     *
     * @return The search keyword as a {@code String}.
     */
    public String getSearchKeyword() {
        return searchKeyword;
    }

    /**
     * Gets the direction type to determine the nature of the blocked users to be fetched.
     * The direction specifies whether to fetch users who are blocked by the logged-in user,
     * users who have blocked the logged-in user, or both.
     *
     * @return The direction type as a {@code String}. It can be one of "blocked_by_me", "blocked_me", or "both".
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Gets the current page number for pagination of blocked users.
     * This represents the page number that was last fetched or is currently being processed.
     *
     * @return The current page number as an {@code int}.
     */
    public int getPage() {
        return currentPage;
    }

    /**
     *  Builder class to set various parameters to fetch list blocked users
     *
     */
    public static class BlockedUsersRequestBuilder {
        int limit = DEFAULT_LIMIT;
        String searchKeyword;
        @Direction
        private String direction = DIRECTION_BOTH;
        private int page;

        /**
         *  A method to set limit. This determines the number of blocked users fetched in a single operation
         *  if default value in the builder is {@value #DEFAULT_LIMIT} and max value is {@value #MAX_LIMIT}
         *
         * @version <b>v2</b>
         * @since   <b>v1</b>
         * @param limit Integer value specified by the Developer
         * @return BlockedUsersRequestBuilder object when <code>build()</code> is called
         *
         */
        public BlockedUsersRequest.BlockedUsersRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         *  A method set Search user with specified name or <code>UID</code> while fetching the messages
         *
         * @version <b>v2</b>
         * @since   <b>v1</b>
         * @param searchKeyword keyword which developer wants to search in the group member list
         * @return BlockedUsersRequestBuilder object when <code>build()</code> is called
         */
        public BlockedUsersRequest.BlockedUsersRequestBuilder setSearchKeyword(String searchKeyword) {
            this.searchKeyword = searchKeyword;
            return this;
        }
        /**
         *  A method that determines the nature of the blocked users to be fetched. This could be
         *  1. users blocked by the logged in user
         *  2. users that have blocked the logged in user
         *  3. both
         *
         * @version <b>v2</b>
         * @since   <b>v1</b>
         * @param direction The type of blocked users to be fetched.
         * @return BlockedUsersRequestBuilder object when <code>build()</code> is called
         */
        public BlockedUsersRequestBuilder setDirection(@Direction String direction){
            this.direction = direction;
            return this;
        }

        /**
         * A method to set the page number for pagination of blocked users.
         *
         * @since <b>v4</b>
         * @param page Integer value specifying the page number to fetch. Used for pagination when fetching blocked users.
         * @return BlockedUsersRequestBuilder object when <code>build()</code> is called
         */
        public BlockedUsersRequestBuilder setPage(int page) {
            this.page = page + PAGE_OFFSET;
            return this;
        }

        public BlockedUsersRequest build() {

            return new BlockedUsersRequest(this);
        }
    }
}
