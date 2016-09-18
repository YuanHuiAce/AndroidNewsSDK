package com.news.yazhidao.database;

/**
 * Created by fengjigang on 15/8/10.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "yazhidao_news.db";
    private static int DATABASE_VERSION = 26;
    private HashMap<String, Dao> mDaos;
    private Context mContext;
    private ArrayList<ChannelItem> oldChannelItems;
    private ChannelItemDao channelDao;

    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mDaos = new HashMap<>();
        Logger.e("jigang", "DatabaseHelper()");
    }

    private static ArrayList<ChannelItem> mChannels = new ArrayList<>();

    static {
        /**默认用户选择的频道*/
        mChannels.add(new ChannelItem("1", "推荐", 1, true));
        mChannels.add(new ChannelItem("3", "娱乐", 2, true));
        mChannels.add(new ChannelItem("6", "体育", 3, true));
        mChannels.add(new ChannelItem("5", "汽车", 4, true));
        mChannels.add(new ChannelItem("21", "搞笑", 5, true));
        mChannels.add(new ChannelItem("26", "美女", 6, true));
        mChannels.add(new ChannelItem("2", "社会", 7, true));
        mChannels.add(new ChannelItem("4", "科技", 8, true));
        mChannels.add(new ChannelItem("7", "财经", 9, true));
        mChannels.add(new ChannelItem("22", "互联网", 10, true));
        mChannels.add(new ChannelItem("8", "军事", 11, true));
        mChannels.add(new ChannelItem("11", "游戏", 12, true));
        mChannels.add(new ChannelItem("30", "影视", 13, true));
        mChannels.add(new ChannelItem("23", "趣图", 14, true));
        mChannels.add(new ChannelItem("9", "国际", 15, true));
        mChannels.add(new ChannelItem("10", "时尚", 16, true));
        /**默认用户未选择的频道,并可选添加*/
        mChannels.add(new ChannelItem("31", "奇闻", 1, false));
        mChannels.add(new ChannelItem("12", "旅游", 2, false));
        mChannels.add(new ChannelItem("39", "帅哥", 3, false));
        mChannels.add(new ChannelItem("24", "健康", 4, false));
        mChannels.add(new ChannelItem("15", "美食", 5, false));
        mChannels.add(new ChannelItem("20", "股票", 6, false));
        mChannels.add(new ChannelItem("25", "科学", 7, false));
        mChannels.add(new ChannelItem("19", "美文", 8, false));
        mChannels.add(new ChannelItem("17", "养生", 9, false));
        mChannels.add(new ChannelItem("32", "萌宠", 10, false));
        mChannels.add(new ChannelItem("37", "风水玄学", 11, false));
        mChannels.add(new ChannelItem("13", "历史", 12, false));
        mChannels.add(new ChannelItem("16", "育儿", 13, false));
        mChannels.add(new ChannelItem("14", "探索", 14, false));
        mChannels.add(new ChannelItem("29", "外媒", 15, false));
        mChannels.add(new ChannelItem("36", "自媒体", 16, false));
        mChannels.add(new ChannelItem("35", "APP", 17, false));
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {

            TableUtils.createTableIfNotExists(connectionSource, ChannelItem.class);
            TableUtils.createTableIfNotExists(connectionSource, NewsFeed.class);
            TableUtils.createTableIfNotExists(connectionSource, NewsDetailComment.class);
            /**初始化数据库或者升级数据库的时候,插入默认值*/
            channelDao = new ChannelItemDao(mContext);
            if (!TextUtil.isListEmpty(oldChannelItems)) {
                channelDao.insertList(oldChannelItems);
            } else {
                channelDao.insertList(mChannels);
            }
            Logger.e("jigang", "DatabaseHelper  onCreate()");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            /***查询数据库升级前的频道列表*/
            ChannelItemDao channelDao = new ChannelItemDao(mContext);
            oldChannelItems = channelDao.queryForAll();
            //删除所有老版本上的频道
            if (oldVersion <= DATABASE_VERSION) {
                oldChannelItems.clear();
            }
            /**在feed流表中添加 isRead(用户是否阅读过该新闻)</> 字段*/
            NewsFeedDao newsFeedDao = new NewsFeedDao(mContext);
            if (oldVersion <= 25) {
                newsFeedDao.executeRaw("ALTER TABLE `tb_news_feed` ADD COLUMN isRead BOOLEAN;");
                newsFeedDao.executeRaw("ALTER TABLE `tb_news_feed` ADD COLUMN rtype INTEGER;");
            }

            TableUtils.dropTable(connectionSource, ChannelItem.class, true);
            TableUtils.dropTable(connectionSource, NewsFeed.class, true);
            TableUtils.dropTable(connectionSource, NewsDetailComment.class, true);
            onCreate(database, connectionSource);
            Logger.e("jigang", "DatabaseHelper  onUpgrade()");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }


    /**
     * 按Class 获取Dao对象
     *
     * @param clazz
     * @return
     * @throws SQLException
     */
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (mDaos.containsKey(className)) {
            dao = mDaos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            mDaos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : mDaos.keySet()) {
            Dao dao = mDaos.get(key);
            dao = null;
        }
    }


}

