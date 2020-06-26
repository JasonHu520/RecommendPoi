package com.example.jasonhu.recommendpoi.adpter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.jasonhu.recommendpoi.DataBase.HistoryRecordDatabase;
import com.example.jasonhu.recommendpoi.R;

public class AutoInputCursorAdapter extends CursorAdapter {

    private SQLiteDatabase sqlite;
    private LayoutInflater layoutInflater;

    public AutoInputCursorAdapter(Context context, Cursor c, boolean autoqury,SQLiteDatabase sqLiteDatabase) {
        super(context, c,autoqury);
        this.sqlite=sqLiteDatabase;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return layoutInflater.inflate(
                R.layout.autoinput_text_item, parent, false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex("info")));
    }

    /*
     * 在结果中选中某个值后，显示在TextView中的值
     */
    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex("info"));
    }
    /*
     * 这个方法根据TextView输入的字符串，在数据库中进行匹配，从而获得cursor，这个cursor包含了数据信息
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if(constraint!=null)
        {
            String selection = "info like \'" + constraint.toString() +"%\'";
            return sqlite.query(HistoryRecordDatabase.TABLE_NAME, null, selection,
                    null, null, null, null);
        }
        else {
            return null;
        }
    }
}
