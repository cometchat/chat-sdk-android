package com.cometchat.chat.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Feature Area: Thread subscription / {@link RepliesOptions} (extended by this branch with
 * {@code SUBSCRIBE_TO_SUBSCRIBED_THREADS}).
 *
 * <p>The numeric {@code value} is a stable wire/persistence code. Crucially,
 * {@link RepliesOptions#get(int)} returns {@code null} for an unrecognized code rather than flooring
 * to a default. That is load-bearing: it is exactly what
 * lets {@code OneOnOnePreferences.fromJson/fromMap} leave a preference "not configured" when a newer
 * backend sends a value an older build has never heard of, rather than asserting a wrong default.
 */
public class RepliesOptionsTest {

    @Test
    public void values_areStableCodes() {
        assertEquals(1, RepliesOptions.DONT_SUBSCRIBE.getValue());
        assertEquals(2, RepliesOptions.SUBSCRIBE_TO_ALL.getValue());
        assertEquals(3, RepliesOptions.SUBSCRIBE_TO_MENTIONS.getValue());
        assertEquals("the value added on this branch", 4, RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS.getValue());
    }

    @Test
    public void get_roundTripsEveryConstant() {
        for (RepliesOptions option : RepliesOptions.values()) {
            assertEquals(option, RepliesOptions.get(option.getValue()));
        }
    }

    @Test
    public void get_unknownCode_isNull_notADefault() {
        assertNull("an unknown code must degrade to null, never to a wrong option", RepliesOptions.get(99));
        assertNull(RepliesOptions.get(0));
        assertNull(RepliesOptions.get(-1));
    }
}
