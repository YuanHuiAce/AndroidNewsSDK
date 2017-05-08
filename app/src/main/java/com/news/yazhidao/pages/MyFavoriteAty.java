package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.LinearLayout;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.sdk.adapter.NewsFeedAdapter;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.DetailOperateRequest;
import com.news.sdk.net.volley.NewsLoveRequest;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.yazhidao.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fengjigang on 16/4/8.
 */
public class MyFavoriteAty extends Activity implements View.OnClickListener {
    private View mFavoriteLeftBack;
    private RelativeLayout bgLayout;
    private PullToRefreshListView mFavoriteListView;
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> newsFeedList = new ArrayList<NewsFeed>();
    private ArrayList<NewsFeed> deleteFeedList = new ArrayList<NewsFeed>();
    private boolean isHaveFooterView;
    private TextView mFavoriteRightManage,aty_myFavorite_number;
    private LinearLayout aty_myFavorite_Deletelayout;
    private boolean isDeleteyFavorite;
    private User user;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initializeViews();
        loadData();
    }

    protected void setContentView() {
        setContentView(R.layout.aty_my_favorite);
    }

    protected void initializeViews() {
        mContext = this;
        mFavoriteLeftBack = findViewById(R.id.mFavoriteLeftBack);

        mFavoriteRightManage = (TextView) findViewById(R.id.mFavoriteRightManage);
        mFavoriteRightManage.setOnClickListener(this);
        aty_myFavorite_number = (TextView) findViewById(R.id.aty_myFavorite_number);
        aty_myFavorite_Deletelayout = (LinearLayout) findViewById(R.id.aty_myFavorite_Deletelayout);
        aty_myFavorite_Deletelayout.setOnClickListener(this);
        user = SharedPreManager.mInstance(mContext).getUser(mContext);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mFavoriteListView = (PullToRefreshListView) findViewById(R.id.aty_myFavorite_PullToRefreshListView);
        mAdapter = new NewsFeedAdapter(this, null, null);
        mAdapter.isFavoriteList();
//        mAdapter.setIntroductionNewsFeed(mIntroductionNewsFeed);
    }

    NewsFeedAdapter.introductionNewsFeed mIntroductionNewsFeed = new NewsFeedAdapter.introductionNewsFeed() {
        @Override
        public void getDate(NewsFeed feed, boolean isCheck) {

            if(isCheck){
//                for (NewsFeed bean:deleteFeedList) {
//                    if(bean.getUrl().equals(feed.getUrl())){
//                        return;
//                    }
//                }
                deleteFeedList.add(feed);

                Logger.e("aaa"," deleteFeedList.size()===添加==="+ deleteFeedList.size());
            }else{
//                for (NewsFeed bean:deleteFeedList) {
//                    if(bean.getUrl().equals(feed.getUrl())){
//
//                        return;
//                    }
//                }
                deleteFeedList.remove(feed);
                Logger.e("aaa"," deleteFeedList.size()===删除==="+ deleteFeedList.size());
            }
            aty_myFavorite_number.setText("(" + deleteFeedList.size() + ")");
        }
    };
    protected boolean translucentStatus() {
        return false;
    }

    protected void loadData() {
        aty_myFavorite_Deletelayout.setVisibility(View.GONE);
        mFavoriteLeftBack.setOnClickListener(this);
        mFavoriteListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mFavoriteListView.setAdapter(mAdapter);
        loadFavorite();
    }
    public void loadFavorite(){
        showBGLayout(true);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        Logger.e("aaa", "URL=======" + HttpConstant.URL_SELECT_FAVORITELIST + "uid=" + SharedPreManager.mInstance(mContext).getUser(mContext).getMuid());
        NewsLoveRequest<ArrayList<NewsFeed>> request = new NewsLoveRequest<ArrayList<NewsFeed>>(Request.Method.GET,
                new TypeToken<ArrayList<NewsFeed>>() {
                }.getType(), HttpConstant.URL_SELECT_FAVORITELIST + "uid=" +user.getMuid()
                , new Response.Listener<ArrayList<NewsFeed>>() {
            @Override
            public void onResponse(ArrayList<NewsFeed> response) {
                Logger.e("aaa", "收藏内容======" + response.toString());
                newsFeedList = response;
                mAdapter.setNewsFeed(newsFeedList);
                mAdapter.notifyDataSetChanged();

                if(newsFeedList.size() == 0){
                    mFavoriteListView.setVisibility(View.GONE);
                }else{
                    if(!isHaveFooterView){
                        isHaveFooterView = true;
                        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                        ListView lv = mFavoriteListView.getRefreshableView();
                        LinearLayout mNewsDetailFootView = (LinearLayout) getLayoutInflater().inflate(R.layout.detail_footview_layout, null);
                        mNewsDetailFootView.setLayoutParams(layoutParams);
                        lv.addFooterView(mNewsDetailFootView);
                    }
                    mFavoriteListView.setVisibility(View.VISIBLE);

                }
                showBGLayout(false);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               //这里有问题，比如点击进入详情页，后关闭网络回来会没有数据（实际是有数据的）

                mFavoriteListView.setVisibility(View.GONE);
                newsFeedList = new ArrayList<NewsFeed>();
                mAdapter.setNewsFeed(newsFeedList);
                mAdapter.notifyDataSetChanged();


                showBGLayout(false);
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

    @Override
    protected void onResume() {
        super.onResume();
//        bgLayout.setVisibility(View.GONE);

//        try {
//            newsFeedList = SharedPreManager.myFavoriteGetList();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Logger.e("aaa", "newsFeedList==" + newsFeedList);
        mAdapter.setNewsFeed(newsFeedList);
        mAdapter.notifyDataSetChanged();

        if(newsFeedList.size() == 0){
            mFavoriteListView.setVisibility(View.GONE);
        }else{
            if(!isHaveFooterView){
                isHaveFooterView = true;
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                ListView lv = mFavoriteListView.getRefreshableView();
                LinearLayout mNewsDetailFootView = (LinearLayout) getLayoutInflater().inflate(R.layout.detail_footview_layout, null);
                mNewsDetailFootView.setLayoutParams(layoutParams);
                lv.addFooterView(mNewsDetailFootView);
            }
            mFavoriteListView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mFavoriteLeftBack:
                finish();
                break;
            case R.id.mFavoriteRightManage:
                if(isDeleteyFavorite){
                    for (int i = 0; i < deleteFeedList.size(); i++) {
                        deleteFeedList.get(i).setFavorite(false);
                    }
                    setDeleteType(false);
                }else{
                    setDeleteType(true);

                }
                break;
            case R.id.aty_myFavorite_Deletelayout:
                if(deleteFeedList.size() == 0){
                    ToastUtil.toastShort("无删除数据。");
                    setDeleteType(false);
                    return;
                }

                for (int i = 0; i < deleteFeedList.size(); i++) {
                    deleteFeedList.get(i).setFavorite(false);
                }
                deleteFavorite(deleteFeedList);


                break;
        }
    }
    public void deleteFavorite(final ArrayList<NewsFeed> list){
        showBGLayout(true);
        SharedPreManager.mInstance(this).myFavoritRemoveList(list);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        for(int i = 0; i < list.size(); i++){
            final int position = i;
            NewsFeed bean = list.get(i);
            JSONObject json = new JSONObject();
            Logger.e("aaa", "nid==" + bean.getNid());
            DetailOperateRequest request = new DetailOperateRequest(Request.Method.DELETE, HttpConstant.URL_ADDORDELETE_FAVORITE + "uid=" + user.getMuid() + "&nid=" + bean.getNid(), json.toString(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (position == list.size() - 1) {
                        aty_myFavorite_number.setText("");
                        loadFavorite();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    showBGLayout(false);
                }
            });
            HashMap<String, String> header = new HashMap<>();
            header.put("Authorization",  SharedPreManager.mInstance(mContext).getUser(mContext).getAuthorToken());
            header.put("Content-Type", "application/json");
            header.put("X-Requested-With", "*");
            request.setRequestHeader(header);
//            request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(request);
        }



    }

    public void setDeleteType(boolean isType){
        if(isType){
            isDeleteyFavorite = true;
            deleteLayoutAnimcation(isDeleteyFavorite);
//            mAdapter.setVisitycheckFavoriteDeleteLayout(isDeleteyFavorite);
            mFavoriteRightManage.setText("取消");
        }else{
            isDeleteyFavorite = false;
            deleteLayoutAnimcation(isDeleteyFavorite);
            mAdapter.setNewsFeed(newsFeedList);
//            mAdapter.setVisitycheckFavoriteDeleteLayout(isDeleteyFavorite);
            mFavoriteRightManage.setText("管理");
            deleteFeedList = new ArrayList<NewsFeed>();
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

    private void deleteLayoutAnimcation(boolean isStart){
        if(isStart){
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
//
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else{
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
    public void showBGLayout(boolean isShow){
        if(isShow){
            if(bgLayout.getVisibility() == View.GONE){
                bgLayout.setVisibility(View.VISIBLE);
            }
        }else{
            if(bgLayout.getVisibility() == View.VISIBLE){
                bgLayout.setVisibility(View.GONE);
            }
        }
    }

}
