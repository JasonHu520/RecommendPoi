package com.example.jasonhu.recommendpoi;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.example.jasonhu.recommendpoi.BaseClass.Check.CheckPermissionsActivity;
import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.BaseClass.SetStatusBarColor;
import com.example.jasonhu.recommendpoi.BaseClass.http.OkHttpUtil;
import com.example.jasonhu.recommendpoi.DataBase.LocationInfoDataBaseHelper;
import com.example.jasonhu.recommendpoi.FunctionClass.Location;
import com.example.jasonhu.recommendpoi.fragment.BaseFragment;
import com.example.jasonhu.recommendpoi.fragment.MeFragment;
import com.example.jasonhu.recommendpoi.service.serviceUtils.PollingUtil;
import com.example.jasonhu.recommendpoi.ui.BottomControlPanel;
import com.example.jasonhu.recommendpoi.ui.HeadControlPanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends CheckPermissionsActivity implements BottomControlPanel.BottomPanelCallback,WeatherSearch.OnWeatherSearchListener {
	public BottomControlPanel bottomPanel = null;
	public HeadControlPanel headPanel = null;

	Boolean first_run;
	BufferedReader is=null;

	PoiApplication App;
	WeatherSearchQuery weatherQuery;
	WeatherSearch weatherSearch;
	TextView exit_note;

	private LocationInfoDataBaseHelper mHelper;//位置信息数据库
	private SQLiteDatabase mDatabase;

	private FragmentManager fragmentManager = null;
	private FragmentTransaction fragmentTransaction = null;

	Location location;//定位功能

	private String CurrentCity,CurrentPoi,weather,tempture,CurrentCounty;
	SetStatusBarColor setStatusBarColor;
	public static String currFlagTag = "";
	Handler handler,tellHandler;
	private ScheduledExecutorService scheduledExecutorService;
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
		initUI();
		setStatusBarColor.setStatusBar(getResources().getColor(R.color.gray));
		fragmentManager = getFragmentManager();
		setDefaultFirstFragment(Constant.FRAGMENT_FLAG_MESSAGE);
		PollingUtil.startPollingService(this,"轮询");
//		tellServer();
//		tellHandler = new Handler(){
//			@Override
//			public void handleMessage(Message msg) {
//				tellServer();
////				Toast.makeText(MainActivity.this,"ok",Toast.LENGTH_SHORT).show();
//			}
//		};
		/**
		 * 监听服务器发来的信息
		 */
//		new Thread() {
//			@Override
//			public void run() {
//				try{
//					socket = new Socket("113.250.155.239", 8000);
//					socket.setSoTimeout(10000);
//					pw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//					is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//		}.start();
	}

	private void tellServer() {
		Request request = new Request.Builder().url("http://27y9317r51.wicp.vip/?method=test").get().build();
		Call call = OkHttpUtil.getOkHttpClient().newCall(request);
		call.enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				in_thread_note("服务器错误,请稍后再试");
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				final String res = response.body().string();
				if (res.equals("ok"))
				{
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									Thread.sleep(1000*60);
									tellHandler.sendEmptyMessage(1002);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();


				}else{
					in_thread_note("连接失败");
				}

			}
		});
	}

	private void initWeather(String city){
		weatherQuery = new WeatherSearchQuery(
				city,
				WeatherSearchQuery.WEATHER_TYPE_LIVE);
		weatherSearch = new WeatherSearch(
				this);
		weatherSearch.setOnWeatherSearchListener(this);
		weatherSearch.setQuery(weatherQuery);
		weatherSearch.searchWeatherAsyn();//异步搜索

	}
	/**
	 * 初始化
	 */
	@SuppressLint("HandlerLeak")
	private void initUI(){

		mHelper = new LocationInfoDataBaseHelper(this);
		mDatabase = mHelper.getWritableDatabase();
		setStatusBarColor=new SetStatusBarColor(getWindow());
		location=new Location(this);
		exit_note = findViewById(R.id.exit_note);
		firstRun();
		location.startLocation();
		queryCityData();
        initWeather(CurrentCounty);
		bottomPanel = findViewById(R.id.bottom_layout);
		App=(PoiApplication)getApplication();
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what){
					case Constant.LOCATION_OK:
                        queryCityData();
                        if(CurrentCity!=null){
                        	headPanel.setCurrentName(CurrentCity+" "+CurrentCounty);
							initWeather(CurrentCounty);}
						break;
					case Constant.LOCATION_DATA_CHANGED:
					{
						SharedPreferences sharedPreferences=getSharedPreferences("LocationInfo",Context.MODE_PRIVATE);
						CurrentCity=sharedPreferences.getString("cityName","");
						CurrentCounty=sharedPreferences.getString("countyName","");
						headPanel.setCurrentName(CurrentCity+" "+CurrentCounty);
						initWeather(CurrentCounty);
					}
					break;
					case Constant.LOG_OK:{
						MeFragment meFragment= (MeFragment) getFragment(Constant.FRAGMENT_FLAG_Me);
						Objects.requireNonNull(meFragment.getView()).findViewById(R.id.user_log);
						meFragment.initUserInfo();
						meFragment.setFlag_for_server("登录成功");
					}
					break;

					case Constant.LOG_OUT:{
						MeFragment meFragment= (MeFragment) getFragment(Constant.FRAGMENT_FLAG_Me);
						Objects.requireNonNull(meFragment.getView()).findViewById(R.id.user_log);
						meFragment.setFlag_for_server("您还未登录,请先登录");
						meFragment.initUserInfo();
					}
					break;
					case Constant.HEAD_PIC_OK:{
						MeFragment meFragment= (MeFragment) getFragment(Constant.FRAGMENT_FLAG_Me);
						String str = msg.getData().getString("image_address",null);
						meFragment.setIv_userHead(str,false);
					}break;
					case Constant.UPDATE_USERINFO_TO_SERVER:
					{
						String type = msg.getData().getString("data");
						String data = msg.getData().getString(type);
						String url ="http://27y9317r51.wicp.vip/?method=update_userInfo";
						updateUserInfoToServer(url,type,data,App.getCurrentUserInfo().getUserName());
					}
					break;
					case 1001:
						exit_note.setVisibility(View.GONE);
						break;
				}
			}
		};
		App.setHandler(handler);
		if(bottomPanel != null){
			bottomPanel.initBottomPanel();
			bottomPanel.setBottomCallback(this);
		}
		headPanel = findViewById(R.id.head_layout);
		if(headPanel != null){
			headPanel.initHeadPanel();
			if(CurrentCounty == null){
				if(CurrentCity == null)
					headPanel.setCurrentName("正在定位...");}
			else{
				headPanel.setCurrentName(CurrentCity+" "+CurrentCounty);
			}
		}
	}

	/* 处理BottomControlPanel的回调
	 */
	@Override
	public void onBottomPanelClick(int itemId) {
		// TODO Auto-generated method stub
		String tag = "";
		if((itemId & Constant.BTN_FLAG_MESSAGE) != 0){
			tag = Constant.FRAGMENT_FLAG_MESSAGE;
		}else if((itemId & Constant.BTN_FLAG_RECOMMEND) != 0){
			tag = Constant.FRAGMENT_FLAG_RECOMMEND;
		}else if((itemId & Constant.BTN_FLAG_Me) != 0){
			tag = Constant.FRAGMENT_FLAG_Me;
		}else if((itemId & Constant.BTN_FLAG_SETTING) != 0){
			tag = Constant.FRAGMENT_FLAG_SETTING;
		}
		setTabSelection(tag); //切换Fragment
		headPanel.setMiddleTitle(tag);//切换标题
	}

	private void setDefaultFirstFragment(String tag){
		setTabSelection(tag);
		bottomPanel.defaultBtnChecked();
	}
	
	private void commitTransactions(String tag){
		if (fragmentTransaction != null && !fragmentTransaction.isEmpty()) {
			fragmentTransaction.commit();
			currFlagTag = tag;
			fragmentTransaction = null;
		}
	}
	
	private FragmentTransaction ensureTransaction( ){
		if(fragmentTransaction == null){
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}
		return fragmentTransaction;
		
	}
	
	private void attachFragment(int layout, Fragment f, String tag){
		if(f != null){
			if(f.isDetached()){
				ensureTransaction();
				fragmentTransaction.attach(f);
				
			}else if(!f.isAdded()){
				ensureTransaction();
				fragmentTransaction.add(layout, f, tag);
			}
		}
	}
	
	private Fragment getFragment(String tag){
		
		Fragment f = fragmentManager.findFragmentByTag(tag);
		
		if(f == null){
			f = BaseFragment.newInstance(getApplicationContext(), tag);
		}
		return f;
		
	}
	private void detachFragment(Fragment f){
		
		if(f != null && !f.isDetached()){
			ensureTransaction();
			fragmentTransaction.detach(f);
		}
	}
	/**切换fragment
	 * @param tag
	 */
	private  void switchFragment(String tag){
		if(TextUtils.equals(tag, currFlagTag)){
			return;
		}
		//把上一个fragment detach掉
		if(currFlagTag != null && !currFlagTag.equals("")){
			detachFragment(getFragment(currFlagTag));
		}
		attachFragment(R.id.fragment_content, getFragment(tag), tag);
		commitTransactions( tag);
	} 
	
	/**设置选中的Tag
	 * @param tag
	 */
	public  void setTabSelection(String tag) {
		// 开启一个Fragment事务
		fragmentTransaction = fragmentManager.beginTransaction();
		 switchFragment(tag);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		currFlagTag = "";
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
	}
	// 让GestureDetectorCompat来接替处理

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {//分发监听对象
		for (MyTouchListener listener : myTouchListeners) {
			listener.onTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	/**
	 * 实时天气
	 * @param localWeatherLiveResult
	 * @param rCode
	 */
	@Override
	public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int rCode) {
		if (rCode == 1000)
		{
			if (localWeatherLiveResult != null&&localWeatherLiveResult.getLiveResult() != null){
				LocalWeatherLive weatherlive = localWeatherLiveResult.getLiveResult();
				weather=weatherlive.getWeather();
				tempture=weatherlive.getTemperature()+"℃";
				headPanel.setWeather(weather+":"+tempture);
			}
		}

	}

	@Override
	public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

	}
	//建立接口
	public interface MyTouchListener {
		 void onTouchEvent(MotionEvent event);
	}
	private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<>();
	/**
	 * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
	 *
	 * @param listener
	 */
	public void registerMyTouchListener(MyTouchListener listener) {
		myTouchListeners.add(listener);
	}

	/**
	 *  提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
	 *  @param listener
	 *
	 */
	public void unRegisterMyTouchListener(MyTouchListener listener) {
		myTouchListeners.remove(listener);
	}

	/**
	 * 查询城市信息
	 */
	public void queryCityData() {
		Cursor cursor = mDatabase.query(LocationInfoDataBaseHelper.TABLE_NAME,
				null,
				null,
				null,
				null,
				null,
				null);// 注意空格！
		while (cursor.moveToNext()) {
			CurrentCity= (cursor.getString(cursor.getColumnIndex("city_name")));
			CurrentPoi=(cursor.getString(cursor.getColumnIndex("poi_name")));
			CurrentCounty=(cursor.getString(cursor.getColumnIndex("county_name")));

		}
		cursor.close();
	}

	public String getCurrentPoi(){
		return CurrentPoi;
	}
	public String getCurrentCity(){
		return CurrentCity;
	}
	public String getCurrentCounty(){
		return CurrentCounty;
	}

    public PoiApplication getApp() {
        return App;
    }

    private void firstRun() {
		SharedPreferences sharedPreferences = getSharedPreferences("FirstRun",0);
		first_run = sharedPreferences.getBoolean("First",true);
		if (first_run){
			sharedPreferences.edit().putBoolean("First",false).apply();
			showNormalDialog();
		}
		else {
			Toast.makeText(this,"欢迎回来！！！",Toast.LENGTH_LONG).show();
		}
	}
	private void showNormalDialog(){
		/**@setIcon 设置对话框图标
		 * @setTitle 设置对话框标题
		 * @setChat_message 设置对话框消息提示
		 * setXXX方法返回Dialog对象，因此可以链式设置属性
		 */
		final AlertDialog.Builder normalDialog =
				new AlertDialog.Builder(MainActivity.this);
		normalDialog.setIcon(R.drawable.location);
		normalDialog.setTitle("欢迎使用");
		normalDialog.setMessage("请选好您的地点");
		normalDialog.setPositiveButton("定位",
				(dialog, which) -> location.startLocation());
		normalDialog.setNegativeButton("取消",
				(dialog, which) -> {
				});
		// 显示
		normalDialog.show();
	}
	private void updateUserInfoToServer(String url, String type,String data,String userName){
		FormBody.Builder formBuilder = new FormBody.Builder();
		formBuilder.add(type, data);
		formBuilder.add("type",type);
        formBuilder.add("userName",userName);
		Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
		Call call = OkHttpUtil.getOkHttpClient().newCall(request);
		call.enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
				Log.e("服务器错误",e.toString());
				in_thread_note("服务器错误,请稍后再试");
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				final String res = response.body().string();
				if (res.equals("ok"))
				{
					in_thread_note("更新"+type+"成功");
				}else{
					in_thread_note("更新"+type+"失败");
				}

			}
		});
	}
	private void in_thread_note(String str){
		Looper.prepare();
		Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
		Looper.loop();
	}

    private static int count =1;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
//			Toast.makeText(this,"你点击了返回",Toast.LENGTH_LONG).show();
            exit_note.setVisibility(View.VISIBLE);
			count++;
			if(count>2){
                return super.onKeyDown(keyCode, event);
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    count=1;
                    handler.sendEmptyMessage(1001);
                }
            },2500);
			return true;
		}
        return super.onKeyDown(keyCode, event);
	}






}
