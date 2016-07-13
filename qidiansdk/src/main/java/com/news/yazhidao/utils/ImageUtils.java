package com.news.yazhidao.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Berkeley on 3/30/15.
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";


    /** 图片最大宽度. */
    public static final int MAX_WIDTH = 4096/2;

    /** 图片最大高度. */
    public static final int MAX_HEIGHT = 4096/2;

    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, float roundPX) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setColor(color);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return bitmap2;
    }


    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels, boolean isSquareTL, boolean isSquareTR, boolean isSquareBL, boolean isSquareBR) {
        int w = input.getWidth();
        int h = input.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        //make sure that our rounded corner is scaled appropriately
        final float roundPx = pixels * densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


        //draw rectangles over the corners we want to be square
        if (!isSquareTL) {
            canvas.drawRect(0, 0, w / 2, h / 2, paint);
        }
        if (!isSquareTR) {
            canvas.drawRect(w / 2, 0, w, h / 2, paint);
        }
        if (!isSquareBL) {
            canvas.drawRect(0, h / 2, w / 2, h, paint);
        }
        if (!isSquareBR) {
            canvas.drawRect(w / 2, h / 2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0, 0, paint);

        return output;
    }

    /**
     * 高和宽等比例缩放
     *
     * @param bm
     * @param newWidth
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bm, int newWidth) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);//横竖都按照水平方向来缩放
        // 得到新的图片bitmap
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 高和宽等比例缩放
     *
     * @param bm
     * @param newHeight
     * @return
     */
    public static Bitmap zoomBitmapWithHeight(Bitmap bm, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleHeight = ((float) newHeight) / height;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleHeight, scaleHeight);//横竖都按照水平方向来缩放
        // 得到新的图片bitmap
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 截取高度
     *
     * @param bm
     * @param screenWidth
     * @return
     */
    public static Bitmap zoomBitmap2(Bitmap bm, int screenWidth, int screenHeight, int type) {
//        boolean flag = false;
//        int newHeight = 0;
//
//        Bitmap bitmap_big = null;
//
//        if(bm != null) {
//            bitmap_big = zoomBitmap(bm, screenWidth);
//        }
//
//        if(bitmap_big != null && bitmap_big.getHeight() < screenHeight * 0.4){
//            bitmap_big = zoomBitmapWithHeight(bitmap_big,(int)(screenHeight * 0.4));
//        }
//
//        if(bitmap_big != null) {
//            newHeight = bitmap_big.getHeight();
//        }
//        int standardHeight = DensityUtil.dip2px(GlobalParams.context,450);
//
//        if(newHeight > standardHeight){
//            flag = true;
//        }
//
//        if (type == 1) {
//            //textviewextend
//            if (newHeight > screenHeight * 0.4) {
//                newHeight = (int) (screenHeight * 0.4);
//            }
//        } else if (type == 2) {
//            //textviewvertical
//            if (newHeight > screenHeight * 0.4) {
//                newHeight = (int) (screenHeight * 0.4);
//            }else{
//                newHeight = (int) (screenHeight * 0.4);
//            }
//
//        }
//
//        Bitmap bitmap = null;
//
//        int shift = DensityUtil.dip2px(GlobalParams.context,100);
//
//        if(flag){
//            if(bitmap_big.getHeight() > shift + newHeight) {
//                bitmap = Bitmap.createBitmap(bitmap_big, 0, shift, screenWidth, newHeight);
//            }else{
//                bitmap = Bitmap.createBitmap(bitmap_big, 0, 0, screenWidth, newHeight);
//            }
//        }else {
//            bitmap = Bitmap.createBitmap(bitmap_big, 0, 0, screenWidth, newHeight);
//        }


        return bm;
    }

    /**
     * 截取宽度
     *
     * @param bm
     * @param screenWidth
     * @return
     */
    public static Bitmap zoomBitmap3(Bitmap bm, int screenWidth, int screenHeight, int type) {

        float scale = (float) (screenHeight * 0.4 / bm.getHeight());

        int newWidth = bm.getWidth();
        //textviewvertical
        if (newWidth > screenWidth) {
            newWidth = screenWidth;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);//横竖都按照水平方向来缩放
        // 得到新的图片bitmap
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

        //得到截取后的bitmap
        Bitmap bitmap = Bitmap.createBitmap(newbm, 0, 0, screenWidth, bm.getHeight());

        return bitmap;
    }


    /**
     * 描述：缩放图片.
     *
     * @param bitmap
     *            the bitmap
     * @param desiredWidth
     *            新图片的宽
     * @param desiredHeight
     *            新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getScaleBitmap(Bitmap bitmap, int desiredWidth, int desiredHeight) {

        if (!checkBitmap(bitmap)) {
            return null;
        }
        Bitmap resizeBmp = null;

        // 获得图片的宽高
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();

        int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
        desiredWidth = size[0];
        desiredHeight = size[1];

        float scale = getMinScale(srcWidth, srcHeight, desiredWidth, desiredHeight);
        resizeBmp = scaleBitmap(bitmap, scale);
        //超出的裁掉
        if (resizeBmp.getWidth() > desiredWidth || resizeBmp.getHeight() > desiredHeight) {
            resizeBmp  = getCutBitmap(resizeBmp,desiredWidth,desiredHeight);
        }
        return resizeBmp;
    }

    private static boolean checkBitmap(Bitmap bitmap) {
        if (bitmap == null) {
//            AbLogUtil.e(AbImageUtil.class, "原图Bitmap为空了");
            return false;
        }

        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
//            AbLogUtil.e(AbImageUtil.class, "原图Bitmap大小为0");
            return false;
        }
        return true;
    }


    private static int[] resizeToMaxSize(int srcWidth, int srcHeight,
                                         int desiredWidth, int desiredHeight) {
        int[] size = new int[2];
        if(desiredWidth <= 0){
            desiredWidth = srcWidth;
        }
        if(desiredHeight <= 0){
            desiredHeight = srcHeight;
        }
        if (desiredWidth > MAX_WIDTH) {
            // 重新计算大小
            desiredWidth = MAX_WIDTH;
            float scaleWidth = (float) desiredWidth / srcWidth;
            desiredHeight = (int) (desiredHeight * scaleWidth);
        }

        if (desiredHeight > MAX_HEIGHT) {
            // 重新计算大小
            desiredHeight = MAX_HEIGHT;
            float scaleHeight = (float) desiredHeight / srcHeight;
            desiredWidth = (int) (desiredWidth * scaleHeight);
        }
        size[0] = desiredWidth;
        size[1] = desiredHeight;
        return size;
    }

    private static float getMinScale(int srcWidth, int srcHeight, int desiredWidth,
                                     int desiredHeight) {
        // 缩放的比例
        float scale = 0;
        // 计算缩放比例，宽高的最小比例
        float scaleWidth = (float) desiredWidth / srcWidth;
        float scaleHeight = (float) desiredHeight / srcHeight;
        if (scaleWidth > scaleHeight) {
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }

        return scale;
    }


    /**
     * 描述：根据等比例缩放图片.
     *
     * @param bitmap
     *            the bitmap
     * @param scale
     *            比例
     * @return Bitmap 新图片
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {

        if (!checkBitmap(bitmap)) {
            return null;
        }

        if (scale == 1) {
            return bitmap;
        }

        Bitmap resizeBmp = null;
        try {
            // 获取Bitmap资源的宽和高
            int bmpW = bitmap.getWidth();
            int bmpH = bitmap.getHeight();

            // 注意这个Matirx是android.graphics底下的那个
            Matrix matrix = new Matrix();
            // 设置缩放系数，分别为原来的0.8和0.8
            matrix.postScale(scale, scale);
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resizeBmp != bitmap) {
                bitmap.recycle();
            }
        }
        return resizeBmp;
    }

    /**
     * 描述：裁剪图片.
     *
     * @param bitmap
     *            the bitmap
     * @param desiredWidth
     *            新图片的宽
     * @param desiredHeight
     *            新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getCutBitmap(Bitmap bitmap, int desiredWidth, int desiredHeight) {

        if (!checkBitmap(bitmap)) {
            return null;
        }

        if (!checkSize(desiredWidth, desiredHeight)) {
            return null;
        }

        Bitmap resizeBmp = null;

        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int offsetX = 0;
            int offsetY = 0;

            if (width > desiredWidth) {
                offsetX = (width - desiredWidth) / 2;
            } else {
                desiredWidth = width;
            }

            if (height > desiredHeight) {
                offsetY = (height - desiredHeight) / 2;
            } else {
                desiredHeight = height;
            }

            resizeBmp = Bitmap.createBitmap(bitmap, offsetX, offsetY, desiredWidth,desiredHeight);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resizeBmp != bitmap) {
                bitmap.recycle();
            }
        }
        return resizeBmp;
    }

    private static boolean checkSize(int desiredWidth, int desiredHeight) {
        if (desiredWidth <= 0 || desiredHeight <= 0) {
//            AbLogUtil.e(AbImageUtil.class, "请求Bitmap的宽高参数必须大于0");
            return false;
        }
        return true;
    }

}
