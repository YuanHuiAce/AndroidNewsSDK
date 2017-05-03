package com.github.jinsedeyuzhou.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.github.jinsedeyuzhou.R;
import com.github.jinsedeyuzhou.bean.PlayerFeed;

import java.util.List;

/**
 * Created by Berkeley on 5/3/17.
 */

public class PlayerAdapter extends BaseAdapter {

    private Context mContext;
    private List<PlayerFeed> list;

    public PlayerAdapter(List<PlayerFeed> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        PlayerFeed playerFeed = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_player, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.ivImageView = (ImageView) convertView.findViewById(R.id.iv_img_view);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTitle.setText(playerFeed.getTitle());
        setGlideDrawViewURI(viewHolder.ivImageView, playerFeed.getImg());


        return convertView;
    }

    final static class ViewHolder {
        ImageView ivImageView;
        TextView tvTitle;
    }

    public void setGlideDrawViewURI(ImageView drawView, String strImg) {
        if (!TextUtils.isEmpty(strImg)) {
            Glide.with(mContext).load(Uri.parse(strImg)).placeholder(R.drawable.ic_user_comment_default).diskCacheStrategy(DiskCacheStrategy.ALL).transform(new GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.Grey_1000))).into(drawView);
        } else
            Glide.with(mContext).load("").placeholder(R.drawable.ic_user_comment_default).transform(new GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.Grey_1000))).into(drawView);
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
