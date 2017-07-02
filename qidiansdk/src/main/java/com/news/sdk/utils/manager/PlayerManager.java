package com.news.sdk.utils.manager;

import android.content.Context;

import com.github.jinsedeyuzhou.VPlayPlayer;
import com.news.sdk.entity.NewsFeed;

/**
 * Created by Berkeley on 12/19/16.
 */
public class PlayerManager {

    public static PlayerManager videoPlayViewManage;
    public static VPlayPlayer videoPlayView;
    public static NewsFeed newsFeed;


    private PlayerManager() {

    }

    public static PlayerManager getPlayerManager() {
        if (videoPlayViewManage == null) {
            videoPlayViewManage = new PlayerManager();
        }
        return videoPlayViewManage;
    }

    public VPlayPlayer initialize(Context context) {
        if (videoPlayView == null) {
            videoPlayView = new VPlayPlayer(context);
        }
        return videoPlayView;
    }

}
