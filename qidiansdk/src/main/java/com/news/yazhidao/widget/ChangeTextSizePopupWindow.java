package com.news.yazhidao.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class ChangeTextSizePopupWindow extends PopupWindow {

    private ImageView mivClose,change_text_size_hintImage;
    private View mMenuView;
    private Activity mContext;
    private SeekBar mSeekBar;
    private SharedPreferences mSharedPreferences;
    private TextView text_success;
    int mTextSize;
    private boolean isDetailOpen;



    public ChangeTextSizePopupWindow(Activity context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_window_change_text_size, null);
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mTextSize = mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL);
        findHeadPortraitImageViews();
    }

    private void findHeadPortraitImageViews() {
        //防止被下面的虚拟键盘遮挡
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mSeekBar = (SeekBar) mMenuView.findViewById(R.id.change_text_size_bar);
        mivClose = (ImageView) mMenuView.findViewById(R.id.close_imageView);
        change_text_size_hintImage = (ImageView) mMenuView.findViewById(R.id.change_text_size_hintImage);
        text_success = (TextView) mMenuView.findViewById(R.id.text_success);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.DialogAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(mContext.getResources().getColor(R.color.half_black));
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        mMenuView.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    dismiss();
//                }
//                return true;
//            }
//        });
        mivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        switch (mTextSize) {
            case CommonConstant.TEXT_SIZE_NORMAL:
                mSeekBar.setProgress(0);
                break;
            case CommonConstant.TEXT_SIZE_BIG:
                mSeekBar.setProgress(50);
                break;
            case CommonConstant.TEXT_SIZE_BIGGER:
                mSeekBar.setProgress(100);
                break;
        }
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("tag", progress + "progress");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = mSeekBar.getProgress();
                Intent intent = new Intent();
                if (progress < 25) {
                    mSeekBar.setProgress(0);
//                    intent.putExtra("textSize", CommonConstant.TEXT_SIZE_NORMAL);
                    mSharedPreferences.edit().putInt("textSize", CommonConstant.TEXT_SIZE_NORMAL).commit();
                } else if (progress >= 25 && progress <= 75) {
                    mSeekBar.setProgress(50);
//                    intent.putExtra("textSize", CommonConstant.TEXT_SIZE_BIG);
                    mSharedPreferences.edit().putInt("textSize", CommonConstant.TEXT_SIZE_BIG).commit();
                } else {
                    mSeekBar.setProgress(100);
//                    intent.putExtra("textSize", CommonConstant.TEXT_SIZE_BIGGER);
                    mSharedPreferences.edit().putInt("textSize", CommonConstant.TEXT_SIZE_BIGGER).commit();
                }
                intent.setAction(CommonConstant.CHANGE_TEXT_ACTION);
                mContext.sendBroadcast(intent);
            }
        });
    }
    public void isDeteilOpen(){
        isDetailOpen = true;
        change_text_size_hintImage.setVisibility(View.GONE);
        mivClose.setVisibility(View.GONE);
        text_success.setVisibility(View.VISIBLE);
        text_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
