package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.app.Activity;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.TypedArray;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//
//import com.news.yazhidao.R;
//import com.news.yazhidao.entity.NewsFeed;
//import com.news.yazhidao.entity.User;
//import com.news.yazhidao.pages.LoginAty;
//import com.news.yazhidao.pages.NewsDetailAty2;
//import com.news.yazhidao.utils.DensityUtil;
//import com.news.yazhidao.utils.Logger;
//import com.news.yazhidao.utils.TextUtil;
//import com.news.yazhidao.utils.ToastUtil;
//import com.news.yazhidao.utils.helper.ShareSdkHelper;
//import com.news.yazhidao.utils.manager.SharedPreManager;
//
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qq.QQ;
//import cn.sharesdk.wechat.friends.Wechat;
//import cn.sharesdk.wechat.moments.WechatMoments;
//
//
///**
// * Created by h.yuan on 2015/3/23.
// */
//public class SharePopupWindow extends PopupWindow {
//
//    private TextViewExtend mtvClose;
//    private View mMenuView;
//    private Activity m_pContext;
//    private TypedArray mTypedArray;
//    private String[] mShareName;
//    private String[] marrSharePlatform;
//    private String mstrTitle, mstrUrl, mstrRemark;
//    private ShareDismiss mShareDismiss;
//    private TextViewExtend mtvFavorite, mtvTextSize, mtvAccusation;
//    private ChangeTextSizePopupWindow mChangeTextSizePopWindow;
//    private LinearLayout mShareLayout;
//    boolean isFavorite;
//    NewsFeed feedBean;
//
//
//    public SharePopupWindow(Activity context, ShareDismiss shareDismiss) {
//        super(context);
//        m_pContext = context;
//        mShareDismiss = shareDismiss;
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuView = inflater.inflate(R.layout.popup_window_share, null);
//        mShareName = m_pContext.getResources().getStringArray(R.array.share_list_name);
//        mTypedArray = m_pContext.getResources().obtainTypedArray(R.array.share_list_image);
//        marrSharePlatform = new String[]{WechatMoments.NAME, Wechat.NAME, SinaWeibo.NAME, QQ.NAME};
//        findHeadPortraitImageViews();
//    }
//
//    private void findHeadPortraitImageViews() {
//
//        mtvClose = (TextViewExtend) mMenuView.findViewById(R.id.close_imageView);
//        mtvFavorite = (TextViewExtend) mMenuView.findViewById(R.id.favorite_view);
//        mtvTextSize = (TextViewExtend) mMenuView.findViewById(R.id.textsize_view);
//        mtvAccusation = (TextViewExtend) mMenuView.findViewById(R.id.accusation_view);
//        mShareLayout = (LinearLayout) mMenuView.findViewById(R.id.share_layout);
//        //设置SelectPicPopupWindow的View
//        this.setContentView(mMenuView);
//        //设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体可点击
//        this.setFocusable(true);
//        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.popupWindowAnimation);
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0x00000000);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        mMenuView.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    dismiss();
//                }
//                return true;
//            }
//        });
//        setOnClick();
//    }
//
//    public void setTitleAndUrl(NewsFeed bean,String remark) {
//        feedBean = bean;
//        mstrTitle = bean.getTitle();
//        mstrUrl = bean.getUrl();
//        mstrRemark = remark;
//        isFavorite = SharedPreManager.myFavoriteisSame(mstrUrl);
//        if(isFavorite){
//            mtvFavorite.setText("已收藏");
//        }else {
//            mtvFavorite.setText("未收藏");
//        }
//    }
//
//    @Override
//    public void dismiss() {
//        super.dismiss();
//        mShareDismiss.shareDismiss();
//    }
//
//    private void setOnClick() {
//        mtvClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//        mtvFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                User user = SharedPreManager.getUser(m_pContext);
//                if (user == null) {
//                    Intent loginAty = new Intent(m_pContext, LoginAty.class);
//                    m_pContext.startActivityForResult(loginAty, NewsDetailAty2.REQUEST_CODE);
//                } else {
//                    if (isFavorite) {
//                        isFavorite = false;
//                        mtvFavorite.setText("未收藏");
//                        SharedPreManager.myFavoritRemoveItem(feedBean.getUrl());
////                    mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
//                    } else {
//                        isFavorite = true;
//                        mtvFavorite.setText("已收藏");
//                        SharedPreManager.myFavoriteSaveList(feedBean);
//
////                    mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
//                    }
//                    dismiss();
//                }
//
//
//            }
//        });
//        mtvTextSize.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mChangeTextSizePopWindow == null) {
//                    mChangeTextSizePopWindow = new ChangeTextSizePopupWindow(m_pContext);
//                    mChangeTextSizePopWindow.isDeteilOpen();
//                    mChangeTextSizePopWindow.showAtLocation(m_pContext.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//                    dismiss();
//                }
//            }
//        });
//        mtvAccusation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//        for (int i = 0; i < mTypedArray.length(); i++) {
//            TextViewExtend viewExtend = new TextViewExtend(m_pContext);
//            Drawable topDrawable = m_pContext.getResources().getDrawable(mTypedArray.getResourceId(i, 0));
//            topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
//            viewExtend.setCompoundDrawables(null, topDrawable, null, null);
//            viewExtend.setTextColor(m_pContext.getResources().getColor(R.color.bg_share_text));
//            viewExtend.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_pContext.getResources().getDimensionPixelSize(R.dimen.new_font6));
//            viewExtend.setText(mShareName[i]);
//            viewExtend.setGravity(Gravity.CENTER_HORIZONTAL);
//            viewExtend.setCompoundDrawablePadding(DensityUtil.dip2px(m_pContext, 8));
//            int margin = DensityUtil.dip2px(m_pContext, 25);
//            if (i == mTypedArray.length() - 1) {
//                viewExtend.setPadding(margin, margin, margin, margin);
//            } else {
//                viewExtend.setPadding(margin, margin, 0, margin);
//            }
//            final String strShareName = mShareName[i];
//            String strSharePlatform = null;
//            if (i < marrSharePlatform.length)
//                strSharePlatform = marrSharePlatform[i];
//            final String finalStrSharePlatform = strSharePlatform;
//            viewExtend.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if ("短信".equals(strShareName)) {
//                        Uri smsToUri = Uri.parse("smsto:");
//                        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
//                        intent.putExtra("sms_body", mstrTitle + mstrUrl);
//                        m_pContext.startActivity(intent);
//                    } else if ("邮件".equals(strShareName)) {
////                        String[] email = {"3802**92@qq.com"}; // 需要注意，email必须以数组形式传入
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("message/rfc822"); // 设置邮件格式
////                        intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
////                        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
//                        intent.putExtra(Intent.EXTRA_SUBJECT, mstrTitle); // 主题
//                        intent.putExtra(Intent.EXTRA_TEXT, mstrUrl); // 正文
//                        m_pContext.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
//                    } else if ("转发链接".equals(strShareName)) {
//                        ClipboardManager cmb = (ClipboardManager) m_pContext.getSystemService(Context.CLIPBOARD_SERVICE);
//                        cmb.setPrimaryClip(ClipData.newPlainText(null, mstrUrl));
//                        ToastUtil.toastShort("复制成功");
////                        Log.i("eva",cmb.getText().toString().trim());
//                    } else {
//                        mstrUrl = "http://deeporiginalx.com/news.html?type=0&url="+ TextUtil.getBase64(mstrUrl) +"&interface";
//                        Logger.e("jigang", "share url=" + mstrUrl);
//                        ShareSdkHelper.ShareToPlatformByNewsDetail(m_pContext, finalStrSharePlatform, mstrTitle, mstrUrl, mstrRemark);
//                    }
//                    SharePopupWindow.this.dismiss();
//                }
//            });
//            mShareLayout.addView(viewExtend);
//        }
//    }
//
//    class ShareAdapter extends BaseAdapter {
//
//        Context mContext;
//
//        ShareAdapter(Context context) {
//            mContext = context;
//        }
//
//        @Override
//        public int getCount() {
//            return mShareName == null ? 0 : mShareName.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return position;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            final Holder holder;
//            if (convertView == null) {
//                holder = new Holder();
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_share, null, false);
//                holder.tvShare = (TextViewExtend) convertView.findViewById(R.id.share_textView);
//                convertView.setTag(holder);
//            } else {
//                holder = (Holder) convertView.getTag();
//            }
//            final String strShareName = mShareName[position];
//            holder.tvShare.setText(strShareName);
//            Drawable drawable = mContext.getResources().getDrawable(mTypedArray.getResourceId(position, 0));
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            holder.tvShare.setCompoundDrawables(null, drawable, null, null);
//            holder.tvShare.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if ("短信".equals(strShareName)) {
//                        Uri smsToUri = Uri.parse("smsto:");
//                        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
//                        intent.putExtra("sms_body", mstrTitle + mstrUrl);
//                        mContext.startActivity(intent);
//                    } else if ("邮件".equals(strShareName)) {
////                        String[] email = {"3802**92@qq.com"}; // 需要注意，email必须以数组形式传入
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("message/rfc822"); // 设置邮件格式
////                        intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
////                        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
//                        intent.putExtra(Intent.EXTRA_SUBJECT, mstrTitle); // 主题
//                        intent.putExtra(Intent.EXTRA_TEXT, mstrUrl); // 正文
//                        mContext.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
//                    } else if ("转发链接".equals(strShareName)) {
//                        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
//                        cmb.setPrimaryClip(ClipData.newPlainText(null, mstrUrl));
//                        ToastUtil.toastShort("复制成功");
////                        Log.i("eva",cmb.getText().toString().trim());
//                    } else {
//                        Logger.e("jigang", "share url=" + mstrUrl);
//                        ShareSdkHelper.ShareToPlatformByNewsDetail(mContext, marrSharePlatform[position], mstrTitle, mstrUrl, mstrRemark);
//                    }
//                    SharePopupWindow.this.dismiss();
//                }
//            });
//            return convertView;
//        }
//    }
//
//
//    class Holder {
//        TextViewExtend tvShare;
//    }
//
//    public interface ShareDismiss {
//        void shareDismiss();
//    }
//}
