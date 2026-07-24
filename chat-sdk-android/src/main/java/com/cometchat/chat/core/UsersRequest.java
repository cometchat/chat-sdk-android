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
 * UsersRequest class helps developer to fetch list of users based on different parameters set by developer
 */
public class UsersRequest {

    public static final String USER_STATUS_ONLINE = "available";
    public static final String USER_STATUS_OFFLINE = "offline";

    @StringDef({USER_STATUS_ONLINE, USER_STATUS_OFFLINE})
    @Retention(RetentionPolicy.SOURCE)

    public @interface UserStatus {

    }

    private static final String TAG = UsersRequest.class.getSimpleName();

    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 30;
    private static final int FIRST_PAGE = 1;
    private static final int PAGE_OFFSET = 1;
    private int limit = DEFAULT_LIMIT;
    private long token = 0L;
    private String searchKeyword;
    private boolean hideBlockedUsers = false;
    private String role;
    private boolean friendsOnly = false;
    private List<String> roles;
    private List<String> tags;
    private boolean withTags = false;
    @UserStatus
    private String userStatus = null;
    private int nextPage = -1;
    private int totalPages = -1;
    private int currentPage = -1;
    private boolean inProgress = false;
    private List<String> uids = null;
    private List<String> searchInFields;
    private String sortBy;
    private String sortOrder;

    private UsersRequest(UsersRequestBuilder builder) {
        this.limit = builder.limit;
        this.searchKeyword = builder.searchKeyword;
        this.hideBlockedUsers = builder.hideBlockedUsers;
        this.userStatus = builder.userStatus;
        this.role = builder.role;
        this.roles = builder.roles;
        this.friendsOnly = builder.friendsOnly;
        if (role != null) {
            if (roles == null)
                roles = new ArrayList<>();
            roles.add(role);
        }
        this.tags = builder.tags;
        this.withTags = builder.withTags;
        this.uids = builder.uids;
        this.searchInFields = builder.searchInFields;
        this.sortBy = builder.sortBy;
        this.sortOrder = builder.sortOrder;
        this.currentPage = builder.page;
        this.nextPage = builder.page;
        this.totalPages = builder.page;
    }

    /**
     * Get list of next set of users based on the parameters specified in <code>UsersRequestBuilder</code> class
     * The Developer need to call this method repeatedly using the same object of <code>UsersRequest</code> class to get paginated list of users
     *
     * @param listener listener An object of the <code>CallbackListener&lt;List&lt;User&gt;&gt;</code> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @see UsersRequestBuilder
     * @since <b>v1</b>
     */
    public void fetchNext(final CometChat.CallbackListener<List<User>> listener) {

        if (limit <= 0) {
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
        } else if (limit > MAX_LIMIT) {
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
                ApiConnection.getInstance().getUsers(limit, token, nextPage, searchKeyword, userStatus, hideBlockedUsers, roles, friendsOnly, tags, withTags, uids, searchInFields, sortBy, sortOrder, new ApiConnection.APIConnectionListener() {
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
                                final List<User> users = User.listFromJsonArray(response);
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inProgress = false;
                                        listener.onSuccess(users);
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
     * Gets the limit on the number of users to be fetched.
     * The default value is {@value #DEFAULT_LIMIT} and the maximum allowed value is {@value #MAX_LIMIT}.
     *
     * @return The limit as an {@code int}.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets the search keyword used to filter the users list.
     * This keyword is used to search for users based on their name or UID.
     *
     * @return The search keyword as a {@code String}.
     */
    public String getSearchKeyword() {
        return searchKeyword;
    }

    /**
     * Determines whether blocked users are excluded from the users list.
     *
     * @return {@code true} if blocked users are hidden; {@code false} otherwise.
     */
    public boolean isHideBlockedUsers() {
        return hideBlockedUsers;
    }

    /**
     * Gets the status filter used to fetch users based on their online or offline status.
     * The possible values can be {@code UsersRequest.USER_STATUS_ONLINE} or {@code UsersRequest.USER_STATUS_OFFLINE}.
     *
     * @return The user status filter as a {@code String}.
     */
    public String getUserStatus() {
        return userStatus;
    }

    /**
     * Gets the user role filter used to fetch users based on their role.
     * This method is deprecated in favor of using the roles filter.
     *
     * @return The user role as a {@code String}.
     * @deprecated Use {@link #getRoles()} instead.
     */
    @Deprecated
    public String getRole() {
        return role;
    }

    /**
     * Determines whether only friends are included in the users list.
     *
     * @return {@code true} if only friends are included; {@code false} otherwise.
     */
    public boolean isFriendsOnly() {
        return friendsOnly;
    }

    /**
     * Gets the list of roles used to filter users.
     * Users will be fetched based on the specified roles.
     *
     * @return The list of roles as a {@code List<String>}.
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Gets the list of tags used to filter users.
     * Users will be fetched based on the specified tags.
     *
     * @return The list of tags as a {@code List<String>}.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Determines whether the tags are included in the users data when fetched.
     *
     * @return {@code true} if the tags are included; {@code false} otherwise.
     */
    public boolean isWithTags() {
        return withTags;
    }

    /**
     * Gets the list of UIDs used to fetch specific users.
     *
     * @return The list of UIDs as a {@code List<String>}.
     */
    public List<String> getUIDs() {
        return uids;
    }

    /**
     * Gets the search keyword used to filter in the user data.
     * Possible fields are "name" and "uid".
     *
     * @return The list of string as a {@code List<String>}.
     */
    public List<String> getSearchIn() {
        return searchInFields;
    }

    /**
     * Gets the field by which the user list is sorted.
     * Possible values are "status" or "name".
     *
     * @return The sort field as a {@code String}.
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Gets the order in which the user list is sorted.
     * Possible values are "asc" for ascending order and "desc" for descending order.
     *
     * @return The sort order as a {@code String}.
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Gets the current page number for pagination of users.
     * This represents the page number that was last fetched or is currently being processed.
     *
     * @return The current page number as an {@code int}.
     */
    public int getPage() {
        return currentPage;
    }

    /**
     * Builder class to set various parameters to fetch list of Users
     */
    public static class UsersRequestBuilder {
        int limit = DEFAULT_LIMIT;
        String searchKeyword;
        private boolean hideBlockedUsers = false;
        private String role;
        private List<String> roles;
        @UserStatus
        private String userStatus = null;
        private boolean friendsOnly = false;
        private List<String> tags;
        private boolean withTags = false;
        private List<String> uids = null;
        private List<String> searchInFields;
        private String sortBy;
        private String sortOrder;
        private int page;

        /**
         * A method to set limit
         * if default value in the builder is {@value #DEFAULT_LIMIT} and max value is {@value #MAX_LIMIT}
         *
         * @param limit Integer value specified by the Developer
         * @return UsersRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public UsersRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }


        /**
         * A method set Search user with specified name or <code>UID</code> while fetching the messages
         *
         * @param searchKeyword keyword which developer wants to search in the User list
         * @return UsersRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public UsersRequestBuilder setSearchKeyword(String searchKeyword) {
            this.searchKeyword = searchKeyword;
            return this;
        }

        /**
         * Method to set parameters to hide or show message from blocked users while fetching
         *
         * @param hideBlockedUsers boolean parameter to consider or avoid blocked users while fetching list of users using <code>UsersRequest</code>
         * @return UsersRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public UsersRequestBuilder hideBlockedUsers(boolean hideBlockedUsers) {
            this.hideBlockedUsers = hideBlockedUsers;
            return this;
        }

        /**
         * method to get list of users based on user status
         *
         * @param userStatus parameter to set status <code>UsersRequest.USER_STATUS_ONLINE<code/> or <code>UsersRequest.USER_STATUS_OFFLINE<code/>
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        public UsersRequestBuilder setUserStatus(String userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        /**
         * method to get list of users based on user role
         *
         * @param role parameter to set role while fetching the list of users
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        @Deprecated
        public UsersRequestBuilder setRole(String role) {
            this.role = role;
            return this;
        }

        /**
         * method to get friends for the logged in user
         *
         * @param friendsOnly parameter if set to true will return only friends and not all the users for the app.
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        public UsersRequestBuilder friendsOnly(boolean friendsOnly) {
            this.friendsOnly = friendsOnly;
            return this;
        }

        /**
         * method to get list of users based on multiple roles
         *
         * @param roles list of roles for which the user list is to be retrieved.
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        public UsersRequestBuilder setRoles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        /**
         * method to get list of Users that are tagged with the specified tags
         *
         * @param tags list of tags for which the user list is to be retrieved.
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        public UsersRequestBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * method to get list of Users along with the tags specified
         *
         * @param withTags boolean to decide if the tags are to be sent along with the users data.
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        public UsersRequestBuilder withTags(boolean withTags) {
            this.withTags = withTags;
            return this;
        }

        /**
         * method to get list of Users for the specified UIDs
         *
         * @param uids The list of UIDs for which the users are to be fetched.
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        public UsersRequestBuilder setUIDs(List<String> uids) {
            this.uids = uids;
            return this;
        }

        /**
         * method to specify the fields the searchKeyword needs to be searched in
         *
         * @param searchInFields The list of the fields in the User class that the searchKeyword needs to be searched in.
         *                       The possible values are
         *                       1. name
         *                       2. uid
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        public UsersRequestBuilder searchIn(List<String> searchInFields) {
            this.searchInFields = searchInFields;
            return this;
        }

        /**
         * method to specify the field based on which the user list needs to be sorted by
         *
         * @param sortBy The field of the user class based on which the user list should be sorted
         *               The possible values are
         *               1. status - The order of the sorting will be [status to name to uid]
         *               2. name - The order of the sorting will be [name to uid]
         * @return UsersRequestBuilder object when <build()> is called
         */
        public UsersRequestBuilder sortBy(@CometChatConstants.SortBy String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        /**
         * method to determine if the user list should be sorted in ascending or descending order
         *
         * @param sortOrder The order in which the User List needs to be sorted
         *                  The possible values are :
         *                  1. asc - The list will be sorted in ascending order.
         *                  2. desc - The list will be sorted in descending order.
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        public UsersRequestBuilder sortByOrder(@CometChatConstants.SortOrder String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        /**
         * A method to set the page number for pagination of users.
         *
         * @since <b>v4</b>
         * @param page Integer value specifying the page number to fetch. Used for pagination when fetching users.
         * @return UsersRequestBuilder object when <code><build()code/> is called
         */
        public UsersRequestBuilder setPage(int page) {
            this.page = page + PAGE_OFFSET;
            return this;
        }

        public UsersRequest build() {

            return new UsersRequest(this);
        }
    }
}
