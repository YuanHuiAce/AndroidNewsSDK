package com.news.yazhidao.pages;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.sdk.common.BaseActivity;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.TextUtil;
import com.news.yazhidao.R;

public class MyMessageAty extends BaseActivity implements View.OnClickListener {

    private View mHeaderDivider;
    private RelativeLayout bgLayout, mMessageTopLayout;
    private ImageView mMessageLeftBack;
    private TextView mMessageTitle ,mMessageText;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_my_message);
    }

    @Override
    protected void initializeViews() {
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mHeaderDivider = findViewById(R.id.mHeaderDivider);
        mMessageTopLayout = (RelativeLayout) findViewById(R.id.mMessageTopLayout);
        mMessageTitle = (TextView) findViewById(R.id.mMessageTitle);
        mMessageText = (TextView) findViewById(R.id.mMessageText);
        mMessageLeftBack = (ImageView) findViewById(R.id.mMessageLeftBack);
        mMessageLeftBack.setOnClickListener(this);
        setTheme();
    }

    private void setTheme() {
        TextUtil.setLayoutBgResource(this, bgLayout, R.color.color6);
        TextUtil.setLayoutBgResource(this, mMessageLeftBack, R.drawable.bg_left_back_selector);
        TextUtil.setImageResource(this, mMessageLeftBack, R.drawable.btn_left_back);
        TextUtil.setLayoutBgResource(this, mMessageTopLayout, R.color.color6);
        TextUtil.setLayoutBgResource(this, mHeaderDivider, R.color.color5);
        TextUtil.setTextColor(this, mMessageTitle, R.color.color2);
        TextUtil.setTextColor(this, mMessageText, R.color.color4);
        ImageUtil.setAlphaImage(mMessageText);
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mMessageLeftBack:
                finish();
                break;
        }
    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }
}
