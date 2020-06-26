package com.example.jasonhu.recommendpoi.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.BaseClass.Callback.OnMyItemClickListener;
import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.FunctionClass.HistoryActivity;
import com.example.jasonhu.recommendpoi.FunctionClass.POIListActivity;
import com.example.jasonhu.recommendpoi.MainActivity;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.adpter.SettingAdapter;

import java.util.ArrayList;

public class SettingFragment extends BaseFragment {

	float mCurPosX,mPosX,mCurPosY,mPosY;
	private ListView setting_list;
	View settingLayout;
	private MainActivity.MyTouchListener myTouchListener ;
	ArrayList<String> datalist;
	MainActivity mainActivity;


	private RecyclerView recyclerView;
	private SettingAdapter myAdapter;
	private LinearLayoutManager linearLayoutManager;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 settingLayout = inflater.inflate(R.layout.setting_layout,
				container, false);

		 init();
		return settingLayout;
	}
	public void init(){

		mainActivity= (MainActivity)getActivity();

		/** init recyclerView */
		recyclerView = settingLayout.findViewById(R.id.setting_list);
		linearLayoutManager = new LinearLayoutManager(mainActivity);
		//linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(linearLayoutManager);


		datalist=new ArrayList<>();
		datalist.add("附近");
		datalist.add("历史记录");
		myAdapter=new SettingAdapter(mainActivity,datalist);
		recyclerView.setAdapter(myAdapter);
		myAdapter.setOnItemClickListener(new OnMyItemClickListener() {
			@Override
			public void onClick(int position) {
				if(position==0)
				{
					Intent intent = new Intent();
					intent.setClass(mainActivity,POIListActivity.class);
					startActivity(intent);
				}

				if(position==1){
					Intent intent = new Intent();
					intent.setClass(mainActivity,HistoryActivity.class);
					startActivity(intent);
				}
			}

			@Override
			public void onLongClick(int position) {

			}
		});
		myTouchListener = new MainActivity.MyTouchListener() {
			@Override
			public void onTouchEvent(MotionEvent event) {
				dealTouchEvent(event);
			}
		};

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

					tag=Constant.FRAGMENT_FLAG_Me;
                    mainActivity.setTabSelection(tag); //切换Fragment
                    mainActivity.headPanel.setMiddleTitle(tag);//切换标题
                    mainActivity.bottomPanel.initBottomPanel();
                    mainActivity.bottomPanel.BtnChecked(tag);
				} else if (mCurPosX - mPosX < 0&&(Math.abs(mCurPosY-mPosY)<100)
						&& (Math.abs(mCurPosX - mPosX) > 80)) {
					//向左滑动
					Toast.makeText(mainActivity,"别滑了，没啦",Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MainActivity.currFlagTag = Constant.FRAGMENT_FLAG_SETTING;
		((MainActivity)getActivity()).registerMyTouchListener(myTouchListener);
		
	}
	@Override
	public void onPause() {
		super.onPause();
		((MainActivity) getActivity()).unRegisterMyTouchListener(myTouchListener);

	}


}
