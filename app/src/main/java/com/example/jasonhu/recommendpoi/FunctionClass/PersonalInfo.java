package com.example.jasonhu.recommendpoi.FunctionClass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jasonhu.recommendpoi.BaseClass.SetStatusBarColor;
import com.example.jasonhu.recommendpoi.bean.UserInfo;
import com.example.jasonhu.recommendpoi.BaseClass.picture_util.ImageUtils;
import com.example.jasonhu.recommendpoi.PoiApplication;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.ui.MyNoteActivity;

/**
 * Created by JasonHu 2018.12.9
 * 个人信息查看类
 */
public class PersonalInfo extends Activity implements View.OnClickListener{

    TextView user_name_tv;
    TextView email_tv;
    TextView phone_tv;
    TextView habit_tv;
    TextView birthday_tv;
    TextView my_note;
    TextView city_tv;

    RelativeLayout relativeLayout;

    ImageView head_img;
    ImageView personInfo_return;

    SetStatusBarColor setStatusBarColor;
    //获取当前用户
    PoiApplication App;
    UserInfo userInfo;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personinfo_layout);
        init();
        setView();
    }

    /**
     * 初始化
     */
    private void init(){
        user_name_tv=findViewById(R.id.user_name_tv);
        email_tv=findViewById(R.id.email_tv);
        phone_tv=findViewById(R.id.phone_tv);
        habit_tv=findViewById(R.id.habit_tv);
        birthday_tv=findViewById(R.id.birthday_tv);
        my_note=findViewById(R.id.my_note);
        city_tv=findViewById(R.id.city_tv);
        head_img=findViewById(R.id.head_img);
        personInfo_return=findViewById(R.id.personInfo_return);
        relativeLayout=findViewById(R.id.my_note_layout);

        //初始化状态栏颜色
        setStatusBarColor=new SetStatusBarColor(getWindow());
        setStatusBarColor.setStatusBar(getResources().getColor(R.color.GhostWhite));

        //设置监听器
        personInfo_return.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);


        App= (PoiApplication) getApplication();
        userInfo=App.getCurrentUserInfo();
    }

    /**
     * 显示控件
     */
    public void setView(){
        user_name_tv.setText(userInfo.getUserName());
        email_tv.setText(userInfo.getEmail());
        phone_tv.setText(userInfo.getPhoneNumber());
        city_tv.setText(userInfo.getCity());
        ImageUtils.loadLocalPicNoOverride(this,userInfo.getHead_picture(),head_img);
        habit_tv.setText("");
        birthday_tv.setText("");
        my_note.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.personInfo_return:
                finish();
                break;
            case R.id.my_note_layout:
            {
                Intent intent = new Intent();
                intent.setClass(PersonalInfo.this,MyNoteActivity.class);
                startActivity(intent);
            }
                break;
        }
    }
}
