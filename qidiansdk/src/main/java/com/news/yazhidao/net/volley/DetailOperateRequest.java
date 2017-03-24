package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.news.yazhidao.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/27.
 */
public class DetailOperateRequest extends JsonObjectRequest {
    private HashMap mParams, mHeader;

    public DetailOperateRequest(int method, String requestUrl, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, requestUrl,requestBody, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Logger.e("aaa", "返回的数据=====" + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject data = new JSONObject();
//            Logger.e("aaa", "返回的数据=====" + jsonObject.optString("code"));
            String code = jsonObject.optString("code");
            if ("2000".equals(code)){
//                data = jsonObject.getJSONObject("data");
                return Response.success(jsonObject,
                        HttpHeaderParser.parseCacheHeaders(response));
            }else {
                return Response.error(new VolleyError(code+"服务器异常!"));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

//    @Override
//    public String getBodyContentType() {
//        return String.format("application/json", "utf-8");
//
//    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeader;
    }
    public void setRequestHeader(HashMap header) {
        this.mHeader = header;
    }


//    @Override
//    protected Map<String, String> getParams() throws AuthFailureError {
//        return mParams;
//    }
//    public void setRequestParams(HashMap params) {
//        this.mParams = params;
//    }

}
