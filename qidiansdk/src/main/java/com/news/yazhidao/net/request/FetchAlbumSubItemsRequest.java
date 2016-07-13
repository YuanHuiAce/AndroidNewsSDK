package com.news.yazhidao.net.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by fengjigang on 15/8/5.
 * 获取指定专辑下所有的items
 */
public class FetchAlbumSubItemsRequest {
    /**
     * 用户id
     */
    public final static String KEY_USER_ID = "user_id";
    /**
     * 专辑列表
     */
    public final static String KEY_ALBUM_ID = "album_id";

    /**是否需要增加专辑item总数  1 是添加,其他 为不添加*/
    public final static String KEY_IS_ADD = "is_add";

    public static void fetchAlbumSubItems(Context pContext, String pAlbumId, boolean isAdd, final JsonCallback<ArrayList<AlbumSubItem>> pListener) {
        final NetworkRequest request = new NetworkRequest(HttpConstant.URL_FETCH_ALBUM_SUBITEMS, NetworkRequest.RequestMethod.POST);
        User user = SharedPreManager.getUser(pContext);
        if(user == null){
            return;
        }
        ArrayList<NameValuePair> params = new ArrayList<>();
        Logger.e("jigang","----  userid "+user.getUserId());
        params.add(new BasicNameValuePair(KEY_USER_ID, user.getUserId()));
        Logger.e("jigang", "----  albumid " + pAlbumId);
        params.add(new BasicNameValuePair(KEY_ALBUM_ID, pAlbumId));
        params.add(new BasicNameValuePair(KEY_IS_ADD, isAdd ? "1" : "0"));
        request.setParams(params);
        if (pListener != null){
            pListener.setReturnType(new TypeToken<ArrayList<AlbumSubItem>>(){}.getType());
        }
        request.setCallback(pListener);
        request.execute();
    }
}
