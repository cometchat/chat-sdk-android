package com.cometchat.chat.settings;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChatUtils;
import com.cometchat.chat.core.Settings;
import com.cometchat.chat.models.CCExtension;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Extensions / feature availability + extension identity.
 *
 * <p>Covers:
 * <ul>
 *   <li>EXT-01 — a feature-availability check reads the cached app configuration only (no network).</li>
 *   <li>EXT-02 — an empty or missing enabled-feature list, or null settings, yields "not enabled".</li>
 *   <li>EXT-04 — an extension's identity (id + display name) is exposed by the model.</li>
 * </ul>
 *
 * <p>{@code Settings.fromJson} is exercised directly to build the cached configuration that the
 * availability check reads, which also demonstrates parsing of the enabled-extensions list.
 */
public class SettingsAndExtensionsTest {

    private String settingsJson(String... enabledExtensionIds) throws Exception {
        JSONArray extensions = new JSONArray();
        for (String id : enabledExtensionIds) {
            extensions.put(new JSONObject().put("id", id));
        }
        return new JSONObject()
                .put(CometChatConstants.SettingsKeys.SETTINGS_EXTENSIONS, extensions)
                .put(CometChatConstants.SettingsKeys.REGION, "us")
                .put(CometChatConstants.SettingsKeys.SECURED_MEDIA_HOST, "secure.example.com")
                .toString();
    }

    // ==================== EXT-01: availability read from cached config ====================

    @Test
    public void ext01_availabilityCheckReadsCachedConfigWithoutNetwork() throws Exception {
        Settings settings = Settings.fromJson(settingsJson("polls", "stickers", "reactions"));

        assertTrue("an enabled feature reads as available",
                CometChatUtils.isExtensionEnabled(settings, "polls"));
        assertTrue(CometChatUtils.isExtensionEnabled(settings, "stickers"));
    }

    @Test
    public void ext01_settingsFromJsonExposesTheEnabledExtensionList() throws Exception {
        Settings settings = Settings.fromJson(settingsJson("polls", "stickers"));

        assertEquals(2, settings.getEnabledExtensions().size());
        assertTrue(settings.getEnabledExtensions().contains("polls"));
        assertEquals("us", settings.getRegion());
        assertEquals("secure.example.com", settings.getSecureMediaHost());
    }

    // ==================== EXT-02: not-enabled cases ====================

    @Test
    public void ext02_featureNotInTheList_isNotEnabled() throws Exception {
        Settings settings = Settings.fromJson(settingsJson("polls"));

        assertFalse(CometChatUtils.isExtensionEnabled(settings, "stickers"));
    }

    @Test
    public void ext02_emptyEnabledList_meansNotEnabled() throws Exception {
        Settings settings = Settings.fromJson(settingsJson());

        assertFalse(CometChatUtils.isExtensionEnabled(settings, "polls"));
    }

    @Test
    public void ext02_nullSettings_meansNotEnabled() {
        assertFalse("a null configuration must not silently allow a feature",
                CometChatUtils.isExtensionEnabled(null, "polls"));
    }

    // ==================== EXT-04: extension identity model ====================

    @Test
    public void ext04_extensionExposesIdAndDisplayName() {
        CCExtension extension = new CCExtension("polls", "Polls");

        assertEquals("polls", extension.getExtensionId());
        assertEquals("Polls", extension.getExtensionName());
    }

    @Test
    public void ext04_extensionsWithSameIdentityAreContentEqual() {
        CCExtension a = new CCExtension("polls", "Polls");
        CCExtension b = new CCExtension("polls", "Polls");

        assertTrue(a.contentEquals(b));
    }
}
