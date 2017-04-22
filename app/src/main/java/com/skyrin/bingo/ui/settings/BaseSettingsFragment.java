package com.skyrin.bingo.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.skyrin.bingo.Config;

/**
 * Created by admin on 2016/12/21.
 */

public abstract class BaseSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(Config.PREFERENCE_NAME);
    }

    protected abstract void todo();
}
