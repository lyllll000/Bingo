package com.skyrin.bingo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skyrin.bingo.Config;
import com.skyrin.bingo.R;
import com.skyrin.bingo.common.device.SystemSetings;
import com.skyrin.bingo.common.ui.UIHelper;
import com.skyrin.bingo.common.ui.ViewFindUtils;
import com.skyrin.bingo.service.BingoService;
import com.skyrin.bingo.ui.settings.QQSetActivity;
import com.skyrin.bingo.util.AccessibilityHelper;

/**
 * Created by admin on 2016/12/19.
 */

public class FragmentQQ extends BaseFragment{

    Context context;

    View rl_enable_service;
    View rl_qq_setting;
    View rl_auto_replay;
    View rl_notify_setting;
    SwitchCompat sw_enable_service;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fr_qq, container, false);
        context = getActivity();
        bindView(rootView);
        iniData();
        setListener();
        return rootView;
    }

    @Override
    protected void bindView(View view) {
        rl_enable_service = ViewFindUtils.find(view,R.id.rl_enable_service);
        sw_enable_service = ViewFindUtils.find(view,R.id.sw_enable_service);
        rl_qq_setting = ViewFindUtils.find(view,R.id.rl_qq_setting);
        rl_auto_replay = ViewFindUtils.find(view,R.id.rl_auto_replay);
        rl_notify_setting = ViewFindUtils.find(view,R.id.rl_notify_setting);
    }

    @Override
    protected void iniData() {
        sw_enable_service.setChecked(Config.getConfig(context).isEnableQQ()&& AccessibilityHelper.isAccessibilityEnable(context));
    }

    @Override
    protected void setListener() {
        rl_enable_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccessibilityHelper.isAccessibilityEnable(context)){
                    showOpenAccessiDialog();
                    return;
                }
                sw_enable_service.setChecked(!Config.getConfig(context).isEnableQQ());
                Config.getConfig(context).setEnableQQ(sw_enable_service.isChecked());
            }
        });
        sw_enable_service.setClickable(false);

        rl_notify_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rl_auto_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rl_qq_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),QQSetActivity.class));
            }
        });
    }

    /**
     * 打开辅助功能
     */
    private void showOpenAccessiDialog() {
        UIHelper.showTipsDialog(getActivity(), "请先打开辅助功能", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SystemSetings.openAccessibilityServiceSettings(getActivity());
                UIHelper.ShowToast(getActivity(),"选中【"+getString(R.string.app_name)+"】开启即可");
            }
        });
    }
}
