package com.example.jasonhu.recommendpoi.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jasonhu.recommendpoi.BaseClass.picture.LocalPicBean;
import com.example.jasonhu.recommendpoi.BaseClass.picture_util.ImageUtils;
import com.example.jasonhu.recommendpoi.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class HistorPicAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<LocalPicBean> historyPicBeans;

    public HistorPicAdapter(Context context,ArrayList<LocalPicBean> historyPicBeans){
        this.context=context;
        this.historyPicBeans=historyPicBeans;
    }
    @Override
    public int getCount() {
        if (historyPicBeans == null)
        {
            return 0;
        }
        return historyPicBeans.size();
    }

    public void setData(ArrayList<LocalPicBean> localPicBeans) {
        this.historyPicBeans = localPicBeans;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_look_img,container,false);
        PhotoView photoView=view.findViewById(R.id.iv_img);
        photoView.setZoomable(true);
        ImageUtils.loadLocalPicNoOverride(context,historyPicBeans.get(position).getImgPath(),photoView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
