package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.DetailOperateRequest;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.utils.AuthorizedUserUtil;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.NewsCommentHeaderView;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.UserCommentDialog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 新闻评论页
 */
public class NewsCommentFgt extends Fragment {

    public static final int REQUEST_CODE = 1030;
    public static final String KEY_NEWS_FEED = "key_news_feed";
    public static final String KEY_TOP_MARGIN = "key_top_margin";
    public static final String KEY_SHOW_COMMENT = "key_show_comment";
    private PullToRefreshListView mNewsCommentList;
    private ArrayList<NewsDetailComment> mComments = new ArrayList<>();
    private CommentsAdapter mCommentsAdapter;
    private int mPageIndex = 1;
    private RefreshPageBroReceiver mRefreshReceiver;
    private RelativeLayout bgLayout;
    private NewsFeed mNewsFeed;
    private SharedPreferences mSharedPreferences;
    private NewsCommentHeaderView mNewsCommentHeaderView;
    private Context mContext;
    private RequestManager mRequestManager;
    private boolean isRefresh;
    private boolean isTopMargin;

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                mCommentsAdapter.notifyDataSetChanged();
                mNewsCommentHeaderView.setNewsCommentTitleTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL) + 2);
            } else {
                NewsDetailComment comment = (NewsDetailComment) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
                mComments.add(0, comment);
                mNewsCommentHeaderView.setNoCommentsLayoutGone();
                mCommentsAdapter.setData(mComments);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mRequestManager = Glide.with(this);
        Bundle arguments = getArguments();
        mNewsFeed = (NewsFeed) arguments.getSerializable(KEY_NEWS_FEED);
        mSharedPreferences = getActivity().getSharedPreferences("showflag", 0);
        isTopMargin = arguments.getBoolean(KEY_TOP_MARGIN);
        if (mRefreshReceiver == null) {
            mRefreshReceiver = new RefreshPageBroReceiver();
            IntentFilter filter = new IntentFilter(NewsDetailAty2.ACTION_REFRESH_COMMENT);
            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            getActivity().registerReceiver(mRefreshReceiver, filter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_news_comment, null);
        mNewsCommentList = (PullToRefreshListView) rootView.findViewById(R.id.mNewsCommentList);
        if (isTopMargin) {
            RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) mNewsCommentList.getLayoutParams();
            layout.topMargin = DensityUtil.dip2px(mContext, 48);
            mNewsCommentList.setLayoutParams(layout);
        }
        TextUtil.setLayoutBgResource(mContext, mNewsCommentList, R.color.white);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        mNewsCommentList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mCommentsAdapter = new CommentsAdapter(getActivity());
        mNewsCommentList.setAdapter(mCommentsAdapter);
        mNewsCommentHeaderView = new NewsCommentHeaderView(getActivity());
        ListView lv = mNewsCommentList.getRefreshableView();
        mNewsCommentHeaderView.setData(mNewsFeed);
        lv.addHeaderView(mNewsCommentHeaderView);
        mNewsCommentList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }
        });
        loadData();
        return rootView;
    }

    private void loadData() {
        User user = SharedPreManager.mInstance(mContext).getUser(mContext);
        if (user != null) {
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            NewsDetailRequest<ArrayList<NewsDetailComment>> feedRequest = new NewsDetailRequest<>(Request.Method.GET, new TypeToken<ArrayList<NewsDetailComment>>() {
            }.getType(), HttpConstant.URL_FETCH_COMMENTS + "did=" + TextUtil.getBase64(mNewsFeed.getDocid()) + "&uid=" + user.getMuid() +
                    "&p=" + (mPageIndex++), new Response.Listener<ArrayList<NewsDetailComment>>() {
                @Override
                public void onResponse(ArrayList<NewsDetailComment> result) {
                    if (bgLayout.getVisibility() == View.VISIBLE) {
                        bgLayout.setVisibility(View.GONE);
                    }
                    mNewsCommentList.onRefreshComplete();
                    if (!TextUtil.isListEmpty(result)) {
                        mComments.addAll(result);
                        mCommentsAdapter.setData(mComments);
                        mNewsCommentHeaderView.setNoCommentsLayoutGone();
                    } else {
                        if (!TextUtil.isListEmpty(mComments)) {
                            mNewsCommentHeaderView.setNoCommentsLayoutGone();
                        } else {
                            mNewsCommentHeaderView.setNoCommentsLayoutVisible();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mNewsCommentList.onRefreshComplete();
                    if (bgLayout.getVisibility() == View.VISIBLE) {
                        bgLayout.setVisibility(View.GONE);
                    }
                    if (error.toString().contains("服务端未找到数据 2002") && mComments.size() == 0) {
                        mNewsCommentHeaderView.setNoCommentsLayoutVisible();
                    }
                }
            });
            feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(feedRequest);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRefreshReceiver != null) {
            getActivity().unregisterReceiver(mRefreshReceiver);
        }
    }

    class CommentsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<NewsDetailComment> comments;

        CommentsAdapter(Context context) {
            mContext = context;
        }

        public void setData(ArrayList<NewsDetailComment> comments) {
            this.comments = comments;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return comments == null ? 0 : comments.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_comment1, null, false);
                holder.tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
                holder.ivHeadIcon = (ImageView) convertView.findViewById(R.id.iv_user_icon);
                holder.tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
                holder.tvTime = (TextViewExtend) convertView.findViewById(R.id.tv_time);
                holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
                holder.tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            TextUtil.setTextColor(mContext, holder.tvName, R.color.new_color2);
            TextUtil.setTextColor(mContext, holder.tvContent, R.color.new_color1);
            TextUtil.setLayoutBgResource(mContext, (ImageView) convertView.findViewById(R.id.mSelectCommentDivider), R.color.new_color4);
            holder.tvContent.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
            final NewsDetailComment comment = comments.get(position);
            final User user = SharedPreManager.mInstance(mContext).getUser(mContext);
            holder.tvTime.setText(comment.getCtime());
            if (!TextUtil.isEmptyString(comment.getAvatar())) {
                mRequestManager.load(Uri.parse(comment.getAvatar())).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
            } else {
                mRequestManager.load(R.drawable.ic_user_comment_default).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
            }
            holder.tvName.setText(comment.getUname());
            int count = comment.getCommend();
            if (count == 0) {
                holder.tvPraiseCount.setVisibility(View.INVISIBLE);
            } else {
                holder.tvPraiseCount.setVisibility(View.VISIBLE);
                holder.tvPraiseCount.setText(comment.getCommend() + "");
            }
            holder.tvContent.setText(comment.getContent());
            if (comment.getUpflag() == 0) {
                holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
            } else {
                holder.ivPraise.setImageResource(R.drawable.bg_praised);
            }
            if (user != null && user.getUserId().equals(comment.getUid())) {
                holder.ivPraise.setVisibility(View.GONE);
            } else {
                holder.ivPraise.setVisibility(View.VISIBLE);
            }
            holder.ivPraise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user != null && user.isVisitor()) {
                        AuthorizedUserUtil.sendUserLoginBroadcast(mContext);
                    } else {
                        if ((user.getMuid() + "").equals(comment.getUid())) {
                            Toast.makeText(mContext, "不能给自己点赞。", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (comment.getUpflag() == 0) {
                            comment.setUpflag(1);
                            holder.ivPraise.setImageResource(R.drawable.bg_praised);
                            int num = 0;
                            if (comment.getCommend() == 0) {
                                num = 1;
                            } else {
                                num = comment.getCommend() + 1;
                            }
                            holder.tvPraiseCount.setVisibility(View.VISIBLE);
                            comment.setCommend(num);
                            holder.tvPraiseCount.setText(num + "");
                            addNewsLove(user, comment, true);
                        } else {
                            comment.setUpflag(0);
                            holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
                            int num = 0;
                            if (comment.getCommend() != 0) {
                                num = comment.getCommend() - 1;
                            }
                            if (num == 0) {
                                holder.tvPraiseCount.setVisibility(View.INVISIBLE);
                            }
                            comment.setCommend(num);
                            holder.tvPraiseCount.setText(num + "");
                            addNewsLove(user, comment, false);
                        }
                    }
                }
            });
            return convertView;
        }
    }

    private void addNewsLove(User user, NewsDetailComment comment, final boolean isAdd) {
        if (isRefresh) {
            return;
        }
        isRefresh = true;
        try {
            String name = URLEncoder.encode(user.getUserName(), "utf-8");
            String cid = URLEncoder.encode(comment.getId(), "utf-8");
            user.setUserName(name);
            comment.setId(cid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        DetailOperateRequest request = new DetailOperateRequest(isAdd ? Request.Method.POST : Request.Method.DELETE,
                HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&cid=" + comment.getId()
                , new JSONObject().toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                if (!TextUtil.isEmptyString(data)) {
                    isRefresh = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isRefresh = false;
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

    class Holder {
        ImageView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvTime;
        TextViewExtend tvPraiseCount;
        ImageView ivPraise;
    }
}
