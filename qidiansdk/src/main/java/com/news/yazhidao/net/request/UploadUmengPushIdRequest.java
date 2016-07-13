package com.news.yazhidao.net.request;

import android.content.Context;

import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.StringCallback;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.manager.SharedPreManager;

import java.util.HashMap;

/**
 * Created by fengjigang on 15/5/14.
 */
public class UploadUmengPushIdRequest {
    private static final String TAG = "UploadUmengPushIdRequest";

    public static void uploadUmengPushId(Context mContext, final String pUmengPushId) {

        User user = SharedPreManager.getUser(mContext);
        NetworkRequest request = new NetworkRequest(HttpConstant.URL_UPLOAD_UMENGPUSHID, NetworkRequest.RequestMethod.GET);
        HashMap<String, Object> params = new HashMap<>();
        params.put("uuid", DeviceInfoUtil.getUUID());
        params.put("umengPushId", pUmengPushId);
        params.put("userId", user == null ? "" : user.getUserId());
        params.put("platformType", user == null ? "" : user.getPlatformType());
        request.getParams = params;
        request.setCallback(new StringCallback() {
            @Override
            public int retryCount() {
                return 3;
            }

            @Override
            public void success(String result) {
                if (result.contains("200")) {
                    SharedPreManager.saveJPushId(pUmengPushId);
                    Logger.i(TAG, "upload jpushid success");
                }else{
                    Logger.i(TAG,"upload jpushid success---"+result);
                }
            }

            @Override
            public void failed(MyAppException exception) {
                Logger.i(TAG, "upload jpushid failed");
            }
        });
        request.execute();
    }
}
