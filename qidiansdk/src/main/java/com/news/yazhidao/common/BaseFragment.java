package com.news.yazhidao.common;

//
//import com.umeng.analytics.MobclickAgent;

import android.support.v4.app.Fragment;

/**
 * Created by fengjigang on 16/3/8.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(getActivity());
    }
}
