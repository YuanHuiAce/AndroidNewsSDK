package demo.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.utils.manager.SharedPreManager;
import com.testin.agent.TestinAgent;
import com.testin.agent.TestinAgentConfig;

public class MainActivity extends AppCompatActivity {
    RelativeLayout newsLayout;
    MainView mainView;
    private TextView mFirstAndTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SharedPreManager.mInstance(this).save("flag","text1","一天天的测试！");
        //activity 跳转
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = SharedPreManager.mInstance(MainActivity.this).getInt("showflag", "textSize");
                if(size == MainView.FONTSIZE.TEXT_SIZE_BIG.getfontsize()){
                    mainView.setTextSize(MainView.FONTSIZE.TEXT_SIZE_NORMAL);
                }else{
                    mainView.setTextSize(MainView.FONTSIZE.TEXT_SIZE_BIG);
                }
            }
        });
        mFirstAndTop = (TextView)findViewById(R.id.mFirstAndTop);
        mFirstAndTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.backFirstItemAndRefreshData();
            }
        });
        //添加View
        newsLayout = (RelativeLayout)findViewById(R.id.newsLayout);
        mainView = new MainView(this); //传入的activity是FragmentActivity
        /**梁帅：修改智能模式（不显示图片）*/
//        mainView.setNotShowImages(false);
        /**梁帅：修改文字大小的方法*/
        mainView.setTextSize(MainView.FONTSIZE.TEXT_SIZE_NORMAL);
        /**梁帅：修改屏幕是否常亮的方法*/
        mainView.setKeepScreenOn(true);
        newsLayout.addView(mainView.getNewsView());




        TestinAgent.init(this, "ed9dfab583e76e3825fd0bbdcf513dc8", "qidian");
        TestinAgentConfig config = new TestinAgentConfig.Builder(this)
                .withAppKey("ed9dfab583e76e3825fd0bbdcf513dc8")   // 您的应用的 AppKey,如果已经在 Manifest 中配置则此处可略
                .withAppChannel("qidian")   // 发布应用的渠道,如果已经在 Manifest 中配置则此处可略
                .withUserInfo("userinfo")   // 用户信息-崩溃分析根据用户记录崩溃信息
                .withDebugModel(true)   // 输出更多SDK的debug信息
                .withErrorActivity(true)   // 发生崩溃时采集Activity信息
                .withCollectNDKCrash(true)   // 收集NDK崩溃信息
                .withOpenCrash(true)   // 收集崩溃信息开关
                .withOpenEx(true)   // 是否收集异常信息
                .withReportOnlyWifi(true)   // 仅在 WiFi 下上报崩溃信息
                .withReportOnBack(true)   // 当APP在后台运行时,是否采集信息
                .withQAMaster(true)   // 是否收集摇一摇反馈
                .withCloseOption(false).withLogCat(true) // 是否在摇一摇菜单展示‘关闭摇一摇选项’
                .build();  // 是否系统操作信息
        TestinAgent.init(config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //设置频道的回调
        mainView.onActivityResult(requestCode, resultCode, data);
    }
    //梁帅: 点击返回如果不喜欢窗口是显示的，隐藏它；
    //如果是不显示的直接退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mainView != null && mainView.closePopWindow()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mainView.unregisterNetWorkReceiver();
        super.onDestroy();
    }




}
