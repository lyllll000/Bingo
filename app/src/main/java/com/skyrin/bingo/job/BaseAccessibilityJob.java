package com.skyrin.bingo.job;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.skyrin.bingo.Config;
import com.skyrin.bingo.service.BingoService;

/**
 * Created by skyrin on 2016/12/18.
 */

public abstract class BaseAccessibilityJob implements AccessibilityJob {
    private BingoService service;

    @Override
    public void onCreateJob(BingoService service) {
        this.service = service;
    }

    public Context getContext() {
        return service.getApplicationContext();
    }

    public Config getConfig() {
        return service.getConfig();
    }

    public BingoService getService() {
        return service;
    }

    /**
     * 粘贴消息
     *
     * @param context
     * @param info 对象
     * @param content  内容
     */
    public static void pasteMsg2Info(Context context, AccessibilityNodeInfo info, String content) {
        // 使用剪切板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("content", content);
        clipboard.setPrimaryClip(clip);
        // 焦点 （n是AccessibilityNodeInfo对象）
        info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        // 粘贴进入内容
        info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
    }

    public void sleepShort(){
//        SystemClock.sleep(300);
    }

    public void sleepLong(){
//        SystemClock.sleep(600);
    }
}
