package com.news.sdk.utils;

import android.util.Log;

/**
 * Created by fengjigang on 14-10-20.
 */
public class Logger {
    public static final boolean isDebug= true;
    public static void d(String TAG, String msg){
        if(isDebug)
            Log.d(TAG, msg);
    }
    public static void e(String TAG, String msg){
        if(isDebug)
            Log.e(TAG, msg);
    }
    public  static void i(String TAG, String msg){
        if(isDebug)
            Log.i(TAG,msg);
    }
    public static void v(String TAG, String msg){
        if(isDebug)
            Log.v(TAG, msg);
    }
    public static void w(String TAG, String msg){
        if(isDebug)
            Log.w(TAG, msg);
    }
}
