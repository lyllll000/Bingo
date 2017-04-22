package com.skyrin.bingo.database.bll;

import android.content.Context;

import com.skyrin.bingo.database.dal.DMsg;
import com.skyrin.bingo.modle.TMsg;

import java.util.List;

/**
 * Created by 罗延林 on 2016/10/8 0008.
 * 消息bll
 */

public class BMsg {
    Context context;
    DMsg dMsg;
    static BMsg bMsg;

    private BMsg(Context context){
        this.context = context;
        dMsg = DMsg.getInstance(context);
    }

    public static synchronized BMsg getInstance(Context context){
        if (bMsg==null){
            bMsg = new BMsg(context);
        }
        return bMsg;
    }
    /**
     * 获取消息集合
     * @return
     */
    public List<TMsg> query(){
        return dMsg.query();
    }

    /**
     * 插入一条消息
     * @param msg
     * @return id
     */
    public long insert(TMsg msg){
        return dMsg.insert(msg);
    }

    /**
     * 更新一条消息
     * @param msg
     * @return
     */
    public boolean update(TMsg msg){
        return dMsg.update(msg);
    }

    /**
     * 删除一条消息
     * @param msg
     * @return
     */
    public boolean delete(TMsg msg){
        return dMsg.delete(msg.getId());
    }

    public void close(){
        dMsg.close();
    }
}
