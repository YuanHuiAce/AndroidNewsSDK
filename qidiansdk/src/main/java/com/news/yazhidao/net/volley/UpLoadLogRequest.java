package com.news.yazhidao.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/4/18.
 */
public class UpLoadLogRequest<T> extends GsonRequest<T>{
    public UpLoadLogRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }

    public UpLoadLogRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, clazz, url, successListener, listener);
    }

    @Override
    protected String checkJsonData(String data, NetworkResponse response) {
        if(response.statusCode == 200){
            return "success";
        }else{
            return "fall";
        }


    }
}
