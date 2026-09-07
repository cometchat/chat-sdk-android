package com.cometchat.chat.notifications;

import com.cometchat.chat.constants.CometChatNotificationsConstants;
import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.QuotedRepliesOptions;
import com.cometchat.chat.enums.RepliesOptions;
import com.cometchat.chat.models.GroupPreferences;
import com.cometchat.chat.models.NotificationCategory;
import com.cometchat.chat.models.NotificationFeedItem;
import com.cometchat.chat.models.NotificationPreferences;
import com.cometchat.chat.models.OneOnOnePreferences;
import com.cometchat.chat.models.PushPreferences;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

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
 *   <li>QR-12 — an unset quoted-replies preference is omitted from the update payload.</li>
 *   <li>QR-18/QR-19 — the quoted-replies preference survives every conversion, copy and comparison
 *       path both preference buckets offer.</li>
 *   <li>QR-20 — the deprecated preferences type reaches the field through the same buckets.</li>
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

    // ==================== QR-18/QR-19: one-on-one bucket, every representation ====================

    @Test
    public void qr19_oneOnOneQuotedReplies_roundTripsThroughJson() throws Exception {
        OneOnOnePreferences original = new OneOnOnePreferences();
        original.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES);

        JSONObject json = original.toJson();

        assertEquals("the value travels on the agreed wire key", 4,
                json.getInt(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES));
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                OneOnOnePreferences.fromJson(json).getQuotedRepliesPreference());
    }

    @Test
    public void qr19_everyOneOnOneQuotedReplyValue_roundTripsUnchanged() throws Exception {
        for (QuotedRepliesOptions option : QuotedRepliesOptions.values()) {
            OneOnOnePreferences original = new OneOnOnePreferences();
            original.setQuotedRepliesPreference(option);

            assertEquals("value " + option.getValue() + " must survive a JSON round trip",
                    option, OneOnOnePreferences.fromJson(original.toJson()).getQuotedRepliesPreference());
            assertEquals("value " + option.getValue() + " must survive a map round trip",
                    option, OneOnOnePreferences.fromMap(original.toMap()).getQuotedRepliesPreference());
        }
    }

    @Test
    public void qr19_oneOnOneQuotedReplies_roundTripsThroughMap() {
        OneOnOnePreferences original = new OneOnOnePreferences();
        original.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_MENTIONS);

        Map<String, Integer> map = original.toMap();

        assertEquals(Integer.valueOf(3),
                map.get(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES));
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_MENTIONS,
                OneOnOnePreferences.fromMap(map).getQuotedRepliesPreference());
    }

    @Test
    public void qr19_oneOnOneQuotedReplies_isCarriedByCloneAndContentEquals() {
        OneOnOnePreferences original = new OneOnOnePreferences();
        original.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_ALL);

        OneOnOnePreferences copy = original.clone();

        assertEquals("a copy must carry the quoted-replies preference",
                QuotedRepliesOptions.SUBSCRIBE_TO_ALL, copy.getQuotedRepliesPreference());
        assertTrue(original.contentEquals(copy));

        copy.setQuotedRepliesPreference(QuotedRepliesOptions.DONT_SUBSCRIBE);
        assertFalse("a difference in quoted replies alone must break content equality",
                original.contentEquals(copy));
    }

    @Test
    public void qr19_oneOnOneQuotedReplies_appearsInToString() {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_ALL);

        assertTrue(prefs.toString().contains("oneOnOneQuotedReplies=SUBSCRIBE_TO_ALL"));
    }

    // ==================== QR-18/QR-19: group bucket, every representation ====================

    @Test
    public void qr19_groupQuotedReplies_roundTripsThroughJson() throws Exception {
        GroupPreferences original = new GroupPreferences();
        original.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES);

        JSONObject json = original.toJson();

        assertEquals(4, json.getInt(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_QUOTED_REPLIES));
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                GroupPreferences.fromJson(json).getQuotedRepliesPreference());
    }

    @Test
    public void qr19_everyGroupQuotedReplyValue_roundTripsUnchanged() throws Exception {
        for (QuotedRepliesOptions option : QuotedRepliesOptions.values()) {
            GroupPreferences original = new GroupPreferences();
            original.setQuotedRepliesPreference(option);

            assertEquals("value " + option.getValue() + " must survive a JSON round trip",
                    option, GroupPreferences.fromJson(original.toJson()).getQuotedRepliesPreference());
            assertEquals("value " + option.getValue() + " must survive a map round trip",
                    option, GroupPreferences.fromMap(original.toMap()).getQuotedRepliesPreference());
        }
    }

    @Test
    public void qr19_groupQuotedReplies_isCarriedByCloneAndContentEquals() {
        GroupPreferences original = new GroupPreferences();
        original.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_MENTIONS);

        GroupPreferences copy = original.clone();

        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_MENTIONS, copy.getQuotedRepliesPreference());
        assertTrue(original.contentEquals(copy));

        copy.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_ALL);
        assertFalse("a difference in quoted replies alone must break content equality",
                original.contentEquals(copy));
    }

    @Test
    public void qr19_groupQuotedReplies_appearsInToString() {
        GroupPreferences prefs = new GroupPreferences();
        prefs.setQuotedRepliesPreference(QuotedRepliesOptions.DONT_SUBSCRIBE);

        assertTrue(prefs.toString().contains("groupQuotedReplies=DONT_SUBSCRIBE"));
    }

    // ==================== QR-12: unset is omitted, never defaulted ====================

    @Test
    public void qr12_unsetQuotedReplies_isAbsentFromTheOneOnOnePayload() {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setMessagesPreference(MessagesOptions.SUBSCRIBE_TO_ALL);

        assertFalse("an unset preference must never be sent — it would overwrite the stored setting",
                prefs.toJson().has(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES));
        assertFalse(prefs.toMap().containsKey(
                CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES));
    }

    @Test
    public void qr12_unsetQuotedReplies_isAbsentFromTheGroupPayload() {
        GroupPreferences prefs = new GroupPreferences();
        prefs.setMessagesPreference(MessagesOptions.SUBSCRIBE_TO_ALL);

        assertFalse(prefs.toJson().has(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_QUOTED_REPLIES));
        assertFalse(prefs.toMap().containsKey(
                CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_QUOTED_REPLIES));
    }

    @Test
    public void qr12_explicitDontSubscribe_isSentAndIsDistinctFromUnset() throws Exception {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setQuotedRepliesPreference(QuotedRepliesOptions.DONT_SUBSCRIBE);

        assertEquals("'do not notify me' is an explicit choice, not the absence of one", 1,
                prefs.toJson().getInt(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES));
    }

    @Test
    public void qr14_aPayloadWithNoQuotedRepliesPreference_isUnchangedFromBefore() throws Exception {
        OneOnOnePreferences prefs = new OneOnOnePreferences();
        prefs.setMessagesPreference(MessagesOptions.SUBSCRIBE_TO_ALL);
        prefs.setRepliesPreference(RepliesOptions.SUBSCRIBE_TO_MENTIONS);

        JSONObject json = prefs.toJson();

        assertEquals("adding the field must not add keys to an existing payload", 2, json.length());
    }

    // ==================== QR-13: an unknown value from the server degrades to unset ====================

    @Test
    public void qr13_anUnknownQuotedReplyValueFromTheServer_degradesToUnsetWithoutFailing() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_MESSAGES,
                        MessagesOptions.SUBSCRIBE_TO_ALL.getValue())
                .put(CometChatNotificationsConstants.OneOnOnePreferencesKeys.ONE_ON_ONE_QUOTED_REPLIES, 99);

        OneOnOnePreferences prefs = OneOnOnePreferences.fromJson(json);

        assertNull("an unrecognised value is treated as unset", prefs.getQuotedRepliesPreference());
        assertEquals("and the rest of the payload still parses",
                MessagesOptions.SUBSCRIBE_TO_ALL, prefs.getMessagesPreference());
    }

    @Test
    public void qr13_anUnknownGroupQuotedReplyValue_degradesToUnsetWithoutFailing() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatNotificationsConstants.GroupPreferencesKeys.KEY_GROUP_QUOTED_REPLIES, 42);

        assertNull(GroupPreferences.fromJson(json).getQuotedRepliesPreference());
    }

    // ==================== QR-20: the deprecated preferences type reaches the field ====================

    @Test
    public void qr20_theDeprecatedPreferencesType_carriesQuotedRepliesOnBothBuckets() throws Exception {
        OneOnOnePreferences oneOnOne = new OneOnOnePreferences();
        oneOnOne.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES);
        GroupPreferences group = new GroupPreferences();
        group.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_MENTIONS);

        @SuppressWarnings("deprecation")
        PushPreferences original = new PushPreferences();
        original.setOneOnOnePreferences(oneOnOne);
        original.setGroupPreferences(group);

        @SuppressWarnings("deprecation")
        PushPreferences fromJson = PushPreferences.fromJson(original.toJson());
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                fromJson.getOneOnOnePreferences().getQuotedRepliesPreference());
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_MENTIONS,
                fromJson.getGroupPreferences().getQuotedRepliesPreference());

        @SuppressWarnings("deprecation")
        PushPreferences fromMap = PushPreferences.fromMap(original.toMap());
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                fromMap.getOneOnOnePreferences().getQuotedRepliesPreference());

        @SuppressWarnings("deprecation")
        PushPreferences copy = original.clone();
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                copy.getOneOnOnePreferences().getQuotedRepliesPreference());
        assertTrue(original.contentEquals(copy));
    }

    @Test
    public void qr17_theCurrentPreferencesType_carriesQuotedRepliesOnBothBuckets() throws Exception {
        OneOnOnePreferences oneOnOne = new OneOnOnePreferences();
        oneOnOne.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_ALL);
        GroupPreferences group = new GroupPreferences();
        group.setQuotedRepliesPreference(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES);

        NotificationPreferences original = new NotificationPreferences();
        original.setOneOnOnePreferences(oneOnOne);
        original.setGroupPreferences(group);

        NotificationPreferences fromJson = NotificationPreferences.fromJson(original.toJson());

        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_ALL,
                fromJson.getOneOnOnePreferences().getQuotedRepliesPreference());
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                fromJson.getGroupPreferences().getQuotedRepliesPreference());

        NotificationPreferences copy = original.clone();
        assertEquals(QuotedRepliesOptions.SUBSCRIBE_TO_QUOTES_ON_OWN_MESSAGES,
                copy.getGroupPreferences().getQuotedRepliesPreference());
        assertTrue(original.contentEquals(copy));
    }
}
