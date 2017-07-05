package com.news.yazhidao.pages;

import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.sdk.common.BaseActivity;
import com.news.sdk.pages.DeveloperAty;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.TextUtil;
import com.news.yazhidao.R;

public class AboutAty extends BaseActivity implements View.OnClickListener {

    private ImageView mAboutLeftBack;
    private TextView mAboutVersion;
    private RelativeLayout mAboutContainer;
    private RelativeLayout mTitleContainer;
    private TextView mTitle;
    private ImageView mAppIcon;
    private TextView mAppName;
    private TextView mAboutAttestation;
    private ImageView mCompanyLogo;
    private View mBottomLine;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_about);
    }

    @Override
    protected void initializeViews() {

        mAboutContainer = (RelativeLayout) findViewById(R.id.rl_about_container);
        mTitleContainer = (RelativeLayout) findViewById(R.id.rl_title_container);
        mTitle = (TextView) findViewById(R.id.tv_about_title);
        mAppIcon = (ImageView) findViewById(R.id.iv_lanuch_icon);
        mCompanyLogo = (ImageView) findViewById(R.id.iv_about_company_logo);
        mBottomLine = findViewById(R.id.iv_bottom_line);
        mAboutLeftBack = (ImageView) findViewById(R.id.mAboutLeftBack);
        mAppName = (TextView) findViewById(R.id.tv_about_appname);
        mAboutAttestation = (TextView) findViewById(R.id.tv_about_attestation);
        mAboutLeftBack.setOnClickListener(this);
        mAboutVersion = (TextView) findViewById(R.id.mAboutVersion);
        mAboutVersion.setText("V" + getResources().getString(R.string.app_version));
        setTheme();

    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {

    }
    private int mSecretNumber = 0;
    private static final long MIN_CLICK_INTERVAL = 600;
    private long mLastClickTime;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mAboutLeftBack:
                finish();
                break;
            case R.id.mAboutVersion:
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;

                if (elapsedTime < MIN_CLICK_INTERVAL) {
                    ++mSecretNumber;
                    if (2 == mSecretNumber) {
                        try {
                            Intent intent = new Intent(this, DeveloperAty.class);
                            startActivity(intent);
                        } catch (Exception e) {
                        }

                    }
                } else {
                    mSecretNumber = 0;
                }
                break;
        }
    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }

    private void setTheme() {
        TextUtil.setLayoutBgResource(this, mAboutContainer, R.color.color6);
        TextUtil.setLayoutBgResource(this, mTitleContainer, R.color.color6);
        TextUtil.setLayoutBgResource(this, mAboutLeftBack, R.drawable.bg_left_back_selector);
        TextUtil.setImageResource(this, mAboutLeftBack, R.drawable.btn_left_back);
        TextUtil.setTextColor(this, mTitle, R.color.color2);
        TextUtil.setTextColor(this, mAppName, R.color.color2);
        TextUtil.setTextColor(this, mAboutVersion, R.color.color3);
        TextUtil.setTextColor(this, mAboutAttestation, R.color.color3);
        TextUtil.setLayoutBgResource(this, mBottomLine, R.color.color5);
        ImageUtil.setAlphaImage(mAppIcon, R.mipmap.ic_launcher);
        ImageUtil.setAlphaImage(mCompanyLogo, R.mipmap.ic_about_company_logo);

    }
}
