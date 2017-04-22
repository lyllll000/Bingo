package com.skyrin.bingo.common.device;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.skyrin.bingo.Config;

/**
 * Created by skyrin on 2017/1/3.
 */

public class PendingIntentRunnable implements Runnable  {
    private PendingIntent pendingIntent;
    private Context context;

    public PendingIntentRunnable(Context context,PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
        this.context = context;
    }

    public void run() {
        if (this.pendingIntent != null) {
            try {
                this.pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }
}
