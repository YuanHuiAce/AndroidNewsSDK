package com.news.yazhidao.net;

import org.apache.http.HttpResponse;

/**
 * Created by fengjigang on 15/1/6.
 */
public interface ICallback<T> {
    void success(T result);
    void failed(MyAppException exception);
    T handle(HttpResponse response, IUpdateProgressListener updateProgressListener) throws MyAppException;
    int retryCount();
    void cancel(boolean force);
    T preRequest();
    T postRequest(T t);
}
