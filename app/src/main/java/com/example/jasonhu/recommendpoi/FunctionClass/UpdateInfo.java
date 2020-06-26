package com.example.jasonhu.recommendpoi.FunctionClass;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.BaseClass.Callback.OnMyItemClickListener;
import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.bean.UserInfo;
import com.example.jasonhu.recommendpoi.BaseClass.util.CommonUtil;
import com.example.jasonhu.recommendpoi.DataBase.UserInfoDatabaseHelper;
import com.example.jasonhu.recommendpoi.PoiApplication;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.adpter.SettingAdapter;
import com.example.jasonhu.recommendpoi.ui.MyNoteActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonHu 2018.12.9
 * 修改个人信息类
 */
public class UpdateInfo extends Activity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SettingAdapter myAdapter;
    ImageView change_userInfo_return;
    List<String> dataList;
    PoiApplication App;
    UserInfo userInfo;
    ContentValues values;
    SQLiteDatabase mDatabase;
    UserInfoDatabaseHelper sqLiteOpenHelper;
    ArrayList<String> getinfolist;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_info_layout);
        init();
    }

    /**
     * 初始化
     */
    private  void init(){
        App=(PoiApplication)getApplication();
        userInfo=App.getCurrentUserInfo();
        recyclerView = findViewById(R.id.update_re_view);
        change_userInfo_return=findViewById(R.id.change_userInfo_return);
        linearLayoutManager = new LinearLayoutManager(UpdateInfo.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        sqLiteOpenHelper = new UserInfoDatabaseHelper(UpdateInfo.this,UserInfoDatabaseHelper.DB_NAME_LOG,null,1);
        mDatabase = sqLiteOpenHelper.getWritableDatabase();
        values=new ContentValues();
        change_userInfo_return.setOnClickListener(this);


        dataList =new ArrayList<>();
        getinfolist=new ArrayList<>();
        getinfolist.add(userInfo.getSecret());
        getinfolist.add(userInfo.getCity());
        getinfolist.add(userInfo.getEmail());
        getinfolist.add(userInfo.getPhoneNumber());

        dataList.add("修改头像");
        dataList.add("修改密码");
        dataList.add("修改城市");
        dataList.add("更换绑定邮箱");
        dataList.add("更换绑定电话");
        dataList.add("添加日记");
        dataList.add("更改生日");

        myAdapter=new SettingAdapter(UpdateInfo.this,dataList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(new OnMyItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position){
                    case 0://修改头像
                        {
                        Intent intent = new Intent(UpdateInfo.this,ChoosePictureforHead.class);
                        startActivity(intent);
                    }break;
                    case 1://修改密码
                    {
                        showCustomDialog(position);
                    }break;
                    case 2://修改城市
                    {
                        showCustomDialog(position);
                    }break;
                    case 3://更改绑定邮箱
                    {
                        showCustomDialog(position);
                    }break;
                    case 4://更改绑定电话
                    {
                        showCustomDialog(position);
                    }break;
                    case 5://添加日记
                    {
                        Intent intent = new Intent();
                        intent.setClass(UpdateInfo.this,MyNoteActivity.class);
                        startActivity(intent);
                    }break;
                    case 6://更改生日
                    {

                    }break;

                }
            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }
    private void  showCustomDialog(final int position){
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        final View layout = (LinearLayout) getLayoutInflater().inflate(R.layout.updateinfo_dialog_layout, null);
        final EditText et_update_info;
        dialog.setView(layout);
        TextView dialog_title;
        Button btn_ok,btn_cancel;

        et_update_info=layout.findViewById(R.id.et_update_info);
        dialog_title=layout.findViewById(R.id.dialog_title);
        btn_cancel=layout.findViewById(R.id.dialog_btn_cancel);
        btn_ok=layout.findViewById(R.id.dialog_btn_ok);

        dialog_title.setText(dataList.get(position));
        et_update_info.setText(getinfolist.get(position-1));

        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String string=et_update_info.getText().toString();
                String type =null;
                switch(position-1){
                    case 0:
                        userInfo.setSecret(string);
                        getinfolist.set(position-1,string);//list.set(int index,String id) 用指定的值替换某个位置的元素
                        values.put("password",string);
                        type = "password";
                        note_tost("你更改了密码");
                        break;
                    case 1:
                        userInfo.setCity(string);
                        getinfolist.set(position-1,string);
                        values.put("City",string);
                        type = "City";
                        note_tost("你更改了城市");
                        break;
                    case 2:
                        userInfo.setEmail(string);
                        values.put("email",string);
                        type = "email";
                        getinfolist.set(position-1,string);
                        note_tost("你更改了邮箱");
                        break;
                    case 3:
                        userInfo.setPhoneNumber(string);
                        if(CommonUtil.isPhone(string)){
                            values.put("phoneNumber",string);
                            note_tost("你更改了电话");
                            type = "phoneNumber";
                            getinfolist.set(position-1,string);

                            }
                        else{
                            note_tost("电话号码格式不正确，重新输入");
                        }
                        break;

                }
                App.setCurrentUserInfo(userInfo);
                if(type!=null){
                    Handler handler =App.getHandler();
                    mDatabase.update("LogInfo",values,"userName=?",new String[]{userInfo.getUserName()});
                    Message message=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString(type,getinfolist.get(position-1));
                    bundle.putString("data",type);
                    message.setData(bundle);
                    message.what = Constant.UPDATE_USERINFO_TO_SERVER;
                    handler.sendMessage(message);
                    values.clear();
                }
                dialog.cancel();

            }
        });
        btn_cancel.setOnClickListener(v -> dialog.cancel());

        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            default:
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.change_userInfo_return:
            {
                finish();
            }
            break;
        }

    }
    private void note_tost(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
}
