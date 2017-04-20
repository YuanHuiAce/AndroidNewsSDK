package com.news.yazhidao.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.news.yazhidao.R;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.LocationEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.UploadLogDataEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.entity.UserLogBasicInfoEntity;
import com.news.yazhidao.net.volley.UpLoadLogRequest;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LogUtil {

    private static UserLogBasicInfoEntity getLogBasicInfo(Context context, Long userId) {
        UserLogBasicInfoEntity userLogBasicInfoEntity = new UserLogBasicInfoEntity();
        userLogBasicInfoEntity.setUid(userId);
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        userLogBasicInfoEntity.setDeviceid(androidId);
        userLogBasicInfoEntity.setPtype(CommonConstant.LOG_PTYPE);
        userLogBasicInfoEntity.setCtype(CommonConstant.LOG_CTYPE);
        userLogBasicInfoEntity.setVersion_text(context.getString(R.string.version_name));
        userLogBasicInfoEntity.setCtime(System.currentTimeMillis());
        return userLogBasicInfoEntity;
    }

    public static void upLoadLog(NewsFeed newsFeed, Context context, Long lastTime, String percent) {
        User user = SharedPreManager.mInstance(context).getUser(context);
        String mUserId = "";
        if (user != null) {
            mUserId = user.getMuid() + "";
        }
        Logger.e("aaa", "开始上传日志！");
        if (newsFeed == null || TextUtil.isEmptyString(mUserId)) {
            Logger.e("tag", "percent kong");
            return;
        }
        final UploadLogDataEntity uploadLogDataEntity = new UploadLogDataEntity();
        uploadLogDataEntity.setN((long) newsFeed.getNid());
        uploadLogDataEntity.setC(newsFeed.getChannel());
        uploadLogDataEntity.setT(newsFeed.getRtype());
        uploadLogDataEntity.setS((int) (lastTime / 1000));
        uploadLogDataEntity.setF(0);
        uploadLogDataEntity.setLt(newsFeed.getLogtype());
        uploadLogDataEntity.setLc(newsFeed.getLogchid());
        uploadLogDataEntity.setPe(percent);
        uploadLogDataEntity.setV(context.getString(R.string.version_name));
        final String locationJsonString = SharedPreManager.mInstance(context).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_USER_LOCATION);
        final String LogData = SharedPreManager.mInstance(context).upLoadLogGet(CommonConstant.UPLOAD_LOG_DETAIL);
        Gson gson = new Gson();
        LocationEntity locationEntity = gson.fromJson(locationJsonString, LocationEntity.class);
        if (!TextUtil.isEmptyString(LogData)) {
            SharedPreManager.mInstance(context).upLoadLogSave(mUserId, CommonConstant.UPLOAD_LOG_DETAIL, locationJsonString, uploadLogDataEntity);
        }
        Logger.e("aaa", "确认上传日志！");
        final RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        String userid = null, p = null, t = null, i = null;
        try {
            userid = URLEncoder.encode(mUserId + "", "utf-8");
            if (locationEntity != null) {
                if (locationEntity.getProvince() != null)
                    p = URLEncoder.encode(locationEntity.getProvince() + "", "utf-8");
                if (locationEntity.getCity() != null)
                    t = URLEncoder.encode(locationEntity.getCity(), "utf-8");
                if (locationEntity.getDistrict() != null)
                    i = URLEncoder.encode(locationEntity.getDistrict(), "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = HttpConstant.URL_UPLOAD_LOG + "u=" + userid + "&p=" + p +
                "&t=" + t + "&i=" + i + "&d=" + TextUtil.getBase64(gson.toJson(uploadLogDataEntity));
        final UpLoadLogRequest<String> request = new UpLoadLogRequest<>(Request.Method.GET, String.class, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("aaa", "上传日志成功！");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);
    }

    public static void userReadLog(NewsFeed newsFeed, Context context, Long begintime, Long endtime) {
        User user = SharedPreManager.mInstance(context).getUser(context);
        Long mUserId = null;
        if (user != null) {
            mUserId = Long.valueOf(user.getMuid());
        }
        Logger.e("aaa", "开始上传日志！");
        if (newsFeed == null || mUserId == null) {
            Logger.e("tag", "percent kong");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        UserLogBasicInfoEntity userLogBasicInfoEntity = getLogBasicInfo(context, mUserId);
        try {
            JSONObject json = new JSONObject();
            json.put("nid", Long.valueOf(newsFeed.getNid()));
            json.put("begintime", begintime);
            json.put("endtime", endtime);
            json.put("readtime", (int) ((endtime - begintime) / 1000));
            jsonObject.put("basicinfo", userLogBasicInfoEntity);
            jsonObject.put("data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = null;
        try {
            url = HttpConstant.URL_LOG_POST_NEWS_READ + "?log_data=" + URLEncoder.encode(jsonObject.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        //返回正确后的操作
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
//        JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_LOG_POST_NEWS_READ, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("tag", "URL_LOG_POST_NEWS_READ");
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("tag", "URL_LOG_POST_NEWS_READ222");
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> header = new HashMap<>();
//                header.put("Content-Type", "application/json");
//                header.put("X-Requested-With", "*");
//                return header;
//            }
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public static void userShowLog(final ArrayList<NewsFeed> arrNewsFeed, Context context) {
        User user = SharedPreManager.mInstance(context).getUser(context);
        Long mUserId = null;
        if (user != null) {
            mUserId = Long.valueOf(user.getMuid());
        }
        Logger.e("aaa", "开始上传日志！");
        if (arrNewsFeed == null || mUserId == null) {
            Logger.e("tag", "percent kong");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        UserLogBasicInfoEntity userLogBasicInfoEntity = getLogBasicInfo(context, mUserId);
        try {
            JSONObject json = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (NewsFeed newsFeed : arrNewsFeed) {
                newsFeed.setUpload(true);
                JSONObject object = new JSONObject();
                if (0 != newsFeed.getNid()) {
                    object.put("nid", Long.valueOf(newsFeed.getNid()));
                    object.put("source", newsFeed.getSource());
                    object.put("chid", newsFeed.getChannel());
                    object.put("logtype", newsFeed.getLogtype());
                    object.put("logchid", newsFeed.getLogchid());
                    object.put("extend", newsFeed.getExtend());
                    object.put("ctime", newsFeed.getCtime());
                } else {
                    object.put("aid", Long.valueOf(newsFeed.getAid()));
                    object.put("source", newsFeed.getSource());
                    object.put("title", newsFeed.getTitle());
                    object.put("extend", newsFeed.getExtend());
                    object.put("ctime", newsFeed.getCtime());
                }
                jsonArray.put(object);
            }
            json.put("news_list", jsonArray);
            jsonObject.put("basicinfo", userLogBasicInfoEntity);
            jsonObject.put("data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = null;
        try {
            url = HttpConstant.URL_LOG_POST_NEWS_SHOW + "?log_data=" + URLEncoder.encode(jsonObject.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        //返回正确后的操作
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        // 设置标签
//        JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.GET, URL_LOG_POST_NEWS_SHOW, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("tag", "URL_LOG_POST_NEWS_SHOW");
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("tag", "URL_LOG_POST_NEWS_SHOW222");
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> header = new HashMap<>();
//                header.put("Content-Type", "application/json");
//                header.put("X-Requested-With", "*");
//                return header;
//            }
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public static void userClickLog(NewsFeed newsFeed, Context context, String source) {
        User user = SharedPreManager.mInstance(context).getUser(context);
        Long mUserId = null;
        if (user != null) {
            mUserId = Long.valueOf(user.getMuid());
        }
        Logger.e("aaa", "开始上传日志！");
        if (newsFeed == null || mUserId == null) {
            Logger.e("tag", "percent kong");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        UserLogBasicInfoEntity userLogBasicInfoEntity = getLogBasicInfo(context, mUserId);
        try {
            JSONObject json = new JSONObject();
            json.put("nid", Long.valueOf(newsFeed.getNid()));
            json.put("source", source);
            json.put("chid", newsFeed.getChannel());
            json.put("logtype", newsFeed.getLogtype());
            json.put("logchid", newsFeed.getLogchid());
            jsonObject.put("extend", newsFeed.getExtend());
            jsonObject.put("basicinfo", userLogBasicInfoEntity);
            jsonObject.put("data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = null;
        try {
            url = HttpConstant.URL_LOG_POST_NEWS_CLICK + "?log_data=" + URLEncoder.encode(jsonObject.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        //返回正确后的操作
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
//        JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_LOG_POST_NEWS_CLICK, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("tag", "URL_LOG_POST_NEWS_CLICK");
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("tag", "URL_LOG_POST_NEWS_CLICK222");
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> header = new HashMap<>();
//                header.put("Content-Type", "application/json");
//                header.put("X-Requested-With", "*");
//                return header;
//            }
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public static void adClickLog(Long aid, Context context, String source) {
        User user = SharedPreManager.mInstance(context).getUser(context);
        Long mUserId = null;
        if (user != null) {
            mUserId = Long.valueOf(user.getMuid());
        }
        Logger.e("aaa", "开始上传日志！");
        if (aid == null || mUserId == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        UserLogBasicInfoEntity userLogBasicInfoEntity = getLogBasicInfo(context, mUserId);
        try {
            JSONObject json = new JSONObject();
            json.put("aid", aid);
            json.put("source", source);
            jsonObject.put("extend", null);
            jsonObject.put("basicinfo", userLogBasicInfoEntity);
            jsonObject.put("data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = null;
        try {
            url = HttpConstant.URL_LOG_POST_AD_CLICK + "?log_data=" + URLEncoder.encode(jsonObject.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        //返回正确后的操作
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
//        JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_LOG_POST_AD_CLICK, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("tag", "URL_LOG_POST_AD_CLICK");
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("tag", "URL_LOG_POST_AD_CLICK222");
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> header = new HashMap<>();
//                header.put("Content-Type", "application/json");
//                header.put("X-Requested-With", "*");
//                return header;
//            }
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public static void appUseLog(Context context, Long begintime, Long endtime) {
        User user = SharedPreManager.mInstance(context).getUser(context);
        Long mUserId = null;
        if (user != null) {
            mUserId = Long.valueOf(user.getMuid());
        }
        Logger.e("aaa", "开始上传日志！");
        if (mUserId == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        UserLogBasicInfoEntity userLogBasicInfoEntity = getLogBasicInfo(context, mUserId);
        try {
            JSONObject json = new JSONObject();
            json.put("begintime", begintime);
            json.put("endtime", endtime);
            json.put("utime", (int) ((endtime - begintime) / 1000));
            jsonObject.put("extend", null);
            jsonObject.put("basicinfo", userLogBasicInfoEntity);
            jsonObject.put("data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = null;
        try {
            url = HttpConstant.URL_LOG_POST_APP_USE + "?log_data=" + URLEncoder.encode(jsonObject.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        //返回正确后的操作
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
//        JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_LOG_POST_APP_USE, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("tag", "URL_LOG_POST_APP_USE");
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("tag", "URL_LOG_POST_APP_USE  222");
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> header = new HashMap<>();
//                header.put("Content-Type", "application/json");
//                header.put("X-Requested-With", "*");
//                return header;
//            }
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);
    }

    public static void userActionLog(Context context, String atype, String from, String to, Object params, boolean effective) {
        User user = SharedPreManager.mInstance(context).getUser(context);
        Long mUserId = null;
        if (user != null) {
            mUserId = Long.valueOf(user.getMuid());
        }
        Logger.e("aaa", "开始上传日志！");
        if (mUserId == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        UserLogBasicInfoEntity userLogBasicInfoEntity = getLogBasicInfo(context, mUserId);
        try {
            JSONObject json = new JSONObject();
            json.put("atype", atype);
            json.put("from", from);
            json.put("to", to);
            json.put("params", params);
            json.put("effective", effective);
            jsonObject.put("extend", null);
            jsonObject.put("basicinfo", userLogBasicInfoEntity);
            jsonObject.put("data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_LOG_POST_APP_ACTION, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("tag", "URL_LOG_POST_APP_ACTION");
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
}
