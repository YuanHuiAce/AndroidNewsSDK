package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.entity.User;
import com.news.sdk.utils.AdUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.PlayerManager;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.utils.manager.UserManager;
import com.news.sdk.widget.NegativeScreenNewsFeedView;
import com.news.yazhidao.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import static com.taobao.accs.ACCSManager.mContext;


public class NegativeScreenActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener {
    NegativeScreenNewsFeedView mainView;
    RelativeLayout newsLayout;
    private ClipboardManager cmb;
    private UserReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        AdUtil.setAdChannel(this);
        UserManager.registerVisitor(this, null);
        //展示广点通sdk
        SharedPreManager.mInstance(this).getBoolean(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE, true);
        //展示广点通API
        SharedPreManager.mInstance(this).getBoolean(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE, false);
        setContentView(R.layout.activity_main);
        cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ThemeManager.registerThemeChangeListener(this);
        newsLayout = (RelativeLayout) findViewById(R.id.newsLayout);
        mainView = new NegativeScreenNewsFeedView(this);
        mainView.setChannelId(1);
        mainView.setChannelId(SharedPreManager.mInstance(mContext).getNegativeChannelInt(CommonConstant.FILE_AD, CommonConstant.NEGATIVE_CHANNEL));
        newsLayout.addView(mainView.getNewsView());
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
        uploadInformation();
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

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        ThemeManager.unregisterThemeChangeListener(this);
        mainView.destroyView();

        if (PlayerManager.videoPlayView != null) {
            PlayerManager.videoPlayView.onDestory();
            PlayerManager.videoPlayView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (PlayerManager.videoPlayView != null) {
            if (PlayerManager.videoPlayView.onKeyDown(keyCode, event))
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (PlayerManager.videoPlayView != null)
        {
            PlayerManager.videoPlayView.onPause();
        }
    }

    @Override
    public void onBackPressed() {
        Log.v("FocusId:",  this.getWindow().getDecorView().findFocus()+"");
        if (!mainView.removeView()) {
            this.finish();
        }
    }

    private void uploadInformation() {
        if (SharedPreManager.mInstance(this).getUser(this) != null) {
            try {
                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                final JSONArray array = new JSONArray();
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("app_id", packageInfo.packageName);
                    jsonObject.put("active", 1);
                    jsonObject.put("app_name", packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                    array.put(jsonObject);
                }
                /** 设置品牌 */
                final String brand = Build.BRAND;
                /** 设置设备型号 */
                final String platform = Build.MODEL;
                final String requestUrl = HttpConstant.URL_UPLOAD_INFORMATION;
                RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                Long uid = null;
                User user = SharedPreManager.mInstance(this).getUser(this);
                if (user != null) {
                    uid = Long.valueOf(user.getMuid());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", uid);
                jsonObject.put("province", SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                jsonObject.put("city", SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                jsonObject.put("area", SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
                jsonObject.put("brand", brand);
                jsonObject.put("model", platform);
                jsonObject.put("apps", array);
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST, requestUrl,
                        jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObj) {
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                requestQueue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onThemeChanged() {
        mainView.initTheme();
    }

    private class UserReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CommonConstant.SHARE_WECHAT_MOMENTS_ACTION.equals(action)) {
                //调用微信朋友圈分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                ShareToPlatformByNewsDetail(WechatMoments.NAME, shareTitle, shareUrl, "");
            } else if (CommonConstant.SHARE_WECHAT_ACTION.equals(action)) {
                //调用微信分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                ShareToPlatformByNewsDetail(Wechat.NAME, shareTitle, shareUrl, "");
            } else if (CommonConstant.SHARE_SINA_WEIBO_ACTION.equals(action)) {
                //调用新浪微博分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                ShareToPlatformByNewsDetail(SinaWeibo.NAME, shareTitle, shareUrl, "");
            } else if (CommonConstant.SHARE_QQ_ACTION.equals(action)) {
                //调用QQ分享
                String shareTitle = intent.getStringExtra(CommonConstant.SHARE_TITLE);
                String shareUrl = intent.getStringExtra(CommonConstant.SHARE_URL);
                ShareToPlatformByNewsDetail(QQ.NAME, shareTitle, shareUrl, "");
            }
        }
    }

    /**
     * 日夜间模式切换方法
     */
    public void changeDayNightMode() {
        ThemeManager.setThemeMode(ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY
                ? ThemeManager.ThemeMode.NIGHT : ThemeManager.ThemeMode.DAY);
    }

    public void ShareToPlatformByNewsDetail(final String argPlatform, String title, String url, final String remark) {
        if (TextUtil.isEmptyString(title)) {
            title = "";
        }
        if (TextUtil.isEmptyString(url)) {
            url = "";
        }
        ShareSDK.initSDK(this);
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
        if (null != pShareParams) {
            pShareParams.setImageData(BitmapFactory.decodeResource(QiDianApplication.getAppContext().getResources(), R.mipmap.ic_launcher));
            if (argPlatform.equals(Wechat.NAME) || argPlatform.equals(WechatMoments.NAME)) {
                pShareParams.setShareType(Platform.SHARE_WEBPAGE);
                pShareParams.setTitle(title);
                pShareParams.setUrl(url);
            } else {
                pShareParams.setText(title + "" + url);
            }
            if (argPlatform.equals(Wechat.NAME)) {
                Platform platform = ShareSDK.getPlatform(Wechat.NAME);
                if (!platform.isClientValid()) {
                    ToastUtil.toastShort("未安装微信");
                    return;
                }
                cmb.setPrimaryClip(ClipData.newPlainText(null, title + "" + url));
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
                cmb.setPrimaryClip(ClipData.newPlainText(null, title + "" + url));
                platform.setPlatformActionListener(pShareListner);
                platform.share(pShareParams);
            } else if (argPlatform.equals(SinaWeibo.NAME)) {
                Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
                if (!platform.isClientValid()) {
                    ToastUtil.toastShort("未安装新浪微博");
                    return;
                }
                platform.SSOSetting(false);
                platform.setPlatformActionListener(pShareListner);
                platform.share(pShareParams);
            } else if (argPlatform.equals(QQ.NAME)) {
                Platform platform = ShareSDK.getPlatform(QQ.NAME);
                platform.setPlatformActionListener(pShareListner);
                pShareParams.setTitle(title);
                pShareParams.setTitleUrl(url);
                pShareParams.setText("奇点资讯分享社区");
                platform.SSOSetting(false);
                platform.share(pShareParams);
            }
        }
    }


}
