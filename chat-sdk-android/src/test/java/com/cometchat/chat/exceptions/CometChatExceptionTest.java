package com.cometchat.chat.exceptions;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin &amp; Save / structured error params on {@link CometChatException}.
 *
 * <p>This branch adds a machine-readable {@code errorParams} map (e.g. the {@code limit} carried by
 * a pinned/saved cap error) plus a 4-arg constructor. The contract these lock down:
 * <ul>
 *   <li>the 4-arg ctor populates code / message / details / errorParams together,</li>
 *   <li>the legacy 2-arg and 3-arg ctors leave {@code errorParams} {@code null} — never an
 *       accidental empty map, so a caller can distinguish "no params" from "empty params",</li>
 *   <li>{@code setErrorParams} carries the map by reference (no defensive copy is made or promised).</li>
 * </ul>
 */
public class CometChatExceptionTest {

    private static final String CODE = "ERR_PINNED_MESSAGES_LIMIT_EXCEEDED";
    private static final String MESSAGE = "The number of pinned messages has reached the allowed limit of 5.";
    private static final String DETAILS = "conversationId=alice_bob";

    @Test
    public void fourArgCtor_populatesCodeMessageDetailsAndParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 5);

        CometChatException e = new CometChatException(CODE, MESSAGE, DETAILS, params);

        assertEquals(CODE, e.getCode());
        assertEquals("message must be readable via Throwable#getMessage", MESSAGE, e.getMessage());
        assertEquals(DETAILS, e.getDetails());
        assertEquals(Integer.valueOf(5), e.getErrorParams().get("limit"));
    }

    @Test
    public void twoArgCtor_leavesErrorParamsNull_notEmptyMap() {
        CometChatException e = new CometChatException(CODE, MESSAGE);
        assertEquals(CODE, e.getCode());
        assertEquals(MESSAGE, e.getMessage());
        assertNull(e.getDetails());
        assertNull("absence of params must read as null, never an empty map", e.getErrorParams());
    }

    @Test
    public void threeArgCtor_leavesErrorParamsNull() {
        CometChatException e = new CometChatException(CODE, MESSAGE, DETAILS);
        assertEquals(DETAILS, e.getDetails());
        assertNull(e.getErrorParams());
    }

    @Test
    public void fourArgCtor_withNullParams_readsBackNull() {
        CometChatException e = new CometChatException(CODE, MESSAGE, DETAILS, null);
        assertNull(e.getErrorParams());
    }

    @Test
    public void setErrorParams_carriesTheSameMapReference() {
        Map<String, Object> params = Collections.<String, Object>singletonMap("scope", "conversation");
        CometChatException e = new CometChatException(CODE, MESSAGE);

        e.setErrorParams(params);

        assertSame("the map is carried by reference", params, e.getErrorParams());
        assertEquals("conversation", e.getErrorParams().get("scope"));
    }

    @Test
    public void isThrowable_soItCanBeDeliveredToCallbacks() {
        // The whole point of the type: it flows through CallbackListener#onError as a Throwable.
        assertTrue(new CometChatException(CODE, MESSAGE) instanceof Exception);
    }
}
