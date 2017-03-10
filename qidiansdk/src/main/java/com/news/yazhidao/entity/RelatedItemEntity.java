package com.news.yazhidao.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/22.
 */
public class RelatedItemEntity implements Serializable,Comparable {
    /**
     * "url": "http://news.163.com/16/0520/08/BNGEG7ID00014Q4P.html",
     "title": "但愿雷洋事件不是一个小插曲",
     "from": "Baidu",
     "rank": 1,
     "pname": "网易新闻",
     "ptime": "2016-05-20 08:47:45",
     "img": "http://some.jpg",         - Option
     "abs": "据@平安北京19日发布..." - Option
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
    /**
     * 莫一年的第一天
     */
    private boolean yearFrist;



    public RelatedItemEntity() {
    }
    @Override
    public int compareTo(Object o) {
        RelatedItemEntity itemEntity = (RelatedItemEntity) o;
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        this.img= imgUrl;
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


}
