package com.news.yazhidao;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.news.sdk.adapter.NewsFeedAdapter;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.database.ChannelItemDao;
import com.news.sdk.database.VideoChannelDao;
import com.news.sdk.entity.ChannelItem;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.User;
import com.news.sdk.entity.Version;
import com.news.sdk.entity.VideoChannel;
import com.news.sdk.net.volley.ChannelListRequest;
import com.news.sdk.net.volley.VersionRequest;
import com.news.sdk.net.volley.VideoChannelRequest;
import com.news.sdk.pages.ChannelOperateAty;
import com.news.sdk.pages.NewsFeedFgt;
import com.news.sdk.utils.AdUtil;
import com.news.sdk.utils.AuthorizedUserUtil;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.utils.manager.UserManager;
import com.news.sdk.widget.CustomDialog;
import com.news.sdk.widget.FeedDislikePopupWindow;
import com.news.sdk.widget.channel.ChannelTabStrip;
import com.news.sdk.widget.tag.TagCloudLayout;
import com.news.yazhidao.pages.UserCenterAty;
import com.news.yazhidao.service.UpdateService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by fengjigang on 15/10/28.
 * 主界面view
 */
public class MainView extends View implements View.OnClickListener, NewsFeedFgt.NewsSaveDataCallBack {
    private static final String TAG = MainView.class.getSimpleName();
    public static final int REQUEST_CODE = 1001;
    public static final String ACTION_USER_LOGIN = "com.news.yazhidao.ACTION_USER_LOGIN";
    public static final String ACTION_USER_LOGOUT = "com.news.yazhidao.ACTION_USER_LOGOUT";
    public static final String KEY_INTENT_USER_URL = "key_intent_user_url";
    private ArrayList<ChannelItem> mUnSelChannelItems;
    RelativeLayout view;
    private ChannelTabStrip mChannelTabStrip;
    private ViewPager mViewPager;
    private MyViewPagerAdapter mViewPagerAdapter;
    private ImageView mChannelExpand, mivUserCenter, mDividingLine;
    private ChannelItemDao mChannelItemDao;
    private InterNetReceiver mReceiver;
    private long mLastPressedBackKeyTime;
    private TextView mtvNewWorkBar;
    private ConnectivityManager mConnectivityManager;
    private ArrayList<ChannelItem> mSelChannelItems;//默认展示的频道
    private HashMap<Integer, ArrayList<NewsFeed>> mSaveData = new HashMap<>();
    private RelativeLayout mMainView;
    private RequestManager mRequestManager;
    private long lastTime, nowTime;
    private final Context mContext;
    private VideoChannelDao videoChannelDao;

    public enum FONTSIZE {
        TEXT_SIZE_NORMAL(16), TEXT_SIZE_BIG(18), TEXT_SIZE_BIGGER(20);
        int size;

        FONTSIZE(int i) {
            size = i;
        }

        public int getfontsize() {
            return size;
        }
    }

    FragmentActivity activity;

    /**
     * 自定义的PopWindow
     */
    FeedDislikePopupWindow dislikePopupWindow;

    public MainView(FragmentActivity context) {
        super(context);
        mContext = context;
        initializeViews(context);
    }


    @Override
    public void result(int channelId, ArrayList<NewsFeed> results) {
        mSaveData.put(channelId, results);
    }

    long firstClick = 0;

    /**
     * 回到"奇点"频道并刷新
     */
    public void backFirstItemAndRefreshData() {
        if (System.currentTimeMillis() - firstClick <= 1500L) {
            firstClick = System.currentTimeMillis();
            return;
        }
        firstClick = System.currentTimeMillis();
        int currentItem = mViewPager.getCurrentItem();
        //1.返回到"奇点"频道
        mViewPager.setCurrentItem(0, false);
        mChannelTabStrip.scrollToFirstItem();
        //2.刷新数据
        MyViewPagerAdapter a = (MyViewPagerAdapter) mViewPager.getAdapter();
        NewsFeedFgt newsFeedFgt = (NewsFeedFgt) a.instantiateItem(mViewPager, mViewPager.getCurrentItem());
//        newsFeedFgt.refreshData();
        if (currentItem == 0) {
            newsFeedFgt.getFirstPosition();
        }
    }


    protected void initializeViews(final FragmentActivity mContext) {
        activity = mContext;
        videoChannelDao = new VideoChannelDao(mContext);
        setVideoChannelList();
        AdUtil.setAdChannel(activity);
        uploadInformation();
        uploadChannelInformation();
        mRequestManager = Glide.with(activity);
        lastTime = System.currentTimeMillis();
        view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.qd_aty_main, null);
        mMainView = (RelativeLayout) view.findViewById(R.id.main_layout);
        mDividingLine = (ImageView) view.findViewById(R.id.mDividingLine);
//        mChannelItemDao = new ChannelItemDao(mContext);

        mChannelItemDao = new ChannelItemDao(activity);
        mSelChannelItems = new ArrayList<>();
        mtvNewWorkBar = (TextView) view.findViewById(R.id.mNetWorkBar);
        mtvNewWorkBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivity(intent);
            }
        });
        mChannelTabStrip = (ChannelTabStrip) view.findViewById(R.id.mChannelTabStrip);
        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);
        mViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        mViewPager.setOffscreenPageLimit(2);
        mChannelExpand = (ImageView) view.findViewById(R.id.mChannelExpand);
        mChannelExpand.setOnClickListener(this);
        mViewPagerAdapter = new MyViewPagerAdapter(mContext.getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mChannelTabStrip.setViewPager(mViewPager);
        mChannelTabStrip.setChannelItems(mSelChannelItems);
        mivUserCenter = (ImageView) view.findViewById(R.id.mUserCenter);
        mivUserCenter.setOnClickListener(this);
        if (!SharedPreManager.mInstance(activity).getUserCenterIsShow()) {
            mivUserCenter.setVisibility(View.GONE);
        }
        dislikePopupWindow = (FeedDislikePopupWindow) view.findViewById(R.id.feedDislike_popupWindow);
//        dislikePopupWindow.setVisibility(View.GONE);

        dislikePopupWindow.setItemClickListerer(new TagCloudLayout.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                switch (position) {
                    case 0://不喜欢
//                        NewsFeedFgt newsFeedFgt= (NewsFeedFgt) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
//                        newsFeedFgt.disLikeItem();
                    case 1://重复、旧闻
                    case 2://内容质量差
                    case 3://不喜欢
                        final User user = SharedPreManager.mInstance(mContext).getUser(mContext);
                        if (user != null) {
                            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                            Map<String, Integer> map = new HashMap<>();
                            map.put("nid", dislikePopupWindow.getNewsId());
                            map.put("uid", user.getMuid());
                            map.put("reason", position);
                            JSONObject jsonObject = new JSONObject(map);
                            JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_DISSLIKE_RECORD, jsonObject,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    HashMap<String, String> header = new HashMap<>();
                                    header.put("Authorization", "Basic " + user.getAuthorToken());
                                    header.put("Content-Type", "application/json");
                                    header.put("X-Requested-With", "*");
                                    return header;
                                }
                            };
                            request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
                            requestQueue.add(request);
                        }
                        mNewsFeedAdapter.disLikeDeleteItem();
                        dislikePopupWindow.setVisibility(View.GONE);
                        ToastUtil.showReduceRecommendToast(mContext);
                        break;
                }
            }
        });
        setTheme();
        /**注册用户登录广播*/
        mReceiver = new InterNetReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        activity.registerReceiver(mReceiver, filter);
        UserManager.registerVisitor(activity, new UserManager.RegisterVisitorListener() {
            @Override
            public void registerSuccess() {
                LogUtil.adUserRegist(activity);
                MobclickAgent.onProfileSignIn(DeviceInfoUtil.getDeviceImei(mContext));
            }
        });
        setChannelList();
//        checkVersion();
    }

    /**
     * 检测是否自动升级
     */
    private void checkVersion() {
        User mUser = SharedPreManager.mInstance(mContext).getUser(mContext);
        VersionRequest<Version> versionRequest = new VersionRequest<Version>(Request.Method.GET,
                Version.class, HttpConstant.URL_APK_UPDATE + (mUser != null ? "&uid=" + SharedPreManager.mInstance(mContext).getUser(mContext).getMuid() : "") + "&ctype=" + CommonConstant.NEWS_CTYPE + "&ptype=" + CommonConstant.NEWS_PTYPE,
                new Response.Listener<Version>() {
                    @Override
                    public void onResponse(Version response) {
                        Logger.e(TAG, response.toString());
                        if (DeviceInfoUtil.getApkVersionCode(mContext) < response.getVersion_code()) {
                            showUpdateDialog(response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.e(TAG, "onErrorResponse");
                    }
                });
        QiDianApplication.getInstance().getRequestQueue().add(versionRequest);
    }


    /**
     * 自定义升级弹窗
     */
    protected void showUpdateDialog(final Version version) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle("发现新版本");
        builder.setMessage(version.getUpdateLog());
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (version.isForceUpdate()) {
                    ((Activity) mContext).finish();
                }
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(mContext, UpdateService.class);
                intent.putExtra("downloadLink", version.getDownloadLink());
                intent.putExtra("md5", version.getMd5());
                bundle.putSerializable("version", version);

                mContext.startService(intent);
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void setVideoChannelList() {
        videoChannelDao.queryForAll().get(0).getVersion_code();
        String params;
        if (videoChannelDao.queryForAll().get(0).getVersion_code()>0) {
            params = "chid=" + 44 + "&channel=" + CommonConstant.NEWS_CTYPE+"&version_code="+videoChannelDao.queryForAll().get(0).getVersion_code();
        }else
        {
            params = "chid=" + 44 + "&channel=" + CommonConstant.NEWS_CTYPE;
        }
        VideoChannelRequest<List<VideoChannel>> videoFeedRequest = new VideoChannelRequest<List<VideoChannel>>(Request.Method.GET, new TypeToken<List<VideoChannel>>() {
        }.getType(), HttpConstant.URL_VIDEO_CHANNEL_LIST+params , new Response.Listener<ArrayList<VideoChannel>>() {
            @Override
            public void onResponse(final ArrayList<VideoChannel> response) {
                Logger.v(TAG, response.toString());
                if (response != null && response.size() != 0) {
                    videoChannelDao.deletaForAll();
                    for (VideoChannel channel : response) {
                        videoChannelDao.insert(channel);
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.v(TAG, error.toString());
                    }
                });
        QiDianApplication.getInstance().getRequestQueue().add(videoFeedRequest);

    }

    private void setChannelList() {
        ChannelListRequest<ArrayList<ChannelItem>> newsFeedRequestPost = new ChannelListRequest(Request.Method.GET, new TypeToken<ArrayList<ChannelItem>>() {
        }.getType(), HttpConstant.URL_FETCH_CHANNEL_LIST + "channel=" + CommonConstant.NEWS_CTYPE, new Response.Listener<ArrayList<ChannelItem>>() {
            @Override
            public void onResponse(final ArrayList<ChannelItem> result) {
                if (!TextUtil.isListEmpty(result)) {
                    ArrayList<ChannelItem> arrayList = mChannelItemDao.queryForSelected();
                    if (!TextUtil.isListEmpty(arrayList) && arrayList.get(0).getVersion_code() != 0) {
                        int version_code = mChannelItemDao.queryForSelected().get(0).getVersion_code();
                        int resultVersion = result.get(0).getVersion_code();
                        if (version_code == resultVersion) {
                            return;
                        }
                    }
                    mChannelItemDao.deletaForAll();
                    mChannelItemDao.insertList(result);
                    ArrayList<ChannelItem> channelItems = mChannelItemDao.queryForSelected();
                    mViewPagerAdapter.setmChannelItems(channelItems);
                    mViewPagerAdapter.notifyDataSetChanged();
                    mChannelTabStrip.setViewPager(mViewPager);
                    mChannelTabStrip.notifyDataSetChanged();
                }
            }
        }, null);
        QiDianApplication.getInstance().getRequestQueue().add(newsFeedRequestPost);
    }

    public void setUserCenterImg(String url) {
        ImageUtil.setRoundImage(activity, mRequestManager, mivUserCenter, url, R.drawable.btn_user_center);
    }


    private class InterNetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    /////////////网络连接
                    String name = netInfo.getTypeName();

                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        /////WiFi网络

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                        /////有线网络

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        /////////3g网络

                    }
                    mtvNewWorkBar.setVisibility(View.GONE);
                } else {
                    ////////网络断开
                    mtvNewWorkBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void unregisterNetWorkReceiver() {
        if (mReceiver != null) {
            activity.unregisterReceiver(mReceiver);
        }
        nowTime = System.currentTimeMillis();
        LogUtil.appUseLog(activity, lastTime, nowTime);
    }

    public View getNewsView() {
        return this.view;
    }

    /**
     * 开始顶部 progress 刷新动画
     */
    public void startTopRefresh() {

    }

    /**
     * 停止顶部 progress 刷新动画
     */
    public void stopTopRefresh() {
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.mChannelExpand) {
            LogUtil.userActionLog(activity, CommonConstant.LOG_ATYPE_CHANNELCLICK, CommonConstant.LOG_PAGE_FEEDPAGE, CommonConstant.LOG_PAGE_CHANNELPAGE, null, false);
            Intent channelOperate = new Intent(activity, ChannelOperateAty.class);
            activity.startActivityForResult(channelOperate, REQUEST_CODE);
        } else if (id == R.id.mUserCenter) {
            User user = SharedPreManager.mInstance(activity).getUser(activity);
            if (user != null && user.isVisitor()) {
                AuthorizedUserUtil.sendUserLoginBroadcast(activity);
                return;
            }
            Intent userCenterAty = new Intent(activity, UserCenterAty.class);
            activity.startActivity(userCenterAty);
        }
    }

    ChannelItem item1;
    ArrayList<ChannelItem> channelItems;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUEST_CODE) {
            channelItems = (ArrayList<ChannelItem>) data.getSerializableExtra(ChannelOperateAty.KEY_USER_SELECT);
            int currPosition = mViewPager.getCurrentItem();
            item1 = mSelChannelItems.get(currPosition);
            int index = -1;
            for (int i = 0; i < channelItems.size(); i++) {
                ChannelItem item = channelItems.get(i);
                if (item1.getId() == (item.getId())) {
                    index = i;
                }
            }
            if (index == -1) {
                index = currPosition > channelItems.size() - 1 ? channelItems.size() - 1 : currPosition;
            }
            mViewPager.setCurrentItem(index);
            Fragment item = mViewPagerAdapter.getItem(index);
            if (item != null) {
                ((NewsFeedFgt) item).setNewsFeed(mSaveData.get(item1.getId()));
            }
            mViewPagerAdapter.setmChannelItems(channelItems);
            mViewPagerAdapter.notifyDataSetChanged();
            mChannelTabStrip.setChannelItems(channelItems);
            mChannelTabStrip.setViewPager(mViewPager);
        }
    }


    public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mSelChannelItems = mChannelItemDao.queryForSelected();
            mUnSelChannelItems = mChannelItemDao.queryForNormal();
            //统计用户频道订阅/非订阅 频道数
            HashMap<String, String> unSubChannel = new HashMap<>();
            unSubChannel.put("unsubscribed_channels", TextUtil.List2String(mUnSelChannelItems));
            HashMap<String, String> subChannel = new HashMap<>();
            subChannel.put("subscribed_channels", TextUtil.List2String(mSelChannelItems));
        }

        public void setmChannelItems(ArrayList<ChannelItem> pChannelItems) {
            mSelChannelItems = pChannelItems;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mSelChannelItems.get(position).getCname();
        }

        @Override
        public int getItemPosition(Object object) {
            int index = -1;
            if (channelItems != null && channelItems.size() > 0) {
                for (int i = 0; i < channelItems.size(); i++) {
                    ChannelItem item = channelItems.get(i);
                    if (item1.getId() == (item.getId())) {
                        index = i;
                    }
                }
            }
            return POSITION_NONE;
//            if (index == -1) {
//                return POSITION_NONE;
//            } else {
//                return POSITION_UNCHANGED;
//            }
        }

        @Override
        public int getCount() {
            return mSelChannelItems.size();
        }

        @Override
        public Fragment getItem(int position) {
            int channelId;
            try {
                channelId = mSelChannelItems.get(position).getId();
            } catch (Exception e) {
                channelId = mSelChannelItems.get(mSelChannelItems.size() - 1).getId();
            }
            NewsFeedFgt feedFgt = NewsFeedFgt.newInstance(channelId);
            feedFgt.setNewsFeedFgtPopWindow(mNewsFeedFgtPopWindow);
            feedFgt.setNewsSaveDataCallBack(MainView.this);
            return feedFgt;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int channelId = mSelChannelItems.get(position).getId();
            NewsFeedFgt fgt = (NewsFeedFgt) super.instantiateItem(container, position);
            ArrayList<NewsFeed> newsFeeds = mSaveData.get(channelId);
            if (TextUtil.isListEmpty(newsFeeds)) {
//                fgt.refreshData();
            } else {
                fgt.setNewsFeed(newsFeeds);
            }
            return fgt;
        }

//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
////            super.destroyItem(container, position, object);
//
//        }

    }

    int[] LocationInWindow = new int[2];
    NewsFeedAdapter mNewsFeedAdapter;
    NewsFeedFgt.NewsFeedFgtPopWindow mNewsFeedFgtPopWindow = new NewsFeedFgt.NewsFeedFgtPopWindow() {
        @Override
        public void showPopWindow(int x, int y, String PubName, int newsId, NewsFeedAdapter mAdapter) {
            mNewsFeedAdapter = mAdapter;
            view.getLocationInWindow(LocationInWindow);
            dislikePopupWindow.setNewsId(newsId);
            dislikePopupWindow.setSourceList("来源：" + PubName);
            dislikePopupWindow.showView(x, y - LocationInWindow[1]);
        }
    };


    public void setTheme() {
        User user = SharedPreManager.mInstance(activity).getUser(activity);
        ImageUtil.setRoundImage(activity, mRequestManager, mivUserCenter, user.getUserIcon(), R.drawable.btn_user_center);
        TextUtil.setLayoutBgResource(activity, mivUserCenter, R.color.color6);
        TextUtil.setLayoutBgResource(activity, mMainView, R.color.color6);
        TextUtil.setLayoutBgResource(activity, mChannelExpand, R.color.color6);
        TextUtil.setLayoutBgResource(activity, mDividingLine, R.color.color5);
        TextUtil.setLayoutBgResource(mContext, mtvNewWorkBar, R.color.color1);
        TextUtil.setLayoutBgResource(mContext, mChannelTabStrip, R.color.color6);
        TextUtil.setTextColor(mContext, mtvNewWorkBar, R.color.color10);
        mChannelTabStrip.notifyDataSetChanged();
    }

    /**
     * 梁帅：隐藏不喜欢窗口的方法
     *
     * @return
     */
    public boolean closePopWindow() {
        if (dislikePopupWindow.getVisibility() == View.VISIBLE) {//判断自定义的 popwindow 是否显示 如果现实按返回键关闭
            dislikePopupWindow.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * 梁帅：设置是否是  智能模式（不显示图片）
     */
    public void setNotShowImages(boolean isShow) {
        SharedPreManager.mInstance(activity).save(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES, isShow);
    }

    /**
     * 梁帅：修改全局字体文字大小
     *
     * @param fontSize {@link FONTSIZE}
     */
    public void setTextSize(FONTSIZE fontSize) {
        SharedPreManager.mInstance(activity).save("showflag", "textSize", fontSize.getfontsize());
        Intent intent = new Intent();
        intent.setAction(CommonConstant.CHANGE_TEXT_ACTION);
        activity.sendBroadcast(intent);
    }

    /**
     * 梁帅：是否让屏幕保持常亮
     *
     * @param isKeepOn
     */
    public void setKeepScreenOn(boolean isKeepOn) {
        SharedPreManager.mInstance(activity).save("showflag", "isKeepScreenOn", isKeepOn);
    }

    /**
     * 传入地理坐标
     *
     * @param location，省，市，县
     */
    public void setLocation(Location location, String province, String city, String address) {
        if (location != null) {
            SharedPreManager.mInstance(activity).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE, String.valueOf(location.getLatitude()));
            SharedPreManager.mInstance(activity).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE, String.valueOf(location.getLongitude()));
        }
        SharedPreManager.mInstance(activity).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE, province);
        SharedPreManager.mInstance(activity).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY, city);
        SharedPreManager.mInstance(activity).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR, address);
    }

    //上传地理位置等信息
    private void uploadInformation() {
        if (SharedPreManager.mInstance(activity).getUser(activity) != null) {
            try {
                List<PackageInfo> packages = activity.getPackageManager().getInstalledPackages(0);
                final JSONArray array = new JSONArray();
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("app_id", packageInfo.packageName);
                    jsonObject.put("active", 1);
                    jsonObject.put("app_name", packageInfo.applicationInfo.loadLabel(activity.getPackageManager()).toString());
                    array.put(jsonObject);
                }
                /** 设置品牌 */
                final String brand = Build.BRAND;
                /** 设置设备型号 */
                final String platform = Build.MODEL;
                final String requestUrl = HttpConstant.URL_UPLOAD_INFORMATION;
                RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                Long uid = null;
                User user = SharedPreManager.mInstance(activity).getUser(activity);
                if (user != null) {
                    uid = Long.valueOf(user.getMuid());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", uid);
                jsonObject.put("province", SharedPreManager.mInstance(activity).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                jsonObject.put("city", SharedPreManager.mInstance(activity).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                jsonObject.put("area", SharedPreManager.mInstance(activity).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
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
        if (SharedPreManager.mInstance(activity).getUser(activity) != null) {
            try {
                final String requestUrl = HttpConstant.URL_UPLOAD_CHANNEL_INFORMATION;
                RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                Long uid = null;
                if (SharedPreManager.mInstance(activity).getUser(activity) != null) {
                    uid = Long.valueOf(SharedPreManager.mInstance(activity).getUser(activity).getMuid());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", uid);
                jsonObject.put("appversion", activity.getString(R.string.version_name));
                //加入广告位id
                jsonObject.put("b", TextUtil.getBase64(AdUtil.getAdMessage(activity, CommonConstant.NEWS_FEED_GDT_API_BIGPOSID)));
                jsonObject.put("province", SharedPreManager.mInstance(activity).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                jsonObject.put("city", SharedPreManager.mInstance(activity).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                jsonObject.put("area", SharedPreManager.mInstance(activity).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
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

}
