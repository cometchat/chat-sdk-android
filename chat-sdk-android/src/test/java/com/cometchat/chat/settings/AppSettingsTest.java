package com.cometchat.chat.settings;

import com.cometchat.chat.core.AppSettings;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Feature Area: Preferences & Settings / App init region/host config.
 *
 * <p>Covers PREF-05 — configuring region, admin/client hosts, and presence-subscription scope.
 * Each scope maps to a stable constant; the most recently chosen scope wins; a roles list is set
 * only for the "specific roles" scope; builder settings read back exactly as configured.
 *
 * <p>PREF-06 (disabling typing/read-receipt/presence broadcasting at init) has no counterpart on
 * Android — {@code AppSettings} exposes only the presence-subscription scope and socket/host
 * config — so it is not covered here.
 */
public class AppSettingsTest {

    // ==================== Subscription scope maps to a stable constant ====================

    @Test
    public void pref05_defaultSubscriptionScopeIsNone() {
        AppSettings settings = new AppSettings.AppSettingsBuilder()
                .setRegion("us")
                .build();

        assertEquals(AppSettings.SUBSCRIPTION_TYPE_NONE, settings.getSubscriptionType());
    }

    @Test
    public void pref05_allUsersScopeMapsToItsConstant() {
        AppSettings settings = new AppSettings.AppSettingsBuilder()
                .subscribePresenceForAllUsers()
                .setRegion("us")
                .build();

        assertEquals(AppSettings.SUBSCRIPTION_TYPE_ALL_USERS, settings.getSubscriptionType());
    }

    @Test
    public void pref05_friendsScopeMapsToItsConstant() {
        AppSettings settings = new AppSettings.AppSettingsBuilder()
                .subscribePresenceForFriends()
                .setRegion("us")
                .build();

        assertEquals(AppSettings.SUBSCRIPTION_TYPE_FRIENDS, settings.getSubscriptionType());
    }

    @Test
    public void pref05_rolesScopeSetsScopeAndRoles() {
        List<String> roles = Arrays.asList("admin", "moderator");
        AppSettings settings = new AppSettings.AppSettingsBuilder()
                .subscribePresenceForRoles(roles)
                .setRegion("us")
                .build();

        assertEquals(AppSettings.SUBSCRIPTION_TYPE_ROLES, settings.getSubscriptionType());
        assertEquals("a roles list is set when the roles scope is chosen", roles, settings.getRoles());
    }

    @Test
    public void pref05_rolesAreNotSetForNonRoleScopes() {
        AppSettings settings = new AppSettings.AppSettingsBuilder()
                .subscribePresenceForAllUsers()
                .setRegion("us")
                .build();

        assertNull("a roles list is only set for the specific-roles scope", settings.getRoles());
    }

    // ==================== Most recently chosen scope wins ====================

    @Test
    public void pref05_mostRecentlyChosenScopeWins() {
        AppSettings settings = new AppSettings.AppSettingsBuilder()
                .subscribePresenceForAllUsers()
                .subscribePresenceForFriends()
                .setRegion("us")
                .build();

        assertEquals("the last scope chosen replaces earlier ones",
                AppSettings.SUBSCRIPTION_TYPE_FRIENDS, settings.getSubscriptionType());
    }

    // ==================== Region and host config read back exactly ====================

    @Test
    public void pref05_regionAndHostsReadBackExactly() {
        AppSettings settings = new AppSettings.AppSettingsBuilder()
                .setRegion("eu")
                .overrideAdminHost("https://admin.example.com")
                .overrideClientHost("https://client.example.com")
                .build();

        assertEquals("eu", settings.getRegion());
        assertEquals("https://admin.example.com", settings.getAdminHost());
        assertEquals("https://client.example.com", settings.getClientHost());
    }
}
