package com.news.sdk.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.news.sdk.entity.ChannelItem;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.TextUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/10/28.
 * 新闻频道操作dao
 */
public class ChannelItemDao {
    private static final String TAG = "ChannelItemDao";
    /**
     * 频道排序id
     */
    private static final String COLUMN_ORDERID = "orderId";
    /**
     * 用户是否选择了该频道
     */
    private static final String COLUMN_SELECTED = "selected";
    private static final String COLUMN_STATE = "state";
    private final Context mContext;
    private Dao<ChannelItem, String> mChannelItemDao;

    public ChannelItemDao(Context pContext) {
        this.mContext = pContext;
        try {
            mChannelItemDao = DatabaseHelper.getHelper(mContext).getDao(ChannelItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, ChannelItem.class + " dao create failure >>>" + e.getMessage());
        }
    }

    /**
     * 插入一个新闻频道
     *
     * @param pItem 新闻频道对象
     */
    public void insert(ChannelItem pItem) {
        try {
            mChannelItemDao.create(pItem);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "insert " + ChannelItem.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 插入一个新闻频道集合
     *
     * @param pItems
     */
    public void insertList(List<ChannelItem> pItems) {
        if (!TextUtil.isListEmpty(pItems)) {
            for (ChannelItem item : pItems) {
                insert(item);
            }
        }
    }

    /**
     * 插入用户选择的新闻频道集合
     *
     * @param pItems 新闻频道集合
     */
    public void insertSelectedList(List<ChannelItem> pItems) {
        if (!TextUtil.isListEmpty(pItems)) {
            for (int i = 0; i < pItems.size(); i++) {
                ChannelItem channelItem = pItems.get(i);
                channelItem.setOrderId(i);
                channelItem.setSelected(true);
                insert(channelItem);
            }
        }
    }

    /**
     * 插入用户未选择新闻频道集合
     *
     * @param pItems 新闻频道集合
     */
    public void insertNormalList(List<ChannelItem> pItems) {
        if (!TextUtil.isListEmpty(pItems)) {
            for (int i = 0; i < pItems.size(); i++) {
                ChannelItem channelItem = pItems.get(i);
                channelItem.setOrderId(i);
                channelItem.setSelected(false);
                insert(channelItem);
            }
        }
    }

    /**
     * 删除一个新闻频道
     *
     * @param pItem
     */
    public void delete(ChannelItem pItem) {
        try {
            mChannelItemDao.delete(pItem);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "delete " + ChannelItem.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 删除所有的新闻频道
     */
    public void deletaForAll() {
        try {
            List<ChannelItem> list = mChannelItemDao.queryForAll();
            mChannelItemDao.delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "delete for all " + ChannelItem.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 查询所有的新闻频道信息,并根据orderId 来排列
     *
     * @return
     */
    public ArrayList<ChannelItem> queryForAll() {
        QueryBuilder<ChannelItem, String> builder = mChannelItemDao.queryBuilder();
        builder.orderBy(COLUMN_ORDERID, true);
        try {
            List<ChannelItem> list = builder.query();
            if (!TextUtil.isListEmpty(list)) {
                return new ArrayList<>(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private ArrayList<ChannelItem> queryForSelected(boolean isSelected) {
        try {
            QueryBuilder<ChannelItem, String> builder = mChannelItemDao.queryBuilder();
            builder.where().eq(COLUMN_SELECTED, isSelected);
            builder.orderBy(COLUMN_ORDERID, true);
            List<ChannelItem> list = builder.query();
            if (!TextUtil.isListEmpty(list)) {
                return new ArrayList<>(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 查询用户已经选择的新闻频道
     *
     * @return
     */
    public ArrayList<ChannelItem> queryForSelected() {
        return queryForSelected(true);
    }

    /**
     * 查询用户未选择的新闻频道
     *
     * @return
     */
    public ArrayList<ChannelItem> queryForNormal() {
        return queryForSelected(false);
    }

    /**
     * 更新一个新闻频道
     *
     * @param pItem 修改后的频道对象
     */
    public void update(ChannelItem pItem) {
        try {
            mChannelItemDao.createOrUpdate(pItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
