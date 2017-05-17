package com.news.sdk.pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.news.sdk.R;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.NewsTopic;
import com.news.sdk.entity.TopicBaseInfo;
import com.news.sdk.entity.TopicClass;
import com.news.sdk.net.volley.NewsTopicRequestGet;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.widget.NewsTopicHeaderView;
import com.news.sdk.widget.SharePopupWindow;
import com.news.sdk.widget.TextViewExtend;

import java.util.ArrayList;
import java.util.HashMap;

public class NewsTopicAty extends BaseActivity implements View.OnClickListener, SharePopupWindow.ShareDismiss {

    public static final int REQUEST_CODE = 1006;
    public static final String KEY_NID = "key_nid";
    private RelativeLayout bgLayout;
    private int mtid;
    private long mFirstClickTime;
    private ExpandableSpecialListViewAdapter mAdapter;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    private Context mContext;
    private ImageView mTopicLeftBack, mNewsLoadingImg;
    private TextView mTopicRightMore;
    private View mNewsDetailLoaddingWrapper;
    private PullToRefreshExpandableListView mlvSpecialNewsFeed;
    private ExpandableListView mExpandableListView;
    private boolean isListRefresh;
    private TopicBaseInfo mTopicBaseInfo;
    private ArrayList<TopicClass> marrTopicClass;
    private NewsTopic mNewTopic;
    private View mDetailView;
    private ImageView mivShareBg;
    private Handler mHandler;
    private SharePopupWindow mSharePopupWindow;
    private SharedPreferences mSharedPreferences;
    private int mScreenWidth, mCardWidth, mCardHeight;
    private NewsTopicHeaderView mSpecialNewsHeaderView;
    private TextView mTopicTitle;
    private RelativeLayout mTopicHeader;
    private NewsFeed mUsedNewsFeed;
    long lastTime, nowTime;
    private String mSource;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_news_topic);
        mContext = this;
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
    }

    @Override
    protected void initializeViews() {
        mtid = getIntent().getIntExtra(KEY_NID, 0);
        mSource = getIntent().getStringExtra(CommonConstant.KEY_SOURCE);
        mUsedNewsFeed = (NewsFeed) getIntent().getSerializableExtra(NewsCommentFgt.KEY_NEWS_FEED);
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
        mAdapter = new ExpandableSpecialListViewAdapter(this);
        mHandler = new Handler();
        mDetailView = findViewById(R.id.mDetailWrapper);
        mSpecialNewsHeaderView = new NewsTopicHeaderView(this);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mTopicHeader = (RelativeLayout) findViewById(R.id.mTopicHeader);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mTopicTitle = (TextView) findViewById(R.id.mTopicTitle);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        mlvSpecialNewsFeed = (PullToRefreshExpandableListView) findViewById(R.id.news_Topic_listView);
        TextUtil.setLayoutBgColor(mContext, mTopicHeader, R.color.color6);
        TextUtil.setTextColor(mContext, mTopicTitle, R.color.color2);
        mlvSpecialNewsFeed.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mlvSpecialNewsFeed.setMainFooterView(true);
        mExpandableListView = mlvSpecialNewsFeed.getRefreshableView();
        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.addHeaderView(mSpecialNewsHeaderView);
        mlvSpecialNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ExpandableListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                isListRefresh = true;
//                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                isListRefresh = true;
//                loadData();
            }
        });
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub
                return true;
            }
        });
//        mlvSpecialNewsFeed.getRefreshableView().addHeaderView(mSpecialNewsHeaderView);
        mTopicLeftBack = (ImageView) findViewById(R.id.mTopicLeftBack);
        mTopicLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTopicRightMore = (TextView) findViewById(R.id.mTopicRightMore);
        mTopicRightMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUsedNewsFeed != null) {
                    mivShareBg.startAnimation(mAlphaAnimationIn);
                    mivShareBg.setVisibility(View.VISIBLE);
                    mSharePopupWindow = new SharePopupWindow(NewsTopicAty.this, NewsTopicAty.this);
                    mSharePopupWindow.setTopic(true);
                    mSharePopupWindow.setTitleAndNid(mUsedNewsFeed.getTitle(), mUsedNewsFeed.getNid(), mUsedNewsFeed.getDescr());
                    mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }
        String requestUrl = HttpConstant.URL_NEWS_TOPIC + "tid=" + mtid;
        if (NetUtil.checkNetWork(mContext)) {
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            NewsTopicRequestGet<NewsTopic> topicRequestGet = new NewsTopicRequestGet<>(Request.Method.GET, new TypeToken<NewsTopic>() {
            }.getType(), requestUrl, new Response.Listener<NewsTopic>() {

                @Override
                public void onResponse(final NewsTopic result) {
                    mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                    mNewTopic = result;
                    mTopicBaseInfo = mNewTopic.getTopicBaseInfo();
                    mTopicTitle.setText(mTopicBaseInfo.getName());
                    marrTopicClass = mNewTopic.getTopicClass();
                    for (int i = 0; i < marrTopicClass.size(); i++) {
                        mExpandableListView.expandGroup(i);
                    }
                    mSpecialNewsHeaderView.setHeaderViewData(mTopicBaseInfo, mScreenWidth);
                    mAdapter.setTopicData(marrTopicClass);
                    mAdapter.notifyDataSetChanged();
                    bgLayout.setVisibility(View.GONE);
                    LogUtil.userClickLog(mUsedNewsFeed, NewsTopicAty.this, mSource);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mNewsLoadingImg.setVisibility(View.VISIBLE);
                    bgLayout.setVisibility(View.GONE);
                }
            });
            HashMap<String, String> header = new HashMap<>();
//            header.put("Authorization", SharedPreManager.mInstance(mContext).getUser(mContext).getAuthorToken());
            topicRequestGet.setRequestHeader(header);
            topicRequestGet.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(topicRequestGet);
        } else {
            mNewsDetailLoaddingWrapper.setVisibility(View.VISIBLE);
            mNewsLoadingImg.setVisibility(View.VISIBLE);
            setRefreshComplete();
//            ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
//            if (TextUtil.isListEmpty(newsFeeds)) {
//                mHomeRetry.setVisibility(View.VISIBLE);
//            } else {
//                mHomeRetry.setVisibility(View.GONE);
//            }
//            mAdapter.setNewsFeed(newsFeeds);
//            mAdapter.notifyDataSetChanged();
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setRefreshComplete() {
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                mlvSpecialNewsFeed.onRefreshComplete();
////            }
//        }, 500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.mLoginWeibo:
//                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
//                    return;
//                }
//                mFirstClickTime = System.currentTimeMillis();
//                break;
//            case R.id.mLoginWeixin:
//                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
//                    return;
//                }
//                mFirstClickTime = System.currentTimeMillis();
//                break;
//            case R.id.mLoginCancel:
//                this.finish();
//                break;
//            case R.id.mLoginSetting:
//                Intent settingAty = new Intent(this, SettingAty.class);
//                startActivity(settingAty);
//                this.finish();
//                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.onPageStart(CommonConstant.LOG_PAGE_TOPICPAGE);
        lastTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        LogUtil.onPageEnd(CommonConstant.LOG_PAGE_TOPICPAGE);
        nowTime = System.currentTimeMillis();
        //上报日志
        LogUtil.userReadLog(mUsedNewsFeed, this, lastTime, nowTime, "100%");
        super.onPause();
    }

    @Override
    public void shareDismiss() {
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mivShareBg.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onThemeChanged() {

    }

    public class ExpandableSpecialListViewAdapter extends BaseExpandableListAdapter {

        private Context mContext;
        private ArrayList<TopicClass> arrTopicClass;
        private int currentType;

        public ExpandableSpecialListViewAdapter(Context context) {
            mContext = context;
        }

        public void setTopicData(ArrayList<TopicClass> topicClass) {
            arrTopicClass = topicClass;
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return arrTopicClass == null ? 0 : arrTopicClass.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return arrTopicClass == null ? 0 : arrTopicClass.get(groupPosition).getNewsFeed().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            NewsFeed feed = arrTopicClass.get(groupPosition).getNewsFeed().get(childPosition);
            int type = feed.getStyle();
            if (0 == type) {
                return NewsFeed.NO_PIC;
            } else if (1 == type || 2 == type) {
                return NewsFeed.ONE_AND_TWO_PIC;
            } else if (3 == type) {
                return NewsFeed.THREE_PIC;
            } else if (11 == type || 12 == type || 13 == type) {
                return NewsFeed.BIG_PIC;
            } else {
                return NewsFeed.EMPTY;
            }
        }

        @Override
        public int getChildTypeCount() {
            return 5;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            TopicClass.TopicClassBaseInfo topicClassBaseInfo = arrTopicClass.get(groupPosition).getTopicClassBaseInfo();
            final GroupHolder groupHolder;
            if (convertView == null || convertView.getTag().getClass() != GroupHolder.class) {
                groupHolder = new GroupHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_topic_group, null, false);
                groupHolder.ivColor = (ImageView) convertView.findViewById(R.id.mGroupColor);
                groupHolder.tvTitle = (TextView) convertView.findViewById(R.id.mGroupTitle);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            TextUtil.setLayoutBgResource(mContext, (ImageView) convertView.findViewById(R.id.line_bottom_imageView), R.drawable.list_divider);
            TextUtil.setLayoutBgColor(mContext, (LinearLayout) convertView.findViewById(R.id.content_layout), R.color.white);
            groupHolder.tvTitle.setText(topicClassBaseInfo.getName());
            TextUtil.setTextColor(mContext, groupHolder.tvTitle, R.color.new_color3);
            groupHolder.ivColor.setBackgroundColor(getResources().getColor(R.color.new_color2));
            convertView.setOnClickListener(null);
            return convertView;
        }


        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            NewsFeed feed = arrTopicClass.get(groupPosition).getNewsFeed().get(childPosition);
            ChildNoPicHolder childNoPicHolderHolder;
            final ChildOnePicHolder childOnePicHolder;
            ChildThreePicHolder childThreePicHolder;
            ChildBigPicHolder childBigPicHolder;
            View vNoPic;
            View vOnePic;
            View vThreePic;
            View vBigPic;
            View vEmpty;
            currentType = getChildType(groupPosition, childPosition);
            if (currentType == NewsFeed.NO_PIC) {
                if (convertView == null) {
                    childNoPicHolderHolder = new ChildNoPicHolder();
                    vNoPic = LayoutInflater.from(mContext).inflate(R.layout.qd_ll_news_item_no_pic, null);
                    childNoPicHolderHolder.rlContent = (RelativeLayout) vNoPic.findViewById(R.id.news_content_relativeLayout);
                    childNoPicHolderHolder.tvTitle = (TextView) vNoPic.findViewById(R.id.title_textView);
                    childNoPicHolderHolder.tvSource = (TextViewExtend) vNoPic.findViewById(R.id.news_source_TextView);
                    childNoPicHolderHolder.tvCommentNum = (TextViewExtend) vNoPic.findViewById(R.id.comment_num_textView);
                    childNoPicHolderHolder.tvType = (TextViewExtend) vNoPic.findViewById(R.id.type_textView);
                    childNoPicHolderHolder.ivDelete = (ImageView) vNoPic.findViewById(R.id.delete_imageView);
                    childNoPicHolderHolder.ivBottomLine = (ImageView) vNoPic.findViewById(R.id.line_bottom_imageView);
                    vNoPic.setTag(childNoPicHolderHolder);
                    convertView = vNoPic;
                } else {
                    childNoPicHolderHolder = (ChildNoPicHolder) convertView.getTag();
                }
                setTitleTextBySpannable(childNoPicHolderHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childNoPicHolderHolder.tvSource, feed.getPname());
                setCommentViewText(childNoPicHolderHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childNoPicHolderHolder.rlContent, feed);
                newsTag(childNoPicHolderHolder.tvType, feed.getRtype());
                childNoPicHolderHolder.ivDelete.setVisibility(View.GONE);
                setBottomLine(childNoPicHolderHolder.ivBottomLine, getChildrenCount(groupPosition), childPosition);
            } else if (currentType == NewsFeed.ONE_AND_TWO_PIC) {
                if (convertView == null) {
                    childOnePicHolder = new ChildOnePicHolder();
                    vOnePic = LayoutInflater.from(mContext).inflate(R.layout.qd_ll_news_item_one_pic, null);
                    childOnePicHolder.ivPicture = (ImageView) vOnePic.findViewById(R.id.title_img_View);
                    childOnePicHolder.rlContent = (RelativeLayout) vOnePic.findViewById(R.id.news_content_relativeLayout);
                    childOnePicHolder.tvTitle = (TextView) vOnePic.findViewById(R.id.title_textView);
                    childOnePicHolder.tvSource = (TextViewExtend) vOnePic.findViewById(R.id.news_source_TextView);
                    childOnePicHolder.tvCommentNum = (TextViewExtend) vOnePic.findViewById(R.id.comment_num_textView);
                    childOnePicHolder.tvType = (TextViewExtend) vOnePic.findViewById(R.id.type_textView);
                    childOnePicHolder.ivDelete = (ImageView) vOnePic.findViewById(R.id.delete_imageView);
                    childOnePicHolder.llSourceOnePic = (LinearLayout) vOnePic.findViewById(R.id.source_content_linearLayout);
                    childOnePicHolder.ivBottomLine = (ImageView) vOnePic.findViewById(R.id.line_bottom_imageView);
                    childOnePicHolder.ivBottomLine = (ImageView) vOnePic.findViewById(R.id.line_bottom_imageView);
                    vOnePic.setTag(childOnePicHolder);
                    convertView = vOnePic;
                } else {
                    childOnePicHolder = (ChildOnePicHolder) convertView.getTag();
                }
                RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) childOnePicHolder.ivPicture.getLayoutParams();
                lpCard.width = mCardWidth;
                lpCard.height = mCardHeight;
                childOnePicHolder.ivPicture.setLayoutParams(lpCard);
                setImageUri(childOnePicHolder.ivPicture, feed.getImgs().get(0), mCardWidth, mCardHeight, feed.getRtype());
                setTitleTextBySpannable(childOnePicHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childOnePicHolder.tvSource, feed.getPname());
                setCommentViewText(childOnePicHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childOnePicHolder.rlContent, feed);
                newsTag(childOnePicHolder.tvType, feed.getRtype());
                childOnePicHolder.ivDelete.setVisibility(View.GONE);
                setBottomLine(childOnePicHolder.ivBottomLine, getChildrenCount(groupPosition), childPosition);
            } else if (currentType == NewsFeed.THREE_PIC) {
                if (convertView == null) {
                    childThreePicHolder = new ChildThreePicHolder();
                    vThreePic = LayoutInflater.from(mContext).inflate(R.layout.qd_ll_news_card, null);
                    childThreePicHolder.ivPicture1 = (ImageView) vThreePic.findViewById(R.id.image_card1);
                    childThreePicHolder.ivPicture2 = (ImageView) vThreePic.findViewById(R.id.image_card2);
                    childThreePicHolder.ivPicture3 = (ImageView) vThreePic.findViewById(R.id.image_card3);
                    childThreePicHolder.rlContent = (RelativeLayout) vThreePic.findViewById(R.id.news_content_relativeLayout);
                    childThreePicHolder.tvTitle = (TextView) vThreePic.findViewById(R.id.title_textView);
                    childThreePicHolder.tvSource = (TextViewExtend) vThreePic.findViewById(R.id.news_source_TextView);
                    childThreePicHolder.tvCommentNum = (TextViewExtend) vThreePic.findViewById(R.id.comment_num_textView);
                    childThreePicHolder.tvType = (TextViewExtend) vThreePic.findViewById(R.id.type_textView);
                    childThreePicHolder.ivDelete = (ImageView) vThreePic.findViewById(R.id.delete_imageView);
                    childThreePicHolder.ivBottomLine = (ImageView) vThreePic.findViewById(R.id.line_bottom_imageView);
                    vThreePic.setTag(childThreePicHolder);
                    convertView = vThreePic;
                } else {
                    childThreePicHolder = (ChildThreePicHolder) convertView.getTag();
                }
                ArrayList<String> strArrImgUrl = feed.getImgs();
                setCardMargin(childThreePicHolder.ivPicture1, 15, 1, 3);
                setCardMargin(childThreePicHolder.ivPicture2, 1, 1, 3);
                setCardMargin(childThreePicHolder.ivPicture3, 1, 15, 3);
                setImageUri(childThreePicHolder.ivPicture1, strArrImgUrl.get(0), mCardWidth, mCardHeight, feed.getRtype());
                setImageUri(childThreePicHolder.ivPicture2, strArrImgUrl.get(1), mCardWidth, mCardHeight, feed.getRtype());
                setImageUri(childThreePicHolder.ivPicture3, strArrImgUrl.get(2), mCardWidth, mCardHeight, feed.getRtype());
                setTitleTextBySpannable(childThreePicHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childThreePicHolder.tvSource, feed.getPname());
                setCommentViewText(childThreePicHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childThreePicHolder.rlContent, feed);
                newsTag(childThreePicHolder.tvType, feed.getRtype());
                childThreePicHolder.ivDelete.setVisibility(View.GONE);
                setBottomLine(childThreePicHolder.ivBottomLine, getChildrenCount(groupPosition), childPosition);
            } else if (currentType == NewsFeed.BIG_PIC) {
                if (convertView == null) {
                    childBigPicHolder = new ChildBigPicHolder();
                    vBigPic = LayoutInflater.from(mContext).inflate(R.layout.ll_news_big_pic2, null);
                    childBigPicHolder.ivBigPicture = (ImageView) vBigPic.findViewById(R.id.title_img_View);
                    childBigPicHolder.rlContent = (RelativeLayout) vBigPic.findViewById(R.id.news_content_relativeLayout);
                    childBigPicHolder.tvTitle = (TextView) vBigPic.findViewById(R.id.title_textView);
                    childBigPicHolder.tvSource = (TextViewExtend) vBigPic.findViewById(R.id.news_source_TextView);
                    childBigPicHolder.tvCommentNum = (TextViewExtend) vBigPic.findViewById(R.id.comment_num_textView);
                    childBigPicHolder.tvType = (TextViewExtend) vBigPic.findViewById(R.id.type_textView);
                    childBigPicHolder.ivDelete = (ImageView) vBigPic.findViewById(R.id.delete_imageView);
                    vBigPic.setTag(childBigPicHolder);
                    convertView = vBigPic;
                } else {
                    childBigPicHolder = (ChildBigPicHolder) convertView.getTag();
                }
                ArrayList<String> strArrBigImgUrl = feed.getImgs();
                int with = mScreenWidth - DensityUtil.dip2px(mContext, 30);
                int num = feed.getStyle() - 11;
                RelativeLayout.LayoutParams lpBig = (RelativeLayout.LayoutParams) childBigPicHolder.ivBigPicture.getLayoutParams();
                lpBig.width = with;
                lpBig.height = (int) (with * 9 / 16.0f);
                childBigPicHolder.ivBigPicture.setLayoutParams(lpBig);
                setImageUri(childBigPicHolder.ivBigPicture, strArrBigImgUrl.get(num), with, (int) (with * 9 / 16.0f), feed.getRtype());
                setTitleTextBySpannable(childBigPicHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childBigPicHolder.tvSource, feed.getPname());
                setCommentViewText(childBigPicHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childBigPicHolder.rlContent, feed);
                newsTag(childBigPicHolder.tvType, feed.getRtype());
                childBigPicHolder.ivDelete.setVisibility(View.GONE);
            } else if (currentType == NewsFeed.EMPTY) {
                if (convertView == null) {
                    childNoPicHolderHolder = new ChildNoPicHolder();
                    vEmpty = LayoutInflater.from(mContext).inflate(R.layout.ll_news_item_empty, null);
                    childNoPicHolderHolder.rlContent = (RelativeLayout) vEmpty.findViewById(R.id.news_content_relativeLayout);
                    vEmpty.setTag(childNoPicHolderHolder);
                    convertView = vEmpty;
                } else {
                    childNoPicHolderHolder = (ChildNoPicHolder) convertView.getTag();
                }
                childNoPicHolderHolder.rlContent.setVisibility(View.GONE);
            }
            return convertView;
        }

        private void setImageUri(ImageView draweeView, String strImg, int width, int height, int rType) {
            if (!TextUtil.isEmptyString(strImg)) {
                Uri uri;
                if (rType != 3 && rType != 4 && rType != 6 && rType != 50 && rType != 51) {
                    String img = strImg.replace("bdp-", "pro-");
                    uri = Uri.parse(img + "@1e_1c_0o_0l_100sh_" + height + "h_" + width + "w_95q.jpg");
                } else {
                    uri = Uri.parse(strImg);
                }
                if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
                    draweeView.setAlpha(0.5f);
                } else {
                    draweeView.setAlpha(1.0f);
                }
                Glide.with(mContext).load(uri).placeholder(R.drawable.bg_load_default_small).into(draweeView);
            }
        }

        private void setCardMargin(ImageView ivCard, int leftMargin, int rightMargin, int pageNum) {
            LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) ivCard.getLayoutParams();
            localLayoutParams.leftMargin = DensityUtil.dip2px(mContext, leftMargin);
            localLayoutParams.rightMargin = DensityUtil.dip2px(mContext, rightMargin);
            int width = (int) (mScreenWidth / 2.0f - DensityUtil.dip2px(mContext, 15));
            if (pageNum == 2) {
                localLayoutParams.width = width;
                localLayoutParams.height = (int) (width * 74 / 102f);
            } else if (pageNum == 3) {
                localLayoutParams.width = mCardWidth;
                localLayoutParams.height = mCardHeight;
            }
            ivCard.setLayoutParams(localLayoutParams);
        }

        private void setTitleTextBySpannable(TextView tvTitle, String strTitle, boolean isRead) {
            tvTitle.setMaxLines(2);
            if (strTitle != null && !"".equals(strTitle)) {
                tvTitle.setText(strTitle);
                tvTitle.setLineSpacing(0, 1.1f);
                if (isRead) {
                    TextUtil.setTextColor(mContext, tvTitle, R.color.new_color7);
                } else {
                    TextUtil.setTextColor(mContext, tvTitle, R.color.newsFeed_titleColor);
                }
                tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
            }
        }

        private void setBottomLine(ImageView ivBottom, int count, int position) {
            TextUtil.setLayoutBgResource(mContext, ivBottom, R.drawable.list_divider);
            if (count == position + 1) {//去掉最后一条的线
                ivBottom.setVisibility(View.INVISIBLE);
            } else {
                ivBottom.setVisibility(View.VISIBLE);
            }
        }

        private void setNewsContentClick(RelativeLayout rlNewsContent, final NewsFeed feed) {
            TextUtil.setLayoutBgResource(mContext, rlNewsContent, R.drawable.bg_feed_list_select);
            rlNewsContent.setOnClickListener(new View.OnClickListener() {
                long firstClick = 0;

                public void onClick(View paramAnonymousView) {
                    if (System.currentTimeMillis() - firstClick <= 1500L) {
                        firstClick = System.currentTimeMillis();
                        return;
                    }
                    firstClick = System.currentTimeMillis();
                    if (feed.getRtype() == 3) {
                        Intent AdIntent = new Intent(mContext, NewsDetailWebviewAty.class);
                        AdIntent.putExtra(NewsDetailWebviewAty.KEY_URL, feed.getPurl());
                        startActivity(AdIntent);
                    } else {
                        Intent intent = new Intent(mContext, NewsDetailAty2.class);
                        intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
                        intent.putExtra(CommonConstant.KEY_SOURCE, CommonConstant.LOG_CLICK_TOPIC_SOURCE);
                        ArrayList<String> imageList = feed.getImgs();
                        if (imageList != null && imageList.size() != 0) {
                            intent.putExtra(NewsFeedFgt.KEY_NEWS_IMAGE, imageList.get(0));
                        }
                        startActivity(intent);
                    }
                }
            });
        }

        private void setSourceViewText(TextViewExtend textView, String strText) {
            if (!TextUtil.isEmptyString(strText)) {
                textView.setText(strText);
                TextUtil.setTextColor(mContext, textView, R.color.new_color3);
            }
        }

        private void setCommentViewText(TextViewExtend textView, String strText) {
            textView.setVisibility(View.GONE);
            textView.setText(TextUtil.getCommentNum(strText));
            TextUtil.setTextColor(mContext, textView, R.color.new_color3);
        }

        private void setBottomLineColor(ImageView imageView) {
            TextUtil.setLayoutBgResource(mContext, imageView, R.drawable.list_divider);
        }

        private void newsTag(TextViewExtend tag, int type) {
            String content = "";
            if (type == 1) {
                if (tag.getVisibility() == View.GONE) {
                    tag.setVisibility(View.VISIBLE);
                }
                content = "热点";
                tag.setTextColor(mContext.getResources().getColor(R.color.newsfeed_red));
                tag.setBackgroundResource(R.drawable.newstag_hotspot_shape);
            } else if (type == 2) {
                if (tag.getVisibility() == View.GONE) {
                    tag.setVisibility(View.VISIBLE);
                }
                content = "推送";
                tag.setTextColor(mContext.getResources().getColor(R.color.color1));
                tag.setBackgroundResource(R.drawable.newstag_push_shape);
            } else if (type == 3) {
                if (tag.getVisibility() == View.GONE) {
                    tag.setVisibility(View.VISIBLE);
                }
                content = "广告";
                tag.setTextColor(mContext.getResources().getColor(R.color.theme_color));
                tag.setBackgroundResource(R.drawable.newstag_ad_shape);
            } else {
                if (tag.getVisibility() == View.VISIBLE) {
                    tag.setVisibility(View.GONE);
                }
                return;
            }
            tag.setText(content);
            tag.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tag.getLayoutParams();
            params.width = DensityUtil.dip2px(mContext, 20);
            params.height = DensityUtil.dip2px(mContext, 11);
            tag.setLayoutParams(params);
        }

        class GroupHolder {
            TextView tvTitle;
            TextViewExtend tvSource;
            ImageView ivColor;
        }

        class ChildNoPicHolder {
            TextView tvTitle;
            TextViewExtend tvSource;
            TextViewExtend tvCommentNum;
            TextViewExtend tvType;
            RelativeLayout rlContent;
            ImageView ivDelete;
            ImageView ivBottomLine;
        }

        class ChildOnePicHolder extends ChildNoPicHolder {
            ImageView ivPicture;
            LinearLayout llSourceOnePic;
            ImageView ivBottomLine;
        }

        class ChildThreePicHolder extends ChildNoPicHolder {
            ImageView ivPicture1;
            ImageView ivPicture2;
            ImageView ivPicture3;
        }

        class ChildBigPicHolder extends ChildNoPicHolder {
            ImageView ivBigPicture;
        }
    }
}
