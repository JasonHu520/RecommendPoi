package com.example.jasonhu.recommendpoi.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jasonhu.recommendpoi.BaseClass.picture.LocalPicBean;
import com.example.jasonhu.recommendpoi.BaseClass.picture_util.ImageUtils;
import com.example.jasonhu.recommendpoi.R;

import java.util.ArrayList;
import java.util.List;

public class PicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<LocalPicBean> localPicBeans;
    private int selectImgNumber;
    private OnItemChildClickListener mOnItemChildClickListener;
    private OnItemClickListener onItemClickListener;

   public PicAdapter(Context context, ArrayList<LocalPicBean> localPicBeans,int selectImgNumber){
       this.context=context;
       this.localPicBeans=localPicBeans;
       this.selectImgNumber=selectImgNumber;
   }

    public ArrayList<LocalPicBean> getLocalPicBeans() {
        return localPicBeans;
    }

    public void setData(ArrayList<LocalPicBean> localPicBeans){
       this.localPicBeans=localPicBeans;
       notifyDataSetChanged();
   }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ThisViewHolder){
             ImageUtils.loadLocalSmallPic(context,localPicBeans.get(position).getImgPath(),((ThisViewHolder) holder).iv_img);
             if (localPicBeans.get(position).isSelected()){
                 ((ThisViewHolder) holder).tv_select.setBackgroundResource(R.drawable.bg_selected);
                 ((ThisViewHolder) holder).tv_select.setText(localPicBeans.get(position).getSelectedSign()+"");
             }else {
                 ((ThisViewHolder) holder).tv_select.setBackgroundResource(R.drawable.bg_select);
                 ((ThisViewHolder) holder).tv_select.setText("");
             }
        }
    }

    @Override
    public int getItemCount() {
        return localPicBeans.size();
    }

    private class ThisViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_img;
        private TextView tv_select;
        private ThisViewHolder(View itemView) {
            super(itemView);
            iv_img=itemView.findViewById(R.id.iv_img);
            tv_select=itemView.findViewById(R.id.tv_select);
        }
    }



    public interface OnItemChildClickListener {
        void onItemChildClick(int position);
    }
    public interface OnItemClickListener {
        void onItemClick(int position,int selectImgNumber);
    }
    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.mOnItemChildClickListener = onItemChildClickListener;
    }
    public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
       this.onItemClickListener = onItemClickListener;
    }

    private RecyclerView.ViewHolder createHolder(ViewGroup parent){
        final ThisViewHolder holder=new ThisViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pic, parent, false));
        holder.iv_img.setOnClickListener(v -> {
            if (onItemClickListener!=null){
                onItemClickListener.onItemClick(holder.getAdapterPosition(),selectImgNumber);
            }

        });
        holder.tv_select.setOnClickListener(v -> {

            if (mOnItemChildClickListener!=null){
                mOnItemChildClickListener.onItemChildClick(holder.getAdapterPosition());
            }

        });

        return holder;
    }

}
