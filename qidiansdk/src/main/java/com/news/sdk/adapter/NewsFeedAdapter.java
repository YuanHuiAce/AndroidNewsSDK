package com.news.sdk.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.news.sdk.R;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.adapter.abslistview.MultiItemCommonAdapter;
import com.news.sdk.adapter.abslistview.MultiItemTypeSupport;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.database.NewsFeedDao;
import com.news.sdk.entity.AttentionListEntity;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.pages.AttentionActivity;
import com.news.sdk.pages.NewsDetailAty2;
import com.news.sdk.pages.NewsDetailVideoAty;
import com.news.sdk.pages.NewsFeedFgt;
import com.news.sdk.pages.NewsTopicAty;
import com.news.sdk.pages.SubscribeListActivity;
import com.news.sdk.utils.AdUtil;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.manager.PlayerManager;
import com.news.sdk.widget.TextViewExtend;
import com.qq.e.ads.nativ.NativeADDataRef;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NewsFeedAdapter extends MultiItemCommonAdapter<NewsFeed> {

    private final NewsFeedFgt mNewsFeedFgt;
    private String mstrKeyWord;
    private int mScreenWidth;
    private Context mContext;
    public static String KEY_URL = "key_url";
    public static String KEY_NEWS_ID = "key_news_id";
    public static int REQUEST_CODE = 10002;
    private SharedPreferences mSharedPreferences;
    private NewsFeedDao mNewsFeedDao;
    private int mCardWidth, mCardHeight;
    private introductionNewsFeed mIntroductionNewsFeed;
    private boolean isFavorite, isCkeckVisity;
    private boolean isNeedShowDisLikeIcon = true;
    private boolean isAttention;
    private RequestManager mRequestManager;

    public NewsFeedAdapter(Context context, NewsFeedFgt newsFeedFgt, ArrayList<NewsFeed> datas) {
        super(context, datas, new MultiItemTypeSupport<NewsFeed>() {
            @Override
            public int getLayoutId(int position, NewsFeed newsFeed) {
                switch (newsFeed.getStyle()) {
                    case 0:
                        return R.layout.qd_ll_news_item_no_pic;
                    case 1:
                    case 2:
                        return R.layout.qd_ll_news_item_one_pic;
                    case 3:
                        return R.layout.qd_ll_news_card;
                    case 900:
                        return R.layout.qd_ll_news_item_time_line;
                    case 4://奇点号Item
                        return R.layout.ll_news_search_item;
                    case 5:
                        return R.layout.ll_news_item_topic;
                    //视频播放列表，可以在列表播放
                    case 6:
                        return R.layout.ll_video_item_player;
                    //item视频布局不能在列表播放，可以在其他列表出现
                    case 8:
                        return R.layout.ll_video_item_big;
                    case 11://大图Item
                    case 12:
                    case 13:
                        return R.layout.ll_news_big_pic2;
                    case 50:
                        return R.layout.ad_ll_news_item_one_pic;
                    case 51:
                        return R.layout.ad_ll_news_big_pic2;
                    default:
                        return R.layout.ll_news_item_empty;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 12;
            }

            @Override
            public int getItemViewType(int position, NewsFeed newsFeed) {
                switch (newsFeed.getStyle()) {
                    case 0:
                        return NewsFeed.NO_PIC;
                    case 1:
                    case 2:
                        return NewsFeed.ONE_AND_TWO_PIC;
                    case 3:
                        return NewsFeed.THREE_PIC;
                    case 900:
                        return NewsFeed.TIME_LINE;
                    case 4://奇点号Item
                        return NewsFeed.SERRCH_ITEM;
                    case 5:
                        return NewsFeed.TOPIC;
                    case 6:
                        return NewsFeed.VIDEO_PLAYER;
                    case 8:
                        return NewsFeed.VIDEO_SMALL;
                    case 11://大图Item
                    case 12:
                    case 13:
                        return NewsFeed.BIG_PIC;
                    case 50:
                        return NewsFeed.AD_ONE_PIC;
                    case 51:
                        return NewsFeed.AD_BIG_PIC;
                    default:
                        return NewsFeed.EMPTY;
                }
            }
        });
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        this.mNewsFeedFgt = newsFeedFgt;
        mRequestManager = Glide.with(mContext);
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mNewsFeedDao = new NewsFeedDao(mContext);
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 36)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 71 / 108.0f);
    }


    public void setSearchKeyWord(String pKeyWord) {
        mstrKeyWord = pKeyWord;
        mDatas = null;
    }

    public void isFavoriteList() {
        isFavorite = true;
        isNeedShowDisLikeIcon = false;
    }

    public void isAttention() {
        isAttention = true;
        isNeedShowDisLikeIcon = false;
    }

    @Override
    public void convert(final CommonViewHolder holder, final NewsFeed feed, int position) {
        //广告
        AdUtil.upLoadFeedAd(feed, mContext);
        boolean isVisble = feed.isVisble();
        if (!isVisble) {
            feed.setVisble(true);
            feed.setCtime(System.currentTimeMillis());
        }
        int layoutId = holder.getLayoutId();
        if (layoutId == R.layout.qd_ll_news_item_no_pic || layoutId == R.layout.qd_ll_news_item_one_pic || layoutId == R.layout.ll_news_big_pic2 || layoutId == R.layout.qd_ll_news_card || layoutId == R.layout.ll_video_item_player || layoutId == R.layout.ll_video_item_big) {
            if (isCkeckVisity) {
                holder.getView(R.id.checkFavoriteDelete_image).setVisibility(View.VISIBLE);
            } else {
                holder.getView(R.id.checkFavoriteDelete_image).setVisibility(View.GONE);
                holder.getImageView(R.id.checkFavoriteDelete_image).setImageResource(R.drawable.favorite_uncheck);
            }
            if (feed.isFavorite()) {
                holder.getImageView(R.id.checkFavoriteDelete_image).setImageResource(R.drawable.favorite_check);
            } else {
                holder.getImageView(R.id.checkFavoriteDelete_image).setImageResource(R.drawable.favorite_uncheck);
            }
            if (isFavorite) {
//                    holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
//                    holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
//                    holder.getView(R.id.line_bottom_imageView).setBackgroundColor(mContext.getResources().getColor(R.color.new_color5));
//                    if (getCount() == position + 1) {//去掉最后一条的线
//                        holder.getView(R.id.line_bottom_imageView).setVisibility(View.INVISIBLE);
//                    }
                if (holder.getView(R.id.icon_source) != null) {
                    holder.getView(R.id.icon_source).setVisibility(View.GONE);
                }
                ClickDeleteFavorite((ImageView) holder.getView(R.id.checkFavoriteDelete_image), feed);
            }
        }
        if (layoutId == R.layout.qd_ll_news_item_no_pic) {
            setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            holder.setGlideDrawViewURI(mRequestManager, R.id.icon_source, feed.getIcon());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed, (TextView) holder.getView(R.id.title_textView));
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            if (isAttention) {
                holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
            if (isFavorite) {
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
        } else if (layoutId == R.layout.ll_news_item_empty) {
            holder.getView(R.id.news_content_relativeLayout).setVisibility(View.GONE);
        } else if (layoutId == R.layout.qd_ll_news_item_one_pic) {
            final String strTitle = feed.getTitle();
            setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), strTitle, feed.isRead());
            ImageView ivCard = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = mCardWidth;
            lpCard.height = mCardHeight;
            ivCard.setLayoutParams(lpCard);
            holder.setGlideDrawViewURI(mRequestManager, R.id.title_img_View, feed.getImgs().get(0), mCardWidth, mCardHeight, feed.getRtype());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
            holder.setGlideDrawViewURI(mRequestManager, R.id.icon_source, feed.getIcon());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed, (TextView) holder.getView(R.id.title_textView));
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
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
            holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            if (isAttention) {
                holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
            if (isFavorite) {
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
        } else if (layoutId == R.layout.ll_news_big_pic2) {
            ArrayList<String> strArrBigImgUrl = feed.getImgs();
            int with = mScreenWidth - DensityUtil.dip2px(mContext, 30);
            int height = (int) (with * 186 / 330.0f);
            int num = feed.getStyle() - 11;
            ImageView ivBigPic = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpBigPic = (RelativeLayout.LayoutParams) ivBigPic.getLayoutParams();
            lpBigPic.width = with;
            lpBigPic.height = height;
            ivBigPic.setLayoutParams(lpBigPic);
            holder.setGlideDrawViewURI(mRequestManager, R.id.title_img_View, strArrBigImgUrl.get(num), with, height, feed.getRtype());
            setTitleTextByBigSpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false);
            LinearLayout llSourceBigPic = holder.getView(R.id.source_content_linearLayout);
            holder.setGlideDrawViewURI(mRequestManager, R.id.icon_source, feed.getIcon());
            setSourceViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.news_source_TextView), feed.getPname());
            setCommentViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.comment_num_textView), feed.getComment() + "");
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed, (TextView) holder.getView(R.id.title_textView));
            setDeleteClick((ImageView) llSourceBigPic.findViewById(R.id.delete_imageView), feed, holder.getConvertView());
            llSourceBigPic.findViewById(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            if (isAttention) {
                holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
            if (isFavorite) {
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
        } else if (layoutId == R.layout.qd_ll_news_card) {
            ArrayList<String> strArrImgUrl = feed.getImgs();
            setCardMargin((ImageView) holder.getView(R.id.image_card1), 15, 1, 3);
            setCardMargin((ImageView) holder.getView(R.id.image_card2), 1, 1, 3);
            setCardMargin((ImageView) holder.getView(R.id.image_card3), 1, 15, 3);
            holder.setGlideDrawViewURI(mRequestManager, R.id.image_card1, strArrImgUrl.get(0), mCardWidth, mCardHeight, feed.getRtype());
            holder.setGlideDrawViewURI(mRequestManager, R.id.image_card2, strArrImgUrl.get(1), mCardWidth, mCardHeight, feed.getRtype());
            holder.setGlideDrawViewURI(mRequestManager, R.id.image_card3, strArrImgUrl.get(2), mCardWidth, mCardHeight, feed.getRtype());
            setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
            holder.setGlideDrawViewURI(mRequestManager, R.id.icon_source, feed.getIcon());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed, (TextView) holder.getView(R.id.title_textView));
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            if (isAttention) {
                holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
            if (isFavorite) {
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
        } else if (layoutId == R.layout.ll_news_search_item) {
            final ArrayList<AttentionListEntity> attentionListEntities = feed.getAttentionListEntities();
            if (!TextUtil.isListEmpty(attentionListEntities)) {
                int size = attentionListEntities.size();
                if (size == 1) {
                    holder.setGlideDrawViewURI(mRequestManager, R.id.img_ll_news_search_item_iconOne, attentionListEntities.get(0).getIcon());
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrOne, attentionListEntities.get(0).getName());
                    holder.setSimpleDrawViewResource(R.id.img_ll_news_search_item_iconTwo, R.drawable.search_item_more);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrTwo, "更多");
                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(0), 0);
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenSubscribeListPage(attentionListEntities);
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setOnClickListener(null);
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setOnClickListener(null);
                } else if (size == 2) {
                    holder.setGlideDrawViewURI(mRequestManager, R.id.img_ll_news_search_item_iconOne, attentionListEntities.get(0).getIcon(), 0);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrOne, attentionListEntities.get(0).getName());
                    holder.setGlideDrawViewURI(mRequestManager, R.id.img_ll_news_search_item_iconTwo, attentionListEntities.get(1).getIcon(), 1);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrTwo, attentionListEntities.get(1).getName());
                    holder.setSimpleDrawViewResource(R.id.img_ll_news_search_item_iconThree, R.drawable.search_item_more);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrThree, "更多");
                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(0), 0);
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(1), 1);
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenSubscribeListPage(attentionListEntities);
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setOnClickListener(null);
                } else if (size >= 3) {
                    holder.setGlideDrawViewURI(mRequestManager, R.id.img_ll_news_search_item_iconOne, attentionListEntities.get(0).getIcon(), 0);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrOne, attentionListEntities.get(0).getName());
                    holder.setGlideDrawViewURI(mRequestManager, R.id.img_ll_news_search_item_iconTwo, attentionListEntities.get(1).getIcon(), 1);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrTwo, attentionListEntities.get(1).getName());
                    holder.setGlideDrawViewURI(mRequestManager, R.id.img_ll_news_search_item_iconThree, attentionListEntities.get(2).getIcon(), 2);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrThree, attentionListEntities.get(2).getName());
                    holder.setSimpleDrawViewResource(R.id.img_ll_news_search_item_iconFour, R.drawable.search_item_more);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrFour, "更多");
                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(0), 0);
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(1), 1);
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(2), 2);
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenSubscribeListPage(attentionListEntities);
                        }
                    });
                }
            }
        } else if (layoutId == R.layout.ll_news_item_topic) {
            ImageView ivTopic = holder.getView(R.id.title_img_View);
            int ivWidth = mScreenWidth - DensityUtil.dip2px(mContext, 30);
            ArrayList<String> imgs = feed.getImgs();
            if (TextUtil.isListEmpty(imgs)) {
                holder.getView(R.id.title_img_View).setVisibility(View.GONE);
            } else {
                holder.getView(R.id.title_img_View).setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams lpTopic = (RelativeLayout.LayoutParams) ivTopic.getLayoutParams();
                lpTopic.width = ivWidth;
                lpTopic.height = (int) (ivWidth * 76 / 310.0f);
                ivTopic.setLayoutParams(lpTopic);
                holder.setGlideDrawViewURI(mRequestManager, R.id.title_img_View, imgs.get(0), 0, 0, feed.getRtype());
            }
            setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
//            holder.setGlideDrawViewURI(mRequestManager, R.id.icon_source, feed.getIcon());
            holder.getView(R.id.icon_source).setVisibility(View.GONE);
            holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed, (TextView) holder.getView(R.id.title_textView));
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
//            if (position == 0) {
//                holder.getView(R.id.top_image).setVisibility(View.VISIBLE);
//            } else {
//                holder.getView(R.id.top_image).setVisibility(View.GONE);
//            }
            holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            if (isAttention) {
                holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
            if (isFavorite) {
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
        } else if (layoutId == R.layout.qd_ll_news_item_time_line) {
            TextUtil.setLayoutBgResource(mContext, holder.getView(R.id.news_content_relativeLayout), R.color.color6);
            TextUtil.setTextColor(mContext, (TextView) holder.getView(R.id.title_textView), R.color.color1);
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            holder.getView(R.id.news_content_relativeLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNewsFeedFgt.refreshData();
                }
            });
        } else if (layoutId == R.layout.ad_ll_news_item_one_pic) {
            NativeADDataRef dataRef = feed.getDataRef();
            if (dataRef != null) {
                dataRef.onExposured(holder.getView(R.layout.ad_ll_news_item_one_pic));
            }
            int cardWidth = mCardWidth;
            int cardHeight = (int) (mCardWidth * 61 / 109.0f);
            final String strTitle = feed.getTitle();
            setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), strTitle, feed.isRead());
            ImageView ivCard = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = cardWidth;
            lpCard.height = cardHeight;
            ivCard.setLayoutParams(lpCard);
            holder.setGlideDrawViewURI(mRequestManager, R.id.title_img_View, feed.getImgs().get(0), 0, 0, feed.getRtype());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
            holder.setGlideDrawViewURI(mRequestManager, R.id.icon_source, feed.getIcon());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed, (TextView) holder.getView(R.id.title_textView));
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
            if (isAttention) {
                holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
            if (isFavorite) {
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
        } else if (layoutId == R.layout.ad_ll_news_big_pic2) {
            NativeADDataRef dataRef = feed.getDataRef();
            if (dataRef != null) {
                dataRef.onExposured(holder.getView(R.layout.ad_ll_news_big_pic2));
            }
            ArrayList<String> strArrBigImgUrl = feed.getImgs();
            int with = mScreenWidth - DensityUtil.dip2px(mContext, 30);
            if (feed.getChannel() == 44) {
                with = mScreenWidth;
            }
            int height = (int) (with * 10 / 19.0f);
            ImageView ivBigPic = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpBigPic = (RelativeLayout.LayoutParams) ivBigPic.getLayoutParams();
            lpBigPic.width = with;
            lpBigPic.height = height;
            ivBigPic.setLayoutParams(lpBigPic);
            holder.setGlideDrawViewURI(mRequestManager, R.id.title_img_View, strArrBigImgUrl.get(0), with, height, feed.getRtype());
            setTitleTextByBigSpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false);
            LinearLayout llSourceBigPic = holder.getView(R.id.source_content_linearLayout);
            setSourceViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.news_source_TextView), feed.getPname());
            setCommentViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.comment_num_textView), feed.getComment() + "");
            holder.setGlideDrawViewURI(mRequestManager, R.id.icon_source, feed.getIcon());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed, (TextView) holder.getView(R.id.title_textView));
            llSourceBigPic.findViewById(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
            if (isAttention) {
                holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
            if (isFavorite) {
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
        } else if (layoutId == R.layout.ll_video_item_player) {
            setTitleTextByBigSpannable((TextView) holder.getView(R.id.tv_video_title), feed.getTitle(), feed.isRead());
            ImageView ivVideo = holder.getView(R.id.image_bg);
            RelativeLayout.LayoutParams lpVideo = (RelativeLayout.LayoutParams) ivVideo.getLayoutParams();
            lpVideo.width = mScreenWidth;
            lpVideo.height = (int) (mScreenWidth * 203 / 360.0f);
            ivVideo.setLayoutParams(lpVideo);
            holder.getView(R.id.layout_item_video).setLayoutParams(lpVideo);
            holder.setGlideDrawViewURI(mRequestManager, R.id.image_bg, feed.getThumbnail(), 0, 0, feed.getRtype());
            setCommentViewText((TextViewExtend) holder.getView(R.id.tv_video_comments), feed.getComment() + "");
            //点击评论跳转
            holder.getView(R.id.tv_video_comments).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCommentClick(feed);
                }
            });
            ImageUtil.setAlphaImage((ImageView) holder.getView(R.id.image_play), R.drawable.video_play);
            ImageUtil.setAlphaImage((ImageView) holder.getView(R.id.iv_video_share), R.drawable.video_share_black);

            setSourceViewText((TextViewExtend) holder.getView(R.id.tve_video_source_username), feed.getPname());
            holder.setGlideDrawViewURI(mRequestManager, R.id.iv_video_source_avatarHd, feed.getIcon());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            //视频播放
            setPlayClick((RelativeLayout) holder.getView(R.id.rl_video_show), position, feed);
            //item点击事件跳转到详情页播放
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed, (TextView) holder.getView(R.id.tv_video_title));
            setVideoDuration((TextView) holder.getView(R.id.tv_video_duration), feed.getDuration(), feed.getClicktimesStr());
            setShareClick((ImageView) holder.getView(R.id.iv_video_share), feed);
//            holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            if (isAttention) {
                holder.getView(R.id.tve_video_source_username).setVisibility(View.GONE);
                holder.getView(R.id.tv_video_comments).setVisibility(View.GONE);
            }
            if (PlayerManager.videoPlayView != null && feed.getNid() != PlayerManager.videoPlayView.cPostion) {
                FrameLayout view = holder.getView(R.id.layout_item_video);
                view.removeAllViews();
                holder.getView(R.id.rl_video_show).setVisibility(View.VISIBLE);
            }
        } else if (layoutId == R.layout.ll_video_item_big) {
            int width = mScreenWidth - DensityUtil.dip2px(mContext, 30);
            setTitleTextByBigSpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            ImageView ivVideoSmall = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpVideoSmall = (RelativeLayout.LayoutParams) ivVideoSmall.getLayoutParams();
            lpVideoSmall.width = width;
            lpVideoSmall.height = (int) (width * 185 / 330.0f);
            ivVideoSmall.setLayoutParams(lpVideoSmall);
            ImageUtil.setAlphaImage((ImageView) holder.getView(R.id.image_play), R.drawable.video_play);
            holder.setGlideDrawViewURI(mRequestManager, R.id.title_img_View, feed.getThumbnail(), 0, 0, feed.getRtype());
            holder.setGlideDrawViewURI(mRequestManager, R.id.icon_source, feed.getIcon());
            //item点击事件跳转到详情页播放
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed, (TextView) holder.getView(R.id.title_textView));
            setVideoDuration((TextView) holder.getView(R.id.tv_video_duration), feed.getDuration(), feed.getClicktimesStr());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            holder.getView(R.id.comment_num_textView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCommentClick(feed);
                }
            });
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            if (isAttention) {
                holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
            if (isFavorite) {
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
            }
        }
    }

    private void setOpenAttentionPage(AttentionListEntity attention, int index) {
        Intent attentionAty = new Intent(mContext, AttentionActivity.class);
        attentionAty.putExtra(CommonConstant.KEY_ATTENTION_CONPUBFLAG, attention.getFlag());
        attentionAty.putExtra(CommonConstant.KEY_ATTENTION_TITLE, attention.getName());
        attentionAty.putExtra(CommonConstant.KEY_ATTENTION_INDEX, index);
        ((Activity) mContext).startActivityForResult(attentionAty, CommonConstant.REQUEST_ATTENTION_CODE);
    }

    private void setOpenSubscribeListPage(ArrayList<AttentionListEntity> attentionListEntities) {
        Intent intent = new Intent(mContext, SubscribeListActivity.class);
        intent.putExtra(CommonConstant.KEY_SUBSCRIBE_LIST, attentionListEntities);
        ((Activity) mContext).startActivityForResult(intent, CommonConstant.REQUEST_SUBSCRIBE_LIST_CODE);
    }

    public void setVisitycheckFavoriteDeleteLayout(boolean isVisity) {
        isCkeckVisity = isVisity;
        notifyDataSetChanged();
    }

    public void setIntroductionNewsFeed(introductionNewsFeed mIntroductionNewsFeed) {
        this.mIntroductionNewsFeed = mIntroductionNewsFeed;
    }

    public void ClickDeleteFavorite(final ImageView checkDelete, final NewsFeed feed) {
        checkDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feed.isFavorite()) {
                    feed.setFavorite(false);
                    mIntroductionNewsFeed.getDate(feed, false);
                    checkDelete.setImageResource(R.drawable.favorite_uncheck);
                } else {
                    feed.setFavorite(true);
                    mIntroductionNewsFeed.getDate(feed, true);
                    checkDelete.setImageResource(R.drawable.favorite_check);
                }
            }
        });
    }

    private void setPlayClick(final RelativeLayout view, final int position, final NewsFeed feed) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlayClickListener != null) {
                    onPlayClickListener.onPlayClick(view, feed);
                }
            }
        });
    }

    public void setVideoDuration(TextView durationView, int duration, String clickNums) {
        durationView.setVisibility(View.VISIBLE);
        if (duration != 0) {
            String time = TextUtil.secToTime(duration);
            durationView.setText(time + clickNums);
        } else if (!TextUtils.isEmpty(clickNums) && !"".equals(clickNums)) {
            durationView.setText("" + clickNums);
        } else {
            durationView.setVisibility(View.GONE);
        }
        TextUtil.setTextColor(mContext, durationView, R.color.color10);
    }

    private void setShareClick(final ImageView imageView, final NewsFeed newsFeed) {
        if (onPlayClickListener != null) {
            onPlayClickListener.onShareClick(imageView, newsFeed);
        }
    }

    private void setCommentClick(NewsFeed newsFeed) {
        Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
        intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, newsFeed);
//        intent.putExtra(NewsFeedFgt.KEY_SHOW_COMMENT, true);
        if (mNewsFeedFgt != null) {
            mNewsFeedFgt.startActivityForResult(intent, REQUEST_CODE);
        } else {
            ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private void setCardMargin(ImageView ivCard, int leftMargin, int rightMargin, int pageNum) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) ivCard.getLayoutParams();
        localLayoutParams.leftMargin = DensityUtil.dip2px(mContext, leftMargin);
        localLayoutParams.rightMargin = DensityUtil.dip2px(mContext, rightMargin);
        int width = (int) (mScreenWidth / 2.0f - DensityUtil.dip2px(mContext, 15));
        if (pageNum == 2) {
            localLayoutParams.width = width;
            localLayoutParams.height = (int) (width * 74 / 102f);
        } else if (pageNum == 3) {
            localLayoutParams.width = mCardWidth;
            localLayoutParams.height = mCardHeight;
        }
        ivCard.setLayoutParams(localLayoutParams);
    }

    private void setNewsTime(TextViewExtend tvComment, String updateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = dateFormat.getCalendar();
        try {
            Date date = dateFormat.parse(updateTime);
            long between = System.currentTimeMillis() - date.getTime();
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (between >= (24 * 3600000)) {
                if (isAttention) {
                    tvComment.setText(month + "月" + day + "日");
                } else {
                    tvComment.setText("");
                }
            } else if (between < (24 * 3600000) && between >= (1 * 3600000)) {
                if (isAttention) {
                    tvComment.setText(between / 3600000 + "小时前");
                } else {
                    tvComment.setText("");
                }
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

    private void setTitleTextBySpannable(TextView tvTitle, String strTitle, boolean isRead) {
        if (!TextUtil.isEmptyString(strTitle)) {
            if (!TextUtil.isEmptyString(mstrKeyWord)) {
                if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY) {
                    strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toLowerCase() + "</font>");
                    strTitle = strTitle.replace(mstrKeyWord.toUpperCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toUpperCase() + "</font>");
                } else {
                    strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#1168a7\">" + mstrKeyWord.toLowerCase() + "</font>");
                    strTitle = strTitle.replace(mstrKeyWord.toUpperCase(), "<font color =\"#1168a7\">" + mstrKeyWord.toUpperCase() + "</font>");
                    strTitle = strTitle.replace("#0091fa", "#1168a7");
                }
                tvTitle.setText(Html.fromHtml(strTitle), TextView.BufferType.SPANNABLE);
            } else {
                tvTitle.setText(strTitle);
            }
            if (isRead) {
                TextUtil.setTextColor(mContext, tvTitle, R.color.color3);
            } else {
                TextUtil.setTextColor(mContext, tvTitle, R.color.color2);
            }
            tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        }
    }

    private void setTitleTextByBigSpannable(TextView tvTitle, String strTitle, boolean isRead) {
        if (!TextUtil.isEmptyString(strTitle)) {
            if (!TextUtil.isEmptyString(mstrKeyWord)) {
                if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY) {
                    strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toLowerCase() + "</font>");
                    strTitle = strTitle.replace(mstrKeyWord.toUpperCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toUpperCase() + "</font>");
                } else {
                    strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#1168a7\">" + mstrKeyWord.toLowerCase() + "</font>");
                    strTitle = strTitle.replace(mstrKeyWord.toUpperCase(), "<font color =\"#1168a7\">" + mstrKeyWord.toUpperCase() + "</font>");
                    strTitle = strTitle.replace("#0091fa", "#1168a7");
                }
                tvTitle.setText(Html.fromHtml(strTitle), TextView.BufferType.SPANNABLE);
            } else {
                tvTitle.setText(strTitle);
                tvTitle.setLineSpacing(0, 1.1f);
            }
            if (isRead) {
                TextUtil.setTextColor(mContext, tvTitle, R.color.color3);
            } else {
                TextUtil.setTextColor(mContext, tvTitle, R.color.color2);
            }
            tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        }
    }

    private void setSourceViewText(TextViewExtend textView, String strText) {
        if (!TextUtil.isEmptyString(strText)) {
            textView.setText(strText);
            TextUtil.setTextColor(mContext, textView, R.color.color3);
        }
    }

    private void setCommentViewText(TextViewExtend textView, String strText) {
        textView.setText(TextUtil.getCommentNum(strText));
        TextUtil.setTextColor(mContext, textView, R.color.color3);
    }

    private void setBottomLineColor(ImageView imageView) {
        TextUtil.setLayoutBgResource(mContext, imageView, R.color.color6);
    }

    /**
     * item的点击事件
     *
     * @param rlNewsContent
     * @param feed
     */
    private void setNewsContentClick(final RelativeLayout rlNewsContent, final NewsFeed feed, final TextView tvTitle) {
        TextUtil.setLayoutBgResource(mContext, rlNewsContent, R.drawable.bg_feed_list_select);
        final float[] down_x = new float[1];
        final float[] down_y = new float[1];
        final float[] up_x = new float[1];
        final float[] up_y = new float[1];
        rlNewsContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (feed.getRtype() == 3) {
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
                if (feed.getDataRef() != null) {
                    LogUtil.adClickLog(Long.valueOf(CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID), mContext, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE, feed.getPname());
                    NativeADDataRef dataRef = feed.getDataRef();
                    dataRef.onClicked(rlNewsContent);
                    return;
                }
                firstClick = System.currentTimeMillis();
                int type = feed.getRtype();
                if (type == 3) {
                    LogUtil.adClickLog(Long.valueOf(CommonConstant.NEWS_FEED_GDT_API_BIGPOSID), mContext, CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE, feed.getPname());
                    AdUtil.upLoadContentClick(feed.getAdDetailEntity(), mContext, down_x[0], down_y[0], up_x[0], up_y[0]);
                } else if (type == 4) {
                    setNewsFeedReadAndUploadUserAction(feed, CommonConstant.LOG_PAGE_TOPICPAGE);
                    Intent intent = new Intent(mContext, NewsTopicAty.class);
                    intent.putExtra(CommonConstant.KEY_SOURCE, CommonConstant.LOG_CLICK_FEED_SOURCE);
                    intent.putExtra(NewsTopicAty.KEY_NID, feed.getNid());
                    intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
                    mContext.startActivity(intent);
                } else if (feed.getRtype() == 6) {
                    setNewsFeedReadAndUploadUserAction(feed, CommonConstant.LOG_PAGE_VIDEODETAILPAGE);
                    if (onPlayClickListener != null) {
                        onPlayClickListener.onItemClick(rlNewsContent, feed);
                    }
                } else {
                    setNewsFeedReadAndUploadUserAction(feed, CommonConstant.LOG_PAGE_DETAILPAGE);
                    Intent intent = new Intent(mContext, NewsDetailAty2.class);
                    intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
                    intent.putExtra(CommonConstant.KEY_SOURCE, CommonConstant.LOG_CLICK_FEED_SOURCE);
                    ArrayList<String> imageList = feed.getImgs();
                    if (imageList != null && imageList.size() != 0) {
                        intent.putExtra(NewsFeedFgt.KEY_NEWS_IMAGE, imageList.get(0));
                    }
                    if (mNewsFeedFgt != null) {
                        mNewsFeedFgt.startActivity(intent);
                    } else {
                        mContext.startActivity(intent);
                    }
                }
                if (feed.isRead()) {
                    TextUtil.setTextColor(mContext, tvTitle, R.color.color3);
                } else {
                    TextUtil.setTextColor(mContext, tvTitle, R.color.color2);
                }
            }
        });
    }

    clickShowPopWindow mClickShowPopWindow;

    public void setClickShowPopWindow(clickShowPopWindow mClickShowPopWindow) {
        this.mClickShowPopWindow = mClickShowPopWindow;
    }

    NewsFeed DeleteClickBean;
    View DeleteView;

    private void setDeleteClick(final ImageView imageView, final NewsFeed feed, final View view) {
        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
            imageView.setAlpha(0.5f);
        } else {
            imageView.setAlpha(1.0f);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteView = view;
                DeleteClickBean = feed;
                int[] LocationOnScreen = new int[2];
                int[] LocationInWindow = new int[2];
                imageView.getLocationInWindow(LocationInWindow);
                mClickShowPopWindow.showPopWindow(LocationInWindow[0] + imageView.getWidth() / 2, LocationInWindow[1] + imageView.getHeight() / 2,
                        feed);
            }
        });
    }

    private void setNewsFeedReadAndUploadUserAction(NewsFeed feed, String toPage) {
        JSONObject jsonObject = new JSONObject();
        try {
            int nid = feed.getNid();
            int logchid = feed.getLogchid();
            if (nid != 0) {
                jsonObject.put("nid", Long.valueOf(nid));
                jsonObject.put("logchid", logchid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!feed.isRead()) {
            feed.setRead(true);
            if (mNewsFeedDao == null) {
                mNewsFeedDao = new NewsFeedDao(mContext);
            }
            mNewsFeedDao.update(feed);
            LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_DETAILCLICK, CommonConstant.LOG_PAGE_FEEDPAGE, toPage, jsonObject, true);
        } else {
            LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_DETAILCLICK, CommonConstant.LOG_PAGE_FEEDPAGE, toPage, jsonObject, false);
        }
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
    }

    int height;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void disLikeDeleteItem() {
        final ViewWrapper wrapper = new ViewWrapper(DeleteView);
        height = DeleteView.getHeight();
        ObjectAnimator changeH = ObjectAnimator.ofInt(wrapper, "height", DeleteView.getHeight(), 0).setDuration(550);
        changeH.start();
        changeH.setInterpolator(new AccelerateInterpolator());
        changeH.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mNewsFeedDao.deleteOnceDate(DeleteClickBean);
//                ObjectAnimator.ofFloat(wrapper, "height", 0, deleteViewHeight).setDuration(0).start();
                ArrayList<NewsFeed> arrayList = getNewsFeed();
                arrayList.remove(DeleteClickBean);
                notifyDataSetChanged();
            }
        });
    }


    class ViewWrapper {
        private View mTarget;

        public ViewWrapper(View mTarget) {
            this.mTarget = mTarget;
        }

        public int getHeight() {
            int height = mTarget.getLayoutParams().height;
            return height;
        }

        public void setHeight(int height) {
            mTarget.getLayoutParams().height = height;
            mTarget.requestLayout();
        }
    }

    /**
     * 接口回调传入数据的添加与删除
     * <p/>
     * isCheck true：添加   false：删除
     */
    public interface introductionNewsFeed {
        public void getDate(NewsFeed feed, boolean isCheck);
    }

    public interface clickShowPopWindow {
        public void showPopWindow(int x, int y, NewsFeed feed);
    }


    //视频播放接口
    private OnPlayClickListener onPlayClickListener;

    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;

    }

    public interface OnPlayClickListener {
        void onPlayClick(RelativeLayout relativeLayout, NewsFeed feed);

        void onItemClick(RelativeLayout rlNewsContent, NewsFeed feed);

        void onShareClick(ImageView imageView, NewsFeed feed);
    }

}