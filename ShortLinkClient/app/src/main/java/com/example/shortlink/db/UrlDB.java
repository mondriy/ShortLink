package com.example.shortlink.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UrlDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "urlDB";
    public static final String DATABASE_TABLE = "urlTable";

    public static final String KEY_ID = "_id";
    public static final String KEY_SHORT_URL = "shortUrl";
    public static final String KEY_ORIGINAL_URL = "originalUrl";

    public UrlDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DATABASE_TABLE + "(" + KEY_ID + " integer primary key," + KEY_ORIGINAL_URL + " text," + KEY_SHORT_URL + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DATABASE_TABLE);
        onCreate(db);
    }
}
