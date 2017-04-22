package com.skyrin.bingo.job;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.skyrin.bingo.Config;
import com.skyrin.bingo.UmengConfig;
import com.skyrin.bingo.common.device.ScreenUtil;
import com.skyrin.bingo.common.ui.UIHelper;
import com.skyrin.bingo.database.bll.BMsg;
import com.skyrin.bingo.modle.TMsg;
import com.skyrin.bingo.service.BingoService;
import com.skyrin.bingo.service.IStatusBarNotification;
import com.skyrin.bingo.ui.NotifyHelper;
import com.skyrin.bingo.ui.settings.OpenLockScreenActivity;
import com.skyrin.bingo.util.AccessibilityHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.skyrin.bingo.util.AccessibilityHelper.findNodeInfosByText;

/**
 * Created by skyrin on 2016/12/18.
 */

public class QQJob extends BaseAccessibilityJob {

    private static final String TAG = "QQJob";
    /**
     * QQ包名
     */
    public static final String TARGET_PKG_NAME = "com.tencent.mobileqq";
    /**
     * 红包消息的关键字
     */
    private static final String HONGBAO_TEXT_KEY = "[QQ红包]";
    /**
     * 聊天item ID
     */
    private static final String CHAT_ITEM_ID = "com.tencent.mobileqq:id/chat_item_content_layout";

    private static final String RL_CLASS_NAME = "android.widget.RelativeLayout";
    private static final String TEXT_VIEW_CLASS_NAME = "android.widget.TextView";
    private static final String EDT_INPUT_ID = "com.tencent.mobileqq:id/input";
    private static final String BTN_SEND_ID = "com.tencent.mobileqq:id/fun_btn";
    private static final String INPUTBAR_ID = "com.tencent.mobileqq:id/inputBar";
    private static final String KOLING_TEXT = "点击输入口令";

    /**
     * QQ群
     */
    private static final String TITLE_CALL_DESC = "群通知中心";
    private static final String TITLE_IMG_DESC = "群资料卡";
    private static final String TITLE_CALL_ID = "com.tencent.mobileqq:id/ivTitleBtnRightCall";
    private static final String TITLE_IMG_ID = "com.tencent.mobileqq:id/ivTitleBtnRightImage";

    /**
     * 是否收到红包
     */
    private boolean isReceivingHongbao = false;
    /**
     * 是否已拆开红包
     */
    private boolean isOpenHongbao = false;
    /**
     * 是否自动回复
     */
    private boolean isAutoReplay = false;
    /**
     * 是否已回复
     */
    private boolean isReplayed = false;
    /**
     * 是否已结束任务
     */
    private boolean isWorkEnd = false;
    /**
     * 是否已找到红包
     */
    private boolean isFindHongbao;

    private static int currentWindow = 0;

    private static final int WINDOW_DETILE_QWALLET = 1001;
    private static final int WINDOW_SPLASH = 1002;
    private static final int MAX_TIME_OUT = 3000;

    private Handler mHandler = null;

    /**
     * 回复消息
     */
    List<TMsg> msgs;
    /**
     * 根节点
     */
    AccessibilityNodeInfo rootInfo = null;

    @Override
    public String getTargetPackageName() {
        return TARGET_PKG_NAME;
    }

    @Override
    public void onReceiveEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();

        if (isAutoReplay && isOpenHongbao) {
            autoReplay();
        }
        whereAmI(event);
        //通知栏事件
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Parcelable data = event.getParcelableData();
            if (data == null || !(data instanceof Notification)) {
                return;
            }
            if (BingoService.isNotificationServiceRunning() && getConfig().isEnableNotificationService()) { //开启快速模式，不处理
                return;
            }
            List<CharSequence> texts = event.getText();
            if (!texts.isEmpty()) {
                String text = String.valueOf(texts.get(0));
                notificationEvent(text, (Notification) data);
            }
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (isWorkEnd){
                return;
            }
            //Log.i(TAG, "TYPE_WINDOW_STATE_CHANGED");
            doSettings(event);
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (isWorkEnd){
                return;
            }
            //Log.i(TAG, "TYPE_WINDOW_CONTENT_CHANGED");
            if (isReceivingHongbao && getConfig().getQQMode() == Config.QQ_MODE_0) {
                findBingo();
            }
        }
    }

    /**
     * 存储金额
     * @param event
     */
    private void saveMoney(AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className != null && "cooperation.qwallet.plugin.QWalletPluginProxyActivity".equals(className)) {
            //Log.d(TAG, "saveMoney>>>>>");
            AccessibilityNodeInfo nodeInfo = event.getSource();

            //Log.d(TAG, "nodeInfoChildCount>>>"+nodeInfo.getChildCount());
            if (nodeInfo == null) {
                //Log.d(TAG, "nodeinfo is null");
            } else {
                AccessibilityNodeInfo node = null;
                AccessibilityNodeInfo over = null;
                long startTime = SystemClock.currentThreadTimeMillis();
                while (SystemClock.currentThreadTimeMillis()-startTime<3000){
                    node = AccessibilityHelper.findNodeInfosByText(nodeInfo,"已存入余额");
                    over = AccessibilityHelper.findNodeInfosByText(nodeInfo,"来晚一步，领完啦~");
                    if (node!=null&&node.isVisibleToUser()){
                        //Log.d(TAG, "已存入余额可见");
                        break;
                    }
                    if (over!=null&&over.isVisibleToUser()){
                        isWorkEnd=true;
                        return;
                    }
                }

                //当上面元素可见时再获取root元素，此时元素才能包含money
                rootInfo = getService().getRootInActiveWindow();

                try {
                    String moneyStr = findMoneyByNodeInfo(rootInfo);
                    if (moneyStr != null) {
                        //Log.i(TAG, "moneyStr " + moneyStr);
//                        long money = (long) Math.round(100.0f * Float.parseFloat(moneyStr));
                        float money = Float.parseFloat(moneyStr);
                        float countMoney = getConfig().getMoneyQQ()+money;
                        getConfig().setMoneyQQ(countMoney);
                        //Log.d(TAG, "已存入");
                    }else {
                        isWorkEnd=true;
                    }
                } catch (Exception e) {
                    //Log.i(TAG, "QQ get money failed", e);
                }
            }
        }

    }

    /**
     * 更新当前位置
     */
    private void whereAmI(AccessibilityEvent event) {
        if ("com.tencent.mobileqq.activity.SplashActivity".equals(String.valueOf(event.getClassName()))) {
            currentWindow = WINDOW_SPLASH;
//            int mode = getConfig().getQQMode();
//            if (mode==Config.WX_MODE_3){ //通知手动抢
//                NotifyHelper.playEffect(getContext(),Config.getConfig(getContext()));
//                return;
//            }
        } else if ("cooperation.qwallet.plugin.QWalletPluginProxyActivity".equals(String.valueOf(event.getClassName()))) {

            //LogUtil.i(TAG,"QWalletPluginProxyActivity");

            currentWindow = WINDOW_DETILE_QWALLET;
        }
    }

    /**
     * 找到金额
     * @param nodeInfo
     * @return
     */
    private String findMoneyByNodeInfo(AccessibilityNodeInfo nodeInfo) {
        ArrayList<String> viewTexts = new ArrayList();
        recursiveFindText(nodeInfo, nodeInfo.getChildCount(), viewTexts);
        int index = viewTexts.indexOf("元");
        if (index < 1) {
            return null;
        }
        return viewTexts.get(index - 1);
    }

    /**
     * 存储数字 info 下所有元素的字符 2 viewTexts
     * @param info
     * @param count
     * @param viewTexts
     */
    private void recursiveFindText(AccessibilityNodeInfo info, int count, ArrayList<String> viewTexts) {
        if (count > 0 && info != null) {
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo NodeInfo = info.getChild(i);
                CharSequence text = NodeInfo.getText();
                if (text != null) {
                    viewTexts.add(text.toString());
                }
                recursiveFindText(NodeInfo, NodeInfo.getChildCount(), viewTexts);
            }
        }
    }

    /**
     * 将一个节点下的所有子节点全部放入list
     *
     * @param node
     * @param map
     */
    private void addChild2Map(AccessibilityNodeInfo node, Map<String, AccessibilityNodeInfo> map) {
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
     * 存储金额
     */
    private void saveMoneyAA() {
        //LogUtil.i(TAG,"saveMoney");
        AccessibilityNodeInfo nodechild = null;
        long start = SystemClock.currentThreadTimeMillis();
        while (SystemClock.currentThreadTimeMillis()-start<MAX_TIME_OUT){
            if (nodechild!=null){
                break;
            }
            nodechild = AccessibilityHelper.findNodeInfosByText(getService().getRootInActiveWindow(),"元");
        }
        AccessibilityNodeInfo nodeparent=null;
        if (nodechild!=null){
            //LogUtil.i(TAG,"find>已存入余额");
            nodeparent = nodechild.getParent();
        }else {
            //LogUtil.i(TAG,"未发现>已存入余额");
        }

        if (nodeparent!=null&&nodeparent.getChildCount()>0){
            for (int i = 0; i <nodeparent.getChildCount() ; i++) {
                AccessibilityNodeInfo node= nodeparent.getChild(i);
                if (TEXT_VIEW_CLASS_NAME.equals(String.valueOf(node.getClassName()))){
                    try {
                        float money = Float.valueOf(String.valueOf(node.getText()));
                        float countMoney = getConfig().getMoneyQQ()+money;
                        getConfig().setMoneyQQ(countMoney);
                        //LogUtil.i(TAG,"countMoney>已计入");
                    }catch (Exception e){}
                }
            }
        }
    }

    /**
     * 获取一条消息
     *
     * @param isRandom 是否随机获取
     * @return
     */
    private String getMsg(List<TMsg> msgs, boolean isRandom) {
        String content;
        if (isRandom) {
            int index = new Random().nextInt(msgs.size());
            content = msgs.get(index).getMsg();
        } else {
            content = msgs.get(0).getMsg();
        }
        return content;
    }

    /**
     * 通过text查找一个元素并点击
     *
     * @param rootInfo
     * @param text
     */
    private void findAndClick4Text(AccessibilityNodeInfo rootInfo, String text) {
        if (rootInfo == null) {
            return;
        }
        AccessibilityNodeInfo target = null;
        long start = SystemClock.currentThreadTimeMillis();
        while (SystemClock.currentThreadTimeMillis()-start<MAX_TIME_OUT){
            if (target != null) {
                break;
            }
            sleepShort();
            target = AccessibilityHelper.findNodeInfosById(rootInfo, text);
        }
        click(target);
    }

    /**
     * 点击一个元素
     *
     * @param info
     */
    private void click(AccessibilityNodeInfo info) {
        AccessibilityHelper.performClick(info);
    }

    /**
     * 自动回复
     */
    void autoReplay() {
        AccessibilityNodeInfo edt_input = AccessibilityHelper.findNodeInfosById(getService().getRootInActiveWindow(), EDT_INPUT_ID);

        long start = SystemClock.currentThreadTimeMillis();
        while (SystemClock.currentThreadTimeMillis()-start<MAX_TIME_OUT){
            if (edt_input != null) {
                break;
            }
            edt_input = AccessibilityHelper.findNodeInfosById(getService().getRootInActiveWindow(), EDT_INPUT_ID);
        }

        if (edt_input != null) {
            edt_input.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            sleepLong();

            msgs = BMsg.getInstance(getContext()).query();

            if (msgs.size() > 0) {
                boolean isRandom = Config.getConfig(getContext()).isRandomRepalyMM();

                pasteMsg2Info(getContext(), edt_input, getMsg(msgs, isRandom));

                sleepLong();

                findAndClick4Text(getService().getRootInActiveWindow(), BTN_SEND_ID);
                isAutoReplay = false;
                AccessibilityHelper.performHome(getService());
            } else {
                UIHelper.ShowToast(getContext(), "没有消息可以回复~");
            }
        } else {
            //LogUtil.e(TAG, "edt_input == null");
        }
    }

    /**
     * 执行设置操作
     *
     * @param event
     */
    private void doSettings(AccessibilityEvent event) {
        //LogUtil.e(TAG, "class>>>>>>>>" + event.getClassName().toString());
        if ("com.tencent.mobileqq.activity.SplashActivity".equals(String.valueOf(event.getClassName()))) {
            currentWindow = WINDOW_SPLASH;
            rootInfo = getService().getRootInActiveWindow();
        } else if ("cooperation.qwallet.plugin.QWalletPluginProxyActivity".equals(String.valueOf(event.getClassName()))) {
            currentWindow = WINDOW_DETILE_QWALLET;
            isOpenHongbao = true;
            //存储金额
            saveMoney(event);

            //存储金失败则任务结束
            if (isWorkEnd){
                return;
            }

            if (getConfig().getQQAfterOpenHongBaoEvent() == Config.WX_AFTER_OPEN_GOHOME) {
                //LogUtil.e(TAG, "class>>>>>>>>performHome");
                AccessibilityHelper.performHome(getService());
                isWorkEnd = true;
            } else if (getConfig().getQQAfterOpenHongBaoEvent() == Config.WX_AFTER_OPEN_AUTO_REPLAY) {
                isAutoReplay = true;
                //LogUtil.e(TAG, "class>>>>>>>>performBack");
                AccessibilityNodeInfo hbjl = null;
                long start = SystemClock.currentThreadTimeMillis();
                while (SystemClock.currentThreadTimeMillis()-start<MAX_TIME_OUT){
                    if (hbjl != null) {
                        break;
                    }
                    hbjl = AccessibilityHelper.findNodeInfosByTexts(getService().getRootInActiveWindow(), new String[]{"查看领取详情 ", "红包记录"});
                }
                if (currentWindow == WINDOW_DETILE_QWALLET) {
                    AccessibilityHelper.performBack(getService());
                    isWorkEnd = true;
                }
            } else {

            }
        }
    }

    private Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 找到红包并打开
     */
    private void findBingo() {

        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if (nodeInfo == null) {
            //Log.w(TAG, "rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> chat_list = nodeInfo.findAccessibilityNodeInfosByViewId(CHAT_ITEM_ID);

        List<AccessibilityNodeInfo> rl_list = new ArrayList<>();

        if (chat_list != null && !chat_list.isEmpty()) {

            for (int i = 0; i < chat_list.size(); i++) {
                AccessibilityNodeInfo item = chat_list.get(i);
                if (RL_CLASS_NAME.equals(String.valueOf(item.getClassName()))) {
                    rl_list.add(item);
                }
            }

            if (!rl_list.isEmpty()) {
                //找到红包
                AccessibilityNodeInfo node = rl_list.get(rl_list.size() - 1);
                //拆开红包
                AccessibilityHelper.performClick(node);
            } else {
                AccessibilityHelper.performHome(getService());
            }

            if (currentWindow != WINDOW_DETILE_QWALLET) {
                final AccessibilityNodeInfo koling_node = findNodeInfosByText(getService().getRootInActiveWindow(), KOLING_TEXT);
                if (koling_node != null) {
                    click(koling_node);
                    findAndClick4Text(getService().getRootInActiveWindow(), BTN_SEND_ID);
                }
            }
            isReceivingHongbao = false;
        }

    }

    /**
     * 当前界面是否是群聊
     *
     * @return
     */
    boolean isMemberChat() {
        AccessibilityNodeInfo root = getService().getRootInActiveWindow();
        AccessibilityNodeInfo title_call_node = AccessibilityHelper.findNodeInfosById(root, TITLE_CALL_ID);
        if (title_call_node != null && TITLE_CALL_DESC.equals(String.valueOf(title_call_node.getContentDescription()))) {
            return true;
        }
        return false;
    }

    @Override
    public void onStopJob() {

    }

    /**
     * 打开通知栏消息
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void newHongBaoNotification(Notification notification) {
        UmengConfig.eventStatistics(getContext(),UmengConfig.EVENT_RECIVE_QQ_LUCKY_MONEY_TIMES);
        isReceivingHongbao = true;
        isWorkEnd = false;
        if (getConfig().getQQAfterOpenHongBaoEvent() != Config.WX_AFTER_OPEN_AUTO_REPLAY) {
            isReplayed = true;
        } else {
            isReplayed = false;
        }
        //以下是精华，将微信的通知栏消息打开
        PendingIntent pendingIntent = notification.contentIntent;
        boolean lock = NotifyHelper.isLockScreen(getContext());

        if (!lock) {
            NotifyHelper.send(pendingIntent);
        } else {
            if (getConfig().isLockedGetHongbao()) {
                wakeScreen();
                unLockScreen(pendingIntent);
            } else {
                NotifyHelper.playEffect(getContext(), getConfig());
            }
        }

        if (getConfig().getQQMode() != Config.QQ_MODE_0) {
            NotifyHelper.playEffect(getContext(), getConfig());
        }

        getConfig().setLuckyMoneyCountQQ(getConfig().getLuckyMoneyCountQQ() + 1);
        getContext().sendBroadcast(new Intent(Config.ACTION_NOTIFY_TIPS_COUNT_CHANGE));
    }

    /**
     * 亮屏
     */
    private void wakeScreen() {
        ScreenUtil.powerOnScreen(getContext());
    }

    /**
     * 解锁屏幕
     */
    private void unLockScreen(PendingIntent pendingIntent) {
        Intent intent = new Intent();
        intent.setClass(getContext(), OpenLockScreenActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra(OpenLockScreenActivity.EXTRA_ACTION_INTENT, pendingIntent);
        getContext().startActivity(intent);
    }

    /**
     * 通知栏事件
     */
    private void notificationEvent(String ticker, Notification nf) {
        String text = ticker;
        int index = text.indexOf(":");
        if (index != -1) {
            text = text.substring(index + 1);
        }
        text = text.trim();
        if (text.contains(HONGBAO_TEXT_KEY)) { //红包消息
            newHongBaoNotification(nf);
        }
    }

    @Override
    public void onNotificationPosted(IStatusBarNotification sbn) {
        Notification nf = sbn.getNotification();
        String text = String.valueOf(sbn.getNotification().tickerText);
        notificationEvent(text, nf);
    }

    @Override
    public boolean isEnable() {
        return getConfig().isEnableQQ();
    }
}
