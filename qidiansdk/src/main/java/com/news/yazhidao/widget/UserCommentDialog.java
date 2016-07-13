package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.v4.app.DialogFragment;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.news.yazhidao.R;
//import com.news.yazhidao.common.HttpConstant;
//import com.news.yazhidao.entity.NewsDetailComment;
//import com.news.yazhidao.entity.User;
//import com.news.yazhidao.pages.LoginAty;
//import com.news.yazhidao.pages.NewsDetailAty2;
//import com.news.yazhidao.utils.DateUtil;
//import com.news.yazhidao.utils.DensityUtil;
//import com.news.yazhidao.utils.Logger;
//import com.news.yazhidao.utils.TextUtil;
//import com.news.yazhidao.utils.ToastUtil;
//import com.news.yazhidao.utils.manager.SharedPreManager;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.UUID;
//
//
///**
// * Created by fengjigang on 16/2/25.
// */
//@SuppressLint("ValidFragment")
//public class UserCommentDialog extends DialogFragment implements View.OnClickListener {
//
//    public static final int REQUEST_CODE = 1005;
//    public static final String KEY_ADD_COMMENT = "key_add_comment";
//    private String mDocid;
//    private Context mContext;
//    private EditText mCommentContent;
//    private TextView mCommentCommit;
//    private String mUserCommentMsg;
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        this.mContext = activity;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.UserComment);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getDialog().getWindow().setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
//        View rootView = inflater.inflate(R.layout.dialog_user_comment, null);
//        rootView.setMinimumWidth(10000);
//        rootView.setMinimumHeight(DensityUtil.dip2px(getActivity(), 150));
//        mCommentContent = (EditText) rootView.findViewById(R.id.mCommentContent);
//        mCommentCommit = (TextView) rootView.findViewById(R.id.mCommentCommit);
//        mCommentContent.addTextChangedListener(new CommentTextWatcher());
//        return rootView;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        (new Handler()).postDelayed(new Runnable() {
//            public void run() {
//                InputMethodManager inManager = (InputMethodManager) mCommentContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        }, 50);
//        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    Logger.e("jigang", "dialog  back");
//                    UserCommentDialog.this.dismiss();
//                }
//                return false;
//            }
//        });
//    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//    }
//
//    @Override
//    public void dismiss() {
//        super.dismiss();
//    }
//
//    public void setDocid(String docid) {
//        this.mDocid = docid;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == LoginAty.REQUEST_CODE && data != null) {
//            User user = (User) data.getSerializableExtra(LoginAty.KEY_USER_LOGIN);
//            submitComment(user);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.mCommentCommit:
//                User user = SharedPreManager.getUser(getActivity());
////                user = new User();
////                user.setUserName("zhangsan");
////                user.setUserIcon("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0");
//                if (user == null) {
//                    Intent loginAty = new Intent(getActivity(), LoginAty.class);
//                    startActivityForResult(loginAty, REQUEST_CODE);
//                } else {
//                    submitComment(user);
//                }
//                break;
//        }
//    }
//
//    private void submitComment(final User user) {
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//        JSONObject json = null;
//        final String nickeName = user.getUserName();
//        String uuid = SharedPreManager.getUUID();
//        final String createTime = DateUtil.getDate();
//        final String profile = user.getUserIcon();
//        final String userid = user.getUserId();
//        Logger.d("aaa", "uuid==" + uuid);
//        Logger.d("aaa", "userid==" + userid);
//        final String docid = mDocid;
//        final String comment_id = UUID.randomUUID().toString();
//        try {
//            json = new JSONObject("{\"comment_id\":\"" + comment_id + "\",\"content\":\"" + mUserCommentMsg + "\",\"nickname\":\"" + nickeName + "\",\"uuid\":\"" + userid + "\",\"love\":0,\"create_time\":\"" + createTime + "\",\"profile\":\"" + profile + "\",\"docid\":\"" + docid + "\",\"pid\":\"pid\"}");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_ADD_COMMENT, json, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Logger.e("jigang", "add comment success =" + response);
//                try {
//                    String code = response.getString("code");
//                    String message = response.getString("message");
//                    String data = response.optString("data");
//                    if ("0".equals(code) && "success".equals(message)) {
//                        Logger.e("jigang", "comment_id" + comment_id);
//                        ToastUtil.toastShort("评论成功!");
//                        Intent intent = new Intent(NewsDetailAty2.ACTION_REFRESH_COMMENT);
//                        NewsDetailComment comment = new NewsDetailComment(comment_id, mUserCommentMsg, createTime, docid, data, 0, nickeName, profile, userid);
//                        //实例化一个新闻评论类
//
////                        NewsDetailCommentItem newsDetailCommentItem = new NewsDetailCommentItem();
////                        newsDetailCommentItem.setComment_id(comment_id);
////                        newsDetailCommentItem.setContent(mUserCommentMsg);
////                        newsDetailCommentItem.setCreate_time(System.currentTimeMillis());
////                        newsDetailCommentItem.setDocid(docid);
////                        newsDetailCommentItem.setLove(0);
////                        newsDetailCommentItem.setPraise(false);
//                        comment.setUser(user);
//
//                        NewsDetailComment newsDetailCommentItem = new NewsDetailComment();
//                        newsDetailCommentItem.setComment_id(comment_id);
//                        newsDetailCommentItem.setContent(mUserCommentMsg);
//                        newsDetailCommentItem.setCreate_time(DateUtil.getDate());
//                        newsDetailCommentItem.setDocid(docid);
//                        newsDetailCommentItem.setLove(0);
//                        newsDetailCommentItem.setPraise(false);
//                        newsDetailCommentItem.setUser(user);
//
//
//
//                        intent.putExtra(KEY_ADD_COMMENT, comment);
//                        getActivity().sendBroadcast(intent);
//                        UserCommentDialog.this.dismiss();
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Logger.e("jigang", "add comment fail =" + error.getMessage());
//                ToastUtil.toastShort("评论失败!");
//            }
//        });
//        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
//        requestQueue.add(jsonRequest);
//    }
//
//    private class CommentTextWatcher implements TextWatcher {
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            Logger.e("jigang", "s=" + s);
//            if (s != null && !TextUtil.isEmptyString(s.toString())) {
//                mUserCommentMsg = mCommentContent.getText().toString();
//                if (mUserCommentMsg.length() >= 144) {
//                    ToastUtil.toastShort("亲,您输入的评论过长");
//                    mUserCommentMsg = mUserCommentMsg.substring(0, 145);
//                    mCommentContent.setText(mUserCommentMsg);
//                }
//                mCommentCommit.setBackgroundResource(R.drawable.bg_user_comment_commit_sel);
//                mCommentCommit.setOnClickListener(UserCommentDialog.this);
//            } else {
//                mCommentCommit.setBackgroundResource(R.drawable.bg_user_comment_commit);
//                mCommentCommit.setOnClickListener(null);
//            }
//        }
//    }
//}
