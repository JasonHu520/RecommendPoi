package com.example.jasonhu.recommendpoi.FunctionClass.Chat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiatom on 2019/4/24.
 */

public class MessageBoxManager {
    String fromUser ,toUser;
    SQLiteDatabase db;
    public MessageBoxManager(SQLiteDatabase db,String fromUser,String toUser){
        this.db = db;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }
    public List<chat_content> getMessages(){
        List<chat_content> messages = new ArrayList<>();
        Cursor cursor = db.query("messageHistory",null,null,null,null,null,null);
        if(cursor!= null){
            if(cursor.moveToFirst())
                do{
                    String message = cursor.getString(cursor.getColumnIndex("message"));
                    String fromUser = cursor.getString(cursor.getColumnIndex("fromUser"));
                    String toUser = cursor.getString(cursor.getColumnIndex("toUser"));
                    String currentTime = cursor.getString(cursor.getColumnIndex("currentTime"));
                    int fromOther = cursor.getInt(cursor.getColumnIndex("fromOther"));
                    String position = cursor.getString(cursor.getColumnIndex("position"));
                    chat_content m = new chat_content(fromOther != -1,fromUser,message,toUser,currentTime,position);
                    if(fromUser.equals(this.fromUser) && toUser.equals(this.toUser)||(fromUser.equals(this.toUser) && toUser.equals(this.fromUser)))
                        messages.add(m);
                }while(cursor.moveToNext());
            cursor.close();
        }
        return messages;
    }

    public void insertMeg(chat_content m){
        ContentValues values = new ContentValues();
        values.put("fromUser",m.getFromUser());
        values.put("message",m.getContent());
        values.put("toUser",m.getToUser());
        values.put("currentTime",m.getCurrentTime());
        values.put("fromOther",m.isFromOthor()?1:-1);
        values.put("position",m.getPosition());
        db.insert("messageHistory",null,values);
        Log.i("db","insert suc");
    }
}
