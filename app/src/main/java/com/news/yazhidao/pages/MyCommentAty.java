package com.news.yazhidao.pages;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.news.yazhidao.adapter.NewsDetailCommentAdapter;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.NewsDetailComment;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.DetailOperateRequest;
import com.news.sdk.net.volley.NewsLoveRequest;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.yazhidao.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;



public class MyCommentAty extends BaseActivity implements View.OnClickListener {

    private ImageView mCommentLeftBack;
    private TextView mCommentUserName;
    private PullToRefreshListView mCommentListView;
    private ArrayList<NewsDetailComment> newsDetailCommentItems = new ArrayList<>();
    private TextView comment_nor;
    private Context mContext;
    private User user;
    private NewsDetailCommentAdapter newsDetailCommentAdapter;
    private RelativeLayout mCommentTopLayout,bgLayout, mHomeRetry;
    private boolean isRefresh, isNetWork;
    private View mHeaderDivider,footer;
    private int pager = 1;
    private ProgressBar imageAni;
    private TextView textAni;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_my_comment);
    }

    @Override
    protected void initializeViews() {
        mContext = this;
        mCommentLeftBack = (ImageView) findViewById(R.id.mCommentLeftBack);
        mCommentLeftBack.setOnClickListener(this);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mHomeRetry = (RelativeLayout) findViewById(R.id.mHomeRetry);
        mHomeRetry.setOnClickListener(this);
        mCommentTopLayout = (RelativeLayout) findViewById(R.id.mCommentTopLayout);
        mHeaderDivider = findViewById(R.id.mHeaderDivider);
        imageAni = (ProgressBar) findViewById(R.id.imageAni);
        textAni = (TextView) findViewById(R.id.textAni);
        mCommentUserName = (TextView) findViewById(R.id.mCommentUserName);
        mCommentListView = (PullToRefreshListView) this.findViewById(R.id.myCommentListView);
        mCommentListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        comment_nor = (TextView) findViewById(R.id.nor_comment);
        newsDetailCommentAdapter = new NewsDetailCommentAdapter(R.layout.user_detail_record_item, this, newsDetailCommentItems);
        mCommentListView.setAdapter(newsDetailCommentAdapter);
        mCommentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isRefresh = false;
                loadComment();
            }
        });
        NewsDetailCommentAdapter.ClickAddOrDeleteLoveItemListener clickAddOrDeleteLoveItemListener = new NewsDetailCommentAdapter.ClickAddOrDeleteLoveItemListener() {
            @Override
            public void addOrDele(int upFlag, int position) {
                NewsDetailComment bean = newsDetailCommentItems.get(position);
                addNewsLove(bean.getId(), position, upFlag == 0);
            }
        };
        NewsDetailCommentAdapter.ClickDeleteCommentItemListener clickDeleteCommentItemListener = new NewsDetailCommentAdapter.ClickDeleteCommentItemListener() {
            @Override
            public void delete(int position) {
                NewsDetailComment bean = newsDetailCommentItems.get(position);
                deleteComment(bean.getId(), bean.getDocid(), position);
            }
        };
        newsDetailCommentAdapter.setClickAddOrDeleteLoveItemListener(clickAddOrDeleteLoveItemListener);
        newsDetailCommentAdapter.setClickDeleteCommentItemListener(clickDeleteCommentItemListener);
//        footer = LayoutInflater.from(this).inflate(R.layout.detail_footview_layout, null);
//        mCommentListView.getRefreshableView().addFooterView(footer, null, false);
        isLoading(true);
        setTheme();
    }

    private void setTheme() {
        TextUtil.setLayoutBgResource(this, mCommentListView, R.color.color6);
        TextUtil.setLayoutBgResource(this, bgLayout, R.color.color6);
        TextUtil.setLayoutBgResource(this, mCommentLeftBack, R.drawable.bg_left_back_selector);
        TextUtil.setImageResource(this, mCommentLeftBack, R.drawable.btn_left_back);
        TextUtil.setLayoutBgResource(this, mCommentTopLayout, R.color.color6);
        TextUtil.setLayoutBgResource(this, mHeaderDivider, R.color.color5);
        TextUtil.setTextColor(this, mCommentUserName, R.color.color2);
        TextUtil.setTextColor(this, comment_nor, R.color.color4);
        ImageUtil.setAlphaImage(comment_nor);
        ImageUtil.setAlphaProgressBar(imageAni);
        TextUtil.setTextColor(this, textAni, R.color.color3);
    }

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
//                mCommentUserName.setText(user.getUserName());
                loadComment();
            }
        } else {
            comment_nor.setVisibility(View.GONE);
            bgLayout.setVisibility(View.GONE);
            mHomeRetry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mCommentLeftBack:
                finish();
                break;
            case R.id.mHomeRetry:
                loadData();
                break;
        }
    }

    public void loadComment() {
        if (isRefresh) {
            return;
        }
        isRefresh = true;
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        NewsLoveRequest<ArrayList<NewsDetailComment>> request = new NewsLoveRequest<>(Request.Method.GET,
                new TypeToken<ArrayList<NewsDetailComment>>() {
                }.getType(), HttpConstant.URL_USER_CREATEORDELETE_COMMENTLIST + "uid=" + user.getMuid() + "&c=5&p=" + pager
                , new Response.Listener<ArrayList<NewsDetailComment>>() {
            @Override
            public void onResponse(ArrayList<NewsDetailComment> response) {
                pager++;
                if (TextUtil.isListEmpty(response)) {
                    mCommentListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    isHaveData();
                } else {
                    newsDetailCommentItems.addAll(response);
                    newsDetailCommentAdapter.setNewsFeed(newsDetailCommentItems);
                    newsDetailCommentAdapter.notifyDataSetChanged();
                    isHaveData();
                }
                isRefresh = false;
                mCommentListView.onRefreshComplete();
                isLoading(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isRefresh = false;
                mCommentListView.onRefreshComplete();
                mCommentListView.setMode(PullToRefreshBase.Mode.DISABLED);
                isHaveData();
                isLoading(false);
            }
        });
        HashMap<String, String> header = new HashMap<>();
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public void isHaveData() {
        if (!TextUtil.isListEmpty(newsDetailCommentItems)) {
            comment_nor.setVisibility(View.GONE);
        } else {
            comment_nor.setVisibility(View.VISIBLE);
        }
    }

    private void isLoading(boolean isLoading) {
        if (isLoading) {
            if (bgLayout.getVisibility() != View.VISIBLE) {
                bgLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (bgLayout.getVisibility() != View.GONE) {
                bgLayout.setVisibility(View.GONE);
            }
        }
    }

    private void addNewsLove(String cid, final int position, final boolean isAdd) {
        if (isNetWork) {
            return;
        }
        isNetWork = true;
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        DetailOperateRequest request = new DetailOperateRequest(isAdd ? Request.Method.POST : Request.Method.DELETE,
                HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&cid=" + cid
                , new JSONObject().toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                if (!TextUtil.isEmptyString(data)) {
                    if (isAdd) {
                        newsDetailCommentItems.get(position).setUpflag(1);
                    } else {
                        newsDetailCommentItems.get(position).setUpflag(0);
                    }
                    newsDetailCommentItems.get(position).setCommend(Integer.parseInt(data));
                    newsDetailCommentAdapter.notifyDataSetChanged();
                    isNetWork = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isNetWork = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(getActivity()).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public void deleteComment(String cid, String did, final int position) {
        if (isNetWork) {
            return;
        }
        isNetWork = true;
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        JSONObject json = new JSONObject();
        DetailOperateRequest request = new DetailOperateRequest(Request.Method.DELETE,
                HttpConstant.URL_USER_CREATEORDELETE_COMMENTLIST + "cid=" + cid + "&did=" + TextUtil.getBase64(did)
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                if (!TextUtil.isEmptyString(data)) {
                    NewsDetailComment bean = newsDetailCommentItems.get(position);
                    newsDetailCommentItems.remove(bean);
                    isHaveData();
                    newsDetailCommentAdapter.notifyDataSetChanged();
                }
                isLoading(false);
                isNetWork = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isNetWork = false;
                isLoading(false);
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }
}
