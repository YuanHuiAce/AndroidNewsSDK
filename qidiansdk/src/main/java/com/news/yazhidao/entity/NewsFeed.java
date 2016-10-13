package com.news.yazhidao.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ariesymark on 2015/3/25.
 */

@DatabaseTable(tableName = "tb_news_feed")
public class NewsFeed implements Serializable {

    public static final int NO_PIC = 0;
    public static final int ONE_AND_TWO_PIC = 1;
    public static final int THREE_PIC = 2;
    public static final int TIME_LINE = 3;
    public static final int SERRCH_ITEM = 4;
    public static final int BIG_PIC = 4;
    public static final int EMPTY = 5;

    public static final String COLUMN_CHANNEL_ID = "channel";
    public static final String COLUMN_NEWS_ID = "nid";
    public static final String COLUMN_UPDATE_TIME = "ptime";
    /**
     * 新闻发布时间
     */
    @DatabaseField
    private String ptime;
    /**
     * 新闻来源地址,tips:此字段要当着获取详情的id使用
     */
    @DatabaseField(id = true)
    private int nid;

    @DatabaseField
    private String url;

    @DatabaseField
    private String docid;
    /**
     * 新闻评论
     */
    @DatabaseField
    private int comment;
    /**
     * 新闻来源名称
     */
    @DatabaseField
    private String pname;
    @DatabaseField
    private String purl;
    /**
     * 新闻样式;0:没有图片,1:一张图片,2:两张图片,3:3张图片,4:900timeLine
     */
    @DatabaseField
    private int style;
    @DatabaseField
    private String title;
    @DatabaseField
    private String province;
    @DatabaseField
    private String city;
    @DatabaseField
    private String district;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> imgs;
    @DatabaseField
    private int channel;
    @DatabaseField
    private int collect;
    @DatabaseField
    private int concern;
    /**
     * 这个参数是收藏用的单张图
     */
    private String imageUrl;
    /**
     * 用户是否看过
     */
    @DatabaseField(dataType = DataType.BOOLEAN)
    private boolean isRead;
    /**
     * 用户是否删除这条收藏数据
     */
    private boolean isFavorite = false;
    /**
     * 新闻备注
     */
    @DatabaseField
    private String descr;

    /**
     * 是(1)否(0)已收藏
     */
    private int colflag;
    /**
     * 是(1)否(0)已关心
     */
    private int conflag;
    /**
     * 是(1)否(0)已关心该新闻对应的发布源
     */
    private int conpubflag;
    /**
     * 0普通新闻(不用显示标识)、1热点、2推送、3广告
     */
    @DatabaseField
    private int rtype;
    /**
     * 印象展示
     */
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> adimpression;

    private boolean isUpload;

    /**
     * 搜索频道中的订阅源
     */
    public ArrayList<AttentionListEntity> attentionListEntities = new ArrayList<AttentionListEntity>();


    @Override
    public String toString() {
        return "NewsFeed{" +
                "ptime='" + ptime + '\'' +
                ", nid=" + nid +
                ", url='" + url + '\'' +
                ", docid='" + docid + '\'' +
                ", comment=" + comment +
                ", pname='" + pname + '\'' +
                ", purl='" + purl + '\'' +
                ", style=" + style +
                ", title='" + title + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", imgs=" + imgs +
                ", channel=" + channel +
                ", collect=" + collect +
                ", concern=" + concern +
                ", imageUrl='" + imageUrl + '\'' +
                ", isRead=" + isRead +
                ", isFavorite=" + isFavorite +
                ", descr='" + descr + '\'' +
                ", colflag=" + colflag +
                ", conflag=" + conflag +
                ", conpubflag=" + conpubflag +
                '}';
    }

    public ArrayList<AttentionListEntity> getAttentionListEntities() {
        return attentionListEntities;
    }

    public void setAttentionListEntities(ArrayList<AttentionListEntity> attentionListEntities) {
        this.attentionListEntities = attentionListEntities;
    }

    public int getRtype() {
        return rtype;
    }

    public void setRtype(int rtype) {
        this.rtype = rtype;
    }

    public int getConpubflag() {
        return conpubflag;
    }

    public void setConpubflag(int conpubflag) {
        this.conpubflag = conpubflag;
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

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
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

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
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

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getConcern() {
        return concern;
    }

    public void setConcern(int concern) {
        this.concern = concern;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public ArrayList<String> getAdimpression() {
        return adimpression;
    }

    public void setAdimpression(ArrayList<String> adimpression) {
        this.adimpression = adimpression;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }
}
