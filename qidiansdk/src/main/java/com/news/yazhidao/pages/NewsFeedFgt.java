package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.news.yazhidao.entity.AdDeviceEntity;
import com.news.yazhidao.entity.AdEntity;
import com.news.yazhidao.entity.AdImpressionEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.NewsFeedRequestPost;
import com.news.yazhidao.receiver.HomeWatcher;
import com.news.yazhidao.receiver.HomeWatcher.OnHomePressedListener;
import com.news.yazhidao.utils.AdUtil;
import com.news.yazhidao.utils.CrashHandler;
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
import java.util.Random;

import static com.news.yazhidao.utils.manager.SharedPreManager.mInstance;

public class NewsFeedFgt extends Fragment implements ThemeManager.OnThemeChangeListener {

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
    private int mDeleteIndex;
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
        rootView = LayoutInflater.inflate(R.layout.qd_activity_news, container, false);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        mRefreshTitleBar = (TextView) rootView.findViewById(R.id.mRefreshTitleBar);
        TextUtil.setLayoutBgColor(mContext, mRefreshTitleBar, R.color.white80);
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
        return rootView;
    }

    NewsFeedAdapter.clickShowPopWindow mClickShowPopWindow = new NewsFeedAdapter.clickShowPopWindow() {
        @Override
        public void showPopWindow(int x, int y, NewsFeed feed) {
            if (mNewsFeedFgtPopWindow != null) {
                mNewsFeedFgtPopWindow.showPopWindow(x, y, feed.getPname(), mAdapter);
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

    public String getAdMessage() {

        Gson gson = new Gson();
        Random random = new Random();
        AdImpressionEntity adImpressionEntity = new AdImpressionEntity();
        adImpressionEntity.setAid(random.nextInt(2) == 0 ? "98" : "100");
        /** 单图91  三图164 */
        adImpressionEntity.setHeight((int) (DeviceInfoUtil.obtainDensity() * 164) + "");
        adImpressionEntity.setWidth(DeviceInfoUtil.getScreenWidth(mContext) + "");

        AdDeviceEntity adDeviceEntity = new AdDeviceEntity();
        /** 设置IMEI */
        String imei = SharedPreManager.mInstance(mContext).get("flag", "imei");
        adDeviceEntity.setImei(DeviceInfoUtil.generateMD5(imei));
        adDeviceEntity.setImeiori(imei);
//        String mac = SharedPreManager.mInstance(mContext).get("flag", "mac");
//        adDeviceEntity.setMac(DeviceInfoUtil.generateMD5(mac));
        /** 设置AndroidID */
        String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        adDeviceEntity.setAnid(TextUtil.isEmptyString(androidId) ? null : DeviceInfoUtil.generateMD5(androidId));
        /** 设置设备品牌 */
        String brand = Build.BRAND;
        adDeviceEntity.setBrand(brand);
        /** 设置设备型号 */
        String platform = Build.MODEL;
        adDeviceEntity.setPlatform(platform);
        /** 设置操作系统 */
        adDeviceEntity.setOs("1");
        /** 设置操作系统版本号 */
        String version = Build.VERSION.RELEASE;
        adDeviceEntity.setOs_version(version);
        /** 设置屏幕分辨率 */
        adDeviceEntity.setDevice_size(CrashHandler.getResolution(mContext));
        /** 设置IP */
        String ip = "";
        if (DeviceInfoUtil.isWifiNetWorkState(mContext)) {
            ip = DeviceInfoUtil.getIpAddress(mContext);
        } else {
            ip = DeviceInfoUtil.getLocalIpAddress();
        }
        adDeviceEntity.setIp(ip);
        /** 设置网络环境 */
        String networkType = DeviceInfoUtil.getNetworkType(mContext);
        if (TextUtil.isEmptyString(networkType)) {
            adDeviceEntity.setNetwork("0");
        } else {
            if ("wifi".endsWith(networkType)) {
                adDeviceEntity.setNetwork("1");
            } else if ("2G".endsWith(networkType)) {
                adDeviceEntity.setNetwork("2");
            } else if ("3G".endsWith(networkType)) {
                adDeviceEntity.setNetwork("3");
            } else if ("4G".endsWith(networkType)) {
                adDeviceEntity.setNetwork("4");
            } else {
                adDeviceEntity.setNetwork("0");
            }
        }
        /** 设置经度 纬度 */
//        String locationJsonString = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_USER_LOCATION);
//        LocationEntity locationEntity = gson.fromJson(locationJsonString, LocationEntity.class);
//        adDeviceEntity.setLongitude(locationEntity.get);
        /** 设置横竖屏幕 */
        if (DeviceInfoUtil.isScreenChange(mContext)) {//横屏
            adDeviceEntity.setScreen_orientation("2");
        } else {//竖屏
            adDeviceEntity.setScreen_orientation("1");
        }


        AdEntity adEntity = new AdEntity();
        adEntity.setTs((System.currentTimeMillis() / 1000) + "");
        adEntity.setDevice(adDeviceEntity);
        adEntity.getImpression().add(adImpressionEntity);


        return gson.toJson(adEntity);

    }

    private void loadNewsFeedData(String url, final int flag) {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }
        String requestUrl;
        String tstart = System.currentTimeMillis() + "";
        String fixedParams = "&cid=" + mstrChannelId + "&uid=" + mInstance(mContext).getUser(mContext).getMuid();
        ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
        adLoadNewsFeedEntity.setCid(TextUtil.isEmptyString(mstrChannelId) ? null : Long.parseLong(mstrChannelId));
        adLoadNewsFeedEntity.setUid(mInstance(mContext).getUser(mContext).getMuid());
        adLoadNewsFeedEntity.setT(1);
        Gson gson = new Gson();
        adLoadNewsFeedEntity.setB(TextUtil.getBase64(getAdMessage()));

        if (flag == PULL_DOWN_REFRESH) {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                NewsFeed firstItem = mArrNewsFeed.get(0);
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

        Logger.e("ccc", "requestUrl==" + requestUrl);
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
//
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
        if (mDeleteIndex != 0) {
            mArrNewsFeed.remove(mDeleteIndex);
            mDeleteIndex = 0;
        }
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
            mDeleteIndex = result.size() - 1;
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
            if (mDeleteIndex != 0) {
                mArrNewsFeed.remove(mDeleteIndex);
                mDeleteIndex = 0;
                mAdapter.notifyDataSetChanged();
            }
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
            User user = mInstance(mContext).getUser(getActivity());
            user.setUtype("2");
            mInstance(mContext).saveUser(user);
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
        User user = mInstance(mContext).getUser(mContext);
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
        void showPopWindow(int x, int y, String pubName, NewsFeedAdapter mAdapter);
    }


    HomeWatcher mHomeWatcher;

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

    public void setTextSize() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    //上传地理位置等信息
    private void uploadInformation() {
        if (mInstance(mContext).getUser(mContext) != null) {
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
                User user = mInstance(mContext).getUser(mContext);
                if (user != null) {
                    uid = Long.valueOf(user.getMuid());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", uid);
                jsonObject.put("province", mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                jsonObject.put("city", mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                jsonObject.put("area", mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
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
                jsonObject.put("b", TextUtil.getBase64(AdUtil.getAdMessage(mContext)));
                jsonObject.put("province", SharedPreManager.mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                jsonObject.put("city", SharedPreManager.mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                jsonObject.put("area", SharedPreManager.mInstance(mContext).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
                /**
                 * 1：奇点资讯， 2：黄历天气，3：纹字锁屏，4：猎鹰浏览器，5：白牌
                 */
                jsonObject.put("ctype", 3);
                /**
                 * 1.ios 2.android 3.网页 4.无法识别
                 */
                jsonObject.put("ptype", 2);
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
}
