package com.news.yazhidao.utils.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by fengjigang on 15/5/20.
 * 通知栏的帮助类
 */
public class NotificationHelper {
    public static Notification mNotification;
    public static boolean isViewed=false;
    /**
     * 发送一个通知
     * @param mContext
     */
    public static void sendNotification(Context mContext, String title, String content, Intent clickIntent){
        NotificationManager nm= (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if(mNotification==null){
            mNotification=new Notification();
        }
//        mNotification.icon= R.drawable.app_icon_version2;
        mNotification.tickerText="头条百家有人反馈消息啦!";
        mNotification.defaults=mNotification.DEFAULT_ALL;
        mNotification.flags |= mNotification.FLAG_AUTO_CANCEL;//点击通知后自动清除通知
        PendingIntent pd= PendingIntent.getActivity(mContext,0,clickIntent,0);
//        mNotification.setLatestEventInfo(mContext, title, content, pd);
        nm.notify(0,mNotification);
    }
}
