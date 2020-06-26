package com.example.jasonhu.recommendpoi.FunctionClass;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasonhu.recommendpoi.DataBase.HistoryRecordDatabase;
import com.example.jasonhu.recommendpoi.R;
import com.example.jasonhu.recommendpoi.bean.HistoryOrder;
import com.example.jasonhu.recommendpoi.adpter.RecyclerAdapter;
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JasonHu
 * 历史浏览搜索记录
 */
public class HistoryActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnHistory = null;
    TextView btn_delete_history = null;
    TextView tvHistory=null;
    HistoryRecordDatabase mHelper=null;
    SQLiteDatabase mDatabase=null;
    private List<HistoryOrder> data ;
    View view;


    private RecyclerView recyclerView;
    private RecyclerAdapter myAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SuperSwipeRefreshLayout swipeRefreshLayout;

    // Header View
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;

    // Footer View
    private ProgressBar footerProgressBar;
    private TextView footerTextView;
    private ImageView footerImageView;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
        // init SuperSwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        int color = getResources().getColor(R.color.aquamarine);
        swipeRefreshLayout.setHeaderViewBackgroundColor(color);
        swipeRefreshLayout.setHeaderView(createHeaderView());// add headerView
        swipeRefreshLayout.setFooterView(createFooterView());
        swipeRefreshLayout.setTargetScrollWithLayout(true);
        swipeRefreshLayout
                .setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {

                    @Override
                    public void onRefresh() {
                        textView.setText("正在刷新");
                        imageView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                            }
                        }, 2000);
                    }

                    @Override
                    public void onPullDistance(int distance) {
                        // pull distance
                    }

                    @Override
                    public void onPullEnable(boolean enable) {
                        textView.setText(enable ? "松开刷新" : "下拉刷新");
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setRotation(enable ? 180 : 0);
                    }
                });
    }
    public void init(){
        data = new ArrayList<HistoryOrder>();
        btnHistory = findViewById(R.id.delete_all);
        view = LayoutInflater.from(this).inflate(R.layout.history_item, null);
        btn_delete_history=view.findViewById(R.id.history_delete);
        //lvHistory=findViewById(R.id.lv_history);
        tvHistory=findViewById(R.id.tv_history);


        /** init recyclerView */
        recyclerView = findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        mHelper = new HistoryRecordDatabase(this);
        mDatabase = mHelper.getWritableDatabase();

        queryData();
        myAdapter = new RecyclerAdapter(this,data);
        myAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(HistoryActivity.this,"你点了一下"+position,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(int position) {
                Toast.makeText(HistoryActivity.this,"你长按了一下"+position,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                mDatabase.delete(HistoryRecordDatabase.TABLE_NAME,
                        HistoryRecordDatabase.TV_CONTENT +"=?",new String[]{data.get(position).getTv_content()});
                myAdapter.remove(position);
            }
        });
        recyclerView.setAdapter(myAdapter);

        btnHistory.setOnClickListener(this);
        btn_delete_history.setOnClickListener(this);
    }

    private View createFooterView() {
        View footerView = LayoutInflater.from(swipeRefreshLayout.getContext())
                .inflate(R.layout.layout_footer, null);
        footerProgressBar = (ProgressBar) footerView
                .findViewById(R.id.footer_pb_view);
        footerImageView = (ImageView) footerView
                .findViewById(R.id.footer_image_view);
        footerTextView = (TextView) footerView
                .findViewById(R.id.footer_text_view);
        footerProgressBar.setVisibility(View.GONE);
        footerImageView.setVisibility(View.VISIBLE);
        footerImageView.setImageResource(R.drawable.down_arrow);
        footerTextView.setText("上拉加载更多...");
        return footerView;
    }

    private View createHeaderView() {
        View headerView = LayoutInflater.from(swipeRefreshLayout.getContext())
                .inflate(R.layout.layout_head, null);
        progressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        textView = (TextView) headerView.findViewById(R.id.text_view);
        textView.setText("下拉刷新");
        imageView = (ImageView) headerView.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.down_arrow);
        progressBar.setVisibility(View.GONE);
        return headerView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void queryData() {
        Cursor cursor = mDatabase.query(HistoryRecordDatabase.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);// 注意空格！
        if (cursor != null)
        {
            cursor.moveToFirst();
            while (cursor.moveToNext())
            {
                String content,time;
                content=cursor.getString(cursor.getColumnIndex(HistoryRecordDatabase.TV_CONTENT));
                time=cursor.getString(cursor.getColumnIndex(HistoryRecordDatabase.TV_TIME));
                HistoryOrder order=new HistoryOrder(content,time);
                data.add(order);
            }
        }
        if (cursor != null && !cursor.isClosed())
            cursor.close();
    }

    @Override
    protected void onDestroy() {
        if(mDatabase!=null)
            mDatabase.close();
        if(!data.isEmpty())
            data.clear();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete_all:
            {
                data.clear();
                recyclerView.setAdapter(myAdapter);
                tvHistory.setVisibility(View.VISIBLE);
            }
            break;

        }
    }
}
