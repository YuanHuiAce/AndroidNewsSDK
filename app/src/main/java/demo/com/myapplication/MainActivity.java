package demo.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jinsedeyuzhou.PlayerManager;
import com.news.yazhidao.common.ThemeManager;
import com.news.yazhidao.entity.AuthorizedUser;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.AuthorizedUserUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;

import java.util.ArrayList;

import static com.news.yazhidao.utils.manager.SharedPreManager.mInstance;

public class MainActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener {
    private static final String TAG = "MainActivity";
    RelativeLayout newsLayout;
    MainView mainView;
    private TextView mFirstAndTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SharedPreManager.mInstance(this).save("flag","text1","测试！");
        //activity 跳转
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = mInstance(MainActivity.this).getInt("showflag", "textSize");
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
        newsLayout.addView(mainView.getNewsView());
        ThemeManager.registerThemeChangeListener(this);
    }

    public void setAuthorizedUserInformation() {
        AuthorizedUser user = new AuthorizedUser();
        user.setMuid(SharedPreManager.mInstance(MainActivity.this).getUser(MainActivity.this).getMuid());
        user.setMsuid("");
        user.setUtype(3);
        user.setPlatform(1);
        user.setSuid("123456");
        user.setStoken("234566");
        user.setSexpires("2016-4-27 17:37:22");
        user.setUname("zhang");
        user.setGender(1);
        user.setAvatar("http://tva4.sinaimg.cn/crop.106.0.819.819.1024/d869d439jw8etv9fxkb1uj20rt0mrwh7.jpg");
        ArrayList<String> averse = new ArrayList<>();
        averse.add("政治");
        averse.add("战争");
        averse.add("腐败");
        user.setAverse(averse);
        ArrayList<String> prefer = new ArrayList<>();
        prefer.add("体育");
        prefer.add("音乐");
        prefer.add("杂志");
        user.setAverse(prefer);
        user.setProvince("河南省");
        user.setCity("郑州市");
        user.setDistrict("二七区");
        //授权用户映射
        AuthorizedUserUtil.AuthorizedUser(user, this);
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
