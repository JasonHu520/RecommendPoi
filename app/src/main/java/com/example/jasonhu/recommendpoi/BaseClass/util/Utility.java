package com.example.jasonhu.recommendpoi.BaseClass.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.jasonhu.recommendpoi.DataBase.WeatherDB;
import com.example.jasonhu.recommendpoi.BaseClass.CityInfo.City;
import com.example.jasonhu.recommendpoi.BaseClass.CityInfo.County;
import com.example.jasonhu.recommendpoi.BaseClass.CityInfo.Province;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Utility {
    // 保存服务器返回的省级数据
    public static boolean saveProvincesResponse(WeatherDB weatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");//分出城市名与城市Id
            if (allProvinces.length > 0) {
                Province province;
                List<Province> provinceList = new ArrayList<>();
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    province = new Province();
                    province.setProvinceId(array[0]);
                    province.setProvinceName(array[1]);
                    provinceList.add(province);
                }
                weatherDB.saveProvinces(provinceList);
                return true;
            }
        }
        return false;
    }

    // 保存服务器返回的市级数据
    public static boolean saveCitiesResponse(WeatherDB weatherDB, String response, String provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities.length > 0) {
                City city;
                List<City> cityList = new ArrayList<>();
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    city = new City();
                    city.setCityId(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    cityList.add(city);
                }
                weatherDB.saveCities(cityList);
                return true;
            }
        }
        return false;
    }

    // 保存服务器返回的县级数据
    public static boolean saveCountiesResponse(WeatherDB weatherDB, String response, String cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if ( allCounties.length > 0) {
                County county;
                List<County> countyList = new ArrayList<>();
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    county = new County();
                    county.setCountyId(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    countyList.add(county);
                }
                weatherDB.saveCounties(countyList);
                return true;
            }
        }
        return false;
    }

    // 处理服务器返回的json数据
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonobject = new JSONObject(response);
            JSONArray title = jsonobject.getJSONArray("HeWeather6");
            JSONObject jsonObject = (JSONObject) title.get(0);

            JSONObject basic = (JSONObject) jsonObject.get("basic");

            JSONObject daily_forecast = (JSONObject) jsonObject.get("now");

            //更新时间
            JSONObject update = (JSONObject) jsonObject.get("update");

            //体感温度
            String bodyFeel = daily_forecast.getString("fl");

            //白天天气
            String dayWeather = daily_forecast.getString("cond_txt");

            //风力
            String windText = daily_forecast.getString("wind_sc")+ "级";
            //风向
            String windF = daily_forecast.getString("wind_dir");
            //温度
            String tempText = daily_forecast.getString("tmp") + "℃" ;
            //更新时间
            String updateTime = update.getString("loc");
            //城市名
            String cityName = basic.getString("parent_city")+"  "+basic.getString("location");
            saveWeatherInfo(context, cityName,  dayWeather, bodyFeel,windText, tempText, updateTime,windF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void saveWeatherInfo(Context context, String cityName,
                                        String dayWeather,String bodyFeel,
                                        String windText, String tempText, String updateTime,String windF
                                       ) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Weather", Context.MODE_PRIVATE).edit();
        editor.putString("cityName", cityName);
        editor.putString("dayWeather", dayWeather);
        editor.putString("wind", windText);
        editor.putString("temp", tempText);
        editor.putString("updateTime", updateTime);
        editor.putString("bodyFeel",bodyFeel);
        editor.putString("windF",windF);
        editor.apply();
    }
}
