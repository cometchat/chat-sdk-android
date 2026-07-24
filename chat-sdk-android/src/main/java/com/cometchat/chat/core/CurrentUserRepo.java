package com.cometchat.chat.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.CurrentUser;
import com.cometchat.chat.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by adityagokula on 25/09/18.
 */

public class CurrentUserRepo {

    private static final String TAG = CurrentUserRepo.class.getSimpleName();
    private CurrentUser user;

    CurrentUserRepo() {

        user = new CurrentUser();
    }


    static String createTable() {
        String createCurrentUserQuery = "CREATE TABLE " + CurrentUser.TABLE_CURRENT_USER + "("
                + CurrentUser.COLUMN_UID + " TEXT PRIMARY KEY, "
                + CurrentUser.COLUMN_NAME + " TEXT, "
                + CurrentUser.COLUMN_EMAIL + " TEXT, "
                + CurrentUser.COLUMN_AVATAR + " TEXT, "
                + CurrentUser.COLUMN_LINK + " TEXT, "
                + CurrentUser.COLUMN_ROLE + " TEXT, "
                + CurrentUser.COLUMN_METADATA + " TEXT, "
                + CurrentUser.COLUMN_CREDITS + " INTEGER, "
                + CurrentUser.COLUMN_STATUS + " TEXT, "
                + CurrentUser.COLUMN_STATUS_MESSAGE + " TEXT, "
                + CurrentUser.COLUMN_LAST_ACTIVE_AT + " INTEGER, "
                + CurrentUser.COLUMN_IDENTITY + " TEXT ,"
                + CurrentUser.COLUMN_SECRET + " TEXT ,"
                + CurrentUser.COLUMN_AUTH_TOKEN + " TEXT ,"
                + CurrentUser.COLUMN_JWT + " TEXT ,"
                + CurrentUser.COLUMN_TAGS + " TEXT ,"
                + CurrentUser.COLUMN_FAT + " TEXT )";
        return createCurrentUserQuery;
    }

    static long insertCurrentUser(HashMap<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (((String) pair.getKey()).equalsIgnoreCase(CurrentUser.COLUMN_CREDITS) ||
                    ((String) pair.getKey()).equalsIgnoreCase(CurrentUser.COLUMN_LAST_ACTIVE_AT) ||
                    ((String) pair.getKey()).equalsIgnoreCase(CurrentUser.COLUMN_STATUS))
                continue;
            contentValues.put((String) pair.getKey(), (String) pair.getValue());
        }
        if (map.get(CurrentUser.COLUMN_CREDITS) != null)
            contentValues.put(CurrentUser.COLUMN_CREDITS, Integer.parseInt(map.get(CurrentUser.COLUMN_CREDITS)));
        else
            contentValues.put(CurrentUser.COLUMN_CREDITS, 0);
        if (map.get(CurrentUser.COLUMN_LAST_ACTIVE_AT) != null)
            contentValues.put(CurrentUser.COLUMN_LAST_ACTIVE_AT, Long.parseLong(map.get(CurrentUser.COLUMN_LAST_ACTIVE_AT)));
        else
            contentValues.put(CurrentUser.COLUMN_LAST_ACTIVE_AT, 0);
        if (map.get(CurrentUser.COLUMN_STATUS) != null)
            contentValues.put(CurrentUser.COLUMN_STATUS, CometChatConstants.USER_STATUS_ONLINE);
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        long result = db.insertWithOnConflict(CurrentUser.TABLE_CURRENT_USER, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        SQLiteManager.getInstance().closeDatabase();
        return result;
    }

    static long updateCurrentUser(HashMap<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (((String) pair.getKey()).equalsIgnoreCase(CurrentUser.COLUMN_CREDITS) || ((String) pair.getKey()).equalsIgnoreCase(CurrentUser.COLUMN_LAST_ACTIVE_AT))
                continue;
            contentValues.put((String) pair.getKey(), (String) pair.getValue());
        }
        if (map.get(CurrentUser.COLUMN_CREDITS) != null)
            contentValues.put(CurrentUser.COLUMN_CREDITS, Integer.parseInt(map.get(CurrentUser.COLUMN_CREDITS)));
        else
            contentValues.put(CurrentUser.COLUMN_CREDITS, 0);
        if (map.get(CurrentUser.COLUMN_LAST_ACTIVE_AT) != null)
            contentValues.put(CurrentUser.COLUMN_LAST_ACTIVE_AT, Long.parseLong(map.get(CurrentUser.COLUMN_LAST_ACTIVE_AT)));
        else
            contentValues.put(CurrentUser.COLUMN_LAST_ACTIVE_AT, 0);
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        long result = db.update(CurrentUser.TABLE_CURRENT_USER, contentValues, "`uid` = '" + map.get("uid") + "'", null);
        SQLiteManager.getInstance().closeDatabase();
        return result;
    }

   public static CurrentUser getCurrentUser() {
        CurrentUser user = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
            cursor = db.rawQuery("SELECT * FROM " + CurrentUser.TABLE_CURRENT_USER + " WHERE " + CurrentUser.COLUMN_UID + " LIKE '" + PreferenceHelper.getLoggedInUID() + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                user = new CurrentUser();
                user.setUid(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setAvatar(cursor.getString(3));
                user.setLink(cursor.getString(4));
                user.setRole(cursor.getString(5));
                if (cursor.getString(6) != null)
                    user.setMetadata(new JSONObject(cursor.getString(6)));
                user.setStatus(cursor.getString(8));
                user.setStatusMessage(cursor.getString(9));
                user.setLastActiveAt(cursor.getLong(10));
                user.setIdentity(cursor.getString(11));
                user.setSecret(cursor.getString(12));
                user.setAuthToken(cursor.getString(13));
                user.setJwt(cursor.getString(14));
                String tagsArray = cursor.getString(15);
                user.setFat(cursor.getString(16));
                if (tagsArray != null) {
                    JSONArray tags = new JSONArray(tagsArray);
                    List<String> tagsList = new ArrayList<>();
                    for (int i = 0; i < tags.length(); i++) {
                        tagsList.add(tags.getString(i));
                    }
                    user.setTags(tagsList);
                }
            }
            SQLiteManager.getInstance().closeDatabase();
        } catch (JSONException je) {
            Logger.error("Error getting users from local database : " + je.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return user;
    }

    static User getLoggedInUser() {
        User user = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
            cursor = db.rawQuery("SELECT * FROM " + CurrentUser.TABLE_CURRENT_USER + " WHERE " + CurrentUser.COLUMN_UID + " LIKE '" + PreferenceHelper.getLoggedInUID() + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                user = new User();
                user.setUid(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setAvatar(cursor.getString(3));
                user.setLink(cursor.getString(4));
                user.setRole(cursor.getString(5));
                if (cursor.getString(6) != null)
                    user.setMetadata(new JSONObject(cursor.getString(6)));
                user.setStatus(cursor.getString(8));
                user.setStatusMessage(cursor.getString(9));
                user.setLastActiveAt(cursor.getLong(10));
                String tagsArray = cursor.getString(15);
                if (tagsArray != null) {
                    JSONArray tags = new JSONArray(tagsArray);
                    List<String> tagsList = new ArrayList<>();
                    for (int i = 0; i < tags.length(); i++) {
                        tagsList.add(tags.getString(i));
                    }
                    user.setTags(tagsList);
                }
            }
            SQLiteManager.getInstance().closeDatabase();
        } catch (JSONException je) {
            Logger.error("Error getting users from local database : " + je.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return user;
    }

    static void clearUser() {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.execSQL("DELETE FROM " + CurrentUser.TABLE_CURRENT_USER);
        SQLiteManager.getInstance().closeDatabase();
    }
}
