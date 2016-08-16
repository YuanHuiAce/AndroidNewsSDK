package com.news.yazhidao.entity;

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
    private String concern;

    public AttentionListEntity(String id, String ctime, String name, String icon, String descr, String concern) {
        this.id = id;
        this.ctime = ctime;
        this.name = name;
        this.icon = icon;
        this.descr = descr;
        this.concern = concern;
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

    public String getConcern() {
        return concern;
    }

    public void setConcern(String concern) {
        this.concern = concern;
    }
}
