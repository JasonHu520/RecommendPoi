package com.example.jasonhu.recommendpoi;

import android.app.Application;

import com.example.jasonhu.recommendpoi.bean.UserInfo;

/**
 * @author Jason
 * Created by 2018.11.29
 * Application 用于数据共享，定义全局变量，可以让应用中的Activity和View都能访问
 */
public class PoiApplication extends Application {

    public static PoiApplication app;
    private android.os.Handler handler=null;
    private UserInfo currentUserInfo;
    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
    }
    public static Application getApp(){
        return app;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    public android.os.Handler getHandler() {
        return handler;
    }

    public UserInfo getCurrentUserInfo() {
        return currentUserInfo;
    }

    public void setCurrentUserInfo(UserInfo currentUserInfo) {
        this.currentUserInfo = currentUserInfo;
    }

    public void setHandler(android.os.Handler handler) {
        this.handler=handler;
    }
}
