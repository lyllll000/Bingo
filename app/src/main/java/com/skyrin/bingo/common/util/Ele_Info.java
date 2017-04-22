package com.skyrin.bingo.common.util;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by 罗延林 on 2016/10/13 0013.
 */

public class Ele_Info {
    private String _windowId = "";
    private String _text = "";
    private String _className = "";
    private String _contentDesc = "";
    private int _bottom = 0;
    private int _top = 0;
    private int _left = 0;
    private int _right = 0;
    private int _childCount = 0;
    boolean _isClickable = false;
    boolean _isCheckable = false;
    boolean _isChecked = false;
    private ArrayList<Ele_Info> _listChild = new ArrayList<Ele_Info>();

    public boolean get_isClickable() {
        return _isClickable;
    }

    public boolean get_isCheckable() {
        return _isCheckable;
    }

    public boolean get__isChecked() {
        return _isChecked;
    }

    public void set_isClickable(boolean _isClickable) {
        this._isClickable = _isClickable;
    }

    public void set_isCheckable(boolean _isCheckable) {
        this._isCheckable = _isCheckable;
    }

    public void set_isChecked(boolean _isChecked) {
        this._isChecked = _isChecked;
    }

    public String get_className() {
        return _className;
    }

    public void set_className(String _className) {
        this._className = _className;
    }

    public int get_bottom() {
        return _bottom;
    }

    public void set_bottom(int _bottom) {
        this._bottom = _bottom;
    }

    public int get_top() {
        return _top;
    }

    public void set_top(int _top) {
        this._top = _top;
    }

    public int get_left() {
        return _left;
    }

    public void set_left(int _left) {
        this._left = _left;
    }

    public int get_right() {
        return _right;
    }

    public void set_right(int _right) {
        this._right = _right;
    }

    public int get_childCount() {
        return _childCount;
    }

    public void set_childCount(int childCount) {
        this._childCount = childCount;
    }

    public String get_windowId() {
        return _windowId;
    }

    public void set_windowId(String _windowId) {
        this._windowId = _windowId;
    }

    public String get_text() {
        return _text;
    }

    public void set_text(String _text) {
        this._text = _text;
    }

    public String get_contentDesc() {
        return _contentDesc;
    }

    public void set_contentDesc(String _Desc) {
        this._contentDesc = _Desc;
    }

    public String ToStringResult() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void set_listChild(ArrayList<Ele_Info> _listChild) {
        this._listChild = _listChild;
    }

    public ArrayList<Ele_Info> get_listChild() {
        return _listChild;
    }
}
