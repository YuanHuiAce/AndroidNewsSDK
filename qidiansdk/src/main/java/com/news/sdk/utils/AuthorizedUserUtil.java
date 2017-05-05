package com.news.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.entity.AuthorizedUser;
import com.news.sdk.entity.User;
import com.news.sdk.utils.manager.SharedPreManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AuthorizedUserUtil {

    public static void authorizedUser(AuthorizedUser authorizedUser, final Context context) {
        if (authorizedUser != null) {
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_AUTHORIZED_USER, new Gson().toJson(authorizedUser),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String code = response.optString("code");
                            if ("2000".equals(code)) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject("data");
                                    User user = SharedPreManager.mInstance(context).getUser(context);
                                    user.setUtype(jsonObject.getInt("utype") + "");
                                    user.setMuid(jsonObject.getInt("uid"));
                                    user.setUserName(jsonObject.getString("uname"));
                                    user.setUserIcon(jsonObject.getString("avatar"));
                                    user.setVisitor(false);
                                    SharedPreManager.mInstance(context).saveUser(user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("tag", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    return headers;
                }
            };
            requestQueue.add(jsonRequest);
        }
    }

    public static void sendUserLoginBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(CommonConstant.USER_LOGIN_ACTION);
        context.sendBroadcast(intent);
    }
}
