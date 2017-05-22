package com.news.sdk.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import com.google.gson.reflect.TypeToken;
import com.news.sdk.R;
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
import com.news.sdk.utils.AuthorizedUserUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.SharePopupWindow;
import com.news.sdk.widget.UserCommentDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * 新闻展示详情页
 */
public class NewsDetailAty2 extends BaseActivity implements View.OnClickListener, SharePopupWindow.ShareDismiss {

    public static final String KEY_IMAGE_WALL_INFO = "key_image_wall_info";
    public static final String ACTION_REFRESH_COMMENT = "com.news.baijia.ACTION_REFRESH_COMMENT";

    //滑动关闭当前activity布局
//    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId = "";
    private ImageView mivShareBg;
    private ArrayList<ArrayList> mNewsContentDataList;
    private ArrayList<View> mImageViews;
    private ArrayList<HashMap<String, String>> mImages;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    /**
     * 返回上一级,全文评论,分享
     */
    private View mHeaderDivider, mDetailComment, mNewsDetailLoaddingWrapper;
    private ImageView mDetailShare;
    private ImageView mDetailLeftBack;
    private TextView mDetailRightMore;
    private View mDetailView;
    private SharePopupWindow mSharePopupWindow;
    private RelativeLayout mDetailHeader, bgLayout;
    private ProgressBar imageAni;
    public boolean isCommentPage;//是否是评论页
    private View mBottomLine;
    public TextView mDetailAddComment, mDetailCommentNum;
    private View mImageWallWrapper;
    private ViewPager mImageWallVPager;
    private TextView mImageWallDesc, careful_Text;
    private View mDetailBottomBanner;
    public ImageView mDetailCommentPic, mDetailFavorite, careful_Image;
    public ViewPager mNewsDetailViewPager;
    private RefreshPageBroReceiver mRefreshReceiver;
    private UserCommentDialog mCommentDialog;
    private NewsFeed mNewsFeed;
    private String mImageUrl;
    private String mNid;
    private NewsDetailCommentDao newsDetailCommentDao;
    private boolean isRefresh = false;
    private LinearLayout carefulLayout;
    private boolean isFavorite;
    long lastTime, nowTime;
    private int mCommentNum;
    private NewsDetailFgt mDetailFgt;
    private NewsCommentFgt mCommentFgt;
    private boolean isUserComment;
    private String mSource;
    private boolean isForeground;

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            isUserComment = true;
            mCommentNum = mCommentNum + 1;
            mDetailCommentNum.setVisibility(View.VISIBLE);
            mDetailCommentNum.setText(TextUtil.getCommentNum(mCommentNum + ""));
            mDetailCommentPic.setImageResource(mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
        }
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_news_detail_layout);
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
    }

    @Override
    protected void initializeViews() {
        mImageUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_IMAGE);
        mSource = getIntent().getStringExtra(CommonConstant.KEY_SOURCE);
//        mSwipeBackLayout = getSwipeBackLayout();
//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        carefulLayout = (LinearLayout) findViewById(R.id.carefulLayout);
        mDetailView = findViewById(R.id.mDetailWrapper);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsDetailLoaddingWrapper.setOnClickListener(this);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        imageAni = (ProgressBar) findViewById(R.id.imageAni);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mDetailHeader = (RelativeLayout) findViewById(R.id.mDetailHeader);
        mHeaderDivider = findViewById(R.id.mHeaderDivider);
        mDetailLeftBack = (ImageView) findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailRightMore = (TextView) findViewById(R.id.mDetailRightMore);
        mDetailRightMore.setOnClickListener(this);
        mDetailComment = findViewById(R.id.mDetailComment);
        mDetailCommentPic = (ImageView) findViewById(R.id.mDetailCommentPic);
        mDetailFavorite = (ImageView) findViewById(R.id.mDetailFavorite);
        mDetailFavorite.setOnClickListener(this);
        if (!SharedPreManager.mInstance(this).getUserCenterIsShow()) {
            mDetailFavorite.setVisibility(View.GONE);
        }
        careful_Text = (TextView) findViewById(R.id.careful_Text);
        careful_Image = (ImageView) findViewById(R.id.careful_Image);
        mDetailComment.setOnClickListener(this);
        mDetailShare = (ImageView) findViewById(R.id.mDetailShare);
        mDetailShare.setOnClickListener(this);
        mBottomLine = findViewById(R.id.mBottomLine);
        mDetailAddComment = (TextView) findViewById(R.id.mDetailAddComment);
        mDetailAddComment.setOnClickListener(this);
        mDetailCommentNum = (TextView) findViewById(R.id.mDetailCommentNum);
        mDetailBottomBanner = findViewById(R.id.mDetailBottomBanner);
        mImageWallWrapper = findViewById(R.id.mImageWallWrapper);
        mImageWallVPager = (ViewPager) findViewById(R.id.mImageWallVPager);
        mImageWallDesc = (TextView) findViewById(R.id.mImageWallDesc);
        mNewsDetailViewPager = (ViewPager) findViewById(R.id.mNewsDetailViewPager);
        //初始化新闻评论DAO
        newsDetailCommentDao = new NewsDetailCommentDao(this);
        setTheme();
    }

    private void setTheme() {
        TextUtil.setLayoutBgResource(this, mDetailLeftBack, R.drawable.bg_left_back_selector);
        TextUtil.setLayoutBgResource(this, mDetailRightMore, R.drawable.bg_left_back_selector);
        TextUtil.setLayoutBgResource(this, mDetailHeader, R.color.color6);
        TextUtil.setLayoutBgResource(this, mHeaderDivider, R.color.color5);
        TextUtil.setLayoutBgResource(this, mNewsDetailLoaddingWrapper, R.color.color6);
        TextUtil.setLayoutBgResource(this, bgLayout, R.color.color6);
        TextUtil.setLayoutBgResource(this, mDetailBottomBanner, R.color.color6);
        TextUtil.setLayoutBgResource(this, mBottomLine, R.color.color5);
        TextUtil.setLayoutBgResource(this, mDetailAddComment, R.drawable.user_add_comment);
        TextUtil.setTextColor(this, mDetailAddComment, R.color.color3);
        TextUtil.setTextColor(this, mDetailCommentNum, R.color.color1);
        TextUtil.setLayoutBgResource(this, mDetailCommentNum, R.color.color6);
        ImageUtil.setAlphaProgressBar(imageAni);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.onPageStart(CommonConstant.LOG_PAGE_DETAILPAGE);
        lastTime = System.currentTimeMillis();
        if (mRefreshReceiver == null) {
            mRefreshReceiver = new RefreshPageBroReceiver();
            IntentFilter filter = new IntentFilter(ACTION_REFRESH_COMMENT);
//            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            registerReceiver(mRefreshReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        LogUtil.onPageEnd(CommonConstant.LOG_PAGE_DETAILPAGE);
        nowTime = System.currentTimeMillis();
        if (mDetailFgt != null) {
            String percent = mDetailFgt.getPercent();
            if (!TextUtil.isEmptyString(percent)) {
                //上报日志
                LogUtil.userReadLog(mNewsFeed, this, lastTime, nowTime, percent);
            }
        }
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
        if (NewsFeedFgt.VALUE_NEWS_NOTIFICATION.equals(mSource)) {
//            Intent main = new Intent(this, MainAty.class);
            Intent main = new Intent();
            main.setClassName("com.news.yazhidao", "com.news.yazhidao.pages.MainActivity");
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(main);
        }

    }

    @Override
    protected void onDestroy() {
        if (mRefreshReceiver != null) {
            unregisterReceiver(mRefreshReceiver);
            mRefreshReceiver = null;
        }
//        if (SharedPreManager.mInstance(this).getBoolean("showflag", "isKeepScreenOn")) {
//            /**梁帅：清除屏幕常亮的这个设置，从而允许屏幕熄灭*/
//            getWindow().clearFlags(WindowManager.LayoutParams.
// );
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
        /** 判断是否收藏 */
        isFavorite = SharedPreManager.mInstance(this).myFavoriteisSame(mNid);
        if (result.getColflag() == 1) {
            if (!isFavorite) {
                SharedPreManager.mInstance(this).myFavoriteSaveList(mNewsFeed);
            }
            isFavorite = true;
            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
        } else {
            if (isFavorite) {
                SharedPreManager.mInstance(this).myFavoritRemoveItem(mNid);
            }
            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
            isFavorite = false;
        }
        mNewsDetailViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    isCommentPage = true;
                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                    mDetailCommentNum.setVisibility(View.GONE);
                } else {
                    isCommentPage = false;
                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment);
                    mDetailCommentPic.setImageResource(mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                    mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
                }
            }
        });
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    mDetailFgt = new NewsDetailFgt();
                    Bundle args = new Bundle();
                    args.putSerializable(NewsDetailFgt.KEY_DETAIL_RESULT, result);
                    args.putString(NewsDetailFgt.KEY_NEWS_DOCID, result.getDocid());
                    args.putString(NewsDetailFgt.KEY_NEWS_ID, mNid);
                    args.putString(NewsDetailFgt.KEY_NEWS_TITLE, mNewsFeed.getTitle());
                    mDetailFgt.setArguments(args);
                    return mDetailFgt;
                } else {
                    mCommentFgt = new NewsCommentFgt();
                    Bundle args = new Bundle();
                    args.putSerializable(NewsCommentFgt.KEY_NEWS_FEED, mNewsFeed);
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
    }

    @Override
    protected void loadData() {
        if (NetUtil.checkNetWork(this)) {
            isRefresh = true;
            mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
            mNewsDetailViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
            bgLayout.setVisibility(View.VISIBLE);
            mNewsFeed = (NewsFeed) getIntent().getSerializableExtra(NewsFeedFgt.KEY_NEWS_FEED);
            if (mNewsFeed != null) {
                mNid = mNewsFeed.getNid() + "";
            } else {
                mNid = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_ID);
                isForeground = true;
            }
            User user = SharedPreManager.mInstance(this).getUser(NewsDetailAty2.this);
            if (user != null) {
                mUserId = user.getMuid() + "";
            }
//        isFavorite = SharedPreManager.mInstance(this).myFavoriteisSame(mNid);
//        if (isFavorite) {
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
//        } else {
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
//        }
//            mNid = "14369644";
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            NewsDetailRequest<NewsDetail> feedRequest = new NewsDetailRequest<NewsDetail>(Request.Method.GET, new TypeToken<NewsDetail>() {
            }.getType(), HttpConstant.URL_FETCH_CONTENT + "nid=" + mNid + "&uid=" + mUserId, new Response.Listener<NewsDetail>() {

                @Override
                public void onResponse(NewsDetail result) {
                    isRefresh = false;
                    mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
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
                            mDetailCommentPic.setImageResource(mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                        }
                        LogUtil.userClickLog(mNewsFeed, NewsDetailAty2.this, mSource);
                    } else {
                        ToastUtil.toastShort("此新闻暂时无法查看!");
                        NewsDetailAty2.this.finish();
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
        mNewsFeed.setImageUrl(mImageUrl);
        mNewsFeed.setNid(result.getNid());
        return mNewsFeed;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isCommentPage) {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0, true);
                mDetailCommentPic.setImageResource(mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void shareDismiss() {
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mivShareBg.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.mDetailLeftBack) {
            if (isCommentPage) {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0);
                mDetailCommentPic.setImageResource(mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
                if (!TextUtil.isEmptyString(mNid)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("nid", Long.valueOf(mNid));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.userActionLog(NewsDetailAty2.this, CommonConstant.LOG_ATYPE_COMMENTCLICK, CommonConstant.LOG_PAGE_COMMENTPAGE, CommonConstant.LOG_PAGE_DETAILPAGE, jsonObject, false);
                }
            } else {
                onBackPressed();
            }
        } else if (getId == R.id.mDetailRightMore) {
            if (mNewsFeed != null) {
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                mSharePopupWindow = new SharePopupWindow(this, this);
                mSharePopupWindow.setTitleAndNid(mNewsFeed.getTitle(), mNewsFeed.getNid(), mNewsFeed.getDescr());
                mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        } else if (getId == R.id.mDetailAddComment) {
            User user = SharedPreManager.mInstance(NewsDetailAty2.this).getUser(NewsDetailAty2.this);
            if (user != null && user.isVisitor()) {
                AuthorizedUserUtil.sendUserLoginBroadcast(NewsDetailAty2.this);
                return;
            }
            if (mNewsFeed != null) {
                mCommentDialog = new UserCommentDialog();
                mCommentDialog.setDocid(mNewsFeed.getDocid());
                mCommentDialog.show(NewsDetailAty2.this.getSupportFragmentManager(), "UserCommentDialog");
            }
        } else if (getId == R.id.mDetailComment) {
            if (!isCommentPage) {
                isCommentPage = true;
                mNewsDetailViewPager.setCurrentItem(1);
                mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                mDetailCommentNum.setVisibility(View.GONE);
                if (!TextUtil.isEmptyString(mNid)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("nid", Long.valueOf(mNid));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.userActionLog(NewsDetailAty2.this, CommonConstant.LOG_ATYPE_COMMENTCLICK, CommonConstant.LOG_PAGE_DETAILPAGE, CommonConstant.LOG_PAGE_COMMENTPAGE, jsonObject, false);
                }
            } else {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0);
                mDetailCommentPic.setImageResource(mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
                if (!TextUtil.isEmptyString(mNid)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("nid", Long.valueOf(mNid));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.userActionLog(NewsDetailAty2.this, CommonConstant.LOG_ATYPE_COMMENTCLICK, CommonConstant.LOG_PAGE_COMMENTPAGE, CommonConstant.LOG_PAGE_DETAILPAGE, jsonObject, false);
                }
            }
        } else if (getId == R.id.mDetailShare) {
            if (mNewsFeed != null) {
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                mSharePopupWindow = new SharePopupWindow(this, this);
                mSharePopupWindow.setTitleAndNid(mNewsFeed.getTitle(), mNewsFeed.getNid(), mNewsFeed.getDescr());
                mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        } else if (getId == R.id.mNewsDetailLoaddingWrapper) {
            if (!isRefresh) {
                loadData();
            }
        } else if (getId == R.id.mDetailFavorite) {
            User user = SharedPreManager.mInstance(this).getUser(this);
            if (user != null && user.isVisitor()) {
                AuthorizedUserUtil.sendUserLoginBroadcast(NewsDetailAty2.this);
                return;
            }
            loadOperate();
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
        if (!NetUtil.checkNetWork(NewsDetailAty2.this)) {
            ToastUtil.toastShort("无法连接到网络，请稍后再试");
            return;
        }
        JSONObject json = new JSONObject();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        DetailOperateRequest detailOperateRequest = new DetailOperateRequest((isFavorite ? Request.Method.DELETE : Request.Method.POST),
                HttpConstant.URL_ADDORDELETE_FAVORITE + "nid=" + mNid + "&uid=" + mUserId,
                json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                careful_Image.setImageResource(R.drawable.hook_image);
                if (isFavorite) {
                    isFavorite = false;
                    careful_Text.setText("收藏已取消");
                    SharedPreManager.mInstance(NewsDetailAty2.this).myFavoritRemoveItem(mNid);
                    mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
                } else {
                    isFavorite = true;
                    careful_Text.setText("收藏成功");
                    Logger.e("aaa", "收藏成功数据：" + mNewsFeed.toString());
                    SharedPreManager.mInstance(NewsDetailAty2.this).myFavoriteSaveList(mNewsFeed);
                    mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
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
