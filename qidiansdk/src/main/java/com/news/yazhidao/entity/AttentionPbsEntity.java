package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/22.
 */
public class AttentionPbsEntity implements Serializable{
    private AttentionListEntity info = new AttentionListEntity();
    private List<NewsFeed> news = new ArrayList<NewsFeed>();

    public AttentionPbsEntity() {
    }

    public AttentionPbsEntity(AttentionListEntity info, List<NewsFeed> news) {
        this.info = info;
        this.news = news;
    }

    @Override
    public String toString() {
        return "AttentionPbsEntity{" +
                "info=" + info +
                ", news=" + news +
                '}';
    }

    public AttentionListEntity getInfo() {
        return info;
    }

    public void setInfo(AttentionListEntity info) {
        this.info = info;
    }

    public List<NewsFeed> getNews() {
        return news;
    }

    public void setNews(List<NewsFeed> news) {
        this.news = news;
    }
}
