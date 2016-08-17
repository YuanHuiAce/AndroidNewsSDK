package com.news.yazhidao.net.volley;

import com.android.volley.Response;

import java.lang.reflect.Type;

/**
 * Created by fengjigang on 16/8/16.
 */
public class FetchHotWordRequest<T> extends GsonRequest<T> {
    public FetchHotWordRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, clazz, url, successListener, listener);
    }

    public FetchHotWordRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }
}
