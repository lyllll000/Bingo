package com.skyrin.bingo.ui.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.skyrin.bingo.R;
import com.skyrin.bingo.ui.BaseActivity;

/**
 * Created by admin on 2016/12/21.
 */

public abstract class BaseSettingsActivity extends BaseActivity {

    Fragment fragment = null;
    FragmentTransaction ft;
    FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        fm = getFragmentManager();
        ft = fm.beginTransaction();

        if (savedInstanceState != null) {
            fragment = fm.findFragmentByTag("fragment");
        }
        if (fragment == null) {
            fragment = getSettingsFragment();
        }
        //此处防止屏幕旋转或者其他操作导致fragment被多次加载
        if (fragment.isAdded()) {
            ft.show(fragment).commitAllowingStateLoss();
        } else {
            ft.add(R.id.container, fragment, "fragment").commitAllowingStateLoss();
        }
    }

    @Override
    protected boolean isShowBack() {
        return true;
    }

    public abstract Fragment getSettingsFragment();

    @Override
    protected void bindView() {

    }

    @Override
    protected void iniData() {

    }

    @Override
    protected void setListener() {

    }
}
