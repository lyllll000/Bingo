package com.skyrin.bingo.common.device;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.List;

public class DeviceUtil {
	
	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground(Context context) {
		// Returns a list of application processes that are running on the
		// deviceActivityManager activityManager;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false; 

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(context.getPackageName())
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前设备的Android版本号
	 * @return
	 */ 
	public static String getDeviceVer() {
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获取设备id
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return manager != null ? manager.getDeviceId() : null;
	}
}
