package com.news.sdk.pages;

import android.widget.RelativeLayout;

import com.news.sdk.R;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.widget.NegativeScreenNewsDetailView;


/**
 * 新闻展示详情页
 */
public class NegativeScreenNewsDetailAty extends BaseActivity {

    RelativeLayout mDetailWrapper;
    NegativeScreenNewsDetailView mNegativeScreenNewsDetailView;
    private NewsFeed mNewsFeed;
    private String mDocid, mTitle, mNewID;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    public void onThemeChanged() {
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_negative_screen_layout);
    }

    @Override
    protected void initializeViews() {
        mDetailWrapper = (RelativeLayout) findViewById(R.id.mDetailWrapper);
        mNegativeScreenNewsDetailView = new NegativeScreenNewsDetailView(this);
        mDetailWrapper.addView(mNegativeScreenNewsDetailView.getNewsView());
    }

    @Override
    protected void loadData() {
        mNewsFeed = (NewsFeed) getIntent().getSerializableExtra(NewsFeedFgt.KEY_NEWS_FEED);
        if (mNewsFeed != null) {
            mNegativeScreenNewsDetailView.setNewsFeed(mNewsFeed);
        } else {
            mNewID = getIntent().getStringExtra(NewsDetailFgt.KEY_NEWS_ID);
            mDocid = getIntent().getStringExtra(NewsDetailFgt.KEY_NEWS_DOCID);
            mTitle = getIntent().getStringExtra(NewsDetailFgt.KEY_NEWS_TITLE);
            mNegativeScreenNewsDetailView.setData(mNewID, mDocid, mTitle);
        }
    }


    @Override
    protected void onDestroy() {
        mNegativeScreenNewsDetailView.destroy();
        super.onDestroy();
    }
}
