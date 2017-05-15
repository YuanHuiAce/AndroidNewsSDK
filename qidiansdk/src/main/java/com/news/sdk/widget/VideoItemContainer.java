package com.news.sdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Berkeley on 1/10/17.
 */

public class VideoItemContainer extends FrameLayout {
    public VideoItemContainer(Context context) {
        super(context);
    }

    public VideoItemContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoItemContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction())
//        {
//            case MotionEvent.ACTION_MOVE:
//                return true;
//        }
//        return super.dispatchTouchEvent(ev);
//    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction())
//        {
//            case MotionEvent.ACTION_MOVE:
//               return true;
//        }
//        return super.onInterceptTouchEvent(ev);
//    }
}
