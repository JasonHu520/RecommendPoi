package com.example.jasonhu.recommendpoi.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jasonhu.recommendpoi.R;

public class PicSettingViewHolder extends RecyclerView.ViewHolder {

    TextView textView;
    public PicSettingViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.item_text_for_list_picSetting);
    }
    //获取实例
    public static PicSettingViewHolder get(Context context, ViewGroup parent, int layoutId) {
        View itemView= LayoutInflater.from(context).inflate(layoutId,parent,false);
        return new PicSettingViewHolder(itemView);
    }
    public void bindView(String content) {
        textView.setText(content);
    }
}
