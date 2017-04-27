package com.news.yazhidao.common;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.news.yazhidao.R;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivityHelper;


/**
 * Created by feng on 3/23/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected SwipeBackActivityHelper mHelper;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        if (isNeedAnimation()) {
            overridePendingTransition(R.anim.qd_aty_right_enter, R.anim.qd_aty_no_ani);
        }
        //fixed:Android 4.4 以上通知栏沉浸式，兼容xiaomi 4.1.2 和华为 4.1.2 系统等
        if (translucentStatus()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//              getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        setContentView();
        initializeViews();
        loadData();
    }

    protected abstract void setContentView();

    protected abstract void initializeViews();

    protected abstract void loadData();

    /**
     * 是否使用状态栏的沉浸式 默认使用
     *
     * @return
     */
    protected boolean translucentStatus() {
        return true;
    }

    /**
     * 子类activity是否在进入和退出的时候需要动画
     *
     * @return
     */
    protected boolean isNeedAnimation() {
        return true;
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

    @Override
    public void finish() {
        super.finish();
        if (isNeedAnimation()) {
            overridePendingTransition(0, R.anim.qd_aty_left_exit);
        }
    }
}
