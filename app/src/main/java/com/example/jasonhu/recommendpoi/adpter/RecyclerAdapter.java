package com.example.jasonhu.recommendpoi.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.bean.HistoryOrder;
import com.example.jasonhu.recommendpoi.viewholder.HistoryViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class RecyclerAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

	private Context mContext;
	private List<HistoryOrder> mDataSet;
	private OnItemClickListener mOnItemClickListener;//列表点击监听器

	public RecyclerAdapter(Context context, List<HistoryOrder> orderList) {
		mContext = context;
		Collections.reverse(orderList);
		mDataSet = orderList;

	}

	/**
	 * 建立ViewHolder
	 * @param parent
	 * @param viewType
	 * @return
	 */
	@NonNull
	@Override
	public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.history_item, parent, false);
		return new HistoryViewHolder(mContext,view,parent);
	}

	@Override
	public void onBindViewHolder(final HistoryViewHolder holder, int position) {
		if(mOnItemClickListener!=null) {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnItemClickListener.onClick(holder.getAdapterPosition());
				}
			});
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					mOnItemClickListener.onLongClick(holder.getAdapterPosition());
					return true;
				}
			});
			holder.tv_delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnItemClickListener.onDeleteClick(holder.getAdapterPosition());
				}
			});
		}
		//convert(holder,mDataSet.get(position).tv_content,mDataSet.get(position).tv_time);
		holder.bindView(mDataSet.get(position).getTv_content(),mDataSet.get(position).getTv_time(), position);
	}


	@Override
	public int getItemCount() {
		return mDataSet.size();
	}


	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	public void add(HistoryOrder historyOrder, int position) {
		mDataSet.add(position, historyOrder);
		notifyItemInserted(position);
	}
	public void remove(int position){
		mDataSet.remove(position);
		notifyDataSetChanged();
	}

	public interface OnItemClickListener{
		void onClick(int position);
		void onLongClick(int position);
		void onDeleteClick(int position);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener= onItemClickListener;
	}


}
