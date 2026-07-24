package com.cometchat.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TestPreferenceHelper {

    private static final String PREFERENCE_NAME = "COMETCHAT_TEST";
    private static final String LAST_TEXT_MESSAGE_ID_TO_USER = "LAST_MESSAGE_ID_TO_USER";
    private static final String LAST_TEXT_MESSAGE_ID_TO_GROUP = "LAST_MESSAGE_ID_TO_GROUP";
    private static final String LAST_CUSTOM_MESSAGE_ID_TO_USER = "LAST_CUSTOM_MESSAGE_ID_TO_USER";
    private static final String LAST_CUSTOM_MESSAGE_ID_TO_GROUP = "LAST_CUSTOM_MESSAGE_ID_TO_GROUP";
    private static final String DEFAULT_MESSAGE_ID_USER = "DEFAULT_MESSAGE_ID_USER";
    private static final String DEFAULT_MESSAGE_ID_GROUP = "DEFAULT_MESSAGE_ID_GROUP";
    private static final String DEFAULT_TIMESTAMP_USER = "DEFAULT_TIMESTAMP_USER";
    private static final String DEFAULT_TIMESTAMP_GROUP = "DEFAULT_TIMESTAMP_GROUP";
    private static final String LAST_CREATED_USER_UID = "LAST_CREATED_USER_UID";
    private static final String LAST_MESSAGE_ID_FROM_BLOCKED_USER_IN_GROUP = "LAST_MESSAGE_ID_FROM_BLOCKED_USER_IN_GROUP";
    private static final String LAST_PUBLIC_GROUP_GUID = "LAST_PUBLIC_GROUP_GUID";
    private static final String LAST_PASSWORD_GROUP_GUID = "LAST_PASSWORD_GROUP_GUID";
    private static final String LAST_PRIVATE_GROUP_GUID = "LAST_PRIVATE_GROUP_GUID";
    private static final String ROLE_1 = "ROLE_1";
    private static final String ROLE_2 = "ROLE_2";
    private static final String ROLE_COUNTER = "ROLE_COUNTER";
    private static Context context;
    private static SharedPreferences sharedPreferences;

    public static void init(Context appContext) {
        context = appContext;
    }

    public static void saveLastTextMessageIdToUser(long messageId){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_TEXT_MESSAGE_ID_TO_USER, messageId);
        editor.apply();
    }

    public static void saveLastTextMessageIdToGroup(long messageId){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_TEXT_MESSAGE_ID_TO_GROUP, messageId);
        editor.apply();
    }

    public static void saveLastCustomMessageIdToUser(long messageId){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_CUSTOM_MESSAGE_ID_TO_USER, messageId);
        editor.apply();
    }

    public static void saveLastCustomMessageIdToGroup(long messageId){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_CUSTOM_MESSAGE_ID_TO_GROUP, messageId);
        editor.apply();
    }

    public static void saveDefaultMessageIdForUser(long messageId){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DEFAULT_MESSAGE_ID_USER, messageId);
        editor.apply();
    }

    public static void saveDefaultTimestampForUser(long timestamp){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DEFAULT_TIMESTAMP_USER, timestamp);
        editor.apply();
    }

    public static void saveDefaultMessageIdForGroup(long messageId){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DEFAULT_MESSAGE_ID_GROUP, messageId);
        editor.apply();
    }

    public static void saveDefaultTimestampForGroup(long timestamp){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DEFAULT_TIMESTAMP_GROUP, timestamp);
        editor.apply();
    }

    public static void saveLastCreatedUserUID(String UID){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_CREATED_USER_UID, UID);
        editor.apply();
    }

    public static void saveLastMessageIdFromBlockedUserInGroup(long messageId){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_MESSAGE_ID_FROM_BLOCKED_USER_IN_GROUP, messageId);
        editor.apply();
    }

    public static void saveLastCreatedPublicGroupGUID(String GUID){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_PUBLIC_GROUP_GUID, GUID);
        editor.apply();
    }

    public static void saveLastCreatedPasswordGroupGUID(String GUID){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_PASSWORD_GROUP_GUID, GUID);
        editor.apply();
    }

    public static void saveLastCreatedPrivateGroupGUID(String GUID){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_PRIVATE_GROUP_GUID, GUID);
        editor.apply();
    }

    public static void saveRole1(String role){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ROLE_1, role);
        editor.apply();
    }

    public static void saveRole2(String role){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ROLE_2, role);
        editor.apply();
    }



    public static long getLastTextMessageIdSentToUser(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_TEXT_MESSAGE_ID_TO_USER, 100000);
    }

    public static long getLastTextMessageIdSentToGroup(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_TEXT_MESSAGE_ID_TO_GROUP, 100000);
    }

    public static long getLastCustomMessageIdSentToUser(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_CUSTOM_MESSAGE_ID_TO_USER, 100000);
    }

    public static long getLastCustomMessageIdSentToGroup(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_CUSTOM_MESSAGE_ID_TO_GROUP, 100000);
    }

    public static long getDefaultMessageIdForUser(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(DEFAULT_MESSAGE_ID_USER, 100000);
    }

    public static long getDefaultMessageIdForGroup(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(DEFAULT_MESSAGE_ID_GROUP, 100000);
    }

    public static long getDefaultTimestampForUser(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(DEFAULT_TIMESTAMP_USER, 100000);
    }

    public static long getDefaultTimestampForGroup(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(DEFAULT_TIMESTAMP_GROUP, 100000);
    }

    public static String getLastCreatedUserUId(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LAST_CREATED_USER_UID, "");
    }

    public static long getLastMessageIdFromBlockedUserInGroup(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_MESSAGE_ID_FROM_BLOCKED_USER_IN_GROUP, 0);
    }

    public static String getLastPublicGroupGUID(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LAST_PUBLIC_GROUP_GUID, "");
    }

    public static String getLastPasswordGroupGUID(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LAST_PASSWORD_GROUP_GUID, "");
    }

    public static String getLastPrivateGroupGUID(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LAST_PRIVATE_GROUP_GUID, "");
    }

    public static String getRole1(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ROLE_1, "");
    }

    public static String getRole2(){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ROLE_2, "");
    }

}



