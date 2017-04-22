package com.skyrin.bingo.common.util;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by 罗延林 on 2016/10/13 0013.
 */

public class EleTree {
    public static String GetTreeInfo(AccessibilityNodeInfo nodeInfo) {
        String result = "";
        Ele_Info hookInfo = InitAllInfo(nodeInfo);
        if (hookInfo != null) {
            result = hookInfo.ToStringResult();
        }
        return result;
    }

    private static Ele_Info InitAllInfo(AccessibilityNodeInfo noteInfo) {
        Ele_Info hookmsg = new Ele_Info();
        Rect rect = new Rect();
        if (noteInfo == null)
            return hookmsg;
        int iChildCount = noteInfo.getChildCount();
        noteInfo.getBoundsInScreen(rect);
        hookmsg.set_childCount(iChildCount);
        hookmsg.set_className(noteInfo.getClassName() == null ? "" : noteInfo
                .getClassName().toString());
        hookmsg.set_text(noteInfo.getText() == null ? "" : noteInfo.getText()
                .toString());
        hookmsg.set_contentDesc(noteInfo.getContentDescription() == null ? ""
                : noteInfo.getContentDescription().toString());
        hookmsg.set_bottom(rect.bottom);
        hookmsg.set_top(rect.top);
        hookmsg.set_left(rect.left);
        hookmsg.set_right(rect.right);
        hookmsg.set_isClickable(noteInfo.isClickable());
        hookmsg.set_isCheckable(noteInfo.isCheckable());
        hookmsg.set_isChecked(noteInfo.isChecked());

        for (int i = 0; i < iChildCount; i++) {
            AccessibilityNodeInfo childInfo = noteInfo.getChild(i);
            if (childInfo != null) {
                Ele_Info hookmsgItem = InitAllInfo(childInfo);
                hookmsg.get_listChild().add(hookmsgItem);
            }
        }
        return hookmsg;
    }
}
