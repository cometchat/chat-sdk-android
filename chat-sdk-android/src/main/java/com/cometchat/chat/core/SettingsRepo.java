package com.cometchat.chat.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;

/**
 * Created by adityagokula on 15/09/18.
 */

public class SettingsRepo {

    private static final String TAG = SettingsRepo.class.getSimpleName();
    private static final String TABLE_SETTINGS = "Settings";
    private static final String SETTING_KEY = "Settings";
    private static final String SETTING_VALUE = "RawData";


    SettingsRepo() {

    }

    static String createTable() {
        String settingsQuery = "CREATE TABLE " + TABLE_SETTINGS + "("
                + SETTING_KEY + " TEXT PRIMARY KEY, "
                + SETTING_VALUE + " TEXT )";
        return settingsQuery;
    }

    static void insertSettings(Settings settings){
        Logger.error(TAG,"Updating Settings");
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTING_KEY, CometChatConstants.SettingsKeys.SETTINGS_DB_KEY);
        contentValues.put(SETTING_VALUE,settings.getRawData());
        db.insertWithOnConflict(TABLE_SETTINGS,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public static Settings getSettings(){
        Cursor cursor = null;
        try {
            SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
             cursor = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS + " WHERE " + SETTING_KEY + " = '" + CometChatConstants.SettingsKeys.SETTINGS_DB_KEY+"'", null);
            if (cursor != null && cursor.moveToFirst()) {
                Settings settings = Settings.fromJson(cursor.getString(1));
                return settings;
            }
        }catch (Exception e){
            Logger.error("Error while parsing Settings");
        } finally {
            if(cursor!=null)
                cursor.close();
        }
        return null;
    }

    static void clearSettings() {
        SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
        db.execSQL("DELETE FROM " + TABLE_SETTINGS);
        SQLiteManager.getInstance().closeDatabase();
    }
}
