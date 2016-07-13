package com.news.yazhidao.net;

import com.news.yazhidao.utils.GsonUtil;

import java.lang.reflect.Type;

/**
 * Created by fengjigang on 15/1/7.
 */
public abstract class JsonCallback<T> extends AbstractCallBack<T> {
    private Class<T> mReturnClass;
    private Type mReturnType;
    @Override
    protected T bindData(String json) {
//        Logger.i(">>> json ", json);
        if(mReturnClass!=null){
            return GsonUtil.deSerializedByClass(json, mReturnClass);
        }else if(mReturnType!=null){
            return GsonUtil.deSerializedByType(json, mReturnType);
        }
        return null;
    }
    public JsonCallback setReturnClass(Class<T> clazz){
        this.mReturnClass=clazz;
        return this;
    }
    public JsonCallback setReturnType(Type type){
        this.mReturnType=type;
        return this;
    }

    @Override
    public T postRequest(T t) {
        return super.postRequest(t);
    }
}
