package com.news.sdk.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fengjigang on 16/3/30.
 * 新闻专题
 */
public class NewsTopic implements Serializable {


    private TopicBaseInfo topicBaseInfo;

    private ArrayList<TopicClass> topicClass;

    public TopicBaseInfo getTopicBaseInfo() {
        return topicBaseInfo;
    }

    public void setTopicBaseInfo(TopicBaseInfo topicBaseInfo) {
        this.topicBaseInfo = topicBaseInfo;
    }

    public ArrayList<TopicClass> getTopicClass() {
        return topicClass;
    }

    public void setTopicClass(ArrayList<TopicClass> topicClass) {
        this.topicClass = topicClass;
    }
}
