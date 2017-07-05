package com.news.sdk.utils;

import android.content.Context;
import android.provider.Settings;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.news.sdk.R;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.User;
import com.news.sdk.entity.UserLogBasicInfoEntity;
import com.news.sdk.utils.manager.SharedPreManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


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

    public static void onPageStart(String source) {
        MobclickAgent.onPageStart(source);
    }

    public static void onPageEnd(String source) {
        MobclickAgent.onPageEnd(source);
    }

    public static void userReadLog(NewsFeed newsFeed, Context context, Long begintime, Long endtime,String percent) {
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
            json.put("percent", percent);
            json.put("readtime", (int) ((endtime - begintime) / 1000));
            jsonObject.put("basicinfo", new JSONObject(gson.toJson(userLogBasicInfoEntity)));
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
                        QiDianApplication.upload++;
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError arg0) {
                QiDianApplication.unload++;
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
                    object.put("source", "feed");
                    object.put("chid", newsFeed.getChannel());
                    object.put("logtype", newsFeed.getLogtype());
                    object.put("logchid", newsFeed.getLogchid());
                    object.put("extend", newsFeed.getExtend());
                    object.put("ctime", newsFeed.getCtime());
                } else {
                    MobclickAgent.onEvent(context, "showAd");
                    object.put("aid", Long.valueOf(newsFeed.getAid()));
                    object.put("source", newsFeed.getSource());
                    object.put("title", newsFeed.getPname());
                    object.put("extend", newsFeed.getExtend());
                    object.put("ctime", newsFeed.getCtime());
                }
                jsonArray.put(object);
            }
            json.put("news_list", jsonArray);
            jsonObject.put("basicinfo", new JSONObject(gson.toJson(userLogBasicInfoEntity)));
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
            jsonObject.put("basicinfo", new JSONObject(gson.toJson(userLogBasicInfoEntity)));
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

    public static void adClickLog(Long aid, Context context, String source, String title) {
        MobclickAgent.onEvent(context, "clickAd");
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
            json.put("title", title);
            jsonObject.put("extend", null);
            jsonObject.put("basicinfo", new JSONObject(gson.toJson(userLogBasicInfoEntity)));
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
            jsonObject.put("basicinfo", new JSONObject(gson.toJson(userLogBasicInfoEntity)));
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
        MobclickAgent.onEvent(context, atype);
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
            jsonObject.put("basicinfo", new JSONObject(gson.toJson(userLogBasicInfoEntity)));
            jsonObject.put("data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = null;
        try {
            url = HttpConstant.URL_LOG_POST_APP_ACTION + "?log_data=" + URLEncoder.encode(jsonObject.toString(), "utf-8");
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
//        JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_LOG_POST_APP_ACTION, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("tag", "URL_LOG_POST_APP_ACTION");
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("tag", "URL_LOG_POST_APP_ACTION  222");
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

    public static void adGetLog(Context context, int rnum, int snum, long aid, String source) {
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
            json.put("rnum", rnum);
            json.put("snum", snum);
            json.put("aid", aid);
            json.put("source", source);
            jsonObject.put("extend", null);
            jsonObject.put("basicinfo", new JSONObject(gson.toJson(userLogBasicInfoEntity)));
            jsonObject.put("data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = null;
        try {
            url = HttpConstant.URL_LOG_POST_AD_GET + "?log_data=" + URLEncoder.encode(jsonObject.toString(), "utf-8");
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
        requestQueue.add(request);
    }

    public static void adUserRegist(Context context) {
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
            jsonObject.put("basicinfo", new JSONObject(gson.toJson(userLogBasicInfoEntity)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = null;
        try {
            url = HttpConstant.URL_LOG_POST_USER_SIGN_UP + "?log_data=" + URLEncoder.encode(jsonObject.toString(), "utf-8");
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
        requestQueue.add(request);
    }

}
