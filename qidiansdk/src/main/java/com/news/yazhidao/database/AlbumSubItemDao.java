package com.news.yazhidao.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/8/12.
 * 挖掘新闻操作Dao
 */
public class AlbumSubItemDao {
    private static final String TAG = "AlbumSubItemDao";
    /**搜索新闻的title列名*/
    public static final String COLUMN_SEARCH_TITLE = "search_key";
    /**搜索新闻的url列名*/
    public static final String COLUMN_SEARCH_URL = "search_url";
    /**是否上传成功列名*/
    public static final String COLUMN_IS_UPLOADED = "is_uploaded";

    private Context mContext;
    private Dao<AlbumSubItem, String> mAlbumSubItemDao;
    private DatabaseHelper mDbHelper;
    public AlbumSubItemDao(Context pContext){
        this.mContext = pContext;
        mDbHelper = DatabaseHelper.getHelper(pContext);
        try {
            mAlbumSubItemDao = mDbHelper.getDao(AlbumSubItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 插入一条专辑数据
     * @param pAlbumSubItem 挖掘新闻对象
     */
    public void insert(AlbumSubItem pAlbumSubItem){
        try {
            if (queryByTitleAndUrl(pAlbumSubItem.getSearch_key(),pAlbumSubItem.getSearch_url())==null){
                mAlbumSubItemDao.create(pAlbumSubItem);
                Logger.e(TAG, "insert " + AlbumSubItem.class.getSimpleName() + " success >>>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "insert " + AlbumSubItem.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }
    /**
     * 向专辑里面插入一个集合数据
     * @param pItemList 挖掘新闻对象
     */
    public void insertList(ArrayList<AlbumSubItem> pItemList){
        try {
            if (!TextUtil.isListEmpty(pItemList)){
                for (AlbumSubItem item: pItemList){
                    insert(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "insert " + AlbumSubItem.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 按照搜索的title和url来查询指定的挖掘新闻对象
     * @param title
     * @param url
     * @return
     */
    public AlbumSubItem queryByTitleAndUrl(String title, String url){
        QueryBuilder<AlbumSubItem, String> builder = mAlbumSubItemDao.queryBuilder();
        Where<AlbumSubItem, String> where = builder.where();
        try {
            where.eq(COLUMN_SEARCH_TITLE,title).and().eq(COLUMN_SEARCH_URL, url);
            List<AlbumSubItem> albumSubItems = builder.query();
            if (!TextUtil.isListEmpty(albumSubItems)){
                return albumSubItems.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "queryByTitleAndUrl " + AlbumSubItem.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
        return null;
    }


    /**
     * 更新挖掘新闻信息
     * @param pAlbumSubItem 要修改的挖掘新闻对象
     */
    public void update(AlbumSubItem pAlbumSubItem){
        if (pAlbumSubItem != null){
            AlbumSubItem existItem = queryByTitleAndUrl(pAlbumSubItem.getSearch_key(), pAlbumSubItem.getSearch_url());
            if(existItem != null) {
                existItem.setInserteId(pAlbumSubItem.getInserteId());
                existItem.setStatus(pAlbumSubItem.getStatus());
                existItem.setDetailForDigger(pAlbumSubItem.getDetailForDigger());
                existItem.setImg(pAlbumSubItem.getImg());
                existItem.setDiggerAlbum(pAlbumSubItem.getDiggerAlbum());
                    try {
                        mAlbumSubItemDao.createOrUpdate(pAlbumSubItem);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Logger.e(TAG, "pAlbumSubItem update " + AlbumSubItem.class.getSimpleName() + " failure >>>" + e.getMessage());
                    }
            }
        }
    }

    /**
     * 根据专辑id获取其所包含的挖掘信息
     * @param pAlbumId
     * @return
     */
    public ArrayList<AlbumSubItem> queryByAlbumId(String pAlbumId){
        List subItems = new ArrayList();
        try {
            QueryBuilder<AlbumSubItem, String> builder = mAlbumSubItemDao.queryBuilder();
            builder.where().eq(AlbumSubItem.COLUMN_ALBUM_ID, pAlbumId);
            builder.orderBy(AlbumSubItem.COLUMN_CREATE_TIME,false);
            subItems = builder.query();
            if (TextUtil.isListEmpty(subItems)){
                return new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "queryByAlbumId " + AlbumSubItem.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
        return new ArrayList<AlbumSubItem>(subItems);
    }

    /**
     * 获取所有的挖掘信息
     * @return
     */
    public ArrayList<AlbumSubItem> queryForAll(){
        List subItems = new ArrayList();
        try {
            subItems = mAlbumSubItemDao.queryForAll();
            if (TextUtil.isListEmpty(subItems)){
                return new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "queryForAll " + AlbumSubItem.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
        return new ArrayList<AlbumSubItem>(subItems);
    }
    /**
     * 查询没有上传成功的挖掘新闻集合
     * @return
     */
    public ArrayList<AlbumSubItem> queryNotUpload(){
        List subItems = new ArrayList();
        try {
            subItems = mAlbumSubItemDao.queryForEq(COLUMN_IS_UPLOADED, "0");
            if (TextUtil.isListEmpty(subItems)){
                return new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<AlbumSubItem>(subItems);
    }

    public int executeRaw(String sql){
        try {
            return mAlbumSubItemDao.executeRaw(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "executeRaw " + AlbumSubItem.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
        return -1;
    }
}
