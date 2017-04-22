package com.skyrin.bingo.update;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by 罗延林 on 2016/9/8 0008.
 * 版本号、名称获取
 */
public class VersionManager {
    /**
     * 获取版本号
     * @param context
     * @param sPackageName 包名
     * @return
     */
    public static int getVerCode(Context context, String sPackageName) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    sPackageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VersionManager",e.getMessage());
        }
        return verCode;
    }

    /**
     * 获取版本名称
     * @param context
     * @param sPackageName 包名
     * @return
     */
    public static String getVerName(Context context, String sPackageName) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    sPackageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VersionManager",e.getMessage());
        }
        return verName;
    }
    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }
}
