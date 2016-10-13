package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/6.
 */
public class AdImpressionEntity implements Serializable{
    /** 当前广告位在 Falcon 广告平台的 ID */
    private String aid;
    /** 广告位的当前宽度 */
    private String width;
    /** 广告位的当前高度 */
    private String height;
    /** 当前广告位所在媒体环境的关键字。多个关键字请用半角或者全角逗号进行分割。如体育，足球。 */
    private String keywords;
    /** 分页时使用，当前页面的 index，index 从 0 开始计数 */
    private String page_index;
    /** 分页时使用，每页显示的记录条数 */
    private String page_size;

    public AdImpressionEntity() {
    }

    public AdImpressionEntity(String aid, String width, String height, String keywords, String page_index, String page_size) {
        this.aid = aid;
        this.width = width;
        this.height = height;
        this.keywords = keywords;
        this.page_index = page_index;
        this.page_size = page_size;
    }

    @Override
    public String toString() {
        return "AdDeviceEntity{" +
                "aid='" + aid + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", keywords='" + keywords + '\'' +
                ", page_index='" + page_index + '\'' +
                ", page_size='" + page_size + '\'' +
                '}';
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getPage_index() {
        return page_index;
    }

    public void setPage_index(String page_index) {
        this.page_index = page_index;
    }

    public String getPage_size() {
        return page_size;
    }

    public void setPage_size(String page_size) {
        this.page_size = page_size;
    }






}
