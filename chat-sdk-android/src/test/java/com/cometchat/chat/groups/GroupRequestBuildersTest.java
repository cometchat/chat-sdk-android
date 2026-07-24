package com.cometchat.chat.groups;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.BannedGroupMembersRequest;
import com.cometchat.chat.core.GroupMembersRequest;
import com.cometchat.chat.core.GroupsRequest;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Groups / Fetching groups, members, and banned members with filters.
 *
 * <p>Covers:
 * <ul>
 *   <li>GRP-06 — group list: paging, search keyword, joined-only, tags; omitted when not set.</li>
 *   <li>GRP-09 — member list: paging, search, scope filters, status filter.</li>
 *   <li>GRP-12 — banned-member list supports the same pagination/search/scope filters.</li>
 * </ul>
 */
public class GroupRequestBuildersTest {

    // ==================== GRP-06: groups list ====================

    @Test
    public void grp06_defaultGroupsRequest_leavesFiltersUnset() {
        GroupsRequest request = new GroupsRequest.GroupsRequestBuilder().build();

        assertEquals(30, request.getLimit());
        assertNull(request.getSearchKeyword());
        assertNull(request.getTags());
        assertFalse(request.isJoinedOnly());
        assertFalse(request.isWithTags());
    }

    @Test
    public void grp06_groupsRequestFilters_areApplied() {
        List<String> tags = Arrays.asList("public", "featured");
        GroupsRequest request = new GroupsRequest.GroupsRequestBuilder()
                .setLimit(25)
                .setSearchKeyWord("dev")
                .joinedOnly(true)
                .setTags(tags)
                .withTags(true)
                .build();

        assertEquals(25, request.getLimit());
        assertEquals("dev", request.getSearchKeyword());
        assertTrue(request.isJoinedOnly());
        assertEquals(tags, request.getTags());
        assertTrue(request.isWithTags());
    }

    // ==================== GRP-09: group members ====================

    @Test
    public void grp09_memberRequestTargetsTheGivenGroup() {
        GroupMembersRequest request = new GroupMembersRequest.GroupMembersRequestBuilder("grp-1").build();

        assertEquals("grp-1", request.getGuid());
    }

    @Test
    public void grp09_memberRequestFilters_areApplied() {
        List<String> scopes = Arrays.asList(CometChatConstants.SCOPE_ADMIN, CometChatConstants.SCOPE_MODERATOR);
        GroupMembersRequest request = new GroupMembersRequest.GroupMembersRequestBuilder("grp-1")
                .setLimit(15)
                .setSearchKeyword("ali")
                .setScopes(scopes)
                .setStatus(CometChatConstants.USER_STATUS_ONLINE)
                .build();

        assertEquals(15, request.getLimit());
        assertEquals("ali", request.getSearchKeyword());
        assertEquals(scopes, request.getScopes());
        assertEquals(CometChatConstants.USER_STATUS_ONLINE, request.getStatus());
    }

    @Test
    public void grp09_memberScopeFilterCombinesMultipleScopes() {
        List<String> scopes = Arrays.asList(
                CometChatConstants.SCOPE_ADMIN,
                CometChatConstants.SCOPE_MODERATOR,
                CometChatConstants.SCOPE_PARTICIPANT);
        GroupMembersRequest request = new GroupMembersRequest.GroupMembersRequestBuilder("grp-2")
                .setScopes(scopes)
                .build();

        assertEquals(3, request.getScopes().size());
    }

    // ==================== GRP-12: banned members ====================

    @Test
    public void grp12_bannedMemberRequestTargetsTheGivenGroup() {
        BannedGroupMembersRequest request =
                new BannedGroupMembersRequest.BannedGroupMembersRequestBuilder("grp-1").build();

        assertEquals("grp-1", request.getGuid());
    }

    @Test
    public void grp12_bannedMemberRequestSupportsTheSameFilters() {
        List<String> scopes = Arrays.asList(CometChatConstants.SCOPE_PARTICIPANT);
        BannedGroupMembersRequest request =
                new BannedGroupMembersRequest.BannedGroupMembersRequestBuilder("grp-1")
                        .setLimit(10)
                        .setSearchKeyword("spam")
                        .setScopes(scopes)
                        .build();

        assertEquals(10, request.getLimit());
        assertEquals("spam", request.getSearchKeyword());
        assertEquals(scopes, request.getScopes());
    }
}
