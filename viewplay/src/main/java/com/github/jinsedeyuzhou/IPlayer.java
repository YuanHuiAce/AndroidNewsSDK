package com.github.jinsedeyuzhou;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 4/13/17.
 */

public interface IPlayer {


    void setOnShareListener();
    void setOnClickOrientationListener(IPlayer.OnClickOrientationListener var1);
    void setOnErrorListener(IPlayer.OnErrorListener var1);
    void setOnPreparedListener(IPlayer.OnPreparedListener var1);
    void setOnInfoListener(IPlayer.OnInfoListener var1);
    void setCompletionListener(IPlayer.CompletionListener var1);
    void setOnNetChangeListener(IPlayer.OnNetChangeListener var1);
    public interface OnClickOrientationListener {
        void landscape();

        void portrait();
    }

    public interface OnShareListener {
        void onShare();

        void onPlayCancel();
    }
    public interface OnPreparedListener{
        void onPrePared(IMediaPlayer mp);
    }
    public interface OnErrorListener {
        void onError(int what, int extra);
    }


    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }


    public interface CompletionListener {
        void completion(IMediaPlayer mp);
    }

    public interface OnNetChangeListener {
        // wifi
        void onWifi();

        // 手机
        void onMobile();

        // 网络断开
        void onDisConnect();

        // 网路不可用
        void onNoAvailable();
    }


}
