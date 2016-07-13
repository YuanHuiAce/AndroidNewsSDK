package com.news.yazhidao.net.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.StringCallback;
import com.news.yazhidao.utils.manager.SharedPreManager;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by fengjigang on 15/8/5.
 * 挖掘新闻请求
 */
public class DigNewsRequest {

    /**用户ID*/
    public final static String KEY_UID = "uid";
    /**专辑ID(用户当前搜索新闻所在的专辑ID)*/
    public final static String KEY_ALID = "album";
    /**用户提供的新闻线索(文字，比如文章标题)*/
    public final static String KEY_KEY = "key";
    /**用户提供的新闻线索(URL，比如文章url)*/
    public final static String KEY_URL = "url";

    /**
     * 开始挖掘新闻
     * @param pContext 上下文
     * @param pAlbumId 专辑id
     * @param pTitle 要挖掘的文本
     * @param pUrl 要挖掘的url
     * @param pLisener 挖掘回调接口
     */
    public static void digNews(Context pContext, String pAlbumId, String pTitle, String pUrl, StringCallback pLisener){
        NetworkRequest request = new NetworkRequest(HttpConstant.URL_DIGGER_ALBUM, NetworkRequest.RequestMethod.GET);
        HashMap<String,Object> params = new HashMap<>();
        //此处默认用户是已经登录过的
        User user = SharedPreManager.getUser(pContext);
        if(user == null){
            return;
        }
        params.put(KEY_UID,user.getUserId());
        params.put(KEY_ALID, pAlbumId);
        if(!TextUtils.isEmpty(pTitle)){
            params.put(KEY_KEY,pTitle);
        }
        if(!TextUtils.isEmpty(pUrl)){
            byte[] bytes = Base64.encode(pUrl.getBytes(), Base64.DEFAULT);
            String url = null;
            try {
                url = new String(bytes,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //去掉最后面的"="符号
            params.put(KEY_URL, url.replace("=",""));
        }
        request.getParams = params;
        request.setCallback(pLisener);
        request.execute();
    }
}
