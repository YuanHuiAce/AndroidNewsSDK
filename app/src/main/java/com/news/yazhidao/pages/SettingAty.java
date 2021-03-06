package com.news.yazhidao.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.database.NewsFeedDao;
import com.news.sdk.entity.User;
import com.news.sdk.entity.Version;
import com.news.sdk.net.volley.VersionRequest;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.CustomDialogUpdate;
import com.umeng.message.IUmengCallback;
import com.umeng.message.PushAgent;
import com.news.yazhidao.R;
import com.news.yazhidao.service.UpdateService;

import org.json.JSONException;
import org.json.JSONObject;

import static com.taobao.accs.ACCSManager.mContext;


public class SettingAty extends BaseActivity implements View.OnClickListener {

    public final static int RESULT_CODE = 1008;
    public static final String KEY_NEED_NOT_SETTING = "key_need_not_setting";
    private View mHeaderDivider, mLine0, mLine1, mLine2, mLine3;
    private LinearLayout mSettingSection1, mSettingSection3;
    private RelativeLayout bgLayout, mSettingHeader, mSettingSection2;
    private TextView mSettingLogout, mSetting;
    private TextView mPushText, mFontSizeText, mDayNightText, mSettingClearCache, mAboutText, mPrivacyPolicyText, mUpdateText;
    private User user;
    private View mSettingPushSwitch, mSettingDayNight;
    private ImageView mSettingPushImg, mDayNightImg;
    private ImageView mPushIcon, mDayNightIcon, mFontSizeIcon, mSettingClearIcon, mAboutIcon, mPrivacyPolicyIcon, mUpdateIcon;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioNormal, mRadioBig, mRadioBigger;
    private ImageView mSettingLeftBack;
    private View mSettingAbout;
    private View mSettingPrivacyPolicy;
    private View mSettingUpdate;
    private SharedPreferences mSharedPreferences;
    private boolean isEnabled;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_setting);
    }

    @Override
    protected void initializeViews() {
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mSettingSection1 = (LinearLayout) findViewById(R.id.mSettingSection1);
        mSettingSection2 = (RelativeLayout) findViewById(R.id.mSettingSection2);
        mSettingSection3 = (LinearLayout) findViewById(R.id.mSettingSection3);
        mLine0 = findViewById(R.id.mLine0);
        mLine1 = findViewById(R.id.mLine1);
        mLine2 = findViewById(R.id.mLine2);
        mLine3 = findViewById(R.id.mLine3);
        mPushIcon = (ImageView) findViewById(R.id.mPushIcon);
        mDayNightIcon = (ImageView) findViewById(R.id.mDayNightIcon);
        mFontSizeIcon = (ImageView) findViewById(R.id.mFontSizeIcon);
        mSettingClearIcon = (ImageView) findViewById(R.id.mSettingClearIcon);
        mAboutIcon = (ImageView) findViewById(R.id.mAboutIcon);
        mPrivacyPolicyIcon = (ImageView) findViewById(R.id.mPrivacyPolicyIcon);
        mUpdateIcon = (ImageView) findViewById(R.id.mUpdateIcon);
        mSettingHeader = (RelativeLayout) findViewById(R.id.mSettingHeader);
        mSettingLeftBack = (ImageView) findViewById(R.id.mSettingLeftBack);
        mSettingLeftBack.setOnClickListener(this);
        mSettingLogout = (TextView) findViewById(R.id.mSettingLogout);
        mSettingLogout.setOnClickListener(this);
        mRadioNormal = (RadioButton) findViewById(R.id.mRadioNormal);
        mRadioBig = (RadioButton) findViewById(R.id.mRadioBig);
        mRadioBigger = (RadioButton) findViewById(R.id.mRadioBigger);
        mSetting = (TextView) findViewById(R.id.mSetting);
        mPushText = (TextView) findViewById(R.id.mPushText);
        mFontSizeText = (TextView) findViewById(R.id.mFontSizeText);
        mDayNightText = (TextView) findViewById(R.id.mDayNightText);
        mSettingClearCache = (TextView) findViewById(R.id.mSettingClearCache);
        mAboutText = (TextView) findViewById(R.id.mAboutText);
        mPrivacyPolicyText = (TextView) findViewById(R.id.mPrivacyPolicyText);
        mUpdateText = (TextView) findViewById(R.id.mUpdateText);
        mHeaderDivider = findViewById(R.id.mHeaderDivider);
        mSettingPushSwitch = findViewById(R.id.mSettingPushSwitch);
        mSettingPushSwitch.setOnClickListener(this);
        mSettingPushImg = (ImageView) findViewById(R.id.mSettingPushImg);
        mSettingDayNight = findViewById(R.id.mSettingDayNight);
        mSettingDayNight.setOnClickListener(this);
        mDayNightImg = (ImageView) findViewById(R.id.mDayNightImg);
        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY) {
            mDayNightImg.setImageResource(R.mipmap.ic_setting_push_off);
        } else {
            mDayNightImg.setImageResource(R.mipmap.ic_setting_push_on);
        }
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        mSharedPreferences = getSharedPreferences("showflag", MODE_PRIVATE);
        int saveFont = mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL);
        isEnabled = mSharedPreferences.getBoolean("isEnabled", false);
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
                LogUtil.userActionLog(SettingAty.this, CommonConstant.LOG_ATYPE_FONTSETTING, CommonConstant.LOG_PAGE_SETTINGPAGE, CommonConstant.LOG_PAGE_SETTINGPAGE, null, false);
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
        mSettingClearCache.setOnClickListener(this);
        mSettingAbout = findViewById(R.id.mSettingAbout);
        mSettingAbout.setOnClickListener(this);
        mSettingPrivacyPolicy = findViewById(R.id.mSettingPrivacyPolicy);
        mSettingPrivacyPolicy.setOnClickListener(this);
        mSettingUpdate = findViewById(R.id.mSettingUpdate);
        mSettingUpdate.setOnClickListener(this);
        setTheme();
        if (isEnabled) {
            mSettingPushImg.setImageResource(R.mipmap.ic_setting_push_on);
        } else {
            mSettingPushImg.setImageResource(R.mipmap.ic_setting_push_off);
        }
    }

    private void setTheme() {
        TextUtil.setLayoutBgResource(this, bgLayout, R.color.color6);
        TextUtil.setLayoutBgResource(this, mSettingLeftBack, R.drawable.bg_left_back_selector);
        TextUtil.setImageResource(this, mSettingLeftBack, R.drawable.btn_left_back);
        TextUtil.setLayoutBgResource(this, mSettingHeader, R.color.color6);
        TextUtil.setLayoutBgResource(this, mHeaderDivider, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLine0, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLine1, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLine2, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLine3, R.color.color5);
        TextUtil.setImageResource(this, mPushIcon, R.mipmap.ic_setting_push_switch);
        TextUtil.setImageResource(this, mDayNightIcon, R.mipmap.ic_setting_night);
        TextUtil.setImageResource(this, mFontSizeIcon, R.mipmap.ic_setting_font);
        TextUtil.setImageResource(this, mSettingClearIcon, R.mipmap.ic_setting_clear_cache);
        TextUtil.setImageResource(this, mAboutIcon, R.mipmap.ic_setting_about);
        TextUtil.setImageResource(this, mPrivacyPolicyIcon, R.mipmap.ic_setting_privacy_policy);
        TextUtil.setImageResource(this, mUpdateIcon, R.mipmap.ic_setting_update);
        TextUtil.setLayoutBgResource(this, mSettingSection1, R.drawable.bg_setting_selector);
        TextUtil.setLayoutBgResource(this, mSettingSection2, R.drawable.bg_setting_selector);
        TextUtil.setLayoutBgResource(this, mSettingSection3, R.drawable.bg_setting_selector);
        TextUtil.setLayoutBgResource(this, mSettingLogout, R.color.color9);
        ImageUtil.setAlphaImage(mSettingPushImg);
        ImageUtil.setAlphaImage(mDayNightImg);
        TextUtil.setTextColor(this, mSetting, R.color.color2);
        TextUtil.setTextColor(this, mPushText, R.color.color2);
        TextUtil.setTextColor(this, mDayNightText, R.color.color2);
        TextUtil.setTextColor(this, mFontSizeText, R.color.color2);
        TextUtil.setTextColor(this, mSettingClearCache, R.color.color2);
        TextUtil.setTextColor(this, mAboutText, R.color.color2);
        TextUtil.setTextColor(this, mPrivacyPolicyText, R.color.color2);
        TextUtil.setTextColor(this, mUpdateText, R.color.color2);
        TextUtil.setTextColor(this, mSettingLogout, R.color.color1);
        ImageUtil.setAlphaImage(mRadioNormal);
        ImageUtil.setAlphaImage(mRadioBig);
        ImageUtil.setAlphaImage(mRadioBigger);
    }

    @Override
    protected void loadData() {
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
        } else {
            mSettingLogout.setText("点击登录");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSettingLeftBack:
                finish();
                break;
            case R.id.mSettingPushSwitch:
                PushAgent pushAgent = PushAgent.getInstance(this);
                if (!isEnabled) {
                    pushAgent.enable(new IUmengCallback() {
                        @Override
                        public void onSuccess() {
                            mSharedPreferences.edit().putBoolean("isEnabled", true).commit();
                            isEnabled = true;

                        }

                        @Override
                        public void onFailure(String s, String s1) {

                        }
                    });
                    mSettingPushImg.setImageResource(R.mipmap.ic_setting_push_on);

                } else {
                    pushAgent.disable(new IUmengCallback() {
                        @Override
                        public void onSuccess() {
                            mSharedPreferences.edit().putBoolean("isEnabled", false).commit();
                            isEnabled = false;

                        }

                        @Override
                        public void onFailure(String s, String s1) {

                        }
                    });
                    mSettingPushImg.setImageResource(R.mipmap.ic_setting_push_off);

                }
                break;
            case R.id.mSettingDayNight:
                if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY) {
                    ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT);
                    mDayNightImg.setImageResource(R.mipmap.ic_setting_push_on);
                } else {
                    ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY);
                    mDayNightImg.setImageResource(R.mipmap.ic_setting_push_off);
                }
                LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_CHANGEMODE, CommonConstant.LOG_PAGE_MYMESSAGEPAGE, CommonConstant.LOG_PAGE_MYMESSAGEPAGE, null, false);
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
                JSONObject jsonAbout = new JSONObject();
                try {
                    jsonAbout.put("type", "about");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.userActionLog(SettingAty.this, CommonConstant.LOG_ATYPE_MYSETTING, CommonConstant.LOG_PAGE_SETTINGPAGE, CommonConstant.LOG_PAGE_SETTINGPAGE, jsonAbout, false);
                Intent aboutAty = new Intent(this, AboutAty.class);
                startActivity(aboutAty);
                break;
            case R.id.mSettingPrivacyPolicy:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "privacyPolicy");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.userActionLog(SettingAty.this, CommonConstant.LOG_ATYPE_MYSETTING, CommonConstant.LOG_PAGE_SETTINGPAGE, CommonConstant.LOG_PAGE_SETTINGPAGE, jsonObject, false);
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
//                            showUpdateDialog(response);
                            showUpdateDialogNew(response);
                        } else {
                            Toast.makeText(getApplicationContext(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                });
        QiDianApplication.getInstance().getRequestQueue().add(versionRequest);
    }
    /**
     * 自定义升级弹窗
     */
    protected void showUpdateDialogNew(final Version version) {
        CustomDialogUpdate.Builder builder = new CustomDialogUpdate.Builder(this);
        builder.setMessage(version.getUpdateLog());
        builder.setNegativeButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (version.isForceUpdate()) {
                    ((Activity) SettingAty.this).finish();
                }
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
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


//    /**
//     * 自定义升级弹窗
//     */
//    protected void showUpdateDialog(final Version version) {
//        CustomDialog.Builder builder = new CustomDialog.Builder(SettingAty.this);
//        builder.setTitle("发现新版本");
//        builder.setMessage(version.getUpdateLog());
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (version.isForceUpdate()) {
//                    ((Activity) SettingAty.this).finish();
//                }
//                dialog.dismiss();
//            }
//        });
//
//        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Bundle bundle = new Bundle();
//                Intent intent = new Intent(SettingAty.this, UpdateService.class);
//                intent.putExtra("downloadLink", version.getDownloadLink());
//                intent.putExtra("md5", version.getMd5());
//                bundle.putSerializable("version", version);
//                SettingAty.this.startService(intent);
//                dialog.dismiss();
//            }
//        });
//        builder.setCancelable(false);
//        builder.create().show();
//    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }
}
