package com.news.yazhidao.pages;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.news.sdk.adapter.NewsFeedAdapter;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.DetailOperateRequest;
import com.news.sdk.net.volley.NewsLoveRequest;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.yazhidao.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MyFavoriteAty extends BaseActivity implements View.OnClickListener {
    private View mFavoriteLeftBack;
    private RelativeLayout bgLayout, mHomeRetry;
    private PullToRefreshListView mFavoriteListView;
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> newsFeedList = new ArrayList<>();
    private boolean isRefresh;
    private TextView comment_nor;
    private TextView mFavoriteRightManage, aty_myFavorite_number;
    private LinearLayout aty_myFavorite_Deletelayout;
    private boolean isDeleteyFavorite;
    private User user;
    private Context mContext;
    private int pager = 1;
    private int mDeleteNum;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_my_favorite);
    }

    @Override
    protected void initializeViews() {
        mContext = this;
        user = SharedPreManager.mInstance(mContext).getUser(mContext);
        mFavoriteLeftBack = findViewById(R.id.mFavoriteLeftBack);
        mFavoriteLeftBack.setOnClickListener(this);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mHomeRetry = (RelativeLayout) findViewById(R.id.mHomeRetry);
        mHomeRetry.setOnClickListener(this);
        comment_nor = (TextView) findViewById(R.id.nor_comment);
        mFavoriteRightManage = (TextView) findViewById(R.id.mFavoriteRightManage);
        mFavoriteRightManage.setOnClickListener(this);
        aty_myFavorite_number = (TextView) findViewById(R.id.aty_myFavorite_number);
        aty_myFavorite_Deletelayout = (LinearLayout) findViewById(R.id.aty_myFavorite_Deletelayout);
        aty_myFavorite_Deletelayout.setOnClickListener(this);
        aty_myFavorite_Deletelayout.setVisibility(View.GONE);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mFavoriteListView = (PullToRefreshListView) findViewById(R.id.aty_myFavorite_PullToRefreshListView);
        mFavoriteListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mFavoriteListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isRefresh = false;
                loadFavorite();
            }
        });
        mAdapter = new NewsFeedAdapter(this, null, null);
        mAdapter.isFavoriteList();
        mAdapter.setIntroductionNewsFeed(mIntroductionNewsFeed);
        mFavoriteListView.setAdapter(mAdapter);
        isLoading(true);
    }

    NewsFeedAdapter.introductionNewsFeed mIntroductionNewsFeed = new NewsFeedAdapter.introductionNewsFeed() {
        @Override
        public void getDate(NewsFeed feed, boolean isCheck) {
            if (isCheck) {
                mDeleteNum++;
            } else {
                mDeleteNum--;
            }
            if (mDeleteNum > 0) {
                aty_myFavorite_number.setText("(" + mDeleteNum + ")");
            } else {
                aty_myFavorite_number.setText("");
            }
        }
    };

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {
        if (NetUtil.checkNetWork(mContext)) {
            bgLayout.setVisibility(View.VISIBLE);
            mHomeRetry.setVisibility(View.GONE);
            user = SharedPreManager.mInstance(mContext).getUser(this);
            if (user != null && !user.isVisitor()) {
                loadFavorite();
            }
        } else {
            bgLayout.setVisibility(View.GONE);
            mHomeRetry.setVisibility(View.VISIBLE);
        }
    }

    public void loadFavorite() {
        if (isRefresh) {
            return;
        }
        isRefresh = true;
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        NewsLoveRequest<ArrayList<NewsFeed>> request = new NewsLoveRequest<ArrayList<NewsFeed>>(Request.Method.GET,
                new TypeToken<ArrayList<NewsFeed>>() {
                }.getType(), HttpConstant.URL_SELECT_FAVORITELIST + "uid=" + user.getMuid() + "&c=8&p=" + pager
                , new Response.Listener<ArrayList<NewsFeed>>() {
            @Override
            public void onResponse(ArrayList<NewsFeed> response) {
                pager++;
                if (TextUtil.isListEmpty(response)) {
                    mFavoriteListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    isHaveData();
                } else {
                    newsFeedList.addAll(response);
                    mAdapter.setNewsFeed(newsFeedList);
                    mAdapter.notifyDataSetChanged();
                    isHaveData();
                }
                isRefresh = false;
                mFavoriteListView.onRefreshComplete();
                isLoading(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isRefresh = false;
                mFavoriteListView.onRefreshComplete();
                mFavoriteListView.setMode(PullToRefreshBase.Mode.DISABLED);
                isHaveData();
                isLoading(false);
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public void isHaveData() {
        if (!TextUtil.isListEmpty(newsFeedList)) {
            comment_nor.setVisibility(View.GONE);
        } else {
            comment_nor.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mFavoriteLeftBack:
                finish();
                break;
            case R.id.mHomeRetry:
                loadData();
                break;
            case R.id.mFavoriteRightManage:
                if (isDeleteyFavorite) {
//                    for (int i = 0; i < deleteFeedList.size(); i++) {
//                        deleteFeedList.get(i).setFavorite(false);
//                    }
                    setDeleteType(false);
                } else {
                    setDeleteType(true);
                }
                break;
            case R.id.aty_myFavorite_Deletelayout:
                deleteFavorite();
                break;
        }
    }

    public void deleteFavorite() {
        if (mDeleteNum == 0 || TextUtil.isListEmpty(newsFeedList)) {
            ToastUtil.toastShort("无删除数据。");
            setDeleteType(false);
            return;
        }
        String nid = "";
        for (Iterator it = newsFeedList.iterator(); it.hasNext(); ) {
            NewsFeed newsFeed = (NewsFeed) it.next();
            if (newsFeed.isFavorite()) {
                nid += newsFeed.getNid() + ",";
                it.remove();
            }
        }
        nid = nid.substring(0, nid.length() - 1);
        mAdapter.notifyDataSetChanged();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        DetailOperateRequest request = new DetailOperateRequest(Request.Method.DELETE, HttpConstant.URL_ADDORDELETE_FAVORITE + "uid=" + user.getMuid() + "&nid=" + nid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setDeleteType(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setDeleteType(true);
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        requestQueue.add(request);
    }

    public void setDeleteType(boolean isType) {
        if (isType) {
            isDeleteyFavorite = true;
            deleteLayoutAnimcation(isDeleteyFavorite);
            mAdapter.setVisitycheckFavoriteDeleteLayout(isDeleteyFavorite);
            mFavoriteRightManage.setText("取消");
        } else {
            isDeleteyFavorite = false;
            deleteLayoutAnimcation(isDeleteyFavorite);
            mAdapter.setNewsFeed(newsFeedList);
            mAdapter.setVisitycheckFavoriteDeleteLayout(isDeleteyFavorite);
            mFavoriteRightManage.setText("管理");
            aty_myFavorite_number.setText("");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isDeleteyFavorite) {
                setDeleteType(false);
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    private void deleteLayoutAnimcation(boolean isStart) {
        if (isStart) {
            aty_myFavorite_Deletelayout.setVisibility(View.VISIBLE);
            //初始化
            Animation translateAnimation = new TranslateAnimation(0, 0, DensityUtil.dip2px(this, 47), 0);
            //设置动画时间
            translateAnimation.setDuration(100);
            aty_myFavorite_Deletelayout.startAnimation(translateAnimation);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            //初始化
            Animation translateAnimation = new TranslateAnimation(0, 0, 0, DensityUtil.dip2px(this, 47));
            //设置动画时间
            translateAnimation.setDuration(100);
            aty_myFavorite_Deletelayout.startAnimation(translateAnimation);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    aty_myFavorite_Deletelayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    public void isLoading(boolean isShow) {
        if (isShow) {
            if (bgLayout.getVisibility() == View.GONE) {
                bgLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        }
    }
}
