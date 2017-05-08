package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.database.NewsDetailCommentDao;
import com.news.sdk.entity.NewsDetailComment;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.DetailOperateRequest;
import com.news.sdk.net.volley.NewsLoveRequest;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailCommentAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fengjigang on 16/4/8.
 */
public class MyCommentAty extends Activity implements View.OnClickListener, NewsDetailCommentAdapter.OnDataIsNullListener {
    private View mCommentLeftBack;
    private TextView mCommentUserName;
    private TextView clip_pic;
    private ListView mCommentListView;
    private ArrayList<NewsDetailComment> newsDetailCommentItems = new ArrayList<>();
    private NewsDetailCommentDao newsDetailCommentDao;
    private int dHeight;
    private RelativeLayout comment_nor;
    private Context mContext;
    private User user;
    private NewsDetailCommentAdapter newsDetailCommentAdapter;
    private RelativeLayout bgLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initializeViews();
        loadData();
    }

    protected void setContentView() {
        setContentView(R.layout.aty_my_comment);
    }

    protected void initializeViews() {
        mContext = this;
        mCommentLeftBack = findViewById(R.id.mCommentLeftBack);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mCommentLeftBack.setOnClickListener(this);
        mCommentUserName = (TextView) findViewById(R.id.mCommentUserName);
        mCommentListView = (ListView) this.findViewById(R.id.myCommentListView);
        newsDetailCommentDao = new NewsDetailCommentDao(this);
        clip_pic = (TextView) findViewById(R.id.clip_pic);
        comment_nor = (RelativeLayout) findViewById(R.id.layout_nor_comment);
        newsDetailCommentAdapter = new NewsDetailCommentAdapter(R.layout.user_detail_record_item, this, newsDetailCommentItems);
        newsDetailCommentAdapter.setOnDataIsNullListener(this);
        newsDetailCommentAdapter.setDaoHeight(dHeight);
        newsDetailCommentAdapter.setClip_pic(clip_pic);
//        newsDetailCommentAdapter.setClickAddorDeleteLoveItemListener(clickAddorDeleteLoveItemListener);
        newsDetailCommentAdapter.setClickDeleteCommentItemListener(clickDeleteCommentItemListener);
        newsDetailCommentAdapter.setNewsDetailCommentDao(newsDetailCommentDao);
        mCommentListView.setAdapter(newsDetailCommentAdapter);
        View footer = LayoutInflater.from(this).inflate(R.layout.detail_footview_layout, null);
        mCommentListView.addFooterView(footer, null, false);
        isLoading(true);

    }

    //    ClickAddorDeleteLoveItemListener clickAddorDeleteLoveItemListener = new ClickAddorDeleteLoveItemListener() {
//        @Override
//        public void addorDele(int upflag, int position) {
//            NewsDetailComment bean = newsDetailCommentItems.get(position);
//            addNewsLove(bean.getComment_id(),position,upflag==0);
//        }
//    };
    NewsDetailCommentAdapter.ClickDeleteCommentItemListener clickDeleteCommentItemListener = new NewsDetailCommentAdapter.ClickDeleteCommentItemListener() {
        @Override
        public void delete(int position) {
            isLoading(true);
            NewsDetailComment bean = newsDetailCommentItems.get(position);
            deleteComment(bean.getId(), bean.getDocid(), position);
        }
    };


    protected boolean translucentStatus() {
        return false;
    }

    protected void loadData() {
        user = SharedPreManager.mInstance(mContext).getUser(this);
//        //FIXME debug
//        if (user == null){
//            user = new User();
//            user.setUserName("forward_one");
//            user.setUserIcon("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0");
//        }
        if (user != null && !user.isVisitor()) {


            mCommentUserName.setText(user.getUserName());
            Uri uri = Uri.parse(user.getUserIcon());
            newsDetailCommentItems = newsDetailCommentDao.queryForAll(user.getMuid());
            Logger.e("aaa", "===========" + newsDetailCommentItems.toString());
            if (!TextUtil.isListEmpty(newsDetailCommentItems)) {
                mCommentListView.setVisibility(View.VISIBLE);
                comment_nor.setVisibility(View.GONE);
                newsDetailCommentAdapter.setNewsFeed(newsDetailCommentItems);
                newsDetailCommentAdapter.notifyDataSetChanged();
                isLoading(false);
            } else {
                loadComment();
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mCommentLeftBack:
                finish();
                break;
        }
    }

    public void loadComment() {
//        showBGLayout(true);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        Logger.e("aaa", "URL=======" + HttpConstant.URL_USER_CREATEORDELETE_COMMENTLIST + "uid=" + user.getMuid());
        NewsLoveRequest<ArrayList<NewsDetailComment>> request = new NewsLoveRequest<ArrayList<NewsDetailComment>>(Request.Method.GET,
                new TypeToken<ArrayList<NewsDetailComment>>() {
                }.getType(), HttpConstant.URL_USER_CREATEORDELETE_COMMENTLIST + "uid=" + user.getMuid()
                , new Response.Listener<ArrayList<NewsDetailComment>>() {
            @Override
            public void onResponse(ArrayList<NewsDetailComment> response) {
                Logger.e("aaa", "评论内容======" + response.toString());
                newsDetailCommentItems = response;
                if (TextUtil.isListEmpty(newsDetailCommentItems)) {
                    isHaveData(false);
                } else {
                    isHaveData(true);
                    newsDetailCommentDao.addList(newsDetailCommentItems);
                    newsDetailCommentAdapter.setNewsFeed(newsDetailCommentItems);
                    newsDetailCommentAdapter.notifyDataSetChanged();
                }
                isLoading(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //这里有问题，比如点击进入详情页，后关闭网络回来会没有数据（实际是有数据的）
                isHaveData(false);
                newsDetailCommentItems = new ArrayList<NewsDetailComment>();
                newsDetailCommentAdapter.setNewsFeed(newsDetailCommentItems);
                newsDetailCommentAdapter.notifyDataSetChanged();
                isLoading(false);
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(mContext).getAuthorToken());
//        header.put("Content-Type", "application/json");
//        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);


    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        Point outP = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outP);
        Rect outRect = new Rect();
        this.getWindow().findViewById(Window.ID_ANDROID_CONTENT)
                .getDrawingRect(outRect);
        dHeight = outP.y - outRect.height();
    }

    public void isHaveData(boolean isVisity) {
        if (isVisity) {
            mCommentListView.setVisibility(View.VISIBLE);
            comment_nor.setVisibility(View.GONE);
        } else {
            mCommentListView.setVisibility(View.GONE);
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

    @Override
    public void onChangeLayout() {
        isHaveData(false);

    }


    boolean isNetWork;

    //    private void addNewsLove(String cid,final int position, final boolean isAdd) {
//        if(isNetWork){
//            return;
//        }
//        isNetWork = true;
//        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
//        Logger.e("jigang", "love url=" +         HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&cid=" + cid);
//        JSONObject json = new JSONObject();
//        Logger.e("aaa","json+++++++++++++++++++++++"+json.toString());
//
//        DetailOperateRequest request = new DetailOperateRequest( isAdd ? Request.Method.POST : Request.Method.DELETE,
//                HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&cid=" + cid
//                , json.toString(), new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                String data = response.optString("data");
//                Logger.e("jigang", "network success, love" + data);
//                if (!TextUtil.isEmptyString(data)) {
//                    if(isAdd){
//                        newsDetailCommentItems.get(position).setUpflag(1);
//                    }else{
//                        newsDetailCommentItems.get(position).setUpflag(0);
//                    }
//                    newsDetailCommentItems.get(position).setCommend(Integer.parseInt(data));
//                    newsDetailCommentAdapter.notifyDataSetChanged();
//                }
//                isNetWork = false;
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Logger.e("jigang", "network fail");
//                isNetWork = false;
//            }
//        });
//        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
//        header.put("Content-Type", "application/json");
//        header.put("X-Requested-With", "*");
//        request.setRequestHeader(header);
//        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
//        requestQueue.add(request);
//
//
//    }
    public void deleteComment(String cid, String did, final int position) {
        if (isNetWork) {
            return;
        }
        isNetWork = true;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
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
                    newsDetailCommentDao.delete(bean);
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
                Logger.e("jigang", "network fail 删除失败");
                isNetWork = false;
                isLoading(false);
            }
        });

        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }


}
