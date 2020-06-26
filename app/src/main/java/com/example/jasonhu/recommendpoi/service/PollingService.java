package com.example.jasonhu.recommendpoi.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.BaseClass.http.OkHttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Created by JasonHu 2020/5/25 20:41
 * @version 1.0
 */

@SuppressLint("Registered")
public class PollingService extends IntentService {
    public static final String ACTION_CHECK_CIRCLE_UPDATE="轮询";
    public static final long DEFAULT_MIN_POLLING_INTERVAL = 1000;//最短轮询间隔1分钟
    public PollingService() {
        super("PollingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
            return;
        final String action = intent.getAction();
        if (ACTION_CHECK_CIRCLE_UPDATE.equals(action)) {
            tellServer();//这个是访问服务器获取朋友圈是否更新
        }
    }
    private void tellServer() {
        Request request = new Request.Builder().url("http://27y9317r51.wicp.vip/?method=test").get().build();
        Call call = OkHttpUtil.getOkHttpClient().newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                in_thread_note("服务器错误,请稍后再试");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                if (res.equals("ok"))
                {

                }else{
                    in_thread_note("连接失败");
                }

            }
        });
    }
    private void in_thread_note(String str){
        Looper.prepare();
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

}
