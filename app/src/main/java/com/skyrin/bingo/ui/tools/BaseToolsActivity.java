package com.skyrin.bingo.ui.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.skyrin.bingo.R;
import com.skyrin.bingo.adapter.AdapterTools;
import com.skyrin.bingo.common.device.SystemSetings;
import com.skyrin.bingo.common.ui.UIHelper;
import com.skyrin.bingo.common.ui.ViewFindUtils;
import com.skyrin.bingo.job.AddMember;
import com.skyrin.bingo.job.QQZJob;
import com.skyrin.bingo.modle.Tools;
import com.skyrin.bingo.service.BingoService;
import com.skyrin.bingo.ui.BaseActivity;
import com.skyrin.bingo.ui.settings.MMsgActivity;
import com.skyrin.bingo.util.AccessibilityHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/1/20.
 */

public class BaseToolsActivity extends BaseActivity {
    RecyclerView rc_tools;
    AdapterTools adapter;
    Context context;
    List<Tools> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
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
        setTitle("小工具");
        context = this;
        View view = getWindow().getDecorView();
        rc_tools = ViewFindUtils.find(view,R.id.rc_tools);
        rc_tools.setLayoutManager(new LinearLayoutManager(context));
        rc_tools.setItemAnimator(new DefaultItemAnimator());
//        rc_tools.addItemDecoration(new RecycleViewDivider(
//                context, LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void iniData() {
        Tools qqZan = new Tools(getString(R.string.tools_qq),Tools.FUNC_QQ_ZAN,R.mipmap.ic_qq_z,R.mipmap.ic_settings,R.mipmap.ic_start);
        Tools mmZan = new Tools("MM赞",Tools.FUNC_MM_ZAN,R.mipmap.ic_settings,R.mipmap.ic_launcher,R.mipmap.ic_me_defult);
        list = new ArrayList<>();
        list.add(qqZan);
        list.add(mmZan);
        adapter = new AdapterTools(context,list);
        rc_tools.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        adapter.setOnItemClickListener(new AdapterTools.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                UIHelper.ShowToast(context,"onItemClick>>>"+position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
//                UIHelper.ShowToast(context,"onItemLongClick>>>"+position);
            }

            @Override
            public void onStartClick(View view, int position, int funcId) {
                if (!AccessibilityHelper.isAccessibilityEnable(context)) {
                    showOpenAccessiDialog();
                    return;
                }
                switch (funcId){
                    case Tools.FUNC_QQ_ZAN:
                        //启动QQ赞功能
                        QQZJob.ISWORKING = QQZJob.START;
                        SystemSetings.startApp(context, QQZJob.TARGET_PKG_NAME);
                        break;
                    case Tools.FUNC_MM_ZAN:
                        AddMember.ISWORKING = AddMember.START;
                        SystemSetings.startApp(context,AddMember.TARGET_PKG_NAME);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onSetClick(View view, int position, int funcId) {
                Intent intent = new Intent(BaseToolsActivity.this,MMsgActivity.class);
                switch (funcId){
                    case Tools.FUNC_QQ_ZAN:
                        intent.setClass(BaseToolsActivity.this,QQZanSettings.class);
                        break;
                    case Tools.FUNC_MM_ZAN:

                        break;
                    default:
                        break;
                }
                startActivity(intent);
            }
        });
    }

    /**
     * 打开辅助功能
     */
    private void showOpenAccessiDialog() {
        String title = "请先开启辅助功能";
        String cancel = "取消";
        String ok = "立即开启";
        View view = View.inflate(context, R.layout.dialog_open_accessbility, null);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SystemSetings.openAccessibilityServiceSettings(getActivity());
//                UIHelper.ShowToast(getActivity(), "找到【" + getString(R.string.app_name) + "】开启即可");
//            }
//        });
        UIHelper.showTipsDialog(context, view, title, ok, cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SystemSetings.openAccessibilityServiceSettings(context);
                UIHelper.ShowToast(context, "找到【" + getString(R.string.app_name) + "】开启即可");
            }
        });
    }
}
