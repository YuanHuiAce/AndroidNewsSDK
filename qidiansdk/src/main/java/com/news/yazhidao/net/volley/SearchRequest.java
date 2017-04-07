package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.entity.AttentionListEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.GsonUtil;
import com.news.yazhidao.utils.TextUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String data = new String(response.data, "utf-8");
            JSONObject jsonRes = new JSONObject(data);
            String code = jsonRes.optString("code");
            if ("2000".equals(code)) {
                JSONObject content = jsonRes.optJSONObject("data");
                JSONArray news = content.optJSONArray("news");
                JSONArray publisher = content.optJSONArray("publisher");
                ArrayList<NewsFeed> newsFeeds = GsonUtil.deSerializedByType(news.toString(), new TypeToken<ArrayList<NewsFeed>>() {
                }.getType());
                if (publisher!= null &&publisher.length() != 0) {
                    NewsFeed newsFeed = new NewsFeed();
                    newsFeed.setStyle(4);
                    ArrayList<AttentionListEntity> subscribeSources = GsonUtil.deSerializedByType(publisher.toString(), new TypeToken<ArrayList<AttentionListEntity>>() {
                    }.getType());
                    newsFeed.setAttentionListEntities(subscribeSources);
                    if (!TextUtil.isListEmpty(newsFeeds) && newsFeeds.size() > 2) {
                        newsFeeds.add(2, newsFeed);
                    } else {
                        newsFeeds.add(newsFeed);
                    }
                }
                return (Response<T>) Response.success(newsFeeds, HttpHeaderParser.parseCacheHeaders(response));
            } else if ("2002".equals(code)) {
                return Response.error(new VolleyError("服务端未找到数据 2002"));
            } else {
                return Response.error(new VolleyError("获取数据异常!--" + code));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));

        }
    }

    protected String checkJsonData(String data, NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String code = jsonObject.optString("code", "");
            if ("2000".equals(code)) {
                JSONObject content = jsonObject.getJSONObject("data");
                JSONArray news = content.getJSONArray("news");
                JSONArray publisher = content.getJSONArray("publisher");
                if (publisher.length() != 0) {
                    NewsFeed newsFeed = new NewsFeed();
                    newsFeed.setStyle(4);
                    ArrayList<AttentionListEntity> subscribeSources = GsonUtil.deSerializedByType(publisher.toString(), new TypeToken<ArrayList<AttentionListEntity>>() {
                    }.getType());
                    newsFeed.setAttentionListEntities(subscribeSources);
                    if (news.length() > 2) {
                        news.put(2, new JSONObject(GsonUtil.serialized(newsFeed)));
                    } else {
                        news.put(new JSONObject(GsonUtil.serialized(newsFeed)));
                    }

                }
                return news.toString();
            } else {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setKeyWordAndPageIndex(String pKeyWord, String pPageIndex) {
        this.mKeyWord = pKeyWord;
        this.mPageIndex = pPageIndex;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<>();
        params.put("keywords", this.mKeyWord);
        params.put("p", this.mPageIndex);
        return params;
    }
}
