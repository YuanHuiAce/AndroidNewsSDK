package com.news.yazhidao.adapter;

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
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.adapter.abslistview.MultiItemCommonAdapter;
import com.news.yazhidao.adapter.abslistview.MultiItemTypeSupport;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.FileUtils;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ZipperUtil;
import com.news.yazhidao.widget.TextViewExtend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class NewsFeedAdapter extends MultiItemCommonAdapter<NewsFeed> {

    private final NewsFeedFgt mNewsFeedFgt;
    private String mstrKeyWord;
    private int mScreenHeight;
    private int mScreenWidth;
    private Context mContext;
    public static String KEY_URL = "key_url";
    public static String KEY_NEWS_ID = "key_news_id";
    public static int REQUEST_CODE = 10002;
    private SharedPreferences mSharedPreferences;
    private NewsFeedDao mNewsFeedDao;
    private final int DELETEANIMTIME = 500;
    private File mNewsFile;
    private int mTitleViewWidth;
    private int mCardWidth, mCardHeight;
    private boolean isFavorite;
    private boolean isNeedShowDisLikeIcon = true;
    boolean isCkeckVisity;


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
                    case 11://大图Item
                    case 12:
                    case 13:
                        return R.layout.ll_news_big_pic2;
                    default:
                        return 0;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 6;
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
                    case 11://大图Item
                    case 12:
                    case 13:
                        return NewsFeed.BIG_PIC;


                    default:
                        return 0;
                }
            }
        });
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        this.mNewsFeedFgt = newsFeedFgt;
        if (mNewsFeedFgt == null) {
            isFavorite = true;
        }
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mNewsFeedDao = new NewsFeedDao(mContext);
        mNewsFile = ZipperUtil.getSaveFontPath(context);
        mTitleViewWidth = mScreenWidth - DensityUtil.dip2px(mContext, 147);
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
    }


    public void setSearchKeyWord(String pKeyWord) {
        mstrKeyWord = pKeyWord;
        mDatas = null;
    }


    @Override
    public void convert(final CommonViewHolder holder, NewsFeed feed, int position) {
        int layoutId = holder.getLayoutId();
        if (layoutId == R.layout.qd_ll_news_item_no_pic || layoutId == R.layout.qd_ll_news_item_one_pic
                || layoutId == R.layout.qd_ll_news_card) {
//        switch (holder.getLayoutId()) {
//
//            case R.layout.qd_ll_news_item_no_pic:
//            case R.layout.qd_ll_news_item_one_pic:
//            case R.layout.qd_ll_news_card:
            if (isCkeckVisity) {
                holder.getView(R.id.checkFavoriteDelete_image).setVisibility(View.VISIBLE);
            } else {
                holder.getView(R.id.checkFavoriteDelete_image).setVisibility(View.GONE);
                holder.getImageView(R.id.checkFavoriteDelete_image).setImageResource(R.drawable.favorite_uncheck);

            }
            if (isFavorite) {
                holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
                holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
                holder.getView(R.id.line_bottom_imageView).setBackgroundColor(mContext.getResources().getColor(R.color.new_color5));
                if (getCount() == position + 1) {//去掉最后一条的线
                    holder.getView(R.id.line_bottom_imageView).setVisibility(View.INVISIBLE);
                }
                ClickDeleteFavorite((ImageView) holder.getView(R.id.checkFavoriteDelete_image), feed);
            }
//                break;
        }
        if (layoutId == R.layout.qd_ll_news_item_no_pic) {

//        switch (holder.getLayoutId()) {
//            case R.layout.qd_ll_news_item_no_pic:
            if (isFavorite) {
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false);
            } else {
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            }

                setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment()+"");
                if (feed.getPtime() != null)
                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
//                break;
        } else if (layoutId == R.layout.qd_ll_news_item_one_pic) {
//            case R.layout.qd_ll_news_item_one_pic:
                holder.setSimpleDraweeViewURI(R.id.title_img_View, feed.getImgs().get(0));
            final String strTitle = feed.getTitle();
            if (isFavorite) {
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false);
            } else {
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            }
            final TextView tvTitle = holder.getView(R.id.title_textView);
            final LinearLayout llSourceContent = holder.getView(R.id.source_content_linearLayout);
            final ImageView ivBottomLine = holder.getView(R.id.line_bottom_imageView);
            SimpleDraweeView ivCard = holder.getView(R.id.title_img_View);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = mCardWidth;
            lpCard.height = mCardHeight;
            ivCard.setLayoutParams(lpCard);
//            RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) llSourceContent.getLayoutParams();
//            RelativeLayout.LayoutParams titleLp = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
//            RelativeLayout.LayoutParams lpBottomLine = (RelativeLayout.LayoutParams) ivBottomLine.getLayoutParams();
//            float textRealWidth = tvTitle.getPaint().measureText(strTitle);
//            if (textRealWidth >= 2 * mTitleViewWidth - 5) {
//                titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 15), 0);
//                lpSourceContent.rightMargin = DensityUtil.dip2px(mContext, 15);
//                lpBottomLine.addRule(RelativeLayout.BELOW, R.id.source_content_linearLayout);
//            } else if (textRealWidth <= mTitleViewWidth) {
//                titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 21), DensityUtil.dip2px(mContext, 15), 0);
//                lpSourceContent.rightMargin = mCardWidth + DensityUtil.dip2px(mContext, 25);
//                lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
//            } else {
//                titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 15), 0);
//                lpSourceContent.rightMargin = mCardWidth + DensityUtil.dip2px(mContext, 25);
//                lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
//            }
//            llSourceContent.setLayoutParams(lpSourceContent);
//            ivBottomLine.setLayoutParams(lpBottomLine);
            tvTitle.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) llSourceContent.getLayoutParams();
                    RelativeLayout.LayoutParams titleLp = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
                    RelativeLayout.LayoutParams lpBottomLine = (RelativeLayout.LayoutParams) ivBottomLine.getLayoutParams();
                    int lineCount = tvTitle.getLineCount();
                    if (lineCount >= 3) {
                        titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 15), 0);
                        lpSourceContent.rightMargin = DensityUtil.dip2px(mContext, 15);
                        lpBottomLine.addRule(RelativeLayout.BELOW, R.id.source_content_linearLayout);
                    } else if (lineCount <= 1) {
                        titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 21), DensityUtil.dip2px(mContext, 15), 0);
                        lpSourceContent.rightMargin = mCardWidth + DensityUtil.dip2px(mContext, 25);
                        lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
                    } else {
                        titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 15), 0);
                        lpSourceContent.rightMargin = mCardWidth + DensityUtil.dip2px(mContext, 25);
                        lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
                    }
                    llSourceContent.setLayoutParams(lpSourceContent);
                    ivBottomLine.setLayoutParams(lpBottomLine);
                }
            });

                setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment()+"");
                if (feed.getPtime() != null) {
                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
            }
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
//                break;
        }
        else if (layoutId==R.layout.ll_news_big_pic2)
        {
            holder.setSimpleDraweeViewURI(R.id.title_img_View, feed.getImgs().get(0));
            if (isFavorite) {
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false);
            } else {
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            }
            LinearLayout llSourceBigPic = holder.getView(R.id.source_content_linearLayout);
            setSourceViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.news_source_TextView), feed.getPname());
//            setFocusBgColor((TextViewExtend) llSourceBigPic.findViewById(R.id.news_source_TextView), feed.getPname(), (TextViewExtend) llSourceBigPic.findViewById(R.id.comment_num_textView), (ImageView) llSourceBigPic.findViewById(R.id.delete_imageView));
            setCommentViewText((TextViewExtend) llSourceBigPic.findViewById(R.id.comment_num_textView), feed.getComment() + "");
            if (feed.getPtime() != null)
                setNewsTime((TextViewExtend) llSourceBigPic.findViewById(R.id.comment_textView), feed.getPtime());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) llSourceBigPic.findViewById(R.id.delete_imageView), feed, holder.getConvertView());
            llSourceBigPic.findViewById(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
        }
        else if (layoutId == R.layout.qd_ll_news_card) {
//            case R.layout.qd_ll_news_card:
                ArrayList<String> strArrImgUrl = feed.getImgs();
            holder.setSimpleDraweeViewURI(R.id.image_card1, strArrImgUrl.get(0));
            holder.setSimpleDraweeViewURI(R.id.image_card2, strArrImgUrl.get(1));
            holder.setSimpleDraweeViewURI(R.id.image_card3, strArrImgUrl.get(2));
            setCardMargin((SimpleDraweeView) holder.getView(R.id.image_card1), 15, 1, 3);
            setCardMargin((SimpleDraweeView) holder.getView(R.id.image_card2), 1, 1, 3);
            setCardMargin((SimpleDraweeView) holder.getView(R.id.image_card3), 1, 15, 3);
            if (isFavorite) {
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false);
            } else {
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
            }
                setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment()+"");
                if (feed.getPtime() != null)
                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
            setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
            setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
//                break;
        } else if (layoutId == R.layout.qd_ll_news_item_time_line) {
//            case R.layout.qd_ll_news_item_time_line:
            holder.getView(R.id.news_content_relativeLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNewsFeedFgt.refreshData();
                }
            });
//                break;
        }
    }


    private void setCardMargin(SimpleDraweeView ivCard, int leftMargin, int rightMargin, int pageNum) {
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
//                tvComment.setText(updateTime);
                tvComment.setText("");
            } else if (between < (24 * 3600000) && between >= (1 * 3600000)) {
//                tvComment.setText(between / 3600000 + "小时前");
                tvComment.setText("");
            } else {
                int time = (int) (between * 60 / 3600000);
                if (time > 0)
                    tvComment.setText(between * 60 / 3600000 + "分钟前");
                else
                    tvComment.setText(between * 60 * 60 / 3600000 + "秒前");
//                if (between / 3600000 == 0) {
//                    tvComment.setText( between *60/ 3600000+"分钟前");
//                } else {
//                    tvComment.setText(between / 3600000 / 60 + "分钟前");
//                }
            }
        } catch (ParseException e) {
            tvComment.setText(updateTime);
            e.printStackTrace();
        }

    }

    private void setTitleTextBySpannable(TextView tvTitle, String strTitle, boolean isRead) {
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
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.new_color7));
            } else {
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.newsFeed_titleColor));
            }
            tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        }
    }

    private void setSourceViewText(TextViewExtend textView, String strText) {
        if (strText != null && !"".equals(strText)) {
            textView.setText(strText);
        }
    }

    private void setCommentViewText(TextViewExtend textView, String strText) {
        if (!TextUtil.isEmptyString(strText) && !"0".equals(strText)) {
            textView.setText(strText + "评");
        } else {
            textView.setText("");
        }
    }

    /**
     * item的点击事件
     *
     * @param rlNewsContent
     * @param feed
     */
    private void setNewsContentClick(RelativeLayout rlNewsContent, final NewsFeed feed) {
        rlNewsContent.setOnClickListener(new View.OnClickListener() {
            long firstClick = 0;

            public void onClick(View paramAnonymousView) {
                if (System.currentTimeMillis() - firstClick <= 1500L) {
                    firstClick = System.currentTimeMillis();
                    return;
                }
                firstClick = System.currentTimeMillis();
                Intent intent = new Intent(mContext, NewsDetailAty2.class);
                intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);

                ArrayList<String> imageList = feed.getImgs();
                if (imageList != null && imageList.size() != 0) {
                    intent.putExtra(NewsFeedFgt.KEY_NEWS_IMAGE, imageList.get(0));
                }
                if (mNewsFeedFgt != null) {
                    mNewsFeedFgt.startActivityForResult(intent, REQUEST_CODE);
                } else {
                    ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
                }
                //推送人员使用
                if (DeviceInfoUtil.getUUID().equals("3b7976c8c1b8cd372a59b05bfa9ac5b3")) {
                    File file = FileUtils.getSavePushInfoPath(mContext, "push.txt");
                    BufferedWriter bis = null;
                    try {
                        bis = new BufferedWriter(new FileWriter(file));
                        bis.write(feed.getTitle() + ",newsid=" + feed.getNid());
                        bis.newLine();
                        bis.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bis != null) {
                            try {
                                bis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

                /**点击查看详情时,统计当前的频道名称*/
                String channelName = "";
                ChannelItemDao dao = new ChannelItemDao(mContext);
                ArrayList<ChannelItem> channelItems = dao.queryForAll();
                for (ChannelItem item: channelItems){
                    if (Integer.valueOf(feed.getChannel()).equals(item.getId())){
                        channelName = item.getName();
                        break;
                    }
                }
                HashMap<String, String> hashmap = new HashMap();
                hashmap.put("channel", channelName);

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
//                ToastUtil.toastShort(feed.getPubName());
                DeleteView = view;
                DeleteClickBean = feed;
                int[] LocationInWindow = new int[2];
                int[] LocationOnScreen = new int[2];
                imageView.getLocationInWindow(LocationInWindow);

                mClickShowPopWindow.showPopWindow(LocationInWindow[0] + imageView.getWidth() / 2, LocationInWindow[1] + imageView.getHeight() / 2,
                        feed);


            }
        });
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


    public void isFavoriteList(){
        isFavorite = true;
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

    private introductionNewsFeed mIntroductionNewsFeed;

    public void setIntroductionNewsFeed(introductionNewsFeed mIntroductionNewsFeed) {
        this.mIntroductionNewsFeed = mIntroductionNewsFeed;
    }

    public void ClickDeleteFavorite(final ImageView checkDelete, final NewsFeed feed) {
//        checkDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(feed.isFavorite()){
//                    feed.setFavorite(false);
//                    mIntroductionNewsFeed.getDate(feed,false);
//                    checkDelete.setImageResource(R.drawable.favorite_uncheck);
//                }else{
//                    mIntroductionNewsFeed.getDate(feed,true);
//                    feed.setFavorite(true);
//                    checkDelete.setImageResource(R.drawable.favorite_check);
//                }
//            }
//        });
    }

    public void setVisitycheckFavoriteDeleteLayout(boolean isVisity) {

        isCkeckVisity = isVisity;
        notifyDataSetChanged();
    }

/**
 * 接口回调传入数据的添加与删除
 * <p>
 * isCheck true：添加   false：删除
 */
public interface introductionNewsFeed {
    public void getDate(NewsFeed feed, boolean isCheck);
}

public interface clickShowPopWindow {
    public void showPopWindow(int x, int y, NewsFeed feed);
}

}