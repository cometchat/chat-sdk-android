package com.cometchat.chat.messaging;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Action;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Messaging / Action messages.
 *
 * <p>Covers:
 * <ul>
 *   <li>MSG-10 — Group activity (member joined, left, kicked, banned, unbanned, added,
 *       scope changed) is received as an action message. The SDK parses who performed
 *       the action, who/what it was performed on, and generates a human-readable
 *       action description for each activity type.</li>
 *   <li>MSG-11 — A member's role/scope is changed in a group; the action message exposes
 *       both the old and the new scope.</li>
 * </ul>
 */
public class ActionMessageParsingTest {

    private static final String ACTOR_UID = "admin-1";
    private static final String ACTOR_NAME = "Sender " + ACTOR_UID;
    private static final String TARGET_UID = "member-1";
    private static final String TARGET_NAME = "Sender " + TARGET_UID;
    private static final String GUID = "group-1";

    /**
     * A {@code groupMember} action payload: {@code by} performed {@code action},
     * {@code on} the target member, {@code for} the group.
     */
    private JSONObject groupMemberAction(String action, boolean withTarget, JSONObject extras) throws Exception {
        JSONObject entities = new JSONObject()
                .put(CometChatConstants.ActionKeys.KEY_BY, MessagePayloads.entity(
                        CometChatConstants.ActionKeys.KEY_ENTITY_USER,
                        MessagePayloads.user(ACTOR_UID, ACTOR_NAME)))
                .put(CometChatConstants.ActionKeys.KEY_FOR, MessagePayloads.entity(
                        CometChatConstants.ActionKeys.KEY_ENTITY_GROUP,
                        MessagePayloads.group(GUID, "Group " + GUID)));
        if (withTarget) {
            entities.put(CometChatConstants.ActionKeys.KEY_ON, MessagePayloads.entity(
                    CometChatConstants.ActionKeys.KEY_ENTITY_USER,
                    MessagePayloads.user(TARGET_UID, TARGET_NAME)));
        }

        JSONObject data = new JSONObject()
                .put(CometChatConstants.ResponseKeys.KEY_ACTION, action)
                .put(CometChatConstants.ResponseKeys.KEY_ENTITIES, entities);
        if (extras != null) {
            data.put(CometChatConstants.ActionKeys.KEY_EXTRAS, extras);
        }

        return MessagePayloads.envelope(301L, CometChatConstants.CATEGORY_ACTION,
                        CometChatConstants.ActionKeys.ACTION_TYPE_GROUP_MEMBER,
                        GUID, CometChatConstants.RECEIVER_TYPE_GROUP)
                .put(CometChatConstants.MessageKeys.KEY_SENT_AT, 1700000000L)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data);
    }

    // ==================== MSG-10: actor, target and description per activity ====================

    @Test
    public void msg10_joinedAction_identifiesTheActorAndDescribesTheActivity() throws Exception {
        Action action = Action.fromJson(groupMemberAction(CometChatConstants.ActionKeys.ACTION_JOINED, false, null));

        assertEquals(CometChatConstants.ActionKeys.ACTION_JOINED, action.getAction());
        assertNotNull("the acting member must be resolved", action.getActionBy());
        assertEquals(ACTOR_UID, ((User) action.getActionBy()).getUid());
        assertEquals(ACTOR_NAME + " joined", action.getMessage());
    }

    @Test
    public void msg10_leftAction_describesTheActivity() throws Exception {
        Action action = Action.fromJson(groupMemberAction(CometChatConstants.ActionKeys.ACTION_LEFT, false, null));

        assertEquals(ACTOR_NAME + " left", action.getMessage());
    }

    @Test
    public void msg10_kickedAction_namesBothTheActorAndTheTarget() throws Exception {
        Action action = Action.fromJson(groupMemberAction(CometChatConstants.ActionKeys.ACTION_KICKED, true, null));

        assertEquals(ACTOR_UID, ((User) action.getActionBy()).getUid());
        assertEquals("the kicked member must be resolved", TARGET_UID, ((User) action.getActionOn()).getUid());
        assertEquals(ACTOR_NAME + " kicked " + TARGET_NAME, action.getMessage());
    }

    @Test
    public void msg10_bannedAction_namesBothTheActorAndTheTarget() throws Exception {
        Action action = Action.fromJson(groupMemberAction(CometChatConstants.ActionKeys.ACTION_BANNED, true, null));

        assertEquals(ACTOR_NAME + " banned " + TARGET_NAME, action.getMessage());
    }

    @Test
    public void msg10_unbannedAction_namesBothTheActorAndTheTarget() throws Exception {
        Action action = Action.fromJson(groupMemberAction(CometChatConstants.ActionKeys.ACTION_UNBANNED, true, null));

        assertEquals(ACTOR_NAME + " unbanned " + TARGET_NAME, action.getMessage());
    }

    @Test
    public void msg10_memberAddedAction_namesBothTheActorAndTheTarget() throws Exception {
        Action action = Action.fromJson(
                groupMemberAction(CometChatConstants.ActionKeys.ACTION_MEMBER_ADDED, true, null));

        assertEquals(ACTOR_NAME + " added " + TARGET_NAME, action.getMessage());
    }

    @Test
    public void msg10_groupMemberAction_resolvesTheGroupAsTheActionTargetAndReceiver() throws Exception {
        Action action = Action.fromJson(groupMemberAction(CometChatConstants.ActionKeys.ACTION_JOINED, false, null));

        assertTrue("actionFor should resolve to the group", action.getActionFor() instanceof Group);
        assertEquals(GUID, ((Group) action.getActionFor()).getGuid());
        assertNotNull("a groupMember action's receiver is the group it happened in", action.getReceiver());
        assertEquals(GUID, ((Group) action.getReceiver()).getGuid());
    }

    @Test
    public void msg10_actionMessage_preservesEnvelopeFields() throws Exception {
        Action action = Action.fromJson(groupMemberAction(CometChatConstants.ActionKeys.ACTION_JOINED, false, null));

        assertEquals(301L, action.getId());
        assertEquals(CometChatConstants.CATEGORY_ACTION, action.getCategory());
        assertEquals(CometChatConstants.ActionKeys.ACTION_TYPE_GROUP_MEMBER, action.getType());
        assertEquals(CometChatConstants.RECEIVER_TYPE_GROUP, action.getReceiverType());
        assertEquals(1700000000L, action.getSentAt());
        assertNotNull("the raw action payload is retained for app-side inspection", action.getRawData());
    }

    // ==================== MSG-11: scope change exposes old and new roles ====================

    @Test
    public void msg11_scopeChange_exposesBothOldAndNewScope() throws Exception {
        JSONObject extras = new JSONObject().put(CometChatConstants.ActionKeys.KEY_SCOPE, new JSONObject()
                .put(CometChatConstants.ActionKeys.KEY_OLD, CometChatConstants.SCOPE_PARTICIPANT)
                .put(CometChatConstants.ActionKeys.KEY_NEW, CometChatConstants.SCOPE_MODERATOR));

        Action action = Action.fromJson(
                groupMemberAction(CometChatConstants.ActionKeys.ACTION_SCOPE_CHANGED, true, extras));

        assertEquals("the previous role is needed to render 'changed from X to Y'",
                CometChatConstants.SCOPE_PARTICIPANT, action.getOldScope());
        assertEquals(CometChatConstants.SCOPE_MODERATOR, action.getNewScope());
    }

    @Test
    public void msg11_scopeChange_describesTheRoleChange() throws Exception {
        JSONObject extras = new JSONObject().put(CometChatConstants.ActionKeys.KEY_SCOPE, new JSONObject()
                .put(CometChatConstants.ActionKeys.KEY_OLD, CometChatConstants.SCOPE_PARTICIPANT)
                .put(CometChatConstants.ActionKeys.KEY_NEW, CometChatConstants.SCOPE_ADMIN));

        Action action = Action.fromJson(
                groupMemberAction(CometChatConstants.ActionKeys.ACTION_SCOPE_CHANGED, true, extras));

        assertEquals(String.format(CometChatConstants.ActionMessages.ACTION_MEMBER_SCOPE_CHANGED,
                ACTOR_NAME, TARGET_NAME, CometChatConstants.SCOPE_ADMIN), action.getMessage());
    }

    @Test
    public void msg11_scopeChangeToEveryRole_isParsed() throws Exception {
        assertScopeChangeParses(CometChatConstants.SCOPE_PARTICIPANT, CometChatConstants.SCOPE_MODERATOR);
        assertScopeChangeParses(CometChatConstants.SCOPE_MODERATOR, CometChatConstants.SCOPE_ADMIN);
        assertScopeChangeParses(CometChatConstants.SCOPE_ADMIN, CometChatConstants.SCOPE_PARTICIPANT);
    }

    private void assertScopeChangeParses(String oldScope, String newScope) throws Exception {
        JSONObject extras = new JSONObject().put(CometChatConstants.ActionKeys.KEY_SCOPE, new JSONObject()
                .put(CometChatConstants.ActionKeys.KEY_OLD, oldScope)
                .put(CometChatConstants.ActionKeys.KEY_NEW, newScope));

        Action action = Action.fromJson(
                groupMemberAction(CometChatConstants.ActionKeys.ACTION_SCOPE_CHANGED, true, extras));

        assertEquals(oldScope + " -> " + newScope + " (old)", oldScope, action.getOldScope());
        assertEquals(oldScope + " -> " + newScope + " (new)", newScope, action.getNewScope());
    }

    @Test
    public void msg11_scopeChangeWithoutExtras_leavesScopesUnset() throws Exception {
        Action action = Action.fromJson(
                groupMemberAction(CometChatConstants.ActionKeys.ACTION_SCOPE_CHANGED, true, null));

        assertEquals("no extras block means no old scope to report", null, action.getOldScope());
        assertEquals(null, action.getNewScope());
    }
}
