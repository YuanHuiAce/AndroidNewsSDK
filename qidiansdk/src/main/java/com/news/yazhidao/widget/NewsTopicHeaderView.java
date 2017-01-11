package com.news.yazhidao.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.news.yazhidao.R;
import com.news.yazhidao.entity.TopicBaseInfo;
import com.news.yazhidao.utils.TextUtil;

public class NewsTopicHeaderView extends RelativeLayout {

    private TextView mTopicDetail;
    private ImageView mTopicView;
    private Context mContext;
    private TopicBaseInfo mTopicBaseInfo;
    private RelativeLayout mHeaderLayout;

    //新闻标题,新闻时间,新闻描述
    public NewsTopicHeaderView(Context context) {
        this(context, null);
        mContext = context;
    }

    public NewsTopicHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        mContext = context;
    }

    public NewsTopicHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View root = View.inflate(context, R.layout.aty_news_topic_hearder_view, this);
        mTopicDetail = (TextView) root.findViewById(R.id.mTopicDetail);
        mTopicView = (ImageView) root.findViewById(R.id.mTopicView);
        mHeaderLayout = (RelativeLayout) root.findViewById(R.id.mHeaderDivider);
        TextUtil.setLayoutBgColor(mContext, mHeaderLayout, R.color.white);
    }

    public void setHeaderViewData(TopicBaseInfo topicBaseInfo, int screenWidth) {
        mTopicBaseInfo = topicBaseInfo;
        String url = mTopicBaseInfo.getCover();
        if (!TextUtil.isEmptyString(url)) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTopicView.getLayoutParams();
            layoutParams.width = screenWidth;
            layoutParams.height = (int) (screenWidth * 86 / 360.0f);
            mTopicView.setLayoutParams(layoutParams);
            Glide.with(mContext).load(Uri.parse(mTopicBaseInfo.getCover())).centerCrop().placeholder(R.drawable.bg_load_default_small).into(mTopicView);
        }
        String description = mTopicBaseInfo.getDescription();
        if (!TextUtil.isEmptyString(description)) {
//            String str="这是设置TextView部分文字背景颜色和前景颜色的demo!";
//            int bstart=str.indexOf("背景");
//            int bend=bstart+"背景".length();
//            int fstart=str.indexOf("前景");
//            int fend=fstart+"前景".length();
//            SpannableStringBuilder style=new SpannableStringBuilder(str);
//            style.setSpan(new BackgroundColorSpan(Color.RED),bstart,bend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            style.setSpan(new ForegroundColorSpan(Color.RED),fstart,fend,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//
//            ColorStateList redColors = ColorStateList.valueOf(0xffff0000);
//            SpannableStringBuilder spanBuilder = new SpannableStringBuilder(description);
////style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
////size  为0 即采用原始的正常的 size大小
//            spanBuilder.setSpan(new TextAppearanceSpan(null, 0, 60, redColors, null), 0, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            mTopicDetail.setText(description);
        } else {
            mTopicDetail.setVisibility(GONE);
        }
    }
}
