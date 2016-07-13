package com.news.yazhidao.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by fengjigang on 15/1/7.
 */
public class GsonUtil {
    private static Gson gson=new Gson();
    public static String serialized(Object obj){
        return gson.toJson(obj);
    }
    public static <T> T deSerializedByClass(String json, Class<T> Clazz){
        return gson.fromJson(json,Clazz);
    }
    public  static <T> T  deSerializedByType(String json, Type type){
        return gson.fromJson(json,type);
    }
}
