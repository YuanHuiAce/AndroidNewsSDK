package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.content.Context;
//import android.graphics.drawable.AnimationDrawable;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.alibaba.sdk.android.oss.callback.GetBytesCallback;
//import com.alibaba.sdk.android.oss.model.OSSException;
//import com.news.yazhidao.R;
//import com.news.yazhidao.utils.DensityUtil;
//import com.news.yazhidao.utils.DeviceInfoUtil;
//import com.news.yazhidao.utils.FileUtils;
//import com.news.yazhidao.utils.Logger;
//import com.news.yazhidao.utils.manager.AliYunOssManager;
//import com.news.yazhidao.utils.manager.MediaPlayerManager;
//
//import java.io.File;
//
///**
// * Created by fengjigang on 15/6/5.
// */
//public class SpeechView extends LinearLayout implements View.OnClickListener {
//    private static final int DOWNLOAD_COMPLETE = 1;
//    public static final int PLAY_COMPLETED=2;
//    public static final int START_PLAY=3;
//    private final View mWaveWrapper;
//    private final int mScreenWidth;
//    private Context mContext;
//    private AnimationDrawable aniSpeech;
//    private ImageView mWave;
//    //语音url
//    private String mUrl;
//    //语音的时长
//    private TextView mDuration;
//    private Handler mHandler = new Handler() {
//        @Override
//        public void dispatchMessage(Message msg) {
//            switch (msg.what) {
//                case DOWNLOAD_COMPLETE:
//                    String filePath = (String) msg.obj;
//                    MediaPlayerManager.getInstance().setData(SpeechView.this,mHandler, filePath);
//                    break;
//                case PLAY_COMPLETED:
//                  stopAnimation();
//                    break;
//                case START_PLAY:
//                    startAnimation();
//                    break;
//            }
//        }
//    };
//
//    public SpeechView(Context context) {
//        this(context, null);
//    }
//
//    public SpeechView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public SpeechView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.mContext = context;
//        View view = View.inflate(context, R.layout.speech_layout, this);
//        mWave = (ImageView) view.findViewById(R.id.mWave);
//        mDuration=(TextView)view.findViewById(R.id.mDuration);
//        mWaveWrapper=view.findViewById(R.id.mWaveWrapper);
//        mScreenWidth= DeviceInfoUtil.getScreenWidth();
//        setOnClickListener(this);
//    }
//
//    public void setUrl(String url, boolean isAutoPlay) {
//        this.mUrl = url;
//        if(isAutoPlay){
//            onClick(this);
//        }
//    }
//    public void setUrlAndDuration(String pUrl, int pDuration, boolean isAutoPlay){
//        setUrl(pUrl,isAutoPlay);
//        setDuration(pDuration);
//    }
//    public void setDuration(int duration) {
//        if(duration<=0||duration>30){
//            return;
//        }
//        mDuration.setText(duration + "\"");
//        if(duration<8){
//            duration=8;
//        }
//        int newWidth= (int) (mScreenWidth*0.62f*duration/30);
//        Logger.i("jigang",newWidth+"--newWidth--"+newWidth);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(newWidth, DensityUtil.dip2px(mContext, 30));
//        mWaveWrapper.setLayoutParams(params);
//    }
//
//    public void startAnimation(){
//        mWave.setImageResource(R.drawable.ani_speech);
//        aniSpeech = (AnimationDrawable) mWave.getDrawable();
//        aniSpeech.start();
//    }
//    public void stopAnimation(){
//        if (aniSpeech != null&&aniSpeech.isRunning()) {
//            mWave.setImageResource(R.drawable.ic_speech_wave3);
//            aniSpeech.stop();
//        }
//    }
//    @Override
//    public void onClick(View v) {
//        Log.e("jigang", "--onclick----" + mUrl);
//        if (TextUtils.isEmpty(mUrl)) {
//            return;
//        }
//        //如果是刚录制完的语音文件则直接播放
//        if(!mUrl.contains("http:")){
//            MediaPlayerManager.getInstance().setData(SpeechView.this, mHandler, mUrl);
//            startAnimation();
//            return;
//        }else {
//            //如果请求的语音在本地有，则走如下逻辑
//            String name=mUrl.substring(mUrl.lastIndexOf("/")+1);
//            File savePath = FileUtils.getSavePath(mContext, name);
//            Logger.i("jigang", "local have path===" + savePath);
//            if(savePath.exists()){
//                MediaPlayerManager.getInstance().setData(SpeechView.this, mHandler, savePath.getPath());
//                startAnimation();
//                return;
//            }
//        }
//        //如果已经存在改语音文件，则直接播放
//        boolean existFile = FileUtils.isExistFile(mContext, mUrl);
//        if (existFile) {
//            String filePath = FileUtils.getFilePath(mContext, mUrl);
//            Log.e("jigang", "----filepath--" + filePath );
//            MediaPlayerManager.getInstance().setData(SpeechView.this, mHandler, filePath);
//            startAnimation();
//
//            return;
//        }
//
//        //下载语音文件到sd 卡
//        AliYunOssManager.getInstance(mContext).downloadSpeechFile(mUrl, new GetBytesCallback() {
//            @Override
//            public void onSuccess(String s, byte[] bytes) {
//                Log.e("jigang", "---onSuccess- success--"+bytes.length);
//                //把文件写入到cache中
//                File path = FileUtils.writeFile2SDCard(mContext, FileUtils.getMD5(mUrl), bytes);
//                Message msg = Message.obtain();
//                msg.obj = path.getPath();
//                msg.what = DOWNLOAD_COMPLETE;
//                mHandler.sendMessage(msg);
//            }
//
//            @Override
//            public void onProgress(String s, int i, int i1) {
//                Log.e("jigang", "---onProgress-");
//            }
//
//            @Override
//            public void onFailure(String s, OSSException e) {
//                Log.e("jigang", "---onFailure- " + e.getMessage());
//            }
//        });
//    }
//
//}
