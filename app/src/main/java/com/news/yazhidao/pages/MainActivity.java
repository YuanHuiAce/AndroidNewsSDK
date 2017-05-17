package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jinsedeyuzhou.VPlayPlayer;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.entity.User;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.PlayerManager;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.utils.manager.UserManager;
import com.news.yazhidao.MainView;
import com.news.yazhidao.R;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


public class MainActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener {
    RelativeLayout newsLayout;
    MainView mainView;
    private TextView mFirstAndTop;
    private UserReceiver mReceiver;
    public static VPlayPlayer vPlayPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //umeng统计
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //显示个人中心
        SharedPreManager.mInstance(this).save(CommonConstant.FILE_USER_CENTER, CommonConstant.USER_CENTER_SHOW, true);
        //activity 跳转
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CrashReport.testJavaCrash();
                int size = SharedPreManager.mInstance(MainActivity.this).getInt("showflag", "textSize");
                if (size == MainView.FONTSIZE.TEXT_SIZE_BIG.getfontsize()) {
                    mainView.setTextSize(MainView.FONTSIZE.TEXT_SIZE_NORMAL);
                } else {
                    mainView.setTextSize(MainView.FONTSIZE.TEXT_SIZE_BIG);
                }
            }
        });
        mFirstAndTop = (TextView) findViewById(R.id.mFirstAndTop);
        mFirstAndTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.backFirstItemAndRefreshData();
            }
        });
        //用户登录成功后对sdk进行用户映射
        TextView login = (TextView) findViewById(R.id.tvLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User visitorUser = SharedPreManager.mInstance(MainActivity.this).getUser(MainActivity.this);
                if (null != visitorUser) {
                    userLogin();
                } else {
                    UserManager.registerVisitor(MainActivity.this, new UserManager.RegisterVisitorListener() {
                        @Override
                        public void registerSuccess() {
                            userLogin();
                        }
                    });
                }
            }
        });
        //退出登录
        TextView logout = (TextView) findViewById(R.id.tvLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User visitorUser = SharedPreManager.mInstance(MainActivity.this).getUser(MainActivity.this);
                if (null != visitorUser) {
                    visitorUser.setUtype("2");
                    visitorUser.setUserName("");
                    visitorUser.setUserIcon("");
                    visitorUser.setVisitor(true);
                    SharedPreManager.mInstance(MainActivity.this).saveUser(visitorUser);
                    //更新user图标
                    if (SharedPreManager.mInstance(MainActivity.this).getUserCenterIsShow()) {
                        mainView.setUserCenterImg("");
                    }
                }
            }
        });
        //添加View
        newsLayout = (RelativeLayout) findViewById(R.id.newsLayout);
        mainView = new MainView(this); //传入的activity是FragmentActivity
        /**梁帅：修改智能模式（不显示图片）*/
//        mainView.setNotShowImages(false);
        /**梁帅：修改文字大小的方法*/
        mainView.setTextSize(MainView.FONTSIZE.TEXT_SIZE_NORMAL);
        /**梁帅：修改屏幕是否常亮的方法*/
        mainView.setKeepScreenOn(true);
        User user = SharedPreManager.mInstance(this).getUser(this);
        mainView.setUserCenterImg(user.getUserIcon());
        //注册登录监听广播
        mReceiver = new UserReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonConstant.USER_LOGIN_ACTION);
        filter.addAction(CommonConstant.USER_LOGOUT_ACTION);
        filter.addAction(CommonConstant.USER_LOGIN_SUCCESS_ACTION);
        filter.addAction(CommonConstant.SHARE_WECHAT_MOMENTS_ACTION);
        filter.addAction(CommonConstant.SHARE_WECHAT_ACTION);
        filter.addAction(CommonConstant.SHARE_SINA_WEIBO_ACTION);
        filter.addAction(CommonConstant.SHARE_QQ_ACTION);
        registerReceiver(mReceiver, filter);
        newsLayout.addView(mainView.getNewsView());
        ThemeManager.registerThemeChangeListener(this);
        changeDayNightMode();
    }

    //设置字体大小不随手机设置而改变
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    private void userLogin() {
        //调用自己的登录授权界面
        Intent intent = new Intent(MainActivity.this, GuideLoginAty.class);
        startActivity(intent);
    }

    private class UserReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CommonConstant.USER_LOGIN_ACTION.equals(action)) {
                userLogin();
            } else if (CommonConstant.USER_LOGOUT_ACTION.equals(action)) {
                mainView.setUserCenterImg("");
            } else if (CommonConstant.USER_LOGIN_SUCCESS_ACTION.equals(action)) {
                User user = SharedPreManager.mInstance(MainActivity.this).getUser(MainActivity.this);
                mainView.setUserCenterImg(user.getUserIcon());
            } else if (CommonConstant.SHARE_WECHAT_MOMENTS_ACTION.equals(action)) {
                //调用微信朋友圈分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                Log.i("tag", shareTitle + "<====>" + shareUrl);
                ShareToPlatformByNewsDetail(WechatMoments.NAME, shareTitle, shareUrl, "");
            } else if (CommonConstant.SHARE_WECHAT_ACTION.equals(action)) {
                //调用微信分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                Log.i("tag", shareTitle + "<====>" + shareUrl);
                ShareToPlatformByNewsDetail(Wechat.NAME, shareTitle, shareUrl, "");
            } else if (CommonConstant.SHARE_SINA_WEIBO_ACTION.equals(action)) {
                //调用新浪微博分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                Log.i("tag", shareTitle + "<====>" + shareUrl);
                ShareToPlatformByNewsDetail(SinaWeibo.NAME, shareTitle, shareUrl, "");
            } else if (CommonConstant.SHARE_QQ_ACTION.equals(action)) {
                //调用QQ分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                Log.i("tag", shareTitle + "<====>" + shareUrl);
                ShareToPlatformByNewsDetail(QQ.NAME, shareTitle, shareUrl, "");
            }
        }
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //设置频道的回调
        super.onActivityResult(requestCode, resultCode, data);
        mainView.onActivityResult(requestCode, resultCode, data);
    }

    //梁帅: 点击返回如果不喜欢窗口是显示的，隐藏它；
    //如果是不显示的直接退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mainView != null && mainView.closePopWindow()) {
                return true;
            }
            if (PlayerManager.videoPlayView != null) {
                if (PlayerManager.videoPlayView.onKeyDown(keyCode, event))
                    return true;
            }
            if (vPlayPlayer != null) {
                if (vPlayPlayer.onKeyDown(keyCode, event))
                    return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        ThemeManager.unregisterThemeChangeListener(this);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        mainView.unregisterNetWorkReceiver();

        if (PlayerManager.videoPlayView != null) {
            PlayerManager.videoPlayView.onDestory();
            PlayerManager.videoPlayView = null;
        }

        if (vPlayPlayer != null) {
            vPlayPlayer.onDestory();
            vPlayPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onThemeChanged() {
        mainView.setTheme();
    }


    /**
     * 日夜间模式切换方法
     */
    public void changeDayNightMode() {
        ThemeManager.setThemeMode(ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY
                ? ThemeManager.ThemeMode.NIGHT : ThemeManager.ThemeMode.DAY);
    }

    public void ShareToPlatformByNewsDetail(final String argPlatform, final String title, final String url, final String remark) {
        PlatformActionListener pShareListner = new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
                ToastUtil.toastShort("分享成功");
            }

            @Override
            public void onError(Platform platform, final int i, Throwable throwable) {
                ToastUtil.toastShort("分享失败");
            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        };

        Platform.ShareParams pShareParams = new Platform.ShareParams();
        pShareParams.setImageData(BitmapFactory.decodeResource(QiDianApplication.getAppContext().getResources(), R.mipmap.ic_launcher));
//        pShareParams.setImageUrl("http://www.wyl.cc/wp-content/uploads/2014/02/10060381306b675f5c5.jpg");
        if (argPlatform.equals(Wechat.NAME) || argPlatform.equals(WechatMoments.NAME)) {
            pShareParams.setShareType(Platform.SHARE_WEBPAGE);
            pShareParams.setTitle(title);
            pShareParams.setUrl(url);
        } else {
            pShareParams.setText(title + url);
        }
        if (argPlatform.equals(Wechat.NAME)) {
            Platform platform = ShareSDK.getPlatform(Wechat.NAME);
            if (!platform.isClientValid()) {
                ToastUtil.toastShort("未安装微信");
                return;
            }
            platform.setPlatformActionListener(pShareListner);
            if (TextUtil.isEmptyString(remark))
                pShareParams.setText("资讯分享社区");
            else
                pShareParams.setText(remark);
            platform.share(pShareParams);
        } else if (argPlatform.equals(WechatMoments.NAME)) {
            Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
            if (!platform.isClientValid()) {
                ToastUtil.toastShort("未安装微信");
                return;
            }
            platform.setPlatformActionListener(pShareListner);
            platform.share(pShareParams);
        } else if (argPlatform.equals(SinaWeibo.NAME)) {
            Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
            platform.setPlatformActionListener(pShareListner);
            platform.share(pShareParams);
        } else if (argPlatform.equals(QQ.NAME)) {
            Platform platform = ShareSDK.getPlatform(QQ.NAME);
            platform.setPlatformActionListener(pShareListner);
            pShareParams.setTitle(title);
            pShareParams.setTitleUrl(url);
            pShareParams.setText("奇点资讯分享社区");
            platform.share(pShareParams);
        }
    }
}
