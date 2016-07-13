package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.app.Activity;
//import android.app.DialogFragment;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.news.yazhidao.R;
//
///**
// * Created by fengjigang on 15/3/5.
// */
//public class ProgressDialogImpl extends DialogFragment {
//    public static ProgressDialogImpl show(Activity mContext){
//        ProgressDialogImpl dialog=new ProgressDialogImpl();
//        dialog.show(mContext.getFragmentManager(),"ProgressDialogImpl");
//        return dialog;
//    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStyle(0, R.style.style_self_dialog_toast);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view=inflater.inflate(R.layout.common_custom_progressbar_dialog,container,false);
//        return view;
//    }
//}
