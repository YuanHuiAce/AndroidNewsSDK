package com.news.sdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Berkeley on 1/9/17.
 */

public class VideoContainer extends FrameLayout {
    public VideoContainer(Context context) {
        super(context);
    }

    public VideoContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }
}
