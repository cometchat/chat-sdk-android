package com.cometchat.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.CometChatUtils;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.utils.ContentEqualsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a developer-sent Card message in the CometChat SDK.
 * This class extends BaseMessage and provides functionality for card messages
 * that contain rich, interactive content described as a block of JSON.
 * <p>
 * Card messages are receive-only — they are created via the Platform API / Bubble Builder
 * and delivered to the SDK. The SDK exposes the card payload raw via {@link #getCard()}.
 * An external renderer library consumes it for display.
 * <p>
 * Wire discrimination: {@code category == "card"} (message type is developer-chosen and ignored).
 *
 * @author CometChat Team
 * @version 5.0
 */
public class CardMessage extends BaseMessage {

    private JSONObject card;
    private String text;
    private List<String> tags;

    /**
     * Private default constructor that sets up the message category.
     */
    private CardMessage() {
        super();
        setCategory(CometChatConstants.CATEGORY_CARD);
    }

    /**
     * Gets the raw card payload as a JSONObject.
     * This is the complete card JSON (version, body, style, fallbackText, etc.)
     * that an external renderer library uses to draw the card UI.
     *
     * @return The raw card JSONObject, or null if not set
     */
    public JSONObject getCard() {
        return card;
    }

    /**
     * Gets the fallback text from the card payload.
     * This is extracted from {@code card.fallbackText} and can be used
     * for conversation-list previews or accessibility.
     *
     * @return The fallback text string, or null if not present in the card
     */
    public String getFallbackText() {
        if (card != null && card.has(CometChatConstants.MessageKeys.KEY_AGENTIC_CARD_FALLBACK_TEXT)) {
            return card.optString(CometChatConstants.MessageKeys.KEY_AGENTIC_CARD_FALLBACK_TEXT, null);
        }
        return null;
    }

    /**
     * Gets the text content of the card message from {@code data.text}.
     * Used for conversation-list previews and fallbacks.
     *
     * @return The text content, or null if not set
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the list of tags associated with this card message.
     *
     * @return The list of tags, or null if no tags are set
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Sets the raw card payload.
     *
     * @param card The card JSONObject to set
     */
    public void setCard(JSONObject card) {
        this.card = card;
    }

    /**
     * Sets the text content for this card message.
     *
     * @param text The text content to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the tags for this card message.
     *
     * @param tags The list of tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Creates a CardMessage instance from a JSON object.
     * This method parses the JSON response from the server and populates
     * all the message properties including card-specific fields.
     *
     * @param jsonObject The JSON object containing the message data
     * @return A fully populated CardMessage instance
     */
    public static CardMessage fromJson(JSONObject jsonObject) {
        CardMessage cardMessage = new CardMessage();
        try {
            cardMessage.setRawMessage(jsonObject);
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID))
                cardMessage.setId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID))
                cardMessage.setConversationId(jsonObject.getString(CometChatConstants.ConversationKeys.KEY_CONVERSATION_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID))
                cardMessage.setParentMessageId(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_REPLY_COUNT))
                cardMessage.setReplyCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT))
                cardMessage.setUnreadRepliesCount(jsonObject.getInt(CometChatConstants.MessageKeys.KEY_UNREAD_REPLY_COUNT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID))
                cardMessage.setReceiverUid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_RECEIVER_UID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID))
                cardMessage.setMuid(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_MUID));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE))
                cardMessage.setReceiverType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_TEXT_RECEIVER_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY))
                cardMessage.setCategory(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE))
                cardMessage.setType(jsonObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_SENT_AT))
                cardMessage.setSentAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_SENT_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_UPDATED_AT))
                cardMessage.setUpdatedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_UPDATED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                cardMessage.setDeliveredAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                cardMessage.setReadAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT))
                cardMessage.setEditedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY))
                cardMessage.setEditedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT))
                cardMessage.setDeletedAt(jsonObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_AT));
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY))
                cardMessage.setDeletedBy(jsonObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_DELETED_BY));
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_DATA)) {
                JSONObject dataObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_DATA);
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT)) {
                    cardMessage.setText(dataObject.getString(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT));
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_AGENTIC_CARD)) {
                    cardMessage.setCard(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_AGENTIC_CARD));
                }
                if (dataObject.has(CometChatConstants.ResponseKeys.KEY_ENTITIES)) {
                    JSONObject entitiesObject = new JSONObject(dataObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITIES).toString());
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_SENDER)) {
                        JSONObject senderObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SENDER);
                        User user = User.fromJson(senderObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                        cardMessage.setSender(user);
                    }
                    if (entitiesObject.has(CometChatConstants.MessageKeys.KEY_RECEIVER_UID)) {
                        JSONObject receiverObject = entitiesObject.getJSONObject(CometChatConstants.MessageKeys.KEY_RECEIVER_UID);
                        String entityType = receiverObject.getString(CometChatConstants.ResponseKeys.KEY_ENTITY_TYPE);
                        if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            User user = User.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            cardMessage.setReceiver(user);
                        } else if (entityType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                            Group group = Group.fromJson(receiverObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_ENTITITY).toString());
                            cardMessage.setReceiver(group);
                        }
                    }
                }
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA)) {
                    cardMessage.setMetadata(dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA));
                }
                // Mentions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_MENTIONS)) {
                    List<User> mentionedUsersList = new ArrayList<>();
                    boolean isMentionedMe = false;
                    User loggedInUserObj = CometChat.getLoggedInUser();
                    JSONObject userObj = dataObject.getJSONObject(CometChatConstants.MessageKeys.KEY_MENTIONS);
                    Iterator<String> keys = userObj.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = userObj.getString(key);
                        User user = User.fromJson(value);
                        mentionedUsersList.add(user);
                        if (loggedInUserObj != null && key.equals(loggedInUserObj.getUid())) {
                            isMentionedMe = true;
                        }
                    }
                    cardMessage.setMentionedUsers(mentionedUsersList);
                    cardMessage.setHasMentionedMe(isMentionedMe);
                }
                // Reactions
                if (dataObject.has(CometChatConstants.MessageKeys.KEY_REACTIONS)) {
                    JSONArray reactionCountObj = dataObject.getJSONArray(CometChatConstants.MessageKeys.KEY_REACTIONS);
                    List<ReactionCount> reactionCountList = ReactionCount.listFromJSONArray(reactionCountObj);
                    cardMessage.setReactions(reactionCountList);
                }
            }
            if (jsonObject.has(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT)) {
                JSONObject receiptsObject = jsonObject.getJSONObject(CometChatConstants.ResponseKeys.KEY_MY_RECEIPT);
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT))
                    cardMessage.setDeliveredToMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT));
                if (receiptsObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT))
                    cardMessage.setReadByMeAt(receiptsObject.getLong(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_TAGS)) {
                cardMessage.setTags(CometChatUtils.getListFromJSONArray(jsonObject.getJSONArray(CometChatConstants.MessageKeys.KEY_TAGS)));
            }
            if (jsonObject.has(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE)) {
                BaseMessage quotedMessage = BaseMessage.processMessage(jsonObject.getJSONObject(CometChatConstants.MessageKeys.KEY_QUOTED_MESSAGE));
                cardMessage.setQuotedMessage(quotedMessage);
                cardMessage.setQuotedMessageId(quotedMessage != null ? quotedMessage.getId() : 0);
            }
        } catch (JSONException je) {
            Logger.error("CardMessage from json exception : " + je.getMessage());
        }
        return cardMessage;
    }

    @NonNull
    @Override
    public String toString() {
        return "CardMessage{" +
                "card=" + card +
                ", text='" + text + '\'' +
                ", id=" + id +
                ", muid='" + muid + '\'' +
                ", sender=" + sender +
                ", receiverUid='" + receiverUid + '\'' +
                ", type='" + type + '\'' +
                ", receiverType='" + receiverType + '\'' +
                ", category='" + category + '\'' +
                ", sentAt=" + sentAt +
                ", deliveredAt=" + deliveredAt +
                ", readAt=" + readAt +
                ", metadata=" + metadata +
                ", readByMeAt=" + readByMeAt +
                ", deliveredToMeAt=" + deliveredToMeAt +
                ", deletedAt=" + deletedAt +
                ", editedAt=" + editedAt +
                ", deletedBy='" + deletedBy + '\'' +
                ", editedBy='" + editedBy + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return contentEquals(obj);
    }

    /**
     * Compares this CardMessage with another object for content equality.
     * Unlike equals() which compares by identity (ID), this method compares all fields
     * for value equality, including inherited fields from BaseMessage.
     *
     * @param other the object to compare with
     * @return true if all fields have equal values, false otherwise
     */
    @Override
    public boolean contentEquals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof CardMessage)) return false;
        if (!super.contentEquals(other)) return false;

        CardMessage that = (CardMessage) other;

        return ContentEqualsHelper.jsonObjectsEqual(card, that.card)
                && ContentEqualsHelper.stringsEqual(text, that.text)
                && ContentEqualsHelper.listsEqual(tags, that.tags);
    }

    // Parcelable implementation
    protected CardMessage(Parcel in) {
        super(in);
        String cardStr = in.readString();
        if (cardStr != null) {
            try {
                card = new JSONObject(cardStr);
            } catch (JSONException e) {
                card = null;
            }
        }
        text = in.readString();
        tags = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(card != null ? card.toString() : null);
        dest.writeString(text);
        dest.writeStringList(tags);
    }

    public static final Creator<CardMessage> CREATOR = new Creator<CardMessage>() {
        @Override
        public CardMessage createFromParcel(Parcel in) {
            return new CardMessage(in);
        }

        @Override
        public CardMessage[] newArray(int size) {
            return new CardMessage[size];
        }
    };

    @Override
    public CardMessage clone() {
        CardMessage clone = (CardMessage) super.clone();
        if (this.card != null) {
            try {
                clone.card = new JSONObject(this.card.toString());
            } catch (JSONException e) {
                clone.card = null;
            }
        }
        clone.text = this.text;
        clone.tags = this.tags != null ? new ArrayList<>(this.tags) : null;
        return clone;
    }
}
