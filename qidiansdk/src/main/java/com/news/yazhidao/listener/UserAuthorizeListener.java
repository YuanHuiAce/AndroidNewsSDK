package com.news.yazhidao.listener;

import com.news.yazhidao.entity.User;

/**
 * Created by fengjigang on 16/4/6.
 * 用户授权登录回调
 */
public interface UserAuthorizeListener {
    void success(User user);
    void failure(String message);
    void cancel();
}
