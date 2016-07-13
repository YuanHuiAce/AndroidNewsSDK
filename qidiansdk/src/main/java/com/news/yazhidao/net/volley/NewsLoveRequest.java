package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.news.yazhidao.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjigang on 16/2/26.
 */
public class NewsLoveRequest<T> extends GsonRequest<T> {
    private HashMap mParams, mHeader;


    public NewsLoveRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, clazz, url, successListener, listener);
    }

    public NewsLoveRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }
    @Override
    protected String checkJsonData(String data,NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String code = jsonObject.optString("code", "");
//            String message = jsonObject.optString("message", "");
//            Logger.e("jigang","code = "+code + ",message=" + message);
            if ("2000".equals(code)) {
                return jsonObject.optString("data", "");
            }else if("2002".equals(code)){
                return "2002";
            }else {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeader;
    }
    public void setRequestHeader(HashMap header) {
        this.mHeader = header;
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }
    public void setRequestParams(HashMap params) {
        this.mParams = params;
    }

}
