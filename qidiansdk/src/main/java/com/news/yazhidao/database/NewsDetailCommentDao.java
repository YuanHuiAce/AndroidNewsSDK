package com.news.yazhidao.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.news.yazhidao.entity.NewsDetailComment;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao on 2016/5/6.
 * 处理评论的DAO
 */
public class NewsDetailCommentDao implements Serializable {
    private static final long serialVersionUID = 8897789878602695966L;
    private Context mContext;
    private Dao<NewsDetailComment, Integer> newsDetailCommentDaoOpe;
    private DatabaseHelper helper;


    @SuppressWarnings("unchecked")
    public NewsDetailCommentDao(Context context) {
        mContext = context;
        try {
            helper = DatabaseHelper.getHelper(mContext);
            newsDetailCommentDaoOpe = helper.getDao(NewsDetailComment.class);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 增加一条评论
     */
    public void add(NewsDetailComment newsDetailCommentItem){
        try {
            newsDetailCommentDaoOpe.create(newsDetailCommentItem);
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void update(NewsDetailComment newsDetailCommentItem){
        try {
            newsDetailCommentDaoOpe.update(newsDetailCommentItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //删除一个评论
    public void delete(NewsDetailComment newsDetailCommentItem){
        try {
            newsDetailCommentDaoOpe.delete(newsDetailCommentItem);
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //查询该用户的所有评论，并按照时间顺序排序
    //暂时先不区分用户
    public ArrayList<NewsDetailComment> queryForAll(int userId){
        List<NewsDetailComment> newsDetailCommentItems = new ArrayList<>();
        try {
            QueryBuilder<NewsDetailComment, Integer> builder = newsDetailCommentDaoOpe.queryBuilder();
            builder.orderBy("ctime", false);
            newsDetailCommentItems = builder.query();
            if(newsDetailCommentItems!=null&&newsDetailCommentItems.size()!=0){
                return (ArrayList<NewsDetailComment>)newsDetailCommentItems;
            }
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return (ArrayList<NewsDetailComment>)newsDetailCommentItems;
    }

    //删除所有评论
    public void deleteAll(List<NewsDetailComment> items){
        try {
            newsDetailCommentDaoOpe.delete(items);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    //根据comment_ids[],查找符合的评论
    public List<NewsDetailComment> qureyByIds(Object[] comment_ids){
        List<NewsDetailComment> newsDetailCommentItems = new ArrayList<>();
        try {
            QueryBuilder<NewsDetailComment, Integer> builder = newsDetailCommentDaoOpe.queryBuilder();
            builder.where().in("comment_id",comment_ids);
            newsDetailCommentItems = builder.query();
            if(newsDetailCommentItems!=null&&newsDetailCommentItems.size()!=0){
                return (ArrayList<NewsDetailComment>)newsDetailCommentItems;
            }
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return (ArrayList<NewsDetailComment>)newsDetailCommentItems;
    }


}
