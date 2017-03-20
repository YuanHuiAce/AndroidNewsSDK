package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.LogUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.NewsDetailHeaderView2;

import java.util.ArrayList;
import java.util.HashMap;

//import com.news.yazhidao.widget.SharePopupWindow;
//import com.news.yazhidao.widget.UserCommentDialog;

/**
 * Created by fengjigang on 15/9/6.
 * 新闻展示详情页
 */
public class NewsDetailVideoAty extends BaseActivity implements View.OnClickListener
//        , SharePopupWindow.ShareDismiss
{

    public static final String KEY_IMAGE_WALL_INFO = "key_image_wall_info";
    public static final String ACTION_REFRESH_COMMENT = "com.news.baijia.ACTION_REFRESH_COMMENT";

    private int mScreenWidth, mScreenHeight;
    //滑动关闭当前activity布局
//    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId = "";
    private String mPlatformType = "";
    private String uuid;
    private ImageView mivShareBg;
    private ArrayList<ArrayList> mNewsContentDataList;
    private ArrayList<View> mImageViews;
    private ArrayList<HashMap<String, String>> mImages;
    private NewsDetailHeaderView2 mDetailHeaderView;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    /**
     * 返回上一级,全文评论,分享
     */
    private View mDetailComment, mDetailHeader, mNewsDetailLoaddingWrapper;
    private ImageView mDetailShare;
    private TextView mDetailLeftBack
//            ,mDetailRightMore
            ;
    private ImageView mNewsLoadingImg;
    private AnimationDrawable mAniNewsLoading;
    private View mDetailView;
    //    private SharePopupWindow mSharePopupWindow;
    //    private ProgressBar mNewsDetailProgress;
    private RelativeLayout bgLayout;

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
    private RefreshPageBroReceiber mRefreshReceiber;
    //    private UserCommentDialog mCommentDialog;
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

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiber extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.e("jigang", "comment fgt refresh br");

//            NewsDetailComment newsDetailComment = (NewsDetailComment) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
//            newsDetailComment.setNewsFeed(mNewsFeed);
//            newsDetailComment.setOriginal(mNewsFeed.getTitle());
//            newsDetailCommentDao.add(newsDetailComment);

//            NewsDetailCommentItem newsDetailComment = (NewsDetailCommentItem) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
//            newsDetailComment.setNewsFeed(mNewsFeed);
//            newsDetailComment.setOriginal(mNewsFeed.getTitle());
//            newsDetailCommentDao.add(newsDetailComment);


//            } else {

            Logger.e("jigang", "comment fgt refresh br");
            int number = 0;
            try {
                number = Integer.valueOf(mDetailCommentNum.getText().toString());
            } catch (Exception e) {

            }
            mDetailCommentNum.setText(number + 1 + "");
            mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);

//            }
        }
    }


    @Override
    protected boolean translucentStatus() {
        return false;
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_video_detail_layout);
        if (SharedPreManager.mInstance(this).getBoolean("showflag", "isKeepScreenOn")) {
            /** 梁帅：保持让屏幕常亮*/
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }


//        Log.e("aaa", "获取Sp数据：" + SharedPreManager.mInstance(this).get("flag", "text1"));
        mScreenWidth = DeviceInfoUtil.getScreenWidth(this);
        mScreenHeight = DeviceInfoUtil.getScreenHeight(this);
        mNewsContentDataList = new ArrayList<>();
        mImageViews = new ArrayList<>();
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
        vPlayPlayer=new VPlayPlayer(this);
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
        mDetailHeaderView = new NewsDetailHeaderView2(this);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setOnClickListener(this);
//        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
//        mNewsDetailProgress = (ProgressBar) findViewById(R.id.mNewsDetailProgress);
        mSmallLayout = (RelativeLayout) findViewById(R.id.detai_small_layout);
        mSmallScreen = (FrameLayout) findViewById(R.id.detail_small_screen);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mDetailHeader = (RelativeLayout) findViewById(R.id.mDetailHeader);
//        TextUtil.setLayoutBgColor(this, (RelativeLayout) mDetailHeader, R.color.white);
        mDetailLeftBack = (TextView) findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailRightMore = (TextView) findViewById(R.id.mDetailRightMore);
        mDetailRightMore.setOnClickListener(this);
//        mDetailRightMore = (TextView) findViewById(R.id.mDetailRightMore);
//        mDetailRightMore.setOnClickListener(this);
//        mDetailComment = findViewById(R.id.mDetailComment);
        mDetailCommentPic = (ImageView) findViewById(R.id.mDetailCommentPic);
        mDetailFavorite = (ImageView) findViewById(R.id.mDetailFavorite);
        mDetailFavorite.setOnClickListener(this);
        carefor_Text = (TextView) findViewById(R.id.carefor_Text);
        carefor_Image = (ImageView) findViewById(R.id.carefor_Image);

//        mDetailComment.setOnClickListener(this);
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

        Logger.e("aaa", "===========================onResume====================");
        nowTime = System.currentTimeMillis();
        mDurationStart = System.currentTimeMillis();
        if (mRefreshReceiber == null) {
            mRefreshReceiber = new RefreshPageBroReceiber();
            IntentFilter filter = new IntentFilter(ACTION_REFRESH_COMMENT);
//            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            registerReceiver(mRefreshReceiber, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vPlayPlayer!=null)
            if (mSmallLayout.getVisibility()==View.VISIBLE)
            {
                mSmallLayout.setVisibility(View.GONE);
                mSmallScreen.removeAllViews();
                vPlayPlayer.stop();
                vPlayPlayer.release();

            }
        Logger.e("aaa", "===========================onPause====================");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vPlayPlayer.onDestory();
        vPlayPlayer=null;
        Logger.e("aaa", "===========================onDestroy====================");
        if (mRefreshReceiber != null) {
            unregisterReceiver(mRefreshReceiber);
            mRefreshReceiber = null;
        }
        lastTime = System.currentTimeMillis();
        //上报日志
        LogUtil.upLoadLog(mUsedNewsFeed, this, lastTime - nowTime);
        if (SharedPreManager.mInstance(this).getBoolean("showflag", "isKeepScreenOn")) {
            /**梁帅：清除屏幕常亮的这个设置，从而允许屏幕熄灭*/
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
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

                    Drawable drawable = getResources().getDrawable(R.drawable.btn_left_back);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mDetailLeftBack.setCompoundDrawables(drawable, null, null, null);
//                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
//                    mDetailCommentNum.setVisibility(View.GONE);
                    Drawable share = getResources().getDrawable(R.drawable.btn_detail_right_more);
                    share.setBounds(0, 0, share.getMinimumWidth(), share.getMinimumHeight());
                    mDetailRightMore.setCompoundDrawables(null, null, share, null);
                } else {
                    isCommentPage = false;
                    Drawable drawable1 = getResources().getDrawable(R.drawable.detial_video_back);
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                    mDetailLeftBack.setCompoundDrawables(drawable1, null, null, null);
//                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment);
//                    mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
//                    mDetailCommentNum.setVisibility(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? View.GONE : View.VISIBLE);
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
//                    detailFgt.setShowCareforLayout(mShowCareforLayout);
                    detailFgt.setArguments(args);
                    return detailFgt;
                } else {
                    NewsCommentVideoFgt commentFgt = new NewsCommentVideoFgt();
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

    long iii;

    @Override
    protected void loadData() {
//        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
//        mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
//        mAniNewsLoading.start();
//        try {
//            Logger.e("aaa", "刚刚进入============" + SharedPreManager.mInstance(this).myFavoriteGetList().toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        mNewsLoadingImg.setVisibility(View.GONE);
        mNewsDetailViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        bgLayout.setVisibility(View.VISIBLE);
        mNewsFeed = (NewsFeed) getIntent().getSerializableExtra(NewsFeedFgt.KEY_NEWS_FEED);
        if (mNewsFeed != null) {
            mUrl = mNewsFeed.getNid() + "";
        } else {
            mUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_ID);
        }
//        mUrl = "9076124";
//        mUrl = "9372991";
        User user = SharedPreManager.mInstance(this).getUser(NewsDetailVideoAty.this);
        if (user != null) {
            mUserId = user.getMuid() + "";
            mPlatformType = user.getPlatformType();
        }
//        else
//        {
//            UserManager.registerVisitor(this, new UserManager.RegisterVisitorListener() {
//                @Override
//                public void registeSuccess() {
//                    User user = SharedPreManager.getUser(NewsDetailVideoAty.this);
//                    mUserId = user.getMuid() + "";
//                    mPlatformType = user.getPlatformType();
//                }
//            });
//        }
        uuid = DeviceInfoUtil.getUUID();

//        isFavorite = SharedPreManager.mInstance(this).myFavoriteisSame(mUrl);
//        if (isFavorite) {
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
//        } else {
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
//        }

        Logger.e("jigang", "detail url=" + HttpConstant.URL_FETCH_CONTENT + "nid=" + mUrl);
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        NewsDetailRequest<NewsDetail> feedRequest = new NewsDetailRequest<NewsDetail>(Request.Method.GET, new TypeToken<NewsDetail>() {
        }.getType(), HttpConstant.URL_VIDEO_CONTENT + "nid=" + mUrl, new Response.Listener<NewsDetail>() {

            @Override
            public void onResponse(NewsDetail result) {
                iii = System.currentTimeMillis();
                mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
                Logger.e("jigang", "network success~~" + result);
                if (result != null) {
                    mNewsFeed = convert2NewsFeed(result);
                    displayDetailAndComment(result);
//                    mDetailHeaderView.updateView(result);
                    if (result.getComment() != 0) {
                        mDetailCommentNum.setVisibility(View.VISIBLE);
                        mDetailCommentNum.setText(TextUtil.getCommentNum(result.getComment() + ""));
                        mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                    }
                } else {
                    ToastUtil.toastShort("此新闻暂时无法查看!");
                    NewsDetailVideoAty.this.finish();
                }
                Log.i("tag", System.currentTimeMillis() - iii + "tag");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("jigang", "network fail");
                mNewsLoadingImg.setVisibility(View.VISIBLE);
                bgLayout.setVisibility(View.GONE);
            }
        });

//        NewsDetailRequest<RelatedEntity> related = new NewsDetailRequest<RelatedEntity>(Request.Method.GET,
//                new TypeToken<RelatedEntity>() {
//                }.getType(),
//                HttpConstant.URL_NEWS_RELATED + "url=" + TextUtil.getBase64(newsId),
//                new Response.Listener<RelatedEntity>() {
//                    @Override
//                    public void onResponse(RelatedEntity response) {
//                        Logger.e("jigang", "network success RelatedEntity~~" + response);
//                        ArrayList<RelatedItemEntity> list = response.getSearchItems();
//                        Logger.e("aaa","time:================比较前=================");
//                        for(int i=0;i<list.size();i++){
//                            Logger.e("aaa","time:==="+list.get(i).getUpdateTime());
//                        }
//                        Collections.sort(list);
//                        Logger.e("aaa","time:================比较====后=================");
//                        for(int i=0;i<list.size();i++){
//                            Logger.e("aaa","time:==="+list.get(i).getUpdateTime());
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Logger.e("jigang", "network error~~");
//                    }
//                });

        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
//        requestQueue.add(related);
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
//            if (mCommentDialog != null && mCommentDialog.isVisible()) {
//                mCommentDialog.dismiss();
//                return true;
//            }
            if (vPlayPlayer.onKeyDown(keyCode,event))
                return true;
            if (isCommentPage) {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0, true);
                mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? View.GONE : View.VISIBLE);
                return true;
            }
        }
        else if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN||keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            if (vPlayPlayer!=null&&vPlayPlayer.handleVolumeKey(keyCode))
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int getId = v.getId();
        if (getId == R.id.mDetailLeftBack) {
//        switch (v.getId()) {
//            case R.id.mDetailLeftBack:
            onBackPressed();
//                break;
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
//                MobclickAgent.onEvent(this,"yazhidao_user_detail_onclick_more");
//                break;
        else if (getId == R.id.mDetailAddComment) {

//            case R.id.mDetailAddComment:
            if (mNewsFeed != null) {
//                    mCommentDialog = new UserCommentDialog();
//                    mCommentDialog.setDocid(mNewsFeed.getDocid());
//                    mCommentDialog.show(NewsDetailAty2.this.getSupportFragmentManager(), "UserCommentDialog");
            }
//                MobclickAgent.onEvent(this,"yazhidao_user_detail_add_comment");
//                break;
        } else if (getId == R.id.mDetailComment) {
//            case R.id.mDetailComment:
            Logger.e("aaa", "onClick: mDetailComment ");
            if (!isCommentPage) {
                isCommentPage = true;
                mNewsDetailViewPager.setCurrentItem(1);
                mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                mDetailCommentNum.setVisibility(View.GONE);
            } else {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0);
                mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? View.GONE : View.VISIBLE);
            }
//                break;
        } else if (getId == R.id.mDetailShare) {

//            case R.id.mDetailShare:
            if (mNewsFeed != null) {
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
//                    mSharePopupWindow = new SharePopupWindow(this, this);
//                    String remark = "1";
//                    String url = "http://deeporiginalx.com/news.html?type=0" + "&url=" + TextUtil.getBase64(mNewsFeed.getUrl()) + "&interface";
//                    mSharePopupWindow.setTitleAndUrl(mNewsFeed, remark);
//                    mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

            }
//                MobclickAgent.onEvent(this,"yazhidao_user_detail_share");
//                break;
        } else if (getId == R.id.mNewsLoadingImg) {
//            case R.id.mNewsLoadingImg:
            loadData();
//                break;
        } else if (getId == R.id.mDetailFavorite) {
//            case R.id.mDetailFavorite:
            User user = SharedPreManager.mInstance(this).getUser(NewsDetailVideoAty.this);
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
    //视频有关


}