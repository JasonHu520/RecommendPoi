package com.example.jasonhu.recommendpoi;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;


public class TestActivity extends Activity {
    BottomNavigationBar mBottomNavigationBar;
    TextBadgeItem mTextBadgeItem;
    ShapeBadgeItem mShapeBadgeItem;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_bottom_activity);
        mBottomNavigationBar= (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        mTextBadgeItem = new TextBadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(R.color.colorAccent)
                .setAnimationDuration(200)
                .setText("3")
                .setHideOnSelect(false);
        mShapeBadgeItem = new ShapeBadgeItem()
                .setShapeColorResource(R.color.colorPrimary)
                .setGravity(Gravity.TOP | Gravity.END)
                .setHideOnSelect(false);


    }
    private void init(){
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        mBottomNavigationBar //值得一提，模式跟背景的设置都要在添加tab前面，不然不会有效果。
                .setActiveColor(R.color.green);//选中颜色 图标和文字
//                .setInActiveColor("#8e8e8e")//默认未选择颜色
//                .setBarBackgroundColor(R.color.white);//默认背景色

        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.message_unselected,"消息")
                        .setBadgeItem(mShapeBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.contacts_unselected,"推荐"))

                .addItem(new BottomNavigationItem(R.drawable.setting_unselected,"学管通")
                        .setBadgeItem(mTextBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.news_unselected,"我的"))
                .setFirstSelectedPosition(0)//设置默认选择的按钮
                .initialise();//所有的设置需在调用该方法前完成

    }
}
