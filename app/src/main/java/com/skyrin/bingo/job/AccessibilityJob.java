package com.skyrin.bingo.job;

import android.view.accessibility.AccessibilityEvent;

import com.skyrin.bingo.service.BingoService;
import com.skyrin.bingo.service.IStatusBarNotification;

/**
 * Created by skyrin on 2016/12/18.
 */

public interface AccessibilityJob {
    String getTargetPackageName();
    void onCreateJob(BingoService service);
    void onReceiveEvent(AccessibilityEvent event);
    void onStopJob();
    void onNotificationPosted(IStatusBarNotification service);
    boolean isEnable();
}
