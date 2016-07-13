package com.news.yazhidao.listener;

import com.news.yazhidao.entity.NewsDetailAdd;

/**
 * Created by fengjigang on 15/6/4.
 * 上传用户评论接口回调
 */
public interface UploadCommentListener {
    void success(NewsDetailAdd.Point result);
    void failed();
}
