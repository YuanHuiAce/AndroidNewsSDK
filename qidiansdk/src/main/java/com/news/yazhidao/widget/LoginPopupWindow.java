package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.ColorDrawable;
//import android.text.Html;
//import android.util.TypedValue;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.news.yazhidao.R;
//import com.news.yazhidao.common.CommonConstant;
//import com.news.yazhidao.entity.User;
//import com.news.yazhidao.listener.DisplayImageListener;
//import com.news.yazhidao.listener.UserLoginListener;
//import com.news.yazhidao.listener.UserLoginPopupStateListener;
//import com.news.yazhidao.pages.ChatAty;
//import com.news.yazhidao.pages.DiggerAty;
//import com.news.yazhidao.pages.FeedBackActivity;
//import com.news.yazhidao.pages.LengjingFgt;
//import com.news.yazhidao.pages.LoginModeFgt;
//import com.news.yazhidao.utils.helper.ShareSdkHelper;
//import com.news.yazhidao.utils.image.ImageManager;
//import com.news.yazhidao.utils.manager.SharedPreManager;
//import com.news.yazhidao.widget.customdialog.Effectstype;
//import com.news.yazhidao.widget.customdialog.SuperDialogBuilder;
//
//import cn.sharesdk.framework.PlatformDb;
//
///**
// * Created by fengjigang on 15/5/12.
// */
//public class LoginPopupWindow extends PopupWindow implements View.OnClickListener, UserLoginListener {
//    private final View mPopupWidow;
//    private Context mContext;
//    private RoundedImageView mHomeUserIcon;
//    private TextView mHomeLogin;
//    private View mHomeChatWrapper;
//    private View mHomeLoginCancel;
//    private UserLoginListener mUserLoginListener;
//    private View mHomeLoginDivide;
//    private View mHomeLogout;
//    private OnDismissListener listener;
//    private View mDigger;//挖掘机选项
//
//    public LoginPopupWindow(Context mContext, OnDismissListener listener) {
//        this.listener = listener;
//        this.mContext = mContext;
//        LayoutInflater inflater = (LayoutInflater) mContext
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mPopupWidow = inflater.inflate(R.layout.aty_home_login, null);
//        initConfig();
//        initViews();
//    }
//
//    private void initViews() {
//        mHomeUserIcon = (RoundedImageView) mPopupWidow.findViewById(R.id.mHomeUserIcon);
//        mHomeLogin = (TextView) mPopupWidow.findViewById(R.id.mHomeLogin);
//        mHomeLogin.setText(Html.fromHtml(mContext.getResources().getString(R.string.home_login_text)));
//        mHomeChatWrapper = mPopupWidow.findViewById(R.id.mHomeChatWrapper);
//        mHomeLoginCancel = mPopupWidow.findViewById(R.id.mHomeLoginCancel);
//        mHomeLogout = mPopupWidow.findViewById(R.id.mHomeLogout);
//        mHomeLoginDivide = mPopupWidow.findViewById(R.id.mHomeLoginDivide);
//        mDigger = mPopupWidow.findViewById(R.id.mDigger);
//        mDigger.setOnClickListener(this);
//        mHomeLogout.setOnClickListener(this);
//        mHomeLoginCancel.setOnClickListener(this);
//        mHomeLogin.setOnClickListener(this);
//        mHomeChatWrapper.setOnClickListener(this);
//        //判断用户是否登录，并且登录有效
//        User user = SharedPreManager.getUser(mContext);
//        if (user != null) {
//            mHomeLogin.setOnClickListener(null);
//            mHomeLogin.setText(user.getUserName());
//            mHomeLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//            mHomeLogout.setVisibility(View.VISIBLE);
//            mHomeLoginDivide.setVisibility(View.VISIBLE);
//            mHomeChatWrapper.setBackgroundResource(R.drawable.bg_login_footer_default);
//            mDigger.setBackgroundResource(R.drawable.bg_login_footer_default);
//            ImageManager.getInstance(mContext).DisplayImage(user.getUserIcon(), mHomeUserIcon, false,new DisplayImageListener() {
//                @Override
//                public void success(int width,int height) {
//
//                }
//
//                @Override
//                public void failed() {
//
//                }
//            });
//        }
//    }
//
//    private void initConfig() {
//        //设置SelectPicPopupWindow的View
//        this.setContentView(mPopupWidow);
//        //设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体可点击
//        this.setFocusable(true);
//        //设置SelectPicPopupWindow弹出窗体动画效果
////        this.setAnimationStyle(R.style.DialogAnimation);
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.mHomeLoginCancel:
//                this.dismiss();
//                break;
//            case R.id.mHomeLogin:
//                openLoginModeWindow();
//                break;
//            case R.id.mHomeLogout:
//                logout();
//                break;
//            case R.id.mHomeChatWrapper:
//                String strJPushId = SharedPreManager.getJPushId();
//                Intent intent;
//                if (CommonConstant.JINYU_JPUSH_ID.equals(strJPushId))
//                    intent = new Intent(mContext, ChatAty.class);
//                else
//                    intent = new Intent(mContext, FeedBackActivity.class);
//                mContext.startActivity(intent);
//                dismiss();
//                break;
//            case R.id.mDigger:
//                Intent digger = new Intent(mContext, DiggerAty.class);
//                mContext.startActivity(digger);
//                //获取红包页面
////                Intent redPacket = new Intent(mContext, NewsDetailWebviewAty.class);
////                redPacket.putExtra(NewsDetailWebviewAty.KEY_URL,"http://dwz.cn/1eBPim");
////                mContext.startActivity(redPacket);
//                this.dismiss();
//                break;
//        }
//    }
//
//    /**
//     * 用户退出登录
//     */
//    private void logout() {
//        final SuperDialogBuilder _DialogBuilder = SuperDialogBuilder.getInstance(mContext);
//        _DialogBuilder.withMessage("退出后，就不能参与评论了")
//                .withDuration(400)
//                .withIcon(R.drawable.app_icon_version3)
//                .withTitle("退出登录")
//                .withEffect(Effectstype.Sidefill)
//                .withButton1Text("确定")
//                .withButton2Text("取消")
//                .setButton1Click(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        _DialogBuilder.dismiss();
//                        listener.onDismiss();
//                        ShareSdkHelper.logout(mContext);
//                        mHomeUserIcon.setImageResource(R.drawable.ic_user_login_default);
//                        mHomeLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                        mHomeLogin.setText(Html.fromHtml(mContext.getResources().getString(R.string.home_login_text)));
//                        mHomeLogin.setOnClickListener(LoginPopupWindow.this);
//                        mHomeLogout.setVisibility(View.GONE);
//                        mHomeLoginDivide.setVisibility(View.GONE);
//                        mHomeChatWrapper.setBackgroundResource(R.drawable.bg_login_footer);
//                        mDigger.setBackgroundResource(R.drawable.bg_login_footer);
//                        //发送广播通知LengJingFgt,刷新界面
//                        Intent userLogoutIntent = new Intent(LengjingFgt.ACTION_USER_LOGOUTED);
//                        mContext.sendBroadcast(userLogoutIntent);
//                    }
//                }).setButton2Click(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                _DialogBuilder.dismiss();
//            }
//        }).show();
//
//
//    }
//
//    private void openLoginModeWindow() {
//        LoginModeFgt loginModeFgt = new LoginModeFgt(mContext, this, new UserLoginPopupStateListener() {
//
//            @Override
//            public void close() {
//                LoginPopupWindow.this.dismiss();
//            }
//        });
//        loginModeFgt.show(((FragmentActivity)(mContext)).getSupportFragmentManager(), "loginModeFgt");
////        ((FragmentActivity)(mContext)).getSupportFragmentManager().beginTransaction().add(loginModeFgt,"loginModeFgt").commitAllowingStateLoss();
//    }
//
//
//    @Override
//    public void userLogin(String platform, PlatformDb platformDb) {
//        mHomeLogin.setOnClickListener(null);
//        mHomeLogin.setText(platformDb.getUserName());
//        mHomeLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//        mHomeChatWrapper.setBackgroundResource(R.drawable.bg_login_footer_default);
//        mDigger.setBackgroundResource(R.drawable.bg_login_footer_default);
//        mHomeLogout.setVisibility(View.VISIBLE);
//        mHomeLoginDivide.setVisibility(View.VISIBLE);
//        ImageManager.getInstance(mContext).DisplayImage(platformDb.getUserIcon(), mHomeUserIcon, false,new DisplayImageListener() {
//            @Override
//            public void success(int width,int height) {
//
//            }
//
//            @Override
//            public void failed() {
//
//            }
//        });
//    }
//
//    @Override
//    public void userLogout() {
//        ShareSdkHelper.logout(mContext);
//        mHomeUserIcon.setImageResource(R.drawable.ic_user_login_default);
//        mHomeLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//        mHomeLogin.setText(Html.fromHtml(mContext.getResources().getString(R.string.home_login_text)));
//        mHomeLogin.setOnClickListener(this);
//    }
//}
