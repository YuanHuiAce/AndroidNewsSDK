package com.news.sdk.entity;

import com.news.sdk.utils.GsonUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 此用户游客用户和第三方授权用户
 * Created by fengjigang on 15/5/13.
 */
public class User implements Serializable {
    private int muid;// 游客用户合并三方用户时提供对应的游客用户ID
    private String msuid;// 三方用户相互合并时提供对应的三方用户ID
    private String uuid;
    private String utype = "2";//本地注册用户	1 ;游客用户	2;微博三方用户	3;微信三方用户	4
    private String userId;
    private String province;
    private String city;
    private String district;
    //token的有效时常（单位：秒）
    private long expiresIn;
    //token的有效截止时间
    private String expiresTime;
    private String token;
    private int userGender;
    private String userIcon;
    private String userName;
    private String platformType;
    private long firstLoginTime;
    private long lastLoginTime;
    private String authorToken;//访问接口时的token
    private boolean isVisitor = true;//是否是游客用户
    private String password;
    private ArrayList<String> channel;
    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", userId='" + userId + '\'' +
                ", expiresIn=" + expiresIn +
                ", expiresTime=" + expiresTime +
                ", token='" + token + '\'' +
                ", userGender='" + userGender + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", userName='" + userName + '\'' +
                ", platformType='" + platformType + '\'' +
                ", firstLoginTime=" + firstLoginTime +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId+"";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(String expiresTime) {
        this.expiresTime = expiresTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserGender() {
        return userGender;
    }

    public void setUserGender(int userGender) {
        this.userGender = userGender;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public long getFirstLoginTime() {
        return firstLoginTime;
    }

    public void setFirstLoginTime(long firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public ArrayList<String> getChannel() {
        return channel;
    }

    public void setChannel(ArrayList<String> channel) {
        this.channel = channel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public boolean isVisitor() {
        return "2".equals(utype);
    }

    public void setVisitor(boolean visitor) {
        isVisitor = visitor;
    }

    public String getMsuid() {
        return msuid;
    }

    public void setMsuid(String msuid) {
        this.msuid = msuid;
    }

    public int getMuid() {
        return muid;
    }

    public void setMuid(int muid) {
        this.muid = muid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public String getAuthorToken() {
        return authorToken;
    }

    public void setAuthorToken(String authorToken) {
        this.authorToken = authorToken;
    }

    /**
     * 把json 反序列化为 User 对象
     * @param userStr
     * @return
     */
    public static User parseUser(String userStr) {
        return GsonUtil.deSerializedByType(userStr, User.class);
    }

    /**
     * 把User 对象序列化 json
     * @return
     */
    public String toJsonString(){
        return GsonUtil.serialized(this);
    }
}
