package com.skyrin.bingo.common.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public final class ScreenUtils {
    private ScreenUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int i = -1;
        try {
            Class cls = Class.forName("com.android.internal.R$dimen");
            i = context.getResources().getDimensionPixelSize(Integer.parseInt(cls.getField("status_bar_height").get(cls.newInstance()).toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * ？？
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap createBitmap = Bitmap.createBitmap(decorView.getDrawingCache(), 0, 0, getScreenWidth(activity), getScreenHeight(activity));
        decorView.destroyDrawingCache();
        return createBitmap;
    }

    /**
     * ？？
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap drawingCache = decorView.getDrawingCache();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int i = rect.top;
        drawingCache = Bitmap.createBitmap(drawingCache, 0, i, getScreenWidth(activity), getScreenHeight(activity) - i);
        decorView.destroyDrawingCache();
        return drawingCache;
    }

    /**
     * ？？
     * @param context
     * @param f
     * @return
     */
    public static int pxConvert(Context context, float f) {
        float px2dp;
        int screenHeight = getScreenHeight(context);
        int screenWidth = getScreenWidth(context);
        if (((double) (((float) screenWidth) / ((float) screenHeight))) >= 0.526d) {
            px2dp = ((float) screenHeight) / px2dp(context, 1280.0f);
        } else {
            px2dp = ((float) screenWidth) / px2dp(context, 720.0f);
        }
        return (int) ((px2dp * px2dp(context, f)) + 0.5f);
    }

    /**
     * ？？
     * @param context
     * @return
     */
    public static float getRealScale(Context context) {
        int screenHeight = getScreenHeight(context);
        int screenWidth = getScreenWidth(context);
        if (((double) (((float) screenWidth) / ((float) screenHeight))) >= 0.526d) {
            return ((float) screenHeight) / 1280.0f;
        }
        return ((float) screenWidth) / 720.0f;
    }

    /**
     * px转dp
     * @param context
     * @param f
     * @return
     */
    public static float px2dp(Context context, float f) {
        return (f / context.getResources().getDisplayMetrics().density) + 0.5f;
    }
}
