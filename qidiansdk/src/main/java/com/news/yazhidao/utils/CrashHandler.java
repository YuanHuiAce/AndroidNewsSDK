package com.news.yazhidao.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 */
@SuppressLint("ParserError")
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    public static final String LOG_DIR = "/TouTiaoBaiJia/crash";
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getPath();

    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;

    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();

    private Context mContext;

    // 用来存储设备信息和异常信息
    private static Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 初始化
     *
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        if (mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfo2File(ex, LOG_DIR);
        return true;
    }

    /**
     * 收集设备参数信息
     */
    public static void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }


    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex, String logPath) {

        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n"); // 设备信息
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();

        // If the Throwable contains a cause, the method will be invoked
        // recursively for the nested
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = time + "-" + timestamp + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = SD_CARD_PATH + logPath;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);
                fos.write(EncodingUtils.getBytes(sb.toString(), HTTP.UTF_8));
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file", e);
        }
        return null;
    }

    // 获取日志文件名
    public static String[] getCrashReportFileNames(Context context) {
        File reportDir = new File(SD_CARD_PATH + LOG_DIR);
        // 文件名过滤器
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".log");
            }
        };
        return reportDir.list(filter);

    }


    public static void deleteFiles(File[] files, String fileIndexs) {
        if (fileIndexs != null && !fileIndexs.equals("")) {
            String[] indexs = fileIndexs.split(",");
            for (String str : indexs) {
                int i = Integer.parseInt(str);
                if (i > files.length - 1) {
                    continue;
                }
                files[i].delete();
                Log.e(TAG, "----> 已删除文件" + files[i].getName());
            }
        }
    }

    // 重新启动程序
    private void reStartProcess() {
        // 重新启动
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(launchIntent);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // 获取分辨率
    public static String getResolution(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels + "x" + dm.widthPixels;
    }
}
