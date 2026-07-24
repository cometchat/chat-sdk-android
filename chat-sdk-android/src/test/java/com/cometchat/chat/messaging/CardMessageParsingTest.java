package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.CardMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Feature Area: Messaging / Card messages.
 *
 * <p>Covers:
 * <ul>
 *   <li>MSG-13 — A developer sends an interactive "card" message; the full card definition
 *       (body elements, styling, fallback text) is preserved and the preview text and
 *       fallback text are independently accessible for rendering or notifications.</li>
 *   <li>MSG-14 — A card message missing its card payload or preview text does not crash;
 *       those fields come back null so the app can fall back to a generic rendering.</li>
 * </ul>
 */
public class CardMessageParsingTest {

    private static final String RECEIVER_UID = "receiver-1";

    /** A card definition of the shape an external card renderer consumes. */
    private JSONObject cardDefinition() throws Exception {
        return new JSONObject()
                .put("version", 1)
                .put(CometChatConstants.MessageKeys.KEY_AGENTIC_CARD_FALLBACK_TEXT, "Buy the Acme Widget for $19.99")
                .put("body", new JSONArray()
                        .put(new JSONObject().put("type", "image")
                                .put("url", "https://example.com/widget.png"))
                        .put(new JSONObject().put("type", "text").put("value", "Acme Widget"))
                        .put(new JSONObject().put("type", "button").put("label", "Buy")
                                .put("action", new JSONObject()
                                        .put("type", "url")
                                        .put("target", "https://example.com/buy?sku=42"))))
                .put("style", new JSONObject()
                        .put("background", "#FFFFFF")
                        .put("cornerRadius", 8));
    }

    private JSONObject cardPayload(JSONObject card, String previewText) throws Exception {
        JSONObject data = new JSONObject();
        if (card != null) {
            data.put(CometChatConstants.MessageKeys.KEY_AGENTIC_CARD, card);
        }
        if (previewText != null) {
            data.put(CometChatConstants.MessageKeys.KEY_AGENTIC_TEXT, previewText);
        }

        return MessagePayloads.envelope(401L, CometChatConstants.CATEGORY_CARD,
                        CometChatConstants.CATEGORY_CARD, RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);
    }

    // ==================== MSG-13: full card definition preserved ====================

    @Test
    public void msg13_cardMessage_preservesTheFullCardDefinition() throws Exception {
        JSONObject card = cardDefinition();

        CardMessage message = CardMessage.fromJson(cardPayload(card, "Product card"));
        JSONObject parsed = message.getCard();

        assertNotNull(parsed);
        assertEquals("the card definition must survive verbatim for the renderer",
                card.toString(), parsed.toString());
    }

    @Test
    public void msg13_cardMessage_preservesNestedBodyElementsAndStyling() throws Exception {
        CardMessage message = CardMessage.fromJson(cardPayload(cardDefinition(), "Product card"));
        JSONObject card = message.getCard();

        JSONArray body = card.getJSONArray("body");
        assertEquals(3, body.length());
        assertEquals("image", body.getJSONObject(0).getString("type"));
        assertEquals("Acme Widget", body.getJSONObject(1).getString("value"));
        assertEquals("a nested button action must not be flattened",
                "https://example.com/buy?sku=42",
                body.getJSONObject(2).getJSONObject("action").getString("target"));
        assertEquals("#FFFFFF", card.getJSONObject("style").getString("background"));
        assertEquals(8, card.getJSONObject("style").getInt("cornerRadius"));
    }

    @Test
    public void msg13_previewTextAndFallbackText_areIndependentlyAccessible() throws Exception {
        CardMessage message = CardMessage.fromJson(cardPayload(cardDefinition(), "Product card"));

        assertEquals("data.text is the conversation-list preview", "Product card", message.getText());
        assertEquals("card.fallbackText is the notification fallback",
                "Buy the Acme Widget for $19.99", message.getFallbackText());
    }

    @Test
    public void msg13_cardMessage_isCategorisedAsCard() throws Exception {
        CardMessage message = CardMessage.fromJson(cardPayload(cardDefinition(), "Product card"));

        assertEquals(CometChatConstants.CATEGORY_CARD, message.getCategory());
        assertEquals(401L, message.getId());
        assertEquals(RECEIVER_UID, message.getReceiverUid());
    }

    // ==================== MSG-14: missing card payload or preview text ====================

    @Test
    public void msg14_cardMessageWithoutCardPayload_returnsNullCardInsteadOfCrashing() throws Exception {
        CardMessage message = CardMessage.fromJson(cardPayload(null, "Only preview text"));

        assertNotNull("the message object must still be returned", message);
        assertNull("an absent card payload reads as null", message.getCard());
        assertNull("fallback text cannot exist without a card", message.getFallbackText());
        assertEquals("Only preview text", message.getText());
    }

    @Test
    public void msg14_cardMessageWithoutPreviewText_returnsNullText() throws Exception {
        CardMessage message = CardMessage.fromJson(cardPayload(cardDefinition(), null));

        assertNull("an absent data.text reads as null", message.getText());
        assertNotNull("the card itself is still available", message.getCard());
        assertEquals("Buy the Acme Widget for $19.99", message.getFallbackText());
    }

    @Test
    public void msg14_cardMessageMissingBothCardAndText_stillParses() throws Exception {
        CardMessage message = CardMessage.fromJson(cardPayload(null, null));

        assertNotNull(message);
        assertNull(message.getCard());
        assertNull(message.getText());
        assertNull(message.getFallbackText());
    }

    @Test
    public void msg14_cardWithoutFallbackText_returnsNullFallback() throws Exception {
        JSONObject cardWithoutFallback = new JSONObject()
                .put("version", 1)
                .put("body", new JSONArray().put(new JSONObject().put("type", "text").put("value", "Hi")));

        CardMessage message = CardMessage.fromJson(cardPayload(cardWithoutFallback, "preview"));

        assertNotNull("the card is present", message.getCard());
        assertNull("a card without fallbackText reports null", message.getFallbackText());
    }

    @Test
    public void msg14_cardMessageWithoutDataBlock_stillParses() throws Exception {
        JSONObject payload = MessagePayloads.envelope(402L, CometChatConstants.CATEGORY_CARD,
                CometChatConstants.CATEGORY_CARD, RECEIVER_UID, CometChatConstants.RECEIVER_TYPE_USER);

        CardMessage message = CardMessage.fromJson(payload);

        assertNotNull(message);
        assertEquals(402L, message.getId());
        assertNull(message.getCard());
        assertNull(message.getText());
    }
}
