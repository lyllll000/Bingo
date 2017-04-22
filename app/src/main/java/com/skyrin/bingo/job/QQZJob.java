package com.skyrin.bingo.job;

import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.utils.StringUtils;
import com.skyrin.bingo.Config;
import com.skyrin.bingo.common.log.LogUtil;
import com.skyrin.bingo.common.util.StringUtil;
import com.skyrin.bingo.service.IStatusBarNotification;
import com.skyrin.bingo.util.AccessibilityHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/4.
 */

public class QQZJob extends BaseAccessibilityJob {
    //test dev
    private static final String TAG = "QQZJob";
    /**
     * 访客page
     */
    private static final String TARGET_PAGE_CLASS = "com.tencent.mobileqq.activity.VisitorsActivity";
    private static final String HOME_PAGE_CLASS = "com.tencent.mobileqq.activity.SplashActivity";
    private static final String Friend_PAGE_CLASS = "com.tencent.mobileqq.activity.FriendProfileCardActivity";
    private static final String BTN_CLASS_NAME = "android.widget.Button";
    private static final String RL_CLASS_NAME = "android.widget.RelativeLayout";
    private static final String IV_CLASS_NAME = "android.widget.ImageView";
    private static final String ABS_LIST_CLASS_NAME = "android.widget.AbsListView";
    private static final String TEXT_VIEW_CLASS_NAME = "android.widget.TextView";

    private static final String id_nickname = "com.tencent.mobileqq:id/nickname";
    private static final String id_visitor_name = "com.tencent.mobileqq:id/text1";
    private static final String id_home_tab = "android:id/tabs";
    private static final String id_home_head = "com.tencent.mobileqq:id/conversation_head";

    public static final boolean START = true;
    public static final boolean STOP = false;
    public static boolean ISWORKING = STOP;
    public static boolean ISTARGETPAGE = false;
    /**
     * QQ包名
     */
    public static final String TARGET_PKG_NAME = "com.tencent.mobileqq";

    @Override
    public String getTargetPackageName() {
        return TARGET_PKG_NAME;
    }

    @Override
    public void onReceiveEvent(AccessibilityEvent event) {
        ISTARGETPAGE = false;
        if (!ISWORKING) {
            return;
        }
        //当前事件返回页
        String currentClass = event.getClassName().toString();

        if (TARGET_PAGE_CLASS.equals(currentClass)) {
            //is target page
            ISTARGETPAGE = true;
            doVisitorsWork(event);
        } else {
            //is'nt target page
            goTargetPage(event);
        }
    }

    /**
     * 是否是QQ首页
     *
     * @param root
     * @return
     */
    private boolean isHomePage(AccessibilityNodeInfo root) {
        AccessibilityNodeInfo tab_node = AccessibilityHelper.findNodeInfosById(root, id_home_tab);
        AccessibilityNodeInfo head_node = AccessibilityHelper.findNodeInfosById(root, id_home_head);
        return tab_node != null && head_node != null;
    }

    /**
     * 去目的地
     *
     * @param event
     */
    private void goTargetPage(AccessibilityEvent event) {
        Log.d(TAG, "goTargetPage");

        if (HOME_PAGE_CLASS.equals(event.getClassName())) {
            AccessibilityNodeInfo btn_head = null;
            while (ISWORKING) {
                HashMap<String, AccessibilityNodeInfo> map = new HashMap<>();
                AccessibilityHelper.addChild2Map(getService().getRootInActiveWindow(), map);
                if (!map.isEmpty()) {
                    btn_head = map.get(BTN_CLASS_NAME);
                    if (btn_head != null) {
                        break;
                    }
                }
            }
            AccessibilityHelper.performClick(btn_head);
            SystemClock.sleep(500);
            AccessibilityHelper.performBack(getService());
            SystemClock.sleep(500);
            AccessibilityHelper.performClick(btn_head);
            AccessibilityHelper.performClick(getService().getRootInActiveWindow(), id_nickname);
        } else if (Friend_PAGE_CLASS.equals(event.getClassName())) {
            //到达friend page查询点击点赞按钮
            ArrayList<AccessibilityNodeInfo> childs = new ArrayList<>();
            AccessibilityHelper.addChild2List(event.getSource(), childs);
            for (AccessibilityNodeInfo child : childs) {
                if (RL_CLASS_NAME.equals(child.getClassName()) && !StringUtil.isEmpty(child.getContentDescription() + "")) {
                    AccessibilityHelper.performClick(child);
                }
            }
        }
    }

    /**
     * 开始操作
     *
     * @param event
     */
    private void doVisitorsWork(AccessibilityEvent event) {
        Log.d(TAG, "doVisitorsWork");
        if (!ISTARGETPAGE) {
            return;
        }
        AccessibilityNodeInfo absList = null;
        int countNum = 0;
        ArrayList<AccessibilityNodeInfo> visitors = new ArrayList<>();
        ArrayList<AccessibilityNodeInfo> childs = new ArrayList<>();
        absList = getVisitorNode(event, absList,visitors, childs);
        while (ISWORKING) {
            for (AccessibilityNodeInfo visitor : visitors) {
                if (!ISTARGETPAGE) {
                    break;
                }
                clickV(visitor);
                countNum++;
                if (countNum > getConfig().getQQZNumber()) {
                    break;
                }
                //点下一个好友间隔时间
                if (getConfig().getQQZDelayTime() > 0) {
                    SystemClock.sleep(getConfig().getQQZDelayTime());
                }
            }
            if (countNum > getConfig().getQQZNumber()) {
                break;
            }
            //flip page
            if (AccessibilityHelper.performPageDown(absList)) {
                //此处等待页面加载确保所有元素出现
                SystemClock.sleep(500);
                visitors.clear();
                childs.clear();
                getVisitorNode(event, absList, visitors, childs);
                //此处等待点击“显示更多”后页面加载延时
            } else {
                SystemClock.sleep(2000);
                if (AccessibilityHelper.performPageDown(absList)){
                    SystemClock.sleep(500);
                    visitors.clear();
                    childs.clear();
                    getVisitorNode(event, absList, visitors, childs);
                }else {
                    break;
                }
            }
        }
        //task end
        ISWORKING = STOP;
    }

    /**
     * 获取赞元素
     *
     * @param event
     * @param absList
     * @param visitors
     * @param childs
     * @return
     */
    private AccessibilityNodeInfo getVisitorNode(AccessibilityEvent event, AccessibilityNodeInfo absList, ArrayList<AccessibilityNodeInfo> visitors, ArrayList<AccessibilityNodeInfo> childs) {
        AccessibilityHelper.addChild2List(event.getSource(), childs);
        for (AccessibilityNodeInfo child : childs) {
            if (!ISTARGETPAGE) {
                break;
            }
            if (ABS_LIST_CLASS_NAME.equals(child.getClassName()) && child.isScrollable()) {
                absList = child;
                //发现分页按钮立即点击
                AccessibilityNodeInfo showMore = AccessibilityHelper.findNodeInfosByText(absList, "显示更多");
                AccessibilityHelper.performClick(showMore);
            }
        }
//        String s = EleTree.GetTreeInfo(absList);
//        String path = Environment.getExternalStorageDirectory()+"/com.rwx/bingo/bingo.txt";
//        if (FileUtils.createOrExistsFile(path)) {
//            FileUtils.writeFileFromString(path,s,false);
//        }
        List<AccessibilityNodeInfo> name_list = AccessibilityHelper.findNodeInfoListById(event.getSource(), id_visitor_name);

        //剔除黑名单
        //name_list.removeAll();
        filterBlackName(name_list);

        if (!name_list.isEmpty()) {
            for (AccessibilityNodeInfo name : name_list) {
//                LogUtil.d(TAG,"name:"+name.getText().toString());
                AccessibilityNodeInfo item = name.getParent();
                for (int i = 0; i < item.getChildCount(); i++) {
                    AccessibilityNodeInfo temp = item.getChild(i);
                    if (temp != null && IV_CLASS_NAME.equals(temp.getClassName())) {
                        visitors.add(temp);
                    }
                }
            }
        }

        return absList;
    }

    /**
     * 从name_list中去除黑名单
     * @param name_list
     */
    private void filterBlackName(List<AccessibilityNodeInfo> name_list) {
        String blackStr = Config.getConfig(getContext()).getQQZBlackList();
        if (StringUtils.isEmpty(blackStr)){
            return;
        }
        String[] blackArr = blackStr.split("#");
        List<String> blackList = Arrays.asList(blackArr);

        Map<String,AccessibilityNodeInfo> map = new HashMap<>();
        for (AccessibilityNodeInfo name : name_list) {
            if (name.getText()!=null){
                map.put(name.getText().toString(),name);
            }
        }

        //从map中删除相应对象
        for (String s : blackList) {
            map.remove(s);
        }

        name_list.clear();
        for (AccessibilityNodeInfo info : map.values()) {
            name_list.add(info);
        }
    }

    /**
     * 点赞
     *
     * @param visitor
     */
    private void clickV(AccessibilityNodeInfo visitor) {
        for (int i = 0; i < getConfig().getQQZTimes(); i++) {
            AccessibilityHelper.performClick(visitor);
            //点赞延时。。。
        }
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
