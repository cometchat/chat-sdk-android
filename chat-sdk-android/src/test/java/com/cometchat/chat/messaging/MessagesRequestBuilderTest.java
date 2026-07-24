package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.MessagesRequest;
import com.cometchat.chat.enums.AttachmentType;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Messaging / Fetching message history.
 *
 * <p>Covers MSG-32 — App requests a page of messages, filtered by user/group, unread status,
 * message category/type, tags, mentions, attachments, links or reactions. The request builder
 * applies each filter independently and combines them, giving fine-grained control over which
 * historical messages are fetched.
 *
 * <p>MSG-33 (fetchNext/fetchPrevious returning an empty result mid-flight or past the last
 * page) is not covered here: the in-progress and has-next flags are set only by a completed
 * network fetch, and the fetch callbacks post to the Android main looper. That guard is
 * exercised by the instrumented suite.
 */
public class MessagesRequestBuilderTest {

    // ==================== Defaults ====================

    @Test
    public void msg32_defaultRequest_appliesNoFiltersAndADefaultLimit() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder().build();

        assertEquals("a default page size must be applied", 30, request.getLimit());
        assertNull(request.getUID());
        assertNull(request.getGUID());
        assertNull(request.getSearchKeyword());
        assertNull(request.getCategory());
        assertNull(request.getType());
        assertFalse(request.isUnread());
        assertFalse(request.isHideDeleted());
        assertFalse(request.isHideReplies());
        assertFalse(request.isHasAttachments());
        assertFalse(request.isHasLinks());
        assertFalse(request.isHasMentions());
        assertFalse(request.isHasReactions());
        assertEquals("an unset messageId must not act as a real cursor", -1L, request.getMessageId());
        assertEquals(-1L, request.getTimestamp());
        assertEquals(-1L, request.getUpdatedAfter());
        assertEquals(-1L, request.getParentMessageId());
    }

    // ==================== Conversation scoping ====================

    @Test
    public void msg32_scopingToAUser_setsOnlyTheUserFilter() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setUID("user-1")
                .build();

        assertEquals("user-1", request.getUID());
        assertNull("scoping to a user must not also scope to a group", request.getGUID());
    }

    @Test
    public void msg32_scopingToAGroup_setsOnlyTheGroupFilter() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setGUID("group-1")
                .build();

        assertEquals("group-1", request.getGUID());
        assertNull(request.getUID());
    }

    @Test
    public void msg32_scopingToAThread_setsTheParentMessageId() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setGUID("group-1")
                .setParentMessageId(9001L)
                .build();

        assertEquals(9001L, request.getParentMessageId());
        assertEquals("group-1", request.getGUID());
    }

    // ==================== Individual filters ====================

    @Test
    public void msg32_unreadFilter_isAppliedIndependently() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setUnread(true)
                .build();

        assertTrue(request.isUnread());
        assertFalse("setting unread must not switch on unrelated filters", request.isHasAttachments());
        assertFalse(request.isHideDeleted());
    }

    @Test
    public void msg32_categoryAndTypeFilters_areAppliedIndependently() {
        MessagesRequest byCategory = new MessagesRequest.MessagesRequestBuilder()
                .setCategory(CometChatConstants.CATEGORY_MESSAGE)
                .build();
        MessagesRequest byType = new MessagesRequest.MessagesRequestBuilder()
                .setType(CometChatConstants.MESSAGE_TYPE_TEXT)
                .build();

        assertEquals(CometChatConstants.CATEGORY_MESSAGE, byCategory.getCategory());
        assertNull("a category filter must not imply a type filter", byCategory.getType());
        assertEquals(CometChatConstants.MESSAGE_TYPE_TEXT, byType.getType());
        assertNull(byType.getCategory());
    }

    @Test
    public void msg32_multiValueCategoryAndTypeFilters_arePreservedAsLists() {
        List<String> categories = Arrays.asList(
                CometChatConstants.CATEGORY_MESSAGE, CometChatConstants.CATEGORY_CUSTOM);
        List<String> types = Arrays.asList(
                CometChatConstants.MESSAGE_TYPE_TEXT, CometChatConstants.MESSAGE_TYPE_IMAGE);

        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setCategories(categories)
                .setTypes(types)
                .build();

        assertEquals(categories, request.getCategories());
        assertEquals(types, request.getTypes());
    }

    @Test
    public void msg32_tagFilters_areApplied() {
        List<String> tags = Arrays.asList("pinned", "important");

        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setTags(tags)
                .withTags(true)
                .build();

        assertEquals(tags, request.getTags());
        assertTrue(request.isWithTags());
    }

    @Test
    public void msg32_attachmentFilters_areApplied() {
        List<AttachmentType> attachmentTypes = Arrays.asList(AttachmentType.IMAGE, AttachmentType.VIDEO);

        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setAttachmentTypes(attachmentTypes)
                .build();

        assertEquals(attachmentTypes, request.getAttachmentTypes());
    }

    @Test
    public void msg32_mentionFilters_areApplied() {
        List<String> mentioned = Collections.singletonList("user-7");

        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setMentionedUIDs(mentioned)
                .mentionsWithTagInfo(true)
                .mentionsWithBlockedInfo(true)
                .build();

        assertEquals(mentioned, request.getMentionedUIDs());
        assertTrue(request.isMentionsWithTagInfo());
        assertTrue(request.isMentionsWithBlockedInfo());
    }

    @Test
    public void msg32_searchKeywordFilter_isApplied() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setSearchKeyword("invoice")
                .build();

        assertEquals("invoice", request.getSearchKeyword());
    }

    @Test
    public void msg32_updatedAfterAndUpdatesOnly_areApplied() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setUpdatedAfter(1700000000L)
                .updatesOnly(true)
                .build();

        assertEquals(1700000000L, request.getUpdatedAfter());
        assertTrue(request.isUpdatesOnly());
    }

    @Test
    public void msg32_hideFilters_areApplied() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .hideReplies(true)
                .hideDeletedMessages(true)
                .hideMessagesFromBlockedUsers(true)
                .build();

        assertTrue(request.isHideReplies());
        assertTrue(request.isHideDeleted());
        assertTrue(request.isHideMessagesFromBlockedUsers());
    }

    // ==================== Filters combine ====================

    @Test
    public void msg32_multipleFiltersCombineWithoutOverwritingEachOther() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setGUID("group-1")
                .setLimit(50)
                .setUnread(true)
                .setCategory(CometChatConstants.CATEGORY_MESSAGE)
                .setType(CometChatConstants.MESSAGE_TYPE_TEXT)
                .setTags(Arrays.asList("pinned"))
                .setSearchKeyword("report")
                .hideDeletedMessages(true)
                .build();

        assertEquals("group-1", request.getGUID());
        assertEquals(50, request.getLimit());
        assertTrue(request.isUnread());
        assertEquals(CometChatConstants.CATEGORY_MESSAGE, request.getCategory());
        assertEquals(CometChatConstants.MESSAGE_TYPE_TEXT, request.getType());
        assertEquals(Arrays.asList("pinned"), request.getTags());
        assertEquals("report", request.getSearchKeyword());
        assertTrue(request.isHideDeleted());
    }

    @Test
    public void msg32_contentPresenceFiltersCombine() {
        MessagesRequest request = new MessagesRequest.MessagesRequestBuilder()
                .setUID("user-1")
                .hasAttachments(true)
                .hasLinks(true)
                .hasMentions(true)
                .hasReactions(true)
                .build();

        assertTrue(request.isHasAttachments());
        assertTrue(request.isHasLinks());
        assertTrue(request.isHasMentions());
        assertTrue(request.isHasReactions());
        assertEquals("user-1", request.getUID());
    }

    // ==================== Pagination cursors ====================

    @Test
    public void msg32_paginationCursors_areApplied() {
        MessagesRequest byMessageId = new MessagesRequest.MessagesRequestBuilder()
                .setUID("user-1")
                .setMessageId(555L)
                .build();
        MessagesRequest byTimestamp = new MessagesRequest.MessagesRequestBuilder()
                .setUID("user-1")
                .setTimestamp(1700000000L)
                .build();

        assertEquals(555L, byMessageId.getMessageId());
        assertEquals("setting a message cursor must not also set a timestamp cursor",
                -1L, byMessageId.getTimestamp());
        assertEquals(1700000000L, byTimestamp.getTimestamp());
        assertEquals(-1L, byTimestamp.getMessageId());
    }

    @Test
    public void msg32_limitIsHonouredAsConfigured() {
        assertEquals(1, new MessagesRequest.MessagesRequestBuilder().setLimit(1).build().getLimit());
        assertEquals(100, new MessagesRequest.MessagesRequestBuilder().setLimit(100).build().getLimit());
    }
}
