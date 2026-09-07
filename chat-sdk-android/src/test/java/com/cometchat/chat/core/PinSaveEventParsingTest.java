package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.TextMessage;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin &amp; Save Message / realtime frame parsing (ENG-37690 §8).
 *
 * <p>The bug this guards against: the WS layer matched event types with a literal switch, had no
 * case for {@code message_pin}, and dropped the frame with "CometChatEvent cannot be null" — so
 * pin/unpin never reached a second device. {@link CometChatPinSaveEvent#tryFromJSON} is the
 * resolver that replaces that: it accepts an alias set for {@code type} and {@code body.action}
 * (the backend fixed the payload shape but not the envelope naming), normalises to the internal
 * {@code ActionKeys} strings, and reads the message from {@code body.message} — NOT from an
 * Action/actionOn envelope.
 *
 * <p>V1 is a structural replica of the only live-captured frame (2026-08-10, app 2547001d03d8066b:
 * {@code type: "message_pin"}, {@code body.action: "message_unpinned"}, full decorated message in
 * {@code body.message}). The save-family vectors follow the same scheme but the wire strings are
 * unconfirmed — which is exactly why the alias set exists.
 *
 * <p>This test lives in {@code com.cometchat.chat.core} because {@code CometChatPinSaveEvent} is
 * package-private.
 */
public class PinSaveEventParsingTest {

    private static final String LIVE_TYPE_PIN = "message_pin";

    /** Envelope shaped like the live capture: body.action + body.message, no top-level deviceId. */
    private static JSONObject frame(String type, String bodyAction, JSONObject message) throws Exception {
        JSONObject body = new JSONObject();
        if (bodyAction != null) {
            body.put(CometChatConstants.WSKeys.KEY_ACTION, bodyAction);
        }
        if (message != null) {
            body.put(CometChatConstants.WSKeys.KEY_MESSAGE, message);
        }
        return new JSONObject()
            .put(CometChatConstants.WSKeys.KEY_APP_ID, "2547001d03d8066b")
            .put(CometChatConstants.WSKeys.KEY_TYPE, type)
            .put(CometChatConstants.WSKeys.KEY_SENDER, "cometchat-uid-5")
            .put(CometChatConstants.WSKeys.KEY_RECEIVER_TYPE, "user")
            .put(CometChatConstants.WSKeys.KEY_RECEIVER, "cometchat-uid-5")
            .put(CometChatConstants.WSKeys.KEY_BODY, body);
    }

    /** A minimal-but-complete text message payload, as carried in body.message on the live frame. */
    private static JSONObject textMessage(long id) throws Exception {
        return new JSONObject()
            .put("id", String.valueOf(id))
            .put("conversationId", "cometchat-uid-3_user_cometchat-uid-5")
            .put("sender", "cometchat-uid-3")
            .put("receiverType", "user")
            .put("receiver", "cometchat-uid-5")
            .put("category", "message")
            .put("type", "text")
            .put("data", new JSONObject().put("text", "hello"))
            .put("sentAt", 1786094356L);
    }

    // ==================== V1: the captured frame ====================

    /** V1 — the live-captured unpin frame parses; action normalises to the internal "unpinned". */
    @Test
    public void v1_liveCapturedUnpinFrame_parsesAndNormalises() throws Exception {
        JSONObject message = textMessage(4009L)
            .put("pinnedBy", "cometchat-uid-5")
            .put("unpinnedBy", "cometchat-uid-5")
            .put("unpinnedAt", 1786350074L);
        JSONObject mainObject = frame(LIVE_TYPE_PIN, "message_unpinned", message);

        CometChatPinSaveEvent event = CometChatPinSaveEvent.tryFromJSON(mainObject, LIVE_TYPE_PIN);

        assertNotNull(event);
        assertEquals(CometChatConstants.ActionKeys.ACTION_MESSAGE_UNPINNED, event.getAction());
        assertNotNull(event.getMessage());
        assertEquals(4009L, event.getMessage().getId());
        assertTrue(event.getMessage() instanceof TextMessage);
        // Canonicalised type so WSConnection.processMessage's switch matches.
        assertEquals(CometChatConstants.WSKeys.KEY_TYPE_MESSAGE_PIN, event.getType());
        // No deviceId on the live frame — the same-resource drop guard must not reject it.
        assertNull(event.getDeviceId());
    }

    /** V2 — pin direction on the same envelope. */
    @Test
    public void v2_pinFrame_normalisesToPinned() throws Exception {
        JSONObject mainObject = frame(LIVE_TYPE_PIN, "message_pinned",
                                      textMessage(4010L).put("pinnedAt", 1786350100L).put("pinnedBy", "cometchat-uid-5"));

        CometChatPinSaveEvent event = CometChatPinSaveEvent.tryFromJSON(mainObject, LIVE_TYPE_PIN);

        assertNotNull(event);
        assertEquals(CometChatConstants.ActionKeys.ACTION_MESSAGE_PINNED, event.getAction());
        assertEquals(4010L, event.getMessage().getId());
    }

    // ==================== alias set ====================

    /** V3 — every accepted body.action alias normalises to the same internal string. */
    @Test
    public void v3_actionAliases_allNormalise() throws Exception {
        String[][] aliasToInternal = {
            {"message_pinned", CometChatConstants.ActionKeys.ACTION_MESSAGE_PINNED},
            {"pinned", CometChatConstants.ActionKeys.ACTION_MESSAGE_PINNED},
            {"pin", CometChatConstants.ActionKeys.ACTION_MESSAGE_PINNED},
            {"message_unpinned", CometChatConstants.ActionKeys.ACTION_MESSAGE_UNPINNED},
            {"unpinned", CometChatConstants.ActionKeys.ACTION_MESSAGE_UNPINNED},
            {"unpin", CometChatConstants.ActionKeys.ACTION_MESSAGE_UNPINNED},
            {"message_saved", CometChatConstants.ActionKeys.ACTION_MESSAGE_SAVED},
            {"saved", CometChatConstants.ActionKeys.ACTION_MESSAGE_SAVED},
            {"save", CometChatConstants.ActionKeys.ACTION_MESSAGE_SAVED},
            {"message_unsaved", CometChatConstants.ActionKeys.ACTION_MESSAGE_UNSAVED},
            {"unsaved", CometChatConstants.ActionKeys.ACTION_MESSAGE_UNSAVED},
            {"unsave", CometChatConstants.ActionKeys.ACTION_MESSAGE_UNSAVED},
        };
        for (String[] pair : aliasToInternal) {
            assertEquals(pair[0], pair[1], CometChatPinSaveEvent.normaliseAction(pair[0]));
        }
        assertNull(CometChatPinSaveEvent.normaliseAction("edited"));
        assertNull(CometChatPinSaveEvent.normaliseAction(null));
    }

    /** V4 — the save family (unconfirmed wire strings) parses under the assumed symmetric scheme. */
    @Test
    public void v4_saveFrame_parsesAndCanonicalisesToSaveType() throws Exception {
        JSONObject mainObject = frame("message_save", "message_saved",
                                      textMessage(4011L).put("savedAt", 1786350200L));

        CometChatPinSaveEvent event = CometChatPinSaveEvent.tryFromJSON(mainObject, "message_save");

        assertNotNull(event);
        assertEquals(CometChatConstants.ActionKeys.ACTION_MESSAGE_SAVED, event.getAction());
        assertEquals(CometChatConstants.WSKeys.KEY_TYPE_MESSAGE_SAVE, event.getType());
    }

    /** V5 — a *_pinned style type doubles as the action when body.action is absent. */
    @Test
    public void v5_typeAliasCarriesTheAction_whenBodyActionAbsent() throws Exception {
        JSONObject mainObject = frame("message_unpinned", null, textMessage(4012L));

        CometChatPinSaveEvent event = CometChatPinSaveEvent.tryFromJSON(mainObject, "message_unpinned");

        assertNotNull(event);
        assertEquals(CometChatConstants.ActionKeys.ACTION_MESSAGE_UNPINNED, event.getAction());
    }

    // ==================== rejection rules ====================

    /** V6 — a non-pin/save type is not this parser's business (returns null, no throw). */
    @Test
    public void v6_unrelatedType_returnsNull() throws Exception {
        JSONObject mainObject = frame("message", "message_unpinned", textMessage(4013L));
        assertNull(CometChatPinSaveEvent.tryFromJSON(mainObject, "message"));
    }

    /**
     * V7 — a bare "message_pin" type with no resolvable action is direction-ambiguous and must be
     * rejected rather than guessed.
     */
    @Test
    public void v7_bareTypeWithoutAction_isAmbiguous_returnsNull() throws Exception {
        JSONObject mainObject = frame(LIVE_TYPE_PIN, null, textMessage(4014L));
        assertNull(CometChatPinSaveEvent.tryFromJSON(mainObject, LIVE_TYPE_PIN));
    }

    /** V8 — no parseable message payload ⇒ null (never a throw into the WS read loop). */
    @Test
    public void v8_missingMessagePayload_returnsNull() throws Exception {
        JSONObject mainObject = frame(LIVE_TYPE_PIN, "message_unpinned", null);
        assertNull(CometChatPinSaveEvent.tryFromJSON(mainObject, LIVE_TYPE_PIN));
    }

    /** V9 — message nested directly in body (no body.message wrapper) still parses — JS fallback. */
    @Test
    public void v9_messageDirectlyInBody_fallbackParses() throws Exception {
        JSONObject body = textMessage(4015L)
            .put(CometChatConstants.WSKeys.KEY_ACTION, "message_unpinned");
        JSONObject mainObject = new JSONObject()
            .put(CometChatConstants.WSKeys.KEY_TYPE, LIVE_TYPE_PIN)
            .put(CometChatConstants.WSKeys.KEY_BODY, body);

        CometChatPinSaveEvent event = CometChatPinSaveEvent.tryFromJSON(mainObject, LIVE_TYPE_PIN);

        assertNotNull(event);
        assertEquals(4015L, event.getMessage().getId());
    }

    /**
     * V10 — the conversation-pin family must NOT resolve here: both families use the bare word
     * "pinned", and collapsing them would hand a MessageListener a conversation payload cast to
     * BaseMessage (ClassCastException on Android, wrong-type delivery at best).
     */
    @Test
    public void v10_conversationPinType_isNotAMessagePinFrame() throws Exception {
        assertFalse(CometChatPinSaveEvent.isPinSaveType("conversation_pin"));
        assertFalse(CometChatPinSaveEvent.isPinSaveType("conversation_pinned"));
        JSONObject mainObject = frame("conversation_pin", "pinned", textMessage(4016L));
        assertNull(CometChatPinSaveEvent.tryFromJSON(mainObject, "conversation_pin"));
    }

    /**
     * V11 — the bare "pin"/"save" types (JS-SDK alias parity). Unlike "message_pin", these DOUBLE
     * as actions through the type fallback, so a frame with no body.action still resolves.
     */
    @Test
    public void v11_bareTypes_resolveViaTypeFallback() throws Exception {
        JSONObject pinFrame = frame("pin", null, textMessage(4017L).put("pinnedAt", 1786350300L));
        CometChatPinSaveEvent pinEvent = CometChatPinSaveEvent.tryFromJSON(pinFrame, "pin");
        assertNotNull(pinEvent);
        assertEquals(CometChatConstants.ActionKeys.ACTION_MESSAGE_PINNED, pinEvent.getAction());
        assertEquals(CometChatConstants.WSKeys.KEY_TYPE_MESSAGE_PIN, pinEvent.getType());

        // body.action still wins over the type when both are present.
        JSONObject unpinFrame = frame("pin", "message_unpinned", textMessage(4018L));
        CometChatPinSaveEvent unpinEvent = CometChatPinSaveEvent.tryFromJSON(unpinFrame, "pin");
        assertNotNull(unpinEvent);
        assertEquals(CometChatConstants.ActionKeys.ACTION_MESSAGE_UNPINNED, unpinEvent.getAction());

        JSONObject saveFrame = frame("save", null, textMessage(4019L).put("savedAt", 1786350400L));
        CometChatPinSaveEvent saveEvent = CometChatPinSaveEvent.tryFromJSON(saveFrame, "save");
        assertNotNull(saveEvent);
        assertEquals(CometChatConstants.ActionKeys.ACTION_MESSAGE_SAVED, saveEvent.getAction());
        assertEquals(CometChatConstants.WSKeys.KEY_TYPE_MESSAGE_SAVE, saveEvent.getType());
    }
}
