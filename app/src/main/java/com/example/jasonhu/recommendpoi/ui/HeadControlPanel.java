package com.example.jasonhu.recommendpoi.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.FunctionClass.ChooseAreaActivity;
import com.example.jasonhu.recommendpoi.R;

public class HeadControlPanel extends RelativeLayout implements View.OnClickListener{

	private Context mContext;
	private TextView mMidleTitle;
	private TextView mRightTitle;
	private TextView City;
	private TextView Weather;
	private static final float middle_title_size = 20f; 
	private static final float right_title_size = 17f;
	private SharedPreferences sharedPreferences;

	//private static final int default_background_color = Color.rgb(0, 0, 0);
	
	public HeadControlPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mMidleTitle = (TextView) findViewById(R.id.midle_title);
		//mRightTitle = (TextView) findViewById(R.id.right_title);
		City=findViewById(R.id.city_tv);
		Weather=findViewById(R.id.weather_tv);
		sharedPreferences = mContext.getSharedPreferences("Weather", Context.MODE_PRIVATE);
		//setBackgroundColor(default_background_color);
	}
	public void initHeadPanel(){
		String cityName,weather;
		cityName=sharedPreferences.getString("cityName", "");
		weather=String.format("%s:%s", sharedPreferences.getString("dayWeather", ""), sharedPreferences.getString("temp", ""));
		if(mMidleTitle != null){
			setMiddleTitle(Constant.FRAGMENT_FLAG_MESSAGE);
		}
		if(City!=null){
			City.setText(cityName);
			City.setOnClickListener(this);
		}
		if(Weather!=null){
			Weather.setText(weather);
		}


	}
	public void setMiddleTitle(String s){
		mMidleTitle.setText(s);
		mMidleTitle.setTextSize(middle_title_size);
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.city_tv){
			Intent intent = new Intent(mContext, ChooseAreaActivity.class);
			mContext.startActivity(intent);
		}
	}
	public void setWeather(String str){

		if(Weather!=null){
		    Weather.setText(str);
		}

	}

	/**
	 * 设置主界面的城市信息
	 * @param str
	 */
	public void setCurrentName(String str){
		City.setText(str);
	}

}
