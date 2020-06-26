package com.example.jasonhu.recommendpoi.BaseClass;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.BaseClass.Callback.HttpCallbackListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                if (listener != null) {
                    listener.onFinish(response.toString());
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    /**
     *  @param url 服务器地址
     * @param data 传递数据
     * @param handler 消息通知
     * @param context 用于通知用户上传成功
     */
    public static void putData2Server(String url, final String data, android.os.Handler handler, Context context){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("head_pic", data);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
                in_thread_note("服务器错误",context);

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                //TODO
                if (res.equals("8"))
                {
                    in_thread_note("上传失败",context);
                    handler.sendEmptyMessage(3);
                }
                else
                {
                    in_thread_note("上传成功",context);
                    handler.sendEmptyMessage(1);

                }

            }
        });
    }
    private static void in_thread_note(String str, Context context){
        Looper.prepare();
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }


}
