package com.example.jasonhu.recommendpoi.BaseClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jasonhu.recommendpoi.bean.Constant;

import java.util.Objects;

public class NetWorkChangeReceiver extends BroadcastReceiver {

    android.os.Handler handler;

    public NetWorkChangeReceiver(){

    }

    public NetWorkChangeReceiver(android.os.Handler handler){
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
        if(Objects.requireNonNull(intent.getAction()).equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)){
            handler.sendEmptyMessage(Constant.NET_WORK_CHANGE);
        }
    }
}

