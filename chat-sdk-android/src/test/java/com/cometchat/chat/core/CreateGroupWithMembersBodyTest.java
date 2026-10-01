package com.cometchat.chat.core;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Regression tests for ENG-39497: createGroupWithMembers sent group `tags`
 * (and `metadata`) as stringified JSON because its hand-built body copied
 * Group.toMap() values verbatim, skipping the field typing that
 * getRequestBodyFromMap applies on the plain createGroup path. The body now
 * routes every group field through putTypedGroupField.
 */
public class CreateGroupWithMembersBodyTest {

    // Exercises the production body construction (ApiConnection.buildGroupBody, the
    // method createGroupWithMembers itself uses) — not a test-local copy of it.
    private JSONObject buildGroupBody(Group group) throws JSONException {
        return ApiConnection.buildGroupBody(group);
    }

    @Test
    public void tags_areSentAsJsonArray_notString() throws JSONException {
        Group group = new Group("repro_tags", "Repro Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group.setTags(Arrays.asList("non-dedicated", "farm"));

        JSONObject body = buildGroupBody(group);

        Object tags = body.get(CometChatConstants.UserKeys.USER_KEY_TAGS);
        assertTrue("tags must be a JSONArray, was " + tags.getClass().getSimpleName(), tags instanceof JSONArray);
        assertEquals(2, ((JSONArray) tags).length());
        assertEquals("non-dedicated", ((JSONArray) tags).getString(0));
    }

    @Test
    public void metadata_isSentAsJsonObject_notString() throws JSONException {
        Group group = new Group("repro_meta", "Repro Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group.setMetadata(new JSONObject().put("productName", "Book"));

        JSONObject body = buildGroupBody(group);

        Object metadata = body.get(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA);
        assertTrue("metadata must be a JSONObject, was " + metadata.getClass().getSimpleName(), metadata instanceof JSONObject);
        assertEquals("Book", ((JSONObject) metadata).getString("productName"));
    }

    @Test
    public void plainFields_stayStrings() throws JSONException {
        Group group = new Group("guid1", "Plain Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);
        group.setDescription("about us");

        JSONObject body = buildGroupBody(group);

        assertEquals("guid1", body.getString(CometChatConstants.GroupKeys.KEY_GROUP_GUID));
        assertEquals("Plain Group", body.getString(CometChatConstants.GroupKeys.KEY_GROUP_NAME));
        assertEquals("about us", body.getString(CometChatConstants.GroupKeys.KEY_GROUP_DESCRIPTION));
        assertTrue(body.get(CometChatConstants.GroupKeys.KEY_GROUP_TYPE) instanceof String);
    }

    @Test
    public void groupWithoutTagsOrMetadata_producesNoSuchKeys() throws JSONException {
        Group group = new Group("guid2", "Bare Group", CometChatConstants.GROUP_TYPE_PUBLIC, null);

        JSONObject body = buildGroupBody(group);

        assertTrue(!body.has(CometChatConstants.UserKeys.USER_KEY_TAGS));
        assertTrue(!body.has(CometChatConstants.MessageKeys.KEY_SEND_TEXT_METADATA));
    }
}
