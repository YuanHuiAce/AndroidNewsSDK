package com.yira.information;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;

import com.yira.information.pages.MainActivity;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.net.volley.request.UploadUmengPushIdRequest;
import com.news.sdk.pages.NewsDetailAty2;
import com.news.sdk.pages.NewsDetailVideoAty;
import com.news.sdk.pages.NewsFeedFgt;
import com.news.sdk.pages.NewsTopicAty;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.TextUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by wudi on 16/6/21.
 */
public class DemoApplication extends Application {

    private static PushAgent mPushAgent;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        ShareSDK.initSDK(this);
        QiDianApplication.initQDApp(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        displayMetrics.scaledDensity = displayMetrics.density;
        mSharedPreferences = getSharedPreferences("showflag", MODE_PRIVATE);
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDisplayNotificationNumber(0);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.v("deviceToken", deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        if (!mSharedPreferences.getBoolean("isEnabled", true)) {
            mPushAgent.disable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(String s, String s1) {
                    mSharedPreferences.edit().putBoolean("isEnabled", true).commit();
                }
            });
        } else {

            mPushAgent.enable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                    mSharedPreferences.edit().putBoolean("isEnabled", true).commit();
                }

                @Override
                public void onFailure(String s, String s1) {

                }
            });
        }
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
                if (!TextUtil.isEmptyString(newsid)) {
                    Intent detailIntent = null;
                    if (!TextUtil.isEmptyString(newsid) && rtype.equals("video")) {
                        detailIntent = new Intent(context, NewsDetailVideoAty.class);
                    } else if (!TextUtil.isEmptyString(newsid) && rtype.equals("topic")) {
                        detailIntent = new Intent(context, NewsTopicAty.class);
                    } else {
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
                } else {
                    Intent HomeIntent = new Intent(context, MainActivity.class);
                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(HomeIntent);
                }

            }
        }
    };

    public static PushAgent getPushAgent() {
        return mPushAgent;
    }

}
