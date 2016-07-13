package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fengjigang on 15/9/7.
 * 新闻详情页中每一段新闻所对应的POJO
 */
public class NewsDetailContent implements Serializable {
    private String content;
    private int imgWidth;
    private int imgHeight;
    private int imgSize;
    private ArrayList<NewsDetailAdd.Point> comments;
    public NewsDetailContent(){}

    public NewsDetailContent(String content, ArrayList<NewsDetailAdd.Point> comments) {
        this.content = content;
        this.comments = comments;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<NewsDetailAdd.Point> getComments() {
        return comments;
    }

    public void setComments(ArrayList<NewsDetailAdd.Point> comments) {
        this.comments = comments;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public int getImgSize() {
        return imgSize;
    }

    public void setImgSize(int imgSize) {
        this.imgSize = imgSize;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }
}
