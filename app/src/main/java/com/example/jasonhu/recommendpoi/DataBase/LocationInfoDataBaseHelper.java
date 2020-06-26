package com.example.jasonhu.recommendpoi.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationInfoDataBaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "location.db";
    // 数据库表名
    public static final String TABLE_NAME = "lon_lat";
    // 数据库版本号
    public static final int DB_VERSION = 1;

    public static final String city_name = "city_name";
    public static final String poi_lat = "poi_lat";
    public static final String poi_lon="poi_long";
    public static final String poi_address="poi_address";
    public static final String poiName="poi_name";
    public static final String county_name="county_name";
    public static final String Id="id";

    public LocationInfoDataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table  lon_lat(id integer primary key autoincrement," +
                "city_name varchar(32),poi_lat varchar(32),poi_long varchar(32),poi_address varchar(32),poi_name varchar(32), county_name varchar(32))" ;
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



}
