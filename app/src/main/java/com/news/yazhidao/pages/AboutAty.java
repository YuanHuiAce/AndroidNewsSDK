package com.news.yazhidao.pages;

import android.view.View;
import android.widget.TextView;

import com.news.sdk.common.BaseActivity;
import com.news.yazhidao.R;

public class AboutAty extends BaseActivity implements View.OnClickListener {

    private View mAboutLeftBack;
    private TextView mAboutVersion;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_about);
    }

    @Override
    protected void initializeViews() {
        mAboutLeftBack = findViewById(R.id.mAboutLeftBack);
        mAboutLeftBack.setOnClickListener(this);
        mAboutVersion = (TextView) findViewById(R.id.mAboutVersion);
        mAboutVersion.setText("V" + getResources().getString(R.string.app_version));
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
            case R.id.mAboutLeftBack:
                finish();
                break;
        }
    }
}
