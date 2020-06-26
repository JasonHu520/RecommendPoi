package com.example.jasonhu.recommendpoi.BaseClass.http;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by  on 2017/6/27.
 */
public class OkHttpUtil {

    private static OkHttpClient client =null;

    public static OkHttpClient getOkHttpClient() {
        if (client==null){
            synchronized (OkHttpClient.class){
                if(client==null)
                    client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
                            .readTimeout(10000, TimeUnit.MILLISECONDS)
                            .writeTimeout(10000, TimeUnit.MILLISECONDS).build();
            }
        }
        return client;
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

    public void downloadFile(String url, final ProgressListener listener, Callback callback,String userName,String picName,String picQuality){

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("userName", userName);
        formBuilder.add("pic_name", picName);
        formBuilder.add("pic_quality", picQuality);
        OkHttpClient client = getOkHttpClient().newBuilder().addNetworkInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponseBody(response.body(),listener)).build();
            }
        }).build();
        Request request  = new Request.Builder().url(url).post(formBuilder.build()).build();
        client.newCall(request).enqueue(callback);
    }

    public void postFile(String url, final ProgressListener listener, Callback callback, File file,String userName){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userName", userName)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();
        Request request  = new Request.Builder().url(url).post(new ProgressRequestBody(requestBody,listener)).build();
        getOkHttpClient().newCall(request).enqueue(callback);
    }



}
