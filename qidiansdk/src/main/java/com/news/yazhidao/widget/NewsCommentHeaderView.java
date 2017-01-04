package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.TextUtil;

public class NewsCommentHeaderView extends LinearLayout {

    private TextView mtvNewsCommentTitle;
    private TextView mtvNewsCommentContent;
    private LinearLayout mllNewsCommentNoCommentsLayout;
    private NewsFeed mNewsFeed;

    //新闻标题,新闻时间,新闻描述
    public NewsCommentHeaderView(Context context) {
        this(context, null);
    }

    public NewsCommentHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NewsCommentHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = View.inflate(context, R.layout.news_comment_fragment_headerview, this);
        mtvNewsCommentTitle = (TextView) rootView.findViewById(R.id.news_comment_Title);
        mtvNewsCommentContent = (TextView) rootView.findViewById(R.id.news_comment_content);
        mllNewsCommentNoCommentsLayout = (LinearLayout) rootView.findViewById(R.id.news_comment_NoCommentsLayout);
    }

    public void setData(NewsFeed newsFeed) {
        mNewsFeed = newsFeed;
        updateView();
    }

    private void updateView() {
        if (mNewsFeed != null) {
            String title = mNewsFeed.getTitle();
//            if (!TextUtil.isEmptyString(title)) {
                mtvNewsCommentTitle.setText(mNewsFeed.getTitle());
//            }
            int comment = mNewsFeed.getComment();
            String pname = mNewsFeed.getPname();
            String ptime = mNewsFeed.getPtime();
            if (0 == comment) {
                mtvNewsCommentContent.setText(pname + "  " + DateUtil.getMonthAndDay(ptime));
                setNoCommentsLayoutVisible();
            } else {
                mtvNewsCommentContent.setText(pname + "  " + DateUtil.getMonthAndDay(ptime) + "  " + TextUtil.getCommentNum(String.valueOf(comment)));
                setNoCommentsLayoutGone();
            }
        }
    }

    public void setNoCommentsLayoutVisible() {
        mllNewsCommentNoCommentsLayout.setVisibility(View.VISIBLE);
    }

    public void setNoCommentsLayoutGone() {
        mllNewsCommentNoCommentsLayout.setVisibility(View.GONE);
    }

    public void setNewsCommentTitleTextSize(int size) {
        mtvNewsCommentTitle.setTextSize(size);
    }
}
