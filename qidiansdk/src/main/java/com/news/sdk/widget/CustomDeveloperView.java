package com.news.sdk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.sdk.R;
import com.news.sdk.utils.TextUtil;


/**
 * Created by Berkeley on 6/21/17.
 */

public class CustomDeveloperView extends RelativeLayout {

    private View mDeveloperTag;
    private TextView mDeveloperDes;
    private TextView mDeveloperInfo;
    private TypedArray attributes;
    private View mline;

    public CustomDeveloperView(Context context) {
        super(context);
        init();
    }


    public CustomDeveloperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomDeveloperView);
        init();
    }

    public CustomDeveloperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_developer, this, true);
        mDeveloperTag = findViewById(R.id.developer_tag);
        mDeveloperDes = (TextView) findViewById(R.id.tv_developer_des);
        mline = findViewById(R.id.custom_developer_line);
        mDeveloperInfo = (TextView) findViewById(R.id.tv_developer_info);
        TextUtil.setTextColor(getContext(),mDeveloperDes,R.color.color2);
        TextUtil.setTextColor(getContext(),mDeveloperInfo,R.color.color3);
        TextUtil.setLayoutBgResource(getContext(),mline,R.color.color5);
        if (attributes != null) {
            boolean tagVisible = attributes.getBoolean(R.styleable.CustomDeveloperView_cdvTagVisible, false);
            if (tagVisible) {
                mDeveloperTag.setVisibility(View.VISIBLE);
            } else {
                mDeveloperTag.setVisibility(View.GONE);
            }

            String mDesText = attributes.getString(R.styleable.CustomDeveloperView_cdvDesText);
            if (!TextUtils.isEmpty(mDesText)) {
                mDeveloperDes.setText(mDesText);
                int mDesTextColor = attributes.getColor(R.styleable.CustomDeveloperView_cdvDesTextColor, getResources().getColor(R.color.color2));
                mDeveloperDes.setTextColor(mDesTextColor);
                float mDesTextSize = attributes.getDimension(R.styleable.CustomDeveloperView_cdvDesTextSize, 15);
                mDeveloperDes.setTextSize(TypedValue.COMPLEX_UNIT_SP,mDesTextSize);
            }
            String mInfoText = attributes.getString(R.styleable.CustomDeveloperView_cdvInfoText);
            if (!TextUtils.isEmpty(mInfoText)) {
                mDeveloperInfo.setText(mDesText);
                int mInfoTextColor = attributes.getColor(R.styleable.CustomDeveloperView_cdvInfoTextColor, getResources().getColor(R.color.color3));
                mDeveloperInfo.setTextColor(mInfoTextColor);
                float mDesTextSize = attributes.getDimension(R.styleable.CustomDeveloperView_cdvInfoTextSize, 15);
                mDeveloperInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP,mDesTextSize);

            }

            attributes.recycle();
        }




    }

    public void setDesText(CharSequence destr)
    {
        mDeveloperDes.setText(destr);
    }

    public void setInfoText(CharSequence infoStr)
    {
        mDeveloperInfo.setText(infoStr);
    }
}
