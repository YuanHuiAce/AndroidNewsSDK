package com.news.yazhidao.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.news.yazhidao.entity.VideoChannel;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/10/28.
 * 新闻频道操作dao
 */
public class VideoChannelDao {
    private static final String TAG = "VideoChannelDao";
    /**
     * 频道排序id
     */
    private static final String COLUMN_ORDERID = "order_num";
    private static final String COLUMN_SELECTED = "selected";
    private final Context mContext;
    private Dao<VideoChannel, String> mVideoChannelDao;

    public VideoChannelDao(Context pContext) {
        this.mContext = pContext;
        try {
            mVideoChannelDao = DatabaseHelper.getHelper(mContext).getDao(VideoChannel.class);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, VideoChannel.class + " dao create failure >>>" + e.getMessage());
        }
    }

    /**
     * 插入一个新闻频道
     *
     * @param pItem 新闻频道对象
     */
    public void insert(VideoChannel pItem) {
        try {
            mVideoChannelDao.create(pItem);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "insert " + VideoChannel.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 插入一个新闻频道集合
     * @param pItems
     */
    public void insertList(List<VideoChannel> pItems){
        if (!TextUtil.isListEmpty(pItems)){
            for (VideoChannel item: pItems){
                insert(item);
            }
        }
    }

    /**
     * 删除一个新闻频道
     *
     * @param pItem
     */
    public void delete(VideoChannel pItem) {
        try {
            mVideoChannelDao.delete(pItem);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "delete " + VideoChannel.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 删除所有的新闻频道
     */
    public void deletaForAll() {
        try {
            List<VideoChannel> list = mVideoChannelDao.queryForAll();
            mVideoChannelDao.delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "delete for all " + VideoChannel.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 查询所有的新闻频道信息,并根据orderId 来排列
     *
     * @return
     */
    public ArrayList<VideoChannel> queryForAll() {
        QueryBuilder<VideoChannel, String> builder = mVideoChannelDao.queryBuilder();
        builder.orderBy(COLUMN_ORDERID, true);
        try {
            List<VideoChannel> list = builder.query();
            if (!TextUtil.isListEmpty(list)) {
                return new ArrayList<>(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private ArrayList<VideoChannel> queryForSelected(boolean isSelected){
        try{
            QueryBuilder<VideoChannel, String> builder = mVideoChannelDao.queryBuilder();
            builder.where().eq(COLUMN_SELECTED, isSelected);
            builder.orderBy(COLUMN_ORDERID, true);
            List<VideoChannel> list = builder.query();
            if (!TextUtil.isListEmpty(list)){
                return new ArrayList<>(list);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    /**
     * 查询用户已经选择的新闻频道
     * @return
     */
    public ArrayList<VideoChannel> queryForSelected(){
        return queryForSelected(true);
    }
    /**
     * 查询用户未选择的新闻频道
     * @return
     */
    public ArrayList<VideoChannel> queryForNormal(){
        return queryForSelected(false);
    }
    /**
     * 更新一个新闻频道
     *
     * @param pItem 修改后的频道对象
     */
    public void update(VideoChannel pItem) {
        try {
            mVideoChannelDao.createOrUpdate(pItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
