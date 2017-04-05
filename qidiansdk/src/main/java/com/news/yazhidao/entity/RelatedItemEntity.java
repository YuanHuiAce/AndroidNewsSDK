package com.news.yazhidao.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/22.
 */
public class RelatedItemEntity implements Serializable, Comparable {

    public static final int NO_PIC = 0;
    public static final int ONE_AND_TWO_PIC = 1;
    public static final int VIDEO_SMALL = 2;
    public static final int EMPTY = 3;
    /**
     * "url": "http://news.163.com/16/0520/08/BNGEG7ID00014Q4P.html",
     * "title": "但愿雷洋事件不是一个小插曲",
     * "from": "Baidu",
     * "rank": 1,
     * "pname": "网易新闻",
     * "ptime": "2016-05-20 08:47:45",
     * "img": "http://some.jpg",         - Option
     * "abs": "据@平安北京19日发布..." - Option
     */
    private String url;
    private int rank;
    private String pname;
    private String from;
    private String img;
    private String title;
    private String abs;
    private String ptime;
    private int nid;
    private int duration;
    private String purl;
    private int style;
    /**
     * 莫一年的第一天
     */
    private boolean yearFrist;
    private boolean isRead;
    /**
     * 0普通新闻(不用显示标识)、1热点、2推送、3广告
     */
    private int rtype;
    private int logtype;
    private int logchid;
    private boolean isUpload;
    /**
     * 印象展示
     */
    private ArrayList<String> adimpression;

    private AdDetailEntity adresponse;

    public AdDetailEntity getAdDetailEntity() {
        return adresponse;
    }

    public void setAdDetailEntity(AdDetailEntity adDetailEntity) {
        this.adresponse = adDetailEntity;
    }

    public RelatedItemEntity() {
    }

    @Override
    public int compareTo(Object o) {
        RelatedItemEntity itemEntity = (RelatedItemEntity) o;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(itemEntity.getPtime());
            date2 = format.parse(this.ptime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int flag = (date1.getTime() + "").compareTo((date2.getTime() + ""));


        return flag;
    }


    public String toTimeString() {
        return "RelatedItemEntity{" +
                "ptime='" + ptime + '\'' +
                '}';
    }


    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean getYearFrist() {
        return yearFrist;
    }

    public void setYearFrist(boolean yearFrist) {
        this.yearFrist = yearFrist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }


    public String getImgUrl() {
        return img;
    }

    public void setImgUrl(String imgUrl) {
        this.img = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    public String getPtime() {
        return ptime;
    }

    public void setptime(String ptime) {
        this.ptime = ptime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getRtype() {
        return rtype;
    }

    public void setRtype(int rtype) {
        this.rtype = rtype;
    }

    public int getLogtype() {
        return logtype;
    }

    public void setLogtype(int logtype) {
        this.logtype = logtype;
    }

    public int getLogchid() {
        return logchid;
    }

    public void setLogchid(int logchid) {
        this.logchid = logchid;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public ArrayList<String> getAdimpression() {
        return adimpression;
    }

    public void setAdimpression(ArrayList<String> adimpression) {
        this.adimpression = adimpression;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }
}
