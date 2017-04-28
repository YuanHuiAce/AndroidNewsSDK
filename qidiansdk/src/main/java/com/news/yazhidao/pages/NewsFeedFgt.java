package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.android.volley.toolbox.StringRequest;
import com.github.jinsedeyuzhou.IPlayer;
import com.github.jinsedeyuzhou.PlayStateParams;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.github.jinsedeyuzhou.utils.NetworkUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.common.ThemeManager;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.entity.ADLoadNewsFeedEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.NewsFeedRequestPost;
import com.news.yazhidao.receiver.HomeWatcher;
import com.news.yazhidao.receiver.HomeWatcher.OnHomePressedListener;
import com.news.yazhidao.utils.AdUtil;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.LogUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.PlayerManager;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.SmallVideoContainer;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.transitionseverywhere.TransitionManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class NewsFeedFgt extends Fragment implements ThemeManager.OnThemeChangeListener, NativeAD.NativeAdListener {

    public static final String TAG = "NewsFeedFgt";
    public static final String KEY_NEWS_FEED = "key_news_feed";
    public static final String KEY_NEWS_IMAGE = "key_news_image";

    /**
     * 当前fragment 所对应的新闻频道
     */
    public static String KEY_CHANNEL_ID = "key_channel_id";
    public static String KEY_WORD = "key_word";
    public static String KEY_NEWS_SOURCE = "key_news_source";
    public static String KEY_NEWS_ID = "key_news_id";
    public static final String KEY_SHOW_COMMENT = "key_show_comment";
    public static final String CURRENT_POSITION = "position";
    public static final int PULL_DOWN_REFRESH = 1;
    public static final int PULL_UP_REFRESH = 2;
    public static final int REQUEST_CODE = 1060;
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsFeed = new ArrayList<>();
    private LinkedList<NewsFeed> mUploadArrNewsFeed = new LinkedList<>();
    private Context mContext;
    private PullToRefreshListView mlvNewsFeed;
    private View rootView;
    private String mstrChannelId, mstrKeyWord;
    private NewsFeedDao mNewsFeedDao;
    private boolean mFlag;
    private SharedPreferences mSharedPreferences;
    private boolean mIsFirst = true;
    private NewsSaveDataCallBack mNewsSaveCallBack;
    private View mHomeRetry;
    private RelativeLayout bgLayout, mrlSearch;
    private boolean isListRefresh = false;
    private boolean isNewVisity = false;//当前页面是否显示
    private Handler mHandler;
    private Runnable mThread;
    private boolean isClickHome;
    private TextView footView_tv, mRefreshTitleBar;
    private ProgressBar footView_progressbar;
    private boolean isBottom;
    private RefreshReceiver mRefreshReceiver;
    private LinearLayout footerView;
    private FrameLayout vPlayerContainer;
    private RelativeLayout mHomeRelative;
    private boolean isLoad = false;
    private ViewGroup mAndroidContent;
    private int position;
    //广点通
    private NativeAD mNativeAD;
    private boolean isADRefresh;
    private List<NativeADDataRef> mADs;
    public static final int AD_COUNT = 5;
    private ImageView mivShareBg;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    private SmallVideoContainer mFeedSmallScreen;
    private RelativeLayout mFeedSmallLayout;
    private ImageView mFeedClose;
    private int adPosition, adFlag;

    @Override
    public void onThemeChanged() {
        initTheme();
    }

    public void initTheme() {
        TextUtil.setLayoutBgColor(mContext, mRefreshTitleBar, R.color.white80);
        mlvNewsFeed.setHeaderLoadingView();
        TextUtil.setLayoutBgResource(mContext, mlvNewsFeed, R.color.news_feed_list);
        TextUtil.setLayoutBgResource(mContext, footerView, R.color.news_feed_list);
        mAdapter.notifyDataSetChanged();
    }


    public interface NewsSaveDataCallBack {
        void result(String channelId, ArrayList<NewsFeed> results);
    }

    public static NewsFeedFgt newInstance(String pChannelId) {
        NewsFeedFgt newsFeedFgt = new NewsFeedFgt();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CHANNEL_ID, pChannelId);
        newsFeedFgt.setArguments(bundle);
        return newsFeedFgt;
    }

    public void setNewsSaveDataCallBack(NewsSaveDataCallBack listener) {
        this.mNewsSaveCallBack = listener;
    }


    boolean isNotLoadData;

    public void setNewsFeed(ArrayList<NewsFeed> results) {
        isNotLoadData = true;
        this.mArrNewsFeed = results;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        }
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


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isNewVisity = isVisibleToUser;
        if (getUserVisibleHint() && !isLoad) {
            isLoad = true;
            if (!TextUtil.isEmptyString(mstrChannelId) && mHandler != null && mThread != null) {
                mHandler.postDelayed(mThread, 500);
            }
        }
        if (vPlayer != null && !isVisibleToUser) {

            if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                vPlayer.stop();
                vPlayer.release();
                mFeedSmallLayout.setVisibility(View.GONE);
                mFeedSmallScreen.removeAllViews();
            } else {
                vPlayer.onPause();
            }
        }

        if (mHomeRetry != null && mHomeRetry.getVisibility() == View.VISIBLE) {
            loadData(PULL_DOWN_REFRESH);
        }
//        if (rootView != null && !isVisibleToUser) {
//            mlvNewsFeed.onRefreshComplete();
//            mHandler.removeCallbacks(mRunnable);
//        }
//        if (rootView != null && isVisibleToUser && isLoadedData) {
//            isLoadedData = false;
//            mHandler.postDelayed(mRunnable, 800);
//            if (mArrNewsFeed == null || mIsFirst) {
//                mArrNewsFeed = mNewsFeedDao.queryByChannelId(mstrChannelId);
//                mAdapter.notifyDataSetChanged();
//                mIsFirst = false;
//            }
//        }
    }

    public void getFirstPosition() {
        if (mlvNewsFeed == null) {//防止listview为空
            return;
        }
//        if (!TextUtil.isListEmpty(mArrNewsFeed)) {
//            isNotLoadData = true;
//        } else {
//            isNotLoadData = false;
//        }
        isNotLoadData = false;
        mlvNewsFeed.getRefreshableView().setSelection(0);
        mlvNewsFeed.getRefreshableView().smoothScrollToPosition(0);
    }


    public void refreshData() {
        if (mlvNewsFeed == null) {//防止listview为空
            return;
        }
        isNotLoadData = false;
        mThread = new Runnable() {
            @Override
            public void run() {
                mlvNewsFeed.setRefreshing();
                isListRefresh = true;
                isClickHome = false;
            }
        };
        mHandler.postDelayed(mThread, 1000);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = getActivity();
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
        mNewsFeedDao = new NewsFeedDao(mContext);
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mFlag = mSharedPreferences.getBoolean("isshow", false);
        /** 梁帅：注册修改字体大小的广播*/
        mRefreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
        intentFilter.addAction(CommonConstant.CHANGE_COMMENT_NUM_ACTION);
        mContext.registerReceiver(mRefreshReceiver, intentFilter);
        ThemeManager.registerThemeChangeListener(this);
        mNativeAD = new NativeAD(QiDianApplication.getInstance().getAppContext(), CommonConstant.APPID, CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID, this);
    }

    public View onCreateView(LayoutInflater LayoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mstrChannelId = arguments.getString(KEY_CHANNEL_ID);
            mstrKeyWord = arguments.getString(KEY_WORD);
        }
        if (!TextUtil.isEmptyString(mstrChannelId) && mstrChannelId.equals("44")) {
            mAndroidContent = (ViewGroup) getActivity().findViewById(Window.ID_ANDROID_CONTENT);
            FrameLayout.LayoutParams lpParent = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vPlayerContainer = new FrameLayout(mContext);
            vPlayerContainer.setBackgroundColor(Color.BLACK);
            vPlayerContainer.setVisibility(View.GONE);
            mAndroidContent.addView(vPlayerContainer, lpParent);
            adPosition = SharedPreManager.mInstance(mContext).getAdFeedPosition(CommonConstant.FILE_AD, CommonConstant.AD_FEED_VIDEO_POS);
        } else {
            adPosition = SharedPreManager.mInstance(mContext).getAdFeedPosition(CommonConstant.FILE_AD, CommonConstant.AD_FEED_POS);
        }
        //==============================视频==========================
        mFeedSmallScreen = (SmallVideoContainer) getActivity().findViewById(R.id.feed_small_screen);
        mFeedSmallLayout = (RelativeLayout) getActivity().findViewById(R.id.feed_small_layout);
        mFeedClose = (ImageView) getActivity().findViewById(R.id.feed_video_close);
        //======================================================
        rootView = LayoutInflater.inflate(R.layout.qd_activity_news, container, false);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        mivShareBg = (ImageView) getActivity().findViewById(R.id.share_bg_imageView);
        mRefreshTitleBar = (TextView) rootView.findViewById(R.id.mRefreshTitleBar);
        TextUtil.setLayoutBgColor(mContext, mRefreshTitleBar, R.color.white80);
        mHomeRetry = rootView.findViewById(R.id.mHomeRetry);
        mHomeRelative = (RelativeLayout) rootView.findViewById(R.id.mHomeRelative);
        mHomeRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlvNewsFeed.setRefreshing();
                mHomeRetry.setVisibility(View.GONE);
            }
        });
        mlvNewsFeed = (PullToRefreshListView) rootView.findViewById(R.id.news_feed_listView);
        mlvNewsFeed.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mlvNewsFeed.setMainFooterView(true);
        mlvNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                adFlag = PULL_DOWN_REFRESH;
                loadData(PULL_DOWN_REFRESH);
                scrollAd();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                adFlag = PULL_UP_REFRESH;
                loadData(PULL_UP_REFRESH);
                scrollAd();
            }
        });
        addHFView(LayoutInflater);
        mAdapter = new NewsFeedAdapter(getActivity(), this, null);
        mAdapter.setClickShowPopWindow(mClickShowPopWindow);
        mlvNewsFeed.setAdapter(mAdapter);
        mlvNewsFeed.setEmptyView(View.inflate(mContext, R.layout.qd_listview_empty_view, null));
        setUserVisibleHint(getUserVisibleHint());
        playVideoControl();
        //load news data
        mHandler = new Handler();
        mThread = new Runnable() {
            @Override
            public void run() {
//                mlvNewsFeed.setRefreshing();
                /**
                 *  梁帅： 2016.08.31 修改加载逻辑
                 *  如果有数据，拿数据的一条的是时间是下拉刷新
                 *  如果没有数据，直接加载
                 */
                ArrayList<NewsFeed> arrNewsFeed = mNewsFeedDao.queryByChannelId(mstrChannelId);
                if (!TextUtil.isListEmpty(arrNewsFeed)) {
                    loadData(PULL_DOWN_REFRESH);
                } else {
                    loadData(PULL_UP_REFRESH);
                }
                isListRefresh = false;
            }
        };
        int delay = 1500;
        if (mstrChannelId != null && mstrChannelId.equals("1")) {
            delay = 500;
        }
        if (isLoad) {
            mHandler.postDelayed(mThread, delay);
        }
        return rootView;
    }


    NewsFeedAdapter.clickShowPopWindow mClickShowPopWindow = new NewsFeedAdapter.clickShowPopWindow() {
        @Override
        public void showPopWindow(int x, int y, NewsFeed feed) {
            if (mNewsFeedFgtPopWindow != null) {
                String pName = feed.getPname();
                mNewsFeedFgtPopWindow.showPopWindow(x, y, pName != null ? pName : "未知来源", feed.getNid(), mAdapter);
            }
        }
    };

    @Override
    public void onDestroy() {
        ThemeManager.unregisterThemeChangeListener(this);
        if (mRefreshReceiver != null) {
            mContext.unregisterReceiver(mRefreshReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeCallbacks(mThread);
        }
        if (rootView != null) {
            unbindDrawables(rootView);
        }
    }


    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    /**
     * 当前新闻feed流fragment的父容器是否是MainAty,如果是则进行刷新动画
     */
    public void startTopRefresh() {
    }

    /**
     * 当前新闻feed流fragment的父容器是否是MainAty,如果是则停止刷新动画
     *
     * @return
     */
    public void stopRefresh() {
    }

    private void loadAD() {
        if (mNativeAD != null && !isADRefresh && !TextUtil.isEmptyString(CommonConstant.APPID)) {
            mNativeAD.loadAD(AD_COUNT);
            isADRefresh = true;
        }
    }

    private void loadNewsFeedData(String url, final int flag) {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }
        String requestUrl;
        String tstart = "";
        ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
        adLoadNewsFeedEntity.setCid(TextUtil.isEmptyString(mstrChannelId) ? null : Long.parseLong(mstrChannelId));
        adLoadNewsFeedEntity.setUid(SharedPreManager.mInstance(mContext).getUser(mContext).getMuid());
        adLoadNewsFeedEntity.setT(1);
        adLoadNewsFeedEntity.setV(1);
        adLoadNewsFeedEntity.setAds(SharedPreManager.mInstance(mContext).getAdChannelInt(CommonConstant.FILE_AD, CommonConstant.AD_CHANNEL));
        adLoadNewsFeedEntity.setB(TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_FEED_GDT_API_BIGPOSID)));
        Gson gson = new Gson();
        if (flag == PULL_DOWN_REFRESH) {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                for (int i = 0; i < mArrNewsFeed.size(); i++) {
                    NewsFeed newsFeed = mArrNewsFeed.get(i);
                    if (newsFeed.getRtype() != 3 && newsFeed.getRtype() != 4) {
                        adLoadNewsFeedEntity.setNid(newsFeed.getNid());
                        break;
                    }
                }
            }
            tstart = getFirstItemTime(mArrNewsFeed);
            requestUrl = HttpConstant.URL_FEED_AD_PULL_DOWN;
            //下拉刷新打点
            if (!TextUtil.isEmptyString(mstrChannelId)) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("chid", Integer.valueOf(mstrChannelId));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_LOADFEED, CommonConstant.LOG_PAGE_FEEDPAGE, CommonConstant.LOG_PAGE_FEEDPAGE, jsonObject, true);
            }
        } else {
            if (mFlag) {
                if (mIsFirst) {
                    ArrayList<NewsFeed> arrNewsFeed = mNewsFeedDao.queryByChannelId(mstrChannelId);
                    tstart = getFirstItemTime(arrNewsFeed);
                } else {
                    tstart = getLastItemTime(mArrNewsFeed);
                }
            } else {
                mSharedPreferences.edit().putBoolean("isshow", true).commit();
                mFlag = true;
                tstart = getFirstItemTime(null);
            }
            requestUrl = HttpConstant.URL_FEED_AD_LOAD_MORE;
            //上拉刷新打点
            if (!TextUtil.isEmptyString(mstrChannelId)) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("chid", Integer.valueOf(mstrChannelId));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_REFRESHFEED, CommonConstant.LOG_PAGE_FEEDPAGE, CommonConstant.LOG_PAGE_FEEDPAGE, jsonObject, true);
            }
        }
        adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        NewsFeedRequestPost<ArrayList<NewsFeed>> newsFeedRequestPost = new NewsFeedRequestPost(requestUrl, gson.toJson(adLoadNewsFeedEntity), new Response.Listener<ArrayList<NewsFeed>>() {
            @Override
            public void onResponse(final ArrayList<NewsFeed> result) {
                loadNewFeedSuccess(result, flag);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadNewFeedError(error, flag);
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        newsFeedRequestPost.setRequestHeaders(header);
        newsFeedRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(newsFeedRequestPost);
    }

    private String getFirstItemTime(ArrayList<NewsFeed> newsFeedArrayList) {
        if (!TextUtil.isListEmpty(newsFeedArrayList)) {
            for (NewsFeed newsFeed : newsFeedArrayList) {
                String time = newsFeed.getPtime();
                if (!TextUtil.isEmptyString(time)) {
                    return DateUtil.dateStr2Long(time) + "";
                }
            }
        }
        return System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
    }

    private String getLastItemTime(ArrayList<NewsFeed> newsFeedArrayList) {
        if (!TextUtil.isListEmpty(newsFeedArrayList)) {
            for (int i = newsFeedArrayList.size() - 1; i >= 0; i--) {
                String time = newsFeedArrayList.get(i).getPtime();
                if (!TextUtil.isEmptyString(time)) {
                    return DateUtil.dateStr2Long(time) + "";
                }
            }
        }
        return System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
    }

    public void loadNewFeedSuccess(final ArrayList<NewsFeed> result, int flag) {
        removePrompt();
        if (mIsFirst || flag == PULL_DOWN_REFRESH) {
            if (result == null || result.size() == 0) {
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
//                mRefreshTitleBar.setText("已是最新数据");
//                mRefreshTitleBar.setVisibility(View.VISIBLE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mRefreshTitleBar.getVisibility() == View.VISIBLE) {
//                            mRefreshTitleBar.setVisibility(View.GONE);
//                        }
//                    }
//                }, 1000);
//                stopRefresh();
//                mlvNewsFeed.onRefreshComplete();
                return;
            }
            mRefreshTitleBar.setText("又发现了" + result.size() + "条新数据");
            mRefreshTitleBarAnimation();
        }
        //如果频道是1,则说明此频道的数据都是来至于其他的频道,为了方便存储,所以要修改其channelId
        if (mstrChannelId != null && ("1".equals(mstrChannelId) || "35".equals(mstrChannelId) || "44".equals(mstrChannelId))) {
            for (NewsFeed newsFeed : result) {
                if ("1".equals(mstrChannelId)) {
                    newsFeed.setChannel_id(1);
                    if (newsFeed.getStyle() == 6) {
                        newsFeed.setStyle(8);
                    }
                } else if ("35".equals(mstrChannelId)) {
                    newsFeed.setChannel_id(35);
                } else if ("44".equals(mstrChannelId)) {
                    newsFeed.setChannel_id(44);
                } else {
                    newsFeed.setChannel_id(newsFeed.getChannel());
                }
                if (newsFeed.getRtype() == 3) {
                    newsFeed.setSource(CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE);
                    newsFeed.setAid(Long.valueOf(CommonConstant.NEWS_FEED_GDT_API_BIGPOSID));
                    LogUtil.adGetLog(mContext, 1, 1, Long.valueOf(CommonConstant.NEWS_FEED_GDT_API_BIGPOSID), CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE);
                } else {
                    newsFeed.setSource(CommonConstant.LOG_SHOW_FEED_SOURCE);
                }
            }
        }
//        for (Iterator it = result.iterator(); it.hasNext();) {
//            NewsFeed newsFeed = (NewsFeed) it.next();
//            if(newsFeed.getRtype()==3){
//                it.remove();
//            }
//        }
        if (flag == PULL_DOWN_REFRESH && !mIsFirst && !TextUtil.isListEmpty(result)) {
            NewsFeed newsFeed = new NewsFeed();
            newsFeed.setStyle(900);
            result.add(newsFeed);
        }
        mHomeRetry.setVisibility(View.GONE);
        stopRefresh();
        if (result != null && result.size() > 0) {
            switch (flag) {
                case PULL_DOWN_REFRESH:
                    if (mArrNewsFeed == null) {
                        mArrNewsFeed = result;
                    } else {
                        //type==4 专题
                        if (result.get(0).getRtype() == 4) {
                            Iterator<NewsFeed> iterator = mArrNewsFeed.iterator();
                            while (iterator.hasNext()) {
                                NewsFeed newsFeed = iterator.next();
                                if (newsFeed.getRtype() == 4 && result.get(0).getNid() == newsFeed.getNid()) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                        mArrNewsFeed.addAll(0, result);
                    }
                    mlvNewsFeed.getRefreshableView().setSelection(0);
                    break;
                case PULL_UP_REFRESH:
                    if (mArrNewsFeed == null) {
                        mArrNewsFeed = result;
                    } else {
                        mArrNewsFeed.addAll(result);
                    }
                    break;
            }
            if (mNewsSaveCallBack != null) {
                mNewsSaveCallBack.result(mstrChannelId, mArrNewsFeed);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mNewsFeedDao.insert(result);
                }
            }).start();
            mAdapter.setNewsFeed(mArrNewsFeed);
            mAdapter.notifyDataSetChanged();
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
            //广点通sdk请求广告
            if (SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE)) {
                if (TextUtil.isListEmpty(mADs)) {
                    loadAD();
                } else {
                    addADToList(flag);
                }
            }
        } else {
            //向服务器发送请求,已成功,但是返回结果为null,需要显示重新加载view
            if (TextUtil.isListEmpty(mArrNewsFeed)) {
                ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
                if (TextUtil.isListEmpty(newsFeeds)) {
                    mHomeRetry.setVisibility(View.VISIBLE);
                } else {
                    mArrNewsFeed = newsFeeds;
                    mHomeRetry.setVisibility(View.GONE);
                    mAdapter.setNewsFeed(newsFeeds);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                mAdapter.setNewsFeed(mArrNewsFeed);
                mAdapter.notifyDataSetChanged();
            }
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        }
        mIsFirst = false;
        mlvNewsFeed.onRefreshComplete();
    }

    private void loadNewFeedError(VolleyError error, final int flag) {
        if (error.toString().contains("2002")) {
            removePrompt();
            mRefreshTitleBar.setText("已是最新数据");
            mRefreshTitleBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mRefreshTitleBar.getVisibility() == View.VISIBLE) {
                        mRefreshTitleBar.setVisibility(View.GONE);
                    }
                }
            }, 1000);
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        } else if (error.toString().contains("4003") && mstrChannelId.equals("1")) {//说明三方登录已过期,防止开启3个loginty
            User user = SharedPreManager.mInstance(mContext).getUser(getActivity());
            user.setUtype("2");
            SharedPreManager.mInstance(mContext).saveUser(user);
//                    Intent loginAty = new Intent(getActivity(), LoginAty.class);
//                    startActivityForResult(loginAty, REQUEST_CODE);
            UserManager.registerVisitor(getActivity(), new UserManager.RegisterVisitorListener() {
                @Override
                public void registerSuccess() {
                    mlvNewsFeed.onRefreshComplete();
                    loadNewsFeedData("", flag);
                }
            });
        }
        if (TextUtil.isListEmpty(mArrNewsFeed)) {
            ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
            if (TextUtil.isListEmpty(newsFeeds)) {
                mHomeRetry.setVisibility(View.VISIBLE);
            } else {
                mArrNewsFeed = newsFeeds;
                mHomeRetry.setVisibility(View.GONE);
                mAdapter.setNewsFeed(newsFeeds);
                mAdapter.notifyDataSetChanged();
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
        }
        stopRefresh();
        mlvNewsFeed.onRefreshComplete();
    }

    public void loadData(final int flag) {
        User user = SharedPreManager.mInstance(mContext).getUser(mContext);
        if (null != user) {
            if (NetUtil.checkNetWork(mContext)) {
                if (!isNotLoadData) {
                    if (!TextUtil.isEmptyString(mstrKeyWord)) {
                        loadNewsFeedData("search", flag);
                    } else if (!TextUtil.isEmptyString(mstrChannelId))
                        loadNewsFeedData("recommend", flag);
                    startTopRefresh();
                } else {
                    isNotLoadData = false;
                    setRefreshComplete();
                }
            } else {
                setRefreshComplete();
                ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
                if (TextUtil.isListEmpty(newsFeeds)) {
                    mHomeRetry.setVisibility(View.VISIBLE);
                } else {
                    mAdapter.setNewsFeed(newsFeeds);
                    mAdapter.notifyDataSetChanged();
                    mHomeRetry.setVisibility(View.GONE);
                }
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
        } else {
            mHomeRetry.setVisibility(View.VISIBLE);
            setRefreshComplete();
            //请求token
            UserManager.registerVisitor(mContext, new UserManager.RegisterVisitorListener() {
                @Override
                public void registerSuccess() {
                    loadData(flag);
                }
            });
        }
    }

    private void setRefreshComplete() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mlvNewsFeed.onRefreshComplete();
            }
        }, 500);
    }

    NewsFeedFgtPopWindow mNewsFeedFgtPopWindow;

    public void setNewsFeedFgtPopWindow(NewsFeedFgtPopWindow mNewsFeedFgtPopWindow) {
        this.mNewsFeedFgtPopWindow = mNewsFeedFgtPopWindow;
    }

    public interface NewsFeedFgtPopWindow {
        void showPopWindow(int x, int y, String pubName, int newsId, NewsFeedAdapter mAdapter);
    }


    HomeWatcher mHomeWatcher;

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("feed");
        if (vPlayer != null) {
            if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                vPlayer.stop();
                vPlayer.release();
                mFeedSmallLayout.setVisibility(View.GONE);
                mFeedSmallScreen.removeAllViews();
            } else {
                vPlayer.onPause();
            }
        }
    }

    @Override
    public void onResume() {
//        mHomeWatcher = new HomeWatcher(this.getActivity());
//        mHomeWatcher.setOnHomePressedListener(mOnHomePressedListener);
//        mHomeWatcher.startWatch();
        MobclickAgent.onPageStart("feed");
        super.onResume();
        if (mRefreshTitleBar.getVisibility() == View.VISIBLE) {
            mRefreshTitleBar.setVisibility(View.GONE);
        }
        long time = (System.currentTimeMillis() - homeTime) / 1000;
        if (isNewVisity && isClickHome && time >= 60) {
//            mlvNewsFeed.setRefreshing();
//            isListRefresh = true;
//            loadData(PULL_DOWN_REFRESH);
//            isClickHome = false;
//            bgLayout.setVisibility(View.VISIBLE);
//            mlvNewsFeed.createLoadingLayoutProxy(true, false);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    isListRefresh = true;
//                    loadData(PULL_DOWN_REFRESH);
//                    isClickHome = false;
//                }
//            },1500);
//            mThread = new Runnable() {
//                @Override
//                public void run() {
//                    mlvNewsFeed.setRefreshing();
//                    isListRefresh = true;
//                    isClickHome = false;
//                }
//            };
//            mHandler.postDelayed(mThread, 1000);
        } else {
            if (mArrNewsFeed != null && bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
                mAdapter.setNewsFeed(mArrNewsFeed);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    long homeTime;
    OnHomePressedListener mOnHomePressedListener = new OnHomePressedListener() {
        @Override
        public void onHomePressed() {
            Logger.e("aaa", "点击home键");
            if (isClickHome) {
                return;
            }
            isClickHome = true;
            homeTime = System.currentTimeMillis();
        }


        @Override
        public void onHomeLongPressed() {
            Logger.e("aaa", "长按home键");
            if (isClickHome) {
                return;
            }
            isClickHome = true;
            homeTime = System.currentTimeMillis();
        }
    };

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                Logger.e("aaa", "文字的改变！！！");
                mAdapter.notifyDataSetChanged();
            } else if (CommonConstant.CHANGE_COMMENT_NUM_ACTION.equals(intent.getAction()) && intent != null) {
                int newsId = intent.getIntExtra(CommonConstant.NEWS_ID, 0);
                if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                    for (NewsFeed newsFeed : mArrNewsFeed) {
                        if (newsFeed.getNid() == newsId) {
                            int num = intent.getIntExtra(CommonConstant.NEWS_COMMENT_NUM, 0);
                            newsFeed.setComment(num);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void addHFView(LayoutInflater LayoutInflater) {
        View mSearchHeaderView = LayoutInflater.inflate(R.layout.search_header_layout, null);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        mSearchHeaderView.setLayoutParams(layoutParams);
        ListView lv = mlvNewsFeed.getRefreshableView();
        if (!mstrChannelId.equals("44"))
            lv.addHeaderView(mSearchHeaderView);
        lv.setHeaderDividersEnabled(false);
        mrlSearch = (RelativeLayout) mSearchHeaderView.findViewById(R.id.search_layout);
        mrlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_SEARCHCLICK, CommonConstant.LOG_PAGE_FEEDPAGE, CommonConstant.LOG_PAGE_SEARCHPAGE, null, true);
                Intent intent = new Intent(mContext, TopicSearchAty.class);
                mContext.startActivity(intent);
            }
        });
        footerView = (LinearLayout) LayoutInflater.inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        TextUtil.setLayoutBgResource(mContext, footerView, R.color.news_feed_list);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);
//        lv.setFooterDividersEnabled(false);
        mlvNewsFeed.setOnStateListener(new PullToRefreshBase.onStateListener() {
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
                mlvNewsFeed.setFooterViewInvisible();
            }
        });

        // 监听listview滚到最底部
        mlvNewsFeed.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调
                //回调顺序如下
                //第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动
                //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）                //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动                //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；                //由于用户的操作，屏幕产生惯性滑动时为2
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            isBottom = true;
                            isListRefresh = true;
                            loadData(PULL_UP_REFRESH);
                        } else {
                            isBottom = false;
                        }
                        if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                            for (NewsFeed newsFeed : mArrNewsFeed) {
                                if (!newsFeed.isUpload() && newsFeed.isVisble()) {
                                    newsFeed.setUpload(true);
                                    mUploadArrNewsFeed.add(newsFeed);
                                }
                            }
                        }
                        if (!TextUtil.isListEmpty(mUploadArrNewsFeed) && mUploadArrNewsFeed.size() > 4) {
                            while (mUploadArrNewsFeed.size() > 9) {
                                ArrayList<NewsFeed> newsFeeds = new ArrayList<>();
                                for (int i = 0; i < 9; i++) {
                                    newsFeeds.add(mUploadArrNewsFeed.poll());
                                }
                                LogUtil.userShowLog(newsFeeds, mContext);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int num = 0;
                if (!TextUtil.isEmptyString(mstrChannelId) && mstrChannelId.equals("44")) {
                    num = firstVisibleItem + visibleItemCount - 2;
                } else {
                    num = firstVisibleItem + visibleItemCount - 3;
                }
                if (!TextUtil.isListEmpty(mArrNewsFeed) && mArrNewsFeed.size() > num && num >= 0) {
                    NewsFeed feed = mArrNewsFeed.get(num);
                    View v = view.getChildAt(visibleItemCount - 1);
                    int percents = getVisibilityPercents(v);
                    if (!feed.isUpload() && feed.isVisble() && percents < 50) {
                        feed.setVisble(false);
                    } else {
                        feed.setVisble(true);
                    }
                }
                if ("44".equals(mstrChannelId) && portrait && !isAuto) {
//                    VideoVisibleControl();
                    VideoShowControl();
                }
            }
        });
    }

    public void mRefreshTitleBarAnimation() {
        //初始化
        Animation mStartAlphaAnimation = new AlphaAnimation(0f, 1.0f);
        //设置动画时间
        mStartAlphaAnimation.setDuration(500);
        mRefreshTitleBar.startAnimation(mStartAlphaAnimation);
        mStartAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRefreshTitleBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation mEndAlphaAnimation = new AlphaAnimation(1.0f, 0f);
                        mEndAlphaAnimation.setDuration(500);
                        mRefreshTitleBar.startAnimation(mEndAlphaAnimation);
                        mEndAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mRefreshTitleBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void removePrompt() {
        if (!TextUtil.isListEmpty(mArrNewsFeed)) {
            Iterator<NewsFeed> iterator = mArrNewsFeed.iterator();
            while (iterator.hasNext()) {
                NewsFeed newsFeed = iterator.next();
                if (newsFeed.getStyle() == 900) {
                    iterator.remove();
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void setTextSize() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void addADToList(int flag) {
        if (!TextUtil.isListEmpty(mADs) && mAdapter != null) {
            // 强烈建议：多个广告之间的间隔最好大一些，优先保证用户体验！
            // 此外，如果开发者的App的使用场景不是经常被用户滚动浏览多屏的话，没有必要在调用loadAD(int count)时去加载多条，只需要在用户即将进入界面时加载1条广告即可。
//                            mAdapter.addADToPosition((AD_POSITION + i * 10) % MAX_ITEMS, mADs.get(i));
            NativeADDataRef data = mADs.get(0);
            if (mArrNewsFeed != null && mArrNewsFeed.size() > 2) {
//                NewsFeed newsFeedFirst = mArrNewsFeed.get(1);
                NewsFeed newsFeed = new NewsFeed();
                newsFeed.setTitle(data.getDesc());
                newsFeed.setRtype(3);
                ArrayList<String> imgs = new ArrayList<>();
                imgs.add(data.getImgUrl());
                newsFeed.setImgs(imgs);
                newsFeed.setIcon(data.getIconUrl());
                newsFeed.setPname(data.getTitle());
//                int style = newsFeedFirst.getStyle();
//                if (style == 11 || style == 12 || style == 13 || style == 5) {
//                    newsFeed.setStyle(50);
//                } else {
//                    newsFeed.setStyle(51);
//                }
//                int i = Math.random() > 0.5 ? 1 : 0;
//                if (i == 0) {
//                    newsFeed.setStyle(50);
//                } else {
//                    newsFeed.setStyle(51);
//                }
                newsFeed.setStyle(51);
                newsFeed.setAid(Long.valueOf(CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID));
                newsFeed.setSource(CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE);
                newsFeed.setDataRef(data);
                if (PULL_DOWN_REFRESH == flag) {
                    if (mArrNewsFeed.size() > adPosition && adPosition > 0) {
                        mArrNewsFeed.add(adPosition, newsFeed);
                    }
                } else {
                    if (mArrNewsFeed.size() >= 14) {
                        mArrNewsFeed.add(mArrNewsFeed.size() - 8, newsFeed);
                    } else {
                        if (mArrNewsFeed.size() > adPosition && adPosition > 0) {
                            mArrNewsFeed.add(adPosition, newsFeed);
                        }
                    }
                }
                mADs.remove(0);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onADLoaded(List<NativeADDataRef> list) {
        if (!TextUtil.isListEmpty(list)) {
            LogUtil.adGetLog(mContext, AD_COUNT, list.size(), Long.valueOf(CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID), CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE);
            mADs = list;
            addADToList(adFlag);
        }
        isADRefresh = false;
    }

    @Override
    public void onNoAD(int i) {
        isADRefresh = false;
    }

    @Override
    public void onADStatusChanged(NativeADDataRef nativeADDataRef) {
        getADButtonText(nativeADDataRef);
        isADRefresh = false;
    }

    @Override
    public void onADError(NativeADDataRef nativeADDataRef, int i) {
        isADRefresh = false;
    }

    /**
     * App类广告安装、下载状态的更新（普链广告没有此状态，其值为-1） 返回的AppStatus含义如下： 0：未下载 1：已安装 2：已安装旧版本 4：下载中（可获取下载进度“0-100”）
     * 8：下载完成 16：下载失败
     */

    private String getADButtonText(NativeADDataRef adItem) {
        if (adItem == null) {
            return "……";
        }
        if (!adItem.isAPP()) {
            return "查看详情";
        }
        switch (adItem.getAPPStatus()) {
            case 0:
                return "点击下载";
            case 1:
                return "点击启动";
            case 2:
                return "点击更新";
            case 4:
                return adItem.getProgress() > 0 ? "下载中" + adItem.getProgress() + "%" : "下载中"; // 特别注意：当进度小于0时，不要使用进度来渲染界面
            case 8:
                return "下载完成";
            case 16:
                return "下载失败,点击重试";
            default:
                return "查看详情";
        }
    }

    /**
     * 广告滑动接口
     */
    private void scrollAd() {
        User user = SharedPreManager.mInstance(mContext).getUser(mContext);
        if (user != null) {
            int uid = user.getMuid();
            //渠道类型, 1：奇点资讯， 2：黄历天气，3：纹字锁频，4：猎鹰浏览器，5：白牌 6:纹字主题
            int ctype = CommonConstant.NEWS_CTYPE;
            //平台类型，1：IOS，2：安卓，3：网页，4：无法识别
            int ptype = CommonConstant.NEWS_PTYPE;
            //mid
            String imei = DeviceInfoUtil.getDeviceImei(mContext);
            String requestUrl = HttpConstant.URL_SCROLL_AD + "?uid=" + uid + "&ctype=" + ctype + "&ptype=" + ptype + "&mid=" + imei;
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            StringRequest request = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, null);
            requestQueue.add(request);
        }
    }


    //========================================视频部分======================================//

    private void initPlayer() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult");


        if (vPlayer != null && resultCode == 1006 && mstrChannelId.equals("44") && data != null) {
            if (vPlayer.getStatus() == PlayStateParams.STATE_PAUSED) {
                int position = data.getIntExtra(NewsFeedFgt.CURRENT_POSITION, 0);
                int newId = data.getIntExtra(NewsFeedFgt.KEY_NEWS_ID, 0);
                if (position != 0 && cPostion == newId && newId != 0) {
                    vPlayer.seekToNewPosition(position);
                    vPlayer.onResume();
                    if (vPlayer.isPlay())
                        if (getPlayItemPosition() != -1) {
                            getShowItemView(getPlayItemPosition()).setVisibility(View.GONE);
                            FrameLayout frameLayout = (FrameLayout) vPlayer.getParent();
                            if (frameLayout != null) {
                                frameLayout.removeView(vPlayer);
                            }
                            getPlayItemView(getPlayItemPosition()).addView(vPlayer);
                            vPlayer.setShowContoller(false);

                        }
                    //延时进入onResume
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 100);
                } else {
                    vPlayer.stop();
                    vPlayer.release();
                    removeViews();
                }
            } else {
                vPlayer.stop();
                vPlayer.release();
                removeViews();
            }

        } else {
            vPlayer.stop();
            vPlayer.release();
            removeViews();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        if (PlayerManager.videoPlayView != null)
            vPlayer = PlayerManager.videoPlayView;
    }

    public int cPostion = -1;
    private int lastPostion = -1;
    private VPlayPlayer vPlayer;
    private boolean portrait = true;
//    private NewsFeed newsFeed;

    /**
     * 横竖屏切换
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        Log.v(TAG, "onConfigurationChanged");
        if (!"44".equals(mstrChannelId))
            return;
        super.onConfigurationChanged(newConfig);
        if (vPlayer != null) {
            vPlayer.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                portrait = true;
                vPlayerContainer.setVisibility(View.GONE);
                mHandler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                vPlayerContainer.removeView(vPlayer);
                                TransitionManager.beginDelayedTransition(vPlayerContainer);
                                Log.v(TAG, "onConfigurationChanged:::" + newConfig.orientation);
                                int position = getPlayItemPosition();
                                if ((vPlayer.getStatus() == PlayStateParams.STATE_PAUSED || vPlayer.isPlay())) {
                                    if (position != -1) {
                                        FrameLayout playItemView = getPlayItemView(position);
                                        playItemView.removeAllViews();
                                        ViewGroup itemView = (ViewGroup) playItemView.getParent();
                                        if (itemView != null) {
                                            itemView.findViewById(R.id.rl_video_show).setVisibility(View.GONE);
                                        }
                                        playItemView.addView(vPlayer);
                                        vPlayer.showBottomControl(true);
                                    } else {
                                        mlvNewsFeed.getRefreshableView().setSelectionFromTop(getNextPosition() + 1, 0);
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                FrameLayout videoContainer = getPlayItemView(getPlayItemPosition());
                                                if (videoContainer != null)
                                                    videoContainer.addView(vPlayer);
                                                RelativeLayout showBg = getItemView(getPlayItemPosition());
                                                if (showBg != null)
                                                    showBg.setVisibility(View.GONE);
                                            }
                                        }, 300);
                                        isAuto = false;
                                    }
                                } else {
                                    if (vPlayer != null) {
                                        vPlayer.stop();
                                        vPlayer.release();
                                    }
                                }

                            }
                        }, 300);
            } else {
                portrait = false;
                mHandler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.v(TAG, "onConfigurationChanged:::" + newConfig.orientation);
                                FrameLayout frameLayout = (FrameLayout) vPlayer.getParent();
                                if (frameLayout != null) {
                                    frameLayout.removeView(vPlayer);
                                    View itemView = (View) frameLayout.getParent();
                                    if (itemView != null) {
                                        View videoSHow = itemView.findViewById(R.id.rl_video_show);

                                        if (videoSHow != null) {
                                            videoSHow.setVisibility(View.VISIBLE);
                                            Log.v(TAG, "onConfigurationChanged");
                                        }
                                    }
                                }
                                vPlayerContainer.setVisibility(View.VISIBLE);
                                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                vPlayerContainer.addView(vPlayer, lp);
                                if (vPlayer.getStatus() != PlayStateParams.STATE_PAUSED)
                                    vPlayer.showBottomControl(false);
                            }
                        }, 300);

            }

        } else {
            mAdapter.notifyDataSetChanged();
            mHomeRelative.setVisibility(View.VISIBLE);
        }
    }

    private SharePopupWindow.ShareDismiss shareDismiss = new SharePopupWindow.ShareDismiss() {
        @Override
        public void shareDismiss() {
            mivShareBg.startAnimation(mAlphaAnimationOut);
            mivShareBg.setVisibility(View.INVISIBLE);
        }
    };

    /**
     * 视频播放控制
     */

    public void playVideoControl() {
        if (null == vPlayer) {
            vPlayer = PlayerManager.getPlayerManager().initialize(mContext);
        }

        mAdapter.setOnPlayClickListener(new NewsFeedAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick(RelativeLayout relativeLayout, NewsFeed feed) {
                isAuto = false;
                if (!NetworkUtils.isConnectionAvailable(mContext))
                    return;
                relativeLayout.setVisibility(View.GONE);
                PlayerManager.newsFeed = feed;
                isAd = false;
                cPostion = feed.getNid();
                if (cPostion != lastPostion) {
                    vPlayer.stop();
                    vPlayer.release();
                }
                if (lastPostion != -1) {
                    removeViews();
                }
                View view = (View) relativeLayout.getParent();
                ViewGroup mItemVideo = (ViewGroup) view.findViewById(R.id.layout_item_video);
                mItemVideo.removeAllViews();
                vPlayer.setTitle(feed.getTitle());
                vPlayer.setDuration(feed.getDuration());
                vPlayer.play(feed.getVideourl());
                mItemVideo.addView(vPlayer);
                vPlayer.setShowContoller(false);
                lastPostion = cPostion;
            }

            @Override
            public void onItemClick(RelativeLayout rlNewsContent, NewsFeed feed) {
                isAuto = false;
                if (feed == null && !NetworkUtils.isConnectionAvailable(mContext))
                    return;
                cPostion = feed.getNid();
                if (cPostion != lastPostion && lastPostion != -1) {
                    vPlayer.stop();
                    vPlayer.release();
                    removeViews();
                } else {
                    vPlayer.onPause();
                }
                Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
                intent.putExtra(CommonConstant.KEY_SOURCE, CommonConstant.LOG_CLICK_FEED_SOURCE);
                intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
                intent.putExtra(NewsFeedFgt.CURRENT_POSITION, vPlayer.getCurrentPosition());
                if (isAdded())
                    NewsFeedFgt.this.startActivityForResult(intent, NewsFeedFgt.REQUEST_CODE);
                else
                    ((Activity) mContext).startActivityForResult(intent, NewsFeedFgt.REQUEST_CODE);

                getActivity().overridePendingTransition(R.anim.qd_aty_right_enter, R.anim.qd_aty_no_ani);
                lastPostion = cPostion;
            }

            @Override
            public void onShareClick(ImageView imgView, final NewsFeed feed) {
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mivShareBg.startAnimation(mAlphaAnimationIn);
                        mivShareBg.setVisibility(View.VISIBLE);
                        SharePopupWindow mSharePopupWindow = new SharePopupWindow((Activity) mContext, shareDismiss);
                        mSharePopupWindow.setVideo(true);
                        mSharePopupWindow.setTitleAndNid(feed.getTitle(), feed.getNid(), feed.getDescr());
                        mSharePopupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    }
                });

            }
        });


        mFeedClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vPlayer.isPlay()) {
                    vPlayer.stop();
                    vPlayer.release();
                    cPostion = -1;
                    lastPostion = -1;
                    mFeedSmallScreen.removeAllViews();
                    mFeedSmallLayout.setVisibility(View.GONE);
                }
            }
        });
        if (mstrChannelId.equals("44")) {
            mFeedSmallLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (PlayerManager.newsFeed == null)
                        return;
                    Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
                    intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, PlayerManager.newsFeed);
                    intent.putExtra(NewsFeedFgt.CURRENT_POSITION, vPlayer.getCurrentPosition());
                    NewsFeedFgt.this.startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.qd_aty_right_enter, R.anim.qd_aty_no_ani);
                    if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                        mFeedSmallLayout.setVisibility(View.GONE);
                        mFeedSmallScreen.removeAllViews();
                        vPlayer.setShowContoller(false);
                        vPlayer.stop();
                        vPlayer.release();
                    }
                    PlayerManager.newsFeed = null;
                }


            });
        }

        if (mstrChannelId.equals("44")) {
            vPlayer.setOnShareListener(new IPlayer.OnShareListener() {
                @Override
                public void onShare() {

                }

                @Override
                public void onPlayCancel() {
                    if (vPlayer != null) {
                        vPlayer.stop();
                        vPlayer.release();
                    }
                    removeViews();
                }
            });


            vPlayer.setCompletionListener(new IPlayer.CompletionListener() {
                @Override
                public void completion(IMediaPlayer mp) {
                    position = getNextPosition();
                    if (position != -1) {
                        if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                            if (vPlayer != null) {
                                vPlayer.stop();
                                vPlayer.release();
                            }
                            mFeedSmallScreen.removeAllViews();
                            vPlayer.setTitle(mArrNewsFeed.get(position).getTitle());
                            vPlayer.start(mArrNewsFeed.get(position).getVideourl());
                            mFeedSmallScreen.addView(vPlayer);
                            lastPostion = cPostion;
                        } else if (vPlayerContainer.getVisibility() == View.VISIBLE) {
                            if (vPlayer != null) {
                                vPlayer.stop();
                                vPlayer.release();
                            }

                            vPlayerContainer.removeView(vPlayer);
                            vPlayer.setTitle(mArrNewsFeed.get(position).getTitle());
                            vPlayer.setDuration(mArrNewsFeed.get(position).getDuration());
                            vPlayer.play(mArrNewsFeed.get(position).getVideourl());
                            vPlayerContainer.addView(vPlayer);
                            lastPostion = cPostion;
                            isAuto = true;

                        } else {
                            if (vPlayer != null) {
                                vPlayer.stop();
                                vPlayer.release();
                            }
                            removeViews();
//                          遇到广告暂停一秒后继续播放下一条
//                            if (isAd) {
//                                mlvNewsFeed.getRefreshableView().setSelectionFromTop(position, 0);
//                                mHandler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        autoPlay();
//                                    }
//                                }, 1000);
//                            } else
                            autoPlay();

                        }

                    } else {
                        if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                            mFeedSmallScreen.removeAllViews();
                            mFeedSmallLayout.setVisibility(View.GONE);
                            vPlayer.setShowContoller(false);
                        } else if (vPlayerContainer.getVisibility() == View.VISIBLE) {
                            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            if (vPlayer != null) {
                                vPlayer.stop();
                                vPlayer.release();
                            }
                            vPlayerContainer.removeView(vPlayer);
                        } else {
                            if (vPlayer != null) {
                                vPlayer.stop();
                                vPlayer.release();
                            }
                            removeViews();
                            lastPostion = -1;
                        }
                    }
                    isAd = false;
                }
            });
        }
    }


    //是否处于自动播放模式
    private boolean isAuto;

    /**
     * 自动播放
     */
    private void autoPlay() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mlvNewsFeed.getRefreshableView().setSelectionFromTop(position + 1, 0);
            }
        }, 200);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout showBg = getItemView(getPlayItemPosition());
                if (showBg != null)
                    showBg.setVisibility(View.GONE);
                FrameLayout videoContainer = getPlayItemView(getPlayItemPosition());
                if (videoContainer != null)
                    videoContainer.addView(vPlayer);
                vPlayer.setTitle(mArrNewsFeed.get(position).getTitle());
                vPlayer.setDuration(mArrNewsFeed.get(position).getDuration());

                vPlayer.play(mArrNewsFeed.get(position).getVideourl());

                lastPostion = cPostion;

                position = 0;
            }
        }, 1000);
    }


    /**
     * 获取播放视频填充布局
     *
     * @param cPosition
     * @return
     */
    public RelativeLayout getItemView(int cPosition) {
        ListView lv = mlvNewsFeed.getRefreshableView();
        if (cPosition != -1) {
            View item = lv.getChildAt(cPosition);
            return (RelativeLayout) item.findViewById(R.id.rl_video_show);
        }

        return null;
    }

    //遇到广告暂停一秒，继续播放下一条
    private boolean isAd;

    /**
     * 获取下一个item位置
     *
     * @return
     */
    public int getNextPosition() {
        int position = -1;
        for (int i = 0; i < mArrNewsFeed.size(); i++) {
            if (mArrNewsFeed.get(i).getNid() == cPostion) {
                for (position = i + 1; position < mArrNewsFeed.size(); position++) {
                    if (mArrNewsFeed.get(position).getVideourl() != null) {
                        if (mArrNewsFeed.get(position - 1).getVideourl() == null)
                            isAd = true;
                        cPostion = mArrNewsFeed.get(position).getNid();
                        return position;
                    }
                }
            }
        }
        return -1;
    }


    /**
     * 判断当前播放item是否可见，-1 不可见
     *
     * @return
     */
    public int getPlayItemPosition() {
        ListView lv = mlvNewsFeed.getRefreshableView();
        for (int i = lv.getFirstVisiblePosition(); i <= lv.getLastVisiblePosition(); i++) {
            if (i == 0)
                continue;
            if (i > mArrNewsFeed.size())
                return -1;
            if (mArrNewsFeed.get(i - 1).getNid() == cPostion) {
                return (i - lv.getFirstVisiblePosition());
            }
        }
        return -1;
    }

    /**
     * 根据位置获取当前可见item 对象
     *
     * @param cPosition
     * @return
     */
    public FrameLayout getPlayItemView(int cPosition) {
        ListView lv = mlvNewsFeed.getRefreshableView();
        if (cPosition != -1) {
            View item = lv.getChildAt(cPosition);
            return (FrameLayout) item.findViewById(R.id.layout_item_video);
        }
        return null;
    }

    /**
     * 根据位置获取当前可见item 对象
     *
     * @param cPosition
     * @return
     */
    public RelativeLayout getShowItemView(int cPosition) {
        ListView lv = mlvNewsFeed.getRefreshableView();
        if (cPosition != -1) {
            View item = lv.getChildAt(cPosition);
            return (RelativeLayout) item.findViewById(R.id.rl_video_show);
        }
        return null;
    }

    /**
     * 移除播放器
     */
    public void removeViews() {
        ViewGroup frameLayout = (ViewGroup) vPlayer.getParent();
        if (frameLayout != null && frameLayout.getChildCount() > 0) {
            frameLayout.removeView(vPlayer);
            View itemView = (View) frameLayout.getParent();
            if (itemView != null) {
                View show = itemView.findViewById(R.id.rl_video_show);
                if (show != null) {
                    show.setVisibility(View.VISIBLE);
                    Log.e(TAG, "removeViews");
                }
            }
        }
    }


    /**
     * 滑动控制视频是否播放
     */
    private void VideoVisibleControl() {
        Log.v(TAG, "VideoVisibleControl");
        try {
            if (vPlayer == null)
                return;
            if (getPlayItemPosition() == -1) {
                FrameLayout frameLayout = (FrameLayout) vPlayer.getParent();
                if (frameLayout != null && frameLayout.getChildCount() > 0) {
                    vPlayer.stop();
                    vPlayer.release();
                    frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();
                    if (itemView != null) {
                        View videoSHow = itemView.findViewById(R.id.rl_video_show);
                        if (videoSHow != null) {
                            Log.e(TAG, "rl_video_show");
                            videoSHow.setVisibility(View.VISIBLE);
                        }

                    }
                }
            } else {
                if (vPlayer.isPlay())
                    if (getPlayItemPosition() != -1) {
                        getShowItemView(getPlayItemPosition()).setVisibility(View.GONE);
//                        getShowItemView((getPlayItemPosition()).setVisibility(View.GONE);

                    }
            }
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
    }


    private void VideoShowControl() {
        ListView lv = mlvNewsFeed.getRefreshableView();
        Log.e(TAG, "mlvNewsFeed: first" + lv.getFirstVisiblePosition() + ",last:" + lv.getLastVisiblePosition() + "ChannelId::" + mstrChannelId);
        boolean isExist = false;
        int position = -1;
        for (int i = lv.getFirstVisiblePosition(); i <= lv.getLastVisiblePosition(); i++) {
            if (i == 0)
                continue;
            if (i > mArrNewsFeed.size())
                break;
            if (mArrNewsFeed.get(i - 1).getNid() == cPostion) {
                isExist = true;
                position = i - lv.getFirstVisiblePosition();
                break;
            }
        }
        RelativeLayout showItemView = getShowItemView(position);
        if (showItemView != null && !vPlayer.isPlay())
            if (getShowItemView(position).getVisibility() == View.GONE) {
                getShowItemView(position).setVisibility(View.VISIBLE);
            }

        if (isExist) {
            View item = lv.getChildAt(position);
            Log.e(TAG, "item:" + item.toString() + "position:" + position);
            FrameLayout frameLayout = (FrameLayout) item.findViewById(R.id.layout_item_video);
            Log.e(TAG, "frameLayout:" + frameLayout.toString());

            if (vPlayer.isPlay() || vPlayer.getStatus() == PlayStateParams.STATE_PAUSED || vPlayer.isPlay() || vPlayer.getStatus() == PlayStateParams.STATE_PREPARE || vPlayer.getStatus() == PlayStateParams.STATE_PREPARING || vPlayer.getStatus() == PlayStateParams.STATE_PREPARED) {
                item.findViewById(R.id.rl_video_show).setVisibility(View.GONE);
            }

            if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                mFeedSmallLayout.setVisibility(View.GONE);
                mFeedSmallScreen.removeAllViews();
                vPlayer.setShow(true);
                vPlayer.isOpenOrientation(true);
                frameLayout.removeAllViews();
                frameLayout.addView(vPlayer);
            }

        } else {
            if (vPlayer != null && mFeedSmallLayout.getVisibility() == View.GONE) {
                FrameLayout frameLayout = (FrameLayout) vPlayer.getParent();
                if (frameLayout != null) {
                    if (vPlayer.getStatus() != PlayStateParams.STATE_PAUSED)
                        frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
                    }
                }

                if (vPlayer.isPlay() || vPlayer.getStatus() == PlayStateParams.STATE_PREPARE || vPlayer.getStatus() == PlayStateParams.STATE_PREPARING || vPlayer.getStatus() == PlayStateParams.STATE_PREPARED) {
                    mFeedSmallScreen.removeAllViews();
                    mFeedSmallScreen.addView(vPlayer);
                    vPlayer.setShowContoller(false);
                    vPlayer.isOpenOrientation(false);
                    mFeedSmallLayout.setVisibility(View.VISIBLE);
                }
            }

        }
    }
}
