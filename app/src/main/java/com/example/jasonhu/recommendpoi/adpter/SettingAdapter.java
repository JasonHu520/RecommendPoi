package com.example.jasonhu.recommendpoi.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jasonhu.recommendpoi.BaseClass.Callback.OnMyItemClickListener;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.viewholder.SettingViewHolder;

import java.util.List;

public class SettingAdapter  extends RecyclerView.Adapter<SettingViewHolder> {

    private Context mContext;
    private List<String> mDataSet;
    private OnMyItemClickListener mOnMyItemClickListener;//列表点击监听器

    public SettingAdapter(Context context, List<String> orderList){
        mContext=context;
        mDataSet=orderList;
    }

    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_setting_item, parent, false);
        return new SettingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SettingViewHolder baseViewHolder, int position) {
        if(mOnMyItemClickListener !=null){
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnMyItemClickListener.onClick(baseViewHolder.getAdapterPosition());
                }
            });
        }
        baseViewHolder.bindView(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return  mDataSet.size();
    }



    public void setOnItemClickListener(OnMyItemClickListener onMyItemClickListener) {
        this.mOnMyItemClickListener = onMyItemClickListener;
    }
}
