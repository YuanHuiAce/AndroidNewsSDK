package demo.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jinsedeyuzhou.PlayerManager;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.ThemeManager;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.ChannelOperateAty;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;
import com.news.yazhidao.widget.FeedDislikePopupWindow;
import com.news.yazhidao.widget.channel.ChannelTabStrip;
import com.news.yazhidao.widget.tag.TagCloudLayout;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by fengjigang on 15/10/28.
 * 主界面view
 */
public class MainView extends View implements View.OnClickListener, NewsFeedFgt.NewsSaveDataCallBack {

    public static final int REQUEST_CODE = 1001;
    public static final String ACTION_USER_LOGIN = "com.news.yazhidao.ACTION_USER_LOGIN";
    public static final String ACTION_USER_LOGOUT = "com.news.yazhidao.ACTION_USER_LOGOUT";
    public static final String KEY_INTENT_USER_URL = "key_intent_user_url";
    private ArrayList<ChannelItem> mUnSelChannelItems;
    RelativeLayout view;

    private ChannelTabStrip mChannelTabStrip;
    private ViewPager mViewPager;
    private MyViewPagerAdapter mViewPagerAdapter;
    private ImageView mChannelExpand;
    private ChannelItemDao mChannelItemDao;
    private Handler mHandler = new Handler();
    private UserLoginReceiver mReceiver;
    private long mLastPressedBackKeyTime;
    private TextView mtvNewWorkBar;
    private ConnectivityManager mConnectivityManager;
    private ArrayList<ChannelItem> mSelChannelItems;//默认展示的频道
    private HashMap<String, ArrayList<NewsFeed>> mSaveData = new HashMap<>();
    private RelativeLayout mMainView;
    private TelephonyManager mTelephonyManager;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private VPlayPlayer vPlayPlayer;

    public enum FONTSIZE {
        TEXT_SIZE_SMALL(16), TEXT_SIZE_NORMAL(18), TEXT_SIZE_BIG(20);
        int size;

        FONTSIZE(int i) {
            size = i;
        }

        public int getfontsize() {
            return size;
        }
    }

    FragmentActivity activity;
    //baidu Map
//    public LocationClient mLocationClient = null;
//    public BDLocationListener myListener = new MyLocationListener();
//    private SimpleDraweeView mUserCenter;

    /**
     * 自定义的PopWindow
     */
    FeedDislikePopupWindow dislikePopupWindow;

    public MainView(FragmentActivity context) {
        super(context);
        initializeViews(context);


//        QiDianApplication.vPlayPlayer=new VPlayPlayer(context);
    }


    @Override
    public void result(String channelId, ArrayList<NewsFeed> results) {
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
//        MobclickAgent.onEvent(this,"bainews_user_assess_app");

        activity = mContext;
//        vPlayPlayer = new VPlayPlayer(mContext);
        vPlayPlayer= PlayerManager.getPlayerManager().initialize(mContext);
        view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.qd_aty_main, null);
        mMainView = (RelativeLayout) view.findViewById(R.id.main_layout);
        TextUtil.setLayoutBgColor(activity, mMainView, R.color.white);
        mChannelItemDao = new ChannelItemDao(mContext);
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
//                mUserCenter.setImageURI(Uri.parse("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0"));
        dislikePopupWindow = (FeedDislikePopupWindow) view.findViewById(R.id.feedDislike_popupWindow);
//        dislikePopupWindow.setVisibility(View.GONE);

        dislikePopupWindow.setItemClickListerer(new TagCloudLayout.TagItemClickListener() {
            Handler mHandler = new Handler();

            @Override
            public void itemClick(int position) {
                switch (position) {
                    case 0://不喜欢
//
//                        NewsFeedFgt newsFeedFgt= (NewsFeedFgt) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
//                        newsFeedFgt.disLikeItem();
                    case 1://重复、旧闻
                    case 2://内容质量差
                    case 3://不喜欢
                        mNewsFeedAdapter.disLikeDeleteItem();
                        dislikePopupWindow.setVisibility(View.GONE);
                        ToastUtil.showReduceRecommendToast(mContext);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 100);
                        break;
                }
            }
        });
        /**更新右下角用户登录图标*/
//        User user = SharedPreManager.getUser(this);
//        if (user != null) {
//            if (!TextUtil.isEmptyString(user.getUserIcon())) {
//                mUserCenter.setImageURI(Uri.parse(user.getUserIcon()));
//            }
//        }
        /**注册用户登录广播*/
        mReceiver = new UserLoginReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ACTION_USER_LOGOUT);
        filter.addAction(ACTION_USER_LOGIN);
        activity.registerReceiver(mReceiver, filter);
        /**请求系统权限*/
        try {
            getDeviceImei();
        } catch (Exception e) {
            SharedPreManager.save("flag", "imei", "");
        }
    }

    /**
     * 保存设置IMEI
     */
    private void getDeviceImei() {
        if (activity != null) {
            mTelephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephonyManager != null) {
                String deviceid = mTelephonyManager.getDeviceId();
                SharedPreManager.mInstance(activity).save("flag", "imei", deviceid);
            }
        }
    }
//    NewsFeedFgt.InitVideoPlayer initVideoPlayer=new NewsFeedFgt.InitVideoPlayer() {
//        @Override
//        public void setPlayer(VPlayPlayer vPlayer) {
//            vPlayer=vPlayPlayer;
//        }
//    };
    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        SharedPreManager.mInstance(activity).save("flag", "mac", info.getMacAddress());
        return info.getMacAddress();
    }

    private class UserLoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (ACTION_USER_LOGIN.equals(intent.getAction())) {
//                String url = intent.getStringExtra(KEY_INTENT_USER_URL);
//                if (!TextUtil.isEmptyString(url)) {
//                    mUserCenter.setImageURI(Uri.parse(url));
//                }
//            } else if (ACTION_USER_LOGOUT.equals(intent.getAction())) {
//                mUserCenter.setImageURI(null);
//            }
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
    }

    public View getNewsView() {
        return this.view;
    }

    public int getListViewScrollY() {

        return 0;
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

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mReceiver != null) {
//            unregisterReceiver(mReceiver);
//        }
//    }

    protected void loadData() {

        UserManager.registerVisitor(activity, null);
        //    mHandler.postDelayed(new Runnable() {
        //       @Override
//public void run() {
        //             UmengUpdateAgent.setUpdateAutoPopup(true);
        //            UmengUpdateAgent.update(MainAty.this);
        //        }
        //}, 2000);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mChannelExpand) {

//        switch (v.getId()) {
////            case R.id.mTopSearch:
////                Intent topicSearch = new Intent(MainAty.this, TopicSearchAty.class);
////                startActivity(topicSearch);
////                break;
//            case R.id.mChannelExpand:
            Intent channelOperate = new Intent(activity, ChannelOperateAty.class);
//                Mobclic/kAgent.onEvent(this, "user_open_channel_edit_page");
            activity.startActivityForResult(channelOperate, REQUEST_CODE);
        }
//                break;
//            case R.id.mUserCenter:
//                User user = SharedPreManager.getUser(this);
////                //FIXME debug
////                if (user == null){
////                    user = new User();
////                    user.setUserName("forward_one");
////                    user.setUserIcon("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0");
////                }
//                if (user == null) {
//                    Intent loginAty = new Intent(this, LoginAty.class);
//                    startActivity(loginAty);
//                } else {
//                    Intent userCenterAty = new Intent(this, UserCenterAty.class);
//                    startActivity(userCenterAty);
//                }
//                MobclickAgent.onEvent(this, "yazhidao_user_open_user_center");
//                break;
//        else if (v.getId() == R.id.mDetailLeftBack) {
////            case R.id.mDetailLeftBack:
//            MainAty.this.finish();
////                break;
//
//        }
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
                if (item1.getId().equals(item.getId())) {
                    index = i;
                }
            }
            if (index == -1) {
                Logger.e("jigang", "index = " + index);
                index = currPosition > channelItems.size() - 1 ? channelItems.size() - 1 : currPosition;
            }
            mViewPager.setCurrentItem(index);
            Fragment item = mViewPagerAdapter.getItem(index);
            if (item != null) {
                ((NewsFeedFgt) item).setNewsFeed(mSaveData.get(item1.getId()));
            }
            mViewPagerAdapter.setmChannelItems(channelItems);
            mViewPagerAdapter.notifyDataSetChanged();
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
//            MobclickAgent.onEventValue(MainAty.this, "user_unsubscribe_channels", unSubChannel, mUnSelChannelItems.size());

            HashMap<String, String> subChannel = new HashMap<>();
            subChannel.put("subscribed_channels", TextUtil.List2String(mSelChannelItems));
//            MobclickAgent.onEventValue(MainAty.this, "user_subscribed_channels", subChannel, mSelChannelItems.size());

        }

        public void setmChannelItems(ArrayList<ChannelItem> pChannelItems) {
            mSelChannelItems = pChannelItems;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mSelChannelItems.get(position).getName();
        }

        @Override
        public int getItemPosition(Object object) {
            int index = -1;
            if (channelItems != null && channelItems.size() > 0) {
                for (int i = 0; i < channelItems.size(); i++) {
                    ChannelItem item = channelItems.get(i);
                    if (item1.getId().equals(item.getId())) {
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
            String channelId = "";
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
            String channelId = mSelChannelItems.get(position).getId();
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
        public void showPopWindow(int x, int y, String PubName, NewsFeedAdapter mAdapter) {
            mNewsFeedAdapter = mAdapter;
            view.getLocationInWindow(LocationInWindow);

            dislikePopupWindow.setSourceList("来源：" + PubName);
            dislikePopupWindow.showView(x, y - LocationInWindow[1]);


        }

    };


    public void setTheme() {
//        TextUtil.setLayoutBgColor(activity,mChannelTabStrip.ge,R.color.white);
        TextUtil.setLayoutBgColor(activity, mMainView, R.color.white);
        mChannelTabStrip.setBackgroundColor(ThemeManager.getCurrentThemeRes(activity, R.color.channeltabstrip_bg));
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
     * @param ，省，市，县
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


}
