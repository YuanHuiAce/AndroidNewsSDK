package com.news.sdk.entity;

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
    private int id;
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
    @DatabaseField
    private int order_num;
    @DatabaseField
    private int version_code;
    @DatabaseField
    private int parent;
    @DatabaseField
    private int channel;

    /**
     * 栏目是否选中
     */
    @DatabaseField
    private int selected;
    /**
     * 栏目是否上线,1表示上线
     */
    @DatabaseField
    private String state;

    public ChannelItem() {
    }

    public ChannelItem(int id, String cname, int orderId, int selected) {
        this.id = id;
        this.cname = cname;
        this.orderId = orderId;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getAdrImg() {
        return adrImg;
    }

    public String getDes() {
        return des;
    }

    public String getOnline() {
        return online;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public int getOrder_num() {
        return order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int isSelected() {
        return selected;
    }

    public String toString() {
        return "ChannelItem [id=" + this.id + ",orderId = " + this.orderId + " cname=" + this.cname
                + ", selected=" + this.selected + ", online=" + this.online + "]";
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