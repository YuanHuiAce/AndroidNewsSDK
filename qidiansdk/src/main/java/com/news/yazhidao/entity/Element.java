package com.news.yazhidao.entity;

/**
 * Created by Berkeley on 7/30/15.
 */
public class Element {

    private String title;

    private String[] baiduHotWord;

    private String type;

    private String createTime;

    private String chemicalBond;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getBaiduHotWord() {
        return baiduHotWord;
    }

    public void setBaiduHotWord(String[] baiduHotWord) {
        this.baiduHotWord = baiduHotWord;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getChemicalBond() {
        return chemicalBond;
    }

    public void setChemicalBond(String chemicalBond) {
        this.chemicalBond = chemicalBond;
    }
}
