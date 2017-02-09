package com.news.yazhidao.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.AdDeviceEntity;
import com.news.yazhidao.entity.AdEntity;
import com.news.yazhidao.entity.AdImpressionEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.manager.SharedPreManager;

import java.util.ArrayList;

public class AdUtil {

    public static String getAdMessage(Context mContext,String Aid) {
        if (mContext != null) {
            Gson gson = new Gson();
            AdImpressionEntity adImpressionEntity = new AdImpressionEntity();
            adImpressionEntity.setAid(Aid);
            /** 单图91  三图164 */
            adImpressionEntity.setHeight((int) (DeviceInfoUtil.obtainDensity() * 164) + "");
            adImpressionEntity.setWidth(DeviceInfoUtil.getScreenWidth(mContext) + "");

            AdDeviceEntity adDeviceEntity = new AdDeviceEntity();
            /** 设置IMEI */
            String imei = SharedPreManager.mInstance(mContext).get("flag", "imei");
            adDeviceEntity.setImei(DeviceInfoUtil.generateMD5(imei));
            adDeviceEntity.setImeiori(imei);
            /** 设置AndroidID */
            String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            adDeviceEntity.setAnid(TextUtil.isEmptyString(androidId) ? null : DeviceInfoUtil.generateMD5(androidId));
            /** 设置设备品牌 */
            String brand = Build.BRAND;
            adDeviceEntity.setBrand(brand);
            /** 设置设备型号 */
            String platform = Build.MODEL;
            adDeviceEntity.setPlatform(platform);
            /** 设置操作系统 */
            adDeviceEntity.setOs("1");
            /** 设置操作系统版本号 */
            String version = Build.VERSION.RELEASE;
            adDeviceEntity.setOs_version(version);
            /** 设置屏幕分辨率 */
            adDeviceEntity.setDevice_size(CrashHandler.getResolution(mContext));
            /** 设置IP */
            String ip = "";
            if (DeviceInfoUtil.isWifiNetWorkState(mContext)) {
                ip = DeviceInfoUtil.getIpAddress(mContext);
            } else {
                ip = DeviceInfoUtil.getLocalIpAddress();
            }
            adDeviceEntity.setIp(ip);
            /** 设置网络环境 */
            String networkType = DeviceInfoUtil.getNetworkType(mContext);
            if (TextUtil.isEmptyString(networkType)) {
                adDeviceEntity.setNetwork("0");
            } else {
                if ("wifi".endsWith(networkType)) {
                    adDeviceEntity.setNetwork("1");
                } else if ("2G".endsWith(networkType)) {
                    adDeviceEntity.setNetwork("2");
                } else if ("3G".endsWith(networkType)) {
                    adDeviceEntity.setNetwork("3");
                } else if ("4G".endsWith(networkType)) {
                    adDeviceEntity.setNetwork("4");
                } else {
                    adDeviceEntity.setNetwork("0");
                }
            }
            /** 设置横竖屏幕 */
            if (DeviceInfoUtil.isScreenChange(mContext)) {//横屏
                adDeviceEntity.setScreen_orientation("2");
            } else {//竖屏
                adDeviceEntity.setScreen_orientation("1");
            }
            AdEntity adEntity = new AdEntity();
            adEntity.setTs((System.currentTimeMillis() / 1000) + "");
            adEntity.setDevice(adDeviceEntity);
            adEntity.getImpression().add(adImpressionEntity);
            return gson.toJson(adEntity);
        } else {
            return "";
        }
    }

    public static void upLoadAd(NewsFeed feed) {
        if (feed.getRtype() == 3) {
            String url = null;
            ArrayList<String> arrUrl = feed.getAdimpression();
            if (!TextUtil.isListEmpty(arrUrl)) {
                url = arrUrl.get(0);
            }
            //广告
            if (!TextUtil.isEmptyString(url) && !feed.isUpload()) {
                feed.setUpload(true);
                //获取经纬度
                String lat = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE);
                String lon = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE);
                String[] realUrl = url.split("&lon");
                final String requestUrl = realUrl[0] + "&lon=" + lon + "&lat=" + lat;
                RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                StringRequest request = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, null);
                requestQueue.add(request);
            }
        }
    }
}
