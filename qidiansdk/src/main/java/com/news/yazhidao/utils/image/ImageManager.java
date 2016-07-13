package com.news.yazhidao.utils.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.news.yazhidao.R;
import com.news.yazhidao.listener.DisplayImageListener;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.ImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImageManager {
    private static ImageManager mInstance;
    private DisplayImageListener listener;
    private Bitmap loadBitmap;
    private int width;
    private int height;
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    // 线程池
    ExecutorService executorService;
    public static ImageManager getInstance(Context mContext){
        if(mInstance==null){
            mInstance=new ImageManager(mContext);
        }
        return mInstance;
    }
    private ImageManager(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(10);
    }

    // 当进入listview时默认的图片，可换成你自己的默认图片
    final int stub_id = R.drawable.default_image;

    // 最主要的方法
    public void DisplayImage(String url, ImageView imageView, boolean isPreHandle, DisplayImageListener listener) {

        this.listener = listener;

        imageViews.put(imageView, url);
        // 先从内存缓存中查找
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            if (isPreHandle) {
                bitmap= ImageUtils.zoomBitmap(bitmap, DeviceInfoUtil.getScreenWidth());
            }

            if(bitmap != null) {
                width = bitmap.getWidth();
                height = bitmap.getHeight();
            }

            imageView.setImageBitmap(bitmap);
            if(listener!=null)
                listener.success(width,height);
        }else {
            // 若没有的话则开启新线程加载图片
            queuePhoto(url, imageView,isPreHandle);
            imageView.setImageResource(stub_id);

        }
    }

    public Bitmap getImage(String url) {
        Bitmap bitmap = memoryCache.get(url);
        return bitmap;
    }

    private void queuePhoto(String url, ImageView imageView, boolean isPreHandle) {
        PhotoToLoad p = new PhotoToLoad(url, imageView,isPreHandle);
        executorService.submit(new PhotosLoader(p));
    }

    public Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        // 先从文件缓存中查找是否有
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

        // 最后从指定的url中下载图片
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream fileStream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(fileStream1, null, o);
            fileStream1.close();

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 500;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream fileStream = new FileInputStream(f);
            Bitmap resultMap = BitmapFactory.decodeStream(fileStream, null, o2);
            fileStream.close();
            return resultMap;
        } catch (Exception e) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public boolean isPreHandle;
        public PhotoToLoad(String u, ImageView i, boolean isPreHandle) {
            this.url = u;
            this.imageView = i;
            this.isPreHandle=isPreHandle;
        }

    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad,listener);
            // 更新的操作放在UI线程中
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    /**
     * 防止图片错位
     *
     * @param photoToLoad
     * @return
     */
    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // 用于在UI线程中更新界面
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        DisplayImageListener mListener;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p, DisplayImageListener listener) {
            bitmap = b;
            photoToLoad = p;
            this.mListener=listener;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null){

                if(photoToLoad.isPreHandle){
                    bitmap= ImageUtils.zoomBitmap(bitmap, DeviceInfoUtil.getScreenWidth());
                }

                if(bitmap != null) {
                    width = bitmap.getWidth();
                    height = bitmap.getHeight();
                }

                if(mListener!=null)
                    mListener.success(width,height);
                photoToLoad.imageView.setImageBitmap(bitmap);
            }
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    private  void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
}