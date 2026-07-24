package com.cometchat.chat.helpers;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.Call;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.models.AIAssistantMessage;
import com.cometchat.chat.models.AIToolArgumentMessage;
import com.cometchat.chat.models.AIToolResultMessage;
import com.cometchat.chat.models.Action;
import com.cometchat.chat.models.CardMessage;
import com.cometchat.chat.models.AppEntity;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.Conversation;
import com.cometchat.chat.models.CustomMessage;
import com.cometchat.chat.models.InteractiveMessage;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.Reaction;
import com.cometchat.chat.models.ReactionEvent;
import com.cometchat.chat.models.ReactionCount;
import com.cometchat.chat.models.TextMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class CometChatHelper {

    private static final String TAG = CometChatHelper.class.getSimpleName();

    /**
     * {@inheritDoc}
     *   This method helps to convert <code>JSONObject<code/> of the message payload obtained from <b>Push Notification</b>
     *
     * @param messageObject <code>JSONObject<code/> of the message payload
     * @return An object of <code>BaseMessage<code/>
     * @throws JSONException * Thrown to indicate a problem with the JSON API. Such problems include
     *                       <ul>
     *                       <li>Attempts to parse or construct malformed documents
     *                       <li>Use of null as a name
     *                       <li>Use of numeric types not available to JSON, such as {@link
     *                       Double#isNaN() NaNs} or {@link Double#isInfinite() infinities}.
     *                       <li>Lookups using an out of range index or nonexistent name
     *                       <li>Type mismatches on lookups
     *                       </ul>
     *
     *                       <p>Although this is a checked exception, it is rarely recoverable. Most
     *                       callers should simply wrap this exception in an unchecked exception and
     *                       rethrow:
     *                       <pre>  public JSONArray toJSONObject() {
     *                             try {
     *                                 JSONObject result = new JSONObject();
     *                                 ...
     *                             } catch (JSONException e) {
     *                                 throw new RuntimeException(e);
     *                             }
     *                         }</pre>
     */
    public static BaseMessage processMessage(JSONObject messageObject) throws JSONException {
        if (messageObject.has(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY)) {
            String category = messageObject.getString(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY);
            if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_MESSAGE)) {
                if (messageObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE)) {
                    String type = messageObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE);
                    if (type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                        return TextMessage.fromJson(messageObject);
                    } else if (type.equalsIgnoreCase(CometChatConstants.MESSAGE_TYPE_CUSTOM)) {
                        return CustomMessage.fromJson(messageObject);
                    } else {
                        return MediaMessage.fromJson(messageObject);
                    }
                }
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_ACTION)) {
                return Action.fromJson(messageObject);

            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CALL)) {
                return Call.fromJson(messageObject.toString());
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CUSTOM)) {
                return CustomMessage.fromJson(messageObject);
            } else if(category.equalsIgnoreCase(CometChatConstants.CATEGORY_INTERACTIVE)){
                return InteractiveMessage.fromJson(messageObject);
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_CARD)) {
                return CardMessage.fromJson(messageObject);
            } else if (category.equalsIgnoreCase(CometChatConstants.CATEGORY_AGENTIC)) {
                if (messageObject.has(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE)) {
                    String type = messageObject.getString(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_TYPE);
                    if (CometChatConstants.MESSAGE_TYPE_ASSISTANT.equalsIgnoreCase(type)) {
                        return AIAssistantMessage.fromJson(messageObject);
                    } else if (CometChatConstants.MESSAGE_TYPE_TOOL_ARGUMENTS.equalsIgnoreCase(type)) {
                        return AIToolArgumentMessage.fromJson(messageObject);
                    } else if (CometChatConstants.MESSAGE_TYPE_TOOL_RESULT.equalsIgnoreCase(type)) {
                        return AIToolResultMessage.fromJson(messageObject);
                    }
                }
            }
        } else {
            throw new JSONException("Category missing while parsing message data");
        }
        return null;
    }

    public static Conversation getConversationFromMessage(BaseMessage message){
        Conversation conversation = new Conversation(message.getConversationId(),message.getReceiverType());
        conversation.setLastMessage(message);
        AppEntity conversationWith = null;
        if(conversation.getConversationType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)){
            if(CometChat.getLoggedInUser().getUid().equalsIgnoreCase(message.getSender().getUid()))
                conversationWith = message.getReceiver();
            else
                conversationWith = message.getSender();
        }else{
            conversationWith = message.getReceiver();
        }
        conversation.setConversationWith(conversationWith);
        conversation.setUpdatedAt(message.getUpdatedAt());
        return conversation;
    }

    public static BaseMessage updateMessageWithReactionInfo(BaseMessage baseMessage, Reaction reaction, @CometChatConstants.ReactionAction String action){
        try {
            if (action.equals(CometChatConstants.REACTION_ADDED)){
                if (baseMessage.getId() == reaction.getMessageId()) {
                    if (baseMessage.getReactions().size() > 0 ){
                        int foundReactionIndex = -1;
                        for (int j = 0; j < baseMessage.getReactions().size(); j++){
                            ReactionCount reactionCount = baseMessage.getReactions().get(j);
                            if (reactionCount.getReaction().equals(reaction.getReaction())){
                                foundReactionIndex = j;
                                break;
                            }
                        }
                        if (foundReactionIndex != -1){
                            ReactionCount tempReactionCount = baseMessage.getReactions().get(foundReactionIndex);
                            baseMessage.getReactions().get(foundReactionIndex).setCount(tempReactionCount.getCount() + 1);
                            if (baseMessage.getReactions().get(foundReactionIndex).getReactedByMe()){
                                baseMessage.getReactions().get(foundReactionIndex).setReactedByMe(true);
                            } else {
                                if (CometChat.getLoggedInUser().getUid().equals(reaction.getUid())) {
                                    baseMessage.getReactions().get(foundReactionIndex).setReactedByMe(true);
                                } else {
                                    baseMessage.getReactions().get(foundReactionIndex).setReactedByMe(false);
                                }
                            }
                        } else {
                            ReactionCount tempReactionCount = new ReactionCount();
                            tempReactionCount.setCount(1);
                            tempReactionCount.setReaction(reaction.getReaction());
                            tempReactionCount.setReactedByMe(reaction.getUid().equals(CometChat.getLoggedInUser().getUid()));
                            baseMessage.getReactions().add(tempReactionCount);
                        }
                    } else {
                        ReactionCount tempReactionCount = new ReactionCount();
                        tempReactionCount.setCount(1);
                        tempReactionCount.setReaction(reaction.getReaction());
                        tempReactionCount.setReactedByMe(reaction.getUid().equals(CometChat.getLoggedInUser().getUid()));
                        baseMessage.getReactions().add(tempReactionCount);
                    }
                    return baseMessage;
                }
            } else if (action.equals(CometChatConstants.REACTION_REMOVED)) {
                if (baseMessage.getId() == reaction.getMessageId()) {
                    if (baseMessage.getReactions().size() > 0){
                        int foundReactionIndex = -1;
                        for (int j = 0; j < baseMessage.getReactions().size(); j++){
                            ReactionCount reactionCount = baseMessage.getReactions().get(j);
                            if (reactionCount.getReaction().equals(reaction.getReaction())){
                                foundReactionIndex = j;
                                break;
                            }
                        }
                        if (foundReactionIndex != -1){
                            ReactionCount tempReactionCount = baseMessage.getReactions().get(foundReactionIndex);
                            if (tempReactionCount.getCount() > 1) {
                                baseMessage.getReactions().get(foundReactionIndex).setCount(tempReactionCount.getCount() - 1);
                                if (CometChat.getLoggedInUser().getUid().equals(reaction.getUid())) {
                                    baseMessage.getReactions().get(foundReactionIndex).setReactedByMe(false);
                                } else {
                                    baseMessage.getReactions().get(foundReactionIndex).setReactedByMe(baseMessage.getReactions().get(foundReactionIndex).getReactedByMe());
                                }
                            } else {
                                baseMessage.getReactions().remove(foundReactionIndex);
                            }
                        }
                    }
                    return baseMessage;
                }
            }
        } catch (Exception e){
            Logger.error(TAG, "Error updateMessageWithReactionInfo: " + e);
        }
        return null;
    }
}
