package com.news.sdk.net.volley.request;


import com.news.sdk.utils.TextUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by fengjigang on 15/1/6.
 */
public abstract class AbstractCallBack<T> implements ICallback<T> {
    private String path;
    private boolean isCanceled;
    private boolean isForceCanceled;
    public void checkIsCanceled() throws MyAppException {
        if(isCanceled){
            throw new MyAppException(MyAppException.ExceptionStatus.CanceledException,"request cancel!!");
        }
    }

    public T preRequest() {
        return null;
    }

    public T postRequest(T t) {
        return t;
    }
    protected void asyncPostRequest(T t){}
    public int retryCount() {
        return 0;
    }

    public T handle(HttpResponse response) throws MyAppException{
        return handle(response,null);
    }
    public T handle(HttpResponse response, IUpdateProgressListener updateProgressListener) throws MyAppException {
        try {
            checkIsCanceled();
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    HttpEntity entity = response.getEntity();
                    Header header = entity.getContentEncoding();
                    if (TextUtil.isValidate(path)) {
                        InputStream is = entity.getContent();
                        FileOutputStream fos = new FileOutputStream(path);
                        byte[] buffer = new byte[1024 * 4];
                        int len = -1;
                        long current=0;
                        long contentLength=entity.getContentLength();
                        while ((len = is.read(buffer)) != -1) {
                            checkIsCanceled();
                            if(updateProgressListener!=null) {
                                current += len;
                                updateProgressListener.updateProgressData((int) (current / 1024), (int) (contentLength/1024));
                            }
                            fos.write(buffer, 0, len);
                        }
                        is.close();
                        fos.close();
                        return bindData(path);
                    } else {
                        //判断返回内容是否为gzip压缩格式
                        if(header != null && header.getValue().equalsIgnoreCase("gzip")){
                            GZIPInputStream gzipStream = new GZIPInputStream(entity.getContent());
                            return bindData(TextUtil.getResponseContent(gzipStream));
                        }else{
                            return bindData(EntityUtils.toString(entity, "UTF-8"));
                        }
                    }
                default:
                    break;
            }
        } catch (IOException e) {
            throw new MyAppException(MyAppException.ExceptionStatus.IOException,e.getMessage());
        }
        return null;
    }
    public AbstractCallBack<T> setPath(String path){
        this.path=path;
        return this;
    }
    protected abstract T bindData(String content);

    public void cancel(boolean force) {
        this.isForceCanceled=force;
        this.isCanceled=true;
    }
    public boolean isForceCanceled(){
        return isForceCanceled;
    }

}
