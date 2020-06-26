package com.example.jasonhu.recommendpoi.FunctionClass;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.jasonhu.recommendpoi.BaseClass.Callback.OnMyItemClickListener;
import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.BaseClass.SetStatusBarColor;
import com.example.jasonhu.recommendpoi.bean.UserInfo;
import com.example.jasonhu.recommendpoi.PoiApplication;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.adpter.SettingAdapter;
import com.example.jasonhu.recommendpoi.ui.MyNoteActivity;

import java.util.ArrayList;

/**
 * 用户信息
 */
public class UserInfoActivity extends Activity implements View.OnClickListener {
    ImageView mImgReturn;
    PoiApplication App;
    Handler mHandler;
    UserInfo userInfo;
    ArrayList<String> datalist;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SettingAdapter myAdapter;

    SetStatusBarColor setStatusBarColor;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_layout);
        init();
    }
    private void init(){
        mImgReturn=findViewById(R.id.userInfo_return);
        App=(PoiApplication)getApplication();
        userInfo=App.getCurrentUserInfo();
        mHandler=App.getHandler();
        mImgReturn.setOnClickListener(this);
        recyclerView = findViewById(R.id.userInfo_choice_list);
        linearLayoutManager = new LinearLayoutManager(UserInfoActivity.this);

        //初始化状态栏颜色
        setStatusBarColor=new SetStatusBarColor(getWindow());
        setStatusBarColor.setStatusBar(getResources().getColor(R.color.divider_color));

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        datalist=new ArrayList<>();
        datalist.add("查看信息");
        datalist.add("修改信息");
        datalist.add("记录点滴");
        datalist.add("退出登录");
        myAdapter=new SettingAdapter(UserInfoActivity.this,datalist);
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new OnMyItemClickListener() {
            @Override
            public void onClick(int position) {
                if(position==0)
                {
                    Intent intent = new Intent();
                    intent.setClass(UserInfoActivity.this,PersonalInfo.class);
                    startActivity(intent);
                }

                if(position==1){
                   Intent intent = new Intent();
                    intent.setClass(UserInfoActivity.this,UpdateInfo.class);
                    startActivity(intent);
                }
                if(position==2){
                    Intent intent = new Intent();
                    intent.setClass(UserInfoActivity.this,MyNoteActivity.class);
                    startActivity(intent);
                }
                if(position==3){
                    showNormalDialog();
                }
            }
            @Override
            public void onLongClick(int position) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.userInfo_return:
            {
                finish();
            }
            break;
        }
    }
    private void showNormalDialog(){
        /*
         * @setTitle 设置对话框标题
         * @setChat_message 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(UserInfoActivity.this);
        normalDialog.setTitle("退出账号");
        normalDialog.setMessage("确定退出?");
        normalDialog.setPositiveButton("确定",
                (dialog, which) -> {
                    //TODO 确认退出
                    App.setCurrentUserInfo(null);
                    mHandler.sendEmptyMessage(Constant.LOG_OUT);
                    SharedPreferences.Editor editor=getSharedPreferences("UserInfo",Context.MODE_PRIVATE).edit();
                    editor.putString("userName","登录/注册");
                    editor.apply();

                    finish();
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO 取消退出
                    }
                });
        // 显示
        normalDialog.show();
    }

}
