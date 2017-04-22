package com.skyrin.bingo.service;

import android.app.Notification;

/**
 * Created by skyrin on 2016/12/18.
 */

public interface IStatusBarNotification {
    String getPackageName();
    Notification getNotification();
}
