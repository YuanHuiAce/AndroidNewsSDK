package com.news.yazhidao.widget;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

/**
 * Created by Berkeley on 5/13/15.
 * 设置字体样式，背景色，大小工具类
 */
public class SpannableStringUtil {

    private static StringBuilder builder;

    /**
     * 设置前景色
     * @param originStr 原始字符串
     * @param start 开始索引
     * @param end 结束索引
     */
    public static String setForegroundColorSpan(String originStr, int start, int end) {

        generateBuilder(originStr);

        SpannableString spanString = new SpannableString(builder.toString());
        ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
        spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString.toString();
    }

    /**
     * 设置背景色
     * @param originStr 原始字符
     * @param start 开始索引
     * @param end 结束索引
     */
    public static String setBackgroundColorSpan(String originStr, int start, int end) {

        generateBuilder(originStr);

        SpannableString spanString = new SpannableString(builder.toString());
        BackgroundColorSpan span = new BackgroundColorSpan(Color.YELLOW);
        spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString.toString();
    }

    /**
     * 设置字体样式
     * @param originStr 原始字符
     * @param start 开始索引
     * @param end 结束索引
     */
    public static String setStyleSpan(String originStr, int start, int end) {

        generateBuilder(originStr);

        SpannableString spanString = new SpannableString(builder.toString());
        StyleSpan span = new StyleSpan(Typeface.BOLD_ITALIC);
        spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString.toString();
    }

    /**
     * 设置字体的相对大小
     * @param originStr 原始字符
     * @param start 开始索引
     * @param end 结束索引
     */
    public static String setRelativeFontSpan(String originStr, int start, int end) {

        generateBuilder(originStr);

        SpannableString spanString = new SpannableString(builder.toString());
        spanString.setSpan(new RelativeSizeSpan(2.5f), start,end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spanString.toString();
    }

    /**
     * 设置字体的绝对大小
     * @param originStr 原始字符
     * @param size 字体大小
     * @param start 开始索引
     * @param end 结束索引
     */
    public static SpannableString setAbsoluteFontSpan(String originStr, int size, int start, int end) {

        generateBuilder(originStr);

        SpannableString spannableString = new SpannableString(builder.toString());
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(size);
        spannableString.setSpan(absoluteSizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private static void generateBuilder(String originalText){
        builder = new StringBuilder();
        for(int i = 0; i < originalText.length(); i++) {
            String c = String.valueOf(originalText.charAt(i));
            builder.append(c);
        }

    }
}
