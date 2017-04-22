package com.skyrin.bingo.ui.settings;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.skyrin.bingo.R;
import com.skyrin.bingo.common.device.PendingIntentRunnable;
import com.skyrin.bingo.common.device.ScreenUtil;

/**
 * Created by skyrin on 2017/1/3.
 */

public class OpenLockScreenActivity extends Activity {
    public static String EXTRA_ACTION_INTENT = "action_intent";
    private PendingIntent mActionIntent;
    private boolean mActionStarted = false;

    class C00311 implements Runnable {
        C00311() {
        }

        public void run() {
            if (!OpenLockScreenActivity.this.mActionStarted) {
                OpenLockScreenActivity.this.mActionStarted = true;
                if (ScreenUtil.isScreenLocked(OpenLockScreenActivity.this.getApplicationContext())) {
                    OpenLockScreenActivity.this.sendBroadcast(new Intent(ScreenUtil.ACTION_SHOW_MIUI_SECURE_KEYGUARD));
                    int sleepTime = 0;
                    while (sleepTime < 300) {
                        long sleepStartTime = System.currentTimeMillis();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                        int delta = (int) (System.currentTimeMillis() - sleepStartTime);
                        if (delta < 0) {
                            delta = 0;
                        }
                        sleepTime += delta;
                        if (!ScreenUtil.isScreenLocked(OpenLockScreenActivity.this.getApplicationContext())) {
                            break;
                        }
                    }
                }
                if (ScreenUtil.isScreenLocked(OpenLockScreenActivity.this.getApplicationContext())) {
                    OpenLockScreenActivity.this.finish();
                    ScreenUtil.unlockSecureMiuiKeyguard(OpenLockScreenActivity.this.getApplicationContext(), OpenLockScreenActivity.this.mActionIntent);
                    return;
                }
                if (OpenLockScreenActivity.this.mActionIntent != null) {
                    new PendingIntentRunnable(OpenLockScreenActivity.this,OpenLockScreenActivity.this.mActionIntent).run();
                }
                OpenLockScreenActivity.this.finish();
                OpenLockScreenActivity.this.sendBroadcast(new Intent(ScreenUtil.ACTION_SHOW_MIUI_SECURE_KEYGUARD));
                ScreenUtil.notifyKeyguardUnlocked();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(4718592);
        setContentView(R.layout.transparent_layout);
        this.mActionIntent = getIntent().getParcelableExtra(EXTRA_ACTION_INTENT);
    }

    protected void onResume() {
        super.onResume();
        startAction(1000);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            startAction(0);
        }
    }

    private void startAction(int milliseconds) {
        new Handler().postDelayed(new C00311(), (long) milliseconds);
    }
}
