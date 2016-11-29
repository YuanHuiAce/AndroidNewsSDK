package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fengjigang on 16/3/30.
 * 新闻详情页
 */
public class TopicClass implements Serializable {


    private TopicClassBaseInfo topicClassBaseInfo;
    private ArrayList<NewsFeed> newsFeed;

    public TopicClassBaseInfo getTopicClassBaseInfo() {
        return topicClassBaseInfo;
    }

    public void setTopicClassBaseInfo(TopicClassBaseInfo topicClassBaseInfo) {
        this.topicClassBaseInfo = topicClassBaseInfo;
    }

    public ArrayList<NewsFeed> getNewsFeed() {
        return newsFeed;
    }

    public void setNewsFeed(ArrayList<NewsFeed> newsFeed) {
        this.newsFeed = newsFeed;
    }

    public class TopicClassBaseInfo implements Serializable {
        private int id;
        private String name;
        private int topic;
        private int order;
        private ArrayList<NewsFeed> newsFeed;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTopic() {
            return topic;
        }

        public void setTopic(int topic) {
            this.topic = topic;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public ArrayList<NewsFeed> getArrNewsFeed() {
            return newsFeed;
        }

        public void setArrNewsFeed(ArrayList<NewsFeed> arrNewsFeed) {
            this.newsFeed = arrNewsFeed;
        }
    }
}
