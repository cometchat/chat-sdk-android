package com.cometchat.chat.moderation;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.FlagDetail;
import com.cometchat.chat.models.FlagReason;

import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

/**
 * Feature Area: Content Moderation / flagging a message and the flag-reason catalog.
 *
 * <p>Covers:
 * <ul>
 *   <li>MOD-01 — a flag detail carries a reason id plus an optional remark; an omitted remark is
 *       "not provided" while a preserved remark is distinguished from it.</li>
 *   <li>MOD-02 — a flag reason has id/name/description and creation/update timestamps; a zero
 *       timestamp is treated as "no timestamp" (stored as 0, not a bogus 1970 date).</li>
 * </ul>
 */
public class ModerationTest {

    // ==================== MOD-01: flag detail ====================

    @Test
    public void mod01_flagDetail_carriesReasonAndRemark() {
        FlagDetail detail = new FlagDetail("spam", "Repeated promotional links");

        assertEquals("spam", detail.getReasonId());
        assertEquals("Repeated promotional links", detail.getRemark());
    }

    @Test
    public void mod01_flagDetailToMap_omitsAnAbsentRemark() {
        FlagDetail detail = new FlagDetail();
        detail.setReasonId("harassment");

        HashMap<String, String> map = detail.toMap();

        assertEquals("harassment", map.get(CometChatConstants.FlagDetail.ID));
        assertNull("an absent remark is not written to the payload",
                map.get(CometChatConstants.FlagDetail.REASON));
    }

    @Test
    public void mod01_flagDetailToMap_preservesAProvidedRemark() {
        FlagDetail detail = new FlagDetail("other", "See attached screenshot");

        HashMap<String, String> map = detail.toMap();

        assertEquals("See attached screenshot", map.get(CometChatConstants.FlagDetail.REASON));
    }

    @Test
    public void mod01_flagDetailRoundTripsThroughAMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(CometChatConstants.FlagDetail.ID, "spam");
        map.put(CometChatConstants.FlagDetail.REASON, "Bot account");

        FlagDetail detail = new FlagDetail().fromJson(map);

        assertEquals("spam", detail.getReasonId());
        assertEquals("Bot account", detail.getRemark());
    }

    // ==================== MOD-02: flag reason catalog ====================

    @Test
    public void mod02_flagReason_exposesIdNameDescriptionAndTimestamps() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.FlagReasonsKeys.ID, "spam")
                .put(CometChatConstants.FlagReasonsKeys.NAME, "Spam")
                .put(CometChatConstants.FlagReasonsKeys.DESCRIPTION, "Unsolicited promotion")
                .put(CometChatConstants.FlagReasonsKeys.CREATED_AT, 1700000000L)
                .put(CometChatConstants.FlagReasonsKeys.UPDATED_AT, 1700000500L);

        FlagReason reason = FlagReason.fromJson(json);

        assertEquals("spam", reason.getId());
        assertEquals("Spam", reason.getName());
        assertEquals("Unsolicited promotion", reason.getDescription());
        assertEquals(1700000000L, reason.getCreatedAt());
        assertEquals(1700000500L, reason.getUpdatedAt());
    }

    @Test
    public void mod02_zeroTimestamp_isStoredAsZeroNotABogusDate() throws Exception {
        JSONObject json = new JSONObject()
                .put(CometChatConstants.FlagReasonsKeys.ID, "other")
                .put(CometChatConstants.FlagReasonsKeys.NAME, "Other")
                .put(CometChatConstants.FlagReasonsKeys.UPDATED_AT, 0L);

        FlagReason reason = FlagReason.fromJson(json);

        assertEquals("a zero update timestamp means 'no timestamp', stored as 0",
                0L, reason.getUpdatedAt());
    }

    @Test
    public void mod02_missingIdAndName_stayUnsetWithoutCrashing() throws Exception {
        // Android leaves id/name null when the backend omits them (rather than "" as in Flutter);
        // the point of the row is that a sparse reason still parses without throwing.
        FlagReason reason = FlagReason.fromJson(new JSONObject());

        assertNull(reason.getId());
        assertNull(reason.getName());
    }
}
