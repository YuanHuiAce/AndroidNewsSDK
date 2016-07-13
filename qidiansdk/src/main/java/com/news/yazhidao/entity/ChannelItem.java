package com.news.yazhidao.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by fengjigang on 15/8/12.
 * 新闻频道ITEM的对应可序化队列属性
 */

@DatabaseTable(tableName = "tb_news_channel")
public class ChannelItem implements Serializable, Comparable<ChannelItem> {

    private static final long serialVersionUID = -6465237897027410019L;
    /**
     * 栏目对应ID
     */
    @DatabaseField(id = true)
    private String id;
    /**
     * 栏目对应的频道名称
     */
    @DatabaseField
    private String cname;
    /**
     * 栏目对应描述
     */
    @DatabaseField
    private String des;
    /**
     * 栏目是否上线,1表示上线
     */
    @DatabaseField
    private String online;
    /**
     * 栏目对应的图片
     */
    @DatabaseField
    private String adrImg;
    /**
     * 栏目在整体中的排序顺序  rank
     */
    @DatabaseField
    private int orderId;
    /**
     * 栏目是否选中
     */
    @DatabaseField
    private boolean selected;

    public ChannelItem() {
    }

    public ChannelItem(String id, String cname, int orderId, boolean selected) {
        this.id = id;
        this.cname = cname;
        this.orderId = orderId;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAdrImg() {
        return adrImg;
    }

    public String getName() {
        return cname;
    }

    public String getDes() {
        return des;
    }

    public String getOnline() {
        return online;
    }

    public String toString() {
        return "ChannelItem [id=" + this.id + ",orderId = " + this.orderId + " cname=" + this.cname
                + ", selected=" + this.selected + ", online=" + this.online +"]";
    }

    @Override
    public int compareTo(ChannelItem another) {
        int anotherOrderId = another.getOrderId();
        if (this.orderId > anotherOrderId) {
            return 1;
        } else if (this.orderId < anotherOrderId) {
            return -1;
        } else {
            return 0;
        }
    }
}