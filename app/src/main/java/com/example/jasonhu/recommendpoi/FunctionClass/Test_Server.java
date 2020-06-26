package com.example.jasonhu.recommendpoi.FunctionClass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Test_Server extends AppCompatActivity implements View.OnClickListener{
    public TextView textView;
    Handler handler=new Handler();
    private SharedPreferences sharedPreferences;
    EditText userNameEdit,passWordEdit,email_dress,code;
    Button logButton,SignButton,get_code_button,test_button;
    private void init(){
        userNameEdit = findViewById(R.id.t_account_edit);
        passWordEdit = findViewById(R.id.t_password_edit);
        logButton = findViewById(R.id.t_btn_login);
        SignButton = findViewById(R.id.t_btn_exit);
        get_code_button = findViewById(R.id.t_get_code);
        textView = findViewById(R.id.t_tv_register);
        email_dress = findViewById(R.id.t_email);
        code = findViewById(R.id.t_email_code);
        test_button = findViewById(R.id.button_test_camera);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_server);
        init();
        logButton.setOnClickListener(this);
        SignButton.setOnClickListener(this);
        textView.setOnClickListener(this);
        get_code_button.setOnClickListener(this);
        test_button.setOnClickListener(this);
    }

    private void getEmailFromServer(String url,final String email_address){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("email", email_address);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
                note("服务器错误");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                if (res.equals("0"))
                {
                    note("请重新输入邮箱");
                }
                else if(res.equals("1"))
                {
                    note("发送失败");
                }
                else//成功
                {
//                        showSuccessSweetDialog(res);

                    note("发送成功，注意查收"+res);
//                    sharedPreferences = getSharedPreferences("UserIDAndPassword", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("username", userName);
//                    editor.apply();
                }

            }
        });
    }

    /**
     * 将用户名和密码发送到服务器进行比对，若成功则跳转到app主界面，若错误则刷新UI提示错误登录信息
     * @param url 服务器地址
     * @param userName 用户名
     * @param passWord 密码
     */
    private void getCheckFromServer(String url,final String userName,String passWord)
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", userName);
        formBuilder.add("password", passWord);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
                note("服务器错误");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                    if (res.equals("0"))
                    {
                        note("无此账号,请先注册");
                    }
                    else if(res.equals("1"))
                    {
                        note("密码不正确");
                    }
                    else//成功
                    {
//                        showSuccessSweetDialog(res);
                        note("登录成功");
                        sharedPreferences = getSharedPreferences("UserIDAndPassword", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", userName);
                        editor.apply();
                    }

            }
        });

    }


    /**
     * 将用户名与密码发送给服务器进行注册活动
     * @param url 服务器地址
     * @param userName 用户名
     * @param passWord 密码
     */
    private void registeNameWordToServer(String url,final String userName,String passWord)
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", userName);
        formBuilder.add("password", passWord);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(@NonNull Call call, IOException e)
            {
               note("服务器错误");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException
            {
                final String res = response.body().string();
                    if (res.equals("0"))
                    {
                        note("该用户名已被注册");
                    }
                    else
                    {
                        note(res);
                        sharedPreferences = getSharedPreferences("UserIDAndPassword", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", userName);
                        editor.apply();
                    }
            }
        });

    }
    private void note(String str ){
        Looper.prepare();
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    /**
     * 测试主机
     * @param url1
     */
    private void test(String url1){
        Thread thread = new Thread(() -> {
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(url1);// 根据自己的服务器地址填写
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setDoOutput(true);// 允许输出
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "utf-8");
                OutputStream os = conn.getOutputStream();
                os.write("name=allen".getBytes());
                if (conn.getResponseCode() == 200) {
                    System.out.println(conn.toString());
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                    bufferedReader = new BufferedReader(isr);
                }
                String result = "";
                String line = "";
                if (bufferedReader != null) {
                    try {
                        while ((line = bufferedReader.readLine()) != null) {
                            result += line;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(result);
            } catch (MalformedURLException e) {
                // URL格式错误
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // 不支持你设置的编码
                e.printStackTrace();
            } catch (ProtocolException e) {
                // 请求方式不支持
                e.printStackTrace();
            } catch (IOException e) {
                // 输入输出通讯出错
                e.printStackTrace();
            }
        });
        thread.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:  // 请求码
                parseUri(data);
                break;
            default:
        }
    }
    public String parseUri(Intent data) {
        Uri uri=data.getData();
        String imagePath;
        // 第二个参数是想要获取的数据
        Cursor cursor = getContentResolver()
                .query(uri, new String[]{MediaStore.Images.ImageColumns.DATA},
                        null, null, null);
        if (cursor == null) {
            imagePath = uri.getPath();
        } else {
            cursor.moveToFirst();
            // 获取数据所在的列下标
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            imagePath = cursor.getString(index);  // 获取指定列的数据
            cursor.close();
        }
        return imagePath;  // 返回图片地址
    }


    @Override
    public void onClick(View v) {
        String userName = userNameEdit.getText().toString();
        String passWord = passWordEdit.getText().toString();
        String emailAddress = email_dress.getText().toString();
//        if(userName.equals("")||passWord.equals(""))
//        {
//            note("账号密码不能为空");
//            return;
//        }
        switch (v.getId())
        {
            case R.id.t_btn_login:
                String url = "http://testserver.nat123.cc/user";/*在此处改变你的服务器地址*/
                getCheckFromServer(url,userName,passWord);
                break;
            case R.id.t_btn_exit:
                String url2 = "http://testserver.nat123.cc/register";/*在此处改变你的服务器地址*/
                registeNameWordToServer(url2,userName,passWord);
                break;
            case R.id.t_tv_register:
                String ur3 = "http://testserver.nat123.cc";
                test(ur3);
                break;
            case R.id.t_get_code:
                String url4 = "http://testserver.nat123.cc/email_captcha";
                getEmailFromServer(url4,emailAddress);
                break;
            case R.id.button_test_camera:
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);  // 第二个参数是请求码
            }
        }
    }

}



