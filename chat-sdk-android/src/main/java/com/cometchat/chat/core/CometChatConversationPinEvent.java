package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Conversation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Realtime conversation pin/unpin frame (assumed shape, ENG-37690 §8):
 *
 * <pre>
 * {"type":"conversation_pin","sender":"uid","receiverType":"user","receiver":"uid",
 *  "body":{"action":"conversation_unpinned","conversation":{...full conversation...}}}
 * </pre>
 *
 * NO live capture of this frame exists yet on any platform — the accepted type/action alias sets
 * mirror the JS SDK's equally speculative handler so the two stay in lock-step, and narrowing to
 * the confirmed strings later is a one-line edit here.
 *
 * <p>The conversation family is kept strictly separate from the message pin family
 * ({@link CometChatPinSaveEvent}): both use the bare word {@code "pinned"} as an action, and
 * collapsing them would hand a {@code MessageListener} a conversation payload cast to BaseMessage
 * (ClassCastException) or vice versa. Type membership decides the family FIRST; only then is the
 * action resolved, so a bare-word action can never cross families.
 *
 * <p>Resolution order: {@code body.action} → top-level {@code action} → {@code type}. The
 * conversation is read from {@code body.conversation}, falling back to {@code body} itself.
 * Actions normalise to {@link CometChatConstants.ActionKeys#ACTION_CONVERSATION_PINNED} /
 * {@link CometChatConstants.ActionKeys#ACTION_CONVERSATION_UNPINNED} — deliberately distinct
 * internal strings from the message family's.
 */
class CometChatConversationPinEvent extends CometChatEvent {

    /** Normalised action: ACTION_CONVERSATION_PINNED or ACTION_CONVERSATION_UNPINNED. */
    private String action;
    private Conversation conversation;

    CometChatConversationPinEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
    }

    String getAction() {
        return action;
    }

    Conversation getConversation() {
        return conversation;
    }

    /** Whether this frame's {@code type} belongs to the conversation-pin family. */
    static boolean isConversationPinType(String type) {
        if (type == null) {
            return false;
        }
        switch (type.toLowerCase()) {
            case CometChatConstants.WSKeys.KEY_TYPE_CONVERSATION_PIN:
            case "conversation_pinned":
            case "conversation_unpinned":
                return true;
            default:
                return false;
        }
    }

    /**
     * Normalises an accepted conversation-pin action alias to the internal ActionKeys string, or
     * null when the value is not one. The bare words are accepted here only because callers gate
     * on {@link #isConversationPinType(String)} first — a message-pin frame never reaches this
     * parser.
     */
    static String normaliseAction(String raw) {
        if (raw == null) {
            return null;
        }
        switch (raw.toLowerCase()) {
            case "conversation_pinned":
            case "conversationpinned":
            case "pinned":
            case "pin":
                return CometChatConstants.ActionKeys.ACTION_CONVERSATION_PINNED;
            case "conversation_unpinned":
            case "conversationunpinned":
            case "unpinned":
            case "unpin":
                return CometChatConstants.ActionKeys.ACTION_CONVERSATION_UNPINNED;
            default:
                return null;
        }
    }

    /**
     * Parses a conversation-pin frame, or returns null when the frame is not one (wrong type, no
     * resolvable action — a bare {@code type: "conversation_pin"} with no action is
     * direction-ambiguous — or an unparseable conversation payload). Never throws parse failures
     * into the WS read loop.
     */
    static CometChatConversationPinEvent tryFromJSON(JSONObject mainObject, String type) throws JSONException {
        if (!isConversationPinType(type)) {
            return null;
        }

        JSONObject bodyObject = mainObject.optJSONObject(CometChatConstants.WSKeys.KEY_BODY);

        // body.action → top-level action → type (the *_pinned/_unpinned type aliases double as
        // actions; the bare "conversation_pin" type is direction-ambiguous and doesn't).
        String action = null;
        if (bodyObject != null && bodyObject.has(CometChatConstants.WSKeys.KEY_ACTION)) {
            action = normaliseAction(bodyObject.optString(CometChatConstants.WSKeys.KEY_ACTION, null));
        }
        if (action == null && mainObject.has(CometChatConstants.WSKeys.KEY_ACTION)) {
            action = normaliseAction(mainObject.optString(CometChatConstants.WSKeys.KEY_ACTION, null));
        }
        if (action == null) {
            action = normaliseAction(type);
        }
        if (action == null) {
            return null;
        }

        // body.conversation, falling back to body itself.
        JSONObject conversationObject = null;
        if (bodyObject != null) {
            conversationObject = bodyObject.optJSONObject(CometChatConstants.WSKeys.KEY_CONVERSATION);
            if (conversationObject == null) {
                conversationObject = bodyObject;
            }
        }
        if (conversationObject == null) {
            return null;
        }
        Conversation parsed;
        try {
            parsed = Conversation.fromJSON(conversationObject);
        } catch (Exception e) {
            // The body-as-conversation fallback can hand fromJSON a non-conversation object;
            // treat any parse failure as "not a conversation-pin frame". Exception (not just
            // JSONException): fromJSON NPEs on conversationWith-without-conversationType.
            return null;
        }
        // fromJSON never fails on an EMPTY object — every key is optional — so a junk body would
        // "parse" into a conversation with no identity at all. Require something addressable.
        if (parsed == null || (parsed.getConversationId() == null && parsed.getConversationWith() == null)) {
            return null;
        }

        String appId = mainObject.has(CometChatConstants.WSKeys.KEY_APP_ID)
            ? mainObject.getString(CometChatConstants.WSKeys.KEY_APP_ID) : null;
        String receiver = mainObject.has(CometChatConstants.WSKeys.KEY_RECEIVER)
            ? mainObject.getString(CometChatConstants.WSKeys.KEY_RECEIVER) : null;
        String receiverType = mainObject.has(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE)
            ? mainObject.getString(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE) : null;
        String deviceId = mainObject.has(CometChatConstants.WSKeys.KEY_DEVICE_ID)
            ? mainObject.getString(CometChatConstants.WSKeys.KEY_DEVICE_ID) : null;
        String sender = mainObject.has(CometChatConstants.WSKeys.KEY_SENDER)
            ? mainObject.getString(CometChatConstants.WSKeys.KEY_SENDER) : null;

        CometChatConversationPinEvent event = new CometChatConversationPinEvent(appId, receiver, receiverType, deviceId, sender);
        // Canonicalise the type so WSConnection.processMessage's switch matches regardless of
        // which alias the wire used.
        event.setType(CometChatConstants.WSKeys.KEY_TYPE_CONVERSATION_PIN);
        event.action = action;
        event.conversation = parsed;
        return event;
    }

    @Override
    protected String getAsString() throws JSONException {
        return null;
    }

    @Override
    protected JSONObject getAsJSONObject() throws JSONException {
        return null;
    }
}
