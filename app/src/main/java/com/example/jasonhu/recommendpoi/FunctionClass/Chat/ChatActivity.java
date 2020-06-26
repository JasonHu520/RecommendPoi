package com.example.jasonhu.recommendpoi.FunctionClass.Chat;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.BaseClass.MessageBeanForChat.Message;
import com.example.jasonhu.recommendpoi.BaseClass.http.OkHttpUtil;
import com.example.jasonhu.recommendpoi.DataBase.UserInfoDatabaseHelper;
import com.example.jasonhu.recommendpoi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChatActivity extends AppCompatActivity{

    private ChatService.NotifyBinder chatbinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            chatbinder = (ChatService.NotifyBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    ListView chat_list;
    chatAdapter adapter;
    List<chat_content> historyMessages;
    private final int UPDATE_TEXT = 1;
    private SQLiteDatabase db;
    private UserInfoDatabaseHelper dbHelper;
    MessageBoxManager messageBox;
    TextView friend_name;
    String from_user = null;
    String to_user = null;
    ActionBar actionBar;
    Handler handler;
    EditText msg ;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_ui);

        Intent intent = getIntent();
        from_user = intent.getStringExtra("name");
        to_user = intent.getStringExtra("to_user");
        setActionBar(to_user);
        adapter = new chatAdapter(ChatActivity.this, R.layout.item, historyMessages);
        chat_list = findViewById(R.id.chatList);
        chat_list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chat_list.setStackFromBottom(true);
        msg = findViewById(R.id.sendMsg);
        msg.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        msg.setSingleLine(false);
        msg.setHorizontallyScrolling(false);
        /*
         * 初始化历史聊天记录
         */
        dbHelper = new UserInfoDatabaseHelper(this,UserInfoDatabaseHelper.DB_NAME_LOG,null,1);
        db = dbHelper.getWritableDatabase();
        messageBox = new MessageBoxManager(db,from_user,to_user);
        historyMessages = messageBox.getMessages();
        adapter = new chatAdapter(ChatActivity.this, R.layout.item, historyMessages);
        chat_list.setAdapter(adapter);
        chat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println(position);
            }
        });
        chat_list.setOnItemLongClickListener((parent, view, position, id) -> {

            String mg = historyMessages.get(position).getContent();
            historyMessages.remove(position);
            adapter.notifyDataSetChanged();
            db.delete("messageHistory","message=? and position=?",new String[]{mg,Integer.toString(position)});
            updatePosition(position);
            return false;
        });
        /**
         * 刷新聊天信息
         */
//        swipeRefreshLayout = findViewById(R.id.chat_layout_refresh);
//        swipeRefreshLayout.setOnRefreshListener(this); // 设置刷新监听
//        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.gray,R.color.azure); // 进度动画颜色

        //异步处理收到的消息
        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case UPDATE_TEXT:
//                        String message = msg.obj.toString();
//                        messageBox.insertMeg(new chat_content(true, message));
//                        historyMessages.add(new chat_content(true, message));
                        Date curDate =  new Date(System.currentTimeMillis());
                        String currentPosition = Integer.toString(historyMessages.size());
                        messageBox.insertMeg(new chat_content(true,to_user, "你最帅",from_user,curDate.toString(),currentPosition));
                        historyMessages.add(new chat_content(true,to_user, "你最帅",from_user,curDate.toString(),currentPosition));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ChatActivity.this,"it is ok",Toast.LENGTH_SHORT).show();
                }
            }
        };

        Button sendMsgButton = findViewById(R.id.send);
        sendMsgButton.setOnClickListener(view -> {
            try {
                String mesg = msg.getText().toString();
                Date curDate =  new Date(System.currentTimeMillis());
                Message  message = new Message(from_user,mesg, to_user,curDate.toString());
                putMsgToServer(message,"http://27y9317r51.wicp.vip/?method=get_message_from_");
                String currentPosition = Integer.toString(historyMessages.size());
                messageBox.insertMeg(new chat_content(false,from_user, mesg,to_user,curDate.toString(),currentPosition));
                historyMessages.add(new chat_content(false,from_user, mesg,to_user,curDate.toString(),currentPosition));
                adapter.notifyDataSetChanged();
                msg.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void setActionBar(String name){
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(
                R.layout.chat_actionbar_view, null);
        actionBar.setCustomView(actionbarLayout);
        friend_name = actionbarLayout.findViewById(R.id.friend_name_chat);
        friend_name.setText(name);
        ImageButton imageButton = actionbarLayout.findViewById(R.id.return_chat);
        imageButton.setOnClickListener(v -> finish());
    }

    private void  putMsgToServer(com.example.jasonhu.recommendpoi.BaseClass.MessageBeanForChat.Message msg, String url){
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("Msg", msg.getChat_message());
        formBuilder.add("current_user", msg.getCurrent_user());
        formBuilder.add("to_user", msg.getTo_user());
        formBuilder.add("current_date", msg.getCurrent_date());
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = OkHttpUtil.getOkHttpClient().newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Log.e("服务器错误",e.toString());
                in_thread_note("服务器错误");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                if (res.equals("2"))
                {
                    in_thread_note("无此账号,请先注册");
                }
                else if(res.equals("ok"))
                {
                    handler.sendEmptyMessage(UPDATE_TEXT);
                }
            }
        });
    }
    private void in_thread_note(String str){
        Looper.prepare();
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    protected void onDestroy() {
//        unbindService(connection);
        Log.i("msg","disconnect");
        super.onDestroy();
    }
    private void updatePosition(int position){
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor = db.query("messageHistory",
                null,
                null,
                null,
                null,
                null,
                null);// 注意空格！
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex("position")))>position){
                    arrayList.add(cursor.getString(cursor.getColumnIndex("position")));
                }
            }
        }
        if (cursor != null && !cursor.isClosed())
            cursor.close();
        for(int i=0;i<arrayList.size();i++){
            ContentValues values = new ContentValues();
            values.put("position",Integer.toString(Integer.parseInt(arrayList.get(i))-1));
            db.update("messageHistory",values,"position=?",new String[]{arrayList.get(i)});
        }
    }

}
