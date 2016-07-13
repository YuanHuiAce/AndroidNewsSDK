package com.news.yazhidao.utils.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.news.yazhidao.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;


/**
 * Created by fengjigang on 15/1/19.
 */
public class ImageLoaderHelper {
    private static ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoaderHelper helper;
    private static ImageLoaderConfiguration config;

    public ImageLoaderHelper(Context context) {
        config = generateConfig(context);
        imageLoader.init(config);
    }

    public static ImageLoader getImageLoader(Context context) {
        return imageLoader;
    }

    public static void dispalyImage(Context context, String url, ImageView imageView) {
        config = generateConfig(context);
        imageLoader.init(config);
        imageLoader.displayImage(url, imageView, getOption());
    }

    public static void dispalyImage(Context context, String url, ImageView imageView, View tv_title) {
        config = generateConfig(context);
        imageLoader.init(config);
        imageLoader.displayImage(url, new ImageViewAware(imageView), getOption(),null,null,tv_title);
    }

    public static void dispalyImage(Context context, String url, ImageView imageView, ImageLoadingListener listener) {
        config = generateConfig(context);
        imageLoader.init(config);
        imageLoader.displayImage(url, imageView, getOption(),listener);
    }

    public static void loadImage(Context context, String url, ImageLoadingListener listener) {
        config = generateConfig(context);
        imageLoader.init(config);
        imageLoader.loadImage(url, getOption(), listener);
    }

    public static void dispalyImage(Context context, String url, ImageView imageView, SimpleImageLoadingListener listener) {
        config = generateConfig(context);
        imageLoader.init(config);
        imageLoader.displayImage(url, imageView, getOption(), listener);
    }

    private static ImageLoaderConfiguration generateConfig(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(720,1280)  // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache()) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 8))
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(300) //缓存的文件数量
                .diskCache(new UnlimitedDiskCache(getDiskCacheDir(context, "BaiNewsCache")))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 15 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        return config;
    }

    private static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public static DisplayImageOptions getOption() {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_load_default_small) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.bg_load_default_small)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.bg_load_default_small)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                //.decodingOptions(android.graphics.BitmapFactory.Options decodingOptions)//设置图片的解码配置
                //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
                //设置图片加入缓存前，对bitmap进行设置
                //.preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                //.displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                //.displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成

        return options;
    }
}

