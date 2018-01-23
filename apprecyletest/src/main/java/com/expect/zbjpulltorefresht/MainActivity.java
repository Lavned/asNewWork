package com.expect.zbjpulltorefresht;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.expect.zbjpulltorefresht.utils.MyAdapter;
import com.expect.zbjpulltorefresht.xlayoutview.XRecyclerView;

import java.util.ArrayList;

public class MainActivity extends Activity implements XRecyclerView.LoadingListener{
//    private PullToRefreshLayout mLayout;


    private XRecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    //测试数据
    private ArrayList<String> listData;
    private int refreshTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }
    //初始化控件
    private void initView(){

//        mLayout= (PullToRefreshLayout) findViewById(R.id.refresh_view);
//        mLayout.setOnRefreshListener(this);
//        mLayout.setPullRefreshEnable(false);
//        mLayout.setPullLoadEnable(false);

        mRecyclerView= (XRecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);


        //添加头部
        View header =   LayoutInflater.from(this).inflate(R.layout.recyclerview_header, (ViewGroup)findViewById(android.R.id.content),false);
        mRecyclerView.addHeaderView(header);

//        mRecyclerView.setPullRefreshEnabled(false);
//        mRecyclerView.setLoadingMoreEnabled(false);

        mRecyclerView.setLoadingListener(this);
    }
    //绑定事件控件
    private void initData(){
        listData = new  ArrayList<String>();
        for(int i = 0; i < 6 ;i++){
            listData.add("item" + listData.size() );
        }
        mAdapter = new MyAdapter(listData);
        mRecyclerView.setAdapter(mAdapter);

        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,GridActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,StaggeredGridActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        refreshTime ++;
        new Handler().postDelayed(new Runnable(){
            public void run() {

                listData.clear();
                for(int i = 0; i < 6 ;i++){
                    listData.add("item" + i + "after " + refreshTime + " times of refresh");
                }
                mAdapter.notifyDataSetChanged();
                mRecyclerView.refreshComplete();
            }

        }, 1000);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable(){
            public void run() {

                for(int i = 0; i <6 ;i++){

                    listData.add("item" + listData.size() );
                }
                mAdapter.notifyDataSetChanged();
                mRecyclerView.loadMoreComplete();
            }
        }, 1000);
    }
}
