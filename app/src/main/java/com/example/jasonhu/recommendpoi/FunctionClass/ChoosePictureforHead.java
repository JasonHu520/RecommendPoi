package com.example.jasonhu.recommendpoi.FunctionClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.BaseClass.Callback.OnMyItemClickListener;
import com.example.jasonhu.recommendpoi.bean.Constant;
import com.example.jasonhu.recommendpoi.bean.UserInfo;
import com.example.jasonhu.recommendpoi.BaseClass.http.OkHttpUtil;
import com.example.jasonhu.recommendpoi.BaseClass.http.ProgressListener;
import com.example.jasonhu.recommendpoi.BaseClass.picture.LocalPicBean;
import com.example.jasonhu.recommendpoi.BaseClass.picture_util.ImageUtils;
import com.example.jasonhu.recommendpoi.BaseClass.util.DateUtils;
import com.example.jasonhu.recommendpoi.BaseClass.util.PackageUtils;
import com.example.jasonhu.recommendpoi.DataBase.UserInfoDatabaseHelper;
import com.example.jasonhu.recommendpoi.PoiApplication;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.adpter.HistorPicAdapter;
import com.example.jasonhu.recommendpoi.adpter.picSettingAdater;
import com.example.jasonhu.recommendpoi.service.serviceUtils.PollingUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.jasonhu.recommendpoi.BaseClass.picture_util.ImageUtils.getPicFromBytes;

/**
 * created by JasonHu 2019.10.24
 */
public class ChoosePictureforHead extends Activity {

    private HistorPicAdapter historPicAdapter;
    private ArrayList<LocalPicBean> historyPicBean;
    private ImageView viewPager;
    String image_address,new_image_addr;
    ImageView imageView_choose_back;
    Handler mGlobHandler,local_handler;
    PoiApplication App;
    SQLiteDatabase mDatabase;
    UserInfoDatabaseHelper sqLiteOpenHelper;
    ContentValues values;
    UserInfo userInfo;
    Bitmap head_bitmap;
    File cameraSavePath;
    private ArrayList<String> pic_setting_list;
    private PopupWindow popupWindow;
    private View popupView;
    private RecyclerView rvPicSetting;
    picSettingAdater mpicSettingAdater;
    private boolean isShowPopupWindow = false;
    private byte[] data;
    private  String pic_Name;
    private Bitmap temp_bitmap=null;

//    ProgressBar

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_picture);
        init();
    }

    @SuppressLint("HandlerLeak")
    private void init(){
        pic_setting_list = new ArrayList<>();
        imageView_choose_back= findViewById(R.id.choose_pic_back);
        historPicAdapter = new HistorPicAdapter(this,historyPicBean);
        viewPager = findViewById(R.id.pic_head_viewPager);
        imageView_choose_back.setOnClickListener(v -> finish());
        App=(PoiApplication) getApplication();
        userInfo= App.getCurrentUserInfo();
        mGlobHandler =App.getHandler();
        sqLiteOpenHelper = new UserInfoDatabaseHelper(ChoosePictureforHead.this,UserInfoDatabaseHelper.DB_NAME_LOG,null,1);
        mDatabase = sqLiteOpenHelper.getWritableDatabase();
        values = new ContentValues();
        ImageUtils.loadLocalPicNoOverride_for_see(this,userInfo.getHead_picture(),viewPager);
        viewPager.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(temp_bitmap==null)
                    temp_bitmap = ImageUtils.getBitmap(userInfo.getHead_picture());
                if (temp_bitmap != null){
                    temp_bitmap = ImageUtils.adjustPhotoRotation(temp_bitmap,90);
                    viewPager.setImageBitmap(temp_bitmap);
                    viewPager.invalidate();
                }
                return false;
            }
        });
        viewPager.invalidate();

        /**
         * 数据上传进度对话框
         */
        final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        final View layout = getLayoutInflater().inflate(R.layout.updata_dialog_layout, null);

        local_handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 2:
                    {
                        //todo
                        Toast.makeText(ChoosePictureforHead.this,"上传完成",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                        break;
                    case 3:
                        Bitmap bitmap = getPicFromBytes(data,null);
                        if(bitmap!=null){
                            viewPager.setImageBitmap(bitmap);
                            viewPager.invalidate();
                        }
                        ImageUtils.saveBmp2Gallery(userInfo.getHead_picture(),bitmap,null,ChoosePictureforHead.this);
                        break;
                }
            }
        };

        /**
         * 图片获取方式
         */
        findViewById(R.id.head_ok).setVisibility(View.GONE);
        findViewById(R.id.choose_type).setOnClickListener(v -> {
            singleDialog();

        });
        /**
         * 确定头像
         */
        findViewById(R.id.head_ok).setOnClickListener(v -> {
            //TODO 设置头像
//            ImageUtils.loadLocalVerySmallPic(this, , iv_img);
            /**
             * 获取压缩后的图片
             */
            new_image_addr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+
                    File.separator+PackageUtils.getAppName(this) +File.separator;
            Date curDate =  new Date(System.currentTimeMillis());
            String regEx="[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";
            pic_Name = DateUtils.date2string(curDate).replaceAll(regEx,"-");
            ImageUtils.saveBmp2Gallery(new_image_addr,head_bitmap,pic_Name,this);
            /**
             * 给主界面通知更新头像消息
             */
            Message message=new Message();
            Bundle bundle=new Bundle();
            bundle.putString("image_address",new_image_addr+pic_Name+".jpg");
            message.setData(bundle);
            message.what = Constant.HEAD_PIC_OK;
            mGlobHandler.sendMessage(message);

            /**
             * 将图片信息更新在本地数据库
             */
            values.put("head_image_address",new_image_addr+pic_Name+".jpg");
            userInfo.setHead_picture(new_image_addr+pic_Name+".jpg");
            App.setCurrentUserInfo(userInfo);
            try {
                /**
                 * 防止数据库中没有备份头像数据
                 */
                mDatabase.update("LogInfo",values,"userName=?",new String[]{userInfo.getUserName()});
            }catch (Exception e){
                System.out.println(e.toString());
                mDatabase.delete("LogInfo","userName=?",new String[]{userInfo.getUserName()});
                values.put("userName",userInfo.getUserName());
                values.put("password",userInfo.getSecret());
                values.put("City",userInfo.getCity());
                values.put("email",userInfo.getEmail());
                values.put("phoneNumber",userInfo.getPhoneNumber());
                values.put("LogState",userInfo.getLogstate());
                mDatabase.insert("LogInfo",null,values);
            }
            /**
             * 把图片上传到服务器
             */
            PollingUtil.stopPollingServices();
            String url = "http://27y9317r51.wicp.vip/?method=getPicFromAndroid";
            File file = new File(new_image_addr+pic_Name+".jpg");
            Log.e("",file.getName()+file.length());
            putData2Server(url,local_handler,file,userInfo.getUserName(),dialog,layout);
        });
        setPopupWindowFun(dialog,layout);
        findViewById(R.id.tv_more).setOnClickListener(this::showPopUp);
    }
    private void showPopUp(View v) {
        Log.v("Lin", "显示Popup");
        if (isShowPopupWindow) {
            popupWindow.dismiss();
            return;
        }
        popupWindow.showAsDropDown(v);
        isShowPopupWindow = true;
    }

    private void setPopupWindowFun(android.support.v7.app.AlertDialog dialog, View layout){
        /**
         * 设置弹出框
         */
        popupView = LayoutInflater.from(this).inflate(R.layout.setting_for_pic_his, null);
        rvPicSetting = popupView.findViewById(R.id.list_for_setting_pic);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPicSetting.setLayoutManager(linearLayoutManager);

        pic_setting_list.add("历史记录");
        pic_setting_list.add("查看原图");
        pic_setting_list.add("取消");

        mpicSettingAdater = new picSettingAdater(this, pic_setting_list);
        rvPicSetting.setAdapter(mpicSettingAdater);
        mpicSettingAdater.setOnItemClickListener(new OnMyItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position){
                    case 0://历史记录
                        Toast.makeText(ChoosePictureforHead.this,"功能有待开发···",Toast.LENGTH_SHORT).show();

                        break;
                    case 1://查看原图
                        String url = "http://27y9317r51.wicp.vip/?method=putPictoAndroid";
                        int size = 0;
                        File file1 = new File(userInfo.getHead_picture());
                        if(!file1.exists()){
                            return;
                        }
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(file1);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            assert fis != null;
                            size = fis.available();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(size/1000>100)
                        {
                            Toast.makeText(ChoosePictureforHead.this,"当前已是原图，不用重新加载^V^",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String current_pic_name =  ImageUtils.parse_picName(userInfo.getHead_picture());
                            getWholePicFromServerwithListener(url,local_handler,userInfo.getUserName(),dialog,layout,current_pic_name,"原图");
                        }

                        break;
                    case 2://取消
                        popupWindow.dismiss();
                        break;
                }
            }
            @Override
            public void onLongClick(int position) {
            }
        });
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popmenu_animation);
        popupWindow.setOnDismissListener(() -> isShowPopupWindow = false);
    }
    private void getWholePicFromServerwithListener(String url, android.os.Handler handler, String userName,android.support.v7.app.AlertDialog dialog,
                                                   View layout, String picName,String picQuality){
        new OkHttpUtil().downloadFile(url, (currentBytes, contentLength, done) -> {
            int progress = (int) (currentBytes * 100 / contentLength);
            showCustomDialog(dialog,layout,progress);
            if (progress==100){
                new Handler().postDelayed(dialog::dismiss,500);
            }
        }, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                    in_thread_note("服务器错误");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                InputStream res = response.body().byteStream();
                int len ;
                byte[] bytes = new byte[1024];
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                while ((len = res.read(bytes)) != -1) {
                    outStream.write(bytes, 0, len);
                }
                data = outStream.toByteArray();
                outStream.close();
                res.close();
                handler.sendEmptyMessage(3);
            }
        },userName,picName,picQuality);

    }

    private void in_thread_note(String str){
        Looper.prepare();
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String photoPath;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<LocalPicBean> localPicBeans = data.getParcelableArrayListExtra("list");
                historPicAdapter.setData(localPicBeans);
            } else {
                Toast.makeText(this, "并未选择图片", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode ==1&& resultCode == RESULT_OK){
            findViewById(R.id.head_ok).setVisibility(View.VISIBLE);
            head_bitmap=ImageUtils.decodeUri(this,data.getData(),1024,1024);
            /**
             * 本地使用
             */
            image_address=parseUri(data);
            if (image_address != null){
                ImageUtils.loadLocalPicNoOverride(this,image_address , viewPager);
            }
            Toast.makeText(this,image_address,Toast.LENGTH_LONG).show();
            Log.e("图片地址",image_address);
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            findViewById(R.id.head_ok).setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoPath = String.valueOf(cameraSavePath);
            } else {
                photoPath = Objects.requireNonNull(data.getData()).getEncodedPath();
            }
            Log.d("拍照返回图片路径:", photoPath);

            ImageUtils.loadLocalPicNoOverride(this,photoPath , viewPager);
            Uri uri = FileProvider.getUriForFile(this, this.getPackageName()+".fileprovider", cameraSavePath);
            head_bitmap=ImageUtils.decodeUri(this,uri,1024,1024);
        }
    }
    public String parseUri(Intent data) {
        Uri uri=data.getData();
        String imagePath;
        if(uri!= null) {
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
        return null;
    }

    private void singleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("获取方式，默认相册");
        final String[] items = { "相册", "像机" };// 创建一个存放选项的数组
        final boolean[] checkedItems = { true, false };// 存放选中状态，true为选中
        builder.setSingleChoiceItems(items, 0, (arg0, arg1) -> {
            // TODO Auto-generated method stub
            for (int i = 0; i < checkedItems.length; i++) {
                checkedItems[i] = false;
            }
            checkedItems[arg1] = true;
        });
        builder.setNegativeButton("取消", (arg0, arg1) -> {
            // TODO Auto-generated method stub
            arg0.dismiss();
        });
        builder.setPositiveButton("确定", (arg0, arg1) -> {
            // TODO Auto-generated method stub

            String str ;
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    str = items[i];
                    if (str.equals("相册")){
//                        startActivityForResult((PicAty.getStartIntent(getApplicationContext(), 1)), 1001);
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent,1);  // 第二个参数是请求码
                    }else{
                        imageCapture();
                    }
                }
            }
        });
        builder.create().show();
    }
    /**
     * 上传图片到服务器
     * @param url 服务器地址
     * @param handler 发送消息出去
     * @param file
     * @param userName
     */
    public void putData2Server(String url, android.os.Handler handler, File file,String userName,android.support.v7.app.AlertDialog dialog,View layout){
        new OkHttpUtil().postFile(url, new ProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength, boolean done) {
                Log.i("", "currentBytes==" + currentBytes + "==contentLength==" + contentLength + "==done==" + done);
                int progress = (int) (currentBytes * 100 / contentLength);
                Log.i("", "result===" + progress);
                showCustomDialog(dialog,layout,progress);

            }
        }, new Callback(){
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String result = response.body().string();
                Log.i("", "result===" + result);
                handler.sendEmptyMessage(2);
            }
        }, file,userName);
    }
    private void  showCustomDialog(android.support.v7.app.AlertDialog dialog,View layout,int progress){


        dialog.setView(layout);
        TextView progress_number;
        ProgressBar progressBar ;

        progress_number=layout.findViewById(R.id.text_progress);
        progressBar=layout.findViewById(R.id.progressBar);

        progressBar.setProgress(progress);
        progress_number.setText(progress + "%");
        dialog.show();
    }
    private void imageCapture() {
        Uri uri;
        String picture_from_camara = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+
                File.separator+PackageUtils.getAppName(this) +File.separator;
        Date curDate =  new Date(System.currentTimeMillis());
        String regEx="[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";
        String pic_Name = DateUtils.date2string(curDate).replaceAll(regEx,"-");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraSavePath= new File(picture_from_camara+pic_Name+".jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //第二个参数为 包名.fileprovider
            String name  = this.getPackageName();
            uri = FileProvider.getUriForFile(this, this.getPackageName()+".fileprovider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 2);
    }

}
