package com.skyrin.bingo.ui.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;

import com.skyrin.bingo.R;
import com.skyrin.bingo.common.ui.UIHelper;
import com.skyrin.bingo.ui.NotifyHelper;

import static com.skyrin.bingo.ui.NotifyHelper.isNightTime;

/**
 * Created by admin on 2016/12/21.
 */

public class MMNotifyActivity extends BaseSettingsActivity {
    @Override
    public Fragment getSettingsFragment() {
        setTitle("红包提醒设置");
        return new NotifyFragment();
    }

    public static class NotifyFragment extends BaseSettingsFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.mm_notify_settings);
            todo();
        }

        @Override
        protected void todo() {
            findPreference("KEY_AUDITION_SOUND").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    NotifyHelper.sound(getActivity());
                    return false;
                }
            });
        }
    }
}
