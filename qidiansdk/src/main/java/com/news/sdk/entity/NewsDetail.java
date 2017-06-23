package com.news.sdk.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fengjigang on 16/3/30.
 * 新闻详情页
 */
public class NewsDetail implements Serializable {

    /**
     * "nid": 6825,
     * "docid": "http://mp.weixin.qq.com/...",
     * "url": "http://mp.weixin.qq.com/s?...",
     * "title": "分享 | 驾照自学直考到底难不难？成都31人报名2人拿证，他们的经验是......",
     * "ptime": "2016-05-22 01:03:00",
     * "pname": "央视新闻",
     * "purl": "http://mp.weixin.qq.com/s?...",
     * "channel": 2,
     * "inum": 6,
     * "tags": ["驾照","驾校"],
     * "descr": "今年4月1日起，武汉、成都、南京、福州等...",
     * "content": [{"txt": "本文来源：荆楚网、华西都市报"},{"img": "http://bdp-pic.deeporiginalx.com/W0JAMjIzZmZhZGQ.jpg"},{"vid": "http://anyvideourl.com"},...],
     * "collect": 1,
     * "concern": 1,
     * "comment": 0
     */

    /** 新闻ID */
    private int nid;
    private int imgNum;
    /** 发布时间 */
    private String ptime;
    private String url;
    /**  用于获取评论的docid */
    private String docid;
    private String descr;
    private String pname;
    private String purl;
    private int commentSize;
    private String title;

    private int channel;
    //标签
    private ArrayList<String> tags;
    private ArrayList<HashMap<String, String>> content;

    /**  正文图片数量 */
    private int inum;
    /**  收藏数 */
    private int collect;
    /** 关心数 */
    private int concern;
    /** 评论数 */
    private int comment;
    /** 是(1)否(0)已收藏 */
    private int colflag;
    /** 是(1)否(0)已关心 */
    private int conflag;
    /** 是(1)否(0)已关心该新闻对应的发布源 */
    private int conpubflag;

    private String icon;
    /**
     * 播放视频地址
     */
    private String videourl;
    /**
     * 背景图片
     */
    private String thumbnail;
    /**
     * 播放次数
     */
    private int clicktimes;
    /**
     * 点赞数
     */
    private int commendup;
    /**
     * 踩数
     */
    private int commenddown;
    /**
     * 该用户对于新闻的态度
     */
    private int commendtype;


    public int getClicktimes() {
        return clicktimes;
    }

    public String getClicktimesStr() {
        if (clicktimes == 0) {
            return "";
        } else if (clicktimes / 10000 == 0) {
            return  clicktimes + "次播放";
        } else {
            return clicktimes / 10000 + "万次播放";
        }
    }

    public void setClicktimes(int clicktimes) {
        this.clicktimes = clicktimes;
    }

    public int getCommendup() {
        return commendup;
    }

    public void setCommendup(int commendup) {
        this.commendup = commendup;
    }

    public int getCommenddown() {
        return commenddown;
    }

    public void setCommenddown(int commenddown) {
        this.commenddown = commenddown;
    }

    public int getCommendtype() {
        return commendtype;
    }

    public void setCommendtype(int commendtype) {
        this.commendtype = commendtype;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getInum() {
        return inum;
    }

    public void setInum(int inum) {
        this.inum = inum;
    }

    public int getConcern() {
        return concern;
    }

    public void setConcern(int concern) {
        this.concern = concern;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getImgNum() {
        return imgNum;
    }

    public void setImgNum(int imgNum) {
        this.imgNum = imgNum;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public int getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(int commentSize) {
        this.commentSize = commentSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<HashMap<String, String>> getContent() {
        return content;
    }

    public void setContent(ArrayList<HashMap<String, String>> content) {
        this.content = content;
    }

    public int getColflag() {
        return colflag;
    }

    public void setColflag(int colflag) {
        this.colflag = colflag;
    }

    public int getConflag() {
        return conflag;
    }

    public void setConflag(int conflag) {
        this.conflag = conflag;
    }

    public int getConpubflag() {
        return conpubflag;
    }

    public void setConpubflag(int conpubflag) {
        this.conpubflag = conpubflag;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
