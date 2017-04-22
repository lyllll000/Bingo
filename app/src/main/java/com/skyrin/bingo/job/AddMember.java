package com.skyrin.bingo.job;

import android.view.accessibility.AccessibilityEvent;

import com.skyrin.bingo.service.IStatusBarNotification;

/**
 * Created by skyrin on 2017/3/5.
 */

public class AddMember extends BaseAccessibilityJob {
    public static final String TARGET_PKG_NAME = "com.tencent.mm";

    public static final boolean START = true;
    public static final boolean STOP = false;
    public static boolean ISWORKING = STOP;

    @Override
    public String getTargetPackageName() {
        return TARGET_PKG_NAME;
    }

    @Override
    public void onReceiveEvent(AccessibilityEvent event) {

    }

    @Override
    public void onStopJob() {

    }

    @Override
    public void onNotificationPosted(IStatusBarNotification service) {

    }

    @Override
    public boolean isEnable() {
        return true;
    }
}
