package com.example.jasonhu.recommendpoi.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.MainActivity;


public class BaseFragment extends Fragment {
	private static final String TAG = "BaseFragment";
	protected FragmentManager mFragmentManager = null;
	protected FragmentTransaction mFragmentTransaction = null;
	MainActivity mainActivity;
    private static final int default_background_color = Color.rgb(0, 0, 0);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		mainActivity=(MainActivity) getActivity();


		//mainActivity.getWindow().setStatusBarColor();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onCreateView...");
//		View v = inflater.inflate(R.layout.messages_layout, container, false);
		
		return 	super.onCreateView(inflater, container, savedInstanceState);
	}

	public static BaseFragment newInstance(Context context,String tag){
		BaseFragment baseFragment =  null;
		if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_RECOMMEND)){
			baseFragment = new RecommendFragment();
		}else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_MESSAGE)){
			baseFragment = new MessageFragment();
		}else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_Me)){
			baseFragment = new MeFragment();
		}else if(TextUtils.equals(tag, Constant.FRAGMENT_FLAG_SETTING)){
			baseFragment = new SettingFragment();
		}
		return baseFragment;
	}


}
