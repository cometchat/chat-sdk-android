package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.CometChatHelper;
import com.cometchat.chat.models.Action;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.CardMessage;
import com.cometchat.chat.models.CustomMessage;
import com.cometchat.chat.models.InteractiveMessage;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.TextMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Feature Area: Messaging / Message type routing.
 *
 * <p>Covers:
 * <ul>
 *   <li>MSG-22 — A message envelope specifies its category and type; the SDK routes each
 *       combination to the correct message class so the app always gets a strongly-typed
 *       object matching its actual content.</li>
 *   <li>MSG-15 — Cards received via a normal message still return the standard message
 *       object for category "card", and unrelated message types remain unaffected.</li>
 *   <li>MSG-21 — A message arrives with an unrecognized category/type combination.</li>
 * </ul>
 *
 * <p>Routing for the {@code call} category is excluded: {@code Call.fromJson} reaches into
 * call-manager state that is not available in a JVM unit test.
 */
public class MessageRoutingTest {

    private static final String RECEIVER_UID = "receiver-1";

    private JSONObject envelopeWithData(String category, String type) throws Exception {
        return MessagePayloads.envelope(501L, category, type, RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA,
                        new JSONObject().put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, "payload"));
    }

    private BaseMessage route(String category, String type) throws Exception {
        return CometChatHelper.processMessage(envelopeWithData(category, type));
    }

    // ==================== MSG-22: category/type routes to the right class ====================

    @Test
    public void msg22_messageTextRoutesToTextMessage() throws Exception {
        BaseMessage message = route(CometChatConstants.CATEGORY_MESSAGE, CometChatConstants.MESSAGE_TYPE_TEXT);

        assertTrue("message/text must produce a TextMessage", message instanceof TextMessage);
        assertEquals("payload", ((TextMessage) message).getText());
    }

    @Test
    public void msg22_mediaTypesRouteToMediaMessage() throws Exception {
        assertRoutesToMedia(CometChatConstants.MESSAGE_TYPE_IMAGE);
        assertRoutesToMedia(CometChatConstants.MESSAGE_TYPE_VIDEO);
        assertRoutesToMedia(CometChatConstants.MESSAGE_TYPE_AUDIO);
        assertRoutesToMedia(CometChatConstants.MESSAGE_TYPE_FILE);
    }

    private void assertRoutesToMedia(String type) throws Exception {
        BaseMessage message = route(CometChatConstants.CATEGORY_MESSAGE, type);

        assertTrue("message/" + type + " must produce a MediaMessage", message instanceof MediaMessage);
        assertEquals(type, message.getType());
    }

    @Test
    public void msg22_messageCustomTypeRoutesToCustomMessage() throws Exception {
        BaseMessage message = route(CometChatConstants.CATEGORY_MESSAGE, CometChatConstants.MESSAGE_TYPE_CUSTOM);

        assertTrue(message instanceof CustomMessage);
    }

    @Test
    public void msg22_customCategoryRoutesToCustomMessage() throws Exception {
        BaseMessage message = route(CometChatConstants.CATEGORY_CUSTOM, "location");

        assertTrue("custom/location must produce a CustomMessage", message instanceof CustomMessage);
        assertEquals("location", message.getType());
    }

    @Test
    public void msg22_actionCategoryRoutesToAction() throws Exception {
        // An action payload must carry data.action; see ActionMessageParsingTest for the
        // full actor/target coverage.
        JSONObject data = new JSONObject()
                .put(CometChatConstants.ResponseKeys.KEY_ACTION, CometChatConstants.ActionKeys.ACTION_JOINED)
                .put(CometChatConstants.ResponseKeys.KEY_ENTITIES, new JSONObject()
                        .put(CometChatConstants.ActionKeys.KEY_BY, MessagePayloads.entity(
                                CometChatConstants.ActionKeys.KEY_ENTITY_USER,
                                MessagePayloads.user("actor-1", "Actor One"))));
        JSONObject payload = MessagePayloads.envelope(504L, CometChatConstants.CATEGORY_ACTION,
                        CometChatConstants.ActionKeys.ACTION_TYPE_GROUP_MEMBER,
                        "group-1", CometChatConstants.RECEIVER_TYPE_GROUP)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);

        BaseMessage message = CometChatHelper.processMessage(payload);

        assertTrue("action/groupMember must produce an Action", message instanceof Action);
    }

    @Test
    public void msg22_interactiveCategoryRoutesToInteractiveMessage() throws Exception {
        BaseMessage message = route(CometChatConstants.CATEGORY_INTERACTIVE, "form");

        assertTrue("interactive/form must produce an InteractiveMessage",
                message instanceof InteractiveMessage);
    }

    @Test
    public void msg22_routingIsDrivenByCategoryNotType() throws Exception {
        // Same type string, different categories: the category decides the class.
        BaseMessage asMessage = route(CometChatConstants.CATEGORY_MESSAGE, CometChatConstants.MESSAGE_TYPE_CUSTOM);
        BaseMessage asCustom = route(CometChatConstants.CATEGORY_CUSTOM, CometChatConstants.MESSAGE_TYPE_CUSTOM);

        assertTrue(asMessage instanceof CustomMessage);
        assertTrue(asCustom instanceof CustomMessage);
        assertEquals(CometChatConstants.CATEGORY_MESSAGE, asMessage.getCategory());
        assertEquals(CometChatConstants.CATEGORY_CUSTOM, asCustom.getCategory());
    }

    @Test
    public void msg22_categoryMatchingIsCaseInsensitive() throws Exception {
        JSONObject payload = MessagePayloads.envelope(502L, "MESSAGE", "TEXT",
                        RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA,
                        new JSONObject().put(CometChatConstants.MessageKeys.KEY_SEND_TEXT_MESSAGE_TEXT, "shouty"));

        BaseMessage message = CometChatHelper.processMessage(payload);

        assertTrue("category casing must not change routing", message instanceof TextMessage);
    }

    // ==================== MSG-15: card category, and other types unaffected ====================

    @Test
    public void msg15_cardCategoryReturnsTheStandardCardMessageObject() throws Exception {
        BaseMessage message = route(CometChatConstants.CATEGORY_CARD, CometChatConstants.CATEGORY_CARD);

        assertTrue("category 'card' must produce a CardMessage", message instanceof CardMessage);
        assertEquals(CometChatConstants.CATEGORY_CARD, message.getCategory());
    }

    @Test
    public void msg15_cardSupportLeavesTextAndCustomMessagesUnaffected() throws Exception {
        BaseMessage text = route(CometChatConstants.CATEGORY_MESSAGE, CometChatConstants.MESSAGE_TYPE_TEXT);
        BaseMessage custom = route(CometChatConstants.CATEGORY_CUSTOM, "location");

        assertTrue("text messages must not be re-routed as cards", text instanceof TextMessage);
        assertTrue("custom messages must not be re-routed as cards", custom instanceof CustomMessage);
    }

    // ==================== MSG-21: unrecognized combinations ====================

    @Test
    public void msg21_unknownCategoryDoesNotCrash() throws Exception {
        BaseMessage message = route("someFutureCategory", "someFutureType");

        assertNull("an unknown category yields no typed message rather than throwing", message);
    }

    @Test
    public void msg21_agenticCategoryWithUnknownTypeDoesNotCrash() throws Exception {
        BaseMessage message = route(CometChatConstants.CATEGORY_AGENTIC, "someFutureAgenticType");

        assertNull(message);
    }

    @Test
    public void msg21_messageCategoryWithoutATypeDoesNotCrash() throws Exception {
        JSONObject payload = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SEND_MESSAGE_ID, 503L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, CometChatConstants.CATEGORY_MESSAGE);

        assertNull(CometChatHelper.processMessage(payload));
    }

    @Test
    public void msg21_missingCategorySurfacesADescriptiveError() {
        JSONObject payload = new JSONObject();

        try {
            CometChatHelper.processMessage(payload);
            fail("a message with no category should be rejected");
        } catch (JSONException expected) {
            assertNotNull("the failure must explain what was wrong", expected.getMessage());
            assertTrue("the error should name the missing field, not be opaque: " + expected.getMessage(),
                    expected.getMessage().toLowerCase().contains("category"));
        }
    }
}
