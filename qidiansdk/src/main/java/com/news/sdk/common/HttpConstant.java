package com.news.sdk.common;

/**
 * Created by fengjigang on 15/1/21.
 */
public class HttpConstant {
    /**
     * 服务器api 域名
     */
    public static final String URL_SERVER_HOST = "http://bdp.deeporiginalx.com/v2";

    public static final String URL_LOG_HOST = "http://log.deeporiginalx.com/rep/v2/log";

    public static final String URL_REGISTER_VISITOR = URL_SERVER_HOST + "/au/sin/g";//注册游客信息

    public static final String URL_VISITOR_LOGIN = URL_SERVER_HOST + "/au/lin/g";//游客登录

    public static final String URL_MERGE_USER_LOGIN = URL_SERVER_HOST + "/au/sin/s";//三方用户注册,1.包含第三方用户登录合并游客用户;2.包含新第三方用户登录覆盖三方用户

    public static final String URL_PRAISE = URL_SERVER_HOST + "/news/baijia/praise";

    public static final String URL_SEARCH_WITH_SUBSCRIBE = URL_SERVER_HOST + "/ns/es/snp";//带有订阅信息的搜索

    public static final String URL_SEARCH = URL_SERVER_HOST + "/ns/es/s";//搜索

    public static final String URL_LOG_POST_NEWS_READ = URL_LOG_HOST + "/news/read";

    public static final String URL_LOG_POST_NEWS_SHOW = URL_LOG_HOST + "/news/show";

    public static final String URL_LOG_POST_NEWS_CLICK = URL_LOG_HOST + "/news/click";

    public static final String URL_LOG_POST_AD_CLICK = URL_LOG_HOST + "/ad/click";

    public static final String URL_LOG_POST_APP_USE = URL_LOG_HOST + "/app/use";

    public static final String URL_LOG_POST_APP_ACTION = URL_LOG_HOST + "/app/action";

    public static final String URL_LOG_POST_AD_GET = URL_LOG_HOST + "/ad/get";

    public static final String URL_LOG_POST_USER_SIGN_UP = URL_LOG_HOST + "/users/signup";

    public static final String URL_POST_AD_SOURCE = URL_SERVER_HOST + "/ad/source";
    /**
     * 新闻阅读时长日志
     */
    public static final String URL_GET_NEWS_CONTENT = URL_SERVER_HOST + "/news/baijia/point";
    /**
     * 谷歌今日焦点
     */
    public static final String URL_USER_LOGIN = URL_SERVER_HOST + "/news/baijia/fetchLogin";

    public static final String URL_UPLOAD_JPUSHID = URL_SERVER_HOST + "/news/baijia/fetchImUser?";

    public static final String URL_UPLOAD_UMENGPUSHID = URL_SERVER_HOST + "/news/baijia/uploadUmengPushId?";

    public static final String URL_SEND_MESSAGE = URL_SERVER_HOST + "/news/baijia/fetchIm?";

    public static final String URL_GET_HISTORY_MESSAGE = URL_SERVER_HOST + "/news/baijia/fetchImContent?";

    public static final String URL_GET_MESSAGE_LIST = URL_SERVER_HOST + "/news/baijia/fetchImList?";
    /**
     * 获取频道列表接口
     */
    public static final String URL_FETCH_CHANNEL_LIST = URL_SERVER_HOST + "/ns/ch/cho?";
    /**
     * 获取专辑列表接口
     */
    public static final String URL_FETCH_ALBUM_LIST = URL_SERVER_HOST + "/news/baijia/fetchAlbumList?";
    /**
     * 挖掘机创建专辑接口
     */
    public static final String URL_CREATE_DIGGER_ALBUM = URL_SERVER_HOST + "/news/baijia/createAlbum?";

    /**
     * 往专辑中添加挖掘内容
     */
    public static final String URL_DIGGER_ALBUM = "http://60.28.29.37:8080/excavator?";

    /**
     * 开始挖掘新闻
     */
    public static final String URL_DIGGER_NEWS = URL_SERVER_HOST + "/news/baijia/dredgeUpStatusforiOS";

    /**
     * 获取指定专辑中的挖掘内容
     */
    public static final String URL_FETCH_ALBUM_SUBITEMS = URL_SERVER_HOST + "/news/baijia/dredgeUpStatus?";

    /**
     * 获取热点话题标签
     */
    public static final String URL_FETCH_ELEMENTS = URL_SERVER_HOST + "/news/baijia/fetchElementary";

    /**
     * 获取新闻详情 api
     */
    public static final String URL_POST_NEWS_DETAIL = URL_SERVER_HOST + "/news/baijia/fetchDetail";
    /**
     * 获取视频详情页
     */
    public static final String URL_VIDEO_CONTENT = URL_SERVER_HOST + "/vi/con?";
    /**
     * feed流上拉加载更多
     */
    public static final String URL_FEED_LOAD_MORE = URL_SERVER_HOST + "/ns/fed/ln?";

    /**
     * feed流下拉刷新
     */
    public static final String URL_FEED_PULL_DOWN = URL_SERVER_HOST + "/ns/fed/rn?";
    /**
     * feed流关注上拉加载更多
     */
    public static final String URL_FEED_FOCUS_LOAD_MORE = URL_SERVER_HOST + "/ns/pbs/cocs/l?";

    /**
     * feed流关注下拉刷新
     */
    public static final String URL_FEED_FOCUS_PULL_DOWN = URL_SERVER_HOST + "/ns/pbs/cocs/r?";

    /**
     * 转发记录
     */
    public static final String URL_REPLAY = URL_SERVER_HOST + "/ns/replay";

    /**
     * 频道刷新加上广告
     */
    public static final String URL_FEED_AD_PULL_DOWN = URL_SERVER_HOST + "/ns/fed/ra?";
    /**
     * 频道加载加上广告
     */
    public static final String URL_FEED_AD_LOAD_MORE = URL_SERVER_HOST + "/ns/fed/la?";

    /**
     * 手机信息上传
     */
    public static final String URL_UPLOAD_INFORMATION = "http://bdp.deeporiginalx.com/v2/au/app";
    /**
     * 获取新闻详情页
     */
    public static final String URL_FETCH_CONTENT = URL_SERVER_HOST + "/ns/con?";
    /**
     * 不感兴趣记录
     */
    public static final String URL_DISSLIKE_RECORD = URL_SERVER_HOST + "/ns/hate";
    /**
     * 新闻普通评论列表
     */
    public static final String URL_FETCH_COMMENTS = URL_SERVER_HOST + "/ns/coms/c?";
    /**
     * 热点评论列表
     */
    public static final String URL_FETCH_HOTCOMMENTS = URL_SERVER_HOST + "/ns/coms/h?";

    /**
     * 新闻评论点赞
     * 取消评论点赞
     */
    public static final String URL_ADDORDELETE_LOVE_COMMENT = URL_SERVER_HOST + "/ns/coms/up?";

    /**
     * 添加新闻评论
     */
    public static final String URL_ADD_COMMENT = URL_SERVER_HOST + "/ns/coms";
    /**
     * 查看评论列表
     * 用户删除已创建评论**
     */
    public static final String URL_USER_CREATEORDELETE_COMMENTLIST = URL_SERVER_HOST + "/ns/au/coms?";


    /**
     * 日志上传
     */
    public static final String URL_UPLOAD_LOG = "http://bdp.deeporiginalx.com/rep/v2/c?";// URL_SERVER_HOST+"/c?"

    /**
     * 新闻客户端-新闻相关属性集
     */
    public static final String URL_NEWS_RELATED = URL_SERVER_HOST + "/ns/ascad";
    /**
     * 添加新闻收藏
     * 取消新闻收藏
     */
    public static final String URL_ADDORDELETE_FAVORITE = URL_SERVER_HOST + "/ns/cols?";

    /**
     * 查看收藏列表
     */
    public static final String URL_SELECT_FAVORITELIST = URL_SERVER_HOST + "/ns/au/cols?";

    /**
     * 添加关心
     * 取消关心
     */
    public static final String URL_ADDORDELETE_CAREFOR = URL_SERVER_HOST + "/ns/cocs?";

    /**
     * 查看收藏列表
     */
    public static final String URL_SELECT_CAREFOR = URL_SERVER_HOST + "/ns/au/cocs?";
    /**
     * 添加新闻发布源关心（POST）
     * 取消新闻发布源关心（DELETE）
     */
    public static final String URL_ADDORDELETE_ATTENTION = URL_SERVER_HOST + "/ns/pbs/cocs?";
    /**
     * 获取新闻详情页
     */
    public static final String URL_GETLIST_ATTENTION = URL_SERVER_HOST + "/ns/pbs?";
    /**
     * 专题详情
     */
    public static final String URL_NEWS_TOPIC = URL_SERVER_HOST + "/ns/tdq?";

    /**
     * 手机渠道信息上传
     */
    public static final String URL_UPLOAD_CHANNEL_INFORMATION = URL_SERVER_HOST + "/au/phone";

    /**
     * 详情页广告
     */
    public static final String URL_NEWS_DETAIL_AD = URL_SERVER_HOST + "/ns/ad";

    /**
     * 开屏页广告
     */
    public static final String URL_NEWS_SPLASH_AD = URL_SERVER_HOST + "/ns/oad";

    /**
     * 滑动接口
     */
    public static final String URL_SCROLL_AD = URL_SERVER_HOST + "/sl/ins";

    /**
     * 用户映射接口
     */
    public static final String URL_AUTHORIZED_USER = URL_SERVER_HOST + "/au/sin/s";
    /**
     * apk升级接口
     */
    public static final String URL_APK_UPDATE=URL_SERVER_HOST+"/version/query?";
    /**
     * 获取二级频道接口
     */
    public static final String URL_VIDEO_CHANNEL_LIST=URL_SERVER_HOST+"/ns/ch/scho?";

    /**
     * 视频详情点赞和倒赞接口
     */

    public static final String URL_VIDEO_ATTITUDE=URL_SERVER_HOST+"/ns/vdnews/commend";


    /**
     * 视频播放量更新接口
     */
    public static final String URL_VIDEO_INCREASE_TIMES=URL_SERVER_HOST+"/ns/vdnews/ptimes?";
}
