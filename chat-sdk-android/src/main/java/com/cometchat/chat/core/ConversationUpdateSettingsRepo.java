package com.cometchat.chat.core;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.helpers.Logger;
import com.cometchat.chat.models.ConversationUpdateSettings;

public class ConversationUpdateSettingsRepo {
    private static final String TABLE_SETTINGS = "Settings";
    private static final String SETTING_KEY = "Settings";

    public static ConversationUpdateSettings getConversationUpdateSettings(){
        Cursor cursor = null;
        try {
            SQLiteDatabase db = SQLiteManager.getInstance().openDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS + " WHERE " + SETTING_KEY + " = '" + CometChatConstants.SettingsKeys.SETTINGS_DB_KEY+"'", null);
            if (cursor != null && cursor.moveToFirst()) {
                return ConversationUpdateSettings.fromJson(cursor.getString(1));
            }
        }catch (Exception e){
            Logger.error("Error while parsing Settings");
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return new ConversationUpdateSettings();
    }
}
