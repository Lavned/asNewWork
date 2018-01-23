package com.shows.getmoney.hanzicode;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;


import com.shows.view.BaseActivity;
import com.yixinke.shows.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodePassActivity extends BaseActivity {
    /**
     * 验证答案框
     */
    private AnswerLayout mAnswerLayout;
    /**
     * 验证文字框
     */
    private GridView mGridView;
    /**
     * 验证码
     */
    private CodeView mCodeView;
    /**
     * 重置按钮
     */
    private Button mResetBtn;
    /**
     * 适配器
     */
    private HanZiCodeAdapter hzAdapter;
    /**
     * 获取到的文字备份
     */
    private List<String> cHanZiList;
    /**
     * 获取到的文字
     */
    private List<String> mHanZiList;
    /**
     * 选择的文字
     */
    private List<String> mChooseHZList;
    /**
     * 正确答案
     */
    private List<String> mCorrectList;
    /**
     * 验证框汉字颜色
     */
    private int mColor;
    /**
     * 测试计数
     */
    private int num = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        initData();
        initView();
        initGridView();
    }

    /**
     * 初始化gridview
     */
    private void initGridView() {
        mGridView = (GridView) findViewById(R.id.gv_hz);
        hzAdapter = new HanZiCodeAdapter();
        mGridView.setAdapter(hzAdapter);
        Util.calGridViewWidthAndHeigh(3, mGridView);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mHanZiList = new ArrayList<String>();
        cHanZiList = new ArrayList<String>();
        mChooseHZList = new ArrayList<String>();
        mCorrectList = new ArrayList<String>();
        mHanZiList.add("我");
        mHanZiList.add("好");
        mHanZiList.add("想");
        mHanZiList.add("你");
        Collections.shuffle(mHanZiList);
        cHanZiList.addAll(mHanZiList);
        // 添加不重复的文字
        for (int i = 0; i < 4; i++) {
            String hz = cHanZiList.get((int) (Math.random() * cHanZiList.size()));
            cHanZiList.remove(hz);
//            mCorrectList.add(hz);
        }
        mCorrectList.add("我");
        mCorrectList.add("好");
        mCorrectList.add("想");
        mCorrectList.add("你");

        mColor = Util.randomColor();

     }


    /**
     * 初始化控件
     */
    private void initView() {
        mAnswerLayout = (AnswerLayout) findViewById(R.id.al_mal);
        mAnswerLayout.setCorrectAnswers(mCorrectList);
        mAnswerLayout.setCheckAnswerListener(new AnswerLayout.CheckAnswerListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(CodePassActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail() {
                Toast.makeText(CodePassActivity.this, "失败", Toast.LENGTH_SHORT).show();
            }
        });
        mCodeView = (CodeView) findViewById(R.id.cv_hz);
        mCodeView.setCode(mCorrectList);
        mResetBtn = (Button) findViewById(R.id.btn_reset);
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
                mAnswerLayout.reSet(mCorrectList);
                mCodeView.reSet(mCorrectList);
                hzAdapter.notifyDataSetChanged();
            }
        });

    }


    /**
     * 汉子展示框适配器
     */
    class HanZiCodeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mHanZiList.size();
        }

        @Override
        public Object getItem(int i) {
            return mHanZiList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();
            if (null == view) {
                view = LayoutInflater.from(CodePassActivity.this).inflate(R.layout.hanzi_item, null, false);
                holder.hzImg = (HanZiView) view.findViewById(R.id.hz_item);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            //设置holder
            holder.hzImg.setText(mHanZiList.get(i));
            holder.hzImg.setTextColor(mColor);
            holder.hzImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mChooseHZList.size() < 4) {
                        mChooseHZList.add(mHanZiList.get(i));
                        mAnswerLayout.setAnswers(mChooseHZList);
                    }
//
                }
            });
            return view;
        }

        class ViewHolder {
            HanZiView hzImg;
        }

    }
}
