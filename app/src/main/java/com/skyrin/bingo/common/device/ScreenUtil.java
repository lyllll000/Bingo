package com.skyrin.bingo.common.device;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.skyrin.bingo.ui.settings.OpenLockScreenActivity;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by skyrin on 2017/1/3.
 */

public class ScreenUtil {
    private static final String ACTION_REMOVE_KEYGUARD_NOTIFICATION = "com.miui.app.ExtraStatusBarManager.action_remove_keyguard_notification";
    public static final String ACTION_SHOW_MIUI_SECURE_KEYGUARD = "xiaomi.intent.action.SHOW_SECURE_KEYGUARD";
    private static final CopyOnWriteArrayList<KeyguardUnlockedListener> keyguardUnlockedListeners = new CopyOnWriteArrayList();
    private static PendingIntent latestAction = null;
    private static boolean registered = false;
    private static BroadcastReceiver unlockBroadcastReceiver = new C00461();

    public interface KeyguardUnlockedListener {
        void onKeyguardUnlocked();
    }

    static class C00461 extends BroadcastReceiver {
        C00461() {
        }

        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);
            ScreenUtil.registered = false;
            if (ScreenUtil.latestAction != null) {
                new PendingIntentRunnable(context,ScreenUtil.latestAction).run();
                ScreenUtil.latestAction = null;
                ScreenUtil.notifyKeyguardUnlocked();
            }
        }
    }

    public static synchronized void unlockKeyguard(Context context, PendingIntent action) {
        synchronized (ScreenUtil.class) {
            latestAction = action;
            unlockMiuiKeyguard(context);
        }
    }

    private static void unlockMiuiKeyguard(Context context) {
        if (isScreenLocked(context)) {
            Intent intent = new Intent();
            intent.setClass(context, OpenLockScreenActivity.class);
            intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.putExtra(OpenLockScreenActivity.EXTRA_ACTION_INTENT, latestAction);
            context.startActivity(intent);
            latestAction = null;
            return;
        }
        new PendingIntentRunnable(context,latestAction).run();
        latestAction = null;
    }

    public static void unlockSecureMiuiKeyguard(Context context, PendingIntent action) {
        if (registered) {
            context.unregisterReceiver(unlockBroadcastReceiver);
            registered = false;
        }
        latestAction = action;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(unlockBroadcastReceiver, filter);
        registered = true;
        context.sendBroadcast(new Intent(ACTION_SHOW_MIUI_SECURE_KEYGUARD));
    }

    public static synchronized void clearKeyguardNotifications(Context context) {
        synchronized (ScreenUtil.class) {
            context.sendBroadcast(new Intent(ACTION_REMOVE_KEYGUARD_NOTIFICATION));
        }
    }

    public static synchronized void powerOnScreen(Context context) {
        synchronized (ScreenUtil.class) {//805306378
            PowerManager.WakeLock wl = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(805306378, "hongbaoassistant");
            try {
                wl.acquire();
                wl.release();
            } catch (Throwable th) {
                wl.release();
            }
        }
    }

    public static synchronized boolean isSecureLocked(Context context) {
        boolean z;
        synchronized (ScreenUtil.class) {
            z = isScreenLocked(context) && isKeyguardSecure(context);
        }
        return z;
    }

    public static synchronized boolean isScreenLocked(Context context) {
        boolean isKeyguardLocked;
        synchronized (ScreenUtil.class) {
            isKeyguardLocked = ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isKeyguardLocked();
        }
        return isKeyguardLocked;
    }

    public static synchronized boolean isKeyguardSecure(Context context) {
        boolean isKeyguardSecure;
        synchronized (ScreenUtil.class) {
            isKeyguardSecure = ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isKeyguardSecure();
        }
        return isKeyguardSecure;
    }

    public static synchronized void register(KeyguardUnlockedListener listener) {
        synchronized (ScreenUtil.class) {
            if (!keyguardUnlockedListeners.contains(listener)) {
                keyguardUnlockedListeners.add(listener);
            }
        }
    }

    public static synchronized void unregister(KeyguardUnlockedListener listener) {
        synchronized (ScreenUtil.class) {
            keyguardUnlockedListeners.remove(listener);
        }
    }

    public static synchronized void notifyKeyguardUnlocked() {
        synchronized (ScreenUtil.class) {
            Iterator i$ = keyguardUnlockedListeners.iterator();
            while (i$.hasNext()) {
                ((KeyguardUnlockedListener) i$.next()).onKeyguardUnlocked();
            }
        }
    }
}
