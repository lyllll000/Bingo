package com.skyrin.bingo.job;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.skyrin.bingo.BuildConfig;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.skyrin.bingo.util.AccessibilityHelper.findNodeInfosByText;

/**
 * Created by skyrin on 2016/12/18.
 */

public class MMJob extends BaseAccessibilityJob {
    private static final String TAG = "MMJob";

    /**
     * 微信的包名
     */
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    /**
     * 红包消息的关键字
     */
    private static final String HONGBAO_TEXT_KEY = "[微信红包]";

    private static final String BUTTON_CLASS_NAME = "android.widget.Button";
    private static final String EDITTEXT_CLASS_NAME = "android.widget.EditText";
    private static final String LINEARLAYOUT_CLASS_NAME = "android.widget.LinearLayout";
    private static final String TEXT_VIEW_CLASS_NAME = "android.widget.TextView";

    /**
     * 根节点
     */
    AccessibilityNodeInfo rootInfo = null;


    /**
     * 不能再使用文字匹配的最小版本号
     */
    private static final int USE_ID_MIN_VERSION = 700;// 6.3.8 对应code为680,6.3.9对应code为700

    private static final int WINDOW_NONE = 0;
    private static final int WINDOW_LUCKYMONEY_RECEIVEUI = 1;
    private static final int WINDOW_LUCKYMONEY_DETAIL = 2;
    private static final int WINDOW_LAUNCHER = 3;
    private static final int WINDOW_OTHER = -1;

    private int mCurrentWindow = WINDOW_NONE;

    private boolean isReceivingHongbao;
    private boolean isAbleAutoReplay = false;
    private boolean isReplayed = false;
    private boolean isWorking = false;
    private PackageInfo mWechatPackageInfo = null;
    private Handler mHandler = null;

    private static final int MAX_TIME_OUT = 3000;

    /**
     * 回复消息
     */
    List<TMsg> msgs;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新安装包信息
            updatePackageInfo();
        }
    };

    @Override
    public void onCreateJob(BingoService service) {
        super.onCreateJob(service);

        updatePackageInfo();

        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");

        getContext().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onStopJob() {
        try {
            getContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(IStatusBarNotification sbn) {
        Notification nf = sbn.getNotification();
        String text = String.valueOf(sbn.getNotification().tickerText);
        notificationEvent(text, nf);
    }

    @Override
    public boolean isEnable() {
        return getConfig().isEnableMM();
    }

    @Override
    public String getTargetPackageName() {
        return WECHAT_PACKAGENAME;
    }

    @Override
    public void onReceiveEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        rootInfo = event.getSource();
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
            //LogUtil.i(TAG, "TYPE_WINDOW_STATE_CHANGED");

            if (isAbleAutoReplay) {
                autoReplay(event);
            }
            openHongBao(event);
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            //LogUtil.i(TAG, "TYPE_WINDOW_CONTENT_CHANGED");
            if (mCurrentWindow != WINDOW_LAUNCHER) { //不在聊天界面或聊天列表，不处理
                return;
            }
            if (isReceivingHongbao) {
                handleChatListHongBao();
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
     * 从map中取出一个元素
     *
     * @param key
     * @param map
     */
    private AccessibilityNodeInfo getChild4Map(String key, Map<String, AccessibilityNodeInfo> map) {
        if (map.size() > 0) {
            return map.get(key);
        }
        return null;
    }

    /**
     * 通过text查找一个元素并点击
     *
     * @param rootInfo
     * @param text
     */
    private void findAndClick4Text(AccessibilityNodeInfo rootInfo, String text) {
        AccessibilityNodeInfo target = null;
        while (isWorking) {
            if (target!=null){
                break;
            }
            target = AccessibilityHelper.findNodeInfosByText(rootInfo, text);
        }
        click(target);
    }

    /**
     * 通过id查找一个元素并点击
     *
     * @param rootInfo
     * @param id
     */
    private void findAndClick4Id(AccessibilityNodeInfo rootInfo, String id) {
        AccessibilityNodeInfo target = null;
        while (isWorking) {
            if (target!=null){
                break;
            }
            target = AccessibilityHelper.findNodeInfosById(rootInfo, id);
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
    private void autoReplay(AccessibilityEvent event) {
        //以微信中弹窗包名识别弹窗
        if (String.valueOf(event.getClassName()).contains(".ui.base.")){
            AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText(getService().getRootInActiveWindow(),"下次再说");
            if (node!=null){
                click(node);
                AccessibilityHelper.performBack(getService());
            }
        }else {
            if (rootInfo == null) {
                isAbleAutoReplay = false;
                return;
            }
            Map<String, AccessibilityNodeInfo> childMap = new HashMap<>();

            addChild2Map(rootInfo, childMap);

            final AccessibilityNodeInfo edt = getChild4Map(EDITTEXT_CLASS_NAME, childMap);
            if (edt != null) {
                edt.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                sleepLong();

                msgs = BMsg.getInstance(getContext()).query();

                if (msgs.size() > 0) {
                    boolean isRandom = Config.getConfig(getContext()).isRandomRepalyMM();

                    pasteMsg2Info(getContext(), edt, getMsg(msgs, isRandom));

                    sleepLong();

                    findAndClick4Text(rootInfo, "发送");
                    isWorking = false;
                } else {
                    UIHelper.ShowToast(getContext(), "没有消息可以回复~");
                }
            }
            isAbleAutoReplay = false;
            AccessibilityHelper.performHome(getService());
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
     * 是否为群聊天
     */
    private boolean isMemberChatUi(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }
        String id = "com.tencent.mm:id/ces";
        int wv = getWechatVersion();
        if (wv <= 680) {
            id = "com.tencent.mm:id/ew";
        } else if (wv <= 700) {
            id = "com.tencent.mm:id/cbo";
        }
        String title = null;
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosById(nodeInfo, id);
        if (target != null) {
            title = String.valueOf(target.getText());
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("返回");

        if (list != null && !list.isEmpty()) {
            AccessibilityNodeInfo parent = null;
            for (AccessibilityNodeInfo node : list) {
                if (!"android.widget.ImageView".equals(node.getClassName())) {
                    continue;
                }
                String desc = String.valueOf(node.getContentDescription());
                if (!"返回".equals(desc)) {
                    continue;
                }
                parent = node.getParent();
                break;
            }
            if (parent != null) {
                parent = parent.getParent();
            }
            if (parent != null) {
                if (parent.getChildCount() >= 2) {
                    AccessibilityNodeInfo node = parent.getChild(1);
                    if ("android.widget.TextView".equals(node.getClassName())) {
                        title = String.valueOf(node.getText());
                    }
                }
            }
        }

        if (title != null && title.endsWith(")")) {
            return true;
        }
        return false;
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

    /**
     * 打开通知栏消息
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void newHongBaoNotification(Notification notification) {
        UmengConfig.eventStatistics(getContext(),UmengConfig.EVENT_RECIVE_MM_LUCKY_MONEY_TIMES);
        isReceivingHongbao = true;
        isWorking = true;
        if (getConfig().getWechatAfterOpenHongBaoEvent() != Config.WX_AFTER_OPEN_AUTO_REPLAY){
            isReplayed = true;
        }else {
            isReplayed = false;
        }
        //将微信的通知栏消息打开
        PendingIntent pendingIntent = notification.contentIntent;
        boolean lock = NotifyHelper.isLockScreen(getContext());

        if (!lock) {
            NotifyHelper.send(pendingIntent);
        } else {
            if (getConfig().isLockedGetHongbao()){
                wakeScreen();
                unLockScreen(pendingIntent);
            }else {
                if (getConfig().getWechatMode() != Config.WX_MODE_0) {
                    NotifyHelper.playEffect(getContext(), getConfig());
                }
                //此处可根据设置弹出悬浮窗跳转到红包窗口
            }
        }
        //更新提醒红包个数
        getConfig().setLuckyMoneyCountMM(getConfig().getLuckyMoneyCountMM()+1);
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
        intent.putExtra(OpenLockScreenActivity.EXTRA_ACTION_INTENT,pendingIntent);
        getContext().startActivity(intent);
    }

    /**
     * 存储金额
     */
    private void saveMoney() {
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
                        float countMoney = getConfig().getMoneyMM()+money;
                        getConfig().setMoneyMM(countMoney);
                    }catch (Exception e){}
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openHongBao(AccessibilityEvent event) {
        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
            mCurrentWindow = WINDOW_LUCKYMONEY_RECEIVEUI;
            //点中了红包，下一步就是去拆红包
            handleLuckyMoneyReceive();
        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
            mCurrentWindow = WINDOW_LUCKYMONEY_DETAIL;
            //存储金额
            saveMoney();
            //拆完红包后看详细的纪录界面
            if (getConfig().getWechatAfterOpenHongBaoEvent() == Config.WX_AFTER_OPEN_GOHOME) { //返回主界面，以便收到下一次的红包通知
                AccessibilityHelper.performHome(getService());
            }
            if (getConfig().getWechatAfterOpenHongBaoEvent() == Config.WX_AFTER_OPEN_AUTO_REPLAY) { //回复致谢语
                //LogUtil.i(TAG, "回复");
                isAbleAutoReplay = true;
                AccessibilityHelper.performBack(getService());
            }
        } else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            mCurrentWindow = WINDOW_LAUNCHER;
            //在聊天界面,去点中红包
            handleChatListHongBao();
        } else {
            mCurrentWindow = WINDOW_OTHER;
        }
    }

    /**
     * 点击聊天里的红包后，显示的界面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleLuckyMoneyReceive() {
        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        int event = getConfig().getWechatAfterGetHongBaoEvent();
        int wechatVersion = getWechatVersion();
        if (event == Config.WX_AFTER_GET_OPEN) { //拆红包
            if (wechatVersion < USE_ID_MIN_VERSION) {
                targetNode = findNodeInfosByText(nodeInfo, "拆红包");
            } else {
                String buttonId = "com.tencent.mm:id/b43";

                if (wechatVersion == 700) {
                    buttonId = "com.tencent.mm:id/b2c";
                }

                if (buttonId != null) {
                    targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, buttonId);
                }

                if (targetNode == null) {
                    //分别对应固定金额的红包 拼手气红包
                    AccessibilityNodeInfo textNode = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, "发了一个红包", "给你发了一个红包", "发了一个红包，金额随机");

                    if (textNode != null) {
                        for (int i = 0; i < textNode.getChildCount(); i++) {
                            AccessibilityNodeInfo node = textNode.getChild(i);
                            if (BUTTON_CLASS_NAME.equals(node.getClassName())) {
                                targetNode = node;
                                break;
                            }
                        }
                    }
                }

                if (targetNode == null) { //通过组件查找
                    targetNode = AccessibilityHelper.findNodeInfosByClassName(nodeInfo, BUTTON_CLASS_NAME);
                }
            }
        } else if (event == Config.WX_AFTER_GET_NONE) {//静静的看着
            return;
        }

        if (targetNode != null) {
            final AccessibilityNodeInfo n = targetNode;
            long sDelayTime = getConfig().getWechatOpenDelayTime();
            if (sDelayTime != 0) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AccessibilityHelper.performClick(n);
                    }
                }, sDelayTime);
            } else {
                AccessibilityHelper.performClick(n);
            }
            if (event == Config.WX_AFTER_GET_OPEN) {
                UmengConfig.eventStatistics(getContext(), UmengConfig.EVENT_OPEN_LUCKY_MONEY);
            } else {
                UmengConfig.eventStatistics(getContext(), UmengConfig.EVENT_OPEN_SEE);
            }
        }
    }

    /**
     * 收到聊天里的红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void handleChatListHongBao() {
        int mode = getConfig().getWechatMode();
        if (mode == Config.WX_MODE_3) { //只通知模式
            return;
        }

        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }

        if (mode != Config.WX_MODE_0) {
            boolean isMember = isMemberChatUi(nodeInfo);
            if (mode == Config.WX_MODE_1 && isMember) {//过滤群聊
                return;
            } else if (mode == Config.WX_MODE_2 && !isMember) { //过滤单聊
                return;
            }
        }

        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");

        if (list != null && list.isEmpty()) {
            // 从消息列表查找红包
            AccessibilityNodeInfo node = findNodeInfosByText(nodeInfo, "[微信红包]");
            if (node != null) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "-->微信红包:" + node);
                }
                isReceivingHongbao = true;
                AccessibilityHelper.performClick(nodeInfo);
            }
        } else if (list != null) {
            if (isReceivingHongbao) {
                //最新的红包领起
                AccessibilityNodeInfo node = list.get(list.size() - 1);
                AccessibilityHelper.performClick(node);
                if (isReplayed){
                    isWorking=false;
                }
                isReceivingHongbao = false;
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
     * 获取微信的版本
     */
    private int getWechatVersion() {
        if (mWechatPackageInfo == null) {
            return 0;
        }
        return mWechatPackageInfo.versionCode;
    }

    /**
     * 更新微信包信息
     */
    private void updatePackageInfo() {
        try {
            mWechatPackageInfo = getContext().getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
