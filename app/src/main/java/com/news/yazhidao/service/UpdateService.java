package com.news.yazhidao.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import com.news.sdk.utils.FileUtils;
import com.news.sdk.utils.Logger;

import java.io.File;

/**
 * Created by fiocca on 17/4/24.
 */

public class UpdateService extends Service {
    private static final String TAG = UpdateService.class.getSimpleName();
    private long downId;
    private String md5;
    public static final String DOWNLOAD_FOLDER_NAME = "qidian";
    public static final String DOWNLOAD_FILE_NAME = "qidian.apk";


    public UpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        File file = getExternalFilesDir(DOWNLOAD_FOLDER_NAME);
        Logger.v(TAG, ":::" + file.toString());
        if (file.exists() && file.isDirectory()) {
            Logger.v(TAG, ":::" + file.toString());
            FileUtils.clear(file);
        } else {
            file.mkdirs();

        }
    }

    /**
     * 安卓系统下载类
     **/
    DownloadManager manager;

    /**
     * 接收下载完的广播
     **/
    DownloadCompleteReceiver receiver;

    /**
     * 初始化下载器
     **/
    private void initDownManager(String downloadLink) {

        //下载之前先移除上一个 不然会导致 多次下载不成功问题
//        long id = (long) SPUtils.get(UpdateService.this, SPUtils.KEY, (long) 0);
//        if (id != 0) {
//            manager.remove(id);
//        }

        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();

        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(downloadLink));

        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        // 下载时，通知栏显示途中
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }

        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        down.setMimeType( "application/vnd.android.package-archive");
        // 设置下载后文件存放的位置

        down.setDestinationInExternalFilesDir(this, DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);

        // 将下载请求放入队列
        manager.enqueue(down);
//        SPUtils.put(this, SPUtils.KEY, manager);
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String downloadLink = intent.getStringExtra("downloadLink");
            md5 = intent.getStringExtra("md5");

            // 调用下载
            initDownManager(downloadLink);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
//        SPUtils.put(this, SPUtils.KEY, downId);
        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);

        super.onDestroy();
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                //获取下载的文件id
                downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

//                //自动安装apk
                File file = getExternalFilesDir(DOWNLOAD_FOLDER_NAME+File.separator+DOWNLOAD_FILE_NAME);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Uri uriForDownloadedFile = manager.getUriForDownloadedFile(downId);
                    Logger.d(TAG, "uri=" + uriForDownloadedFile.getEncodedPath()+":::"+uriForDownloadedFile.getPath());
                }
                installApkNew(Uri.fromFile(file));
                //停止服务并关闭广播
                UpdateService.this.stopSelf();

            }
        }

        //安装apk
        protected void installApkNew(Uri uri) {

            Logger.v(TAG, "installApkNew:" + uri.toString());
            Logger.v(TAG, "installApkNew:" + downId);
            Intent intent = new Intent();
            //执行动作
            intent.setAction(Intent.ACTION_VIEW);
            //执行的数据类型
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            //不加下面这句话是可以的，查考的里面说如果不加上这句的话在apk安装完成之后点击单开会崩溃
//            android.os.Process.killProcess(android.os.Process.myPid());
            startActivity(intent);
        }
    }
}
