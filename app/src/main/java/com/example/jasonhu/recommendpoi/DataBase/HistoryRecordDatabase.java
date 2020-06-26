package com.example.jasonhu.recommendpoi.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryRecordDatabase extends SQLiteOpenHelper {
    public static final String DB_NAME = "history.db";
    // 数据库表名
    public static final String TABLE_NAME = "his_search_info";
    // 数据库版本号
    public static final int DB_VERSION = 1;

    public static final String TV_CONTENT = "info";
    public static final String TV_TIME = "time";
    public static final String Id="_id";
    public HistoryRecordDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table  his_search_info(_id integer primary key autoincrement,info text,time text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
