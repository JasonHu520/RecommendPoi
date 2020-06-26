package com.example.jasonhu.recommendpoi.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.MainActivity;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.adpter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MessageFragment extends BaseFragment implements  SwipeRefreshLayout.OnRefreshListener{

	private MainActivity mMainActivity ;
	private ViewPager mViewPaper;
	float mCurPosX,mPosX,mPosY,mCurPosY;
    private SwipeRefreshLayout swipeRefreshLayout;
	private List<ImageView> images;
    TextView title;
	private ViewPagerAdapter adapter;
	private int currentItem,oldposition=0;

	private ScheduledExecutorService scheduledExecutorService;

    //存放图片的id 
	private int[] imageIds = new int[]{
			R.drawable.ad_001,
			R.drawable.ad_002,
			R.drawable.ad_003
	};
	private String[]ad_title=new String[]{
			"爱情",
			"面包",
			"牛奶"
	};
	private MainActivity.MyTouchListener myTouchListener ;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View messageLayout = inflater.inflate(R.layout.message_layout,
				container, false);
		init();
		mViewPaper = (ViewPager)messageLayout.findViewById(R.id.ad_vp);
		title = (TextView) messageLayout.findViewById(R.id.title);

		images=new ArrayList<ImageView>();
		for (int imageId : imageIds) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setBackgroundResource(imageId);
			images.add(imageView);
		}
		title.setText(ad_title[0]);

		adapter = new ViewPagerAdapter(mMainActivity,images);
		mViewPaper.setAdapter(adapter);

		//viewpager监听
		mViewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {

			}

			@Override
			public void onPageSelected(int i) {
				title.setText(ad_title[i]);
				oldposition = i;
				currentItem = i;
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});
		swipeRefreshLayout = messageLayout.findViewById(R.id.recommend_layout);
		swipeRefreshLayout.setOnRefreshListener(this); // 设置刷新监听
		swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.purple,R.color.azure); // 进度动画颜色
		return messageLayout;
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			mViewPaper.setCurrentItem(currentItem);
		}
	};
	private class ViewPageTask implements Runnable{
		@Override
		public void run() {
			currentItem = (currentItem + 1) % imageIds.length;
			mHandler.sendEmptyMessage(0);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleWithFixedDelay(new ViewPageTask(),2,
				5, java.util.concurrent.TimeUnit.SECONDS);

	}

	@Override
	public void onStop() {
		super.onStop();
		if(scheduledExecutorService != null){
			scheduledExecutorService.shutdown();
			scheduledExecutorService = null;
		}
	}


	//初始化
	private void init(){

		mMainActivity = (MainActivity) getActivity();
		mFragmentManager = getActivity().getFragmentManager();
		myTouchListener = new MainActivity.MyTouchListener() {
			@Override
			public void onTouchEvent(MotionEvent event) {
				dealTouchEvent(event);
			}
		};
	}


	/**
	 * 处理滑动事件
	 * @param event
	 */
	private void dealTouchEvent(MotionEvent event) {
		String tag;
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
				if (mCurPosX - mPosX > 0&&(Math.abs(mCurPosY-mPosY)<100)&&mPosY>800
						&& (Math.abs(mCurPosX - mPosX) > 80)) {
					//向右滑動
					Toast.makeText(mainActivity,"别滑了，没啦",Toast.LENGTH_SHORT).show();

				} else if (mCurPosX - mPosX < 0&&(Math.abs(mCurPosY-mPosY)<100)&&mPosY>800
						&& (Math.abs(mCurPosX - mPosX) > 80)) {
					//向左滑动
					tag=Constant.FRAGMENT_FLAG_RECOMMEND;
					mainActivity.setTabSelection(tag); //切换Fragment
					mainActivity.headPanel.setMiddleTitle(tag);//切换标题
					mainActivity.bottomPanel.initBottomPanel();
					mainActivity.bottomPanel.BtnChecked(tag);
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
		MainActivity.currFlagTag = Constant.FRAGMENT_FLAG_MESSAGE;
		((MainActivity)getActivity()).registerMyTouchListener(myTouchListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		((MainActivity) getActivity()).unRegisterMyTouchListener(myTouchListener);
	}

	@Override
	public void onRefresh() {

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(false);
                mainActivity.getApp().getHandler().sendEmptyMessage(Constant.LOCATION_OK);
			}
		}, 2000);
		mMainActivity.headPanel.setCurrentName("刷新中...");
	}
}
