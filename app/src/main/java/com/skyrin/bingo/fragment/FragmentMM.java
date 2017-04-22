package com.skyrin.bingo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skyrin.bingo.Config;
import com.skyrin.bingo.HttpPath;
import com.skyrin.bingo.R;
import com.skyrin.bingo.UmengConfig;
import com.skyrin.bingo.common.device.SystemSetings;
import com.skyrin.bingo.common.ui.UIHelper;
import com.skyrin.bingo.common.ui.ViewFindUtils;
import com.skyrin.bingo.common.util.ShareUtils;
import com.skyrin.bingo.service.BingoService;
import com.skyrin.bingo.ui.WebActivity;
import com.skyrin.bingo.ui.settings.MMActivity;
import com.skyrin.bingo.ui.settings.MMAutoReplayActivity;
import com.skyrin.bingo.ui.settings.MMNotifyActivity;
import com.skyrin.bingo.ui.tools.BaseToolsActivity;
import com.skyrin.bingo.util.AccessibilityHelper;

/**
 * Created by admin on 2016/12/19.
 */

public class FragmentMM extends BaseFragment {

    SwitchCompat sw_enable_service, sw_enable_qq_service;
    View rl_mm_setting, rl_auto_replay, rl_notify_setting, rl_enable_service, rl_share, rl_thumbs_up;
    View rl_enable_qq_service;
    View rl_tools;
    TextView tv_reminder;
    TextView tv_help;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fr_mm, container, false);
        context = getActivity();
        bindView(rootView);
        iniData();
        setListener();
        return rootView;
    }

    @Override
    public void onResume() {
        iniData();
        super.onResume();
    }

    @Override
    protected void bindView(View view) {
        sw_enable_service = ViewFindUtils.find(view, R.id.sw_enable_service);
        rl_mm_setting = ViewFindUtils.find(view, R.id.rl_mm_setting);
        rl_auto_replay = ViewFindUtils.find(view, R.id.rl_auto_replay);
        rl_notify_setting = ViewFindUtils.find(view, R.id.rl_notify_setting);
        rl_enable_service = ViewFindUtils.find(view, R.id.rl_enable_service);
        rl_enable_qq_service = ViewFindUtils.find(view, R.id.rl_enable_qq_service);
        sw_enable_qq_service = ViewFindUtils.find(view, R.id.sw_enable_qq_service);
        tv_reminder = ViewFindUtils.find(view, R.id.tv_reminder);
        rl_thumbs_up = ViewFindUtils.find(view, R.id.rl_thumbs_up);
        rl_share = ViewFindUtils.find(view, R.id.rl_share);
        rl_tools = ViewFindUtils.find(view, R.id.rl_tools);
        tv_help = ViewFindUtils.find(view, R.id.tv_help);

        String str = getString(R.string.help);
        SpannableString sps = new SpannableString(str);
        sps.setSpan(new UnderlineSpan(),0,sps.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_help.setText(sps);
    }

    @Override
    protected void iniData() {
        sw_enable_service.setChecked(Config.getConfig(getActivity()).isEnableMM() && AccessibilityHelper.isAccessibilityEnable(context));
        sw_enable_qq_service.setChecked(Config.getConfig(getActivity()).isEnableQQ() && AccessibilityHelper.isAccessibilityEnable(context));
        sw_enable_service.setClickable(false);
        sw_enable_qq_service.setClickable(false);
        tv_reminder.setText(getString(R.string.reminder, new Object[]{getString(R.string.app_name)}));
    }

    @Override
    protected void setListener() {
        rl_enable_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccessibilityHelper.isAccessibilityEnable(context)) {
                    showOpenAccessiDialog();
                    return;
                }
                sw_enable_service.setChecked(!sw_enable_service.isChecked());
                Config.getConfig(getActivity()).setEnableMM(sw_enable_service.isChecked());
                //统计mm服务开启次数
                if (sw_enable_service.isChecked()) {
                    UmengConfig.eventStatistics(getActivity(), UmengConfig.EVENT_OPEN_MM_SERVICE);
                }
            }
        });
        rl_enable_qq_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccessibilityHelper.isAccessibilityEnable(context)) {
                    showOpenAccessiDialog();
                    return;
                }
                sw_enable_qq_service.setChecked(!sw_enable_qq_service.isChecked());
                Config.getConfig(getActivity()).setEnableQQ(sw_enable_qq_service.isChecked());
                //统计qq服务开启次数
                if (sw_enable_qq_service.isChecked()) {
                    UmengConfig.eventStatistics(getActivity(), UmengConfig.EVENT_OPEN_QQ_SERVICE);
                }
            }
        });
        rl_auto_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MMAutoReplayActivity.class));
            }
        });
        rl_notify_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MMNotifyActivity.class));
            }
        });
        rl_mm_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MMActivity.class));
            }
        });
        rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.share(getActivity(), getString(R.string.share_content,new Object[]{HttpPath.SHARE_URL}));
                UmengConfig.eventStatistics(getActivity(), UmengConfig.EVENT_SHARE);
            }
        });
        rl_thumbs_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMarket();
                UmengConfig.eventStatistics(getActivity(), UmengConfig.EVENT_THUMBS_UP);
            }
        });
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), WebActivity.class);
                intent.putExtra(WebActivity.KEY_URL,"file:///android_asset/help.html");
                intent.putExtra(WebActivity.KEY_TITLE,"常见问题");
                getActivity().startActivity(intent);
            }
        });
        rl_tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BaseToolsActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    /**
     * 跳转到应用市场
     */
    private void startMarket() {
        try {
            String mAddress = "market://details?id=" + getActivity().getPackageName();
            Intent marketIntent = new Intent("android.intent.action.VIEW");
            marketIntent.setData(Uri.parse(mAddress ));
            startActivity(marketIntent);
        }catch (Exception e){
            UIHelper.ShowToast(getActivity(), "无法跳转到应用市场");
        }
    }

    /**
     * 启动浏览器
     *
     * @param context
     * @param url
     */
    private void startWeb(Context context, String url) {
        try {
            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开辅助功能
     */
    private void showOpenAccessiDialog() {
        String title = "请先开启辅助功能";
        String cancel = "取消";
        String ok = "立即开启";
        View view = View.inflate(getActivity(), R.layout.dialog_open_accessbility, null);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SystemSetings.openAccessibilityServiceSettings(getActivity());
//                UIHelper.ShowToast(getActivity(), "找到【" + getString(R.string.app_name) + "】开启即可");
//            }
//        });
        UIHelper.showTipsDialog(getActivity(), view, title, ok, cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SystemSetings.openAccessibilityServiceSettings(getActivity());
                UIHelper.ShowToast(getActivity(), "找到【" + getString(R.string.app_name) + "】开启即可");
            }
        });
    }
}
