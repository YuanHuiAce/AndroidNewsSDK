package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsDetailCommentDao;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.utils.AuthorizedUserUtil;
import com.news.yazhidao.utils.LogUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.UserCommentDialog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fengjigang on 15/9/6.
 * 视频详情页
 */
public class NewsDetailVideoAty extends BaseActivity implements View.OnClickListener {
    //    public static final String KEY_IMAGE_WALL_INFO = "key_image_wall_info";
    public static final String ACTION_REFRESH_COMMENT = "com.news.baijia.ACTION_REFRESH_COMMENT";

    //    private int mScreenWidth, mScreenHeight;
//    //滑动关闭当前activity布局
////    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId = "";
    private ImageView mivShareBg;
    private ArrayList<ArrayList> mNewsContentDataList;
    private ArrayList<View> mImageViews;
    private ArrayList<HashMap<String, String>> mImages;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    /**
     * 返回上一级,全文评论,分享
     */
    private View mDetailComment, mNewsDetailLoaddingWrapper;
    private ImageView mDetailShare;
    private TextView mDetailLeftBack;
    //            ,mDetailRightMore
    private ImageView mNewsLoadingImg;
    private View mDetailView;
    //    private SharePopupWindow mSharePopupWindow;
    private RelativeLayout mDetailHeader, bgLayout;

    private boolean isDisplay = true;
    private int defaultH;//图片新闻文本描述的默认高度
    private long mDurationStart;//统计用户读此条新闻时话费的时间
    private boolean isReadOver;//是否看完了全文,此处指的是翻到最下面
    public boolean isCommentPage;//是否是评论页
    private View mDetailAddComment;
    public TextView mDetailCommentNum;
    private View mImageWallWrapper;
    private ViewPager mImageWallVPager;
    private TextView mImageWallDesc, carefor_Text;
    private View mDetailBottomBanner;
    public ImageView mDetailCommentPic, mDetailFavorite, carefor_Image;
    public ViewPager mNewsDetailViewPager;
    private RefreshPageBroReceiver mRefreshReceiver;
    private NewsFeed mNewsFeed;
    private String mSource, mImageUrl;
    private String mUrl;
    private NewsDetailCommentDao newsDetailCommentDao;

    private LinearLayout careforLayout;
    //    boolean isFavorite;
    public static final int REQUEST_CODE = 1030;
    private NewsFeed mUsedNewsFeed;
    private RelativeLayout mSmallLayout;
    private FrameLayout mSmallScreen;
    public VPlayPlayer vPlayPlayer;
    private Handler mHandler;
    private TextView mDetailRightMore;
    private UserCommentDialog mCommentDialog;
    private int mCommentNum;

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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
    }

    @Override
    protected void initializeViews() {
        mUsedNewsFeed = (NewsFeed) getIntent().getSerializableExtra(NewsCommentFgt.KEY_NEWS_FEED);
        mSource = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_SOURCE);
        mImageUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_IMAGE);
        careforLayout = (LinearLayout) findViewById(R.id.careforLayout);
        mDetailView = findViewById(R.id.mDetailWrapper);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setOnClickListener(this);


        mSmallLayout = (RelativeLayout) findViewById(R.id.detai_small_layout);
        mSmallScreen = (FrameLayout) findViewById(R.id.detail_small_screen);

        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mDetailHeader = (RelativeLayout) findViewById(R.id.mDetailHeader);
        TextUtil.setLayoutBgColor(this, mDetailHeader, R.color.white);
        mDetailLeftBack = (TextView) findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailRightMore = (TextView) findViewById(R.id.mDetailRightMore);
        mDetailRightMore.setOnClickListener(this);
        mDetailCommentPic = (ImageView) findViewById(R.id.mDetailCommentPic);
        mDetailFavorite = (ImageView) findViewById(R.id.mDetailFavorite);
        mDetailFavorite.setOnClickListener(this);
        carefor_Text = (TextView) findViewById(R.id.carefor_Text);
        carefor_Image = (ImageView) findViewById(R.id.carefor_Image);
        mDetailComment = findViewById(R.id.mDetailComment);
        mDetailComment.setOnClickListener(this);
        mDetailShare = (ImageView) findViewById(R.id.mDetailShare);
        mDetailShare.setOnClickListener(this);
        mDetailAddComment = findViewById(R.id.mDetailAddComment);
        mDetailAddComment.setOnClickListener(this);
        mDetailCommentNum = (TextView) findViewById(R.id.mDetailCommentNum);
        mDetailBottomBanner = findViewById(R.id.mDetailBottomBanner);
        mImageWallWrapper = findViewById(R.id.mImageWallWrapper);
        mImageWallVPager = (ViewPager) findViewById(R.id.mImageWallVPager);
        mImageWallDesc = (TextView) findViewById(R.id.mImageWallDesc);
        mNewsDetailViewPager = (ViewPager) findViewById(R.id.mNewsDetailViewPager);

        //初始化新闻评论DAO
        newsDetailCommentDao = new NewsDetailCommentDao(this);
    }

    long lastTime, nowTime;

    @Override
    protected void onResume() {
        super.onResume();
        mDurationStart = System.currentTimeMillis();
        nowTime = System.currentTimeMillis();
        if (mRefreshReceiver == null) {
            mRefreshReceiver = new RefreshPageBroReceiver();
            IntentFilter filter = new IntentFilter(ACTION_REFRESH_COMMENT);
//            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            registerReceiver(mRefreshReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vPlayPlayer != null) {
            if (mSmallLayout.getVisibility() == View.VISIBLE) {
                mSmallLayout.setVisibility(View.GONE);
                mSmallScreen.removeAllViews();
                vPlayPlayer.stop();
                vPlayPlayer.release();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vPlayPlayer.onDestory();
        vPlayPlayer = null;
        lastTime = System.currentTimeMillis();
        if (mRefreshReceiver != null) {
            unregisterReceiver(mRefreshReceiver);
            mRefreshReceiver = null;
        }
        //上报日志
        LogUtil.upLoadLog(mUsedNewsFeed, this, lastTime - nowTime);
//        if (SharedPreManager.mInstance(this).getBoolean("showflag", "isKeepScreenOn")) {
//            /**梁帅：清除屏幕常亮的这个设置，从而允许屏幕熄灭*/
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
    }

    FragmentPagerAdapter pagerAdapter;

    /**
     * 显示新闻详情和评论
     *
     * @param result
     */
    private void displayDetailAndComment(final NewsDetail result) {
        mNewsDetailViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Message msg = mHandler.obtainMessage();
                msg.what = 2;
                msg.obj = position;
                mHandler.sendMessage(msg);
                if (position == 1) {
                    isCommentPage = true;
                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                    mDetailCommentNum.setVisibility(View.GONE);
                    Drawable drawable = getResources().getDrawable(R.drawable.btn_left_back);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mDetailLeftBack.setCompoundDrawables(drawable, null, null, null);
                    Drawable share = getResources().getDrawable(R.drawable.btn_detail_right_more);
                    share.setBounds(0, 0, share.getMinimumWidth(), share.getMinimumHeight());
                    mDetailRightMore.setCompoundDrawables(null, null, share, null);
                } else {
                    isCommentPage = false;
                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment);
                    mDetailCommentPic.setImageResource(mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                    mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
                    Drawable drawable1 = getResources().getDrawable(R.drawable.detial_video_back);
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                    mDetailLeftBack.setCompoundDrawables(drawable1, null, null, null);
                    Drawable share = getResources().getDrawable(R.drawable.detai_video_share);
                    share.setBounds(0, 0, share.getMinimumWidth(), share.getMinimumHeight());
                    mDetailRightMore.setCompoundDrawables(null, null, share, null);
                }
            }
        });
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    NewsDetailVideoFgt detailFgt = new NewsDetailVideoFgt();
                    Bundle args = new Bundle();
                    args.putSerializable(NewsDetailFgt.KEY_DETAIL_RESULT, result);
                    args.putString(NewsDetailFgt.KEY_NEWS_DOCID, result.getDocid());
                    args.putString(NewsDetailFgt.KEY_NEWS_ID, mUrl);
                    args.putString(NewsDetailFgt.KEY_NEWS_TITLE, mNewsFeed.getTitle());
                    detailFgt.setArguments(args);
                    return detailFgt;
                } else {
//                    NewsCommentVideoFgt commentFgt = new NewsCommentVideoFgt();
//                    Bundle args = new Bundle();
//                    args.putSerializable(NewsCommentFgt.KEY_NEWS_FEED, mNewsFeed);
//                    commentFgt.setArguments(args);
                    NewsCommentFgt commentFgt = new NewsCommentFgt();
                    Bundle args = new Bundle();
                    args.putSerializable(NewsCommentFgt.KEY_NEWS_FEED, mNewsFeed);
                    commentFgt.setArguments(args);
                    return commentFgt;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        mNewsDetailViewPager.setAdapter(pagerAdapter);
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
                mUrl = mNewsFeed.getNid() + "";
            } else {
                mUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_ID);
            }
            User user = SharedPreManager.mInstance(this).getUser(NewsDetailVideoAty.this);
            if (user != null) {
                mUserId = user.getMuid() + "";
            }
            Logger.e("jigang", "detail url=" + HttpConstant.URL_FETCH_CONTENT + "nid=" + mUrl);
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            NewsDetailRequest<NewsDetail> feedRequest = new NewsDetailRequest<NewsDetail>(Request.Method.GET, new TypeToken<NewsDetail>() {
            }.getType(), HttpConstant.URL_VIDEO_CONTENT + "nid=" + mUrl + "&uid=" + mUserId, new Response.Listener<NewsDetail>() {

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
        }
    }

    private NewsFeed convert2NewsFeed(NewsDetail result) {
        NewsFeed mNewsFeed = new NewsFeed();
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
                mDetailCommentPic.setImageResource(mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
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
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.mDetailLeftBack) {
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
                mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                mDetailCommentNum.setVisibility(View.GONE);
            } else {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0);
                mDetailCommentPic.setImageResource(mCommentNum == 0 ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(mCommentNum == 0 ? View.GONE : View.VISIBLE);
            }
        } else if (getId == R.id.mDetailShare) {
            if (mNewsFeed != null) {
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
            }
        } else if (getId == R.id.mNewsLoadingImg) {
            if (!isRefresh) {
                loadData();
            }
        } else if (getId == R.id.mDetailFavorite) {
//            case R.id.mDetailFavorite:
//            User user = SharedPreManager.mInstance(this).getUser(NewsDetailVideoAty.this);
//                if (user == null) {
//                    Intent loginAty = new Intent(NewsDetailAty2.this, LoginAty.class);
//                    startActivityForResult(loginAty, REQUEST_CODE);
//                } else {
//                    Logger.e("bbb","收藏触发的点击事件！！！！！");
//                    CareForAnimation(false);
//
//                }
//                MobclickAgent.onEvent(this,"yazhidao_user_detail_favorite");
//                break;
        }
    }
}
