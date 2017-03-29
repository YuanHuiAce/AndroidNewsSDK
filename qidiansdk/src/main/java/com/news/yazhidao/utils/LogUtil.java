package com.news.yazhidao.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.LocationEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.UploadLogDataEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.UpLoadLogRequest;
import com.news.yazhidao.utils.manager.SharedPreManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class LogUtil {

    public static void upLoadLog(NewsFeed newsFeed, Context context, Long lastTime, String percent, String version ,boolean isUserComment) {
        User user = SharedPreManager.mInstance(context).getUser(context);
        String mUserId = "";
        if (user != null) {
            mUserId = user.getMuid() + "";
        }
        Logger.e("aaa", "开始上传日志！");
        if (newsFeed == null || TextUtil.isEmptyString(mUserId)) {
            Log.i("tag","percent kong");
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
                "&t=" + t + "&i=" + i + "&d=" + TextUtil.getBase64(gson.toJson(uploadLogDataEntity)) + "&pe=" + percent + "&v=" + version;
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

}
