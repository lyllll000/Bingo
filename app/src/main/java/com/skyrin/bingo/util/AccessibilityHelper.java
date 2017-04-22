package com.skyrin.bingo.util;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.utils.StringUtils;
import com.skyrin.bingo.common.log.LogUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by skyrin on 2016/12/18.
 */

public class AccessibilityHelper {
    private AccessibilityHelper() {}

    /**
     * 辅助服务是否启动
     * @param context
     * @return
     */
    public static boolean isAccessibilityEnable(Context context){
        int accessEnable = 0;
        try {
            accessEnable = Settings.Secure.getInt(context.getContentResolver(),Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnable==1){
            String services = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            LogUtil.d("services:"+services);
            if (services!=null){
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }

    /**
     * 通过多个id查找元素集合
     */
    public static List<AccessibilityNodeInfo> findNodeInfoListByIds(
            AccessibilityNodeInfo nodeInfo, String... ids) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = new ArrayList<>();
            for (String id : ids) {
                try {
                    list.addAll(nodeInfo.findAccessibilityNodeInfosByViewId(id));
                } catch (Exception e) {
                    return null;
                }
            }
            if (list != null && !list.isEmpty()) {
                return list;
            }
        }
        return null;
    }

    /**
     * 通过多个Text查找元素集合
     */
    public static List<AccessibilityNodeInfo> findNodeInfoListByTexts(
            AccessibilityNodeInfo nodeInfo, String... texts) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = new ArrayList<>();
            for (String text : texts) {
                try {
                    list.addAll(nodeInfo.findAccessibilityNodeInfosByText(text));
                } catch (Exception e) {
                    return null;
                }
            }
            if (list != null && !list.isEmpty()) {
                return list;
            }
        }
        return null;
    }

    /**
     * 通过单个id查找元素集合
     */
    public static List<AccessibilityNodeInfo> findNodeInfoListById(
            AccessibilityNodeInfo nodeInfo, String resId) {
        if (nodeInfo==null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByViewId(resId);
            if (list != null) {
                return list;
            }
        }
        return null;
    }

    /** 通过id查找*/
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo, String resId) {
        if(nodeInfo == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if(list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    /** 通过文本查找*/
    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        if(nodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if(list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /** 通过关键字查找*/
    public static AccessibilityNodeInfo findNodeInfosByTexts(AccessibilityNodeInfo nodeInfo, String... texts) {
        if(nodeInfo == null) {
            return null;
        }
        for(String key : texts) {
            AccessibilityNodeInfo info = findNodeInfosByText(nodeInfo, key);
            if(info != null) {
                return info;
            }
        }
        return null;
    }

    /** 通过组件名字查找*/
    public static AccessibilityNodeInfo findNodeInfosByClassName(AccessibilityNodeInfo nodeInfo, String className) {
        if(nodeInfo == null) {
            return null;
        }
        if(TextUtils.isEmpty(className)) {
            return null;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo node = nodeInfo.getChild(i);
            if(className.equals(node.getClassName())) {
                return node;
            }
        }
        return null;
    }

    /** 找父组件*/
    public static AccessibilityNodeInfo findParentNodeInfosByClassName(AccessibilityNodeInfo nodeInfo, String className) {
        if(nodeInfo == null) {
            return null;
        }
        if(TextUtils.isEmpty(className)) {
            return null;
        }
        if(className.equals(nodeInfo.getClassName())) {
            return nodeInfo;
        }
        return findParentNodeInfosByClassName(nodeInfo.getParent(), className);
    }

    private static final Field sSourceNodeField;

    static {
        Field field = null;
        try {
            field = AccessibilityNodeInfo.class.getDeclaredField("mSourceNodeId");
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sSourceNodeField = field;
    }

    public static long getSourceNodeId (AccessibilityNodeInfo nodeInfo) {
        if(sSourceNodeField == null) {
            return -1;
        }
        try {
            return sSourceNodeField.getLong(nodeInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getViewIdResourceName(AccessibilityNodeInfo nodeInfo) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return nodeInfo.getViewIdResourceName();
        }
        return null;
    }

    /** 返回主界面事件*/
    public static void performHome(AccessibilityService service) {
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    /** 返回事件*/
    public static void performBack(AccessibilityService service) {
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /** 点击事件*/
    public static void performClick(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return;
        }
        if(nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

    /**
     * 点击事件
     * @param root
     * @param nodeId
     */
    public static void performClick(AccessibilityNodeInfo root,String nodeId){
        if (StringUtils.isEmpty(nodeId)){
            return;
        }
        performClick(findNodeInfosById(root,nodeId));
    }

    /**
     * 将一个节点下的所有子节点全部放入map
     *
     * @param node
     * @param map
     */
    public static void addChild2Map(AccessibilityNodeInfo node, Map<String, AccessibilityNodeInfo> map) {
        if (node == null) {
            return;
        }
        if (node.getChildCount() > 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo temp = node.getChild(i);
                if (temp != null && temp.getChildCount() > 0) {
                    addChild2Map(temp, map);
                }
                if (temp != null) {
                    map.put(String.valueOf(temp.getClassName()), temp);
                }
            }
        }
    }

    /**
     * 将一个节点下的所有子节点全部放入list
     *
     * @param node
     * @param list
     */
    public static void addChild2List(AccessibilityNodeInfo node, ArrayList<AccessibilityNodeInfo> list) {
        if (node == null) {
            return;
        }
        if (node.getChildCount() > 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo temp = node.getChild(i);
                if (temp != null && temp.getChildCount() > 0) {
                    addChild2List(temp, list);
                }
                if (temp != null) {
                    list.add(temp);
                }
            }
        }
    }

    /**
     * 翻页事件向下
     */
    public static boolean performPageDown(AccessibilityNodeInfo nodeInfo) {
        boolean result = false;
        if (nodeInfo == null) {
            return result;
        }
        if (nodeInfo.isScrollable()) {
            result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        } else {
            performPageDown(nodeInfo.getParent());
        }
        return result;
    }

    /**
     * 翻页事件向上
     */
    public static boolean performPageUp(AccessibilityNodeInfo nodeInfo) {
        boolean result = false;
        if (nodeInfo == null) {
            return result;
        }
        if (nodeInfo.isScrollable()) {
            result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        } else {
            performPageDown(nodeInfo.getParent());
        }
        return result;
    }
}
