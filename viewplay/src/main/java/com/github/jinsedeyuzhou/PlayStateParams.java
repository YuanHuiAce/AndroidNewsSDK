package com.github.jinsedeyuzhou;

/**
 * Created by Berkeley on 11/2/16.
 */
public  class PlayStateParams {

    /**
     * 播放错误
     */
    public static final int STATE_ERROR = -1;
    /**
     * 空闲
     */
    public static final int STATE_IDLE = 0;
    /**
     * 正准备资源
     */
    public static final int STATE_PREPARING = 1;
    /**
     * 准备完成
     */
    public static final int STATE_PREPARED = 2;
    /**
     * 正在播放
     */
    public static final int STATE_PLAYING = 3;
    /**
     * 暂停
     */
    public static final int STATE_PAUSED = 4;
    /**
     * 完成
     */
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_PREPARE = 6;


    /**
     * 设置view隐藏
     */
    public static final int SET_VIEW_HIDE = 1;
    /**
     * 超时时间限制
     */
    public static final int TIME_OUT = 3000;
    /**
     * 展示进度条
     */
    public static final int MESSAGE_SHOW_PROGRESS = 2;
    /**
     * 隐藏暂停按钮
     */
    public static final int PAUSE_IMAGE_HIDE = 3;
    /**
     * seekbar新位置
     */
    public static final int MESSAGE_SEEK_NEW_POSITION = 4;
    /**
     * 隐藏控制bar
     */
    public static final int MESSAGE_HIDE_CONTOLL = 5;

    /**
     * 网络提示
     */

    public static final int MESSAGE_SHOW_DIALOG = 6;





}
