package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.news.yazhidao.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjigang on 15/11/24.
 */
public class SearchRequest<T> extends GsonRequest<T> {

    private String mKeyWord;
    private String mPageIndex;

    public SearchRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, clazz, url, successListener, listener);
    }

    public SearchRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }


    protected String checkJsonData(String data, NetworkResponse response) {
        try {
//            Logger.d("aaa","data = "+data );
//            JSONArray jsonArray = new JSONArray(data);
//            Logger.d("aaa", "jsonArray.getString(0 = " + jsonArray.getString(0));
            JSONObject jsonObject = new JSONObject(data);
            String code = jsonObject.optString("code", "");
            String message = jsonObject.optString("message", "");
            Logger.e("jigang","code = "+code + ",message=" + message);
            return jsonObject.optString("data","");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void  setKeyWordAndPageIndex(String pKeyWord, String pPageIndex){
        this.mKeyWord = pKeyWord;
        this.mPageIndex = pPageIndex;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        HashMap<String,String> params = new HashMap<>();
        params.put("keyword",this.mKeyWord);
        params.put("page", this.mPageIndex);
        return params;
    }
}
