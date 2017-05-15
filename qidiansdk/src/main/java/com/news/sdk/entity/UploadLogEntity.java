package com.news.sdk.entity;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/18.
 */
public class UploadLogEntity implements Serializable {
    private String uid;
    private String cou;
    private String pro;
    private String city;
    private String dis;
    private int clas;
    private ArrayList<UploadLogDataEntity> data = new ArrayList<UploadLogDataEntity>();

    public UploadLogEntity(String uid, String cou, String pro, String city, String dis, int clas, ArrayList<UploadLogDataEntity> data) {
        this.uid = uid;
        this.cou = cou;
        this.pro = pro;
        this.city = city;
        this.dis = dis;
        this.clas = clas;
        this.data = data;
    }

    public UploadLogEntity() {
    }

    public String getJson(){
        Gson gson = new Gson();
        return gson.toJson(data);
    }
    @Override
    public String toString() {
        return "UploadLogEntity{" +
                "uid='" + uid + '\'' +
                ", cou='" + cou + '\'' +
                ", pro='" + pro + '\'' +
                ", city='" + city + '\'' +
                ", dis='" + dis + '\'' +
                ", clas='" + clas + '\'' +
                ", data=" + data +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCou() {
        return cou;
    }

    public void setCou(String cou) {
        this.cou = cou;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public int getClas() {
        return clas;
    }

    public void setClas(int clas) {
        this.clas = clas;
    }

    public ArrayList<UploadLogDataEntity> getData() {
        return data;
    }

    public void setData(ArrayList<UploadLogDataEntity> data) {
        this.data = data;
    }
}
