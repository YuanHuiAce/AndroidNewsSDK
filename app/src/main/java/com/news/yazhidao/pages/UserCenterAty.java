package com.news.yazhidao.pages;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.entity.User;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.yazhidao.R;


public class UserCenterAty extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1008;
    private View mLine1, mLine2, mLine3, mLine4;
    private View mHeaderDivider, mCenterComment, mCenterFavorite, mCenterMessage;
    private RelativeLayout bgLayout, mCenterHeader;
    private ImageView mSettingLeftBack, mCenterUserIcon;
    private ImageView mCenterCommentIcon, mCenterFavoriteIcon, mCenterMessageIcon;
    private TextView mTitle1, mTitle2, mTitle3;
    private TextView mSetting, mTitle;
    private TextView mCenterUserName;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_user_center);
    }

    @Override
    protected void initializeViews() {
        mSettingLeftBack = (ImageView) findViewById(R.id.mSettingLeftBack);
        mSettingLeftBack.setOnClickListener(this);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mCenterHeader = (RelativeLayout) findViewById(R.id.mCenterHeader);
        mHeaderDivider = findViewById(R.id.mHeaderDivider);
        mCenterUserIcon = (ImageView) findViewById(R.id.mCenterUserIcon);
        mSetting = (TextView) findViewById(R.id.mSetting);
        mSetting.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.mTitle);
        mTitle1 = (TextView) findViewById(R.id.mTitle1);
        mTitle2 = (TextView) findViewById(R.id.mTitle2);
        mTitle3 = (TextView) findViewById(R.id.mTitle3);
        mCenterCommentIcon = (ImageView) findViewById(R.id.mCenterCommentIcon);
        mCenterFavoriteIcon = (ImageView) findViewById(R.id.mCenterFavoriteIcon);
        mCenterMessageIcon = (ImageView) findViewById(R.id.mCenterMessageIcon);
        mCenterUserName = (TextView) findViewById(R.id.mCenterUserName);
        mCenterComment = findViewById(R.id.mCenterComment);
        mCenterComment.setOnClickListener(this);
        mCenterFavorite = findViewById(R.id.mCenterFavorite);
        mCenterFavorite.setOnClickListener(this);
        mCenterMessage = findViewById(R.id.mCenterMessage);
        mCenterMessage.setOnClickListener(this);
        mLine1 = findViewById(R.id.mLine1);
        mLine2 = findViewById(R.id.mLine2);
        mLine3 = findViewById(R.id.mLine3);
        mLine4 = findViewById(R.id.mLine4);
        Glide.with(UserCenterAty.this).load("").placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(UserCenterAty.this, 5, getResources().getColor(R.color.white))).into(mCenterUserIcon);
        setTheme();
    }

    private void setTheme() {
        TextUtil.setLayoutBgResource(this, mSettingLeftBack, R.drawable.bg_left_back_selector);
        TextUtil.setImageResource(this, mSettingLeftBack, R.drawable.btn_left_back);
        TextUtil.setLayoutBgResource(this, mSetting, R.drawable.bg_more_selector);
        TextUtil.setLayoutBgResource(this, bgLayout, R.color.color6);
        TextUtil.setLayoutBgResource(this, mCenterHeader, R.color.color6);
        TextUtil.setLayoutBgResource(this, mHeaderDivider, R.color.color5);
        TextUtil.setLayoutBgResource(this, mCenterComment, R.color.color9);
        TextUtil.setLayoutBgResource(this, mCenterFavorite, R.color.color9);
        TextUtil.setLayoutBgResource(this, mCenterMessage, R.color.color9);
        TextUtil.setLayoutBgResource(this, mLine1, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLine2, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLine3, R.color.color5);
        TextUtil.setLayoutBgResource(this, mLine4, R.color.color5);
        TextUtil.setImageResource(this, mCenterCommentIcon, R.mipmap.ic_user_center_comment);
        TextUtil.setImageResource(this, mCenterFavoriteIcon, R.mipmap.ic_user_center_favorite);
        TextUtil.setImageResource(this, mCenterMessageIcon, R.mipmap.ic_user_center_message);
        TextUtil.setTextColor(this, mTitle, R.color.color2);
        TextUtil.setTextColor(this, mTitle1, R.color.color2);
        TextUtil.setTextColor(this, mTitle2, R.color.color2);
        TextUtil.setTextColor(this, mTitle3, R.color.color2);
        TextUtil.setTextColor(this, mSetting, R.color.color2);
        TextUtil.setTextColor(this, mCenterUserName, R.color.color2);
        ImageUtil.setAlphaImage(mCenterUserIcon);
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    protected void loadData() {
        User user = SharedPreManager.mInstance(this).getUser(this);
        if (user != null && !user.isVisitor()) {
            String uri = user.getUserIcon();
            if (!TextUtil.isEmptyString(uri)) {
                Glide.with(UserCenterAty.this).load(Uri.parse(uri)).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(UserCenterAty.this, 5, getResources().getColor(R.color.white))).into(mCenterUserIcon);
            }
            String userName = user.getUserName();
            if (!TextUtil.isEmptyString(userName)) {
                mCenterUserName.setText(userName);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SettingAty.RESULT_CODE) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSettingLeftBack:
                finish();
                break;
            case R.id.mCenterComment:
                LogUtil.userActionLog(this, CommonConstant.LOG_ATYPE_MYCOMMENTS, CommonConstant.LOG_PAGE_USERCENTERPAGE, CommonConstant.LOG_PAGE_MYCOMMENTPAGE, null, true);
                Intent myCommentAty = new Intent(this, MyCommentAty.class);
                startActivity(myCommentAty);
                break;
            case R.id.mCenterFavorite:
                LogUtil.userActionLog(this, CommonConstant.LOG_ATYPE_MYCOLLECTIONS, CommonConstant.LOG_PAGE_USERCENTERPAGE, CommonConstant.LOG_PAGE_MYCOLLECTIONPAGE, null, true);
                Intent myFavoriteAty = new Intent(this, MyFavoriteAty.class);
                startActivity(myFavoriteAty);
                break;
            case R.id.mCenterMessage:
                LogUtil.userActionLog(this, CommonConstant.LOG_ATYPE_MYMESSAGES, CommonConstant.LOG_PAGE_USERCENTERPAGE, CommonConstant.LOG_PAGE_MYMESSAGEPAGE, null, true);
                Intent myMessageAty = new Intent(this, MyMessageAty.class);
                startActivity(myMessageAty);
                break;
            case R.id.mSetting:
                LogUtil.userActionLog(this, CommonConstant.LOG_ATYPE_MYSETTING, CommonConstant.LOG_PAGE_USERCENTERPAGE, CommonConstant.LOG_PAGE_SETTINGPAGE, null, false);
                Intent settingAty = new Intent(this, SettingAty.class);
                startActivityForResult(settingAty, REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onThemeChanged() {
        setTheme();
    }
}
