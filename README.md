##1.4更新内容：
    1.加入广点通广告
    2.修复专题页面新闻展示位置问题

传入地理位置信息方法：在mainview中的setLocation()传入，经纬度，省，市，县。
设置广告位id：在mainview中的setADAppId()传入，appId,nativePosID。
添加广点通代码混淆：app目录下proguard-rules.pro中##广点通。

##1.3更新内容：
    1.能够正确获取IMEI
    2.增加日夜间切换模式
    3.修复图片显示大小错位问题

IMEI获取方法：在mainview中的initializeViews()方法中申请获取权限当用户允许权限后通过getDeviceImei()方法获取IMEI并存入SharedPreManager中，在NewsFeedFgt类中调用getAdMessage()方法，在其中设置IMEI值从而获得广告


##1.2更新内容：
    1.添加专题频道
    2.更换加载动画
    3.优化详情页内存问题
    4.在MianView中调用setLocation方法传入相应地理位置信息可对用户做到更精确的新闻推荐
  
# AndroidNewsSDK
 ![Version Status](https://img.shields.io/badge/AndroidNewsSdk-1.0.1-yellow.svg)
 ![Platform](https://img.shields.io/badge/platform-android-brightgreen.svg)
##主要依赖的第三方框架为
    compile 'com.android.support:appcompat-v7:22.2.1'
    compile 'com.mcxiaoke.volley:library:1.0.19'
    compile files('libs/ormlite-core-4.48.jar')
    compile files('libs/ormlite-android-4.48.jar')
    compile 'com.google.code.gson:gson:2.3.1'
    compile files('libs/glide-3.6.1.jar')
    compile files('libs/tbs_sdk_thirdapp_v2.2.0.1096_36549_sharewithdownload_withoutGame_obfs_20160830_211645.jar')
##项目结构（主要部分）
```
├── qidiansdk/src/main
|   ├──java/news/yazhidao
│   │    ├──adapter    
│   │    │     ├── ChannelNormalAdapter/ChannelSelectedAdapter   频道选择适配器
│   │    │     ├── NewsDetailFgtAdapter                          新闻详情页适配器
│   │    │     └── NewsFeedAdapter                               新闻列表页适配器
│   │    ├──common                                               定义activity、fragment等基类，设置常量与请求接口地址
│   │    ├──database
│   │    │     ├──DatabaseHelper                                 管理整体数据库 用于创建升级等操作
│   │    │     ├──ChannelItemDao                                 处理频道数据
│   │    │     ├──NewsDetailCommentDao                           处理评论数据
│   │    │     └──NewsFeedDao                                    处理新闻数据
│   │    ├──entity                                               新闻，详情等实体类
│   │    ├──net.volley                                           网络请求封装类
│   │    ├──pages   
│   │    │     ├── ChannelOperateAty                             新闻频道管理页面
│   │    │     ├── MainAty                                       主页面
│   │    │     ├── NewsCommentFgt                                新闻评论展示
│   │    │     ├── NewsDetailAty2                                新闻详情
│   │    │     ├── NewsDetailFgt                                 新闻详情展示
│   │    │     ├── NewsDetailWebviewAty                          新闻详情webview展示
│   │    │     ├── NewsFeedFgt                                   新闻列表展示
│   │    │     ├── TopicSearchAty                                新闻查找页面
│   │    │     └── PlayVideoAty                                  视频播放页面
│   │    ├──receiver                                             Home键监听封装
│   │    ├──utils                                                工具类(json解析，压缩包解压等)
│   │    ├──widget                                               自定义view
│   │
├── jniLibs 播放视频用的so文件
├── res  # 资源文件，如图片、音频等
└── AndroidMainfest.xml  
```
##Request API
[API](https://github.com/YuanHuiAce/AndroidNewsSDK/wiki/request-Api "悬停显示")  
