package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkRequest;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.adapter.SearchListViewOpenAdapter;
import com.news.yazhidao.adapter.SearchListViewOpenAdapter.onFocusItemClick;
import com.news.yazhidao.adapter.SearchListViewOpenAdapter.onSearchListViewOpenItemClick;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AttentionListEntity;
import com.news.yazhidao.entity.Element;
import com.news.yazhidao.entity.HistoryEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.net.volley.GsonRequest;
import com.news.yazhidao.net.volley.SearchRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.HotLabelsLayout;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/10/29.
 */
public class TopicSearchAty extends BaseActivity implements View.OnClickListener {

    public final static String KEY_NOT_NEED_OPEN_HOME_ATY = "KEY_NOT_NEED_OPEN_HOME_ATY";
    /**
     * 标签页的容量
     */
    private final static int PAGE_CAPACITY = 10;

    private TextView mSearchLeftBack;
    private EditText mSearchContent;
    private View mSearchClear;
    private TextView mDoSearch;
    private HotLabelsLayout mHotLabelsLayout;
    private TextView mDoSearchChangeBatch;
    private PullToRefreshListView mSearchListView, mSearchListViewOpen;
    private View mSearchHotLabelLayout;
    private NewsFeedAdapter mNewsFeedAdapter;
    private View mSearchLoaddingWrapper;
    private ImageView mSearchTipImg;
    private TextView mSearchTip;
    //    private ProgressBar mSearchProgress;
    private ArrayList<Element> mHotLabels;
    private ArrayList<NewsFeed> mNewsFeedLists = new ArrayList<>();
    private int mTotalPage;
    private int mCurrPageIndex;
    private String mKeyWord;
    private int mPageIndex = 1;//搜索index
    private SearchListViewOpenAdapter mSearchListViewOpenAdapter;
    private ArrayList<HistoryEntity> historyEntities = new ArrayList<HistoryEntity>();
    private RelativeLayout HistoryLayout, HotSearchlayout;
    private View mBgLayout;
    private boolean misPullUpToRefresh = false;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_topic_search);
    }

    @Override
    protected void initializeViews() {
        mSearchLeftBack = (TextView) findViewById(R.id.mSearchLeftBack);
        mSearchLeftBack.setOnClickListener(this);
        mSearchContent = (EditText) findViewById(R.id.mSearchContent);
        mSearchContent.addTextChangedListener(new TopicTextWatcher());
        mSearchClear = findViewById(R.id.mSearchClear);
        mSearchClear.setOnClickListener(this);
        mDoSearch = (TextView) findViewById(R.id.mDoSearch);

        mBgLayout = findViewById(R.id.bgLayout);
        mSearchLoaddingWrapper = findViewById(R.id.mSearchLoaddingWrapper);
        mSearchTipImg = (ImageView) findViewById(R.id.mSearchTipImg);
        mSearchTip = (TextView) findViewById(R.id.mSearchTip);
//        mSearchProgress = (ProgressBar) findViewById(R.id.mSearchProgress);
        mNewsFeedAdapter = new NewsFeedAdapter(this, null, null);
        mNewsFeedAdapter.isFavoriteList();
        mSearchListView = (PullToRefreshListView) findViewById(R.id.mSearchListView);
        mSearchListViewOpen = (PullToRefreshListView) findViewById(R.id.mSearchListViewOpen);
        mSearchListView.setAdapter(mNewsFeedAdapter);
        mSearchListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mSearchListViewOpen.setMode(PullToRefreshBase.Mode.DISABLED);

        addListViewHFView();
        mSearchListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                misPullUpToRefresh = true;
                loadNewsData(mKeyWord, ++mPageIndex + "");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mSearchLeftBack) {
            finish();

        } else if (v.getId() == R.id.mSearchClear) {
            mSearchContent.setText("");
            isVisity(false);
        } else if (v.getId() == R.id.mDoSearch) {
            if (mKeyWord != null && !mKeyWord.equals("")) {
                hideKeyboard(v);
                mSearchTip.setText("暂无搜索结果");
                SharedPreManager.HistorySave(mKeyWord);
                try {
                    historyEntities = SharedPreManager.HistoryGetList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isVisity(true);
                mSearchListViewOpenAdapter.setNewsFeed(historyEntities);
                mSearchListViewOpenAdapter.notifyDataSetChanged();
                mPageIndex = 1;
                loadNewsData(mKeyWord, mPageIndex + "");
            } else {
                ToastUtil.toastShort("请您输入搜索关键词");
            }
        } else if (v.getId() == R.id.mDoSearchChangeBatch) {
            setHotLabelLayoutData(mCurrPageIndex++);
        }
//        switch (v.getId()) {
//            case R.id.mSearchLeftBack:
//                finish();
//                break;
//            case R.id.mSearchClear:
//                mSearchContent.setText("");
//                isVisity(false);
//                break;
//            case R.id.mDoSearch:
//                if(mKeyWord!=null&&!mKeyWord.equals("")) {
//                    hideKeyboard(v);
//                    mSearchTip.setText("暂无搜索结果");
//                    SharedPreManager.HistorySave(mKeyWord);
//                    try {
//                        historyEntities = SharedPreManager.HistoryGetList();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    isVisity(true);
//                    mSearchListViewOpenAdapter.setNewsFeed(historyEntities);
//                    mSearchListViewOpenAdapter.notifyDataSetChanged();
//                    mPageIndex = 1;
//                    loadNewsData(mKeyWord, mPageIndex + "");
//                }else {
//                    ToastUtil.toastShort("请您输入搜索关键词");
//                }
//                break;
//            case R.id.mDoSearchChangeBatch:
//                setHotLabelLayoutData(mCurrPageIndex++);
//                break;
//        }
    }

    /**
     * 获取新闻数据
     */
    private void loadNewsData(String pKeyWord, final String pPageIndex) {
        System.out.println("search=====:keyword:" + pKeyWord + "pageIndex:" + pPageIndex + "Url:" + HttpConstant.URL_SEARCH + "?keywords=" + pKeyWord + "&p=" + pPageIndex);
        if (!misPullUpToRefresh)
            mBgLayout.setVisibility(View.VISIBLE);
        String keyWord = "";
        try {
            keyWord = URLEncoder.encode(pKeyWord, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(TopicSearchAty.this);
        SearchRequest<ArrayList<NewsFeed>> searchRequest = new SearchRequest<>(Request.Method.GET, new TypeToken<ArrayList<NewsFeed>>() {
        }.getType(), HttpConstant.URL_SEARCH + "?keywords=" + keyWord + "&p=" + pPageIndex, new Response.Listener<ArrayList<NewsFeed>>() {

            @Override
            public void onResponse(ArrayList<NewsFeed> response) {
//                System.out.println("search=========sucessful:"+response.toString());
                mSearchListView.onRefreshComplete();
                mBgLayout.setVisibility(View.GONE);
                misPullUpToRefresh = false;
                if (!TextUtil.isListEmpty(response)) {
                    mSearchLoaddingWrapper.setVisibility(View.GONE);
                    if (pPageIndex.equals("1")) {
                        mNewsFeedLists.removeAll(mNewsFeedLists);

                    }
                    mNewsFeedLists.addAll(response);
                    if (pPageIndex.equals("1")) {
                        setAttentionDate();
                    }
                    mNewsFeedAdapter.setSearchKeyWord(mKeyWord);

                    mNewsFeedAdapter.setNewsFeed(mNewsFeedLists);
                    mNewsFeedAdapter.notifyDataSetChanged();

                } else {
                    Logger.e("jigang", "response is null");
                    misPullUpToRefresh = false;
                    if (mPageIndex > 1) {
                        ToastUtil.toastShort("没有更多数据");
                    } else {
                        ToastUtil.toastShort("没有搜索到与\"" + mKeyWord + "\"相关的数据");
                        mSearchTipImg.setVisibility(View.VISIBLE);
                        mSearchTip.setVisibility(View.VISIBLE);
//                        mSearchProgress.setVisibility(View.GONE);

                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("search=========:" + error.toString());
                misPullUpToRefresh = false;
                mSearchListView.onRefreshComplete();
                Logger.e("jigang", "========" + error.getMessage());
                mSearchTipImg.setVisibility(View.VISIBLE);
                mSearchTip.setVisibility(View.VISIBLE);
                mBgLayout.setVisibility(View.GONE);
            }
        });
//        searchRequest.setKeyWordAndPageIndex(pKeyWord, pPageIndex);
        searchRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));
        requestQueue.add(searchRequest);
    }

    public void setAttentionDate() {
        NewsFeed newsFeed = new NewsFeed();
        ArrayList<AttentionListEntity> attentionListEntities = new ArrayList<AttentionListEntity>();
        for (int i = 0; i < 4; i++) {
            AttentionListEntity attentionListEntity = new AttentionListEntity();
            attentionListEntity.setIcon("http://www.ld12.com/upimg358/20160201/yd33s1bvzmm1543.jpg");
            attentionListEntity.setDescr("音乐风云");
            attentionListEntities.add(attentionListEntity);
        }
        newsFeed.setAttentionListEntities(attentionListEntities);
        newsFeed.setStyle(4);
        if (!TextUtil.isListEmpty(mNewsFeedLists)) {//判断不为空
            if (mNewsFeedLists.size() <= 2) {//判断搜索列表的数量小于等于2
                mNewsFeedLists.add(newsFeed);
            } else {
                mNewsFeedLists.add(2, newsFeed);
            }
        }

    }

    @Override
    protected void loadData() {
        mSearchLoaddingWrapper.setVisibility(View.VISIBLE);
        mSearchTip.setText("暂无热门搜索热词");
        GsonRequest<ArrayList<Element>> hotWordRequest = new GsonRequest<ArrayList<Element>>(Request.Method.POST, new TypeToken<ArrayList<Element>>() {
        }.getType(), "http://121.40.34.56/news/baijia/fetchElementary", new Response.Listener<ArrayList<Element>>() {
            @Override
            public void onResponse(ArrayList<Element> result) {
                mHotLabels = result;
                mBgLayout.setVisibility(View.GONE);
                if (!TextUtil.isListEmpty(mHotLabels)) {
                    int temp = mHotLabels.size() % PAGE_CAPACITY;
                    mTotalPage = (temp == 0) ? mHotLabels.size() / PAGE_CAPACITY : mHotLabels.size() / PAGE_CAPACITY + 1;
                    setHotLabelLayoutData(mCurrPageIndex++);
                    mSearchLoaddingWrapper.setVisibility(View.GONE);
                } else {
                    Logger.e("jigang", "-----No Date ~");
                    mSearchTipImg.setVisibility(View.VISIBLE);
                    mSearchTip.setVisibility(View.VISIBLE);
                    mSearchLoaddingWrapper.setVisibility(View.GONE);
                    HotSearchlayout.setVisibility(View.GONE);
//                    mSearchProgress.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mBgLayout.setVisibility(View.GONE);
                HotSearchlayout.setVisibility(View.GONE);
                mSearchLoaddingWrapper.setVisibility(View.GONE);
                mSearchTipImg.setVisibility(View.VISIBLE);
                mSearchTip.setVisibility(View.VISIBLE);
                HistoryLayout.setVisibility(View.GONE);
            }
        });
        QiDianApplication.getInstance().getRequestQueue().add(hotWordRequest);

    }

    LinearLayout mFootView;

    public void addListViewHFView() {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        ListView lv = mSearchListViewOpen.getRefreshableView();
        LinearLayout headView = (LinearLayout) getLayoutInflater().inflate(R.layout.aty_topic_search_headview, null);
        headView.setLayoutParams(layoutParams);
        mSearchHotLabelLayout = headView.findViewById(R.id.mSearchHotLabelLayout);
        mSearchHotLabelLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View view = getWindow().peekDecorView();
                if (view != null) {
                    mSearchContent.clearFocus();
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return false;
            }
        });
        mDoSearchChangeBatch = (TextView) headView.findViewById(R.id.mDoSearchChangeBatch);
        mHotLabelsLayout = (HotLabelsLayout) headView.findViewById(R.id.mHotLabelsLayout);
        HistoryLayout = (RelativeLayout) headView.findViewById(R.id.HistoryLayout);
        HotSearchlayout = (RelativeLayout) headView.findViewById(R.id.HotSearchlayout);
        mDoSearchChangeBatch.setOnClickListener(this);
        lv.addHeaderView(headView);
        try {
            historyEntities = SharedPreManager.HistoryGetList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e("aaa", "historyEntities============" + historyEntities.toString());
        if (historyEntities.size() == 0) {
            HistoryEntity bean = new HistoryEntity(-1);
            historyEntities.add(bean);
            HistoryLayout.setVisibility(View.GONE);
        } else {
            addListViewFootView();
        }

        mSearchListViewOpenAdapter = new SearchListViewOpenAdapter(TopicSearchAty.this);
        mSearchListViewOpenAdapter.setOnSearchListViewOpenItemClick(mOnSearchListViewOpenItemClick);
        mSearchListViewOpenAdapter.setOnFocusItemClick(mOnFocusItemClick);
        mSearchListViewOpenAdapter.setNewsFeed(historyEntities);
        mSearchListViewOpen.setAdapter(mSearchListViewOpenAdapter);
    }

    onSearchListViewOpenItemClick mOnSearchListViewOpenItemClick = new onSearchListViewOpenItemClick() {
        @Override
        public void listener(String content) {
            mSearchContent.setText(content);


//            hideKeyboard(view);
            mSearchTip.setText("暂无搜索结果");
            SharedPreManager.HistorySave(mKeyWord);
            try {
                historyEntities = SharedPreManager.HistoryGetList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isVisity(true);
            mSearchListViewOpenAdapter.setNewsFeed(historyEntities);
            mSearchListViewOpenAdapter.notifyDataSetChanged();
            mPageIndex = 1;
            loadNewsData(mKeyWord, mPageIndex + "");
        }
    };

    onFocusItemClick mOnFocusItemClick = new onFocusItemClick() {
        @Override
        public void listener(HistoryEntity historyEntity) {

        }
    };


    public void setHotLabelLayoutData(int mCurrPageIndex) {
        if (mTotalPage >= 1) {
            mHotLabelsLayout.removeAllViews();
            int index = mCurrPageIndex % mTotalPage;
            List<Element> elements = mHotLabels.subList(index * PAGE_CAPACITY, index * PAGE_CAPACITY + ((index == mTotalPage - 1) ? (mHotLabels.size() % PAGE_CAPACITY) : PAGE_CAPACITY));
            for (int i = 0; i < elements.size(); i++) {
                TextView textView = new TextView(this);
                final Element element = elements.get(i);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int marginLeft = DensityUtil.dip2px(this, 12);
                int marginTop = DensityUtil.dip2px(this, 15);
                lp.setMargins(marginLeft, marginTop, 0, 0);
                textView.setLayoutParams(lp);
                int padding = DensityUtil.dip2px(this, 8);
                int paddingLR = DensityUtil.dip2px(this, 10);
                textView.setPadding(paddingLR, padding, paddingLR, padding);
                textView.setTextColor(getResources().getColor(R.color.bg_share_text));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//                Drawable drawable = getResources().getDrawable(R.drawable.bg_search_hotlabel);
//                drawable.setColorFilter(new
//                        PorterDuffColorFilter(TextUtil.getRandomColor4Hotlabel(this), PorterDuff.Mode.SRC_IN));
                textView.setBackgroundResource(R.drawable.bg_search_hotlabel);
                textView.setText(element.getTitle());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard(v);
                        mSearchContent.setText(element.getTitle());
                        mSearchTip.setText("暂无搜索结果");
                        SharedPreManager.HistorySave(element.getTitle());
                        try {
                            historyEntities = SharedPreManager.HistoryGetList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        isVisity(true);
                        mSearchListViewOpenAdapter.setNewsFeed(historyEntities);
                        mSearchListViewOpenAdapter.notifyDataSetChanged();
                        mPageIndex = 1;
                        loadNewsData(element.getTitle(), mPageIndex + "");
//                        Intent diggerIntent = new Intent(TopicSearchAty.this, DiggerAty.class);
//                        diggerIntent.setType("text/plain");
//                        diggerIntent.putExtra(Intent.EXTRA_TEXT, element.getTitle());
//                        diggerIntent.putExtra(KEY_NOT_NEED_OPEN_HOME_ATY, true);
//                        startActivity(diggerIntent);
                    }
                });
                mHotLabelsLayout.addView(textView);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {


            if (requestCode == NewsFeedAdapter.REQUEST_CODE) {
                int newsId = data.getIntExtra(NewsFeedAdapter.KEY_NEWS_ID, 0);
                Logger.e("jigang", "news nid = " + newsId);
                if (!TextUtil.isListEmpty(mNewsFeedLists)) {
                    for (NewsFeed item : mNewsFeedLists) {
                        if (item != null && newsId == item.getNid()) {
                            item.setRead(true);
                        }
                    }
                    mNewsFeedAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            System.out.println("TopActivity:" + e.toString());
        }
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param view
     */
    private void hideKeyboard(View view) {
        IBinder token = view.getWindowToken();
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private class TopicTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Logger.e("jigang", "s=" + s + ",start=" + start + ",count=" + count + ",after=" + after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Logger.e("jigang", "s=" + s + ",start=" + start + ",before=" + before + ",count=" + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Logger.e("jigang", "s=" + s);
            if (s != null && !TextUtil.isEmptyString(s.toString())) {
                mKeyWord = mSearchContent.getText().toString();
                mSearchClear.setVisibility(View.VISIBLE);
//                mDoSearch.setTextColor(TopicSearchAty.this.getResources().getColor(R.color.do_search_can_do));//搜索文字变色
                mDoSearch.setOnClickListener(TopicSearchAty.this);
            } else {
                mKeyWord = "";
//                isVisity(false);//没有文字隐藏查找的listview等等
                mSearchClear.setVisibility(View.GONE);
//                mDoSearch.setTextColor(TopicSearchAty.this.getResources().getColor(R.color.do_search_not_do));//搜索文字变色
                mDoSearch.setOnClickListener(null);
            }
        }
    }

    /**
     * @param isType true显示搜索结果 false显示搜索界面
     */
    public void isVisity(boolean isType) {
        if (isType) {
            mSearchListViewOpen.setVisibility(View.GONE);
            mSearchListView.setVisibility(View.VISIBLE);
            if (mSearchLoaddingWrapper.getVisibility() == View.GONE) {
                mSearchLoaddingWrapper.setVisibility(View.VISIBLE);
            }
            if (mSearchTipImg.getVisibility() == View.VISIBLE) {
                mSearchTipImg.setVisibility(View.GONE);
            }
            if (mSearchTip.getVisibility() == View.VISIBLE) {
                mSearchTip.setVisibility(View.GONE);
                ;
            }
//            if (mSearchProgress.getVisibility() == View.GONE) {mSearchProgress.setVisibility(View.VISIBLE);}
        } else {
            mSearchListViewOpen.setVisibility(View.VISIBLE);
            mSearchListView.setVisibility(View.GONE);
            if (mSearchLoaddingWrapper.getVisibility() == View.VISIBLE) {
                mSearchLoaddingWrapper.setVisibility(View.GONE);
            }
            if (mSearchTipImg.getVisibility() == View.GONE) {
                mSearchTipImg.setVisibility(View.VISIBLE);
            }
            if (mSearchTip.getVisibility() == View.GONE) {
                mSearchTip.setVisibility(View.VISIBLE);
                ;
            }
//            if (mSearchProgress.getVisibility() == View.VISIBLE) {mSearchProgress.setVisibility(View.GONE);}
        }
        if (historyEntities.size() != 0) {
            if (mFootView == null) {
                addListViewFootView();
            }
            if (HistoryLayout.getVisibility() == View.GONE) {
                HistoryLayout.setVisibility(View.VISIBLE);
            }

        }

    }

    public void addListViewFootView() {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        final ListView lv = mSearchListViewOpen.getRefreshableView();
        mFootView = (LinearLayout) getLayoutInflater().inflate(R.layout.detail_footview_layout, null);
        mFootView.setLayoutParams(layoutParams);
        lv.addFooterView(mFootView);
        TextView footView_text = (TextView) mFootView.findViewById(R.id.footView_text);
        footView_text.setText("清除历史搜索");
        footView_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreManager.Historyremove();
                HistoryLayout.setVisibility(View.GONE);
                historyEntities = new ArrayList<HistoryEntity>();
                HistoryEntity bean = new HistoryEntity(-1);
                historyEntities.add(bean);
                mSearchListViewOpenAdapter.setNewsFeed(historyEntities);
                mSearchListViewOpenAdapter.notifyDataSetChanged();
                lv.removeFooterView(mFootView);
                mFootView = null;
            }
        });
    }
}
