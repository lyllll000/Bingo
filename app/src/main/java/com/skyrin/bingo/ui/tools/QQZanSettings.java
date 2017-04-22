package com.skyrin.bingo.ui.tools;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import com.skyrin.bingo.Config;
import com.skyrin.bingo.R;
import com.skyrin.bingo.common.ui.UIHelper;
import com.skyrin.bingo.ui.settings.BaseSettingsActivity;
import com.skyrin.bingo.ui.settings.BaseSettingsFragment;

/**
 * Created by admin on 2017/1/23.
 */

public class QQZanSettings extends BaseSettingsActivity {
    @Override
    public Fragment getSettingsFragment() {
        setTitle("QQ点赞设置");
        return new QQZanFragment();
    }

    public static class QQZanFragment extends BaseSettingsFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.qq_zan_settings);
            todo();
        }

        @Override
        protected void todo() {
            final EditTextPreference epf_number = (EditTextPreference) findPreference(Config.KEY_QQZ_NUMBER);
            epf_number.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    if (value<1||value>999999){
                        UIHelper.ShowToast(getActivity(),"请填写范围内参数~");
                        return false;
                    }
                    epf_number.setSummary("点赞"+value+"个好友");
                    return true;
                }
            });

            final ListPreference lpf_times = (ListPreference) findPreference(Config.KEY_QQZ_TIMES);
            lpf_times.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    lpf_times.setSummary("每个好友点赞"+value+"次");
                    return true;
                }
            });

            final EditTextPreference epf_delay = (EditTextPreference) findPreference(Config.KEY_QQZ_DELAY_TIME);
            epf_delay.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(String.valueOf(newValue));
                    epf_delay.setSummary(value+"毫秒后赞下一个好友");
                    return true;
                }
            });

            //初始化值
            String numbers = epf_number.getText();
            CharSequence times = lpf_times.getEntries()[Integer.parseInt(lpf_times.getValue())-1];
            String delay = epf_delay.getText();
            epf_number.setSummary("点赞"+numbers+"个好友");
            lpf_times.setSummary("每个好友点赞"+times+"次");
            epf_delay.setSummary(delay+"毫秒后赞下一个好友");
        }
    }
}
