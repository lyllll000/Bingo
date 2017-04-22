package com.skyrin.bingo;

import android.content.Context;
import android.content.PeriodicSync;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;

import static u.aly.x.k;

/**
 * Created by skyrin on 2016/12/18.
 */

public class Config {

    /**
     * Action
     * */
    public static final String ACTION_NOTIFY_TIPS_COUNT_CHANGE = "com.skyrin.bingo.ACTION_NOTIFY_TIPS_COUNT_CHANGE";
    public static final String ACTION_BINGO_SERVICE_DISCONNECT = "com.skyrin.bingo.ACTION_BINGO_SERVICE_DISCONNECT";
    public static final String ACTION_BINGO_SERVICE_CONNECT = "com.skyrin.bingo.ACTION_BINGO_SERVICE_CONNECT";

    public static final String ACTION_NOTIFY_LISTENER_SERVICE_DISCONNECT = "com.skyrin.bingo.ACTION_NOTIFY_LISTENER_SERVICE_DISCONNECT";
    public static final String ACTION_NOTIFY_LISTENER_SERVICE_CONNECT = "com.skyrin.bingo.ACTION_NOTIFY_LISTENER_SERVICE_CONNECT";

    /**
     * Key
     * */
    public static final String PREFERENCE_NAME = "config";
    public static final String KEY_ENABLE_MM = "KEY_ENABLE_MM";
    public static final String KEY_ENABLE_QQ = "KEY_ENABLE_QQ";
    public static final String KEY_RANDOM_REPLAY_MM = "KEY_RANDOM_REPLAY_MM";
    public static final String KEY_RANDOM_REPLAY_QQ = "KEY_RANDOM_REPLAY_QQ";

    public static final String KEY_TIPS_COUNT = "KEY_TIPS_COUNT";
    public static final String KEY_GET_HONGBAO_COUNT_MM = "KEY_GET_HONGBAO_COUNT_MM";
    public static final String KEY_GET_HONGBAO_COUNT_QQ = "KEY_GET_HONGBAO_COUNT_QQ";
    public static final String KEY_GET_MONEY_QQ = "KEY_GET_MONEY_COUNT_QQ";
    public static final String KEY_GET_MONEY_MM= "KEY_GET_MONEY_MM";

    /**
     * QQ赞次数及间隔时间
     */
    public static final String KEY_QQZ_TIMES= "KEY_QQZ_TIMES";
    public static final String KEY_QQZ_DELAY_TIME= "KEY_QQZ_DELAY_TIME";
    public static final String KEY_QQZ_NUMBER= "KEY_QQZ_NUMBER";
    public static final String KEY_BLACK_LIST= "KEY_BLACK_LIST";


    /**
     * 提示设置
     */
    public static final String KEY_NOTIFY_SOUND = "KEY_NOTIFY_SOUND";
    public static final String KEY_NOTIFY_VIBRATE = "KEY_NOTIFY_VIBRATE";
    public static final String KEY_NOTIFY_NIGHT = "KEY_NOTIFY_NIGHT_ENABLE";

    private static final String KEY_AGREEMENT = "KEY_AGREEMENT";

    public static final String KEY_MM_AFTER_OPEN_HONGBAO = "KEY_MM_AFTER_OPEN_HONGBAO";
    public static final String KEY_MM_DELAY_TIME = "KEY_MM_DELAY_TIME";
    public static final String KEY_MM_AFTER_GET_HONGBAO = "KEY_MM_AFTER_GET_HONGBAO";
    public static final String KEY_MM_MODE = "KEY_MM_MODE";

    public static final String KEY_QQ_MODE = "KEY_QQ_MODE";
    public static final String KEY_QQ_AFTER_OPEN_HONGBAO = "KEY_QQ_AFTER_OPEN_HONGBAO";

    public static final int QQ_MODE_0 = 0;//自动抢
    public static final int QQ_MODE_1 = 1;//通知手动抢

    public static final String KEY_NOTIFICATION_SERVICE_ENABLE = "KEY_NOTIFICATION_SERVICE_ENABLE";

    public static final int WX_MODE_0 = 0;//自动抢
    public static final int WX_MODE_1 = 1;//抢单聊红包,群聊红包只通知
    public static final int WX_MODE_2 = 2;//抢群聊红包,单聊红包只通知
    public static final int WX_MODE_3 = 3;//通知手动抢

    public static final int WX_AFTER_OPEN_GOHOME = 0; //返回桌面
    public static final int WX_AFTER_OPEN_NONE = 1; //静静地看着
    public static final int WX_AFTER_OPEN_AUTO_REPLAY = 2; //自动回复

    public static final int WX_AFTER_GET_OPEN = 0;//拆红包
    public static final int WX_AFTER_GET_NONE = 1; //静静地看着

    /**
     * 通用设置
     */
    //锁屏获取红包
    public static final String KEY_LOCKED_GET_HONGBAO = "KEY_LOCKED_GET_HONGBAO";

    Context context;
    static Config config;
    SharedPreferences preferences;

    private Config (Context context){
        this.context = context;
        preferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    public static synchronized Config getConfig(Context context){
        if (config==null){
            config = new Config(context.getApplicationContext());
        }
        return config;
    }

    /**
     * QQ赞执行次数 1~10
     * @return
     */
    public int getQQZTimes(){
        int defaultValue = 8;
        String result =  preferences.getString(KEY_QQZ_TIMES, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /**
     * QQ赞黑名单
     * @return
     */
    public String getQQZBlackList(){
        return preferences.getString(KEY_BLACK_LIST,"");
    }

    /**
     * QQ赞好友个数 1~599
     * @return
     */
    public int getQQZNumber(){
        int defaultValue = 599;
        String result =  preferences.getString(KEY_QQZ_NUMBER, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /**
     * qq赞执行间隔时间
     * @return
     */
    public int getQQZDelayTime(){
        int defaultValue = 0;
        String result =  preferences.getString(KEY_QQZ_DELAY_TIME, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /**
     * 获取MM红包个数
     * @return
     */
    public int getLuckyMoneyCountMM(){
        return preferences.getInt(KEY_GET_HONGBAO_COUNT_MM,0);
    }

    /**
     * 获取QQ红包个数
     * @return
     */
    public int getLuckyMoneyCountQQ(){
        return preferences.getInt(KEY_GET_HONGBAO_COUNT_QQ,0);
    }

    /**
     * 设置QQ红包个数
     * @return
     */
    public void setLuckyMoneyCountQQ(int count){
        preferences.edit().putInt(KEY_GET_HONGBAO_COUNT_QQ,count).apply();
    }

    /**
     * 设置MM红包个数
     * @return
     */
    public void setLuckyMoneyCountMM(int count){
        preferences.edit().putInt(KEY_GET_HONGBAO_COUNT_MM,count).apply();
    }

    //
    /**
     * 获取MM红包个数
     * @return
     */
    public float getMoneyMM(){
        return preferences.getFloat(KEY_GET_MONEY_MM,0);
    }

    /**
     * 获取QQ红包个数
     * @return
     */
    public float getMoneyQQ(){
        return preferences.getFloat(KEY_GET_MONEY_QQ,0);
    }

    /**
     * 设置QQ红包个数
     * @return
     */
    public void setMoneyQQ(float count){
        preferences.edit().putFloat(KEY_GET_MONEY_QQ,count).apply();
    }

    /**
     * 设置MM红包个数
     * @return
     */
    public void setMoneyMM(float count){
        preferences.edit().putFloat(KEY_GET_MONEY_MM,count).apply();
    }

    /**
     * 获取红包提醒次数
     * @return
     */
    public int getTipsCount(){
        return preferences.getInt(KEY_TIPS_COUNT,0);
    }

    /**
     * 设置红包提醒次数
     * @return
     */
    public void setTipsCount(int count){
        preferences.edit().putInt(KEY_TIPS_COUNT,count).apply();
    }

    /**
     * 是否锁屏抢红包
     * @return
     */
    public boolean isLockedGetHongbao(){
        return preferences.getBoolean(KEY_LOCKED_GET_HONGBAO,false);
    }

    /**
     * 是否随机回复
     * @return
     */
    public boolean isRandomRepalyMM(){
        return preferences.getBoolean(KEY_RANDOM_REPLAY_MM,false);
    }

    /**
     * 设置随机回复
     * @param b
     */
    public void setRandomReplayMM(boolean b){
        preferences.edit().putBoolean(KEY_RANDOM_REPLAY_MM,b).apply();
    }

    /**
     * 是否随机回复
     * @return
     */
    public boolean isRandomRepalyQQ(){
        return preferences.getBoolean(KEY_RANDOM_REPLAY_QQ,false);
    }

    /**
     * 设置随机回复
     * @param b
     */
    public void setRandomReplayQQ(boolean b){
        preferences.edit().putBoolean(KEY_RANDOM_REPLAY_QQ,b).apply();
    }

    /**
     * 是否开启声音提示
     * @return
     */
    public boolean isNotifySound(){
        return preferences.getBoolean(KEY_NOTIFY_SOUND,false);
    }

    /**
     * 是否开启震动
     * @return
     */
    public boolean isNotifyVibrate(){
        return preferences.getBoolean(KEY_NOTIFY_VIBRATE,false);
    }

    /**
     * 是否开启夜间免打扰
     * @return
     */
    public boolean isNotifyNight(){
        return preferences.getBoolean(KEY_NOTIFY_NIGHT,false);
    }

    /**
     * 是否开启微信
     * @return
     */
    public boolean isEnableMM(){
        return preferences.getBoolean(KEY_ENABLE_MM,false);
    }

    /**
     * 是否开启QQ
     * @return
     */
    public boolean isEnableQQ(){
        return preferences.getBoolean(KEY_ENABLE_QQ,false);
    }

    /** 微信打开红包后的事件*/
    public int getWechatAfterOpenHongBaoEvent() {
        int defaultValue = 0;
        String result =  preferences.getString(KEY_MM_AFTER_OPEN_HONGBAO, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /** 微信抢到红包后的事件*/
    public int getWechatAfterGetHongBaoEvent() {
        int defaultValue = 1;
        String result =  preferences.getString(KEY_MM_AFTER_GET_HONGBAO, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /** 微信打开红包后延时时间*/
    public int getWechatOpenDelayTime() {
        int defaultValue = 50;
        String result = preferences.getString(KEY_MM_DELAY_TIME, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /** 获取抢微信红包的模式*/
    public int getWechatMode() {
        int defaultValue = 0;
        String result = preferences.getString(KEY_MM_MODE, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /** 获取抢QQ红包的模式*/
    public int getQQMode() {
        int defaultValue = 0;
        String result = preferences.getString(KEY_QQ_MODE, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /** QQ打开红包后的事件*/
    public int getQQAfterOpenHongBaoEvent() {
        int defaultValue = 0;
        String result =  preferences.getString(KEY_QQ_AFTER_OPEN_HONGBAO, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /** 是否启动通知栏模式*/
    public boolean isEnableNotificationService() {
        return preferences.getBoolean(KEY_NOTIFICATION_SERVICE_ENABLE, true);
    }

    public void setNotificationServiceEnable(boolean enable) {
        preferences.edit().putBoolean(KEY_NOTIFICATION_SERVICE_ENABLE, enable).apply();
    }

    public void setEnableMM(boolean b){
        preferences.edit().putBoolean(KEY_ENABLE_MM,b).apply();
    }

    public void setEnableQQ(boolean b){
        preferences.edit().putBoolean(KEY_ENABLE_QQ,b).apply();
    }

    /** 免费声明*/
    public boolean isAgreement() {
        return preferences.getBoolean(KEY_AGREEMENT, true);
    }

    /** 设置是否同意*/
    public void setAgreement(boolean agreement) {
        preferences.edit().putBoolean(KEY_AGREEMENT, agreement).apply();
    }
}
