package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Berkeley on 7/8/15.
 * SplashAty 中的新闻POJO
 */
public class StartUrl implements Serializable {

    private String title;
    private String imgUrl;
    private String updateTime;
    private ArrayList<String> news_url_list;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * 获取左右滑动的新闻数据
     * @return
     */
    public ArrayList<String> getNews_url_list() {
        return news_url_list;
    }

    public void setNews_url_list(ArrayList<String> news_url_list) {
        this.news_url_list = news_url_list;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;

    }
}
