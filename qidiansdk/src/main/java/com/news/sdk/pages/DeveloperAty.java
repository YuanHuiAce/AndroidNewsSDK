package com.news.sdk.pages;

import android.widget.LinearLayout;

import com.news.sdk.R;
import com.news.sdk.common.BaseActivity;

/**
 * Created by Berkeley on 6/16/17.
 */

public class DeveloperAty extends BaseActivity
{

    private LinearLayout mDeveloperUser;
    private LinearLayout mDeveloperDevice;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_developer);

    }

    @Override
    protected void initializeViews() {
        mDeveloperUser = (LinearLayout) findViewById(R.id.ll_developer_user);
        mDeveloperDevice = (LinearLayout) findViewById(R.id.ll_developer_device);

    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onThemeChanged() {

    }
}
