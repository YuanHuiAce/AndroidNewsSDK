package com.news.yazhidao.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.NewsDetail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjigang on 15/1/27.
 */
public class TextUtil {


    /**
     * 判断一个List 是否为null 或者是否长度为0
     *
     * @param list
     * @return
     */
    public static boolean isListEmpty(List list) {
        if (list == null) {
            return true;
        }
        if (list.size() == 0) {
            return true;
        }
        return false;
    }


    /**
     * 判断字符串是否为null 或者 长度为0 或者 只包含空字符
     *
     * @param pString
     * @return
     */
    public static boolean isEmptyString(String pString) {
        if (pString == null) {
            return true;
        }
        if (pString.length() == 0 || pString.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static String List2String(ArrayList<ChannelItem> list) {
        if (isListEmpty(list)) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (ChannelItem item : list) {
            sb.append(item.getName()).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }



    /**
     * 获取字符串的Base64格式
     */
    public static String getBase64(String target) {
        String url = "";
        if (!TextUtils.isEmpty(target)) {
            byte[] bytes = Base64.encode(target.getBytes(), Base64.DEFAULT);
            try {
                url = new String(bytes, "utf-8");
                Logger.e("jigang", "base 64= " + url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            url = url.replace("=", "").replace("\n", "").replace("\r", "");
        }
        return url;
    }

    /**
     * 生成新闻详情中的css样式
     */
    public static String generateCSS() {
        StringBuilder cssBuilder = new StringBuilder("<style type=\"text/css\">");
        cssBuilder.append("" +
                "body { margin: 14px 18px 18px 18px; background-color: #f6f6f6;} " +
                "h3 { margin: 0px; } " +
                ".top{position:relative;border:0}.top :after{content:'';position:absolute;left:0;background:#d3d3d3;width:100%;height:1px;top: 180%;-webkit-transform:scaleY(0.3);transform:scaleY(0.3);-webkit-transform-origin:0 0;transform-origin:0 0} " +
                ".content { letter-spacing: 0.5px; line-height: 150%; font-size: 18px; }" +
                ".content img { width: 100%; }" +
                ".p_img { text-align: center; }");
        cssBuilder.append("</style>");
        return cssBuilder.toString();
    }

    /**
     * 生成新闻详情的html
     */
    public static String genarateHTML(NewsDetail detail, int textSize) {
        if (detail == null) {
            return "";
        }
        int titleTextSize, commentTextSize, contentTextSize;
        if (textSize == CommonConstant.TEXT_SIZE_NORMAL) {
            titleTextSize = 20;
            commentTextSize = 13;
            contentTextSize = 16;
        } else if (textSize == CommonConstant.TEXT_SIZE_BIG) {
            titleTextSize = 23;
            commentTextSize = 15;
            contentTextSize = 17;
        } else {
            titleTextSize = 24;
            commentTextSize = 16;
            contentTextSize = 19;
        }
        StringBuilder contentBuilder = new StringBuilder("<!DOCTYPE html><html><head lang=\"en\"><meta charset=\"UTF-8\"><meta name=\"“viewport”\" content=\"“width=device-width,\" initial-scale=\"1.0,\" user-scalable=\"yes,target-densitydpi=device-dpi”\">" +
                generateCSS() +
                "</head>" +
                "<body><div style=\"font-size:" + titleTextSize + "px;font-weight:bold;margin: 0px 0px 11px 0px;\">" +
                detail.getTitle() +
                "</div><div style=\"font-size:" + commentTextSize + "px;margin: 0px 0px 25px 0px;color: #9a9a9a;\" class=\"top\"><span>" +
                detail.getPname() + "</span>" +
                "&nbsp; <span>" + DateUtil.getMonthAndDay(detail.getPtime()) + "</span>");
        if (detail.getCommentSize() != 0) {
            contentBuilder.append("&nbsp; <span>" + detail.getCommentSize() + "评论" + "</span>");
        }
            contentBuilder.append("</div><div class=\"content\">");

        ArrayList<HashMap<String, String>> content = detail.getContent();
        if (!TextUtil.isListEmpty(content)) {
            for (HashMap<String, String> map : content) {
                String txt = map.get("txt");
                String img = map.get("img");
                if (!TextUtil.isEmptyString(txt)) {
                    contentBuilder.append("<p style=\"font-size:" + contentTextSize + "px;color: #333333;\">" + txt + "</p>");
                }
                if (!TextUtil.isEmptyString(img)) {
                    contentBuilder.append("<p class=\"p_img\"><img src=\"" + img + "\"></p>");
                }
            }
        }
        contentBuilder.append("</div></body></html>");
        return contentBuilder.toString();
    }
}
