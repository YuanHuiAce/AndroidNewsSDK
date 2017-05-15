package com.news.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebSettings;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.news.sdk.R;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.AdDetailEntity;
import com.news.sdk.entity.AdDeviceEntity;
import com.news.sdk.entity.AdEntity;
import com.news.sdk.entity.AdImpressionEntity;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.RelatedItemEntity;
import com.news.sdk.entity.User;
import com.news.sdk.pages.NewsDetailWebviewAty;
import com.news.sdk.utils.manager.SharedPreManager;
import com.qq.e.ads.nativ.NativeADDataRef;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdUtil {

    public static String getAdMessage(Context mContext, String Aid) {
        if (mContext != null) {
            Gson gson = new Gson();
            AdImpressionEntity adImpressionEntity = new AdImpressionEntity();
            adImpressionEntity.setAid(Aid);
            /** 单图91  三图164 */
//            adImpressionEntity.setHeight((int) (DeviceInfoUtil.obtainDensity() * 164) + "");
//            adImpressionEntity.setWidth(DeviceInfoUtil.getScreenWidth(mContext) + "");

            AdDeviceEntity adDeviceEntity = new AdDeviceEntity();
            /** 设置IMEI */
            String imei = DeviceInfoUtil.getDeviceImei(mContext);
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
            /** 客户端浏览器*/
            adDeviceEntity.setUa(WebSettings.getDefaultUserAgent(mContext));
            /** 设置屏幕密度*/
            adDeviceEntity.setDensity(mContext.getApplicationContext().getResources().getDisplayMetrics().densityDpi + "");
            /** 设置用户 SIM 卡的 imsi 号*/
            adDeviceEntity.setImsi(DeviceInfoUtil.getDeviceImsi(mContext));
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

    public static void setAdChannel(final Context context) {
        User user = SharedPreManager.mInstance(context).getUser(context);
        Long mUserId = null;
        if (user != null) {
            mUserId = Long.valueOf(user.getMuid());
        }
        if (mUserId == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", mUserId);
            jsonObject.put("did", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            jsonObject.put("ctype", Integer.valueOf(CommonConstant.NEWS_CTYPE));
            jsonObject.put("ptype", Integer.valueOf(CommonConstant.NEWS_PTYPE));
            jsonObject.put("aversion", context.getString(R.string.version_name));
            jsonObject.put("ctime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_POST_AD_SOURCE, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject json = response;
                        try {
                            int adChannel = (int) json.get("data");
                            if (adChannel == 1) {
                                //展示广点通sdk
                                SharedPreManager.mInstance(context).save(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE, false);
                                //展示广点通API
                                SharedPreManager.mInstance(context).save(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE, true);
                            } else if (adChannel == 2) {
                                //展示广点通sdk
                                SharedPreManager.mInstance(context).save(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE, true);
                                //展示广点通API
                                SharedPreManager.mInstance(context).save(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE, false);
                            }
                            SharedPreManager.mInstance(context).save(CommonConstant.FILE_AD, CommonConstant.AD_CHANNEL, adChannel);
                            SharedPreManager.mInstance(context).save(CommonConstant.FILE_AD, CommonConstant.AD_FEED_POS, (int) json.get("feedAdPos"));
                            SharedPreManager.mInstance(context).save(CommonConstant.FILE_AD, CommonConstant.AD_FEED_VIDEO_POS, (int) json.get("feedVideoAdPos"));
                            SharedPreManager.mInstance(context).save(CommonConstant.FILE_AD, CommonConstant.AD_RELATED_POS, (int) json.get("relatedAdPos"));
                            SharedPreManager.mInstance(context).save(CommonConstant.FILE_AD, CommonConstant.AD_RELATED_VIDEO_POS, (int) json.get("relatedVideoAdPos"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("tag", "URL_LOG_POST_APP_ACTION  222");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                header.put("X-Requested-With", "*");
                return header;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public static void upLogAdShowGDTSDK(List<NativeADDataRef> dataRefs, Context context) {
        if (!TextUtil.isListEmpty(dataRefs)) {
            ArrayList<NewsFeed> arrayList = new ArrayList<>();
            for (NativeADDataRef dataRef : dataRefs) {
                NewsFeed newsFeed = new NewsFeed();
                newsFeed.setPname(dataRef.getTitle());
                newsFeed.setCtime(System.currentTimeMillis());
                newsFeed.setSource(CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE);
                newsFeed.setAid(Long.valueOf(CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID));
                arrayList.add(newsFeed);
            }
            LogUtil.userShowLog(arrayList, context);
        }
    }

    public static void upLoadFeedAd(NewsFeed feed, Context context) {
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

    public static void upLoadAd(AdDetailEntity adDetailEntity, Context context) {
        if (adDetailEntity != null) {
            List<String> arrUrl = adDetailEntity.getData().getAdspace().get(0).getCreative().get(0).getImpression();
            if (!TextUtil.isListEmpty(arrUrl)) {
                for (String url : arrUrl) {
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

    public static void upLoadFeedAd(RelatedItemEntity feed, Context context) {
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

    public static void upLoadContentClick(AdDetailEntity adDetailEntity, final Context context, float downX, float downY, float upX, float upY) {
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
                                String url = event.getEventValue();
                                String lat = SharedPreManager.mInstance(context).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE);
                                String lon = SharedPreManager.mInstance(context).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE);
                                if (!TextUtil.isEmptyString(lat)) {
                                    url = url + "&lat=" + lat + "&lon=" + lon;
                                }
                                String first = url.split("s=")[0];
                                String end = url.split("&s=")[1];
                                end = URLDecoder.decode(end);
                                end = end.replace("\"down_x\":-999", "\"down_x\":" + downX).replace("\"down_y\":-999", "\"down_y\":" + downY).replace("\"up_x\":-999", "\"up_x\":" + upX).replace("\"up_y\":-999", "\"up_y\":" + upY);
                                end = URLEncoder.encode(end);
                                AdIntent.putExtra("key_url", first + end);
                                Log.i("tag", "event===1" + first + end);
                                context.startActivity(AdIntent);
                            } else {
                                String url = event.getEventValue();
                                url = url.replace("acttype=&", "acttype=1&");
                                String first = url.split("s=")[0];
                                String end = url.split("&s=")[1];
                                end = URLDecoder.decode(end);
                                end = end.replace("\"down_x\":-999", "\"down_x\":" + downX).replace("\"down_y\":-999", "\"down_y\":" + downY).replace("\"up_x\":-999", "\"up_x\":" + upX).replace("\"up_y\":-999", "\"up_y\":" + upY);
                                end = URLEncoder.encode(end);
                                url = first + end;
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
