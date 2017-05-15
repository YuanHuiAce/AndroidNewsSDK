package com.news.sdk.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/19.
 */
public class AttentionListEntity implements Serializable{
    private String id;
    private String ctime;
    private String name;
    private String icon;
    private String descr;
    private int concern;
    private int flag;

    public AttentionListEntity(int concern, String ctime, String descr, int flag, String icon, String id, String name) {
        this.concern = concern;
        this.ctime = ctime;
        this.descr = descr;
        this.flag = flag;
        this.icon = icon;
        this.id = id;
        this.name = name;
    }

    public AttentionListEntity() {
    }

    @Override
    public String toString() {
        return "AttentionListEntity{" +
                "id='" + id + '\'' +
                ", ctime='" + ctime + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", descr='" + descr + '\'' +
                ", concern='" + concern + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getConcern() {
        return concern;
    }

    public void setConcern(int concern) {
        this.concern = concern;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
