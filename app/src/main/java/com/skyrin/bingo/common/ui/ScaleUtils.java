package com.skyrin.bingo.common.ui;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.TextView;

public class ScaleUtils {
    /**
     * 缩放视图
     * @param view
     * @param f
     * @param i
     */
    public static void scaleViewAndChildren(View view, float f, int i) {
        LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.width > 0) {
            layoutParams.width = (int) (((float) layoutParams.width) * f);
        }
        if (layoutParams.height > 0) {
            layoutParams.height = (int) (((float) layoutParams.height) * f);
        }
        if (layoutParams instanceof MarginLayoutParams) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams;
            marginLayoutParams.leftMargin = (int) (((float) marginLayoutParams.leftMargin) * f);
            marginLayoutParams.topMargin = (int) (((float) marginLayoutParams.topMargin) * f);
            marginLayoutParams.rightMargin = (int) (((float) marginLayoutParams.rightMargin) * f);
            marginLayoutParams.bottomMargin = (int) (((float) marginLayoutParams.bottomMargin) * f);
        }
        view.setLayoutParams(layoutParams);
        if (!(view instanceof EditText)) {
            view.setPadding((int) (((float) view.getPaddingLeft()) * f), (int) (((float) view.getPaddingTop()) * f), (int) (((float) view.getPaddingRight()) * f), (int) (((float) view.getPaddingBottom()) * f));
        }
        if (view instanceof TextView) {
            scaleTextSize((TextView) view, f);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                scaleViewAndChildren(viewGroup.getChildAt(i2), f, i + 1);
            }
        }
    }

    public static void scaleTextSize(TextView textView, float f) {
        textView.setTextSize(0, textView.getTextSize() * f);
    }
}
