package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/8.
 */
public class ADLoadNewsFeedEntity implements Serializable {
    private long cid;
    private long tcr;
    private long uid;
    /**
     * 是(1)否(0)模拟实时发布时间(部分新闻的发布时间修改为5分钟以内)
     */
    private int tmk;
    /**
     * 这两个值 默认的类型是 long 因为不传，所以改成String
     */
    private String p;
    private String c;

    private String b;
    private int t;

    public ADLoadNewsFeedEntity() {
    }

    public ADLoadNewsFeedEntity(long cid, long tcr, long uid, int tmk, String p, String c, String b) {
        this.cid = cid;
        this.tcr = tcr;
        this.uid = uid;
        this.tmk = tmk;
        this.p = p;
        this.c = c;
        this.b = b;
    }

    @Override
    public String toString() {
        return "ADLoadNewsFeedEntity{" +
                "cid=" + cid +
                ", tcr=" + tcr +
                ", uid=" + uid +
                ", tmk=" + tmk +
                ", p=" + p +
                ", c=" + c +
                ", b='" + b + '\'' +
                '}';
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public long getTcr() {
        return tcr;
    }

    public void setTcr(long tcr) {
        this.tcr = tcr;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getTmk() {
        return tmk;
    }

    public void setTmk(int tmk) {
        this.tmk = tmk;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}
