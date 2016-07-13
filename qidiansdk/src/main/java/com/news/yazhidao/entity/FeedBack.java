package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedBack implements Serializable {

    //聊聊时间
    public String serviceId;
    public String updateTime;
    public ArrayList<Content> content;

    public class Content {
        public String content;
        public String type;
        public String imgUrl;
    }

}
