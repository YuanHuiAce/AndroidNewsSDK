package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.adapter.abslistview.MultiItemCommonAdapter;
import com.news.yazhidao.adapter.abslistview.MultiItemTypeSupport;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsDetailFgt;
import com.news.yazhidao.pages.NewsDetailVideoAty;
import com.news.yazhidao.utils.AdUtil;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.widget.EllipsizeEndTextView;
import com.news.yazhidao.widget.TextViewExtend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Administrator on 2016/4/22.
 */
public class NewsDetailFgtAdapter extends MultiItemCommonAdapter<RelatedItemEntity> {

    private Context mContext;
    public static final int REQUEST_CODE = 1030;
    private int mCardWidth, mCardHeight;
    private int mScreenWidth;

    public NewsDetailFgtAdapter(Context context, ArrayList<RelatedItemEntity> datas) {
        super(context, datas, new MultiItemTypeSupport<RelatedItemEntity>() {
            @Override
            public int getLayoutId(int position, RelatedItemEntity RelatedItemEntity) {
                switch (RelatedItemEntity.getStyle()) {
                    case 0:
                        return R.layout.qd_ll_news_item_no_pic;
                    case 1:
                        return R.layout.qd_ll_news_item_one_pic;
                    case 8:
                        return R.layout.ll_video_item_small;
                    default:
                        return R.layout.ll_news_item_empty;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 4;
            }

            @Override
            public int getItemViewType(int position, RelatedItemEntity RelatedItemEntity) {
                switch (RelatedItemEntity.getStyle()) {
                    case 0:
                        return RelatedItemEntity.NO_PIC;
                    case 1:
                        return RelatedItemEntity.ONE_AND_TWO_PIC;
                    case 8:
                        return RelatedItemEntity.VIDEO_SMALL;
                    default:
                        return RelatedItemEntity.EMPTY;
                }
            }
        });
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
    }


    @Override
    public void convert(CommonViewHolder holder, RelatedItemEntity relatedItemEntity, int position) {
        AdUtil.upLoadAd(relatedItemEntity, mContext);
        int layoutId = holder.getLayoutId();
        if (layoutId == R.layout.ll_news_item_empty) {
            holder.getView(R.id.news_content_relativeLayout).setVisibility(View.GONE);
        } else if (layoutId == R.layout.qd_ll_news_item_no_pic) {
            setTitleTextBySpannable((EllipsizeEndTextView) holder.getView(R.id.title_textView), relatedItemEntity.getTitle(), relatedItemEntity.isRead());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), relatedItemEntity.getPname());
            if (relatedItemEntity.getPtime() != null)
                setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), relatedItemEntity.getPtime());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), relatedItemEntity, (EllipsizeEndTextView) holder.getView(R.id.title_textView));
            TextUtil.setLayoutBgColor(mContext, (RelativeLayout) holder.getView(R.id.news_content_relativeLayout), R.color.bg_detail);
            holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), relatedItemEntity.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
        } else if (layoutId == R.layout.qd_ll_news_item_one_pic) {
            final String strTitle = relatedItemEntity.getTitle();
            setTitleTextBySpannable((EllipsizeEndTextView) holder.getView(R.id.title_textView), strTitle, relatedItemEntity.isRead());
            ImageView ivCard = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = mCardWidth;
            lpCard.height = mCardHeight;
            ivCard.setLayoutParams(lpCard);
            holder.setGlideDrawViewURI(R.id.title_img_View, relatedItemEntity.getImgUrl(), mCardWidth, mCardHeight, relatedItemEntity.getRtype());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), relatedItemEntity.getPname());
            if (relatedItemEntity.getPtime() != null) {
                setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), relatedItemEntity.getPtime());
            }
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), relatedItemEntity, (EllipsizeEndTextView) holder.getView(R.id.title_textView));
            TextUtil.setLayoutBgColor(mContext, (RelativeLayout) holder.getView(R.id.news_content_relativeLayout), R.color.bg_detail);
            holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), relatedItemEntity.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            final EllipsizeEndTextView tvTitle = holder.getView(R.id.title_textView);
            final LinearLayout llSourceOnePic = holder.getView(R.id.source_content_linearLayout);
            final ImageView ivBottomLine = holder.getView(R.id.line_bottom_imageView);
            tvTitle.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) llSourceOnePic.getLayoutParams();
                    RelativeLayout.LayoutParams lpBottomLine = (RelativeLayout.LayoutParams) ivBottomLine.getLayoutParams();
                    int lineCount = tvTitle.getLineCount();
                    if (lineCount >= 3) {
                        lpSourceContent.addRule(RelativeLayout.BELOW, R.id.title_img_View);
                        lpSourceContent.addRule(RelativeLayout.ALIGN_RIGHT, R.id.title_img_View);
                        lpSourceContent.topMargin = DensityUtil.dip2px(mContext, 6);
                        lpBottomLine.topMargin = DensityUtil.dip2px(mContext, 30);
                    } else {
                        lpSourceContent.addRule(RelativeLayout.BELOW, R.id.title_textView);
                        lpSourceContent.addRule(RelativeLayout.ALIGN_RIGHT, R.id.title_textView);
                        lpSourceContent.topMargin = DensityUtil.dip2px(mContext, 6);
                        lpBottomLine.topMargin = DensityUtil.dip2px(mContext, 12);
                    }
                    llSourceOnePic.setLayoutParams(lpSourceContent);
                    ivBottomLine.setLayoutParams(lpBottomLine);
                }
            });
        } else if (layoutId == R.layout.ll_video_item_small) {
            setTitleTextBySpannable((EllipsizeEndTextView) holder.getView(R.id.title_textView), relatedItemEntity.getTitle(), relatedItemEntity.isRead());
            ImageView ivVideoSmall = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpVideoSmall = (RelativeLayout.LayoutParams) ivVideoSmall.getLayoutParams();
            lpVideoSmall.width = mCardWidth;
            lpVideoSmall.height = mCardHeight;
            ivVideoSmall.setLayoutParams(lpVideoSmall);
            holder.setGlideDrawViewURI(R.id.title_img_View, relatedItemEntity.getImgUrl(), 0, 0, relatedItemEntity.getRtype());
            //item点击事件跳转到详情页播放
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), relatedItemEntity, (EllipsizeEndTextView) holder.getView(R.id.title_textView));
            TextUtil.setLayoutBgColor(mContext, (RelativeLayout) holder.getView(R.id.news_content_relativeLayout), R.color.bg_detail);
            holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
            setVideoDuration((TextView) holder.getView(R.id.tv_video_duration), relatedItemEntity.getDuration());
            if (relatedItemEntity.getPtime() != null) {
                setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), relatedItemEntity.getPtime());
            }
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), relatedItemEntity.getPname());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), relatedItemEntity.getRtype());
            final EllipsizeEndTextView tvTitle = holder.getView(R.id.title_textView);
            final LinearLayout llSourceOnePic = holder.getView(R.id.source_content_linearLayout);
            final ImageView ivBottomLine = holder.getView(R.id.line_bottom_imageView);
            tvTitle.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) llSourceOnePic.getLayoutParams();
                    RelativeLayout.LayoutParams lpBottomLine = (RelativeLayout.LayoutParams) ivBottomLine.getLayoutParams();
                    int lineCount = tvTitle.getLineCount();
                    if (lineCount >= 3) {
                        lpSourceContent.addRule(RelativeLayout.BELOW, R.id.title_img_View);
                        lpSourceContent.addRule(RelativeLayout.ALIGN_RIGHT, R.id.title_img_View);
                        lpSourceContent.topMargin = DensityUtil.dip2px(mContext, 6);
                        lpBottomLine.topMargin = DensityUtil.dip2px(mContext, 30);
                    } else {
                        lpSourceContent.addRule(RelativeLayout.BELOW, R.id.title_textView);
                        lpSourceContent.addRule(RelativeLayout.ALIGN_RIGHT, R.id.title_textView);
                        lpSourceContent.topMargin = DensityUtil.dip2px(mContext, 6);
                        lpBottomLine.topMargin = DensityUtil.dip2px(mContext, 12);
                    }
                    llSourceOnePic.setLayoutParams(lpSourceContent);
                    ivBottomLine.setLayoutParams(lpBottomLine);
                }
            });
            Log.i("tag", "Count" + getCount());
            int count = getCount();
            if (count != 0 && position == count - 1) {
                holder.getView(R.id.line_bottom_imageView).setVisibility(View.GONE);
            } else {
                holder.getView(R.id.line_bottom_imageView).setVisibility(View.VISIBLE);
            }
        }
    }

    public void setVideoDuration(TextView durationView, int duration) {
        if (duration != 0) {
            String time = TextUtil.secToTime(duration);
            durationView.setText(time);
        } else {
            durationView.setText("");
        }
    }

    private void setNewsTime(TextViewExtend tvComment, String updateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(updateTime);
            long between = System.currentTimeMillis() - date.getTime();
            if (between >= (24 * 3600000)) {
                tvComment.setText("");
            } else if (between < (24 * 3600000) && between >= (1 * 3600000)) {
                tvComment.setText("");
            } else {
                int time = (int) (between * 60 / 3600000);
                if (time > 0) {
                    tvComment.setText(between * 60 / 3600000 + "分钟前");
                } else if (time <= 0) {
                    tvComment.setText("");
                } else {
                    tvComment.setText(between * 60 * 60 / 3600000 + "秒前");
                }
            }
        } catch (ParseException e) {
            tvComment.setText(updateTime);
            e.printStackTrace();
        }

    }

    private void setTitleTextBySpannable(EllipsizeEndTextView tvTitle, String strTitle, boolean isRead) {
        if (strTitle != null && !"".equals(strTitle)) {
            tvTitle.setMaxLines(2);
            strTitle = strTitle.replace("<font color='#0091fa' >", "").replace("</font>", "");
            tvTitle.setText(strTitle);
            if (isRead) {
                TextUtil.setTextColor(mContext, tvTitle, R.color.new_color7);
            } else {
                TextUtil.setTextColor(mContext, tvTitle, R.color.newsFeed_titleColor);
            }
        }
    }

    private void setSourceViewText(TextViewExtend textView, String strText) {
        if (strText != null && !"".equals(strText)) {
            textView.setText(strText);
            TextUtil.setTextColor(mContext, textView, R.color.new_color3);
        }
    }

    private void setBottomLineColor(ImageView imageView) {
        TextUtil.setLayoutBgResource(mContext, imageView, R.drawable.list_divider);
    }

    private void setRead(RelatedItemEntity relatedItemEntity) {
        if (!relatedItemEntity.isRead()) {
            relatedItemEntity.setRead(true);
        }
    }

    /**
     * item的点击事件
     *
     * @param rlNewsContent
     * @param relatedItemEntity
     */
    private void setNewsContentClick(final RelativeLayout rlNewsContent, final RelatedItemEntity relatedItemEntity, final EllipsizeEndTextView tvTitle) {
        TextUtil.setLayoutBgResource(mContext, rlNewsContent, R.drawable.bg_feed_list_select);
        final float[] down_x = new float[1];
        final float[] down_y = new float[1];
        final float[] up_x = new float[1];
        final float[] up_y = new float[1];
        rlNewsContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (relatedItemEntity.getRtype() == 3) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            down_x[0] = motionEvent.getX(0);
                            down_y[0] = rlNewsContent.getY() + motionEvent.getY(0);
                            break;
                        case MotionEvent.ACTION_UP:
                            up_x[0] = motionEvent.getX(0);
                            up_y[0] = rlNewsContent.getY() + motionEvent.getY(0);
                            break;
                    }
                }
                return false;
            }
        });
        rlNewsContent.setOnClickListener(new View.OnClickListener() {
            long firstClick = 0;

            public void onClick(View paramAnonymousView) {
                if (System.currentTimeMillis() - firstClick <= 1500L) {
                    firstClick = System.currentTimeMillis();
                    return;
                }
                firstClick = System.currentTimeMillis();
                int type = relatedItemEntity.getRtype();
                if (type == 3) {
                    AdUtil.upLoadContentClick(relatedItemEntity, mContext, down_x[0], down_y[0], up_x[0], up_y[0]);
                } else if (relatedItemEntity.getRtype() == 6) {
                    setRead(relatedItemEntity);
                    Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
                    intent.putExtra(NewsDetailFgt.KEY_NEWS_ID, relatedItemEntity.getNid() + "");
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                } else {
                    setRead(relatedItemEntity);
                    Intent intent = new Intent(mContext, NewsDetailAty2.class);
                    intent.putExtra(NewsDetailFgt.KEY_NEWS_ID, relatedItemEntity.getNid() + "");
                    mContext.startActivity(intent);
                }
                if (relatedItemEntity.isRead()) {
                    TextUtil.setTextColor(mContext, tvTitle, R.color.new_color7);
                } else {
                    TextUtil.setTextColor(mContext, tvTitle, R.color.newsFeed_titleColor);
                }
            }
        });
    }

    /**
     * 新闻标签的样式（rtype   0普通新闻(不用显示标识)、1热点、2推送、3广告）
     *
     * @param tag
     * @param type
     */
    public void newsTag(TextViewExtend tag, int type) {
        String content = "";
        if (type == 1) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "热点";
            tag.setTextColor(mContext.getResources().getColor(R.color.newsfeed_red));
            tag.setBackgroundResource(R.drawable.newstag_hotspot_shape);
        } else if (type == 2) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "推送";
            tag.setTextColor(mContext.getResources().getColor(R.color.color1));
            tag.setBackgroundResource(R.drawable.newstag_push_shape);
        } else if (type == 3) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "广告";
            tag.setTextColor(mContext.getResources().getColor(R.color.new_color3));
            tag.setBackgroundResource(R.drawable.newstag_ad_shape);
        } else if (type == 4) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "专题";
            tag.setTextColor(mContext.getResources().getColor(R.color.newsfeed_red));
            tag.setBackgroundResource(R.drawable.newstag_hotspot_shape);
        } else if (type == 6) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "视频";
            tag.setTextColor(mContext.getResources().getColor(R.color.newsfeed_red));
            tag.setBackgroundResource(R.drawable.newstag_hotspot_shape);
        } else {
            if (tag.getVisibility() == View.VISIBLE) {
                tag.setVisibility(View.GONE);
            }
            return;
        }
        tag.setText(content);
        tag.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tag.getLayoutParams();
        params.width = DensityUtil.dip2px(mContext, 20);
        params.height = DensityUtil.dip2px(mContext, 11);
        tag.setLayoutParams(params);
    }
}
