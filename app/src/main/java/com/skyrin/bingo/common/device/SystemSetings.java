package com.skyrin.bingo.common.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by skyrin on 2016/9/25.
 * 系统设置相关
 */

public class SystemSetings {
    /**
     * 辅助服务是否开启
     * @param context
     * @return true if Accessibility is on.
     */
    private static boolean isAccessibilitySettingsOn(Context context) {
        int i;
        try {
            i = Settings.Secure.getInt(context.getContentResolver(), "accessibility_enabled");
        } catch (Settings.SettingNotFoundException e) {
            Log.i("AccessibilitySettingsOn", e.getMessage());
            i = 0;
        }
        if (i != 1) {
            return false;
        }
        String string = Settings.Secure.getString(context.getContentResolver(), "enabled_accessibility_services");
        if (string != null) {
            return string.toLowerCase().contains(context.getPackageName().toLowerCase());
        }
        return false;
    }
    /**
     * 打开辅助服务的设置
     */
    public static boolean openAccessibilityServiceSettings(Context context) {
        boolean result = true;
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /** 打开通知栏设置*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public static void openNotificationServiceSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开app权限的设置
     * @param context
     * @return
     */
    public static boolean openFloatWindowSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 打开app权限的设置
     * @param context
     * @return
     */
    public static boolean openFloatWindowSettings(Context context,String pkgName) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", pkgName, null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 启动app
     *
     * @param context
     * @param pkgName 包名
     * @return 是否启动成功
     */
    public static boolean startApp(Context context, String pkgName) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent openApp = manager.getLaunchIntentForPackage(pkgName);
            if (openApp==null){
                return false;
            }
            context.startActivity(openApp);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
