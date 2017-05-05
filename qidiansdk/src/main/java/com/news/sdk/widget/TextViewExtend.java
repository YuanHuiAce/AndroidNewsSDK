package com.news.sdk.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Hashtable;

public class TextViewExtend extends TextView {
    private static final String TAG = "TextViewExtend";
    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();
    public TextViewExtend(Context context) {
        this(context,null);
    }

    public TextViewExtend(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextViewExtend(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setCustomFont(context);
    }

//    private void setCustomFont(Context ctx) {
//        String _KeyFontName=ZipperUtil.getSaveFontPath(ctx).toString();
//        Typeface tf = fontCache.get(_KeyFontName);
//        if(tf == null) {
//            try {
//                tf = Typeface.createFromFile(_KeyFontName);
//            }
//            catch (Exception e) {
//                Logger.e(TAG,e.getMessage());
//            }
//            if(tf==null){
//                return;
//            }
//            fontCache.put(_KeyFontName, tf);
//        }
//        setTypeface(tf);
//    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if(style== Typeface.BOLD){
            getPaint().setFakeBoldText(true);
        }
    }
}