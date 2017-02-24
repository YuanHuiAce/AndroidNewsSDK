package com.news.yazhidao.pages;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailFgtAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsDetailCommentDao;
import com.news.yazhidao.entity.ADLoadNewsFeedEntity;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.javascript.VideoJavaScriptBridge;
import com.news.yazhidao.net.volley.NewsDetailADRequestPost;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.utils.AdUtil;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.webview.LoadWebView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.news.yazhidao.utils.manager.SharedPreManager.mInstance;

/**
 * Created by fengjigang on 16/3/31.
 * 新闻详情页
 */
public class NewsDetailFgt extends Fragment {
    public static final String KEY_DETAIL_RESULT = "key_detail_result";
    private LoadWebView mDetailWebView;
    private NewsDetail mResult;
    private SharedPreferences mSharedPreferences;
    private PullToRefreshListView mNewsDetailList;
    private NewsDetailFgtAdapter mAdapter;
    private boolean isListRefresh;
    private User user;
    private RelativeLayout bgLayout;
    private String mDocid, mTitle, mPubName, mPubTime, mCommentCount, mNewID;
    private ArrayList<NewsDetailComment> mComments = new ArrayList<>();
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_ID = "key_news_id";
    public static final String KEY_NEWS_TITLE = "key_news_title";
    public static final int REQUEST_CODE = 1030;
    private LinearLayout detail_shared_FriendCircleLayout,
            detail_shared_CareForLayout,
            mCommentLayout,
            mNewsDetailHeaderView;

    private TextView detail_shared_PraiseText,
            detail_shared_Text,
            detail_shared_hotComment;
    private RelativeLayout detail_shared_ShareImageLayout, detail_shared_MoreComment,
            detail_shared_CommentTitleLayout,
            detail_shared_ViewPointTitleLayout, adLayout;
    private ImageView detail_shared_AttentionImage;
    private int CommentType = 0;
    private LayoutInflater inflater;
    ViewGroup container;
    private RefreshPageBroReceiver mRefreshReceiver;
    private boolean isWebSuccess, isCommentSuccess, isCorrelationSuccess;
    private TextView mDetailSharedHotComment;
    boolean isNoHaveBean;
    private final int LOAD_MORE = 0;
    private final int LOAD_BOTTOM = 1;
    private boolean isLike;
    private NewsDetailCommentDao mNewsDetailCommentDao;

    private TextView footView_tv;
    private ProgressBar footView_progressbar;
    private LinearLayout footerView_layout;
    private boolean isBottom;

    private boolean isLoadDate;
    private boolean isNetWork;
    public boolean isClickMyLike;
    FrameLayout video;
    View rootView;
    private Context mContext;
    private RequestManager mRequestManager;
    private int mScreenWidth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MobclickAgent.onEvent(getActivity(),"yazhidao_user_enter_detail_page");
        Bundle arguments = getArguments();
        mContext = getActivity();
        mDocid = arguments.getString(KEY_NEWS_DOCID);
        mNewID = arguments.getString(KEY_NEWS_ID);
        mTitle = arguments.getString(KEY_NEWS_TITLE);
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mResult = (NewsDetail) arguments.getSerializable(KEY_DETAIL_RESULT);
        mSharedPreferences = getActivity().getSharedPreferences("showflag", 0);
        mRequestManager = Glide.with(this);
        if (mRefreshReceiver == null) {
            mRefreshReceiver = new RefreshPageBroReceiver();
            IntentFilter filter = new IntentFilter(NewsDetailAty2.ACTION_REFRESH_COMMENT);
            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            getActivity().registerReceiver(mRefreshReceiver, filter);
        }

    }


    private int oldLastPositon;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_news_detail_listview, null);
        this.inflater = inflater;
        this.container = container;
        user = SharedPreManager.mInstance(getActivity()).getUser(getActivity());
        // 声明video，把之后的视频放到这里面去
//        video = (FrameLayout) rootView.findViewById(R.id.video);
        mNewsDetailList = (PullToRefreshListView) rootView.findViewById(R.id.fgt_new_detail_PullToRefreshListView);
        TextUtil.setLayoutBgColor(mContext, mNewsDetailList, R.color.white);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        bgLayout.setVisibility(View.GONE);
        mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        mNewsDetailList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isLoadDate) {
                    return;
                }

                isLoadDate = true;
                if (MAXPage > viewpointPage) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            beanList.addAll(beanPageList.get(viewpointPage));
                            viewpointPage++;
                            mAdapter.setNewsFeed(beanList);
                            mAdapter.notifyDataSetChanged();
                            mNewsDetailList.onRefreshComplete();
                            if (MAXPage <= viewpointPage) {
                                mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
                                footView_tv.setText("内容加载完毕");
                            }
                            isLoadDate = false;
                        }
                    }, 1000);
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
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            Logger.e("aaa", "滑动到底部");
                            isBottom = true;


                        } else {
                            isBottom = false;
                            Logger.e("aaa", "在33333isBottom ==" + isBottom);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
//                if (beanList.size() == 0) {
//
//                    return;
//                }
//                int lastPositon =  absListView.getLastVisiblePosition();
//                Logger.e("aaa", "lastPositon====" + lastPositon);
//                Message msg = new Message();
//                if(lastPositon -2 ==beanList.size()-1){
//                    if (MAXPage > viewpointPage) {
//                        if(oldLastPositon == lastPositon){
//                            return;
//                        }
//                        msg.what = LOAD_MORE;
//                        mHandler.sendMessage(msg);
//                    }else{
//                        msg.what = LOAD_BOTTOM;
//                        mHandler.sendMessage(msg);
//
//                    }
//                }
//                oldLastPositon = lastPositon;
            }
        });
        mAdapter = new NewsDetailFgtAdapter(getActivity());

        mNewsDetailList.setAdapter(mAdapter);
        addHeadView(inflater, container);
        loadData();
        loadADData();
        return rootView;
    }

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case LOAD_MORE:
//                    beanList.addAll(beanPageList.get(viewpointPage));
//                    viewpointPage++;
//                    mAdapter.setNewsFeed(beanList);
//                    mAdapter.notifyDataSetChanged();
//                    mNewsDetailList.onRefreshComplete();
//                    break;
//                case LOAD_BOTTOM:
//                    if (isNoHaveBean) {
//                        return;
//                    }
//
//                    isNoHaveBean = true;
//
//                    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
//                    ListView lv = mNewsDetailList.getRefreshableView();
//                    LinearLayout mNewsDetailFootView = (LinearLayout) inflater.inflate(R.layout.detail_footview_layout, container, false);
//                    mNewsDetailFootView.setLayoutParams(layoutParams);
//                    lv.addFooterView(mNewsDetailFootView);
//                    break;
//            }
//        }
//    };

    @Override
    public void onDetach() {
        super.onDetach();
        if (mRefreshReceiver != null) {
            getActivity().unregisterReceiver(mRefreshReceiver);
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
                Logger.e("aaa", "webView的点击");
            }
        });
//        TextUtil.setLayoutBgColor(mContext,mNewsDetailHeaderView,R.color.color7);
        mDetailWebView = new LoadWebView(getActivity().getApplicationContext());
        mDetailWebView.setLayoutParams(params);
//        if (Build.VERSION.SDK_INT >= 19) {//防止视频加载不出来。
//            mDetailWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            mDetailWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
        mDetailWebView.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));
        mDetailWebView.getSettings().setJavaScriptEnabled(true);
        mDetailWebView.getSettings().setDatabaseEnabled(true);
        mDetailWebView.getSettings().setDomStorageEnabled(true);
        mDetailWebView.getSettings().setLoadsImagesAutomatically(true);
        mDetailWebView.getSettings().setBlockNetworkImage(false);
        mDetailWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mDetailWebView.getSettings().setLoadWithOverviewMode(true);
        mDetailWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mDetailWebView.addJavascriptInterface(new VideoJavaScriptBridge(this.getActivity()), "VideoJavaScriptBridge");
//        mDetailWebView.loadData(TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL)), "text/html;charset=UTF-8", null);
        /** 梁帅：判断图片是不是  不显示 */


//        mDetailWebView.loadUrl("http://deeporiginalx.com/content.html?type=0&nid="+mNewID);
        mDetailWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                Log.i("tag", "url===" + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i("tag", description + "===" + failingUrl);
                //view.loadData("ERROR: " + description,"text/plain","utf8");
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //handler.cancel(); 默认的处理方式，WebView变成空白页
//                        //接受证书
                handler.proceed();
                //handleMessage(Message msg); 其他处理
            }
        });
        mDetailWebView.loadDataWithBaseURL(null, TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL),
                mInstance(getActivity()).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)),
                "text/html", "utf-8", null);
        mDetailWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                Logger.i("TAG", url);
                // view.loadUrl(url);
                if (openWithWevView(url)) {//如果是超链接，执行此方法
                    startIntentBrowser("com.lieying.browser", url);
                } else {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://www.baidu.com");
                    intent.setData(content_url);
                    intent.setClassName("com.lieying.browser", "com.lieying.browser.BrowserActivity");
                    startActivity(intent);
                }
                return true;
            }
        });
        //梁帅：判断图片是不是  不显示
//        if(SharedPreManager.mInstance(getActivity()).getBoolean(CommonConstant.FILE_USER,CommonConstant.TYPE_SHOWIMAGES)){
//            mDetailWebView.getSettings().setLoadsImagesAutomatically(false);
//        }else{
//            mDetailWebView.getSettings().setLoadsImagesAutomatically(true);
//
//        }
        mDetailWebView.setDf(new LoadWebView.PlayFinish() {
            @Override
            public void After() {
                Logger.e("aaa", "22222");
                isWebSuccess = true;
                isBgLayoutSuccess();
//                Log.e("aaa","1111");
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


        //第2部分的CommentTitle
        final View mCommentTitleView = inflater.inflate(R.layout.detail_shared_layout, container, false);
        mCommentTitleView.setLayoutParams(layoutParams);
//        mNewsDetailHeaderView.addView(mCommentTitleView);
        detail_shared_FriendCircleLayout = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_shared_FriendCircleLayout);
        detail_shared_CareForLayout = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_shared_PraiseLayout);
        mDetailSharedHotComment = (TextView) mCommentTitleView.findViewById(R.id.detail_shared_hotComment);
        detail_shared_PraiseText = (TextView) mCommentTitleView.findViewById(R.id.detail_shared_PraiseText);
        detail_shared_AttentionImage = (ImageView) mCommentTitleView.findViewById(R.id.detail_shared_AttentionImage);
        mCommentLayout = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_shared_Layout);
        detail_shared_CommentTitleLayout = (RelativeLayout) mCommentTitleView.findViewById(R.id.detail_shared_TitleLayout);

        detail_shared_CareForLayout.setVisibility(View.GONE);
        detail_shared_FriendCircleLayout.setVisibility(View.GONE);
        mCommentTitleView.findViewById(R.id.detail_shared_Text).setVisibility(View.GONE);

        detail_shared_FriendCircleLayout.getParent().requestDisallowInterceptTouchEvent(true);
        detail_shared_FriendCircleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.e("aaa", "点击朋友圈");
//                ShareSdkHelper.ShareToPlatformByNewsDetail(getActivity(), WechatMoments.NAME,mTitle , mNewID, "1");
//                MobclickAgent.onEvent(getActivity(),"yazhidao_detail_middle_share_weixin");
            }
        });
        detail_shared_CareForLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MobclickAgent.onEvent(getActivity(),"yazhidao_detail_middle_like");
                Logger.e("aaa", "点击点赞");
                if (isLike) {
                    isLike = false;
                    detail_shared_AttentionImage.setImageResource(R.drawable.bg_normal_attention);
                } else {
                    isLike = true;
                    mShowCareforLayout.show();
                    detail_shared_AttentionImage.setImageResource(R.drawable.bg_attention);
                }


            }
        });
        ////第3部分的CommentContent(这个内容是动态的获取数据后添加)

        //第4部分的viewPointContent
        final View mViewPointLayout = inflater.inflate(R.layout.detail_relate_layout, container, false);
        mViewPointLayout.setLayoutParams(layoutParams);
        //延时加载热点评论和相关观点
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNewsDetailHeaderView.addView(mCommentTitleView);
                mNewsDetailHeaderView.addView(mViewPointLayout);
            }
        }, 1500);

        detail_shared_ShareImageLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_ShareImageLayout);
        detail_shared_Text = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_Text);
        detail_shared_MoreComment = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_MoreComment);
        detail_shared_hotComment = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_hotComment);
        detail_shared_ViewPointTitleLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_TitleLayout);
        adLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.adLayout);
        detail_shared_ShareImageLayout.setVisibility(View.GONE);
        detail_shared_Text.setVisibility(View.GONE);
        detail_shared_MoreComment.setVisibility(View.GONE);
        detail_shared_MoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailAty2 mActivity = (NewsDetailAty2) getActivity();
                if (!mActivity.isCommentPage) {
                    mActivity.isCommentPage = true;
                    mActivity.mNewsDetailViewPager.setCurrentItem(1);
                    mActivity.mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                    mActivity.mDetailCommentNum.setVisibility(View.GONE);
                }
            }
        });

        detail_shared_hotComment.setText("相关观点");
        TextUtil.setLayoutBgColor(mContext, (LinearLayout) mViewPointLayout, R.color.white);
        TextUtil.setLayoutBgColor(mContext, detail_shared_ViewPointTitleLayout, R.color.white);
        TextUtil.setTextColor(mContext, detail_shared_hotComment, R.color.newsFeed_titleColor);

        final LinearLayout footerView = (LinearLayout) inflater.inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);
        footerView_layout = (LinearLayout) footerView.findViewById(R.id.footerView_layout);
        footerView_layout.setVisibility(View.GONE);


    }


//    addNewsLoveListener addNewsLoveListener = new addNewsLoveListener() {
//        @Override
//        public void addLove(NewsDetailComment comment, int position) {
//            addNewsLove(comment);
//        }
//    };


    private void loadData() {

        Logger.e("jigang", "fetch comments url=" + HttpConstant.URL_FETCH_HOTCOMMENTS + "did=" + TextUtil.getBase64(mDocid) + "&p=" + (1) + "&c=" + (20));
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        NewsDetailRequest<ArrayList<NewsDetailComment>> feedRequest = null;
        NewsDetailRequest<ArrayList<RelatedItemEntity>> related = null;
//        feedRequest = new NewsDetailRequest<ArrayList<NewsDetailComment>>(Request.Method.GET, new TypeToken<ArrayList<NewsDetailComment>>() {
//        }.getType(), HttpConstant.URL_FETCH_HOTCOMMENTS + "did=" + TextUtil.getBase64(mDocid) +
//                (user!=null?"&uid="+SharedPreManager.mInstance(getActivity()).getUser(getActivity()).getMuid():"")+
//                "&p=" + (1)+ "&c=" + (20)
//                , new Response.Listener<ArrayList<NewsDetailComment>>() {
//
//            @Override
//            public void onResponse(ArrayList<NewsDetailComment> result) {
//                isCommentSuccess = true;
//                isBgLayoutSuccess();
//                mNewsDetailList.onRefreshComplete();
//                Logger.e("jigang", "network success, comment" + result);
//
//                if (!TextUtil.isListEmpty(result)) {
//                    mComments = result;
//                    for(int i = 0;i<mComments.size();i++){
//                        if(i>2){
//                            mComments.remove(i);
//                        }
//                    }
////                        mAdapter.setCommentList(mComments);
////                        mAdapter.notifyDataSetChanged();
//                    Logger.d("aaa", "评论加载完毕！！！！！！");
//                    //同步服务器上的评论数据到本地数据库
//                    //  addCommentInfoToSql(mComments);
//                    mDetailSharedHotComment.setText("热门评论");//
//                    addCommentContent(result);
//                } else {
//                    detail_shared_CommentTitleLayout.setVisibility(View.GONE);
//                    detail_shared_MoreComment.setVisibility(View.GONE);
//
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                isCommentSuccess = true;
//                isBgLayoutSuccess();
//                mNewsDetailList.onRefreshComplete();
//                detail_shared_CommentTitleLayout.setVisibility(View.GONE);
//                detail_shared_MoreComment.setVisibility(View.GONE);
//                Logger.e("jigang", "URL_FETCH_HOTCOMMENTS  network fail");
//
//            }
//        });
//        Logger.e("jigang", "URL_NEWS_RELATED=" + HttpConstant.URL_NEWS_RELATED + "nid=" + mNewID);
//        requestQueue.add(feedRequest);
//        setNoRelatedDate();
        related = new NewsDetailRequest<ArrayList<RelatedItemEntity>>(Request.Method.GET,
                new TypeToken<ArrayList<RelatedItemEntity>>() {
                }.getType(),

                HttpConstant.URL_NEWS_RELATED + "nid=" + mNewID,
                new Response.Listener<ArrayList<RelatedItemEntity>>() {
                    @Override
                    public void onResponse(ArrayList<RelatedItemEntity> relatedItemEntities) {

                        Logger.e("jigang", "URL_NEWS_RELATED  network success~~" + relatedItemEntities.toString());
                        isCorrelationSuccess = true;
                        isBgLayoutSuccess();
                        /**去掉跳转到h5
                         */
                        Iterator<RelatedItemEntity> iterator = relatedItemEntities.iterator();
                        while (iterator.hasNext()) {
                            RelatedItemEntity relatedItemEntity = iterator.next();
                            if (!relatedItemEntity.getUrl().contains("deeporiginalx.com")) {
                                iterator.remove();
                            }
                        }
//                            ArrayList<RelatedItemEntity> relatedItemEntities = response.getSearchItems();
//                            Logger.e("jigang", "network success RelatedEntity~~" + response);

                        if (!TextUtil.isListEmpty(relatedItemEntities)) {
                            setBeanPageList(relatedItemEntities);
                        } else {
                            setNoRelatedDate();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isCorrelationSuccess = true;
                        isBgLayoutSuccess();
                        setNoRelatedDate();
                        Logger.e("jigang", "URL_NEWS_RELATED  network error~~");
                    }
                });
        //热门评论  不添加
        detail_shared_CommentTitleLayout.setVisibility(View.GONE);
        detail_shared_MoreComment.setVisibility(View.GONE);
//        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        related.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));


        requestQueue.add(related);


    }

    public void setNoRelatedDate() {
        RelatedItemEntity entity = new RelatedItemEntity();
        entity.setUrl("-1");
        ArrayList<RelatedItemEntity> relatedItemEntities = new ArrayList<RelatedItemEntity>();
//        if(relatedItemEntities == null||relatedItemEntities.size() == 0){
//            relatedItemEntities = new ArrayList<RelatedItemEntity>();
//        }
        mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        relatedItemEntities.add(entity);
        mAdapter.setNewsFeed(relatedItemEntities);
        mAdapter.notifyDataSetChanged();
        detail_shared_ViewPointTitleLayout.setVisibility(View.GONE);
        if (footerView_layout.getVisibility() == View.VISIBLE) {
            footerView_layout.setVisibility(View.GONE);
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
            mNewsDetailCommentDao = new NewsDetailCommentDao(getActivity());
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

    ArrayList<ArrayList<RelatedItemEntity>> beanPageList = new ArrayList<ArrayList<RelatedItemEntity>>();
    ArrayList<RelatedItemEntity> beanList = new ArrayList<RelatedItemEntity>();
    int viewpointPage = 0;
    int pageSize = 6;
    int MAXPage;

    //
    public void setBeanPageList(ArrayList<RelatedItemEntity> relatedItemEntities) {
        Logger.e("aaa", "time:================比较前=================");
        for (int i = 0; i < relatedItemEntities.size(); i++) {
            Logger.e("aaa", "time:===" + relatedItemEntities.get(i).getPtime());
        }
        Collections.sort(relatedItemEntities);
        Logger.e("aaa", "time:================比较====后=================");
        for (int i = 0; i < relatedItemEntities.size(); i++) {
            Logger.e("aaa", "time:===" + relatedItemEntities.get(i).getPtime());
        }
        int listSice = relatedItemEntities.size();
        int page = (listSice / pageSize) + (listSice % pageSize == 0 ? 0 : 1);
        MAXPage = page;
        int mYear = 0;
        for (int i = 0; i < page; i++) {
            ArrayList<RelatedItemEntity> listBean = new ArrayList<RelatedItemEntity>();
            for (int j = 0; j < pageSize; j++) {
                int itemPosition = j + i * pageSize;
                if (itemPosition + 1 > listSice) {
                    break;
                }
                Logger.e("aaa", "page:" + itemPosition);
                Calendar calendar = DateUtil.strToCalendarLong(relatedItemEntities.get(itemPosition).getPtime());

                int thisYear = calendar.get(Calendar.YEAR);//获取年份
                if (thisYear != mYear) {
                    mYear = thisYear;
                    relatedItemEntities.get(itemPosition).setYearFrist(true);
                }

                listBean.add(relatedItemEntities.get(itemPosition));
            }
            beanPageList.add(listBean);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                beanList.addAll(beanPageList.get(viewpointPage));
                viewpointPage++;
                mAdapter.setNewsFeed(beanList);
                mAdapter.notifyDataSetChanged();
                if (MAXPage <= viewpointPage) {
                    mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
                    footView_tv.setText("内容加载完毕");
                } else {
                    if (mNewsDetailList.getMode() != PullToRefreshBase.Mode.PULL_FROM_END) {
                        mNewsDetailList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    }
                }
                if (footerView_layout.getVisibility() == View.GONE) {
                    footerView_layout.setVisibility(View.VISIBLE);
                }
                detail_shared_ViewPointTitleLayout.setVisibility(View.VISIBLE);
            }
        }, 500);

    }

    private void addNewsLove(NewsDetailComment comment, final int position, final CommentHolder holder) {
        try {
            String name = URLEncoder.encode(user.getUserName(), "utf-8");
            String cid = URLEncoder.encode(comment.getId(), "utf-8");
            user.setUserName(name);
            comment.setId(cid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        Logger.e("jigang", "love url=" + HttpConstant.URL_LOVE_COMMENT + "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName());
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//        NewsLoveRequest<String> loveRequest = new NewsLoveRequest<String>(Request.Method.PUT, new TypeToken<String>() {
//        }.getType(), HttpConstant.URL_LOVE_COMMENT + "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName(), new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String result) {
//                mNewsDetailList.onRefreshComplete();
//                Logger.e("jigang", "network success, love" + result);
//                if (!TextUtil.isEmptyString(result)) {
//                    mComments.get(position).setPraise(true);
//                    mComments.get(position).setLove(Integer.parseInt(result));
//                    holder.ivPraise.setImageResource(R.drawable.bg_praised);
//                    holder.tvPraiseCount.setText(result);
////                    viewList.get(position).invalidate();//刷新界面
////                    mAdapter.notifyDataSetChanged();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mNewsDetailList.onRefreshComplete();
//                Logger.e("jigang", "network fail");
//            }
//        });
//        loveRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
//        requestQueue.add(loveRequest);
    }

    ArrayList<CommentHolder> holderList = new ArrayList<CommentHolder>();
    ArrayList<View> viewList = new ArrayList<View>();
    private View mCCView;

    public void addCommentContent(final ArrayList<NewsDetailComment> result) {
        int listSice = result.size();
        if (listSice == 0) {
            CommentType = 0;
            detail_shared_CommentTitleLayout.setVisibility(View.GONE);
            detail_shared_MoreComment.setVisibility(View.GONE);
//        }else if(listSice == 1){
//            ShowCommentBar();
//            mCCView = inflater.inflate(R.layout.adapter_list_comment1,container,false);
//            CommentHolder holder = new CommentHolder(mCCView);
//            holderList.add(holder);
//
//        }else if(listSice == 2){
//            ShowCommentBar();
//            for(int i = 0; i<listSice ;i++){
//                mCCView = inflater.inflate(R.layout.adapter_list_comment1,container,false);
//                CommentHolder holder = new CommentHolder(mCCView);
//                holderList.add(holder);
//            }
        } else {
            ShowCommentBar();
            for (int i = 0; i < listSice && i < 3; i++) {
                CommentType = i + 1;
                mCCView = inflater.inflate(R.layout.adapter_list_comment1, container, false);
                View mSelectCommentDivider = mCCView.findViewById(R.id.mSelectCommentDivider);
                CommentHolder holder = new CommentHolder(mCCView);

                int position = i;
                NewsDetailComment comment = result.get(i);

                UpdateCCView(holder, comment, position);
                holderList.add(holder);
                viewList.add(mCCView);
                if (i == 2) {
                    mSelectCommentDivider.setVisibility(View.GONE);
                }
                mCommentLayout.addView(mCCView);

            }
        }
    }


    public void ShowCommentBar() {
        if (detail_shared_CommentTitleLayout.getVisibility() == View.GONE) {
            detail_shared_CommentTitleLayout.setVisibility(View.VISIBLE);
        }
        Logger.e("aaa", "mComments.size() = " + mComments.size());
        if (mComments.size() > 3) {
            if (detail_shared_MoreComment.getVisibility() == View.GONE) {
                detail_shared_MoreComment.setVisibility(View.VISIBLE);
            }
        } else {
            if (detail_shared_MoreComment.getVisibility() == View.VISIBLE) {
                detail_shared_MoreComment.setVisibility(View.GONE);
            }
        }
    }

    class CommentHolder {
        ImageView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvPraiseCount;
        ImageView ivPraise;

        public CommentHolder(View convertView) {
            tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
            ivHeadIcon = (ImageView) convertView.findViewById(R.id.iv_user_icon);
            tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
            ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
            tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
        }
    }

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                Logger.e("aaa", "详情页===文字的改变！！！");
//                int size = intent.getIntExtra("textSize", CommonConstant.TEXT_SIZE_NORMAL);
//                mSharedPreferences.edit().putInt("textSize", size).commit();
                /** 梁帅：判断图片是不是  不显示 */
                mDetailWebView.loadDataWithBaseURL(null, TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL),
                        SharedPreManager.mInstance(getActivity()).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)),
                        "text/html", "utf-8", null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mDetailWebView.setLayoutParams(params);
                mAdapter.notifyDataSetChanged();
                CCViewNotifyDataSetChanged();

            } else {
//                Logger.e("jigang", "detailaty refresh br");
//                NewsDetailComment comment = (NewsDetailComment) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
//                mComments.add(0, comment);
//                UpdateCCOneData();
            }

        }
    }

    public void UpdateCCOneData() {
        if (CommentType == 3) {
            CCViewNotifyDataSetChanged();
            ShowCommentBar();
        } else {
            CommentType = CommentType + 1;
            mCCView = inflater.inflate(R.layout.adapter_list_comment1, container, false);
            CommentHolder holder = new CommentHolder(mCCView);
            holderList.add(holder);
            viewList.add(mCCView);
            CCViewNotifyDataSetChanged();
            mCommentLayout.addView(mCCView);
            ShowCommentBar();
        }

    }

    public void CCViewNotifyDataSetChanged() {
        for (int i = 0; i < CommentType; i++) {
            CommentHolder holder = holderList.get(i);
            NewsDetailComment newsDetailComment = mComments.get(i);
            UpdateCCView(holder, newsDetailComment, i);
        }
    }


    public void UpdateCCView(final CommentHolder holder, final NewsDetailComment comment, final int position) {
        final User user = SharedPreManager.mInstance(getActivity()).getUser(getActivity());
        if (!TextUtil.isEmptyString(comment.getAvatar())) {
            Uri uri = Uri.parse(comment.getAvatar());
            mRequestManager.load(uri).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
        } else {
            mRequestManager.load(R.drawable.ic_user_comment_default).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
        }
        holder.tvName.setText(comment.getUname());
        holder.tvPraiseCount.setText(comment.getCommend() + "");

        holder.tvContent.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        holder.tvContent.setText(comment.getContent());
        holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.e("aaa", "点击内容");
            }
        });

        if (comment.getUpflag() == 0) {
            holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
        } else {
            holder.ivPraise.setImageResource(R.drawable.bg_praised);
        }

//        String commentUserid = comment.getUid();
//        if (commentUserid != null && commentUserid.length() != 0&&user != null) {
//
//            if ((user.getMuid()+"").equals(comment.getUid())) {
//                holder.ivPraise.setVisibility(View.GONE);
//            } else {
//                holder.ivPraise.setVisibility(View.VISIBLE);
//            }
//        }

        holder.ivPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (user == null) {
//                    Intent loginAty = new Intent(getActivity(), LoginAty.class);
//                    startActivityForResult(loginAty, REQUEST_CODE);
//                } else {
//                    addNewsLove(comment, position, holder);
//
//                }

            }
        });
    }

    public void isBgLayoutSuccess() {
        if (isCommentSuccess && isWebSuccess && isCorrelationSuccess && bgLayout.getVisibility() == View.VISIBLE) {
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
        super.onDestroy();
        /**2016年8月31日 冯纪纲 解决webview内存泄露的问题*/
        if (mNewsDetailHeaderView != null && mDetailWebView != null) {
            ((ViewGroup) mDetailWebView.getParent()).removeView(mDetailWebView);
        }
        mDetailWebView.removeAllViews();
        mDetailWebView.destroy();
        mDetailWebView = null;
    }

    public interface ShowCareforLayout {
        void show();
    }

    ShowCareforLayout mShowCareforLayout;

    public void setShowCareforLayout(ShowCareforLayout showCareforLayout) {
        mShowCareforLayout = showCareforLayout;
    }

    protected boolean openWithWevView(String url) {//处理判断url的合法性
        // TODO Auto-generated method stub
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
        PackageManager packageManager = getActivity().getPackageManager();
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
        if (existLY) {
//            intent = packageManager.getLaunchIntentForPackage(packageName);
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            String keyword = "";
            try {
                keyword = URLDecoder.decode(Uri.parse(url).toString().substring(40), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            intent.putExtra(SearchManager.QUERY, keyword);
            intent.setClassName("com.lieying.browser", "com.lieying.browser.BrowserActivity");
            intent.putExtra("back_to_navigation", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setData(Uri.parse(url));
            startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("url",url);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }


    }

    private void loadADData() {
        if (SharedPreManager.mInstance(mContext).getUser(mContext) != null) {
            String requestUrl = HttpConstant.URL_NEWS_DETAIL_AD;
            ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
            adLoadNewsFeedEntity.setUid(SharedPreManager.mInstance(mContext).getUser(mContext).getMuid());
            Gson gson = new Gson();
            //加入详情页广告位id
            adLoadNewsFeedEntity.setB(TextUtil.getBase64(AdUtil.getAdMessage(mContext, "237")));
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            Logger.e("aaa", "gson==" + gson.toJson(adLoadNewsFeedEntity));
            Logger.e("ccc", "requestBody==" + gson.toJson(adLoadNewsFeedEntity));
            NewsDetailADRequestPost<ArrayList<NewsFeed>> newsFeedRequestPost = new NewsDetailADRequestPost(requestUrl, gson.toJson(adLoadNewsFeedEntity), new Response.Listener<ArrayList<NewsFeed>>() {
                @Override
                public void onResponse(final ArrayList<NewsFeed> result) {
                    adLayout.setVisibility(View.VISIBLE);
                    final NewsFeed newsFeed = result.get(0);
                    if (newsFeed != null) {
                        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.ll_ad_item_big, null);
                        TextViewExtend title = (TextViewExtend) layout.findViewById(R.id.title_textView);
                        title.setText(newsFeed.getTitle());
                        final ImageView imageView = (ImageView) layout.findViewById(R.id.adImage);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                        int imageWidth = mScreenWidth - DensityUtil.dip2px(mContext, 56);
                        layoutParams.width = imageWidth;
                        layoutParams.height = (int) (imageWidth * 627 / 1200.0f);
                        imageView.setLayoutParams(layoutParams);
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                mRequestManager.load(result.get(0).getImgs().get(0)).into(imageView);
                            }
                        });
                        adLayout.addView(layout);
                        adLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent AdIntent = new Intent(mContext, NewsDetailWebviewAty.class);
                                AdIntent.putExtra("key_url", newsFeed.getPurl());
                                mContext.startActivity(AdIntent);
                            }
                        });
                        AdUtil.upLoadAd(newsFeed, mContext);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    adLayout.setVisibility(View.GONE);
                }
            });
            newsFeedRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(newsFeedRequestPost);
        }
    }
}
