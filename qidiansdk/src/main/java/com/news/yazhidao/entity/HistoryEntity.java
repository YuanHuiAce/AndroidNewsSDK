package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/11.
 */
public class HistoryEntity implements Serializable {
    private String cotent;
    private int position = 0;
    private boolean isFocus = false;

    public HistoryEntity(String cotent ) {
        this.cotent = cotent;
    }
    public HistoryEntity(int position) {
        this.position = position;
    }

    public String getCotent() {
        return cotent;
    }

    public void setCotent(String cotent) {
        this.cotent = cotent;
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
