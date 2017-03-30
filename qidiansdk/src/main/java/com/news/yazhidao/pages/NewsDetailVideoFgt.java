package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.jinsedeyuzhou.PlayStateParams;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.github.jinsedeyuzhou.utils.MediaNetUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailFgtAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsDetailCommentDao;
import com.news.yazhidao.entity.ADLoadNewsFeedEntity;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.DetailOperateRequest;
import com.news.yazhidao.net.volley.NewsDetailADRequestPost;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.net.volley.RelatePointRequestPost;
import com.news.yazhidao.utils.AdUtil;
import com.news.yazhidao.utils.AuthorizedUserUtil;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.SmallVideoContainer;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.VideoContainer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by fengjigang on 16/3/31.
 * 新闻详情页
 */
public class NewsDetailVideoFgt extends Fragment {
    public static final String KEY_DETAIL_RESULT = "key_detail_result";
    private NewsDetail mResult;
    private SharedPreferences mSharedPreferences;
    private PullToRefreshListView mNewsDetailList;
    private NewsDetailFgtAdapter mAdapter;
    private RelativeLayout bgLayout;
    private String mDocid, mTitle, mNewID;
    private ArrayList<NewsDetailComment> mComments;
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_ID = "key_news_id";
    public static final String KEY_NEWS_TITLE = "key_news_title";
    public static final int REQUEST_CODE = 1030;
    private LinearLayout detail_shared_FriendCircleLayout,
            detail_shared_CareForLayout,
            mCommentLayout,
            mNewsDetailHeaderView;
    private RelativeLayout detail_hot_layout;
    private TextView detail_shared_PraiseText,
            detail_shared_Text,
            detail_shared_hotComment;
    private RelativeLayout detail_shared_ShareImageLayout, detail_shared_MoreComment,
            detail_Hot_Layout,
            detail_shared_ViewPointTitleLayout, adLayout;
    private ImageView detail_shared_AttentionImage;
    private LayoutInflater inflater;
    ViewGroup container;
    private RefreshPageBroReceiver mRefreshReceiver;
    private boolean isWebSuccess;
    private static final int VIDEO_SMALL = 2;
    private static final int VIDEO_FULLSCREEN = 3;
    private static final int VIDEO_NORMAL = 5;
    private boolean isLike;
    private NewsDetailCommentDao mNewsDetailCommentDao;

    private TextView footView_tv;
    private ProgressBar footView_progressbar;
    private LinearLayout footerView_layout;
    private boolean isBottom;
    private boolean isLoadDate;
    View rootView;
    private Context mContext;
    private RequestManager mRequestManager;
    private int mScreenWidth;
    private RelativeLayout mDetailContainer;
    private ImageView mDetailBg;
    private VideoContainer mFullScreen;
    private SmallVideoContainer mSmallScreen;
    private RelativeLayout mSmallLayout;
    private RelativeLayout mDetailWrapper;
    private TextView mDetailLeftBack;
    private VideoContainer mDetailVideo;
    private RelativeLayout mVideoShowBg;
    private ImageView mClose;
    private VPlayPlayer vplayer;
    private int position;
    private NewsDetailVideoAty mNewsDetailVideoAty;
    private TextView mDetailVideoTitle;
    private User mUser;
    //广告
    private TextViewExtend adtvTitle;
    private ImageView adImageView;
    private int viewpointPage = 1;
    private LinearLayout mVideoDetailFootView;
    private LinearLayout footerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mContext = getActivity();
        mDocid = arguments.getString(KEY_NEWS_DOCID);
        mNewID = arguments.getString(KEY_NEWS_ID);
        mTitle = arguments.getString(KEY_NEWS_TITLE);
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mResult = (NewsDetail) arguments.getSerializable(KEY_DETAIL_RESULT);
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mRequestManager = Glide.with(this);
        if (mRefreshReceiver == null) {
            mRefreshReceiver = new RefreshPageBroReceiver();
            IntentFilter filter = new IntentFilter(NewsDetailAty2.ACTION_REFRESH_COMMENT);
            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            mContext.registerReceiver(mRefreshReceiver, filter);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_video_detail_listview, null);
        this.inflater = inflater;
        this.container = container;
        mUser = SharedPreManager.mInstance(mContext).getUser(mContext);
        mNewsDetailList = (PullToRefreshListView) rootView.findViewById(R.id.fgt_new_detail_PullToRefreshListView);
        TextUtil.setLayoutBgColor(mContext, mNewsDetailList, R.color.bg_detail);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        bgLayout.setVisibility(View.GONE);
        mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        mNewsDetailList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                if (!isLoadDate) {
//                    loadRelatedData();
//                }
            }
        });
//        mNewsDetailList.setOnStateListener(new PullToRefreshBase.onStateListener() {
//            @Override
//            public void getState(PullToRefreshBase.State mState) {
//                if (!isBottom) {
//                    return;
//                }
//                boolean isVisisyProgressBar = false;
//                switch (mState) {
//                    case RESET://初始
//                        isVisisyProgressBar = false;
//                        footView_tv.setText("上拉获取更多文章");
//                        break;
//                    case PULL_TO_REFRESH://更多推荐
//                        isVisisyProgressBar = false;
//                        footView_tv.setText("上拉获取更多文章");
//                        break;
//                    case RELEASE_TO_REFRESH://松开推荐
//                        isVisisyProgressBar = false;
//                        footView_tv.setText("松手获取更多文章");
//                        break;
//                    case REFRESHING:
//                    case MANUAL_REFRESHING://推荐中
//                        isVisisyProgressBar = true;
//                        footView_tv.setText("正在获取更多文章...");
//                        break;
//                    case OVERSCROLLING:
//                        // NO-OP
//                        break;
//                }
//                if (isVisisyProgressBar) {
//                    footView_progressbar.setVisibility(View.VISIBLE);
//                } else {
//                    footView_progressbar.setVisibility(View.GONE);
//                }
//                mNewsDetailList.setFooterViewInvisible();
//            }
//        });
//
//        mNewsDetailList.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                switch (scrollState) {
//                    // 当不滚动时
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//                        // 判断滚动到底部
//                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
//                            isBottom = true;
//                        } else {
//                            isBottom = false;
//                        }
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
//            }
//        });

        mAdapter = new NewsDetailFgtAdapter(getActivity(), null);
        mNewsDetailList.setAdapter(mAdapter);
        addHeadView(inflater, container);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
                loadADData();
            }
        }, 1000);
        initPlayer();
        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (mRefreshReceiver != null) {
            getActivity().unregisterReceiver(mRefreshReceiver);
        }
    }


    public void addHeadView(LayoutInflater inflater, ViewGroup container) {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        ListView lv = mNewsDetailList.getRefreshableView();
        mNewsDetailHeaderView = (LinearLayout) inflater.inflate(R.layout.fgt_news_detail, container, false);
        mVideoDetailFootView = (LinearLayout) inflater.inflate(R.layout.fgt_video_detail, container, false);
        mNewsDetailHeaderView.setLayoutParams(layoutParams);
        mVideoDetailFootView.setLayoutParams(layoutParams);
        lv.addHeaderView(mNewsDetailHeaderView);
        lv.addFooterView(mVideoDetailFootView);

        //第1部分的CommentTitle
        final View mCommentTitleView = inflater.inflate(R.layout.detail_shared_layout, container, false);
        mCommentTitleView.setLayoutParams(layoutParams);
        mNewsDetailHeaderView.addView(mCommentTitleView);
        mDetailVideoTitle = (TextView) mCommentTitleView.findViewById(R.id.detail_video_title);
        mDetailVideoTitle.setText(mResult.getTitle());

        //第2部分的viewPointContent
        final View mViewPointLayout = inflater.inflate(R.layout.vdetail_relate_layout, container, false);
        mViewPointLayout.setLayoutParams(layoutParams);
        //延时加载热点评论和相关观点
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideoDetailFootView.addView(footerView);
                mVideoDetailFootView.addView(mViewPointLayout);
            }
        }, 500);
        detail_shared_ShareImageLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_ShareImageLayout);
//        detail_shared_Text = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_Text);
        detail_shared_MoreComment = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_MoreComment);
//        detail_shared_hotComment = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_hotComment);
        detail_shared_ViewPointTitleLayout = (RelativeLayout) mCommentTitleView.findViewById(R.id.detail_shared_TitleLayout);
        detail_hot_layout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_Hot_Layout);
        mCommentLayout = (LinearLayout) mViewPointLayout.findViewById(R.id.detail_CommentLayout);

        //广告
        adLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.adLayout);
        adtvTitle = (TextViewExtend) adLayout.findViewById(R.id.title_textView);
        adImageView = (ImageView) adLayout.findViewById(R.id.adImage);
        RelativeLayout.LayoutParams adLayoutParams = (RelativeLayout.LayoutParams) adImageView.getLayoutParams();
        int imageWidth = mScreenWidth - DensityUtil.dip2px(mContext, 30);
        adLayoutParams.width = imageWidth;
        adLayoutParams.height = (int) (imageWidth * 627 / 1200.0f);
        adImageView.setLayoutParams(adLayoutParams);

        detail_shared_MoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailVideoAty mActivity = (NewsDetailVideoAty) getActivity();
                if (!mActivity.isCommentPage) {
                    mActivity.isCommentPage = true;
                    mActivity.mNewsDetailViewPager.setCurrentItem(1);
                    mActivity.mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                    mActivity.mDetailCommentNum.setVisibility(View.GONE);
                }
            }
        });
        TextUtil.setLayoutBgColor(mContext, (LinearLayout) mViewPointLayout, R.color.bg_detail);
        TextUtil.setLayoutBgColor(mContext, detail_shared_ViewPointTitleLayout, R.color.bg_detail);

        footerView = (LinearLayout) inflater.inflate(R.layout.footerview_layout, null);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);
        footerView_layout = (LinearLayout) footerView.findViewById(R.id.footerView_layout);
        footerView_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRelatedData();
            }
        });
//        footerView_layout.setVisibility(View.GONE);
    }

    private void loadData() {
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        NewsDetailRequest<ArrayList<NewsDetailComment>> feedRequest = new NewsDetailRequest<>(Request.Method.GET, new TypeToken<ArrayList<NewsDetailComment>>() {
        }.getType(), HttpConstant.URL_FETCH_HOTCOMMENTS + "did=" + TextUtil.getBase64(mDocid) +
                (mUser != null ? "&uid=" + SharedPreManager.mInstance(mContext).getUser(mContext).getMuid() : "") +
                "&p=" + (1) + "&c=" + (4), new Response.Listener<ArrayList<NewsDetailComment>>() {

            @Override
            public void onResponse(ArrayList<NewsDetailComment> result) {
                isWebSuccess = true;
                isBgLayoutSuccess();
                if (!TextUtil.isListEmpty(result)) {
                    //同步服务器上的评论数据到本地数据库
                    //  addCommentInfoToSql(mComments);
                    addCommentContent(result);
                } else {
                    detail_shared_MoreComment.setVisibility(View.GONE);
                    detail_hot_layout.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isWebSuccess = true;
                isBgLayoutSuccess();
                detail_shared_MoreComment.setVisibility(View.GONE);
                detail_hot_layout.setVisibility(View.GONE);
            }
        });
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
        loadRelatedData();
    }

    private void loadRelatedData() {
        if (!TextUtil.isEmptyString(mNewID)) {
            isLoadDate = true;
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            String requestUrl = HttpConstant.URL_NEWS_RELATED;
            ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
            adLoadNewsFeedEntity.setUid(SharedPreManager.mInstance(mContext).getUser(mContext).getMuid());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("nid", Integer.valueOf(mNewID));
                jsonObject.put("b", TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_FEED_AD_ID)));
                jsonObject.put("p", viewpointPage);
                jsonObject.put("c", (6));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //加入详情页广告位id
            adLoadNewsFeedEntity.setB(TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_DETAIL_AD_ID)));
            RelatePointRequestPost<ArrayList<RelatedItemEntity>> relateRequestPost = new RelatePointRequestPost(requestUrl, jsonObject.toString(), new Response.Listener<ArrayList<RelatedItemEntity>>() {
                @Override
                public void onResponse(final ArrayList<RelatedItemEntity> relatedItemEntities) {
                    isLoadDate = false;
                    viewpointPage++;
                    //去掉跳转到h5
                    Iterator<RelatedItemEntity> iterator = relatedItemEntities.iterator();
                    while (iterator.hasNext()) {
                        RelatedItemEntity relatedItemEntity = iterator.next();
                        String url = relatedItemEntity.getUrl();
                        if (relatedItemEntity.getRtype() != 3 && !url.contains("deeporiginalx.com")) {
                            iterator.remove();
                        }
                    }
                    for (RelatedItemEntity relatedItemEntity : relatedItemEntities) {
                        if (TextUtil.isEmptyString(relatedItemEntity.getImgUrl())) {
                            relatedItemEntity.setStyle(0);
                        } else {
                            int rtype = relatedItemEntity.getRtype();
                            if (rtype == 6) {
                                relatedItemEntity.setStyle(8);
                            } else {
                                relatedItemEntity.setStyle(1);
                            }
                        }
                    }
                    mNewsDetailList.onRefreshComplete();
                    if (!TextUtil.isListEmpty(relatedItemEntities)) {
                        setBeanPageList(relatedItemEntities);
                    } else {
                        setNoRelatedDate();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isLoadDate = false;
                    mNewsDetailList.onRefreshComplete();
                    setNoRelatedDate();
                }
            });
            relateRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(relateRequestPost);
        }
    }

    public void setNoRelatedDate() {
        mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        if (viewpointPage > 1) {
            if (footerView_layout.getVisibility() == View.GONE) {
                footerView_layout.setVisibility(View.VISIBLE);
            }
            footView_tv.setText("内容加载完毕");
        } else {
            mAdapter.setNewsFeed(null);
            mAdapter.notifyDataSetChanged();
            if (footerView_layout.getVisibility() == View.VISIBLE) {
                footerView_layout.setVisibility(View.GONE);
            }
        }
    }

    private void addCommentInfoToSql(ArrayList<NewsDetailComment> mComments) {
        if (TextUtil.isListEmpty(mComments)) {
            int commentNum = mComments.size();
            List<NewsDetailComment> newsDetailCommentItems = new ArrayList<>();
            String[] commentIds = new String[commentNum];
            for (int i = 0; i < commentNum; i++) {
                commentIds[i] = mComments.get(i).getComment_id();
            }
            mNewsDetailCommentDao = new NewsDetailCommentDao(getActivity());
            newsDetailCommentItems = mNewsDetailCommentDao.qureyByIds(commentIds);
            if (newsDetailCommentItems == null || newsDetailCommentItems.size() == 0) {
                return;
            } else {
                for (NewsDetailComment ndc : newsDetailCommentItems) {
                    mNewsDetailCommentDao.update(ndc);
                }
            }
        }
    }

    ArrayList<RelatedItemEntity> beanList = new ArrayList<RelatedItemEntity>();

    public void setBeanPageList(ArrayList<RelatedItemEntity> relatedItemEntities) {
        beanList.addAll(relatedItemEntities);
        mAdapter.setNewsFeed(beanList);
        mAdapter.notifyDataSetChanged();
        if (mNewsDetailList.getMode() != PullToRefreshBase.Mode.PULL_FROM_END) {
            mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        if (footerView_layout.getVisibility() == View.GONE) {
            footerView_layout.setVisibility(View.VISIBLE);
        }
        if (relatedItemEntities.size() < 6) {
            footView_tv.setText("内容加载完毕");
            mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        detail_shared_ViewPointTitleLayout.setVisibility(View.VISIBLE);
    }

    public void addCommentContent(final ArrayList<NewsDetailComment> result) {
        detail_hot_layout.setVisibility(View.VISIBLE);
        int num;
        int size = result.size();
        if (size > 3) {
            num = 3;
            detail_shared_MoreComment.setVisibility(View.VISIBLE);
        } else {
            num = size;
            detail_shared_MoreComment.setVisibility(View.GONE);
        }
        for (int i = 0; i < num; i++) {
            RelativeLayout ccView = (RelativeLayout) inflater.inflate(R.layout.adapter_list_comment1, container, false);
            View mSelectCommentDivider = ccView.findViewById(R.id.mSelectCommentDivider);
            NewsDetailVideoFgt.CommentHolder holder = new NewsDetailVideoFgt.CommentHolder(ccView);
            NewsDetailComment comment = result.get(i);
            UpdateCCView(holder, comment);
            if (num - 1 == i) {
                mSelectCommentDivider.setVisibility(View.GONE);
            }
            mCommentLayout.addView(ccView);
        }
    }

    class CommentHolder {
        ImageView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvTime;
        TextViewExtend tvPraiseCount;
        ImageView ivPraise;

        public CommentHolder(View convertView) {
            tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
            ivHeadIcon = (ImageView) convertView.findViewById(R.id.iv_user_icon);
            tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
            ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
            tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
            tvTime = (TextViewExtend) convertView.findViewById(R.id.tv_time);
        }
    }

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    public void UpdateCCView(final NewsDetailVideoFgt.CommentHolder holder, final NewsDetailComment comment) {
        if (!TextUtil.isEmptyString(comment.getAvatar())) {
            Uri uri = Uri.parse(comment.getAvatar());
            mRequestManager.load(uri).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
        } else {
            mRequestManager.load(R.drawable.ic_user_comment_default).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
        }
        holder.tvName.setText(comment.getUname());
        holder.tvContent.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        holder.tvContent.setText(comment.getContent());
        holder.tvTime.setText(comment.getCtime());
        if (comment.getUpflag() == 0) {
            holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
        } else {
            holder.ivPraise.setImageResource(R.drawable.bg_praised);
        }
        int count = comment.getCommend();
        if (count == 0) {
            holder.tvPraiseCount.setVisibility(View.INVISIBLE);
        } else {
            holder.tvPraiseCount.setVisibility(View.VISIBLE);
            holder.tvPraiseCount.setText(comment.getCommend() + "");
        }
        holder.ivPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = SharedPreManager.mInstance(mContext).getUser(mContext);
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
    }

    private boolean isRefresh;

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

    public void isBgLayoutSuccess() {
        if (isWebSuccess && bgLayout.getVisibility() == View.VISIBLE) {
            bgLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(null);
        if (vplayer != null) {
            vplayer.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vplayer != null) {
            if (vplayer.getParent() != null)
                ((ViewGroup) vplayer.getParent()).removeAllViews();
            vplayer.onDestory();
        }
        vplayer = null;

    }

    public interface ShowCareforLayout {
        void show();
    }

    ShowCareforLayout mShowCareforLayout;

    public void setShowCareforLayout(ShowCareforLayout showCareforLayout) {
        mShowCareforLayout = showCareforLayout;
    }

    private void loadADData() {
        if (SharedPreManager.mInstance(mContext).getUser(mContext) != null) {
            String requestUrl = HttpConstant.URL_NEWS_DETAIL_AD;
            ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
            adLoadNewsFeedEntity.setUid(SharedPreManager.mInstance(mContext).getUser(mContext).getMuid());
            Gson gson = new Gson();
            //加入详情页广告位id
            adLoadNewsFeedEntity.setB(TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_DETAIL_AD_ID)));
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            NewsDetailADRequestPost<ArrayList<NewsFeed>> newsFeedRequestPost = new NewsDetailADRequestPost(requestUrl, gson.toJson(adLoadNewsFeedEntity), new Response.Listener<ArrayList<NewsFeed>>() {
                @Override
                public void onResponse(final ArrayList<NewsFeed> result) {
                    final NewsFeed newsFeed = result.get(0);
                    if (newsFeed != null) {
                        adtvTitle.setText(newsFeed.getTitle());
                        final ArrayList<String> imgs = newsFeed.getImgs();
                        if (!TextUtil.isListEmpty(imgs)) {
                            mRequestManager.load(imgs.get(0)).placeholder(R.drawable.bg_load_default_small).into(adImageView);
                            adImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    mRequestManager.load(imgs.get(0)).placeholder(R.drawable.bg_load_default_small).into(adImageView);
                                }
                            });
                        }
                        adLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent AdIntent = new Intent(mContext, NewsDetailWebviewAty.class);
                                AdIntent.putExtra("key_url", newsFeed.getPurl());
                                mContext.startActivity(AdIntent);
                            }
                        });
                        AdUtil.upLoadAd(newsFeed, mContext);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    adLayout.setVisibility(View.GONE);
                }
            });
            newsFeedRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(newsFeedRequestPost);
        }
    }

    //=======================================视频播放器新增==========================================


    private void initPlayer() {
        //=====================================视频==================================//
        mDetailContainer = (RelativeLayout) getActivity().findViewById(R.id.rl_detail_container);
        mDetailBg = (ImageView) rootView.findViewById(R.id.detail_image_bg);
        mFullScreen = (VideoContainer) getActivity().findViewById(R.id.detail_full_screen);
        mSmallScreen = (SmallVideoContainer) getActivity().findViewById(R.id.detail_small_screen);
        mSmallLayout = (RelativeLayout) getActivity().findViewById(R.id.detai_small_layout);
        mDetailWrapper = (RelativeLayout) getActivity().findViewById(R.id.mDetailWrapper);
        mDetailLeftBack = (TextView) getActivity().findViewById(R.id.mDetailLeftBack);
        mClose = (ImageView) getActivity().findViewById(R.id.detial_video_close);
        //视频
        mDetailVideo = (VideoContainer) rootView.findViewById(R.id.fgt_new_detail_video);
        mVideoShowBg = (RelativeLayout) rootView.findViewById(R.id.detial_video_show);
        mVideoShowBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoShowBg.setVisibility(View.GONE);
                mDetailVideo.setVisibility(View.VISIBLE);
                if (vplayer.getParent() != null)
                    ((ViewGroup) vplayer.getParent()).removeAllViews();
                vplayer.setTitle(mResult.getTitle());
                mDetailVideo.addView(vplayer);
                vplayer.play(mResult.getVideourl(), position);
            }
        });
        mSmallLayout.setClickable(true);
        mSmallLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsDetailVideoAty mActivity = (NewsDetailVideoAty) mContext;
                if (mActivity.isCommentPage) {
                    mActivity.isCommentPage = false;
                    mActivity.mNewsDetailViewPager.setCurrentItem(0);

                }
            }
        });

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vplayer != null && vplayer.isPlay()) {
                    vplayer.stop();
                    vplayer.release();
                    mSmallScreen.removeAllViews();
                    mSmallLayout.setVisibility(View.GONE);
                    mVideoShowBg.setVisibility(View.VISIBLE);
                }
            }
        });
        //视频新增
        int widths = mScreenWidth - DensityUtil.dip2px(mContext, 0);
        RelativeLayout.LayoutParams lpVideo = (RelativeLayout.LayoutParams) mDetailBg.getLayoutParams();
        lpVideo.width = widths;
        lpVideo.height = (int) (widths * 185 / 330.0f);
        mDetailBg.setLayoutParams(lpVideo);
        setIsShowImagesSimpleDraweeViewURI(mDetailBg, mResult.getThumbnail());
        if (vplayer.getParent() != null)
            ((ViewGroup) vplayer.getParent()).removeAllViews();
//        vp.setTitle(mResult.getTitle());
//        vp.start(mResult.getVideourl());


        if (MediaNetUtils.getNetworkType(mContext) == 3) {
            mVideoShowBg.setVisibility(View.GONE);
            vplayer.setTitle(mResult.getTitle());
            vplayer.play(mResult.getVideourl(), position);
//            vplayer.start(mResult.getVideourl());
            mDetailVideo.addView(vplayer);
        }

        vplayer.setCompletionListener(new VPlayPlayer.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                if (mSmallLayout.getVisibility() == View.VISIBLE) {
                    mSmallScreen.removeAllViews();
                    mSmallLayout.setVisibility(View.GONE);
                } else if (mFullScreen.getVisibility() == View.VISIBLE) {
                    mFullScreen.removeAllViews();
                    mFullScreen.setVisibility(View.GONE);
                } else if (mDetailVideo.getVisibility() == View.VISIBLE) {
                    mDetailVideo.removeAllViews();
                    mDetailVideo.setVisibility(View.GONE);
                }
                if (vplayer != null) {
                    vplayer.stop();
                    vplayer.release();
                }
                position = 0;

                mVideoShowBg.setVisibility(View.VISIBLE);

            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case VIDEO_SMALL:
                    if (vplayer == null)
                        return;
                    int currentItem = (int) msg.obj;
                    if (currentItem == 0) {
                        if (vplayer.isPlay() || (vplayer.isPlay() || vplayer.getStatus() == PlayStateParams.STATE_PREPARE)) {
                            if (vplayer.getParent() != null)
                                ((ViewGroup) vplayer.getParent()).removeAllViews();
                            mDetailVideo.addView(vplayer);
                            vplayer.setShowContoller(true);
                            mSmallScreen.removeAllViews();
                            mSmallLayout.setVisibility(View.GONE);

                        } else if (vplayer.getStatus() == PlayStateParams.STATE_PAUSED) {
                            mSmallLayout.setVisibility(View.GONE);
                        }

//                        else if (vp.getStatus()== PlayStateParams.STATE_PAUSED)
//                        {
//                            mSmallLayout.setVisibility(View.GONE);
//                        }

                        else {
                            if (vplayer.getParent() != null)
                                ((ViewGroup) vplayer.getParent()).removeAllViews();
                            vplayer.stop();
                            vplayer.release();
                            mVideoShowBg.setVisibility(View.VISIBLE);
                        }

                    } else if (currentItem == 1 && (vplayer.isPlay() || vplayer.getStatus() == PlayStateParams.STATE_PREPARE)) {
                        if (vplayer.getParent() != null)
                            ((ViewGroup) vplayer.getParent()).removeAllViews();
                        mSmallScreen.addView(vplayer);
                        vplayer.setShowContoller(false);
                        mSmallLayout.setVisibility(View.VISIBLE);
                        mDetailVideo.removeAllViews();
                    }
                    break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mNewsDetailVideoAty = (NewsDetailVideoAty) activity;
        vplayer = mNewsDetailVideoAty.vPlayPlayer;
        mNewsDetailVideoAty.setHandler(mHandler);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (vplayer != null) {
            vplayer.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                FrameLayout frameLayout = (FrameLayout) vplayer.getParent();
                if (frameLayout != null) {
                    frameLayout.removeAllViews();
                }
                mFullScreen.setVisibility(View.GONE);
                mDetailContainer.setVisibility(View.VISIBLE);
                mDetailVideo.addView(vplayer);
                if (vplayer.getStatus() != PlayStateParams.STATE_PAUSED)
                    vplayer.showBottomControl(false);
                mDetailVideo.setVisibility(View.VISIBLE);

            } else {
                mDetailContainer.setVisibility(View.GONE);
                FrameLayout frameLayout = (FrameLayout) vplayer.getParent();
                if (frameLayout != null) {
                    frameLayout.removeAllViews();
                }
                mDetailVideo.setVisibility(View.GONE);
                mFullScreen.addView(vplayer);

                if (vplayer.getStatus() != PlayStateParams.STATE_PAUSED)
                    vplayer.showBottomControl(false);
                mFullScreen.setVisibility(View.VISIBLE);
            }
        } else
            mDetailContainer.setVisibility(View.VISIBLE);

    }

    public void setIsShowImagesSimpleDraweeViewURI(ImageView draweeView, String strImg) {
        if (!TextUtil.isEmptyString(strImg)) {
            if (SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)) {
                draweeView.setImageResource(R.drawable.bg_load_default_small);
            } else {
                Uri uri = Uri.parse(strImg);
                Glide.with(mContext).load(uri).placeholder(R.drawable.bg_load_default_small).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(draweeView);
            }
        }
    }
}
