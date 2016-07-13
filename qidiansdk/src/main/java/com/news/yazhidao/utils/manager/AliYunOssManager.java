package com.news.yazhidao.utils.manager;//package com.news.yazhidao.utils.manager;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.alibaba.sdk.android.oss.OSSService;
//import com.alibaba.sdk.android.oss.OSSServiceProvider;
//import com.alibaba.sdk.android.oss.callback.GetBytesCallback;
//import com.alibaba.sdk.android.oss.callback.SaveCallback;
//import com.alibaba.sdk.android.oss.model.AccessControlList;
//import com.alibaba.sdk.android.oss.model.AuthenticationType;
//import com.alibaba.sdk.android.oss.model.ClientConfiguration;
//import com.alibaba.sdk.android.oss.model.OSSException;
//import com.alibaba.sdk.android.oss.model.TokenGenerator;
//import com.alibaba.sdk.android.oss.storage.OSSBucket;
//import com.alibaba.sdk.android.oss.storage.OSSData;
//import com.alibaba.sdk.android.oss.util.OSSToolKit;
//import com.news.yazhidao.utils.DateUtil;
//import com.news.yazhidao.utils.Logger;
//import com.news.yazhidao.utils.TextUtil;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.net.URLDecoder;
//
///**
// * Created by fengjigang on 15/6/4.
// * 阿里云OSS存储管理类
// */
//public class AliYunOssManager {
//    private OSSService ossService;
//    private OSSBucket sampleBucket;
//    private Context mContext;
//    private static AliYunOssManager mInstance;
//    //上传语音失败重试的次数
//    private int retryTime;
//    private AliYunOssManager(Context mContext){
//        this.mContext=mContext;
//        initConfig();
//        authorize();
//        initBucket();
//    }
//    public static AliYunOssManager getInstance(Context mContext){
//        if(mInstance==null){
//            mInstance=new AliYunOssManager(mContext);
//        }
//        return mInstance;
//    }
//
//    /**
//     * 下载语音
//     * @param url 语音的url
//     * @param callback 下载回调
//     */
//    public void downloadSpeechFile(String url, GetBytesCallback callback){
//         if(TextUtils.isEmpty(url)){
//             return;
//         }
//        OSSData ossData = ossService.getOssData(sampleBucket, url.substring("http://data.deeporiginalx.com/".length()));
//        if(callback!=null){
//            ossData.getInBackground(callback);
//        }
//
//    }
//    /**
//     * 上传语音文件到阿里云的OSS
//     * @param filePath 语音文件的绝对路径
//     */
//    public String uploadSpeechFile(final String filePath) {
//        try {
//            if(TextUtils.isEmpty(filePath)){
//                return filePath;
//            }
//            InputStream inputStream = new FileInputStream(new File(filePath));
//            final OSSData ossData = ossService.getOssData(sampleBucket, DateUtil.getCurrentDate()+"/"+filePath.substring(filePath.lastIndexOf("/")+1));
//            ossData.setData(TextUtil.toByteArray(inputStream), "raw"); // 指定需要上传的数据和它的类型
//            ossData.enableUploadCheckMd5sum(); // 开启上传MD5校验
//            ossData.uploadInBackground(new SaveCallback() {
//                @Override
//                public void onSuccess(String objectKey) {
//                    retryTime=0;
//                    Log.e("jigang", "upload speech file success,objKey=" + objectKey);
//                }
//
//                @Override
//                public void onProgress(String objectKey, int byteCount, int totalSize) {
//
//                }
//
//                @Override
//                public void onFailure(String objectKey, OSSException ossException) {
//                    Logger.e("jigang","upload speech fail==="+ossException.getMessage());
//                    if(retryTime++<=3){
//                        uploadSpeechFile(filePath);
//                    }
//                }
//            });
//            String url = ossData.getResourceURL("QK8FahuiSCpzlWG8", Integer.MAX_VALUE);
//            return URLDecoder.decode(url, "utf-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    private void initBucket() {
//        sampleBucket = ossService.getOssBucket("news-baijia-speech");
//        sampleBucket.setBucketACL(AccessControlList.PUBLIC_READ); // 指明该Bucket的访问权限
//        sampleBucket.setBucketHostId("oss-cn-qingdao.aliyuncs.com"); // 指明该Bucket所在数据中心的域名或已经绑定Bucket的CNAME域名
//        sampleBucket.setCdnAccelerateHostId("data.deeporiginalx.com"); // 设置指向CDN加速域名的CNAME域名
//    }
//    private void authorize() {
//        final String accessKey = "QK8FahuiSCpzlWG8"; // 实际使用中，AK/SK不应明文保存在代码中
//        final String secretKey = "TGXhTCwUoEU4yNEGsfZSDvp0dNqw2p";
//        ossService.setAuthenticationType(AuthenticationType.ORIGIN_AKSK);
//        ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() {
//            @Override
//            public String generateToken(String httpMethod, String md5, String type, String date, String ossHeaders,
//                                        String resource) {
//                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date
//                        + "\n" + ossHeaders + resource;
//
//                return OSSToolKit.generateToken(accessKey, secretKey, content);
//            }
//        });
//    }
//
//    private void initConfig() {
//        ossService = OSSServiceProvider.getService();
//        ossService.setGlobalDefaultHostId("oss-cn-qingdao.aliyuncs.com");
//        ossService.setApplicationContext(mContext);
////        ossService.setCustomStandardTimeWithEpochSec(currentEpochTimeInSec); // epoch时间，从1970年1月1日00:00:00 UTC经过的秒数
//
//        ClientConfiguration conf = new ClientConfiguration();
//        conf.setConnectTimeout(15 * 1000); // 设置建连超时时间，默认为30s
//        conf.setSocketTimeout(15 * 1000); // 设置socket超时时间，默认为30s
//        conf.setMaxConnections(50); // 设置全局最大并发连接数，默认为50个
//        ossService.setClientConfiguration(conf);
//    }
//}
