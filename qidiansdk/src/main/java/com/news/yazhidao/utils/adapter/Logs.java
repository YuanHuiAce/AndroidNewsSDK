package com.news.yazhidao.utils.adapter;

import android.util.Log;

public class Logs {
    public static long b;
    public static long a;

    public static int LEVEL = 0;
    public static String TAG = "";

    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void v(String msg) {
        if (LEVEL <= 0) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (LEVEL <= 1) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (LEVEL <= 2) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (LEVEL <= 3) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (LEVEL <= 4) {
            Log.e(TAG, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (LEVEL <= 0) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LEVEL <= 1) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (LEVEL <= 2) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (LEVEL <= 3) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LEVEL <= 4) {
            Log.e(tag, msg);
        }
    }
}
