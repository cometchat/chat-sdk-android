package com.cometchat.chat.core;

import androidx.annotation.NonNull;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Reaction;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * MessageRequest class helps developer to fetch list of messages based on different parameters set by developer
 */
public class ReactionsRequest {
    private static final String TAG = ReactionsRequest.class.getSimpleName();
    private static final int MAX_LIMIT = 20;
    private static final int DEFAULT_LIMIT = 10;
    @CometChatConstants.Affix
    private String affix;
    private int limit = DEFAULT_LIMIT;
    private long messageId = -1;
    private String reaction;
    private boolean inProgress = false;
    private String previousReactionId;
    private String nextReactionId;
    private boolean hasPrevious = true;

    private ReactionsRequest(ReactionsRequestBuilder builder) {
        this.limit = builder.limit;
        this.messageId = builder.messageId;
        this.reaction = builder.reaction;
    }

    /**
     * Get list of previous list of the users who reacted with a reaction based on the parameters specified in <code>ReactionRequestBuilder</code> class
     * The Developer need to call this method repeatedly using the same object of <code>ReactionRequest</code> class to get paginated list of message.
     *
     * @param listener An object of the <code>CallbackListener&lt;List&lt;ReactedUser&gt;&gt;</code> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @see ReactionsRequestBuilder
     * @since <b>v4</b>
     */
    public void fetchPrevious(final CometChat.CallbackListener<List<Reaction>> listener) {
        if (limit <= MAX_LIMIT) {
            if (hasPrevious) {
                this.affix = CometChatConstants.AFFIX_PREPEND;
                getReactedUsers(listener, previousReactionId);
            } else {
                listener.onSuccess(new ArrayList<Reaction>());
            }
        } else {
            returnError(new CometChatException(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED, String.format(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE, "Reaction")), listener);
        }
    }

    /**
     * Get list of next list of the users who reacted with a reaction based on the parameters specified in <code>ReactionRequestBuilder</code> class
     * The Developer need to call this method repeatedly using the same object of <code>ReactionRequest</code> class to get paginated list of message.
     *
     * @param listener An object of the <code>CallbackListener&lt;List&lt;ReactedUser&gt;&gt;</code> class that helps inform the developer if the operation was successful or any error occurred.
     * @version <b>v2</b>
     * @see ReactionsRequestBuilder
     * @since <b>v4</b>
     */
    public void fetchNext(final CometChat.CallbackListener<List<Reaction>> listener) {
        if (limit <= MAX_LIMIT) {
            this.affix = CometChatConstants.AFFIX_APPEND;
            getReactedUsers(listener, nextReactionId);
        } else {
            returnError(new CometChatException(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED, String.format(CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE, "Reaction")), listener);
        }
    }

    private void getReactedUsers(final CometChat.CallbackListener<List<Reaction>> listener, String paginationReactionId) {
        final CometChatException ce = validateReactionRequest();
        if (ce == null) {
            if (inProgress) {
                returnError(new CometChatException(CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS, CometChatConstants.Errors.ERROR_REQUEST_IN_PROGRESS_MESSAGE), listener);
            } else {
                inProgress = true;
                ApiConnection.getInstance().getReactedUsersList(limit, affix, messageId, paginationReactionId, reaction, new ApiConnection.APIConnectionListener() {
                    @Override
                    public void onResponse(String response, final CometChatException ce) {
                        inProgress = false;
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
                                    if (jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).has(CometChatConstants.PaginationKeys.KEY_PAGINATION_PREVIOUS)) {
                                        hasPrevious = true;
                                        if (jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getJSONObject(CometChatConstants.PaginationKeys.KEY_PAGINATION_PREVIOUS).has(CometChatConstants.PaginationKeys.KEY_PAGINATION_ID)) {
                                            previousReactionId = jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getJSONObject(CometChatConstants.PaginationKeys.KEY_PAGINATION_PREVIOUS).getString(CometChatConstants.PaginationKeys.KEY_PAGINATION_ID);
                                        }
                                    } else {
                                        hasPrevious = false;
                                    }
                                    if (jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).has(CometChatConstants.PaginationKeys.KEY_PAGINATION_NEXT)) {
                                        if (jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getJSONObject(CometChatConstants.PaginationKeys.KEY_PAGINATION_NEXT).has(CometChatConstants.PaginationKeys.KEY_PAGINATION_ID)) {
                                            nextReactionId = jsonObject.getJSONObject(CometChatConstants.PaginationKeys.KEY_META).getJSONObject(CometChatConstants.PaginationKeys.KEY_PAGINATION_NEXT).getString(CometChatConstants.PaginationKeys.KEY_PAGINATION_ID);
                                        }
                                    }
                                }
                                final List<Reaction> members = Reaction.listFromJSONArray(jsonObject);
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(members);
                                    }
                                });
                            } catch (final Exception e) {
                                CometChat.postOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onError(new CometChatException(CometChatConstants.Errors.ERROR_UNHANDLED_EXCEPTION, e.toString()));
                                    }
                                });
                            }
                        }
                    }
                });
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

    private CometChatException validateReactionRequest() {
        CometChatException ce = null;
        if (limit <= 0)
            ce = new CometChatException(CometChatConstants.Errors.ERROR_NON_POSITIVE_LIMIT, CometChatConstants.Errors.ERROR_LIMIT_EXCEEDED_MESSAGE);
        else if (messageId <= 0)
            ce = new CometChatException(CometChatConstants.Errors.ERROR_INVALID_MESSAGE_ID, CometChatConstants.Errors.ERROR_INVALID_MESSAGEID_MESSAGE);
        return ce;
    }

    private void returnError(final CometChatException ce, final CometChat.CallbackListener<List<Reaction>> listener) {
        CometChat.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                listener.onError(ce);
            }
        });
    }

    /**
     * Gets the limit on the number of reactions to be fetched in a single operation.
     * The default value is {@value #DEFAULT_LIMIT} and the maximum allowed value is {@value #MAX_LIMIT}.
     *
     * @return The limit as an {@code int}.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets the message ID for which reactions are to be fetched.
     *
     * @return The message ID as an {@code int}.
     */
    public long getMessageId() {
        return messageId;
    }

    /**
     * Gets the specific reaction type by which reactions are to be fetched.
     * This can be used to filter reactions of a specific type (e.g., "like", "love").
     *
     * @return The reaction type as a {@code String}.
     */
    public String getReaction() {
        return reaction;
    }

    @Override
    public String toString() {
        return "ReactionRequest{" +
                "affix='" + affix + '\'' +
                ", limit=" + limit +
                ", messageId=" + messageId +
                ", reaction='" + reaction + '\'' +
                ", inProgress=" + inProgress +
                ", previousReactionId='" + previousReactionId + '\'' +
                ", nextReactionId='" + nextReactionId + '\'' +
                '}';
    }

    /**
     * Builder class to set various parameters to fetch list of Messages
     */
    public static class ReactionsRequestBuilder {
        int limit = DEFAULT_LIMIT;
        private long messageId = -1;
        private String reaction;

        public ReactionsRequestBuilder() {
        }

        /**
         * A method to set limit
         * if default value in the builder is {@value #DEFAULT_LIMIT} and max value is {@value #MAX_LIMIT}
         *
         * @param limit Integer value specified by the Developer
         * @return MessagesRequestBuilder object when <code>build()</code> is called
         */
        public ReactionsRequestBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * A method to set the message id by which the reactions are to be fetched
         *
         * @param messageId of the message
         * @return MessagesRequestBuilder
         */
        public ReactionsRequestBuilder setMessageId(@NonNull long messageId) {
            this.messageId = messageId;
            return this;
        }

        /**
         * A method to set the reaction by which the reactions are to be fetched
         *
         * @param reaction of the message
         * @return MessagesRequestBuilder
         */
        public ReactionsRequestBuilder setReaction(@NonNull String reaction) {
            this.reaction = reaction;
            return this;
        }

        public ReactionsRequest build() {
            return new ReactionsRequest(this);
        }
    }

}
