package com.cometchat.chat.receipts;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.MessageReceipt;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Feature Area: Receipts / Delivery and read acknowledgements.
 *
 * <p>Covers:
 * <ul>
 *   <li>RCPT-01 — a "delivered" receipt is distinguished from a "read" receipt; when a message
 *       is both, read is the definitive state.</li>
 *   <li>RCPT-03 — backend timestamps are interpreted into delivered/read times, and the receipt
 *       type is inferred from whichever timestamp is present.</li>
 * </ul>
 *
 * <p>Each receipt element includes a {@code recipient} so parsing resolves the sender from the
 * payload rather than {@code CometChat.getLoggedInUser()} (the JVM-safe path).
 */
public class MessageReceiptTest {

    /** A receipt element carrying its recipient inline, so parsing never falls back to the session user. */
    private JSONObject recipient(String uid) throws Exception {
        return new JSONObject().put(CometChatConstants.ResponseKeys.KEY_RECIPIENT,
                new JSONObject().put(CometChatConstants.UserKeys.USER_KEY_UID, uid));
    }

    private JSONArray receiptArray(JSONObject... receipts) {
        JSONArray array = new JSONArray();
        for (JSONObject r : receipts) {
            array.put(r);
        }
        return array;
    }

    // ==================== RCPT-03: type inferred from timestamp ====================

    @Test
    public void rcpt03_deliveredOnlyReceipt_isTypedDelivered() throws Exception {
        JSONObject receipt = recipient("recip-1")
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT, 1700000100L);

        List<MessageReceipt> receipts = MessageReceipt.receiptsFromJSON(
                receiptArray(receipt), "u-1", CometChatConstants.RECEIVER_TYPE_USER, 500L);

        assertEquals(1, receipts.size());
        MessageReceipt r = receipts.get(0);
        assertEquals(MessageReceipt.RECEIPT_TYPE_DELIVERED, r.getReceiptType());
        assertEquals(1700000100L, r.getDeliveredAt());
        assertEquals("the receipt binds to the given message/receiver", 500L, r.getMessageId());
        assertEquals("u-1", r.getReceiverId());
    }

    @Test
    public void rcpt03_readOnlyReceipt_isTypedRead() throws Exception {
        JSONObject receipt = recipient("recip-2")
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT, 1700000200L);

        List<MessageReceipt> receipts = MessageReceipt.receiptsFromJSON(
                receiptArray(receipt), "u-2", CometChatConstants.RECEIVER_TYPE_USER, 501L);

        MessageReceipt r = receipts.get(0);
        assertEquals(MessageReceipt.RECEIPT_TYPE_READ, r.getReceiptType());
        assertEquals(1700000200L, r.getReadAt());
    }

    // ==================== RCPT-01: read is the definitive state ====================

    @Test
    public void rcpt01_receiptThatIsBothDeliveredAndRead_reportsRead() throws Exception {
        JSONObject receipt = recipient("recip-3")
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT, 1700000100L)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT, 1700000200L);

        List<MessageReceipt> receipts = MessageReceipt.receiptsFromJSON(
                receiptArray(receipt), "u-3", CometChatConstants.RECEIVER_TYPE_USER, 502L);

        MessageReceipt r = receipts.get(0);
        assertEquals("read must win over delivered as the definitive state",
                MessageReceipt.RECEIPT_TYPE_READ, r.getReceiptType());
        assertEquals("both timestamps remain available to the app", 1700000100L, r.getDeliveredAt());
        assertEquals(1700000200L, r.getReadAt());
    }

    @Test
    public void rcpt01_deliveredAndReadReceiptTypesAreDistinct() throws Exception {
        JSONObject delivered = recipient("d")
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_DELIVERED_AT, 1L);
        JSONObject read = recipient("r")
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT, 2L);

        List<MessageReceipt> receipts = MessageReceipt.receiptsFromJSON(
                receiptArray(delivered, read), "u-4", CometChatConstants.RECEIVER_TYPE_GROUP, 503L);

        assertEquals(MessageReceipt.RECEIPT_TYPE_DELIVERED, receipts.get(0).getReceiptType());
        assertEquals(MessageReceipt.RECEIPT_TYPE_READ, receipts.get(1).getReceiptType());
    }

    @Test
    public void rcpt03_receiptCarriesTheResolvedRecipient() throws Exception {
        JSONObject receipt = recipient("recip-4")
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_READ_AT, 1700000200L);

        List<MessageReceipt> receipts = MessageReceipt.receiptsFromJSON(
                receiptArray(receipt), "u-5", CometChatConstants.RECEIVER_TYPE_USER, 504L);

        assertEquals("the recipient is resolved from the payload, not the logged-in user",
                "recip-4", receipts.get(0).getSender().getUid());
    }
}
