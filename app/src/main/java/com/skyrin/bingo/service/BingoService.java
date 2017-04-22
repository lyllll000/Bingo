package com.skyrin.bingo.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.skyrin.bingo.Config;
import com.skyrin.bingo.job.AccessibilityJob;
import com.skyrin.bingo.job.MMJob;
import com.skyrin.bingo.job.QQJob;
import com.skyrin.bingo.job.QQZJob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by skyrin on 2016/12/18.
 */

public class BingoService extends AccessibilityService{

    private static final String TAG = "BingoService";

    static BingoService service;

    private static final Class[] ACCESSIBILITY_JOBS= {
            MMJob.class,
            QQJob.class,
            QQZJob.class
    };

    private List<AccessibilityJob> mAccessibilityJobs;
    private HashMap<String, AccessibilityJob> mPkgAccessibilityJobMap;

    @Override
    public void onCreate() {
        super.onCreate();
        mAccessibilityJobs = new ArrayList<>();
        mPkgAccessibilityJobMap = new HashMap<>();

        for (Class clazz : ACCESSIBILITY_JOBS) {
            try {
                Object object = clazz.newInstance();
                if(object instanceof AccessibilityJob) {
                    AccessibilityJob job = (AccessibilityJob) object;
                    job.onCreateJob(this);
                    mAccessibilityJobs.add(job);
                    mPkgAccessibilityJobMap.put(job.getTargetPackageName(), job);
                }
            }catch (Exception e){ e.printStackTrace();}
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除停止所有服务
        if (mPkgAccessibilityJobMap!=null){
            mPkgAccessibilityJobMap.clear();
        }
        if (mAccessibilityJobs!=null&&!mAccessibilityJobs.isEmpty()){
            for (AccessibilityJob job : mAccessibilityJobs) {
                job.onStopJob();
            }
            mAccessibilityJobs.clear();
        }

        service=null;
        mAccessibilityJobs=null;
        mPkgAccessibilityJobMap=null;

        Intent intent = new Intent(Config.ACTION_BINGO_SERVICE_DISCONNECT);
        sendBroadcast(intent);
        //发送广播已断开辅助服务
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //服务上线
        service = this;
        //发送广播，已经连接上了
        Intent intent = new Intent(Config.ACTION_BINGO_SERVICE_CONNECT);
        sendBroadcast(intent);
        Toast.makeText(this, "已连接抢红包服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        String pkgName = String.valueOf(accessibilityEvent.getPackageName());
        if (mAccessibilityJobs!=null&&!mAccessibilityJobs.isEmpty()){
            if (!getConfig().isAgreement()){
                return;
            }
            for (AccessibilityJob job : mAccessibilityJobs) {
                if (pkgName.equals(job.getTargetPackageName())&&job.isEnable()){
                    job.onReceiveEvent(accessibilityEvent);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
    }

    public Config getConfig(){
        return Config.getConfig(this);
    }

    /** 接收通知栏事件*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void handelNotificationPosted(IStatusBarNotification notificationService) {
        if(notificationService == null) {
            return;
        }
        if(service == null || service.mPkgAccessibilityJobMap == null) {
            return;
        }
        String pack = notificationService.getPackageName();
        AccessibilityJob job = service.mPkgAccessibilityJobMap.get(pack);
        if(job == null) {
            return;
        }
        job.onNotificationPosted(notificationService);
    }

//    /**
//     * 判断当前服务是否正在运行
//     * */
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    public static boolean isRunning() {
//        if(service == null) {
//            return false;
//        }
//        AccessibilityManager accessibilityManager = (AccessibilityManager) service.getSystemService(Context.ACCESSIBILITY_SERVICE);
//        AccessibilityServiceInfo info = service.getServiceInfo();
//        if(info == null) {
//            return false;
//        }
//        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
//        Iterator<AccessibilityServiceInfo> iterator = list.iterator();
//
//        boolean isConnect = false;
//        while (iterator.hasNext()) {
//            AccessibilityServiceInfo i = iterator.next();
//            if(i.getId().equals(info.getId())) {
//                isConnect = true;
//                break;
//            }
//        }
//        if(!isConnect) {
//            return false;
//        }
//        return true;
//    }

    /** 快速读取通知栏服务是否启动*/
    public static boolean isNotificationServiceRunning() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return false;
        }
        //部份手机没有NotificationService服务
        try {
            return BingoNotificationService.isRunning();
        } catch (Throwable t) {}
        return false;
    }
}
