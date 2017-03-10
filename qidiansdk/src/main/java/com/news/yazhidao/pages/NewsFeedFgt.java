package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.github.jinsedeyuzhou.PlayStateParams;
import com.github.jinsedeyuzhou.PlayerManager;
import com.github.jinsedeyuzhou.VPlayPlayer;
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
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

import static com.news.yazhidao.utils.CommonUtil.getActionBarHeight;
import static com.news.yazhidao.utils.CommonUtil.getStatusBarHeight;

public class NewsFeedFgt extends Fragment implements ThemeManager.OnThemeChangeListener {

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
    public static final int PULL_DOWN_REFRESH = 1;
    public static final int PULL_UP_REFRESH = 2;
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsFeed = new ArrayList<NewsFeed>();
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
    private RelativeLayout bgLayout;
    private boolean isListRefresh = false;
    private boolean isNewVisity = false;//当前页面是否显示
    private boolean isNeedAddSP = true;
    private Handler mHandler;
    private Runnable mThread;
    private boolean isClickHome;
    private TextView footView_tv, mRefreshTitleBar;
    private ProgressBar footView_progressbar;
    private boolean isBottom;
    private RefreshReceiver mRefreshReciver;
    private LinearLayout footerView;
    private ViewGroup vPlayerContainer;
    private RelativeLayout mHomeRelative;


    @Override
    public void onThemeChanged() {
        initTheme();
    }

    public void initTheme() {
        TextUtil.setLayoutBgColor(mContext, mRefreshTitleBar, R.color.white80);
        mlvNewsFeed.setHeaderLoadingView();
        TextUtil.setLayoutBgResource(mContext, mlvNewsFeed, R.color.white);
        TextUtil.setLayoutBgResource(mContext, footerView, R.color.white);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isNewVisity = isVisibleToUser;

        if (vPlayer!= null && !isVisibleToUser)
        {
            VideoVisibleControl();
        }

        if (isNewVisity && isNeedAddSP) {//切换到别的页面加入他
//            addSP(mArrNewsFeed);//第一次进入主页的时候会加入一次，不用担心这次加入是没有数据的

            isNeedAddSP = false;
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
//            Logger.e("jigang", "refresh " + mstrChannelId);
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
        mNewsFeedDao = new NewsFeedDao(mContext);
        mSharedPreferences = getActivity().getSharedPreferences("showflag", 0);
        mFlag = mSharedPreferences.getBoolean("isshow", false);
        /** 梁帅：注册修改字体大小的广播*/
        mRefreshReciver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter(CommonConstant.CHANGE_TEXT_ACTION);
        mContext.registerReceiver(mRefreshReciver, intentFilter);
        ThemeManager.registerThemeChangeListener(this);
    }

    public View onCreateView(LayoutInflater LayoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mstrChannelId = arguments.getString(KEY_CHANNEL_ID);
            mstrKeyWord = arguments.getString(KEY_WORD);
        }
        vPlayerContainer = (ViewGroup) getActivity().findViewById(Window.ID_ANDROID_CONTENT);

        rootView = LayoutInflater.inflate(R.layout.qd_activity_news, container, false);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
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
                Logger.e("aaa", "刷新");
                loadData(PULL_DOWN_REFRESH);
                scrollAd();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                Logger.e("aaa", "加载");
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
//        String platform = AnalyticsConfig.getChannel(getActivity());
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
        mHandler.postDelayed(mThread, delay);
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                return vPlayer.onKeyDown(keyCode, event);
            }
        });
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
        super.onDestroy();
        ThemeManager.unregisterThemeChangeListener(this);
        if (mRefreshReciver != null) {
            mContext.unregisterReceiver(mRefreshReciver);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeCallbacks(mThread);
        }
        Logger.e("jigang", "newsfeedfgt onDestroyView" + mstrChannelId);
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

    private void loadNewsFeedData(String url, final int flag) {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }
        String requestUrl;
        String tstart = System.currentTimeMillis() + "";
        String fixedParams = "&cid=" + mstrChannelId + "&uid=" + SharedPreManager.mInstance(mContext).getUser(mContext).getMuid();
        ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
        adLoadNewsFeedEntity.setCid(TextUtil.isEmptyString(mstrChannelId) ? null : Long.parseLong(mstrChannelId));
        adLoadNewsFeedEntity.setUid(SharedPreManager.mInstance(mContext).getUser(mContext).getMuid());
        adLoadNewsFeedEntity.setT(1);
        adLoadNewsFeedEntity.setV(1);
        Gson gson = new Gson();
        //加入feed流广告位id
        adLoadNewsFeedEntity.setB(TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_FEED_AD_ID)));
        if (flag == PULL_DOWN_REFRESH) {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                NewsFeed firstItem = mArrNewsFeed.get(0);
//                for (int i = 0; i < mArrNewsFeed.size(); i++) {
//                    NewsFeed newsFeed = mArrNewsFeed.get(i);
//                    if (newsFeed.getRtype() != 3 && newsFeed.getRtype() != 4) {
//                        adLoadNewsFeedEntity.setNid(newsFeed.getNid());
//                        break;
//                    }
//                }
                for (int i = 0; i < mArrNewsFeed.size(); i++) {
                    NewsFeed newsFeed = mArrNewsFeed.get(i);
                    if (newsFeed.getRtype() != 3 && newsFeed.getRtype() != 4) {
                        adLoadNewsFeedEntity.setNid(newsFeed.getNid());
                        break;
                    }
                }
                tstart = DateUtil.dateStr2Long(firstItem.getPtime()) + "";
            } else {
                tstart = System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
            }
//            requestUrl = HttpConstant.URL_FEED_PULL_DOWN + "tcr=" + tstart + fixedParams;
            adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
            /** 梁帅：判断是否是奇点频道 */
            requestUrl = HttpConstant.URL_FEED_AD_PULL_DOWN;
        } else {
            if (mFlag) {
                if (mIsFirst) {
                    ArrayList<NewsFeed> arrNewsFeed = mNewsFeedDao.queryByChannelId(mstrChannelId);
                    if (!TextUtil.isListEmpty(arrNewsFeed)) {
                        NewsFeed newsFeed = arrNewsFeed.get(0);
                        tstart = DateUtil.dateStr2Long(newsFeed.getPtime()) + "";
                    } else {
                        tstart = System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
                    }
//                  requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                    adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
                    requestUrl = HttpConstant.URL_FEED_AD_LOAD_MORE;
                } else {
                    if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                        NewsFeed lastItem = mArrNewsFeed.get(mArrNewsFeed.size() - 1);
                        tstart = DateUtil.dateStr2Long(lastItem.getPtime()) + "";

                    }
//                  requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                    adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
                    requestUrl = HttpConstant.URL_FEED_AD_LOAD_MORE;

                }
            } else {
                mSharedPreferences.edit().putBoolean("isshow", true).commit();
                mFlag = true;
                tstart = Long.valueOf(tstart) - 1000 * 60 * 60 * 12 + "";
//              requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
                requestUrl = HttpConstant.URL_FEED_AD_LOAD_MORE;
            }
        }
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
//        if ("1".equals(mstrChannelId)) {
        Logger.e("aaa", "gson==" + gson.toJson(adLoadNewsFeedEntity));
        Logger.e("ccc", "requestBody==" + gson.toJson(adLoadNewsFeedEntity));
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
//        } else {
//            FeedRequest<ArrayList<NewsFeed>> feedRequest = new FeedRequest<ArrayList<NewsFeed>>(Request.Method.GET, new TypeToken<ArrayList<NewsFeed>>() {
//            }.getType(), requestUrl, new Response.Listener<ArrayList<NewsFeed>>() {
//
//                @Override
//                public void onResponse(final ArrayList<NewsFeed> result) {
//                    loadNewFeedSuccess(result, flag);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    loadNewFeedError(error, flag);
//                }
//            });
////            HashMap<String, String> header = new HashMap<>();
////        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
////            header.put("Content-Type", "application/json");
////            feedRequest.setRequestHeaders(header);
//            HashMap<String, String> header = new HashMap<>();
//            header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(mContext).getAuthorToken());
//            feedRequest.setRequestHeader(header);
//            feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
//            requestQueue.add(feedRequest);
//        }
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
            mRefreshTitleBarAnimtation();

        }
//        for (Iterator it = result.iterator(); it.hasNext();) {
//            NewsFeed newsFeed = (NewsFeed) it.next();
//            if(newsFeed.getRtype()==3){
//                it.remove();
//            }
//        }
        if (flag == PULL_DOWN_REFRESH && !mIsFirst && result != null && result.size() > 0) {
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
                    Logger.e("aaa", "===========PULL_UP_REFRESH==========");
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
            //如果频道是1,则说明此频道的数据都是来至于其他的频道,为了方便存储,所以要修改其channelId
            if (mstrChannelId != null && ("1".equals(mstrChannelId) || "35".equals(mstrChannelId) || "44".equals(mstrChannelId))) {
                for (NewsFeed newsFeed : result) {
                    if ("1".equals(mstrChannelId)) {
                        newsFeed.setChannel(1);
                        if (newsFeed.getStyle() == 6) {
                            newsFeed.setStyle(8);
                        }
                    } else if ("35".equals(mstrChannelId)) {
                        newsFeed.setChannel(35);
                    } else if ("44".equals(mstrChannelId)) {
                        newsFeed.setChannel(44);
                    }
                }
//                    newsFeed.setChannel(1);
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
        } else if (error.toString().contains("4003") && mstrChannelId.equals("1")) {//说明三方登录已过期,防止开启3个loginty
            User user = SharedPreManager.mInstance(mContext).getUser(getActivity());
            user.setUtype("2");
            SharedPreManager.mInstance(mContext).saveUser(user);
//                    Intent loginAty = new Intent(getActivity(), LoginAty.class);
//                    startActivityForResult(loginAty, REQUEST_CODE);
            UserManager.registerVisitor(getActivity(), new UserManager.RegisterVisitorListener() {
                @Override
                public void registeSuccess() {
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
            UserManager.registerVisitor(getActivity(), new UserManager.RegisterVisitorListener() {
                @Override
                public void registeSuccess() {
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
        VideoVisibleControl();
    }

    @Override
    public void onResume() {
//        mHomeWatcher = new HomeWatcher(this.getActivity());
//        mHomeWatcher.setOnHomePressedListener(mOnHomePressedListener);
//        mHomeWatcher.startWatch();
        super.onResume();
        if (mRefreshTitleBar.getVisibility() == View.VISIBLE) {
            mRefreshTitleBar.setVisibility(View.GONE);
        }
        long time = (System.currentTimeMillis() - homeTime) / 1000;
        Logger.e("aaa", "time====" + time);
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
        if (mstrChannelId.equals("1")) {
            uploadInformation();
            uploadChannelInformation();
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
            }
        }
    }

    public void addHFView(LayoutInflater LayoutInflater) {

//        View mSearchHeaderView = LayoutInflater.inflate(R.layout.search_header_layout, null);
//        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
//        mSearchHeaderView.setLayoutParams(layoutParams);
        ListView lv = mlvNewsFeed.getRefreshableView();
//        lv.addHeaderView(mSearchHeaderView);
//        lv.setHeaderDividersEnabled(false);
//        mSearch_layout = (RelativeLayout) mSearchHeaderView.findViewById(R.id.search_layout);
//        mSearch_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent in = new Intent(getActivity(), TopicSearchAty.class);
//                getActivity().startActivity(in);
//            }
//        });
        footerView = (LinearLayout) LayoutInflater.inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        TextUtil.setLayoutBgResource(mContext, footerView, R.color.white);
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
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            Logger.e("aaa", "滑动到底部");
                            isBottom = true;
                            isListRefresh = true;
                            loadData(PULL_UP_REFRESH);
                        } else {
                            isBottom = false;
                            Logger.e("aaa", "在33333isBottom ==" + isBottom);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.v(TAG, "onScroll");
                if ("44".equals(mstrChannelId))
                   VideoVisibleControl();
            }
        });


    }

    public void mRefreshTitleBarAnimtation() {
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

    //上传地理位置等信息
    private void uploadInformation() {
        if (SharedPreManager.mInstance(mContext).getUser(mContext) != null) {
            try {
                List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
                final JSONArray array = new JSONArray();
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("app_id", packageInfo.packageName);
                    jsonObject.put("active", 1);
                    jsonObject.put("app_name", packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString());
                    array.put(jsonObject);
                }
                /** 设置品牌 */
                final String brand = Build.BRAND;
                /** 设置设备型号 */
                final String platform = Build.MODEL;
                final String requestUrl = HttpConstant.URL_UPLOAD_INFORMATION;
                RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                Long uid = null;
                User user = SharedPreManager.mInstance(mContext).getUser(mContext);
                if (user != null) {
                    uid = Long.valueOf(user.getMuid());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", uid);
                jsonObject.put("province", SharedPreManager.mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                jsonObject.put("city", SharedPreManager.mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                jsonObject.put("area", SharedPreManager.mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
                jsonObject.put("brand", brand);
                jsonObject.put("model", platform);
                jsonObject.put("apps", array);
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST, requestUrl,
                        jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObj) {
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                requestQueue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadChannelInformation() {
        if (SharedPreManager.mInstance(mContext).getUser(mContext) != null) {
            try {
                final String requestUrl = HttpConstant.URL_UPLOAD_CHANNEL_INFORMATION;
                RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                Long uid = null;
                if (SharedPreManager.mInstance(mContext).getUser(mContext) != null) {
                    uid = Long.valueOf(SharedPreManager.mInstance(mContext).getUser(mContext).getMuid());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", uid);
                //加入广告位id
                jsonObject.put("b", TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_FEED_AD_ID)));
                jsonObject.put("province", SharedPreManager.mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                jsonObject.put("city", SharedPreManager.mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                jsonObject.put("area", SharedPreManager.mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
                /**
                 * 1：奇点资讯， 2：黄历天气，3：纹字锁屏，4：猎鹰浏览器，5：白牌 6.纹字主题
                 */
                jsonObject.put("ctype", CommonConstant.NEWS_CTYPE);
                /**
                 * 1.ios 2.android 3.网页 4.无法识别
                 */
                jsonObject.put("ptype", CommonConstant.NEWS_PTYPE);
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST, requestUrl,
                        jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObj) {
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                requestQueue.add(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        if (PlayerManager.videoPlayView != null)
            vPlayer = PlayerManager.videoPlayView;
//        vPlayer= PlayerManager.getPlayerManager().initialize(mContext);

    }

    public int cPostion = -1;
    private int lastPostion = -1;
    private VPlayPlayer vPlayer;
    private boolean hasStarted = false;
    protected int[] mListItemRect;//当前item框的屏幕位置

    protected int[] mListItemSize;//当前item的大小

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, "执行了几次");
        if (vPlayer != null) {
            vPlayer.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        TransitionManager.beginDelayedTransition(vPlayerContainer);
                        vPlayer.onChanged(newConfig);
                        vPlayerContainer.removeView(vPlayer);
                        int position = getPlayItemPosition();
                        if (position != -1 && (vPlayer.getStatus() == PlayStateParams.STATE_PAUSED || vPlayer.isPlay())) {

                            FrameLayout playItemView = getPlayItemView(position);
                            View itemView = (View) playItemView.getParent();
                            if (itemView != null) {
                                itemView.findViewById(R.id.rl_video_show).setVisibility(View.GONE);
                            }
                            playItemView.removeAllViews();
                            playItemView.addView(vPlayer);

                            if (vPlayer.getStatus() != PlayStateParams.STATE_PAUSED)
                                vPlayer.showBottomControl(false);
                        }
                    }
                }, 300);


            } else {
//                mListItemRect = new int[2];
//                mListItemSize = new int[2];
//                saveLocationStatus(mContext,true,true);
//                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(vPlayer.getWidth(), vPlayer.getHeight());
//                lp.setMargins(mListItemRect[0], mListItemRect[1], 0, 0);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        TransitionManager.beginDelayedTransition(vPlayer);
//                        vPlayer.onChanged(newConfig);
//                        FrameLayout frameLayout = (FrameLayout) vPlayer.getParent();
//                        if (frameLayout != null) {
//                            frameLayout.removeView(vPlayer);
////                    frameLayout.removeAllViews();
//                            View itemView = (View) frameLayout.getParent();
//                            if (itemView != null) {
////                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
//                                View videoSHow = itemView.findViewById(R.id.rl_video_show);
//                                if (videoSHow != null) {
//                                    videoSHow.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        }
//                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                        vPlayerContainer.addView(vPlayer, lp);
//                        if (vPlayer.getStatus() != PlayStateParams.STATE_PAUSED)
//                            vPlayer.showBottomControl(false);
//
//                    }
//
//                }, 300);
//            }
                vPlayer.onChanged(newConfig);
                FrameLayout frameLayout = (FrameLayout) vPlayer.getParent();
                if (frameLayout != null) {
                    frameLayout.removeView(vPlayer);
//                    frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();
                    if (itemView != null) {
//                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
                        View videoSHow = itemView.findViewById(R.id.rl_video_show);
                        if (videoSHow != null) {
                            videoSHow.setVisibility(View.VISIBLE);
                        }
                    }
                }
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                vPlayerContainer.addView(vPlayer, lp);
                if (vPlayer.getStatus() != PlayStateParams.STATE_PAUSED)
                    vPlayer.showBottomControl(false);


            }

        } else {
            mAdapter.notifyDataSetChanged();
            mHomeRelative.setVisibility(View.VISIBLE);
        }
    }




    /**
     * 保存大小和状态
     */
    private void saveLocationStatus(Context context, boolean statusBar, boolean actionBar) {
        vPlayer.getLocationOnScreen(mListItemRect);
        int statusBarH = getStatusBarHeight(context);
        int actionBerH = getActionBarHeight((Activity) context);
        if (statusBar) {
            mListItemRect[1] = mListItemRect[1] - statusBarH;
        }
        if (actionBar) {
            mListItemRect[1] = mListItemRect[1] - actionBerH;
        }
        mListItemSize[0] = vPlayer.getWidth();
        mListItemSize[1] = vPlayer.getHeight();
    }


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
                relativeLayout.setVisibility(View.GONE);
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
                vPlayer.play(feed.getVideourl());
                mItemVideo.addView(vPlayer);
                lastPostion = cPostion;
            }

            @Override
            public void onItemClick(RelativeLayout rlNewsContent, NewsFeed feed) {

                if (feed == null)
                    return;
                cPostion = feed.getNid();
//        mainAty.newsFeedVideo = feed;
                if (cPostion != lastPostion && lastPostion != -1) {
                    vPlayer.stop();
                    vPlayer.release();
                    removeViews();
                }
                Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
                intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
                mContext.startActivity(intent);

                lastPostion = cPostion;
            }
        });


        vPlayer.setCompletionListener(new VPlayPlayer.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                if (vPlayer!=null) {
                    vPlayer.stop();
                    vPlayer.release();
                }
                removeViews();
                lastPostion = -1;
            }
        });


    }

    public void changePlayView(final NewsFeed feed) {
        cPostion = feed.getNid();
//        mainAty.newsFeedVideo = feed;
        if (cPostion != lastPostion && lastPostion != -1) {
            vPlayer.stop();
            vPlayer.release();
            removeViews();
        }
    }


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

    public FrameLayout getPlayItemView(int cPosition) {
        ListView lv = mlvNewsFeed.getRefreshableView();
        if (cPosition != -1) {
            View item = lv.getChildAt(cPosition);
            return (FrameLayout) item.findViewById(R.id.layout_item_video);
        }

        return null;
    }

    public void removeViews() {
        ViewGroup frameLayout = (ViewGroup) vPlayer.getParent();
        if (frameLayout != null && frameLayout.getChildCount() > 0) {
            frameLayout.removeView(vPlayer);
            View itemView = (View) frameLayout.getParent();
            if (itemView != null) {
                View show = itemView.findViewById(R.id.rl_video_show);
                if (show!=null)
                     show.setVisibility(View.VISIBLE);
            }
        }
    }


    private void VideoVisibleControl() {
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
                            videoSHow.setVisibility(View.VISIBLE);
                        }

                    }

                }
            }

        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
    }


}
