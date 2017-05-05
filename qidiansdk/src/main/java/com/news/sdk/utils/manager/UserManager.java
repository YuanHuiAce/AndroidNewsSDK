package com.news.sdk.utils.manager;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.entity.User;
import com.news.sdk.utils.GsonUtil;
import com.news.sdk.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 用户管理类
 * Created by fengjigang on 16/5/30.
 */
public class UserManager {

    public interface RegisterVisitorListener {
        void registerSuccess();
    }

    /**
     * 注册游客身份,获取访问所有接口数据的token
     */
    public static void registerVisitor(final Context mContext, final RegisterVisitorListener mListener) {
        final User user = SharedPreManager.mInstance(mContext).getUser(mContext);
        if (user == null) {
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("utype", 2);
                requestBody.put("platform", 2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            com.news.sdk.net.volley.RegisterVisitorRequest jsonObjectRequest = new com.news.sdk.net.volley.RegisterVisitorRequest(requestBody.toString(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    User user = new User();
                    user.setAuthorToken(response.optString("Authorization"));
                    user.setUtype(response.optString("utype"));
                    user.setMuid(response.optInt("uid"));
                    user.setPassword(response.optString("password"));
                    try {
                        ArrayList<String> channels = GsonUtil.deSerializedByType(response.getJSONArray("channel").toString(), new TypeToken<ArrayList<String>>() {
                        }.getType());
                        user.setChannel(channels);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SharedPreManager.mInstance(mContext).saveUser(user);
                    if (mListener != null){
                        mListener.registerSuccess();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e("jigang", "error" + error.getMessage());
                }
            });
            QiDianApplication.getInstance().getRequestQueue().add(jsonObjectRequest);
        }else {
                if (user.isVisitor()){
                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("uid", user.getMuid());
                        requestBody.put("password", user.getPassword());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    com.news.sdk.net.volley.VisitorLoginRequest loginRequest = new com.news.sdk.net.volley.VisitorLoginRequest(requestBody.toString(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            User user = new User();
                            user.setAuthorToken(response.optString("Authorization"));
                            user.setUtype(response.optString("utype"));
                            user.setMuid(response.optInt("uid"));
                            user.setPassword(response.optString("password"));
                            SharedPreManager.mInstance(mContext).saveUser(user);
                            if (mListener != null){
                                mListener.registerSuccess();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Logger.e("jigang", "error" + error.getMessage());
                        }
                    });
                    QiDianApplication.getInstance().getRequestQueue().add(loginRequest);
                }
//                else {
//                    ShareSdkHelper.reRegisterThidUser();
//                }
        }
    }
}
