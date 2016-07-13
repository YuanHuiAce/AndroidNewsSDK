package com.news.yazhidao.entity;

import com.news.yazhidao.utils.GsonUtil;

import java.io.Serializable;

/**
 * Created by fiocca on 15/5/15.
 */
public class Message implements Serializable {
    private String receiverId;
    private String senderId;
    private String content;
    private String msgType;

    public Message(String receiverId, String senderId, String content, String msgType) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.content = content;
        this.msgType = msgType;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String toJsonString(){
        return GsonUtil.serialized(this);
    }
}
