package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fengjigang on 15/9/7.
 * 新闻详情页中图片墙POJO
 */
public class NewsDetailImageWall implements Serializable {
    public ArrayList<HashMap<String,String>> imgWall;

    public ArrayList<HashMap<String, String>> getImgWall() {
        return imgWall;
    }

    public void setImgWall(ArrayList<HashMap<String, String>> imgWall) {
        this.imgWall = imgWall;
    }
}
