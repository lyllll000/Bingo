package com.skyrin.bingo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.AppUtils;
import com.skyrin.bingo.Config;
import com.skyrin.bingo.HttpPath;
import com.skyrin.bingo.R;
import com.skyrin.bingo.UmengConfig;
import com.skyrin.bingo.common.device.SystemSetings;
import com.skyrin.bingo.common.log.LogUtil;
import com.skyrin.bingo.common.ui.UIHelper;
import com.skyrin.bingo.common.ui.ViewFindUtils;
import com.skyrin.bingo.common.util.ShareUtils;
import com.skyrin.bingo.fragment.FragmentMM;
import com.skyrin.bingo.service.BingoNotificationService;
import com.skyrin.bingo.service.BingoService;
import com.skyrin.bingo.update.AppUpdate;
import com.skyrin.bingo.util.AccessibilityHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    ActionBar actionBar;
    TabLayout tab;
    ImageView iv_acc_able;
    TextView tv_acc_able;
    TextView tv_tips_count;
    ViewPager mViewpager;
    SelectionAdapter adapter;
    List<Fragment> fragments=null;
    Integer[] icons_df = {R.mipmap.ic_mm_defult,R.mipmap.ic_qq_defult,R.mipmap.ic_me_defult};
    Integer[] icons_sl = {R.mipmap.ic_mm_select ,R.mipmap.ic_qq_select,R.mipmap.ic_me_select};
    String[] titles = {"Bingo","QQ","我的"};

    Config config;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Config.ACTION_NOTIFY_TIPS_COUNT_CHANGE.equals(String.valueOf(intent.getAction()))){
                updateTips();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setElevation(0);
//        actionBar.setLogo(R.mipmap.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);

        bindView();
        iniData();
        setListener();
        checkUpdate();
//        LogUtil.i(TAG,UmengConfig.getDeviceInfo(this));
    }

    /**
     * 检测升级
     */
    private void checkUpdate() {
        showUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"开启极速模式");
        menu.add(0,2,2,"开启辅助功能");
//        menu.add(0,3,3,"开启悬浮窗");
        menu.add(0,4,4,"检查更新");
//        menu.add(0,5,5,"分享");

        return true;
    }

    @Override
    public void onBackPressed() {
        //这里相当于直接按了home键
        Intent intent= new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Toast.makeText(this, "该功能只支持安卓4.3以上的系统", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (BingoNotificationService.isRunning()){
                    UIHelper.ShowToast(this,"已开启成功");
                }else {
                    SystemSetings.openNotificationServiceSettings(this);
                    UIHelper.ShowToast(this,"选中【"+getString(R.string.app_name)+"】开启即可");
                }
                break;
            case 2:
                if (SystemSetings.openAccessibilityServiceSettings(this)){
                    UIHelper.ShowToast(this,"选中【"+getString(R.string.app_name)+"】即可");
                }else {
                    UIHelper.ShowToast(this,"您的设备不支持此模式");
                }
                UmengConfig.eventStatistics(this,UmengConfig.EVENT_OPEN_SPEED_MODE);
                break;
            case 3:
                SystemSetings.openFloatWindowSettings(this);
                UIHelper.ShowToast(this,"选择 权限管理>悬浮窗 开启即可");
                break;
            case 4:
                showUpdate();
                UmengConfig.eventStatistics(this,UmengConfig.EVENT_UPDATE);
//                String s=null;
//                s.replace(" ","");
                break;
            case 5:
                ShareUtils.share(MainActivity.this,"红包神器");
                break;
        }
        return true;
    }

    /**
     * 检测升级
     */
    private void showUpdate() {

        LogUtil.i(TAG,"showUpdate");


        //根据channel获取升级链接
        String url = HttpPath.UPDATE_URL;
        String url_oss = HttpPath.UPDATE_URL_OSS;

        new AppUpdate(MainActivity.this,url_oss).update();
    }

    @Override
    protected void bindView() {
        tab = (TabLayout) findViewById(R.id.tab);
        tab.setVisibility(View.GONE);
        mViewpager = (ViewPager) findViewById(R.id.vp);
        iv_acc_able = (ImageView) findViewById(R.id.iv_acc_able);
        tv_acc_able = (TextView) findViewById(R.id.tv_acc_able);
        tv_tips_count = (TextView) findViewById(R.id.tv_tips_count);
        tv_tips_count.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void iniData() {

        config = Config.getConfig(this);

        config.setNotificationServiceEnable(true);

        if (fragments==null||fragments.isEmpty()){
            fragments = new ArrayList<>();
            fragments.add(new FragmentMM());
//            fragments.add(new FragmentQQ());
//            fragments.add(new FragmentAbt());
        }
        adapter = new SelectionAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(adapter);

        tab.setupWithViewPager(mViewpager);
        tab.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < fragments.size(); i++) {
            tab.getTabAt(i).setIcon(icons_df[i]);
        }

        updateTips();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.ACTION_NOTIFY_TIPS_COUNT_CHANGE);
        registerReceiver(receiver,filter);
    }

    /**
     * 更新提醒红包个数
     */
    private void updateTips() {
        int allCount = config.getLuckyMoneyCountMM()+config.getLuckyMoneyCountQQ();
        if (allCount>0){
            tv_tips_count.setVisibility(View.VISIBLE);
        }
        tv_tips_count.setText(getString(R.string.tips_count,new Object[]{String.valueOf(allCount)}));
    }

    @Override
    protected void setListener() {
        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(icons_sl[tab.getPosition()]);
                actionBar.setTitle(titles[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(icons_df[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tv_tips_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(MainActivity.this,R.layout.view_tips_dialog,null);
                TextView tv_count_hongbao_mm = ViewFindUtils.find(view,R.id.tv_count_hongbao_mm);
                TextView tv_count_hongbao_qq = ViewFindUtils.find(view,R.id.tv_count_hongbao_qq);
                TextView tv_count_money_mm = ViewFindUtils.find(view,R.id.tv_count_money_mm);
                TextView tv_count_money_qq = ViewFindUtils.find(view,R.id.tv_count_money_qq);

                DecimalFormat df = new DecimalFormat("######0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.

                tv_count_hongbao_mm.setText(config.getLuckyMoneyCountMM()+"个红包");
                tv_count_hongbao_qq.setText(config.getLuckyMoneyCountQQ()+"个红包");
                tv_count_money_mm.setText(df.format(config.getMoneyMM())+"元");
                tv_count_money_qq.setText(df.format(config.getMoneyQQ())+"元");

                UIHelper.showCunstomDialog(MainActivity.this,view);
                UmengConfig.eventStatistics(MainActivity.this,UmengConfig.EVENT_CHECK_GET);
            }
        });

        actionBar.setTitle("Bingo "+ AppUtils.getAppVersionName(getApplicationContext()));
        tab.getTabAt(0).setIcon(icons_sl[0]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //是否同意服务条款
        if (!Config.getConfig(this).isAgreement()){
//            showAgreementDialog();
        }
        //辅助服务是否运行
        if (AccessibilityHelper.isAccessibilityEnable(this)){
            tv_acc_able.setText(getString(R.string.acc_able));
            iv_acc_able.setImageResource(R.mipmap.acc_enable);
        }else {
            tv_acc_able.setText(getString(R.string.acc_unable));
            iv_acc_able.setImageResource(R.mipmap.acc_unable);
        }
    }

    private void showAgreementDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("免责声明")
                .setMessage(getString(R.string.agreement))
                .setNegativeButton("不同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Config.getConfig(MainActivity.this).setAgreement(true);
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (receiver!=null){
                unregisterReceiver(receiver);
            }
        }catch (Exception e){}
    }

    public class SelectionAdapter extends FragmentPagerAdapter{

        public SelectionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
