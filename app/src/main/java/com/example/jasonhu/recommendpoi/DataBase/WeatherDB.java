package com.example.jasonhu.recommendpoi.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jasonhu.recommendpoi.BaseClass.CityInfo.City;
import com.example.jasonhu.recommendpoi.BaseClass.CityInfo.County;
import com.example.jasonhu.recommendpoi.BaseClass.CityInfo.Province;

import java.util.ArrayList;
import java.util.List;

public class WeatherDB {
    private final String DataBaseName = "ZyWeather";

    private final int VERSION = 1;

    private SQLiteDatabase database;

    private static WeatherDB weatherDB;

    private WeatherDB(Context context) {
        UserInfoDatabaseHelper dataBaseHelperUserInfoUserInfo = new UserInfoDatabaseHelper(context,
                DataBaseName, null, VERSION);
        database = dataBaseHelperUserInfoUserInfo.getWritableDatabase();
    }

    //获取实例
    public static WeatherDB getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    //保存省份信息
    public void saveProvinces(List<Province> provinceList) {
        if (provinceList != null && provinceList.size() > 0) {
            ContentValues values = new ContentValues();
            for (int i = 0; i < provinceList.size(); i++) {
                values.put("provinceName", provinceList.get(i).getProvinceName());
                values.put("provinceId", provinceList.get(i).getProvinceId());
                database.insert("Province", null, values);
                values.clear();
            }
        }
    }

    //保存城市信息
    public void saveCities(List<City> cityList) {
        if (cityList != null && cityList.size() > 0) {
            ContentValues values = new ContentValues();
            for (int i = 0; i < cityList.size(); i++) {
                values.put("cityName", cityList.get(i).getCityName());
                values.put("cityId", cityList.get(i).getCityId());
                values.put("provinceId", cityList.get(i).getProvinceId());
                database.insert("City", null, values);
                values.clear();
            }
        }
    }

    //保存乡村信息
    public void saveCounties(List<County> countyList) {
        if (countyList != null && countyList.size() > 0) {
            ContentValues values = new ContentValues();
            for (int i = 0; i < countyList.size(); i++) {
                values.put("countyName", countyList.get(i).getCountyName());
                values.put("countyId", countyList.get(i).getCountyId());
                values.put("cityId", countyList.get(i).getCityId());
                database.insert("County", null, values);
                values.clear();
            }
        }
    }

    //返回所有省份信息
    public List<Province> getAllProvince() {
        Cursor cursor = database.query("Province", null, null, null, null, null, null);
        List<Province> list = new ArrayList<>();
        Province province;
        if (cursor.moveToFirst()) {
            do {
                province = new Province();
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                province.setProvinceId(cursor.getString(cursor.getColumnIndex("provinceId")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    //返回指定省份下的所有城市
    public List<City> getAllCity(String provinceId) {
        List<City> list = new ArrayList<>();
        City city;
        Cursor cursor = database.query("City", null, "provinceId = ?", new String[]{provinceId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                city = new City();
                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                city.setCityId(cursor.getString(cursor.getColumnIndex("cityId")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    //返回指定城市下的所有乡村
    public List<County> getAllCountry(String cityId) {
        List<County> list = new ArrayList<>();
        Cursor cursor = database.query("County", null, "cityId=?", new String[]{cityId}, null, null, null);
        County county;
        if (cursor.moveToFirst()) {
            do {
                county = new County();
                county.setCountyName(cursor.getString(cursor.getColumnIndex("countyName")));
                county.setCountyId(cursor.getString(cursor.getColumnIndex("countyId")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }
}
