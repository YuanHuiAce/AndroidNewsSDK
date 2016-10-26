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
