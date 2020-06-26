package com.example.jasonhu.recommendpoi.fragment;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.BaseClass.Callback.OnMyItemClickListener;
import com.example.jasonhu.recommendpoi.BaseClass.Check.CheckNet;
import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.BaseClass.NetWorkChangeReceiver;
import com.example.jasonhu.recommendpoi.bean.UserInfo;
import com.example.jasonhu.recommendpoi.BaseClass.http.OkHttpUtil;
import com.example.jasonhu.recommendpoi.BaseClass.picture_util.ImageUtils;
import com.example.jasonhu.recommendpoi.BaseClass.util.PackageUtils;
import com.example.jasonhu.recommendpoi.DataBase.UserInfoDatabaseHelper;
import com.example.jasonhu.recommendpoi.FunctionClass.Chat.ChatActivity;
import com.example.jasonhu.recommendpoi.FunctionClass.LoginActivity;
import com.example.jasonhu.recommendpoi.FunctionClass.UserInfoActivity;
import com.example.jasonhu.recommendpoi.MainActivity;
import com.example.jasonhu.recommendpoi.PoiApplication;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.adpter.FriendAdapter;
import com.example.jasonhu.recommendpoi.bean.FriendOder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MeFragment extends BaseFragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

	float mCurPosX,mPosX,mPosY,mCurPosY;
	private MainActivity.MyTouchListener myTouchListener ;
	LinearLayout user_layout;
	MainActivity mainActivity;
	View meLayout;
	private TextView tv_currentPoi;
	TextView tv_login,falg_for_server;
	ImageView head_pic_view,backround_view;
    SQLiteDatabase mDatabase;
    UserInfoDatabaseHelper sqLiteOpenHelper;
    SharedPreferences sharedPreferences;
	Intent mGlobleIntent;
	UserInfo userInfo;
	PoiApplication App;
	String user;
	static final String noUser="登录/注册";
    Calendar calendar=null;
    Handler handler;
	Bitmap bitmap;

	JSONObject jsonObject;

	private RecyclerView recyclerView;
	private FriendOder friendOder;
	private FriendAdapter friendAdapter;
	ArrayList<FriendOder> friendOderList;
    private SwipeRefreshLayout swipeRefreshLayout;
	private LinearLayoutManager linearLayoutManager;
	private boolean isRegistered = false;
	private NetWorkChangeReceiver netWorkChangReceiver;
	boolean network_biaozhi = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		meLayout = inflater.inflate(R.layout.me_layout, container,
				false);
		init();
		initUserInfo();
		return meLayout;
	}
	public void changeflag(boolean netWork_state){
		if(!netWork_state){
			falg_for_server.setVisibility(View.VISIBLE);
			falg_for_server.setText("您的网络不给力哦，点我设置？");
			falg_for_server.setClickable(true);
		}
		else{
			if(network_biaozhi) {
				falg_for_server.setVisibility(View.VISIBLE);
				falg_for_server.setClickable(false);
				String url = "http://27y9317r51.wicp.vip/?method=isOnline";
				falg_for_server.setText("正在连接...");
				tellIamOnlinetoServer(url, user);
				network_biaozhi = false;
			}
		}
	}
	public void initUserInfo(){
		//检查网络
		sharedPreferences=mainActivity.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
		user=sharedPreferences.getString("userName",noUser);
		assert user != null;
		if(user.equals(noUser)){
			mGlobleIntent=new Intent(mainActivity,LoginActivity.class);
            setIv_userHead(userInfo.getHead_picture(),true);
		}else{
			mGlobleIntent=new Intent(mainActivity,UserInfoActivity.class);
			queryUserData(user);
			setIv_userHead(userInfo.getHead_picture(),false);
			String url = "http://27y9317r51.wicp.vip/?method=get_friend_list";
			queryUserFriend(user);
			if(friendOderList.size()==0){
				getFriendList(url,user);
			}else{
				for(int i=0;i<friendOderList.size();i++){
					File file = new File(friendOderList.get(i).getHead_pic());
					if(!file.exists()){
						url = "http://27y9317r51.wicp.vip/?method=putPictoAndroid";
						getDataFromServer(url,friendOderList.get(i).getName(),
								ImageUtils.parse_picName(friendOderList.get(i).getHead_pic()),true,i,"friend_head");
					}
				}
				friendAdapter.notifyDataSetChanged();
			}
//
		}
		setTv_UserName(user);
	}
	public void setFlag_for_server(String str){
		falg_for_server.setVisibility(View.VISIBLE);
		falg_for_server.setText(str);
		new Handler().postDelayed(() -> {
			falg_for_server.setVisibility(View.GONE);
		}, 2000);    //延时1s执行
	}
	/**
	 * 初始化
	 */
	@SuppressLint("HandlerLeak")
	private void init(){
		mainActivity=(MainActivity)getActivity();
		App= (PoiApplication) mainActivity.getApplication();
		myTouchListener = this::dealTouchEvent;
		tv_currentPoi= meLayout.findViewById(R.id.CurrentPoi_tv);
		tv_currentPoi.setText(mainActivity.getCurrentPoi());
		user_layout= meLayout.findViewById(R.id.user_layout);
		tv_login= meLayout.findViewById(R.id.user_log);
		falg_for_server = meLayout.findViewById(R.id.flag_for_server);
        swipeRefreshLayout = meLayout.findViewById(R.id.me_layout_refresh);
        swipeRefreshLayout.setOnRefreshListener(this); // 设置刷新监听
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.gray,R.color.azure); // 进度动画颜色
		tv_login.setOnClickListener(this);
		user_layout.setOnClickListener(this);
        sqLiteOpenHelper = new UserInfoDatabaseHelper(mainActivity,UserInfoDatabaseHelper.DB_NAME_LOG,null,1);
        mDatabase = sqLiteOpenHelper.getWritableDatabase();
        userInfo=new UserInfo();
        calendar=Calendar.getInstance();
		head_pic_view = meLayout.findViewById(R.id.headPic_for_current_user);
		backround_view = meLayout.findViewById(R.id.ad_vp);
		backround_view.setOnClickListener(this);
		recyclerView = meLayout.findViewById(R.id.lv_friend);

		linearLayoutManager = new LinearLayoutManager(mainActivity);
		recyclerView.setLayoutManager(linearLayoutManager);
        sharedPreferences=mainActivity.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        user=sharedPreferences.getString("userName",noUser);


		friendOderList=new ArrayList<>();
		friendAdapter=new FriendAdapter(mainActivity,friendOderList);
		recyclerView.setAdapter(friendAdapter);
		friendAdapter.setOnItemClickListener(new OnMyItemClickListener() {
			@Override
			public void onClick(int position) {
				Intent intent=new Intent(mainActivity,ChatActivity.class);
				intent.putExtra("name",userInfo.getUserName());
				intent.putExtra("to_user",friendOderList.get(position).getName());
				startActivity(intent);
//				Intent intent=new Intent(mainActivity,TesetServer.class);
//				startActivity(intent);
			}

			@Override
			public void onLongClick(int position) {

			}
		});

		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what){
					case 1:
						if(msg.getData().getBoolean("isFriend",false)){
							String new_image_addr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
									File.separator + PackageUtils.getAppName(mainActivity) + File.separator + "friend_head_pic" + File.separator;
							File file = new File(new_image_addr);
							if (!file.exists())
								file.mkdirs();
							ImageUtils.saveBmp2Gallery(new_image_addr, bitmap, msg.getData().getString("pic_name"), mainActivity);
							friendOderList.get(msg.getData().getInt("position")).setHead_pic(new_image_addr+msg.getData().getString("pic_name"));
							friendAdapter.notifyDataSetChanged();
						}
						else {
							String new_image_addr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
									File.separator + PackageUtils.getAppName(mainActivity) + File.separator;
							File file = new File(new_image_addr);
							if (!file.exists())
								file.mkdirs();
							ImageUtils.saveBmp2Gallery(new_image_addr, bitmap, msg.getData().getString("pic_name"), mainActivity);
							ContentValues values = new ContentValues();
							values.put("head_image_address", new_image_addr + msg.getData().getString("pic_name"));
							mDatabase.update("LogInfo", values, "userName=?", new String[]{userInfo.getUserName()});
							head_pic_view.setImageBitmap(bitmap);
							head_pic_view.invalidate();
						}
						break;
					case 2:
						//TODO 连接服务器失败
                        falg_for_server.setVisibility(View.VISIBLE);
                        falg_for_server.setText("连接服务器失败，请刷新");
						break;
					case 3:
						//TODO 连接服务器成功
                        falg_for_server.setVisibility(View.VISIBLE);
                        falg_for_server.setText("连接服务器成功");
                        new Handler().postDelayed(() -> {
                            //do something
                            falg_for_server.setVisibility(View.GONE);
                        }, 2000);    //延时1s执行
						break;
					case Constant.NET_WORK_CHANGE:
						{
							if(CheckNet.getNetState(mainActivity)==CheckNet.NET_NONE)
							{
								changeflag(false);
								network_biaozhi = true;
							}else{
								changeflag(true);
							}
						}
						break;
					case 10:
						JSONArray friendlist = jsonObject.optJSONArray("friendList");
						friendOderList.clear();
						ArrayList<Boolean> arrayList =new ArrayList<>();
						for(int i=0;i<friendlist.length();i++){
							try {
								JSONObject object =friendlist.getJSONObject(i);

								String pic_name = ImageUtils.parse_picName(object.getString("head_pic"));
								String new_image_addr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
										File.separator + PackageUtils.getAppName(mainActivity) + File.separator + "friend_head_pic" + File.separator;
								File file = new File(new_image_addr+pic_name);
								if(file.exists()){
									arrayList.add(true);//本地有
								}else{
									arrayList.add(false);//本地无
								}
								friendOder = new FriendOder("你好",object.getString("friend_name"),
										new_image_addr+pic_name);
								friendOder.setTime("默认时间");
								friendOderList.add(friendOder);

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						friendAdapter.notifyDataSetChanged();
						//Todo 从服务器获取图片
						for (int i = 0;i<friendOderList.size();i++)
						{	//如果本地有暂不从服务器加载
							if(!arrayList.get(i))
							{
								String pic_name = ImageUtils.parse_picName(friendOderList.get(i).getHead_pic());
								String url = "http://27y9317r51.wicp.vip/?method=putPictoAndroid";
								getDataFromServer(url,friendOderList.get(i).getName(),pic_name,true,i,"friend_head");
							}
						}
						for(int i= 0;i<friendOderList.size();i++){
							if(!isFriendInLocal(user,friendOderList.get(i).getName())){
								ContentValues values = new ContentValues();
								values.put("currentUser",user);
								values.put("friendName",friendOderList.get(i).getName());
								values.put("message",friendOderList.get(i).getMessage());
								values.put("headPic",friendOderList.get(i).getHead_pic());
								mDatabase.insert("friendInfo",null,values);
							}
						}
						break;
				}
			}
		};

		//网络监测
		netWorkChangReceiver = new NetWorkChangeReceiver(handler);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mainActivity.registerReceiver(netWorkChangReceiver, filter);
		isRegistered = true;


		falg_for_server.setOnClickListener(v -> {
			Intent intent =  new Intent(Settings.ACTION_SETTINGS);
			startActivity(intent);
		});

//		new Runnable() {
//			@Override
//			public void run() {
//				String url = "http://27y9317r51.wicp.vip/isOnline";
//				tellIamOnlinetoServer(url,user);
//				if (!isPause) {
//					//递归调用本runable对象，实现每隔30秒一次请求数据
//					mhandle.postDelayed(this, 50*1000);
//				}
//			}
//		}.run();

	}

    /**
     * 处理滑动事件
     * @param event
     */
	private void dealTouchEvent(MotionEvent event) {
		String tag= MainActivity.currFlagTag;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mPosX = event.getX();
				mPosY=event.getY();
				mCurPosX = mPosX;
				mCurPosY=mPosY;
				break;
			case MotionEvent.ACTION_MOVE:
				mCurPosX = event.getX();
				mCurPosY=event.getY();
				break;
			case MotionEvent.ACTION_UP:
				if (mCurPosX - mPosX > 0&&(Math.abs(mCurPosY-mPosY)<100)
						&& (Math.abs(mCurPosX - mPosX) > 80)) {
					//向右滑動
					tag=Constant.FRAGMENT_FLAG_RECOMMEND;
				} else if (mCurPosX - mPosX < 0&&(Math.abs(mCurPosY-mPosY)<100)
						&& (Math.abs(mCurPosX - mPosX) > 80)) {
					//向左滑动
					tag=Constant.FRAGMENT_FLAG_SETTING;

				}
				mainActivity.setTabSelection(tag); //切换Fragment
				mainActivity.headPanel.setMiddleTitle(tag);//切换标题
				mainActivity.bottomPanel.initBottomPanel();
				mainActivity.bottomPanel.BtnChecked(tag);
				break;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MainActivity.currFlagTag = Constant.FRAGMENT_FLAG_Me;
		((MainActivity)getActivity()).registerMyTouchListener(myTouchListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		((MainActivity) getActivity()).unRegisterMyTouchListener(myTouchListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.user_layout:
			{
				if(!user.equals(noUser)) {
					Intent intent = new Intent(mainActivity, UserInfoActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(mainActivity,"您还未登录,请先登录",Toast.LENGTH_SHORT).show();
				}
			}
			break;
			case R.id.user_log:
			{
				startActivity(mGlobleIntent);
			}
			break;
			case R.id.ad_vp:
			{
				Toast.makeText(mainActivity,"哇，你点到了背景，你好厉害哦",Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	public void setTv_UserName(String userName){
		tv_login.setText(userName);
	}
	public void setIv_userHead(String imagePath,boolean is_log_out){
	    if(!is_log_out){
            if (imagePath != null){
				File file = new File(imagePath);
				if(file.exists()){
                	ImageUtils.loadLocalPicNoOverride(mainActivity,imagePath , head_pic_view);
				}
				else{
					//Todo 从服务器获取图片
					String pic_name = ImageUtils.parse_picName(imagePath);
					String url = "http://27y9317r51.wicp.vip/?method=putPictoAndroid";
					getDataFromServer(url,user,pic_name,false,0,"user_head");
				}
            }
	    }else{
	        head_pic_view.setImageResource(R.drawable.user);
        }
	}

    /**
     * 查询用户登录状态
     * @param userName
     * @return
     */
    public void  queryUserData(String userName) {
        Cursor cursor = mDatabase.query("LogInfo",
                null,
                null,
                null,
                null,
                null,
                null);// 注意空格！
        if (cursor != null) {
			while (cursor.moveToNext()) {
				if(userName.equals(cursor.getString(cursor.getColumnIndex("userName")))){
			   userInfo.setCity(cursor.getString(cursor.getColumnIndex("City")));
			   userInfo.setUserName(userName);
			   userInfo.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			   userInfo.setLogstate(cursor.getString(cursor.getColumnIndex("LogState")));
			   userInfo.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
			   userInfo.setSecret(cursor.getString(cursor.getColumnIndex("password")));
			   try{
			   		userInfo.setHead_picture(cursor.getString(cursor.getColumnIndex("head_image_address")));
			   }catch (Exception e){
			   		System.out.println(e.toString());
				   userInfo.setHead_picture("");
			   }
			   App.setCurrentUserInfo(userInfo);
				}
			}
    	}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
    }

	private void  queryUserFriend(String userName){
		Cursor cursor = mDatabase.query("friendInfo",
				null,
				null,
				null,
				null,
				null,
				null);// 注意空格！
		if (cursor != null) {
			friendOderList.clear();
			while (cursor.moveToNext()) {
				if(userName.equals(cursor.getString(cursor.getColumnIndex("currentUser")))){

					friendOderList.add(new FriendOder(cursor.getString(cursor.getColumnIndex("message")),
							cursor.getString(cursor.getColumnIndex("friendName")),
							cursor.getString(cursor.getColumnIndex("headPic"))));
//					userInfo.setLogstate(cursor.getInt(cursor.getColumnIndex("position")));
//					userInfo.setSecret(cursor.getString(cursor.getColumnIndex("currentTime")));
				}
			}
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
	}
	private boolean isFriendInLocal(String userName,String friend_name){
		Cursor cursor = mDatabase.query("friendInfo",
				null,
				null,
				null,
				null,
				null,
				null);// 注意空格！
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if(userName.equals(cursor.getString(cursor.getColumnIndex("currentUser")))){
					if(friend_name.equals(cursor.getString(cursor.getColumnIndex("friendName"))))
						return true;
				}
			}
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return false;
	}

	public void getDataFromServer(String url,final String userName,final String pic_name,boolean isFriend,int position,String picQuality){
    	FormBody.Builder formBuilder = new FormBody.Builder();
		formBuilder.add("userName", userName);
		formBuilder.add("pic_name", pic_name);
		formBuilder.add("pic_quality", picQuality);
		Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
		Call call = OkHttpUtil.getOkHttpClient().newCall(request);
		call.enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
				in_thread_note("服务器错误");
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				InputStream res = response.body().byteStream();
				bitmap = BitmapFactory.decodeStream(res);

				Message message=new Message();
				Bundle bundle=new Bundle();
				bundle.putString("pic_name",pic_name);
				bundle.putBoolean("isFriend",isFriend);
				if(isFriend){
					bundle.putInt("position",position);
				}
				message.setData(bundle);
				message.what = 1;
				handler.sendMessage(message);
			}
		});
	}
	private void in_thread_note(String str){
		Looper.prepare();
		Toast.makeText(mainActivity,str,Toast.LENGTH_SHORT).show();
		Looper.loop();
	}


	public void tellIamOnlinetoServer(String url,final String userName)
	{
		FormBody.Builder formBuilder = new FormBody.Builder();
		formBuilder.add("userName", userName);
		Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
		Call call = OkHttpUtil.getOkHttpClient().newCall(request);
		call.enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
				Log.e("服务器错误",e.toString());
				handler.sendEmptyMessage(2);
				in_thread_note("服务器错误,请稍后再试");
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				final String res = response.body().string();
				if(res.equals("1"))
				{
//                        showSuccessSweetDialog(res);
					handler.sendEmptyMessage(3);
				}
			}
		});

	}
	private void getFriendList(String url,final String userName){
		FormBody.Builder formBuilder = new FormBody.Builder();
		formBuilder.add("userName", userName);
		Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
		Call call = OkHttpUtil.getOkHttpClient().newCall(request);
		call.enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
				Log.e("服务器错误",e.toString());
//				handler.sendEmptyMessage(2);
				in_thread_note("服务器错误,请稍后再试");
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				final String res = response.body().string();
				try {
					jsonObject = new JSONObject(res);
					handler.sendEmptyMessage(10);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 下拉刷新与服务器的交互
	 */
    @Override
    public void onRefresh() {
		falg_for_server.setVisibility(View.VISIBLE);
		if (user.equals(noUser)){
			falg_for_server.setText("当前未登录,请先登录");
			swipeRefreshLayout.setRefreshing(false);}
		else {
			falg_for_server.setText("正在刷新...");
			new Handler().postDelayed(() -> {
				String url = "http://27y9317r51.wicp.vip/?method=isOnline";
				swipeRefreshLayout.setRefreshing(false);
				falg_for_server.setVisibility(View.GONE);
				tellIamOnlinetoServer(url,user);
			}, 2000);
    	}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//解绑
		if (isRegistered) {
			mainActivity.unregisterReceiver(netWorkChangReceiver);
		}
	}
}
