package com.news.yazhidao.net.request;

import android.content.Context;

import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.UploadCommentListener;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

//import com.news.yazhidao.utils.manager.AliYunOssManager;

/**
 * Created by fengjigang on 15/6/4.
 * 上传用户评论的请求
 */
public class UploadCommentRequest {
    /**文本类型段落评论*/
    public static final String TEXT_PARAGRAPH="text_paragraph";
    /**文本类型全文评论*/
    public static final String TEXT_DOC="text_doc";
    /**语音类型段落评论*/
    public static final String SPEECH_PARAGRAPH="speech_paragraph";
    /**语音类型全文评论*/
    public static final String SPEECH_DOC="speech_doc";


    /**
     * 用于上传用户评论，评论可以段落评论，也可以是全文的评论，根据type来决定
     * @param mContext
     * @param sourceUrl 新闻源的url
     * @param comment 用户的评论，可以是文本也可以是语音，如果是语音的话，则此参数为语音文件的绝对路径
     * @param paragraphIndex 如果是段落评论的话，此参数表示被评论的段落索引，如果是全文评论，则需指定该值为-1
     * @param type 评论的类型
     * @param speechDuration 如果是语音评论，此参数即语音的时长
     * @param listener 上传结果回调  @see #TEXT_PARAGRAPH
     * @see #TEXT_DOC
     * @see #SPEECH_PARAGRAPH
     * @see #SPEECH_DOC
     */
    public static void uploadComment(Context mContext, String sourceUrl, String comment, String paragraphIndex, String type, int speechDuration, final UploadCommentListener listener){
        final NetworkRequest request=new NetworkRequest(HttpConstant.URL_GET_NEWS_CONTENT, NetworkRequest.RequestMethod.POST);
        List<NameValuePair> pairs=new ArrayList<>();
        //如果是语音评论，则须上传到阿里云的OSS
//        if(SPEECH_PARAGRAPH.equals(type)||SPEECH_DOC.equals(type)){
//           comment= AliYunOssManager.getInstance(mContext).uploadSpeechFile(comment);
//        }
        Logger.i("jigang","-+++--"+comment+"+++"+speechDuration);
        //此处默认用户是已经登录过的
        User user = SharedPreManager.getUser(mContext);
        pairs.add(new BasicNameValuePair("sourceUrl",sourceUrl));
        pairs.add(new BasicNameValuePair("srcText",comment));
        pairs.add(new BasicNameValuePair("desText",""));
        pairs.add(new BasicNameValuePair("paragraphIndex",paragraphIndex));
        pairs.add(new BasicNameValuePair("type",type));
        pairs.add(new BasicNameValuePair("srcTextTime",speechDuration/1000+""));
        pairs.add(new BasicNameValuePair("uuid",user.getUuid()));
        pairs.add(new BasicNameValuePair("userIcon",user.getUserIcon()));
        pairs.add(new BasicNameValuePair("userName",user.getUserName()));
        pairs.add(new BasicNameValuePair("userId",user.getUserId()));
        pairs.add(new BasicNameValuePair("platformType", user.getPlatformType()));

        request.setParams(pairs);
        request.setCallback(new JsonCallback<NewsDetailAdd.Point>() {
            @Override
            public void success(NewsDetailAdd.Point result) {
                if (listener != null) {
                    listener.success(result);
                }
            }

            @Override
            public void failed(MyAppException exception) {
                if (listener != null) {
                    listener.failed();
                }
            }
        }.setReturnClass(NewsDetailAdd.Point.class));
        request.execute();
    }

}
