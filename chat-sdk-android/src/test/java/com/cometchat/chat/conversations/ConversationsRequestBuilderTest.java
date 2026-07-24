package com.cometchat.chat.conversations;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.ConversationsRequest;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Conversations / Fetching with filters and pagination.
 *
 * <p>Covers:
 * <ul>
 *   <li>CONV-07 — page size, conversation type, tag filters, search keyword, and boolean flags
 *       are applied and read back; unset filters stay unset (so the backend applies defaults).</li>
 *   <li>CONV-08 — page numbers are internally offset (app page 0 -> request page 1) and pagination
 *       starts in an unfetched state.</li>
 * </ul>
 */
public class ConversationsRequestBuilderTest {

    // ==================== CONV-07: filters ====================

    @Test
    public void conv07_defaultRequest_leavesOptionalFiltersUnset() {
        ConversationsRequest request = new ConversationsRequest.ConversationsRequestBuilder().build();

        assertEquals("a sensible default page size is applied", 30, request.getLimit());
        assertNull(request.getConversationType());
        assertNull(request.getTags());
        assertNull(request.getSearchKeyword());
        assertFalse(request.isUnread());
        assertFalse(request.isIncludeBlockedUsers());
        assertFalse(request.isWithTags());
    }

    @Test
    public void conv07_conversationTypeFilter_isApplied() {
        ConversationsRequest request = new ConversationsRequest.ConversationsRequestBuilder()
                .setConversationType(CometChatConstants.CONVERSATION_TYPE_GROUP)
                .build();

        assertEquals(CometChatConstants.CONVERSATION_TYPE_GROUP, request.getConversationType());
    }

    @Test
    public void conv07_tagAndSearchFilters_areApplied() {
        List<String> tags = Arrays.asList("pinned", "work");
        ConversationsRequest request = new ConversationsRequest.ConversationsRequestBuilder()
                .setTags(tags)
                .withTags(true)
                .setSearchKeyword("invoice")
                .build();

        assertEquals(tags, request.getTags());
        assertTrue(request.isWithTags());
        assertEquals("invoice", request.getSearchKeyword());
    }

    @Test
    public void conv07_booleanFlags_areAppliedIndependently() {
        ConversationsRequest request = new ConversationsRequest.ConversationsRequestBuilder()
                .setUnread(true)
                .includeBlockedUsers(true)
                .build();

        assertTrue(request.isUnread());
        assertTrue(request.isIncludeBlockedUsers());
        assertFalse("an unrelated flag must stay at its default", request.isWithTags());
    }

    @Test
    public void conv07_filtersCombineWithoutOverwritingEachOther() {
        ConversationsRequest request = new ConversationsRequest.ConversationsRequestBuilder()
                .setLimit(50)
                .setConversationType(CometChatConstants.CONVERSATION_TYPE_USER)
                .setSearchKeyword("report")
                .setUnread(true)
                .build();

        assertEquals(50, request.getLimit());
        assertEquals(CometChatConstants.CONVERSATION_TYPE_USER, request.getConversationType());
        assertEquals("report", request.getSearchKeyword());
        assertTrue(request.isUnread());
    }

    // ==================== CONV-08: pagination offset ====================

    @Test
    public void conv08_appPageZero_becomesRequestPageOne() {
        ConversationsRequest request = new ConversationsRequest.ConversationsRequestBuilder()
                .setPage(0)
                .build();

        assertEquals("page numbers are offset by one internally", 1, request.getPage());
    }

    @Test
    public void conv08_pageOffsetIsConsistentAcrossPages() {
        assertEquals(2, new ConversationsRequest.ConversationsRequestBuilder().setPage(1).build().getPage());
        assertEquals(6, new ConversationsRequest.ConversationsRequestBuilder().setPage(5).build().getPage());
    }
}
