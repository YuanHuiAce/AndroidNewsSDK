package com.news.sdk.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.jinsedeyuzhou.IPlayer;
import com.github.jinsedeyuzhou.PlayStateParams;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.github.jinsedeyuzhou.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.sdk.R;
import com.news.sdk.adapter.NegativeScreenNewsDetailFgtAdapter;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.entity.ADLoadNewsFeedEntity;
import com.news.sdk.entity.NewsDetail;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.RelatedItemEntity;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.NewsDetailADRequestPost;
import com.news.sdk.net.volley.NewsDetailRequest;
import com.news.sdk.net.volley.RelatePointRequestPost;
import com.news.sdk.pages.NewsDetailWebviewAty;
import com.news.sdk.receiver.HomeWatcher;
import com.news.sdk.utils.AdUtil;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.PlayerManager;
import com.news.sdk.utils.manager.SharedPreManager;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 6/27/17.
 */

public class NegativeScreenVideoDetailView extends RelativeLayout implements ThemeManager.OnThemeChangeListener, View.OnClickListener, NativeAD.NativeAdListener {
    private Context mContext;

    private RelativeLayout view;
    private RelativeLayout bgLayout;
    private RelativeLayout mDetailHeader;
    private ImageView mDetailLeftBack;
    private ImageView mDetailRightMore;
    private View mNewsDetailLoaddingWrapper;
    private ImageView mNewsLoadingImg;
    private ProgressBar footView_progressbar;
    private TextView footView_tv;


    private String mUserId = "";
    private boolean isRefresh = false;
    private NewsFeed mNewsFeed;
    private NewsDetail mResult;
    private String mNid;
    private VPlayPlayer vplayer;
    private RequestManager mRequestManager;
    private boolean isBottom;
    private boolean isLoadDate;
    private int viewpointPage = 1;
    private String mDocid, mTitle, mNewID;
    //广告
    private int mAdCount = 10;
    private NativeAD mNativeAD;
    private int adPosition;
    private List<NativeADDataRef> marrlist;
    private int prcent;
    private String Aid, source, title;
    private boolean isUploadBigAd;
    private int mIntScorllY;
    private String mSource;
    private boolean isShow;

    private PullToRefreshListView mNewsDetailList;
    private VideoContainer mFullVideoContainer;
    private VideoContainer mVideoContainer;
    private ImageView mNoNetworkImage;
    private TextView mNoet;
    private RelativeLayout mDetailShow;
    private ImageView mDetailBg;
    private int mScreenWidth;
    private NegativeScreenNewsDetailFgtAdapter mAdapter;
    private LinearLayout mNewsDetailHeaderView;
    private LinearLayout mVideoDetailFootView;
    private View mCommentTitleView;
    private TextView mDetailVideoTitle;
    private TextView mRelateView;
    private RelativeLayout mDetailSharedTitleLayout;
    private RelativeLayout relativeLayout_attention;
    private View mViewPointLayout;
    private RelativeLayout adLayout;
    private TextViewExtend adtvTitle;
    private TextViewExtend adtvType;
    private ImageView adImageView;
    private LinearLayout footerView;
    private LinearLayout footerView_layout;
    private RelativeLayout detail_shared_shareImageLayout;
    private TextView detail_shared_moreComment;
    private RelativeLayout detail_hot_layout;
    private TextView detail_hotComment;
    private View detail_shared_hotComment_line1;
    private View detail_shared_hotComment_line2;
    private LinearLayout mCommentLayout;
    private AlphaAnimation mAlphaAnimationOut;
    private RelativeLayout mOnlinesContainer;
    private ImageView mTitleOff;
    private LinearLayout mTitleContainer;
    private HomeWatcher mHomeWatcher;


    public NegativeScreenVideoDetailView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.aty_negative_screen_video_detail_layout, this);
        //初始化相关参数
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mRequestManager = Glide.with(mContext);
        //titlebar
        mDetailHeader = (RelativeLayout) view.findViewById(R.id.mDetailHeader);
        mDetailLeftBack = (ImageView) view.findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailRightMore = (ImageView) view.findViewById(R.id.mDetailRightMore);
        mDetailRightMore.setOnClickListener(this);

        //无网络显示动画
        mNewsDetailLoaddingWrapper = view.findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsLoadingImg = (ImageView) view.findViewById(R.id.mNewsLoadingImg);
        mNoNetworkImage = (ImageView) view.findViewById(R.id.detail_image_bg);
        mNoet = (TextView) view.findViewById(R.id.tv_detail_video_noet);

        mDetailShow = (RelativeLayout) view.findViewById(R.id.detial_video_show);
        mDetailShow.setOnClickListener(this);

        //加载动画
        bgLayout = (RelativeLayout) view.findViewById(R.id.bgLayout);
        bgLayout.setVisibility(View.GONE);

        //相关新闻
        mNewsDetailList = (PullToRefreshListView) view.findViewById(R.id.fgt_new_detail_PullToRefreshListView);
        mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        mNewsDetailList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!isLoadDate) {
                    loadRelatedData();
                }
            }
        });
        mNewsDetailList.setOnStateListener(new PullToRefreshBase.onStateListener() {
            @Override
            public void getState(PullToRefreshBase.State mState) {
                if (!isBottom) {
                    return;
                }
                boolean isVisisyProgressBar = false;
                switch (mState) {
                    case RESET://初始
                        isVisisyProgressBar = false;
                        footView_tv.setText("上拉获取更多文章");
                        break;
                    case PULL_TO_REFRESH://更多推荐
                        isVisisyProgressBar = false;
                        footView_tv.setText("上拉获取更多文章");
                        break;
                    case RELEASE_TO_REFRESH://松开推荐
                        isVisisyProgressBar = false;
                        footView_tv.setText("松手获取更多文章");
                        break;
                    case REFRESHING:
                    case MANUAL_REFRESHING://推荐中
                        isVisisyProgressBar = true;
                        footView_tv.setText("正在获取更多文章...");
                        break;
                    case OVERSCROLLING:
                        // NO-OP
                        break;
                }
                if (isVisisyProgressBar) {
                    footView_progressbar.setVisibility(View.VISIBLE);
                } else {
                    footView_progressbar.setVisibility(View.GONE);
                }
                mNewsDetailList.setFooterViewInvisible();
            }
        });

        mNewsDetailList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private SparseArray recordSp = new SparseArray(0);
            private int mCurrentFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            isBottom = true;
                        } else {
                            isBottom = false;
                        }
                        if (!TextUtil.isListEmpty(beanList)) {
                            for (RelatedItemEntity relatedItemEntity : beanList) {
                                if (!relatedItemEntity.isUpload() && relatedItemEntity.isVisble() && relatedItemEntity.getRtype() == 3) {
                                    relatedItemEntity.setUpload(true);
                                    ArrayList<NewsFeed> newsFeeds = new ArrayList<>();
                                    NewsFeed feed = new NewsFeed();
                                    feed.setAid(Long.valueOf(Aid));
                                    feed.setSource(source);
                                    feed.setPname(relatedItemEntity.getPname());
                                    feed.setCtime(System.currentTimeMillis());
                                    newsFeeds.add(feed);
                                    LogUtil.userShowLog(newsFeeds, mContext);
                                }
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
                mCurrentFirstVisibleItem = firstVisibleItem;
                View firstView = absListView.getChildAt(0);
                if (null != firstView && null != recordSp) {
                    ItemRecord itemRecord = (ItemRecord) recordSp.get(firstVisibleItem);
                    if (null == itemRecord) {
                        itemRecord = new ItemRecord();
                    }
                    itemRecord.height = firstView.getHeight();
                    itemRecord.top = firstView.getTop();
                    recordSp.append(firstVisibleItem, itemRecord);
                }
                if (mIntScorllY < getScrollY()) {
                    mIntScorllY = getScrollY();
                }
                prcent = getVisibilityPercents(adLayout);
                if (prcent >= 50 && !isUploadBigAd && !TextUtil.isEmptyString(Aid) && !TextUtil.isEmptyString(source) && !TextUtil.isEmptyString(title)) {
                    isUploadBigAd = true;
                    ArrayList<NewsFeed> newsFeeds = new ArrayList<>();
                    NewsFeed feed = new NewsFeed();
                    feed.setAid(Long.valueOf(Aid));
                    feed.setSource(source);
                    feed.setPname(title);
                    feed.setCtime(System.currentTimeMillis());
                    newsFeeds.add(feed);
                    LogUtil.userShowLog(newsFeeds, mContext);
                }
            }


            private int getScrollY() {
                int height = 0;
                for (int i = 0; i < mCurrentFirstVisibleItem; i++) {
                    ItemRecord itemRecord = (ItemRecord) recordSp.get(i);
                    if (null != itemRecord) {
                        height += itemRecord.height;
                    }
                }
                ItemRecord itemRecord = (ItemRecord) recordSp.get(mCurrentFirstVisibleItem);
                if (null == itemRecord) {
                    itemRecord = new ItemRecord();
                }
                return height - itemRecord.top;
            }
        });

        mAdapter = new NegativeScreenNewsDetailFgtAdapter(mContext, null);
        mNewsDetailList.setAdapter(mAdapter);
        addHeadView();
        initPlayer();
        setTheme();
    }


    public Rect rect = new Rect();

    public int getVisibilityPercents(View view) {
        View tv = view;
        tv.getLocalVisibleRect(rect);
        int height = tv.getHeight();
        int percents = 100;
        if (rect.top == 0 && rect.bottom == height) {
            percents = 100;
        } else if (rect.top > 0) {
            percents = (height - rect.top) * 100 / height;
        } else if (rect.bottom > 0 && rect.bottom < height) {
            percents = rect.bottom * 100 / height;
        }
        return percents;
    }

    class ItemRecord {
        int height = 0;
        int top = 0;
    }

    private void addHeadView() {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        ListView lv = mNewsDetailList.getRefreshableView();
        mNewsDetailHeaderView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.fgt_news_detail, view, false);
        mVideoDetailFootView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.fgt_video_detail, view, false);
        mNewsDetailHeaderView.setLayoutParams(layoutParams);
        mVideoDetailFootView.setLayoutParams(layoutParams);
        lv.addHeaderView(mNewsDetailHeaderView);

        //标题
        mCommentTitleView = LayoutInflater.from(mContext).inflate(R.layout.vdetail_shared_layout, view, false);
        mCommentTitleView.setLayoutParams(layoutParams);
        mNewsDetailHeaderView.addView(mCommentTitleView);
        mDetailVideoTitle = (TextView) mCommentTitleView.findViewById(R.id.detail_video_title);
        mRelateView = (TextView) mCommentTitleView.findViewById(R.id.detail_ViewPoint);
        mDetailSharedTitleLayout = (RelativeLayout) mCommentTitleView.findViewById(R.id.detail_shared_TitleLayout);
        mOnlinesContainer = (RelativeLayout) mCommentTitleView.findViewById(R.id.detail_container_onlines);
        mOnlinesContainer.setVisibility(View.GONE);
        mTitleOff = (ImageView) mCommentTitleView.findViewById(R.id.ib_title_onff);
        mTitleContainer = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_video_title_container);
        mTitleContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShow) {
                    mDetailVideoTitle.setMaxLines(Integer.MAX_VALUE);
                    mTitleOff.setImageResource(R.drawable.ic_title_on);
                    mDetailVideoTitle.requestLayout();
                    mTitleOff.requestLayout();
                } else {
                    mDetailVideoTitle.setMaxLines(2);
                    mDetailVideoTitle.requestLayout();
                    mTitleOff.setImageResource(R.drawable.ic_title_off);
                    mTitleOff.requestLayout();
                }
                isShow = !isShow;

            }
        });

        //关注
        relativeLayout_attention = (RelativeLayout) mCommentTitleView.findViewById(R.id.relativeLayout_attention);
        relativeLayout_attention.setVisibility(View.GONE);


        //第2部分的viewPointContent
        mViewPointLayout = LayoutInflater.from(mContext).inflate(R.layout.vdetail_relate_layout, view, false);
        mViewPointLayout.setLayoutParams(layoutParams);
        //延时加载热点评论和相关观点
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                mVideoDetailFootView.addView(footerView);
//                mNewsDetailHeaderView.addView(mViewPointLayout);
                mVideoDetailFootView.addView(mViewPointLayout);
            }
        }, 1500);
        //评论
        detail_shared_shareImageLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_ShareImageLayout);
        detail_shared_moreComment = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_MoreComment);
        detail_hot_layout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_Hot_Layout);
        detail_hotComment = (TextView) mViewPointLayout.findViewById(R.id.detail_hotComment);
        detail_shared_hotComment_line1 = mViewPointLayout.findViewById(R.id.detail_shared_hotComment_Line1);
        detail_shared_hotComment_line2 = mViewPointLayout.findViewById(R.id.detail_shared_hotComment_Line2);
        mCommentLayout = (LinearLayout) mViewPointLayout.findViewById(R.id.detail_CommentLayout);
        detail_hot_layout.setVisibility(GONE);
        mCommentLayout.setVisibility(GONE);
        //广告
        adLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.adLayout);
        adtvTitle = (TextViewExtend) adLayout.findViewById(R.id.title_textView);
        adtvType = (TextViewExtend) adLayout.findViewById(R.id.type_textView);
        adImageView = (ImageView) adLayout.findViewById(R.id.adImage);
        RelativeLayout.LayoutParams adLayoutParams = (RelativeLayout.LayoutParams) adImageView.getLayoutParams();
        int imageWidth = mScreenWidth - DensityUtil.dip2px(mContext, 30);
        adLayoutParams.width = imageWidth;
        if (TextUtil.isEmptyString(CommonConstant.NEWS_DETAIL_GDT_SDK_BIGPOSID)) {
            adLayoutParams.height = (int) (imageWidth * 627 / 1200.0f);
        } else {
            adLayoutParams.height = (int) (imageWidth * 10 / 19.0f);
        }
        adImageView.setLayoutParams(adLayoutParams);
        detail_shared_moreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        footerView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.footerview_layout, null);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);
        footerView_layout = (LinearLayout) footerView.findViewById(R.id.footerView_layout);
        footerView_layout.setVisibility(View.GONE);
        lv.addFooterView(footerView);
        footView_tv.setVisibility(View.VISIBLE);


    }


    public void setNewsFeed(NewsFeed newsFeed, String source) {
        this.mNewsFeed = newsFeed;
        mSource = source;
        if (mNewsFeed != null) {
            mNid = mNewsFeed.getNid() + "";
            loadData();
        }
    }

    public void setData(String newID, String docId, String title, String source) {
        mNid = newID;
        mDocid = docId;
        mTitle = title;
        mSource = source;
        loadData();
    }

    public View getRootView() {
        return this.view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.mDetailLeftBack) {
            onBackUp();
            if (mAlphaAnimationOut != null) {
                view.startAnimation(mAlphaAnimationOut);
                mAlphaAnimationOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(GONE);
                        view.removeAllViews();
                        view.destroyDrawingCache();
                        view = null;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        } else if (id == R.id.mDetailRightMore) {

        } else if (id == R.id.mNewsDetailLoaddingWrapper) {
            loadData();
        } else if (id == R.id.detial_video_show) {
            if (!NetworkUtils.isConnectionAvailable(mContext)) {
                ToastUtil.toastShort("无网络，请稍后重试！");
                return;
            }
            mDetailShow.setVisibility(View.GONE);
            mVideoContainer.setVisibility(View.VISIBLE);
            if (vplayer != null && vplayer.getParent() != null) {
                ((ViewGroup) vplayer.getParent()).removeAllViews();
            }
            if (vplayer != null && mResult != null) {
                vplayer.setTitle(mResult.getTitle());
                vplayer.play(mResult.getVideourl());
                mVideoContainer.addView(vplayer);
            }

        }

    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }

    private void setTheme() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        TextUtil.setLayoutBgColor(mContext, mNewsDetailList, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, mViewPointLayout, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, adtvTitle, R.color.color9);
        TextUtil.setTextColor(mContext, adtvTitle, R.color.color2);
        TextUtil.setTextColor(mContext, adtvType, R.color.color11);
        TextUtil.setLayoutBgResource(mContext, adtvType, R.drawable.tag_detail_ad_shape);
        TextUtil.setTextColor(mContext, footView_tv, R.color.color2);
        TextUtil.setTextColor(mContext, detail_hotComment, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, detail_shared_hotComment_line1, R.color.color1);
        TextUtil.setLayoutBgResource(mContext, detail_shared_hotComment_line2, R.color.color5);
        TextUtil.setTextColor(mContext, mDetailVideoTitle, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, relativeLayout_attention, R.drawable.attention_detail_video_shape);
        ImageUtil.setAlphaImage(adImageView);
        TextUtil.setImageResource(mContext, mDetailLeftBack, R.drawable.detial_video_back);
        ImageUtil.setAlphaImage(mDetailBg);
        ImageUtil.setAlphaView(mDetailLeftBack);
        if (vplayer != null)
            ImageUtil.setAlphaView(vplayer);

    }

    protected void loadData() {
        if (NetUtil.checkNetWork(mContext)) {
            bgLayout.setVisibility(View.VISIBLE);
            isRefresh = true;
            mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
            if (mNewsFeed != null) {
                mNid = mNewsFeed.getNid() + "";
            }
            User user = SharedPreManager.mInstance(mContext).getUser(mContext);
            if (user != null) {
                mUserId = user.getMuid() + "";
            }
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            NewsDetailRequest<NewsDetail> feedRequest = new NewsDetailRequest<>(Request.Method.GET, new TypeToken<NewsDetail>() {
            }.getType(), HttpConstant.URL_VIDEO_CONTENT + "nid=" + mNid + "&uid=" + mUserId, new Response.Listener<NewsDetail>() {

                @Override
                public void onResponse(NewsDetail result) {
                    loadADData();
                    isRefresh = false;
                    mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                    mDetailShow.setVisibility(View.GONE);
                    if (bgLayout.getVisibility() == View.VISIBLE) {
                        bgLayout.setVisibility(View.GONE);
                    }
                    if (result != null) {
                        mResult = result;
                        mNewsFeed = convert2NewsFeed(result);
                        initData();
//                        if (mDetailWebView != null) {
//                            mDetailWebView.loadDataWithBaseURL(null, TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL),
//                                    SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)),
//                                    "text/html", "utf-8", null);
//                        }
                        LogUtil.userClickLog(mNewsFeed, mContext, mSource);
                    } else {
                        ToastUtil.toastShort("此新闻暂时无法查看!");
//                        mContext.finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isRefresh = false;
                    mNewsDetailLoaddingWrapper.setVisibility(View.VISIBLE);
                    bgLayout.setVisibility(View.GONE);
                }
            });
            feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(feedRequest);
        } else {
            mNewsDetailLoaddingWrapper.setVisibility(View.VISIBLE);
            bgLayout.setVisibility(View.GONE);
            mDetailShow.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        int widths = mScreenWidth - DensityUtil.dip2px(mContext, 0);
        RelativeLayout.LayoutParams lpVideo = (RelativeLayout.LayoutParams) mDetailBg.getLayoutParams();
        lpVideo.width = widths;
        lpVideo.height = (int) (widths * 185 / 330.0f);
        mDetailBg.setLayoutParams(lpVideo);
        setIsShowImagesSimpleDraweeViewURI(mDetailBg, mResult.getThumbnail());
        mDetailVideoTitle.setText(mResult.getTitle());
        if (NetworkUtils.isWifiAvailable(mContext) && vplayer != null&&!vplayer.isPlay()) {
            mDetailShow.setVisibility(View.GONE);
            vplayer.setTitle(mResult.getTitle());
            vplayer.play(mResult.getVideourl());
            removeVPlayer();
            mVideoContainer.addView(vplayer);
        }
        mAdapter.setRootView((RelativeLayout) view.getParent());


    }


    private NewsFeed convert2NewsFeed(NewsDetail result) {
        if (mNewsFeed == null) {
            mNewsFeed = new NewsFeed();
        }
        if (!TextUtil.isEmptyString(mNid)) {
            mNewsFeed.setNid(Integer.valueOf(mNid));
        }
        mNewsFeed.setDocid(result.getDocid());
        mNewsFeed.setUrl(result.getUrl());
        mNewsFeed.setTitle(result.getTitle());
        mNewsFeed.setPname(result.getPname());
        mNewsFeed.setPtime(result.getPtime());
        mNewsFeed.setComment(result.getComment());
        mNewsFeed.setChannel(result.getChannel());
        mNewsFeed.setStyle(result.getImgNum());
//        mNewsFeed.setImageUrl(mImageUrl);
        mNewsFeed.setNid(result.getNid());
        return mNewsFeed;
    }

    private void loadRelatedData() {
        if (!TextUtil.isEmptyString(mNid)) {
            isLoadDate = true;
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            String requestUrl = HttpConstant.URL_NEWS_RELATED;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("nid", Integer.valueOf(mNid));
                jsonObject.put("b", TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_RELATE_GDT_API_SMALLID)));
                jsonObject.put("p", viewpointPage);
                jsonObject.put("c", (6));
                jsonObject.put("ads", SharedPreManager.mInstance(mContext).getAdChannelInt(CommonConstant.FILE_AD, CommonConstant.AD_CHANNEL));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RelatePointRequestPost<ArrayList<RelatedItemEntity>> relateRequestPost = new RelatePointRequestPost(requestUrl, jsonObject.toString(), new Response.Listener<ArrayList<RelatedItemEntity>>() {
                @Override
                public void onResponse(final ArrayList<RelatedItemEntity> relatedItemEntities) {
                    isLoadDate = false;
                    viewpointPage++;
                    //去掉跳转到h5
                    Iterator<RelatedItemEntity> iterator = relatedItemEntities.iterator();
                    while (iterator.hasNext()) {
                        RelatedItemEntity relatedItemEntity = iterator.next();
                        String url = relatedItemEntity.getUrl();
                        if (relatedItemEntity.getRtype() != 3 && !url.contains("deeporiginalx.com")) {
                            iterator.remove();
                        }
                    }
                    for (RelatedItemEntity relatedItemEntity : relatedItemEntities) {
                        if (TextUtil.isEmptyString(relatedItemEntity.getImgUrl())) {
                            relatedItemEntity.setStyle(0);
                        } else {
                            int rtype = relatedItemEntity.getRtype();
                            if (rtype == 6) {
                                relatedItemEntity.setStyle(8);
                            } else {
                                relatedItemEntity.setStyle(1);
                            }
                        }
                    }
                    mNewsDetailList.onRefreshComplete();
                    if (!TextUtil.isListEmpty(relatedItemEntities)) {
                        setBeanPageList(relatedItemEntities);
                    } else {
                        setNoRelatedDate();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isLoadDate = false;
                    mNewsDetailList.onRefreshComplete();
                    setNoRelatedDate();
                }
            });
            relateRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(relateRequestPost);
        }
    }

    public void setNoRelatedDate() {
        mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        if (viewpointPage > 1) {
            if (footerView_layout.getVisibility() == View.GONE) {
                footerView_layout.setVisibility(View.VISIBLE);
            }
            footView_tv.setText("内容加载完毕");
        } else {
            mAdapter.setNewsFeed(null);
            mAdapter.notifyDataSetChanged();
            if (footerView_layout.getVisibility() == View.VISIBLE) {
                footerView_layout.setVisibility(View.GONE);
            }
        }
    }

    ArrayList<RelatedItemEntity> beanList = new ArrayList<>();
    ArrayList<RelatedItemEntity> mUploadArrRelated = new ArrayList<>();

    public void setBeanPageList(ArrayList<RelatedItemEntity> relatedItemEntities) {
        if (!TextUtil.isListEmpty(relatedItemEntities)) {
            if (SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE) && adPosition < relatedItemEntities.size() && adPosition > 0) {
                if (!TextUtil.isListEmpty(marrlist)) {
                    NativeADDataRef dataRelate = null;
                    if (!TextUtil.isListEmpty(marrlist)) {
                        dataRelate = marrlist.get(0);
                    }
                    if (dataRelate != null) {
                        RelatedItemEntity relatedItemEntity = new RelatedItemEntity();
                        relatedItemEntity.setRtype(3);
                        relatedItemEntity.setStyle(50);
                        relatedItemEntity.setTitle(dataRelate.getDesc());
                        relatedItemEntity.setPname(dataRelate.getTitle());
                        relatedItemEntity.setImgUrl(dataRelate.getImgUrl());
                        relatedItemEntity.setDataRef(dataRelate);
                        relatedItemEntities.add(adPosition, relatedItemEntity);
                        marrlist.remove(0);
                    }
                } else {
                    mNativeAD.loadAD(mAdCount);
                }
            }
            beanList.addAll(relatedItemEntities);
            mAdapter.setNewsFeed(beanList);
            mAdapter.notifyDataSetChanged();
            if (mNewsDetailList.getMode() != PullToRefreshBase.Mode.PULL_FROM_END) {
                mNewsDetailList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            }
            if (footerView_layout.getVisibility() == View.GONE) {
                footerView_layout.setVisibility(View.VISIBLE);
            }
            if (relatedItemEntities.size() < 6) {
                footView_tv.setText("内容加载完毕");
                mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
            }
            mDetailSharedTitleLayout.setVisibility(View.VISIBLE);
//            detail_shared_ViewPointTitleLayout.setVisibility(View.VISIBLE);
        }
    }


    public void setIsShowImagesSimpleDraweeViewURI(ImageView draweeView, String strImg) {
        if (!TextUtil.isEmptyString(strImg)) {
            if (SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)) {
                draweeView.setImageResource(R.drawable.bg_load_default_small);
            } else {
                Uri uri = Uri.parse(strImg);
                mRequestManager.load(uri).placeholder(R.drawable.bg_load_default_small).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(draweeView);
            }
        }
    }

    private void loadADData() {
        if (mNativeAD != null && SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE)) {
            mNativeAD.loadAD(mAdCount);
            Aid = CommonConstant.NEWS_DETAIL_GDT_SDK_BIGPOSID;
            source = CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE;
            adPosition = SharedPreManager.mInstance(mContext).getAdDetailPosition(CommonConstant.FILE_AD, CommonConstant.AD_RELATED_POS);
        } else {
            if (SharedPreManager.mInstance(mContext).getUser(mContext) != null) {
                String requestUrl = HttpConstant.URL_NEWS_DETAIL_AD;
                ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
                adLoadNewsFeedEntity.setUid(SharedPreManager.mInstance(mContext).getUser(mContext).getMuid());
                Gson gson = new Gson();
                //加入详情页广告位id
                Aid = CommonConstant.NEWS_DETAIL_GDT_API_BIGPOSID;
                source = CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE;
                adLoadNewsFeedEntity.setB(TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_DETAIL_GDT_API_BIGPOSID)));
                RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                NewsDetailADRequestPost<ArrayList<NewsFeed>> newsFeedRequestPost = new NewsDetailADRequestPost(requestUrl, gson.toJson(adLoadNewsFeedEntity), new Response.Listener<ArrayList<NewsFeed>>() {
                    @Override
                    public void onResponse(final ArrayList<NewsFeed> result) {
                        loadRelatedData();
                        if (!TextUtil.isListEmpty(result)) {
                            LogUtil.adGetLog(mContext, mAdCount, result.size(), Long.valueOf(CommonConstant.NEWS_DETAIL_GDT_API_BIGPOSID), CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE);
                            final NewsFeed newsFeed = result.get(0);
                            if (newsFeed != null) {
                                adtvTitle.setText(newsFeed.getTitle());
                                title = newsFeed.getTitle();
                                final ArrayList<String> imgs = newsFeed.getImgs();
                                if (!TextUtil.isListEmpty(imgs)) {
                                    mRequestManager.load(imgs.get(0)).placeholder(R.drawable.bg_load_default_small).into(adImageView);
                                    adImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            mRequestManager.load(imgs.get(0)).placeholder(R.drawable.bg_load_default_small).into(adImageView);
                                        }
                                    });
                                }
                                final float[] down_x = new float[1];
                                final float[] down_y = new float[1];
                                final float[] up_x = new float[1];
                                final float[] up_y = new float[1];
                                adLayout.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        if (newsFeed.getRtype() == 3) {
                                            switch (motionEvent.getAction()) {
                                                case MotionEvent.ACTION_DOWN:
                                                    down_x[0] = motionEvent.getX(0);
                                                    down_y[0] = adLayout.getY() + motionEvent.getY(0);
                                                    break;
                                                case MotionEvent.ACTION_UP:
                                                    up_x[0] = motionEvent.getX(0);
                                                    up_y[0] = adLayout.getY() + motionEvent.getY(0);
                                                    break;
                                            }
                                        }
                                        return false;
                                    }
                                });
                                adLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AdUtil.upLoadContentClick(newsFeed.getAdDetailEntity(), mContext, down_x[0], down_y[0], up_x[0], up_y[0]);
                                        LogUtil.adClickLog(Long.valueOf(CommonConstant.NEWS_DETAIL_GDT_API_BIGPOSID), mContext, CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE, newsFeed.getPname());
                                        Intent AdIntent = new Intent(mContext, NewsDetailWebviewAty.class);
                                        AdIntent.putExtra("key_url", newsFeed.getPurl());
                                        mContext.startActivity(AdIntent);
                                    }
                                });
                                AdUtil.upLoadAd(newsFeed.getAdDetailEntity(), mContext);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadRelatedData();
                        adLayout.setVisibility(View.GONE);
                    }
                });
                newsFeedRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
                requestQueue.add(newsFeedRequestPost);
            }
        }
    }

    @Override
    public void onADLoaded(List<NativeADDataRef> list) {
        marrlist = list;
        adLayout.setVisibility(View.VISIBLE);
        if (!TextUtil.isListEmpty(marrlist)) {
            LogUtil.adGetLog(mContext, mAdCount, list.size(), Long.valueOf(CommonConstant.NEWS_DETAIL_GDT_SDK_BIGPOSID), CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE);
            final NativeADDataRef dataRef = list.get(0);
            if (dataRef != null && TextUtil.isEmptyString(title)) {
                title = dataRef.getTitle();
                adtvTitle.setText(dataRef.getDesc());
                final String url = dataRef.getImgUrl();
                if (!TextUtil.isEmptyString(url)) {
                    mRequestManager.load(Uri.parse(url)).placeholder(R.drawable.bg_load_default_small).diskCacheStrategy(DiskCacheStrategy.ALL).into(adImageView);
                    adImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mRequestManager.load(url).placeholder(R.drawable.bg_load_default_small).into(adImageView);
                        }
                    });
                }
                dataRef.onExposured(adLayout);
                adLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogUtil.adClickLog(Long.valueOf(CommonConstant.NEWS_DETAIL_GDT_SDK_BIGPOSID), mContext, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE, dataRef.getTitle());
                        dataRef.onClicked(adLayout);
                    }
                });
                marrlist.remove(0);
            }
        }
        if (TextUtil.isListEmpty(beanList)) {
            loadRelatedData();
        }
    }

    @Override
    public void onNoAD(int i) {
        loadRelatedData();
        adLayout.setVisibility(View.GONE);
    }

    @Override
    public void onADStatusChanged(NativeADDataRef nativeADDataRef) {

    }

    @Override
    public void onADError(NativeADDataRef nativeADDataRef, int i) {
        loadRelatedData();
        adLayout.setVisibility(View.GONE);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return onBackUp();
        }
        return super.onKeyDown(keyCode, event);
    }


    public boolean onBackUp() {
        if (mFullVideoContainer.getVisibility() == View.VISIBLE) {
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            FrameLayout frameLayout = (FrameLayout) vplayer.getParent();
            if (frameLayout != null) {
                frameLayout.removeAllViews();
            }
            mFullVideoContainer.setVisibility(View.GONE);
            mDetailShow.setVisibility(View.GONE);
            mVideoContainer.addView(vplayer);
            vplayer.showBottomControl(true);

            return true;
        }
//        else if (mVideoContainer.getVisibility() == View.VISIBLE) {
//            FrameLayout frameLayout = (FrameLayout) vplayer.getParent();
//            if (frameLayout != null) {
//                frameLayout.removeAllViews();
//            }
//            if (vplayer != null) {
//                vplayer.stop();
//                vplayer.release();
//            }
//            mDetailShow.setVisibility(View.VISIBLE);
//            mVideoContainer.setVisibility(View.GONE);
//            return false;
//        }

        return false;
    }

    private void initPlayer() {
        if (PlayerManager.videoPlayView != null) {
            vplayer = PlayerManager.videoPlayView;
        } else {
            vplayer = PlayerManager.getPlayerManager().initialize(mContext);
        }
        //视频相关
        mFullVideoContainer = (VideoContainer) view.findViewById(R.id.detail_video_fullscreen);
        mVideoContainer = (VideoContainer) view.findViewById(R.id.detail_video_container);
        mDetailShow = (RelativeLayout) view.findViewById(R.id.detial_video_show);
        mDetailBg = (ImageView) view.findViewById(R.id.detail_image_bg);
        mDetailShow.setOnClickListener(this);
        if (vplayer != null && vplayer.getParent() != null)
            ((ViewGroup) vplayer.getParent()).removeAllViews();
        vplayer.setOnShareListener(new IPlayer.OnShareListener() {
            @Override
            public void onShare() {

            }

            @Override
            public void onPlayCancel() {
                if (vplayer != null) {
                    vplayer.stop();
                    vplayer.release();
                    if (vplayer.getParent() != null)
                        ((ViewGroup) vplayer.getParent()).removeAllViews();
                }
                mDetailShow.setVisibility(View.VISIBLE);
                mVideoContainer.setVisibility(View.GONE);
            }
        });


        vplayer.setCompletionListener(new IPlayer.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                if (vplayer != null) {
                    vplayer.stop();
                    vplayer.release();
                }
                if (mFullVideoContainer.getVisibility() == View.VISIBLE) {
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mFullVideoContainer.removeAllViews();
                    mFullVideoContainer.setVisibility(View.GONE);
                } else if (mVideoContainer.getVisibility() == View.VISIBLE) {
                    mVideoContainer.removeAllViews();
                    mVideoContainer.setVisibility(View.GONE);
                }

                mDetailShow.setVisibility(View.VISIBLE);

            }
        });
        if (NetworkUtils.isWifiAvailable(mContext) && vplayer != null&&vplayer.isPlay()) {
            mDetailShow.setVisibility(View.GONE);
            vplayer.setTitle(mResult.getTitle());
            vplayer.play(mResult.getVideourl());
            removeVPlayer();
            mVideoContainer.addView(vplayer);
        }
    }

    /**
     * 释放播放器
     */
    public void removeVPlayer() {
        if (vplayer != null) {
            ViewGroup parent = (ViewGroup) vplayer.getParent();
            if (parent != null)
                parent.removeAllViews();
        }
    }
    //把获取焦点强制为True，就有焦点了
    @Override
    public boolean isFocused() {
//        return super.isFocused();
        return true;
    }

    public void OnDestory() {
        if (vplayer != null) {
            vplayer.stop();
            vplayer.release();
            if (vplayer.getParent() != null)
                ((ViewGroup) vplayer.getParent()).removeAllViews();
            vplayer.onDestory();
        }
        vplayer = null;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (vplayer != null) {
            vplayer.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                FrameLayout frameLayout = (FrameLayout) vplayer.getParent();
                if (frameLayout != null) {
                    frameLayout.removeAllViews();
                }
                mFullVideoContainer.setVisibility(View.GONE);
                mDetailShow.setVisibility(View.GONE);
                mVideoContainer.addView(vplayer);
                vplayer.showBottomControl(true);

            } else {
                FrameLayout frameLayout = (FrameLayout) vplayer.getParent();
                if (frameLayout != null) {
                    frameLayout.removeAllViews();
                }
                mDetailShow.setVisibility(View.VISIBLE);
                mFullVideoContainer.setVisibility(View.VISIBLE);
                mFullVideoContainer.addView(vplayer);
                if (vplayer.getStatus() != PlayStateParams.STATE_PAUSED)
                    vplayer.showBottomControl(false);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mHomeWatcher = new HomeWatcher(mContext);
        mHomeWatcher.setOnHomePressedListener(mOnHomePressedListener);
        mHomeWatcher.startWatch();
    }

    @Override
    protected void onDetachedFromWindow() {
        OnDestory();
        mHomeWatcher.stopWatch();
        mHomeWatcher = null;
        if (vplayer != null) {
            vplayer.stop();
            vplayer.release();
            vplayer = null;
        }
        super.onDetachedFromWindow();
    }

    HomeWatcher.OnHomePressedListener mOnHomePressedListener = new HomeWatcher.OnHomePressedListener() {
        @Override
        public void onHomePressed() {
            Logger.e("aaa", "点击home键");
            onBackUp();
        }


        @Override
        public void onHomeLongPressed() {
            Logger.e("aaa", "长按home键");
            onBackUp();
        }
    };


}
