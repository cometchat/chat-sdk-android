package com.cometchat.chat.settings;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.Settings;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Pin &amp; Save / feature-flag parsing edges on {@link Settings}.
 *
 * <p>Complements {@code PinSaveFeatureFlagTest} (default-enabled / explicit-false) with the corners:
 * the three flags are independent, an explicit {@code true} on the conversation-pin flag enables it,
 * a non-boolean value falls back to the enabled default (an older backend must not dead-disable the
 * feature by serving the wrong type), and the setters move each flag on their own.
 */
public class PinSaveFeatureFlagEdgeTest {

    private static Settings fromParameters(JSONObject parameters) throws Exception {
        return Settings.fromJson(new JSONObject()
                .put(CometChatConstants.SettingsKeys.PARAMETERS, parameters)
                .toString());
    }

    @Test
    public void conversationPinFlag_explicitTrue_enables() throws Exception {
        Settings settings = fromParameters(new JSONObject()
                .put(CometChatConstants.FeatureKeys.FEATURE_PIN_CONVERSATION, true));
        assertTrue(settings.isConversationPinnedEnabled());
    }

    @Test
    public void savedFlag_disablesIndependently_pinAndConversationStayEnabled() throws Exception {
        Settings settings = fromParameters(new JSONObject()
                .put(CometChatConstants.FeatureKeys.FEATURE_PIN_MESSAGE, true)
                .put(CometChatConstants.FeatureKeys.FEATURE_SAVE_MESSAGE, false)
                .put(CometChatConstants.FeatureKeys.FEATURE_PIN_CONVERSATION, true));

        assertTrue(settings.isPinnedMessagesEnabled());
        assertFalse(settings.isSavedMessagesEnabled());
        assertTrue(settings.isConversationPinnedEnabled());
    }

    @Test
    public void nonBooleanFlagValue_fallsBackToEnabledDefault() throws Exception {
        // A backend that serves the wrong type (here a number) must not dead-disable the feature:
        // optBoolean(key, true) cannot coerce it, so it falls through to the enabled default.
        Settings settings = fromParameters(new JSONObject()
                .put(CometChatConstants.FeatureKeys.FEATURE_PIN_MESSAGE, 0)
                .put(CometChatConstants.FeatureKeys.FEATURE_SAVE_MESSAGE, 0));

        assertTrue(settings.isPinnedMessagesEnabled());
        assertTrue(settings.isSavedMessagesEnabled());
    }

    @Test
    public void setters_moveEachFlagIndependently() throws Exception {
        Settings settings = Settings.fromJson(new JSONObject().toString()); // all default-enabled

        settings.setSavedMessagesEnabled(false);
        assertFalse(settings.isSavedMessagesEnabled());
        assertTrue("disabling save must not touch pin", settings.isPinnedMessagesEnabled());
        assertTrue("disabling save must not touch conversation pin", settings.isConversationPinnedEnabled());
    }
}
