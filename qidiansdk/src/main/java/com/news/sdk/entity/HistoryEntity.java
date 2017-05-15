package com.news.sdk.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/11.
 */
public class HistoryEntity implements Serializable {
    private String content;
    private int position = 0;
    private boolean isFocus = false;

    public HistoryEntity(String content ) {
        this.content = content;
    }
    public HistoryEntity(int position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String cotent) {
        this.content = cotent;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

}
