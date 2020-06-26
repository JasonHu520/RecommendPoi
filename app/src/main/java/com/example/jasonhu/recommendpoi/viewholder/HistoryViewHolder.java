package com.example.jasonhu.recommendpoi.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jasonhu.recommendpoi.R;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class HistoryViewHolder extends RecyclerView.ViewHolder {

	private SparseArray<View> mViews;//存储list_Item的View
	private View mConvertView;//list_Item
	public TextView tv_content;
	public TextView tv_time;
	public TextView tv_delete;
	public HistoryViewHolder(Context context, View itemView, ViewGroup parent) {
		super(itemView);
		mConvertView=itemView;
		tv_content = (TextView) itemView.findViewById(R.id.tv_list_history);
		tv_time = itemView.findViewById(R.id.tv_history_time);
		tv_delete=itemView.findViewById(R.id.history_delete);
		mViews= new SparseArray<>();
	}

	//获取实例
	public static HistoryViewHolder get(Context context, ViewGroup parent, int layoutId) {
		View itemView= LayoutInflater.from(context).inflate(layoutId,parent,false);
		return new HistoryViewHolder(context,itemView,parent);
	}
	public void bindView(String content,String time, int position) {
		tv_content.setText(content);
		tv_time.setText(time);
		tv_delete.setText("X");
	}
	public <T extends View> T getView(int viewId) {
		View view=mViews.get(viewId);
		if(view==null) {
			view=mConvertView.findViewById(viewId);
			mViews.put(viewId,view);
		}
		return (T)view;
	}


}
