package com.news.sdk.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 */
public class AdEntity implements Serializable{
    /** 当前版本号为 1.0 */
    private String version = "1.0";
    /** 取当前服务器的时间戳，精确到秒 */
    private String ts;
    /** 自定义数据，在广告 Response 时一同携带返回 */
    private String extend_data;
    /** 曝光对象，一次 request 可以包含多个 impression */
    private List<AdImpressionEntity> impression = new ArrayList<AdImpressionEntity>();
    /** 设备对象 */
    private AdDeviceEntity device;

    public AdEntity() {
    }

    public AdEntity(String version, String ts, String extend_data, List<AdImpressionEntity> impression, AdDeviceEntity device) {
        this.version = version;
        this.ts = ts;
        this.extend_data = extend_data;
        this.impression = impression;
        this.device = device;
    }

    @Override
    public String toString() {
        return "AdEntity{" +
                "version='" + version + '\'' +
                ", ts='" + ts + '\'' +
                ", extend_data='" + extend_data + '\'' +
                ", impression=" + impression +
                ", device=" + device +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getExtend_data() {
        return extend_data;
    }

    public void setExtend_data(String extend_data) {
        this.extend_data = extend_data;
    }

    public List<AdImpressionEntity> getImpression() {
        return impression;
    }

    public void setImpression(List<AdImpressionEntity> impression) {
        this.impression = impression;
    }

    public AdDeviceEntity getDevice() {
        return device;
    }

    public void setDevice(AdDeviceEntity device) {
        this.device = device;
    }
}
