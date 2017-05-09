package com.news.yazhidao.pages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.NewsDetailComment;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.DetailOperateRequest;
import com.news.sdk.net.volley.NewsLoveRequest;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailCommentAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyCommentAty extends BaseActivity implements View.OnClickListener {

    private TextView mCommentLeftBack;
    private TextView mCommentUserName;
    private ListView mCommentListView;
    private ArrayList<NewsDetailComment> newsDetailCommentItems = new ArrayList<>();
    private TextView comment_nor;
    private Context mContext;
    private User user;
    private NewsDetailCommentAdapter newsDetailCommentAdapter;
    private RelativeLayout bgLayout, mHomeRetry;
    private boolean isNetWork;
    private View footer;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_my_comment);
    }

    @Override
    protected void initializeViews() {
        mContext = this;
        mCommentLeftBack = (TextView) findViewById(R.id.mCommentLeftBack);
        mCommentLeftBack.setOnClickListener(this);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mHomeRetry = (RelativeLayout) findViewById(R.id.mHomeRetry);
        mHomeRetry.setOnClickListener(this);
        mCommentUserName = (TextView) findViewById(R.id.mCommentUserName);
        mCommentListView = (ListView) this.findViewById(R.id.myCommentListView);
        comment_nor = (TextView) findViewById(R.id.nor_comment);
        newsDetailCommentAdapter = new NewsDetailCommentAdapter(R.layout.user_detail_record_item, this, newsDetailCommentItems);
        mCommentListView.setAdapter(newsDetailCommentAdapter);
        NewsDetailCommentAdapter.ClickAddOrDeleteLoveItemListener clickAddOrDeleteLoveItemListener = new NewsDetailCommentAdapter.ClickAddOrDeleteLoveItemListener() {
            @Override
            public void addOrDele(int upFlag, int position) {
                NewsDetailComment bean = newsDetailCommentItems.get(position);
                addNewsLove(bean.getComment_id(), position, upFlag == 0);
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
        footer = LayoutInflater.from(this).inflate(R.layout.detail_footview_layout, null);
        mCommentListView.addFooterView(footer, null, false);
        isLoading(true);
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {
        if (NetUtil.checkNetWork(mContext)) {
            footer.setVisibility(View.VISIBLE);
            bgLayout.setVisibility(View.VISIBLE);
            mHomeRetry.setVisibility(View.GONE);
            user = SharedPreManager.mInstance(mContext).getUser(this);
            if (user != null && !user.isVisitor()) {
                mCommentUserName.setText(user.getUserName());
                loadComment();
            }
        } else {
            comment_nor.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);
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
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        NewsLoveRequest<ArrayList<NewsDetailComment>> request = new NewsLoveRequest<>(Request.Method.GET,
                new TypeToken<ArrayList<NewsDetailComment>>() {
                }.getType(), HttpConstant.URL_USER_CREATEORDELETE_COMMENTLIST + "uid=" + user.getMuid()
                , new Response.Listener<ArrayList<NewsDetailComment>>() {
            @Override
            public void onResponse(ArrayList<NewsDetailComment> response) {
                newsDetailCommentItems = response;
                if (TextUtil.isListEmpty(newsDetailCommentItems)) {
                    isHaveData(false);
                } else {
                    isHaveData(true);
                    newsDetailCommentAdapter.setNewsFeed(newsDetailCommentItems);
                    newsDetailCommentAdapter.notifyDataSetChanged();
                }
                isLoading(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isHaveData(false);
                isLoading(false);
            }
        });
        HashMap<String, String> header = new HashMap<>();
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public void isHaveData(boolean isVisity) {
        if (isVisity) {
            comment_nor.setVisibility(View.GONE);
            footer.setVisibility(View.VISIBLE);
        } else {
            comment_nor.setVisibility(View.VISIBLE);
            footer.setVisibility(View.GONE);
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
        JSONObject json = new JSONObject();
        DetailOperateRequest request = new DetailOperateRequest(isAdd ? Request.Method.POST : Request.Method.DELETE,
                HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&cid=" + cid
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                Logger.e("jigang", "network success, love" + data);
                if (!TextUtil.isEmptyString(data)) {
                    if (isAdd) {
                        newsDetailCommentItems.get(position).setUpflag(1);
                    } else {
                        newsDetailCommentItems.get(position).setUpflag(0);
                    }
                    newsDetailCommentItems.get(position).setCommend(Integer.parseInt(data));
                    newsDetailCommentAdapter.notifyDataSetChanged();
                }
                isNetWork = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isNetWork = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
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
                Logger.e("jigang", "network success, 删除成功===" + data);
                if (!TextUtil.isEmptyString(data)) {
                    NewsDetailComment bean = newsDetailCommentItems.get(position);
                    newsDetailCommentItems.remove(bean);
                    if (newsDetailCommentItems.size() == 0) {
                        isHaveData(false);
                    }
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
}
