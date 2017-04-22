package com.skyrin.bingo.ui.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;

import com.skyrin.bingo.Config;
import com.skyrin.bingo.R;
import com.skyrin.bingo.UmengConfig;
import com.skyrin.bingo.common.ui.UIHelper;
import com.skyrin.bingo.database.bll.BMsg;
import com.skyrin.bingo.modle.TMsg;

import java.util.List;

/**
 * Created by admin on 2016/12/21.
 */

public class MMActivity extends BaseSettingsActivity {
    @Override
    public Fragment getSettingsFragment() {
        setTitle("基本设置");
        return new MMFragment();
    }

    public static class MMFragment extends BaseSettingsFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.mm_settings);
            todo();
        }

        @Override
        protected void todo() {
            //summary初始化

            final SwitchPreference sw_lock_get = (SwitchPreference) findPreference(Config.KEY_LOCKED_GET_HONGBAO);
            sw_lock_get.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (Boolean.parseBoolean(String.valueOf(newValue))){
                        UmengConfig.eventStatistics(getActivity(),UmengConfig.EVENT_OPEN_LOCK_GET);
                    }
                    return true;
                }
            });

            //模式
            final ListPreference modepf = (ListPreference) findPreference(Config.KEY_MM_MODE);
            modepf.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    modepf.setSummary(modepf.getEntries()[value]);
                    return true;
                }
            });
            modepf.setSummary(modepf.getEntries()[Integer.parseInt(modepf.getValue())]);
            //抢红包后
            final ListPreference getpf = (ListPreference) findPreference(Config.KEY_MM_AFTER_GET_HONGBAO);
            getpf.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    getpf.setSummary(getpf.getEntries()[value]);
                    return true;
                }
            });
            getpf.setSummary(getpf.getEntries()[Integer.parseInt(getpf.getValue())]);
            //拆红包延时
            final EditTextPreference delaypf = (EditTextPreference) findPreference(Config.KEY_MM_DELAY_TIME);
            delaypf.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    delaypf.setSummary("已延时"+newValue+"毫秒");
                    return true;
                }
            });
            String time = delaypf.getText();
            delaypf.setSummary("已延时"+time+"毫秒");
            //拆红包后
            final ListPreference openpf = (ListPreference) findPreference(Config.KEY_MM_AFTER_OPEN_HONGBAO);
            openpf.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    if (value==Config.WX_AFTER_OPEN_AUTO_REPLAY){
                        List<TMsg> list = BMsg.getInstance(getActivity()).query();
                        if (list.isEmpty()){
                            UIHelper.ShowToast(getActivity(),"请先在自动回复设置中添加消息");
                            return false;
                        }
                    }
                    openpf.setSummary(openpf.getEntries()[value]);
                    return true;
                }
            });
            openpf.setSummary(openpf.getEntries()[Integer.parseInt(openpf.getValue())]);

            /**
             * QQ
             * */
            //模式
            final ListPreference qqmodepf = (ListPreference) findPreference(Config.KEY_QQ_MODE);
            qqmodepf.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    qqmodepf.setSummary(qqmodepf.getEntries()[value]);
                    return true;
                }
            });
            qqmodepf.setSummary(qqmodepf.getEntries()[Integer.parseInt(qqmodepf.getValue())]);
            //拆红包后
            final ListPreference qqopenpf = (ListPreference) findPreference(Config.KEY_QQ_AFTER_OPEN_HONGBAO);
            qqopenpf.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    if (value==Config.WX_AFTER_OPEN_AUTO_REPLAY){
                        List<TMsg> list = BMsg.getInstance(getActivity()).query();
                        if (list.isEmpty()){
                            UIHelper.ShowToast(getActivity(),"请先在自动回复设置中添加消息");
                            return false;
                        }
                    }
                    qqopenpf.setSummary(qqopenpf.getEntries()[value]);
                    return true;
                }
            });
            qqopenpf.setSummary(qqopenpf.getEntries()[Integer.parseInt(qqopenpf.getValue())]);
        }
    }
}
