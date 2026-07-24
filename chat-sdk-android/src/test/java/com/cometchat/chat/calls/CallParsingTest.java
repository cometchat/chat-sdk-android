package com.cometchat.chat.calls;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.Call;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Calls / Placing/receiving calls, lifecycle, participants, resilience.
 *
 * <p>Covers:
 * <ul>
 *   <li>CALL-01 / MSG-12 — audio vs video, individual vs group, initiator/receiver identity.</li>
 *   <li>CALL-02 — current status and timestamps (initiated, joined).</li>
 *   <li>CALL-03 — initiator and receiver resolved to full User/Group objects.</li>
 *   <li>CALL-04 — missing optional details leave fields unset rather than guessed.</li>
 * </ul>
 *
 * <p>{@code Call.fromJson} touches {@code CometChat.getLoggedInUser()} only inside the
 * {@code data.mentions} block, so these payloads omit {@code mentions}.
 */
public class CallParsingTest {

    private JSONObject userEntity(String uid, String name) throws Exception {
        JSONObject user = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, uid)
                .put(CometChatConstants.UserKeys.USER_KEY_NAME, name);
        return new JSONObject().put(CometChatConstants.ActionKeys.KEY_ENTITY, user);
    }

    private JSONObject groupReceiverEntity(String guid, String name) throws Exception {
        JSONObject group = new JSONObject()
                .put(CometChatConstants.GroupKeys.KEY_GROUP_GUID, guid)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_NAME, name)
                .put(CometChatConstants.GroupKeys.KEY_GROUP_TYPE, CometChatConstants.GROUP_TYPE_PUBLIC);
        return new JSONObject()
                .put(CometChatConstants.ActionKeys.KEY_ENTITY_TYPE, CometChatConstants.CONVERSATION_TYPE_GROUP)
                .put(CometChatConstants.ActionKeys.KEY_ENTITY, group);
    }

    private JSONObject userReceiverEntity(String uid, String name) throws Exception {
        JSONObject user = new JSONObject()
                .put(CometChatConstants.UserKeys.USER_KEY_UID, uid)
                .put(CometChatConstants.UserKeys.USER_KEY_NAME, name);
        return new JSONObject()
                .put(CometChatConstants.ActionKeys.KEY_ENTITY_TYPE, CometChatConstants.CONVERSATION_TYPE_USER)
                .put(CometChatConstants.ActionKeys.KEY_ENTITY, user);
    }

    /**
     * Builds the deeply-nested call envelope the backend delivers. The call session detail lives at
     * {@code data.entities.on.entity}, and the caller/callee live under that entity's own
     * {@code data.entities}.
     */
    private String callPayload(String callType, String receiverType, String receiverId,
                               String sessionId, String status, Long initiatedAt, Long joinedAt,
                               JSONObject callReceiverEntity) throws Exception {
        JSONObject sessionEntity = new JSONObject();
        if (sessionId != null) sessionEntity.put(CometChatConstants.CallKeys.CALL_SESSION_ID, sessionId);
        if (status != null) sessionEntity.put(CometChatConstants.CallKeys.CALL_STATUS, status);
        if (initiatedAt != null) sessionEntity.put(CometChatConstants.CallKeys.CALL_INITIATED_AT, initiatedAt);
        if (joinedAt != null) sessionEntity.put(CometChatConstants.CallKeys.CALL_JOINED_AT, joinedAt);

        JSONObject innerEntities = new JSONObject()
                .put(CometChatConstants.MessageKeys.KEY_SENDER, userEntity("caller-1", "Caller One"))
                .put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, callReceiverEntity);
        sessionEntity.put(CometChatConstants.ResponseKeys.KEY_DATA,
                new JSONObject().put(CometChatConstants.ResponseKeys.KEY_ENTITIES, innerEntities));

        JSONObject on = new JSONObject().put(CometChatConstants.ResponseKeys.KEY_ENTITITY, sessionEntity);

        JSONObject forEntity = new JSONObject()
                .put(CometChatConstants.ActionKeys.KEY_ENTITY_TYPE, receiverType)
                .put(CometChatConstants.ActionKeys.KEY_ENTITY,
                        receiverType.equalsIgnoreCase(CometChatConstants.CONVERSATION_TYPE_GROUP)
                                ? new JSONObject()
                                    .put(CometChatConstants.GroupKeys.KEY_GROUP_GUID, receiverId)
                                    .put(CometChatConstants.GroupKeys.KEY_GROUP_NAME, "Recv")
                                    .put(CometChatConstants.GroupKeys.KEY_GROUP_TYPE, CometChatConstants.GROUP_TYPE_PUBLIC)
                                : new JSONObject().put(CometChatConstants.UserKeys.USER_KEY_UID, receiverId));

        JSONObject dataEntities = new JSONObject()
                .put(CometChatConstants.ActionKeys.KEY_ON, on)
                .put(CometChatConstants.ActionKeys.KEY_BY, userEntity("caller-1", "Caller One"))
                .put(CometChatConstants.ActionKeys.KEY_FOR, forEntity);

        JSONObject data = new JSONObject()
                .put(CometChatConstants.ResponseKeys.KEY_ACTION, status == null ? "initiated" : status)
                .put(CometChatConstants.ResponseKeys.KEY_ENTITIES, dataEntities);

        return new JSONObject()
                .put(CometChatConstants.CallKeys.CALL_ID, 700L)
                .put(CometChatConstants.CallKeys.CALL_TYPE, callType)
                .put(CometChatConstants.CallKeys.CALL_RECEIVER_TYPE, receiverType)
                .put(CometChatConstants.MessageKeys.KEY_RECEIVER_UID, receiverId)
                .put(CometChatConstants.MessageKeys.KEY_MESSAGE_CATEGORY, CometChatConstants.CATEGORY_CALL)
                .put(CometChatConstants.MessageKeys.KEY_SENT_AT, 1700000000L)
                .put(CometChatConstants.ResponseKeys.KEY_DATA, data)
                .toString();
    }

    // ==================== CALL-01: audio/video, individual/group ====================

    @Test
    public void call01_audioCallToAUser_preservesTypeAndTarget() throws Exception {
        Call call = Call.fromJson(callPayload(CometChatConstants.CALL_TYPE_AUDIO,
                CometChatConstants.RECEIVER_TYPE_USER, "callee-1", "sess-1",
                CometChatConstants.CALL_STATUS_INITIATED, 1700000000L, null,
                userReceiverEntity("callee-1", "Callee One")));

        assertEquals(CometChatConstants.CALL_TYPE_AUDIO, call.getType());
        assertEquals(CometChatConstants.RECEIVER_TYPE_USER, call.getReceiverType());
        assertEquals(700L, call.getId());
    }

    @Test
    public void call01_videoCallToAGroup_preservesTypeAndTarget() throws Exception {
        Call call = Call.fromJson(callPayload(CometChatConstants.CALL_TYPE_VIDEO,
                CometChatConstants.RECEIVER_TYPE_GROUP, "grp-call", "sess-2",
                CometChatConstants.CALL_STATUS_ONGOING, 1700000000L, 1700000050L,
                groupReceiverEntity("grp-call", "Group Call")));

        assertEquals(CometChatConstants.CALL_TYPE_VIDEO, call.getType());
        assertEquals(CometChatConstants.RECEIVER_TYPE_GROUP, call.getReceiverType());
        assertTrue("a group call's receiver must resolve to a Group", call.getReceiver() instanceof Group);
    }

    // ==================== CALL-02: lifecycle status and timestamps ====================

    @Test
    public void call02_statusAndTimestamps_areParsed() throws Exception {
        Call call = Call.fromJson(callPayload(CometChatConstants.CALL_TYPE_VIDEO,
                CometChatConstants.RECEIVER_TYPE_USER, "callee-2", "sess-3",
                CometChatConstants.CALL_STATUS_ONGOING, 1700000000L, 1700000030L,
                userReceiverEntity("callee-2", "Callee Two")));

        assertEquals("sess-3", call.getSessionId());
        assertEquals(CometChatConstants.CALL_STATUS_ONGOING, call.getCallStatus());
        assertEquals(1700000000L, call.getInitiatedAt());
        assertEquals(1700000030L, call.getJoinedAt());
    }

    @Test
    public void call02_initiatedCall_reportsInitiatedStatus() throws Exception {
        Call call = Call.fromJson(callPayload(CometChatConstants.CALL_TYPE_AUDIO,
                CometChatConstants.RECEIVER_TYPE_USER, "callee-3", "sess-4",
                CometChatConstants.CALL_STATUS_INITIATED, 1700000000L, null,
                userReceiverEntity("callee-3", "Callee Three")));

        assertEquals(CometChatConstants.CALL_STATUS_INITIATED, call.getCallStatus());
    }

    // ==================== CALL-03: initiator and receiver resolved ====================

    @Test
    public void call03_initiatorAndReceiver_resolveToFullProfiles() throws Exception {
        Call call = Call.fromJson(callPayload(CometChatConstants.CALL_TYPE_VIDEO,
                CometChatConstants.RECEIVER_TYPE_USER, "callee-4", "sess-5",
                CometChatConstants.CALL_STATUS_ONGOING, 1700000000L, 1700000010L,
                userReceiverEntity("callee-4", "Callee Four")));

        assertNotNull("the call initiator must be resolved", call.getCallInitiator());
        assertEquals("caller-1", ((User) call.getCallInitiator()).getUid());
        assertNotNull("the call receiver must be resolved", call.getCallReceiver());
        assertEquals("callee-4", ((User) call.getCallReceiver()).getUid());
    }

    // ==================== CALL-04: resilience to missing optional details ====================

    @Test
    public void call04_missingSessionStatusAndTimestamps_leaveFieldsUnset() throws Exception {
        Call call = Call.fromJson(callPayload(CometChatConstants.CALL_TYPE_AUDIO,
                CometChatConstants.RECEIVER_TYPE_USER, "callee-5", null, null, null, null,
                userReceiverEntity("callee-5", "Callee Five")));

        assertNull("an absent session id stays unset, not guessed", call.getSessionId());
        assertNull("an absent status stays unset", call.getCallStatus());
        assertEquals("an absent initiated time stays at zero", 0L, call.getInitiatedAt());
        assertEquals(0L, call.getJoinedAt());
    }

    @Test
    public void call04_structurallyRequiredReceiverFields_fallBackToSafeValues() throws Exception {
        Call call = Call.fromJson(callPayload(CometChatConstants.CALL_TYPE_AUDIO,
                CometChatConstants.RECEIVER_TYPE_USER, "callee-6", null, null, null, null,
                userReceiverEntity("callee-6", "Callee Six")));

        assertEquals("callee-6", call.getReceiverUid());
        assertEquals(CometChatConstants.RECEIVER_TYPE_USER, call.getReceiverType());
    }
}
