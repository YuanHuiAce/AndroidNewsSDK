package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fengjigang on 15/11/24.
 * 挖掘的新闻详情
 */
public class NewsDetailForDigger implements Serializable {


    /**
     * status : 0
     * weibo : []
     * douban : {"url":"","title":""}
     * searchItems : [{"url":"http://sports.sohu.com/lanqiu.shtml","title":"篮球"},{"url":"http://baike.baidu.com/view/2278.htm","title":"篮球（球类运动）"},{"url":"http://www.4399.com/special/35.htm","title":"篮球小游戏,篮球小游戏大全,4399篮球小游戏全集,4399小游戏"},{"url":"http://www.baike.com/wiki/%E7%AF%AE%E7%90%83","title":"篮球"},{"url":"http://match.sports.sina.com.cn/basketball/","title":"篮球比分直播"},{"url":"http://tv.sohu.com/s2013/basketball/","title":"篮球"},{"url":"http://baike.sogou.com/v673577.htm","title":"篮球 "},{"url":"http://cctv.cntv.cn/lm/lanqiugongyuan/","title":"篮球公园\u2014CCTV5\u2014官网\u2014首页"},{"url":"http://sports.cntv.cn/basketball/index.shtml","title":"篮球"},{"url":"http://www.bing.com/knows/%E3%80%8A%E7%AF%AE%E7%90%83%E3%80%8B?mkt=zh-cn","title":"《篮球》 "},{"url":"http://sports.sina.com.cn/cba/2015-11-23/doc-ifxkwuwv3563046.shtml","title":"街头篮球未来联赛循环赛4队晋级 圆一个街球梦 "},{"url":"http://sports.sina.com.cn/cba/2015-11-23/doc-ifxkwuwv3562691.shtml","title":"街头篮球未来联赛循环赛四队晋级 圆你街球梦 "},{"url":"http://enews.xwh.cn/shtml/xwhb/20151124/235819.shtml","title":"草根篮球高手想\u201c挑战\u201d东北虎吗?"},{"url":"http://www.eol.cn/shaanxi/campus/201511/t20151124_1341402.shtml","title":"西安医学院女篮闯入第十八届CUBA中国大学生篮球联赛"},{"url":"http://www.jiangxi.gov.cn/xzx/jxyw/sxyw/201511/t20151124_1230201.html","title":"宜丰女篮小将获全省百县青少年篮球运动会团体二等奖"},{"url":"http://sports.xinmin.cn/2015/11/24/28986165.html","title":"2015黑龙江省大众三人篮球挑战赛开赛"},{"url":"http://www.nnrb.com.cn/html/2015-11/24/content_187195.htm","title":"万村农民篮球赛奏响体育节尾声 - 南宁日报多媒体数字报文章"},{"url":"http://education.cqnews.net/html/2015-11/24/content_35831941.htm","title":"青年职业技术学院获第四届高校篮球比赛冠军 "},{"url":"http://mzrb.meizhou.cn/html/2015-11/24/content_83299.htm","title":"蕉岭\u201c县长杯\u201d篮球联赛鸣哨 "},{"url":"http://news.idoican.com.cn/qdwk/html/2015-11/24/content_5559095.htm?div=0","title":"文山城区学校篮球赛落幕"},{"url":"http://sports.163.com/15/1123/16/B94CRH0I00052UUC.html","title":"北京篮球1喜1忧 京媒调侃:大哥日子难小弟挺红火"},{"url":"http://sports.qq.com/a/20151123/037904.htm?tu_biz=v1","title":"在伊拉克 她们用篮球远离战争"},{"url":"http://sports.sina.com.cn/l/2015-11-23/doc-ifxkwuwx0295696.shtml?from=wap","title":"[彩客网]红红篮篮双色球第15138期:蓝球看好奇码 "},{"url":"http://news.163.com/15/1123/07/B93EJ0MS00014AED.html","title":"来吧,一起感受民间篮球热"},{"url":"http://news.ifeng.com/a/20151123/46348269_0.shtml","title":"校园掀起\u201c篮球旋风\u201d"},{"url":"http://news.ifeng.com/a/20151123/46356819_1.shtml","title":"第18届CUBA中国大学生篮球联赛(四川)预选赛开赛"},{"url":"http://sports.sina.com.cn/games/ftx2/2015-11-23/114512874.shtml","title":"《范特西篮球2》12月4日服务器合区公告"},{"url":"http://money.163.com/15/1123/11/B93RRJKE00254TI5.html","title":"打出团队篮球 山西力克辽宁"},{"url":"http://news.cntv.cn/2015/11/23/VIDE1448276771893456.shtml","title":"[湖南新闻联播]怀化:山里女娃打篮球 打进全国八强"},{"url":"http://sports.163.com/15/1123/16/B94C4EGA00051CA1.html","title":"ESPN:快乐篮球成勇士制胜武器 小AI:我像个教练"},{"url":"http://cbachina.sports.sohu.com/20151124/n427903371.shtml","title":"首钢篮球获出境游管家 众信旅游成其指定旅行社"},{"url":"http://sports.163.com/photoview/69PR0005/134681.html","title":"CBL篮球联赛3V3成都站南山队勇夺冠军"},{"url":"http://sports.gmw.cn/2015-11/24/content_17841333.htm","title":"詹皇致敬大O:篮球联系起我们!感谢你奥斯卡"},{"url":"http://www.tibet.cn/news/d/1448284134609.shtml","title":"杭州有支爷爷级民间篮球队 平均年龄超过70岁(图)"},{"url":"http://news.sina.com.cn/c/2015-11-24/doc-ifxkwaxv2697475.shtml","title":"草根篮球高手想\"挑战\"东北虎吗?"},{"url":"http://news.youth.cn/gn/201511/t20151123_7341104.htm","title":"第18届CUBA中国大学生篮球联赛(四川)预选赛开赛"},{"url":"http://cnews.chinadaily.com.cn/2015-11/23/content_22511335.htm","title":"街头篮球未来联赛 圆你一个街头篮球梦"},{"url":"http://www.cnr.cn/ent/list/20151123/t20151123_520567712.shtml","title":"秦海璐谈二胎:老公想生支篮球队"},{"url":"http://hb.sina.com.cn/hezuo/shangxun/2015-11-24/1143242089.html","title":"京金联:篮球赛为企业文化添活力"},{"url":"http://www.nmg.xinhuanet.com/xwzx/tysj/2015-11/24/c_1117241761.htm","title":"北京篮球1喜1忧 京媒调侃:大哥日子难小弟挺红火"},{"url":"http://sports.sohu.com/20151124/n427903384.shtml","title":"首钢篮球获出境游管家 众信旅游成其指定旅行社"},{"url":"http://sports.sohu.com/20151124/n427865287.shtml","title":"街头篮球赛循环赛第二第三场结束 四支队晋级"},{"url":"http://news.163.com/15/1124/13/B96J5S9S00014AEE.html","title":"青年职业技术学院获第四届高校篮球比赛冠军(图)"},{"url":"http://www.9game.cn/lqgs/582064.html","title":"感恩回馈季《篮球高手》嗨翻新赛季"},{"url":"http://news.163.com/15/1124/06/B95QAUI100014AED.html","title":"中国大学生篮球联赛（四川）预选赛开赛"},{"url":"http://news.163.com/15/1124/04/B95JGFM000014Q4P.html","title":"不强迫儿子练篮球 库里父亲教育有一套"},{"url":"http://news.163.com/15/1124/00/B9583UF600014AED.html","title":"草根篮球高手想\u201c挑战\u201d东北虎吗？"},{"url":"http://sc.people.com.cn/n/2015/1123/c345509-27161404.html","title":"第18届CUBA中国大学生篮球联赛（四川）预选赛开赛"},{"url":"http://www.chinadaily.com.cn/hqgj/jryw/2015-11-23/content_14352375.html","title":"杭州有支爷爷级民间篮球队 平均年龄超过70岁(图)"},{"url":"http://games.qq.com/a/20151123/038667.htm","title":"街头篮球龙之队集结完毕全力备战"},{"url":"http://news.163.com/15/1123/12/B93UIVFD00014AEE.html","title":"岳麓区学士街道首届\u201c岳麓智谷杯\u201d篮球赛开幕"},{"url":"http://news.163.com/15/1123/11/B93RRI1K00014JB6.html","title":"打出团队篮球 山西力克辽宁"},{"url":"http://roll.sohu.com/20151123/n427613530.shtml","title":"篮球运动 意识为上(图)"},{"url":"http://sports.163.com/15/1123/08/B93ENLSQ00052UUC.html","title":"来吧，一起感受民间篮球热"},{"url":"http://yule.sohu.com/20151123/n427602868.shtml","title":"秦海璐谈二胎：老公想生支篮球队"},{"url":"http://news.163.com/15/1123/07/B93C44KA00014Q4P.html","title":"校园掀起\u201c篮球旋风\u201d"},{"url":"http://roll.sohu.com/20151123/n427586783.shtml","title":"\u201c金苑杯\u201d第二季篮球联赛举行"},{"url":"http://sports.qq.com/a/20151123/000347.htm","title":"关注中国篮球官方微信公众号 享更多CBA动态"},{"url":"http://news.hexun.com/2015-11-22/180728081.html","title":"（体育）（6）篮球\u2014CBA常规赛：广东胜山东"},{"url":"http://sports.qq.com/a/20151122/031106.htm","title":"高清：篮球宝贝大秀双节棍 火辣热舞不惧走光"},{"url":"http://roll.sohu.com/20151120/n427396095.shtml","title":"中国大学生 篮球 联赛青海赛区选拔赛开赛"},{"url":"http://news.sohu.com/20151120/n427253238.shtml","title":"美国9岁女童拥有 篮球 神技 胯下来回运两只球（图）"},{"url":"http://lq2.7m.cn/news/20151124/143238.shtml","title":"篮球 推荐：勇士VS湖人 勇士打破历史记录"},{"url":"http://lq2.7m.cn/news/20151124/143237.shtml","title":"篮球 推荐：灰熊VS小牛 攻守合一灰熊擒牛"},{"url":"http://news.hexun.com/2015-11-24/180766551.html","title":"2015全国U16男子 篮球 集训赛沪上揭幕"},{"url":"http://biz.xinmin.cn/2015/11/23/28984256.html","title":"第18届CUBA中国大学生 篮球 联赛（四川）预选赛开赛"},{"url":"http://roll.sohu.com/20151119/n427112729.shtml","title":"关于公示《中国 篮球 协会赛事注册管理办法》的通知"},{"url":"http://news.766.com/dl/2015-11-23/2653502.shtml","title":"街头 篮球 龙之队集结完毕全力备战"},{"url":"http://news.dahe.cn/2015/11-23/106036105.html","title":"活力 篮球 健康生活"}]
     * content : [{"text":"和上周日的赛程相比，昨天两支北京球队的对手正好换了个个儿。主场迎战上海队，北控队替\u201c老大哥\u201d报了仇，顺利赢下比赛。客场挑战福建队，首钢队没重演\u201c小老弟\u201d的好戏，败走晋江。同为四胜四负，两支球队的处境却不尽相同，\u201c小老弟\u201d势头风风火火，\u201c老大哥\u201d日子有些艰难。"},{"src":"http://img6.cache.netease.com/sports/2015/11/23/2015112316490607ba4_500.jpg"},{"text":"赛季最高失分"},{"text":"老马累了 首钢难了"},{"text":"面对缺少王哲林的福建队，北京首钢队还是以102比113输掉了比赛。凭113分创下球队本赛季的失分纪录。8轮过后，战绩仅为4胜4负，跌出前八。身为卫冕冠军，四年三冠的北京队真正遇到了困难。"},{"text":"三外援的球队难打，在王哲林伤停后，福建队的进攻球权更是集中在哈提布、拜克斯和菲莫斯这3人手中。合力砍下73分。福建队三外援共计出手51次，占据全队总出手的70%，本土球员一共才出手了22次。北京队三分球31投11中，外线命中率仅为35.48%。福建队只用16次出手就投进10记三分，外线命中率高达62.5%。不仅外线效率惊人，福建队两分球的投篮命中率也比北京队高出两个百分点。在两队均抢下11个进攻篮板的情况下，福建队的总篮板数以38比31占优。"},{"text":"马布里太累了，全场拼了36分钟，送出18分、8个篮板和8次助攻。比赛的最后时候，马布里还像往常一样要靠一己之力来对抗对手，但在并未产生任何身体接触的情况下，老马腿下一软，摔了一跤。"},{"text":"从比赛开始，马布里有意减少出手次数，将更多机会留给队友，将自己的体能和精力留到关键的第四节。前三节战罢，老马只出手6次。末节一次次冲锋，让老马的体能急速下滑。毕竟是连续客场，毕竟他已近39岁。至少就本场比赛而言，北京队没有人能帮助马布里分担压力。关键的第四节，孙悦投篮磕筐而出，吉喆近在咫尺的上篮被封盖，翟晓川在关键时刻被冻结。"},{"text":"最近两个赛季，北京队常规赛排名均不出色，很大程度上是有意为之，马布里为季后赛蓄力。本赛季却有所不同。无论是此役打福建，还是此前打上海，老马都是拼尽了全力却依旧失利。"},{"text":"4胜4负，首钢队在联赛前8轮就输了4场，积分榜上跌出了前8。如今对手们实力不断增强，卫冕冠军的头衔又使得自己成为了众矢之的，首钢队究竟靠什么走出困境杀出重围？对闵鹿蕾、马布里、首钢队来说，这都是亟待解决的问题。"},{"text":"平上赛季胜场数"},{"text":"赖特醒了 北控活了"},{"text":"一直状态低迷的赖特终于复苏，北控队也在主场以99比89战胜了上海队。打了8轮比赛，北控队取得了4场胜利，已经平了上赛季的胜场数，三外援全部进入状态。"},{"text":"在本赛季加盟北控队后，赖特的表现就一直被外界诟病。CBA常规赛前7轮，赖特场均只有13分、4篮板进账，显然不是一张合格的成绩单。在上场北控不敌八一的比赛中，赖特只有1分钟的出场时间，加之对阵福建队中途赖特还不辞而别，一度有北控队想换掉赖特的说法。"},{"text":"就在北京大雪之日，赖特找回了手感。对阵上海队，他里突外投，砍下全场最高的26分。与之前的表现相比，赖特像换了一个人。第三节，赖特机敏抢断，并助攻贺希宁完成空中接力，率领北控打出小高潮。上海队不得不申请暂停，赖特显得十分兴奋，与队友一一击掌之后，才回到替补席，这种场面在赖特加盟北控队后还是头一次看到。"},{"text":"北控主教练王锡东表扬了赖特的表现，\u201c赖特的状态比以前要强很多，他在今天找到了自己的节奏。\u201d是什么使得赖特的状态有如此大的转变呢？王锡东给出了答案，赖特自我心态的调整和教练组布置的战术令他复苏。\u201c赖特的问题主要是出在心态上，最近我一直在和他沟通，\u201d王锡东说，\u201c他本身不是一个强攻型的队员，而是一个很好的配角，一个很好的投手，这场比赛我布置的是让卡姆拉尼和巴蒂斯塔在强侧吸引防守，当球转到弱侧的时候，就是他发挥的时候，他今天的节奏非常好。并且他在防守端也起到了作用，抢到了8个篮板球并贡献了4次抢断。\u201d"},{"text":"北控队的取胜不仅仅单靠赖特一人，全队5人得分上双，巴蒂斯塔19分17个篮板，卡姆拉尼10分11次助攻，两位本土选手张翌星和于梁也取得两位数得分。"},{"text":"力克上海队，是北控队的第四场胜利，王锡东显得非常兴奋。\u201c这场比赛我们很好地贯彻了赛前布置的任务，控制了对方主要的得分点，主要赢在了防守上。\u201d王锡东说。随着赖特的复苏，他与巴蒂斯塔、卡姆拉尼的三外援组合实力更上一层楼，北控队的前景更让人期待。"}]
     * zhihu : [{"url":"篮球评论圈里有没有比张佳玮更出色的篮球评论员?","user":"spadek","title":"http://www.zhihu.com/question/37345668"},{"url":"篮球界有哪些帅哥?","user":"王睿-Ray","title":"http://www.zhihu.com/question/20584731"},{"url":"如何评价科比的篮球智商?","user":"sailor","title":"http://www.zhihu.com/question/21746439"},{"url":"姚明的成就主要源自身高还是篮球天赋和智商?","user":"杨夏","title":"http://www.zhihu.com/question/30840098"},{"url":"如何理解sd粉无法接受喜爱黑子的篮球?","user":"","title":"http://www.zhihu.com/question/27227376"},{"url":"为啥男生晚上在打篮球,他们看得见么?","user":"郑文龙","title":"http://www.zhihu.com/question/23859322"},{"url":"为什么中国在\u201c三大球\u201d(篮球、足球、排球)领域的成绩远不如\u201c三小球\u201d(乒乓球、羽毛球、网球)的成绩?","user":"苏群","title":"http://www.zhihu.com/question/20014159"},{"url":"为什么詹姆斯总是带着团队篮球的标签,有那么多助攻,但是,在骑士跟热火,最后都玩成一个人的球队了?","user":"王珦","title":"http://www.zhihu.com/question/24165443"},{"url":"篮球运动的最高境界是什么?","user":"激动战士矮达","title":"http://www.zhihu.com/question/34453998"},{"url":"篮球领域有哪些经典笑话?","user":"长缨在手","title":"http://www.zhihu.com/question/26291599"}]
     * postImg : http://img6.cache.netease.com/sports/2015/11/23/2015112316490607ba4_500.jpg
     * baike : {"url":"","abs":"","title":""}
     */

    private String status;
    /**
     * url :
     * title :
     */

    private DoubanEntity douban;
    private String postImg;
    /**
     * url :
     * abs :
     * title :
     */

    private BaikeEntity baike;
    private List<?> weibo;
    /**
     * url : http://sports.sohu.com/lanqiu.shtml
     * title : 篮球
     */

    private List<SearchItemsEntity> searchItems;
    /**
     * text : 和上周日的赛程相比，昨天两支北京球队的对手正好换了个个儿。主场迎战上海队，北控队替“老大哥”报了仇，顺利赢下比赛。客场挑战福建队，首钢队没重演“小老弟”的好戏，败走晋江。同为四胜四负，两支球队的处境却不尽相同，“小老弟”势头风风火火，“老大哥”日子有些艰难。
     */

    private List<ContentEntity> content;
    /**
     * url : 篮球评论圈里有没有比张佳玮更出色的篮球评论员?
     * user : spadek
     * title : http://www.zhihu.com/question/37345668
     */

    private List<ZhihuEntity> zhihu;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDouban(DoubanEntity douban) {
        this.douban = douban;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public void setBaike(BaikeEntity baike) {
        this.baike = baike;
    }

    public void setWeibo(List<?> weibo) {
        this.weibo = weibo;
    }

    public void setSearchItems(List<SearchItemsEntity> searchItems) {
        this.searchItems = searchItems;
    }

    public void setContent(List<ContentEntity> content) {
        this.content = content;
    }

    public void setZhihu(List<ZhihuEntity> zhihu) {
        this.zhihu = zhihu;
    }

    public String getStatus() {
        return status;
    }

    public DoubanEntity getDouban() {
        return douban;
    }

    public String getPostImg() {
        return postImg;
    }

    public BaikeEntity getBaike() {
        return baike;
    }

    public List<?> getWeibo() {
        return weibo;
    }

    public List<SearchItemsEntity> getSearchItems() {
        return searchItems;
    }

    public List<ContentEntity> getContent() {
        return content;
    }

    public List<ZhihuEntity> getZhihu() {
        return zhihu;
    }

    public static class DoubanEntity implements Serializable {
        private String url;
        private String title;

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class BaikeEntity implements Serializable {
        private String url;
        private String abs;
        private String title;

        public void setUrl(String url) {
            this.url = url;
        }

        public void setAbs(String abs) {
            this.abs = abs;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public String getAbs() {
            return abs;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class SearchItemsEntity implements Serializable {
        private String url;
        private String title;

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class ContentEntity implements Serializable {
        private String text;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        private String src;

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class ZhihuEntity implements Serializable {
        private String url;
        private String user;
        private String title;

        public void setUrl(String url) {
            this.url = url;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public String getUser() {
            return user;
        }

        public String getTitle() {
            return title;
        }
    }
}
