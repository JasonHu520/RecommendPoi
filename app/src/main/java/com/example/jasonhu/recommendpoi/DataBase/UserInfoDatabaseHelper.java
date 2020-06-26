package com.example.jasonhu.recommendpoi.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserInfoDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME_LOG ="UserInfo.db";

    private final String CREATE_PROVINCE = "create table Province ("
            + "provinceName text," + "provinceId text )";

    private final String CREATE_CITY = "create table City("
            + "cityName text," + "cityId text," + "provinceId text)";

    private final String CREATE_COUNTY = "create table County("
            + "countyName text," + "countyId text," + "cityId text)";
    private final String CREATE_LOGFILE = "create table LogInfo("
            + "userName text," + "password text," + "email text,"+"City text,"+"phoneNumber text,"+"LogState text,"+"head_image_address text)";

    private final String CREATE_MESSAGE = "create table messageHistory(id integer primary key autoincrement,fromOther integer,message text,toUser text,fromUser text," +
            "currentTime text,position text)";

    private final String CREATE_FRIEND = "create table friendInfo(id integer primary key autoincrement,currentUser text,friendName text," +
            "message text,position integer,headPic text, currentTime text)";

    public UserInfoDatabaseHelper(Context context, String DbName,
                                  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DbName, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
        db.execSQL(CREATE_LOGFILE);
        db.execSQL(CREATE_MESSAGE);
        db.execSQL(CREATE_FRIEND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>=2){
            db.execSQL(CREATE_PROVINCE);
            db.execSQL(CREATE_CITY);
            db.execSQL(CREATE_COUNTY);
            db.execSQL(CREATE_LOGFILE);
            db.execSQL(CREATE_MESSAGE);
            db.execSQL(CREATE_FRIEND);
        }

    }
}
