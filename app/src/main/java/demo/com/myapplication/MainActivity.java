package demo.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jinsedeyuzhou.PlayerManager;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.ThemeManager;
import com.news.yazhidao.entity.AuthorizedUser;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.AuthorizedUserUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener {
    private static final String TAG = "MainActivity";
    RelativeLayout newsLayout;
    MainView mainView;
    private TextView mFirstAndTop;
    private UserReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //显示个人中心
        SharedPreManager.mInstance(this).save("flag", "showUserCenter", true);
        //activity 跳转
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    setAuthorizedUserInformation();
                } else {
                    UserManager.registerVisitor(MainActivity.this, new UserManager.RegisterVisitorListener() {
                        @Override
                        public void registerSuccess() {
                            setAuthorizedUserInformation();
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
                    if (SharedPreManager.mInstance(MainActivity.this).getBoolean("flag", "showUserCenter")) {
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

    public void setAuthorizedUserInformation() {
        AuthorizedUser user = new AuthorizedUser();
        //游客合并三方时提供该字段
        user.setMuid(SharedPreManager.mInstance(MainActivity.this).getUser(MainActivity.this).getMuid());
        //三方之间进行合并时提供该字段
        user.setMsuid("");
        //用户类型 - 本地注册用户:1, 游客用户:2 ,微博三方用户:3 ,微信三方用户:4, 黄历天气:12, 纹字锁频:13, 猎鹰浏览器:14, 白牌:15
        user.setUtype(3);
        //平台类型 - IOS:1, 安卓:2, 网页:3, 无法识别:4
        user.setPlatform(2);
        //第三方用户id
        user.setSuid("3344667788");
        //第三方登录token
        user.setStoken("2233445566");
        //过期时间
        user.setSexpires("2016-5-27 17:37:22");
        //用户名
        user.setUname("test");
        //性别 0:男 1:女
        user.setGender(0);
        //头像地址
        user.setAvatar("http://tva4.sinaimg.cn/crop.106.0.819.819.1024/d869d439jw8etv9fxkb1uj20rt0mrwh7.jpg");
        //用户屏蔽字段列表  没有可以不填
        ArrayList<String> averse = new ArrayList<>();
        averse.add("政治");
        averse.add("战争");
        averse.add("腐败");
        user.setAverse(averse);
        //用户偏好字段列表 没有可以不填
        ArrayList<String> prefer = new ArrayList<>();
        prefer.add("体育");
        prefer.add("音乐");
        prefer.add("杂志");
        user.setAverse(prefer);
        //用户地理位置信息 没有可以不填
        user.setProvince("河南省");
        user.setCity("郑州市");
        user.setDistrict("二七区");
        //授权用户映射
        AuthorizedUserUtil.authorizedUser(user, this);
        //更新user图标
        if (SharedPreManager.mInstance(this).getBoolean("flag", "showUserCenter")) {
            mainView.setUserCenterImg(user.getAvatar());
        }
    }

    private class UserReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CommonConstant.USER_LOGIN_ACTION.equals(action)) {
                //调用登录界面 授权成功后
                ToastUtil.toastLong("请先登录");
                setAuthorizedUserInformation();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //设置频道的回调
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
