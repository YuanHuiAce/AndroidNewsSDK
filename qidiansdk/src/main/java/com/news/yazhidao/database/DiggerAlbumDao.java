package com.news.yazhidao.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/8/11.
 * 专辑表操作Dao
 */
public class DiggerAlbumDao {
    private static final String TAG = "DiggerAlbunDao";
    /**是否上传成功列名*/
    public static final String COLUMN_IS_UPLOADED = "is_uploaded";
    /**专辑描述列名*/
    public static final String COLUMN_ALBUM_DES = "album_des";
    /**专辑标题列名*/
    public static final String COLUMN_ALBUM_TITLE = "album_title";

    private Context mContext;
    private Dao<DiggerAlbum, String> mDiggerAlbumDao;
    private DatabaseHelper mDbHelper;
    public DiggerAlbumDao(Context pContext){
        mContext = pContext;
        mDbHelper = DatabaseHelper.getHelper(pContext);
        try {
            mDiggerAlbumDao  = mDbHelper.getDao(DiggerAlbum.class);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG,"dao failure >>>"+e.getMessage());
        }
    }

    /**
     * 插入一条专辑数据
     * @param pDiggerAlbum 专辑对象
     */
    public void insert(DiggerAlbum pDiggerAlbum){
        try {
            mDiggerAlbumDao.create(pDiggerAlbum);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "insert " + DiggerAlbum.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 插入一个集合专辑数据
     * @param pAlbumList 专辑对象集合
     */
    public void insertList(List<DiggerAlbum> pAlbumList){
        try {
            if (!TextUtil.isListEmpty(pAlbumList)){
                for (DiggerAlbum album : pAlbumList){
                    insert(album);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "insert " + DiggerAlbum.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 按照专辑id 来查询指定专辑
     * @param id 专辑id
     * @return 指定id的专辑对象
     */
    public DiggerAlbum queryById(String id){
        try {
            return  mDiggerAlbumDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "queryById " + DiggerAlbum.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
        return null;
    }

    /**
     * 查询到所有的专辑
     * @return 专辑ArrayList 集合
     */
    public ArrayList<DiggerAlbum> querForAll(){
        List list = new ArrayList();
        try {
            list = mDiggerAlbumDao.queryForAll();
            if (TextUtil.isListEmpty(list)){
                return new ArrayList<DiggerAlbum>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<DiggerAlbum>(list);
    }

    /**
     * 更新专辑信息
     * @param pDiggerAlbum 要修改的专辑对象
     */
    public void update(DiggerAlbum pDiggerAlbum){
        if (pDiggerAlbum != null){
            DiggerAlbum existAlbum = queryById(pDiggerAlbum.getAlbum_id());
            if (existAlbum != null){
                try {
                    mDiggerAlbumDao.update(pDiggerAlbum);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Logger.e(TAG, "update " + DiggerAlbum.class.getSimpleName() + " failure >>>" + e.getMessage());
                }
            }

        }
    }

    /**
     * 查询标题和描述一致的专辑
     * @param pDiggerAlbum 专辑对象
     * @return
     */
    public ArrayList<DiggerAlbum> existedDiggerAlbum(DiggerAlbum pDiggerAlbum){
        List<DiggerAlbum> list = new ArrayList<>();
        QueryBuilder<DiggerAlbum, String> builder = mDiggerAlbumDao.queryBuilder();
        Where<DiggerAlbum, String> where = builder.where();
        try {
            where.eq(COLUMN_ALBUM_TITLE,pDiggerAlbum.getAlbum_title()).and().eq(COLUMN_ALBUM_DES, pDiggerAlbum.getAlbum_des());
            list = builder.query();
            if (TextUtil.isListEmpty(list)){
                return new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(list);
    }
    /**
     * 查询没有上传成功的专辑集合
     * @return
     */
    public ArrayList<DiggerAlbum> queryNotUpload(){
        List albums = new ArrayList();
        try {
            albums = mDiggerAlbumDao.queryForEq(COLUMN_IS_UPLOADED, "0");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(albums);
    }
}
