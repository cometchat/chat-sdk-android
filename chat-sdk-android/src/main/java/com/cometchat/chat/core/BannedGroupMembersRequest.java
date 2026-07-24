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
 * BannedGroupMembersRequest class helps developer to fetch list of banned group members for a particular group based on different parameters set by developer
 * Created by adityagokula on 14/11/18.
 */

public class BannedGroupMembersRequest {

    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 30;
    private static final int PAGE_OFFSET = 1;
    private String guid;
    private int limit = DEFAULT_LIMIT;
    private int currentPage = 0;
    private int totalPages = 0;
    private int nextPage = 0;
    private long cursor;
    private String searchKeyword;
    private List<String> scopes;

    BannedGroupMembersRequest(BannedGroupMembersRequestBuilder builder) {
        this.guid = builder.guid;
        this.limit = builder.limit;
        this.searchKeyword = builder.searchKeyword;
        this.scopes = builder.scopes;
        this.currentPage = builder.nextPage;
        this.nextPage = builder.nextPage;
        this.totalPages = builder.nextPage;
    }

    /**
     * Get list of next set of banned group members based on the parameters specified in <code>BannedGroupMembersRequestBuilder</code> class
     * The Developer need to call this method repeatedly using the same object of <code>BannedGroupMembersRequest</code> class to get paginated list of the banned group members
     *
     * @param listener listener An object of the <code>CallbackListener&lt;List&lt;GroupMember&gt;&gt;</code> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @see BannedGroupMembersRequestBuilder
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
                    ApiConnection.getInstance().getBannedMembers(guid, limit, cursor, nextPage, searchKeyword,scopes, new ApiConnection.APIConnectionListener() {
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
     * Get the unique identifier of the group for which banned members are requested.
     *
     * @return The group ID.
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Get the maximum number of banned group members to fetch in a single operation.
     *
     * @return The limit for the number of banned group members to fetch.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Get the search keyword used to filter banned group members.
     *
     * @return The search keyword.
     */
    public String getSearchKeyword() {
        return searchKeyword;
    }

    /**
     * Gets the list of scopes used to filter banned group members based on specific criteria.
     * The scopes define the categories or roles of the group members to be fetched.
     *
     * @return The list of scopes as a {@code List<String>}.
     */
    public List<String> getScopes() {
        return scopes;
    }

    /**
     * Get the current page number for pagination of banned group members.
     * This represents the page number that was last fetched or is currently being processed.
     *
     * @return The current page number as an integer.
     */
    public int getPage() {
        return currentPage;
    }

    public static class BannedGroupMembersRequestBuilder {
        int limit;
        String guid;
        private String searchKeyword;
        private List<String> scopes;
        private int nextPage;

        public BannedGroupMembersRequestBuilder(String guid) {
            this.guid = guid;
        }

        /**
         * A method that helps you fetch banned memebers of any group based on the scope of the members
         *
         * @param scopes List of scopes based on which the banned group members are to be fetched.
         * @return GroupMembersRequestBuilder object when <code>build()</code> is called
         */
        public BannedGroupMembersRequest.BannedGroupMembersRequestBuilder setScopes(List<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        /**
         * Set the unique identifier of the group.
         *
         * @param guid The group ID to set.
         * @return GroupMembersRequestBuilder object when <code>build()</code> is called
         */
        public BannedGroupMembersRequest.BannedGroupMembersRequestBuilder setGuid(String guid) {
            this.guid = guid;
            return this;
        }

        /**
         * method to set the limit to specify the nymber of banned group members to be fetched in a single operation
         *
         * @param limit integer value to specify the number of banned group members to be fetched in a single operation. The maximum value can be 100.
         * @return BannedGroupMembersRequestBuilder object when <code>build()</code> is called
         */
        public BannedGroupMembersRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * method to set search keyword to determine that the group members fetched contain the specified keyword
         *
         * @param searchKeyword String that determines that the group members fetched need to contain this text.
         * @return BannedGroupMembersRequestBuilder object when <code>build()</code> is called
         */
        public BannedGroupMembersRequestBuilder setSearchKeyword(String searchKeyword) {
            this.searchKeyword = searchKeyword;
            return this;
        }

        /**
         * A method to set the page number for pagination of banned group members
         *
         * @since <b>v4</b>
         * @param nextPage Integer value specifying the page number to fetch. Used for pagination when fetching banned group members.
         * @return BannedGroupMembersRequestBuilder object when <code>build()</code> is called
         */
        public BannedGroupMembersRequestBuilder setPage(int nextPage) {
            this.nextPage = nextPage + PAGE_OFFSET;
            return this;
        }

        public BannedGroupMembersRequest build() {

            return new BannedGroupMembersRequest(this);
        }
    }
}
