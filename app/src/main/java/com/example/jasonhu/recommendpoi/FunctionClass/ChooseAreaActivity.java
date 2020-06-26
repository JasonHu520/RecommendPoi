package com.example.jasonhu.recommendpoi.FunctionClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.BaseClass.Callback.HttpCallbackListener;
import com.example.jasonhu.recommendpoi.BaseClass.HttpUtil;
import com.example.jasonhu.recommendpoi.BaseClass.util.Utility;
import com.example.jasonhu.recommendpoi.DataBase.WeatherDB;
import com.example.jasonhu.recommendpoi.PoiApplication;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.BaseClass.CityInfo.City;
import com.example.jasonhu.recommendpoi.BaseClass.CityInfo.County;
import com.example.jasonhu.recommendpoi.BaseClass.CityInfo.Province;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {
    // 标记当前列表为省份
    public static final int LEVEL_PROVINCE = 0;
    // 标记当前列表为城市
    public static final int LEVEL_CITY = 1;
    // 标记当前列表为县
    public static final int LEVEL_COUNTY = 2;
    // 进度对话框
    private ProgressDialog progressDialog;
    // 标题栏
    private TextView titleText;
    // 数据列表
    private ListView listView;
    // 列表数据
    private ArrayAdapter<String> adapter;
    // 数据库
    private WeatherDB weatherDB;

    private List<String> dataList;

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;
    //选择的省份
    private Province selectedProvince;
    //选择的城市
    private City selectedCity;
    //当前选择的列表类型
    private int currentLevel;
    //标记是否从WeatherActivity跳转而来的
    private boolean isFromWeatherActivity;

    PoiApplication App;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SharedPreferences sharedPreferences = getSharedPreferences("Weather", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = getSharedPreferences("LocationInfo", Context.MODE_PRIVATE).edit();
        // 如果country已选择且本Activity不是从天气界面启动而来的，则直接跳转到WeatherActivity

        setContentView(R.layout.activity_choose_area);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        listView = (ListView) findViewById(R.id.listView);
        titleText = (TextView) findViewById(R.id.title);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        weatherDB = WeatherDB.getInstance(this);
        App=(PoiApplication)getApplication();
        handler=App.getHandler();
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String provinceName=null,cityName=null;
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {

                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(index);
                    provinceName=provinceList.get(index).getProvinceName();
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    cityName=cityList.get(index).getCityName();
                    queryCounties();
                }
                else if (currentLevel == LEVEL_COUNTY) {
                    //当点击到县列表时，就利用Intent跳转到天气信息界面
                    String countyName = countyList.get(index).getCountyName();
                    handler.sendEmptyMessage(Constant.LOCATION_DATA_CHANGED);
                    editor.putString("countyName",countyName);
                    editor.putString("cityName",cityName);
                    editor.apply();
                    finish();
                }
            }
        });

    }


    private void queryProvinces() {
        showProgressDialog();
        provinceList = weatherDB.getAllProvince();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
            closeProgressDialog();
        } else {
            queryFromServer(null, "province");
        }
    }

    private void queryCities() {
        showProgressDialog();
        cityList = weatherDB.getAllCity(selectedProvince.getProvinceId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
            closeProgressDialog();
        } else {
            queryFromServer(selectedProvince.getProvinceId(), "city");
        }
    }

    private void queryCounties() {
        showProgressDialog();
        countyList = weatherDB.getAllCountry(selectedCity.getCityId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
            closeProgressDialog();
        } else {
            queryFromServer(selectedCity.getCityId(), "county");
        }
    }

    //获取城市名数字列表
    private void queryFromServer(final String code, final String type) {
        String address;
        // code不为空
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.saveProvincesResponse(weatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.saveCitiesResponse(weatherDB, response, selectedProvince.getProvinceId());
                } else if ("county".equals(type)) {
                    result = Utility.saveCountiesResponse(weatherDB, response, selectedCity.getCityId());
                }
                if (result) {
                    runOnUiThread(() -> {
                        if ("province".equals(type)) {
                            queryProvinces();
                        } else if ("city".equals(type)) {
                            queryCities();
                        } else if ("county".equals(type)) {
                            queryCounties();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        closeProgressDialog();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载……");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_PROVINCE)
        {
            finish();
        }
        else if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        }

    }
}
