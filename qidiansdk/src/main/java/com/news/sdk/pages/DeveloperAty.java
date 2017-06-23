package com.news.sdk.pages;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.news.sdk.R;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.CustomDeveloperView;

import static com.news.sdk.common.CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE;
import static com.news.sdk.common.CommonConstant.NEWS_DETAIL_GDT_API_BIGPOSID;
import static com.news.sdk.common.CommonConstant.NEWS_DETAIL_GDT_SDK_BIGPOSID;
import static com.news.sdk.common.CommonConstant.NEWS_FEED_GDT_API_BIGPOSID;
import static com.news.sdk.common.CommonConstant.NEWS_FEED_GDT_API_SPLASHPOSID;
import static com.news.sdk.common.CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID;
import static com.news.sdk.common.CommonConstant.NEWS_FEED_GDT_SDK_SPLASHPOSID;
import static com.news.sdk.common.CommonConstant.NEWS_RELATE_GDT_API_SMALLID;
import static com.news.sdk.common.CommonConstant.NEWS_RELATE_GDT_SDK_SMALLPOSID;

/**
 * Created by Berkeley on 6/16/17.
 */

public class DeveloperAty extends BaseActivity implements View.OnClickListener {

    private LinearLayout mDeveloperUser;
    private LinearLayout mDeveloperDevice;
    private LinearLayout mDeveloperProduct;
    private LinearLayout mDeveloperAd;
    private TextView close;
    private TextView copy;
    private LinearLayout mDeveloperLog;
    private StringBuffer copyStr;
    private LinearLayout mDeveloperContainer;
    private View mLineDeveloper;
    private View mTagUser;
    private View mTagDevice;
    private View mTagAd;
    private View mTagLog;
    private View mTagProduct;
    private View mLineUser;
    private View mLineDevice;
    private View mLineProduct;
    private View mLineAd;
    private View mLineLog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private TextView mUser;
    private TextView mDevice;
    private TextView mProduct;
    private TextView mAd;
    private TextView mLog;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_developer);

    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    private void setTheme() {
        TextUtil.setLayoutBgResource(this, mDeveloperContainer, R.color.color6);
        TextUtil.setLayoutBgResource(this, mLineDeveloper, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLineUser, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLineDevice, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLineProduct, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLineAd, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLineLog, R.color.color5);
        TextUtil.setLayoutBgResource(this, mTagUser, R.color.color1);
        TextUtil.setLayoutBgResource(this, mTagDevice, R.color.color1);
        TextUtil.setLayoutBgResource(this, mTagAd, R.color.color1);
        TextUtil.setLayoutBgResource(this, mTagLog, R.color.color1);
        TextUtil.setLayoutBgResource(this, mTagProduct, R.color.color1);

        TextUtil.setTextColor(this, mUser, R.color.color2);
        TextUtil.setTextColor(this, mDevice, R.color.color2);
        TextUtil.setTextColor(this, mProduct, R.color.color2);
        TextUtil.setTextColor(this, mAd, R.color.color2);
        TextUtil.setTextColor(this, mLog, R.color.color2);
        TextUtil.setTextColor(this, close, R.color.color1);
        TextUtil.setTextColor(this, copy, R.color.color1);

    }

    @Override
    protected void initializeViews() {
        mDeveloperContainer = (LinearLayout) findViewById(R.id.developer_container);
        close = (TextView) findViewById(R.id.developer_close);
        copy = (TextView) findViewById(R.id.developer_copy);
        mLineDeveloper = findViewById(R.id.line_developer);
        close.setOnClickListener(this);
        copy.setOnClickListener(this);
        copyStr = new StringBuffer();

        mDeveloperUser = (LinearLayout) findViewById(R.id.ll_developer_user);
        mTagUser = findViewById(R.id.tag_user);
        mTagDevice = findViewById(R.id.tag_device);
        mTagProduct = findViewById(R.id.tag_product);
        mTagAd = findViewById(R.id.tag_ad);
        mTagLog = findViewById(R.id.tag_log);
        mLineUser = findViewById(R.id.line_user);
        mLineDevice = findViewById(R.id.line_device);
        mLineProduct = findViewById(R.id.line_product);
        mLineAd = findViewById(R.id.line_ad);
        mLineLog = findViewById(R.id.line_log);
        mUser = (TextView) findViewById(R.id.tv_developer_user);
        mDevice = (TextView) findViewById(R.id.tv_developer_device);
        mProduct = (TextView) findViewById(R.id.tv_developer_product);
        mAd = (TextView) findViewById(R.id.tv_developer_ad);
        mLog = (TextView) findViewById(R.id.tv_developer_log);
        mDeveloperDevice = (LinearLayout) findViewById(R.id.ll_developer_device);
        mDeveloperProduct = (LinearLayout) findViewById(R.id.ll_developer_product);
        mDeveloperAd = (LinearLayout) findViewById(R.id.ll_developer_ad);
        mDeveloperLog = (LinearLayout) findViewById(R.id.ll_developer_log);
        //用户信息
        CustomDeveloperView uid = new CustomDeveloperView(this);
        uid.setDesText("uid");
        copyStr.append("uid:" + SharedPreManager.mInstance(this).getUser(this).getMuid() + "");
        uid.setInfoText(SharedPreManager.mInstance(this).getUser(this).getMuid() + "");
        mDeveloperUser.addView(uid);

        CustomDeveloperView longitude = new CustomDeveloperView(this);
        longitude.setDesText("所在经度");
        copyStr.append("所在经度:" + SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE));
        longitude.setInfoText(SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE));
        mDeveloperUser.addView(longitude);

        CustomDeveloperView latitude = new CustomDeveloperView(this);
        latitude.setDesText("所在纬度");
        copyStr.append("所在纬度:" + SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE));
        latitude.setInfoText(SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE));
        mDeveloperUser.addView(latitude);

        CustomDeveloperView province = new CustomDeveloperView(this);
        province.setDesText("所在省份城市");
        copyStr.append("所在省份城市:" + SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
        province.setInfoText(SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
        mDeveloperUser.addView(province);

        CustomDeveloperView addr = new CustomDeveloperView(this);
        addr.setDesText("所在街道");
        copyStr.append("所在街道:" + SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
        addr.setInfoText(SharedPreManager.mInstance(this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
        mDeveloperUser.addView(addr);

        //设备信息
        CustomDeveloperView model = new CustomDeveloperView(this);
        model.setDesText("手机型号");
        copyStr.append("手机型号:" + DeviceInfoUtil.getSystemModel());
        model.setInfoText(DeviceInfoUtil.getSystemModel());
        mDeveloperDevice.addView(model);

        CustomDeveloperView brand = new CustomDeveloperView(this);
        brand.setDesText("手机厂商");
        copyStr.append("手机厂商:" + DeviceInfoUtil.getDeviceBrand());
        brand.setInfoText(DeviceInfoUtil.getDeviceBrand());
        mDeveloperDevice.addView(brand);

        CustomDeveloperView systemVersion = new CustomDeveloperView(this);
        systemVersion.setDesText("android 版本");
        copyStr.append("android 版本:" + DeviceInfoUtil.getOsVersion());
        systemVersion.setInfoText(DeviceInfoUtil.getOsVersion());
        mDeveloperDevice.addView(systemVersion);

        CustomDeveloperView imei = new CustomDeveloperView(this);
        imei.setDesText("IMEI");
        copyStr.append("IMEI:" + DeviceInfoUtil.getDeviceImei(this));
        imei.setInfoText(DeviceInfoUtil.getDeviceImei(this));
        mDeveloperDevice.addView(imei);

//        CustomDeveloperView imsi = new CustomDeveloperView(this);
//        imsi.setDesText("IMSI");
//        copyStr.append("IMSI:" + DeviceInfoUtil.getDeviceImsi(this));
//        imsi.setInfoText(DeviceInfoUtil.getDeviceImsi(this));
//        mDeveloperDevice.addView(imsi);

        CustomDeveloperView baseVer = new CustomDeveloperView(this);
        baseVer.setDesText("基带版本");
        copyStr.append("基带版本:" + DeviceInfoUtil.getBaseband_Ver());
        baseVer.setInfoText(DeviceInfoUtil.getBaseband_Ver());
        mDeveloperDevice.addView(baseVer);

        CustomDeveloperView linuxVer = new CustomDeveloperView(this);
        linuxVer.setDesText("内核版本");
        copyStr.append("内核版本:" + DeviceInfoUtil.getLinuxCore_Ver());
        linuxVer.setInfoText(DeviceInfoUtil.getLinuxCore_Ver());
        mDeveloperDevice.addView(linuxVer);

        CustomDeveloperView ipAddress = new CustomDeveloperView(this);
        ipAddress.setDesText("IP地址");
        copyStr.append("IP地址:" + DeviceInfoUtil.getIpAddress(this));
        ipAddress.setInfoText(DeviceInfoUtil.getIpAddress(this));
        mDeveloperDevice.addView(ipAddress);

        CustomDeveloperView mac = new CustomDeveloperView(this);
        mac.setDesText("WLAN MAC地址");
        copyStr.append("WLAN MAC地址:" + DeviceInfoUtil.getMacAddress());
        mac.setInfoText(DeviceInfoUtil.getMacAddress());
        mDeveloperDevice.addView(mac);

        CustomDeveloperView netType = new CustomDeveloperView(this);
        netType.setDesText("网络类型");
        copyStr.append("网络类型:" + DeviceInfoUtil.getNetworkType(this));
        netType.setInfoText(DeviceInfoUtil.getNetworkType(this));
        mDeveloperDevice.addView(netType);

        CustomDeveloperView cpu = new CustomDeveloperView(this);
        cpu.setDesText("CPU类型");
        copyStr.append("CPU类型:" + DeviceInfoUtil.getCpuName());
        cpu.setInfoText(DeviceInfoUtil.getCpuName());
        mDeveloperDevice.addView(cpu);
//
//        CustomDeveloperView memory = new CustomDeveloperView(this);
//        memory.setDesText("内存");
//        memory.setInfoText(DeviceInfoUtil.getTotalMemory());
//        mDeveloperDevice.addView(memory);
//
//        CustomDeveloperView rom = new CustomDeveloperView(this);
//        rom.setDesText("ROM");
//        rom.setInfoText(DeviceInfoUtil.getTotalMemory());
//        mDeveloperDevice.addView(rom);

        //产品信息
        CustomDeveloperView appName = new CustomDeveloperView(this);
        appName.setDesText("产品名称");
        appName.setInfoText(DeviceInfoUtil.getAppName(this));
        mDeveloperProduct.addView(appName);

        CustomDeveloperView packageName = new CustomDeveloperView(this);
        packageName.setDesText("宿主包名");
        copyStr.append("宿主包名:" + DeviceInfoUtil.getApkPackageName(this));
        packageName.setInfoText(DeviceInfoUtil.getApkPackageName(this));
        mDeveloperProduct.addView(packageName);

        CustomDeveloperView appVersion = new CustomDeveloperView(this);
        appVersion.setDesText("app版本");
        copyStr.append("app版本:" + DeviceInfoUtil.getApkVersion(this));
        appVersion.setInfoText(DeviceInfoUtil.getApkVersion(this));
        mDeveloperProduct.addView(appVersion);

        CustomDeveloperView appVersionCode = new CustomDeveloperView(this);
        appVersionCode.setDesText("app版本号");
        copyStr.append("app版本号:" + String.valueOf(DeviceInfoUtil.getApkVersionCode(this)));
        appVersionCode.setInfoText(String.valueOf(DeviceInfoUtil.getApkVersionCode(this)));
        mDeveloperProduct.addView(appVersionCode);

        CustomDeveloperView sdkPackage = new CustomDeveloperView(this);
        sdkPackage.setDesText("sdk包名");
        sdkPackage.setInfoText(getResources().getString(R.string.sdk_package));
        copyStr.append("sdk包名:" + getResources().getString(R.string.sdk_package));
        mDeveloperProduct.addView(sdkPackage);

        CustomDeveloperView sdkVersion = new CustomDeveloperView(this);
        sdkVersion.setDesText("sdk版本号");
        sdkVersion.setInfoText(getResources().getString(R.string.version_name));
        copyStr.append("sdk版本号:" + getResources().getString(R.string.version_name));
        mDeveloperProduct.addView(sdkVersion);

        CustomDeveloperView productFlavor = new CustomDeveloperView(this);
        productFlavor.setDesText("产品渠道");
        copyStr.append("产品渠道:" + String.valueOf(DeviceInfoUtil.getApkSource(this)));
        productFlavor.setInfoText(String.valueOf(DeviceInfoUtil.getApkSource(this)));
        mDeveloperProduct.addView(productFlavor);

        //广告信息
        String adPlat = "";
        String adSplashId = "";
        String adFeedId = "";
        String adFeedPos = SharedPreManager.mInstance(this).getAdFeedPosition(CommonConstant.FILE_AD, CommonConstant.AD_FEED_POS) + "";
        String adDetailId = "";
        String adDtailPos = SharedPreManager.mInstance(this).getAdFeedPosition(CommonConstant.FILE_AD, CommonConstant.AD_FEED_VIDEO_POS) + "";
        ;
        String adRelateId = "";
        String adRelatePos = SharedPreManager.mInstance(this).getAdDetailPosition(CommonConstant.FILE_AD, CommonConstant.AD_RELATED_POS) + "";
        //展示广点通sdk
        if (SharedPreManager.mInstance(this).getBoolean(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE, true)) {
            adPlat = "广点通sdk";
            adSplashId = NEWS_FEED_GDT_SDK_SPLASHPOSID;
            adFeedId = NEWS_FEED_GDT_SDK_BIGPOSID;
            adDetailId = NEWS_DETAIL_GDT_SDK_BIGPOSID;
            adRelateId = NEWS_RELATE_GDT_SDK_SMALLPOSID;


        } else if (SharedPreManager.mInstance(this).getBoolean(CommonConstant.FILE_AD, LOG_SHOW_FEED_AD_GDT_API_SOURCE, false)) {
            adPlat = "广点通API";
            adSplashId = NEWS_FEED_GDT_API_SPLASHPOSID;
            adFeedId = NEWS_FEED_GDT_API_BIGPOSID;
            adDetailId = NEWS_DETAIL_GDT_API_BIGPOSID;
            adRelateId = NEWS_RELATE_GDT_API_SMALLID;

        }

        CustomDeveloperView adPlatform = new CustomDeveloperView(this);
        adPlatform.setDesText("广告平台");
        adPlatform.setInfoText(adPlat);
        copyStr.append("广告平台:" + adPlat);
        mDeveloperAd.addView(adPlatform);

        CustomDeveloperView adSplashIdv = new CustomDeveloperView(this);
        adSplashIdv.setDesText("开屏广告ID");
        adSplashIdv.setInfoText(adSplashId);
        copyStr.append("开平广告ID:" + adSplashId);
        mDeveloperAd.addView(adSplashIdv);

        CustomDeveloperView adFeedPlatform = new CustomDeveloperView(this);
        adFeedPlatform.setDesText("列表页广告平台");
        copyStr.append("列表页广告平台:" + adPlat);
        adFeedPlatform.setInfoText(adPlat);
        mDeveloperAd.addView(adFeedPlatform);

        CustomDeveloperView adFeedIdv = new CustomDeveloperView(this);
        adFeedIdv.setDesText("列表页广告ID");
        adFeedIdv.setInfoText(adFeedId);
        copyStr.append("列表页广告ID:" + adFeedIdv);
        mDeveloperAd.addView(adFeedIdv);

        CustomDeveloperView adFeedPosv = new CustomDeveloperView(this);
        adFeedPosv.setDesText("列表页广告位置");
        adFeedPosv.setInfoText(adFeedPos);
        copyStr.append("列表页广告位置:" + adFeedPos);
        mDeveloperAd.addView(adFeedPosv);

        CustomDeveloperView adDetailPlatform = new CustomDeveloperView(this);
        adDetailPlatform.setDesText("详情页广告平台");
        adDetailPlatform.setInfoText(adPlat);
        copyStr.append("列表页广告位置:" + adPlat);
        mDeveloperAd.addView(adDetailPlatform);

        CustomDeveloperView adDetailIdV = new CustomDeveloperView(this);
        adDetailIdV.setDesText("详情页广告ID");
        adDetailIdV.setInfoText(adDetailId);
        copyStr.append("详情页广告ID:" + adDetailId);
        mDeveloperAd.addView(adDetailIdV);

//        CustomDeveloperView adDetailPosV = new CustomDeveloperView(this);
//        adDetailPosV.setDesText("详情页广告位置");
//        adDetailPosV.setInfoText(adDtailPos);
//        copyStr.append("详情页广告位置:" + adDtailPos);
//        mDeveloperAd.addView(adDetailPosV);

        CustomDeveloperView adRelatePlatform = new CustomDeveloperView(this);
        adRelatePlatform.setDesText("相关广告平台");
        adRelatePlatform.setInfoText(adPlat);
        copyStr.append("相关广告平台:" + adPlat);
        mDeveloperAd.addView(adRelatePlatform);

        CustomDeveloperView adRelateIdV = new CustomDeveloperView(this);
        adRelateIdV.setDesText("相关广告ID");
        adRelateIdV.setInfoText(adRelateId);
        copyStr.append("相关广告ID:" + adRelateId);
        mDeveloperAd.addView(adRelateIdV);

        CustomDeveloperView adRelatePosV = new CustomDeveloperView(this);
        adRelatePosV.setDesText("相关广告位置");
        adRelatePosV.setInfoText(adRelatePos);
        copyStr.append("相关广告位置:" + adRelatePos);
        mDeveloperAd.addView(adRelatePosV);


        //日志信息
        CustomDeveloperView totalLog = new CustomDeveloperView(this);
        totalLog.setDesText("共产生日志");
        totalLog.setInfoText((QiDianApplication.upload + QiDianApplication.unload) + "");
        copyStr.append("共产生日志:" + (QiDianApplication.upload + QiDianApplication.unload));
        mDeveloperLog.addView(totalLog);

        CustomDeveloperView uploadLog = new CustomDeveloperView(this);
        uploadLog.setDesText("已上传日志");
        uploadLog.setInfoText(QiDianApplication.upload + "");
        copyStr.append("已上传日志:" + QiDianApplication.upload);
        mDeveloperLog.addView(uploadLog);

        CustomDeveloperView unloadLog = new CustomDeveloperView(this);
        unloadLog.setDesText("未上传日志");
        unloadLog.setInfoText(QiDianApplication.unload + "");
        copyStr.append("未上传日志:" + QiDianApplication.unload);
        mDeveloperLog.addView(unloadLog);
        setTheme();

    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.developer_close) {
            DeveloperAty.this.finish();
        } else if (id == R.id.developer_copy) {
            copy(copyStr.toString(), this);
            ToastUtil.toastShort("已复制");
        }
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    public static void copy(String content, Context context) {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        cmb.setPrimaryClip(ClipData.newPlainText(null, content));
    }

    /**
     * 实现粘贴功能
     *
     * @param context
     * @return
     */
    public static String paste(Context context) {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DeveloperAty Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
