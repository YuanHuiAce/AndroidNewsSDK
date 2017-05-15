package com.news.sdk.pages;

import android.net.http.SslError;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.news.sdk.R;
import com.news.sdk.common.BaseActivity;

/**
 * Created by fengjigang on 15/3/27.
 */
public class NewsWebviewAty extends BaseActivity {
    ListView linearLayout;
    WebView webView;
    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_webview);
        linearLayout = (ListView) findViewById(R.id.play);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 1000);
        LinearLayout linearLayout1 = new LinearLayout(this);
        linearLayout1.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        params.weight =1 ;
        webView = new WebView(this);
        linearLayout.addHeaderView(linearLayout1);
        linearLayout1.addView(webView);
        WebSettings settings = webView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setDatabaseEnabled(true);
//        settings.setDomStorageEnabled(true);
//        settings.setLoadsImagesAutomatically(true);
//        settings.setBlockNetworkImage(false);
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        this.webView.getSettings().setSupportZoom(false);
//      this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        webView.setLayoutParams(params);
//        webView.loadUrl("http://deeporiginalx.com/content.html?type=0&nid=9372991");
//        webView.loadUrl("http://deeporiginalx.com/error.html");
        webView.loadUrl("http://www.baidu.com");
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i("tag",description+"==="+failingUrl);
                //view.loadData("ERROR: " + description,"text/plain","utf8");
            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                //handler.cancel(); 默认的处理方式，WebView变成空白页
//                        //接受证书
                handler.proceed();
                //handleMessage(Message msg); 其他处理
            }

        });
//        linearLayout.addView(webView);
//        ImageView imageView = new ImageView(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1400);
//        imageView.setLayoutParams(layoutParams);
//        imageView.setBackgroundResource(R.color.red);
//        linearLayout.addView(imageView);
    }


    @Override
    protected void initializeViews() {
    }

    @Override
    protected void loadData() {

    }


    @Override
    public void onPause() {
        super.onPause();
        // 加载空白页
        webView.loadUrl("about:blank");
    }
}