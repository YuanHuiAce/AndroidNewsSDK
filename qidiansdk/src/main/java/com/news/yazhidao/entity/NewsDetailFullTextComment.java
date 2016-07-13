package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by fengjigang on 15/9/7.
 * 全文精选评论POJO
 */
public class NewsDetailFullTextComment implements Serializable {
    private String userIconUrl;
    private String userName;
    private String userCommtent;
    private String userPraiseCount;

    public NewsDetailFullTextComment(String userIconUrl, String userName, String userCommtent, String userPraiseCount) {
        this.userIconUrl = userIconUrl;
        this.userName = userName;
        this.userCommtent = userCommtent;
        this.userPraiseCount = userPraiseCount;
    }
}
