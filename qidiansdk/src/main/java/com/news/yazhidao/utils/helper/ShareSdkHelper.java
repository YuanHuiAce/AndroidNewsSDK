package com.news.yazhidao.utils.helper;//package com.news.yazhidao.utils.helper;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.os.Handler;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.news.yazhidao.application.QiDianApplication;
//import com.news.yazhidao.R;
//import com.news.yazhidao.application.QiDianApplication;
//import com.news.yazhidao.common.HttpConstant;
//import com.news.yazhidao.entity.NewsFeed;
//import com.news.yazhidao.entity.User;
//import com.news.yazhidao.listener.UserAuthorizeListener;
//import com.news.yazhidao.listener.UserLoginListener;
//import com.news.yazhidao.listener.UserLoginPopupStateListener;
//import com.news.yazhidao.listener.UserLoginRequestListener;
//import com.news.yazhidao.net.MyAppException;
//import com.news.yazhidao.net.NetworkRequest;
//import com.news.yazhidao.net.StringCallback;
//import com.news.yazhidao.net.UserCallback;
//import com.news.yazhidao.net.request.UploadJpushidRequest;
//import com.news.yazhidao.net.request.UserLoginRequest;
//import com.news.yazhidao.pages.MainAty;
//import com.news.yazhidao.utils.DeviceInfoUtil;
//import com.news.yazhidao.utils.Logger;
//import com.news.yazhidao.utils.TextUtil;
//import com.news.yazhidao.utils.ToastUtil;
//import com.news.yazhidao.utils.manager.SharedPreManager;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//
//import cn.sharesdk.douban.Douban;
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.PlatformDb;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.renren.Renren;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qq.QQ;
//import cn.sharesdk.tencent.qzone.QZone;
//import cn.sharesdk.tencent.weibo.TencentWeibo;
//import cn.sharesdk.wechat.friends.Wechat;
//import cn.sharesdk.wechat.moments.WechatMoments;
//import sdk.meizu.auth.MzAuthenticator;
//import sdk.meizu.auth.OAuthError;
//import sdk.meizu.auth.callback.CodeCallback;
//
///**
// * Created by fengjigang on 15/5/8.
// * ShareSdk 分享和授权帮助类
// */
//public class ShareSdkHelper {
//
//
//    public static enum AuthorizePlatform {
//        WEIXIN,WEIBO,MEIZU
//    }
//
//    private static final String TAG = "ShareSdkHelper";
//    private static String CLIENT_ID = "tsGKllOEx2MnUVmBmRey";
//    private static String REDIRECT_URI = "http://www.deeporiginalx.com/";
//    private static String CLIENT_SECRET = "gOMuh3824Tx2UKJWvu3Qa3DsUTSvyv";
//    private static final String WECHAT_CLIENT_NOT_EXIST_EXCEPTION = "cn.sharesdk.wechat.utils.WechatClientNotExistException";
//    private static Context mContext;
//    private static Handler mHandler = new Handler();
//    private static UserLoginListener mUserLoginListener;
//    private static UserLoginPopupStateListener mUserLoginPopupStateListener;
//    private static UserAuthorizeListener mAuthorizeListener;
//    private static PlatformActionListener mActionListener = new PlatformActionListener() {
//        @Override
//        public void onComplete(final Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
//            Logger.i(TAG, "onComplete-----");
//            String userId = platform.getDb().getUserId();
//            if (userId != null) {
//                PlatformDb platformDb = platform.getDb();
//                String nickName = platformDb.getUserName();
//                String gender = platformDb.getUserGender();
//                String iconURL = platformDb.getUserIcon();
//                String token = platformDb.getToken();
//                //关注官方微博
//                if (SinaWeibo.NAME.equals(platformDb.getPlatformNname())) {
//                    platform.followFriend(mContext.getResources().getString(R.string.app_name));
//                }
//                Log.i(TAG, "nickName=" + nickName + ",gender=" + gender + ",iconURL=" + iconURL + ",token=" + token);
//                UserLoginRequest.userLogin(platformDb, new UserLoginRequestListener() {
//                    @Override
//                    public void success(User user) {
//                        if (mAuthorizeListener != null){
//                            mAuthorizeListener.success(user);
//                        }
//                        //保存user json串到sp 中
//                        SharedPreManager.saveUser(user);
//                        String jPushId = SharedPreManager.getJPushId();
//                        if (!TextUtils.isEmpty(jPushId)) {
//                            UploadJpushidRequest.uploadJpushId(mContext, jPushId);
//                        }
////                      SharedPreManager.saveUserIdAndPlatform(CommonConstant.FILE_USER, CommonConstant.KEY_USER_ID_AND_PLATFORM, userId, platform.getName());
//
//                        Intent intent = new Intent(MainAty.ACTION_USER_LOGIN);
//                        intent.putExtra(MainAty.KEY_INTENT_USER_URL, user.getUserIcon());
//                        mContext.sendBroadcast(intent);
//
//                    }
//
//                    @Override
//                    public void failed(MyAppException exception) {
//                        Logger.e(TAG, "UserLoginRequest exception");
//                        if (mAuthorizeListener != null){
//                            mAuthorizeListener.failure(exception.getMessage());
//                        }
//                    }
//                });
//
//            }
//        }
//
//        @Override
//        public void onError(final Platform platform, int i, final Throwable throwable) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (WECHAT_CLIENT_NOT_EXIST_EXCEPTION.equals(throwable.toString())) {
//                        Logger.e(TAG, "000000");
//                        ToastUtil.toastShort("您手机还未安装微信客户端");
//                    }
//
////                    mUserLoginPopupStateListener.close();
//                }
//            });
//            throwable.printStackTrace();
//            if (mAuthorizeListener != null){
//                mAuthorizeListener.failure(throwable.getMessage());
//            }
//            Logger.e(TAG, "authorize error-----" + i + ",,," + throwable.toString());
//        }
//
//        @Override
//        public void onCancel(Platform platform, int i) {
//            Logger.e(TAG, "authorize cancel-----");
//            if (mAuthorizeListener != null){
//                mAuthorizeListener.cancel();
//            }
////            mHandler.post(new Runnable() {
////                @Override
////                public void run() {
////                    mUserLoginPopupStateListener.close();
////
////                }
////            });
//        }
//    };
//
//
//    public static void authorize(Context pContext, AuthorizePlatform pPlatform, UserAuthorizeListener pAuthorizeListener){
//        mContext = pContext;
//        mAuthorizeListener = pAuthorizeListener;
//        String shareSdkPlatform = null;
//        if (pPlatform == AuthorizePlatform.WEIBO){
//            shareSdkPlatform = SinaWeibo.NAME;
//        }else if (pPlatform == AuthorizePlatform.WEIXIN){
//            shareSdkPlatform = Wechat.NAME;
//        }
//        Platform _Plateform = ShareSDK.getPlatform(mContext, shareSdkPlatform);
//        //判断指定平台是否已经完成授权
//        User user = SharedPreManager.getUser(mContext);
//        if (_Plateform.isAuthValid() && user != null) {
//            String userId = _Plateform.getDb().getUserId();
//            if (userId != null) {
//                if (pAuthorizeListener != null){
//                    pAuthorizeListener.success(user);
//                }
//                return;
//            }
//        }
//        _Plateform.SSOSetting(false);
//        _Plateform.setPlatformActionListener(mActionListener);
//        _Plateform.authorize();
//    }
//
//    /**
//     * sharesdk 授权认证
//     *
//     * @param context
//     * @param platform
//     * @param loginListener
//     * @param userLoginPopupStateListener
//     */
//    public static void authorize(Context context, String platform, UserLoginListener loginListener, UserLoginPopupStateListener userLoginPopupStateListener) {
////        mProgressDialog.show();
//        mContext = context;
//        mUserLoginListener = loginListener;
//        mUserLoginPopupStateListener = userLoginPopupStateListener;
//        Platform _Plateform = ShareSDK.getPlatform(mContext, platform);
//        if ("meizu".equals(platform)) {
//            meizuLogin();
//            return;
//        }
//        //判断指定平台是否已经完成授权
//        if (_Plateform.isValid() && SharedPreManager.getUser(context) != null) {
//            String userId = _Plateform.getDb().getUserId();
//            if (userId != null) {
//                if (mUserLoginListener != null) {
//                    mUserLoginListener.userLogin(platform, _Plateform.getDb());
//                }
//                return;
//            }
//        }
//        _Plateform.SSOSetting(false);
//        _Plateform.setPlatformActionListener(mActionListener);
//        _Plateform.authorize();
//    }
//
//    /**
//     * 用户注销登陆
//     */
//    public static void logout(Context mContext) {
//        //TODO 用户注销
//        SharedPreManager.deleteUser(mContext);
//    }
//
//
//    //分享内容到具体平台
//    public static void ShareToPlatform(final Context context, final String argPlatform, final NewsFeed newsFeed) {
//        mHandler = new Handler(context.getMainLooper());
//        PlatformActionListener pShareListner = new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
//                Log.i("jigang", "share complete");
//                Runnable myRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.toastShort("分享成功");
//                    }
//                };
//                mHandler.post(myRunnable);
//
//            }
//
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                Log.e("jigang", "share error " + throwable.getMessage());
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.toastShort("分享失败");
//                    }
//                });
//            }
//
//            @Override
//            public void onCancel(Platform platform, int i) {
//                Log.i("jigang", "share cancel");
//            }
//        };
//        String strShareText = "这里是分享的标题";
//
//        Platform.ShareParams pShareParams = new Platform.ShareParams();
//        pShareParams.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");
//        if (argPlatform.equals(Wechat.NAME) ||
//                argPlatform.equals(WechatMoments.NAME)) {
//            pShareParams.setShareType(Platform.SHARE_WEBPAGE);
//            pShareParams.setTitle(strShareText);
//            pShareParams.setUrl("http://www.baidu.com");
//        } else {
//            pShareParams.setText("aaaaaa");
//        }
//
//        if (argPlatform.equals(Wechat.NAME)) {
//            Platform platform = ShareSDK.getPlatform(Wechat.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            pShareParams.setText("头条百家分享社区");
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(WechatMoments.NAME)) {
//            Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(SinaWeibo.NAME)) {
//            Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(TencentWeibo.NAME)) {
//            Platform platform = ShareSDK.getPlatform(TencentWeibo.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(Renren.NAME)) {
//            Platform platform = ShareSDK.getPlatform(Renren.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(Douban.NAME)) {
//            Platform platform = ShareSDK.getPlatform(Douban.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        }
//    }
//
//    private static void meizuLogin() {
//        MzAuthenticator mAuthenticator = new MzAuthenticator(CLIENT_ID, REDIRECT_URI);
//        mAuthenticator.requestCodeAuth((MainAty) mContext, "uc_basic_info", new CodeCallback() {
//            @Override
//            public void onError(OAuthError oAuthError) {
//                mUserLoginPopupStateListener.close();
//                ToastUtil.toastShort("魅族账号授权失败!");
//            }
//
//            @Override
//            public void onGetCode(String code) {
//                List<NameValuePair> nameValuePairs = new LinkedList<>();
//                nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
//                nameValuePairs.add(new BasicNameValuePair("client_id", CLIENT_ID));
//                nameValuePairs.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
//                nameValuePairs.add(new BasicNameValuePair("code", code));
//                nameValuePairs.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
//                nameValuePairs.add(new BasicNameValuePair("state", "11"));
//                NetworkRequest request = new NetworkRequest("https://open-api.flyme.cn/oauth/token", NetworkRequest.RequestMethod.POST);
//                request.setParams(nameValuePairs);
//                request.setTimeOut(10000);
//                request.setCallback(new StringCallback() {
//                    @Override
//                    public void success(String result) {
//                        JSONObject dataJson;
//                        String strToken = null;
//                        try {
//                            dataJson = new JSONObject(result);
//                            strToken = dataJson.getString("access_token");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        final String token = strToken;
//                        NetworkRequest request = new NetworkRequest("https://open-api.flyme.cn/v2/me?access_token=" + strToken);
//                        request.setTimeOut(10000);
//                        request.setCallback(new StringCallback() {
//
//                            @Override
//                            public void success(String result) {
//                                JSONObject resultJson;
//                                try {
//                                    resultJson = new JSONObject(result);
//                                    JSONObject jsonvalue = resultJson.getJSONObject("value");
//                                    Log.i("eva", jsonvalue.toString());
//                                    final String strIcon = jsonvalue.getString("icon");
//                                    final String strNickname = jsonvalue.getString("nickname");
//                                    Log.i("eva", strIcon + "strIcon");
//                                    Log.i("eva", strNickname + "strNickname");
//                                    HashMap<String, Object> params = new HashMap<>();
//                                    params.put("uuid", DeviceInfoUtil.getUUID());
//                                    params.put("userId", TextUtil.getDatabaseId());
//                                    params.put("expiresIn", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30l);
//                                    params.put("expiresTime", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30l);
//                                    params.put("token", token);
//                                    params.put("userGender", "1");
//                                    params.put("userIcon", strIcon);
//                                    params.put("userName", strNickname);
//                                    params.put("platformType", "meizu");
//                                    NetworkRequest request = new NetworkRequest(HttpConstant.URL_USER_LOGIN, NetworkRequest.RequestMethod.GET);
//                                    request.getParams = params;
//                                    request.setCallback(new UserCallback<User>() {
//                                        @Override
//                                        public void success(final User user) {
//                                            SharedPreManager.saveUser(user);
//
//                                            Intent intent = new Intent("saveuser");
//                                            intent.putExtra("url", user.getUserIcon());
//                                            mContext.sendBroadcast(intent);
//
//                                            if (mUserLoginListener != null) {
//                                                new Handler().post(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        mUserLoginPopupStateListener.close();
//                                                        PlatformDb platformDb = new PlatformDb(mContext, "platformNname", 1);
//                                                        platformDb.put("nickname", strNickname);
//                                                        platformDb.put("icon", strIcon);
//                                                        mUserLoginListener.userLogin(strNickname, platformDb);
//                                                    }
//                                                });
//                                            }
//                                        }
//
//                                        @Override
//                                        public void failed(MyAppException exception) {
//                                            Logger.e("jigang", "333--" + exception.getMessage());
//                                        }
//                                    }.setReturnClass(User.class));
//                                    request.execute();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void failed(MyAppException exception) {
//                                Logger.e("jigang", "111--" + exception.getMessage());
//                            }
//                        });
//                        request.execute();
//                    }
//
//                    @Override
//                    public void failed(MyAppException exception) {
//                        Logger.e("jigang", "222--" + exception.getMessage());
//                    }
//                });
//                request.execute();
//            }
//        });
//    }
//
//    public static void ShareToPlatformByNewsDetail(final Context context, final String argPlatform, final String title, final String url, final String remark) {
//        mHandler = new Handler(context.getMainLooper());
//        PlatformActionListener pShareListner = new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
//                Runnable myRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.toastShort("分享成功");
//                    }
//                };
//                mHandler.post(myRunnable);
//
//            }
//
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.toastShort("分享失败");
//                    }
//                });
//            }
//
//            @Override
//            public void onCancel(Platform platform, int i) {
//            }
//        };
//
//        Platform.ShareParams pShareParams = new Platform.ShareParams();
//        pShareParams.setImageData(BitmapFactory.decodeResource(QiDianApplication.getAppContext().getResources(), R.drawable.app_icon));
////        pShareParams.setImageUrl("http://www.wyl.cc/wp-content/uploads/2014/02/10060381306b675f5c5.jpg");
//        if (argPlatform.equals(Wechat.NAME) ||
//                argPlatform.equals(WechatMoments.NAME)) {
//            pShareParams.setShareType(Platform.SHARE_WEBPAGE);
//            pShareParams.setTitle(title);
//            pShareParams.setUrl(url);
//        } else {
//            pShareParams.setText(title + url);
//        }
//        if (argPlatform.equals(Wechat.NAME)) {
//            Platform platform = ShareSDK.getPlatform(Wechat.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            if (TextUtil.isEmptyString(remark))
//                pShareParams.setText("头条百家分享社区");
//            else
//                pShareParams.setText(remark);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(WechatMoments.NAME)) {
//            Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(SinaWeibo.NAME)) {
//            Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(TencentWeibo.NAME)) {
//            Platform platform = ShareSDK.getPlatform(TencentWeibo.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(QQ.NAME)) {
//            Platform platform = ShareSDK.getPlatform(QQ.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(QZone.NAME)) {
//            Platform platform = ShareSDK.getPlatform(QZone.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(Renren.NAME)) {
//            Platform platform = ShareSDK.getPlatform(Renren.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        } else if (argPlatform.equals(Douban.NAME)) {
//            Platform platform = ShareSDK.getPlatform(Douban.NAME);
//            platform.setPlatformActionListener(pShareListner);
//            platform.share(pShareParams);
//        }
//    }
//}
