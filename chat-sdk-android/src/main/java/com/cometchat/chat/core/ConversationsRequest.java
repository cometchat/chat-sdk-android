package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Conversation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ConversationsRequest class helps developer to fetch list of conversations based on different parameters set by developer.
 */
public class ConversationsRequest {


    private static final int MAX_LIMIT = 50;
    private static final int DEFAULT_LIMIT = 30;
    private static final int PAGE_OFFSET = 1;

    private int limit = DEFAULT_LIMIT;
    private String conversationType;
    private boolean withUserAndGroupTags = false;
    private List<String> tags;
    private boolean withTags = false;
    private  String searchKeyword;
    private int nextPage = -1;
    private int totalPages = -1;
    private int currentPage = -1;
    private boolean inProgress = false;
    private List<String> userTags;
    private List<String> groupTags;
    private boolean includeBlockedUsers;
    private boolean withBlockedInfo;
    private boolean unread;
    private boolean hideAgentic;
    private boolean onlyAgentic;

    private ConversationsRequest(ConversationsRequestBuilder builder) {
        this.limit = builder.limit;
        this.conversationType = builder.conversationType;
        this.withUserAndGroupTags = builder.withUserAndGroupTags;
        this.tags = builder.tags;
        this.withTags = builder.withTags;
        this.userTags = builder.userTags;
        this.groupTags = builder.groupTags;
        this.includeBlockedUsers = builder.includeBlockedUsers;
        this.withBlockedInfo = builder.withBlockedInfo;
        this.searchKeyword = builder.searchKeyword;
        this.unread = builder.unread;
        this.currentPage = builder.page;
        this.nextPage = builder.page;
        this.totalPages = builder.page;
        this.hideAgentic = builder.hideAgentic;
        this.onlyAgentic = builder.onlyAgentic;
    }

    /**
     * Get list of next set of conversations based on the parameters specified in <code>ConversationsRequestBuilder</code> class
     * The Developer need to call this method repeatedly using the same object of <code>ConversationsRequest</code> class to get paginated list of conversations
     *
     * @param listener listener An object of the <code>CallbackListener&lt;List&lt;Conversation&gt;&gt;</code> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @see ConversationsRequest.ConversationsRequestBuilder
     * @since <b>v2</b>
     */
    public void fetchNext(final CometChat.CallbackListener<List<Conversation>> listener) {
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
                ApiConnection.getInstance().getConversations(limit, conversationType, withUserAndGroupTags, tags, withTags, nextPage, userTags, groupTags,includeBlockedUsers, withBlockedInfo, searchKeyword,unread,hideAgentic,onlyAgentic, new ApiConnection.APIConnectionListener() {
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
                                    }
                                }
                                // Convert JSON to Conversation objects
                                final List<Conversation> conversations = Conversation.listFromJsonArray(response);
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inProgress = false;
                                        listener.onSuccess(conversations);
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
                        listener.onSuccess(new ArrayList<Conversation>());
                    }
                });
            }

        }
    }

    /**
     * Gets the limit for the number of items to be fetched or processed.
     *
     * @return the limit as an integer.
     * @since <b>v4</b>
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Retrieves the type of the conversation.
     * This can be used to differentiate between different types of conversations, such as user and group.
     *
     * @return a string representing the conversation type.
     * @since <b>v4</b>
     */
    public String getConversationType() {
        return conversationType;
    }

    /**
     * Checks if the filtering should include both user and group tags.
     *
     * @return true if both user and group tags are included. false otherwise.
     * @since <b>v4</b>
     */
    public boolean isWithUserAndGroupTags() {
        return withUserAndGroupTags;
    }

    /**
     * Gets the list of tags associated with the conversation item.
     * These tags can be used for categorization or filtering.
     *
     * @return a list of strings representing the tags.
     * @since <b>v4</b>
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Determines whether the filtering or selection process should consider tags.
     *
     * @return true if tags should be included in the process. false otherwise.
     * @since <b>v4</b>
     */
    public boolean isWithTags() {
        return withTags;
    }

    /**
     * Retrieves the list of user-specific tags.
     * These tags are used to filter and fetch conversations that are associated with users tagged with these specified tags.
     *
     * @return a list of strings representing the user tags.
     * The returned list contains tags that determine which conversations belong to the users tagged with these tags.
     * @since <b>v4</b>
     */
    public List<String> getUserTags() {
        return userTags;
    }

    /**
     * Retrieves the list of group-specific tags.
     * These tags are used to filter and fetch conversations that are associated with groups tagged with these specified tags.
     *
     * @return a list of strings representing the group tags.
     * The returned list contains tags that determine which conversations belong to the groups tagged with these tags.
     * @since v3
     */
    public List<String> getGroupTags() {
        return groupTags;
    }

    /**
     * Checks if blocked users are to be included in the list of conversations.
     * This setting determines whether conversations with users who have been blocked should be retrieved.
     *
     * @return true if blocked users should be included in the list of conversations. false otherwise.
     * @since v4
     */
    public boolean isIncludeBlockedUsers() {
        return includeBlockedUsers;
    }

    /**
     * Checks if blocked information should be included in the list of conversations.
     * This determines whether the details regarding blocked users should be included in the conversation data.
     *
     * @return true if blocked information should be included. false otherwise.
     * @since v4
     */
    public boolean isWithBlockedInfo() {
        return withBlockedInfo;
    }

    /**
     * Retrieves the search keyword used to search conversations based on the keyword.
     *
     * @return a string representing the search keyword.
     * @since v4
     */
    public String getSearchKeyword() {
        return searchKeyword;
    }

    /**
     * Checks if the conversations are unread.
     *
     * @return true if the conversations are unread. false otherwise.
     * @since v4
     */
    public boolean isUnread() {
        return unread;
    }

    /**
     * Retrieves the current page number for pagination of conversations.
     * This represents the page number that was last fetched or is currently being processed.
     *
     * @return an integer representing the current page number.
     * @since v4
     */
    public int getPage() {
        return currentPage;
    }

    /**
     * Checks if agentic conversations should be hidden from the results.
     * When true, conversations classified as agentic will be excluded from the fetched list.
     *
     * @return true if agentic conversations should be hidden, false otherwise.
     * @since v4
     */
    public boolean isHideAgentic() {
        return hideAgentic;
    }

    /**
     * Checks if only agentic conversations should be fetched.
     * When true, only conversations classified as agentic will be included in the results.
     *
     * @return true if only agentic conversations should be fetched, false otherwise.
     * @since v4
     */
    public boolean isOnlyAgentic() {
        return onlyAgentic;
    }
    public static class ConversationsRequestBuilder {
        private String searchKeyword;
        int limit = DEFAULT_LIMIT;
        String conversationType = null;
        boolean withUserAndGroupTags = false;
        List<String> tags;
        boolean withTags = false;
        private List<String> userTags;
        private List<String> groupTags;
        private boolean includeBlockedUsers;
        private boolean withBlockedInfo;
        private boolean unread;
        private int page;
        private boolean hideAgentic = false;
        private boolean onlyAgentic = false;

        /**
         * A method to set limit for the number of Conversations returned in a single iteration.
         * if default value in the builder is {@value #DEFAULT_LIMIT} and max value is {@value #MAX_LIMIT}
         *
         * @param limit Integer value specified by the Developer to return the number if conversations in a single iteration
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v2</b>
         */
        public ConversationsRequest.ConversationsRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * A method to set the conversation type.
         * It can take once of the two values:
         * 1. CometChatConstants.CONVERSATION_TYPE_USER - to fetch only the user concversations.
         * 2. CometChatConstants.CONVERSATION_TYPE_GROUP - to fetch only the group conversations.
         *
         * @param conversationType String value based on which the conversations are fetched.
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @version <b>v2</b>
         * @since <b>v2</b>
         */
        public ConversationsRequest.ConversationsRequestBuilder setConversationType(@CometChatConstants.ConversationTypes String conversationType) {
            this.conversationType = conversationType;
            return this;
        }

        /**
         * A method to determine if the user and groups in the conversationWith field should hold the tags details
         *
         * @param withUserAndGroupTags a boolean that decides if the user/group tags information is received in the conversationWith field
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @version <b>v3</b>
         * @since <b>v3</b>
         */
        public ConversationsRequestBuilder withUserAndGroupTags(boolean withUserAndGroupTags) {
            this.withUserAndGroupTags = withUserAndGroupTags;
            return this;
        }

        /**
         * A method to provide the list of tags so that the conversations belonging to those specific tags are only fetched.
         *
         * @param tags A list of strings that determine that only the conversations tagged with the specified tags are to be fetched.
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @version <b>v3</b>
         * @since <b>v3</b>
         */
        public ConversationsRequestBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * A method to determine of the tags information should be present in the conversations list
         *
         * @param withTags a boolean value to determine if the tags information should be present in the list of conversations.
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @version <b>v3</b>
         * @since <b>v3</b>
         */
        public ConversationsRequestBuilder withTags(boolean withTags) {
            this.withTags = withTags;
            return this;
        }

        /**
         * A method to provide the list of tags so that the conversations that are fetched only belong to the users who are tagged with the specified tags.
         *
         * @param userTags A list of strings that determine that only those conversations are fetched that belong to the users who are tagged with the specified tags.
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @version <b>v3</b>
         * @since <b>v3</b>
         */
        public ConversationsRequestBuilder setUserTags(List<String> userTags) {
            this.userTags = userTags;
            return this;
        }

        /**
         * A method to provide the list of tags so that the conversations that are fetched only belong to the groups that are tagged with the specified tags.
         *
         * @param groupTags A list of strings that determine that only those conversations are fetched that belong to the groups that are tagged with the specified tags.
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @version <b>v3</b>
         * @since <b>v3</b>
         */
        public ConversationsRequestBuilder setGroupTags(List<String> groupTags) {
            this.groupTags = groupTags;
            return this;
        }

        /**
         * A method to determine if the blocked users should be included in the list of conversations
         *
         * @param includeBlockedUsers a boolean value to determine if the blocked users should be included in the list of conversations
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @since <b>v4</b>
         */
        public ConversationsRequestBuilder includeBlockedUsers(boolean includeBlockedUsers) {
            this.includeBlockedUsers = includeBlockedUsers;
            return this;
        }

        /**
         * A method to determine if the blocked information should be present in the list of conversations
         *
         * @param withBlockedInfo a boolean value to determine if the blocked information should be present in the list of conversations
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @since <b>v4</b>
         */

        public ConversationsRequestBuilder withBlockedInfo(boolean withBlockedInfo) {
            this.withBlockedInfo = withBlockedInfo;
            return this;
        }

        /**
         * A method to set the search keyword to search conversations based on the keyword
         *
         * @param searchKeyword String value based on which the conversations are fetched.
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @since <b>v4</b>
         */
        public ConversationsRequestBuilder setSearchKeyword(String searchKeyword) {
            this.searchKeyword = searchKeyword;
            return this;
        }

        /**
         * A method to fetch only unread conversations
         *
         * @param unread boolean value when set true it will fetch only unread conversations
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @since <b>v4</b>
         */
        public ConversationsRequestBuilder setUnread(boolean unread) {
            this.unread = unread;
            return this;
        }

        /**
         * A method to set the page number for pagination of conversations
         *
         * @param page Integer value specifying the page number to fetch. Used for pagination when fetching conversations.
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @since <b>v4</b>
         */
        public ConversationsRequestBuilder setPage(int page) {
            this.page = page + PAGE_OFFSET;
            return this;
        }

        /**
         * A method to hide agentic conversations from the list of conversations.
         * When set to true, conversations that are classified as agentic will be excluded from the results.
         *
         * @param hideAgentic boolean value when set to true will exclude agentic conversations from the fetched list
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @since <b>v4</b>
         */
        public ConversationsRequestBuilder hideAgentic(boolean hideAgentic) {
            this.hideAgentic = hideAgentic;
            return this;
        }

        /**
         * A method to fetch only agentic conversations.
         * When set to true, only conversations that are classified as agentic will be included in the results.
         *
         * @param onlyAgentic boolean value when set to true will fetch only agentic conversations
         * @return ConversationsRequestBuilder object when <code>build()</code> is called
         * @since <b>v4</b>
         */
        public ConversationsRequestBuilder onlyAgentic(boolean onlyAgentic) {
            this.onlyAgentic = onlyAgentic;
            return this;
        }

        public ConversationsRequest build() {

            return new ConversationsRequest(this);
        }
    }

}
