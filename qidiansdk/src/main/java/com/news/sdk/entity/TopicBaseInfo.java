package com.news.sdk.entity;

import java.io.Serializable;

/**
 * Created by fengjigang on 16/3/30.
 * 新闻专题
 */
public class TopicBaseInfo implements Serializable {

    private int id;
    private String name;
    private String cover;
    private String description;
    private String create_time;
    private int class_count;
    private int news_count;
    private int online;
    private int top;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getClass_count() {
        return class_count;
    }

    public void setClass_count(int class_count) {
        this.class_count = class_count;
    }

    public int getNews_count() {
        return news_count;
    }

    public void setNews_count(int news_count) {
        this.news_count = news_count;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

}
