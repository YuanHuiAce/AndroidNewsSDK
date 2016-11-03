package demo.com.myapplication;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.robotium.solo.Solo;

/**
 * Created by fiocca on 16/9/26.
 */

public class NotePadTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    @SuppressWarnings("unchecked")
    public NotePadTest() {
        super(MainActivity.class);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void test() throws Exception {
//        solo.clickOnView(solo.getView("rbnMine"));
//        solo.clickInList(20);
        Thread.sleep(5000);

        PullToRefreshListView pullToRefreshListView = (PullToRefreshListView)solo.getView("id/news_feed_listView");
        ListView listview = pullToRefreshListView.getRefreshableView();
//        solo.scrollListToBottom(listview.getCount()-5);
        for (int i=0;i<30;i++){
            solo.scrollToSide(Solo.RIGHT);
            Thread.sleep(1000);
            solo.scrollDown();
        }
        for (int i=0;i<30;i++){
            solo.scrollToSide(Solo.LEFT);
        }

//        Thread.sleep(7000);
//        int location[]=new int[2];
//        listview.getLocationOnScreen(location);//获取listiew的坐标
//        solo.drag(location[0]+500,location[0]+500,location[1],location[1]+listview.getHeight()+300,50);
        int[] location=new int[2];
        listview.getLocationOnScreen(location);
        location[1]=location[1]+listview.getBottom();
//        if(solo.waitForView(listview)){
//            //从上往下滑动
//            int newlistcount, listcount = listview.getCount();
//            Log.i("tag",listcount+"listcount");
//            while(true){
//                solo.scrollListToLine(listview, listcount);
//                solo.sleep(500);
//                solo.drag(location[0]+10f, location[0]+10f,location[1]-10f, location[0]-100f,50);
//                solo.sleep(2000);
//                newlistcount=listview.getCount();
//                Log.i("tag",newlistcount+"newlistcount");
//                if(newlistcount==listcount){
//                    break;
//                }else{
//                    listcount=newlistcount;
//                    Log.d("Tag","[Location]:  "+Integer.toString(listcount));
//                }
//            }
//        }

//        int[] location = new int[2];
//        listview.getLocationOnScreen(location);
//        location[1] = location[1] + listview.getBottom();
//        Log.i(TAG, "[Location x]:  " + Integer.toString(location[0]));
//        Log.i(TAG, "[Location y]:  " + Integer.toString(location[1]));
//        //获取上拉加载更多拖动点的坐标
//        if (solo.waitForView(listview)) {
//            int newlistcount, listcount = listview.getCount();
//            while (true) {
//                solo.scrollListToLine(listview, listcount);
//                solo.sleep(500);
//                solo.drag(location[0] + 10f, location[0] + 10f,
//                        location[1] - 10f, location[0] - 100f, 50);
//                solo.sleep(2000);
//                newlistcount = listview.getCount();
//                if (newlistcount == listcount) {
//                    break;
//                } else {
//                    listcount = newlistcount;
//                    Log.i(TAG, "[List count]:  " + Integer.toString(listcount));
//                }
//            }
//        }
        Thread.sleep(12000);
//        solo.clickOnImage(0);
//        Thread.sleep(2000);
//        solo.clearEditText(0);
//        solo.enterText((android.widget.EditText) solo.getView("edtUname"), "sylovezp");
//        solo.enterText((android.widget.EditText) solo.getView("edtUpass"),"qqqqqq");
//        Thread.sleep(1000);
//        solo.clickOnButton("登录");
//        //进入活动页面
//        solo.clickOnView(solo.getView("rbnActivities"));
//
//        //切换我的活动/热门活动/正在点评/已结束活动
//        solo.clickOnButton(0);
//        solo.clickOnText("我的活动");
//        solo.scrollDown();
//        solo.clickOnButton(0);
//        solo.clickOnText("正在点评");
//        solo.scrollDown();
//        solo.clickOnButton(0);
//        solo.clickOnText("已结束");
//        solo.scrollDown();
//        solo.clickOnButton(0);
//        solo.clickOnText("热门活动");
//        Thread.sleep(2000);
//        //进入热门活动第一条中的活动详情页
//        solo.clickOnImage(0);
//        //切换活动规则/关于影片/他们报名啦页面
//        solo.clickOnButton("关于影片");
//        //观看视频
//        solo.clickOnView(solo.getView("btnActPlay"));
//        Thread.sleep(3000);
//        solo.clickOnView(solo.getView("btnBack"));
//        //报名参加，看看是否正确记录手机号
//        solo.clickOnText("我要报名");
//        solo.clickOnView(solo.getView("btnNext"));
//        solo.clickOnView(solo.getView("btnBack"));
//
//        //退出活动详情页
//        solo.clickOnView(solo.getView("btnBack"));
//        //以下执行一个简单的登陆/退出的操作
//
//        //findElementById("rbnMine").doClick();//athrun
//        solo.clickOnView(solo.getView("rbnMine"));//当一个页面文本名称出现多个时，就不能用text了，此时可以用这个方法
//        Thread.sleep(1000);
//        solo.clickOnText("系统设置");
//        Thread.sleep(1000);
//        solo.clickOnButton("退出登录");
//        Thread.sleep(1000);

    }

    /**
     * 还有漏洞，需要补充
     * 滚动超过一屏的listview item子项，并点击它
     * <p>
     * param index：在listview中的总的索引
     * param linesNum： 一屏内listview Item的行数，比如可以指定多少行，根据不同的分辨率指定不同的行数
     * param vIndex： 一屏内listview Item有效的行数索引
     * param screenIndex： listview 指定行数的屏个数的索引
     * param screenCount： listview 指定行数的总屏数
     * <p>
     * author：andrew
     * date： 2013.05.21
     * email： 850811845@qq。com
     */
    public static void scrollOutsideScreenListItem(Solo solo, int index, int linesNum,
                                                   int vIndex, int screenIndex, int screenCount) {
        if (vIndex < (linesNum + 1)) {
            solo.clickInList(vIndex);

            solo.sleep(5000);
            vIndex++;

            solo.goBack();
            solo.sleep(3000);

            if (vIndex == linesNum) {
                DisplayMetrics outMetrics = new DisplayMetrics();
                Activity act = solo.getCurrentActivity();
                act.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

                float formX = outMetrics.widthPixels / 4;
                float formY = outMetrics.heightPixels / 4;

                if (formX < 30) {
                    formX = 30;
                }
                if (formY < (linesNum - 1)) {
                    int temp = (index % linesNum);
                    solo.scrollDownList(temp);
                    solo.sleep(3000);
                    if (screenIndex < screenCount) {
                        screenIndex++;
                    }
                    vIndex = 0;
                }
            }

        }
    }
}
