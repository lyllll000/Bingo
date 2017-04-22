package com.skyrin.bingo.modle;

/**
 * Created by admin on 2017/1/23.
 * 工具item
 */

public class Tools {

    public static final int FUNC_QQ_ZAN = 1001;
    public static final int FUNC_MM_ZAN = 1002;

    CharSequence name;
    int funcId;
    int iconId;
    int btnStartId;
    int btnSetId;

    public Tools(CharSequence name,int funcId,int iconId,int btnStartId,int btnSetId){
        this.name = name;
        this.funcId = funcId;
        this.iconId = iconId;
        this.btnSetId = btnSetId;
        this.btnStartId = btnStartId;
    }

    public Tools(){}

    public CharSequence getName() {
        return name;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getBtnStartId() {
        return btnStartId;
    }

    public void setBtnStartId(int btnStartId) {
        this.btnStartId = btnStartId;
    }

    public int getBtnSetId() {
        return btnSetId;
    }

    public void setBtnSetId(int btnSetId) {
        this.btnSetId = btnSetId;
    }

    public int getFuncId() {
        return funcId;
    }

    public void setFuncId(int funcId) {
        this.funcId = funcId;
    }
}
