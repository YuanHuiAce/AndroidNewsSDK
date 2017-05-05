package com.news.sdk.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/6.
 */
public class AuthorizedUser implements Serializable {
    private int muid;
    private String msuid;
    private int utype;
    private int platform;
    private String suid;
    private String stoken;
    private String sexpires;
    private String uname;
    private int gender;
    private String avatar;
    private ArrayList<String> averse;
    private ArrayList<String> prefer;
    private String province;
    private String city;
    private String district;

    public int getMuid() {
        return muid;
    }

    public void setMuid(int muid) {
        this.muid = muid;
    }

    public String getMsuid() {
        return msuid;
    }

    public void setMsuid(String msuid) {
        this.msuid = msuid;
    }

    public int getUtype() {
        return utype;
    }

    public void setUtype(int utype) {
        this.utype = utype;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }

    public String getStoken() {
        return stoken;
    }

    public void setStoken(String stoken) {
        this.stoken = stoken;
    }

    public String getSexpires() {
        return sexpires;
    }

    public void setSexpires(String sexpires) {
        this.sexpires = sexpires;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public ArrayList<String> getAverse() {
        return averse;
    }

    public void setAverse(ArrayList<String> averse) {
        this.averse = averse;
    }

    public ArrayList<String> getPrefer() {
        return prefer;
    }

    public void setPrefer(ArrayList<String> prefer) {
        this.prefer = prefer;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
