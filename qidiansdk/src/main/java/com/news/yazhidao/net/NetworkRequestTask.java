package com.news.yazhidao.net;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;

/**
 * Created by fengjigang on 15/1/6.
 */
public class NetworkRequestTask extends AsyncTask<String, Integer, Object> {
    private NetworkRequest request;

    public NetworkRequestTask(NetworkRequest request) {
        this.request = request;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... params) {
        int retryCount = 0;
        int retry = 0;
        if (request.callback != null) {
            retryCount = request.callback.retryCount();
        }
        return request(retry, retryCount);
    }

    private Object request(int retry, int retryCount) {
        try {
            Object result;
            if(request.callback!=null){
                result=request.callback.preRequest();
                if(result!=null){
                    return result;
                }
            }
            HttpResponse response = HttpClientUtil.execute(request);
            if (request.callback != null) {
                if (request.updateProgressListener != null) {
                    result= request.callback.handle(response, new IUpdateProgressListener() {
                        @Override
                        public void updateProgressData(int curPos, int contentLength) {
                            publishProgress(curPos, contentLength);
                        }
                    });
                } else {
                    result= request.callback.handle(response);
                }
                final Object finalResult = result;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //用于从网络获取到数据后，异步存储
                        request.callback.asyncPostRequest(finalResult);
                    }
                }).start();
                return request.callback.postRequest(result);
            } else {
                return null;
            }
        } catch (MyAppException e) {
            if (e.getExceptionStatus() == MyAppException.ExceptionStatus.TimeOutException) {
                if (retry < retryCount) {
                    request(retry++, retryCount);
                }
            }
            return e;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if(request.callback.isForceCanceled()){
            return;
        }
        if (result instanceof MyAppException) {
            request.callback.failed((MyAppException) result);
        } else {
            request.callback.success(result);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        request.updateProgressListener.updateProgressData(values[0], values[1]);
    }
}
