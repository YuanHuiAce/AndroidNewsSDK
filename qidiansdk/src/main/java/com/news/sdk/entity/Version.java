package com.news.sdk.entity;

import java.io.Serializable;

/**
 * "ctype": 1,
 * "ptype": 2,
 * "version": "1.1.2",
 * "version_code": 3,
 * "updateLog": "更新日志",
 * "downloadLink": "fdasfafasdfsdfsd",
 * "forceUpdate": false
 */

public class Version implements Serializable {
    private static final long serialVersionUID = 1L;
    //渠道类型
    private int ctype;
    //平台类型
    private int ptype;
    //版本名
    private String version;
    //版本号
    private int version_code;
    //更新日志
    private String updateLog="1、发现新版本，更新内容\n2、发现新版本,请更新内容，\n3、发现新版本，更新内容\n5、发现新版本，请更新内容";
    //下载连接
    private String downloadLink;
    //是否强制更新
    private boolean forceUpdate;

    private String md5;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Version() {

    }

    @Override
    public String toString() {
        return "Version{" +
                "ctype=" + ctype +
                ", ptype=" + ptype +
                ", version='" + version + '\'' +
                ", version_code=" + version_code +
                ", updateLog='" + updateLog + '\'' +
                ", downloadLink='" + downloadLink + '\'' +
                ", forceUpdate=" + forceUpdate +
                ", md5='" + md5 + '\'' +
                '}';
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getCtype() {
        return ctype;
    }

    public void setCtype(int ctype) {
        this.ctype = ctype;
    }

    public int getPtype() {
        return ptype;
    }

    public void setPtype(int ptype) {
        this.ptype = ptype;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
