package com.news.sdk.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/18.
 */
public class UploadLogDataEntity implements Serializable{
    private Long n;
    private int c;
    private int t;
    private int s;
    private int f;
    private int lt;
    private int lc;
    private String pe;
    private String v;

    public Long getN() {
        return n;
    }

    public void setN(Long n) {
        this.n = n;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getLt() {
        return lt;
    }

    public void setLt(int lt) {
        this.lt = lt;
    }

    public int getLc() {
        return lc;
    }

    public void setLc(int lc) {
        this.lc = lc;
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}
