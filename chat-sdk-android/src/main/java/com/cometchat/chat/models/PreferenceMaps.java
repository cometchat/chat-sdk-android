package com.cometchat.chat.models;

import java.util.Map;

/**
 * Null-tolerant reads for the preference maps that cross the SDK boundary.
 *
 * <p>The {@code fromMap} factories on the preference models are the entry point used by the wrapper
 * SDKs (React Native, Flutter), which build these maps from JSON. A JSON {@code null} becomes a key
 * that is <i>present with a null value</i> rather than an absent key, so a {@code containsKey} guard
 * lets it through. Reading that entry into a primitive — the enum {@code get(int)} resolvers, or an
 * {@code (int)}/{@code (boolean)} cast — force-unboxes and throws {@link NullPointerException} out of
 * a public static factory that has no {@code try/catch} to contain it.
 *
 * <p>Reading through this class makes "key absent" and "key present but null" the same case: the
 * preference is left unset and its documented default applies. That is what the notification
 * preference specification requires — an unrecognised value must be treated as unset and must not
 * fail, throw or crash on any code path.
 *
 * <p>Non-null values are returned exactly as stored; callers keep their existing casts and so keep
 * their existing behaviour for every value that is not null.
 */
final class PreferenceMaps {

    private PreferenceMaps() {
    }

    /**
     * Reads a value from a preference map, treating an absent key, an explicitly null value, and a
     * null map alike.
     *
     * @param map the preference map, which may itself be {@code null}
     * @param key the preference key
     * @return the stored value, or {@code null} when there is none to read
     */
    static <V> V opt(Map<String, V> map, String key) {
        return map == null ? null : map.get(key);
    }
}
