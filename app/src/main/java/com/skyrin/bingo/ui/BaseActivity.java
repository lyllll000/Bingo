package com.skyrin.bingo.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.skyrin.bingo.UmengConfig;
import com.skyrin.bingo.common.log.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by skyrin on 2016/12/18.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    InputMethodManager imm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isShowBack()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengConfig.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengConfig.onPause(this);
    }

    protected boolean isShowBack() {
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        LogUtil.i(TAG,"onSupportNavigateUp");
        onBackPressed();
        return true;
    }

    protected abstract void bindView();
    protected abstract void iniData();
    protected abstract void setListener();

    /**
     * 显示输入法
     *
     * @param v
     */
    public void showImm(final View v) {
        if (imm==null){
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                imm.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
            }
        }, 300);
    }

    /**
     * 隐藏输入法
     *
     * @param v
     */
    public void hideImm(View v) {
        if (imm==null){
            return;
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
