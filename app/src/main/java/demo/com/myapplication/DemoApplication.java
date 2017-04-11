package demo.com.myapplication;

import android.app.Application;
import android.text.TextUtils;

import com.news.yazhidao.application.QiDianApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by wudi on 16/6/21.
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QiDianApplication.initQDApp(this);
//        Context ctx =getApplicationContext();
//        // 获取当前包名
//        String packageName = getPackageName();
//        // 获取当前进程名
//        String processName = getProcessName(android.os.Process.myPid());
//        // 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(ctx);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
//        // 初始化Bugly
////        CrashReport.initCrashReport(this, "876dac1311", isDebug, strategy);
//        CrashReport.initCrashReport(getApplicationContext(), "876dac1311", true);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
