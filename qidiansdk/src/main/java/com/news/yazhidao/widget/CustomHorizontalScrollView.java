package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.widget.HorizontalScrollView;
//
//import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;
//
///**
// * Created by fengjigang on 15/5/20.
// */
//public class CustomHorizontalScrollView extends HorizontalScrollView {
//    private SwipeBackActivity mActivity;
//
//    public CustomHorizontalScrollView(Context context) {
//        this(context, null);
//    }
//
//    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        mActivity = (SwipeBackActivity) context;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mActivity.getSwipeBackLayout().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mActivity.getSwipeBackLayout().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                requestDisallowInterceptTouchEvent(false);
//                break;
//        }
//        return super.onInterceptTouchEvent(event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mActivity.getSwipeBackLayout().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mActivity.getSwipeBackLayout().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                mActivity.getSwipeBackLayout().requestDisallowInterceptTouchEvent(false);
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//
//            case MotionEvent.ACTION_DOWN:
//
//                mActivity.getSwipeBackLayout().requestDisallowInterceptTouchEvent(true);
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                mActivity.getSwipeBackLayout().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                mActivity.getSwipeBackLayout().requestDisallowInterceptTouchEvent(false);
//                break;
//        }
//        return super.dispatchTouchEvent(event);
//    }
//}
