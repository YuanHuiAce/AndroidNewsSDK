package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Berkeley on 1/5/17.
 */

public class SmallVideoContainer extends FrameLayout {
    public SmallVideoContainer(Context context) {
        super(context);
    }

    public SmallVideoContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmallVideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
