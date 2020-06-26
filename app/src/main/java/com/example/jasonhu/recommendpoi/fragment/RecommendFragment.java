package com.example.jasonhu.recommendpoi.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.MainActivity;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.view.AutoSplitTextView;

public class RecommendFragment extends BaseFragment {
	float mCurPosX,mPosX,mPosY,mCurPosY;
	private MainActivity.MyTouchListener myTouchListener ;
	MainActivity mainActivity;
	Button btn_recommend;
	TextView name,distance,address;
	SharedPreferences sharedPreferences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contactsLayout = inflater.inflate(R.layout.recommend_layout,
				container, false);
		mainActivity=(MainActivity)getActivity();
		sharedPreferences=mainActivity.getSharedPreferences("RecommendList",Context.MODE_PRIVATE);
		btn_recommend=contactsLayout.findViewById(R.id.btn_recommend);
		name=contactsLayout.findViewById(R.id.recommend_name);
		name.setSingleLine(false);
		name.setHorizontallyScrolling(false);
		distance=contactsLayout.findViewById(R.id.recommend_distance);
		address=contactsLayout.findViewById(R.id.recommend_address);
		name.setText("暂无");
		distance.setText("暂无");
		address.setText("暂无");
		btn_recommend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					name.setText(sharedPreferences.getString("name",""));
					distance.setText(sharedPreferences.getString("distance",""));
					address.setText(sharedPreferences.getString("address",""));

			}
		});

		myTouchListener = new MainActivity.MyTouchListener() {
			@Override
			public void onTouchEvent(MotionEvent event) {
				dealTouchEvent(event);
			}
		};
		return contactsLayout;
	}

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
					tag=Constant.FRAGMENT_FLAG_MESSAGE;
				} else if (mCurPosX - mPosX < 0&&(Math.abs(mCurPosY-mPosY)<100)
						&& (Math.abs(mCurPosX - mPosX) > 80)) {
					//向左滑动
					tag=Constant.FRAGMENT_FLAG_Me;
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
		MainActivity.currFlagTag = Constant.FRAGMENT_FLAG_RECOMMEND;
        ((MainActivity)getActivity()).registerMyTouchListener(myTouchListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		((MainActivity) getActivity()).unRegisterMyTouchListener(myTouchListener);

	}


}
