package com.skyrin.bingo.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.skyrin.bingo.Config;
import com.skyrin.bingo.R;
import com.skyrin.bingo.common.ui.ViewFindUtils;
import com.skyrin.bingo.ui.BaseActivity;

/**
 * Created by admin on 2016/12/21.
 */

public class MMAutoReplayActivity extends BaseActivity {

    Context context;
    View rl_replay_list,rl_random_replay;
    SwitchCompat sw_random_replay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_replay);
        bindView();
        iniData();
        setListener();
    }

    @Override
    protected boolean isShowBack() {
        return true;
    }

    @Override
    protected void bindView() {
        setTitle("自动回复设置");
        View view = getWindow().getDecorView();
        context = getBaseContext();
        rl_random_replay = ViewFindUtils.find(view,R.id.rl_random_replay);
        rl_replay_list = ViewFindUtils.find(view,R.id.rl_replay_list);
        sw_random_replay = ViewFindUtils.find(view,R.id.sw_random_replay);
    }

    @Override
    protected void iniData() {
        sw_random_replay.setChecked(Config.getConfig(context).isRandomRepalyMM());
        sw_random_replay.setClickable(false);
    }

    @Override
    protected void setListener() {
        rl_random_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sw_random_replay.setChecked(!sw_random_replay.isChecked());
                Config.getConfig(context).setRandomReplayMM(sw_random_replay.isChecked());
            }
        });
        rl_replay_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,MMsgActivity.class));
            }
        });
    }
}
