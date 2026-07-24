package com.cometchat.chat.users;

import com.cometchat.chat.core.BlockedUsersRequest;
import com.cometchat.chat.core.UsersRequest;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Users / Directory search, listing, and viewing blocked users.
 *
 * <p>Covers:
 * <ul>
 *   <li>USR-09 — directory filters (keyword, tags, roles, status, friends-only).</li>
 *   <li>USR-10 — pagination parameters (page size, page).</li>
 *   <li>USR-11 — optional filters omitted when not set.</li>
 *   <li>USR-15 — the blocked-user list is filterable by direction and paginated like the directory.</li>
 * </ul>
 */
public class UserRequestBuildersTest {

    // ==================== USR-11: defaults / omitted filters ====================

    @Test
    public void usr11_defaultRequest_leavesOptionalFiltersUnset() {
        UsersRequest request = new UsersRequest.UsersRequestBuilder().build();

        assertEquals(30, request.getLimit());
        assertNull(request.getSearchKeyword());
        assertNull(request.getRoles());
        assertNull(request.getTags());
        assertNull(request.getUserStatus());
        assertFalse(request.isFriendsOnly());
        assertFalse(request.isHideBlockedUsers());
    }

    // ==================== USR-09: directory filters ====================

    @Test
    public void usr09_searchKeywordAndStatusFilters_areApplied() {
        UsersRequest request = new UsersRequest.UsersRequestBuilder()
                .setSearchKeyword("kevin")
                .setUserStatus(UsersRequest.USER_STATUS_ONLINE)
                .build();

        assertEquals("kevin", request.getSearchKeyword());
        assertEquals(UsersRequest.USER_STATUS_ONLINE, request.getUserStatus());
    }

    @Test
    public void usr09_tagAndRoleFilters_areApplied() {
        List<String> roles = Arrays.asList("admin", "moderator");
        List<String> tags = Arrays.asList("vip");
        UsersRequest request = new UsersRequest.UsersRequestBuilder()
                .setRoles(roles)
                .setTags(tags)
                .withTags(true)
                .build();

        assertTrue("every configured role must be present", request.getRoles().containsAll(roles));
        assertEquals(tags, request.getTags());
        assertTrue(request.isWithTags());
    }

    @Test
    public void usr09_friendsOnlyAndHideBlocked_areAppliedIndependently() {
        UsersRequest request = new UsersRequest.UsersRequestBuilder()
                .friendsOnly(true)
                .hideBlockedUsers(true)
                .build();

        assertTrue(request.isFriendsOnly());
        assertTrue(request.isHideBlockedUsers());
    }

    // ==================== USR-10: pagination ====================

    @Test
    public void usr10_paginationParameters_areApplied() {
        UsersRequest request = new UsersRequest.UsersRequestBuilder()
                .setLimit(20)
                .setPage(0)
                .build();

        assertEquals(20, request.getLimit());
        assertEquals("app page 0 maps to request page 1", 1, request.getPage());
    }

    // ==================== USR-15: blocked-user list ====================

    @Test
    public void usr15_blockedUsersRequest_defaultsToBothDirections() {
        BlockedUsersRequest request = new BlockedUsersRequest.BlockedUsersRequestBuilder().build();

        assertEquals(30, request.getLimit());
        assertEquals("the default direction lists blocks in both directions",
                BlockedUsersRequest.DIRECTION_BOTH, request.getDirection());
    }

    @Test
    public void usr15_blockedUsersRequest_filtersByDirection() {
        BlockedUsersRequest blockedByMe = new BlockedUsersRequest.BlockedUsersRequestBuilder()
                .setDirection(BlockedUsersRequest.DIRECTION_BLOCKED_BY_ME)
                .build();
        BlockedUsersRequest hasBlockedMe = new BlockedUsersRequest.BlockedUsersRequestBuilder()
                .setDirection(BlockedUsersRequest.DIRECTION_HAS_BLOCKED_ME)
                .build();

        assertEquals(BlockedUsersRequest.DIRECTION_BLOCKED_BY_ME,
                blockedByMe.getDirection());
        assertEquals(BlockedUsersRequest.DIRECTION_HAS_BLOCKED_ME,
                hasBlockedMe.getDirection());
    }

    @Test
    public void usr15_blockedUsersRequest_paginatesLikeTheDirectory() {
        BlockedUsersRequest request = new BlockedUsersRequest.BlockedUsersRequestBuilder()
                .setLimit(10)
                .setPage(2)
                .build();

        assertEquals(10, request.getLimit());
        assertEquals(3, request.getPage());
    }
}
