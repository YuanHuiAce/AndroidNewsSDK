package com.news.sdk.utils;


import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.entity.AttentionListEntity;
import com.news.sdk.entity.ChannelItem;
import com.news.sdk.entity.NewsDetail;
import com.news.sdk.widget.EllipsizeEndTextView;
import com.news.sdk.widget.TextViewExtend;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            sb.append(item.getCname()).append(",");
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
                if (oldItem.getId() == (newItem.getId())) {
                    if (oldItem.getOrder_num() != (newItem.getOrder_num())) {
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
//        videoUrl = "http://v.qq.com/iframe/player.html?vid=p0327a7pt4p&width=290&height=217.5&auto=0";
        String[] split = videoUrl.split("\"");
        if (videoUrl.contains("https:") && videoUrl.contains("preview") && videoUrl.contains("qq.com")) {
            for (int i = 0; i < split.length; i++) {
                if (split[i].contains("https:")) {
                    videoUrl = split[i].replace("https", "http").replace("\\", "").replace("preview", "player");
                    break;
                } else if (split[i].contains("http:")) {
                    videoUrl = split[i].replace("\\", "");
                }
            }
            Logger.e("jigang", "video url=" + videoUrl + ",?=" + videoUrl.indexOf("?"));
//            if (videoUrl.contains("vid=")) {
//                if (videoUrl.contains("&")) {
//                    videoUrl = videoUrl.substring(0, videoUrl.indexOf("&"));
//                }
//            }
//            "<iframe src='http://v.qq.com/iframe/player.html?width=500&height=375&auto=0&vid=i0020zlwtrs'></iframe>";
            if (videoUrl.contains("&")) {
//                url = url.substring(0, url.indexOf("&"));
                Pattern p = Pattern.compile("vid=[a-zA-Z0-9]+");
                Matcher m = p.matcher(videoUrl);
                ArrayList<String> strs = new ArrayList<>();
                while (m.find()) {
                    strs.add(m.group(0));
                    videoUrl = videoUrl.split("src='")[1].split("\\?")[0] + "?" + strs.get(0);
                }
            }
        } else {
            //        <iframe src='http://player.youku.com/embed/XMTg2MzQxNDMwMA=='></iframe>'
            Pattern pt = Pattern.compile("src='([^\r\n']+)'");
            Matcher match = pt.matcher(videoUrl);
            if (match.find()) {
                videoUrl = match.group(1);
            }
        }
        return videoUrl;
//        String params = url.substring(url.indexOf("?") + 1);
//        Logger.e("jigang", "params url=" + params);
//        String[] paramsArr = params.split("=|&");
//        for (int i = 0; i < paramsArr.length; i++) {
//            Logger.e("jigang", "param --->" + paramsArr[i] + "\n");
//        }
//        for (int i = 0; i < paramsArr.length; i++) {
//            if (paramsArr[i].contains("width")) {
//                paramsArr[i + 1] = w + "";
//            }
//            if (paramsArr[i].contains("auto")) {
//                paramsArr[i + 1] = "1";
//            }
//            if (paramsArr[i].contains("height")) {
//                paramsArr[i + 1] = h + "";
//            }
//        }
//        StringBuilder sb = new StringBuilder(url.substring(0, url.indexOf("?") + 1));
//        for (int i = 0; i < paramsArr.length; i++) {
//            if (i % 2 == 0) {
//                sb.append(paramsArr[i] + "=");
//            } else {
//                if (i != paramsArr.length - 1) {
//                    sb.append(paramsArr[i] + "&");
//                } else {
//                    sb.append(paramsArr[i]);
//                }
//            }
//        }
//        return sb.toString();
    }

    /**
     * 生成新闻详情中的css样式
     */
    public static String generateCSS() {
        String bgColor = "f8f8f8";
        String lineColor = "e8e8e8";
        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
            bgColor = "202020";
            lineColor = "2a2a2a";
        }
        StringBuilder cssBuilder = new StringBuilder("<style type=\"text/css\">");
        cssBuilder.append("" +
                "body { margin: 14px 18px 18px 18px; background-color: #" + bgColor + ";} " +
                "h3 { margin: 0px; } h1, h2, h3, h4, h5, h6 { line-height: 150%; font-size: 18px;}" +
                ".top{position:relative;border:0}.top :after{content:'';position:absolute;left:0;background:#" + lineColor + ";width:100%;height:1px;top: 180%;-webkit-transform:scaleY(0.3);transform:scaleY(0.3);-webkit-transform-origin:0 0;transform-origin:0 0} " +
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
        return "<script type=\"text/javascript\">function openVideo(url){console.log(url);window.VideoJavaScriptBridge.openVideo(url)}var obj=new Object();function imgOnload(img,url,isLoadImag){console.log(\"img pro \"+url);if(!isLoadImag){return}if(obj[url]!==1){obj[url]=1;console.log(\"img load \"+url);img.src=url}}</script>";
    }


    /**
     * 生成新闻详情的html
     */
    public static String genarateHTML(NewsDetail detail, int textSize, boolean isLoadImgs) {
        if (detail == null) {
            return "";
        }
        int titleTextSize, commentTextSize, contentTextSize;
        if (textSize == CommonConstant.TEXT_SIZE_NORMAL) {
            titleTextSize = 24;
            commentTextSize = 12;
            contentTextSize = 18;
        } else if (textSize == CommonConstant.TEXT_SIZE_BIG) {
            titleTextSize = 26;
            commentTextSize = 14;
            contentTextSize = 20;
        } else {
            titleTextSize = 28;
            commentTextSize = 16;
            contentTextSize = 22;
        }
        String titleColor = "333333";
        String commentColor = "999999";
        if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
            titleColor = "909090";
            commentColor = "545454";
        }
        StringBuilder contentBuilder = new StringBuilder("<!DOCTYPE html><html><head lang=\"en\"><meta charset=\"UTF-8\"><meta name=\"“viewport”\" content=\"“width=device-width,\" initial-scale=\"1.0,\" user-scalable=\"yes,target-densitydpi=device-dpi”\">" +
                generateCSS() + generateJs() +
                "</head>" +
                "<body><div style=\"font-size:" + titleTextSize + "px;font-weight:bold;margin: 0px 0px 11px 0px;color: #" + titleColor + ";\">" +
                detail.getTitle() +
                "</div><div style=\"font-size:" + commentTextSize + "px;margin: 0px 0px 25px 0px;color: #" + commentColor + ";\" class=\"top\"><span>" +
                detail.getPname() + "</span>" +
                "&nbsp; <span style=\"font-size: " + commentTextSize + "px;color: #" + commentColor + "\">" + DateUtil.getMonthAndDay(detail.getPtime()) + "</span>");
        if (detail.getCommentSize() != 0) {
            contentBuilder.append("&nbsp; <span style=\"font-size: " + commentTextSize + "px;color: #" + commentColor + "\">" + detail.getCommentSize() + "评论" + "</span>");
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
                String playImgUrl = "file:///android_asset/detail_play_default.png";
                if (!TextUtil.isEmptyString(txt)) {
//                    txt = "<h1>" + txt + "</h1>";
                    contentBuilder.append("<p style=\"font-size:" + contentTextSize + "px;color: #" + titleColor + ";\">" + txt + "</p>");
                }
                if (!TextUtil.isEmptyString(img)) {
                    Logger.e("jigang", "img " + img);
                    /**2016年9月5日 冯纪纲 修改webview 中只能无图加载*/
                    contentBuilder.append("<p class=\"p_img\"><img src=\"" + imgUrl + "\" onload=\"imgOnload(this,'" + img + "'," + !isLoadImgs + ")\"  onclick=\"imgOnload(this,'" + img + "',true)\"></p>");
                }
                if (!TextUtil.isEmptyString(vid)) {
                    int w = (int) (DeviceInfoUtil.getScreenWidth() / DeviceInfoUtil.obtainDensity());
                    int h = (int) (w * 0.75);
                    vid = vid.replace("\"", "\'");
                    String url = parseVideoUrl(vid, w, h);
                    if (url.contains("player.html")) {
                        contentBuilder.append("<p class=\"p_video\" style=\"position:relative\"><div onclick=\"openVideo('" + url + "')\" style=\"position:absolute;width:94%;height:" + h + "px\"></div><iframe allowfullscreen class=\"video_iframe\" frameborder=\"0\" height=\"" + h + "\" width=\"100%\" src=\"" + url + "\"></iframe></p>");
                    } else {
                        contentBuilder.append("<p class=\"p_video\" style=\"position:relative\"><div onclick=\"openVideo('" + url + "')\" style=\"position:absolute;width:94%;height:" + h + "px\"></div><img src=\"" + playImgUrl + "\" style=\"width: 100%;height: auto\"></p>");
                    }
                }
            }
        }
        contentBuilder.append("</div></body></html>");
        return contentBuilder.toString();
    }

    public static ArrayList<AttentionListEntity> copyArrayList(ArrayList<AttentionListEntity> target) {
        ArrayList<AttentionListEntity> newList = new ArrayList<>();
        if (!TextUtil.isListEmpty(target)) {
            for (AttentionListEntity entity : target) {
                newList.add(new AttentionListEntity(entity.getConcern(), entity.getCtime(), entity.getDescr(), entity.getFlag(), entity.getIcon(), entity.getId(), entity.getName()));
            }
        }
        return newList;
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getCommentNum(String strText) {
        if (!TextUtil.isEmptyString(strText) && !"0".equals(strText)) {
            int num = Integer.valueOf(strText);
            if (num >= 10000) {
                int i = num % 10000 / 1000;
                if (i > 0) {
                    strText = num / 10000 + "." + String.valueOf(i).substring(0, 1) + "万";
                } else {
                    strText = num / 10000 + "万";
                }
            }
            return strText + "评";
        } else {
            return "";
        }
    }

    public static void setTextColor(Context context, EllipsizeEndTextView textView, int textColor) {
        textView.setTextColor(context.getResources().getColor(ThemeManager.getCurrentThemeRes(context, textColor)));
    }

    public static void setTextColor(Context context, TextViewExtend textView, int textColor) {
        textView.setTextColor(context.getResources().getColor(ThemeManager.getCurrentThemeRes(context, textColor)));
    }

    public static void setTextColor(Context context, TextView textView, int textColor) {
        textView.setTextColor(context.getResources().getColor(ThemeManager.getCurrentThemeRes(context, textColor)));
    }

    public static void setLayoutBgColor(Context context, View view, int color) {
        view.setBackgroundColor(context.getResources().getColor(ThemeManager.getCurrentThemeRes(context, color)));
    }

    public static void setLayoutBgResource(Context context, View layout, int bgColor) {
        layout.setBackgroundResource(ThemeManager.getCurrentThemeRes(context, bgColor));
    }

}
