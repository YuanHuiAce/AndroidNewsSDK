package com.news.yazhidao.utils;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class AMRAudioRecorder implements Callback {

    private String m_strFileName;
    private String m_strPath;
    private MediaRecorder mediaRecorder;
    private File audioFile;
    private static final int RECORDER_STOP = 1;
    boolean isRecording = false;
    int bufferSizeInBytes = 0;
    private Context mContext;

    public AMRAudioRecorder(Context myContext, String argFileName, String argPath) {
        this.mContext = myContext;
        m_strFileName = argFileName;
        m_strPath = argPath;
    }


    public void startRecorder() {
//        if (mAudioAnimation != null && !mAudioAnimation.isRunning()
//                && mblnIsPlay == false) {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            // 第1步：设置音频来源（MIC表示麦克风）
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            //第2步：设置音频输出格式（默认的输出格式）
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //第3步：设置音频编码方式（默认的编码方式）
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.setAudioSamplingRate(8000);
            //创建一个临时的音频输出文件
        }
        audioFile = new File(FileUtils.getSaveDir(mContext) + File.separator + m_strFileName + ".amr");
        Log.i("---", "audioFile.getAbsolutePath()---" + audioFile.getAbsolutePath());
        //第4步：指定音频输出文件
        mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

        try {
            //第5步：调用prepare方法
            mediaRecorder.prepare();

            //第6步：调用start方法开始录音
            mediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();

            mediaRecorder = null;
            return;
        }
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public void stopRecorder() {
        if (mediaRecorder != null && isRecording) {
            isRecording = false;
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    // 这里得到可播放的音频文件
    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = 8000;
        int channels = 2;
        long byteRate = 16 * 8000 * channels / 8;
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
     * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
     * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
     * 自己特有的头文件。
     */
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    String GetAudioPath() {
        return Environment
                .getExternalStorageDirectory().getAbsolutePath() + m_strPath + m_strFileName + ".mp3";
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case RECORDER_STOP:
                stopRecorder();
                break;
        }
        return false;
    }


    /**
     * 获取录音时间
     *
     * @return
     */
    public double getAmplitude() {

        if (mediaRecorder != null) {
            return (mediaRecorder.getMaxAmplitude());
        }
        return 0;
    }
}
