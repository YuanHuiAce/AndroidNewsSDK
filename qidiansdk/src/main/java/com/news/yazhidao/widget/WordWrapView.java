package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.news.yazhidao.R;
import com.news.yazhidao.utils.DensityUtil;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class WordWrapView extends ViewGroup {

    private Context mContext;
    private static final int PADDING_HOR = 20;//水平方向padding
    private static final int PADDING_VERTICAL = 0;//垂直方向padding
    private static int SIDE_MARGIN;//左右间距
    private static int TEXT_MARGIN;

    /**
     * @param context
     */
    public WordWrapView(Context context) {
        super(context);
        mContext = context;
        SIDE_MARGIN = DensityUtil.dip2px(mContext, 12);
        TEXT_MARGIN = DensityUtil.dip2px(mContext, 12);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public WordWrapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        SIDE_MARGIN = DensityUtil.dip2px(mContext, 12);
        TEXT_MARGIN = DensityUtil.dip2px(mContext, 12);
    }


    /**
     * @param context
     * @param attrs
     */
    public WordWrapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        SIDE_MARGIN = DensityUtil.dip2px(mContext, 12);
        TEXT_MARGIN = DensityUtil.dip2px(mContext, 12);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int autualWidth = r - l;
        int x = SIDE_MARGIN;// 横坐标开始
        int y = 0;//纵坐标开始
        int rows = 1;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            view.setBackgroundResource(R.drawable.bg_douban_item);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            x += width + TEXT_MARGIN;
            if (x > autualWidth) {
                x = width + SIDE_MARGIN + TEXT_MARGIN;
                rows++;
            }
            y = rows * (height + TEXT_MARGIN);
//            if (i == 0) {
            view.layout(x - width - TEXT_MARGIN, y - height, x - TEXT_MARGIN, y);
//            }
//            else if(i==1){
//                view.layout(x - width- TEXT_MARGIN, y - height, x- TEXT_MARGIN, y);
//            }else {
//                view.layout(x - width, y - height, x, y);
//            }
        }
    }

    ;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x = 0;//横坐标
        int y = 0;//纵坐标
        int rows = 1;//总行数
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int actualWidth = specWidth - SIDE_MARGIN * 2;//实际宽度
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            child.setPadding(PADDING_HOR, PADDING_VERTICAL, PADDING_HOR, PADDING_VERTICAL);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            x += width + TEXT_MARGIN;
            if (x > actualWidth) {//换行
                x = width;
                rows++;
            }
            y = rows * (height + TEXT_MARGIN);
        }
        setMeasuredDimension(actualWidth, y);
    }

}