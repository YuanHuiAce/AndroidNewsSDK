package com.news.sdk.adapter.abslistview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.news.sdk.R;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.widget.TextViewExtend;

/**
 * Created by fengjigang on 16/4/14.
 * ListView GridView 通用ViewHolder
 */
public class CommonViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private Context mContext;
    private View mConvertView;
    private int mLayoutId;
    private SharedPreferences mSharedPreferences;

    public CommonViewHolder(Context mContext, View mConvertView, ViewGroup mViewGroup, int mPosition) {
        this.mContext = mContext;
        this.mConvertView = mConvertView;
        this.mPosition = mPosition;
        this.mViews = new SparseArray<>();
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context mContext, View mConvertView, ViewGroup mViewGroup, int mLayoutId, int mPosition) {
        if (mConvertView == null) {
            View itemView = LayoutInflater.from(mContext).inflate(mLayoutId, mViewGroup, false);
            CommonViewHolder viewHolder = new CommonViewHolder(mContext, itemView, mViewGroup, mPosition);
            viewHolder.mLayoutId = mLayoutId;
            return viewHolder;
        } else {
            CommonViewHolder viewHolder = (CommonViewHolder) mConvertView.getTag();
            viewHolder.mPosition = mPosition;
            return viewHolder;
        }
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public ImageView getImageView(int ViewID) {
        ImageView image = getView(ViewID);
        return image;
    }

    public void setTextViewText(int ViewID, String content) {
        TextView text = getView(ViewID);
        text.setText(content);
    }

    public void setTextViewTextBackgroundResource(int ViewID, int resource) {
        TextView text = getView(ViewID);
        text.setBackgroundResource(resource);
    }

    public void setTextViewExtendText(int ViewID, String content) {
        TextViewExtend text = getView(ViewID);
        text.setText(content);
    }

    public void setTextViewExtendTextandTextSice(int ViewID, String content) {
        TextViewExtend text = getView(ViewID);
        text.setText(content);
        text.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
    }

    public void setTextViewTextColor(int ViewID, int color) {
        TextView text = getView(ViewID);
        text.setTextColor(mContext.getResources().getColor(color));
    }

    public void setTextViewExtendTextColor(int ViewID, int color) {
        TextViewExtend text = getView(ViewID);
        text.setTextColor(mContext.getResources().getColor(color));
    }

    public void setTextViewExtendTextBackground(int ViewID, int color) {
        TextViewExtend text = getView(ViewID);
        text.setBackgroundColor(mContext.getResources().getColor(color));
    }

    public void setTextViewExtendTextBackgroundResource(int ViewID, int resource) {
        TextViewExtend text = getView(ViewID);
        text.setBackgroundResource(resource);
    }


    public View getConvertView() {
        return mConvertView;
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    public void setSimpleDrawViewResource(int drawView, int Resource) {
        ImageView imageView = getView(drawView);
        imageView.setImageResource(Resource);
    }

    public void setGlideDrawViewURI(int drawView, String strImg) {
        ImageView imageView = getView(drawView);
        if (!TextUtil.isEmptyString(strImg)) {
            Glide.with(mContext).load(Uri.parse(strImg)).placeholder(R.drawable.ic_user_comment_default).diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.news_source_bg))).into(imageView);
        } else
            Glide.with(mContext).load("").placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.news_source_bg))).into(imageView);
    }

    public void setGlideDrawViewURI(int drawView, String strImg, int width, int height, int rType) {
        ImageView imageView = getView(drawView);
        if (!TextUtil.isEmptyString(strImg)) {
            if (SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)) {
                imageView.setImageResource(R.drawable.bg_load_default_small);
//                imageView.setImageURI(Uri.parse("res://com.news.yazhidao/" + R.drawable.bg_load_default_small));
//                Glide.with(mContext).load(R.drawable.bg_load_default_small).into(imageView);
            } else {
                Uri uri;
                if (rType != 3 && rType != 4 && rType != 6 && rType != 50 && rType != 51) {
                    String img = strImg.replace("bdp-", "pro-");
                    uri = Uri.parse(img + "@1e_1c_0o_0l_100sh_" + height + "h_" + width + "w_95q.jpg");
                } else {
                    uri = Uri.parse(strImg);
                }
                if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
                    imageView.setAlpha(0.5f);
                } else {
                    imageView.setAlpha(1.0f);
                }
                Glide.with(mContext).load(uri).placeholder(R.drawable.bg_load_default_small).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            }
        }
    }

    public void setGlideDrawViewURI(int drawView, String strImg, int position) {
        ImageView imageView = getView(drawView);
        if (!TextUtil.isEmptyString(strImg)) {
            Glide.with(mContext).load(Uri.parse(strImg)).placeholder(R.drawable.m_r_q1).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        } else {
            int type = position % 5;
            if (type == 0) {
                imageView.setImageResource(R.drawable.m_r_q1);
            } else if (type == 1) {
                imageView.setImageResource(R.drawable.m_r_q2);
            } else if (type == 2) {
                imageView.setImageResource(R.drawable.m_r_q3);
            } else if (type == 3) {
                imageView.setImageResource(R.drawable.m_r_q4);
            } else if (type == 4) {
                imageView.setImageResource(R.drawable.m_r_q5);
            }
        }
    }

    public static class GlideCircleTransform extends BitmapTransformation {

        private Paint mBorderPaint;
        private float mBorderWidth;

        public GlideCircleTransform(Context context) {
            super(context);
        }

        public GlideCircleTransform(Context context, int borderWidth, int borderColor) {
            super(context);
            mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;

            mBorderPaint = new Paint();
            mBorderPaint.setDither(true);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }


        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            if (mBorderPaint != null) {
                float borderRadius = r - mBorderWidth / 2;
                canvas.drawCircle(r, r, borderRadius, mBorderPaint);
            }
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }
}
