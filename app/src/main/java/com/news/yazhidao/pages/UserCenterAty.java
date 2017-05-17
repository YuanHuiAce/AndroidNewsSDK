package com.news.yazhidao.pages;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.entity.User;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.yazhidao.R;


public class UserCenterAty extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1008;

    private View mSettingLeftBack, mCenterComment, mCenterFavorite, mCenterMessage, mCenterSetting;
    private ImageView mCenterUserIcon;
    private TextView mCenterUserName;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_user_center);
    }

    @Override
    protected void initializeViews() {
        mSettingLeftBack = findViewById(R.id.mSettingLeftBack);
        mSettingLeftBack.setOnClickListener(this);
        mCenterUserIcon = (ImageView) findViewById(R.id.mCenterUserIcon);
        mCenterUserName = (TextView) findViewById(R.id.mCenterUserName);
        mCenterComment = findViewById(R.id.mCenterComment);
        mCenterComment.setOnClickListener(this);
        mCenterFavorite = findViewById(R.id.mCenterFavorite);
        mCenterFavorite.setOnClickListener(this);
        mCenterMessage = findViewById(R.id.mCenterMessage);
        mCenterMessage.setOnClickListener(this);
        mCenterSetting = findViewById(R.id.mCenterSetting);
        mCenterSetting.setOnClickListener(this);
        Glide.with(UserCenterAty.this).load(R.drawable.ic_user_comment_default).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(UserCenterAty.this, 5, getResources().getColor(R.color.white))).into(mCenterUserIcon);
    }

    @Override
    protected boolean isNeedAnimation() {
        return true;
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
            case R.id.mCenterSetting:
                LogUtil.userActionLog(this, CommonConstant.LOG_ATYPE_MYSETTING, CommonConstant.LOG_PAGE_USERCENTERPAGE, CommonConstant.LOG_PAGE_SETTINGPAGE, null, false);
                Intent settingAty = new Intent(this, SettingAty.class);
                startActivityForResult(settingAty, REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onThemeChanged() {

    }
}
