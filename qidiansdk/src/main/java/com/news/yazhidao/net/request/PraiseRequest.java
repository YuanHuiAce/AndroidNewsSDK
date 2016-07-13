package com.news.yazhidao.net.request;

import android.content.Context;

import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.listener.PraiseListener;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.StringCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Berkeley on 6/19/15.
 */
public class PraiseRequest {

    /**
     * 用于用户点赞
     * @param mContext
     * @param userId 用户id
     * @param platformType 平台类型
     * @param uuid 设备id
     * @param sourceUrl 新闻源的url
     * @param commentId 评论id
     *@param listener 上传结果回调  @see #TEXT_PARAGRAPH
     */
    public static void Praise(Context mContext, String userId, String platformType, String uuid, String sourceUrl, String commentId, final PraiseListener listener){
        final NetworkRequest request=new NetworkRequest(HttpConstant.URL_PRAISE, NetworkRequest.RequestMethod.POST);
        List<NameValuePair> pairs=new ArrayList<>();

        pairs.add(new BasicNameValuePair("userId",userId));
        pairs.add(new BasicNameValuePair("platformType",platformType));
        pairs.add(new BasicNameValuePair("uuid",uuid));
        pairs.add(new BasicNameValuePair("sourceUrl",sourceUrl));
        pairs.add(new BasicNameValuePair("commentId",commentId));
        pairs.add(new BasicNameValuePair("deviceType","android"));
        request.setParams(pairs);
        request.setCallback(new StringCallback() {
            @Override
            public void success(String result) {
                if(result != null && result.contains("200")){
                    if(listener!=null){
                        listener.success();
                    }
                }else{
                    if(listener!=null){
                        listener.failed();
                    }
                }
            }

            @Override
            public void failed(MyAppException exception) {
                if(listener!=null){
                    listener.failed();
                }
            }
        });
        request.execute();
    }


}
