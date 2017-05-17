package com.news.sdk.pages;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.news.sdk.common.BaseActivity;
import com.news.sdk.javascript.VideoJavaScriptBridge;
import com.news.sdk.utils.Logger;

public class PlayVideoAty extends BaseActivity {
    private WebView mPlayVideoWebView;
    private String mVideoUrl;
//    private JavascriptInterface javascriptInterface;


    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mPlayVideoWebView = new WebView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        mPlayVideoWebView.setLayoutParams(layoutParams);
        setContentView(mPlayVideoWebView);
    }

    @Override
    protected void initializeViews() {
        mVideoUrl = getIntent().getStringExtra(VideoJavaScriptBridge.KEY_VIDEO_URL);
//        if (!TextUtil.isEmptyString(mVideoUrl) && mVideoUrl.contains("&")) {
//            mVideoUrl = mVideoUrl.substring(0, mVideoUrl.indexOf("&"));
//        }
//
        Logger.e("jigang", "aty url =" + mVideoUrl);
        initWebView();

    }

    public void initWebView() {
        WebSettings settings = mPlayVideoWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        InsideWebViewClient mInsideWebViewClient = new InsideWebViewClient();
        mPlayVideoWebView.setWebChromeClient(new WebChromeClient());
        mPlayVideoWebView.setWebViewClient(mInsideWebViewClient);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setSimulateClick(mPlayVideoWebView, mPlayVideoWebView.getWidth() / 2, mPlayVideoWebView.getHeight() / 2);
            }
        }, 1000);

    }

    /**
     * Android 模拟点击
     *
     * @param view
     * @param x
     * @param y
     */
    private void setSimulateClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_UP, x, y, 0);
        Logger.e("aaa", "x===" + x);
        Logger.e("aaa", "y===" + y);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPlayVideoWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayVideoWebView.onPause();
    }

    @Override
    public void onDestroy() {
        if (mPlayVideoWebView != null) {
            mPlayVideoWebView.removeAllViews();
            mPlayVideoWebView.destroy();
            mPlayVideoWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {

        super.onConfigurationChanged(config);

        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }

    }


    @Override
    protected void loadData() {
        mPlayVideoWebView.loadUrl(mVideoUrl);
    }

    @Override
    public void onThemeChanged() {

    }

    private class InsideWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
    }

}
