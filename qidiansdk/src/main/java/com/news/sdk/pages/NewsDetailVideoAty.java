package com.news.sdk.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.github.jinsedeyuzhou.utils.NetworkUtils;
import com.google.gson.reflect.TypeToken;
import com.news.sdk.R;
import com.news.sdk.adapter.NewsFeedAdapter;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.database.NewsDetailCommentDao;
import com.news.sdk.entity.NewsDetail;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.DetailOperateRequest;
import com.news.sdk.net.volley.NewsDetailRequest;
import com.news.sdk.receiver.HomeWatcher;
import com.news.sdk.utils.AuthorizedUserUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.SharePopupWindow;
import com.news.sdk.widget.SwipeBackViewpager;
import com.news.sdk.widget.UserCommentDialog;
import com.news.sdk.widget.swipebackactivity.SwipeBackLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.news.sdk.pages.NewsFeedFgt.VALUE_NEWS_NOTIFICATION;

/**
 * Created by fengjigang on 15/9/6.
 * 视频详情页
 */
public class NewsDetailVideoAty extends BaseActivity implements View.OnClickListener, SharePopupWindow.ShareDismiss {
    //    public static final String KEY_IMAGE_WALL_INFO = "key_image_wall_info";
    public static final String ACTION_REFRESH_COMMENT = "com.news.baijia.ACTION_REFRESH_COMMENT";

    //    private int mScreenWidth, mScreenHeight;
    //滑动关闭当前activity布局
    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId = "";
    private ImageView mivShareBg;
    private ArrayList<ArrayList> mNewsContentDataList;
    private ArrayList<View> mImageViews;
    private ArrayList<HashMap<String, String>> mImages;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    private ProgressBar imageAni;
    private TextView textAni;
    /**
     * 返回上一级,全文评论,分享
     */
    private View mDetailComment, mNewsDetailLoaddingWrapper;
    private ImageView mDetailShare;
    private ImageView mDetailLeftBack;
    //            ,mDetailRightMore
    private ImageView mNewsLoadingImg;
    private View mDetailView;
    private SharePopupWindow mSharePopupWindow;
    private RelativeLayout mDetailHeader, bgLayout;
    public static int REQUEST_CODE = 10003;

    private boolean isDisplay = true;
    private int defaultH;//图片新闻文本描述的默认高度
    private long mDurationStart;//统计用户读此条新闻时话费的时间
    private boolean isReadOver;//是否看完了全文,此处指的是翻到最下面
    public boolean isCommentPage;//是否是评论页
    private TextView mDetailAddComment;
    public TextView mDetailCommentNum;
    private View mImageWallWrapper;
    private ViewPager mImageWallVPager;
    private TextView mImageWallDesc, careful_Text;
    private View mDetailBottomBanner;
    public ImageView mDetailCommentPic, mDetailFavorite, careful_Image;
    public SwipeBackViewpager mNewsDetailViewPager;
    private RefreshPageBroReceiver mRefreshReceiver;
    private NewsFeed mNewsFeed;
    private String mImageUrl;
    private String mNid;
    private NewsDetailCommentDao newsDetailCommentDao;
    private NewsDetailVideoFgt mDetailVideoFgt;
    private NewsCommentFgt mCommentFgt;
    private LinearLayout carefulLayout;
    private boolean isFavorite;
    private RelativeLayout mSmallLayout;
    private FrameLayout mSmallScreen;
    public VPlayPlayer vPlayPlayer;
    private Handler mHandler;
    private ImageView mDetailRightMore;
    private UserCommentDialog mCommentDialog;
    private int mCommentNum;
    private boolean isUserComment;
    private String mSource;
    private boolean isShowComment;
    public int cPosition;
    private RelativeLayout mNoNetShow;
    private View mBottomLine;
    private ImageView mNoNetworkImage;
    private Context mContext;
    private View mTitleBottomLine;
    private ImageView mDetailPlayShow;
    private TextView mNoet;
    private HomeWatcher mHomeWatcher;

    @Override
    protected boolean isNeedAnimation() {
        return false;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }

    private void setTheme() {
        if (mDetailVideoFgt != null) {
            mDetailVideoFgt.setTheme();
        }
        if (mCommentFgt != null) {
            mCommentFgt.setTheme();
        }
        if (mNewsDetailViewPager != null) {
            int position = mNewsDetailViewPager.getCurrentItem();
            if (position == 0) {
                TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailCommentPic, mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
            } else {
                TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailCommentPic, R.drawable.btn_detail_switch_comment);
                mDetailCommentNum.setVisibility(View.GONE);
            }
        }
        if (isFavorite) {
            TextUtil.setImageResource(this, mDetailFavorite, R.drawable.btn_detail_favorite_select);
        } else {
            TextUtil.setImageResource(this, mDetailFavorite, R.drawable.btn_detail_favorite_normal);
        }
//        TextUtil.setLayoutBgResource(this, mDetailLeftBack, R.drawable.bg_left_back_selector);
        if (isCommentPage) {
            TextUtil.setImageResource(this, mDetailLeftBack, R.drawable.btn_left_back);
            TextUtil.setImageResource(this, mDetailRightMore, R.drawable.btn_detail_right_more);
        } else {
            ImageUtil.setAlphaImage(mDetailLeftBack, R.drawable.detial_video_back);
            ImageUtil.setAlphaImage(mDetailRightMore, R.drawable.detai_video_share);
        }
        TextUtil.setLayoutBgResource(this, mTitleBottomLine, R.color.color5);
        TextUtil.setLayoutBgResource(this, mDetailView, R.color.color6);
        TextUtil.setLayoutBgResource(this, mNewsDetailLoaddingWrapper, R.color.color6);
        TextUtil.setLayoutBgResource(this, bgLayout, R.color.color6);
        TextUtil.setLayoutBgResource(this, mDetailBottomBanner, R.color.color6);
        TextUtil.setLayoutBgResource(this, mDetailAddComment, R.drawable.user_add_comment);
        TextUtil.setTextColor(this, mDetailCommentNum, R.color.color1);
        TextUtil.setTextColor(this, mDetailAddComment, R.color.color3);
        TextUtil.setTextColor(this, mNoet, R.color.color3);
        TextUtil.setLayoutBgResource(this, mDetailCommentNum, R.color.color6);
        TextUtil.setImageResource(this, mDetailShare, R.drawable.btn_detail_share);
        TextUtil.setLayoutBgResource(this, mBottomLine, R.color.color5);
        TextUtil.setLayoutBgResource(this, mTitleBottomLine, R.color.color5);
        ImageUtil.setAlphaProgressBar(imageAni);
        TextUtil.setTextColor(this, textAni, R.color.color3);
        ImageUtil.setAlphaImage(mDetailPlayShow);
    }

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("nid", Long.valueOf(mNid));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LogUtil.userActionLog(NewsDetailVideoAty.this, CommonConstant.LOG_ATYPE_COMMENT, CommonConstant.LOG_PAGE_DETAILPAGE, CommonConstant.LOG_PAGE_DETAILPAGE, jsonObject, true);
            isUserComment = true;
            mCommentNum = mCommentNum + 1;
            mDetailCommentNum.setVisibility(View.VISIBLE);
            mDetailCommentNum.setText(TextUtil.getCommentNum(mCommentNum + ""));
            TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailCommentPic, mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
            careful_Image.setImageResource(R.drawable.hook_image);
            careful_Text.setText("评论成功");
            carefulAnimation();
        }
    }


    @Override
    protected boolean translucentStatus() {
        return false;
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_video_detail_layout);
//        if (SharedPreManager.mInstance(this).getBoolean("showflag", "isKeepScreenOn")) {
//            /** 梁帅：保持让屏幕常亮*/
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
        mNewsContentDataList = new ArrayList<>();
        mImageViews = new ArrayList<>();
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
        vPlayPlayer = new VPlayPlayer(this);
        vPlayPlayer.setAllowTouch(true);
    }

    @Override
    protected void initializeViews() {
        mContext = this;
        mImageUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_IMAGE);
        mSource = getIntent().getStringExtra(CommonConstant.KEY_SOURCE);
        isShowComment = getIntent().getBooleanExtra(NewsCommentFgt.KEY_SHOW_COMMENT, false);
        cPosition = getIntent().getIntExtra("position", 0);
        carefulLayout = (LinearLayout) findViewById(R.id.carefulLayout);
        mDetailView = findViewById(R.id.mDetailWrapper);
        mBottomLine = findViewById(R.id.mBottomLine);

        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mDetailPlayShow = (ImageView) findViewById(R.id.iv_detail_image_play);
        mTitleBottomLine = findViewById(R.id.title_bottom_line);

        mNoNetworkImage = (ImageView) findViewById(R.id.detail_image_bg);
        mNoet = (TextView) findViewById(R.id.tv_detail_video_noet);
        mNoNetShow = (RelativeLayout) findViewById(R.id.nonet_show);
        mNoNetShow.setOnClickListener(this);
        mNewsLoadingImg.setOnClickListener(this);
        mSmallLayout = (RelativeLayout) findViewById(R.id.detai_small_layout);
        mSmallScreen = (FrameLayout) findViewById(R.id.detail_small_screen);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        imageAni = (ProgressBar) findViewById(R.id.imageAni);
        textAni = (TextView) findViewById(R.id.textAni);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);

        mDetailHeader = (RelativeLayout) findViewById(R.id.mDetailHeader);
        mDetailLeftBack = (ImageView) findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailRightMore = (ImageView) findViewById(R.id.mDetailRightMore);
        mDetailRightMore.setOnClickListener(this);
        mDetailCommentPic = (ImageView) findViewById(R.id.mDetailCommentPic);
        mDetailFavorite = (ImageView) findViewById(R.id.mDetailFavorite);
        mDetailFavorite.setOnClickListener(this);
        if (!SharedPreManager.mInstance(this).getUserCenterIsShow()) {
            mDetailFavorite.setVisibility(View.GONE);
        }
        careful_Text = (TextView) findViewById(R.id.careful_Text);
        careful_Image = (ImageView) findViewById(R.id.careful_Image);
        mDetailComment = findViewById(R.id.mDetailComment);
        mDetailComment.setOnClickListener(this);
        mDetailShare = (ImageView) findViewById(R.id.mDetailShare);
        mDetailShare.setOnClickListener(this);
        mDetailAddComment = (TextView) findViewById(R.id.mDetailAddComment);
        mDetailAddComment.setOnClickListener(this);
        mDetailCommentNum = (TextView) findViewById(R.id.mDetailCommentNum);
        mDetailBottomBanner = findViewById(R.id.mDetailBottomBanner);
        mImageWallWrapper = findViewById(R.id.mImageWallWrapper);
        mImageWallVPager = (ViewPager) findViewById(R.id.mImageWallVPager);
        mImageWallDesc = (TextView) findViewById(R.id.mImageWallDesc);
        mNewsDetailViewPager = (SwipeBackViewpager) findViewById(R.id.mNewsDetailViewPager);

        //初始化新闻评论DAO
        newsDetailCommentDao = new NewsDetailCommentDao(this);
        setTheme();
    }

    long lastTime, nowTime;

    @Override
    protected void onResume() {
        super.onResume();
        if (mHomeWatcher == null) {
            mHomeWatcher = new HomeWatcher(this);
            mHomeWatcher.setOnHomePressedListener(mOnHomePressedListener);
            mHomeWatcher.startWatch();
        }
        LogUtil.onPageStart(CommonConstant.LOG_PAGE_VIDEODETAILPAGE);
        mDurationStart = System.currentTimeMillis();
        lastTime = System.currentTimeMillis();
        if (mRefreshReceiver == null) {
            mRefreshReceiver = new RefreshPageBroReceiver();
            IntentFilter filter = new IntentFilter(ACTION_REFRESH_COMMENT);
//            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            registerReceiver(mRefreshReceiver, filter);
        }
    }

    HomeWatcher.OnHomePressedListener mOnHomePressedListener = new HomeWatcher.OnHomePressedListener() {
        @Override
        public void onHomePressed() {
            finish();
        }


        @Override
        public void onHomeLongPressed() {
        }
    };

    @Override
    protected void onPause() {
        if (mHomeWatcher != null) {
            mHomeWatcher.stopWatch();
            mHomeWatcher = null;
        }
        LogUtil.onPageEnd(CommonConstant.LOG_PAGE_VIDEODETAILPAGE);
        nowTime = System.currentTimeMillis();
        //上报日志
        LogUtil.userReadLog(mNewsFeed, this, lastTime, nowTime, "100%");
        super.onPause();
    }


    @Override
    public void finish() {
        if (mNewsFeed != null && isUserComment && mNewsFeed.getNid() != 0) {
            Intent intent = new Intent();
            intent.putExtra(CommonConstant.NEWS_COMMENT_NUM, mCommentNum);
            intent.putExtra(CommonConstant.NEWS_ID, mNewsFeed.getNid());
            intent.setAction(CommonConstant.CHANGE_COMMENT_NUM_ACTION);
            sendBroadcast(intent);
        }
        super.finish();
        //如果是后台推送新闻消息过来的话，关闭新闻详情页的时候，就会打开主页面
        if (VALUE_NEWS_NOTIFICATION.equals(mSource)) {
//            Intent main = new Intent(this, MainAty.class);
            Intent main = new Intent();
            main.setClassName("com.news.yazhidao", "com.news.yazhidao.pages.MainActivity");
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(main);
        }
    }

    @Override
    protected void onDestroy() {
        vPlayPlayer.onDestory();
        vPlayPlayer = null;
        if (mRefreshReceiver != null) {
            unregisterReceiver(mRefreshReceiver);
            mRefreshReceiver = null;
        }
        //上报日志
//        if (SharedPreManager.mInstance(this).getBoolean("showflag", "isKeepScreenOn")) {
//            /**梁帅：清除屏幕常亮的这个设置，从而允许屏幕熄灭*/
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
        super.onDestroy();
    }

    FragmentPagerAdapter pagerAdapter;

    /**
     * 显示新闻详情和评论
     *
     * @param result
     */
    private void displayDetailAndComment(final NewsDetail result) {
        result.setIcon(mNewsFeed.getIcon());
        isFavorite = SharedPreManager.mInstance(this).myFavoriteisSame(mNid);
        if (result.getColflag() == 1) {
            if (!isFavorite) {
                SharedPreManager.mInstance(this).myFavoriteSaveList(mNewsFeed);
            }
            isFavorite = true;
            TextUtil.setImageResource(this, mDetailFavorite, R.drawable.btn_detail_favorite_select);
        } else {
            if (isFavorite) {
                SharedPreManager.mInstance(this).myFavoritRemoveItem(mNid);
            }
            TextUtil.setImageResource(this, mDetailFavorite, R.drawable.btn_detail_favorite_normal);
            isFavorite = false;
        }
        mNewsDetailViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Message msg = mHandler.obtainMessage();
                msg.what = 2;
                msg.obj = position;
                mHandler.sendMessage(msg);
                if (position == 1) {
                    isCommentPage = true;
                    TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailCommentPic, R.drawable.btn_detail_switch_comment);
                    mDetailCommentNum.setVisibility(View.GONE);
                    mTitleBottomLine.setVisibility(View.VISIBLE);
                    TextUtil.setImageResource(mContext, mDetailLeftBack, R.drawable.btn_left_back);
                    TextUtil.setImageResource(mContext, mDetailRightMore, R.drawable.btn_detail_right_more);
                } else {
                    isCommentPage = false;
                    mTitleBottomLine.setVisibility(View.GONE);
                    TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailCommentPic, mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                    mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
                    ImageUtil.setAlphaImage(mDetailLeftBack, R.drawable.detial_video_back);
                    ImageUtil.setAlphaImage(mDetailRightMore, R.drawable.detai_video_share);
                }
            }
        });
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    mDetailVideoFgt = new NewsDetailVideoFgt();
                    Bundle args = new Bundle();
                    args.putSerializable(NewsDetailFgt.KEY_DETAIL_RESULT, result);
                    args.putString(NewsDetailFgt.KEY_NEWS_DOCID, result.getDocid());
                    args.putString(NewsDetailFgt.KEY_NEWS_ID, mNid);
                    args.putString(NewsDetailFgt.KEY_NEWS_TITLE, mNewsFeed.getTitle());
                    args.putInt("position", cPosition);
                    mDetailVideoFgt.setArguments(args);
                    return mDetailVideoFgt;
                } else {
                    mCommentFgt = new NewsCommentFgt();
                    Bundle args = new Bundle();
                    args.putSerializable(NewsCommentFgt.KEY_NEWS_FEED, mNewsFeed);
                    args.putBoolean(NewsCommentFgt.KEY_TOP_MARGIN, true);
                    mCommentFgt.setArguments(args);
                    return mCommentFgt;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        mNewsDetailViewPager.setAdapter(pagerAdapter);
        if (isShowComment) {
            mNewsDetailViewPager.setCurrentItem(1);
        }
    }

    private boolean isRefresh = false;

    @Override
    protected void loadData() {
        if (NetUtil.checkNetWork(this)) {
            isRefresh = true;
            mNewsLoadingImg.setVisibility(View.GONE);
            mNewsDetailViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
            bgLayout.setVisibility(View.VISIBLE);
            mNewsFeed = (NewsFeed) getIntent().getSerializableExtra(NewsFeedFgt.KEY_NEWS_FEED);
            if (mNewsFeed != null) {
                mNid = mNewsFeed.getNid() + "";
            } else {
                mNid = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_ID);
            }
            User user = SharedPreManager.mInstance(this).getUser(NewsDetailVideoAty.this);
            if (user != null) {
                mUserId = user.getMuid() + "";
            }
//            mNid = 20022659+"";
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            NewsDetailRequest<NewsDetail> feedRequest = new NewsDetailRequest<NewsDetail>(Request.Method.GET, new TypeToken<NewsDetail>() {
            }.getType(), HttpConstant.URL_VIDEO_CONTENT + "nid=" + mNid + "&uid=" + mUserId, new Response.Listener<NewsDetail>() {

                @Override
                public void onResponse(NewsDetail result) {
                    isRefresh = false;
                    mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                    mNoNetShow.setVisibility(View.GONE);
                    if (bgLayout.getVisibility() == View.VISIBLE) {
                        bgLayout.setVisibility(View.GONE);
                    }
                    if (result != null) {
                        mNewsFeed = convert2NewsFeed(result);
                        displayDetailAndComment(result);
                        if (result.getComment() != 0) {
                            mDetailCommentNum.setVisibility(View.VISIBLE);
                            mCommentNum = result.getComment();
                            mDetailCommentNum.setText(TextUtil.getCommentNum(mCommentNum + ""));
                            TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailCommentPic, mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                        }
                        LogUtil.userClickLog(mNewsFeed, NewsDetailVideoAty.this, mSource);
                    } else {
                        ToastUtil.toastShort("此新闻暂时无法查看!");
                        NewsDetailVideoAty.this.finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isRefresh = false;
                    mNewsLoadingImg.setVisibility(View.VISIBLE);
                    bgLayout.setVisibility(View.GONE);
                }
            });
            feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(feedRequest);
        } else {
            mNewsDetailLoaddingWrapper.setVisibility(View.VISIBLE);
            mNoNetShow.setVisibility(View.VISIBLE);
            mNewsLoadingImg.setVisibility(View.VISIBLE);
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
        mNewsFeed.setImageUrl(mImageUrl);
        mNewsFeed.setNid(result.getNid());
        return mNewsFeed;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //视频全屏退出
            if (vPlayPlayer.onKeyDown(keyCode, event))
                return true;
            if (isCommentPage) {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0, true);
                TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailCommentPic, mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
                return true;
            }
        }
        //系统音量键控制
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (vPlayPlayer != null && vPlayPlayer.handleVolumeKey(keyCode))
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void shareDismiss() {
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mivShareBg.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (NetUtil.checkNetWork(this) && mNewsFeed != null && mNewsFeed.getNid() != 0) {
            Intent localIntent = new Intent();
            localIntent.putExtra(NewsFeedAdapter.KEY_NEWS_ID, mNewsFeed.getNid());
            if ((vPlayPlayer != null) && (vPlayPlayer.isPlay()))
                localIntent.putExtra("position", vPlayPlayer.getCurrentPosition());
            setResult(1006, localIntent);
        }
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.mDetailLeftBack) {
            if (isCommentPage) {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0, true);
                return;
            }
            onBackPressed();
        } else if (getId == R.id.mDetailAddComment) {
            User user = SharedPreManager.mInstance(NewsDetailVideoAty.this).getUser(NewsDetailVideoAty.this);
            if (user != null && user.isVisitor()) {
                AuthorizedUserUtil.sendUserLoginBroadcast(NewsDetailVideoAty.this);
                return;
            }
            if (mNewsFeed != null) {
                mCommentDialog = new UserCommentDialog();
                mCommentDialog.setDocid(mNewsFeed.getDocid());
                mCommentDialog.show(NewsDetailVideoAty.this.getSupportFragmentManager(), "UserCommentDialog");
            }
        } else if (getId == R.id.mDetailComment) {
            if (!isCommentPage) {
                isCommentPage = true;
                mNewsDetailViewPager.setCurrentItem(1);
                TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailCommentPic, R.drawable.btn_detail_switch_comment);
                mDetailCommentNum.setVisibility(View.GONE);
                if (!TextUtil.isEmptyString(mNid)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("nid", Long.valueOf(mNid));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.userActionLog(NewsDetailVideoAty.this, CommonConstant.LOG_ATYPE_COMMENTCLICK, CommonConstant.LOG_PAGE_VIDEODETAILPAGE, CommonConstant.LOG_PAGE_COMMENTPAGE, jsonObject, false);
                }
            } else {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0);
                TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailCommentPic, mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
                if (!TextUtil.isEmptyString(mNid)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("nid", Long.valueOf(mNid));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.userActionLog(NewsDetailVideoAty.this, CommonConstant.LOG_ATYPE_COMMENTCLICK, CommonConstant.LOG_PAGE_COMMENTPAGE, CommonConstant.LOG_PAGE_VIDEODETAILPAGE, jsonObject, false);
                }
            }
        } else if (getId == R.id.mDetailShare) {
            if (mNewsFeed != null) {
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                mSharePopupWindow = new SharePopupWindow(this, this);
                mSharePopupWindow.setVideo(true);
                mSharePopupWindow.setTitleAndNid(mNewsFeed.getTitle(), mNewsFeed.getNid(), mNewsFeed.getDescr());
                mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        } else if (getId == R.id.mNewsLoadingImg) {
            if (!isRefresh) {
                loadData();
            }
        } else if (getId == R.id.mDetailFavorite) {
            User user = SharedPreManager.mInstance(this).getUser(this);
            if (user != null && user.isVisitor()) {
                AuthorizedUserUtil.sendUserLoginBroadcast(NewsDetailVideoAty.this);
                return;
            }
            loadOperate();
        } else if (getId == R.id.nonet_show) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                if (!isRefresh) {
                    loadData();
                }
            } else {
                ToastUtil.toastShort("当前网络不可用，请检查网络设置");
            }
        } else if (getId == R.id.mDetailRightMore) {
            if (mNewsFeed != null) {
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                mSharePopupWindow = new SharePopupWindow(this, this);
                mSharePopupWindow.setTitleAndNid(mNewsFeed.getTitle(), mNewsFeed.getNid(), mNewsFeed.getDescr());
                mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        }
    }

    public void carefulAnimation() {
        //图片渐变模糊度始终
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
        //渐变时间
        alphaAnimation.setDuration(500);
        carefulLayout.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (carefulLayout.getVisibility() == View.GONE) {
                    carefulLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlphaAnimation alphaAnimationEnd = new AlphaAnimation(1.0f, 0f);
                        //渐变时间
                        alphaAnimationEnd.setDuration(500);
                        carefulLayout.startAnimation(alphaAnimationEnd);
                        alphaAnimationEnd.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if (carefulLayout.getVisibility() == View.VISIBLE) {
                                    carefulLayout.setVisibility(View.GONE);
                                }

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


    /**
     * 梁帅：收藏上传接口(关心放到NewsDetailFgt)
     */
    public void loadOperate() {
        if (!NetUtil.checkNetWork(this)) {
            ToastUtil.toastShort("无法连接到网络，请稍后再试");
            return;
        }
        JSONObject json = new JSONObject();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        Logger.e("aaa", "type====" + (isFavorite ? Request.Method.DELETE : Request.Method.POST));
        Logger.e("aaa", "url===" + HttpConstant.URL_ADDORDELETE_FAVORITE + "nid=" + mNid + "&uid=" + mUserId);
        DetailOperateRequest detailOperateRequest = new DetailOperateRequest((isFavorite ? Request.Method.DELETE : Request.Method.POST),
                HttpConstant.URL_ADDORDELETE_FAVORITE + "nid=" + mNid + "&uid=" + mUserId,
                json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                careful_Image.setImageResource(R.drawable.hook_image);
                if (isFavorite) {
                    isFavorite = false;
                    careful_Text.setText("收藏已取消");
                    SharedPreManager.mInstance(NewsDetailVideoAty.this).myFavoritRemoveItem(mNid);
                    TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailFavorite, R.drawable.btn_detail_favorite_normal);
                } else {
                    isFavorite = true;
                    careful_Text.setText("收藏成功");
                    Logger.e("aaa", "收藏成功数据：" + mNewsFeed.toString());
                    SharedPreManager.mInstance(NewsDetailVideoAty.this).myFavoriteSaveList(mNewsFeed);
                    TextUtil.setImageResource(NewsDetailVideoAty.this, mDetailFavorite, R.drawable.btn_detail_favorite_select);
                }
                carefulAnimation();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                careful_Text.setText("收藏失败");
                carefulAnimation();
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.mInstance(this).getUser(this).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        detailOperateRequest.setRequestHeader(header);
        detailOperateRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(detailOperateRequest);
    }
}
