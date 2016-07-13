package com.news.yazhidao.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.news.yazhidao.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by fengjigang on 15/8/5.
 * 专辑中所包含的挖掘items
 */
@DatabaseTable(tableName = "tb_album_item")
public class AlbumSubItem implements Serializable {

    public final static String COLUMN_ALBUM_ID = "album_id";
    public final static String COLUMN_CREATE_TIME = "createTime";
    public final static String UPLOAD_DONE = "1";
    public final static String UPLOAD_NOT_DONE = "0";
    @DatabaseField(generatedId = true)
    private  int item_id;
    /**当前新闻挖掘的状态和进度*/
    @DatabaseField(columnName = "status")
    private String status;
    /**挖掘的url*/
    @DatabaseField(columnName = "search_url")
    private String search_url;
    /**当前挖掘新闻的id,唯一*/
    @DatabaseField(columnName = "inserteId")
    private String inserteId;
    /**挖掘的title*/
    @DatabaseField(columnName = "search_key")
    private String search_key;
    /**是否已经向服务器上传成功*/
    @DatabaseField(columnName = "is_uploaded")
    private String is_uploaded;
    /**创建的时间*/
    @DatabaseField(columnName = "createTime")
    private String createTime;
    /**图片*/
    @DatabaseField(columnName = "img")
    private String img;
    /**挖掘新闻的内容*/
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private NewsDetailForDigger detailForDigger;

    /**外键,该对象属于哪个专辑*/
    @DatabaseField(canBeNull = true,foreign = true,columnName = COLUMN_ALBUM_ID,foreignAutoRefresh = true)
    private DiggerAlbum diggerAlbum;
    public AlbumSubItem(){}

    public AlbumSubItem(String search_key, String search_url) {
        this.status = "1";
        this.search_url = search_url;
        this.inserteId = "";
        this.search_key = search_key;
        this.is_uploaded = UPLOAD_NOT_DONE;
        this.createTime = DateUtil.getDateWithMS();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSearch_url(String search_url) {
        this.search_url = search_url;
    }

    public void setInserteId(String inserteId) {
        this.inserteId = inserteId;
    }

    public void setSearch_key(String search_key) {
        this.search_key = search_key;
    }

    public String getStatus() {
        return status;
    }

    public String getSearch_url() {
        return search_url;
    }

    public String getInserteId() {
        return inserteId;
    }

    public String getSearch_key() {
        return search_key;
    }

    public String getIs_uploaded() {
        return is_uploaded;
    }

    public void setIs_uploaded(String is_uploaded) {
        this.is_uploaded = is_uploaded;
    }

    public DiggerAlbum getDiggerAlbum() {
        return diggerAlbum;
    }

    public void setDiggerAlbum(DiggerAlbum diggerAlbum) {
        this.diggerAlbum = diggerAlbum;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public NewsDetailForDigger getDetailForDigger() {
        return detailForDigger;
    }

    public void setDetailForDigger(NewsDetailForDigger detailForDigger) {
        this.detailForDigger = detailForDigger;
    }

    @Override
    public String toString() {
        return "<"+this.getClass().getSimpleName()+">:"+"status="+status+",search_url="+search_url+",search_key="+search_key+",inserteid="+inserteId+",is_upload="+is_uploaded+",diggerAlbum="+diggerAlbum;
    }
}
