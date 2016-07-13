package com.news.yazhidao.utils.manager;//package com.news.yazhidao.utils.manager;
//
//import android.media.MediaPlayer;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//import com.news.yazhidao.widget.SpeechView;
//
//import java.io.IOException;
//
///**
// * Created by fengjigang on 15/6/4.
// * 语音评论播放器管理
// */
//public class MediaPlayerManager {
//    private MediaPlayerManager(){
//        if(mMediaPlayer==null){
//            mMediaPlayer=new MediaPlayer();
//        }
//    }
//    private static MediaPlayerManager mInstance=null;
//    public static MediaPlayerManager getInstance(){
//        if(mInstance==null){
//            mInstance=new MediaPlayerManager();
//        }
//        return mInstance;
//    }
//    private MediaPlayer mMediaPlayer;
//    private String mUrl;
//    private SpeechView mSpeechView;
//    //是否是再一次点击停止
//    private boolean mIsSecondClickStop;
//    public  void setData(SpeechView speechView, final Handler mHandler, String path){
//        try {
//            if(mSpeechView!=null){
//                mSpeechView.stopAnimation();
//            }
//            //说明是第一次点击语音
//            if(mUrl==null){
//                mMediaPlayer.reset();
//                mMediaPlayer.setDataSource(path);
//                mMediaPlayer.prepare();
//            }else{
//                //点击的是同一条语音
//                if(mUrl.equals(path)){
//                    if(!mIsSecondClickStop){
//                        stop();
//                        Message msg= Message.obtain();
//                        msg.what= SpeechView.PLAY_COMPLETED;
//                        mHandler.sendMessage(msg);
//                        mIsSecondClickStop=true;
//                    }else {
//                        mIsSecondClickStop=false;
//                        mMediaPlayer.reset();
//                        mMediaPlayer.setDataSource(path);
//                        mMediaPlayer.prepare();
//                    }
//                }else{
//                    mIsSecondClickStop=false;
//
//                    Message msg= Message.obtain();
//                    msg.what= SpeechView.PLAY_COMPLETED;
//                    mHandler.sendMessage(msg);
//                    mMediaPlayer.reset();
//                    mMediaPlayer.setDataSource(path);
//                    mMediaPlayer.prepare();
//
//                }
//            }
//            mUrl=path;
//            mSpeechView=speechView;
//            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    Message msg= Message.obtain();
//                    msg.what= SpeechView.PLAY_COMPLETED;
//                    mHandler.sendMessage(msg);
//                }
//            });
//            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    Log.e("jigang","prepared---");
//                    Message msg= Message.obtain();
//                    msg.what= SpeechView.START_PLAY;
//                    mHandler.sendMessage(msg);
//                    mp.start();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    public  void pause(){
//        if(mMediaPlayer.isPlaying()){
//            mMediaPlayer.pause();
//        }
//    }
//    public  void stop(){
//        if(mMediaPlayer.isPlaying()){
//            mMediaPlayer.stop();
//        }
//    }
//}
