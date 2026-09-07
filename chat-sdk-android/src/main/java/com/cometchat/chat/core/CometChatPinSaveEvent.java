package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.CometChatHelper;
import com.cometchat.chat.models.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Realtime message pin/save frame:
 *
 * <pre>
 * {"type":"message_pin","sender":"uid","receiverType":"user","receiver":"uid",
 *  "body":{"action":"message_unpinned","pinnedBy":"uid","message":{...full message...}}}
 * </pre>
 *
 * Only the pin family's wire strings ({@code type: "message_pin"},
 * {@code body.action: "message_unpinned"}) are confirmed by a live capture; the save family is
 * assumed symmetric. Because the backend design doc fixed the payload shape but not the envelope
 * naming, this parser deliberately accepts an alias set per action and normalises to the internal
 * {@link CometChatConstants.ActionKeys} strings ({@code pinned}/{@code unpinned}/{@code saved}/
 * {@code unsaved}) before anything else in the SDK sees them — mirroring the JS SDK's resolver.
 *
 * <p>Resolution order: {@code body.action} → top-level {@code action} → top-level {@code type}.
 * The message is read from {@code body.message}, falling back to {@code body} itself. The frame
 * carries a full decorated message (pin/save attributes included), NOT an Action envelope — so
 * this event class dispatches through its own path, not the Action branch.
 */
class CometChatPinSaveEvent extends CometChatEvent {

    /** Normalised action: one of the internal ActionKeys pin/save strings. */
    private String action;
    private BaseMessage message;

    CometChatPinSaveEvent(String appId, String receiver, String receiverType, String deviceId, String sender) {
        super(appId, receiver, receiverType, deviceId, sender);
    }

    String getAction() {
        return action;
    }

    BaseMessage getMessage() {
        return message;
    }

    /** Whether this frame's {@code type} belongs to the message pin/save family. */
    static boolean isPinSaveType(String type) {
        if (type == null) {
            return false;
        }
        switch (type.toLowerCase()) {
            case CometChatConstants.WSKeys.KEY_TYPE_MESSAGE_PIN:
            case CometChatConstants.WSKeys.KEY_TYPE_MESSAGE_SAVE:
            case "message_pinned":
            case "message_unpinned":
            case "message_saved":
            case "message_unsaved":
            // Bare forms, accepted for JS-SDK alias parity. Unlike the bare "message_pin", these
            // DO double as actions via the type fallback ("pin" → PINNED), matching JS exactly.
            case "pin":
            case "save":
                return true;
            default:
                return false;
        }
    }

    /**
     * Normalises any accepted pin/save action alias to the internal ActionKeys string, or null if
     * the value is not a pin/save action. The bare words ({@code pinned} etc.) are accepted here
     * only because callers gate on {@link #isPinSaveType(String)} first — a conversation-pin frame
     * carrying the bare word never reaches this parser.
     */
    static String normaliseAction(String raw) {
        if (raw == null) {
            return null;
        }
        switch (raw.toLowerCase()) {
            case "message_pinned":
            case "pinned":
            case "pin":
                return CometChatConstants.ActionKeys.ACTION_MESSAGE_PINNED;
            case "message_unpinned":
            case "unpinned":
            case "unpin":
                return CometChatConstants.ActionKeys.ACTION_MESSAGE_UNPINNED;
            case "message_saved":
            case "saved":
            case "save":
                return CometChatConstants.ActionKeys.ACTION_MESSAGE_SAVED;
            case "message_unsaved":
            case "unsaved":
            case "unsave":
                return CometChatConstants.ActionKeys.ACTION_MESSAGE_UNSAVED;
            default:
                return null;
        }
    }

    /**
     * Parses a pin/save frame, or returns null when the frame is not one (wrong type, no resolvable
     * action — e.g. a bare {@code type: "message_pin"} with no {@code body.action} is ambiguous
     * between pin and unpin — or an unparseable message payload).
     */
    static CometChatPinSaveEvent tryFromJSON(JSONObject mainObject, String type) throws JSONException {
        if (!isPinSaveType(type)) {
            return null;
        }

        JSONObject bodyObject = mainObject.optJSONObject(CometChatConstants.WSKeys.KEY_BODY);

        // body.action → top-level action → type (the *_pinned/*_saved type aliases double as
        // actions; the bare "message_pin"/"message_save" types are direction-ambiguous and don't).
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

        // body.message, falling back to body itself.
        JSONObject messageObject = null;
        if (bodyObject != null) {
            messageObject = bodyObject.optJSONObject(CometChatConstants.WSKeys.KEY_MESSAGE);
            if (messageObject == null) {
                messageObject = bodyObject;
            }
        }
        if (messageObject == null) {
            return null;
        }
        // The body-as-message fallback can hand processMessage a non-message object (e.g. a body
        // holding only the action); treat any parse failure as "not a pin/save frame" rather than
        // letting it escape into the WS read loop.
        BaseMessage baseMessage;
        try {
            baseMessage = CometChatHelper.processMessage(messageObject);
        } catch (JSONException e) {
            return null;
        }
        if (baseMessage == null) {
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

        CometChatPinSaveEvent event = new CometChatPinSaveEvent(appId, receiver, receiverType, deviceId, sender);
        // Canonicalise the type so WSConnection.processMessage's switch matches regardless of
        // which alias the wire used.
        if (CometChatConstants.ActionKeys.ACTION_MESSAGE_SAVED.equals(action)
            || CometChatConstants.ActionKeys.ACTION_MESSAGE_UNSAVED.equals(action)) {
            event.setType(CometChatConstants.WSKeys.KEY_TYPE_MESSAGE_SAVE);
        } else {
            event.setType(CometChatConstants.WSKeys.KEY_TYPE_MESSAGE_PIN);
        }
        event.action = action;
        event.message = baseMessage;
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
