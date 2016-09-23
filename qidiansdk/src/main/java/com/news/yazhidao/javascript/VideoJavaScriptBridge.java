package com.news.yazhidao.javascript;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.news.yazhidao.pages.PlayVideoAty;
import com.news.yazhidao.utils.Logger;
import com.tencent.smtt.sdk.TbsVideo;

/**
 * Created by fengjigang on 16/6/22.
 */
public class VideoJavaScriptBridge {

    public static final String KEY_VIDEO_URL = "KEY_VIDEO_URL";
    private Activity mContext;

    public VideoJavaScriptBridge(Activity mContext) {
        this.mContext = mContext;
    }

    @JavascriptInterface
    public void openVideo(String url) {
//        int v = new Random().nextInt(1000);
//        if (v % 2 == 0) {
////            TbsVideo.openVideo(mContext,"http://video.jiecao.fm/9/6/%E7%83%A8/%E7%9B%B8%E5%A3%B0.mp4");
////        TbsVideo.openVideo(mContext,"http://v.qq.com/iframe/player.html?vid=p0327a7pt4p&width=290&height=217.5&auto=0");
//            Intent playAty = new Intent(mContext, PlayVideoAty.class);
//            playAty.putExtra(KEY_VIDEO_URL, "http://v.qq.com/iframe/player.html?vid=p0327a7pt4p&width=290&height=217.5&auto=0");
//            mContext.startActivity(playAty);
//
//        } else {
//            TbsVideo.openVideo(mContext, "http://video.jiecao.fm/9/6/%E7%83%A8/%E7%9B%B8%E5%A3%B0.mp4");
//        }
        if (url.contains("player.html")) {
//            TbsVideo.openVideo(mContext,"http://video.jiecao.fm/9/6/%E7%83%A8/%E7%9B%B8%E5%A3%B0.mp4");
//        TbsVideo.openVideo(mContext,"http://v.qq.com/iframe/player.html?vid=p0327a7pt4p&width=290&height=217.5&auto=0");
            Intent playAty = new Intent(mContext, PlayVideoAty.class);
            playAty.putExtra(KEY_VIDEO_URL, url);
            mContext.startActivity(playAty);

        } else {
            TbsVideo.openVideo(mContext, url);
        }
        Logger.e("jigang", "video url =" + url);
//
    }
}
