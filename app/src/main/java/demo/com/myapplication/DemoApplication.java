package demo.com.myapplication;

import android.app.Application;

import com.news.yazhidao.application.QiDianApplication;

/**
 * Created by wudi on 16/6/21.
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QiDianApplication.initQDApp(this);
    }
}
