package com.news.sdk.net.volley.request;


import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjigang on 15/1/5.
 */
public class NetworkRequest {

    public IUpdateProgressListener updateProgressListener;
    public HashMap<String, Object> getParams;
    private int timeOut = 15000;

    public NetworkRequest(String url, RequestMethod method) {
        this.url = url;
        this.method = method;
        //默认添加请求头 使用gzip
        addHeader("Accept-Encoding", "gzip");
    }

    public NetworkRequest(String url, RequestMethod method, List<NameValuePair> params) {
        this.url = url;
        this.method = method;
        this.params = params;
        //默认添加请求头 使用gzip
        addHeader("Accept-Encoding", "gzip");
    }

    public NetworkRequest(String url) {
        this.url = url;
        this.method = RequestMethod.GET;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public enum RequestMethod {
        GET, POST, PUT, DELETE
    }

    public List<NameValuePair> getParams() {
        return params;
    }

    public void setParams(List<NameValuePair> params) {
        this.params = params;
    }

    public RequestMethod method;

    public String url;
    public HttpEntity entity;
    public List<NameValuePair> params;
    public HashMap<String, String> headers;
    public AbstractCallBack callback;
    private NetworkRequestTask nrTask;

    public void setCallback(AbstractCallBack callback) {
        this.callback = callback;
    }

    public void setUpdateProgressListener(IUpdateProgressListener listener) {
        this.updateProgressListener = listener;
    }

    public void execute() {
        nrTask = new NetworkRequestTask(this);
        nrTask.execute();
    }

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<String, String>();
            headers.put(key, value);
        }
    }

    public void cancel(boolean force) {
        if (force && nrTask != null) {
            nrTask.cancel(true);
        }
        if (this.callback != null) {
            this.callback.cancel(force);
        }
    }
}
