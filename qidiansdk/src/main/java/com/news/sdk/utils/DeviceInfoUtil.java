package com.news.sdk.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.news.sdk.application.QiDianApplication;
import com.news.sdk.utils.manager.SharedPreManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by fengjigang on 15-2-2.
 */
public class DeviceInfoUtil {
    private static final String TAG = "DeviceStateUtil";

    public static int getScreenWidth() {
        return obtainDisMetri().widthPixels;
    }

    public static int getScreenHeight() {
        return obtainDisMetri().heightPixels;
    }

    private static DisplayMetrics obtainDisMetri() {
        WindowManager wm = (WindowManager) QiDianApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    /**
     * 获取手机屏幕宽度,此方法有效防止空指针
     *
     * @param pContext
     * @return
     */
    public static int getScreenWidth(Context pContext) {
        WindowManager wm = (WindowManager) pContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取手机屏幕高度,此方法有效防止空指针
     *
     * @param pContext
     * @return
     */
    public static int getScreenHeight(Context pContext) {
        WindowManager wm = (WindowManager) pContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static float obtainDensity() {
        WindowManager wm = (WindowManager) QiDianApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.density;
    }

    /**
     * 获取状态栏的高度
     *
     * @param mContext
     * @return
     */
    public static int getStatusBarHeight(Context mContext) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, barHeight = DensityUtil.dip2px(mContext, 25);//默认为25dp，貌似大部分是这样的

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            barHeight = mContext.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return barHeight;
    }

    /**
     * 获取当前网络状态(wifi)的ip地址
     *
     * @return
     */
    public static String getIpAddress(Context appContext) {
        WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();
        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

    }

    /**
     * 获取当前网络状态(3G)ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前系统的版本号
     *
     * @return
     */
    public static String getOsVersion() {
        return "Android " + Build.VERSION.RELEASE;
    }

    /**
     * 获取当前应用的渠道
     *
     * @param
     * @return
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return "wifi";
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return "2G";
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return "3G";
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return "4G";
                        default:
                            return "unknown";
                    }
                default:
                    return "unknown";
            }

        }
        return "unknown";
    }

    /**
     * 判断当前的网络状态是否是WIFI
     *
     * @param mContext
     * @return
     */
    public static boolean isWifiNetWorkState(Context mContext) {
        return "wifi".equals(getNetworkType(mContext));
    }

    public static String getApkSource(Context appContext) {
        return getManifestMetaData(appContext, "UMENG_CHANNEL");
    }

    /**
     * 获取当前应用的包名
     *
     * @param pContext
     * @return
     */
    public static String getApkPackageName(Context pContext) {
        PackageInfo info = null;
        String packageName = null;
        try {
            info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), 0);
            packageName = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }

    /**
     * 网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        // 判断网络是否连接
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 判断手机是否是wifi连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断手机是否是数据网络连接
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取ManifestMetaData 中meta中的值
     *
     * @param appContext
     * @param metaKey
     * @return
     */
    public static String getManifestMetaData(Context appContext, String metaKey) {
        try {
            ApplicationInfo appi = appContext.getPackageManager()
                    .getApplicationInfo(appContext.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = appi.metaData;
            Object value = bundle.get(metaKey);
            return String.valueOf(value);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 获取当前显示的activity的ClassName
     *
     * @param mContext
     * @return
     */
    public static String getTopActivityName(Context mContext) {
        String activityName = null;
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appTasks = manager.getRunningTasks(1);
        if (!appTasks.isEmpty()) {
            ComponentName topActivity = appTasks.get(0).topActivity;
            activityName = topActivity.getClassName();
        }
        return activityName;
    }

    /**
     * 判断activity是否在栈顶
     *
     * @param mContext
     * @param simpleClassName
     * @return
     */
    public static boolean isRunningForeground(Context mContext, String simpleClassName) {
        String packageName = mContext.getPackageName();
        String topActivityName = getTopActivityName(mContext);
        if (packageName != null && topActivityName != null) {
            return topActivityName.endsWith(simpleClassName);
        } else {
            return false;
        }
    }

    /**
     * 获取当前app版本
     *
     * @param appContext
     * @return
     */
    public static String getApkVersion(Context appContext) {
        PackageManager manager = appContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(appContext.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前app VersionCode版本
     *
     * @param appContext
     * @return
     */
    public static int getApkVersionCode(Context appContext) {
        PackageManager manager = appContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(appContext.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getMacAddress(Context c) {
        WifiManager wifiMan = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String macAddr = wifiInf.getMacAddress();
        return macAddr;
    }

    /**
     * 获取当前的设备号
     *
     * @return
     */
    public static String getUUID() {
        TelephonyManager telephonyManager = (TelephonyManager) QiDianApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = Build.PRODUCT + getMacAddress(QiDianApplication.getAppContext()) + getDeviceSerial() + Settings.Secure.getString(QiDianApplication.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return generateMD5(uuid);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static String getDeviceSerial() {
        if (Build.VERSION.SDK_INT >= 9) {
            return Build.SERIAL;
        }
        String serial = "unknown";
        try {
            Class<?> clazz = Class.forName("android.os.Build");
            Class<?> paraTypes = Class.forName("java.lang.String");
            Method method = clazz.getDeclaredMethod("getString", paraTypes);
            if (method != null && !method.isAccessible()) {
                method.setAccessible(true);
                serial = (String) method.invoke(new Build(), "ro.serialno");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 把字符串MD5
     *
     * @param original
     * @return
     */
    public static String generateMD5(String original) {
        try {
            Logger.d(TAG, "before MD5 " + original);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(original.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            Logger.d(TAG, "after MD5 " + sb.toString());
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isHaveWeixin(Context context) {
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = (PackageInfo) packlist.get(i);

            // 判断是否为非系统预装的应用程序
            // 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
                if (pak.packageName.equals("com.tencent.mm")) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * 判断是否是魅族系统
     *
     * @return
     */
    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    public static int getDpi(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        int height = 0;
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            height = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }

    public static int[] getScreenWH(Context poCotext) {
        WindowManager wm = (WindowManager) poCotext
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new int[]{width, height};
    }

    /**
     * 获取非魅族手机底部虚拟键的高度
     *
     * @param poCotext
     * @return
     */
    public static int getVrtualBtnHeight(Context poCotext) {
        int location[] = getScreenWH(poCotext);
        int realHeiht = getDpi((Activity) poCotext);
        int virvalHeight = realHeiht - location[1];
        return virvalHeight;
    }

    /**
     * 判断横竖屏幕
     *
     * @return
     */
    public static boolean isScreenChange(Context mContext) {

        Configuration mConfiguration = mContext.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            return true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            return false;
        }
        return false;
    }

    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMacAddress() {
        /*获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。*/
        String macAddress;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }

    /**
     * 保存设置IMEI
     */
    public static String getDeviceImei(Context context) {
        try {
            if (context != null) {
                TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephonyManager != null) {
                    String deviceid = mTelephonyManager.getDeviceId();
                    return TextUtils.isEmpty(deviceid) ? SharedPreManager.mInstance(context).get("flag", "imei") : deviceid;
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * 保存设置IMSI
     */
    public static String getDeviceImsi(Context context) {
        try {
            if (context != null) {
                TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephonyManager != null) {
                    String subscriberId = mTelephonyManager.getSubscriberId();
                    return TextUtils.isEmpty(subscriberId) ? SharedPreManager.mInstance(context).get("flag", "imei") : subscriberId;
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }
}
