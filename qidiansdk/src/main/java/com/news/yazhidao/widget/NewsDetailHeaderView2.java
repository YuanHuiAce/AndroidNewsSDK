package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.utils.TextUtil;

/**
 * Created by fengjigang on 15/9/6.
 */
public class NewsDetailHeaderView2 extends RelativeLayout {

    private final TextView mNewsDetailSourceAndTime;
    private final TextView mNewsDetailTitle;

    //新闻标题,新闻时间,新闻描述
    public NewsDetailHeaderView2(Context context) {
        this(context, null);
    }

    public NewsDetailHeaderView2(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NewsDetailHeaderView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View root = View.inflate(context, R.layout.aty_news_detail_header_view2, this);
        mNewsDetailSourceAndTime = (TextView)root.findViewById(R.id.mNewsDetailSourceAndTime);
        mNewsDetailTitle = (TextView)root.findViewById(R.id.mNewsDetailTitle);
    }

    /**
     * 获取数据后刷新界面
     * @param pNewsDetail
     */
    public void updateView(Object pNewsDetail){
         if(pNewsDetail instanceof NewsDetailAdd){
            NewsDetailAdd detail = (NewsDetailAdd)pNewsDetail;
             mNewsDetailTitle.setText(detail.title);
             String source = detail.sourceSiteName;
             String time = detail.updateTime;
             if (!TextUtil.isEmptyString(source)){
                 source = source.replace("\n","") + "  ";
             }else {
                 source ="";
             }
             if (!TextUtil.isEmptyString(time)){
                 time = time.replace("\n","");
             }
             if (TextUtil.isEmptyString(source + time)){
                 mNewsDetailSourceAndTime.setVisibility(GONE);
             }
             mNewsDetailSourceAndTime.setText(source+time);
        }
    }
}
