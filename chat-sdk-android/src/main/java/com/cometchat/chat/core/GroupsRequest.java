package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Group;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * GroupsRequest class helps developer to fetch list of groups based on different parameters set by developer
 */
public class GroupsRequest {

    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 30;
    private static final int FIRST_PAGE = 1;
    private static final int PAGE_OFFSET = 1;
    private int limit = DEFAULT_LIMIT;
    private long cursor;
    private String searchKeyword;
    private int nextPage = 0;
    private int totalPages = 0;
    private int currentPage = 0;
    private boolean inProgress = false;
    private boolean joinedOnly = false;
    private List<String> tags;
    private boolean withTags = false;

    GroupsRequest(GroupsRequest.GroupsRequestBuilder builder) {
        this.limit = builder.limit;
        this.searchKeyword = builder.searchKeyWord;
        this.joinedOnly = builder.joinedOnly;
        this.tags = builder.tags;
        this.withTags = builder.withTags;
        this.currentPage = builder.page;
        this.nextPage = builder.page;
        this.totalPages = builder.page;
    }

    /**
     * Get list of next set of groups based on the parameters specified in <code>GroupsRequestBuilder</code> class
     * The Developer need to call this method repeatedly using the same object of <code>GroupsRequest</code> class to get paginated list of groups
     *
     * @param listener listener An object of the <code>CallbackListener&lt;List&lt;Group&gt;&gt;</code> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @see GroupsRequestBuilder
     * @since <b>v1</b>
     */
    public void fetchNext(final CometChat.CallbackListener<List<Group>> listener) {
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
        } else if (limit <= 0) {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    CometChat.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT, CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT_MESSSAGE));
                        }
                    });
                }
            });
        } else {
            if (nextPage <= totalPages && !inProgress) {
                inProgress = true;
                ApiConnection.getInstance().getGroups(limit, cursor, nextPage, searchKeyword, joinedOnly, tags, withTags, new ApiConnection.APIConnectionListener() {
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
                                        cursor = jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getLong(CometChatConstants.PaginationKeys.KEY_CURSOR);
                                    }
                                }
                                final List<Group> groups = Group.fromJSONArray(response);
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inProgress = false;
                                        listener.onSuccess(groups);
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
                        listener.onSuccess(new ArrayList<Group>());
                    }
                });
            }

        }
    }

    /**
     * Gets the limit on the number of groups to be fetched in a single operation.
     * The limit determines the maximum number of groups returned by the request.
     * <p>
     * The default value for the limit is {@value #DEFAULT_LIMIT}, and the maximum allowed value is {@value #MAX_LIMIT}.
     * </p>
     *
     * @return The limit as an {@code int}.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets the search keyword used to filter the groups by their name or GUID.
     * Only groups whose names or GUIDs contain this keyword will be fetched.
     *
     * @return The search keyword as a {@code String}.
     */
    public String getSearchKeyword() {
        return searchKeyword;
    }

    /**
     * Gets the flag indicating whether only the groups the user has joined should be fetched.
     *
     * @return {@code true} if only joined groups should be fetched, {@code false} otherwise.
     */
    public boolean isJoinedOnly() {
        return joinedOnly;
    }

    /**
     * Gets the list of tags used to filter the groups.
     * Only groups associated with these tags will be fetched.
     *
     * @return The list of tags as a {@code List<String>}.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Gets the flag indicating whether tags should be included with the groups data.
     *
     * @return {@code true} if tags should be included, {@code false} otherwise.
     */
    public boolean isWithTags() {
        return withTags;
    }

    /**
     * Gets the current page number for pagination of groups.
     * This represents the page number that was last fetched or is currently being processed.
     *
     * @return The current page number as an {@code int}.
     */
    public int getPage() {
        return currentPage;
    }


    /**
     * Builder class to set various parameters to fetch list of Groups
     */
    public static class GroupsRequestBuilder {
        int limit = DEFAULT_LIMIT;
        String searchKeyWord;
        boolean joinedOnly = false;
        List<String> tags;
        boolean withTags = false;
        private int page;

        /**
         * A method to set limit
         * if default value in the builder is {@value #DEFAULT_LIMIT} and max value is {@value #MAX_LIMIT}
         *
         * @param limit Integer value specified by the Developer
         * @return GroupsRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public GroupsRequest.GroupsRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * A method set Search user with specified name or <code>GUID</code> while fetching the list of Groups
         *
         * @param searchKeyword keyword which developer wants to search in the Group list
         * @return GroupsRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public GroupsRequest.GroupsRequestBuilder setSearchKeyWord(String searchKeyword) {
            this.searchKeyWord = searchKeyword;
            return this;
        }

        /**
         * A method to set parameter to get edited/updated messages
         *
         * @param joinedOnly boolean parameter get joined Groups only
         * @return GroupsRequestBuilder object when <code>build()</code> is called
         */
        public GroupsRequestBuilder joinedOnly(boolean joinedOnly) {
            this.joinedOnly = joinedOnly;
            return this;
        }

        /**
         * A method to set tags based on which the groups are to be fetched
         *
         * @param tags List of tags based on which the groups are to be fetched.
         * @return GroupsRequestBuilder object when <code>build()</code> is called
         */
        public GroupsRequestBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * method to get list of Groups along with the tags specified
         *
         * @param withTags boolean to decide if the tags are to be sent along with the groups data.
         * @return GroupsRequestBuilder object when <code>build()</code> is called
         */
        public GroupsRequestBuilder withTags(boolean withTags) {
            this.withTags = withTags;
            return this;
        }

        /**
         * A method to set the page number for pagination of groups.
         *
         * @since <b>v4</b>
         * @param page Integer value specifying the page number to fetch. Used for pagination when fetching groups.
         * @return GroupsRequestBuilder object when <code>build()</code> is called
         */
        public GroupsRequestBuilder setPage(int page) {
            this.page = page + PAGE_OFFSET;
            return this;
        }

        public GroupsRequest build() {
            return new GroupsRequest(this);
        }
    }
}
