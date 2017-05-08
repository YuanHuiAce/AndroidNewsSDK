package com.news.yazhidao.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Berkeley on 5/8/17.
 */
@DatabaseTable(tableName = "tb_video_channel")
public class VideoChannel implements Serializable, Comparable<VideoChannel> {
    @DatabaseField(id = true)
    private int channelId;

    @DatabaseField
    private String cname;



    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    @Override
    public int compareTo(VideoChannel another) {
        int id = another.getChannelId();
        if (id < this.channelId)
            return -1;
        else if (id > this.channelId)
            return 1;
        else
            return 0;
    }
}
