package com.news.yazhidao.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.adapter.abslistview.MultiItemCommonAdapter;
import com.news.yazhidao.adapter.abslistview.MultiItemTypeSupport;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.pages.NewsTopicAty;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.widget.EllipsizeEndTextView;
import com.news.yazhidao.widget.TextViewExtend;
import com.qq.e.ads.nativ.NativeADDataRef;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private boolean isNeedShowDisLikeIcon = true;


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
//                    case 4://奇点号Item
//                        return R.layout.ll_news_search_item;
                    case 5:
                        return R.layout.ll_news_item_topic;
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
                return 9;
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
//                    case 4://奇点号Item
//                        return NewsFeed.SERRCH_ITEM;
                    case 5:
                        return NewsFeed.TOPIC;
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
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mNewsFeedDao = new NewsFeedDao(mContext);
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
    }


    public void setSearchKeyWord(String pKeyWord) {
        mstrKeyWord = pKeyWord;
        mDatas = null;
    }


    @Override
    public void convert(final CommonViewHolder holder, NewsFeed feed, int position) {
        //广告
        int layoutId = holder.getLayoutId();
        if (layoutId == R.layout.qd_ll_news_item_no_pic) {
            setTitleTextBySpannable((EllipsizeEndTextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            if (feed.getPtime() != null)
                setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
        } else if (layoutId == R.layout.ll_news_item_empty) {
            holder.getView(R.id.news_content_relativeLayout).setVisibility(View.GONE);
        } else if (layoutId == R.layout.qd_ll_news_item_one_pic) {
            holder.setGlideDraweeViewURI(R.id.title_img_View, feed.getImgs().get(0), mCardWidth, mCardHeight, feed.getRtype());
            final String strTitle = feed.getTitle();
            setTitleTextBySpannable((EllipsizeEndTextView) holder.getView(R.id.title_textView), strTitle, feed.isRead());
            ImageView ivCard = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = mCardWidth;
            lpCard.height = mCardHeight;
            ivCard.setLayoutParams(lpCard);
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            if (feed.getPtime() != null) {
                setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
            }
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            final EllipsizeEndTextView tvTitle = holder.getView(R.id.title_textView);
            final LinearLayout llSourceOnePic = holder.getView(R.id.source_content_linearLayout);
            final ImageView ivBottomLine = holder.getView(R.id.line_bottom_imageView);
            tvTitle.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) llSourceOnePic.getLayoutParams();
                    RelativeLayout.LayoutParams titleLp = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
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
        } else if (layoutId == R.layout.ll_news_big_pic2) {
            ArrayList<String> strArrBigImgUrl = feed.getImgs();
            int with = mScreenWidth - DensityUtil.dip2px(mContext, 30);
            int height = (int) (with * 9 / 16.0f);
            int num = feed.getStyle() - 11;
            holder.setGlideDraweeViewURI(R.id.title_img_View, strArrBigImgUrl.get(num), with, height, feed.getRtype());
            ImageView ivBigPic = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpBigPic = (RelativeLayout.LayoutParams) ivBigPic.getLayoutParams();
            lpBigPic.width = with;
            lpBigPic.height = height;
            ivBigPic.setLayoutParams(lpBigPic);
            setTitleTextByBigSpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false);
            LinearLayout llSourceBigPic = holder.getView(R.id.source_content_linearLayout);
            setSourceViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.news_source_TextView), feed.getPname());
            setCommentViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.comment_num_textView), feed.getComment() + "");
            if (feed.getPtime() != null)
                setNewsTime((TextViewExtend) llSourceBigPic.findViewById(R.id.comment_textView), feed.getPtime());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) llSourceBigPic.findViewById(R.id.delete_imageView), feed, holder.getConvertView());
            llSourceBigPic.findViewById(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
        } else if (layoutId == R.layout.qd_ll_news_card) {
            ArrayList<String> strArrImgUrl = feed.getImgs();
            setCardMargin((ImageView) holder.getView(R.id.image_card1), 15, 1, 3);
            setCardMargin((ImageView) holder.getView(R.id.image_card2), 1, 1, 3);
            setCardMargin((ImageView) holder.getView(R.id.image_card3), 1, 15, 3);
            holder.setGlideDraweeViewURI(R.id.image_card1, strArrImgUrl.get(0), mCardWidth, mCardHeight, feed.getRtype());
            holder.setGlideDraweeViewURI(R.id.image_card2, strArrImgUrl.get(1), mCardWidth, mCardHeight, feed.getRtype());
            holder.setGlideDraweeViewURI(R.id.image_card3, strArrImgUrl.get(2), mCardWidth, mCardHeight, feed.getRtype());
            setTitleTextBySpannable((EllipsizeEndTextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            if (feed.getPtime() != null)
                setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
        } else if (layoutId == R.layout.ll_news_item_topic) {
            ImageView ivTopic = holder.getView(R.id.title_img_View);
            int ivWidth = mScreenWidth - DensityUtil.dip2px(mContext, 30);
            RelativeLayout.LayoutParams lpTopic = (RelativeLayout.LayoutParams) ivTopic.getLayoutParams();
            lpTopic.width = ivWidth;
            lpTopic.height = (int) (ivWidth * 76 / 310.0f);
            ivTopic.setLayoutParams(lpTopic);
            holder.setGlideDraweeViewURI(R.id.title_img_View, feed.getImgs().get(0), 0, 0, feed.getRtype());
            setTitleTextBySpannable((EllipsizeEndTextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
//            if (position == 0) {
//                holder.getView(R.id.top_image).setVisibility(View.VISIBLE);
//            } else {
//                holder.getView(R.id.top_image).setVisibility(View.GONE);
//            }
        } else if (layoutId == R.layout.qd_ll_news_item_time_line) {
            TextUtil.setLayoutBgResource(mContext, (LinearLayout) holder.getView(R.id.news_content_relativeLayout), R.drawable.bg_feed_list_select);
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
            holder.getView(R.id.news_content_relativeLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNewsFeedFgt.refreshData();
                }
            });
        } else if (layoutId == R.layout.ad_ll_news_item_one_pic) {
            int cardWidth = (int) (mCardWidth * 145 / 110.0f);
            int cardHeight = (int) (cardWidth * 9 / 16.0f);
            holder.setGlideDraweeViewURI(R.id.title_img_View, feed.getImgs().get(0), 0, 0, feed.getRtype());
            final String strTitle = feed.getTitle();
            setTitleTextBySpannable((EllipsizeEndTextView) holder.getView(R.id.title_textView), strTitle, feed.isRead());
            ImageView ivCard = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = cardWidth;
            lpCard.height = cardHeight;
            ivCard.setLayoutParams(lpCard);
            setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
            setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
            if (feed.getPtime() != null) {
                setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
            }
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
        } else if (layoutId == R.layout.ad_ll_news_big_pic2) {
            ArrayList<String> strArrBigImgUrl = feed.getImgs();
            int with = mScreenWidth - DensityUtil.dip2px(mContext, 30);
            int height = (int) (with * 9 / 16.0f);
            holder.setGlideDraweeViewURI(R.id.title_img_View, strArrBigImgUrl.get(0), with, height, feed.getRtype());
            ImageView ivBigPic = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpBigPic = (RelativeLayout.LayoutParams) ivBigPic.getLayoutParams();
            lpBigPic.width = with;
            lpBigPic.height = height;
            ivBigPic.setLayoutParams(lpBigPic);
            setTitleTextByADBigSpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false);
            LinearLayout llSourceBigPic = holder.getView(R.id.source_content_linearLayout);
            setSourceViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.news_source_TextView), feed.getPname());
            setCommentViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.comment_num_textView), feed.getComment() + "");
            if (feed.getPtime() != null)
                setNewsTime((TextViewExtend) llSourceBigPic.findViewById(R.id.comment_textView), feed.getPtime());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) llSourceBigPic.findViewById(R.id.delete_imageView), feed, holder.getConvertView());
            llSourceBigPic.findViewById(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
            newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
            setBottomLineColor((ImageView) holder.getView(R.id.line_bottom_imageView));
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
            tvTitle.setMaxLines(3);
            if (mstrKeyWord != null && !"".equals(mstrKeyWord)) {
                strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toLowerCase() + "</font>");
                strTitle = strTitle.replace(mstrKeyWord.toUpperCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toUpperCase() + "</font>");
                tvTitle.setText(Html.fromHtml(strTitle), TextView.BufferType.SPANNABLE);
            } else {
                tvTitle.setText(strTitle);
//                tvTitle.setLineSpacing(0, 1f);
            }
            if (isRead) {
                TextUtil.setTextColor(mContext, tvTitle, R.color.new_color7);
            } else {
                TextUtil.setTextColor(mContext, tvTitle, R.color.newsFeed_titleColor);
            }
            tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        }
    }

    private void setTitleTextByBigSpannable(TextView tvTitle, String strTitle, boolean isRead) {
        if (strTitle != null && !"".equals(strTitle)) {
            if (mstrKeyWord != null && !"".equals(mstrKeyWord)) {
                strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toLowerCase() + "</font>");
                strTitle = strTitle.replace(mstrKeyWord.toUpperCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toUpperCase() + "</font>");
                tvTitle.setText(Html.fromHtml(strTitle), TextView.BufferType.SPANNABLE);
            } else {
                tvTitle.setText(strTitle);
                tvTitle.setLineSpacing(0, 1.1f);
            }
            if (isRead) {
                TextUtil.setTextColor(mContext, tvTitle, R.color.newsFeed_titleColorBig);
            } else {
                TextUtil.setTextColor(mContext, tvTitle, R.color.newsFeed_titleColorBig);
            }
            tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        }
    }

    private void setTitleTextByADBigSpannable(TextView tvTitle, String strTitle, boolean isRead) {
        if (strTitle != null && !"".equals(strTitle)) {
            if (mstrKeyWord != null && !"".equals(mstrKeyWord)) {
                strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toLowerCase() + "</font>");
                strTitle = strTitle.replace(mstrKeyWord.toUpperCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toUpperCase() + "</font>");
                tvTitle.setText(Html.fromHtml(strTitle), TextView.BufferType.SPANNABLE);
            } else {
                tvTitle.setText(strTitle);
                tvTitle.setLineSpacing(0, 1.1f);
            }
            if (isRead) {
                TextUtil.setTextColor(mContext, tvTitle, R.color.new_color7);
            } else {
                TextUtil.setTextColor(mContext, tvTitle, R.color.newsFeed_titleColor);
            }
            tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        }
    }


    private void setSourceViewText(TextViewExtend textView, String strText) {
        if (strText != null && !"".equals(strText)) {
            textView.setText(strText);
            TextUtil.setTextColor(mContext, textView, R.color.new_color3);
        }
    }

    private void setCommentViewText(TextViewExtend textView, String strText) {
        textView.setText(TextUtil.getCommentNum(strText));
        TextUtil.setTextColor(mContext, textView, R.color.new_color3);
    }

    private void setBottomLineColor(ImageView imageView) {
        TextUtil.setLayoutBgResource(mContext, imageView, R.drawable.list_divider);
    }

    /**
     * item的点击事件
     *
     * @param rlNewsContent
     * @param feed
     */
    private void setNewsContentClick(final RelativeLayout rlNewsContent, final NewsFeed feed) {
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
                    NativeADDataRef dataRef = feed.getDataRef();
                    dataRef.onExposured(rlNewsContent);
                    dataRef.onClicked(rlNewsContent);
                    return;
                }
                firstClick = System.currentTimeMillis();
                int type = feed.getRtype();
                if (type == 4) {
                    if (!feed.isRead()) {
                        feed.setRead(true);
                        if (mNewsFeedDao == null) {
                            mNewsFeedDao = new NewsFeedDao(mContext);
                        }
                        mNewsFeedDao.update(feed);
                        notifyDataSetChanged();
                    }
                    Intent AdIntent = new Intent(mContext, NewsTopicAty.class);
                    AdIntent.putExtra(NewsTopicAty.KEY_NID, feed.getNid());
                    if (mNewsFeedFgt != null) {
                        mNewsFeedFgt.startActivity(AdIntent);
                    } else {
                        (mContext).startActivity(AdIntent);
                    }
                } else {
                    if (!feed.isRead()) {
                        feed.setRead(true);
                        if (mNewsFeedDao == null) {
                            mNewsFeedDao = new NewsFeedDao(mContext);
                        }
                        mNewsFeedDao.update(feed);
                        notifyDataSetChanged();
                    }
                    Intent intent = new Intent(mContext, NewsDetailAty2.class);
                    intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
                    ArrayList<String> imageList = feed.getImgs();
                    if (imageList != null && imageList.size() != 0) {
                        intent.putExtra(NewsFeedFgt.KEY_NEWS_IMAGE, imageList.get(0));
                    }
                    mContext.startActivity(intent);
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


    public void isFavoriteList() {
        isNeedShowDisLikeIcon = false;
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

}