package com.news.sdk.entity;

import com.google.gson.reflect.TypeToken;
import com.news.sdk.utils.GsonUtil;

import java.io.Serializable;

/**
 * Created by wudi on 16/5/31.
 */
public class UserVisitorEntity implements Serializable{
    String uid;
    String password;

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    String utype;

    public String getPassword() {
        return password;
    }

    public String getUid() {
        return uid;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "UserVisitorEntity{" +
                "uid='" + uid + '\'' +
                ", password='" + password +
                '}';
    }
    /**
     * 把json 反序列化为 User 对象
     * @param userStr
     * @return
     */
    public static UserVisitorEntity parseUser(String userStr) {
        return GsonUtil.deSerializedByType(userStr, new TypeToken<UserVisitorEntity>() {
        }.getType());
    }

    /**
     * 把User 对象序列化 json
     * @return
     */
    public String toJsonString(){
        return GsonUtil.serialized(this);
    }



}
