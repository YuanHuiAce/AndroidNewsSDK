package com.news.sdk.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.news.sdk.R;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.adapter.abslistview.MultiItemCommonAdapter;
import com.news.sdk.adapter.abslistview.MultiItemTypeSupport;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.entity.RelatedItemEntity;
import com.news.sdk.pages.NewsDetailAty2;
import com.news.sdk.pages.NewsDetailFgt;
import com.news.sdk.pages.NewsDetailVideoAty;
import com.news.sdk.utils.AdUtil;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.widget.TextViewExtend;
import com.qq.e.ads.nativ.NativeADDataRef;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/4/22.
 */
public class NewsDetailFgtAdapter extends MultiItemCommonAdapter<RelatedItemEntity> {

    private Context mContext;
    public static final int REQUEST_CODE = 1030;
    private int mCardWidth, mCardHeight;
    private int mScreenWidth;
    private SharedPreferences mSharedPreferences;
    private RequestManager mRequestManager;

    public NewsDetailFgtAdapter(Context context, ArrayList<RelatedItemEntity> datas) {
        super(context, datas, new MultiItemTypeSupport<RelatedItemEntity>() {
            @Override
            public int getLayoutId(int position, RelatedItemEntity RelatedItemEntity) {
                switch (RelatedItemEntity.getStyle()) {
                    case 0:
                        return R.layout.qd_ll_news_item_no_pic;
                    case 1:
                        return R.layout.qd_ll_news_item_one_pic;
                    case 50:
                        return R.layout.ad_ll_news_item_one_pic;
                    case 8:
                        return R.layout.ll_video_item_small;
                    default:
                        return R.layout.ll_news_item_empty;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 5;
            }

            @Override
            public int getItemViewType(int position, RelatedItemEntity RelatedItemEntity) {
                switch (RelatedItemEntity.getStyle()) {
                    case 0:
                        return RelatedItemEntity.NO_PIC;
                    case 1:
                        return RelatedItemEntity.ONE_AND_TWO_PIC;
                    case 50:
                        return RelatedItemEntity.AD_ONE_PIC;
                    case 8:
                        return RelatedItemEntity.VIDEO_SMALL;
                    default:
                        return RelatedItemEntity.EMPTY;
                }
            }
        });
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mRequestManager = Glide.with(mContext);
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
    }


    @Override
    public void convert(CommonViewHolder holder, RelatedItemEntity relatedItemEntity, int position) {
        AdUtil.upLoadFeedAd(relatedItemEntity, mContext);
        int layoutId = holder.getLayoutId();
        if (layoutId == R.layout.ll_news_item_empty) {
            holder.getView(R.id.news_content_relativeLayout).setVisibility(View.GONE);
        } else if (layoutId == R.layout.qd_ll_news_item_no_pic) {
            setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), relatedItemEntity.getTitle(), relatedItemEntity.isRead());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), relatedItemEntity.getPname());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), relatedItemEntity, (TextView) holder.getView(R.id.title_textView));
            holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
            holder.getView(R.id.icon_source).setVisibility(View.GONE);
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), relatedItemEntity.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
        } else if (layoutId == R.layout.qd_ll_news_item_one_pic) {
            final String strTitle = relatedItemEntity.getTitle();
            setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), strTitle, relatedItemEntity.isRead());
            ImageView ivCard = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = mCardWidth;
            lpCard.height = mCardHeight;
            ivCard.setLayoutParams(lpCard);
            holder.setGlideDrawViewURI(mRequestManager, R.id.title_img_View, relatedItemEntity.getImgUrl(), mCardWidth, mCardHeight, relatedItemEntity.getRtype());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), relatedItemEntity.getPname());
            holder.getView(R.id.icon_source).setVisibility(View.GONE);
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), relatedItemEntity, (TextView) holder.getView(R.id.title_textView));
            holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), relatedItemEntity.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            final TextView tvTitle = holder.getView(R.id.title_textView);
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
        } else if (layoutId == R.layout.ad_ll_news_item_one_pic) {
            NativeADDataRef dataRef = relatedItemEntity.getDataRef();
            if (dataRef != null) {
                dataRef.onExposured(holder.getView(R.layout.ad_ll_news_item_one_pic));
            }
            int cardWidth = mCardWidth;
            int cardHeight = (int) (mCardWidth * 9 / 16.0f);
            final String strTitle = relatedItemEntity.getTitle();
            setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), strTitle, relatedItemEntity.isRead());
            ImageView ivCard = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = cardWidth;
            lpCard.height = cardHeight;
            ivCard.setLayoutParams(lpCard);
            if (!TextUtil.isEmptyString(relatedItemEntity.getImgUrl())) {
                ImageUtil.setAlphaImage((ImageView) holder.getView(R.id.title_img_View));
                mRequestManager.load(Uri.parse(relatedItemEntity.getImgUrl())).diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) holder.getView(R.id.title_img_View));
            }
            holder.getView(R.id.icon_source).setVisibility(View.GONE);
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), relatedItemEntity.getPname());
            holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            holder.getView(R.id.icon_source).setVisibility(View.GONE);
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), relatedItemEntity, (TextView) holder.getView(R.id.title_textView));
            holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), relatedItemEntity.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
        } else if (layoutId == R.layout.ll_video_item_small) {
            setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), relatedItemEntity.getTitle(), relatedItemEntity.isRead());
            ImageView ivVideoSmall = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpVideoSmall = (RelativeLayout.LayoutParams) ivVideoSmall.getLayoutParams();
            lpVideoSmall.width = mCardWidth;
            lpVideoSmall.height = mCardHeight;
            ivVideoSmall.setLayoutParams(lpVideoSmall);
            holder.setGlideDrawViewURI(mRequestManager, R.id.title_img_View, relatedItemEntity.getImgUrl(), 0, 0, relatedItemEntity.getRtype());
            //item点击事件跳转到详情页播放
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), relatedItemEntity, (TextView) holder.getView(R.id.title_textView));
            holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
            setVideoDuration((TextView) holder.getView(R.id.tv_video_duration), relatedItemEntity.getDuration());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), relatedItemEntity.getPname());
//            newsTag((TextViewExtend) holder.getView(R.id.type_textView), relatedItemEntity.getRtype());
            final TextView tvTitle = holder.getView(R.id.title_textView);
            final LinearLayout llSourceOnePic = holder.getView(R.id.source_content_linearLayout);
            final ImageView ivBottomLine = holder.getView(R.id.line_bottom_imageView);
            tvTitle.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) llSourceOnePic.getLayoutParams();
                    RelativeLayout.LayoutParams lpBottomLine = (RelativeLayout.LayoutParams) ivBottomLine.getLayoutParams();
                    int lineCount = tvTitle.getLineCount();
//                    if (lineCount >= 3) {
//                        lpSourceContent.addRule(RelativeLayout.BELOW, R.id.title_img_View);
//                        lpSourceContent.addRule(RelativeLayout.ALIGN_RIGHT, R.id.title_img_View);
//                        lpSourceContent.topMargin = DensityUtil.dip2px(mContext, 6);
//                        lpBottomLine.topMargin = DensityUtil.dip2px(mContext, 30);
//                    } else {
                    lpSourceContent.addRule(RelativeLayout.BELOW, R.id.title_textView);
                    lpSourceContent.addRule(RelativeLayout.ALIGN_RIGHT, R.id.title_textView);
                    lpSourceContent.topMargin = DensityUtil.dip2px(mContext, 6);
                    lpBottomLine.topMargin = DensityUtil.dip2px(mContext, 12);
//                    }
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

    private void setTitleTextBySpannable(TextView tvTitle, String strTitle, boolean isRead) {
        if (strTitle != null && !"".equals(strTitle)) {
            strTitle = strTitle.replace("<font color='#0091fa' >", "").replace("</font>", "");
            tvTitle.setText(strTitle);
            if (isRead) {
                TextUtil.setTextColor(mContext, tvTitle, R.color.color3);
            } else {
                TextUtil.setTextColor(mContext, tvTitle, R.color.color2);
            }
            tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        }
    }

    private void setSourceViewText(TextViewExtend textView, String strText) {
        if (strText != null && !"".equals(strText)) {
            textView.setText(strText);
            TextUtil.setTextColor(mContext, textView, R.color.color3);
        }
    }

    private void setBottomLineColor(ImageView imageView) {
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        layout.height = 1;
        layout.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        layout.leftMargin = DensityUtil.dip2px(mContext, 15);
        layout.rightMargin = DensityUtil.dip2px(mContext, 15);
        imageView.setLayoutParams(layout);
        TextUtil.setLayoutBgResource(mContext, imageView, R.color.color5);
    }

    private void setNewsFeedReadAndUploadUserAction(RelatedItemEntity relatedItemEntity, String formPage, String toPage) {
        int nid = relatedItemEntity.getNid();
        JSONObject jsonObject = new JSONObject();
        if (nid != 0) {
            try {
                jsonObject.put("chid", Long.valueOf(nid));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!relatedItemEntity.isRead()) {
            relatedItemEntity.setRead(true);
            LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_RELATECLICK, formPage, toPage, relatedItemEntity.getNid(), true);
        } else {
            LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_RELATECLICK, formPage, toPage, relatedItemEntity.getNid(), false);
        }
    }

    /**
     * item的点击事件
     *
     * @param rlNewsContent
     * @param relatedItemEntity
     */
    private void setNewsContentClick(final RelativeLayout rlNewsContent, final RelatedItemEntity relatedItemEntity, final TextView tvTitle) {
        TextUtil.setLayoutBgResource(mContext, rlNewsContent, R.drawable.bg_detail_list_select);
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
                if (relatedItemEntity.getDataRef() != null) {
                    LogUtil.adClickLog(Long.valueOf(CommonConstant.NEWS_DETAIL_GDT_SDK_BIGPOSID), mContext, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE, relatedItemEntity.getPname());
                    NativeADDataRef dataRef = relatedItemEntity.getDataRef();
                    dataRef.onClicked(rlNewsContent);
                    return;
                }
                int type = relatedItemEntity.getRtype();
                if (type == 3) {
                    LogUtil.adClickLog(Long.valueOf(CommonConstant.NEWS_RELATE_GDT_API_SMALLID), mContext, CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE, relatedItemEntity.getPname());
                    AdUtil.upLoadContentClick(relatedItemEntity.getAdDetailEntity(), mContext, down_x[0], down_y[0], up_x[0], up_y[0]);
                } else if (relatedItemEntity.getRtype() == 6) {
                    setNewsFeedReadAndUploadUserAction(relatedItemEntity, CommonConstant.LOG_PAGE_VIDEODETAILPAGE, CommonConstant.LOG_PAGE_VIDEODETAILPAGE);
                    Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
                    intent.putExtra(CommonConstant.KEY_SOURCE, CommonConstant.LOG_CLICK_RELATE_SOURCE);
                    intent.putExtra(NewsDetailFgt.KEY_NEWS_ID, relatedItemEntity.getNid() + "");
                    ((Activity) mContext).startActivityForResult(intent, 1060);
                    ((Activity) mContext).finish();
                    ((Activity) mContext).overridePendingTransition(0, 0);
                } else {
                    setNewsFeedReadAndUploadUserAction(relatedItemEntity, CommonConstant.LOG_PAGE_DETAILPAGE, CommonConstant.LOG_PAGE_DETAILPAGE);
                    Intent intent = new Intent(mContext, NewsDetailAty2.class);
                    intent.putExtra(CommonConstant.KEY_SOURCE, CommonConstant.LOG_CLICK_RELATE_SOURCE);
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
            TextUtil.setTextColor(mContext, tag, R.color.color7);
            TextUtil.setLayoutBgResource(mContext, tag, R.drawable.newstag_hotspot_shape);
        } else if (type == 2) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "推送";
            TextUtil.setTextColor(mContext, tag, R.color.color1);
            TextUtil.setLayoutBgResource(mContext, tag, R.drawable.newstag_push_shape);
        } else if (type == 3) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "广告";
            TextUtil.setTextColor(mContext, tag, R.color.color3);
            TextUtil.setLayoutBgResource(mContext, tag, R.drawable.newstag_ad_shape);
        } else if (type == 4) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "专题";
            TextUtil.setTextColor(mContext, tag, R.color.color8);
            TextUtil.setLayoutBgResource(mContext, tag, R.drawable.newstag_video_shape);
        } else if (type == 6) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "视频";
            TextUtil.setTextColor(mContext, tag, R.color.color8);
            TextUtil.setLayoutBgResource(mContext, tag, R.drawable.newstag_video_shape);
        } else {
            if (tag.getVisibility() == View.VISIBLE) {
                tag.setVisibility(View.GONE);
            }
            return;
        }
        tag.setText(content);
//        tag.setGravity(Gravity.CENTER_VERTICAL);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tag.getLayoutParams();
//        params.width = DensityUtil.dip2px(mContext, 24);
//        params.height = DensityUtil.dip2px(mContext, 11);
//        tag.setLayoutParams(params);
    }
}
