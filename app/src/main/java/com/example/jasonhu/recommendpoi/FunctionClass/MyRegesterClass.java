package com.example.jasonhu.recommendpoi.FunctionClass;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.BaseClass.http.OkHttpUtil;
import com.example.jasonhu.recommendpoi.BaseClass.util.CommonUtil;
import com.example.jasonhu.recommendpoi.DataBase.UserInfoDatabaseHelper;
import com.example.jasonhu.recommendpoi.MainActivity;
import com.example.jasonhu.recommendpoi.R;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MyRegesterClass extends Activity implements View.OnClickListener{
    EditText mEt_user;
    EditText mEt_Secret;
    EditText mEt_city;
    EditText mEt_phonenumber;
    EditText mEt_identify;
    EditText mEt_email;
    Button mBtn_sendCode;
    Button mBtn_regist;
    TextView mTv_check;
    SQLiteDatabase mDatabase;
    UserInfoDatabaseHelper sqLiteOpenHelper;

    MainActivity mainActivity;
    Handler mLocalHandler;
    private TimeCount timer;
    ContentValues values;
    String checktpye = "手机";//default
    String code;
    String Register = "123";



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();
    }
    @SuppressLint("HandlerLeak")
    private  void initView(){
        mBtn_regist=findViewById(R.id.btn_register);
        mBtn_sendCode=findViewById(R.id.btn_sendCode);
        mEt_city=findViewById(R.id.et_city);
        mEt_identify=findViewById(R.id.et_identify);
        mEt_phonenumber=findViewById(R.id.et_phone);
        mEt_user=findViewById(R.id.et_user_name);
        mEt_Secret=findViewById(R.id.et_secforlog);
        mEt_email=findViewById(R.id.et_email);
        mTv_check=findViewById(R.id.reg_check_tv);
        sqLiteOpenHelper = new UserInfoDatabaseHelper(MyRegesterClass.this,UserInfoDatabaseHelper.DB_NAME_LOG,null,1);
        mDatabase = sqLiteOpenHelper.getWritableDatabase();
        mainActivity=new MainActivity();
        values= new ContentValues();

        timer=new TimeCount(60000,1000);//设置获取验证码的倒计时
        mLocalHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
               switch (msg.what){
                   case 1:
                   {
                       changeTv("验证正确",Color.GREEN);
                       mDatabase.insert("LogInfo",null,values);
                       String url2 = "http://27y9317r51.wicp.vip/?method=register";
                       updataToServer(url2,values);
                       mBtn_regist.setText("请等待···");
                       mBtn_regist.setClickable(false);

                   }
                       break;
                   case 2: {
                       changeTv("验证失败", Color.RED);
                       Toast.makeText(MyRegesterClass.this,"注册失败",Toast.LENGTH_SHORT).show();
                   }
                       break;
                   case 3:
                   {
                       Intent intent=new Intent(MyRegesterClass.this,LoginActivity.class);
                       startActivity(intent);
                       mBtn_regist.setText("注册");
                       mBtn_regist.setClickable(true);
                       finish();
                   }
                       break;
                   case 4:
                   {
                       Toast.makeText(MyRegesterClass.this,"注册成功,返回登录",Toast.LENGTH_SHORT).show();
                       mLocalHandler.sendEmptyMessage(3);
                   }
                   break;
               }
            }
        };
        mBtn_sendCode.setOnClickListener(this);
        mBtn_regist.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

      switch (v.getId()){
          case R.id.btn_register:
          {
              if(mEt_user.getText()!=null&&queryData(mEt_user.getText().toString())){
                  values.put("userName",mEt_user.getText().toString());
                  if(mEt_Secret.getText()!=null){
                      values.put("password",mEt_Secret.getText().toString());
                      if(mEt_city.getText()!=null){
                          values.put("City",mEt_city.getText().toString());
                          if(mEt_email.getText()!=null){
                              values.put("email",mEt_email.getText().toString());
                              if(mEt_phonenumber.getText()!=null){
                                  values.put("phoneNumber",mEt_phonenumber.getText().toString().trim());
                                  if(mEt_identify!=null){
                                      values.put("LogState","N");
                                      if (checktpye.equals("手机")){
                                             submitCode("86",mEt_phonenumber.getText().toString(),
                                                      mEt_identify.getText().toString().trim());
                                      }else{
                                          check_email_code(mEt_identify.getText().toString(),code);
                                      }

                                  }

                              }

                          }
                      }
                  }

              }
              else{
                  Toast.makeText(MyRegesterClass.this,"该用户名已被使用",Toast.LENGTH_SHORT).show();
              }
          }
          break;
          case R.id.btn_sendCode:
          {
              if (mEt_phonenumber.getText()!=null && mEt_email.getText()!=null){
                  singleDialog();
              }

          }
          break;
      }
    }
    // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
    public void sendCode(String country, String phone) {
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

    private void check_email_code(final String got_code,final String result){
        if(result.equals(got_code)){
            mLocalHandler.sendEmptyMessage(1);
        }
        else{
            // TODO 处理错误的结果
            mLocalHandler.sendEmptyMessage(2);
        }
    }

    // 提交验证码，其中的code表示验证码，如“1357”

    public void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果

        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    mLocalHandler.sendEmptyMessage(1);

                } else{
                    // TODO 处理错误的结果
                    mLocalHandler.sendEmptyMessage(2);
                }
            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);

    }
    private void changeTv(String messaage,int color){
        mTv_check.setText(messaage);
        mTv_check.setTextColor(color);
    }

    /**
     * 定义倒计时类
     */
    public class TimeCount extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mBtn_sendCode.setClickable(false);
            mBtn_sendCode.setBackgroundColor(Color.GRAY);
            mBtn_sendCode.setText(millisUntilFinished / 1000 + "秒后重新获取");

        }

        @TargetApi(Build.VERSION_CODES.O)
        @Override
        public void onFinish() {
            mBtn_sendCode.setText("点击重新获取");
            mBtn_sendCode.setBackgroundColor(getResources().getColor(R.color.my_blue));
            mBtn_sendCode.setClickable(true);
        }
    }

    /**
     * 查询用户名是否被使用
     * @param userName
     * @return
     */
    public boolean queryData(String userName) {
        boolean is = true;
        Cursor cursor = mDatabase.query("LogInfo",
                null,
                null,
                null,
                null,
                null,
                null);// 注意空格！
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    String user = cursor.getString(cursor.getColumnIndex("userName"));
                    if(user.equals(userName))
                    {
                        is=false;
                        break;
                    }
                }
            }
        }
        if (cursor != null && !cursor.isClosed())
            cursor.close();
        return is;
    }
    /**
     * 单选对话框
     */
    private void singleDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("验证方式,默认手机验证");
        final String[] items = { "手机", "邮箱" };// 创建一个存放选项的数组
        final boolean[] checkedItems = { true, false };// 存放选中状态，true为选中
        // ，false为未选中，和setSingleChoiceItems中第二个参数对应
        // 为对话框添加单选列表项
        // 第一个参数存放选项的数组，第二个参数存放默认被选中的项，第三个参数点击事件
        builder.setSingleChoiceItems(items, 0, (arg0, arg1) -> {
            // TODO Auto-generated method stub
            for (int i = 0; i < checkedItems.length; i++) {
                checkedItems[i] = false;
            }
            checkedItems[arg1] = true;
        });
        builder.setNegativeButton("取消", (arg0, arg1) -> {
            // TODO Auto-generated method stub

            if(CommonUtil.isPhone(mEt_phonenumber.getText().toString().trim())){
                sendCode("86",mEt_phonenumber.getText().toString().trim());
                timer.start();
                Toast.makeText(MyRegesterClass.this,"已发送",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MyRegesterClass.this,"号码不正确",Toast.LENGTH_SHORT).show();
            }

            arg0.dismiss();
        });
        builder.setPositiveButton("确定", (arg0, arg1) -> {
            // TODO Auto-generated method stub
            String str ;
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    str = items[i];
                    checktpye = str;
                    if (checktpye.equals("手机")){

                        if(CommonUtil.isPhone(mEt_phonenumber.getText().toString().trim())){
                            sendCode("86",mEt_phonenumber.getText().toString().trim());
                            timer.start();
                            Toast.makeText(MyRegesterClass.this,"已发送",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MyRegesterClass.this,"号码不正确",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        String url4 = "http://27y9317r51.wicp.vip/?method=email_captcha";
                        getEmailFromServer(url4,mEt_email.getText().toString());
                        System.out.print(code);
                        timer.start();
                    }
                }
            }
        });
        builder.create().show();

    }
    public void  getEmailFromServer(String url,final String email_address){

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("email", email_address);
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = OkHttpUtil.getOkHttpClient().newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                note("服务器错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                if (res.equals("7"))
                {
                    note("请重新输入邮箱");
                }
                else if(res.equals("6"))
                {
                    note("发送失败");
                }
                else//成功
                {
                    code = res;
                    note("发送成功，注意查收");
                }

            }
        });
    }



    private void note(final String str){
        Thread  thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(MyRegesterClass.this,str,Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
        thread.setDaemon(true);
        thread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
    public void updataToServer(String url,ContentValues values){
        FormBody.Builder formBuilder = new FormBody.Builder().addEncoded("encode","utf-8");
        formBuilder.add("userName", values.get("userName").toString());
        formBuilder.add("password", values.get("password").toString());
        formBuilder.add("City", values.get("City").toString());
        formBuilder.add("email", values.get("email").toString());
        formBuilder.add("phoneNumber", values.get("phoneNumber").toString());
        formBuilder.add("LogState", values.get("LogState").toString());
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = OkHttpUtil.getOkHttpClient().newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
//                runOnUiThread(() -> showWarnSweetDialog("服务器错误"));
                note("服务器错误");
                Register = "123";

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                switch (res){
                    case "4":
                    {
                        note("用户名已注册");
                    }
                    break;
                    case "5":{
                        mLocalHandler.sendEmptyMessage(4);
                    }
                }

            }
        });
    }
}

