package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fengjigang on 15/10/31.
 * 热词搜索标签布局
 */
public class HotLabelsLayout extends ViewGroup {
    private Context mContext;

    public HotLabelsLayout(Context context) {
        this(context, null);
    }

    public HotLabelsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotLabelsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(mContext,attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        int lineWidth = 0;//当前行的宽度
        int lineHeight = 0;//当前行的高度
        int totalWidth = 0;//整个View的宽度
        int totalHeight = 0;//整个View的高度

        int count = getChildCount();
        for (int i = 0; i < count; i++){
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            //需要换行
            if (lineWidth + childWidth > measureWidth){
                totalWidth = Math.max(lineWidth,childWidth);
                totalHeight += childHeight;
                lineWidth = childWidth;
                lineHeight = childHeight;
            }else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight,childHeight);
            }

            //最后一个单独处理
            if (i == count -1){
                totalHeight += childHeight;
                lineWidth = Math.max(childWidth,lineWidth);
            }
        }
        setMeasuredDimension(measureWidthMode == MeasureSpec.EXACTLY ? measureWidth : totalWidth,measureHeightMode == MeasureSpec.EXACTLY ? measureHeight : totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineWidth = 0;
        int lineHeight = 0;
        int left = 0,top = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++){
            View child = getChildAt(i);
            MarginLayoutParams marginParams = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + marginParams.leftMargin + marginParams.rightMargin;
            int childHeight = child.getMeasuredHeight() + marginParams.topMargin + marginParams.bottomMargin;
            if (lineWidth + childWidth > getMeasuredWidth()){
                top += childHeight;//换行时,高度增加
                left = 0;//换行时,left 重置为0
                lineHeight = childHeight;
                lineWidth = childWidth;
            }else {
                lineHeight = Math.max(lineHeight,childHeight);
                lineWidth += childWidth;
            }
            int lc = left + marginParams.leftMargin;
            int tc = top + marginParams.topMargin;
            int rc = lc + child.getMeasuredWidth();
            int bc = tc + child.getMeasuredHeight();
            child.layout(lc, tc, rc, bc);
            //增加left 偏移量
            left += childWidth;
        }
    }
}
