package com.news.yazhidao;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.net.volley.request.UploadUmengPushIdRequest;
import com.news.sdk.pages.NewsDetailAty2;
import com.news.sdk.pages.NewsDetailVideoAty;
import com.news.sdk.pages.NewsFeedFgt;
import com.news.sdk.pages.NewsTopicAty;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.TextUtil;
import com.news.yazhidao.pages.MainActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by wudi on 16/6/21.
 */
public class DemoApplication extends Application {

    private PushAgent mPushAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        QiDianApplication.initQDApp(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        displayMetrics.scaledDensity = displayMetrics.density;
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDisplayNotificationNumber(0);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.v("deviceToken",deviceToken);
//                UploadUmengPushIdRequest.uploadUmengPushId(this, deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

//        Context ctx =getApplicationContext();
//        // 获取当前包名
//        String packageName = getPackageName();
//        // 获取当前进程名
//        String processName = getProcessName(android.os.Process.myPid());
//        // 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(ctx);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
//        // 初始化Bugly
////        CrashReport.initCrashReport(this, "876dac1311", isDebug, strategy);
//        CrashReport.initCrashReport(getApplicationContext(), "876dac1311", true);
    }

    /**
     * 该Handler是在BroadcastReceiver中被调用，故
     * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
     */
    UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
            //messageKey key
            String messageAction = msg.extra.get("messageKey");
            if ("action_registration_id".equals(messageAction)) {
                String device_token = mPushAgent.getRegistrationId();
                Logger.e("device_token", "token=" + device_token);
                UploadUmengPushIdRequest.uploadUmengPushId(context, device_token);
            } else if ("action_message_received".equals(messageAction)) {
//                String title = msg.extra.get("extra_title");
//                String message = msg.extra.get("extra_message");
//                String extras = msg.extra.get("extra_extra");
//                String type = msg.extra.get("extra_content_type");
//                String file = msg.extra.get("extra_richpush_file_path");
//                //判断反馈界面是否在前台
//                boolean isFeedBackForeground = DeviceInfoUtil.isRunningForeground(context, FeedBackActivity.class.getSimpleName());
//                //判断会话列表是否在前台
//                boolean isMessageListForeground = DeviceInfoUtil.isRunningForeground(context, ChatAty.class.getSimpleName());
//                Intent intent1;
//                if (isFeedBackForeground) {
//                    intent1 = new Intent("FeedBackMessage");
//                    intent1.putExtra("message", message);
//                    context.sendBroadcast(intent1);
//                } else if (isMessageListForeground) {
//                    intent1 = new Intent("FeedBackMessageList");
//                    intent1.putExtra("message", message);
//                    context.sendBroadcast(intent1);
//                } else {
////                    intent1 = new Intent(context, FeedBackActivity.class);
////                    NotificationHelper.sendNotification(context, "测试title", message, intent1);
//                }
//                Logger.i("jigang", "receive custom title=" + title);
//                Logger.i("jigang", "receive custom message=" + message);
//                Logger.i("jigang", "receive custom extras=" + extras);
//                Logger.i("jigang", "receive custom type=" + type);
//                Logger.i("jigang", "receive custom file=" + file);
            } else if ("action_notification_received".equals(messageAction)) {
                //umeng statistic notification received
                MobclickAgent.onEvent(context, CommonConstant.US_BAINEWS_NOTIFICATION_RECEIVED);
            } else if ("action_notification_opened".equals(messageAction)) {
                //umeng statistic notification received and opened it
                MobclickAgent.onEvent(context, CommonConstant.US_BAINEWS_NOTIFICATION_OPENED);
                String newsid = msg.extra.get("newsid");
                String collection = msg.extra.get("collection");
                String newVersion = msg.extra.get("version");
                String rtype = msg.extra.get("rtype");
                //此处对传过来的json字符串做处理 {"news_url":"www.baidu.com"}
                if (!TextUtil.isEmptyString(newsid)) {
                    Intent detailIntent = null;
                    if (!TextUtil.isEmptyString(newsid) && rtype.equals("video")) {
                        detailIntent = new Intent(context, NewsDetailVideoAty.class);
                    } else if (!TextUtil.isEmptyString(newsid) && rtype.equals("topic")){
                        detailIntent = new Intent(context, NewsTopicAty.class);
                    }else
                    {
                        detailIntent = new Intent(context, NewsDetailAty2.class);
                    }
                    detailIntent.putExtra(NewsFeedFgt.KEY_NEWS_ID, newsid);
                    detailIntent.putExtra(NewsTopicAty.KEY_NID, Integer.valueOf(newsid));
                    detailIntent.putExtra(CommonConstant.KEY_SOURCE, NewsFeedFgt.VALUE_NEWS_NOTIFICATION);
                    detailIntent.putExtra(NewsFeedFgt.KEY_COLLECTION, collection);
                    detailIntent.putExtra(NewsFeedFgt.KEY_PUSH_NEWS, collection);
                    detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(detailIntent);
                    MobclickAgent.onEvent(DemoApplication.this, "notification_open");
                } else if (!TextUtil.isEmptyString(newVersion)) {
//                    UmengUpdateAgent.silentUpdate(context);

//                    Logger.e("jigang", "need update");
                } else {
                    Intent HomeIntent = new Intent(context, MainActivity.class);
                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(HomeIntent);
                }

            }
        }
    };


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
