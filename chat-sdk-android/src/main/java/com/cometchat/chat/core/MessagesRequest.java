package com.cometchat.chat.core;

import androidx.annotation.NonNull;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.enums.AttachmentType;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * MessageRequest class helps developer to fetch list of messages based on different parameters set by developer
 */
public class MessagesRequest {

    private static final String TAG = MessagesRequest.class.getSimpleName();

    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 30;

    private String UID;
    private String GUID;
    private int limit = DEFAULT_LIMIT;
    private long messageId = -1;
    private long timestamp = -1;
    private int currentPage = -1;
    private int totalPages = -1;
    @CometChatConstants.Affix
    private String affix;
    private boolean hasNext = true;
    private boolean hasPrevious = true;
    private boolean inProgress = false;
    private boolean unread = false;
    private boolean hideMessagesFromBlockedUsers = false;
    private String searchKeyword;
    private long updatedAfter = -1;
    private boolean updatesOnly = false;
    private String category;
    private String type;
    private long parentMessageId = -1;
    private boolean hideReplies = false;
    private boolean hideDeleted = false;
    private List<String> categories;
    private List<String> types;
    private List<String> tags;
    private boolean withTags = false;
    private boolean mentionsWithTagInfo = false;
    private boolean mentionsWithBlockedInfo = false;
    private boolean interactionGoalCompletedOnly = false;
    private boolean hasAttachments;
    private boolean hasLinks;
    private boolean hasMentions;
    private boolean hasReactions;
    private List<String> mentionedUIDs;
    private List<AttachmentType> attachmentTypes;
    private boolean withParent = false;
    private boolean hideQuotedMessages = false;

    private MessagesRequest(MessagesRequest.MessagesRequestBuilder builder) {
        this.limit = builder.limit;
        this.UID = builder.UID;
        this.GUID = builder.GUID;
        this.messageId = builder.messageId;
        this.timestamp = builder.timestamp;
        this.unread = builder.unread;
        this.hideMessagesFromBlockedUsers = builder.hideMessagesFromBlockedUsers;
        this.searchKeyword = builder.searchKeyword;
        this.updatedAfter = builder.updatedAfter;
        this.updatesOnly = builder.updatesOnly;
        this.category = builder.category;
        this.type = builder.type;
        this.parentMessageId = builder.parentMessageId;
        this.hideReplies = builder.hideReplies;
        this.categories = builder.categories;
        this.types = builder.types;
        this.attachmentTypes = builder.attachmentTypes;
        if (category != null) {
            if (categories == null)
                categories = new ArrayList<>();
            categories.add(category);
        }
        if (type != null) {
            if (types == null)
                types = new ArrayList<>();
            types.add(type);
        }
        this.hideDeleted = builder.hideDeleted;
        this.tags = builder.tags;
        this.withTags = builder.withTags;
        this.mentionsWithTagInfo = builder.mentionsWithTagInfo;
        this.mentionsWithBlockedInfo = builder.mentionsWithBlockedInfo;
        this.interactionGoalCompletedOnly = builder.interactionGoalCompletedOnly;
        this.hasAttachments = builder.hasAttachments;
        this.hasLinks = builder.hasLinks;
        this.hasMentions = builder.hasMentions;
        this.hasReactions = builder.hasReactions;
        this.mentionedUIDs = builder.mentionedUIDs;
        this.withParent = builder.withParent;
        this.hideQuotedMessages = builder.hideQuotedMessages;
    }

    /**
     * Get list of previous message based on the parameters specified in <code>MessagesRequestBuilder</code> class.
     * The Developer need to call this method repeatedly using the same object of <code>MessagesRequest</code> class to get paginated list of message.
     *
     * @param listener An object of the <code>CallbackListener&lt;List&lt;BaseMessage&gt;&gt;</code> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @see MessagesRequestBuilder
     * @since <b>v1</b>
     */
    public void fetchPrevious(final CometChat.CallbackListener<List<BaseMessage>> listener) {
        this.affix = CometChatConstants.AFFIX_PREPEND;
        final CometChatException ce = validateMessageRequest();
        if (ce == null) {
            if (this.updatesOnly && this.updatedAfter == -1) {
                returnError(new CometChatException(CometChatConstants.Errors.ERROR_UPDATESONLY_WITHOUT_UPDATEDAFTER, CometChatConstants.Errors.ERROR_UPDATESONLY_WITHOUT_UPDATEDAFTER_MESSAGE), listener);
            } else {
                if (hasPrevious) {
                    if (this.parentMessageId != -1) {
                        fetchThreadedMessages(affix, listener);
                    } else if (this.UID != null && this.GUID != null) {
                        fetchMessagesForUserInGroup(affix, listener);
                    } else if (this.UID != null) {
                        fetchMessagesForUser(affix, listener);
                    } else if (this.GUID != null) {
                        fetchMessagesForGroup(affix, listener);
                    } else {
                        fetchAllMessages(affix, listener);
                    }
                } else {
                    returnMessageList(new ArrayList<BaseMessage>(), listener);
                }
            }
        } else {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(ce);
                }
            });
        }
    }

    /**
     * Get list of next message based on the parameters specified in <code>MessagesRequestBuilder</code> class
     * The Developer need to call this method repeatedly using the same object of <code>MessagesRequest</code> class to get paginated list of message.
     *
     * @param listener An object of the <code>CallbackListener&lt;List&lt;BaseMessage&gt;&gt;<code/> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @see MessagesRequestBuilder
     * @since <b>v1</b>
     */
    public void fetchNext(final CometChat.CallbackListener<List<BaseMessage>> listener) {
        this.affix = CometChatConstants.AFFIX_APPEND;
        final CometChatException ce = validateMessageRequest();
        if (ce == null) {
            if (messageId == -1 && timestamp == -1 && updatedAfter == -1) {
                returnError(new CometChatException(CometChatConstants.Errors.ERROR_FILTERS_MISSING, CometChatConstants.Errors.ERROR_FILTERS_MISSING_MESSAGE), listener);
            } else {
                if (updatesOnly && updatedAfter == -1) {
                    returnError(new CometChatException(CometChatConstants.Errors.ERROR_UPDATESONLY_WITHOUT_UPDATEDAFTER, CometChatConstants.Errors.ERROR_UPDATESONLY_WITHOUT_UPDATEDAFTER_MESSAGE), listener);
                } else if (hasNext) {
                    if (this.parentMessageId != -1) {
                        fetchThreadedMessages(affix, listener);
                    } else if (this.UID != null && this.GUID != null) {
                        fetchMessagesForUserInGroup(affix, listener);
                    } else if (this.UID != null) {
                        fetchMessagesForUser(affix, listener);
                    } else if (this.GUID != null) {
                        fetchMessagesForGroup(affix, listener);
                    } else {
                        fetchAllMessages(affix, listener);
                    }
                } else {
                    returnMessageList(new ArrayList<BaseMessage>(), listener);
                }
            }
        } else {
            CometChat.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError(ce);
                }
            });

        }
    }

    private void fetchThreadedMessages(@CometChatConstants.Affix String affix, final CometChat.CallbackListener<List<BaseMessage>> listener) {
        if (limit <= MAX_LIMIT) {
            if (!inProgress) {
                inProgress = true;
                CometChat.getThreadedMessages(this.parentMessageId, this.limit, affix, timestamp, messageId, unread, hideMessagesFromBlockedUsers, searchKeyword, updatedAfter, updatesOnly, categories, types, hideDeleted, tags, withTags, interactionGoalCompletedOnly, mentionsWithTagInfo, mentionsWithBlockedInfo, hasAttachments, hasLinks, hasMentions, hasReactions,
                                              mentionedUIDs, attachmentTypes, withParent, hideQuotedMessages, new MessagesFetchedListener() {
                    @Override
                    public void onMessagesFetched(List<BaseMessage> baseMessage, String response, CometChatException e) {
                        handleResponse(baseMessage, response, e, listener);
                    }
                });

            } else {
                returnError(new CometChatException(CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS, CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS_MESSAGE), listener);

            }
        } else {
            returnError(new CometChatException(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED, CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE), listener);
        }
    }


    private void fetchMessagesForUser(@CometChatConstants.Affix String affix, final CometChat.CallbackListener<List<BaseMessage>> listener) {
        if (limit <= MAX_LIMIT) {
            if (!inProgress) {
                inProgress = true;
                CometChat.getUserConversations(UID, limit, affix, timestamp, messageId, unread, hideMessagesFromBlockedUsers, searchKeyword, updatedAfter, updatesOnly, categories, types, hideReplies, hideDeleted, tags, withTags, interactionGoalCompletedOnly, mentionsWithTagInfo, mentionsWithBlockedInfo, hasAttachments, hasLinks, hasMentions, hasReactions,
                                               mentionedUIDs, attachmentTypes, hideQuotedMessages, new MessagesFetchedListener() {
                    @Override
                    public void onMessagesFetched(List<BaseMessage> baseMessage, String response, CometChatException e) {
                        handleResponse(baseMessage, response, e, listener);
                    }
                });
            } else {
                returnError(new CometChatException(CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS, CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS_MESSAGE), listener);
            }
        } else {
            returnError(new CometChatException(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED, CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE), listener);
        }
    }


    private void fetchMessagesForGroup(@CometChatConstants.Affix String affix, final CometChat.CallbackListener<List<BaseMessage>> listener) {
        if (limit <= MAX_LIMIT) {
            if (!inProgress) {
                inProgress = true;
                CometChat.getGroupConversations(GUID, limit, affix, timestamp, messageId, unread, hideMessagesFromBlockedUsers, searchKeyword, updatedAfter, updatesOnly, categories, types, hideReplies, hideDeleted, tags, withTags, interactionGoalCompletedOnly, mentionsWithTagInfo, mentionsWithBlockedInfo, hasAttachments, hasLinks, hasMentions, hasReactions,
                                                mentionedUIDs, attachmentTypes, hideQuotedMessages, new MessagesFetchedListener() {
                    @Override
                    public void onMessagesFetched(List<BaseMessage> baseMessage, String response, CometChatException e) {
                        handleResponse(baseMessage, response, e, listener);
                    }
                });
            } else {
                returnError(new CometChatException(CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS, CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS_MESSAGE), listener);
            }
        } else {
            returnError(new CometChatException(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED, CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE), listener);
        }
    }

    private void fetchMessagesForUserInGroup(@CometChatConstants.Affix String affix, final CometChat.CallbackListener<List<BaseMessage>> listener) {
        if (limit <= MAX_LIMIT) {
            if (!inProgress) {
                inProgress = true;
                CometChat.getUserConversationsInGroup(UID, GUID, limit, affix, timestamp, messageId, unread, hideMessagesFromBlockedUsers, searchKeyword, updatedAfter, updatesOnly, categories, types, hideReplies, hideDeleted, tags, withTags, interactionGoalCompletedOnly, mentionsWithTagInfo, mentionsWithBlockedInfo, hasAttachments, hasLinks, hasMentions, hasReactions,
                                                      mentionedUIDs, attachmentTypes, hideQuotedMessages, new MessagesFetchedListener() {
                    @Override
                    public void onMessagesFetched(List<BaseMessage> baseMessage, String response, CometChatException e) {
                        handleResponse(baseMessage, response, e, listener);
                    }
                });

            } else {
                returnError(new CometChatException(CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS, CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS_MESSAGE), listener);

            }
        } else {
            returnError(new CometChatException(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED, CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE), listener);
        }
    }

    private void fetchAllMessages(@CometChatConstants.Affix String affix, final CometChat.CallbackListener<List<BaseMessage>> listener) {
        if (limit <= MAX_LIMIT) {
            if (!inProgress) {
                inProgress = true;
                CometChat.getAllMessages(limit, affix, timestamp, messageId, unread, hideMessagesFromBlockedUsers, searchKeyword, updatedAfter, updatesOnly, categories, types, hideReplies, hideDeleted, tags, withTags, interactionGoalCompletedOnly, mentionsWithTagInfo, mentionsWithBlockedInfo, hasAttachments, hasLinks, hasMentions, hasReactions,
                                         mentionedUIDs, attachmentTypes, hideQuotedMessages, new MessagesFetchedListener() {
                    @Override
                    public void onMessagesFetched(List<BaseMessage> baseMessage, String response, CometChatException e) {
                        handleResponse(baseMessage, response, e, listener);
                    }
                });

            } else {
                returnError(new CometChatException(CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS, CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS_MESSAGE), listener);
            }
        } else {
            returnError(new CometChatException(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED, CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE), listener);
        }
    }


    private void handleResponse(List<BaseMessage> baseMessages, String response, final CometChatException ce, final CometChat.CallbackListener<List<BaseMessage>> listener) {
        if (ce != null) {
            inProgress = false;
            returnError(ce, listener);
        } else {
            Logger.error("Conversations Fetched : " + response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has(CometChatConstants.PaginationKeys.KEY_META)) {
                    JSONObject metaObject = jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META);
                    if (metaObject.has(CometChatConstants.PaginationKeys.KEY_PAGINATION)) {
                        JSONObject paginationObject = metaObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_PAGINATION);
                        currentPage = paginationObject.getInt(CometChatConstants.PaginationKeys.KEY_PAGINATION_CURRENT_PAGE);
                        totalPages = paginationObject.getInt(CometChatConstants.PaginationKeys.KEY_PAGINATION_TOTAL_PAGES);
                    }
                    if (metaObject.has(CometChatConstants.PaginationKeys.KEY_CURSOR)) {
                        JSONObject cursorObject = metaObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_CURSOR);
                        if (messageId == -1 && cursorObject.has(CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID)) {
                            messageId = cursorObject.getLong(CometChatConstants.PaginationKeys.KEY_FIELD_MESSAGEID);
                        }
                        if (timestamp == -1 && cursorObject.has(CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP)) {
                            timestamp = cursorObject.getLong(CometChatConstants.PaginationKeys.KEY_FIELD_TIMESTAMP);
                        }
                        if (updatedAfter == -1 && cursorObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT)) {
                            updatedAfter = cursorObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT);
                        }
                    }
                }
                updateMessageIdAndTimestamp(affix, baseMessages);
                baseMessages = ExtensionManager.callOnMessageListFetched(baseMessages);
                inProgress = false;
                if (affix.equalsIgnoreCase(CometChatConstants.AFFIX_APPEND) && currentPage == totalPages) {
                    hasNext = false;
                    hasPrevious = true;
                }
                if (affix.equalsIgnoreCase(CometChatConstants.AFFIX_PREPEND) && currentPage == totalPages) {
                    hasPrevious = false;
                    hasNext = true;
                }
                returnMessageList(baseMessages, listener);
            } catch (final JSONException je) {
                inProgress = false;
                returnError(new CometChatException(CometChatConstants.Errors.ERROR_JSON_EXCEPTION, je.getMessage()), listener);

            }

        }
    }


    private void updateMessageIdAndTimestamp(String affix, List<BaseMessage> messages) {
        Logger.error(TAG, toString());
        if (affix.equalsIgnoreCase(CometChatConstants.AFFIX_APPEND)) {
            for (BaseMessage baseMessage : messages) {
                Logger.error(TAG, "Message ID : " + baseMessage.getId());
                if (messageId != -1) {
                    if (baseMessage.getId() > messageId)
                        messageId = baseMessage.getId();
                }
                if (timestamp != -1) {
                    if (baseMessage.getSentAt() > timestamp)
                        timestamp = baseMessage.getSentAt();
                }
                if (updatedAfter != -1) {
                    if (baseMessage.getUpdatedAt() > updatedAfter)
                        updatedAfter = baseMessage.getUpdatedAt();
                }
            }
        } else {
            for (BaseMessage baseMessage : messages) {
                Logger.error(TAG, "Message ID : " + baseMessage.getId());
                if (messageId != -1) {
                    if (baseMessage.getId() < messageId)
                        messageId = baseMessage.getId();
                }
                if (timestamp != -1) {
                    if (baseMessage.getSentAt() < timestamp)
                        timestamp = baseMessage.getSentAt();
                }
                if (updatedAfter != -1) {
                    if (baseMessage.getUpdatedAt() < updatedAfter)
                        updatedAfter = baseMessage.getUpdatedAt();
                }
            }
        }
        Logger.error(TAG, toString());
    }

    private CometChatException validateMessageRequest() {
        CometChatException ce = null;
        if (limit <= 0)
            ce = new CometChatException(CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT, CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE);
        else if (CometChatUtils.isEmpty(UID))
            ce = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_UID, CometChatConstants.Errors.ERROR_INVALID_UID_MESSAGE);
        else if (CometChatUtils.isEmpty(GUID))
            ce = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_GUID, CometChatConstants.Errors.ERROR_INVALID_GUID_MESSAGE);
        else if (messageId == 0 || messageId < -1)
            ce = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_ID, CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE);
        else if (timestamp == 0 || messageId < -1)
            ce = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_TIMESTAMP, CometChatConstants.Errors.ERROR_INVALID_TIMESTAMP_MESSAGE);
        else if (CometChatUtils.isEmpty(category) || validateCategory(category))
            ce = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_CATEGORY, CometChatConstants.Errors.ERROR_INVALID_CATEGORY_MESSAGE);
        return ce;
    }

    private boolean validateCategory(String category) {
        if (category == null)
            return false;
        return ((!category.equalsIgnoreCase(CometChatConstants.CATEGORY_MESSAGE) &&
                !category.equalsIgnoreCase(CometChatConstants.CATEGORY_ACTION) &&
                !category.equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM) &&
                !category.equalsIgnoreCase(CometChatConstants.CATEGORY_CALL)));
    }

    @Override
    public String toString() {
        return "MessagesRequest{" +
                "UID='" + UID + '\'' +
                ", GUID='" + GUID + '\'' +
                ", limit=" + limit +
                ", messageId=" + messageId +
                ", timestamp=" + timestamp +
                ", currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", affix='" + affix + '\'' +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                ", inProgress=" + inProgress +
                ", unread=" + unread +
                ", hideMessagesFromBlockedUsers=" + hideMessagesFromBlockedUsers +
                ", searchKeyword='" + searchKeyword + '\'' +
                ", updatedAfter=" + updatedAfter +
                ", updatesOnly=" + updatesOnly +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", parentMessageId=" + parentMessageId +
                ", hideReplies=" + hideReplies +
                ", hideDeleted=" + hideDeleted +
                ", categories=" + categories +
                ", types=" + types +
                ", tags=" + tags +
                ", withTags=" + withTags +
                ", mentionsWithTagInfo=" + mentionsWithTagInfo +
                ", mentionsWithBlockedInfo=" + mentionsWithBlockedInfo +
                ", interactionGoalCompletedOnly=" + interactionGoalCompletedOnly +
                ", hasAttachments=" + hasAttachments +
                ", hasLinks=" + hasLinks +
                ", hasMentions=" + hasMentions +
                ", hasReactions=" + hasReactions +
                ", mentionedUids=" + mentionedUIDs +
                ", attachmentTypes=" + attachmentTypes +
                '}';
    }

    private void returnError(final CometChatException ce, final CometChat.CallbackListener<List<BaseMessage>> listener) {
        CometChat.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                listener.onError(ce);
            }
        });
    }

    private void returnMessageList(final List<BaseMessage> messages, final CometChat.CallbackListener<List<BaseMessage>> listener) {
        CometChat.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                listener.onSuccess(messages);
            }
        });
    }

    interface MessagesFetchedListener {
        public void onMessagesFetched(List<BaseMessage> baseMessage, String response, CometChatException e);
    }

    /**
     * Gets the limit on the number of messages to be fetched in a single operation.
     * The default value is {@value #DEFAULT_LIMIT} and the maximum allowed value is {@value #MAX_LIMIT}.
     *
     * @return The limit as an {@code int}.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets the UID of the user for which the messages are being fetched.
     *
     * @return The UID as a {@code String}.
     */
    public String getUID() {
        return UID;
    }

    /**
     * Gets the GUID of the group for which the messages are being fetched.
     *
     * @return The GUID as a {@code String}.
     */
    public String getGUID() {
        return GUID;
    }

    /**
     * Gets the message ID from which subsequent or previous messages are to be fetched.
     *
     * @return The message ID as an {@code int}.
     */
    public long getMessageId() {
        return messageId;
    }

    /**
     * Gets the flag indicating whether to fetch only unread messages.
     *
     * @return {@code true} if only unread messages should be fetched, {@code false} otherwise.
     */
    public boolean isUnread() {
        return unread;
    }

    /**
     * Gets the flag indicating whether to hide messages from blocked users.
     *
     * @return {@code true} if messages from blocked users should be hidden, {@code false} otherwise.
     */
    public boolean isHideMessagesFromBlockedUsers() {
        return hideMessagesFromBlockedUsers;
    }

    /**
     * Gets the timestamp from which messages are to be fetched.
     * Used to fetch messages around a specific time point.
     *
     * @return The timestamp as a {@code long}.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the search keyword used to filter messages.
     * Only messages containing this keyword will be fetched.
     *
     * @return The search keyword as a {@code String}.
     */
    public String getSearchKeyword() {
        return searchKeyword;
    }

    /**
     * Gets the timestamp after which updated or edited messages are to be fetched.
     *
     * @return The timestamp as a {@code long}.
     */
    public long getUpdatedAfter() {
        return updatedAfter;
    }

    /**
     * Gets the flag indicating whether to fetch only updated or edited messages.
     *
     * @return {@code true} if only updated messages should be fetched, {@code false} otherwise.
     */
    public boolean isUpdatesOnly() {
        return updatesOnly;
    }

    /**
     * Gets the category for which the messages are to be fetched.
     * This method is deprecated and replaced by {@link #getCategories()}.
     *
     * @return The category as a {@code String}.
     * @deprecated Use {@link #getCategories()} instead.
     */
    @Deprecated
    public String getCategory() {
        return category;
    }

    /**
     * Gets the list of categories for which the messages are to be fetched.
     *
     * @return The list of categories as a {@code List<String>}.
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * Gets the type for which the messages are to be fetched.
     * This method is deprecated and replaced by {@link #getTypes()}.
     *
     * @return The type as a {@code String}.
     * @deprecated Use {@link #getTypes()} instead.
     */
    @Deprecated
    public String getType() {
        return type;
    }

    /**
     * Gets the list of types for which the messages are to be fetched.
     *
     * @return The list of types as a {@code List<String>}.
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * Returns the list of selected attachment types used for filtering messages.
     * @return {@code List<AttachmentTypes>}
     */
    public List<AttachmentType> getAttachmentTypes() {
        return attachmentTypes;
    }

    /**
     * Gets the parent message ID to fetch messages belonging to a specific thread.
     *
     * @return The parent message ID as an {@code int}.
     */
    public long getParentMessageId() {
        return parentMessageId;
    }

    /**
     * Gets the flag indicating whether to exclude replies (threaded messages) when fetching messages.
     *
     * @return {@code true} if replies should be excluded, {@code false} otherwise.
     */
    public boolean isHideReplies() {
        return hideReplies;
    }

    /**
     * Gets the flag indicating whether to hide deleted messages when fetching messages.
     *
     * @return {@code true} if deleted messages should be hidden, {@code false} otherwise.
     */
    public boolean isHideDeleted() {
        return hideDeleted;
    }

    /**
     * Gets the list of tags for which the messages are to be fetched.
     *
     * @return The list of tags as a {@code List<String>}.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Gets the flag indicating whether messages should be fetched with their tags.
     *
     * @return {@code true} if messages should be fetched with tags, {@code false} otherwise.
     */
    public boolean isWithTags() {
        return withTags;
    }

    /**
     * Gets the flag indicating whether to fetch mentioned messages with user tag information.
     *
     * @return {@code true} if mentioned messages with user tag information should be fetched, {@code false} otherwise.
     */
    public boolean isMentionsWithTagInfo() {
        return mentionsWithTagInfo;
    }

    /**
     * Gets the flag indicating whether to fetch mentioned messages with blocked relation information.
     *
     * @return {@code true} if mentioned messages with blocked relation information should be fetched, {@code false} otherwise.
     */
    public boolean isMentionsWithBlockedInfo() {
        return mentionsWithBlockedInfo;
    }

    /**
     * Gets the flag indicating whether to fetch only messages with completed interaction goals.
     *
     * @return {@code true} if only messages with completed interaction goals should be fetched, {@code false} otherwise.
     */
    public boolean isInteractionGoalCompletedOnly() {
        return interactionGoalCompletedOnly;
    }

    /**
     * Gets the flag indicating whether to fetch only messages with attachments.
     *
     * @return {@code true} if only messages with attachments should be fetched, {@code false} otherwise.
     */
    public boolean isHasAttachments() {
        return hasAttachments;
    }

    /**
     * Gets the flag indicating whether to fetch only messages with links.
     *
     * @return {@code true} if only messages with links should be fetched, {@code false} otherwise.
     */
    public boolean isHasLinks() {
        return hasLinks;
    }

    /**
     * Gets the flag indicating whether to fetch only messages with reactions.
     *
     * @return {@code true} if only messages with reactions should be fetched, {@code false} otherwise.
     */
    public boolean isHasReactions() {
        return hasReactions;
    }

    /**
     * Gets the flag indicating whether to fetch only messages with mentions of the current user.
     *
     * @return {@code true} if only messages with mentions of the current user should be fetched, {@code false} otherwise.
     */
    public boolean isHasMentions() {
        return hasMentions;
    }

    /**
     * Gets the flag indicating whether to fetch messages along with their parent messages.
     *
     * @return {@code true} if messages should be fetched with their parent messages, {@code false} otherwise.
     */
    public boolean isWithParent() {
        return withParent;
    }

    /**
     * Gets the flag indicating whether to hide quoted messages when fetching messages.
     *
     * @return {@code true} if quoted messages should be hidden, {@code false} otherwise.
     */
    public boolean isHideQuotedMessages() {
        return hideQuotedMessages;
    }

    /**
     * Gets the list of mentioned user IDs for which the messages are to be fetched.
     *
     * @return The list of mentioned user IDs as a {@code List<String>}.
     */
    public List<String> getMentionedUIDs() {
        return mentionedUIDs;
    }

    /**
     * Builder class to set various parameters to fetch list of Messages
     */
    public static class MessagesRequestBuilder {
        int limit = DEFAULT_LIMIT;
        private String UID;
        private String GUID;
        private long messageId = -1;
        private long timestamp = -1;
        private boolean unread = false;
        private boolean hideMessagesFromBlockedUsers = false;
        private String searchKeyword;
        private long updatedAfter = -1;
        private boolean updatesOnly = false;
        private String category;
        private String type;
        private long parentMessageId = -1;
        private boolean hideReplies = false;
        private boolean hideDeleted = false;
        private List<String> categories;
        private List<String> types;
        private List<String> tags;
        private boolean withTags = false;
        private boolean mentionsWithTagInfo = false;
        private boolean mentionsWithBlockedInfo = false;
        private boolean interactionGoalCompletedOnly = false;
        private boolean hasAttachments = false;
        private boolean hasLinks = false;
        private boolean hasMentions = false;
        private boolean hasReactions = false;
        private List<String> mentionedUIDs;
        private List<AttachmentType> attachmentTypes;
        private boolean withParent = false;
        private boolean hideQuotedMessages = false;

        public MessagesRequestBuilder() {

        }

        /**
         * A method to set limit
         * if default value in the builder is {@value #DEFAULT_LIMIT} and max value is {@value #MAX_LIMIT}
         *
         * @param limit Integer value specified by the Developer
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * A method to set UID of a User to get messages of particular conversation
         *
         * @param UID Unique Identifier of a User
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setUID(@NonNull String UID) {
            this.UID = UID;
            return this;
        }

        /**
         * A method to set GUID of a Group to get messages of particular conversation
         *
         * @param GUID Unique Identifier of a Group
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setGUID(@NonNull String GUID) {
            this.GUID = GUID;
            return this;
        }

        /**
         * A method to set Message Id of a particular message from which developer can call <code><fetchNext()code/> or <code>fetchPrevious()<code/>
         *
         * @param messageId Unique Id of a message
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setMessageId(long messageId) {
            this.messageId = messageId;
            return this;
        }

        /**
         * Method to set unread message while fetching
         *
         * @param unread boolean parameter to consider or avoid unread messages while fetching messages using <code>MessagesRequest<code/>
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setUnread(boolean unread) {
            this.unread = unread;
            return this;
        }


        /**
         * Method to set parameters to hide or show message from blocked users while fetching
         *
         * @param hideMessagesFromBlockedUsers boolean parameter to consider or avoid messages from blocked users while fetching messages using <code>MessagesRequest<code/>
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder hideMessagesFromBlockedUsers(boolean hideMessagesFromBlockedUsers) {
            this.hideMessagesFromBlockedUsers = hideMessagesFromBlockedUsers;
            return this;
        }

        /**
         * A method to set timestamp from which developer can call <code><fetchNext()code/> or <code>fetchPrevious()<code/>
         *
         * @param timestamp timestamp from which developer wants to fetch previous or next messsages
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * A method set Search keyword while fetching the messages
         *
         * @param searchKeyword keyword which developer wants to search in the message list
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setSearchKeyword(String searchKeyword) {
            this.searchKeyword = searchKeyword;
            return this;
        }

        /**
         * A method set timestamp to get fetch messages updated/edited after that timestamp
         *
         * @param updatedAfter timestamp to get messages updated after the specified timestamp
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setUpdatedAfter(long updatedAfter) {
            this.updatedAfter = updatedAfter;
            return this;
        }


        /**
         * A method to set parameter to get edited/updated messages
         *
         * @param updatesOnly boolean parameter show or hide edited/updated messages only
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder updatesOnly(boolean updatesOnly) {
            this.updatesOnly = updatesOnly;
            return this;
        }

        /**
         * A method to set parameter to get the messages belonging to a specific category.
         *
         * @param category The category for which the messages are to be fetched.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        @Deprecated
        public MessagesRequestBuilder setCategory(String category) {
            this.category = category;
            return this;
        }

        /**
         * A method to set parameter to get the messages belonging to a specific category.
         *
         * @param categories The list of categories for which the messages are to be fetched.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setCategories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        /**
         * A method to set parameter to get the messages belonging to a specific type.
         *
         * @param type The type for which the messages are to be fetched.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        @Deprecated
        public MessagesRequestBuilder setType(String type) {
            this.type = type;
            return this;
        }

        /**
         * A method to set parameter to get the messages belonging to a specific type.
         *
         * @param types The list of types for which the messages are to be fetched.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v1</b>
         */
        public MessagesRequestBuilder setTypes(List<String> types) {
            this.types = types;
            return this;
        }

        /**
         * Filters messages by specific attachment types.
         * @param attachmentTypes {@code List<AttachmentTypes>}
         * @return MessagesRequestBuilder object when {@code build()} is called
         */
        public MessagesRequestBuilder setAttachmentTypes(List<AttachmentType> attachmentTypes) {
            this.attachmentTypes = attachmentTypes;
            return this;
        }

        /**
         * A method to set parent id to retrieve messages only belonging to the particular thread.
         *
         * @param parentMessageId The id of the message for which the messages are to be fetched.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v2</b>
         */
        public MessagesRequestBuilder setParentMessageId(long parentMessageId) {
            this.parentMessageId = parentMessageId;
            return this;
        }

        /**
         * A method to exclude threaded messages while fecthing messages for Users/Groups.
         *
         * @param hideReplies A boolean variable which when set to true excludes the threaded messages from the list of messages.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v2</b>
         */
        public MessagesRequestBuilder hideReplies(boolean hideReplies) {
            this.hideReplies = hideReplies;
            return this;
        }

        /**
         * A method to hide deleted messages from list of messages to be fetched.
         *
         * @param hideDeleted A boolean variable which when set to true excludes the deleted messages from the list of messages.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v2</b>
         * @since <b>v2</b>
         */
        public MessagesRequestBuilder hideDeletedMessages(boolean hideDeleted) {
            this.hideDeleted = hideDeleted;
            return this;
        }

        /**
         * A method set the tags for which the messages are to be fetched
         *
         * @param tags A list of strings that determines the list of tags for which the messages are to be fetched.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v3</b>
         * @since <b>v3</b>
         */
        public MessagesRequestBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * A method to inform the SDK that the messages to be fetched are to be fetched along with the tags.
         *
         * @param withTags This boolean when set to true will fetch the message along with the tags else the messages will be fetched without tags.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v3</b>
         * @since <b>v3</b>
         */
        public MessagesRequestBuilder withTags(boolean withTags) {
            this.withTags = withTags;
            return this;
        }

        /**
         * A method to inform the SDK that the messages to be fetched are to be mentioned messages with user tag.
         *
         * @param mentionsWithTagInfo This boolean when set to true will fetch the mentioned messages with user tag.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v4</b>
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder mentionsWithTagInfo(boolean mentionsWithTagInfo) {
            this.mentionsWithTagInfo = mentionsWithTagInfo;
            return this;
        }

        /**
         * A method to inform the SDK that the messages to be fetched are to be mentioned messages with blocked relation.
         *
         * @param mentionsWithBlockedInfo This boolean when set to true will fetch the mentioned messages with blocked relation.
         * @return MessagesRequestBuilder object when <code><build()code/> is called
         * @version <b>v4</b>
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder mentionsWithBlockedInfo(boolean mentionsWithBlockedInfo) {
            this.mentionsWithBlockedInfo = mentionsWithBlockedInfo;
            return this;
        }

        /**
         * Sets whether to fetch only messages with completed interaction goals.
         *
         * @param interactionGoalCompleted The boolean value indicating whether to fetch only messages with completed interaction goals.
         * @return The instance of MessagesRequestBuilder.
         * @version <b>v4</b>
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder setInteractionGoalCompletedOnly(boolean interactionGoalCompleted) {
            this.interactionGoalCompletedOnly = interactionGoalCompleted;
            return this;
        }

        /**
         * Sets whether to fetch only messages with attachments.
         *
         * @param hasAttachments The boolean value indicating whether to fetch only messages with attachments.
         * @return The instance of MessagesRequestBuilder.
         * @version <b>v4</b>
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder hasAttachments(boolean hasAttachments) {
            this.hasAttachments = hasAttachments;
            return this;
        }

        /**
         * Sets whether to fetch only messages with links.
         *
         * @param hasLinks The boolean value indicating whether to fetch only messages with links.
         * @return The instance of MessagesRequestBuilder.
         * @version <b>v4</b>
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder hasLinks(boolean hasLinks) {
            this.hasLinks = hasLinks;
            return this;
        }

        /**
         * Sets whether to fetch only messages with mentions of the logged-in user.
         *
         * @param hasMentions The boolean value indicating whether to fetch only messages with mentions of the logged-in user.
         * @return The instance of MessagesRequestBuilder.
         * @version <b>v4</b>
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder hasMentions(boolean hasMentions) {
            this.hasMentions = hasMentions;
            return this;
        }

        /**
         * Sets whether to fetch only messages with reactions.
         *
         * @param hasReactions The boolean value indicating whether to fetch only messages with reactions.
         * @return The instance of MessagesRequestBuilder.
         * @version <b>v4</b>
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder hasReactions(boolean hasReactions) {
            this.hasReactions = hasReactions;
            return this;
        }

        /**
         * Sets the list of mentioned UIDs for which the messages are to be fetched.
         *
         * @param mentionedUIDs The list of UIDs for which the messages are to be fetched.
         * @return The instance of MessagesRequestBuilder.
         * @version <b>v4</b>
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder setMentionedUIDs(List<String> mentionedUIDs) {
            this.mentionedUIDs = mentionedUIDs;
            return this;
        }

        /**
         * Sets whether to fetch messages with their parent message details.
         *
         * @param withParent The boolean value indicating whether to fetch messages with their parent message details.
         * @return The instance of MessagesRequestBuilder.
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder withParent(boolean withParent){
            this.withParent = withParent;
            return this;
        }

        /**
         * Sets whether to hide quoted messages when fetching messages.
         *
         * @param hideQuotedMessages The boolean value indicating whether to hide quoted messages.
         * @return The instance of MessagesRequestBuilder.
         * @since <b>v4</b>
         */
        public MessagesRequestBuilder hideQuotedMessages(boolean hideQuotedMessages) {
            this.hideQuotedMessages = hideQuotedMessages;
            return this;
        }

        public MessagesRequest build() {
            return new MessagesRequest(this);
        }
    }
}
