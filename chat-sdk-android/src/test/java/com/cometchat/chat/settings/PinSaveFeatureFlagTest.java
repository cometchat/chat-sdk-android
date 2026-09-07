package com.cometchat.chat.settings;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.Settings;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin &amp; Save / feature-flag parsing on the {@link Settings} model.
 *
 * <p>Contract (parity with the other SDKs): the pin/save UX flags live in the settings
 * {@code parameters} block; <b>absence ⇒ enabled</b>, and only an explicit {@code false} disables
 * the feature so an older backend does not dead-disable it.
 */
public class PinSaveFeatureFlagTest {

    private String settingsJson(Boolean pinned, Boolean saved) throws Exception {
        JSONObject parameters = new JSONObject();
        if (pinned != null) parameters.put(CometChatConstants.FeatureKeys.FEATURE_PIN_MESSAGE, pinned.booleanValue());
        if (saved != null) parameters.put(CometChatConstants.FeatureKeys.FEATURE_SAVE_MESSAGE, saved.booleanValue());
        return new JSONObject()
                .put(CometChatConstants.SettingsKeys.PARAMETERS, parameters)
                .toString();
    }

    @Test
    public void absentFlags_defaultToEnabled() throws Exception {
        Settings settings = Settings.fromJson(settingsJson(null, null));
        assertTrue(settings.isPinnedMessagesEnabled());
        assertTrue(settings.isSavedMessagesEnabled());
    }

    @Test
    public void noParametersBlock_defaultsToEnabled() throws Exception {
        Settings settings = Settings.fromJson(new JSONObject().put(CometChatConstants.SettingsKeys.REGION, "us").toString());
        assertTrue(settings.isPinnedMessagesEnabled());
        assertTrue(settings.isSavedMessagesEnabled());
    }

    @Test
    public void explicitFalse_disables() throws Exception {
        Settings settings = Settings.fromJson(settingsJson(false, false));
        assertFalse(settings.isPinnedMessagesEnabled());
        assertFalse(settings.isSavedMessagesEnabled());
    }

    @Test
    public void explicitTrue_enables_independently() throws Exception {
        Settings settings = Settings.fromJson(settingsJson(true, false));
        assertTrue(settings.isPinnedMessagesEnabled());
        assertFalse(settings.isSavedMessagesEnabled());
    }

    @Test
    public void absentLimits_defaultToUnspecified() throws Exception {
        Settings settings = Settings.fromJson(settingsJson(null, null));
        assertEquals(Settings.LIMIT_UNSPECIFIED, settings.getPinnedMessagesLimit());
        assertEquals(Settings.LIMIT_UNSPECIFIED, settings.getPinnedMessagesSystemLimit());
        assertEquals(Settings.LIMIT_UNSPECIFIED, settings.getSavedMessagesLimit());
        assertEquals(Settings.LIMIT_UNSPECIFIED, settings.getConversationPinnedLimit());
        assertEquals(Settings.LIMIT_UNSPECIFIED, settings.getConversationPinnedSystemLimit());
    }

    @Test
    public void noParametersBlock_limitsDefaultToUnspecified() throws Exception {
        Settings settings = Settings.fromJson(new JSONObject().put(CometChatConstants.SettingsKeys.REGION, "us").toString());
        assertEquals(Settings.LIMIT_UNSPECIFIED, settings.getPinnedMessagesLimit());
        assertEquals(Settings.LIMIT_UNSPECIFIED, settings.getConversationPinnedLimit());
    }

    @Test
    public void servedLimits_areParsedIndependently() throws Exception {
        String json = new JSONObject()
                .put(CometChatConstants.SettingsKeys.PARAMETERS, new JSONObject()
                        .put(CometChatConstants.FeatureKeys.FEATURE_PIN_MESSAGE_LIMIT, 25)
                        .put(CometChatConstants.FeatureKeys.FEATURE_PIN_MESSAGE_SYSTEM_LIMIT, 5)
                        .put(CometChatConstants.FeatureKeys.FEATURE_SAVE_MESSAGE_LIMIT, 100)
                        .put(CometChatConstants.FeatureKeys.FEATURE_PIN_CONVERSATION_LIMIT, 10)
                        .put(CometChatConstants.FeatureKeys.FEATURE_PIN_CONVERSATION_SYSTEM_LIMIT, 3))
                .toString();
        Settings settings = Settings.fromJson(json);
        assertEquals(25, settings.getPinnedMessagesLimit());
        assertEquals(5, settings.getPinnedMessagesSystemLimit());
        assertEquals(100, settings.getSavedMessagesLimit());
        assertEquals(10, settings.getConversationPinnedLimit());
        assertEquals(3, settings.getConversationPinnedSystemLimit());
    }

    @Test
    public void stringTypedLimit_isCoercedToInt() throws Exception {
        String json = new JSONObject()
                .put(CometChatConstants.SettingsKeys.PARAMETERS, new JSONObject()
                        .put(CometChatConstants.FeatureKeys.FEATURE_PIN_CONVERSATION_LIMIT, "7"))
                .toString();
        assertEquals(7, Settings.fromJson(json).getConversationPinnedLimit());
    }

    @Test
    public void nonNumericLimit_fallsBackToUnspecified() throws Exception {
        String json = new JSONObject()
                .put(CometChatConstants.SettingsKeys.PARAMETERS, new JSONObject()
                        .put(CometChatConstants.FeatureKeys.FEATURE_PIN_MESSAGE_LIMIT, "not-a-number"))
                .toString();
        assertEquals(Settings.LIMIT_UNSPECIFIED, Settings.fromJson(json).getPinnedMessagesLimit());
    }

    @Test
    public void conversationPinFlag_defaultsEnabled_andExplicitFalseDisables() throws Exception {
        assertTrue(Settings.fromJson(settingsJson(null, null)).isConversationPinnedEnabled());

        String json = new JSONObject()
                .put(CometChatConstants.SettingsKeys.PARAMETERS, new JSONObject()
                        .put(CometChatConstants.FeatureKeys.FEATURE_PIN_CONVERSATION, false))
                .toString();
        assertFalse(Settings.fromJson(json).isConversationPinnedEnabled());
    }
}
