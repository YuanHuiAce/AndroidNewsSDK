package com.news.yazhidao.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Berkeley on 5/8/17.
 */
@DatabaseTable(tableName = "tb_video_channel")
public class VideoChannel implements Serializable, Comparable<VideoChannel> {
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
     * 栏目是否上线,1表示上线
     */
    @DatabaseField
    private int state;
    /**
     * 渠道号
     */

    @DatabaseField
    private int channel;
    /**
     * 序号
     */
    @DatabaseField
    private int order_num;
    /**
     * 父类频道
     */
    @DatabaseField
    private int parent;
    /**
     * 栏目是否选中
     */
    @DatabaseField
    private boolean selected;

    public VideoChannel() {
    }


    public VideoChannel(int id, String cname, boolean selected, int order_num) {
        this.id = id;
        this.cname = cname;
        this.selected = selected;
        this.order_num = order_num;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getOrderId() {
        return order_num;
    }

    public void setOrderId(int order_num) {
        this.order_num = order_num;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    @Override
    public int compareTo(VideoChannel another) {
        int id = another.getOrderId();
        if (id < this.order_num)
            return -1;
        else if (id > this.order_num)
            return 1;
        else
            return 0;
    }
}
