package com.jy.project.jyshop.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jy.project.jyshop.R;
import com.jy.project.jyshop.adapter.FooterAdpater;
import com.jy.project.jyshop.view.AdvanceDecoration;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class RecyleActivity extends Activity {


    @ViewInject(R.id.id_recyclerview)
    RecyclerView recyclerView;

    @ViewInject(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;


    FooterAdpater mAdapter ;
    List<String> mDatas;
    public int lastVisibleItem;

    public  LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyle);
        x.view().inject(this);//注解绑定
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new FooterAdpater(RecyleActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //添加分隔线
        recyclerView.addItemDecoration(new AdvanceDecoration(this, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<String> newDatas = new ArrayList<String>();
                        for (int i = 0; i <5; i++) {
                            int index = i + 1;
                            newDatas.add("new item" + index);
                        }
                        mAdapter.addItem(newDatas);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(RecyleActivity.this, "更新了五条数据...", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);
            }
        });

        //RecyclerView滑动监听
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                    mAdapter.changeMoreStatus(FooterAdpater.LOADING_MORE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<String> newDatas = new ArrayList<String>();
                            for (int i = 0; i < 5; i++) {
                                int index = i + 1;
                                newDatas.add("more item" + index);
                            }
                            mAdapter.addMoreItem(newDatas);
                            mAdapter.changeMoreStatus(FooterAdpater.PULLUP_LOAD_MORE);
                        }
                    }, 2500);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }



    private static final int TYPE_ITEM =0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView

    class RefreshRecyclerAdapter extends RecyclerView.Adapter<RefreshRecyclerAdapter.ViewHolder>{
        private LayoutInflater mInflater;
        private List<String> mTitles=null;
        public RefreshRecyclerAdapter(Context context){
            this.mInflater=LayoutInflater.from(context);
            this.mTitles=new ArrayList<String>();
            for (int i=0;i<20;i++){
                int index=i+1;
                mTitles.add("item"+index);
            }
        }
        /**
         * item显示类型
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view=mInflater.inflate(R.layout.recycleitems,parent,false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            ViewHolder viewHolder=new ViewHolder(view);

            return viewHolder;
        }

        /**
         * 数据的绑定显示
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.item_tv.setText(mTitles.get(position));
            holder.itemView.setTag(position);
        }
        @Override
        public int getItemCount() {
            return mTitles.size();
        }

        public int getItemViewType(int position) {
            // 最后一个item设置为footerView
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }


        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public  class ViewHolder extends RecyclerView.ViewHolder {
            public TextView item_tv;
            public ViewHolder(View view){
                super(view);
                item_tv = view.findViewById(R.id.textView5);
            }
        }

        //添加数据
        public void addItem(List<String> newDatas) {
            //mTitles.add(position, data);
            //notifyItemInserted(position);
            newDatas.addAll(mTitles);
            mTitles.removeAll(mTitles);
            mTitles.addAll(newDatas);
            notifyDataSetChanged();
        }

        public void addMoreItem(List<String> newDatas) {
            mTitles.addAll(newDatas);
            notifyDataSetChanged();
        }
    }




}