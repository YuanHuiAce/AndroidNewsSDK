package com.news.yazhidao.pages;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.news.sdk.common.BaseActivity;
import com.news.yazhidao.R;

public class PrivacyPolicyAty extends BaseActivity implements View.OnClickListener {
    private View mPrivacyLeftBack;
    private WebView mPrivacyWebView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_privacy_policy);
    }

    @Override
    protected void initializeViews() {
        mPrivacyLeftBack = findViewById(R.id.mPrivacyLeftBack);
        mPrivacyLeftBack.setOnClickListener(this);
        mPrivacyWebView = (WebView) findViewById(R.id.mPrivacyWebView);
        mPrivacyWebView.loadUrl("file:///android_asset/PrivacyPolicy.html");
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPrivacyWebView != null) {
            ((ViewGroup) mPrivacyWebView.getParent()).removeView(mPrivacyWebView);
            mPrivacyWebView.destroy();
            mPrivacyWebView = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mPrivacyLeftBack:
                finish();
                break;
        }
    }

    @Override
    public void onThemeChanged() {

    }
}
