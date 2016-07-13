package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/18.
 */
public class UploadLogDataEntity implements Serializable{
    private String n;
    private String c;
    private String t;
    private String s;
    private String f;

    public UploadLogDataEntity(String n, String c, String t, String s, String sltime, String f) {
        this.n = n;
        this.c = c;
        this.t = t;
        this.s = s;
        this.f = f;
    }

    public UploadLogDataEntity() {
    }



    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }


    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }
}
