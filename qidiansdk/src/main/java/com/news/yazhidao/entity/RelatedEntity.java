package com.news.yazhidao.entity;

import com.news.yazhidao.utils.Logger;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/22.
 */
public class RelatedEntity implements Serializable {
    private ArrayList<RelatedItemEntity> searchItems = new ArrayList<RelatedItemEntity>();
    private ArrayList<RelatedItemEntity> zhihu = new ArrayList<RelatedItemEntity>();
    private ArrayList<RelatedItemEntity> weibo = new ArrayList<RelatedItemEntity>();
    private ArrayList<RelatedItemEntity> comment = new ArrayList<RelatedItemEntity>();

    public RelatedEntity(ArrayList<RelatedItemEntity> searchItems, ArrayList<RelatedItemEntity> zhihu, ArrayList<RelatedItemEntity> weibo, ArrayList<RelatedItemEntity> comment) {
        this.searchItems = searchItems;
        this.zhihu = zhihu;
        this.weibo = weibo;
        this.comment = comment;

    }

    public RelatedEntity() {
    }

    @Override
    public String toString() {
        return "RelatedEntity{" +
                "searchItems=" + searchItems +
                ", zhihu=" + zhihu +
                ", weibo=" + weibo +
                ", comment=" + comment +
                '}';
    }
    public void toTimeString() {
        for(RelatedItemEntity s :searchItems){
            Logger.d("aaa","time:==="+s.toTimeString());
        }
    }


    public ArrayList<RelatedItemEntity> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(ArrayList<RelatedItemEntity> searchItems) {
        this.searchItems = searchItems;
    }

    public ArrayList<RelatedItemEntity> getZhihu() {
        return zhihu;
    }

    public void setZhihu(ArrayList<RelatedItemEntity> zhihu) {
        this.zhihu = zhihu;
    }

    public ArrayList<RelatedItemEntity> getWeibo() {
        return weibo;
    }

    public void setWeibo(ArrayList<RelatedItemEntity> weibo) {
        this.weibo = weibo;
    }

    public ArrayList<RelatedItemEntity> getComment() {
        return comment;
    }

    public void setComment(ArrayList<RelatedItemEntity> comment) {
        this.comment = comment;
    }
}
