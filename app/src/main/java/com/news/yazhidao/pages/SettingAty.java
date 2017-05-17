package com.news.yazhidao.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.database.NewsFeedDao;
import com.news.sdk.entity.User;
import com.news.sdk.entity.Version;
import com.news.sdk.net.volley.VersionRequest;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.CustomDialog;
import com.news.yazhidao.R;
import com.news.yazhidao.service.UpdateService;


public class SettingAty extends BaseActivity implements View.OnClickListener {

    public final static int RESULT_CODE = 1008;
    public static final String KEY_NEED_NOT_SETTING = "key_need_not_setting";

    private TextView mSettingLogout;
    private User user;
    private View mSettingPushSwitch;
    private ImageView mSettingPushImg;
    private RadioGroup mRadioGroup;
    private View mSettingClearCache;
    private View mSettingtLeftBack;
    private View mSettingAbout;
    private View mSettingPrivacyPolicy;
    private View mSettingUpdate;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_setting);
    }

    @Override
    protected void initializeViews() {
        mSettingtLeftBack = findViewById(R.id.mSettingLeftBack);
        mSettingtLeftBack.setOnClickListener(this);
        mSettingLogout = (TextView) findViewById(R.id.mSettingLogout);
        mSettingLogout.setOnClickListener(this);
        mSettingPushSwitch = findViewById(R.id.mSettingPushSwitch);
        mSettingPushSwitch.setOnClickListener(this);
        mSettingPushImg = (ImageView) findViewById(R.id.mSettingPushImg);
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        mSharedPreferences = getSharedPreferences("showflag", MODE_PRIVATE);
        int saveFont = mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL);
        switch (saveFont) {
            case CommonConstant.TEXT_SIZE_NORMAL:
                mRadioGroup.check(R.id.mRadioNormal);
                break;
            case CommonConstant.TEXT_SIZE_BIG:
                mRadioGroup.check(R.id.mRadioBig);
                break;
            case CommonConstant.TEXT_SIZE_BIGGER:
                mRadioGroup.check(R.id.mRadioBigger);
                break;
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.mRadioNormal:
                        mSharedPreferences.edit().putInt("textSize", CommonConstant.TEXT_SIZE_NORMAL).commit();
                        break;
                    case R.id.mRadioBig:
                        mSharedPreferences.edit().putInt("textSize", CommonConstant.TEXT_SIZE_BIG).commit();
                        break;
                    case R.id.mRadioBigger:
                        mSharedPreferences.edit().putInt("textSize", CommonConstant.TEXT_SIZE_BIGGER).commit();
                        break;
                }
            }
        });
        mSettingClearCache = findViewById(R.id.mSettingClearCache);
        mSettingClearCache.setOnClickListener(this);
        mSettingAbout = findViewById(R.id.mSettingAbout);
        mSettingAbout.setOnClickListener(this);
        mSettingPrivacyPolicy = findViewById(R.id.mSettingPrivacyPolicy);
        mSettingPrivacyPolicy.setOnClickListener(this);
        mSettingUpdate = findViewById(R.id.mSettingUpdate);
        mSettingUpdate.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected boolean isNeedAnimation() {
        return true;
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = SharedPreManager.mInstance(this).getUser(this);
        if (user != null && !user.isVisitor()) {
            mSettingLogout.setText("退出登录");
            mSettingLogout.setTextColor(getResources().getColor(R.color.new_color2));
        } else {
            mSettingLogout.setText("点击登录");
            mSettingLogout.setTextColor(getResources().getColor(R.color.new_color1));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSettingLeftBack:
                finish();
                break;
            case R.id.mSettingPushSwitch:
//                PushAgent pushAgent = PushAgent.getInstance(this);
//                if (pushAgent.isEnabled()) {
//                    pushAgent.disable();
//                    mSettingPushImg.setImageResource(R.drawable.ic_setting_push_off);
//                } else {
//                    pushAgent.enable();
//                    mSettingPushImg.setImageResource(R.drawable.ic_setting_push_on);
//                }
                break;
            case R.id.mSettingClearCache:
                AlertDialog.Builder clearBuilder = new AlertDialog.Builder(this);
                clearBuilder.setMessage("缓存文件可以帮助您节约流量,但较大时会占用较多的磁盘空间。\n确定开始清理吗?");
                clearBuilder.setTitle("提示");
                clearBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                1.清理webview 中缓存的数据
//                        DataCleanManager.clearWebViewCache(SettingAty.this);
//                2.删除缓存的新闻数据
                        NewsFeedDao newsFeedDao = new NewsFeedDao(SettingAty.this);
                        newsFeedDao.deleteAllData();
                        try {
                            ToastUtil.toastShort("清理缓存已完成");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                clearBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                clearBuilder.create().show();
                break;
            case R.id.mSettingAbout:
                Intent aboutAty = new Intent(this, AboutAty.class);
                startActivity(aboutAty);
                break;
            case R.id.mSettingPrivacyPolicy:
                Intent privacyAty = new Intent(this, PrivacyPolicyAty.class);
                startActivity(privacyAty);
                break;
            case R.id.mSettingUpdate:
                checkVersion();
                break;
            case R.id.mSettingLogout:
                if (user != null && !user.isVisitor()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("确认退出吗?");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SharedPreManager.mInstance(SettingAty.this).deleteUser(SettingAty.this);
                            setResult(RESULT_CODE, null);
                            sendBroadcast(new Intent(CommonConstant.USER_LOGOUT_ACTION));
                            finish();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    Intent loginAty = new Intent(this, GuideLoginAty.class);
                    loginAty.putExtra(KEY_NEED_NOT_SETTING, true);
                    startActivity(loginAty);
                }
                break;
        }
    }

    /**
     * 检测是否自动升级
     */
    private void checkVersion() {
        User mUser = SharedPreManager.mInstance(this).getUser(this);
        VersionRequest<Version> versionRequest = new VersionRequest<Version>(Request.Method.GET,
                Version.class, HttpConstant.URL_APK_UPDATE + (mUser != null ? "&uid=" + SharedPreManager.mInstance(SettingAty.this).getUser(SettingAty.this).getMuid() : "") + "&ctype=" + CommonConstant.NEWS_CTYPE + "&ptype=" + CommonConstant.NEWS_PTYPE,
                new Response.Listener<Version>() {
                    @Override
                    public void onResponse(Version response) {
                        if (DeviceInfoUtil.getApkVersionCode(SettingAty.this) < response.getVersion_code()) {
                            showUpdateDialog(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        QiDianApplication.getInstance().getRequestQueue().add(versionRequest);
    }

    /**
     * 自定义升级弹窗
     */
    protected void showUpdateDialog(final Version version) {
        CustomDialog.Builder builder = new CustomDialog.Builder(SettingAty.this);
        builder.setTitle("发现新版本");
        builder.setMessage(version.getUpdateLog());
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (version.isForceUpdate()) {
                    ((Activity) SettingAty.this).finish();
                }
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(SettingAty.this, UpdateService.class);
                intent.putExtra("downloadLink", version.getDownloadLink());
                intent.putExtra("md5", version.getMd5());
                bundle.putSerializable("version", version);
                SettingAty.this.startService(intent);
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    public void onThemeChanged() {

    }
}
