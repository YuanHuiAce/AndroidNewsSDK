package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by h.yuan on 2015/3/23.
 */
public class NewsGridView extends GridView {

    public NewsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsGridView(Context context) {
        super(context);
    }

    public NewsGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
