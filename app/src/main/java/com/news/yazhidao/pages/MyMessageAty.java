package com.news.yazhidao.pages;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.news.yazhidao.R;

/**
 * Created by fengjigang on 16/4/8.
 */
public class MyMessageAty extends Activity implements View.OnClickListener {

    private View mMessagetLeftBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView();
        initializeViews();
        loadData();
    }

    protected void setContentView() {
        setContentView(R.layout.aty_my_message);
    }

    protected void initializeViews() {
        mMessagetLeftBack = findViewById(R.id.mMessagetLeftBack);
        mMessagetLeftBack.setOnClickListener(this);
    }


    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mMessagetLeftBack:
                finish();
                break;
        }
    }
}
