package com.cometchat.chat.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cometchat.chat.helpers.Logger;

/**
 * Created by adityagokula on 13/09/18.
 */
 class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteHelper";

    private Context context;

    SQLiteHelper(Context context, String databaseName, int databaseVersion){
        super(context,databaseName,null,databaseVersion);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Logger.error(TAG, "onCreate : ");
        sqLiteDatabase.execSQL(SettingsRepo.createTable());
        sqLiteDatabase.execSQL(CurrentUserRepo.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Logger.error(TAG, "onUpgrade : " + oldVersion + " " + newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.error(TAG, "onDowngrade : " + oldVersion + " " + newVersion);
    }

}
