package com.news.yazhidao.listener;

import android.content.Intent;

/**
 * 倒计时结束后，更新界面接口
 * Created by fiocca on 15/4/29.
 */
public interface TimeOutAlarmUpdateListener {
    void updateUI(Intent intent);
}
