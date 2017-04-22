package com.skyrin.bingo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skyrin.bingo.R;

/**
 * Created by admin on 2016/12/20.
 */

public class FragmentAbt extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fr_mm, container, false);
        return rootView;
    }

    @Override
    protected void bindView(View view) {

    }

    @Override
    protected void iniData() {

    }

    @Override
    protected void setListener() {

    }
}
