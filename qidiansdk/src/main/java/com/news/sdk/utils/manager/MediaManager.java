package com.news.sdk.utils.manager;

import android.content.Context;

import com.github.jinsedeyuzhou.MediaPlayer;
import com.news.sdk.entity.NewsFeed;


/**
 * Created by Berkeley on 12/19/16.
 */
public class MediaManager {

    public static MediaManager videoPlayViewManage;
    public static MediaPlayer videoPlayView;
    public static NewsFeed newsFeed;
    public static boolean isList;


    private MediaManager() {

    }

    public static MediaManager getPlayerManager() {
        if (videoPlayViewManage == null) {
            videoPlayViewManage = new MediaManager();
        }
        return videoPlayViewManage;
    }

    public MediaPlayer initialize(Context context) {
        if (videoPlayView == null) {
            videoPlayView = new MediaPlayer(context);
        }
        return videoPlayView;
    }

}
