package com.news.sdk.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/4/18.
 */
public class UpLoadLogRequest<T> extends GsonRequest<T> {
    public UpLoadLogRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }

    public UpLoadLogRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, clazz, url, successListener, listener);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            if (response.statusCode == 200) {
                return (Response<T>) Response.success("success", HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.error(new VolleyError("获取数据异常!--" + response.statusCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected String checkJsonData(String data, NetworkResponse response) {
        if (response.statusCode == 200) {
            return "success";
        } else {
            return "fall";
        }
    }
}
