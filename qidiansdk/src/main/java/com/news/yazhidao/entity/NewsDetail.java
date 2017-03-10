package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /**
     * 播放视频地址
     */
    private String videourl;
    /**
     * 背景图片
     */
    private String thumbnail;


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

    public List<String> getTags() {
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

}
