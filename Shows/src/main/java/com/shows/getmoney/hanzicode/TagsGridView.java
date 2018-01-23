package com.shows.getmoney.hanzicode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;


public class TagsGridView extends GridView {

    public TagsGridView(Context context) {
        super(context);
    }

    public TagsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagsGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            //禁止GridView滑动
            return true;
        }
        return super.dispatchTouchEvent(ev);

    }
}
