package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.entity.Element;
import com.news.yazhidao.utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/9.
 */
public class FetchElementaryRequestPost<T> extends JsonRequest<T> {

    private HashMap mHeader = new HashMap();

    public FetchElementaryRequestPost(String url, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, requestBody, listener, errorListener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            ArrayList<Element> result = GsonUtil.deSerializedByType(jsonString, new TypeToken<ArrayList<Element>>() {
            }.getType());
            return (Response<T>) Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeader;
    }

    public void setRequestHeaders(HashMap headers) {
        this.mHeader = headers;
    }
}
