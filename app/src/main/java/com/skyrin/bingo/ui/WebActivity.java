package com.skyrin.bingo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.skyrin.bingo.R;
import com.skyrin.bingo.common.ui.ViewFindUtils;

/**
 * Created by skyrin on 2017/1/12.
 */

public class WebActivity extends BaseActivity {

    WebView webView;

    public static final String KEY_URL = "key_url";
    public static final String KEY_TITLE = "key_title";
    String url="";
    String title="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        bindView();
        iniData();
        setListener();
    }

    @Override
    protected boolean isShowBack() {
        return true;
    }

    @Override
    protected void bindView() {
        webView = ViewFindUtils.find(getWindow().getDecorView(),R.id.web_view);
    }

    @Override
    protected void iniData() {
        url = getIntent().getStringExtra(KEY_URL);
        title = getIntent().getStringExtra(KEY_TITLE);
        setTitle(title);
        webView.loadUrl(url);
    }

    @Override
    protected void setListener() {

    }
}
