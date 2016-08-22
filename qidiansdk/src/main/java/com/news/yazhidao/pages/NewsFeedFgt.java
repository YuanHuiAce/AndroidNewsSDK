package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
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
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.FeedRequest;
import com.news.yazhidao.receiver.HomeWatcher;
import com.news.yazhidao.receiver.HomeWatcher.OnHomePressedListener;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;

import java.util.ArrayList;
import java.util.HashMap;

public class NewsFeedFgt extends Fragment implements Handler.Callback {

    public static final String KEY_NEWS_CHANNEL = "key_news_channel";
    public static final String KEY_PUSH_NEWS = "key_push_news";//表示该新闻是后台推送过来的
    public static final String KEY_NEWS_IMG_URL = "key_news_img_url";//确保新闻详情中有一张图
    public static final String KEY_NEWS_TYPE = "key_news_type";//新闻类型,是否是大图新闻
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_FEED = "key_news_feed";
    public static final String KEY_NEWS_IMAGE = "key_news_image";

    /**
     * 当前fragment 所对应的新闻频道
     */
    public static String KEY_CHANNEL_ID = "key_channel_id";
    public static String KEY_WORD = "key_word";
    public static String KEY_NEWS_SOURCE = "key_news_source";
    public static String KEY_URL = "key_url";
    public static String KEY_NEWS_ID = "key_news_id";
    public static String KEY_COLLECTION = "key_collection";

    public static String KEY_TITLE = "key_title";
    public static String KEY_PUBNAME = "key_pubname";
    public static String KEY_PUBTIME = "key_pubtime";
    public static String KEY_COMMENTCOUNT = "key_commentcount";

    public static final int REQUEST_CODE = 1060;

    public static final String VALUE_NEWS_NOTIFICATION = "notification";
    public static final int PULL_DOWN_REFRESH = 1;
    public static final int PULL_UP_REFRESH = 2;
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsFeed = new ArrayList<NewsFeed>();
    private Context mContext;
    private NetworkRequest mRequest;
    private PullToRefreshListView mlvNewsFeed;
    private View rootView;
    private String mstrUserId, mstrChannelId, mstrKeyWord;
    private NewsFeedDao mNewsFeedDao;
    private boolean mFlag;
    private SharedPreferences mSharedPreferences;
    /**
     * 热词页面加载更多
     */
    private int mSearchPage = 1;
    private boolean mIsFirst = true;
    private int mDeleteIndex;
    /**
     * 当前的fragment 是否已经加载过数据
     */
//    private boolean isLoadedData = false;
    private NewsSaveDataCallBack mNewsSaveCallBack;
    private View mHomeRelative;
    private View mHomeRetry;
    private RelativeLayout bgLayout, mSearch_layout;
    private boolean isListRefresh = false;
    private boolean isNewVisity = false;//当前页面是否显示
    private boolean isNeedAddSP = true;
    private Handler mHandler;
    private Runnable mThread;
    private boolean isClickHome;
    private TextView footView_tv, mRefreshTitleBar;
    private ProgressBar footView_progressbar;
    private boolean isBottom;


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

    boolean isNoteLoadDate;

    public void setNewsFeed(ArrayList<NewsFeed> results) {
        isNoteLoadDate = true;
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
        if (isNewVisity && isNeedAddSP) {//切换到别的页面加入他
//            addSP(mArrNewsFeed);//第一次进入主页的时候会加入一次，不用担心这次加入是没有数据的

            isNeedAddSP = false;
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
        if (!TextUtil.isListEmpty(mArrNewsFeed)) {
            isNoteLoadDate = true;
        } else {
            isNoteLoadDate = false;
        }
        mlvNewsFeed.getRefreshableView().setSelection(0);
        mlvNewsFeed.getRefreshableView().smoothScrollToPosition(0);
    }


    public void refreshData() {
        if (mlvNewsFeed == null) {//防止listview为空
            return;
        }
        isNoteLoadDate = false;
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
        User user = SharedPreManager.getUser(mContext);
        if (user != null)
            mstrUserId = user.getUserId();
        else
            mstrUserId = "";
        mSharedPreferences = getActivity().getSharedPreferences("showflag", 0);
        mFlag = mSharedPreferences.getBoolean("isshow", false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e("jigang", "requestCode = " + requestCode);
        if (requestCode == NewsFeedAdapter.REQUEST_CODE && data != null) {
            int newsId = data.getIntExtra(NewsFeedAdapter.KEY_NEWS_ID, 0);
            Logger.e("jigang", "newsid = " + newsId);

            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                for (NewsFeed item : mArrNewsFeed) {
                    if (item != null && newsId == item.getNid()) {
                        item.setRead(true);
                        mNewsFeedDao.update(item);
                    }


                }
                mAdapter.notifyDataSetChanged();
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
//        }else if (requestCode == LoginAty.REQUEST_CODE && data != null){
//            loadData(PULL_DOWN_REFRESH);
        }
    }


    public View onCreateView(LayoutInflater LayoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mstrChannelId = arguments.getString(KEY_CHANNEL_ID);
            mstrKeyWord = arguments.getString(KEY_WORD);
        }
        rootView = LayoutInflater.inflate(R.layout.qd_activity_news, container, false);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        mHomeRelative = rootView.findViewById(R.id.mHomeRelative);
        mRefreshTitleBar = (TextView) rootView.findViewById(R.id.mRefreshTitleBar);


        mHomeRetry = rootView.findViewById(R.id.mHomeRetry);
        mHomeRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlvNewsFeed.setRefreshing();
                mHomeRetry.setVisibility(View.GONE);
            }
        });


        mlvNewsFeed = (PullToRefreshListView) rootView.findViewById(R.id.news_feed_listView);
        mlvNewsFeed.setMode(PullToRefreshBase.Mode.BOTH);
        mlvNewsFeed.setMainFooterView(true);

        mlvNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                Logger.e("aaa", "刷新");
                loadData(PULL_DOWN_REFRESH);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                Logger.e("aaa", "加载");
                loadData(PULL_UP_REFRESH);

            }
        });

        addHFView(LayoutInflater);


        mAdapter = new NewsFeedAdapter(getActivity(), this, null);
        mAdapter.setClickShowPopWindow(mClickShowPopWindow);

        mlvNewsFeed.setAdapter(mAdapter);

        mlvNewsFeed.setEmptyView(View.inflate(mContext, R.layout.qd_listview_empty_view, null));

        setUserVisibleHint(getUserVisibleHint());
//        String platform = AnalyticsConfig.getChannel(getActivity());
        //load news data
        mHandler = new Handler();
        mThread = new Runnable() {
            @Override
            public void run() {
//                mlvNewsFeed.setRefreshing();
                loadData(PULL_DOWN_REFRESH);
                isListRefresh = false;

            }
        };
        int delay = 1500;
        if (mstrChannelId != null && mstrChannelId.equals("1")) {
            delay = 500;
        }
        mHandler.postDelayed(mThread, delay);
        return rootView;
    }

    NewsFeedAdapter.clickShowPopWindow mClickShowPopWindow = new NewsFeedAdapter.clickShowPopWindow() {
        @Override
        public void showPopWindow(int x, int y, NewsFeed feed) {
            mNewsFeedFgtPopWindow.showPopWindow(x, y, feed.getPname(), mAdapter);

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeCallbacks(mThread);
        }
        Logger.e("jigang", "newsfeedfgt onDestroyView" + mstrChannelId);
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    /**
     * 设置搜索热词
     *
     * @param pKeyWord
     */
    public void setSearchKeyWord(String pKeyWord) {
        mAdapter.setSearchKeyWord(pKeyWord);
        this.mstrKeyWord = pKeyWord;
        mArrNewsFeed = null;
        mSearchPage = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                mlvNewsFeed.setRefreshing();
                loadData(PULL_DOWN_REFRESH);
                isListRefresh = false;
            }
        }, 1000);
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
        String fixedParams = "&cid=" + mstrChannelId + "&uid=" + SharedPreManager.getUser(mContext).getMuid();
        if (flag == PULL_DOWN_REFRESH) {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                NewsFeed firstItem = mArrNewsFeed.get(0);
                tstart = DateUtil.dateStr2Long(firstItem.getPtime()) + "";
            } else {
                tstart = System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
            }
            requestUrl = HttpConstant.URL_FEED_PULL_DOWN + "tcr=" + tstart + fixedParams;
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
                    requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                } else {
                    if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                        NewsFeed lastItem = mArrNewsFeed.get(mArrNewsFeed.size() - 1);
                        tstart = DateUtil.dateStr2Long(lastItem.getPtime()) + "";
                    }
                    requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                }
            } else {
                mSharedPreferences.edit().putBoolean("isshow", true).commit();
                mFlag = true;
                tstart = Long.valueOf(tstart) - 1000 * 60 * 60 * 12 + "";
                requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
            }
        }
        System.out.println("requestUrl:" + requestUrl);
        Logger.e("jigang", "request url =" + requestUrl);
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        FeedRequest<ArrayList<NewsFeed>> feedRequest = new FeedRequest<ArrayList<NewsFeed>>(Request.Method.GET, new TypeToken<ArrayList<NewsFeed>>() {
        }.getType(), requestUrl, new Response.Listener<ArrayList<NewsFeed>>() {

            @Override
            public void onResponse(final ArrayList<NewsFeed> result) {

                if (mDeleteIndex != 0) {
                    mArrNewsFeed.remove(mDeleteIndex);
                    mDeleteIndex = 0;
                }
                if (mIsFirst || flag == PULL_DOWN_REFRESH) {
                    if (result == null || result.size() == 0) {
                        return;
                    }
                    mRefreshTitleBar.setText("又发现了" + result.size() + "条新数据");
                    mRefreshTitleBarAnimtation();

                }
                if (flag == PULL_DOWN_REFRESH && !mIsFirst && result != null && result.size() > 0) {
                    NewsFeed newsFeed = new NewsFeed();
                    newsFeed.setStyle(900);
                    result.add(newsFeed);
                    mDeleteIndex = result.size() - 1;
                }

                mHomeRetry.setVisibility(View.GONE);
                stopRefresh();
                if (result != null && result.size() > 0) {

                    mSearchPage++;
                    switch (flag) {
                        case PULL_DOWN_REFRESH:
                            if (mArrNewsFeed == null)
                                mArrNewsFeed = result;
                            else
                                mArrNewsFeed.addAll(0, result);
                            mlvNewsFeed.getRefreshableView().setSelection(0);
                            Logger.i("aaa", "mlvNewsFeed.getRefreshableView().setSelection(0);");
//                            mRefreshTitleBar.setText("又发现了"+result.size()+"条新数据");
//                            mRefreshTitleBar.setVisibility(View.VISIBLE);
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(mRefreshTitleBar.getVisibility() == View.VISIBLE){
//                                        mRefreshTitleBar.setVisibility(View.GONE);
//                                    }
//
//                                }
//                            }, 1000);
                            break;
                        case PULL_UP_REFRESH:
                            Logger.e("aaa", "===========PULL_UP_REFRESH==========");
                            if (isNewVisity) {//首次进入加入他
//                                addSP(result);
                                isNeedAddSP = false;

                            }
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
                    if (mstrChannelId != null && "1".equals(mstrChannelId)) {
                        for (NewsFeed newsFeed : result)
                            newsFeed.setChannel(1);
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
//                    showChangeTextSizeView();
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
                if (flag == PULL_DOWN_REFRESH)
                    mlvNewsFeed.getRefreshableView().setSelection(0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
                if (error.toString().contains("2002")) {
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
                    User user = SharedPreManager.getUser(getActivity());
                    user.setUtype("2");
                    SharedPreManager.saveUser(user);
//                    Intent loginAty = new Intent(getActivity(), LoginAty.class);
//                    startActivityForResult(loginAty, REQUEST_CODE);
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
                if (flag == PULL_DOWN_REFRESH)
                    mlvNewsFeed.getRefreshableView().setSelection(0);
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
        feedRequest.setRequestHeader(header);
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
        Logger.e("jigang", "uuid = " + SharedPreManager.getUUID() + ",channelid =" + mstrChannelId + ",tstart =" + tstart);
    }


    public void loadData(final int flag) {
        User user = SharedPreManager.getUser(mContext);
        Logger.e("aaa", "loaddata -----" + flag);
        Logger.e("aaa", "loadData:user === " + user);
        if (null != user) {
            if (NetUtil.checkNetWork(mContext)) {
                if (!isNoteLoadDate) {
                    if (!TextUtil.isEmptyString(mstrKeyWord)) {
                        loadNewsFeedData("search", flag);
                    } else if (!TextUtil.isEmptyString(mstrChannelId))
                        loadNewsFeedData("recommend", flag);
                    startTopRefresh();
                } else {
                    isNoteLoadDate = false;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mlvNewsFeed.onRefreshComplete();
                        }
                    }, 500);
                }
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mlvNewsFeed.onRefreshComplete();
                    }
                }, 500);
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
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mlvNewsFeed.onRefreshComplete();
                }
            }, 500);
            //请求token
            UserManager.registerVisitor(getActivity(), new UserManager.RegisterVisitorListener() {
                @Override
                public void registeSuccess() {
                    loadData(flag);
                }
            });
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }


    NewsFeedFgtPopWindow mNewsFeedFgtPopWindow;

    public void setNewsFeedFgtPopWindow(NewsFeedFgtPopWindow mNewsFeedFgtPopWindow) {
        this.mNewsFeedFgtPopWindow = mNewsFeedFgtPopWindow;
    }

    public interface NewsFeedFgtPopWindow {
        public void showPopWindow(int x, int y, String pubName, NewsFeedAdapter mAdapter);
    }


    HomeWatcher mHomeWatcher;

    @Override
    public void onResume() {
        mHomeWatcher = new HomeWatcher(this.getActivity());
        mHomeWatcher.setOnHomePressedListener(mOnHomePressedListener);
        mHomeWatcher.startWatch();
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


    }

    @Override
    public void onPause() {
        super.onPause();
        mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
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

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                Logger.e("aaa", "文字的改变！！！");
//                int size = intent.getIntExtra("textSize", CommonConstant.TEXT_SIZE_NORMAL);
//                mSharedPreferences.edit().putInt("textSize", size).commit();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

//    public void addSP(ArrayList<NewsFeed> result) {
//        ArrayList<UploadLogDataEntity> uploadLogDataEntities = new ArrayList<UploadLogDataEntity>();
//        for (NewsFeed bean : result) {
//            UploadLogDataEntity uploadLogDataEntity = new UploadLogDataEntity();
//            uploadLogDataEntity.setN(bean.getNid()+"");
//            uploadLogDataEntity.setT("0");//需要改成typeID
//            uploadLogDataEntity.setC(bean.getChannel()+"");
//            uploadLogDataEntities.add(uploadLogDataEntity);
//        }
//        int saveNum = SharedPreManager.upLoadLogSaveList(mstrUserId, CommonConstant.UPLOAD_LOG_MAIN, uploadLogDataEntities);
//        Logger.e("ccc", "主页的数据====" + SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_MAIN));
//    }


    //    int lastY = 0;
//    int MAX_PULL_BOTTOM_HEIGHT = 100;
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
        final LinearLayout footerView = (LinearLayout) LayoutInflater.inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
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

}
