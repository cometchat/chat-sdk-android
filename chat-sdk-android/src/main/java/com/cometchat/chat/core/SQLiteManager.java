package com.cometchat.chat.core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.models.CurrentUser;

/**
 * Created by adityagokula on 13/09/18.
 */

public class SQLiteManager {
    private Integer mOpenCounter = 0;

    private static SQLiteManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    static synchronized void init(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new SQLiteManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized SQLiteManager getInstance() {
        if (instance == null) {
            throw new RuntimeException(CometChatConstants.Errors.ERROR_INIT_NOT_CALLED_MESSAGE);
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter += 1;
        if (mOpenCounter == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        mOpenCounter -= 1;
        if (mOpenCounter == 0) {
            // Closing database
            mDatabase.close();

        }
    }

    synchronized void deleteDatabase(Context context) {
        SettingsRepo.clearSettings();
        CurrentUserRepo.clearUser();
        closeDatabase();
    }

    synchronized void runMigrationForJWT() {
        SQLiteDatabase sqLiteDatabase = SQLiteManager.getInstance().openDatabase();
        boolean isColumnPresent = isColumnPresent(sqLiteDatabase, CurrentUser.TABLE_CURRENT_USER, "jwt");
        if (!isColumnPresent) {
            sqLiteDatabase.execSQL("ALTER TABLE " + CurrentUser.TABLE_CURRENT_USER + " ADD COLUMN jwt TEXT;");
        }
        SQLiteManager.getInstance().closeDatabase();
    }

    synchronized void runMigrationForTags() {
        SQLiteDatabase sqLiteDatabase = SQLiteManager.getInstance().openDatabase();
        boolean isColumnPresent = isColumnPresent(sqLiteDatabase, CurrentUser.TABLE_CURRENT_USER, "tags");
        if (!isColumnPresent) {
            sqLiteDatabase.execSQL("ALTER TABLE " + CurrentUser.TABLE_CURRENT_USER + " ADD COLUMN tags TEXT;");
        }
        SQLiteManager.getInstance().closeDatabase();
    }

    synchronized void runMigrationForFat() {
        SQLiteDatabase sqLiteDatabase = SQLiteManager.getInstance().openDatabase();
        boolean isColumnPresent = isColumnPresent(sqLiteDatabase, CurrentUser.TABLE_CURRENT_USER, "fat");
        if (!isColumnPresent) {
            sqLiteDatabase.execSQL("ALTER TABLE " + CurrentUser.TABLE_CURRENT_USER + " ADD COLUMN fat TEXT;");
        }
        SQLiteManager.getInstance().closeDatabase();
    }

    private boolean isColumnPresent(SQLiteDatabase db, String tableName, String columnName) {
        boolean isColumnPresent = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            if (cursor != null) {
                int i = cursor.getColumnIndex(columnName);
                if (i != -1)
                    isColumnPresent = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return isColumnPresent;
    }


}
