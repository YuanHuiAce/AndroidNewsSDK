package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class TimeFeed implements Serializable {

    private String next_update_freq;
    private String next_update_time;
    private String next_update_type;
    private String request_time;
    private ArrayList<String> history_date;

    public String getNext_update_freq() {
        return next_update_freq;
    }

    public void setNext_update_freq(String next_update_freq) {
        this.next_update_freq = next_update_freq;
    }

    public String getNext_upate_time() {
        return next_update_time;
    }

    public void setNext_upate_time(String next_upate_time) {
        this.next_update_time = next_upate_time;
    }

    public String getNext_update_type() {
        return next_update_type;
    }

    public void setNext_update_type(String next_update_type) {
        this.next_update_type = next_update_type;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }

    public ArrayList<String> getHistory_date() {
        return history_date;
    }

    public void setHistory_date(ArrayList<String> history_date) {
        this.history_date = history_date;
    }
}
