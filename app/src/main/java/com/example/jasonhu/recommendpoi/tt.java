package com.example.jasonhu.recommendpoi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class tt extends AppCompatActivity {

    Button button = null;
    TextView textView = null;
    EditText editText = null;
    Socket socket;
    BufferedWriter pw=null;
    BufferedReader is=null;
    String string="baba";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);




        button = (Button) findViewById(R.id.test_button);
        textView = (TextView) findViewById(R.id.test_tv);
        editText = (EditText) findViewById(R.id.test_et);

        handler.sendEmptyMessageDelayed(1, 100);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run()
                    {
                        String msg = editText.getText().toString();
                        try{
                            pw.write(msg);
                            pw.flush();
                        } catch (UnknownHostException e) {
                            Toast.makeText(tt.this,"失败1",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (IOException e) {
                            Toast.makeText(tt.this,"失败2",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }.start();
                editText.setText("");
            }
        });

    }
    private Handler handler = new Handler() {

        public void handleMessage(Message message) {
            try {
                Moving();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    };
    private void Moving() throws IOException {
        new Thread() {
            @Override
            public void run() {
                try {
                    if(is!= null){
                    if (is.ready())
                        string = is.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        byte[] b=string.getBytes();
        String s1=new String(b);
        System.out.println(s1);
        textView.setText(string);
        handler.sendEmptyMessageDelayed(1, 100);
    }

    @Override
    protected void onResume() {
        new Thread() {
            @Override
            public void run() {
                try{
                    socket = new Socket("113.250.155.239", 8000);
                    socket.setSoTimeout(10000);
                    pw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        super.onResume();
    }
}
