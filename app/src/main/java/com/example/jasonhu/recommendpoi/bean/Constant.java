package com.example.jasonhu.recommendpoi.bean;

public class Constant {
    //Btn的标识
    public static final int BTN_FLAG_MESSAGE = 0x01;
    public static final int BTN_FLAG_RECOMMEND = 0x01 << 1;
    public static final int BTN_FLAG_Me = 0x01 << 2;
    public static final int BTN_FLAG_SETTING = 0x01 << 3;

    //Fragment的标识
    public static final String FRAGMENT_FLAG_MESSAGE = "消息";
    public static final String FRAGMENT_FLAG_RECOMMEND = "推荐";
    public static final String FRAGMENT_FLAG_Me = "我";
    public static final String FRAGMENT_FLAG_SETTING = "设置";

    //handler
    public static final int LOCATION_OK =1;
    public static final int LOCATION_DATA_CHANGED=2;
    public static final int LOG_OK=3;
    public static final int LOG_OUT=4;
    public static final int HEAD_PIC_OK=5;
    public static final int UPDATE_USERINFO_TO_SERVER=9;



    //网络检测
    public static final int NET_WORK_OK=6;
    public static final int NET_WORK_ERRO=7;
    public static final int NET_WORK_CHANGE=8;


}
