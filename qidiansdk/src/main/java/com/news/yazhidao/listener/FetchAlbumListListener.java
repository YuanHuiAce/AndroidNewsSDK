package com.news.yazhidao.listener;

import com.news.yazhidao.entity.DiggerAlbum;

import java.util.ArrayList;

/**
 * Created by fengjigang on 15/8/5.
 * 获取专辑列表回调接口
 */
public interface FetchAlbumListListener {
    void success(ArrayList<DiggerAlbum> resultList);
    void failure();
}
