package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by fengjigang on 15/9/9.
 * 详情页中新闻词条栏目的POJO
 */
public class NewsDetailEntry implements Serializable {
    public enum EntyType{
        BAIDUBAIKE,DOUBAN
    }
    private String title;
    private EntyType type;
    private String url;
    public NewsDetailEntry(){}

    public NewsDetailEntry(String title, EntyType type, String url) {
        this.title = title;
        this.type = type;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EntyType getType() {
        return type;
    }

    public void setType(EntyType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
