package com.skyrin.bingo.ui.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import com.skyrin.bingo.Config;
import com.skyrin.bingo.R;

/**
 * Created by skyrin on 2016/12/30.
 */

public class QQSetActivity extends BaseSettingsActivity {
    @Override
    public Fragment getSettingsFragment() {
        setTitle("基本设置");
        return new QQSetFragment();
    }
    public static class QQSetFragment extends BaseSettingsFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.qq_settings);
            todo();
        }

        @Override
        protected void todo() {
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
                    qqopenpf.setSummary(qqopenpf.getEntries()[value]);
                    return true;
                }
            });
            qqopenpf.setSummary(qqopenpf.getEntries()[Integer.parseInt(qqopenpf.getValue())]);
        }
    }
}
