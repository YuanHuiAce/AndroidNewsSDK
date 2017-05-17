package com.news.sdk.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
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
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.sdk.R;
import com.news.sdk.adapter.NewsFeedAdapter;
import com.news.sdk.adapter.SearchListViewOpenAdapter;
import com.news.sdk.adapter.SearchListViewOpenAdapter.onFocusItemClick;
import com.news.sdk.adapter.SearchListViewOpenAdapter.onSearchListViewOpenItemClick;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.AttentionListEntity;
import com.news.sdk.entity.Element;
import com.news.sdk.entity.HistoryEntity;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.SearchRequest;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.GsonUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.HotLabelsLayout;
import com.news.sdk.widget.swipebackactivity.SwipeBackActivity;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * 搜索
 */
public class TopicSearchAty extends SwipeBackActivity implements View.OnClickListener {

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
    private ArrayList<Element> mHotLabels;
    private ArrayList<NewsFeed> mNewsFeedLists = new ArrayList<>();
    private int mTotalPage;
    private int mCurrPageIndex;
    private String mKeyWord;
    private int mPageIndex = 1;//搜索index
    private SearchListViewOpenAdapter mSearchListViewOpenAdapter;
    private ArrayList<HistoryEntity> historyEntities = new ArrayList<HistoryEntity>();
    private RelativeLayout HistoryLayout, HotSearchLayout, bgLayout;
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
        mSearchContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    doSearch();
                    return true;
                }
                return false;
            }
        });
        mSearchClear = findViewById(R.id.mSearchClear);
        mSearchClear.setOnClickListener(this);
        mDoSearch = (TextView) findViewById(R.id.mDoSearch);
        mSearchLoaddingWrapper = findViewById(R.id.mSearchLoaddingWrapper);
        mSearchTipImg = (ImageView) findViewById(R.id.mSearchTipImg);
        mSearchTip = (TextView) findViewById(R.id.mSearchTip);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
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
        int id = v.getId();
        if (id == R.id.mSearchLeftBack) {
            hideKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 200);
        } else if (id == R.id.mSearchClear) {
            mSearchContent.setText("");
            isVisibility(false);
        } else if (id == R.id.mDoSearch) {
            doSearch();
        } else if (id == R.id.mDoSearchChangeBatch) {
            setHotLabelLayoutData(mCurrPageIndex++);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CommonConstant.REQUEST_ATTENTION_CODE && resultCode == CommonConstant.RESULT_ATTENTION_CODE) {
            int index = data.getIntExtra(CommonConstant.KEY_ATTENTION_INDEX, 0);
            boolean attention = data.getBooleanExtra(CommonConstant.KEY_ATTENTION_CONPUBFLAG, false);
            if (!TextUtil.isListEmpty(mNewsFeedLists)) {
                for (NewsFeed item : mNewsFeedLists) {
                    ArrayList<AttentionListEntity> attentionListEntities = item.getAttentionListEntities();
                    if (!TextUtil.isListEmpty(attentionListEntities)) {
                        for (int i = 0; i < attentionListEntities.size(); i++) {
                            if (i == index) {
                                if (!attention) {
                                    attentionListEntities.get(i).setFlag(0);
                                } else {
                                    attentionListEntities.get(i).setFlag(1);
                                }
                                mNewsFeedAdapter.setNewsFeed(mNewsFeedLists);
                                mNewsFeedAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
        } else if (requestCode == CommonConstant.REQUEST_SUBSCRIBE_LIST_CODE && resultCode == CommonConstant.RESULT_SUBSCRIBE_LIST_CODE) {
            ArrayList<AttentionListEntity> entities = (ArrayList<AttentionListEntity>) data.getSerializableExtra(CommonConstant.KEY_SUBSCRIBE_LIST);
            if (!TextUtil.isListEmpty(mNewsFeedLists)) {
                for (NewsFeed item : mNewsFeedLists) {
                    ArrayList<AttentionListEntity> attentionListEntities = item.getAttentionListEntities();
                    if (!TextUtil.isListEmpty(attentionListEntities)) {
                        item.setAttentionListEntities(entities);
                        mNewsFeedAdapter.setNewsFeed(mNewsFeedLists);
                        mNewsFeedAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    private void doSearch() {
        hideKeyboard();
        if (!TextUtil.isEmptyString(mKeyWord)) {
            mSearchTip.setText("暂无搜索结果");
            SharedPreManager.mInstance(TopicSearchAty.this).HistorySave(mKeyWord);
            SharedPreManager.mInstance(TopicSearchAty.this).deleteSubscribeList();
            try {
                historyEntities = SharedPreManager.mInstance(this).HistoryGetList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isVisibility(true);
            mSearchListViewOpenAdapter.setNewsFeed(historyEntities);
            mSearchListViewOpenAdapter.notifyDataSetChanged();
            mPageIndex = 1;
            loadNewsData(mKeyWord, mPageIndex + "");
            LogUtil.userActionLog(this, CommonConstant.LOG_ATYPE_SEARCH, CommonConstant.LOG_PAGE_SEARCHPAGE, CommonConstant.LOG_PAGE_SEARCHPAGE, null, true);
        } else {
            ToastUtil.toastShort("请您输入搜索关键词");
        }
    }

    /**
     * 获取新闻数据
     */
    private void loadNewsData(String pKeyWord, final String pPageIndex) {
        if (!misPullUpToRefresh)
            bgLayout.setVisibility(View.VISIBLE);
        String keyWord = "";
        try {
            keyWord = URLEncoder.encode(pKeyWord, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        User user = SharedPreManager.mInstance(this).getUser(this);
        int uid = 0;
        if (user != null) {
            uid = user.getMuid();
        }
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        SearchRequest<ArrayList<NewsFeed>> searchRequest = new SearchRequest<>(Request.Method.GET, new TypeToken<ArrayList<NewsFeed>>() {
        }.getType(), HttpConstant.URL_SEARCH_WITH_SUBSCRIBE + "?keywords=" + keyWord + "&p=" + pPageIndex + "&uid=" + uid, new Response.Listener<ArrayList<NewsFeed>>() {

            @Override
            public void onResponse(ArrayList<NewsFeed> response) {
                mSearchListView.onRefreshComplete();
                misPullUpToRefresh = false;
                if (!TextUtil.isListEmpty(response)) {
                    //去掉新闻号
//                    Iterator<NewsFeed> iterator = response.iterator();
//                    while (iterator.hasNext()) {
//                        NewsFeed newsFeed = iterator.next();
//                        int nid = newsFeed.getNid();
//                        if (nid == 0) {
//                            iterator.remove();
//                        }
//                    }
                    mSearchLoaddingWrapper.setVisibility(View.GONE);
                    if (pPageIndex.equals("1")) {
                        mNewsFeedLists.removeAll(mNewsFeedLists);
                    }
                    mNewsFeedLists.addAll(response);
                    mNewsFeedAdapter.setSearchKeyWord(mKeyWord);
                    mNewsFeedAdapter.setNewsFeed(mNewsFeedLists);
                    mNewsFeedAdapter.notifyDataSetChanged();
                } else {
                    if (mPageIndex > 1) {
                        ToastUtil.toastShort("没有更多数据");
                    } else {
                        ToastUtil.toastShort("没有搜索到与\"" + mKeyWord + "\"相关的数据");
                        mSearchTipImg.setVisibility(View.VISIBLE);
                        mSearchTip.setVisibility(View.VISIBLE);
                    }
                }
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                misPullUpToRefresh = false;
                mSearchListView.onRefreshComplete();
                mSearchTipImg.setVisibility(View.VISIBLE);
                mSearchTip.setVisibility(View.VISIBLE);
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
        });
//        searchRequest.setKeyWordAndPageIndex(pKeyWord, pPageIndex);
        searchRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));
        requestQueue.add(searchRequest);
    }

    @Override
    protected void loadData() {
        mSearchLoaddingWrapper.setVisibility(View.VISIBLE);
        mSearchTip.setText("暂无热门搜索热词");
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.GET, HttpConstant.URL_SERVER_HOST + "/hot/words",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = null;
                        if (!TextUtil.isEmptyString(response)) {
                            try {
                                result = new String(response.getBytes("iso-8859-1"), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            mHotLabels = GsonUtil.deSerializedByType(result, new TypeToken<ArrayList<Element>>() {
                            }.getType());
                        }
                        if (!TextUtil.isListEmpty(mHotLabels)) {
                            int temp = mHotLabels.size() % PAGE_CAPACITY;
                            mTotalPage = (temp == 0) ? mHotLabels.size() / PAGE_CAPACITY : mHotLabels.size() / PAGE_CAPACITY + 1;
                            setHotLabelLayoutData(mCurrPageIndex++);
                            mSearchLoaddingWrapper.setVisibility(View.GONE);
                        } else {
                            mSearchTipImg.setVisibility(View.VISIBLE);
                            mSearchTip.setVisibility(View.VISIBLE);
                            mSearchLoaddingWrapper.setVisibility(View.GONE);
                            HotSearchLayout.setVisibility(View.GONE);
                        }
                        if (bgLayout.getVisibility() == View.VISIBLE) {
                            bgLayout.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HotSearchLayout.setVisibility(View.GONE);
                mSearchLoaddingWrapper.setVisibility(View.GONE);
                mSearchTipImg.setVisibility(View.VISIBLE);
                mSearchTip.setVisibility(View.VISIBLE);
                HistoryLayout.setVisibility(View.GONE);
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
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
                hideKeyboard();
                return false;
            }
        });
        mDoSearchChangeBatch = (TextView) headView.findViewById(R.id.mDoSearchChangeBatch);
        mHotLabelsLayout = (HotLabelsLayout) headView.findViewById(R.id.mHotLabelsLayout);
        HistoryLayout = (RelativeLayout) headView.findViewById(R.id.HistoryLayout);
        HotSearchLayout = (RelativeLayout) headView.findViewById(R.id.HotSearchlayout);
        mDoSearchChangeBatch.setOnClickListener(this);
        lv.addHeaderView(headView);
        try {
            historyEntities = SharedPreManager.mInstance(this).HistoryGetList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (historyEntities.size() == 0) {
            HistoryEntity bean = new HistoryEntity(-1);
            historyEntities.add(bean);
            HistoryLayout.setVisibility(View.GONE);
        } else {
            addListViewFootView();
        }
        mSearchListViewOpenAdapter = new SearchListViewOpenAdapter(this);
        mSearchListViewOpenAdapter.setOnSearchListViewOpenItemClick(mOnSearchListViewOpenItemClick);
        mSearchListViewOpenAdapter.setOnFocusItemClick(mOnFocusItemClick);
        mSearchListViewOpenAdapter.setNewsFeed(historyEntities);
        mSearchListViewOpen.setAdapter(mSearchListViewOpenAdapter);
    }

    onSearchListViewOpenItemClick mOnSearchListViewOpenItemClick = new onSearchListViewOpenItemClick() {
        @Override
        public void listener(String content) {
            hideKeyboard();
            mSearchContent.setText(content);
            mSearchTip.setText("暂无搜索结果");
            SharedPreManager.mInstance(TopicSearchAty.this).HistorySave(mKeyWord);
            try {
                historyEntities = SharedPreManager.mInstance(TopicSearchAty.this).HistoryGetList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isVisibility(true);
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
                textView.setBackgroundResource(R.drawable.bg_search_hotlabel);
                textView.setText(element.getTitle());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard();
                        mSearchContent.setText(element.getTitle());
                        mSearchTip.setText("暂无搜索结果");
                        SharedPreManager.mInstance(TopicSearchAty.this).HistorySave(element.getTitle());
                        try {
                            historyEntities = SharedPreManager.mInstance(TopicSearchAty.this).HistoryGetList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        isVisibility(true);
                        mSearchListViewOpenAdapter.setNewsFeed(historyEntities);
                        mSearchListViewOpenAdapter.notifyDataSetChanged();
                        mPageIndex = 1;
                        loadNewsData(element.getTitle(), mPageIndex + "");
                    }
                });
                mHotLabelsLayout.addView(textView);
            }
        }

    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private void hideKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            mSearchContent.clearFocus();
            InputMethodManager inputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private class TopicTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && !TextUtil.isEmptyString(s.toString())) {
                mKeyWord = mSearchContent.getText().toString();
                mSearchClear.setVisibility(View.VISIBLE);
//                mDoSearch.setTextColor(TopicSearchAty.this.getResources().getColor(R.color.do_search_can_do));//搜索文字变色
                mDoSearch.setOnClickListener(TopicSearchAty.this);
            } else {
                mKeyWord = "";
//                isVisibility(false);//没有文字隐藏查找的listview等等
                mSearchClear.setVisibility(View.GONE);
//                mDoSearch.setTextColor(TopicSearchAty.this.getResources().getColor(R.color.do_search_not_do));//搜索文字变色
                mDoSearch.setOnClickListener(null);
            }
        }
    }

    /**
     * @param isType true显示搜索结果 false显示搜索界面
     */
    public void isVisibility(boolean isType) {
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
            }
            if (bgLayout.getVisibility() == View.GONE) {
                bgLayout.setVisibility(View.VISIBLE);
            }
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
            }
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
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
                SharedPreManager.mInstance(TopicSearchAty.this).HistoryRemove();
                HistoryLayout.setVisibility(View.GONE);
                historyEntities = new ArrayList<>();
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
