package com.news.sdk.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.news.sdk.adapter.NewsDetailFgtAdapter;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.database.NewsDetailCommentDao;
import com.news.sdk.entity.ADLoadNewsFeedEntity;
import com.news.sdk.entity.NewsDetail;
import com.news.sdk.entity.NewsDetailComment;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.RelatedItemEntity;
import com.news.sdk.entity.User;
import com.news.sdk.javascript.VideoJavaScriptBridge;
import com.news.sdk.net.volley.DetailOperateRequest;
import com.news.sdk.net.volley.NewsDetailADRequestPost;
import com.news.sdk.net.volley.NewsDetailRequest;
import com.news.sdk.net.volley.RelatePointRequestPost;
import com.news.sdk.utils.AdUtil;
import com.news.sdk.utils.AuthorizedUserUtil;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.TextViewExtend;
import com.news.sdk.widget.webview.LoadWebView;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * 新闻详情页
 */
public class NewsDetailFgt extends Fragment implements NativeAD.NativeAdListener {
    public static final String KEY_DETAIL_RESULT = "key_detail_result";
    private LoadWebView mDetailWebView;
    private NewsDetail mResult;
    private SharedPreferences mSharedPreferences;
    private PullToRefreshListView mNewsDetailList;
    private NewsDetailFgtAdapter mAdapter;
    private User mUser;
    private RelativeLayout bgLayout;
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
    private RelativeLayout detail_shared_ShareImageLayout,
            detail_Hot_Layout, relativeLayout_attention,
            detail_shared_ViewPointTitleLayout, adLayout;
    private ImageView detail_shared_AttentionImage, iv_attention_icon;
    private TextView detail_shared_MoreComment, detail_hotComment, detail_ViewPoint, attention_btn;
    private View detail_shared_hotComment_Line1, detail_shared_hotComment_Line2, detail_ViewPoint_Line1, detail_ViewPoint_Line2;
    private LayoutInflater inflater;
    ViewGroup container;
    private RefreshPageBroReceiver mRefreshReceiver;
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
    View rootView;
    private Context mContext;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mContext = getActivity();
        mDocid = arguments.getString(KEY_NEWS_DOCID);
        mNewID = arguments.getString(KEY_NEWS_ID);
        mTitle = arguments.getString(KEY_NEWS_TITLE);
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        mResult = (NewsDetail) arguments.getSerializable(KEY_DETAIL_RESULT);
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mRequestManager = Glide.with(this);
        if (mRefreshReceiver == null) {
            mRefreshReceiver = new RefreshPageBroReceiver();
            IntentFilter filter = new IntentFilter(NewsDetailAty2.ACTION_REFRESH_COMMENT);
            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            mContext.registerReceiver(mRefreshReceiver, filter);
        }
        mNativeAD = new NativeAD(QiDianApplication.getInstance().getAppContext(), CommonConstant.APPID, CommonConstant.NEWS_DETAIL_GDT_SDK_BIGPOSID, this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_news_detail_listview, null);
        this.inflater = inflater;
        this.container = container;
        mUser = SharedPreManager.mInstance(mContext).getUser(mContext);
        mNewsDetailList = (PullToRefreshListView) rootView.findViewById(R.id.fgt_new_detail_PullToRefreshListView);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        bgLayout.setVisibility(View.GONE);
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
                    ItemRecod itemRecord = (ItemRecod) recordSp.get(firstVisibleItem);
                    if (null == itemRecord) {
                        itemRecord = new ItemRecod();
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
                    ItemRecod itemRecod = (ItemRecod) recordSp.get(i);
                    if (null != itemRecod) {
                        height += itemRecod.height;
                    }
                }
                ItemRecod itemRecod = (ItemRecod) recordSp.get(mCurrentFirstVisibleItem);
                if (null == itemRecod) {
                    itemRecod = new ItemRecod();
                }
                return height - itemRecod.top;
            }
        });
        mAdapter = new NewsDetailFgtAdapter(mContext, null);
        mNewsDetailList.setAdapter(mAdapter);
        addHeadView(inflater, container);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
                loadADData();
            }
        }, 1000);
        return rootView;
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

    class ItemRecod {
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

    @Override
    public void onDetach() {
        super.onDetach();
        if (mRefreshReceiver != null) {
            mContext.unregisterReceiver(mRefreshReceiver);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CommonConstant.REQUEST_ATTENTION_CODE && resultCode == CommonConstant.RESULT_ATTENTION_CODE) {
            isAttention = data.getBooleanExtra(CommonConstant.KEY_ATTENTION_CONPUBFLAG, false);
            if (isAttention) {
                setAttentionStatus(true);
                mResult.setConpubflag(1);
            } else {
                setAttentionStatus(false);
                mResult.setConpubflag(0);
            }
        }
    }

    public void addHeadView(LayoutInflater inflater, ViewGroup container) {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ListView lv = mNewsDetailList.getRefreshableView();
        //第1部分的WebView
        mNewsDetailHeaderView = (LinearLayout) inflater.inflate(R.layout.fgt_news_detail, container, false);
        mNewsDetailHeaderView.setLayoutParams(layoutParams);
        lv.addHeaderView(mNewsDetailHeaderView);
        mNewsDetailHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        mDetailWebView = new LoadWebView(mContext.getApplicationContext());
        mDetailWebView.setLayoutParams(params);
//        if (Build.VERSION.SDK_INT >= 19) {//防止视频加载不出来。
//            mDetailWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            mDetailWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
        mDetailWebView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        mDetailWebView.getSettings().setJavaScriptEnabled(true);
        mDetailWebView.getSettings().setDatabaseEnabled(true);
        mDetailWebView.getSettings().setDomStorageEnabled(true);
        mDetailWebView.getSettings().setLoadsImagesAutomatically(true);
        mDetailWebView.getSettings().setBlockNetworkImage(false);
        mDetailWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mDetailWebView.getSettings().setLoadWithOverviewMode(true);
        mDetailWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mDetailWebView.addJavascriptInterface(new VideoJavaScriptBridge((NewsDetailAty2) mContext), "VideoJavaScriptBridge");
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
                String contentUrl="https://m.baidu.com/s?from=1019278a&word="+keyword;
                Intent intent=new Intent(mContext,RelevantViewWebviewAty.class);
                intent.putExtra(RelevantViewWebviewAty.KEY_URL,contentUrl);
                startActivity(intent);

//                view.loadUrl(url);
//                if (openWithWebView(url)) {//如果是超链接，执行此方法
//                    startIntentBrowser("com.lieying.browser", url);
//                } else {
//                    Intent intent = new Intent();
//                    intent.setAction("android.intent.action.VIEW");
//                    Uri content_url = Uri.parse("http://www.baidu.com");
//                    intent.setData(content_url);
//                    intent.setClassName("com.lieying.browser", "com.lieying.browser.BrowserActivity");
//                    startActivity(intent);
//                }
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
//        mDetailWebView.loadDataWithBaseURL(null, TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL),
//                SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)),
//                "text/html", "utf-8", null);
        //梁帅：判断图片是不是不显示
//        if(SharedPreManager.mInstance(getActivity()).getBoolean(CommonConstant.FILE_USER,CommonConstant.TYPE_SHOWIMAGES)){
//            mDetailWebView.getSettings().setLoadsImagesAutomatically(false);
//        }else{
//            mDetailWebView.getSettings().setLoadsImagesAutomatically(true);
//        }
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
        mViewPointLayout = inflater.inflate(R.layout.detail_relate_layout, container, false);
        mViewPointLayout.setLayoutParams(layoutParams);
        //延时加载热点评论和相关观点
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNewsDetailHeaderView.addView(mViewPointLayout);
            }
        }, 1000);
        //关心
        detail_shared_FriendCircleLayout = (LinearLayout) mViewPointLayout.findViewById(R.id.detail_shared_FriendCircleLayout);
        detail_shared_CareForLayout = (LinearLayout) mViewPointLayout.findViewById(R.id.detail_shared_PraiseLayout);
        detail_shared_AttentionImage = (ImageView) mViewPointLayout.findViewById(R.id.detail_shared_AttentionImage);
        if (mResult.getConflag() == 1) {
            isLike = true;
            detail_shared_AttentionImage.setImageResource(R.drawable.bg_attention);
        } else {
            isLike = false;
            detail_shared_AttentionImage.setImageResource(R.drawable.bg_normal_attention);
        }
        detail_shared_FriendCircleLayout.getParent().requestDisallowInterceptTouchEvent(true);
        detail_shared_FriendCircleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = SharedPreManager.mInstance(mContext).getUser(mContext);
                if (user != null) {
                    if (user.isVisitor()) {
                        AuthorizedUserUtil.sendUserLoginBroadcast(mContext);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(CommonConstant.SHARE_WECHAT_MOMENTS_ACTION);
                        intent.putExtra(CommonConstant.SHARE_TITLE, mTitle);
                        intent.putExtra(CommonConstant.SHARE_URL, "http://deeporiginalx.com/news.html?type=0&url=" + mNewID);
                        mContext.sendBroadcast(intent);
                    }
                }
            }
        });
        detail_shared_CareForLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = SharedPreManager.mInstance(mContext).getUser(mContext);
                if (user != null && user.isVisitor()) {
                    AuthorizedUserUtil.sendUserLoginBroadcast(mContext);
                } else {
                    setCareForType();
                }
            }
        });
        //关注
        relativeLayout_attention = (RelativeLayout) mViewPointLayout.findViewById(R.id.relativeLayout_attention);
        iv_attention_icon = (ImageView) mViewPointLayout.findViewById(R.id.iv_attention_icon);
        tv_attention_title = (TextView) mViewPointLayout.findViewById(R.id.tv_attention_title);
        attention_btn = (TextView) mViewPointLayout.findViewById(R.id.attention_btn);
        String icon = mResult.getIcon();
        String name = mResult.getPname();
        if (!TextUtil.isEmptyString(icon)) {
            mRequestManager.load(Uri.parse(icon)).placeholder(R.drawable.detail_attention_placeholder).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, getResources().getColor(R.color.white))).into(iv_attention_icon);
        } else {
            mRequestManager.load("").placeholder(R.drawable.detail_attention_placeholder).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, getResources().getColor(R.color.white))).into(iv_attention_icon);
        }
        if (!TextUtil.isEmptyString(name)) {
            tv_attention_title.setText(name);
        }
        isAttention = mResult.getConpubflag() == 1;
        if (isAttention) {
            setAttentionStatus(true);
        } else {
            setAttentionStatus(false);
        }
        relativeLayout_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ChannelItemDao channelItemDao = new ChannelItemDao(mContext);
//                channelItemDao.setFocusOnline();
                if (!TextUtil.isEmptyString(mNewID)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("nid", Long.valueOf(mNewID));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_SUBPUBLISHER, CommonConstant.LOG_PAGE_DETAILPAGE, CommonConstant.LOG_PAGE_ATTENTIONPAGE, jsonObject, true);
                }
                Intent intent = new Intent(mContext, AttentionActivity.class);
                intent.putExtra(CommonConstant.KEY_ATTENTION_TITLE, mResult.getPname());
                intent.putExtra(CommonConstant.KEY_ATTENTION_CONPUBFLAG, mResult.getConpubflag());
                startActivityForResult(intent, CommonConstant.REQUEST_ATTENTION_CODE);
            }
        });
        attention_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = SharedPreManager.mInstance(mContext).getUser(mContext);
                if (user != null && user.isVisitor()) {
                    AuthorizedUserUtil.sendUserLoginBroadcast(mContext);
                } else {
                    addordeleteAttention(true);
                }
            }
        });
        //评论
        detail_shared_ShareImageLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_ShareImageLayout);
        detail_shared_MoreComment = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_MoreComment);
        detail_shared_ViewPointTitleLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_TitleLayout);
        detail_Hot_Layout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_Hot_Layout);
        detail_hotComment = (TextView) mViewPointLayout.findViewById(R.id.detail_hotComment);
        detail_shared_hotComment_Line1 = mViewPointLayout.findViewById(R.id.detail_shared_hotComment_Line1);
        detail_shared_hotComment_Line2 = mViewPointLayout.findViewById(R.id.detail_shared_hotComment_Line2);
        detail_ViewPoint = (TextView) mViewPointLayout.findViewById(R.id.detail_ViewPoint);
        detail_ViewPoint_Line1 = mViewPointLayout.findViewById(R.id.detail_ViewPoint_Line1);
        detail_ViewPoint_Line2 = mViewPointLayout.findViewById(R.id.detail_ViewPoint_Line2);
        mCommentLayout = (LinearLayout) mViewPointLayout.findViewById(R.id.detail_CommentLayout);
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
        detail_shared_MoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailAty2 mActivity = (NewsDetailAty2) mContext;
                if (!mActivity.isCommentPage) {
                    mActivity.isCommentPage = true;
                    mActivity.mNewsDetailViewPager.setCurrentItem(1);
                    mActivity.mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                    mActivity.mDetailCommentNum.setVisibility(View.GONE);
                    if (!TextUtil.isEmptyString(mNewID)) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("nid", Long.valueOf(mNewID));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_COMMENTCLICK, CommonConstant.LOG_PAGE_DETAILPAGE, CommonConstant.LOG_PAGE_COMMENTPAGE, jsonObject, false);
                    }
                }
            }
        });
        final LinearLayout footerView = (LinearLayout) inflater.inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);
        footerView_layout = (LinearLayout) footerView.findViewById(R.id.footerView_layout);
        footerView_layout.setVisibility(View.GONE);
        footView_tv.setVisibility(View.VISIBLE);
        setTheme();
    }


    public void setTheme() {
        if (mDetailWebView != null) {
            mDetailWebView.loadDataWithBaseURL(null, TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL),
                    SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)),
                    "text/html", "utf-8", null);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (!TextUtil.isListEmpty(mComments)) {
            //同步服务器上的评论数据到本地数据库
            //  addCommentInfoToSql(mComments);
            mCommentLayout.removeAllViews();
            addCommentContent(mComments);
        } else {
            detail_shared_MoreComment.setVisibility(View.GONE);
            detail_Hot_Layout.setVisibility(View.GONE);
        }
        TextUtil.setLayoutBgColor(mContext, mNewsDetailList, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, mViewPointLayout, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, detail_shared_ViewPointTitleLayout, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, adtvTitle, R.color.color9);
        TextUtil.setTextColor(mContext, adtvTitle, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, adtvType, R.drawable.tag_detail_ad_shape);
        TextUtil.setTextColor(mContext, adtvType, R.color.color11);
        TextUtil.setTextColor(mContext, footView_tv, R.color.color2);
        TextUtil.setTextColor(mContext, detail_hotComment, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, detail_shared_hotComment_Line1, R.color.color1);
        TextUtil.setLayoutBgResource(mContext, detail_shared_hotComment_Line2, R.color.color5);
        TextUtil.setTextColor(mContext, detail_ViewPoint, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, detail_ViewPoint_Line1, R.color.color1);
        TextUtil.setLayoutBgResource(mContext, detail_ViewPoint_Line2, R.color.color5);
        TextUtil.setLayoutBgResource(mContext, detail_shared_MoreComment, R.drawable.bg_select_comment_more);
        TextUtil.setTextColor(mContext, detail_shared_MoreComment, R.color.color1);
        ImageUtil.setAlphaImage(adImageView);
        TextUtil.setLayoutBgResource(mContext, relativeLayout_attention, R.drawable.bg_detail_attention);
        ImageUtil.setAlphaImage(iv_attention_icon);
        TextUtil.setTextColor(mContext, tv_attention_title, R.color.color2);
        ImageUtil.setAlphaView(attention_btn);
        if (isAttention) {
            TextUtil.setLayoutBgResource(mContext, attention_btn, R.drawable.bg_attention_btn_press);
        } else {
            TextUtil.setLayoutBgResource(mContext, attention_btn, R.drawable.bg_attention_btn_nor);
        }
    }

    private void loadData() {
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        NewsDetailRequest<ArrayList<NewsDetailComment>> feedRequest = new NewsDetailRequest<>(Request.Method.GET, new TypeToken<ArrayList<NewsDetailComment>>() {
        }.getType(), HttpConstant.URL_FETCH_HOTCOMMENTS + "did=" + TextUtil.getBase64(mDocid) +
                (mUser != null ? "&uid=" + SharedPreManager.mInstance(mContext).getUser(mContext).getMuid() : "") +
                "&p=" + (1) + "&c=" + (4), new Response.Listener<ArrayList<NewsDetailComment>>() {

            @Override
            public void onResponse(ArrayList<NewsDetailComment> result) {
                mComments = result;
                if (!TextUtil.isListEmpty(mComments)) {
                    //同步服务器上的评论数据到本地数据库
                    //  addCommentInfoToSql(mComments);
                    addCommentContent(mComments);
                } else {
                    detail_shared_MoreComment.setVisibility(View.GONE);
                    detail_Hot_Layout.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detail_shared_MoreComment.setVisibility(View.GONE);
                detail_Hot_Layout.setVisibility(View.GONE);
            }
        });
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
    }

    private void loadRelatedData() {
        if (!TextUtil.isEmptyString(mNewID)) {
            isLoadDate = true;
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            String requestUrl = HttpConstant.URL_NEWS_RELATED;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("nid", Integer.valueOf(mNewID));
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

    public void setAttentionStatus(boolean isAttention) {
        if (isAttention) {
            attention_btn.setText("已关注");
            Drawable drawable = getResources().getDrawable(R.drawable.btn_attention_press);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            attention_btn.setCompoundDrawables(drawable, null, null, null);
            attention_btn.setPadding(DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 2), DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 2));
            ImageUtil.setAlphaImage(attention_btn);
            TextUtil.setTextColor(mContext, attention_btn, R.color.white);
            TextUtil.setLayoutBgResource(mContext, attention_btn, R.drawable.bg_attention_btn_press);
        } else {
            attention_btn.setText("关注");
            Drawable drawable = getResources().getDrawable(R.drawable.btn_attention_nor);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            attention_btn.setCompoundDrawables(drawable, null, null, null);
            attention_btn.setPadding(DensityUtil.dip2px(mContext, 13), DensityUtil.dip2px(mContext, 2), DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 2));
            ImageUtil.setAlphaImage(attention_btn);
            TextUtil.setTextColor(mContext, attention_btn, R.color.white);
            TextUtil.setLayoutBgResource(mContext, attention_btn, R.drawable.bg_attention_btn_nor);
        }
    }

    public void setCareForType() {
        if (!NetUtil.checkNetWork(mContext)) {
            ToastUtil.toastShort("无法连接到网络，请稍后再试");
            return;
        }
        if (isNetWork) {
            return;
        }
        isNetWork = true;
        JSONObject json = new JSONObject();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        Logger.e("aaa", "type====" + (isLike ? Request.Method.DELETE : Request.Method.POST));
        User user = SharedPreManager.mInstance(mContext).getUser(mContext);
        int userID = 0;
        if (user != null) {
            userID = user.getMuid();
        }
        DetailOperateRequest detailOperateRequest = new DetailOperateRequest((isLike ? Request.Method.DELETE : Request.Method.POST),
                HttpConstant.URL_ADDORDELETE_CAREFOR + "nid=" + mNewID + (userID != 0 ? "&uid=" + userID : ""),

                json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isLike) {
                    isLike = false;
                    detail_shared_AttentionImage.setImageResource(R.drawable.bg_normal_attention);
                    ToastUtil.toastLong("取消关心");
                } else {
                    isLike = true;
                    detail_shared_AttentionImage.setImageResource(R.drawable.bg_attention);
                    ToastUtil.toastLong("将推荐更多此类文章");
                }
                isNetWork = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.toastLong("关心失败");
                isNetWork = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        detailOperateRequest.setRequestHeader(header);
        detailOperateRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(detailOperateRequest);
    }

    public void addordeleteAttention(boolean isAttention) {
        if (isNetWork) {
            return;
        }
        String pname = null;
        try {
            pname = URLEncoder.encode(mResult.getPname(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        User user = SharedPreManager.mInstance(mContext).getUser(mContext);
        int uid = 0;
        if (user != null) {
            uid = user.getMuid();
        }
        isNetWork = true;
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        Logger.e("jigang", "attention url=" + HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&pname=" + pname);
        JSONObject json = new JSONObject();
        DetailOperateRequest request = new DetailOperateRequest(isAttention ? Request.Method.POST : Request.Method.DELETE,
                HttpConstant.URL_ADDORDELETE_ATTENTION + "uid=" + uid + "&pname=" + pname
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                Logger.e("aaa", "json+++++++++++++++++++++++" + data);
                //保存关注信息
                SharedPreManager.mInstance(mContext).addAttention(mResult.getPname());
                if (SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID)) {
//                    ToastUtil.showAttentionSuccessToast(mContext);
                } else {
//                    attentionDetailDialog = new AttentionDetailDialog(mContext, mResult.getPname());
//                    attentionDetailDialog.show();
                    SharedPreManager.mInstance(mContext).save(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID, true);
                }
                setAttentionStatus(true);
                mResult.setConpubflag(1);
                isNetWork = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage().indexOf("2003") != -1) {
                    ToastUtil.toastShort("用户已关注该信息！");
                    setAttentionStatus(true);
                    mResult.setConpubflag(1);
                    return;
                }
                ToastUtil.toastShort("关注失败！");
                isNetWork = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
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

    private void addCommentInfoToSql(ArrayList<NewsDetailComment> mComments) {
        if (TextUtil.isListEmpty(mComments)) {
            int commentNum = mComments.size();
            List<NewsDetailComment> newsDetailCommentItems = new ArrayList<>();
            String[] commentIds = new String[commentNum];
            for (int i = 0; i < commentNum; i++) {
                commentIds[i] = mComments.get(i).getComment_id();
            }
            mNewsDetailCommentDao = new NewsDetailCommentDao(mContext);
            newsDetailCommentItems = mNewsDetailCommentDao.qureyByIds(commentIds);
            if (newsDetailCommentItems == null || newsDetailCommentItems.size() == 0) {
                return;
            } else {
                for (NewsDetailComment ndc : newsDetailCommentItems) {
                    mNewsDetailCommentDao.update(ndc);
                }
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

    public void addCommentContent(final ArrayList<NewsDetailComment> result) {
        detail_Hot_Layout.setVisibility(View.VISIBLE);
        int num;
        int size = result.size();
        if (size > 3) {
            num = 3;
            detail_shared_MoreComment.setVisibility(View.VISIBLE);
        } else {
            num = size;
            detail_shared_MoreComment.setVisibility(View.GONE);
        }
        for (int i = 0; i < num; i++) {
            RelativeLayout ccView = (RelativeLayout) inflater.inflate(R.layout.adapter_list_comment1, container, false);
            View mSelectCommentDivider = ccView.findViewById(R.id.mSelectCommentDivider);
            CommentHolder holder = new CommentHolder(ccView);
            NewsDetailComment comment = result.get(i);
            UpdateCCView(holder, comment);
            if (num - 1 == i) {
                mSelectCommentDivider.setVisibility(View.GONE);
            }
            mCommentLayout.addView(ccView);
        }
    }

    class CommentHolder {
        ImageView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvTime;
        TextViewExtend tvPraiseCount;
        ImageView ivPraise;
        ImageView mSelectCommentDivider;

        public CommentHolder(View convertView) {
            tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
            ivHeadIcon = (ImageView) convertView.findViewById(R.id.iv_user_icon);
            tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
            ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
            tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
            tvTime = (TextViewExtend) convertView.findViewById(R.id.tv_time);
            mSelectCommentDivider = (ImageView) convertView.findViewById(R.id.mSelectCommentDivider);
            ImageUtil.setAlphaImage(ivHeadIcon);
            TextUtil.setTextColor(mContext, tvName, R.color.color1);
            TextUtil.setTextColor(mContext, tvTime, R.color.color3);
            TextUtil.setTextColor(mContext, tvPraiseCount, R.color.color3);
            TextUtil.setTextColor(mContext, tvContent, R.color.color2);
            TextUtil.setLayoutBgResource(mContext, mSelectCommentDivider, R.color.color5);
        }
    }

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                /** 梁帅：判断图片是不是  不显示 */
                mDetailWebView.loadDataWithBaseURL(null, TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL),
                        SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)),
                        "text/html", "utf-8", null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mDetailWebView.setLayoutParams(params);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void UpdateCCView(final CommentHolder holder, final NewsDetailComment comment) {
        if (!TextUtil.isEmptyString(comment.getAvatar())) {
            Uri uri = Uri.parse(comment.getAvatar());
            mRequestManager.load(uri).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
        } else {
            mRequestManager.load("").placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
        }
        holder.tvName.setText(comment.getUname());
        holder.tvContent.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        holder.tvContent.setText(comment.getContent());
        holder.tvTime.setText(comment.getCtime());
        if (comment.getUpflag() == 0) {
            holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
        } else {
            holder.ivPraise.setImageResource(R.drawable.bg_praised);
        }
        ImageUtil.setAlphaImage(holder.ivPraise);
        int count = comment.getCommend();
        if (count == 0) {
            holder.tvPraiseCount.setVisibility(View.INVISIBLE);
        } else {
            holder.tvPraiseCount.setVisibility(View.VISIBLE);
            holder.tvPraiseCount.setText(comment.getCommend() + "");
        }
        holder.ivPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = SharedPreManager.mInstance(mContext).getUser(mContext);
                if (user != null && user.isVisitor()) {
                    AuthorizedUserUtil.sendUserLoginBroadcast(mContext);
                } else {
//                    if ((user.getMuid() + "").equals(comment.getUid())) {
//                        Toast.makeText(mContext, "不能给自己点赞。", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    if (comment.getUpflag() == 0) {
                        comment.setUpflag(1);
                        holder.ivPraise.setImageResource(R.drawable.bg_praised);
                        int num = 0;
                        if (comment.getCommend() == 0) {
                            num = 1;
                        } else {
                            num = comment.getCommend() + 1;
                        }
                        holder.tvPraiseCount.setVisibility(View.VISIBLE);
                        comment.setCommend(num);
                        holder.tvPraiseCount.setText(num + "");
                        addNewsLove(user, comment, true);
                    } else {
                        comment.setUpflag(0);
                        holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
                        int num = 0;
                        if (comment.getCommend() != 0) {
                            num = comment.getCommend() - 1;
                        }
                        if (num == 0) {
                            holder.tvPraiseCount.setVisibility(View.INVISIBLE);
                        }
                        comment.setCommend(num);
                        holder.tvPraiseCount.setText(num + "");
                        addNewsLove(user, comment, false);
                    }
                }
            }
        });
    }

    private boolean isRefresh;

    private void addNewsLove(User user, NewsDetailComment comment, final boolean isAdd) {
        if (isRefresh) {
            return;
        }
        isRefresh = true;
        try {
            String name = URLEncoder.encode(user.getUserName(), "utf-8");
            String cid = URLEncoder.encode(comment.getId(), "utf-8");
            user.setUserName(name);
            comment.setId(cid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        DetailOperateRequest request = new DetailOperateRequest(isAdd ? Request.Method.POST : Request.Method.DELETE,
                HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&cid=" + comment.getId()
                , new JSONObject().toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                if (!TextUtil.isEmptyString(data)) {
                    isRefresh = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isRefresh = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(getActivity()).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public void isBgLayoutSuccess() {
        if (isWebSuccess && bgLayout.getVisibility() == View.VISIBLE) {
            bgLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDetailWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDetailWebView.onPause();
    }

    @Override
    public void onDestroy() {
        /**2016年8月31日 冯纪纲 解决webview内存泄露的问题*/
        if (mNewsDetailHeaderView != null && mDetailWebView != null) {
            ((ViewGroup) mDetailWebView.getParent()).removeView(mDetailWebView);
        }
        mDetailWebView.removeAllViews();
        mDetailWebView.destroy();
        mDetailWebView = null;
        super.onDestroy();
    }

    protected boolean openWithWebView(String url) {//处理判断url的合法性
        if (url.startsWith("http:") || url.startsWith("https:")) {
            return true;
        }
        return false;
    }

    /**
     * 判断跳转猎鹰浏览器
     *
     * @param packageName
     * @param url
     */
    public void startIntentBrowser(String packageName, String url) {
        boolean existLY = false;
        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo packageinfo : packageInfos) {
            String stemp = packageinfo.packageName;
            Logger.v("PACKAGENAME:", stemp);
            if (stemp.equals(packageName)) {
                existLY = true;
                break;
            }
        }
        Intent intent;
        String keyword = "";
        try {
            keyword = URLDecoder.decode(Uri.parse(url).toString().substring(40), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String contentUrl="https://m.baidu.com/s?from=1019278a&word="+keyword;
        if (existLY) {
//            intent = packageManager.getLaunchIntentForPackage(packageName);
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.putExtra(SearchManager.QUERY, keyword);
            intent.setClassName("com.lieying.browser", "com.lieying.browser.BrowserActivity");
            intent.putExtra("back_to_navigation", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(contentUrl));
            startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("url",url);
            intent.setData(Uri.parse(contentUrl));
            startActivity(intent);
        }
    }

    /**
     * 判断跳转猎鹰浏览器
     *
     * @param packageName
     * @param url
     */
    public void startIntent(View packageName, String url) {
        boolean existLY = false;
        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo packageinfo : packageInfos) {
            String stemp = packageinfo.packageName;
            Logger.v("PACKAGENAME:", stemp);
            if (stemp.equals(packageName)) {
                existLY = true;
                break;
            }
        }
        Intent intent;
        String keyword = "";
        try {
            keyword = URLDecoder.decode(Uri.parse(url).toString().substring(40), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String contentUrl="https://m.baidu.com/s?from=1019278a&word="+keyword;
        if (existLY) {
//            intent = packageManager.getLaunchIntentForPackage(packageName);
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.putExtra(SearchManager.QUERY, keyword);
            intent.setClassName("com.lieying.browser", "com.lieying.browser.BrowserActivity");
            intent.putExtra("back_to_navigation", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(contentUrl));
            startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("url",url);
            intent.setData(Uri.parse(contentUrl));
            startActivity(intent);
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
}
