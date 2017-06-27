package com.news.sdk.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
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
import com.news.sdk.database.NewsDetailCommentDao;
import com.news.sdk.entity.ADLoadNewsFeedEntity;
import com.news.sdk.entity.NewsDetail;
import com.news.sdk.entity.NewsDetailComment;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.RelatedItemEntity;
import com.news.sdk.entity.User;
import com.news.sdk.javascript.VideoJavaScriptBridge;
import com.news.sdk.net.volley.NewsDetailADRequestPost;
import com.news.sdk.net.volley.NewsDetailRequest;
import com.news.sdk.net.volley.RelatePointRequestPost;
import com.news.sdk.pages.NewsDetailFgt;
import com.news.sdk.pages.NewsDetailWebviewAty;
import com.news.sdk.pages.RelevantViewWebviewAty;
import com.news.sdk.utils.AdUtil;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.webview.LoadWebView;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class NegativeScreenNewsDetailView extends View implements ThemeManager.OnThemeChangeListener, NativeAD.NativeAdListener, View.OnClickListener {

    Context mContext;
    RelativeLayout mRootView;

    private String mUserId = "";
    /**
     * 返回上一级,全文评论,分享
     */
    private View mHeaderDivider;
    private ImageView mDetailLeftBack;
    private ImageView mDetailRightMore;
    private RelativeLayout mDetailHeader, bgLayout;
    private ProgressBar imageAni;
    private TextView textAni;
    private NewsFeed mNewsFeed;
    private String mNid;
    private boolean isRefresh = false;
    private String mSource;

    private LoadWebView mDetailWebView;
    private NewsDetail mResult;
    private SharedPreferences mSharedPreferences;
    private PullToRefreshListView mNewsDetailList;
    private NegativeScreenNewsDetailFgtAdapter mAdapter;
    private User mUser;
    private String mDocid, mTitle, mNewID;
    private ArrayList<NewsDetailComment> mComments;
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_FEED = "key_news_feed";
    public static final String KEY_NEWS_IMAGE = "key_news_image";
    public static final String KEY_NEWS_ID = "key_news_id";
    public static final String KEY_NEWS_TITLE = "key_news_title";
    public static final int REQUEST_CODE = 1030;
    private LinearLayout detail_shared_FriendCircleLayout,
            detail_shared_CareForLayout,
            mCommentLayout,
            mNewsDetailHeaderView;
    private View mViewPointLayout;
    private RelativeLayout mNewsDetailLoaddingWrapper;
    private RelativeLayout detail_shared_ShareImageLayout,
            detail_Hot_Layout, relativeLayout_attention,
            detail_shared_ViewPointTitleLayout, adLayout;
    private ImageView detail_shared_AttentionImage, iv_attention_icon;
    private TextView detail_hotComment, detail_ViewPoint, attention_btn;
    private View detail_shared_hotComment_Line1, detail_shared_hotComment_Line2, detail_ViewPoint_Line1, detail_ViewPoint_Line2;
    private NewsDetailFgt.RefreshPageBroReceiver mRefreshReceiver;
    private boolean isWebSuccess;
    private boolean isLike;
    private boolean isAttention;
    private NewsDetailCommentDao mNewsDetailCommentDao;
    private TextView detail_shared_PraiseText, tv_attention_title, footView_tv;
    private ProgressBar footView_progressbar;
    private LinearLayout footerView_layout;
    private boolean isBottom;
    private boolean isLoadDate;
    private boolean isNetWork;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    private RequestManager mRequestManager;
    private int mScreenWidth, mScreenHeight;
    private TextViewExtend adtvTitle, adtvType;
    private ImageView adImageView;
    private int viewpointPage = 1;
    private int mIntScorllY;
    //广告
    private int mAdCount = 10;
    private NativeAD mNativeAD;
    private int adPosition;
    private List<NativeADDataRef> marrlist;
    private int prcent;
    private String Aid, source, title;
    private boolean isUploadBigAd;

    public NegativeScreenNewsDetailView(Context context) {
        super(context);
        mContext = context;
        initializeViews();
    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }

    protected void initializeViews() {
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
        mRequestManager = Glide.with(mContext);
        mNativeAD = new NativeAD(QiDianApplication.getInstance().getAppContext(), CommonConstant.APPID, CommonConstant.NEWS_DETAIL_GDT_SDK_BIGPOSID, this);
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mRootView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.aty_negative_screen_news_detail_layout, null);
        mNewsDetailLoaddingWrapper = (RelativeLayout) mRootView.findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsDetailLoaddingWrapper.setOnClickListener(this);
        bgLayout = (RelativeLayout) mRootView.findViewById(R.id.bgLayout);
        imageAni = (ProgressBar) mRootView.findViewById(R.id.imageAni);
        textAni = (TextView) mRootView.findViewById(R.id.textAni);
        mDetailHeader = (RelativeLayout) mRootView.findViewById(R.id.mDetailHeader);
        mHeaderDivider = mRootView.findViewById(R.id.mHeaderDivider);
        mDetailLeftBack = (ImageView) mRootView.findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailRightMore = (ImageView) mRootView.findViewById(R.id.mDetailRightMore);
        mDetailRightMore.setVisibility(GONE);
        mUser = SharedPreManager.mInstance(mContext).getUser(mContext);
        mNewsDetailList = (PullToRefreshListView) mRootView.findViewById(R.id.fgt_new_detail_PullToRefreshListView);
        bgLayout = (RelativeLayout) mRootView.findViewById(R.id.bgLayout);
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
        mAdapter.setRootView(mRootView);
        addHeadView();
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

    public String getPercent() {
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        float percent = 0;
        if (mDetailWebView != null) {
            float webViewHeight = mDetailWebView.getHeight();
            if (webViewHeight != 0) {
                percent = (float) (mIntScorllY + mScreenHeight) / (float) mDetailWebView.getHeight();
                if (percent >= 1.00f) {
                    percent = 1.00f;
                }
            }
        }
        return nt.format(percent);
    }

    public View getNewsView() {
        return this.mRootView;
    }

    public void addHeadView() {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ListView lv = mNewsDetailList.getRefreshableView();
        //第1部分的WebView
        mNewsDetailHeaderView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.fgt_news_detail, null);
        mNewsDetailHeaderView.setLayoutParams(layoutParams);
        lv.addHeaderView(mNewsDetailHeaderView);
        mNewsDetailHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        mDetailWebView = new LoadWebView(mContext.getApplicationContext());
        mDetailWebView.setLayoutParams(params);
        mDetailWebView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        mDetailWebView.getSettings().setJavaScriptEnabled(true);
        mDetailWebView.getSettings().setDatabaseEnabled(true);
        mDetailWebView.getSettings().setDomStorageEnabled(true);
        mDetailWebView.getSettings().setLoadsImagesAutomatically(true);
        mDetailWebView.getSettings().setBlockNetworkImage(false);
        mDetailWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mDetailWebView.getSettings().setLoadWithOverviewMode(true);
        mDetailWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mDetailWebView.addJavascriptInterface(new VideoJavaScriptBridge(mContext), "VideoJavaScriptBridge");
        //梁帅：判断图片是不是  不显示
        mDetailWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                String keyword = "";
                try {
                    keyword = URLDecoder.decode(Uri.parse(url).toString().substring(40), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String contentUrl = "https://m.baidu.com/s?from=1019278a&word=" + keyword;
                Intent intent = new Intent(mContext, RelevantViewWebviewAty.class);
                intent.putExtra(RelevantViewWebviewAty.KEY_URL, contentUrl);
                mContext.startActivity(intent);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //view.loadData("ERROR: " + description,"text/plain","utf8");
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                //接受证书
                handler.proceed();
            }
        });
        mDetailWebView.setDf(new LoadWebView.PlayFinish() {
            @Override
            public void After() {
                isWebSuccess = true;
                isBgLayoutSuccess();
            }
        });
        //禁止长按移动到底部。
        mDetailWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mNewsDetailHeaderView.addView(mDetailWebView);
        mViewPointLayout = LayoutInflater.from(mContext).inflate(R.layout.detail_relate_layout, null);
        mViewPointLayout.setLayoutParams(layoutParams);
        //延时加载热点评论和相关观点
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNewsDetailHeaderView.addView(mViewPointLayout);
            }
        }, 1000);
        //关注
        relativeLayout_attention = (RelativeLayout) mViewPointLayout.findViewById(R.id.relativeLayout_attention);
        relativeLayout_attention.setVisibility(View.GONE);
        //评论
        detail_shared_ViewPointTitleLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_TitleLayout);
        detail_ViewPoint = (TextView) mViewPointLayout.findViewById(R.id.detail_ViewPoint);
        detail_ViewPoint_Line1 = mViewPointLayout.findViewById(R.id.detail_ViewPoint_Line1);
        detail_ViewPoint_Line2 = mViewPointLayout.findViewById(R.id.detail_ViewPoint_Line2);
        detail_shared_ShareImageLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_ShareImageLayout);
        detail_Hot_Layout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_Hot_Layout);
        mCommentLayout = (LinearLayout) mViewPointLayout.findViewById(R.id.detail_CommentLayout);
        detail_Hot_Layout.setVisibility(GONE);
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
        final LinearLayout footerView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);
        footerView_layout = (LinearLayout) footerView.findViewById(R.id.footerView_layout);
        footerView_layout.setVisibility(View.GONE);
        footView_tv.setVisibility(View.VISIBLE);
    }

    public void setNewsFeed(NewsFeed newsFeed, String source) {
        mNewsFeed = newsFeed;
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

    public void isBgLayoutSuccess() {
        if (isWebSuccess && bgLayout.getVisibility() == View.VISIBLE) {
            bgLayout.setVisibility(View.GONE);
        }
    }

    public void setTheme() {
        TextUtil.setLayoutBgResource(mContext, mDetailLeftBack, R.drawable.bg_left_back_selector);
        TextUtil.setImageResource(mContext, mDetailLeftBack, R.drawable.btn_left_back);
        TextUtil.setLayoutBgResource(mContext, mDetailRightMore, R.drawable.bg_more_selector);
        TextUtil.setImageResource(mContext, mDetailRightMore, R.drawable.btn_detail_right_more);
        TextUtil.setLayoutBgResource(mContext, mDetailHeader, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, mHeaderDivider, R.color.color5);
        TextUtil.setLayoutBgResource(mContext, mNewsDetailLoaddingWrapper, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, bgLayout, R.color.color6);
        TextUtil.setTextColor(mContext, textAni, R.color.color3);
        ImageUtil.setAlphaProgressBar(imageAni);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        TextUtil.setLayoutBgColor(mContext, mNewsDetailList, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, adtvTitle, R.color.color9);
        TextUtil.setTextColor(mContext, adtvTitle, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, adtvType, R.drawable.tag_detail_ad_shape);
        TextUtil.setTextColor(mContext, adtvType, R.color.color11);
        TextUtil.setTextColor(mContext, footView_tv, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, detail_shared_ViewPointTitleLayout, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, detail_shared_hotComment_Line1, R.color.color1);
        TextUtil.setLayoutBgResource(mContext, detail_shared_hotComment_Line2, R.color.color5);
        TextUtil.setTextColor(mContext, detail_ViewPoint, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, detail_ViewPoint_Line1, R.color.color1);
        TextUtil.setLayoutBgResource(mContext, detail_ViewPoint_Line2, R.color.color5);
        ImageUtil.setAlphaImage(adImageView);
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
            detail_shared_ViewPointTitleLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void loadData() {
        if (NetUtil.checkNetWork(mContext)) {
            bgLayout.setVisibility(View.VISIBLE);
            isRefresh = true;
            mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
            bgLayout.setVisibility(View.VISIBLE);
            if (mNewsFeed != null) {
                mNid = mNewsFeed.getNid() + "";
            }
            User user = SharedPreManager.mInstance(mContext).getUser(mContext);
            if (user != null) {
                mUserId = user.getMuid() + "";
            }
//            mNid = "19560218";
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            NewsDetailRequest<NewsDetail> feedRequest = new NewsDetailRequest<>(Request.Method.GET, new TypeToken<NewsDetail>() {
            }.getType(), HttpConstant.URL_FETCH_CONTENT + "nid=" + mNid + "&uid=" + mUserId, new Response.Listener<NewsDetail>() {

                @Override
                public void onResponse(NewsDetail result) {
                    loadADData();
                    isRefresh = false;
                    mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                    if (bgLayout.getVisibility() == View.VISIBLE) {
                        bgLayout.setVisibility(View.GONE);
                    }
                    if (result != null) {
                        mResult = result;
                        mNewsFeed = convert2NewsFeed(result);
                        if (mDetailWebView != null) {
                            mDetailWebView.loadDataWithBaseURL(null, TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL),
                                    SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)),
                                    "text/html", "utf-8", null);
                        }
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
        }
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mDetailLeftBack) {
            if (mAlphaAnimationOut != null) {
                mRootView.startAnimation(mAlphaAnimationOut);
                mAlphaAnimationOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mRootView.setVisibility(GONE);
                        destroy();
                        mRootView.removeAllViews();
                        mRootView.destroyDrawingCache();
                        mRootView = null;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        } else if (view.getId() == R.id.mNewsDetailLoaddingWrapper) {
            loadData();
        }
    }

    public void destroy() {
        /**2016年8月31日 冯纪纲 解决webview内存泄露的问题*/
        if (mNewsDetailHeaderView != null && mDetailWebView != null) {
            ((ViewGroup) mDetailWebView.getParent()).removeView(mDetailWebView);
        }
        mDetailWebView.removeAllViews();
        mDetailWebView.destroy();
        mDetailWebView = null;
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
}
