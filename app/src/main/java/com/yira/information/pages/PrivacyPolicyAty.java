package com.yira.information.pages;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.utils.TextUtil;
import com.news.yazhidao.R;

public class PrivacyPolicyAty extends BaseActivity implements View.OnClickListener {
    private ImageView mPrivacyLeftBack;
    private WebView mPrivacyWebView;
    private RelativeLayout mPrivacyContainer;
    private TextView mPrivacyTitle;
    private View mBottomLine;
    private RelativeLayout mPrivacyHeader;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_privacy_policy);
    }

    @Override
    protected void initializeViews() {
        mPrivacyLeftBack = (ImageView) findViewById(R.id.mPrivacyLeftBack);
        mPrivacyContainer = (RelativeLayout) findViewById(R.id.rl_privacy_container);
        mPrivacyTitle = (TextView) findViewById(R.id.tv_privacy_title);
        mBottomLine = findViewById(R.id.privacy_bottom_line);
        mPrivacyHeader = (RelativeLayout) findViewById(R.id.mPrivacyHeader);


        mPrivacyLeftBack.setOnClickListener(this);
        mPrivacyWebView = (WebView) findViewById(R.id.mPrivacyWebView);
        mPrivacyWebView.setBackgroundColor(getResources().getColor(R.color.transparent));
        mPrivacyWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY)
            mPrivacyWebView.loadUrl("file:///android_asset/PrivacyPolicy.html");
        else
            mPrivacyWebView.loadUrl("file:///android_asset/PrivacyPolicyNight.html");
        setTheme();
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
        setTheme();
    }

    private void setTheme() {
        TextUtil.setLayoutBgResource(this, mPrivacyContainer, R.color.color6);
        TextUtil.setLayoutBgResource(this, mPrivacyHeader, R.color.color6);
        TextUtil.setLayoutBgResource(this, mPrivacyLeftBack, R.drawable.bg_left_back_selector);
        TextUtil.setImageResource(this, mPrivacyLeftBack, R.drawable.btn_left_back);
//        TextUtil.setLayoutBgResource(this, mPrivacyWebView, R.color.color6);
        TextUtil.setLayoutBgResource(this, mBottomLine, R.color.color5);
        TextUtil.setTextColor(this, mPrivacyTitle, R.color.color2);
    }
}
