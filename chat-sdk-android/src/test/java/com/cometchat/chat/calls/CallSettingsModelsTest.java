package com.cometchat.chat.calls;

import com.cometchat.chat.core.CallSettings;
import com.cometchat.chat.models.AudioMode;
import com.cometchat.chat.models.MainVideoContainerSetting;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Feature Area: Calls / Audio output selection and video layout customization.
 *
 * <p>Covers:
 * <ul>
 *   <li>CALL-06 — an audio output mode is a distinct, immutable choice with a selected flag;
 *       two identical modes are equal and cloning yields an equal-but-separate copy.</li>
 *   <li>CALL-07 — each video-layout setting is applied independently, unconfigured settings fall
 *       back to defaults, and two identical configurations are equal.</li>
 * </ul>
 */
public class CallSettingsModelsTest {

    // ==================== CALL-06: audio output mode ====================

    @Test
    public void call06_audioMode_carriesModeAndSelectedFlag() {
        AudioMode speaker = new AudioMode("speaker", true);

        assertEquals("speaker", speaker.getMode());
        assertTrue(speaker.isSelected());
    }

    @Test
    public void call06_unselectedMode_reportsNotSelected() {
        AudioMode bluetooth = new AudioMode("bluetooth", false);

        assertFalse("a mode that isn't active must report not-selected", bluetooth.isSelected());
    }

    @Test
    public void call06_twoIdenticalModes_areContentEqual() {
        AudioMode a = new AudioMode("earpiece", true);
        AudioMode b = new AudioMode("earpiece", true);

        assertTrue(a.contentEquals(b));
        assertEquals(a, b);
    }

    @Test
    public void call06_modesDifferingBySelection_areNotEqual() {
        AudioMode selected = new AudioMode("speaker", true);
        AudioMode notSelected = new AudioMode("speaker", false);

        assertFalse(selected.contentEquals(notSelected));
    }

    @Test
    public void call06_clonedMode_isEqualButSeparate() {
        AudioMode original = new AudioMode("speaker", true);
        AudioMode cloned = original.clone();

        assertNotNull(cloned);
        assertTrue("a clone must be content-equal", original.contentEquals(cloned));
        cloned.setSelected(false);
        assertFalse("mutating the clone must not affect the original", original.isSelected() == cloned.isSelected());
    }

    // ==================== CALL-07: video layout container ====================

    @Test
    public void call07_configuredAspectRatio_isApplied() {
        MainVideoContainerSetting setting = new MainVideoContainerSetting();
        setting.setMainVideoAspectRatio(CallSettings.ASPECT_RATIO_COVER);

        assertEquals(CallSettings.ASPECT_RATIO_COVER, setting.getMainVideoAspectRatio());
    }

    @Test
    public void call07_unconfiguredAspectRatio_fallsBackToDefault() {
        MainVideoContainerSetting setting = new MainVideoContainerSetting();

        assertEquals("an unset aspect ratio must apply the default",
                CallSettings.ASPECT_RATIO_DEFAULT, setting.getMainVideoAspectRatio());
    }

    @Test
    public void call07_settingsAreAppliedIndependently() {
        MainVideoContainerSetting setting = new MainVideoContainerSetting();
        setting.setFullScreenButtonParams(CallSettings.POSITION_TOP_LEFT, false);
        setting.setNameLabelParams(CallSettings.POSITION_BOTTOM_LEFT, true, "#000000");

        assertEquals(CallSettings.POSITION_TOP_LEFT, setting.getFullScreenButtonParams().getPosition());
        assertFalse(setting.getFullScreenButtonParams().getVisibility());
        assertEquals(CallSettings.POSITION_BOTTOM_LEFT, setting.getNameLabelParams().getPosition());
        assertTrue(setting.getNameLabelParams().getVisibility());
        assertEquals("#000000", setting.getNameLabelParams().getColor());
    }

    @Test
    public void call07_unconfiguredButton_fallsBackToItsDefaultLayout() {
        MainVideoContainerSetting setting = new MainVideoContainerSetting();

        // getters supply a sensible default when the app never configured the button.
        assertNotNull(setting.getZoomButtonParams());
        assertEquals(CallSettings.POSITION_BOTTOM_RIGHT, setting.getZoomButtonParams().getPosition());
        assertTrue(setting.getZoomButtonParams().getVisibility());
    }

    @Test
    public void call07_twoIdenticalConfigurations_areContentEqual() {
        MainVideoContainerSetting a = new MainVideoContainerSetting();
        a.setMainVideoAspectRatio(CallSettings.ASPECT_RATIO_COVER);
        a.setFullScreenButtonParams(CallSettings.POSITION_TOP_LEFT, true);

        MainVideoContainerSetting b = new MainVideoContainerSetting();
        b.setMainVideoAspectRatio(CallSettings.ASPECT_RATIO_COVER);
        b.setFullScreenButtonParams(CallSettings.POSITION_TOP_LEFT, true);

        assertTrue(a.contentEquals(b));
    }
}
