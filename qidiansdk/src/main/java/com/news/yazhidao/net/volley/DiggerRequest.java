package com.news.yazhidao.net.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.NewsDetailForDigger;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjigang on 15/11/24.
 */
public class DiggerRequest<T> extends GsonRequest<T> {

    private AlbumSubItem mAlbumSubItem;
    private Context mContext;

    private DiggerRequest(int method, Class clazz, String url, Response.Listener GsonListener, Response.ErrorListener listener) {
        super(method, clazz, url, GsonListener, listener);
    }

    private DiggerRequest(int method, Type reflectType, String url, Response.Listener GsonListener, Response.ErrorListener listener) {
        super(method, reflectType, url, GsonListener, listener);
    }

    public DiggerRequest(Context mContext, AlbumSubItem albumItem, String url, Response.Listener successListener, Response.ErrorListener listener){
        this(Request.Method.POST, NewsDetailForDigger.class,url,successListener,listener);
        this.mContext = mContext;
        this.mAlbumSubItem = albumItem;
    }

    @Override
    protected String checkJsonData(String data, NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String status = jsonObject.optString("status", "");
            Logger.e("jigang","status = "+status);
            if ("0".equals(status)){
                return data;
            }else {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.checkJsonData(data,response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        User user = SharedPreManager.getUser(mContext);
        HashMap<String,String> params = new HashMap<>();
        params.put("uid",user.getUserId());
        params.put("album",mAlbumSubItem.getDiggerAlbum().getAlbum_id());
        params.put("key",mAlbumSubItem.getSearch_key());
        params.put("url", mAlbumSubItem.getSearch_url());
        return params;
    }
}
