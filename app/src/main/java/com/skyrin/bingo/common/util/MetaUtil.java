package com.skyrin.bingo.common.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by 罗延林 on 2016/10/17 0017.
 */

public class MetaUtil {
    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }
    /**
     * 获取版本号
     * @param context
     * @param dataName
     * @return
     */
    public static String getMetaData(Context context, String dataName) {
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(getPackageName(context),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return appInfo.metaData.getString(dataName);
    }
}
