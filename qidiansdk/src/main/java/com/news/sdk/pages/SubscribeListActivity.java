package com.news.sdk.pages;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.sdk.R;
import com.news.sdk.adapter.abslistview.CommonAdapter;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.AttentionListEntity;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.DetailOperateRequest;
import com.news.sdk.utils.AuthorizedUserUtil;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.swipebackactivity.SwipeBackActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class SubscribeListActivity extends SwipeBackActivity {

    private TextView mSubscribeListLeftBack;
    private PullToRefreshListView mAttentionListView;
    private SubscribeListAdapter mAdapter;
    private Context mContext;
    private ArrayList<AttentionListEntity> mAttentionListEntities = new ArrayList<AttentionListEntity>();
    private ArrayList<AttentionListEntity> mAttentionListTemp;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_subscribe_list);
        mContext = this;
    }

    @Override
    protected void initializeViews() {
        mSubscribeListLeftBack = (TextView) findViewById(R.id.mSubscribeListLeftBack);
        mAttentionListView = (PullToRefreshListView) findViewById(R.id.aty_SubscribeList_PullToRefreshListView);
    }

    @Override
    protected void loadData() {
//        ArrayList<AttentionListEntity> subscribeList = SharedPreManager.mInstance(this).getSubscribeList();
//        if (!TextUtil.isListEmpty(subscribeList)) {
//            mAttentionListEntities = subscribeList;
//        } else {
        mAttentionListEntities = (ArrayList<AttentionListEntity>) getIntent().getSerializableExtra(CommonConstant.KEY_SUBSCRIBE_LIST);
//        }
        mAttentionListTemp = TextUtil.copyArrayList(mAttentionListEntities);
        mAttentionListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mAttentionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent attentionAty = new Intent(SubscribeListActivity.this, AttentionActivity.class);
                AttentionListEntity attention = mAttentionListEntities.get(position - 1);
                attentionAty.putExtra(CommonConstant.KEY_ATTENTION_CONPUBFLAG, attention.getFlag());
                attentionAty.putExtra(CommonConstant.KEY_ATTENTION_HEADIMAGE, attention.getIcon());
                attentionAty.putExtra(CommonConstant.KEY_ATTENTION_TITLE, attention.getName());
                attentionAty.putExtra(CommonConstant.KEY_ATTENTION_INDEX, position - 1);
                startActivityForResult(attentionAty, CommonConstant.REQUEST_ATTENTION_CODE);
            }
        });
        mAdapter = new SubscribeListAdapter(mContext);
        mAdapter.setNewsFeed(mAttentionListEntities);
        mAttentionListView.setAdapter(mAdapter);
        mSubscribeListLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CommonConstant.REQUEST_ATTENTION_CODE && resultCode == CommonConstant.RESULT_ATTENTION_CODE) {
            if (data != null) {
                boolean currentAttention = data.getBooleanExtra(CommonConstant.KEY_ATTENTION_CONPUBFLAG, false);
                int position = data.getIntExtra(CommonConstant.KEY_ATTENTION_INDEX, 0);
                AttentionListEntity entity = mAttentionListEntities.get(position);
                int flag = entity.getFlag();
                boolean attention;
                if (flag == 0) {
                    attention = false;
                } else {
                    attention = true;
                }
                if (currentAttention != attention) {
                    if (currentAttention) {
                        entity.setFlag(1);
                        entity.setConcern(entity.getConcern() + 1);
                    } else {
                        entity.setFlag(0);
                        entity.setConcern(entity.getConcern() - 1);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
//        else if (requestCode == REQUEST_LOGIN_CODE && resultCode == LoginAty.REQUEST_CODE) {
//            if (data != null) {
//                int position = data.getIntExtra(KEY_ATTENTION_INDEX, 0);
//                changeAttentionStatus(mAdapter, mAttentionListEntities.get(position));
//            }
//        }
    }

    /**
     * 订阅源适配器
     */
    public class SubscribeListAdapter extends CommonAdapter<AttentionListEntity> {
        private Context mContext;
        private RequestManager mRequestManager;

        public SubscribeListAdapter(Context mContext) {
            super(R.layout.subscribelist_item, mContext, null);
            this.mContext = mContext;
            mRequestManager = Glide.with(mContext);
        }

        @Override
        public void convert(CommonViewHolder holder, final AttentionListEntity attentionListEntity, final int position) {
            holder.setGlideDrawViewURI(mRequestManager, R.id.img_SubscribeListItem_icon, attentionListEntity.getIcon(), position);
            holder.setTextViewText(R.id.tv_SubscribeListItem_name, attentionListEntity.getName());
            int concern = attentionListEntity.getConcern();
            String personNum = "";
            if (concern > 10000) {
                float result = (float) concern / 10000;
                personNum = Math.round(result * 10) / 10f + "万人关注";
            } else if (concern > 0) {
                personNum = concern + "人关注";
            } else {
                personNum = "";
            }
            holder.setTextViewText(R.id.tv_SubscribeListItem_personNum, personNum);
            if (attentionListEntity.getFlag() == 0) {
                holder.setTextViewTextBackgroundResource(R.id.mAttention_btn, R.drawable.unattention_tv_shape);
                holder.setTextViewTextColor(R.id.mAttention_btn, R.color.attention_line_color);
                holder.setTextViewText(R.id.mAttention_btn, "关注");
            } else {
                holder.setTextViewTextBackgroundResource(R.id.mAttention_btn, R.drawable.attention_tv_shape);
                holder.setTextViewTextColor(R.id.mAttention_btn, R.color.unattention_line_color);
                holder.setTextViewText(R.id.mAttention_btn, "已关注");
            }
            holder.getView(R.id.mAttention_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = SharedPreManager.mInstance(SubscribeListActivity.this).getUser(mContext);
                    if (user != null && user.isVisitor()) {
                        AuthorizedUserUtil.sendUserLoginBroadcast(mContext);
                    } else {
                        changeAttentionStatus(attentionListEntity);
                    }
                }
            });
        }
    }

    /**
     * 更改关注的状态
     *
     * @param attentionListEntity
     */
    private void changeAttentionStatus(AttentionListEntity attentionListEntity) {
        if (attentionListEntity.getFlag() == 1) {
//            SharedPreManager.mInstance(this).deleteAttention(attentionListEntity.getName());
            attentionListEntity.setFlag(0);
            attentionListEntity.setConcern(attentionListEntity.getConcern() - 1);
        } else {
//            SharedPreManager.mInstance(this).addAttention(attentionListEntity.getName());
//            if (SharedPreManager.mInstance(this).getBoolean(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID)) {
//                ToastUtil.showAttentionSuccessToast(mContext);
//            } else {
//                Atte、ntionDetailDialog attentionDetailDialog = new AttentionDetailDialog(mContext,attentionListEntity.getName());
//                attentionDetailDialog.show();
//                SharedPreManager.mInstance(this).save(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID, true);
//            }
            attentionListEntity.setFlag(1);
            attentionListEntity.setConcern(attentionListEntity.getConcern() + 1);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
//        SharedPreManager.mInstance(this).saveSubscribeList(mAttentionListEntities);
        /**用户退出时发送关注和取消关注的状态*/
        User user = SharedPreManager.mInstance(this).getUser(this);
        for (int i = 0; i < mAttentionListTemp.size(); i++) {
            AttentionListEntity oldEntity = mAttentionListTemp.get(i);
            AttentionListEntity newEntity = mAttentionListEntities.get(i);
            if (oldEntity.getFlag() != newEntity.getFlag()) {
                attentionSubscribe(newEntity, user);
            }
        }
        super.onPause();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(CommonConstant.KEY_SUBSCRIBE_LIST, mAttentionListEntities);
        setResult(CommonConstant.RESULT_SUBSCRIBE_LIST_CODE, intent);
        super.finish();
    }

    //请求应该改成list也可以
    private void attentionSubscribe(final AttentionListEntity attentionListEntity, User user) {
        String pname = null;
        try {
            pname = URLEncoder.encode(attentionListEntity.getName(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        JSONObject json = new JSONObject();
        final int requestMethod = attentionListEntity.getFlag() > 0 ? Request.Method.POST : Request.Method.DELETE;
        Logger.e("jigang", "attention url = " + (HttpConstant.URL_ADDORDELETE_ATTENTION + "uid=" + user.getMuid() + "&pname=" + pname) + ",==" + requestMethod);
        DetailOperateRequest request = new DetailOperateRequest(requestMethod,
                HttpConstant.URL_ADDORDELETE_ATTENTION + "uid=" + user.getMuid() + "&pname=" + pname
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                Logger.e("jigang", "attention data=" + data);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mNewsDetailList.onRefreshComplete();
                Logger.e("jigang", "attention = network fail " + error.getMessage());
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.mInstance(this).getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }
}
