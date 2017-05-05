package com.news.sdk.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * zip解压缩util
 */
public class ZipperUtil {
    public interface ZipCompleteListener{
        void complate();
    }
    public static boolean PASSED;
    public static File path;
    public static String name = "wryh.ttf";
    public static String fileName = "news.txt";
    private static final int buffer = 1024 * 1024;

    public static void unzip(final Context mContext, final ZipCompleteListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long _Start = System.currentTimeMillis();
                try {
                    path = getSaveFontPath(mContext);
                    if (path != null && path.exists()) {
                        listener.complate();
                        return;
                    }
                    InputStream is;

                    is = mContext.getApplicationContext().getAssets().open("fonts/wryh.ttf.zip");

                    ZipInputStream zipInputStream = new ZipInputStream(is);

                    ZipEntry zipEntry = null;
                    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                        byte data[] = new byte[buffer];
                        // 通过ZipFile的getInputStream方法拿到具体的ZipEntry的输入流
                        FileOutputStream fos = new FileOutputStream(path);
                        BufferedOutputStream bos = new BufferedOutputStream(fos, buffer);

                        int count;
                        while ((count = zipInputStream.read(data, 0, buffer)) != -1) {
                            bos.write(data, 0, count);
                        }

                        bos.flush();
                        bos.close();
                    }
                    Logger.i("xxxx", "xxxx time " + (System.currentTimeMillis() - _Start) + "");
                    zipInputStream.close();
                    PASSED = true;
                    if(listener!=null){
                        listener.complate();
                    }
                } catch (Exception e) {
                    if(listener!=null){
                        listener.complate();
                    }
                    e.printStackTrace();
                    Logger.e("xxxx", " unzip font fail");
                }
            }
        }).start();


    }

    public static File getSaveFontPath(Context mContext) {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            file = new File(Environment.getExternalStorageDirectory().toString());
        } else {
            file = new File(mContext.getFilesDir().toString());
        }
        if(file!=null){
            file=new File(file+ File.separator+"yazhidao");
            if(!file.exists()){
                try {
                    file.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new File(file,fileName);
    }

}
