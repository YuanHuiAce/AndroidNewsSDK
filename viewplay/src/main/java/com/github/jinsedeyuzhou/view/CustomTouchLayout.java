package com.github.jinsedeyuzhou.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Berkeley on 4/18/17.
 */

public class CustomTouchLayout extends LinearLayout {

    public CustomTouchLayout(Context context) {
        super(context);
    }

    public CustomTouchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTouchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomTouchLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN)
        {
            return true;
        }
        return super.onTouchEvent(event);
    }
}
