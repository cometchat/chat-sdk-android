package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.CustomMessage;
import com.cometchat.chat.models.TextMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin &amp; Save Message / parse contract (PIN_SAVE_CONTRACT).
 *
 * <p>Golden vectors for the single parse chokepoint {@link BaseMessage#applyPinSaveAttributes}
 * and its wiring into the subtype {@code fromJson} methods (DD §7.8). The core rule everywhere:
 * <b>presence of the key IS the boolean</b> — an absent key means not pinned/saved, never {@code 0}
 * and never a throw; a wrong-typed value is treated as missing; unpin/unsave clears the field.
 */
public class PinSaveMessageParsingTest {

    private static final long PINNED_AT = 1785332391L;
    private static final long SAVED_AT = 1785332395L;

    private static JSONObject withPinSave(Long pinnedAt, String pinnedBy, Long savedAt) throws Exception {
        JSONObject json = new JSONObject();
        if (pinnedAt != null) json.put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_AT, pinnedAt.longValue());
        if (pinnedBy != null) json.put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_BY, pinnedBy);
        if (savedAt != null) json.put(CometChatConstants.MessageKeys.KEY_MESSAGE_SAVED_AT, savedAt.longValue());
        return json;
    }

    // ==================== chokepoint: the semantic rules ====================

    /** V1 — absent keys ⇒ not pinned / not saved (never 0-as-error, never a throw). */
    @Test
    public void v1_absentAttributes_readAsNotPinnedNotSaved() throws Exception {
        BaseMessage message = new BaseMessage();
        BaseMessage.applyPinSaveAttributes(message, new JSONObject());

        assertFalse(message.isPinned());
        assertFalse(message.isSaved());
        assertFalse(message.isSystemPinned());
        assertEquals(0L, message.getPinnedAt());
        assertNull(message.getPinnedBy());
        assertEquals(0L, message.getSavedAt());
    }

    /** V2 — a user pin exposes pinnedAt/pinnedBy and is not a system pin. */
    @Test
    public void v2_userPin_isPinnedButNotSystemPinned() throws Exception {
        BaseMessage message = new BaseMessage();
        BaseMessage.applyPinSaveAttributes(message, withPinSave(PINNED_AT, "uid1", null));

        assertTrue(message.isPinned());
        assertFalse(message.isSystemPinned());
        assertEquals(PINNED_AT, message.getPinnedAt());
        assertEquals("uid1", message.getPinnedBy());
    }

    /** V3 — the app_system sentinel denotes a system / admin pin. */
    @Test
    public void v3_systemPin_isSystemPinned() throws Exception {
        BaseMessage message = new BaseMessage();
        BaseMessage.applyPinSaveAttributes(message, withPinSave(PINNED_AT, CometChatConstants.MessageKeys.PINNED_BY_SYSTEM, null));

        assertTrue(message.isPinned());
        assertTrue(message.isSystemPinned());
        assertEquals(CometChatConstants.MessageKeys.PINNED_BY_SYSTEM, message.getPinnedBy());
    }

    /** V4 — savedAt marks the message saved for the viewer. */
    @Test
    public void v4_saved_isSaved() throws Exception {
        BaseMessage message = new BaseMessage();
        BaseMessage.applyPinSaveAttributes(message, withPinSave(null, null, SAVED_AT));

        assertTrue(message.isSaved());
        assertFalse(message.isPinned());
        assertEquals(SAVED_AT, message.getSavedAt());
    }

    /** V5 — pin and save are independent and can coexist. */
    @Test
    public void v5_pinnedAndSaved_bothTrue() throws Exception {
        BaseMessage message = new BaseMessage();
        BaseMessage.applyPinSaveAttributes(message, withPinSave(PINNED_AT, "uid1", SAVED_AT));

        assertTrue(message.isPinned());
        assertTrue(message.isSaved());
    }

    /** V6 — Rule 5: an unpin/unsave response omits the keys, which CLEARS a previously-set value. */
    @Test
    public void v6_unpinUnsave_clearsStaleValues() throws Exception {
        BaseMessage message = new BaseMessage();
        BaseMessage.applyPinSaveAttributes(message, withPinSave(PINNED_AT, "uid1", SAVED_AT));
        assertTrue(message.isPinned());
        assertTrue(message.isSaved());

        // Server response after unpin + unsave: the keys are simply gone.
        BaseMessage.applyPinSaveAttributes(message, new JSONObject());

        assertFalse(message.isPinned());
        assertFalse(message.isSaved());
        assertEquals(0L, message.getPinnedAt());
        assertNull(message.getPinnedBy());
        assertEquals(0L, message.getSavedAt());
    }

    /** V7 — a non-numeric string pinnedAt is treated as missing and does not throw. */
    @Test
    public void v7_wrongTypedString_readsAsNotPinned() throws Exception {
        BaseMessage message = new BaseMessage();
        JSONObject json = new JSONObject().put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_AT, "not-a-number");
        BaseMessage.applyPinSaveAttributes(message, json);

        assertFalse(message.isPinned());
        assertEquals(0L, message.getPinnedAt());
    }

    /** V8 — an object-typed pinnedAt is treated as missing and does not throw. */
    @Test
    public void v8_wrongTypedObject_readsAsNotPinned() throws Exception {
        BaseMessage message = new BaseMessage();
        JSONObject json = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_AT, new JSONObject().put("x", 1))
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_SAVED_AT, new JSONArray().put(1));
        BaseMessage.applyPinSaveAttributes(message, json);

        assertFalse(message.isPinned());
        assertFalse(message.isSaved());
    }

    /** V9 — an explicit JSON null is treated as absent. */
    @Test
    public void v9_jsonNull_readsAsNotPinned() throws Exception {
        BaseMessage message = new BaseMessage();
        JSONObject json = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_AT, JSONObject.NULL)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_BY, JSONObject.NULL)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_SAVED_AT, JSONObject.NULL);
        BaseMessage.applyPinSaveAttributes(message, json);

        assertFalse(message.isPinned());
        assertFalse(message.isSaved());
        assertNull(message.getPinnedBy());
    }

    /** V10 — null message / null json are no-ops, not crashes. */
    @Test
    public void v10_nullArguments_areNoOps() throws Exception {
        BaseMessage message = new BaseMessage();
        BaseMessage.applyPinSaveAttributes(message, null); // must not throw
        BaseMessage.applyPinSaveAttributes(null, new JSONObject()); // must not throw
        assertFalse(message.isPinned());
    }

    // ==================== wiring: the chokepoint is reached from fromJson ====================

    /** V11 — a full text-message frame (as the socket/REST delivers) carries the pin attributes. */
    @Test
    public void v11_textMessageFrame_carriesPinAttributes() throws Exception {
        JSONObject json = MessagePayloads.envelope(101L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, "receiver-1", CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_AT, PINNED_AT)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_BY, "uid1")
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_SAVED_AT, SAVED_AT);

        BaseMessage message = BaseMessage.processMessage(json);

        assertTrue(message instanceof TextMessage);
        assertTrue(message.isPinned());
        assertEquals("uid1", message.getPinnedBy());
        assertTrue(message.isSaved());
    }

    /** V12 — a custom-message frame also routes through the chokepoint. */
    @Test
    public void v12_customMessageFrame_carriesPinAttributes() throws Exception {
        JSONObject json = MessagePayloads.envelope(202L, CometChatConstants.CATEGORY_CUSTOM,
                        "myCustomType", "receiver-1", CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_AT, PINNED_AT)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_BY, CometChatConstants.MessageKeys.PINNED_BY_SYSTEM);

        BaseMessage message = BaseMessage.processMessage(json);

        assertTrue(message instanceof CustomMessage);
        assertTrue(message.isPinned());
        assertTrue(message.isSystemPinned());
    }

    /** V13 — a thread reply (parentMessageId set) is pinnable; the parent ref is preserved. */
    @Test
    public void v13_threadReply_isPinnable() throws Exception {
        JSONObject json = MessagePayloads.envelope(303L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, "receiver-1", CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.MessageKeys.KEY_PARENT_MESSAGE_ID, 300L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_AT, PINNED_AT)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_BY, "uid1");

        BaseMessage message = BaseMessage.processMessage(json);

        assertEquals(300L, message.getParentMessageId());
        assertTrue(message.isPinned());
    }

    /** V14 — an edited pinned message keeps its pin (edit echoes the pin attributes). */
    @Test
    public void v14_editEchoesPin_pinPreservedThroughEdit() throws Exception {
        JSONObject json = MessagePayloads.envelope(404L, CometChatConstants.CATEGORY_MESSAGE,
                        CometChatConstants.MESSAGE_TYPE_TEXT, "receiver-1", CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_AT, 1785332400L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_EDITED_BY, "uid1")
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_AT, PINNED_AT)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_PINNED_BY, "uid1");

        BaseMessage message = BaseMessage.processMessage(json);

        assertTrue(message.getEditedAt() > 0);
        assertTrue("edit must preserve the pin", message.isPinned());
    }

    /** V15 — contentEquals reflects a difference in the pin/save attributes. */
    @Test
    public void v15_contentEquals_accountsForPinSaveAttributes() throws Exception {
        BaseMessage pinned = new BaseMessage();
        BaseMessage.applyPinSaveAttributes(pinned, withPinSave(PINNED_AT, "uid1", null));
        BaseMessage notPinned = new BaseMessage();

        assertFalse(pinned.contentEquals(notPinned));
    }
}
