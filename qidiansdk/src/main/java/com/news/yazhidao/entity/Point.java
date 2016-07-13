package com.news.yazhidao.entity;

/**
 * Created by Berkeley on 5/15/15.
 */
public class Point {

    private String userName;
    private String srcText;
    private String desText;
    private String paragraphIndex;
    private String type;
    private String uuid;
    private String userIcon;
    private String sourceUrl;


    public String getSrcText() {
        return srcText;
    }

    public void setSrcText(String srcText) {
        this.srcText = srcText;
    }

    public String getDesText() {
        return desText;
    }

    public void setDesText(String desText) {
        this.desText = desText;
    }

    public String getParagraphIndex() {
        return paragraphIndex;
    }

    public void setParagraphIndex(String paragraphIndex) {
        this.paragraphIndex = paragraphIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
