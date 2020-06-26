package com.example.jasonhu.recommendpoi.FunctionClass;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.example.jasonhu.recommendpoi.BaseClass.Utils;
import com.example.jasonhu.recommendpoi.DataBase.LocationInfoDataBaseHelper;
import com.example.jasonhu.recommendpoi.MainActivity;

/**
 * 高精度定位模式功能演示
 *
 * @创建时间： 2015年11月24日 下午5:22:42
 * @项目名称： AMapLocationDemo2.x
 * @author hongming.wang
 * @文件名称: Hight_Accuracy_Activity.java
 * @类型名称: Hight_Accuracy_Activity
 */
public class Location extends Activity {
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = null;
	private double lon;
	private double lat;
	private String cityName=null;
	private String poiName=null;
	private String address=null,countyName=null;
	private LocationInfoDataBaseHelper mHelper;
	private SQLiteDatabase mDatabase;
	private Context mContext;
	MainActivity mainActivity;


	public Location(Context context){
		mContext=context;
		mainActivity= (MainActivity) context;
	}

	/**
	 * 初始化定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void initLocation(){

		//初始化数据库
		mHelper = new LocationInfoDataBaseHelper(mContext);
		mDatabase = mHelper.getWritableDatabase();
		//初始化client
		locationClient = new AMapLocationClient(mContext);
		locationOption = getDefaultOption();
		//设置定位参数
		locationClient.setLocationOption(locationOption);
		// 设置定位监听
		locationClient.setLocationListener(locationListener);
	}
	
	/**
	 * 默认的定位参数
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private AMapLocationClientOption getDefaultOption(){
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
		return mOption;
	}
	
	/**
	 * 定位监听
	 */

	private AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation location) {
			if (null != location) {
				StringBuilder sb = new StringBuilder();
				//errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
				if(location.getErrorCode() == 0){
					sb.append("经    度    : ").append(location.getLongitude()).append("\n");
					lon=location.getLongitude();
					sb.append("纬    度    : ").append(location.getLatitude()).append("\n");
					lat=location.getLatitude();
					sb.append("精    度    : ").append(location.getAccuracy()).append("米").append("\n");

					sb.append("速    度    : ").append(location.getSpeed()).append("米/秒").append("\n");
					sb.append("角    度    : ").append(location.getBearing()).append("\n");
					// 获取当前提供定位服务的卫星个数
					sb.append("星    数    : ").append(location.getSatellites()).append("\n");
					sb.append("国    家    : ").append(location.getCountry()).append("\n");
					sb.append("省            : ").append(location.getProvince()).append("\n");
					cityName=location.getCity();
					sb.append("市            : ").append(location.getCity()).append("\n");
					sb.append("城市编码 : ").append(location.getCityCode()).append("\n");
					sb.append("区            : ").append(location.getDistrict()).append("\n");
                    countyName=location.getDistrict();
					sb.append("区域 码   : ").append(location.getAdCode()).append("\n");
					sb.append("地    址    : ").append(location.getAddress()).append("\n");
					address=location.getAddress();
					sb.append("兴趣点    : ").append(location.getPoiName()).append("\n");
					poiName=location.getPoiName();
					//定位完成的时间
					sb.append("定位时间: ").append(Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss")).append("\n");
				}
				//解析定位结果，
				deleteTable(mDatabase);
				insertData(cityName,lat,lon,address,poiName,countyName);


			} else {
				//定位失败
				Toast.makeText(mContext,"失败",Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * @author Jason Hu
	 * 插入数据到地理位置数据库
	 * @param CityName
	 * @param lat
	 * @param lon
	 * @param address
	 * @param poiName
	 */
	private void insertData(String CityName, double lat, double lon, String address, String poiName,String countyName) {
		ContentValues values = new ContentValues();
		values.put(LocationInfoDataBaseHelper.city_name, CityName);
		values.put(LocationInfoDataBaseHelper.poi_lat,lat );
		values.put(LocationInfoDataBaseHelper.poi_lon,lon);
		values.put(LocationInfoDataBaseHelper.poiName,poiName);
		values.put(LocationInfoDataBaseHelper.poi_address,address);
        values.put(LocationInfoDataBaseHelper.county_name,countyName);
		mDatabase.insert(LocationInfoDataBaseHelper.TABLE_NAME, null, values);
	}

	/**
	 * 开始定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	public void startLocation(){
		initLocation();
		// 设置定位参数
		locationClient.setLocationOption(locationOption);
		// 启动定位
		locationClient.startLocation();
	}
	
	/**
	 * 停止定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	public void stopLocation(){
		// 停止定位
		locationClient.stopLocation();
	}
	
	/**
	 * 销毁定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	public void destroyLocation(){
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}
	public void deleteTable(SQLiteDatabase db){
		db.execSQL("delete from lon_lat");
	}

}
