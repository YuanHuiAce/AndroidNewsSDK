package com.news.sdk.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjigang on 16/2/26.
 */
public class ChannelListRequest<T> extends GsonRequest<T> {
    private HashMap mParams, mHeader;


    public ChannelListRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, clazz, url, successListener, listener);
    }

    public ChannelListRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }

    @Override
    protected String checkJsonData(String data, NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String code = jsonObject.optString("code", "");
            if ("2000".equals(code)) {
                return jsonObject.optString("data", "");
            } else if ("2002".equals(code)) {
                return "2002";
            } else {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    public void setRequestParams(HashMap params) {
        this.mParams = params;
    }
}
