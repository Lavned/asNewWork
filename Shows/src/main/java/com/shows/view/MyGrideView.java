package com.shows.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/4/19 0019.
 */
public class MyGrideView extends GridView {

    BaseAdapter baseAdapter;
    public MyGrideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyGrideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGrideView(Context context) {
        super(context);
    }

    public void setAdapter(BaseAdapter baseAdapter) {
        this.baseAdapter = baseAdapter;
    }

    /**
     * 设置不滚动
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try{
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
//            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
//                    getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
            super.onMeasure(widthMeasureSpec, expandSpec);
        }catch (Exception e){
            e.printStackTrace();
        }


    }


}
