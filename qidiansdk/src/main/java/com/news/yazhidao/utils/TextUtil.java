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

    public static boolean isChannelChanged(ArrayList<ChannelItem> oldList, ArrayList<ChannelItem> newList) {
        if (isListEmpty(oldList) || isListEmpty(newList)) {
            return false;
        }
        for (int i = 0; i < oldList.size(); i++) {
            ChannelItem oldItem = oldList.get(i);
            for (int j = 0; j < newList.size(); j++) {
                ChannelItem newItem = newList.get(j);
                if (oldItem.getId().equals(newItem.getId())) {
                    if (!oldItem.getOrderId().equals(newItem.getOrderId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 解析出iframe中的video url
     */
    private static String parseVideoUrl(String videoUrl, int w, int h) {
        String[] split = videoUrl.split("\"");
        String url = "";
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains("https:")) {
                url = split[i].replace("https", "http").replace("\\", "").replace("preview", "player");
                break;
            } else if (split[i].contains("http:")) {
                url = split[i].replace("\\", "");
            }
        }
        Logger.e("jigang", "video url=" + url + ",?=" + url.indexOf("?"));

        String params = url.substring(url.indexOf("?") + 1);
        Logger.e("jigang", "params url=" + params);
        String[] paramsArr = params.split("=|&");
        for (int i = 0; i < paramsArr.length; i++) {
            Logger.e("jigang", "param --->" + paramsArr[i] + "\n");
        }
        for (int i = 0; i < paramsArr.length; i++) {
            if (paramsArr[i].contains("width")) {
                paramsArr[i + 1] = w + "";
            }
            if (paramsArr[i].contains("auto")) {
                paramsArr[i + 1] = "1";
            }
            if (paramsArr[i].contains("height")) {
                paramsArr[i + 1] = h + "";
            }
        }
        StringBuilder sb = new StringBuilder(url.substring(0, url.indexOf("?") + 1));
        for (int i = 0; i < paramsArr.length; i++) {
            if (i % 2 == 0) {
                sb.append(paramsArr[i] + "=");
            } else {
                if (i != paramsArr.length - 1) {
                    sb.append(paramsArr[i] + "&");
                } else {
                    sb.append(paramsArr[i]);
                }
            }
        }
        return sb.toString();
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
                ".p_img { text-align: center; }" +
                ".p_video { text-align: center;position: relative; }"
        );
        cssBuilder.append("</style>");
        return cssBuilder.toString();
    }

    public static String generateJs() {
//        return "<script type=\"text/javascript\">function openVideo(url){console.log(url);window.VideoJavaScriptBridge.openVideo(url);}</script>";
        return "<script type=\"text/javascript\">function openVideo(url){console.log(url);window.VideoJavaScriptBridge.openVideo(url)}var obj=new Object();function imgOnload(img,url,isLoadImag){console.log(\"img pro \"+url);if(!isLoadImag){return}if(obj[url]!==1){obj[url]=1;console.log(\"img load \"+url);img.src=url}}</script>";    }


    /**
     * 生成新闻详情的html
     */
    public static String genarateHTML(NewsDetail detail, int textSize,boolean isLoadImgs) {
        if (detail == null) {
            return "";
        }
        int titleTextSize, commentTextSize, contentTextSize;
        if (textSize == CommonConstant.TEXT_SIZE_NORMAL) {
            titleTextSize = 20;
            commentTextSize = 13;
            contentTextSize = 17;
        } else if (textSize == CommonConstant.TEXT_SIZE_BIG) {
            titleTextSize = 22;
            commentTextSize = 15;
            contentTextSize = 18;
        } else {
            titleTextSize = 23;
            commentTextSize = 16;
            contentTextSize = 19;
        }
        StringBuilder contentBuilder = new StringBuilder("<!DOCTYPE html><html><head lang=\"en\"><meta charset=\"UTF-8\"><meta name=\"“viewport”\" content=\"“width=device-width,\" initial-scale=\"1.0,\" user-scalable=\"yes,target-densitydpi=device-dpi”\">" +
                generateCSS() + generateJs() +
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
//            HashMap<String, String> add = new HashMap<>();
//            add.put("vid", "<iframe allowfullscreen=\\\"\\\" class=\\\"video_iframe\\\" data-src=\\\"https://v.qq.com/iframe/preview.html?vid=d0307rjka3y&amp;width=500&amp;height=375&amp;auto=0\\\" frameborder=\\\"0\\\" height=\\\"375\\\" src=\\\"https://v.qq.com/iframe/preview.html?vid=d0307rjka3y&amp;width=500&amp;height=375&amp;auto=0\\\" width=\\\"500\\\"></iframe>");
//            content.add(add);
            for (HashMap<String, String> map : content) {
                String txt = map.get("txt");
                String img = map.get("img");
                String vid = map.get("vid");
                String imgUrl = "file:///android_asset/deail_default.png";
                if (!TextUtil.isEmptyString(txt)) {
                    contentBuilder.append("<p style=\"font-size:" + contentTextSize + "px;color: #333333;\">" + txt + "</p>");
                }
                if (!TextUtil.isEmptyString(img)) {
                    Logger.e("jigang", "img " + img);
                    /**2016年9月5日 冯纪纲 修改webview 中只能无图加载*/
                    contentBuilder.append("<p class=\"p_img\"><img src=\"" + imgUrl + "\" onload=\"imgOnload(this,'" + img + "',"+isLoadImgs+")\"  onclick=\"imgOnload(this,'" + img + "',true)\"></p>");
                }
                if (!TextUtil.isEmptyString(vid)) {
                    int w = (int) (DeviceInfoUtil.getScreenWidth() / DeviceInfoUtil.obtainDensity());
                    int h = (int) (w * 0.75);
                    String url = parseVideoUrl(vid, w, h);
                    contentBuilder.append("<p class=\"p_video\" style=\"position:relative\"><div onclick=\"openVideo('" + url + "')\" style=\"position:absolute;width:94%;height:" + h + "px\"></div><iframe allowfullscreen class=\"video_iframe\" frameborder=\"0\" height=\"" + h + "\" width=\"100%\" src=\"" + url + "\"></iframe></p>");
                }
            }
        }
        contentBuilder.append("</div></body></html>");
        return contentBuilder.toString();
    }
}
