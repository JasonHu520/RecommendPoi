package com.example.jasonhu.recommendpoi.FunctionClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.bean.UserInfo;
import com.example.jasonhu.recommendpoi.BaseClass.http.OkHttpUtil;
import com.example.jasonhu.recommendpoi.BaseClass.picture_util.ImageUtils;
import com.example.jasonhu.recommendpoi.BaseClass.util.PackageUtils;
import com.example.jasonhu.recommendpoi.DataBase.UserInfoDatabaseHelper;
import com.example.jasonhu.recommendpoi.PoiApplication;
import com.example.jasonhu.recommendpoi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends Activity implements View.OnClickListener {
    ImageView mImgReturn;
    Button btn_login;
    Button btn_exit;
    TextView tv_register;
    TextView tv_check_account;
    EditText account_edit;
    EditText password_edit;
    SQLiteDatabase mDatabase;
    UserInfoDatabaseHelper sqLiteOpenHelper;
    Handler mGlobHandler;
    Handler mLocalHandler;
    PoiApplication App;
    UserInfo userInfo;
    JSONObject jsonObject;
    ContentValues values;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        init();
    }
    @SuppressLint("HandlerLeak")
    private void init(){
        sqLiteOpenHelper = new UserInfoDatabaseHelper(LoginActivity.this,UserInfoDatabaseHelper.DB_NAME_LOG,null,1);
        mDatabase = sqLiteOpenHelper.getWritableDatabase();
        mImgReturn=findViewById(R.id.log_return);
        btn_exit=findViewById(R.id.btn_exit);
        btn_login=findViewById(R.id.btn_login);
        tv_register=findViewById(R.id.tv_register);
        account_edit=findViewById(R.id.account_edit);
        password_edit=findViewById(R.id.password_edit);
        tv_check_account=findViewById(R.id.tv_check_account);
        btn_login.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        mImgReturn.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        App=(PoiApplication) getApplication();
        mGlobHandler =App.getHandler();
        userInfo=new UserInfo();
        values = new ContentValues();

        mLocalHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        mGlobHandler.sendEmptyMessage(Constant.LOG_OK);
                        note("登录成功");
                        btn_login.setText("登录");
                        btn_login.setClickable(true);
                        finish();
                    }
                    break;
                    case 2:{
                        note("数据来了");

                        String userName = jsonObject.optString("userName");
                        String password = jsonObject.optString("password");
                        String email = jsonObject.optString("email");
                        String City = jsonObject.optString("city");
                        String phoneNumber = jsonObject.optString("phoneNumber");
                        String LogState = jsonObject.optString("logState");
                        String image_head = jsonObject.optString("image_head");
                        String pic_Name = ImageUtils.parse_picName(image_head);
                        String image_addr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+
                                File.separator+PackageUtils.getAppName(LoginActivity.this) +File.separator;

                        //保留此次登录信息，下次启动使用
                        final SharedPreferences.Editor editor = getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
                        editor.putString("userName", userName);
                        editor.apply();

                        //把账户信息记录在本地
                        values.put("userName",userName);
                        values.put("password",password);
                        values.put("City",City);
                        values.put("email",email);
                        values.put("phoneNumber",phoneNumber);
                        values.put("LogState",LogState);
                        values.put("head_image_address",image_addr+pic_Name);
                        mDatabase.insert("LogInfo",null,values);

                        //更新当前用户信息
                        userInfo.setUserName(userName);
                        userInfo.setSecret(password);
                        userInfo.setPhoneNumber(phoneNumber);
                        userInfo.setLogstate("Yes");
                        userInfo.setEmail(email);
                        userInfo.setCity(City);
                        userInfo.setHead_picture(image_addr+pic_Name);
                        App.setCurrentUserInfo(userInfo);

                        mGlobHandler.sendEmptyMessage(Constant.LOG_OK);
//                        note("登录成功");
//                        btn_login.setText("登录");
//                        btn_login.setClickable(true);
                        finish();
                    }
                    break;
                    case 3:{
                        tv_check_account.setText("该账户没有注册，请先注册");
                    }
                    break;
                    case 4:{
                        note("解析数据错误");
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.log_return:
            {
                finish();
            }
            break;
            case R.id.btn_login:
            {
                if(queryData(account_edit.getText().toString(),password_edit.getText().toString())) {
                    tv_check_account.setText("");
                    String url = "http://27y9317r51.wicp.vip/?method=login";
                    getCheckFromServer(url,account_edit.getText().toString(),password_edit.getText().toString());
                    btn_login.setText("正在登录···");
                    btn_login.setClickable(false);

               }else{
                    String server_url = "http://27y9317r51.wicp.vip/?method=get_user_info";
                    getDataFromServer(server_url,account_edit.getText().toString(),password_edit.getText().toString());
                }

            }
            break;
            case R.id.btn_exit:
            {
                finish();
            }
            break;
            case R.id.tv_register:
            {
                Intent intent=new Intent(LoginActivity.this,MyRegesterClass.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * 查询本地数据
     * @param userName
     * @param pass
     * @return
     */
    public boolean queryData(String userName,String pass) {
        boolean is = false;
        Cursor cursor = mDatabase.query("LogInfo",
                null,
                null,
                null,
                null,
                null,
                null);// 注意空格！
        if (cursor != null) {
            int count=cursor.getCount();
            if(count>0) {
                while (cursor.moveToNext()) {

                    String user = cursor.getString(cursor.getColumnIndex("userName"));
                    String password=cursor.getString(cursor.getColumnIndex("password"));
                    String phone=cursor.getString(cursor.getColumnIndex("phoneNumber"));
                    String city=cursor.getString(cursor.getColumnIndex("City"));
                    String email=cursor.getString(cursor.getColumnIndex("email"));

                    if (userName.equals(user) || userName.equals(phone)) {
                        if (pass.equals(password)) {

                            //保留此次登录信息，下次启动使用
                            final SharedPreferences.Editor editor = getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
                            editor.putString("userName", user);
                            editor.apply();

                            //更新当前用户信息
                            userInfo.setUserName(user);
                            userInfo.setSecret(password);
                            userInfo.setPhoneNumber(phone);
                            userInfo.setLogstate("Yes");
                            userInfo.setEmail(email);
                            userInfo.setCity(city);
                            App.setCurrentUserInfo(userInfo);

                            is = true;
                            break;
                        }
                        else{
                            account_edit.setText("");
                            password_edit.setText("");
                            tv_check_account.setText("*账户或密码错误请从新输入");
                            tv_check_account.setTextColor(Color.RED);
                        }
                    }

                }
//                tv_check_account.setText("该账户没有注册，请先注册");
            }else{
//            tv_check_account.setText("该账户没有注册，请先注册");
            }
        }

        if (cursor != null && !cursor.isClosed())
            cursor.close();
        return is;
    }
    /**
     * 检查登录
     * @param url
     * @param userName
     * @param passWord
     */
    public void getCheckFromServer(String url,final String userName,String passWord)
    {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("userName", userName);
        formBuilder.add("password", passWord);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = OkHttpUtil.getOkHttpClient().newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
                Log.e("服务器错误",e.toString());
                in_thread_note("服务器错误,请稍后再试");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {

                final String res = response.body().string();
                if (res.equals("2"))
                {
                    in_thread_note("无此账号,请先注册");
                }
                else if(res.equals("3"))
                {
                    in_thread_note("密码不正确");
                }
                else if(res.equals("1"))
                {
//                        showSuccessSweetDialog(res);
                    mLocalHandler.sendEmptyMessage(1);

                }

            }
        });

    }

    /**
     * 获取用户信息
     * @param url 地址
     * @param userName 用户名
     * @param password 密码
     */
    public void getDataFromServer(String url,final String userName,final String password){
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("userName", userName);
        formBuilder.add("password", password);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = OkHttpUtil.getOkHttpClient().newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
                in_thread_note("服务器错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                if (res.equals("8"))
                {
                    in_thread_note("无此账号,请先注册11");
                    mLocalHandler.sendEmptyMessage(3);
                }
                else
                {
                    try {
                        jsonObject = new JSONObject(res);
                        mLocalHandler.sendEmptyMessage(2);
                    } catch (JSONException e) {
                        mLocalHandler.sendEmptyMessage(4);
                    }

                }
            }
        });
    }
    private void in_thread_note(String str){
        Looper.prepare();
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
    private void note(String string){
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
    }
}
