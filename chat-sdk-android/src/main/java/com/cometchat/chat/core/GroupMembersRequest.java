package com.cometchat.chat.core;

import android.text.TextUtils;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.GroupMember;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * GroupMembersRequest class to get list of members of a particular Group
 */
public class GroupMembersRequest {

    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 30;
    private static final int FIRST_PAGE = 1;
    private static final int PAGE_OFFSET = 1;
    private String guid;
    private int limit = DEFAULT_LIMIT;
    private int currentPage = 0;
    private int totalPages = 0;
    private int nextPage = 0;
    private long cursor;
    private String searchKeyword;
    private List<String> scopes;

    private final @UsersRequest.UserStatus String status;

    GroupMembersRequest(GroupMembersRequestBuilder builder) {
        this.guid = builder.guid;
        this.limit = builder.limit;
        this.searchKeyword = builder.searchKeyword;
        this.scopes = builder.scopes;
        this.currentPage = builder.page;
        this.nextPage = builder.page;
        this.totalPages = builder.page;
        this.status = builder.status;
    }

    /**
     * Get list of next set of group members based on the parameters specified in <code>GroupMembersRequestBuilder</code> class
     * The Developer need to call this method repeatedly using the same object of <code>GroupMembersRequest</code> class to get paginated list of members
     *
     * @param listener listener An object of the <code>CallbackListener&lt;List&lt;GroupMember&gt;&gt;</code> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @see GroupMembersRequestBuilder
     * @since <b>v1</b>
     */
    public void fetchNext(final CometChat.CallbackListener<List<GroupMember>> listener) {
        if (guid != null && !TextUtils.isEmpty(guid)) {
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
                if (nextPage <= totalPages) {
                    ApiConnection.getInstance().getGroupMembers(guid, limit, cursor, nextPage, searchKeyword, scopes, status, new ApiConnection.APIConnectionListener() {
                        @Override
                        public void onResponse(String response, final CometChatException ce) {
                            if (ce != null) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
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
                                    final List<GroupMember> members = GroupMember.listFromJSONArray(response);
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onSuccess(members);
                                        }
                                    });
                                } catch (final JSONException je) {
                                    CometChat.postOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
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
                            listener.onSuccess(new ArrayList<GroupMember>());
                        }
                    });
                }

            }
        } else {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID, CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE));
                }
            });
        }
    }

    /**
     * Get the unique identifier of the group for which group members are requested.
     *
     * @return The group ID.
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Gets the limit on the number of group members to be fetched in a single operation.
     * The limit determines the maximum number of group members returned by the request.
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
     * Gets the search keyword used to filter the group members by name or UID.
     * Only group members whose names or UIDs contain this keyword will be fetched.
     *
     * @return The search keyword as a {@code String}.
     */
    public String getSearchKeyword() {
        return searchKeyword;
    }

    /**
     * Gets the list of scopes used to filter group members based on specific criteria.
     * The scopes define the categories or roles of the group members to be fetched.
     *
     * @return The list of scopes as a {@code List<String>}.
     */
    public List<String> getScopes() {
        return scopes;
    }

    /**
     * Gets the current page number for pagination of group members.
     * This represents the page number that was last fetched or is currently being processed.
     *
     * @return The current page number as an {@code int}.
     */
    public int getPage() {
        return currentPage;
    }

    /**
     * Gets the status filter used to fetch group members based on their online/offline status.
     * This determines whether to fetch online members, offline members, or all members regardless of status.
     *
     * @return The status filter as a {@code String} annotated with {@code @UsersRequest.UserStatus}.
     */
    public @UsersRequest.UserStatus String getStatus() {
        return status;
    }

    /**
     * Builder class to set various parameters to fetch list of Group Members
     */
    public static class GroupMembersRequestBuilder {
        int limit;
        String guid;
        private String searchKeyword;
        private List<String> scopes;
        private int page;
        private @UsersRequest.UserStatus String status;

        public GroupMembersRequestBuilder(String guid) {
            this.guid = guid;
        }

        /**
         * Set the unique identifier of the group.
         *
         * @param guid The group ID to set.
         * @return GroupMembersRequestBuilder object when <code>build()</code> is called
         */
        public GroupMembersRequest.GroupMembersRequestBuilder setGuid(String guid) {
            this.guid = guid;
            return this;
        }

        /**
         * A method to set limit
         * if default value in the builder is {@value #DEFAULT_LIMIT} and max value is {@value #MAX_LIMIT}
         *
         * @param limit Integer value specified by the Developer
         * @return GroupMembersRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public GroupMembersRequest.GroupMembersRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * A method set Search user with specified name or <code>UID</code> while fetching the messages
         *
         * @param searchKeyword keyword which developer wants to search in the group member list
         * @return GroupMembersRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public GroupMembersRequestBuilder setSearchKeyword(String searchKeyword) {
            this.searchKeyword = searchKeyword;
            return this;
        }

        /**
         * A method that helps you fetch memebers of any group based on the scope of the members
         *
         * @param scopes List of scopes based on which the group members are to be fetched.
         * @return GroupMembersRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public GroupMembersRequestBuilder setScopes(List<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        /**
         * Sets the status filter to fetch group members based on their online/offline status.
         * This method allows filtering group members by their current status.
         *
         * @param status The status filter to apply. Use {@code UsersRequest.USER_STATUS_ONLINE} to fetch only online members,
         *               {@code UsersRequest.USER_STATUS_OFFLINE} to fetch only offline members, or leave unset to fetch all members.
         * @return GroupMembersRequestBuilder object when {@code build()} is called
         * @since <b>v4</b>
         */
        public GroupMembersRequestBuilder setStatus(@UsersRequest.UserStatus String status) {
            this.status = status;
            return this;
        }

        /**
         * A method to set the page number for pagination of group members.
         *
         * @param page Integer value specifying the page number to fetch. Used for pagination when fetching group members.
         * @return GroupMembersRequestBuilder object when <code>build()</code> is called
         * @since <b>v4</b>
         */
        public GroupMembersRequestBuilder setPage(int page) {
            this.page = page + PAGE_OFFSET;
            return this;
        }

        public GroupMembersRequest build() {

            return new GroupMembersRequest(this);
        }
    }

}
