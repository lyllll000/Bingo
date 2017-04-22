package com.skyrin.bingo.modle;

/**
 * Created by 罗延林 on 2016/10/8 0008.
 * 消息列表
 */

public class TMsg {
    private long id;
    private String msg;

    public TMsg(){}

    public TMsg(String msg, long id){
        this.msg = msg;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
