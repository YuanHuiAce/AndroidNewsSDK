package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by h.yuan on 2015/3/23.
 */
public class NewsListView extends ListView {

    public NewsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsListView(Context context) {
        super(context);
    }

    public NewsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
