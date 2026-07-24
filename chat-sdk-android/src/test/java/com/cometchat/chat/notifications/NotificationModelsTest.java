package com.cometchat.chat.notifications;

import com.cometchat.chat.models.NotificationCategory;
import com.cometchat.chat.models.NotificationFeedItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Notifications / Feed items, categories, data integrity.
 *
 * <p>Covers:
 * <ul>
 *   <li>NOTIF-05 — a category with a missing id/label still yields a usable object.</li>
 *   <li>NOTIF-15 — a notification is read exactly when it has a read timestamp (incl. zero).</li>
 *   <li>NOTIF-33 — a fully-populated notification exposes all fields consistently.</li>
 *   <li>NOTIF-34 — missing optional fields default gracefully; required fields stay populated.</li>
 *   <li>NOTIF-35 — deeply nested rich content is preserved exactly.</li>
 *   <li>NOTIF-38 — unusual identifier formats are preserved verbatim.</li>
 * </ul>
 *
 * <p><b>Android divergence (NOTIF-05):</b> {@code NotificationCategory} leaves a missing id/name
 * as {@code null} rather than defaulting to an empty string. The test documents this.
 */
public class NotificationModelsTest {

    // ==================== NOTIF-33: fully-populated feed item ====================

    @Test
    public void notif33_fullyPopulatedItem_exposesAllFields() throws Exception {
        JSONObject json = new JSONObject()
                .put("id", "notif-1")
                .put("category", "message")
                .put("data", new JSONObject().put("title", "New message").put("body", "Hello"))
                .put("readAt", 1700000200L)
                .put("deliveredAt", 1700000100L)
                .put("sentAt", 1700000000L)
                .put("tags", new JSONArray().put("urgent"))
                .put("sender", "sender-1")
                .put("receiver", "receiver-1");

        NotificationFeedItem item = NotificationFeedItem.fromJson(json);

        assertEquals("notif-1", item.getId());
        assertEquals("message", item.getCategory());
        assertEquals("New message", item.getContent().getString("title"));
        assertEquals(Long.valueOf(1700000200L), item.getReadAt());
        assertEquals(Long.valueOf(1700000100L), item.getDeliveredAt());
        assertEquals(1700000000L, item.getSentAt());
        assertEquals("sender-1", item.getSender());
        assertTrue(item.getTags().contains("urgent"));
    }

    // ==================== NOTIF-34: minimal item ====================

    @Test
    public void notif34_minimalItem_keepsRequiredFieldsAndDefaultsTheRest() throws Exception {
        JSONObject json = new JSONObject()
                .put("id", "notif-2")
                .put("category", "system");

        NotificationFeedItem item = NotificationFeedItem.fromJson(json);

        assertEquals("notif-2", item.getId());
        assertEquals("system", item.getCategory());
        assertNull("an absent readAt stays unset", item.getReadAt());
        assertNull("an absent deliveredAt stays unset", item.getDeliveredAt());
        assertFalse("with no read timestamp the item is unread", item.isRead());
    }

    // ==================== NOTIF-15: read state ====================

    @Test
    public void notif15_itemWithAReadTimestamp_isRead() throws Exception {
        JSONObject json = new JSONObject().put("id", "n").put("category", "c").put("readAt", 1700000200L);

        assertTrue(NotificationFeedItem.fromJson(json).isRead());
    }

    @Test
    public void notif15_itemWithZeroReadTimestamp_isStillConsideredRead() throws Exception {
        JSONObject json = new JSONObject().put("id", "n").put("category", "c").put("readAt", 0L);

        assertTrue("a read timestamp of zero must still count as read",
                NotificationFeedItem.fromJson(json).isRead());
    }

    @Test
    public void notif15_itemWithoutAReadTimestamp_isUnread() throws Exception {
        JSONObject json = new JSONObject().put("id", "n").put("category", "c");

        assertFalse(NotificationFeedItem.fromJson(json).isRead());
    }

    // ==================== NOTIF-35: deeply nested content ====================

    @Test
    public void notif35_deeplyNestedContent_isPreservedExactly() throws Exception {
        JSONObject nested = new JSONObject()
                .put("card", new JSONObject()
                        .put("sections", new JSONArray()
                                .put(new JSONObject()
                                        .put("actions", new JSONArray()
                                                .put(new JSONObject().put("type", "open").put("url", "https://x/y?z=1"))))));
        JSONObject json = new JSONObject().put("id", "n3").put("category", "rich").put("data", nested);

        NotificationFeedItem item = NotificationFeedItem.fromJson(json);

        String action = item.getContent()
                .getJSONObject("card").getJSONArray("sections").getJSONObject(0)
                .getJSONArray("actions").getJSONObject(0).getString("url");
        assertEquals("arbitrarily nested content must not be lost", "https://x/y?z=1", action);
    }

    // ==================== NOTIF-38: unusual identifiers ====================

    @Test
    public void notif38_unusualIdentifier_isPreservedVerbatim() throws Exception {
        String weirdId = "notif::2024/特殊-🎉-#42";
        JSONObject json = new JSONObject().put("id", weirdId).put("category", "c");

        assertEquals(weirdId, NotificationFeedItem.fromJson(json).getId());
    }

    // ==================== NOTIF-05: category with missing fields ====================

    @Test
    public void notif05_categoryWithIdAndLabel_isParsed() throws Exception {
        JSONObject json = new JSONObject().put("id", "cat-1").put("label", "Promotions");

        NotificationCategory category = NotificationCategory.fromJson(json);

        assertEquals("cat-1", category.getId());
        assertEquals("a 'label' maps to the display name", "Promotions", category.getName());
    }

    @Test
    public void notif05_categoryWithMissingFields_isStillUsable() throws Exception {
        NotificationCategory category = NotificationCategory.fromJson(new JSONObject());

        assertNotNull("the category object is still returned rather than throwing", category);
        // Android divergence: missing id/name stay null rather than defaulting to "".
        assertNull(category.getId());
        assertNull(category.getName());
    }
}
