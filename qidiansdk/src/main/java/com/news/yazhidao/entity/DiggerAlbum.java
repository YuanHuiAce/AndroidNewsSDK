package com.news.yazhidao.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 挖掘机界面中得专辑entity
 * Created by fengjigang on 15/7/31.
 */
@DatabaseTable(tableName = "tb_album")
public class DiggerAlbum implements Serializable {

    public final static String UPLOAD_DONE = "1";
    public final static String UPLOAD_NOT_DONE = "0";

    /**专辑id*/
    @DatabaseField(id = true,columnName = "album_id")
    private String album_id;
    /**专辑创建的时间*/
    @DatabaseField(columnName = "create_time")
    private String create_time;
    /**专辑描述*/
    @DatabaseField(columnName = "album_des")
    private String album_des;
    /**用户id*/
    @DatabaseField(columnName = "user_id")
    private String user_id;
    /**专辑标题*/
    @DatabaseField(columnName = "album_title")
    private String album_title;
    /**专辑中包含挖掘内容的个数*/
    @DatabaseField(columnName = "album_news_count")
    private String album_news_count;
    /**专辑的背景图片*/
    @DatabaseField(columnName = "album_img")
    private String album_img;
    /**新建专辑时,是否上传成功 0 为未上传成功,1为上传成功*/
    @DatabaseField(columnName = "is_uploaded")
    private String is_uploaded;

    public DiggerAlbum(){}
    public DiggerAlbum(String album_id, String create_time, String album_des, String user_id, String album_title, String album_news_count, String album_img, String is_uploaded) {
        this.album_id = album_id;
        this.create_time = create_time;
        this.album_des = album_des;
        this.user_id = user_id;
        this.album_title = album_title;
        this.album_news_count = album_news_count;
        this.album_img = album_img;
        this.is_uploaded = is_uploaded;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setAlbum_des(String album_des) {
        this.album_des = album_des;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public void setAlbum_news_count(String album_news_count) {
        this.album_news_count = album_news_count;
    }

    public void setAlbum_img(String album_img) {
        this.album_img = album_img;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getAlbum_des() {
        return album_des;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public String getAlbum_news_count() {
        return album_news_count;
    }

    public String getAlbum_img() {
        return album_img;
    }

    public String getIs_uploaded() {
        return is_uploaded;
    }

    public void setIs_uploaded(String is_uploaded) {
        this.is_uploaded = is_uploaded;
    }

    @Override
    public String toString() {
        return "<"+this.getClass().getSimpleName()+">:"+"album_id="+album_id+",create_time="+create_time+",album_des="+album_des+",user_id="+user_id+",album_title="+album_title+",album_news_count="+album_news_count+",album_img="+album_img+",is_upload="+is_uploaded;
    }
}
