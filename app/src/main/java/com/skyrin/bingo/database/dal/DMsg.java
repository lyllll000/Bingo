package com.skyrin.bingo.database.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.skyrin.bingo.database.DBHelper;
import com.skyrin.bingo.database.DBHelperManager;
import com.skyrin.bingo.modle.TMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 罗延林 on 2016/10/8 0008.
 * 消息dal
 */

public class DMsg {

    static DMsg d_msg=null;
    Context context;

    DBHelper helper = null;

    public String TAG = this.getClass().getName();
    /** 消息列表的表名 */
    public static final String TABLE_NAME = "tb_msg";
    /** 消息列表的主键ID的字段名称 */
    public static final String TABLE_ID = "_id";
    /** 消息列表中的消息名称的字段名称 */
    public static final String TABLE_COLUMN_TEXT_MSG = "msg";
    /** 消息描述字段名称 */
    public static final String TABLE_COLUMN_TEXT_MSGDESC = "msgDesc";

    private DMsg(Context context){
        this.context = context;
        helper = DBHelperManager.getDBHelper(context);
    }

    public static synchronized DMsg getInstance(Context context){
        if (d_msg==null){
            d_msg = new DMsg(context);
        }
        return d_msg;
    }

    /**
     * 插入一条消息
     * @param msg
     * @return 插入行的id
     */
    public long insert(TMsg msg){
        ContentValues values = getContentValues(msg);
        return helper.insert(TABLE_NAME,values);
    }

    /**
     * 根据id删除一条消息
     * @param msgId
     * @return
     */
    public boolean delete(long msgId){
        boolean result = false;
        if (helper.delete(TABLE_NAME, TABLE_ID + "=?",
                new String[] { msgId + "" }) > 0) {
            result = true;
        }
        return result;
    }

    /**
     * 更新一条消息
     * @return
     */
    public boolean update(TMsg msg){
        boolean result = false;
        ContentValues values = getContentValues(msg);
        if (helper.update(TABLE_NAME, values, TABLE_ID + "=?",
                new String[] { msg.getId() + "" }) > 0) {
            result = true;
        }
        return result;
    }

    /**
     * 获取消息集合
     * @return
     */
    public List<TMsg> query(){
        List<TMsg> list = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME +" order by _id DESC";
        Cursor cursor = helper.rawQuery(sql,null);
        if (cursor != null && cursor.moveToFirst()&&cursor.getCount()>0) {
            list = new ArrayList<>();
            do {
                TMsg msg = getMsg(cursor);
                list.add(msg);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 通过cursor获取T_msg对象
     * */
    private TMsg getMsg(Cursor cursor) {
        TMsg msg = new TMsg();
        msg.setId(cursor.getLong(cursor.getColumnIndex(TABLE_ID)));
        msg.setMsg(cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_TEXT_MSG)));
        return msg;
    }

    /**
     * 将消息对象转换为ContentValues对象
     *
     * @param msg
     *            微信消息对象
     * */
    private ContentValues getContentValues(TMsg msg) {
        ContentValues values = new ContentValues();
        values.put(TABLE_COLUMN_TEXT_MSG, msg.getMsg());
//        values.put(TABLE_ID,msg.getId());
        return values;
    }

    /**
     * 关闭数据库操作对象
     */
    public void close(){
        DBHelperManager.closeDB();
    }
}
