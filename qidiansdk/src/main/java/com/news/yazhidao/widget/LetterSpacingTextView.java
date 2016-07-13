package com.news.yazhidao.widget;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fengjigang on 15/4/10.
 */
public class LetterSpacingTextView extends TextViewExtend {
    public final static float NORMAL = 0;
    public final static float NORMALBIG = 1f;
    public final static float BIG = 1.75f;
    public final static float BIGGEST = 4f;
    private float letterSpacing = BIGGEST;
    private CharSequence originalText = "";


    public LetterSpacingTextView(Context context) {
        super(context);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        originalText = super.getText();
        applyLetterSpacing();
        this.invalidate();
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }
    public void setFontSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
        applyLetterSpacing();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        applyLetterSpacing();
    }

    @Override
    public CharSequence getText() {
        return originalText;
    }

    private void applyLetterSpacing() {
        if (this == null || this.originalText == null) return;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < originalText.length(); i++) {
            String c = String.valueOf(originalText.charAt(i));
            builder.append(c);
            if (i + 1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if(builder.toString().length() > 1) {
            for(int i = 1; i < builder.toString().length(); i+=2) {
                finalText.setSpan(new ScaleXSpan((letterSpacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        super.setText(finalText, BufferType.SPANNABLE);
    }


    //工具类：判断是否是字母或者数字
    public boolean isNumOrLetters(String str)
    {
        String regEx="^[A-Za-z0-9_]+$";
        Pattern p= Pattern.compile(regEx);
        Matcher m=p.matcher(str);
        return m.matches();
    }
}
