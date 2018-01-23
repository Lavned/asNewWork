package com.expect.zbjpulltorefresht;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.expect.zbjpulltorefresht.R;
import com.expect.zbjpulltorefresht.utils.MyAdapter;
import com.expect.zbjpulltorefresht.xlayoutview.XRecyclerView;

import java.util.ArrayList;

public class StaggeredGridActivity extends Activity implements XRecyclerView.LoadingListener{
    private XRecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    //测试数据
    private ArrayList<String> listData;
    private int refreshTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staggered_grid);
        initView();
        initData();
    }


    //初始化控件
    private void initView(){
        mRecyclerView= (XRecyclerView) findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(layoutManager);

        //添加头部
        View header =   LayoutInflater.from(this).inflate(R.layout.recyclerview_header, (ViewGroup)findViewById(android.R.id.content),false);
        mRecyclerView.addHeaderView(header);

        mRecyclerView.setLoadingListener(this);
    }
    //绑定事件控件
    private void initData(){
        listData = new  ArrayList<String>();
        for(int i = 0; i < 19 ;i++){
            listData.add("item" + listData.size() );
        }
        mAdapter = new MyAdapter(listData);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        refreshTime ++;
        new Handler().postDelayed(new Runnable(){
            public void run() {

                listData.clear();
                for(int i = 0; i < 19 ;i++){
                    listData.add("item" + i + "after " + refreshTime + " times of refresh");
                }
                mAdapter.notifyDataSetChanged();
                mRecyclerView.refreshComplete();
            }

        }, 1000);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            public void run() {

                for (int i = 0; i < 19; i++) {

                    listData.add("item" + listData.size());
                }
                mAdapter.notifyDataSetChanged();
                mRecyclerView.loadMoreComplete();
            }
        }, 1000);
    }
}
