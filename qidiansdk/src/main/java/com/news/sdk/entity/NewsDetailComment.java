package com.news.sdk.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by fengjigang on 16/3/31.
 * 新闻评论
 */
@DatabaseTable(tableName = "tb_news_detail_comment_item")
public class NewsDetailComment implements Serializable{

    /**
     *old
     * docid : /group/6267906567157874945/
     * comment_id : 6222592828
     * id : 4666198
     * profile : http://p1.pstatp.com/thumb/398/2537495648
     * love : 212
     * content : 凭啥老百姓吃草根？他天天大鱼大肉！这种人不用再为他卖命，真的不值。
     * create_time : 2016-03-31 12:39:00
     * nickname : 你猜23803670
     *
     * new
     * "id": 2,
     "content": "66666",
     "commend": 10, **
     "ctime": "2016-05-24 19:22:11",
     "uid": 4,
     "uname": "zhange",
     "docid": "http://toutiao.com/group/2223/comments/111",
     "upflag": 1 - 用户是否为该条评论点赞过，0、1 对应 未点、已点
     *
     */


    /**  在创建评论时：如果你当前用户有头像，就提供该值，没有则不提供
     评论列表响应时：如果创建该评论时提供了头像就会有该值，否则没有 */
    private String avatar;

    private String uname;

    private String uid;
    public NewsDetailComment(){}
    @DatabaseField(columnName="docid")
    private String docid;
    @DatabaseField(columnName="comment_id")
    private String comment_id;
    @DatabaseField(id = true)
    private String id;
    /**  点赞数 */
    @DatabaseField(columnName="commend")
    private int commend;
    @DatabaseField(columnName="content")
    private String content;
    @DatabaseField(columnName="ctime")
    private String ctime;
    @DatabaseField(columnName="isPraise")
    private boolean isPraise;
    @DatabaseField(columnName = "original")
    private String original;
    @DatabaseField(columnName = "ntitle")
    private String ntitle;
    private User user;
    /** 用户是否为该条评论点赞过，0、1 对应 未点、已点 */
    private int upflag;
    @DatabaseField(columnName = "rtype")
    private int rtype;
    @DatabaseField(columnName="nid")
    private int nid;

    public int getUpflag() {
        return upflag;
    }

    public void setUpflag(int upflag) {
        this.upflag = upflag;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NewsFeed getNewsFeed() {
        return newsFeed;
    }

    public void setNewsFeed(NewsFeed newsFeed) {
        this.newsFeed = newsFeed;
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private NewsFeed newsFeed;

    public NewsDetailComment(String comment_id, String content, String ctime, String docid, String id, int commend, String uname, String avatar, String uid) {
        this.comment_id = comment_id;
        this.content = content;
        this.ctime = ctime;
        this.docid = docid;
        this.id = id;
        this.commend = commend;
        this.uname = uname;
        this.avatar = avatar;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "NewsDetailComment{" +
                "avatar='" + avatar + '\'' +
                ", uname='" + uname + '\'' +
                ", uid='" + uid + '\'' +
                ", docid='" + docid + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", id='" + id + '\'' +
                ", commend=" + commend +
                ", content='" + content + '\'' +
                ", ctime='" + ctime + '\'' +
                ", isPraise=" + isPraise +
                ", original='" + original + '\'' +
                ", user=" + user +
                ", upflag=" + upflag +
                ", newsFeed=" + newsFeed +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
    }
    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getCommend() {
        return commend;
    }

    public void setCommend(int commend) {
        this.commend = commend;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getNtitle() {
        return ntitle;
    }

    public void setNtitle(String ntitle) {
        this.ntitle = ntitle;
    }

    public int getRtype() {
        return rtype;
    }

    public void setRtype(int rtype) {
        this.rtype = rtype;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }
}
