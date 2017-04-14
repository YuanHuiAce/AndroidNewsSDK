package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/18.
 */
public class UserLogBasicInfoEntity implements Serializable {
    private Long uid;
    private String deviceid;
    private String ptype;
    private String ctype;
    private String version_text;
    private Long ctime;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getVersion_text() {
        return version_text;
    }

    public void setVersion_text(String version_text) {
        this.version_text = version_text;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }
}
