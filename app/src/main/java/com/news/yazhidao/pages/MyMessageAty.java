package com.news.yazhidao.pages;

import android.view.View;

import com.news.sdk.common.BaseActivity;
import com.news.yazhidao.R;

public class MyMessageAty extends BaseActivity implements View.OnClickListener {

    private View mMessagetLeftBack;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_my_message);
    }

    @Override
    protected void initializeViews() {
        mMessagetLeftBack = findViewById(R.id.mMessagetLeftBack);
        mMessagetLeftBack.setOnClickListener(this);
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
            case R.id.mMessagetLeftBack:
                finish();
                break;
        }
    }

    @Override
    public void onThemeChanged() {

    }
}
