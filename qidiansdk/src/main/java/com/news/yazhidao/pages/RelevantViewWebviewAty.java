package com.news.yazhidao.pages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.utils.Logger;

/**
 * Created by fengjigang on 15/3/27.
 */
public class RelevantViewWebviewAty extends BaseActivity {
    private static final String TAG = "NewsDetailWebviewAty";
    public static final String KEY_URL = "key_url";
    private WebView mNewsSourcesiteWebview;
    private String mNewsUrl;
    private ImageView mLeftBack;
    private ProgressBar mNewsSourcesiteProgress;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_relevant_view_webview);
        mNewsSourcesiteProgress = (ProgressBar) findViewById(R.id.mNewsSourcesiteProgress);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, R.id.mDetailWebHeader);
        mNewsSourcesiteWebview = new WebView(getApplicationContext());
        mNewsSourcesiteWebview.setLayoutParams(params);
        layout.addView(mNewsSourcesiteWebview);
        mLeftBack = (ImageView) findViewById(R.id.mLeftBack);
        mLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initializeViews() {
        mNewsUrl = getIntent().getStringExtra(KEY_URL);
//        mNewsSourcesiteWebview.getSettings().setUseWideViewPort(true);                    //让webview读取网页设置的viewport
//        mNewsSourcesiteWebview.getSettings().setLoadWithOverviewMode(true);           //设置一个默认的viewport=800，如果网页自己没有设置viewport，就用800
        mNewsSourcesiteWebview.getSettings().setJavaScriptEnabled(true);
//        mNewsSourcesiteWebview.getSettings().setSupportZoom(true);
        mNewsSourcesiteWebview.getSettings().setBuiltInZoomControls(true);
        mNewsSourcesiteWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mNewsSourcesiteWebview.getSettings().setDisplayZoomControls(false);
        mNewsSourcesiteWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Logger.e(TAG, "xxx  " + newProgress);
//                if(newProgress>=89&&mProgressDialog!=null&&mProgressDialog.isShowing()){
//                    mProgressDialog.dismiss();
//                }
                mNewsSourcesiteProgress.setProgress(newProgress);
                if (newProgress == 100) {
                    mNewsSourcesiteProgress.setVisibility(View.GONE);
                }
            }
        });
        mNewsSourcesiteWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                resend.sendToTarget();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Logger.e(TAG, "xxxx shouldOverrideUrlLoading"+url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                if(mProgressDialog==null){
//                    mProgressDialog=ProgressDialog.show(NewsDetailWebviewAty.this,null,"加载中...");
//                    mProgressDialog.setCancelable(true);
//                }
                Logger.e(TAG, "xxxx onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.e(TAG, "xxxx onPageFinished");
//                if(mProgressDialog!=null&&mProgressDialog.isShowing()){
//                    mProgressDialog.dismiss();
//                }
            }
        });
    }

    @Override
    protected void loadData() {
        mNewsSourcesiteWebview.loadUrl(mNewsUrl);
        mNewsSourcesiteWebview.setDownloadListener(new MyWebViewDownLoadListener());
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
//            Intent intent = new Intent(NewsDetailWebviewAty.this, NewsDetailWebviewAty.class);
//            String zhihuUrl = relatedItemEntity.getUrl();
//            intent.putExtra(NewsDetailWebviewAty.KEY_URL, url);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNewsSourcesiteWebview != null) {
            ((ViewGroup) mNewsSourcesiteWebview.getParent()).removeView(mNewsSourcesiteWebview);
//            mNewsSourcesiteWebview.removeAllViews();
            mNewsSourcesiteWebview.destroy();
            mNewsSourcesiteWebview = null;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
