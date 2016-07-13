package com.news.yazhidao.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.news.yazhidao.common.HttpConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * 注册游客用户
 * Created by fengjigang on 16/5/25.
 */
public class MergeThirdUserLoginRequest extends JsonObjectRequest {
    public MergeThirdUserLoginRequest(String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, HttpConstant.URL_MERGE_USER_LOGIN,requestBody, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject data = new JSONObject();
            if ("2000".equals(jsonObject.optString("code"))){
                data = jsonObject.getJSONObject("data");
                data.put("Authorization",response.headers.get("Authorization"));
                return Response.success(data,
                        HttpHeaderParser.parseCacheHeaders(response));
            }else {
                return Response.error(new VolleyError("服务器异常!"));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

}
