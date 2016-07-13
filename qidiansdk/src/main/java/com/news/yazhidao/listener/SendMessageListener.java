package com.news.yazhidao.listener;

import com.news.yazhidao.net.MyAppException;

/**
 * Created by fiocca on 15/5/15.
 */
public interface SendMessageListener {
     void success(String result);

     void failed(MyAppException exception);
}
