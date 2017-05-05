package com.news.sdk.application;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.github.jinsedeyuzhou.PlayerApplication;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by fengjigang on 15/2/1.
 */
public class QiDianApplication {
    private static Context mContext;
    private static QiDianApplication mInstance;
    private RequestQueue mRequestQueue;
//    public static VPlayPlayer vPlayPlayer;

    public static void initQDApp(Context context) {
        mContext = context;
        mInstance = new QiDianApplication();
        PlayerApplication.initApp(context);
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        displayMetrics.scaledDensity = displayMetrics.density;
//        Context ctx = context.getApplicationContext();
//        // 获取当前包名
//        String packageName = context.getPackageName();
//        // 获取当前进程名
//        String processName = getProcessName(android.os.Process.myPid());
//        // 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(ctx);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
//        // 初始化Bugly
//
//        CrashReport.initCrashReport(context.getApplicationContext(), "876dac1311", isDebug, strategy);
        CrashReport.initCrashReport(context.getApplicationContext(), "876dac1311", true);
//        Fresco.initialize(context);
    }

    QiDianApplication() {
        mInstance = this;
//        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
//                .newBuilder(mContext).setDownsampleEnabled(true)
//                .build();
    }

    //    public void onCreate() {
//        mContext=this;
//        mInstance = this;
    //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(this);
//        PushAgent mPushAgent = PushAgent.getInstance(this);
//        mPushAgent.setDebugMode(false);
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    //init fresco
//        Fresco.initialize(this);
//        String device_token = UmengRegistrar.getRegistrationId(this);
//        Logger.e("device_token","token="+device_token);
//        super.onCreate();
//    }
    public static Context getAppContext() {
        return mContext;
    }

    /**
     * 该Handler是在BroadcastReceiver中被调用，故
     * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
     */
//    UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
//
//        @Override
//        public void dealWithCustomAction(Context context, UMessage msg) {
//            //messageKey key
//            String messageAction = msg.extra.get("messageKey");
//            if ("action_registration_id".equals(messageAction)) {
//                String device_token = UmengRegistrar.getRegistrationId(context);
//                Logger.e("device_token","token="+device_token);
//                UploadUmengPushIdRequest.uploadUmengPushId(context, device_token);
//            } else if ("action_message_received".equals(messageAction)) {
//                String title =msg.extra.get("extra_title");
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
//                    intent1 = new Intent(context, FeedBackActivity.class);
//                    NotificationHelper.sendNotification(context, "测试title", message, intent1);
//                }
//                Logger.i("jigang", "receive custom title=" + title);
//                Logger.i("jigang", "receive custom message=" + message);
//                Logger.i("jigang", "receive custom extras=" + extras);
//                Logger.i("jigang", "receive custom type=" + type);
//                Logger.i("jigang", "receive custom file=" + file);
//            }else if("action_notification_received".equals(messageAction)){
//                //umeng statistic notification received
//                MobclickAgent.onEvent(context, CommonConstant.US_BAINEWS_NOTIFICATION_RECEIVED);
//            } else if ("action_notification_opened".equals(messageAction)) {
//                //umeng statistic notification received and opened it
//                MobclickAgent.onEvent(context,CommonConstant.US_BAINEWS_NOTIFICATION_OPENED);
//
//                String newsid = msg.extra.get("newsid");
//                String collection = msg.extra.get("collection");
//                String newVersion = msg.extra.get("version");
//                Logger.i("jigang", "receive custom newsid=" + newsid + ",collection=" + collection);
//                //此处对传过来的json字符串做处理 {"news_url":"www.baidu.com"}
//                if (!TextUtil.isEmptyString(newsid)) {
//                    Intent detailIntent = new Intent(context, NewsDetailAty2.class);
//                    detailIntent.putExtra(NewsFeedFgt.KEY_NEWS_ID, newsid);
//                    detailIntent.putExtra(NewsFeedFgt.KEY_NEWS_SOURCE, NewsFeedFgt.VALUE_NEWS_NOTIFICATION);
//                    detailIntent.putExtra(NewsFeedFgt.KEY_COLLECTION, collection);
//                    detailIntent.putExtra(NewsFeedFgt.KEY_PUSH_NEWS, collection);
//                    detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(detailIntent);
//                    MobclickAgent.onEvent(QiDianApplication.this,"notification_open");
//                } else if (!TextUtil.isEmptyString(newVersion)){
//                    UmengUpdateAgent.silentUpdate(context);
//                    Logger.e("jigang","need update");
//                } else {
//                    Intent HomeIntent = new Intent(context, MainAty.class);
//                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(HomeIntent);
//                }
//
//            }
//        }
//    };
    public static synchronized QiDianApplication getInstance() {
        if (null == mInstance) {
            mInstance = new QiDianApplication();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

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
