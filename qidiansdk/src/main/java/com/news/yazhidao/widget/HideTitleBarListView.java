package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class HideTitleBarListView extends PullToRefreshListView {



    public HideTitleBarListView(Context context, Mode mode) {
        super(context, mode);
    }

    public HideTitleBarListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    public HideTitleBarListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



}
