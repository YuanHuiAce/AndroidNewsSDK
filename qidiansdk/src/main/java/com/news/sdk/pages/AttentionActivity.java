package com.news.sdk.pages;


import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.sdk.R;
import com.news.sdk.adapter.NewsFeedAdapter;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.AttentionListEntity;
import com.news.sdk.entity.AttentionPbsEntity;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.DetailOperateRequest;
import com.news.sdk.net.volley.NewsDetailRequest;
import com.news.sdk.utils.AuthorizedUserUtil;
import com.news.sdk.utils.DateUtil;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.SharePopupWindow;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AttentionActivity extends BaseActivity implements View.OnClickListener, SharePopupWindow.ShareDismiss {


    private PullToRefreshListView mAttentionList;
    private RequestManager mRequestManager;
    private TextView mAttentionLeftBack,
            mAttentionRightMore,
            mAttention_btn,
            tv_attention_title,
            mAttentionCenterTitle;
    private String mPName;
    private int conpubflag;
    private ImageView iv_attention_headImage;
    private RelativeLayout bgLayout, mAttentionTitleLayout;
    private User mUser;
    private NewsFeedAdapter mAdapter;
    ArrayList<NewsFeed> mNewsFeeds = new ArrayList<>();
    private int mPageIndex = 1;
    private boolean ismAttention;
    private int mIndex;
    /**
     * 添加更多的属性
     */
    private ImageView mivShareBg;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    private SharePopupWindow mSharePopupWindow;
    private View mAttentionRelativeLayout;
    private int thisVisibleItemCount, thisTotalItemCount;
    private int maxMargin;
    private boolean isBottom;
    private TextView attention_headViewItem_Content,
            footView_tv,
            tv_attention_historyTitle,
            tv_attention_noData;
    private ImageView img_attention_line;
    private LinearLayout linear_attention_descrLayout;
    private ProgressBar footView_progressbar;
    private LinearLayout footerView;
    private ListView lv;

    @Override
    protected void initializeViews() {
        mPName = getIntent().getStringExtra(CommonConstant.KEY_ATTENTION_TITLE);
        conpubflag = getIntent().getIntExtra(CommonConstant.KEY_ATTENTION_CONPUBFLAG, 0);
        mIndex = getIntent().getIntExtra(CommonConstant.KEY_ATTENTION_INDEX, 0);
        ismAttention = (conpubflag > 0);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mAttentionLeftBack = (TextView) findViewById(R.id.mAttentionLeftBack);
        mAttentionRightMore = (TextView) findViewById(R.id.mAttentionRightMore);
        mAttention_btn = (TextView) findViewById(R.id.mAttention_btn);
        mAttentionCenterTitle = (TextView) findViewById(R.id.mAttentionCenterTitle);
        mAttentionRelativeLayout = findViewById(R.id.mAttentionRelativeLayout);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mAttentionCenterTitle.setText(mPName);
        mAttentionTitleLayout = (RelativeLayout) findViewById(R.id.mAttentionTitleLayout);
        mAttentionTitleLayout.setOnClickListener(this);
        mAttentionLeftBack.setOnClickListener(this);
        mAttentionRightMore.setOnClickListener(this);
        mAttention_btn.setOnClickListener(this);
        if (ismAttention) {
            mAttention_btn.setText("已关注");
            mAttention_btn.setBackgroundResource(R.drawable.attention_tv_shape);
            mAttention_btn.setTextColor(getResources().getColor(R.color.unattention_line_color));
        } else {
            mAttention_btn.setText("关注");
            mAttention_btn.setBackgroundResource(R.drawable.unattention_tv_shape);
            mAttention_btn.setTextColor(getResources().getColor(R.color.attention_line_color));
        }
        mAttentionList = (PullToRefreshListView) findViewById(R.id.mAttentionList);
        mAttentionList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mAttentionList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }
        });
        mAttentionList.setOnStateListener(new PullToRefreshBase.onStateListener() {
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
                mAttentionList.setFooterViewInvisible();
            }
        });
        mAdapter = new NewsFeedAdapter(this, null, null);
        mAdapter.isAttention();
        mAttentionList.setAdapter(mAdapter);
        addListOtherView();
        mAttentionList.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * ListView的状态改变时触发
             * @param view
             * @param scrollState
             */
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

            /**
             * 正在滚动
             * firstVisibleItem第一个Item的位置
             * visibleItemCount 可见的Item的数量
             * totalItemCount item的总数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (thisVisibleItemCount < totalItemCount) {
                    thisTotalItemCount = totalItemCount;
                    thisVisibleItemCount = visibleItemCount;
                }
                if (firstVisibleItem <= 1) {
                    mAttention_btn.setVisibility(View.VISIBLE);
                } else {
                    if (mAttentionCenterTitle.getVisibility() == View.GONE) {
                        mAttentionCenterTitle.setVisibility(View.VISIBLE);
                        mAttentionTitleLayout.setBackgroundColor(getResources().getColor(R.color.bg_share));
                    }
                    mAttention_btn.setVisibility(View.GONE);
                }
                if (firstVisibleItem != 1) {
                    return;
                }
                final int[] location = new int[2];
                tv_attention_title.getLocationInWindow(location);
                int mY = location[1];
                final int[] locationTitle = new int[2];
                mAttentionCenterTitle.getLocationInWindow(locationTitle);
                int gY = locationTitle[1];
                Logger.e("aaa", "mY====" + locationTitle[1]);

                if (gY >= mY) {
                    if (tv_attention_title.getVisibility() == View.VISIBLE) {
                        tv_attention_title.setVisibility(View.INVISIBLE);
                    }
                    if (mAttentionCenterTitle.getVisibility() == View.GONE) {
                        mAttentionCenterTitle.setVisibility(View.VISIBLE);
                    }
                    mAttentionTitleLayout.setBackgroundColor(getResources().getColor(R.color.bg_share));
                } else {
                    if (tv_attention_title.getVisibility() == View.INVISIBLE) {
                        tv_attention_title.setVisibility(View.VISIBLE);
                    }
                    if (mAttentionCenterTitle.getVisibility() == View.VISIBLE) {
                        mAttentionCenterTitle.setVisibility(View.GONE);
                    }
                    mAttentionTitleLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                int margin = mY - gY;
                if (maxMargin < margin) {
                    maxMargin = margin;
                }
                float proportion = (float) margin / (float) maxMargin;
                Logger.e("aaa", "proportion==" + proportion);
                mAttention_btn.setAlpha(proportion);
            }
        });
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_attention);
        mUser = SharedPreManager.mInstance(this).getUser(this);
        mRequestManager = Glide.with(this);
//        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
//        mAlphaAnimationIn.setDuration(500);
//        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
//        mAlphaAnimationOut.setDuration(500);
    }


    public void addListOtherView() {
        LinearLayout attentionHeadView = (LinearLayout) getLayoutInflater().inflate(R.layout.attention_headview_item, null);
        lv = mAttentionList.getRefreshableView();
        lv.addHeaderView(attentionHeadView);
        linear_attention_descrLayout = (LinearLayout) attentionHeadView.findViewById(R.id.linear_attention_descrLayout);
        attention_headViewItem_Content = (TextView) attentionHeadView.findViewById(R.id.attention_headViewItem_Content);
        tv_attention_historyTitle = (TextView) attentionHeadView.findViewById(R.id.tv_attention_historyTitle);
        tv_attention_noData = (TextView) attentionHeadView.findViewById(R.id.tv_attention_noData);
        img_attention_line = (ImageView) attentionHeadView.findViewById(R.id.img_attention_line);
        tv_attention_title = (TextView) attentionHeadView.findViewById(R.id.tv_attention_title);
        tv_attention_title.setText(mPName);
        iv_attention_headImage = (ImageView) attentionHeadView.findViewById(R.id.iv_attention_headImage);
        footerView = (LinearLayout) getLayoutInflater().inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);
    }

    public void loadData() {
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        String pname = null;
        String tstart = System.currentTimeMillis() + "";
        if (!TextUtil.isListEmpty(mNewsFeeds)) {//如果关注源列表数据不为空，拿到当前集合最后一条的时间给接口
            int position = mNewsFeeds.size() - 1;
            String Ptime = mNewsFeeds.get(position).getPtime();
            if (!TextUtil.isEmptyString(Ptime)) {
                tstart = DateUtil.dateStr2Long(Ptime) + "";
            }
        }
        try {
            pname = URLEncoder.encode(mPName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        NewsDetailRequest<AttentionPbsEntity> attentionRequest = new NewsDetailRequest<>(Request.Method.GET,
                new TypeToken<AttentionPbsEntity>() {
                }.getType(),
                HttpConstant.URL_GETLIST_ATTENTION + "pname=" + pname + "&info=1" + "&tcr=" + tstart + "&p=" + (mPageIndex++),
                new Response.Listener<AttentionPbsEntity>() {

                    @Override
                    public void onResponse(AttentionPbsEntity result) {
                        if (bgLayout.getVisibility() == View.VISIBLE) {
                            bgLayout.setVisibility(View.GONE);
                        }
                        mAttentionList.onRefreshComplete();
                        AttentionListEntity attentionListEntity = result.getInfo();
                        String descr = attentionListEntity.getDescr();
                        if (!TextUtil.isEmptyString(descr)) {
                            linear_attention_descrLayout.setVisibility(View.VISIBLE);
                            img_attention_line.setVisibility(View.VISIBLE);
                            attention_headViewItem_Content.setText(descr);
                        } else {
                            linear_attention_descrLayout.setVisibility(View.GONE);
                            img_attention_line.setVisibility(View.GONE);
                        }
                        String icon = attentionListEntity.getIcon();
                        if (!TextUtil.isEmptyString(icon)) {
                            mRequestManager.load(Uri.parse(icon)).transform(new CommonViewHolder.GlideCircleTransform(AttentionActivity.this, 0, AttentionActivity.this.getResources().getColor(R.color.white))).into(iv_attention_headImage);
                        } else {
                            mRequestManager.load("").placeholder(R.drawable.detail_attention_placeholder).into(iv_attention_headImage);
                        }
                        List<NewsFeed> newsFeeds = result.getNews();
                        if (!TextUtil.isListEmpty(newsFeeds)) {
                            if (newsFeeds.size() < 20) {
                                mAttentionList.setMode(PullToRefreshBase.Mode.DISABLED);
                                footView_tv.setVisibility(View.GONE);
                            }
                            mNewsFeeds.addAll(newsFeeds);
                            mAdapter.setNewsFeed(mNewsFeeds);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            lv.removeFooterView(footerView);
                            mAttentionList.setMode(PullToRefreshBase.Mode.DISABLED);
                            if (TextUtil.isListEmpty(mNewsFeeds)) {
                                tv_attention_historyTitle.setVisibility(View.GONE);
                                tv_attention_noData.setVisibility(View.VISIBLE);
                            }
                        }
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (thisVisibleItemCount >= thisTotalItemCount) {//删除 footerView 这个方法可以显示无数据的情况
//                                    Logger.e("aaa", "==================================");
//                                    lv.removeFooterView(footerView);
//                                    mAttentionList.setMode(PullToRefreshBase.Mode.DISABLED);
//                                }
//                            }
//                        }, 100);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mAttentionList.onRefreshComplete();
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
                lv.removeFooterView(footerView);
                mAttentionList.setMode(PullToRefreshBase.Mode.DISABLED);
                if (TextUtil.isListEmpty(mNewsFeeds)) {
                    tv_attention_historyTitle.setVisibility(View.GONE);
                    tv_attention_noData.setVisibility(View.VISIBLE);
                }
            }
        });
        attentionRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(attentionRequest);
    }

    boolean isNetWork;

    public void addOrDeleteAttention(final boolean isAttention) {
        if (isNetWork) {
            return;
        }
        isNetWork = true;
        String pname = null;

        try {
            pname = URLEncoder.encode(mPName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        Logger.e("jigang", "attention url=" + HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + mUser.getMuid() + "&pname=" + pname);
        JSONObject json = new JSONObject();
        Logger.e("aaa", "json+++++++++++++++++++++++" + json.toString());

        DetailOperateRequest request = new DetailOperateRequest(isAttention ? Request.Method.DELETE : Request.Method.POST,
                HttpConstant.URL_ADDORDELETE_ATTENTION + "uid=" + mUser.getMuid() + "&pname=" + pname
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                Logger.e("aaa", "json+++++++++++++++++++++++" + data);
                if (isAttention) {
                    SharedPreManager.mInstance(AttentionActivity.this).deleteAttention(mPName);
                    ismAttention = false;
                    mAttention_btn.setText("关注");
                    mAttention_btn.setBackgroundResource(R.drawable.unattention_tv_shape);
                    mAttention_btn.setTextColor(getResources().getColor(R.color.attention_line_color));
                } else {
                    SharedPreManager.mInstance(AttentionActivity.this).addAttention(mPName);
                    ismAttention = true;
                    if (SharedPreManager.mInstance(AttentionActivity.this).getBoolean(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID)) {
//                        ToastUtil.showAttentionSuccessToast(AttentionActivity.this);
                    } else {
//                        AttentionDetailDialog attentionDetailDialog = new AttentionDetailDialog(AttentionActivity.this, mPName);
//                        attentionDetailDialog.show();
                        SharedPreManager.mInstance(AttentionActivity.this).save(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID, true);
                    }

                    mAttention_btn.setText("已关注");
                    mAttention_btn.setBackgroundResource(R.drawable.attention_tv_shape);
                    mAttention_btn.setTextColor(getResources().getColor(R.color.unattention_line_color));
                }
                isNetWork = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mNewsDetailList.onRefreshComplete();
                if (error.getMessage().indexOf("2003") != -1) {
                    ismAttention = true;
                    ToastUtil.toastShort("用户已关注该信息！");
                    mAttention_btn.setText("已关注");
                    mAttention_btn.setBackgroundResource(R.drawable.attention_tv_shape);
                    mAttention_btn.setTextColor(getResources().getColor(R.color.unattention_line_color));
                    return;
                }
                Logger.e("jigang", "network fail");
                isNetWork = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.mInstance(AttentionActivity.this).getUser(this).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // AppBar的监听
//        mAblAppBar.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // AppBar的监听
//        mAblAppBar.removeOnOffsetChangedListener(this);
    }


    @Override
    public void onClick(View view) {
//        mAttentionLeftBack,mAttentionRightMore, mAttention_btn
        int id = view.getId();
        if (id == R.id.mAttentionLeftBack) {
            finish();

        } else if (id == R.id.mAttentionTitleLayout) {//这里什么都不用写，这是title的点击事件

        } else if (id == R.id.mAttentionRightMore) {//                if (mNewsFeed != null) {
//                    mivShareBg.startAnimation(mAlphaAnimationIn);
//                    mivShareBg.setVisibility(View.VISIBLE);
//                    mSharePopupWindow = new SharePopupWindow(this, this);
//                    String remark = mNewsFeed.getDescr();
//                    String url = "http://deeporiginalx.com/news.html?type=0" + "&url=" + TextUtil.getBase64(mNewsFeed.getUrl()) + "&interface";
//                    mSharePopupWindow.setTitleAndUrl(mNewsFeed, remark);
////                    mSharePopupWindow.setOnFavoritListener(listener);
//                    mSharePopupWindow.showAtLocation(mAttentionRelativeLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//                }
        } else if (id == R.id.mAttention_btn) {
            if (mUser != null && mUser.isVisitor()) {
                AuthorizedUserUtil.sendUserLoginBroadcast(AttentionActivity.this);
            } else {
                addOrDeleteAttention(ismAttention);
            }
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(CommonConstant.KEY_ATTENTION_CONPUBFLAG, ismAttention);
        intent.putExtra(CommonConstant.KEY_ATTENTION_INDEX, mIndex);
        setResult(CommonConstant.RESULT_ATTENTION_CODE, intent);
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonConstant.REQUEST_ATTENTION_CODE && resultCode == 1006) {
            mUser = SharedPreManager.mInstance(this).getUser(this);
            addOrDeleteAttention(false);
        }
    }

    @Override
    public void shareDismiss() {
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mivShareBg.setVisibility(View.INVISIBLE);
//        isFavorite = SharedPreManager.myFavoriteisSame(mUrl);
//        if(isFavorite){
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
//        }else {
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
//        }
    }

    @Override
    public void onThemeChanged() {

    }
}

