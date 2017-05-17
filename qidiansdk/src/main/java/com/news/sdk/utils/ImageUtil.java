package com.news.sdk.utils;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.RequestManager;
import com.news.sdk.R;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.common.ThemeManager;

/**
 * Created by fiocca on 17/5/15.
 */

public class ImageUtil {
    public static void setRoundImage(Context context, RequestManager requestManager, ImageView view, String uri, int placeholder) {
        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
            view.setAlpha(0.5f);
        } else {
            view.setAlpha(1.0f);
        }
        if (!TextUtil.isEmptyString(uri)) {
            requestManager.load(Uri.parse(uri)).placeholder(placeholder).transform(new CommonViewHolder.GlideCircleTransform(context, 2, context.getResources().getColor(R.color.white))).into(view);
        } else {
            requestManager.load("").placeholder(placeholder).transform(new CommonViewHolder.GlideCircleTransform(context, 2, context.getResources().getColor(R.color.white))).into(view);
        }
    }

    public static void setAlphaImage(ImageView view, int drawable) {
        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
            view.setAlpha(0.5f);
        } else {
            view.setAlpha(1.0f);
        }
        view.setImageResource(drawable);

    }

    public static void setAlphaView(View view) {
        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
            view.setAlpha(0.5f);
        } else {
            view.setAlpha(1.0f);
        }
    }

    public static void setAlphaProgressBar(ProgressBar progressBar) {
        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
            progressBar.setAlpha(0.5f);
        } else {
            progressBar.setAlpha(1.0f);
        }
    }
}
