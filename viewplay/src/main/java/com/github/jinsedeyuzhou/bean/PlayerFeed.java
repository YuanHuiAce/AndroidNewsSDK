package com.github.jinsedeyuzhou.bean;

import java.io.Serializable;

/**
 * Created by Berkeley on 5/3/17.
 */

public class PlayerFeed implements Serializable {
    private String streamUrl;
    private String img;
    private String title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
