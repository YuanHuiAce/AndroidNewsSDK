package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jinsedeyuzhou.VPlayPlayer;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.entity.AuthorizedUser;
import com.news.sdk.entity.User;
import com.news.sdk.utils.manager.PlayerManager;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.utils.manager.UserManager;
import com.news.yazhidao.MainView;
import com.news.yazhidao.R;
import com.umeng.analytics.MobclickAgent;


public class MainActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener {
    private static final String TAG = "MainActivity";
    RelativeLayout newsLayout;
    MainView mainView;
    private TextView mFirstAndTop;
    private UserReceiver mReceiver;
    private AuthorizedUser authorizedUser;
    public static VPlayPlayer vPlayPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //umeng统计
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //显示个人中心
        SharedPreManager.mInstance(this).save(CommonConstant.FILE_USER_CENTER, CommonConstant.USER_CENTER_SHOW, false);
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
        authorizedUser = (AuthorizedUser) getIntent().getSerializableExtra(CommonConstant.LOGIN_AUTHORIZEDUSER_ACTION);
        if (authorizedUser != null) {
            mainView.setAuthorizedUserInformation(authorizedUser);
        }
        //注册登录监听广播
        mReceiver = new UserReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonConstant.USER_LOGIN_ACTION);
        filter.addAction(CommonConstant.SHARE_WECHAT_MOMENTS_ACTION);
        filter.addAction(CommonConstant.SHARE_WECHAT_ACTION);
        filter.addAction(CommonConstant.SHARE_SINA_WEIBO_ACTION);
        filter.addAction(CommonConstant.SHARE_QQ_ACTION);
        registerReceiver(mReceiver, filter);
        newsLayout.addView(mainView.getNewsView());
        ThemeManager.registerThemeChangeListener(this);
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
        startActivityForResult(intent, CommonConstant.REQUEST_LOGIN_CODE);
    }

    private class UserReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CommonConstant.USER_LOGIN_ACTION.equals(action)) {
                userLogin();
            } else if (CommonConstant.SHARE_WECHAT_MOMENTS_ACTION.equals(action)) {
                //调用微信朋友圈分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                Log.i("tag", shareTitle + "<====>" + shareUrl);
            } else if (CommonConstant.SHARE_WECHAT_ACTION.equals(action)) {
                //调用微信分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                Log.i("tag", shareTitle + "<====>" + shareUrl);
            } else if (CommonConstant.SHARE_SINA_WEIBO_ACTION.equals(action)) {
                //调用新浪微博分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                Log.i("tag", shareTitle + "<====>" + shareUrl);
            } else if (CommonConstant.SHARE_QQ_ACTION.equals(action)) {
                //调用QQ分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                Log.i("tag", shareTitle + "<====>" + shareUrl);
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
        if (CommonConstant.REQUEST_LOGIN_CODE == requestCode && CommonConstant.RESULT_LOGIN_CODE == resultCode) {
            AuthorizedUser user = (AuthorizedUser) data.getSerializableExtra(CommonConstant.LOGIN_AUTHORIZEDUSER_ACTION);
            mainView.setAuthorizedUserInformation(user);
        }
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
}
