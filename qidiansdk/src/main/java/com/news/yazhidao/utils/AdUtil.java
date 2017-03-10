package com.news.yazhidao.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.AdDetailEntity;
import com.news.yazhidao.entity.AdDeviceEntity;
import com.news.yazhidao.entity.AdEntity;
import com.news.yazhidao.entity.AdImpressionEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdUtil {

    public static String getAdMessage(Context mContext, String Aid) {
        if (mContext != null) {
            Gson gson = new Gson();
            AdImpressionEntity adImpressionEntity = new AdImpressionEntity();
            adImpressionEntity.setAid(Aid);
            /** 单图91  三图164 */
            adImpressionEntity.setHeight((int) (DeviceInfoUtil.obtainDensity() * 164) + "");
            adImpressionEntity.setWidth(DeviceInfoUtil.getScreenWidth(mContext) + "");

            AdDeviceEntity adDeviceEntity = new AdDeviceEntity();
            /** 设置IMEI */
            String imei = SharedPreManager.get("flag", "imei");
            adDeviceEntity.setImei(DeviceInfoUtil.generateMD5(imei));
            adDeviceEntity.setImeiori(imei);
            /** 设置MAC */
            String mac = DeviceInfoUtil.getMacAddress();
            String mac1 = mac.replace(":", "");
            adDeviceEntity.setMac(TextUtil.isEmptyString(mac1) ? null : DeviceInfoUtil.generateMD5(mac1));
            adDeviceEntity.setMacori(mac1);
            adDeviceEntity.setMac1(TextUtil.isEmptyString(mac) ? null : DeviceInfoUtil.generateMD5(mac));
            /** 设置AndroidID */
            String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            adDeviceEntity.setAnid(TextUtil.isEmptyString(androidId) ? null : DeviceInfoUtil.generateMD5(androidId));
            adDeviceEntity.setAnidori(TextUtil.isEmptyString(androidId) ? null : androidId);
            /** 设置设备品牌 */
            String brand = Build.BRAND;
            adDeviceEntity.setBrand(brand);
            /** 设置设备型号 */
            String platform = Build.MODEL;
            adDeviceEntity.setPlatform(platform);
            /** 设置运营商号 */
            adDeviceEntity.setOperator(NetUtil.getSimOperatorInfo(mContext));
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

    public static void upLoadAd(NewsFeed feed, Context context) {
        if (feed.getRtype() == 3) {
            ArrayList<String> arrUrl = feed.getAdimpression();
            if (!TextUtil.isListEmpty(arrUrl) && !feed.isUpload()) {
                for (String url : arrUrl) {
                    feed.setUpload(true);
                    //广告
                    if (!TextUtil.isEmptyString(url)) {
                        //获取经纬度
                        String lat = SharedPreManager.mInstance(context).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE);
                        String lon = SharedPreManager.mInstance(context).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE);
                        String[] realUrl = url.split("&lon");
                        String requestUrl = realUrl[0];
                        if (!TextUtil.isEmptyString(lat)) {
                            requestUrl = requestUrl + "&lon=" + lon + "&lat=" + lat;
                        }
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
    }

    public static void upLoadContentClick(NewsFeed feed, final Context context, float downX, float downY, float upX, float upY) {
        AdDetailEntity adDetailEntity = feed.getAdDetailEntity();
        if (adDetailEntity != null) {
            AdDetailEntity.Data data = adDetailEntity.getData();
            if (data != null) {
                List<AdDetailEntity.Adspace> adSpace = data.getAdspace();
                if (adSpace != null && adSpace.size() > 0) {
                    final List<AdDetailEntity.Creative> creative = adSpace.get(0).getCreative();
                    if (creative != null && creative.size() > 0) {
                        List<String> arrUrl = creative.get(0).getClick();
                        if (arrUrl != null && arrUrl.size() > 0) {
                            for (final String url : arrUrl) {
                                RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.i("tag", "click" + url);
                                    }
                                }, null);
                                requestQueue.add(request);
                            }
                        }
                        List<AdDetailEntity.Event> events = creative.get(0).getEvent();
                        if (events != null && events.size() > 0) {
                            AdDetailEntity.Event event = events.get(0);
                            if (event.getEventKey() == 1) {
                                Intent AdIntent = new Intent(context, NewsDetailWebviewAty.class);
                                String url = feed.getPurl();
                                String lat = SharedPreManager.mInstance(context).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE);
                                String lon = SharedPreManager.mInstance(context).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE);
                                if (!TextUtil.isEmptyString(lat)) {
                                    url = url + "&lat=" + lat + "&lon=" + lon;
                                }
                                url = url.replace("\"down_x\":-999", "\"down_x\":" + downX).replace("\"down_y\":-999", "\"down_y\":" + downY).replace("\"up_x\":-999", "\"up_x\":" + upX).replace("\"up_y\":-999", "\"up_y\":" + upY);
                                AdIntent.putExtra("key_url", url);
                                Log.i("tag", "event===1" + url);
                                context.startActivity(AdIntent);
                            } else {
                                String url = event.getEventValue();
                                url = url.replace("\"down_x\":-999", "\"down_x\":" + downX).replace("\"down_y\":-999", "\"down_y\":" + downY).replace("\"up_x\":-999", "\"up_x\":" + upX).replace("\"up_y\":-999", "\"up_y\":" + upY);
                                url = url.replace("acttype=&", "acttype=1&");
                                RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String clickId = response.getJSONObject("data").getString("clickid");
                                            String url = response.getJSONObject("data").getString("dstlink");
                                            List<AdDetailEntity.Tracking> trackings = creative.get(0).getTracking();
                                            AdDetailEntity.Tracking tracking = trackings.get(0);
                                            if (tracking != null) {
                                                List<String> value = tracking.getTracking_value();
                                                if (value != null && value.size() > 0) {
                                                    String tracking_value = value.get(0);
                                                    tracking_value = tracking_value.replace("%%CLICKID%%", clickId);
                                                    Log.i("tag", "tracking_value" + tracking_value);
                                                    RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
                                                    StringRequest request = new StringRequest(Request.Method.GET, tracking_value, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                        }
                                                    }, null);
                                                    requestQueue.add(request);
                                                }
                                            }
                                            Log.i("tag", "dstlink" + url);
                                            Intent AdIntent = new Intent(context, NewsDetailWebviewAty.class);
                                            AdIntent.putExtra("key_url", url);
                                            context.startActivity(AdIntent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                requestQueue.add(request);
                            }
                        }
                    }
                }
            }
        }
    }

}
