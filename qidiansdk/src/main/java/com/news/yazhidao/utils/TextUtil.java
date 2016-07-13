package com.news.yazhidao.utils;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.internal.LinkedTreeMap;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.NewsDetailContent;
import com.news.yazhidao.entity.NewsDetailEntry;
import com.news.yazhidao.entity.NewsDetailImageWall;
import com.news.yazhidao.net.request.UploadCommentRequest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fengjigang on 15/1/27.
 */
public class TextUtil {
    public static String convertTemp(String origin) {
        if (!TextUtils.isEmpty(origin)) {
            return origin.replace("度", "℃");
        }
        return "";
    }

    public static String trimBlankSpace(String data) {
        if (data.contains("  ")) {
            int index = data.indexOf("  ");
            StringBuilder before = new StringBuilder(data.replace("  ", "").substring(0, index));
            StringBuilder after = new StringBuilder(data.replace("  ", "").substring(index));
            return before + "  " + after;
        }
        return data;
    }

    public static void setTopLineBackground(String category, LinearLayout ll_top_line) {

        if ("焦点".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#ff4341"));
        } else if ("国际".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#007fff"));
        } else if ("港台".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#726bf8"));
        } else if ("内地".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#18a68b"));
        } else if ("财经".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#32bfcd"));
        } else if ("科技".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#007fff"));
        } else if ("体育".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#df8145"));
        } else if ("社会".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#00b285"));
        } else if ("国内".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#726bf8"));
        } else if ("娱乐".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#ff7272"));
        }

    }

    public static void setViewCompatBackground(String category, LoadingLayout loadingLayout) {

//        if ("焦点".equals(category)) {
//            loadingLayout.setHeaderBackground("#ff4341");
//        } else if ("国际".equals(category)) {
//            loadingLayout.setHeaderBackground("#007fff");
//        } else if ("港台".equals(category)) {
//            loadingLayout.setHeaderBackground("#726bf8");
//        } else if ("内地".equals(category)) {
//            loadingLayout.setHeaderBackground("#18a68b");
//        } else if ("财经".equals(category)) {
//            loadingLayout.setHeaderBackground("#32bfcd");
//        } else if ("科技".equals(category)) {
//            loadingLayout.setHeaderBackground("#007fff");
//        } else if ("体育".equals(category)) {
//            loadingLayout.setHeaderBackground("#df8145");
//        } else if ("社会".equals(category)) {
//            loadingLayout.setHeaderBackground("#00b285");
//        } else if ("国内".equals(category)) {
//            loadingLayout.setHeaderBackground("#726bf8");
//        } else if ("娱乐".equals(category)) {
//            loadingLayout.setHeaderBackground("#ff7272");
//        }

    }


    //卡片2类别背景设置
//    public static void setNewsBackGround(TextViewExtend tv_news_category, String category) {
//        if ("焦点".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_jiaodian);
//        } else if ("国际".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_guoji);
//        } else if ("港台".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_gangtai);
//        } else if ("内地".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_neidi);
//        } else if ("财经".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_caijing);
//        } else if ("科技".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_keji);
//        } else if ("体育".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_tiyu);
//        } else if ("社会".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_shehui);
//        } else if ("国内".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_guonei);
//        } else if ("娱乐".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_yule);
//        }
//    }


    //卡片3类别背景设置
//    public static void setNewsBackGroundRight(TextViewExtend tv_news_category, String category) {
//        if ("焦点".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_jiaodian);
//        } else if ("国际".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_guoji);
//        } else if ("港台".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_gangtai);
//        } else if ("内地".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_neidi);
//        } else if ("财经".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_caijing);
//        } else if ("科技".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_keji);
//        } else if ("体育".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_tiyu);
//        } else if ("社会".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_shehui);
//        } else if ("国内".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_guonei);
//        } else if ("娱乐".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_yule);
//        } else {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_keji);
//        }
//    }

//    public static void setTextBackGround(TextView tv_news_category, String category) {
//
//        if ("焦点".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_jiaodian);
//        } else if ("国际".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_guoji);
//        } else if ("港台".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_gangtai);
//        } else if ("内地".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_neidi);
//        } else if ("财经".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_caijing);
//        } else if ("科技".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_keji);
//        } else if ("体育".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_tiyu);
//        } else if ("社会".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_shehui);
//        } else if ("国内".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_guonei);
//        } else if ("娱乐".equals(category)) {
//            tv_news_category.setBackgroundResource(R.drawable.bg_category_yule);
//        }
//
//    }

    public static void setResourceSiteIcon(ImageView iv_source, String source_name) {

        if (TextUtils.isEmpty(source_name)) {
            return;
        }
    }

    public static String trimEnterInContent(String[] split) {
        StringBuilder _StringB = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (i != split.length - 1) {
                _StringB.append(split[i] + "\n");
            } else {
                _StringB.append(split[i]);
            }
        }
        return _StringB.toString();
    }

    /**
     * 获取inputStream中的数据
     *
     * @param in
     * @return
     */
    public static String getResponseContent(InputStream in) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Logger.i("jigang", "gzip json=" + new String(sb.toString().getBytes(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * InputStream 转换成byte[]
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    //把 所有的半角符号转化为全角符号
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 随机产生一个颜色值给热词搜索做背景色
     */
//    public static int getRandomColor4Hotlabel(Context mContext) {
//        String[] colorArr = mContext.getResources().getStringArray(R.array.bg_special_colors);
//        Random random = new Random();
//        int index = random.nextInt(colorArr.length);
//        return Color.parseColor(colorArr[index]);
//    }

    /**
     * 专辑列表获取对应的背景
     *
     * @return
     */
    public static int getSpecialBgPic(int positon) {
        return 0;
    }


    /**
     * 获取别的app分享进来的新闻链接中的url
     *
     * @param data
     */
    public static String getNewsTitle(String data) {
        String regEx4Title = "[\\((][\\s\\S]*[\\))]";
        String regEx4Url = "(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";
        //去除多余文字
        Pattern patternTitle = Pattern.compile(regEx4Title);
        Matcher matcherTitle = patternTitle.matcher(data);
        while (matcherTitle.find()) {
            data = data.replace(matcherTitle.group(), "");
        }
        data = data.replace("【", "").replace("】", "");

        //去除多余的url

        Pattern patternUrl = Pattern.compile(regEx4Url);
        Matcher matcherUrl = patternUrl.matcher(data);
        while (matcherUrl.find()) {
            data = data.replace(matcherUrl.group(), "");
        }
        return data.replace("\n", "");
    }

    /**
     * 获取别的app分享进来的新闻链接中的标题
     *
     * @param data
     * @return
     */
    public static String getNewsUrl(String data) {
        String regEx4Url = "(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";
        Pattern pattern = Pattern.compile(regEx4Url);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            data = matcher.group();
        }
        return "";
    }

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
     * 获取数据库表id
     *
     * @return 24位十六进制字符串
     */
    public static String getDatabaseId() {
        return new ObjectId().toString();
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
     * 解析新闻详情POJO,转换成expandablelistview 所须POJO
     *
     * @param pNewsDetail
     * @return
     */
    public static ArrayList<ArrayList> parseNewsDetail(ArrayList<ArrayList> mNewsContentDataList, NewsDetailAdd pNewsDetail, String mImgUrl) {
        mNewsContentDataList.clear();
        /**计算展示内容需要多少个组,其中包括 新闻内容,多图集合,差异化观点,精选评论,新闻词条(百度百科,豆瓣),相关观点,微博热点,知乎推荐*/
        if (pNewsDetail != null) {
            /**计算新闻内容所在组*/
            if (!TextUtil.isListEmpty(pNewsDetail.content)) {
                ArrayList list = new ArrayList<>();
                ArrayList<NewsDetailAdd.Point> points = pNewsDetail.point;
                boolean isHaveImgs = false;
                for (int i = 0; i < pNewsDetail.content.size(); i++) {
                    LinkedTreeMap<String, HashMap<String, String>> treeMap = pNewsDetail.content.get(i);
                    HashMap<String, String> hashMap = treeMap.get(i + "");
                    if (hashMap != null) {
                        if (!TextUtil.isEmptyString(hashMap.get("txt"))) {
                            NewsDetailContent content = new NewsDetailContent();
                            content.setContent(hashMap.get("txt"));//img img_info txt
                            content.setComments(new ArrayList<NewsDetailAdd.Point>());
                            list.add(content);
                        }
                        if (!TextUtil.isEmptyString(hashMap.get("img"))) {
                            NewsDetailContent content = new NewsDetailContent();
                            content.setContent(hashMap.get("img"));//img img_info txt
                            String imgWidth = (hashMap.get("width") == null) ? "0" : hashMap.get("width");
                            String imgHeight = (hashMap.get("height") == null) ? "0" : hashMap.get("height");
                            String imgSize = (hashMap.get("size") == null) ? "0" : hashMap.get("size");
                            content.setImgWidth(Integer.parseInt(imgWidth));
                            content.setImgHeight(Integer.parseInt(imgHeight));
                            content.setImgSize(Integer.parseInt(imgSize));
                            content.setComments(new ArrayList<NewsDetailAdd.Point>());
                            list.add(content);
                            isHaveImgs = true;
                        }
                    }
                }
                //如果feed流中有图片,而详情页中没有的话,此处要确保详情页中有一张图
                if (!isHaveImgs && !TextUtil.isEmptyString(mImgUrl)) {
                    NewsDetailContent content = new NewsDetailContent();
                    content.setContent(mImgUrl);
                    content.setComments(new ArrayList<NewsDetailAdd.Point>());
                    list.add(0, content);
                }
                if (!TextUtil.isListEmpty(points)) {
                    for (int j = 0; j < points.size(); j++) {
                        NewsDetailAdd.Point point = points.get(j);
                        int paragraphIndex = Integer.valueOf(point.paragraphIndex);
                        if (UploadCommentRequest.TEXT_PARAGRAPH.equals(point.type)) {
                            if (paragraphIndex < list.size()) {
                                NewsDetailContent content = (NewsDetailContent) list.get(paragraphIndex);
                                content.getComments().add(point);
                            }
                        }
                    }
                }
                if (list.size() > 0) {
                    mNewsContentDataList.add(list);
                }
            }
            /**计算图片墙所在组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.imgWall)) {
                NewsDetailImageWall imageWall = new NewsDetailImageWall();
                imageWall.setImgWall(pNewsDetail.imgWall);
                ArrayList<NewsDetailImageWall> list = new ArrayList();
                list.add(imageWall);
                mNewsContentDataList.add(list);
                if (!TextUtil.isListEmpty(pNewsDetail.content)) {
                    for (int i = 0; i < pNewsDetail.content.size(); i++) {
                        LinkedTreeMap<String, HashMap<String, String>> treeMap = pNewsDetail.content.get(i);
                        HashMap<String, String> hashMap = treeMap.get(i + "");
                        if (hashMap.get("img") != null) {
                            HashMap<String, String> image = new HashMap<>();//img img_info txt
                            image.put("img", hashMap.get("img"));
                            imageWall.getImgWall().add(image);
                        }
                    }
                }
            }
            /**计算差异化观点所在组数据*/
            if (pNewsDetail.relate_opinion != null) {
                ArrayList<NewsDetailAdd.Article> self_opinion = pNewsDetail.relate_opinion.getSelf_opinion();
                if (!TextUtil.isListEmpty(self_opinion)) {
                    mNewsContentDataList.add(self_opinion);
                }
            }
            /**计算精选评论观点组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.point)) {
                ArrayList<NewsDetailAdd.Point> points = new ArrayList<>();
                for (int j = 0; j < pNewsDetail.point.size(); j++) {
                    NewsDetailAdd.Point point = pNewsDetail.point.get(j);
                    if (UploadCommentRequest.TEXT_DOC.equals(point.type) || UploadCommentRequest.TEXT_PARAGRAPH.equals(point.type)) {
                        points.add(point);
                    }
                }
                Collections.sort(points);
                /**只要3条评论*/
                if (points.size() > 3) {
                    points = new ArrayList<>(points.subList(0, 3));
                }
                points.add(new NewsDetailAdd.Point());
                mNewsContentDataList.add(points);
            }
            /**计算新闻词条组数据*/
            ArrayList<NewsDetailEntry> entryList = new ArrayList<>();
            if (!TextUtil.isListEmpty(pNewsDetail.baike)) {
                for (NewsDetailAdd.BaiDuBaiKe item : pNewsDetail.baike) {
                    entryList.add(new NewsDetailEntry(item.title, NewsDetailEntry.EntyType.BAIDUBAIKE, item.url));
                }
            }
            if (!TextUtil.isListEmpty(pNewsDetail.douban)) {
                for (ArrayList item : pNewsDetail.douban) {
                    entryList.add(new NewsDetailEntry((String) item.get(0), NewsDetailEntry.EntyType.DOUBAN, (String) item.get(1)));
                }
            }
            if (entryList.size() != 0) {
                if (entryList.size() > 3) {
                    entryList = new ArrayList<>(entryList.subList(0, 3));
                }
                mNewsContentDataList.add(entryList);
            }

            /**相关观点组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.relate)) {
                if (pNewsDetail.relate.size() > 3) {
                    mNewsContentDataList.add(new ArrayList(pNewsDetail.relate.subList(0, 3)));
                } else {
                    mNewsContentDataList.add(pNewsDetail.relate);
                }
            }
            /**微博组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.weibo)) {
                if (pNewsDetail.weibo.size() > 3) {
                    mNewsContentDataList.add(new ArrayList(pNewsDetail.weibo.subList(0, 3)));
                } else {
                    mNewsContentDataList.add(pNewsDetail.weibo);
                }
            }
            /**知乎组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.zhihu)) {
                if (pNewsDetail.zhihu.size() > 3) {
                    mNewsContentDataList.add(new ArrayList(pNewsDetail.zhihu.subList(0, 3)));
                } else {
                    mNewsContentDataList.add(pNewsDetail.zhihu);
                }
            }
        }
        return mNewsContentDataList;
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
