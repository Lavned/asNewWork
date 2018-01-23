package com.shows.getmoney.hanzicode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.yixinke.shows.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junweiliu on 16/5/4.
 */
public class AnswerLayout extends LinearLayout {
    /**
     * 整体的宽度
     */
    private int pWidth;
    /**
     * 需要验证字的个数(多少个框,默认为4个)
     */
    private int HANZI_TEST_SIZE;
    /**
     * 需要创建的子View个数
     */
    private int childViewCount;
    /**
     * 回答的答案列表
     */
    private List<String> mAnswers;
    /**
     * 正确答案
     */
    private List<String> mCorrectAnswer;
    /**
     * 删除按钮资源
     */
    private int deleteBtnSrc;
    /**
     * 汉子格边框颜色
     */
    private int hanziBorderColor;
    /**
     * 汉子格边框宽度
     */
    private float hanziBorderStrokeWidth;
    /**
     * 汉字格文字的颜色
     */
    private int hanziTextColor;
    /**
     * 汉字格文字的大小
     */
    private int hanziTextSize;

    /**
     * 回调接口
     */
    interface CheckAnswerListener {
        // 成功回调
        public void onSuccess();

        // 失败回调
        public void onFail();

    }

    /**
     * 回调
     */
    private CheckAnswerListener mListener;

    /**
     * 设置回调
     *
     * @param listener
     */
    public void setCheckAnswerListener(CheckAnswerListener listener) {
        this.mListener = listener;
    }

    public AnswerLayout(Context context) {
        this(context, null);
    }

    public AnswerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnswerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AnswerLayout);
        deleteBtnSrc = ta.getResourceId(R.styleable.AnswerLayout_deleteBtnSrc, R.drawable.delete_btn_selector);
        hanziBorderColor = ta.getColor(R.styleable.AnswerLayout_hanziBorderColor, Color.GRAY);
        hanziBorderStrokeWidth = ta.getFloat(R.styleable.AnswerLayout_hanziBorderStrokeWidth, 3.0f);
        HANZI_TEST_SIZE = ta.getInt(R.styleable.AnswerLayout_hanziTestNum, 4);
        hanziTextColor = ta.getColor(R.styleable.AnswerLayout_hanziTextColor, Color.BLACK);
        hanziTextSize = ta.getDimensionPixelSize(R.styleable.AnswerLayout_hanziTextSize, 16);
        ta.recycle();
        init();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mAnswers.size() > HANZI_TEST_SIZE) {
            return;
        }
        if (mAnswers.size() == 0) {
            for (int i = 0; i < HANZI_TEST_SIZE; i++) {
                TextView tv = (TextView) getChildAt(i);
                tv.setText("");
            }
        } else {
            for (int i = 0; i < mAnswers.size(); i++) {
                TextView tv = (TextView) getChildAt(i);
                tv.setText(mAnswers.get(i));
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        pWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (HANZI_TEST_SIZE <= 0) {
            return;
        }
        // 需要加入一个删除按钮,所以子view的总数需要加1
        childViewCount = HANZI_TEST_SIZE + 1;
        for (int i = 0; i < HANZI_TEST_SIZE; i++) {
            addView(makeItemView(i));
        }
        addView(makeDeleteView());
    }


    /**
     * 初始化数据
     */
    public void init() {
        mAnswers = new ArrayList<String>();
        mCorrectAnswer = new ArrayList<String>();
    }

    /**
     * 填写的答案
     *
     * @param answers
     */
    public void setAnswers(List<String> answers) {
        this.mAnswers = answers;
        invalidate();
        if (compare(mAnswers, mCorrectAnswer) && mAnswers.size() == HANZI_TEST_SIZE) {
            if (null != mListener)
                mListener.onSuccess();
        } else if (mAnswers.size() == HANZI_TEST_SIZE) {
            if (null != mListener)
                mListener.onFail();
        }

    }

    /**
     * 判断两个list是否完全相同
     *
     * @param a
     * @param b
     * @param <T>
     * @return
     */
    private <T extends Comparable<T>> boolean compare(List<T> a, List<T> b) {
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i)))
                return false;
        }
        return true;
    }

    /**
     * 正确答案
     *
     * @param correctAnswers
     */
    public void setCorrectAnswers(List<String> correctAnswers) {
        this.mCorrectAnswer = correctAnswers;
    }

    /**
     * 重置
     */
    public void reSet(List<String> correctAnswers) {
        mAnswers.clear();
        mCorrectAnswer = correctAnswers;
        invalidate();
    }

    /**
     * 创建删除按钮
     *
     * @return
     */
    private View makeDeleteView() {
        ImageView deleteView = new ImageView(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = pWidth / childViewCount;
        lp.height = pWidth / childViewCount;
        deleteView.setImageResource(deleteBtnSrc);
        deleteView.setScaleType(ImageView.ScaleType.FIT_XY);
        deleteView.setLayoutParams(lp);
        deleteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = HANZI_TEST_SIZE; i > 0; i--) {
                    TextView tv = (TextView) getChildAt(i - 1);
                    if (!"".equals(tv.getText())) {
                        mAnswers.remove(i - 1);
                        tv.setText("");
                        break;
                    }

                }
            }
        });
        return deleteView;
    }


    /**
     * 创建汉子验证格
     *
     * @return
     */
    private View makeItemView(final int i) {
        final BorderTextView bv = new BorderTextView(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = pWidth / childViewCount;
        lp.height = pWidth / childViewCount;
        bv.setBorderColor(hanziBorderColor);
        bv.setTextSize(hanziTextSize);
        bv.setGravity(Gravity.CENTER);
        bv.setText("");
        bv.setLayoutParams(lp);
        bv.setTextColor(hanziTextColor);
        bv.setBorderStrokeWidth(hanziBorderStrokeWidth);
        return bv;
    }
}
