package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by fengjigang on 15/11/24.
 */
public class GsonRequest<T> extends Request<T> {

    private Type mReflectType;
    private Class mClazz;
    private Response.Listener mSuccessListener;
    private Gson mGson;
    private HashMap<String,String> mParams;

    public HashMap<String, String> getmParams() {
        return mParams;
    }

    public void setmParams(HashMap<String, String> mParams) {
        this.mParams = mParams;
    }


    private GsonRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    /**
     * 使用volley进行网络请求
     */
    public GsonRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener){
        this(method, url, listener);
        this.mReflectType = reflectType;
        this.mSuccessListener = successListener;
        this.mGson = new Gson();
    }

    /**
     * 使用volley进行网络请求
     */
    public GsonRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener){
        this(method, url, listener);
        this.mClazz = clazz;
        this.mSuccessListener = successListener;
        this.mGson = new Gson();
    }

    protected String checkJsonData(String data, NetworkResponse response){
        return data;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String data = new String(response.data, "utf-8");
            JSONObject jsonRes = new JSONObject(data);
            String code = jsonRes.optString("code");
            if ("2000".equals(code)) {
                data = checkJsonData(data, response);
                T o = mGson.fromJson(data, mReflectType == null ? mClazz : mReflectType);
                return Response.success(o, HttpHeaderParser.parseCacheHeaders(response));
            } else if ("2002".equals(code)){
                return Response.error(new VolleyError("服务端未找到数据 2002"));
            }else {
                return Response.error(new VolleyError("获取数据异常!--" + code));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (mSuccessListener != null){
            mSuccessListener.onResponse(response);
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return getmParams();
    }

    private int getShort(byte[] data) {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }

    public static String uncompress(byte[] str) throws IOException {
        if (str == null || str.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(str);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
        return out.toString();
    }
}
