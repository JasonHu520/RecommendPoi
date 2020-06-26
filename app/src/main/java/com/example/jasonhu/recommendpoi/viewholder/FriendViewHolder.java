package com.example.jasonhu.recommendpoi.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jasonhu.recommendpoi.BaseClass.picture_util.ImageUtils;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.bean.FriendOder;

import java.io.File;

public class FriendViewHolder extends RecyclerView.ViewHolder {
    private TextView tv_friend_name,tv_friend_message,tv_friend_time;
    ImageView head_pic_view;
    private Context mcontext;

    public FriendViewHolder(@NonNull View itemView,Context context) {
        super(itemView);
        tv_friend_name=itemView.findViewById(R.id.tv_friend_name);
        tv_friend_message=itemView.findViewById(R.id.tv_friend_message);
        head_pic_view=itemView.findViewById(R.id.headPic_for_friend);
        tv_friend_time=itemView.findViewById(R.id.tv_friend_time);
        mcontext =context;
    }
    //获取实例
    public static FriendViewHolder get(Context context, ViewGroup parent, int layoutId) {
        View itemView= LayoutInflater.from(context).inflate(layoutId,parent,false);
        return new FriendViewHolder(itemView,context);
    }
    public void bindView(FriendOder oder) {
        tv_friend_message.setText(oder.getMessage());
        tv_friend_name.setText(oder.getName());
        tv_friend_time.setText(oder.getTime());
        File file = new File(oder.getHead_pic());
        if(file.exists())
            ImageUtils.loadLocalPicNoOverride(mcontext,oder.getHead_pic(),head_pic_view);
    }
}
