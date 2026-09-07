package com.cometchat.chat.models;

import com.cometchat.chat.enums.MessagesOptions;
import com.cometchat.chat.enums.ReactionsOptions;
import com.cometchat.chat.enums.RepliesOptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Feature Area: Notifications / one-on-one preference parcel restore.
 *
 * <p>Covers R6 — {@link OneOnOnePreferences} restores each preference from its parcelled
 * {@link Enum#name()}. Resolving that name must never crash when it is unrecognized, which is
 * exactly what happens the moment the backend introduces a new value (e.g.
 * {@link RepliesOptions#SUBSCRIBE_TO_SUBSCRIBED_THREADS}) that an already-shipped build cannot
 * resolve. The name-based restore path is unique to {@code OneOnOnePreferences};
 * {@code GroupPreferences} restores from the numeric value and is already null-safe.
 *
 * <p>A true {@code Parcel} round-trip needs the Android runtime; these tests exercise the
 * name-resolution helper the parcel constructor delegates to.
 */
public class OneOnOnePreferencesParcelHardeningTest {

    @Test
    public void knownEnumName_resolvesOnRestore() {
        assertEquals(RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS,
                OneOnOnePreferences.safeValueOf(RepliesOptions.class,
                        RepliesOptions.SUBSCRIBE_TO_SUBSCRIBED_THREADS.name()));
        assertEquals(MessagesOptions.SUBSCRIBE_TO_ALL,
                OneOnOnePreferences.safeValueOf(MessagesOptions.class,
                        MessagesOptions.SUBSCRIBE_TO_ALL.name()));
    }

    @Test
    public void unknownEnumName_resolvesToNullNeverThrows() {
        // A newer build parcels a name an older build has never heard of. It must degrade to
        // "not yet configured", not crash the app (R6).
        assertNull(OneOnOnePreferences.safeValueOf(RepliesOptions.class, "SUBSCRIBE_TO_SOMETHING_FUTURE"));
        assertNull(OneOnOnePreferences.safeValueOf(ReactionsOptions.class, "not_an_enum_constant"));
    }

    @Test
    public void nullEnumName_resolvesToNull() {
        assertNull(OneOnOnePreferences.safeValueOf(RepliesOptions.class, null));
    }
}
