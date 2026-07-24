package com.cometchat.chat.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by adityagokula on 07/09/18.
 */

public class PreferenceHelper {

    private static final String PREFERENCE_NAME = "COMETCHATSDK";
    private static final String KEY_APPID = "APPID";
    private static final String KEY_UID = "UID";
    private static final String KEY_LAST_DELIVERED_MESSAGEID = "LAST_DELIVERED_MESSAGEID";
    private static final String KEY_SOURCE_RESOURCE = "SOURCE_RESOURCE";
    private static final String KEY_SOURCE_PLATFORM = "SOURCE_PLATFORM";
    private static final String KEY_SOURCE_LANGUAGE = "SOURCE_LANGUAGE";
    private static final String KEY_RESTART_AUTHTOKEN = "RESTART_AUTHTOKEN";
    private static final String KEY_MIGRATION_SUCCESSFUL = "MIGRATION_SUCCESSFUL";
    private static final String KEY_TAGS_MIGRATION_SUCCESSFUL = "TAGS_MIGRATION_SUCCESSFUL";
    private static final String KEY_FAT_MIGRATION_SUCCESSFUL = "FAT_MIGRATION_SUCCESSFUL";
    private static final String SDK_SESSION = "SDK_SESSION";
    public static boolean isInitialized = false;

    private static Context context;
    private static SharedPreferences sharedPreferences;

    static void init(Context appContext) {
        isInitialized = true;
        context = appContext;
    }

    static void saveAppID(String appID) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_APPID, appID);
        editor.apply();
    }

    public static String getAppID() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_APPID, null);

    }

    static void saveLoggedInUID(String UID) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_UID, UID);
        editor.apply();
    }

    public static String getLoggedInUID() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_UID, null);

    }

    static void clearPreferences() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(KEY_UID).apply();
        sharedPreferences.edit().remove(KEY_LAST_DELIVERED_MESSAGEID).apply();
    }

    public static void saveLastDeliveredMessageId(long messageId) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_DELIVERED_MESSAGEID, messageId);
        editor.apply();
    }

    public static long getLastDeliveredMessageId() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        try {
            return sharedPreferences.getLong(KEY_LAST_DELIVERED_MESSAGEID, 0);
        } catch (ClassCastException e) {
            int legacyValue = sharedPreferences.getInt(KEY_LAST_DELIVERED_MESSAGEID, 0);
            saveLastDeliveredMessageId(legacyValue);
            return legacyValue;
        }
    }

    static void saveResource(String resource) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SOURCE_RESOURCE, resource);
        editor.apply();
    }

    static String getResource() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SOURCE_RESOURCE, null);
    }

    static void savePlatform(String platform) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SOURCE_PLATFORM, platform);
        editor.apply();
    }

    static String getPlatform() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SOURCE_PLATFORM, null);
    }

    static void saveLanguage(String language) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SOURCE_LANGUAGE, language);
        editor.apply();
    }

    static String getLanguage() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SOURCE_LANGUAGE, null);
    }

    static void saveRestartAuthToken(String authToken) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RESTART_AUTHTOKEN, authToken);
        editor.apply();
    }

    static String getRestartAUthToken() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_RESTART_AUTHTOKEN, null);
    }


    static void clearRestartAuthToken() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(KEY_RESTART_AUTHTOKEN).apply();
    }

    static void saveMigrationSuccessful(boolean migrationSuccessful){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_MIGRATION_SUCCESSFUL, migrationSuccessful);
        editor.apply();
    }

    static boolean isMigrationSuccessful(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_MIGRATION_SUCCESSFUL, false);
    }


    public static void saveTagsMigrationSuccessful(boolean success) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_TAGS_MIGRATION_SUCCESSFUL, success);
        editor.apply();
    }

    public static boolean isTagsMigrationSuccessful() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_TAGS_MIGRATION_SUCCESSFUL, false);
    }
    
    public static void saveFatMigrationSuccessful(boolean success) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_FAT_MIGRATION_SUCCESSFUL, success);
        editor.apply();
    }

    public static boolean isFatMigrationSuccessful() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_FAT_MIGRATION_SUCCESSFUL, false);
    }

    public static void saveSDKSessionHash(String hash) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SDK_SESSION, hash);
        editor.apply();
    }

    public static String getSDKSessionHash() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SDK_SESSION, "");
    }

    private static final String KEY_INTEGRATION_SOURCE = "INTEGRATION_SOURCE";

    static void saveIntegrationSource(String source) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_INTEGRATION_SOURCE, source);
        editor.apply();
    }

    static String getIntegrationSource() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_INTEGRATION_SOURCE, "manual");
    }

    private static final String KEY_SETTINGS_AUTH_KEY = "SETTINGS_AUTH_KEY";

    static void saveSettingsAuthKey(String authKey) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SETTINGS_AUTH_KEY, authKey);
        editor.apply();
    }

    public static String getSettingsAuthKey() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SETTINGS_AUTH_KEY, null);
    }
}


