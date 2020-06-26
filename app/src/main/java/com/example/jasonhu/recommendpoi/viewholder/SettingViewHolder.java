package com.example.jasonhu.recommendpoi.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jasonhu.recommendpoi.R;

public class SettingViewHolder extends RecyclerView.ViewHolder {

    TextView tv_setting;

    public SettingViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_setting=itemView.findViewById(R.id.tv_setting);
    }
    //获取实例
    public static SettingViewHolder get(Context context, ViewGroup parent, int layoutId) {
        View itemView= LayoutInflater.from(context).inflate(layoutId,parent,false);
        return new SettingViewHolder(itemView);
    }
    public void bindView(String content) {
        tv_setting.setText(content);
    }
}
