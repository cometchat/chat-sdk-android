package com.cometchat.chat.groups;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.GroupMember;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Groups / Fetching groups and members.
 *
 * <p>Covers:
 * <ul>
 *   <li>GRP-01 — group identity, privacy type, member count, joined/banned status.</li>
 *   <li>GRP-02 — group timestamps convert consistently and stay unset when absent.</li>
 *   <li>GRP-03 — a minimal group (id, name, type) is usable; emoji/special chars survive.</li>
 *   <li>GRP-07 — member scope, status, join time; join time as number or string;
 *       status normalization ("available" -> "online", missing -> "offline").</li>
 *   <li>GRP-08 — a group member's block-relationship flags (see divergence note).</li>
 * </ul>
 *
 * <p><b>Android divergence (GRP-08):</b> {@code GroupMember.listFromJSONArray} does not copy the
 * block-relationship flags onto members, and {@code User}'s flags are primitive booleans, so a
 * member's "blocked" state reads as {@code false} rather than "unknown". The test documents this.
 */
public class GroupParsingTest {

    private JSONObject group(String guid, String name, String type) throws Exception {
        return new JSONObject()
                .put(CometChatConstants.GroupKeys.KEY_GROUP_GUID, guid)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_NAME, name)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_TYPE, type);
    }

    // ==================== GRP-01: identity, type, counts, membership ====================

    @Test
    public void grp01_publicGroup_exposesIdentityTypeAndCounts() throws Exception {
        JSONObject json = group("g-1", "Team Rocket", CometChatConstants.GROUP_TYPE_PUBLIC)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBERS_COUNT, 42)
                .put(CometChatConstants.GroupKeys.KEY_HAS_JOINED, true);

        Group grp = Group.fromJson(json.toString());

        assertEquals("g-1", grp.getGuid());
        assertEquals("Team Rocket", grp.getName());
        assertEquals(CometChatConstants.GROUP_TYPE_PUBLIC, grp.getGroupType());
        assertEquals(42, grp.getMembersCount());
        assertTrue(grp.isJoined());
    }

    @Test
    public void grp01_allPrivacyTypesAreRepresented() throws Exception {
        assertEquals(CometChatConstants.GROUP_TYPE_PUBLIC,
                Group.fromJson(group("a", "A", CometChatConstants.GROUP_TYPE_PUBLIC).toString()).getGroupType());
        assertEquals(CometChatConstants.GROUP_TYPE_PRIVATE,
                Group.fromJson(group("b", "B", CometChatConstants.GROUP_TYPE_PRIVATE).toString()).getGroupType());
        assertEquals(CometChatConstants.GROUP_TYPE_PASSWORD,
                Group.fromJson(group("c", "C", CometChatConstants.GROUP_TYPE_PASSWORD).toString()).getGroupType());
    }

    @Test
    public void grp01_memberCountDefaultsToZeroWhenAbsent() throws Exception {
        Group grp = Group.fromJson(group("g-2", "No Count", CometChatConstants.GROUP_TYPE_PUBLIC).toString());

        assertEquals(0, grp.getMembersCount());
    }

    @Test
    public void grp01_falseMembershipAndBanFlags_arePreservedNotDropped() throws Exception {
        JSONObject json = group("g-3", "Flags", CometChatConstants.GROUP_TYPE_PUBLIC)
                .put(CometChatConstants.GroupKeys.KEY_HAS_JOINED, false)
                .put(CometChatConstants.GroupKeys.GROUP_KEY_IS_BANNED, false);

        Group grp = Group.fromJson(json.toString());

        assertFalse("a false 'joined' must survive rather than be dropped", grp.isJoined());
        assertFalse(grp.isBannedFromGroup());
    }

    @Test
    public void grp01_bannedStatus_isPreserved() throws Exception {
        JSONObject json = group("g-4", "Banned", CometChatConstants.GROUP_TYPE_PUBLIC)
                .put(CometChatConstants.GroupKeys.GROUP_KEY_IS_BANNED, true);

        Group grp = Group.fromJson(json.toString());

        assertTrue(grp.isBannedFromGroup());
    }

    // ==================== GRP-02: timestamps ====================

    @Test
    public void grp02_timestamps_convertConsistently() throws Exception {
        JSONObject json = group("g-5", "Timed", CometChatConstants.GROUP_TYPE_PUBLIC)
                .put(CometChatConstants.GroupKeys.KEY_CREATED_AT, 1700000000L)
                .put(CometChatConstants.GroupKeys.KEY_UPDATED_AT, 1700000500L)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_JOINED_AT, 1700000100L);

        Group grp = Group.fromJson(json.toString());

        assertEquals(1700000000L, grp.getCreatedAt());
        assertEquals(1700000500L, grp.getUpdatedAt());
        assertEquals(1700000100L, grp.getJoinedAt());
    }

    @Test
    public void grp02_timestamps_stayUnsetWhenBackendOmitsThem() throws Exception {
        Group grp = Group.fromJson(group("g-6", "Untimed", CometChatConstants.GROUP_TYPE_PUBLIC).toString());

        assertEquals("created/updated default to 0 when absent", 0L, grp.getCreatedAt());
        assertEquals(0L, grp.getUpdatedAt());
        assertEquals("joinedAt uses -1 as its 'not joined / unknown' sentinel", -1L, grp.getJoinedAt());
    }

    // ==================== GRP-03: minimal + special characters ====================

    @Test
    public void grp03_minimalGroup_isUsable() throws Exception {
        Group grp = Group.fromJson(group("g-7", "Minimal", CometChatConstants.GROUP_TYPE_PUBLIC).toString());

        assertEquals("g-7", grp.getGuid());
        assertEquals("Minimal", grp.getName());
        assertNull("optional description stays unset", grp.getDescription());
        assertNull("optional icon stays unset", grp.getIcon());
    }

    @Test
    public void grp03_specialCharactersAndEmojiInName_survivWithoutCorruption() throws Exception {
        String fancyName = "Café ☕ 会議 مجموعة 🎉";
        Group grp = Group.fromJson(group("g-8", fancyName, CometChatConstants.GROUP_TYPE_PUBLIC).toString());

        assertEquals(fancyName, grp.getName());
    }

    // ==================== GRP-07: member scope, status, join time ====================

    private JSONObject memberListWrapper(JSONObject... members) throws Exception {
        JSONArray data = new JSONArray();
        for (JSONObject m : members) {
            data.put(m);
        }
        return new JSONObject().put(CometChatConstants.ResponseKeys.KEY_DATA, data);
    }

    @Test
    public void grp07_memberList_exposesScopeStatusAndJoinTime() throws Exception {
        JSONObject member = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "m-1")
                .put(CometChatConstants.UserKeys.USER_KEY_NAME, "Alice")
                .put(CometChatConstants.UserKeys.USER_KEY_STATUS, CometChatConstants.USER_STATUS_ONLINE)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE, CometChatConstants.SCOPE_ADMIN)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_JOINED_AT, 1700000000L);

        List<GroupMember> members = GroupMember.listFromJSONArray(memberListWrapper(member).toString());

        assertEquals(1, members.size());
        GroupMember m = members.get(0);
        assertEquals("m-1", m.getUid());
        assertEquals(CometChatConstants.SCOPE_ADMIN, m.getScope());
        assertEquals(CometChatConstants.USER_STATUS_ONLINE, m.getStatus());
        assertEquals(1700000000L, m.getJoinedAt());
    }

    @Test
    public void grp07_joinTimeAcceptedAsNumericString() throws Exception {
        JSONObject member = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "m-2")
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE, CometChatConstants.SCOPE_PARTICIPANT)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_JOINED_AT, "1700000123");

        List<GroupMember> members = GroupMember.listFromJSONArray(memberListWrapper(member).toString());

        assertEquals("a join time sent as a numeric string must still parse",
                1700000123L, members.get(0).getJoinedAt());
    }

    @Test
    public void grp07_memberWithoutStatus_defaultsToOffline() throws Exception {
        JSONObject member = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "m-3")
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE, CometChatConstants.SCOPE_PARTICIPANT);

        List<GroupMember> members = GroupMember.listFromJSONArray(memberListWrapper(member).toString());

        assertEquals(CometChatConstants.USER_STATUS_OFFLINE, members.get(0).getStatus());
    }

    @Test
    public void grp07_memberAvailableStatus_isNormalizedToOnline() throws Exception {
        JSONObject member = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "m-4")
                .put(CometChatConstants.UserKeys.USER_KEY_STATUS, "available")
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE, CometChatConstants.SCOPE_MODERATOR);

        List<GroupMember> members = GroupMember.listFromJSONArray(memberListWrapper(member).toString());

        assertEquals(CometChatConstants.USER_STATUS_ONLINE, members.get(0).getStatus());
        assertEquals(CometChatConstants.SCOPE_MODERATOR, members.get(0).getScope());
    }

    @Test
    public void grp07_multipleMembersEachRetainTheirOwnScope() throws Exception {
        JSONObject admin = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "admin")
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE, CometChatConstants.SCOPE_ADMIN);
        JSONObject participant = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "member")
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE, CometChatConstants.SCOPE_PARTICIPANT);

        List<GroupMember> members = GroupMember.listFromJSONArray(memberListWrapper(admin, participant).toString());

        assertEquals(2, members.size());
        assertEquals(CometChatConstants.SCOPE_ADMIN, members.get(0).getScope());
        assertEquals(CometChatConstants.SCOPE_PARTICIPANT, members.get(1).getScope());
    }

    // ==================== GRP-08: block flags on members (Android behavior) ====================

    @Test
    public void grp08_memberBlockFlags_readAsFalseOnAndroid() throws Exception {
        // Even when the backend reports block flags on a member entry, the Android member-list
        // parser does not copy them onto GroupMember; combined with primitive booleans this
        // yields false rather than the Flutter SDK's "unknown".
        JSONObject member = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, "m-5")
                .put(CometChatConstants.GroupKeys.KEY_GROUP_MEMBER_SCOPE, CometChatConstants.SCOPE_PARTICIPANT)
                .put(CometChatConstants.UserKeys.USER_KEY_HAS_BLOCKED_ME, true)
                .put(CometChatConstants.UserKeys.USER_KEY_BLOCKED_BY_ME, true);

        List<GroupMember> members = GroupMember.listFromJSONArray(memberListWrapper(member).toString());

        assertFalse("member-list parsing does not surface block flags", members.get(0).isHasBlockedMe());
        assertFalse(members.get(0).isBlockedByMe());
    }
}
