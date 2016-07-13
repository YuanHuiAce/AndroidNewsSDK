package com.news.yazhidao.net;

import com.news.yazhidao.utils.GsonUtil;
import com.news.yazhidao.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by fengjigang on 15/5/13.
 */
public abstract class UserCallback<T> extends AbstractCallBack<T> {
    private Class<T> mReturnClass;
    private Type mReturnType;
    @Override
    protected T bindData(String json) {
        Logger.i(">>> json ", json);
        try {
            JSONObject userObj=new JSONObject(json);
            JSONObject userRet=userObj.getJSONObject("user");
            if(mReturnClass!=null){
                return GsonUtil.deSerializedByClass(userRet.toString(), mReturnClass);
            }else if(mReturnType!=null){
                return GsonUtil.deSerializedByType(userRet.toString(), mReturnType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public UserCallback<T> setReturnClass(Class<T> clazz){
        this.mReturnClass=clazz;
        return this;
    }
    public UserCallback<T> setReturnType(Type type){
        this.mReturnType=type;
        return this;
    }
}
