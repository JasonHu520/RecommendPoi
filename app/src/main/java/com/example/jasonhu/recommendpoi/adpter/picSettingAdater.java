package com.example.jasonhu.recommendpoi.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jasonhu.recommendpoi.BaseClass.Callback.OnMyItemClickListener;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.viewholder.PicSettingViewHolder;

import java.util.List;

/**
 * created by JasonHu 2019.12.9
 */
public class picSettingAdater extends RecyclerView.Adapter<PicSettingViewHolder> {

    private OnMyItemClickListener mOnMyItemClickListener;//列表点击监听器
    private Context mContext;
    private List<String> mDataSet;

    public picSettingAdater(Context context, List<String> orderList){
        mContext=context;
        mDataSet=orderList;
    }

    @NonNull
    @Override
    public PicSettingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.pic_setting_list_item, viewGroup, false);
        return new PicSettingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PicSettingViewHolder picSettingViewHolder, int i) {
        if(mOnMyItemClickListener !=null){
            picSettingViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnMyItemClickListener.onClick(picSettingViewHolder.getAdapterPosition());
                }
            });
        }
        picSettingViewHolder.bindView(mDataSet.get(i));

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
    public void setOnItemClickListener(OnMyItemClickListener onMyItemClickListener) {
        this.mOnMyItemClickListener = onMyItemClickListener;
    }
}
