package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsDetailCommentDao;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.utils.AuthorizedUserUtil;
import com.news.yazhidao.utils.LogUtil;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.UserCommentDialog;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 新闻展示详情页
 */
public class NewsDetailAty2 extends BaseActivity implements View.OnClickListener
//        , SharePopupWindow.ShareDismiss
{

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
    private View mDetailComment, mNewsDetailLoaddingWrapper;
    private ImageView mDetailShare;
    private ImageView mDetailLeftBack;
    //            ,mDetailRightMore;
    private ImageView mNewsLoadingImg;
    private View mDetailView;
    //    private SharePopupWindow mSharePopupWindow;
    private RelativeLayout mDetailHeader, bgLayout;

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
    private UserCommentDialog mCommentDialog;
    private NewsFeed mNewsFeed;
    private String mSource, mImageUrl;
    private String mNid;
    private NewsDetailCommentDao newsDetailCommentDao;
    private boolean isRefresh = false;
    private LinearLayout careforLayout;
    //    boolean isFavorite;
    public static final int REQUEST_CODE = 1030;
    private NewsFeed mUsedNewsFeed;
    long lastTime, nowTime;
    private int mCommentNum;
    private NewsDetailFgt mDetailFgt;
    private NewsCommentFgt mCommentFgt;
    private boolean isUserComment;

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
        mUsedNewsFeed = (NewsFeed) getIntent().getSerializableExtra(NewsCommentFgt.KEY_NEWS_FEED);
        mSource = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_SOURCE);
        mImageUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_IMAGE);
//        mSwipeBackLayout = getSwipeBackLayout();
//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        careforLayout = (LinearLayout) findViewById(R.id.careforLayout);
        mDetailView = findViewById(R.id.mDetailWrapper);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setOnClickListener(this);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mDetailHeader = (RelativeLayout) findViewById(R.id.mDetailHeader);
        TextUtil.setLayoutBgColor(this, mDetailHeader, R.color.white);
        mDetailLeftBack = (ImageView) findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
//        mDetailRightMore = (TextView) findViewById(R.id.mDetailRightMore);
//        mDetailRightMore.setOnClickListener(this);
        mDetailComment = findViewById(R.id.mDetailComment);
        mDetailCommentPic = (ImageView) findViewById(R.id.mDetailCommentPic);
        mDetailFavorite = (ImageView) findViewById(R.id.mDetailFavorite);
        mDetailFavorite.setOnClickListener(this);
        carefor_Text = (TextView) findViewById(R.id.carefor_Text);
        carefor_Image = (ImageView) findViewById(R.id.carefor_Image);
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

    @Override
    protected void onResume() {
        super.onResume();
        lastTime = System.currentTimeMillis();
        if (mRefreshReceiver == null) {
            mRefreshReceiver = new RefreshPageBroReceiver();
            IntentFilter filter = new IntentFilter(ACTION_REFRESH_COMMENT);
//            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            registerReceiver(mRefreshReceiver, filter);
        }
    }

    @Override
    public void finish() {
        if (mNewsFeed != null && isUserComment) {
            Intent intent = new Intent();
            intent.putExtra(CommonConstant.NEWS_COMMENT_NUM, mCommentNum);
            intent.putExtra(CommonConstant.NEWS_ID, mNewsFeed.getNid());
            intent.setAction(CommonConstant.CHANGE_COMMENT_NUM_ACTION);
            sendBroadcast(intent);
        }
        super.finish();
    }

    @Override
    protected void onPause() {
        nowTime = System.currentTimeMillis();
        if (mDetailFgt != null) {
            String percent = mDetailFgt.getPercent();
            if (!TextUtil.isEmptyString(percent)) {
                //上报日志
                LogUtil.upLoadLog(mUsedNewsFeed, this, nowTime - lastTime, percent, this.getString(R.string.version_name), isUserComment);
            }
        }
        super.onPause();
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
//                    detailFgt.setShowCareforLayout(mShowCareforLayout);
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
            mNewsLoadingImg.setVisibility(View.GONE);
            mNewsDetailViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
            bgLayout.setVisibility(View.VISIBLE);
            mNewsFeed = (NewsFeed) getIntent().getSerializableExtra(NewsFeedFgt.KEY_NEWS_FEED);
            if (mNewsFeed != null) {
                mNid = mNewsFeed.getNid() + "";
            } else {
                mNid = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_ID);
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
                    } else {
                        ToastUtil.toastShort("此新闻暂时无法查看!");
                        NewsDetailAty2.this.finish();
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
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.mDetailLeftBack) {
            onBackPressed();
        }
//            case R.id.mDetailRightMore://更多的点击
//                if (mNewsFeed != null) {
//                    mivShareBg.startAnimation(mAlphaAnimationIn);
//                    mivShareBg.setVisibility(View.VISIBLE);
//                    mSharePopupWindow = new SharePopupWindow(this, this);
//                    String remark = "1";
//                    String url = "http://deeporiginalx.com/news.html?type=0" + "&url=" + TextUtil.getBase64(mNewsFeed.getUrl()) + "&interface";
//                    mSharePopupWindow.setTitleAndUrl(mNewsFeed, remark);
//                    mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//
//                }
        else if (getId == R.id.mDetailAddComment) {
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
//                    mSharePopupWindow = new SharePopupWindow(this, this);
//                    String remark = "1";
//                    String url = "http://deeporiginalx.com/news.html?type=0" + "&url=" + TextUtil.getBase64(mNewsFeed.getUrl()) + "&interface";
//                    mSharePopupWindow.setTitleAndUrl(mNewsFeed, remark);
//                    mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

            }
        } else if (getId == R.id.mNewsLoadingImg) {
            if (!isRefresh) {
                loadData();
            }
        } else if (getId == R.id.mDetailFavorite) {
//            User user = SharedPreManager.mInstance(this).getUser(NewsDetailAty2.this);
//                if (user == null) {
//                    Intent loginAty = new Intent(NewsDetailAty2.this, LoginAty.class);
//                    startActivityForResult(loginAty, REQUEST_CODE);
//                } else {
//                    Logger.e("bbb","收藏触发的点击事件！！！！！");
//                    CareForAnimation(false);
//                }
        }
    }
}
