package com.news.yazhidao.listener;

import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.MyAppException;

/**
 * Created by fengjigang on 15/5/13.
 * 用户登录请求回调接口
 */
public interface UserLoginRequestListener {
    void success(User user);
    void failed(MyAppException exception);
}
