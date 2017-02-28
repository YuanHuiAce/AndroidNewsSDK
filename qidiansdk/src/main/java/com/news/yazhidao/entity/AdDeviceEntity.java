package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/6.
 */
public class AdDeviceEntity implements Serializable {
    /** 用户终端的 IMEI，15 位数字，md5 加密 */
    private String imei;
    private String imeiori;
    /** 用户终端的eth0接口的MAC地址（大写去除冒号分隔符），md5 加密 */
    private String mac;
    private String macori;
    /** 用户终端的 eth0 接口的 MAC 地址（大写且保留冒号分隔符），md5 加密 */
    private String mac1;
    /** IOS IDFA 适用于 IOS6 及以上，md5 加密 */
    private String idfa;
    /** Android Advertising ID，md5 加密 */
    private String aaid;
    /** Android ID，仅适用于 Android 设备，md5 加密 */
    private String anid;
    private String anidori;
    /** IOS UDID，md5 加密 */
    private String udid;
    /** Windows Phone 用户终端的 DUID，md5 加密 */
    private String duid;
    /** 设备品牌 */
    private String brand;
    /** 设备型号 */
    private String platform;
    /** 操作系统 0：未知 1：android 2：ios 3：windows */
    private String os;
    /** 设备操作系统版本号 */
    private String os_version;
    /** 屏幕分辨率，例如：1024*768。建议填写，可以帮助获取最优尺寸创意。 */
    private String device_size;
    /** 网络环境 0：未知 1：wifi 2：2G 3：3G 4：4G */
    private String network;
    /** 运营商 0：未知 1：中国移动 2：中国联通 3：中国电信 */
    private String  operator;
    /** 设备所在地理位置的经度，例如：31.2415 */
    private String longitude;
    /** 设备所在地理位置的纬度，例如：31.2415 */
    private String latitude;
    /** 横竖屏。建议填写，可以帮助获取最优尺寸创意。 0：未知 1：竖屏 2：横屏 */
    private String screen_orientation;
    /** 设备所在的 IP 地址 */
    private String ip;

    public AdDeviceEntity() {
    }

    public AdDeviceEntity(String imei, String mac, String mac1, String idfa, String aaid, String anid, String udid, String duid, String brand, String platform, String os, String os_version, String device_size, String network, String operator, String longitude, String latitude, String screen_orientation, String ip) {
        this.imei = imei;
        this.mac = mac;
        this.mac1 = mac1;
        this.idfa = idfa;
        this.aaid = aaid;
        this.anid = anid;
        this.udid = udid;
        this.duid = duid;
        this.brand = brand;
        this.platform = platform;
        this.os = os;
        this.os_version = os_version;
        this.device_size = device_size;
        this.network = network;
        this.operator = operator;
        this.longitude = longitude;
        this.latitude = latitude;
        this.screen_orientation = screen_orientation;
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "AdImpressionEntity{" +
                "imei='" + imei + '\'' +
                ", mac='" + mac + '\'' +
                ", mac1='" + mac1 + '\'' +
                ", idfa='" + idfa + '\'' +
                ", aaid='" + aaid + '\'' +
                ", anid='" + anid + '\'' +
                ", udid='" + udid + '\'' +
                ", duid='" + duid + '\'' +
                ", brand='" + brand + '\'' +
                ", platform='" + platform + '\'' +
                ", os='" + os + '\'' +
                ", os_version='" + os_version + '\'' +
                ", device_size='" + device_size + '\'' +
                ", network='" + network + '\'' +
                ", operator='" + operator + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", screen_orientation='" + screen_orientation + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMac1() {
        return mac1;
    }

    public void setMac1(String mac1) {
        this.mac1 = mac1;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getAaid() {
        return aaid;
    }

    public void setAaid(String aaid) {
        this.aaid = aaid;
    }

    public String getAnid() {
        return anid;
    }

    public void setAnid(String anid) {
        this.anid = anid;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getDuid() {
        return duid;
    }

    public void setDuid(String duid) {
        this.duid = duid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public String getDevice_size() {
        return device_size;
    }

    public void setDevice_size(String device_size) {
        this.device_size = device_size;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getScreen_orientation() {
        return screen_orientation;
    }

    public void setScreen_orientation(String screen_orientation) {
        this.screen_orientation = screen_orientation;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getImeiori() {
        return imeiori;
    }

    public void setImeiori(String imeiori) {
        this.imeiori = imeiori;
    }

    public String getMacori() {
        return macori;
    }

    public void setMacori(String macori) {
        this.macori = macori;
    }

    public String getAnidori() {
        return anidori;
    }

    public void setAnidori(String anidori) {
        this.anidori = anidori;
    }
}
