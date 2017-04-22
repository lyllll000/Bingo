package com.skyrin.bingo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by skyrin on 2016/12/18.
 */

public class UmengConfig {
    /**
     * 打开微信抢红包服务
     */
    public static final String EVENT_OPEN_MM_SERVICE = "event_open_mm_service";
    /**
     * 分享QQ抢红包服务
     */
    public static final String EVENT_OPEN_QQ_SERVICE = "event_open_qq_service";
    /**
     * 分享事件
     */
    public static final String EVENT_SHARE = "event_share";
    /**
     * 好评事件
     */
    public static final String EVENT_THUMBS_UP = "event_thumbs_up";
    /**
     * 打开看着
     */
    public static final String EVENT_OPEN_SEE = "event_open_see";
    /**
     * 打开红包
     */
    public static final String EVENT_OPEN_LUCKY_MONEY = "event_hongbao";
    /**
     * 获得多少次红包
     */
    public static final String EVENT_RECIVE_LUCKY_MONEY_TIMES = "event_get_times";
    /**
     * 获得多少次微信红包
     */
    public static final String EVENT_RECIVE_QQ_LUCKY_MONEY_TIMES = "event_get_qq_times";
    /**
     * 获得多少次qq红包
     */
    public static final String EVENT_RECIVE_MM_LUCKY_MONEY_TIMES = "event_get_mm_times";
    /**
     * 开启锁屏抢红包
     */
    public static final String EVENT_OPEN_LOCK_GET = "event_open_lock_get";
    /**
     * 升级
     */
    public static final String EVENT_UPDATE = "event_update";
    /**
     * 开启极速模式
     */
    public static final String EVENT_OPEN_SPEED_MODE = "event_open_speed_mode";
    /**
     * 查看获取
     */
    public static final String EVENT_CHECK_GET = "event_check_get";
    /**
     * 会话开始
     * @param context
     */
    public static void onResume(Context context){
        MobclickAgent.onResume(context);
    }

    /**
     * 会话结束
     * @param context
     */
    public static void onPause(Context context){
        MobclickAgent.onPause(context);
    }
    /** 事件统计*/
    public static void eventStatistics(Context context, String event) {
        MobclickAgent.onEvent(context,event);
    }

    /** 事件统计*/
    public static void eventStatistics(Context context, String event, String tag) {
        MobclickAgent.onEvent(context, event, tag);
    }

    /**
     * 错误统计
     * @param context
     * @param error
     */
    public static void reportError(Context context, String error){
        MobclickAgent.reportError(context,error);
    }

    /**
     * 开启测试模式
     * @param b
     */
    public static void setDebugMode(boolean b){
        MobclickAgent.setDebugMode(b);
    }

    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
