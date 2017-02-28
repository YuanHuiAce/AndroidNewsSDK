package com.news.yazhidao.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * 网络类型判断
 *
 * @author Administrator
 */
public class NetUtil {
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");// 4.0模拟器屏蔽掉该权�?

    /**
     * 判断网络情况
     *
     * @return
     */
    public static boolean checkNetWork(Context context) {
        // 重点判断wap方式

        // 操作步骤�?
        // 判断联网的渠道：WLAN VS APN
        boolean isWIFI = isWIFIConnection(context);
        boolean isAPN = isAPNConnection(context);

        // 分支：如果isWIFI、isAPN均为false
        if (!isWIFI && !isAPN) {
            return false;
        }

        return true;
    }

    /**
     * 判断WIFI的链接状�?
     *
     * @param context
     * @return
     */
    private static boolean isAPNConnection(Context context) {
        // 获取到系统服务�?—关于链接的管理
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取到WIFI的链接描述信�?
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 判断WIFI的链接状�?
     *
     * @param context
     * @return
     */
    public static boolean isWIFIConnection(Context context) {

        // 获取到系统服务�?—关于链接的管理
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取到WIFI的链接描述信�?
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 判断网络是不是WIFI
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static String getSimOperatorInfo(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String operatorString = telephonyManager.getSimOperator();

        if (operatorString == null) {
            return "0";
        }

        if (operatorString.equals("46000") || operatorString.equals("46002")) {
            //中国移动
            return "1";
        } else if (operatorString.equals("46001")) {
            //中国联通
            return "2";
        } else if (operatorString.equals("46003")) {
            //中国电信
            return "3";
        }

        //error
        return "0";
    }


}
