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
@Deprecated
public class UploadJpushidRequest {
    private static final String TAG = "UploadJpushidRequest";

    public static void uploadJpushId(Context mContext, final String jpushId) {

        User user = SharedPreManager.getUser(mContext);
        NetworkRequest request = new NetworkRequest(HttpConstant.URL_UPLOAD_JPUSHID, NetworkRequest.RequestMethod.GET);
        HashMap<String, Object> params = new HashMap<>();
        params.put("uuid", DeviceInfoUtil.getUUID());
        params.put("jpushId", jpushId);
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
                    SharedPreManager.saveJPushId(jpushId);
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
