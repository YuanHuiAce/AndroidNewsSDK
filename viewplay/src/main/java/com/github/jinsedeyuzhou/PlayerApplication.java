package com.github.jinsedeyuzhou;

import android.content.Context;

/**
 * Created by Berkeley on 2/10/17.
 */

public class PlayerApplication {

    private static Context mContext;
    private static PlayerApplication mInstance;
    public static boolean isSound;

    public static void initApp(Context context) {
        mContext = context;
        mInstance = new PlayerApplication();


    }

    private PlayerApplication() {
        mInstance = this;
    }

    public static   Context getAppContext() {
        return mContext;
    }

    public static synchronized PlayerApplication getInstance() {
        if (null == mInstance) {
            mInstance = new PlayerApplication();
        }
        return mInstance;
    }
}
