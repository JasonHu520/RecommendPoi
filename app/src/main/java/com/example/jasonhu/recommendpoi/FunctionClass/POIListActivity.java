package com.example.jasonhu.recommendpoi.FunctionClass;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.jasonhu.recommendpoi.BaseClass.Check.CheckNet;
import com.example.jasonhu.recommendpoi.DataBase.HistoryRecordDatabase;
import com.example.jasonhu.recommendpoi.DataBase.LocationInfoDataBaseHelper;
import com.example.jasonhu.recommendpoi.bean.LocationInfo;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.adpter.AutoInputCursorAdapter;
import com.example.jasonhu.recommendpoi.pinyinSearch.SearchAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class POIListActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {

    String searchType="餐饮";
    private LocationInfoDataBaseHelper mHelper;
    private SQLiteDatabase mDatabase;
    private HistoryRecordDatabase historyRecordDatabase;
    private SQLiteDatabase sqLiteDatabase;
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp ;
    public LocationInfo locationInfo;
    Button btnSearch = null;
    ListView lv = null;
    PoiSearch poiSearch = null;
    private ArrayList<String> dataInfo;
    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    private List<PoiItem> poiItems;// poi数据
    AutoCompleteTextView autoCompleteTextView;
    Calendar calendar = null;// 获取系统时间
    InputMethodManager manager;
    private PoiResult poiResult; // poi返回的结果



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.poi_list);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //检查网络
        if(CheckNet.getNetState(POIListActivity.this)==CheckNet.NET_NONE)
        {
            Toast.makeText(POIListActivity.this,"网络不通，请连接网络",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(POIListActivity.this,"网络OK",Toast.LENGTH_LONG).show();
        }

        mHelper = new LocationInfoDataBaseHelper(this);
        mDatabase = mHelper.getWritableDatabase();

        historyRecordDatabase = new HistoryRecordDatabase(this);
        sqLiteDatabase = historyRecordDatabase.getWritableDatabase();




        locationInfo = new LocationInfo();
        dataInfo=new ArrayList<String>();

        //查询地理位置信息
        queryData();
        //获取历史信息
        getData();

        btnSearch = (Button)findViewById(R.id.list_search);
        //editText=findViewById(R.id.et_poi_list);
        lv = (ListView)findViewById(R.id.poi_list);
        autoCompleteTextView = findViewById(R.id.act_input);

        lp = new LatLonPoint(locationInfo.lat, locationInfo.lon);// 116.472995,39.993743

        autoCompleteTextView.setThreshold(0);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                if(s.charAt(0)<='Z'&&s.charAt(0)>='A'||s.charAt(0)<='z'&&s.charAt(0)>='a'){
                    SearchAdapter<String> adapter = new SearchAdapter<String>(POIListActivity.this,
                            R.layout.autoinput_text_item, dataInfo, SearchAdapter.ALL);
                    autoCompleteTextView.setAdapter(adapter);
                }
                else
                    {
                        Cursor cursor = sqLiteDatabase.rawQuery(
                            "select * from his_search_info where info like?",
                            new String[] { s.toString() + "%" });

                            // 新建新的Adapter
                            AutoInputCursorAdapter dictionaryAdapter = new AutoInputCursorAdapter(POIListActivity.this,cursor, true,sqLiteDatabase);

                            // 绑定适配器
                            autoCompleteTextView.setAdapter(dictionaryAdapter);
                            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    doSearchQuery();
                                }
                            });
                    }
            }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!autoCompleteTextView.getText().toString().isEmpty())
                {
                    ContentValues values=new ContentValues();
                    values.put(HistoryRecordDatabase.TV_CONTENT,autoCompleteTextView.getText().toString());
                    calendar=Calendar.getInstance();
                    String time="";
                    time=time+calendar.get(Calendar.YEAR)+"年"+calendar.get(Calendar.MONTH)+"月"+calendar.get(Calendar.DATE)+"日"
                            +calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
                    values.put(HistoryRecordDatabase.TV_TIME,time);
                    sqLiteDatabase.insert(HistoryRecordDatabase.TABLE_NAME,null,values);
                    //Toast.makeText(POIListActivity.this,"成功",Toast.LENGTH_SHORT).show();
                    doSearchQuery();
                    //nearbySearch(0);
                }
                else Toast.makeText(POIListActivity.this,"请输入",Toast.LENGTH_SHORT).show();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.add(0,0,0,"忽略该POI");
                        menu.add(0,1,0,"取消");
                    }
                });
            }
        });
    }
    public ArrayList<HashMap<String,String>>getPoiData(){
        return data;
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                data.remove(3);
                lv.setAdapter(new SimpleAdapter(POIListActivity.this, data, R.layout.poi_item, new String[]{"name", "address","distance"}, new int[]{R.id.poi_name, R.id.poi_address,R.id.poi_distance}));
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        //清除原有的数据
        if(!data.isEmpty())
            data.clear();
        searchType=autoCompleteTextView.getText().toString();
        int currentPage = 0;
        query = new PoiSearch.Query(searchType, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    //附近搜索
    @Override
    public void onPoiSearched(PoiResult result, int rcode) {

        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        for (PoiItem poiItem:poiItems){
                            HashMap<String,String> item = new HashMap<String,String>();
                            item.put("name",poiItem.getTitle());
                            item.put("address",poiItem.getSnippet());
                            item.put("distance", poiItem.getDistance()+"m");
                            data.add(item);
                        }
                        SharedPreferences.Editor editor=getSharedPreferences("RecommendList",Context.MODE_PRIVATE).edit();
                        editor.putString("name",data.get(0).get("name"));
                        editor.putString("address",data.get(0).get("address"));
                        editor.putString("distance",data.get(0).get("distance"));
                        editor.apply();
                        lv.setAdapter(new SimpleAdapter(POIListActivity.this, data, R.layout.poi_item, new String[]{"name","address","distance"}, new int[]{R.id.poi_name,R.id.poi_address,R.id.poi_distance}));
                        lv.invalidate();
                    }
                    else{
                        Toast.makeText(POIListActivity.this, "没有搜索到结果", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        else {
            Toast.makeText(POIListActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
//
    }


    //查询
    public void queryData() {
        Cursor cursor = mDatabase.query(LocationInfoDataBaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);// 注意空格！
        while (cursor.moveToNext()) {
            parseOrder(cursor);
        }

    }
    public void getData() {
        Cursor cursor = sqLiteDatabase.query(HistoryRecordDatabase.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);// 注意空格！
        while (cursor.moveToNext()) {
            String str;
            str=cursor.getString(cursor.getColumnIndex(HistoryRecordDatabase.TV_CONTENT));
            dataInfo.add(str);
        }

        if(!cursor.isClosed()){
            cursor.close();
        }
    }

    private void parseOrder(Cursor cursor){
        locationInfo.lon = (cursor.getDouble(cursor.getColumnIndex("poi_long")));
        locationInfo.lat = (cursor.getDouble(cursor.getColumnIndex("poi_lat")));
    }

    @Override
    protected void onDestroy() {
        if(mDatabase!=null)
            mDatabase.close();
        super.onDestroy();
    }
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
