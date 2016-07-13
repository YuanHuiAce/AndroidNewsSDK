package com.news.yazhidao.entity;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 游客实体类
 * Created by fengjigang on 16/5/25.
 */
public class Visitor implements Serializable {
    private int utype;//本地注册用户	1 ;游客用户	2;微博三方用户	3;微信三方用户	4
    private int uid;
    private String password;
    private String token;//注意此token需要从header中获取,访问接口的时候需要之
    private ArrayList<String> channel;


    public ArrayList<String> getChannel() {
        return channel;
    }

    public void setChannel(ArrayList<String> channel) {
        this.channel = channel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUtype() {
        return utype;
    }

    public void setUtype(int utype) {
        this.utype = utype;
    }

    /**
     * 序列化Visitor 为json字符串
     */
    public String toJsonStr(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * 把json 字符串反序列化为Visitor对象
     */

    public static Visitor json2Visitor(String visitor){
        return new Gson().fromJson(visitor,Visitor.class);
    }
}
