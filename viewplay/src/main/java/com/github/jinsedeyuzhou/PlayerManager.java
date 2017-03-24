package com.github.jinsedeyuzhou;

import android.content.Context;

/**
 * Created by Berkeley on 12/19/16.
 * 这样写有问题
 */
public class PlayerManager {

    public static PlayerManager videoPlayViewManage;
    public static VPlayPlayer videoPlayView;


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
