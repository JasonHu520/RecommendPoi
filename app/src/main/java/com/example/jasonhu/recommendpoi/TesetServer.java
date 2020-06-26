package com.example.jasonhu.recommendpoi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jasonhu.recommendpoi.Server.Client;

public class TesetServer extends AppCompatActivity implements View.OnClickListener {
    Button btn_send;
    EditText et_content;
    Client client;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_server);
        init();

    }
    private void init(){
        btn_send = findViewById(R.id.t_get_code);
        et_content = findViewById(R.id.t_email);
        btn_send.setOnClickListener(this);
        startService(new Intent(this,Client.class));
        client = new Client();
        client.run();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.t_get_code){
//            todo 发送数据给服务器
            if(!et_content.getText().toString().equals("")){
                client.sendMessagetoServer(et_content.getText().toString(),this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,Client.class));
        super.onDestroy();
    }
}
