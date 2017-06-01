package com.news.sdk.widget;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.news.sdk.R;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.entity.User;
import com.news.sdk.utils.AuthorizedUserUtil;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class SharePopupWindow extends PopupWindow {

    private TextViewExtend mtvClose;
    private View mMenuView;
    private Activity mContext;
    private TypedArray mTypedArray;
    private String[] mShareName;
    private String[] marrSharePlatform;
    private String mstrTitle, mstrUrl, mstrRemark;
    private ShareDismiss mShareDismiss;
    private TextViewExtend mtvFavorite, mtvTextSize, mtvAccusation;
    private LinearLayout mShareLayout, mOtherLayout;
    boolean isFavorite;
    boolean isVideo, isTopic;
    private int mNid;
    private View line_layout, line1_layout;


    public SharePopupWindow(Activity context, ShareDismiss shareDismiss) {
        super(context);
        mContext = context;
        mShareDismiss = shareDismiss;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_window_share, null);
        mShareName = mContext.getResources().getStringArray(R.array.share_list_name);
        mTypedArray = mContext.getResources().obtainTypedArray(R.array.share_list_image);
//        marrSharePlatform = new String[]{WechatMoments.NAME, Wechat.NAME, SinaWeibo.NAME, QQ.NAME};
        findHeadPortraitImageViews();
    }

    private void findHeadPortraitImageViews() {
        mtvClose = (TextViewExtend) mMenuView.findViewById(R.id.close_imageView);
        mShareLayout = (LinearLayout) mMenuView.findViewById(R.id.share_layout);
        mOtherLayout = (LinearLayout) mMenuView.findViewById(R.id.other_layout);
        line_layout = mMenuView.findViewById(R.id.line_layout);
        line1_layout = mMenuView.findViewById(R.id.line1_layout);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popupWindowAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    dismiss();
                }
                return true;
            }
        });
        setOnClick();
        TextUtil.setTextColor(mContext, mtvClose, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, mtvClose, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, mShareLayout, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, mOtherLayout, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, line_layout, R.color.color5);
        TextUtil.setLayoutBgResource(mContext, line1_layout, R.color.color5);
    }

    public void setTitleAndNid(String title, int nid, String remark) {
        mstrTitle = title;
        mNid = nid;
        mstrRemark = remark;
        mstrUrl = nid + "";
    }

    public void setFavoriteGone() {
        mtvFavorite.setVisibility(View.GONE);
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public void setTopic(boolean topic) {
        isTopic = topic;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mShareDismiss != null) {
            mShareDismiss.shareDismiss();
        }
    }

    public interface OnFavoritListener {
        public void FavoritListener(boolean isFavoriteType);
    }

    OnFavoritListener listener;

    public void setOnFavoritListener(OnFavoritListener listener) {
        this.listener = listener;
    }

    private void setOnClick() {
        mtvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        for (int i = 0; i < mTypedArray.length(); i++) {
            TextViewExtend viewExtend = new TextViewExtend(mContext);
            Drawable topDrawable = mContext.getResources().getDrawable(mTypedArray.getResourceId(i, 0));
            topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
            viewExtend.setCompoundDrawables(null, topDrawable, null, null);
            TextUtil.setTextColor(mContext, viewExtend, R.color.color2);
            viewExtend.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.new_font6));
            viewExtend.setText(mShareName[i]);
            viewExtend.setGravity(Gravity.CENTER_HORIZONTAL);
            viewExtend.setCompoundDrawablePadding(DensityUtil.dip2px(mContext, 8));
            ImageUtil.setAlphaImage(viewExtend);
            int margin = DensityUtil.dip2px(mContext, 25);
            if (i == mTypedArray.length() - 1) {
                viewExtend.setPadding(margin, margin, margin, margin);
            } else {
                viewExtend.setPadding(margin, margin, 0, margin);
            }
            final String strShareName = mShareName[i];
            viewExtend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isVideo && !isTopic) {
                        mstrUrl = "http://deeporiginalx.com/videoShare/index.html?nid=" + mNid;
                    } else if (!isVideo && isTopic) {
                        mstrUrl = "http://deeporiginalx.com/zhuanti-share/index.html?tid=" + mNid;
                    } else {
                        mstrUrl = "http://deeporiginalx.com/news.html?type=0&url=" + mstrUrl;//TextUtil.getBase64(mstrUrl) +"&interface"
                    }
                    if ("短信".equals(strShareName)) {
                        Uri smsToUri = Uri.parse("smsto:");
                        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                        intent.putExtra("sms_body", mstrTitle + mstrUrl);
                        mContext.startActivity(intent);
                        replay(5);
                    } else if ("邮件".equals(strShareName)) {
//                        String[] email = {"3802**92@qq.com"}; // 需要注意，email必须以数组形式传入
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822"); // 设置邮件格式
//                        intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
//                        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                        intent.putExtra(Intent.EXTRA_SUBJECT, mstrTitle); // 主题
                        intent.putExtra(Intent.EXTRA_TEXT, mstrUrl); // 正文
                        mContext.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
                        replay(6);
                    } else if ("转发链接".equals(strShareName)) {
                        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setPrimaryClip(ClipData.newPlainText(null, mstrUrl));
                        ToastUtil.toastShort("复制成功");
                        replay(7);
                    } else if ("夜间模式".equals(strShareName)) {
                        ThemeManager.setThemeMode(ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY
                                ? ThemeManager.ThemeMode.NIGHT : ThemeManager.ThemeMode.DAY);
                    } else {
                        Intent intent = new Intent();
                        int whereabout = 0;
                        User user = SharedPreManager.mInstance(mContext).getUser(mContext);
                        if (user != null) {
                            if (user.isVisitor()) {
                                AuthorizedUserUtil.sendUserLoginBroadcast(mContext);
                            } else {
                                if ("微信朋友圈".equals(strShareName)) {
                                    whereabout = 1;
                                    intent.setAction(CommonConstant.SHARE_WECHAT_MOMENTS_ACTION);
                                } else if ("微信好友".equals(strShareName)) {
                                    whereabout = 2;
                                    intent.setAction(CommonConstant.SHARE_WECHAT_ACTION);
                                } else if ("新浪微博".equals(strShareName)) {
                                    whereabout = 4;
                                    intent.setAction(CommonConstant.SHARE_SINA_WEIBO_ACTION);
                                } else if ("QQ好友".equals(strShareName)) {
                                    whereabout = 3;
                                    intent.setAction(CommonConstant.SHARE_QQ_ACTION);
                                }
                            }
                        }
                        intent.putExtra(CommonConstant.SHARE_TITLE, mstrTitle);
                        intent.putExtra(CommonConstant.SHARE_URL, mstrUrl);
                        mContext.sendBroadcast(intent);
                        replay(whereabout);
                    }
                    SharePopupWindow.this.dismiss();
                }
            });
            if (i < 4) {
                mShareLayout.addView(viewExtend);
            } else {
                mOtherLayout.addView(viewExtend);
            }
        }
    }

    public interface ShareDismiss {
        void shareDismiss();
    }

    private void replay(int whereabout) {
        //转发记录
        final User user = SharedPreManager.mInstance(mContext).getUser(mContext);
        if (user != null) {
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            Map<String, Integer> map = new HashMap<>();
            map.put("nid", mNid);
            map.put("uid", user.getMuid());
            map.put("whereabout", whereabout);
            JSONObject jsonObject = new JSONObject(map);
            JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_REPLAY, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> header = new HashMap<>();
//                    header.put("Authorization", "Basic " + user.getAuthorToken());
                    header.put("Content-Type", "application/json");
                    header.put("X-Requested-With", "*");
                    return header;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(request);
        }
    }
}
