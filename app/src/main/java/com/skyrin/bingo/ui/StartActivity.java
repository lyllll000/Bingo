package com.skyrin.bingo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyrin.bingo.R;
import com.skyrin.bingo.common.ui.ViewFindUtils;

/**
 * Created by admin on 2017/2/16.
 */

public class StartActivity extends Activity{
    ImageView iv_logo;
    TextView tv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        iniView();
        iniData();
    }

    private void iniView() {
        iv_logo = ViewFindUtils.find(getWindow().getDecorView(),R.id.iv_logo);
        tv_logo = ViewFindUtils.find(getWindow().getDecorView(),R.id.tv_logo);
        tv_logo.setVisibility(View.GONE);
    }

    private void iniData() {
        Animation anim = new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(800);
        iv_logo.setAnimation(anim);
        tv_logo.setAnimation(anim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },800);
    }
}
