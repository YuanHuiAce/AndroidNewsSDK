package com.news.yazhidao.entity;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 新闻详情entity
 * Created by fengjigang on 15/1/21.
 */
public class NewsDetailAdd implements Serializable {
    //新闻来源
    public String originsourceSiteName;
    public String sourceSiteName;
    public String createTime;
    //新闻描述
    public String abs;
    //新闻内容
    public ArrayList<LinkedTreeMap<String,HashMap<String,String>>> content;
    //图片url
    public String imgUrl;
    //标题
    public String title;
    //新闻时间
    public String updateTime;
    //温度
    public String root_class;

    public ArrayList<BaiDuBaiKe> baike;

    public boolean isdoc;

    public String docUrl;

    public String docTime;

    public String docUserIcon;

    public ArrayList<ZhiHu> zhihu;

    public ArrayList<Point> point;

    public ArrayList<ArrayList<String>> douban;  //get(0)  title  get(1) url

    public ArrayList<Weibo> weibo;
    public ArrayList<HashMap<String,String>> imgWall;
    //相关新闻
    public ArrayList<Relate> relate;
    /**差异化观点*/
    public NewsDetailSelfOpinion relate_opinion;
    public String rc;

    //name entity
    public Ne ne;
    public class Ne {
        public ArrayList<String> time;
        public ArrayList<String> gpe;
        public ArrayList<String> person;
        public ArrayList<String> loc;
        public ArrayList<String> org;
    }

    public class Article{
        public String url;
        public String title;
        public String self_opinion;
    }
    public class BaiDuBaiKe {
        public String imgUrl;
        public String title;
        public String url;
        public String abs;
    }

    public class Relate {
        public String url;
        public String sourceSitename;
        public String img;
        public String title;
        public String updateTime;
    }

    public class ZhiHu {
        public String url;
        public String user;
        public String title;
    }
    public class Weibo {
        //        public sourceSitename sourceSitename;
        public String url;
        public String profileImageUrl;
        public String user;
        public String title;
        public String img;
        public String sourceSitename;
        public String isCommentFlag;
        public String comments_count;
        public int like_count;
        public int reposts_count;
        public ArrayList<String> imgs;
    }
    public static class Point implements Comparable {
        public String userName;
        /**用户评论内容*/
        public String srcText;
        public String desText;
        public String paragraphIndex;
        /**
         * @see com.news.yazhidao.net.request.UploadCommentRequest#TEXT_DOC
         * @see com.news.yazhidao.net.request.UploadCommentRequest#TEXT_PARAGRAPH
         * @see com.news.yazhidao.net.request.UploadCommentRequest#SPEECH_DOC
         * @see com.news.yazhidao.net.request.UploadCommentRequest#SPEECH_PARAGRAPH
         * */
        public String type;//
        public String up;
        public String down;
        public String comments_count;
        public String uuid;
        public String userIcon;
        public String sourceUrl;
        public String isPraiseFlag;
        public String commentId;
        //语音评论的时长
        public int srcTextTime;

        @Override
        public int compareTo(Object another) {
            if (this.up == null){
                this.up = "0";
            }
            if (((Point)another).up == null){
                ((Point)another).up = "";
            }
            int first = Integer.valueOf(this.up);
            int second = Integer.valueOf(((Point)another).up);
            if (first < second){
                return 1;
            }
            if (first == second){
                return 0;
            }
            return -1;
        }
    }
}
