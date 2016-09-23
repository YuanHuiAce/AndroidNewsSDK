package com.news.yazhidao.widget.webview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebView;


/**
 * Created by Administrator on 2016/5/16.
 */
public class LoadWebView extends WebView {
    public LoadWebView(Context context) {
        super(context);
    }

    boolean isOnceSuccess;
    public LoadWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //onDraw表示显示完毕
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isOnceSuccess){
           return;
        }
        isOnceSuccess = true;
        df.After();
    }
    public interface PlayFinish{
        void After();
    }
    PlayFinish df;
    public void setDf(PlayFinish playFinish) {
        this.df = playFinish;
    }


}
